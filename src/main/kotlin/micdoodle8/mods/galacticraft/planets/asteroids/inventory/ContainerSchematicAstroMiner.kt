package micdoodle8.mods.galacticraft.planets.asteroids.inventory

import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult
import micdoodle8.mods.galacticraft.planets.mars.util.RecipeUtilMars
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ContainerSchematicAstroMiner constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) :
    Container() {
    @JvmField
    var craftMatrix: InventorySchematicAstroMiner = InventorySchematicAstroMiner(this)

    @JvmField
    var craftResult: IInventory = InventoryCraftResult()
    private val world: World

    init {
        this.world = par1InventoryPlayer.player.world
        this.addSlotToContainer(
            SlotRocketBenchResult(
                par1InventoryPlayer.player,
                this.craftMatrix,
                this.craftResult,
                0,
                142,
                72
            )
        )
        var i: Int
        var j: Int
        var count: Int = 1

        // Miner top layer
        i = 0
        while (i < 4) {
            this.addSlotToContainer(
                SlotSchematicAstroMiner(
                    this.craftMatrix,
                    count++,
                    27 + i * 18,
                    35,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            i++
        }

        // Miner mid layer
        i = 0
        while (i < 5) {
            this.addSlotToContainer(
                SlotSchematicAstroMiner(
                    this.craftMatrix,
                    count++,
                    16 + i * 18,
                    53,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            i++
        }

        // Miner bottom layer
        i = 0
        while (i < 3) {
            this.addSlotToContainer(
                SlotSchematicAstroMiner(
                    this.craftMatrix,
                    count++,
                    44 + i * 18,
                    71,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            i++
        }

        // Laser
        i = 0
        while (i < 2) {
            this.addSlotToContainer(
                SlotSchematicAstroMiner(
                    this.craftMatrix,
                    count++,
                    8 + i * 18,
                    77,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            ++i
        }

        // Player inv:
        i = 0
        while (i < 3) {
            j = 0
            while (j < 9) {
                this.addSlotToContainer(Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 140 + i * 18 - 26))
                ++j
            }
            ++i
        }

        i = 0
        while (i < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, i, 8 + i * 18, 144 + 54 - 26))
            ++i
        }

        this.onCraftMatrixChanged(this.craftMatrix)
    }

    override fun onContainerClosed(par1EntityPlayer: EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer)

        if (!this.world.isRemote) {
            for (var2: Int in 1..<this.craftMatrix.getSizeInventory()) {
                val var3: ItemStack = this.craftMatrix.removeStackFromSlot(var2)

                if (!var3.isEmpty()) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f)
                }
            }
        }
    }

    override fun onCraftMatrixChanged(par1IInventory: IInventory) {
        this.craftResult.setInventorySlotContents(0, RecipeUtilMars.findMatchingAstroMinerRecipe(this.craftMatrix))
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val var3: Slot? = this.inventorySlots.get(par1)

        if (var3 != null && var3.getHasStack()) {
            val var4: ItemStack = var3.getStack()
            var2 = var4.copy()

            val done: Boolean = false
            if (par1 <= 14) {
                if (!this.mergeItemStack(var4, 15, 51, false)) {
                    return ItemStack.EMPTY
                }

                var3.onSlotChange(var4, var2)
            } else {
                var valid: Boolean = false
                for (i: Int in 1..14) {
                    val testSlot: Slot = this.inventorySlots.get(i)
                    if (!testSlot.getHasStack() && testSlot.isItemValid(var2)) {
                        valid = true
                        break
                    }
                }
                if (valid) {
                    if (!this.mergeOneItemTestValid(var4, 1, 15, false)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    if (par1 >= 15 && par1 < 42) {
                        if (!this.mergeItemStack(var4, 42, 51, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 >= 42 && par1 < 51) {
                        if (!this.mergeItemStack(var4, 15, 42, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.mergeItemStack(var4, 15, 51, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (var4.isEmpty()) {
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

    protected open fun mergeOneItemTestValid(par1ItemStack: ItemStack, par2: Int, par3: Int, par4: Boolean): Boolean {
        var flag1: Boolean = false
        if (!par1ItemStack.isEmpty()) {
            var slot: Slot
            var slotStack: ItemStack

            for (k: Int in par2..<par3) {
                slot = this.inventorySlots.get(k)
                slotStack = slot.getStack()

                if (slotStack.isEmpty() && slot.isItemValid(par1ItemStack)) {
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
