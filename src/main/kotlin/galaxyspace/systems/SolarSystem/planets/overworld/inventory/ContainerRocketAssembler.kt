package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import foundation.item.legacyItemValue

import galaxyspace.core.prefab.inventory.SlotUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityRocketAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.SlotRocketAssembly
import galaxyspace.core.GSItems
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.inventory.SlotFurnaceOutput
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

open class ContainerRocketAssembler constructor(
    par1InventoryPlayer: InventoryPlayer,
    private var tileEntity: TileEntityRocketAssembler
) : Container() {
    init {
        tileEntity.rocketCraftMatrix.craftingEventHandler = this


        // Battery Slot
        this.addSlot(SlotSpecific(tileEntity, 0, 183, 142, ItemElectricBase::class.java))

        // Smelting result
        this.addSlot(SlotFurnaceOutput(par1InventoryPlayer.player, tileEntity, 1, 174, 87))


        //Cone
        this.addSlot(SlotRocketAssembly(tileEntity.rocketCraftMatrix, 2, 40, 25))
        //Bodies
        this.addSlot(SlotRocketAssembly(tileEntity.rocketCraftMatrix, 3, 40, 55))
        this.addSlot(SlotRocketAssembly(tileEntity.rocketCraftMatrix, 4, 40, 85))
        //Engine
        this.addSlot(SlotRocketAssembly(tileEntity.rocketCraftMatrix, 5, 40, 110))
        //Boosters
        this.addSlot(SlotRocketAssembly(tileEntity.rocketCraftMatrix, 6, 16, 90))
        this.addSlot(SlotRocketAssembly(tileEntity.rocketCraftMatrix, 7, 64, 90))
        //Stabilizers
        this.addSlot(SlotRocketAssembly(tileEntity.rocketCraftMatrix, 8, 16, 120))
        this.addSlot(SlotRocketAssembly(tileEntity.rocketCraftMatrix, 9, 64, 120))
        this.addSlot(SlotRocketAssembly(tileEntity.rocketCraftMatrix, 10, 100, 35))

        for (i: Int in 0..2) this.addSlot(
            SlotRocketAssembly(
                tileEntity.rocketCraftMatrix,
                11 + i,
                130 + (21 * i),
                35
            )
        )


        //for (int i = 0; i < 3; ++i)
        // this.addSlot(new Slot(tileEntity, 2, 85 + 21 * 4, 35));        

        //this.addSlot(new SlotRocketAssembly(tileEntity.rocketCraftMatrix, 11, 159, 35));
        //this.addSlot(new SlotRocketAssembly(tileEntity.rocketCraftMatrix, 12, 178, 35));
        var var3: Int
        var3 = 0
        while (var3 < 4) {
            this.addSlot(
                SlotUpgrades(
                    tileEntity,
                    3 + var3,
                    207,
                    11 + (21 * var3),
                    1,
                    ItemUpgrades::class.java
                )
            )
            ++var3
        }

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 24 + var4 * 18, 178 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 24 + var3 * 18, 235))
            ++var3
        }
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    override fun slotsChanged(par1IInventory: IInventory) {
        super.slotsChanged(par1IInventory)
        this.tileEntity.updateInput()
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

            if (par1 <= 14) {
                if (!this.moveItemStackTo(var4, 14, this.inventorySlots.size, false)) {
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
                } else if (var4.getItem() === GSItems.ROCKET_PARTS) {
                    if (var4.legacyItemValue == 0 || var4.legacyItemValue == 5 || var4.legacyItemValue == 10 || var4.legacyItemValue == 15 || var4.legacyItemValue == 20) {
                        if (!this.moveItemStackTo(var4, 2, 3, false)) {
                            return ItemStack.EMPTY
                        }
                    }

                    if (var4.legacyItemValue == 1 || var4.legacyItemValue == 6 || var4.legacyItemValue == 11 || var4.legacyItemValue == 16 || var4.legacyItemValue == 21) {
                        if (!this.moveItemStackTo(var4, 3, 5, false)) {
                            return ItemStack.EMPTY
                        }
                    }

                    if (var4.legacyItemValue == 2 || var4.legacyItemValue == 7 || var4.legacyItemValue == 12 || var4.legacyItemValue == 17 || var4.legacyItemValue == 22) {
                        if (!this.moveItemStackTo(var4, 5, 6, false)) {
                            return ItemStack.EMPTY
                        }
                    }

                    if (var4.legacyItemValue == 3 || var4.legacyItemValue == 8 || var4.legacyItemValue == 13 || var4.legacyItemValue == 18 || var4.legacyItemValue == 23) {
                        if (!this.moveItemStackTo(var4, 6, 8, false)) {
                            return ItemStack.EMPTY
                        }
                    }

                    if (var4.legacyItemValue == 4 || var4.legacyItemValue == 9 || var4.legacyItemValue == 14 || var4.legacyItemValue == 19 || var4.legacyItemValue == 24) {
                        if (!this.moveItemStackTo(var4, 8, 10, false)) {
                            return ItemStack.EMPTY
                        }
                    }
                } else if (var4.getItem() === Item.getItemFromBlock(Blocks.CHEST)) {
                    if (!this.moveItemStackTo(var4, 10, 13, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (var4.getItem() === GSItems.ROCKET_MODULES) {
                    if (!this.moveItemStackTo(var4, 13, 14, false)) {
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
    } /*
    protected boolean mergeOneItem(ItemStack par1ItemStack, int par2, int par3, boolean par4)
    {
        boolean flag1 = false;
        if (par1ItemStack.getCount() > 0)
        {
            Slot slot;
            ItemStack slotStack;

            for (int k = par2; k < par3; k++)
            {
                slot = (Slot) this.inventorySlots.get(k);
                slotStack = slot.getStack();

                if (slotStack == null)
                {
                    ItemStack stackOneItem = par1ItemStack.copy();
                    stackOneItem.setCount(1);
                    par1ItemStack.shrink(1);
                    slot.putStack(stackOneItem);
                    slot.setChanged();
                    flag1 = true;
                    break;
                }
            }
        }

        return flag1;
    }*/
}
