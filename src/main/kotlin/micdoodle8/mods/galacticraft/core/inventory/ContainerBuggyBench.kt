package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ContainerBuggyBench constructor(
    par1InventoryPlayer: InventoryPlayer,
    pos: BlockPos?,
    player: EntityPlayer?
) : Container() {
    @JvmField
    var craftMatrix: InventoryBuggyBench = InventoryBuggyBench(this)

    @JvmField
    var craftResult: IInventory = InventoryCraftResult()
    private val world: World

    init {
        val change: Int = 27
        this.world = par1InventoryPlayer.player.world
        this.addSlotToContainer(
            SlotRocketBenchResult(
                par1InventoryPlayer.player,
                this.craftMatrix,
                this.craftResult,
                0,
                142,
                79 + change
            )
        )
        var var6: Int
        var var7: Int

        // Body
        var6 = 0
        while (var6 < 4) {
            var7 = 0
            while (var7 < 3) {
                this.addSlotToContainer(
                    SlotBuggyBench(
                        this.craftMatrix,
                        var7 * 4 + var6 + 1,
                        39 + var7 * 18,
                        14 + var6 * 18 + change,
                        (pos)!!,
                        par1InventoryPlayer.player
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 2) {
            var7 = 0
            while (var7 < 2) {
                this.addSlotToContainer(
                    SlotBuggyBench(
                        this.craftMatrix,
                        var7 * 2 + var6 + 13,
                        21 + var7 * 72,
                        14 + var6 * 54 + change,
                        (pos)!!,
                        par1InventoryPlayer.player
                    )
                )
                ++var7
            }
            ++var6
        }

        // Addons
        for (var8: Int in 0..2) {
            this.addSlotToContainer(
                SlotBuggyBench(
                    this.craftMatrix,
                    17 + var8,
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
                this.addSlotToContainer(
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
            this.addSlotToContainer(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 169 + change))
            ++var6
        }

        this.onCraftMatrixChanged(this.craftMatrix)
    }

    override fun onContainerClosed(par1EntityPlayer: EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer)

        if (!this.world.isRemote) {
            for (var2: Int in 1..<this.craftMatrix.getSizeInventory()) {
                val slot: ItemStack = this.craftMatrix.removeStackFromSlot(var2)

                if (!slot.isEmpty()) {
                    par1EntityPlayer.entityDropItem(slot, 0.0f)
                }
            }
        }
    }

    override fun onCraftMatrixChanged(par1IInventory: IInventory) {
        this.craftResult.setInventorySlotContents(0, RecipeUtil.findMatchingBuggy(this.craftMatrix))
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.getHasStack()) {
            val var4: ItemStack = slot.getStack()
            var2 = var4.copy()

            if (par1 < b - 36) {
                if (!this.mergeItemStack(var4, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 0) {
                    slot.onSlotChange(var4, var2)
                }
            } else {
                val i: Item = var4.getItem()
                if (i === GCItems.heavyPlatingTier1 || i === GCItems.partBuggy) {
                    for (j: Int in 1..19) {
                        if ((this.inventorySlots.get(j) as Slot).isItemValid(var4)) {
                            this.mergeOneItem(var4, j, j + 1, false)
                        }
                    }
                } else {
                    if (par1 < b - 9) {
                        if (!this.mergeItemStack(var4, b - 9, b, false)) {
                            return ItemStack.EMPTY
                        }
                    } else {
                        if (!this.mergeItemStack(var4, b - 36, b - 9, false)) {
                            return ItemStack.EMPTY
                        }
                    }
                }
            }

            if (var4.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY)
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onSlotChanged()
            slot.onTake(par1EntityPlayer, var4)
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
                slotStack = slot.getStack()

                if (slotStack.isEmpty()) {
                    val stackOneItem: ItemStack = par1ItemStack.copy()
                    stackOneItem.setCount(1)
                    par1ItemStack.shrink(1)
                    slot.putStack(stackOneItem)
                    slot.onSlotChanged()
                    flag1 = true
                    break
                }
            }
        }

        return flag1
    }
}
