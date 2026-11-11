package micdoodle8.mods.galacticraft.core.util

import foundation.item.legacyItemValue

import foundation.persistence.containsTag

import javax.annotation.Nonnull

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.inventory.InventoryBuggyBench
import micdoodle8.mods.galacticraft.core.inventory.InventoryRocketBench
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.ShapedRecipes
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.registries.GameData
import java.util.HashMap

object RecipeUtil {
    @JvmStatic
    @Nonnull
    fun findMatchingBuggy(benchStacks: InventoryBuggyBench?): ItemStack {
        for (recipe in GalacticraftRegistry.getBuggyBenchRecipes()) {
            if ((recipe)!!.matches(benchStacks)) {
                return (recipe)!!.getRecipeOutput()
            }
        }

        return ItemStack.EMPTY
    }

    @JvmStatic
    @Nonnull
    fun findMatchingSpaceshipRecipe(inventoryRocketBench: InventoryRocketBench?): ItemStack {
        for (recipe in GalacticraftRegistry.getRocketT1Recipes()) {
            if ((recipe)!!.matches(inventoryRocketBench)) {
                return (recipe)!!.getRecipeOutput()
            }
        }

        return ItemStack.EMPTY
    }

    @JvmStatic
    fun addRecipe(result: ItemStack, obj: Array<Any?>) {
        val pattern: CraftingHelper.ShapedPrimer = CraftingHelper.parseShaped(*obj)
        addCustomRecipe(
            ShapedRecipes(
                result.getItem().getRegistryName().toString(),
                pattern.width,
                pattern.height,
                pattern.input,
                result
            )
        )
    }

    @JvmStatic
    fun addCustomRecipe(rec: IRecipe) {
        val modID: String = Loader.instance().activeModContainer()!!.getModId()
        var newLocation: ResourceLocation =
            ResourceLocation(modID, (rec.getRecipeOutput().getItem().getRegistryName())!!.getPath())
        if (CraftingManager.REGISTRY.containsKey(newLocation)) {
            var count: Int = 1
            val newNameBase: String = newLocation.getPath() + "_"
            while (CraftingManager.REGISTRY.containsKey(newLocation)) {
                newLocation = ResourceLocation.fromNamespaceAndPath(modID, newNameBase + count++)
            }
        }
        rec.setRegistryName(newLocation)

        GameData.register_impl<IRecipe?>(rec)
    }

    @JvmStatic
    fun addRocketBenchRecipe(result: ItemStack?, input: HashMap<Int, ItemStack?>?) {
        GalacticraftRegistry.addT1RocketRecipe(NasaWorkbenchRecipe((result)!!, (input)!!))
    }

    @JvmStatic
    fun addBuggyBenchRecipe(result: ItemStack?, input: HashMap<Int, ItemStack?>?) {
        GalacticraftRegistry.addMoonBuggyRecipe(NasaWorkbenchRecipe((result)!!, (input)!!))
    }

    /**
     * An extended version of areItemStackTagsEqual which ignores LevelUp's "NoPlacing" tag on mined blocks
     */
    @JvmStatic
    fun areItemStackTagsEqual(stackA: ItemStack, stackB: ItemStack): Boolean {
        if (ItemStack.areItemStackTagsEqual(stackA, stackB)) return true

        var query: NBTTagCompound? = null
        if (stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
            query = stackB.getTagCompound()
        } else if (stackA.getTagCompound() != null && stackB.getTagCompound() == null) {
            query = stackA.getTagCompound()
        }
        if (query != null) {
            if (query.getKeySet().size == 1 && query.containsTag("NoPlacing")) return true
        }

        return false
    }

    /**
     *
     * @param itemstack - it is assumed this one is not null in calling code
     * @param itemstack1
     * @return
     */
    @JvmStatic
    fun stacksMatch(itemstack: ItemStack, itemstack1: ItemStack): Boolean {
        return !itemstack1.isEmpty() && itemstack1.getItem() === itemstack.getItem() && (!itemstack.getHasSubtypes() || itemstack.legacyItemValue == itemstack1.legacyItemValue) && areItemStackTagsEqual(
            itemstack,
            itemstack1
        )
    }
}
