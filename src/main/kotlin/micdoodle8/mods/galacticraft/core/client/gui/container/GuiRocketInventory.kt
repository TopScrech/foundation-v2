package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType
import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.inventory.ContainerRocketInventory
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.Container as IInventory
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class GuiRocketInventory constructor(
    private val upperChestInventory: IInventory,
    par2IInventory: IInventory,
    rocketType: IRocketType.EnumRocketType
) : GuiContainerGC(
    ContainerRocketInventory(
        upperChestInventory,
        par2IInventory,
        rocketType,
        FMLClientHandler.instance().getClient().player
    )
) {
    private val rocketType: IRocketType.EnumRocketType

    init {
        this.allowUserInput = false
        this.ySize = if (rocketType.getInventorySpace() <= 3) 132 else 145 + rocketType.getInventorySpace() * 2
        this.rocketType = rocketType
    }

    override fun initGui() {
        super.initGui()
        val fuelTankDesc: MutableList<String?> = ArrayList<String?>()
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.0"))
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + (if (((this.mc.player.getRidingEntity() as EntityTieredRocket).rocketType)!!.getInventorySpace() == 2) 70 else 71),
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
        this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.fuel.name"), 8, 2 + 3, 4210752)

        this.fontRenderer.drawString(GCCoreUtil.translate(this.upperChestInventory.getName()), 8, 34 + 2 + 3, 4210752)

        if (this.mc.player != null && this.mc.player.getRidingEntity() != null && this.mc.player.getRidingEntity() is EntitySpaceshipBase) {
            this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.fuel.name") + ":", 125, 15 + 3, 4210752)
            val percentage: Double =
                (this.mc.player.getRidingEntity() as EntitySpaceshipBase).getScaledFuelLevel(100).toDouble()
            val color: String? =
                if (percentage > 80.0) EnumColor.BRIGHT_GREEN.getCode() else if (percentage > 40.0) EnumColor.ORANGE.getCode() else EnumColor.RED.getCode()
            val str: String = percentage.toString() + "% " + GCCoreUtil.translate("gui.message.full.name")
            this.fontRenderer.drawString(color + str, 117 - str.length / 2, 20 + 8, 4210752)
        }
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.getTextureManager().bindTexture(rocketTextures[(this.rocketType.getInventorySpace() - 2) / 18])
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, 176, this.ySize)

        if (this.mc.player != null && this.mc.player.getRidingEntity() != null && this.mc.player.getRidingEntity() is EntitySpaceshipBase) {
            val fuelLevel: Int = (this.mc.player.getRidingEntity() as EntitySpaceshipBase).getScaledFuelLevel(38)

            this.drawTexturedModalRect(
                (this.width - this.xSize) / 2 + (if (((this.mc.player.getRidingEntity() as EntityTieredRocket).rocketType)!!.getInventorySpace() == 2) 71 else 72),
                (this.height - this.ySize) / 2 + 45 - fuelLevel,
                176,
                38 - fuelLevel,
                42,
                fuelLevel
            )
        }
    }

    companion object {
        private val rocketTextures = arrayOfNulls<ResourceLocation>(4)

        init {
            for (i: Int in 0..3) {
                rocketTextures[i] = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/rocket_" + i * 18 + ".png")
            }
        }
    }
}
