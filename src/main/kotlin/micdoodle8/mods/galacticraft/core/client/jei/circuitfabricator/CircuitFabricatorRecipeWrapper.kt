package micdoodle8.mods.galacticraft.core.client.jei.circuitfabricator

import javax.annotation.Nonnull

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.item.ItemStack

open class CircuitFabricatorRecipeWrapper constructor(
    @field:Nonnull @param:Nonnull private val input: MutableList<Any?>,
    @field:Nonnull @param:Nonnull private val output: ItemStack
) : IRecipeWrapper {
    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputs<Any?>(ItemStack::class.java, this.input)
        ingredients.setOutput<ItemStack?>(ItemStack::class.java, this.output)
    }

    override fun equals(o: Any?): Boolean {
        if (o is CircuitFabricatorRecipeWrapper) {
            val match: CircuitFabricatorRecipeWrapper = o
            if (!ItemStack.areItemStacksEqual(match.output, this.output)) return false
            for (i: Int in this.input.indices) {
                val a: Any? = this.input.get(i)
                val b: Any? = match.input.get(i)
                if (a == null && b == null) continue

                if (a is ItemStack) {
                    if (b !is ItemStack) return false
                    if (!ItemStack.areItemStacksEqual(a, b)) return false
                } else if (a is MutableList<*>) {
                    if (b !is MutableList<*>) return false
                    val aa: MutableList<*> = a
                    val bb: MutableList<*> = b
                    if (aa.size != bb.size) return false
                    for (j: Int in aa.indices) {
                        val c: ItemStack = aa.get(j) as ItemStack
                        val d: ItemStack = bb.get(j) as ItemStack
                        if (!ItemStack.areItemStacksEqual(c as ItemStack?, d as ItemStack?)) return false
                    }
                }
            }
            return true
        }
        return false
    }
}
