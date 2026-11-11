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
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
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
    override fun removed(entityplayer: EntityPlayer) {
        //super.removed(entityplayer);
        this.wolfInventory.closeInventory(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.wolfInventory.stillValid(par1EntityPlayer)
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, index: Int): ItemStack {
        var itemstack: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(index)

        if (slot != null && slot.hasItem()) {
            val itemstack1: ItemStack = slot.getItem()
            itemstack = itemstack1.copy()

            if (index < this.wolfInventory.getContainerSize()) {
                if (!this.moveItemStackTo(
                        itemstack1,
                        this.wolfInventory.getContainerSize(),
                        this.inventorySlots.size,
                        true
                    )
                ) {
                    return ItemStack.EMPTY
                }
            } else if (this.getSlot(0).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (this.getSlot(1).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY
                }
            }
            if (this.getSlot(2).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 2, 3, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (this.wolfInventory.getContainerSize() <= 3 || !this.moveItemStackTo(
                    itemstack1,
                    3,
                    this.wolfInventory.getContainerSize(),
                    false
                )
            ) {
                return ItemStack.EMPTY
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
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
