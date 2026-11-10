package galaxyspace.systems.SolarSystem.planets.overworld.recipes

import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RocketAssemblyRecipes
import micdoodle8.mods.galacticraft.api.recipe.ShapedRecipesGC
import micdoodle8.mods.galacticraft.api.recipe.ShapelessOreRecipeGC
import net.minecraft.block.Block
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary
import java.lang.RuntimeException
import java.util.ArrayList
import java.util.HashMap

object RocketAssemblyRecipes {
    private val recipes: MutableList<IRecipe> = ArrayList<IRecipe>()

    @JvmStatic
    fun addRecipe(output: ItemStack?, vararg inputList: Any?): ShapedRecipesGC {
        var s: String = ""
        var i: Int = 0
        var j: Int = 0
        var k: Int = 0

        if ((inputList[i] is Array<*> && String::class.java.isAssignableFrom(inputList[i]!!.javaClass.componentType))) {
            val astring: Array<String> = inputList[i++] as Array<String>

            for (s1: String in astring) {
                ++k
                j = s1.length
                s = s + s1
            }
        } else {
            while (inputList[i] is String) {
                val s2: String = inputList[i++] as String
                ++k
                j = s2.length
                s = s + s2
            }
        }

        val hashmap: HashMap<Char, ItemStack?>

        hashmap = HashMap<Char, ItemStack?>()
        while (i < inputList.size) {
            val character: Char? = inputList[i] as Char?
            var itemstack1: ItemStack? = ItemStack.EMPTY

            if (inputList[i + 1] is Item) {
                itemstack1 = ItemStack(inputList[i + 1] as Item?)
            } else if (inputList[i + 1] is Block) {
                itemstack1 = ItemStack(inputList[i + 1] as Block?, 1, 32767)
            } else if (inputList[i + 1] is ItemStack) {
                itemstack1 = inputList[i + 1] as ItemStack?
            }

            hashmap.put((character)!!, itemstack1)
            i += 2
        }

        val aitemstack: Array<ItemStack?> = arrayOfNulls<ItemStack>(j * k)

        for (i1: Int in 0..<j * k) {
            val c0: Char = s.get(i1)

            if (hashmap.containsKey(Character.valueOf(c0))) {
                aitemstack[i1] = hashmap.get(Character.valueOf(c0))!!.copy()
            } else {
                aitemstack[i1] = ItemStack.EMPTY
            }
        }

        val shapedrecipes: ShapedRecipesGC = ShapedRecipesGC(j, k, (aitemstack as Array<ItemStack>), (output)!!)
        recipes.add(shapedrecipes)
        return shapedrecipes
    }

    @JvmStatic
    fun addShapelessRecipe(par1ItemStack: ItemStack, vararg par2ArrayOfObj: Any?) {
        val arraylist: ArrayList<Any?> = ArrayList<Any?>()
        val i: Int = par2ArrayOfObj.size

        for (j: Int in 0..<i) {
            val object1: Any? = par2ArrayOfObj[j]


            if (object1 is ItemStack) {
                arraylist.add(object1.copy())
            } else if (object1 is Item) {
                arraylist.add(ItemStack(object1))
            } else if (object1 is String) {
                arraylist.add(object1)
            } else {
                if (object1 !is Block) {
                    throw RuntimeException("Invalid shapeless compressor recipe!")
                }

                arraylist.add(ItemStack(object1))
            }
        }

        recipes.add(ShapelessOreRecipeGC(par1ItemStack, *arraylist.toTypedArray()))
    }

