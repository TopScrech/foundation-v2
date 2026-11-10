package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicBase
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.Items
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.Slot
import net.minecraft.inventory.SlotFurnaceOutput
import net.minecraft.item.ItemStack

open class ContainerHydroponicBase constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityHydroponicBase
) : Container() {
    init {
        //this.addSlotToContainer(new SlotSpecific(tileEntity, 0, 7, 7, new ItemStack(GCItems.bucketFuel)));
        // Battery Slot
        this.addSlotToContainer(SlotSpecific(tileEntity, 0, 78, 120, ItemElectricBase::class.java))
        this.addSlotToContainer(Slot(tileEntity, 1, 7, 44))
        var var3: Int

        //this.addSlotToContainer(new SlotSpecific(tileEntity, 2, 41, 14, new ItemStack(Items.dye, 1, 15)));
        val x: Int = tileEntity.moduleLevel

        //GalaxySpace.debug("Inv: " + x);
        var i: Int = 0
        while (i < x && x > 0) {
            this.addSlotToContainer(Slot(tileEntity, 2 + i * 2, 41, 77 - (22 * i)))
            this.addSlotToContainer(
                SlotFurnaceOutput(
                    par1InventoryPlayer.player,
                    tileEntity,
                    3 + i * 2,
                    108,
                    77 - (22 * i)
                )
            )
            this.addSlotToContainer(
                SlotFurnaceOutput(
                    par1InventoryPlayer.player,
                    tileEntity,
                    9 + i,
                    129,
                    77 - (22 * i)
                )
            )
            i++
        }

        this.addSlotToContainer(
            SlotSpecific(
                tileEntity,
                8,
                7,
                24,
                ItemStack(Items.DYE, 1, 15),
                BasicItems.DOLOMITE_MEAL.getItemStack()
            )
        )

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlotToContainer(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 157 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 215))
            ++var3
        }
    }

    override fun onContainerClosed(entityplayer: EntityPlayer) {
        super.onContainerClosed(entityplayer)
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    override fun detectAndSendChanges() {
        for (i: Int in this.inventorySlots.indices) {
            val itemstack: ItemStack = (this.inventorySlots.get(i) as Slot).getStack()
            var itemstack1: ItemStack = this.inventoryItemStacks.get(i)

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && (itemstack.getItem() != itemstack1.getItem())) {
                    if (i == 2) this.tileEntity.processTicks_0 = 0
                    if (i == 4) this.tileEntity.processTicks_1 = 0
                    if (i == 6) this.tileEntity.processTicks_2 = 0
                }


                val clientStackChanged: Boolean = !ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack1, itemstack)
                itemstack1 = if (itemstack.isEmpty()) ItemStack.EMPTY else itemstack.copy()
                this.inventoryItemStacks.set(i, itemstack1)

                if (clientStackChanged) for (j: Int in this.listeners.indices) {
                    (this.listeners.get(j) as IContainerListener).sendSlotContents(this, i, itemstack1)
                }
            }
        }
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

            if (par1 <= 9) {
                if (!this.mergeItemStack(var4, 9, 35, true)) {
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
                } else if (var4.getItem() === Items.WATER_BUCKET) {
                    if (!this.mergeItemStack(var4, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
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
