package micdoodle8.mods.galacticraft.planets.mars.client.gui

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars
import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.inventory.ContainerRocketInventory
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import net.minecraft.client.gui.GuiButton
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiCargoRocket @JvmOverloads constructor(
    private val upperChestInventory: IInventory,
    private val rocket: EntityCargoRocket,
    rocketType: IRocketType.EnumRocketType = (rocket.rocketType)!!
) : GuiContainerGC(
    ContainerRocketInventory(
        upperChestInventory,
        rocket,
        rocketType,
        FMLClientHandler.instance().getClient().player
    )
) {
    private val rocketType: IRocketType.EnumRocketType
    private var launchButton: GuiButton? = null

    init {
        this.allowUserInput = false
        this.ySize = if (rocketType.getInventorySpace() <= 3) 132 else 145 + rocketType.getInventorySpace() * 2
        this.rocketType = rocketType
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimpleMars(
                    PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_CARGO_ROCKET_STATUS,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any>(this.rocket.getEntityId(), 0)
                )
            )

            else -> {}
        }
    }

    override fun initGui() {
        super.initGui()
        val var6: Int = (this.height - this.ySize) / 2
        val var7: Int = (this.width - this.xSize) / 2
        this.launchButton = GuiButton(0, var7 + 116, var6 + 26, 50, 20, GCCoreUtil.translate("gui.message.launch.name"))
        this.buttonList.add(this.launchButton)
        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.0"))
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + (if ((this.rocket.rocketType)!!.getInventorySpace() == 2) 70 else 71),
                (this.height - this.ySize) / 2 + 6,
                36,
                40,
                fuelTankDesc,
                this.width,
                this.height,
                this
            )
        )
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        if ((this.rocket.rocketType)!!.getInventorySpace() == 2) {
            this.fontRenderer.drawString(
                GCCoreUtil.translate(this.upperChestInventory.getName()),
                8,
                76 + ((this.rocket.rocketType)!!.getInventorySpace() - 20) / 9 * 18,
                4210752
            )
        } else {
            this.fontRenderer.drawString(
                GCCoreUtil.translate(this.upperChestInventory.getName()),
                8,
                89 + ((this.rocket.rocketType)!!.getInventorySpace() - 20) / 9 * 18,
                4210752
            )
        }

        var str: String = GCCoreUtil.translate("gui.message.fuel.name") + ":"
        this.fontRenderer.drawString(str, 140 - this.fontRenderer.getStringWidth(str) / 2, 5, 4210752)
        val percentage: Double = this.rocket.getScaledFuelLevel(100).toDouble()
        val color: String? =
            if (percentage > 80.0) EnumColor.BRIGHT_GREEN.getCode() else if (percentage > 40.0) EnumColor.ORANGE.getCode() else EnumColor.RED.getCode()
        str = percentage.toString() + "% " + GCCoreUtil.translate("gui.message.full.name")
        this.fontRenderer.drawString(color + str, 140 - this.fontRenderer.getStringWidth(str) / 2, 15, 4210752)
        str = GCCoreUtil.translate("gui.message.status.name") + ":"
        this.fontRenderer.drawString(str, 40 - this.fontRenderer.getStringWidth(str) / 2, 9, 4210752)

        var spltString: Array<String> = arrayOf<String>("")
        var colour: String? = EnumColor.YELLOW.toString()

        if (this.rocket.statusMessageCooldown == 0 || this.rocket.statusMessage == null) {
            spltString = (arrayOfNulls<String>(2) as Array<String>)
            spltString[0] = GCCoreUtil.translate("gui.cargorocket.status.waiting.0")
            spltString[1] = GCCoreUtil.translate("gui.cargorocket.status.waiting.1")

            if (this.rocket.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.UNIGNITED.ordinal) {
                spltString = (arrayOfNulls<String>(2) as Array<String>)
                spltString[0] = GCCoreUtil.translate("gui.cargorocket.status.launched.0")
                spltString[1] = GCCoreUtil.translate("gui.cargorocket.status.launched.1")
                this.launchButton!!.enabled = false
            }
        } else {
            spltString = ((this.rocket.statusMessage)!!.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())!!
            colour = this.rocket.statusColour
        }

        var y: Int = 2
        for (splitString: String in spltString) {
            this.fontRenderer.drawString(
                colour + splitString,
                35 - this.fontRenderer.getStringWidth(splitString) / 2,
                9 * y,
                4210752
            )
            y++
        }

        if (this.rocket.statusValid && this.rocket.statusMessageCooldown > 0 && this.rocket.statusMessageCooldown < 4) {
            this.mc.displayGuiScreen(null)
        }
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.getTextureManager().bindTexture(rocketTextures[(this.rocketType.getInventorySpace() - 2) / 18])
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, 176, this.ySize)

        val fuelLevel: Int = this.rocket.getScaledFuelLevel(38)
        this.drawTexturedModalRect(
            (this.width - this.xSize) / 2 + (if ((this.rocket.rocketType)!!.getInventorySpace() == 2) 71 else 72),
            (this.height - this.ySize) / 2 + 45 - fuelLevel,
            176,
            38 - fuelLevel,
            42,
            fuelLevel
        )
    }

    companion object {
        private val rocketTextures = arrayOfNulls<ResourceLocation>(4)

        init {
            for (i: Int in 0..3) {
                rocketTextures[i] = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/rocket_" + i * 18 + ".png")
            }
        }
    }
}
