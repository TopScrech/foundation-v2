package micdoodle8.mods.galacticraft.core.recipe

import foundation.item.legacyItemValue

import net.minecraft.world.level.block.Block
import net.minecraft.world.inventory.CraftingContainer as InventoryCrafting
import foundation.inventory.getSizeInventory
import foundation.inventory.getStackInSlot
import foundation.inventory.getStackInRowAndColumn
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.core.NonNullList
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistryEntry
import java.lang.RuntimeException
import java.util.HashMap

open class ShapedRecipeNBT constructor(private val recipeOutput: ItemStack?, layout: Array<Any?>) :
    IForgeRegistryEntry.Impl<IRecipe?>(), IRecipe {
    @JvmField
    var recipeWidth: Int

    @JvmField
    var recipeHeight: Int

    @JvmField
    val recipeItems: Array<ItemStack?>
    private val copyIngredientNBT = false

    init {
        var shape: String = ""
        var idx: Int = 0

        var width: Int = 0
        var height: Int = 0
        while (layout[idx] is String) {
            val s: String = layout[idx++] as String
            shape += s
            width = s.length
            height++
        }
        if (width * height != shape.length) {
            var ret: String = "Invalid shaped recipe: "
            for (tmp: Any? in layout) {
                ret += tmp.toString() + ", "
            }
            throw RuntimeException(ret)
        }

        val itemMap: HashMap<Char, ItemStack?> = HashMap<Char, ItemStack?>()

        while (idx < layout.size) {
            val chr: Char? = layout[idx] as Char?
            val `in`: Any? = layout[idx + 1]

            if (`in` is ItemStack) {
                itemMap.put((chr)!!, `in`.copy())
            } else if (`in` is Item) {
                itemMap.put((chr)!!, ItemStack(`in`))
            } else if (`in` is Block) {
                itemMap.put((chr)!!, ItemStack(`in`, 1, OreDictionary.WILDCARD_VALUE))
            } else {
                var ret: String = "Invalid shaped recipe: "
                for (tmp: Any? in layout) {
                    ret += tmp.toString() + ", "
                }
                throw RuntimeException(ret)
            }
            idx += 2
        }

        this.recipeWidth = width
        this.recipeHeight = height
        this.recipeItems = arrayOfNulls<ItemStack>(width * height)
        var x: Int = 0
        for (chr: Char in shape.toCharArray()) {
            this.recipeItems[x++] = itemMap.get(chr)
        }
    }

    override fun getRecipeOutput(): ItemStack? {
        return this.recipeOutput
    }

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv)
    }

    override fun matches(inv: InventoryCrafting, worldIn: World): Boolean {
        for (i: Int in 0..3 - this.recipeWidth) {
            for (j: Int in 0..3 - this.recipeHeight) {
                if (this.checkMatch(inv, i, j, false)) {
                    return true
                }

                if (this.checkMatch(inv, i, j, true)) {
                    return true
                }
            }
        }

        return false
    }

    private fun checkMatch(inv: InventoryCrafting, x: Int, z: Int, mirror: Boolean): Boolean {
        for (i: Int in 0..2) {
            for (j: Int in 0..2) {
                val k: Int = i - x
                val l: Int = j - z
                var target: ItemStack? = null

                if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
                    if (mirror) {
                        target = this.recipeItems[this.recipeWidth - k - 1 + l * this.recipeWidth]
                    } else {
                        target = this.recipeItems[k + l * this.recipeWidth]
                    }
                }

                val itemstack1: ItemStack = inv.getStackInRowAndColumn(i, j)

                if (!itemstack1.isEmpty() || target != null) {
                    if (itemstack1.isEmpty() || target == null) {
                        return false
                    }

                    if (target.getItem() !== itemstack1.getItem()) {
                        return false
                    }

                    if (target.legacyItemValue != 32767 && target.legacyItemValue != itemstack1.legacyItemValue) {
                        return false
                    }

                    if (!ItemStack.areItemStackTagsEqual(target, itemstack1)) {
                        return false
                    }
                }
            }
        }

        return true
    }

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
        return this.getRecipeOutput()!!.copy()
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width >= this.recipeWidth && height >= this.recipeHeight
    }
}
