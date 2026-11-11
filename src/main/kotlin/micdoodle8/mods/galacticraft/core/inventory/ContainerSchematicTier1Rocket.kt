package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

open class ContainerSchematicTier1Rocket constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) :
    Container() {
    @JvmField
    var craftMatrix: InventoryRocketBench = InventoryRocketBench(this)

    @JvmField
    var craftResult: IInventory = InventoryCraftResult()
    private val world: World

    init {
        val change: Int = 27
        this.world = par1InventoryPlayer.player.world
        this.addSlot(
            SlotRocketBenchResult(
                par1InventoryPlayer.player,
                this.craftMatrix,
                this.craftResult,
                0,
                142,
                69 + change
            )
        )
        var var6: Int
        var var7: Int

        // Cone
        this.addSlot(SlotRocketBench(this.craftMatrix, 1, 48, -8 + change, (pos)!!, par1InventoryPlayer.player))

        // Body
        var6 = 0
        while (var6 < 4) {
            this.addSlot(
                SlotRocketBench(
                    this.craftMatrix,
                    2 + var6,
                    39,
                    -6 + var6 * 18 + 16 + change,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            ++var6
        }

        // Body Right
        var6 = 0
        while (var6 < 4) {
            this.addSlot(
                SlotRocketBench(
                    this.craftMatrix,
                    6 + var6,
                    57,
                    -6 + var6 * 18 + 16 + change,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            ++var6
        }

        // Left fins
        this.addSlot(SlotRocketBench(this.craftMatrix, 10, 21, 64 + change, (pos)!!, par1InventoryPlayer.player))
        this.addSlot(SlotRocketBench(this.craftMatrix, 11, 21, 82 + change, (pos)!!, par1InventoryPlayer.player))

        // Engine
        this.addSlot(SlotRocketBench(this.craftMatrix, 12, 48, 82 + change, (pos)!!, par1InventoryPlayer.player))

        // Right fins
        this.addSlot(SlotRocketBench(this.craftMatrix, 13, 75, 64 + change, (pos)!!, par1InventoryPlayer.player))
        this.addSlot(SlotRocketBench(this.craftMatrix, 14, 75, 82 + change, (pos)!!, par1InventoryPlayer.player))

        // Addons
        for (var8: Int in 0..2) {
            this.addSlot(
                SlotRocketBench(
                    this.craftMatrix,
                    15 + var8,
                    93 + var8 * 26,
                    -15 + change,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
        }

        // Player inv:
        var6 = 0
        while (var6 < 3) {
            var7 = 0
            while (var7 < 9) {
                this.addSlot(
                    Slot(
                        par1InventoryPlayer,
                        var7 + var6 * 9 + 9,
                        8 + var7 * 18,
                        111 + var6 * 18 + change
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 169 + change))
            ++var6
        }

        this.slotsChanged(this.craftMatrix)
    }

    override fun removed(par1EntityPlayer: EntityPlayer) {
        super.removed(par1EntityPlayer)

        if (!this.world.isClientSide) {
            for (var2: Int in 1..17) {
                val var3: ItemStack = this.craftMatrix.removeStackFromSlot(var2)

                if (!var3.isEmpty()) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f)
                }
            }
        }
    }

    override fun slotsChanged(par1IInventory: IInventory) {
        this.craftResult.setItem(0, RecipeUtil.findMatchingSpaceshipRecipe(this.craftMatrix))
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return true
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

            if (par1 <= 17) {
                if (!this.moveItemStackTo(var4, 18, 54, false)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 0) {
                    var3.onQuickCraft(var4, var2)
                }
            } else if (var2.getItem() === GCItems.partNoseCone) {
                if (!this.mergeOneItem(var4, 1, 2, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GCItems.heavyPlatingTier1) {
                if (!this.mergeOneItem(var4, 2, 10, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GCItems.partFins) {
                if (!this.mergeOneItem(var4, 10, 12, false) && !this.mergeOneItem(var4, 13, 15, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GCItems.rocketEngine) {
                if (!this.mergeOneItem(var4, 12, 13, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === Item.getItemFromBlock(Blocks.CHEST)) {
                if (!this.mergeOneItem(var4, 15, 18, false)) {
                    return ItemStack.EMPTY
                }
            } else if (par1 >= 18 && par1 < 45) {
                if (!this.moveItemStackTo(var4, 45, 54, false)) {
                    return ItemStack.EMPTY
                }
            } else if (par1 >= 45 && par1 < 54) {
                if (!this.moveItemStackTo(var4, 18, 45, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (var4.getCount() == 0) {
                if (par1 == 0) {
                    var3.onTake(par1EntityPlayer, var4)
                }
                var3.set(ItemStack.EMPTY)
                return var2
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            var3.onTake(par1EntityPlayer, var4)
            if (par1 == 0) {
                var3.setChanged()
            }
        }

        return var2
    }

    protected open fun mergeOneItem(par1ItemStack: ItemStack, par2: Int, par3: Int, par4: Boolean): Boolean {
        var flag1: Boolean = false
        if (par1ItemStack.getCount() > 0) {
            var slot: Slot
            var slotStack: ItemStack

            for (k: Int in par2..<par3) {
                slot = this.inventorySlots.get(k)
                slotStack = slot.getItem()

                if (slotStack.isEmpty()) {
                    val stackOneItem: ItemStack = par1ItemStack.copy()
                    stackOneItem.setCount(1)
                    par1ItemStack.shrink(1)
                    slot.set(stackOneItem)
                    slot.setChanged()
                    flag1 = true
                    break
                }
            }
        }

        return flag1
    }
}
