package galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe


import galaxyspace.core.GSItems
import galaxyspace.core.util.GSUtils
import galaxyspace.core.util.GSRecipeUtil
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicCone
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.SlotSchematicCone
import galaxyspace.core.GSBlocks
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

open class ContainerSchematicCone constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos?) : Container() {
    @JvmField
    var craftMatrix: InventorySchematicCone = InventorySchematicCone(this)

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

        // Cone
        this.addSlot(
            SlotSchematicCone(
                this.craftMatrix,
                1,
                44,
                13 + change,
                (pos)!!,
                par1InventoryPlayer.player
            )
        )

        /*// Body Left
        for (var6 = 0; var6 < 2; ++var6)
        {
       		this.addSlot(new SlotSchematicCone(this.craftMatrix, 2 + var6, 21, 18 + var6 * 18 + 16 + change, x, y, z, par1InventoryPlayer.player));
        }
        */
        // Body Right
        var6 = 0
        while (var6 < 3) {
            this.addSlot(
                SlotSchematicCone(
                    this.craftMatrix,
                    2 + var6,
                    22 + var6 * 19 + 3,
                    32 + change,
                    (pos)!!,
                    par1InventoryPlayer.player
                )
            )
            ++var6
        }


        // Body Center
        var6 = 0
        while (var6 < 4) {
            var7 = 0
            while (var7 < 5) {
                this.addSlot(
                    SlotSchematicCone(
                        this.craftMatrix,
                        var6 + var7 * 4 + 5,
                        6 + var7 * 19,
                        51 + var6 * 19 + change,
                        (pos)!!,
                        par1InventoryPlayer.player
                    )
                )
                ++var7
            }
            ++var6
        }

        /*
      
        // Left fins
        this.addSlot(new SlotSchematicCone(this.craftMatrix, 12, 21, 64 + change, x, y, z, par1InventoryPlayer.player));
        this.addSlot(new SlotSchematicCone(this.craftMatrix, 13, 21, 82 + change, x, y, z, par1InventoryPlayer.player));
        this.addSlot(new SlotSchematicCone(this.craftMatrix, 14, 21, 100 + change, x, y, z, par1InventoryPlayer.player));

        // Engine
        this.addSlot(new SlotSchematicCone(this.craftMatrix, 15, 48, 100 + change, x, y, z, par1InventoryPlayer.player));

        // Right fins
        this.addSlot(new SlotSchematicCone(this.craftMatrix, 16, 75, 64 + change, x, y, z, par1InventoryPlayer.player));
        this.addSlot(new SlotSchematicCone(this.craftMatrix, 17, 75, 82 + change, x, y, z, par1InventoryPlayer.player));
        this.addSlot(new SlotSchematicCone(this.craftMatrix, 18, 75, 100 + change, x, y, z, par1InventoryPlayer.player));

        // Rocket Old
        this.addSlot(new SlotSchematicCone(this.craftMatrix, 22, 142, 46 + change, x, y, z, par1InventoryPlayer.player));
        
        // Rocket Addons GS (23, 24, 25, 26)
        for (int var9 = 0; var9 < 4; var9++)
        {
        	this.addSlot(new SlotSchematicTier2Rocket(this.craftMatrix, 23 + var9, 92 + var9 * 18, 12 + change, x, y, z, par1InventoryPlayer.player));
        }
        
        // Addons
        for (int var8 = 0; var8 < 3; var8++)
        {
            this.addSlot(new SlotSchematicCone(this.craftMatrix, 19 + var8, 93 + var8 * 26, -15 + change, x, y, z, par1InventoryPlayer.player));
        }
        
        */
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
            GSUtils.findMatchingNASARecipe((GSRecipeUtil.getConeRecipes() as MutableList<INasaWorkbenchRecipe>), this.craftMatrix)
        )
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)

        if (slot != null && slot.hasItem()) {
            val var4: ItemStack = slot.getItem()
            var2 = var4.copy()

            if (par1 <= 24) {
                if (!this.moveItemStackTo(var4, 25, this.inventorySlots.size, false)) {
                    return ItemStack.EMPTY
                }
                /*if (par1 == 0)
                {
                    slot.onQuickCraft(var4, var2);
                }*/
            } else if (var2.getItem() === GSItems.HDP || var2.getItem() === MarsItems.marsItemBasic && var2.legacyItemValue == 3 || var2.getItem() === AsteroidsItems.basicItem && var2.legacyItemValue == 5) {
                if (!this.mergeOneItem(var4, 1, 25, false) /*&& !this.mergeOneItem(var4, 2, 3, false) 
	            		&& !this.mergeOneItem(var4, 4, 5, false)
	            		&& !this.mergeOneItem(var4, 5, 6, false)
	            		&& !this.mergeOneItem(var4, 9, 10, false)
	            		&& !this.mergeOneItem(var4, 10, 11, false)
	            		&& !this.mergeOneItem(var4, 14, 15, false)
	            		&& !this.mergeOneItem(var4, 15, 16, false)
	            		&& !this.mergeOneItem(var4, 19, 20, false)
	            		&& !this.mergeOneItem(var4, 20, 21, false)
	            		&& !this.mergeOneItem(var4, 24, 25, false)*/
                ) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === Item.getItemFromBlock(GSBlocks.FUTURE_GLASS_BASIC)) {
                if (!this.mergeOneItem(var4, 3, 4, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GCItems.oxygenVent) {
                if (!this.mergeOneItem(var4, 6, 7, false)
                    && !this.mergeOneItem(var4, 8, 9, false)
                ) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === Item.getItemFromBlock(GCBlocks.screen)) {
                if (!this.mergeOneItem(var4, 7, 8, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GCItems.oxygenFan) {
                if (!this.mergeOneItem(var4, 11, 12, false)
                    && !this.mergeOneItem(var4, 13, 14, false)
                ) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GCItems.partBuggy && var2.legacyItemValue == 1) {
                if (!this.mergeOneItem(var4, 12, 13, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === Item.getItemFromBlock(GCBlocks.sealableBlock) && var2.legacyItemValue == 14) {
                if (!this.mergeOneItem(var4, 16, 17, false)
                    && !this.mergeOneItem(var4, 18, 19, false)
                ) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GCItems.oxTankHeavy && ItemOxygenTank.used(var2) == 0) {
                if (!this.mergeOneItem(var4, 17, 18, false)) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GCItems.rocketEngine) {
                if (!this.mergeOneItem(var4, 21, 22, false)
                    && !this.mergeOneItem(var4, 23, 24, false)
                ) {
                    return ItemStack.EMPTY
                }
            } else if (var2.getItem() === GSItems.ROCKET_MODULES) {
                if (!this.mergeOneItem(var4, 22, 23, false)) {
                    return ItemStack.EMPTY
                }
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
