package micdoodle8.mods.galacticraft.planets.mars.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemAtmosphericValve
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityMethaneSynthesizer
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerMethaneSynthesizer constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityMethaneSynthesizer,
    player: EntityPlayer
) : Container() {
    init {
        // Electric Input Slot
        this.addSlot(SlotSpecific(tileEntity, 0, 53, 53, IItemElectric::class.java))

        // Input slot - hydrogen
        this.addSlot(Slot(tileEntity, 1, 7, 7))

        // Input slot - CO2
        this.addSlot(Slot(tileEntity, 2, 28, 7))

        // Carbon slot
        this.addSlot(SlotSpecific(tileEntity, 3, 28, 53, ItemStack(MarsItems.carbonFragments, 1, 0)))

        // Output slot
        this.addSlot(Slot(tileEntity, 4, 153, 7))
        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(
                    Slot(
                        par1InventoryPlayer,
                        var4 + var3 * 9 + 9,
                        8 + var4 * 18,
                        104 + var3 * 18 - 18
                    )
                )
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 144))
            ++var3
        }

        tileEntity.openInventory(player)
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
        this.tileEntity.closeInventory(entityplayer)
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
        val slot: Slot? = this.inventorySlots.get(par1)

        if (slot != null && slot.hasItem()) {
            val var4: ItemStack = slot.getItem()
            var2 = var4.copy()

            if (par1 < 5) {
                if (!this.moveItemStackTo(var4, 5, 41, true)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 2) {
                    slot.onQuickCraft(var4, var2)
                }
            } else {
                if (EnergyUtil.isElectricItem(var4.getItem())) {
                    if (!this.moveItemStackTo(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    if (var4.getItem() is ItemAtmosphericValve) {
                        if (!this.moveItemStackTo(var4, 2, 3, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (var4.getItem() === MarsItems.carbonFragments) {
                        if (!this.moveItemStackTo(var4, 3, 4, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (FluidUtil.isPartialContainer(var4, AsteroidsItems.methaneCanister)) {
                        if (!this.moveItemStackTo(var4, 4, 5, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 < 32) {
                        if (!this.moveItemStackTo(var4, 32, 41, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.moveItemStackTo(var4, 5, 32, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (var4.isEmpty()) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, var4)
        }

        return var2
    }
}
