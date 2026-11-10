package micdoodle8.mods.galacticraft.core.client.gui.container

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy
import micdoodle8.mods.galacticraft.core.inventory.ContainerBuggy
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiBuggy constructor(
    private val upperChestInventory: IInventory,
    par2IInventory: IInventory,
    type: Int
) : GuiContainerGC(
    ContainerBuggy(
        upperChestInventory,
        par2IInventory,
        type,
        FMLClientHandler.instance().getClient().player
    )
) {
    private val type: Int

    init {
        this.allowUserInput = false
        this.type = type
        this.ySize = 145 + this.type * 36
    }

    override fun initGui() {
        super.initGui()
        val oxygenDesc: MutableList<String?> = ArrayList<String?>()
        oxygenDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.0"))
        oxygenDesc.add(GCCoreUtil.translate("gui.fuel_tank.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 71,
                (this.height - this.ySize) / 2 + 6,
                36,
                40,
                oxygenDesc,
                this.width,
                this.height,
                this
            )
        )
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.fuel.name"), 8, 2 + 3, 4210752)

        this.fontRenderer.drawString(
            GCCoreUtil.translate(this.upperChestInventory.getName()),
            8,
            if (this.type == 0) 50 else 39,
            4210752
        )

        if (this.mc.player != null && this.mc.player.getRidingEntity() != null && this.mc.player.getRidingEntity() is EntityBuggy) {
            this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.fuel.name") + ":", 125, 15 + 3, 4210752)
            val percentage: Double =
                (this.mc.player.getRidingEntity() as EntityBuggy).getScaledFuelLevel(100).toDouble()
            val color: String? =
                if (percentage > 80.0) EnumColor.BRIGHT_GREEN.getCode() else if (percentage > 40.0) EnumColor.ORANGE.getCode() else EnumColor.RED.getCode()
            val str: String = percentage.toString() + "% " + GCCoreUtil.translate("gui.message.full.name")
            this.fontRenderer.drawString(color + str, 117 - str.length / 2, 20 + 8, 4210752)
        }
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.getTextureManager().bindTexture(sealerTexture[this.type])
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, 176, this.ySize)

        if (this.mc.player != null && this.mc.player.getRidingEntity() != null && this.mc.player.getRidingEntity() is EntityBuggy) {
            val fuelLevel: Int = (this.mc.player.getRidingEntity() as EntityBuggy).getScaledFuelLevel(38)

            this.drawTexturedModalRect(
                (this.width - this.xSize) / 2 + 72,
                (this.height - this.ySize) / 2 + 45 - fuelLevel,
                176,
                38 - fuelLevel,
                42,
                fuelLevel
            )
        }
    }

    companion object {
        private val sealerTexture = arrayOfNulls<ResourceLocation>(4)

        init {
            for (i: Int in 0..3) {
                sealerTexture[i] = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/buggy_" + i * 18 + ".png")
            }
        }
    }
}
