package micdoodle8.mods.galacticraft.planets.asteroids.recipe

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemCanisterLiquidOxygen
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.item.crafting.ShapelessRecipes
import net.minecraft.util.NonNullList
import net.minecraft.world.World

open class CanisterRecipes constructor(stack: ItemStack, list: NonNullList<Ingredient?>) :
    ShapelessRecipes("canisters", stack, list) {
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    override fun matches(p_77569_1_: InventoryCrafting, p_77569_2_: World): Boolean {
        var itemCanister: ItemStack = ItemStack.EMPTY
        var itemTank: ItemStack = ItemStack.EMPTY

        for (i: Int in 0..<p_77569_1_.getSizeInventory()) {
            val itemstack1: ItemStack = p_77569_1_.getStackInSlot(i)

            if (!itemstack1.isEmpty()) {
                val testItem: Item = itemstack1.getItem()
                if (testItem is ItemCanisterLiquidOxygen || testItem === GCItems.oxygenCanisterInfinite) {
                    if (!itemCanister.isEmpty()) {
                        //Two canisters
                        return false
                    }

                    itemCanister = itemstack1
                } else {
                    if (testItem !is ItemOxygenTank || !itemTank.isEmpty()) {
                        //Something other than an oxygen tank
                        return false
                    }

                    itemTank = itemstack1
                }
            }
        }

        //Need one canister + one tank
        if (itemCanister.isEmpty() || itemTank.isEmpty()) {
            return false
        }

        //Empty canister
        if (itemCanister.getItemDamage() >= itemCanister.getMaxDamage()) {
            return false
        }

        //Full tank
        if (itemTank.getItemDamage() <= 0) {
            return false
        }

        return true
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
        var itemTank: ItemStack? = null //Intentionally null - internal use only
        var itemCanister: ItemStack? = null //Intentionally null - internal use only

        for (i: Int in 0..<inv.getSizeInventory()) {
            val itemstack1: ItemStack = inv.getStackInSlot(i)

            if (!itemstack1.isEmpty()) {
                val testItem: Item = itemstack1.getItem()
                if (testItem is ItemCanisterLiquidOxygen || testItem === GCItems.oxygenCanisterInfinite) {
                    //Intentional item null check
                    if (itemCanister != null) {
                        //Two canisters
                        return ItemStack.EMPTY
                    }

                    itemCanister = itemstack1
                } else {
                    //Intentional item null check
                    if (testItem !is ItemOxygenTank || itemTank != null) {
                        //Something other than an oxygen tank
                        return ItemStack.EMPTY
                    }

                    itemTank = itemstack1
                }
            }
        }

        //Need one canister + one tank (intentional item null check)
        if (itemCanister == null || itemTank == null) {
            return ItemStack.EMPTY
        }

        //Empty canister
        if (itemCanister.getItemDamage() >= itemCanister.getMaxDamage()) {
            return ItemStack.EMPTY
        }

        //Full tank
        if (itemTank.getItemDamage() <= 0) {
            return ItemStack.EMPTY
        }

        val oxygenAvail: Int = itemCanister.getMaxDamage() - itemCanister.getItemDamage()
        val oxygenToFill: Int = itemTank.getItemDamage() * 5 / 54

        if (oxygenAvail >= oxygenToFill) {
            val result: ItemStack = itemTank.copy()
            result.setItemDamage(0)
            if (itemCanister.getItem() is ItemCanisterLiquidOxygen) {
                ItemCanisterLiquidOxygen.Companion.saveDamage(itemCanister, itemCanister.getItemDamage() + oxygenToFill)
            }
            return result
        }

        val tankDamageNew: Int = (oxygenToFill - oxygenAvail) * 54 / 5
        val result: ItemStack = itemTank.copy()
        result.setItemDamage(tankDamageNew)
        if (itemCanister.getItem() is ItemCanisterLiquidOxygen) {
            ItemCanisterLiquidOxygen.Companion.saveDamage(itemCanister, itemCanister.getMaxDamage())
        }
        return result
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width * height >= 2
    }
}
