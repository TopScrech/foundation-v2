package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicBase
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.world.inventory.Slot
import net.minecraft.inventory.SlotFurnaceOutput
import net.minecraft.world.item.ItemStack

open class ContainerHydroponicBase constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityHydroponicBase
) : Container() {
    init {
        //this.addSlot(new SlotSpecific(tileEntity, 0, 7, 7, new ItemStack(GCItems.bucketFuel)));
        // Battery Slot
        this.addSlot(SlotSpecific(tileEntity, 0, 78, 120, ItemElectricBase::class.java))
        this.addSlot(Slot(tileEntity, 1, 7, 44))
        var var3: Int

        //this.addSlot(new SlotSpecific(tileEntity, 2, 41, 14, new ItemStack(Items.dye, 1, 15)));
        val x: Int = tileEntity.moduleLevel

        //GalaxySpace.debug("Inv: " + x);
        var i: Int = 0
        while (i < x && x > 0) {
            this.addSlot(Slot(tileEntity, 2 + i * 2, 41, 77 - (22 * i)))
            this.addSlot(
                SlotFurnaceOutput(
                    par1InventoryPlayer.player,
                    tileEntity,
                    3 + i * 2,
                    108,
                    77 - (22 * i)
                )
            )
            this.addSlot(
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

        this.addSlot(
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
                this.addSlot(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 157 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 215))
            ++var3
        }
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    override fun broadcastChanges() {
        for (i: Int in this.inventorySlots.indices) {
            val itemstack: ItemStack = (this.inventorySlots.get(i) as Slot).getItem()
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
    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val var3: Slot? = this.inventorySlots.get(par1)

        if (var3 != null && var3.hasItem()) {
            val var4: ItemStack = var3.getItem()
            var2 = var4.copy()

            if (par1 <= 9) {
                if (!this.moveItemStackTo(var4, 9, 35, true)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 1) {
                    var3.onQuickCraft(var4, var2)
                }
            } else {
                if (var4.getItem() is IItemElectric) {
                    if (!this.moveItemStackTo(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (var4.getItem() === Items.WATER_BUCKET) {
                    if (!this.moveItemStackTo(var4, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                }
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
