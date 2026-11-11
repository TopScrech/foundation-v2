package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import foundation.item.legacyItemValue

import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerWindGenerator constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityWindGenerator
) : Container() {
    init {
        this.addSlot(SlotSpecific(tileEntity, 0, 152, 84, IItemElectric::class.java))
        val stacks: Array<ItemStack?> = arrayOf<ItemStack?>(
            BasicItems.IRON_FAN.getItemStack(),
            BasicItems.STEEL_FAN.getItemStack(),
            BasicItems.PLASTIC_FAN.getItemStack(),
            BasicItems.CARBON_FAN.getItemStack()
        )
        this.addSlot(SlotSpecific(tileEntity, 1, 152, 22, *stacks))

        // Player inv:
        for (var6: Int in 0..2) {
            for (var7: Int in 0..8) {
                this.addSlot(
                    Slot(
                        par1InventoryPlayer,
                        var7 + var6 * 9 + 9,
                        8 + var7 * 18,
                        65 + 68 + var6 * 18
                    )
                )
            }
        }

        for (var6: Int in 0..8) {
            this.addSlot(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 75 + 116))
        }
    }

    override fun stillValid(var1: EntityPlayer): Boolean {
        return this.tileEntity.stillValid(var1)
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.hasItem()) {
            val stack: ItemStack = slot.getItem()
            var2 = stack.copy()

            if (par1 == 0 || par1 == 1) {
                if (!this.moveItemStackTo(stack, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (stack.getItem() is IItemElectric) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (stack.getItem() === GSItems.BASIC && stack.legacyItemValue >= 33 && stack.legacyItemValue <= 36) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    if (par1 < b - 9) {
                        if (!this.moveItemStackTo(stack, b - 9, b, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.moveItemStackTo(stack, b - 36, b - 9, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (stack.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, stack)
        }

        return var2
    }
}
