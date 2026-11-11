package micdoodle8.mods.galacticraft.core.client.gui.container

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

abstract class GuiContainerGC(container: Container) : GuiContainer(container) {
    @JvmField
    var infoRegions: MutableList<GuiElementInfoRegion> = ArrayList<GuiElementInfoRegion>()

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        this.drawDefaultBackground()
        super.drawScreen(par1, par2, par3)
        this.renderHoveredToolTip(par1, par2)

        for (k: Int in this.infoRegions.indices) {
            val guibutton: GuiElementInfoRegion = this.infoRegions.get(k)
            guibutton.drawRegion(par1, par2)
        }
    }

    override fun setWorldAndResolution(par1Minecraft: Minecraft, par2: Int, par3: Int) {
        this.infoRegions.clear()
        super.setWorldAndResolution(par1Minecraft, par2, par3)
    }

    fun getTooltipOffset(par1: Int, par2: Int): Int {
        for (i1: Int in this.inventorySlots.inventorySlots.indices) {
            val slot: Slot = this.inventorySlots.inventorySlots.get(i1)

            if (slot.isActive() && this.isPointInRegion(slot.x, slot.y, 16, 16, par1, par2)) {
                val itemStack: ItemStack = slot.getItem()

                if (!itemStack.isEmpty()) {
                    val list: MutableList<*> = itemStack.getTooltip(
                        this.mc.player,
                        if (this.mc.gameSettings.advancedItemTooltips) ITooltipFlag.TooltipFlags.ADVANCED else ITooltipFlag.TooltipFlags.NORMAL
                    )
                    var size: Int = list.size

                    if (CompatibilityManager.isWailaLoaded()) {
                        size++
                    }

                    return size * 10 + 10
                }
            }
        }

        return 0
    }
}
