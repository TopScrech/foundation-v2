package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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
        this.addSlotToContainer(
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
        this.addSlotToContainer(SlotRocketBench(this.craftMatrix, 1, 48, -8 + change, (pos)!!, par1InventoryPlayer.player))

        // Body
        var6 = 0
        while (var6 < 4) {
            this.addSlotToContainer(
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
            this.addSlotToContainer(
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
        this.addSlotToContainer(SlotRocketBench(this.craftMatrix, 10, 21, 64 + change, (pos)!!, par1InventoryPlayer.player))
        this.addSlotToContainer(SlotRocketBench(this.craftMatrix, 11, 21, 82 + change, (pos)!!, par1InventoryPlayer.player))

        // Engine
        this.addSlotToContainer(SlotRocketBench(this.craftMatrix, 12, 48, 82 + change, (pos)!!, par1InventoryPlayer.player))

        // Right fins
        this.addSlotToContainer(SlotRocketBench(this.craftMatrix, 13, 75, 64 + change, (pos)!!, par1InventoryPlayer.player))
        this.addSlotToContainer(SlotRocketBench(this.craftMatrix, 14, 75, 82 + change, (pos)!!, par1InventoryPlayer.player))

        // Addons
        for (var8: Int in 0..2) {
            this.addSlotToContainer(
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
            for (var2: Int in 1..17) {
                val var3: ItemStack = this.craftMatrix.removeStackFromSlot(var2)

                if (!var3.isEmpty()) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f)
                }
            }
        }
    }

    override fun onCraftMatrixChanged(par1IInventory: IInventory) {
        this.craftResult.setInventorySlotContents(0, RecipeUtil.findMatchingSpaceshipRecipe(this.craftMatrix))
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
        val var3: Slot? = this.inventorySlots.get(par1)

        if (var3 != null && var3.getHasStack()) {
            val var4: ItemStack = var3.getStack()
            var2 = var4.copy()

            if (par1 <= 17) {
                if (!this.mergeItemStack(var4, 18, 54, false)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 0) {
                    var3.onSlotChange(var4, var2)
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
                if (!this.mergeItemStack(var4, 45, 54, false)) {
                    return ItemStack.EMPTY
                }
            } else if (par1 >= 45 && par1 < 54) {
                if (!this.mergeItemStack(var4, 18, 45, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (var4.getCount() == 0) {
                if (par1 == 0) {
                    var3.onTake(par1EntityPlayer, var4)
                }
                var3.putStack(ItemStack.EMPTY)
                return var2
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            var3.onTake(par1EntityPlayer, var4)
            if (par1 == 0) {
                var3.onSlotChanged()
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