    @JvmStatic
    fun findMatchingRecipe(inventory: IInventory, par2World: World?): ItemStack {
        var i: Int = 0
        var itemstack: ItemStack = ItemStack.EMPTY
        var itemstack1: ItemStack = ItemStack.EMPTY
        var j: Int

        j = 0
        while (j < inventory.getSizeInventory()) {
            val itemstack2: ItemStack = inventory.getStackInSlot(j)

            if (itemstack2 != ItemStack.EMPTY) {
                if (i == 0) {
                    itemstack = itemstack2
                }

                if (i == 1) {
                    itemstack1 = itemstack2
                }

                ++i
            }
            ++j
        }

        if (i == 2 && itemstack.getItem() === itemstack1.getItem() && itemstack.getCount() == 1 && itemstack1.getCount() == 1 && itemstack.getItem()
                .isRepairable()
        ) {
            val k: Int = itemstack.getItem().getMaxDamage() - itemstack.getItemDamage()
            val l: Int = itemstack.getItem().getMaxDamage() - itemstack1.getItemDamage()
            val i1: Int = k + l + itemstack.getItem().getMaxDamage() * 5 / 100
            var j1: Int = itemstack.getItem().getMaxDamage() - i1

            if (j1 < 0) {
                j1 = 0
            }

            return ItemStack(itemstack.getItem(), 1, j1)
        } else {
            j = 0
            while (j < recipes.size) {
                val irecipe: IRecipe = recipes.get(j)!!

                if (irecipe is ShapedRecipesGC && matches(irecipe, inventory, par2World)) {
                    return irecipe.getRecipeOutput().copy()
                } else if (irecipe is ShapelessOreRecipeGC && matchesShapeless(irecipe, inventory, par2World)) {
                    return irecipe.getRecipeOutput().copy()
                }
                ++j
            }

            return ItemStack.EMPTY
        }
    }

    @JvmStatic
    private fun matches(recipe: ShapedRecipesGC, inventory: IInventory, par2World: World?): Boolean {
        for (i: Int in 0..3 - recipe.recipeWidth) {
            for (j: Int in 0..3 - recipe.recipeHeight) {
                if (checkMatch(recipe, inventory, i, j, true)) {
                    return true
                }

                if (checkMatch(recipe, inventory, i, j, false)) {
                    return true
                }
            }
        }

        return false
    }

    @JvmStatic
    private fun checkMatch(
        recipe: ShapedRecipesGC,
        inventory: IInventory,
        par2: Int,
        par3: Int,
        par4: Boolean
    ): Boolean {
        for (k: Int in 0..2) {
            for (l: Int in 0..2) {
                val i1: Int = k - par2
                val j1: Int = l - par3
                var itemstack: ItemStack = ItemStack.EMPTY

                if (i1 >= 0 && j1 >= 0 && i1 < recipe.recipeWidth && j1 < recipe.recipeHeight) {
                    if (par4) {
                        itemstack = recipe.recipeItems[recipe.recipeWidth - i1 - 1 + j1 * recipe.recipeWidth]
                    } else {
                        itemstack = recipe.recipeItems[i1 + j1 * recipe.recipeWidth]
                    }
                }

                var itemstack1: ItemStack = ItemStack.EMPTY

                if (k >= 0 && l < 3) {
                    val k2: Int = k + l * 3
                    itemstack1 = inventory.getStackInSlot(k2)
                }

                if (itemstack1 != ItemStack.EMPTY || itemstack != ItemStack.EMPTY) {
                    if (itemstack1 == ItemStack.EMPTY && itemstack != ItemStack.EMPTY || itemstack1 != ItemStack.EMPTY && itemstack == ItemStack.EMPTY) {
                        return false
                    }

                    if (itemstack.getItem() !== itemstack1.getItem()) {
                        return false
                    }

                    if (itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack1.getItemDamage()) {
                        return false
                    }
                }
            }
        }

        return true
    }

    @JvmStatic
    private fun matchesShapeless(recipe: ShapelessOreRecipeGC, var1: IInventory, par2World: World?): Boolean {
        val required: ArrayList<Any?> = ArrayList<Any?>(recipe.getInput())

        for (x: Int in 0..<var1.getSizeInventory()) {
            val slot: ItemStack = var1.getStackInSlot(x)

            if (slot != ItemStack.EMPTY) {
                var inRecipe: Boolean = false
                val req: MutableIterator<Any?> = required.iterator()

                while (req.hasNext()) {
                    var match: Boolean = false

                    val next: Any? = req.next()

                    if (next is ItemStack) {
                        match = OreDictionary.itemMatches(next, slot, false)
                    } else if (next is ArrayList<*>) {
                        val itr: MutableIterator<ItemStack?> = (next as ArrayList<ItemStack?>).iterator()
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

    @JvmStatic
    fun getRecipeList(): MutableList<IRecipe> {
        return recipes
    }

    @JvmStatic
    fun removeRecipe(match: ItemStack) {
        recipes.removeIf { irecipe: IRecipe? -> ItemStack.areItemStacksEqual(match, irecipe!!.getRecipeOutput()) }
    }
}
