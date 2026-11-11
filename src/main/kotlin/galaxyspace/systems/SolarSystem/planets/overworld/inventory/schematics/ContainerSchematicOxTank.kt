package galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe


import galaxyspace.core.util.GSUtils
import galaxyspace.core.util.GSRecipeUtil
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicOxTank
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.SlotSchematicOxTank
import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

open class ContainerSchematicOxTank constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) :
    Container() {
    @JvmField
    var craftMatrix: InventorySchematicOxTank = InventorySchematicOxTank(this)

    @JvmField
    var craftResult: IInventory = InventoryCraftResult()
    private val worldObj: World

    init {
        val change: Int = 27
        this.worldObj = par1InventoryPlayer.player.world
        this.addSlot(
            SlotRocketBenchResult(
                par1InventoryPlayer.player,
                this.craftMatrix,
                this.craftResult,
                0,
                140,
                95 + change
            )
        )
        var var6: Int
        var var7: Int


        // Body Center
        var6 = 0
        while (var6 < 4) {
            var7 = 0
            while (var7 < 3) {
                this.addSlot(
                    SlotSchematicOxTank(
                        this.craftMatrix,
                        var6 + var7 * 4 + 1,
                        26 + var7 * 19,
                        26 + var6 * 19 + change,
                        (pos)!!,
                        par1InventoryPlayer.player
                    )
                )
                ++var7
            }
            ++var6
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
                        17 + var7 * 18,
                        144 + var6 * 18 + change
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var6, 17 + var6 * 18, 18 + 184 + change))
            ++var6
        }

        this.slotsChanged(this.craftMatrix)
    }

    override fun removed(par1EntityPlayer: EntityPlayer) {
        super.removed(par1EntityPlayer)

        if (!this.worldObj.isClientSide) {
            for (var2: Int in 1..<this.craftMatrix.getSizeInventory()) {
                val var3: ItemStack = this.craftMatrix.removeStackFromSlot(var2)

                if (!var3.isEmpty()) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f)
                }
            }
        }
    }

    override fun slotsChanged(par1IInventory: IInventory) {
        this.craftResult.setItem(
            0,
            GSUtils.findMatchingNASARecipe((GSRecipeUtil.getOxTankRecipes() as MutableList<INasaWorkbenchRecipe>), this.craftMatrix)
        )
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.hasItem()) {
            val var4: ItemStack = slot.getItem()
            var2 = var4.copy()

            if (par1 <= 12) {
                if (!this.moveItemStackTo(var4, 13, this.inventorySlots.size, false)) {
                    return ItemStack.EMPTY
                }
                if (par1 == 0) {
                    slot.onQuickCraft(var4, var2)
                }
            } else if (!this.mergeOneItem(var4, 1, 13, false)) {
                return ItemStack.EMPTY
            }

            if (var4.getCount() == 0) {
                if (par1 == 0) {
                    slot.onTake(par1EntityPlayer, var4)
                }
                slot.set(ItemStack.EMPTY)
                return var2
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, var4)
            if (par1 == 0) {
                slot.setChanged()
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
                slot = this.inventorySlots.get(k) as Slot
                slotStack = slot.getItem()

                if (slotStack == null) {
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
