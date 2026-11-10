package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.entities.IScaleableFuelLevel
import micdoodle8.mods.galacticraft.core.inventory.ContainerParaChest
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiParaChest constructor(
    private val upperChestInventory: IInventory,
    private val lowerChestInventory: IInventory
) : GuiContainerGC(
    ContainerParaChest(
        upperChestInventory,
        lowerChestInventory,
        FMLClientHandler.instance().getClient().player
    )
) {
    private var inventorySize = 0

    init {
        this.allowUserInput = false
        this.inventorySize = lowerChestInventory.getSizeInventory()
        this.ySize = 146 + ((this.inventorySize)!! * 2)!!
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.lowerChestInventory.getName(), 8, 6, 4210752)
        this.fontRenderer.drawString(
            if (this.upperChestInventory.hasCustomName()) this.upperChestInventory.getName() else GCCoreUtil.translate(
                this.upperChestInventory.getName()
            ), 8, this.ySize - 103 + (if (this.inventorySize == 3) 2 else 4), 4210752
        )
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(parachestTexture[(this.inventorySize - 3) / 18])
        val k: Int = (this.width - this.xSize) / 2
        val l: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize)

        if (this.lowerChestInventory is IScaleableFuelLevel) {
            val fuelLevel: Int = (this.lowerChestInventory as IScaleableFuelLevel).getScaledFuelLevel(28)
            this.drawTexturedModalRect(
                k + 17,
                l + (if (this.inventorySize == 3) 40 else 42) - fuelLevel + this.inventorySize * 2,
                176,
                28 - fuelLevel,
                34,
                fuelLevel
            )
        }
    }

    companion object {
        private val parachestTexture = arrayOfNulls<ResourceLocation>(4)

        init {
            for (i: Int in 0..3) {
                parachestTexture[i] = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/chest_" + i * 18 + ".png")
            }
        }
    }
}
