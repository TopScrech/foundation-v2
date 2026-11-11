package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.core.prefab.inventory.SlotUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityUniversalRecycler
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.inventory.SlotFurnaceOutput
import net.minecraft.world.item.ItemStack

open class ContainerUniversalRecycler constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityUniversalRecycler
) : Container() {
    init {
        // Battery Slot
        this.addSlot(SlotSpecific(tileEntity, 0, 79, 91, ItemElectricBase::class.java))
        this.addSlot(Slot(tileEntity, 1, 20, 45))

        this.addSlot(SlotSpecific(tileEntity, 2, 148, 19, ItemCanisterGeneric::class.java))
        this.addSlot(SlotFurnaceOutput(par1InventoryPlayer.player, tileEntity, 3, 105, 35))
        this.addSlot(SlotFurnaceOutput(par1InventoryPlayer.player, tileEntity, 4, 105, 35 + 21))


        // this.addSlot(new SlotSpecific(tileEntity, 4, 6, 19, ItemGSBucket.class));
        // Smelting result
        //this.addSlot(new SlotFurnace(par1InventoryPlayer.player, tileEntity, 1, 15, 36));
        var var3: Int
        var3 = 0
        while (var3 < 4) {
            this.addSlot(
                SlotUpgrades(
                    tileEntity,
                    5 + var3,
                    179,
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
                this.addSlot(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 137 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 195))
            ++var3
        }
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val var3: Slot? = this.inventorySlots.get(par1)

        if (var3 != null && var3.hasItem()) {
            val var4: ItemStack = var3.getItem()
            var2 = var4.copy()

            if (par1 <= 3) {
                if (!this.moveItemStackTo(var4, 4, 35, true)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 3) {
                    var3.onQuickCraft(var4, var2)
                }
            } else {
                if (var4.getItem() is IItemElectric) {
                    if (!this.moveItemStackTo(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (var4.getItem() is ItemCanisterGeneric) {
                    if (!this.moveItemStackTo(var4, 2, 3, false)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    if (!this.moveItemStackTo(var4, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } /*
                else if (par1 < 38)
                {
                    if (!this.moveItemStackTo(var4, 0, 9, false) && !this.moveItemStackTo(var4, 38, 48, false))
                    {
                        return null;
                    }
                }
                else if (!this.moveItemStackTo(var4, 0, 9, false) && !this.moveItemStackTo(var4, 11, 38, false))
                {
                    return null;
                }*/
            }

            if (var4.getCount() == 0) {
                var3.set(ItemStack.EMPTY)
            } else {
                var3.setChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            var3.onTake(par1EntityPlayer, var4)
        }

        return var2
    }
}
