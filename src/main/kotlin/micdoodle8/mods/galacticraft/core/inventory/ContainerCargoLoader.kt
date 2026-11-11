package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.api.tile.ILockable
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerCargoLoader constructor(par1InventoryPlayer: InventoryPlayer, cargoLoader: IInventory) :
    Container() {
    private var tileEntity: TileBaseElectricBlock
    private var locked = false

    init {
        this.tileEntity = cargoLoader as TileBaseElectricBlock
        if (tileEntity is ILockable) {
            this.locked = (tileEntity as ILockable).getLocked()
        }

        this.addSlot(SlotSpecific(cargoLoader, 0, 10, 27, IItemElectric::class.java))

        var var6: Int
        var var7: Int

        var6 = 0
        while (var6 < 2) {
            var7 = 0
            while (var7 < 7) {
                this.addSlot(Slot(cargoLoader, var7 + var6 * 7 + 1, 38 + var7 * 18, 27 + var6 * 18))
                ++var7
            }
            ++var6
        }

        // Player inv:
        var6 = 0
        while (var6 < 3) {
            var7 = 0
            while (var7 < 9) {
                this.addSlot(Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 124 + var6 * 18))
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 66 + 116))
            ++var6
        }
    }

    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: EntityPlayer): ItemStack {
        if (this.locked && slotId > 0 && slotId < 15) {
            return ItemStack.EMPTY
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player)
    }

    override fun stillValid(var1: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(var1)
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par2: Int): ItemStack {
        var var3: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par2)

        if (slot != null && slot.hasItem()) {
            val var5: ItemStack = slot.getItem()
            var3 = var5.copy()

            if (par2 < 15) {
                if ((this.locked && par2 > 0) || !this.moveItemStackTo(var5, 15, 51, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(var5.getItem())) {
                    if (!this.moveItemStackTo(var5, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par2 < 42) {
                    if ((this.locked || !this.moveItemStackTo(var5, 1, 15, false)) && !this.moveItemStackTo(
                            var5,
                            42,
                            51,
                            false
                        )
                    ) {
                        return ItemStack.EMPTY
                    }
                } else if ((this.locked || !this.moveItemStackTo(var5, 1, 15, false)) && !this.moveItemStackTo(
                        var5,
                        15,
                        42,
                        false
                    )
                ) {
                    return ItemStack.EMPTY
                }
            }

            if (var5.getCount() == 0) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (var5.getCount() == var3.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, var5)
        }

        return var3
    }
}
