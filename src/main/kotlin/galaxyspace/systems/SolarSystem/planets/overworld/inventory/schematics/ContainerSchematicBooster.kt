package galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe


import galaxyspace.core.GSItems
import galaxyspace.core.util.GSUtils
import galaxyspace.core.util.GSRecipeUtil
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicBooster
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.SlotSchematicBooster
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
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

open class ContainerSchematicBooster constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) :
    Container() {
    @JvmField
    var craftMatrix: InventorySchematicBooster = InventorySchematicBooster(this)

    @JvmField
    var craftResult: IInventory = InventoryCraftResult()
    private val worldObj: World

    init {
        val change: Int = 27
        this.worldObj = par1InventoryPlayer.player.world
        this.addSlotToContainer(
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

        this.addSlotToContainer(
            SlotSchematicBooster(
                this.craftMatrix,
                1,
                44,
                27 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )


        // Body Center
        var6 = 0
        while (var6 < 3) {
            var7 = 0
            while (var7 < 3) {
                this.addSlotToContainer(
                    SlotSchematicBooster(
                        this.craftMatrix,
                        var6 + var7 * 3 + 2,
                        25 + var7 * 19,
                        46 + var6 * 19 + change,
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
                this.addSlotToContainer(
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
            this.addSlotToContainer(Slot(par1InventoryPlayer, var6, 17 + var6 * 18, 18 + 184 + change))
            ++var6
        }

        this.onCraftMatrixChanged(this.craftMatrix)
    }

    override fun onContainerClosed(par1EntityPlayer: EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer)

        if (!this.worldObj.isRemote) {
            for (var2: Int in 1..<this.craftMatrix.getSizeInventory()) {
                val var3: ItemStack = this.craftMatrix.removeStackFromSlot(var2)

                if (!var3.isEmpty()) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f)
                }
            }
        }
    }

    override fun onCraftMatrixChanged(par1IInventory: IInventory) {
        this.craftResult.setInventorySlotContents(
            0,
            GSUtils.findMatchingNASARecipe((GSRecipeUtil.getBoosterRecipes() as MutableList<INasaWorkbenchRecipe>), this.craftMatrix)
        )
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.getHasStack()) {
            val var4: ItemStack = slot.getStack()
            var2 = var4.copy()

            if (par1 <= 10) {
                if (!this.mergeItemStack(var4, 11, this.inventorySlots.size, false)) {
                    return ItemStack.EMPTY
                }
                if (par1 == 0) {
                    slot.onSlotChange(var4, var2)
                }
            } else if (var2.getItem() === GSItems.HDP || var2.getItem() === MarsItems.marsItemBasic && var2.getItemDamage() == 3 || var2.getItem() === AsteroidsItems.basicItem && var2.getItemDamage() == 5) {
                if (!this.mergeOneItem(var4, 1, 3, false) && !this.mergeOneItem(
                        var4,
                        4,
                        6,
                        false
                    ) && !this.mergeOneItem(var4, 7, 9, false) && !this.mergeOneItem(var4, 10, 11, false)
                ) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GCItems.fuelCanister) {
                if (!this.mergeOneItem(var4, 3, 4, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === Item.getItemFromBlock(GCBlocks.oxygenPipe)) {
                if (!this.mergeOneItem(var4, 6, 7, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === AsteroidsItems.basicItem && var2.getItemDamage() == 1 || var2.getItem() === GCItems.rocketEngine) {
                if (!this.mergeOneItem(var4, 9, 10, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (var4.getCount() == 0) {
                if (par1 == 0) {
                    slot.onTake(par1EntityPlayer, var4)
                }
                slot.putStack(ItemStack.EMPTY)
                return var2
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, var4)
            if (par1 == 0) {
                slot.onSlotChanged()
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
                slotStack = slot.getStack()

                if (slotStack == null) {
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
