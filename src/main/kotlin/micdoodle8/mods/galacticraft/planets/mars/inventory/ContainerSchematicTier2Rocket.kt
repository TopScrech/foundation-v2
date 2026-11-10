package micdoodle8.mods.galacticraft.planets.mars.inventory

import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult
import micdoodle8.mods.galacticraft.planets.mars.util.RecipeUtilMars
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

open class ContainerSchematicTier2Rocket constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) :
    Container() {
    @JvmField
    var craftMatrix: InventorySchematicTier2Rocket = InventorySchematicTier2Rocket(this)

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
                18 + 69 + change
            )
        )
        var var6: Int
        var var7: Int

        // Cone
        this.addSlotToContainer(
            SlotSchematicTier2Rocket(
                this.craftMatrix,
                1,
                48,
                -8 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        // Body
        var6 = 0
        while (var6 < 5) {
            this.addSlotToContainer(
                SlotSchematicTier2Rocket(
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
        while (var6 < 5) {
            this.addSlotToContainer(
                SlotSchematicTier2Rocket(
                    this.craftMatrix,
                    7 + var6,
                    57,
                    -6 + var6 * 18 + 16 + change,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            ++var6
        }

        // Left fins
        this.addSlotToContainer(
            SlotSchematicTier2Rocket(
                this.craftMatrix,
                12,
                21,
                64 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )
        this.addSlotToContainer(
            SlotSchematicTier2Rocket(
                this.craftMatrix,
                13,
                21,
                82 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )
        this.addSlotToContainer(
            SlotSchematicTier2Rocket(
                this.craftMatrix,
                14,
                21,
                100 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        // Engine
        this.addSlotToContainer(
            SlotSchematicTier2Rocket(
                this.craftMatrix,
                15,
                48,
                100 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        // Right fins
        this.addSlotToContainer(
            SlotSchematicTier2Rocket(
                this.craftMatrix,
                16,
                75,
                64 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )
        this.addSlotToContainer(
            SlotSchematicTier2Rocket(
                this.craftMatrix,
                17,
                75,
                82 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )
        this.addSlotToContainer(
            SlotSchematicTier2Rocket(
                this.craftMatrix,
                18,
                75,
                100 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        // Addons
        for (var8: Int in 0..2) {
            this.addSlotToContainer(
                SlotSchematicTier2Rocket(
                    this.craftMatrix,
                    19 + var8,
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
                        129 + var6 * 18 + change
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 18 + 169 + change))
            ++var6
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
        this.craftResult.setInventorySlotContents(0, RecipeUtilMars.findMatchingSpaceshipT2Recipe(this.craftMatrix))
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

            var done: Boolean = false
            if (par1 <= 21) {
                if (!this.mergeItemStack(var4, 22, 58, false)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 0) {
                    var3.onSlotChange(var4, var2)
                }
            } else {
                for (i: Int in 1..18) {
                    val testSlot: Slot = this.inventorySlots.get(i) as Slot
                    if (!testSlot.getHasStack() && testSlot.isItemValid(var2)) {
                        if (!this.mergeOneItem(var4, i, i + 1, false)) {
                            return ItemStack.EMPTY
                        }
                        done = true
                        break
                    }
                }

                if (!done) {
                    if (var2.getItem() === Item.getItemFromBlock(Blocks.CHEST) && !(this.inventorySlots.get(19) as Slot).getHasStack()) {
                        if (!this.mergeOneItem(var4, 19, 20, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (var2.getItem() === Item.getItemFromBlock(Blocks.CHEST) && !(this.inventorySlots.get(20) as Slot).getHasStack()) {
                        if (!this.mergeOneItem(var4, 20, 21, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (var2.getItem() === Item.getItemFromBlock(Blocks.CHEST) && !(this.inventorySlots.get(21) as Slot).getHasStack()) {
                        if (!this.mergeOneItem(var4, 21, 22, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 >= 22 && par1 < 49) {
                        if (!this.mergeItemStack(var4, 49, 58, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 >= 49 && par1 < 58) {
                        if (!this.mergeItemStack(var4, 22, 49, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.mergeItemStack(var4, 22, 58, false)) {
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

    protected open fun mergeOneItem(par1ItemStack: ItemStack, par2: Int, par3: Int, par4: Boolean): Boolean {
        var flag1: Boolean = false
        if (!par1ItemStack.isEmpty()) {
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
