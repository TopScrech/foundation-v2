package micdoodle8.mods.galacticraft.planets.mars.inventory

import foundation.item.legacyItemValue

import java.util.ArrayList
import java.util.LinkedList

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityTerraformer
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockBush
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*

open class ContainerTerraformer constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityTerraformer,
    player: EntityPlayer
) : Container() {
    init {
        this.addSlot(
            SlotSpecific(
                tileEntity,
                0,
                25,
                19,
                ItemStack(Items.WATER_BUCKET),
                ItemStack(Items.BUCKET)
            )
        )

        this.addSlot(SlotSpecific(tileEntity, 1, 25, 39, IItemElectric::class.java))

        var var6: Int
        var var7: Int

        var6 = 0
        while (var6 < 3) {
            val stacks: MutableList<ItemStack?> = ArrayList<ItemStack?>()

            if (var6 == 0) {
                stacks.add(ItemStack(Items.DYE, 1, 15))
            } else if (var6 == 1) {
                if (saplingList == null) {
                    initSaplingList()
                }

                stacks.addAll(saplingList!!)
            } else if (var6 == 2) {
                stacks.add(ItemStack(Items.WHEAT_SEEDS))
            }

            var7 = 0
            while (var7 < 4) {
                this.addSlot(
                    SlotSpecific(
                        tileEntity,
                        var7 + var6 * 4 + 2,
                        25 + var7 * 18,
                        63 + var6 * 24,
                        *stacks.toTypedArray<ItemStack?>()
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 3) {
            var7 = 0
            while (var7 < 9) {
                this.addSlot(Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 155 + var6 * 18))
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 213))
            ++var6
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

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.hasItem()) {
            val var4: ItemStack = slot.getItem()
            var2 = var4.copy()

            if (par1 < b - 36) {
                if (!this.moveItemStackTo(var4, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(var4.getItem())) {
                    if (!this.moveItemStackTo(var4, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (var4.getItem() === Items.WATER_BUCKET) {
                    if (!this.moveItemStackTo(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (var4.getItem() === Items.DYE && var4.legacyItemValue == 15) {
                    if (!this.moveItemStackTo(var4, 2, 6, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (this.getSlot(6).mayPlace(var4)) {
                    if (!this.moveItemStackTo(var4, 6, 10, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (var4.getItem() === Items.WHEAT_SEEDS) {
                    if (!this.moveItemStackTo(var4, 10, 14, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par1 < b - 9) {
                    if (!this.moveItemStackTo(var4, b - 9, b, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.moveItemStackTo(var4, b - 36, b - 9, false)) {
                    return ItemStack.EMPTY
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

    companion object {
        private var saplingList: LinkedList<ItemStack>? = null

        @JvmStatic
        fun isOnSaplingList(stack: ItemStack): Boolean {
            if (saplingList == null) {
                initSaplingList()
            }

            for (sapling: ItemStack in saplingList!!) {
                if (sapling.isItemEqual(stack)) {
                    return true
                }
            }

            return false
        }

        @JvmStatic
        private fun initSaplingList() {
            saplingList = LinkedList<ItemStack>()

            for (b: Block? in Block.REGISTRY) {
                if (b is BlockBush) {
                    try {
                        val item: Item = Item.getItemFromBlock(b)
                        if (item !== Items.AIR) {
                            //item.getSubItems(item, null, subItemsList); - can't use because clientside only
                            saplingList!!.add(ItemStack(item, 1, 0))
                            val basicName: String? = item.getTranslationKey(ItemStack(item, 1, 0))
                            for (i: Int in 1..15) {
                                val testStack: ItemStack = ItemStack(item, 1, i)
                                val testName: String = item.getTranslationKey(testStack)
                                if (testName == "" || testName == basicName) {
                                    break
                                }
                                saplingList!!.add(testStack)
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}
