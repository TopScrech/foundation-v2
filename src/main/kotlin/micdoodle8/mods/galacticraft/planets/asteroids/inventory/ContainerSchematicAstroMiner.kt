package micdoodle8.mods.galacticraft.planets.asteroids.inventory

import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult
import micdoodle8.mods.galacticraft.planets.mars.util.RecipeUtilMars
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

open class ContainerSchematicAstroMiner constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) :
    Container() {
    @JvmField
    var craftMatrix: InventorySchematicAstroMiner = InventorySchematicAstroMiner(this)

    @JvmField
    var craftResult: IInventory = InventoryCraftResult()
    private val world: World

    init {
        this.world = par1InventoryPlayer.player.world
        this.addSlot(
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
            this.addSlot(
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
            this.addSlot(
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
            this.addSlot(
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
            this.addSlot(
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
                this.addSlot(Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 140 + i * 18 - 26))
                ++j
            }
            ++i
        }

        i = 0
        while (i < 9) {
            this.addSlot(Slot(par1InventoryPlayer, i, 8 + i * 18, 144 + 54 - 26))
            ++i
        }

        this.slotsChanged(this.craftMatrix)
    }

    override fun removed(par1EntityPlayer: EntityPlayer) {
        super.removed(par1EntityPlayer)

        if (!this.world.isClientSide) {
            for (var2: Int in 1..<this.craftMatrix.getSizeInventory()) {
                val var3: ItemStack = this.craftMatrix.removeStackFromSlot(var2)

                if (!var3.isEmpty()) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f)
                }
            }
        }
    }

    override fun slotsChanged(par1IInventory: IInventory) {
        this.craftResult.setItem(0, RecipeUtilMars.findMatchingAstroMinerRecipe(this.craftMatrix))
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val var3: Slot? = this.inventorySlots.get(par1)

        if (var3 != null && var3.hasItem()) {
            val var4: ItemStack = var3.getItem()
            var2 = var4.copy()

            val done: Boolean = false
            if (par1 <= 14) {
                if (!this.moveItemStackTo(var4, 15, 51, false)) {
                    return ItemStack.EMPTY
                }

                var3.onQuickCraft(var4, var2)
            } else {
                var valid: Boolean = false
                for (i: Int in 1..14) {
                    val testSlot: Slot = this.inventorySlots.get(i)
                    if (!testSlot.hasItem() && testSlot.mayPlace(var2)) {
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
                        if (!this.moveItemStackTo(var4, 42, 51, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 >= 42 && par1 < 51) {
                        if (!this.moveItemStackTo(var4, 15, 42, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.moveItemStackTo(var4, 15, 51, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (var4.isEmpty()) {
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

    protected open fun mergeOneItemTestValid(par1ItemStack: ItemStack, par2: Int, par3: Int, par4: Boolean): Boolean {
        var flag1: Boolean = false
        if (!par1ItemStack.isEmpty()) {
            var slot: Slot
            var slotStack: ItemStack

            for (k: Int in par2..<par3) {
                slot = this.inventorySlots.get(k)
                slotStack = slot.getItem()

                if (slotStack.isEmpty() && slot.mayPlace(par1ItemStack)) {
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
