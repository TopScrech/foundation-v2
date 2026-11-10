package micdoodle8.mods.galacticraft.api.recipe

import java.util.List

import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import java.lang.RuntimeException
import java.util.ArrayList

object CircuitFabricatorRecipes {
    private val recipeInputs: MutableList<NonNullList<Any?>> = ArrayList<NonNullList<Any?>>()
    private val recipeOutputs: MutableList<ItemStack?> = ArrayList<ItemStack?>()

    @JvmField
    var slotValidItems: ArrayList<ArrayList<ItemStack?>> = ArrayList<ArrayList<ItemStack?>>(5)

    /**
     * Input list must be array with 5 elements matching the 5 slots in the machine.  Use null if no
     * item is used in the slot.  Use a List<ItemStack> for Oredicted ingredients.
     *
     *
     * 0: Diamond  1: Silicon  2: Silicon  3: Redstone dust  4: Recipe item
     *
     * @param output  ItemStack
     * @param inputList  Object array.  Must contain only null, ItemStack, or List<ItemStack>.
     *
     * @return
    </ItemStack></ItemStack> */
    @JvmStatic
    fun addRecipe(output: ItemStack?, inputList: MutableList<Any?>) {
        if (inputList.size != 5) {
            throw RuntimeException("Invalid circuit fabricator recipe!  Inputs length should be 5, was " + inputList.size)
        }
        for (o: Any? in inputList) {
            if (o == null || o is ItemStack) {
                continue
            }
            if (o is MutableList<*> && o.size > 0) {
                continue
            }
            throw RuntimeException("Invalid circuit fabricator recipe!  Input must be ItemStack or List<ItemStack>: " + o.toString())
        }

        val inputs: NonNullList<Any?> = NonNullList.create<Any?>()
        inputs.addAll(inputList)
        recipeInputs.add(inputs)
        recipeOutputs.add(output)
        validateItems(inputList)
    }

    /** Add the recipe ingredients to the valid items for each slot
     *
     * @param inputList
     */
    @JvmStatic
    private fun validateItems(inputList: MutableList<Any?>) {
        //First initialise the ArrayList if this is the first time it's used
        if (slotValidItems.size == 0) {
            for (i: Int in 0..4) {
                val entry: ArrayList<ItemStack?> = ArrayList<ItemStack?>(3)
                slotValidItems.add(entry)
            }
        }
        //Now see if the recipe items are already valid for their slots, if not add them
        for (i: Int in 0..4) {
            val input: Any? = inputList.get(i)
            if (input is ItemStack) {
                validateItem(i, input)
            } else if (input is MutableList<*>) {
                for (stack: ItemStack in input as MutableList<ItemStack>) {
                    validateItem(i, stack)
                }
            }
        }
    }

    @JvmStatic
    private fun validateItem(i: Int, inputStack: ItemStack) {
        if (inputStack.isEmpty()) {
            return
        }

        val validItems: ArrayList<ItemStack?> = slotValidItems.get(i)

        var found: Boolean = false
        for (j: Int in validItems.indices) {
            if (inputStack.isItemEqual(validItems.get(j))) {
                found = true
                break
            }
        }
        if (!found) {
            validItems.add(inputStack.copy())
        }
    }

    /**
     * Gets the output ItemStack for the passed input.
     *
     * @param inputList ItemStack array of input items
     * @return The result ItemStack
     */
    @JvmStatic
    fun getOutputForInput(inputList: MutableList<ItemStack>): ItemStack? {
        if (inputList.size != 5) {
            return ItemStack.EMPTY
        }

        var count: Int = 0
        for (recipe in recipeInputs) {
            var found: Boolean = true
            for (i: Int in 0..4) {
                val recipeStack: Any = recipe.get(i)
                val inputStack: ItemStack = inputList.get(i)

                if (recipeStack is ItemStack && recipeStack.isEmpty() || inputStack.isEmpty()) {
                    if (!(recipeStack is ItemStack && recipeStack.isEmpty()) || !inputStack.isEmpty()) {
                        found = false
                        break
                    }
                } else if (recipeStack is ItemStack) {
                    val stack: ItemStack = recipeStack
                    if (stack.getItem() !== inputStack.getItem() || stack.getItemDamage() != inputStack.getItemDamage() || !RecipeUtil.areItemStackTagsEqual(
                            stack,
                            inputStack
                        )
                    ) {
                        found = false
                        break
                    }
                } else if (recipeStack is MutableList<*>) {
                    var listMatchOne: Boolean = false
                    for (stack: ItemStack in recipeStack as MutableList<ItemStack>) {
                        if (stack.getItem() === inputStack.getItem() && stack.getItemDamage() == inputStack.getItemDamage() && RecipeUtil.areItemStackTagsEqual(
                                stack,
                                inputStack
                            )
                        ) {
                            listMatchOne = true
                            break
                        }
                    }
                    if (listMatchOne == false) {
                        found = false
                        break
                    }
                }
            }

            if (found) {
                return recipeOutputs.get(count)
            }

            count++
        }

        return ItemStack.EMPTY
    }

    @JvmStatic
    fun getRecipes(): MutableList<NonNullList<Any?>> {
        return recipeInputs
    }

    @JvmStatic
    fun getOutput(count: Int): ItemStack? {
        return recipeOutputs.get(count)
    }

    /**
     * Caution: call this BEFORE the JEI plugin registers recipes - or else the removed recipe will still be shown in JEI.
     */
    @JvmStatic
    fun removeRecipe(match: ItemStack) {
        var count: Int = 0
        for (output: ItemStack in ArrayList<ItemStack>(recipeOutputs)) {
            if (ItemStack.areItemStacksEqual(match, output)) {
                recipeInputs.removeAt(count)
                recipeOutputs.removeAt(count)
                // Do not increment count if we just removed the element!
            } else count++
        }
    }

    @JvmStatic
    fun replaceRecipeIngredient(ingredient: ItemStack?, replacement: MutableList<ItemStack?>) {
        if (ingredient == null) return
        slotValidItems.clear()
        val newIngredient: Any = replacement

        for (recipe: NonNullList<Any?> in recipeInputs) {
            for (i: Int in 0..4) {
                val recipeStack: Any = recipe.get(i)
                if (recipeStack == null) {
                    continue
                }

                if (recipeStack is ItemStack) {
                    val stack: ItemStack = recipeStack
                    if (stack.getItem() === ingredient.getItem() && stack.getItemDamage() == ingredient.getItemDamage() && ItemStack.areItemStackTagsEqual(
                            stack,
                            ingredient
                        )
                    ) {
                        recipe.set(i, newIngredient)
                    }
                } else if (recipeStack is MutableList<*>) {
                    val listMatchOne: Boolean = false
                    for (stack: ItemStack in recipeStack as MutableList<ItemStack>) {
                        if (stack.getItem() === ingredient.getItem() && stack.getItemDamage() == ingredient.getItemDamage() && ItemStack.areItemStackTagsEqual(
                                stack,
                                ingredient
                            )
                        ) {
                            recipe.set(i, newIngredient)
                            break
                        }
                    }
                }
            }

            validateItems(recipe)
        }
    }
}
