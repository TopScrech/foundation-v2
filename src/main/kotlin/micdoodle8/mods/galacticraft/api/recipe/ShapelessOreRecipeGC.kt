package micdoodle8.mods.galacticraft.api.recipe

import java.util.ArrayList
import java.util.LinkedList

import micdoodle8.mods.galacticraft.core.util.RecipeUtil
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
import java.util.*

open class ShapelessOreRecipeGC constructor(result: ItemStack, vararg recipe: Any?) :
    IForgeRegistryEntry.Impl<IRecipe?>(), IRecipeUpdatable {
    @JvmField
    protected var output: ItemStack? = null

    @JvmField
    protected var input: ArrayList<Any?> = ArrayList<Any?>()

    constructor(result: Block, vararg recipe: Any?) : this(ItemStack(result), *recipe)

    constructor(result: Item, vararg recipe: Any?) : this(ItemStack(result), *recipe)

    init {
        output = result.copy()
        for (`in`: Any? in recipe) {
            if (`in` is ItemStack) {
                input.add(`in`.copy())
            } else if (`in` is Item) {
                input.add(ItemStack(`in`))
            } else if (`in` is Block) {
                input.add(ItemStack(`in`))
            } else if (`in` is String) {
                input.add(OreDictionary.getOres(`in`))
            } else {
                val ret: StringBuilder = StringBuilder("Invalid compressor shapeless ore recipe: ")
                for (tmp: Any? in recipe) {
                    ret.append(tmp).append(", ")
                }
                ret.append(output)
                throw RuntimeException(ret.toString())
            }
        }
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width * height >= input.size
    }

    override fun getRecipeOutput(): ItemStack {
        return output!!
    }

    override fun getCraftingResult(var1: InventoryCrafting): ItemStack {
        return output!!.copy()
    }

    override fun matches(var1: InventoryCrafting, world: World): Boolean {
        val required: MutableList<Any?> = LinkedList<Any?>(input)

        for (x: Int in 0..<var1.getSizeInventory()) {
            val slot: ItemStack = var1.getStackInSlot(x)

            if (!slot.isEmpty()) {
                var inRecipe: Boolean = false
                val req: MutableIterator<Any?> = required.iterator()

                while (req.hasNext()) {
                    var match: Boolean = false

                    val next: Any? = req.next()

                    if (next is ItemStack) {
                        match = OreDictionary.itemMatches(next, slot, false)
                    } else if (next is MutableList<*>) {
                        val itr: MutableIterator<ItemStack?> = (next as MutableList<ItemStack?>).iterator()
                        while (itr.hasNext() && !match) {
                            match = OreDictionary.itemMatches(itr.next()!!, slot, false)
                        }
                    }

                    if (match) {
                        inRecipe = true
                        required.remove(next)
                        break
                    }
                }

                if (!inRecipe) {
                    return false
                }
            }
        }

        return required.isEmpty()
    }

    open fun matches(var1: MutableList<ItemStack?>): Boolean {
        val required: MutableList<Any?> = LinkedList<Any?>(input)

        for (slot: ItemStack? in var1) {
            if (slot != null) {
                var inRecipe: Boolean = false
                val req: MutableIterator<Any?> = required.iterator()

                while (req.hasNext()) {
                    var match: Boolean = false

                    val next: Any? = req.next()

                    if (next is ItemStack) {
                        match = OreDictionary.itemMatches(next, slot, false)
                    } else if (next is MutableList<*>) {
                        val itr: MutableIterator<ItemStack?> = (next as MutableList<ItemStack?>).iterator()
                        while (itr.hasNext() && !match) {
                            match = OreDictionary.itemMatches(itr.next()!!, slot, false)
                        }
                    }

                    if (match) {
                        inRecipe = true
                        required.remove(next)
                        break
                    }
                }

                if (!inRecipe) {
                    return false
                }
            }
        }

        return required.isEmpty()
    }

    open fun getInput(): ArrayList<Any?> {
        return this.input
    }

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> //getRecipeLeftovers
    {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv)
    }

    override fun replaceInput(inputA: ItemStack?, inputB: MutableList<ItemStack?>?) {
        for (i: Int in this.input.indices) {
            val test: Any? = this.input.get(i)
            if (test is ItemStack && ItemStack.areItemsEqual(inputA, test) && RecipeUtil.areItemStackTagsEqual(
                    (inputA)!!,
                    test
                )
            ) {
                this.input.set(i, inputB)
            } else if (test is MutableList<*> && itemListContains(test, (inputA)!!)) {
                this.input.set(i, inputB)
            }
        }
    }

    override fun replaceInput(inputB: ItemStack?) {
        for (i: Int in this.input.indices) {
            val test: Any? = this.input.get(i)
            if (test is MutableList<*> && itemListContains(test, (inputB)!!)) {
                this.input.set(i, inputB)
            }
        }
    }

    companion object {
        @JvmStatic
        private fun itemListContains(test: MutableList<*>, stack: ItemStack): Boolean {
            for (b: Any? in test) {
                if (b is ItemStack && ItemStack.areItemsEqual(stack, b) && RecipeUtil.areItemStackTagsEqual(
                        stack,
                        b
                    )
                ) return true
            }
            return false
        }
    }
}
