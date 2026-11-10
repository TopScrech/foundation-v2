package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.core.prefab.inventory.SlotUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAssembler
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.inventory.SlotFurnaceOutput
import net.minecraft.item.ItemStack

open class ContainerAssembler constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityAssembler
) : Container() {
    init {
        tileEntity.testCraftMatrix.gs_eventHandler = this

        for (x: Int in 0..2) {
            for (y: Int in 0..2) {
                this.addSlotToContainer(Slot(tileEntity.testCraftMatrix, y + x * 3, 19 + y * 18, 18 + x * 18))
            }
        }

        // Battery Slot
        this.addSlotToContainer(SlotSpecific(tileEntity, 0, 77, 83, ItemElectricBase::class.java))

        // Smelting result
        this.addSlotToContainer(SlotFurnaceOutput(par1InventoryPlayer.player, tileEntity, 1, 138, 36))


        var var3: Int
        var3 = 0
        while (var3 < 4) {
            this.addSlotToContainer(
                SlotUpgrades(
                    tileEntity,
                    2 + var3,
                    178,
                    18 + (21 * var3),
                    1,
                    ItemUpgrades::class.java
                )
            )
            ++var3
        }


        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlotToContainer(
                    Slot(
                        par1InventoryPlayer,
                        var4 + var3 * 9 + 9,
                        8 + var4 * 18,
                        117 + 12 + var3 * 18
                    )
                )
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 175 + 12))
            ++var3
        }
    }

    override fun onContainerClosed(entityplayer: EntityPlayer) {
        super.onContainerClosed(entityplayer)
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    override fun onCraftMatrixChanged(par1IInventory: IInventory) {
        super.onCraftMatrixChanged(par1IInventory)
        this.tileEntity.updateInput()
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val var3: Slot? = this.inventorySlots.get(par1)

        if (var3 != null && var3.getHasStack()) {
            val var4: ItemStack = var3.getStack()
            var2 = var4.copy()

            if (par1 <= 10) {
                if (!this.mergeItemStack(var4, 11, 47, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (var4.getItem() is IItemElectric) {
                    if (!this.mergeItemStack(var4, 9, 10, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par1 < 39) {
                    if (!this.mergeItemStack(var4, 0, 9, false) && !this.mergeItemStack(var4, 39, 48, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.mergeItemStack(var4, 0, 9, false) && !this.mergeItemStack(var4, 12, 39, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (var4.getCount() == 0) {
                var3.putStack(ItemStack.EMPTY)
            } else {
                var3.onSlotChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            var3.onTake(par1EntityPlayer, var4)
        }
        return var2
    }
}
