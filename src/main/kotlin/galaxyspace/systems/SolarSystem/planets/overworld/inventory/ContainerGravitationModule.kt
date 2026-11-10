package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.core.prefab.inventory.SlotUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGravitationModule
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerGravitationModule constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityGravitationModule
) : Container() {
    init {
        //this.addSlotToContainer(new SlotSpecific(tileEntity, 0, 7, 7, new ItemStack(GCItems.bucketFuel)));
        //this.addSlotToContainer(new Slot(tileEntity, 1, 7, 7));
        var var3: Int


        // Battery Slot
        this.addSlotToContainer(SlotSpecific(tileEntity, 0, 78, 99, ItemElectricBase::class.java))

        //this.addSlotToContainer(new SlotSpecific(tileEntity, 1, 7, 20, new ItemStack(GSItems.UPGRADES, 1, 1)));
        var3 = 0
        while (var3 < 4) {
            this.addSlotToContainer(
                SlotUpgrades(
                    tileEntity,
                    1 + var3,
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
                this.addSlotToContainer(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 137 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 195))
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

            if (par1 != 0) {
                if (var4.getItem() === GCItems.bucketFuel) {
                    if (!this.mergeItemStack(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                }
                if (var4.getItem() === GCItems.fuelCanister && var4.getItemDamage() != 1001) {
                    if (!this.mergeItemStack(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par1 >= 28) {
                    if (!this.mergeItemStack(var4, 1, 28, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.mergeItemStack(var4, 28, 37, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(var4, 1, 37, false)) {
                return ItemStack.EMPTY
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
