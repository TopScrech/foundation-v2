package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.core.prefab.inventory.SlotUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidExtractor
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import micdoodle8.mods.galacticraft.core.items.ItemOilCanister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemBucket
import net.minecraft.item.ItemStack

open class ContainerLiquidExtractor constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityLiquidExtractor
) : Container() {
    init {
        // Battery Slot
        this.addSlotToContainer(SlotSpecific(tileEntity, 0, 10, 80, ItemElectricBase::class.java))
        this.addSlotToContainer(
            SlotSpecific(
                tileEntity,
                1,
                79,
                63,
                ItemOilCanister::class.java,
                ItemBucket::class.java
            )
        )


        // this.addSlotToContainer(new SlotSpecific(tileEntity, 2, 10, 40, new ItemStack(GSItems.BASIC, 1, 11)));

        // Smelting result
        //this.addSlotToContainer(new SlotFurnace(par1InventoryPlayer.player, tileEntity, 1, 15, 36));
        var var3: Int
        var3 = 0
        while (var3 < 4) {
            this.addSlotToContainer(
                SlotUpgrades(
                    tileEntity,
                    3 + var3,
                    177,
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
                this.addSlotToContainer(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 122 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 180))
            ++var3
        }
    }

    override fun onContainerClosed(entityplayer: EntityPlayer) {
        super.onContainerClosed(entityplayer)
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
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

            if (par1 <= 3) {
                if (!this.mergeItemStack(var4, 3, 35, false)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 1) {
                    var3.onSlotChange(var4, var2)
                }
            } else {
                if (var4.getItem() is IItemElectric) {
                    if (!this.mergeItemStack(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (var4.getItem() === Item.getItemFromBlock(GCBlocks.blockMoon) && var4.getItemDamage() == 5) {
                    if (!this.mergeItemStack(var4, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (var4.getItem() === GCItems.oilCanister && var4.getItemDamage() == (GCItems.oilCanister)!!.getMaxDamage()) {
                    if (!this.mergeItemStack(var4, 2, 3, false)) {
                        return ItemStack.EMPTY
                    }
                } /*
                else if (par1 < 38)
                {
                    if (!this.mergeItemStack(var4, 0, 9, false) && !this.mergeItemStack(var4, 38, 48, false))
                    {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(var4, 0, 9, false) && !this.mergeItemStack(var4, 11, 38, false))
                {
                    return null;
                }*/
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
