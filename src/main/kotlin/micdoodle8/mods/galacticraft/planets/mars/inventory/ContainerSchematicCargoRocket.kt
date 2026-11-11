package micdoodle8.mods.galacticraft.planets.mars.inventory

import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult
import micdoodle8.mods.galacticraft.planets.mars.util.RecipeUtilMars
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

open class ContainerSchematicCargoRocket constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) :
    Container() {
    @JvmField
    var craftMatrix: InventorySchematicCargoRocket = InventorySchematicCargoRocket(this)

    @JvmField
    var craftResult: IInventory = InventoryCraftResult()
    private val world: World

    init {
        var change: Int = 27
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
        this.addSlot(
            SlotSchematicCargoRocket(
                this.craftMatrix,
                1,
                48,
                -9 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        this.addSlot(
            SlotSchematicCargoRocket(
                this.craftMatrix,
                2,
                48,
                -9 + 18 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        // Body
        var6 = 0
        while (var6 < 3) {
            this.addSlot(
                SlotSchematicCargoRocket(
                    this.craftMatrix,
                    3 + var6,
                    39,
                    -7 + var6 * 18 + 16 + 18 + change,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            ++var6
        }

        // Body Right
        var6 = 0
        while (var6 < 3) {
            this.addSlot(
                SlotSchematicCargoRocket(
                    this.craftMatrix,
                    6 + var6,
                    57,
                    -7 + var6 * 18 + 16 + 18 + change,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            ++var6
        }

        // Left fins
        this.addSlot(
            SlotSchematicCargoRocket(
                this.craftMatrix,
                9,
                21,
                63 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )
        this.addSlot(
            SlotSchematicCargoRocket(
                this.craftMatrix,
                10,
                21,
                81 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        // Engine
        this.addSlot(
            SlotSchematicCargoRocket(
                this.craftMatrix,
                11,
                48,
                81 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        // Right fins
        this.addSlot(
            SlotSchematicCargoRocket(
                this.craftMatrix,
                12,
                75,
                63 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )
        this.addSlot(
            SlotSchematicCargoRocket(
                this.craftMatrix,
                13,
                75,
                81 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        // Addons
        for (var8: Int in 0..2) {
            this.addSlot(
                SlotSchematicCargoRocket(
                    this.craftMatrix,
                    14 + var8,
                    93 + var8 * 26,
                    -15 + change,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
        }

        change = 9

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
                        129 + var6 * 18 + change
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 18 + 169 + change))
            ++var6
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
        this.craftResult.setItem(0, RecipeUtilMars.findMatchingCargoRocketRecipe(this.craftMatrix))
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

            var done: Boolean = false
            if (par1 <= 16) {
                if (!this.moveItemStackTo(var4, 17, 53, false)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 0) {
                    var3.onQuickCraft(var4, var2)
                }
            } else {
                for (i: Int in 1..13) {
                    val testSlot: Slot = this.inventorySlots.get(i)
                    if (!testSlot.hasItem() && testSlot.mayPlace(var2)) {
                        if (!this.mergeOneItem(var4, i, i + 1, false)) {
                            return ItemStack.EMPTY
                        }
                        done = true
                        break
                    }
                }

                if (!done) {
                    if (var2.getItem() === Item.getItemFromBlock(Blocks.CHEST) && !(this.inventorySlots.get(14) as Slot).hasItem()) {
                        if (!this.mergeOneItem(var4, 14, 15, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (var2.getItem() === Item.getItemFromBlock(Blocks.CHEST) && !(this.inventorySlots.get(15) as Slot).hasItem()) {
                        if (!this.mergeOneItem(var4, 15, 16, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (var2.getItem() === Item.getItemFromBlock(Blocks.CHEST) && !(this.inventorySlots.get(16) as Slot).hasItem()) {
                        if (!this.mergeOneItem(var4, 16, 17, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 >= 17 && par1 < 44) {
                        if (!this.moveItemStackTo(var4, 44, 53, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 >= 44 && par1 < 53) {
                        if (!this.moveItemStackTo(var4, 17, 44, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.moveItemStackTo(var4, 17, 53, false)) {
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

    protected open fun mergeOneItem(par1ItemStack: ItemStack, par2: Int, par3: Int, par4: Boolean): Boolean {
        var flag1: Boolean = false
        if (!par1ItemStack.isEmpty()) {
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
