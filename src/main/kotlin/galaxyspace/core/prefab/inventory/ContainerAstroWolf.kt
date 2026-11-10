package galaxyspace.core.prefab.inventory

import galaxyspace.core.prefab.inventory.InventoryAstroWolf
import galaxyspace.core.prefab.entities.EntityAstroWolf
import galaxyspace.core.prefab.inventory.ContainerAstroWolf
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import micdoodle8.mods.galacticraft.core.items.ItemCanisterOxygenInfinite
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerSlimeling
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import java.util.Collections

open class ContainerAstroWolf constructor(
    playerInventory: InventoryPlayer,
    wolf: EntityAstroWolf,
    player: EntityPlayer
) : Container() {
    private val wolfInventory: InventoryAstroWolf

    init {
        this.wolfInventory = wolf.wolfInventory
        this.wolfInventory.currentContainer = this
        this.wolfInventory.openInventory(player)

        addSlots(this, playerInventory, wolf)
    }

    /*
    public static void addAdditionalSlots(ContainerSlimeling container, EntitySlimeling slimeling, ItemStack stack)
    {
        if (!stack.isEmpty() && stack.getItem() == MarsItems.marsItemBasic && stack.getItemDamage() == 4)
        {
        	//Note that if NEI is installed, this can be called by InventorySlimeling.setInventorySlotContents even if the container already has the slots
        	if (container.inventorySlots.size() < 63)
        	{
        		for (int var3 = 0; var3 < 3; ++var3)
        		{
        			for (int var4 = 0; var4 < 9; ++var4)
        			{
        				Slot slot = new Slot(slimeling.slimelingInventory, var4 + var3 * 9 + 2, 8 + var4 * 18, 54 + var3 * 18);
        				slot.slotNumber = container.inventorySlots.size();
        				container.inventorySlots.add(slot);
        				container.inventoryItemStacks.add(ItemStack.EMPTY);
        			}
        		}
        	}
        }
    }*/
    override fun onContainerClosed(entityplayer: EntityPlayer) {
        //super.onContainerClosed(entityplayer);
        this.wolfInventory.closeInventory(entityplayer)
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.wolfInventory.isUsableByPlayer(par1EntityPlayer)
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, index: Int): ItemStack {
        var itemstack: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(index)

        if (slot != null && slot.getHasStack()) {
            val itemstack1: ItemStack = slot.getStack()
            itemstack = itemstack1.copy()

            if (index < this.wolfInventory.getSizeInventory()) {
                if (!this.mergeItemStack(
                        itemstack1,
                        this.wolfInventory.getSizeInventory(),
                        this.inventorySlots.size,
                        true
                    )
                ) {
                    return ItemStack.EMPTY
                }
            } else if (this.getSlot(0).isItemValid(itemstack1)) {
                if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (this.getSlot(1).isItemValid(itemstack1)) {
                if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY
                }
            }
            if (this.getSlot(2).isItemValid(itemstack1)) {
                if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (this.wolfInventory.getSizeInventory() <= 3 || !this.mergeItemStack(
                    itemstack1,
                    3,
                    this.wolfInventory.getSizeInventory(),
                    false
                )
            ) {
                return ItemStack.EMPTY
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }
        }

        return itemstack
    }

    companion object {
        @JvmStatic
        fun addSlots(container: ContainerAstroWolf, playerInventory: InventoryPlayer, wolf: EntityAstroWolf) {
            container.addSlotToContainer(SlotSpecific(wolf.wolfInventory, 0, 6, 15, ItemStack(GCItems.oxMask)))
            container.addSlotToContainer(
                SlotSpecific(
                    wolf.wolfInventory,
                    1,
                    6,
                    35,
                    BasicItems.WOLF_THERMAL_SUIT.getItemStack()
                )
            )
            container.addSlotToContainer(
                SlotSpecific(
                    wolf.wolfInventory,
                    2,
                    6,
                    55,
                    ItemOxygenTank::class.java,
                    ItemCanisterOxygenInfinite::class.java
                )
            )

            var var3: Int

            var3 = 0
            while (var3 < 3) {
                for (var4: Int in 0..8) {
                    container.addSlotToContainer(
                        Slot(
                            playerInventory,
                            var4 + var3 * 9 + 9,
                            8 + var4 * 18,
                            84 + var3 * 18
                        )
                    )
                }
                ++var3
            }

            var3 = 0
            while (var3 < 9) {
                container.addSlotToContainer(Slot(playerInventory, var3, 8 + var3 * 18, 142))
                ++var3
            }
        }

        @JvmStatic
        fun removeSlots(container: ContainerSlimeling) {
            Collections.copy<ItemStack?>(container.inventoryItemStacks, container.inventoryItemStacks.subList(0, 37))
            container.inventorySlots = container.inventorySlots.subList(0, 37)
        }
    }
}
