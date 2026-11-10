package micdoodle8.mods.galacticraft.planets.mars.inventory

import java.util.Collections

import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import java.util.*

open class ContainerSlimeling constructor(
    playerInventory: InventoryPlayer,
    slimeling: EntitySlimeling,
    player: EntityPlayer
) : Container() {
    private val slimelingInventory: InventorySlimeling

    init {
        this.slimelingInventory = slimeling.slimelingInventory
        this.slimelingInventory.currentContainer = this

        addSlots(this, playerInventory, slimeling)
        addAdditionalSlots(this, slimeling, this.slimelingInventory.getStackInSlot(1))

        this.slimelingInventory.openInventory(player)
    }

    override fun onContainerClosed(entityplayer: EntityPlayer) {
        this.slimelingInventory.closeInventory(entityplayer)
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.slimelingInventory.isUsableByPlayer(par1EntityPlayer)
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.getHasStack()) {
            val var4: ItemStack = slot.getStack()
            var2 = var4.copy()

            if (b < 39) {
                if (par1 < b - 36) {
                    if (!this.mergeItemStack(var4, b - 36, b, true)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    if (var4.getItem() === MarsItems.marsItemBasic && var4.getItemDamage() == 4) {
                        if (!this.mergeItemStack(var4, 0, 1, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 < b - 9) {
                        if (!this.mergeItemStack(var4, b - 9, b, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.mergeItemStack(var4, b - 36, b - 9, false)) {
                        return ItemStack.EMPTY
                    }
                }
            } else {
                //With inventory bag, slot 0 is a bag slot
                //Slots 1-36 are regular inventory (27 inventory, 9 hotbar)
                //Slots 37-63 are the inventory bag slots
                if (par1 == 0) {
                    return ItemStack.EMPTY
                }

                if (par1 > 36) {
                    if (!this.mergeItemStack(var4, 1, 37, true)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    if (par1 < 28) {
                        if (!this.mergeItemStack(var4, 37, 64, false)) {
                            if (!this.mergeItemStack(var4, 28, 37, false)) {
                                return ItemStack.EMPTY
                            }
                        }
                    } else if (!this.mergeItemStack(var4, 37, 64, false)) {
                        if (!this.mergeItemStack(var4, 1, 28, false)) {
                            return ItemStack.EMPTY
                        }
                    }
                }
            }

            if (var4.isEmpty()) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, var4)
        }

        return var2
    }

    companion object {
        @JvmStatic
        fun addSlots(container: ContainerSlimeling, playerInventory: InventoryPlayer, slimeling: EntitySlimeling) {
            var slot: Slot =
                SlotSpecific(slimeling.slimelingInventory, 1, 9, 30, ItemStack(MarsItems.marsItemBasic, 1, 4))
            container.addSlotToContainer(slot)

            var var3: Int

            var3 = 0
            while (var3 < 3) {
                for (var4: Int in 0..8) {
                    slot = Slot(playerInventory, var4 + var3 * 9 + 9, 8 + var4 * 18, 129 + var3 * 18)
                    container.addSlotToContainer(slot)
                }
                ++var3
            }

            var3 = 0
            while (var3 < 9) {
                slot = Slot(playerInventory, var3, 8 + var3 * 18, 187)
                container.addSlotToContainer(slot)
                ++var3
            }
        }

        @JvmStatic
        fun removeSlots(container: ContainerSlimeling) {
            Collections.copy<ItemStack?>(container.inventoryItemStacks, container.inventoryItemStacks.subList(0, 37))
            container.inventorySlots = container.inventorySlots.subList(0, 37)
        }

        @JvmStatic
        fun addAdditionalSlots(container: ContainerSlimeling, slimeling: EntitySlimeling, stack: ItemStack) {
            if (!stack.isEmpty() && stack.getItem() === MarsItems.marsItemBasic && stack.getItemDamage() == 4) {
                //Note that if NEI is installed, this can be called by InventorySlimeling.setInventorySlotContents even if the container already has the slots
                if (container.inventorySlots.size < 63) {
                    for (var3: Int in 0..2) {
                        for (var4: Int in 0..8) {
                            val slot: Slot =
                                Slot(slimeling.slimelingInventory, var4 + var3 * 9 + 2, 8 + var4 * 18, 54 + var3 * 18)
                            slot.slotNumber = container.inventorySlots.size
                            container.inventorySlots.add(slot)
                            container.inventoryItemStacks.add(ItemStack.EMPTY)
                        }
                    }
                }
            }
        }
    }
}
