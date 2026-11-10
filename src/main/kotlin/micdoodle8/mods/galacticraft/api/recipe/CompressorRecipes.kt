package micdoodle8.mods.galacticraft.api.recipe

import micdoodle8.mods.galacticraft.api.GalacticraftConfigAccess
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.HashMap

object CompressorRecipes {
    private val recipes: MutableList<IRecipe> = ArrayList<IRecipe>()
    private val recipesAdventure: MutableList<IRecipe> = ArrayList<IRecipe>()
    private var adventureOnly = false
    private val adventureFlag: Field? = null
    private const val flagNotCached = true

    @JvmField
    var steelIngotsPresent: Boolean = false

    @JvmField
    var steelRecipeGC: MutableList<ItemStack?>? = null

    @JvmStatic
    fun addRecipe(output: ItemStack?, vararg inputList: Any?): ShapedRecipesGC {
        val s: StringBuilder = StringBuilder()
        var i: Int = 0
        var j: Int = 0
        var k: Int = 0

        if (inputList[i] is Array<*> && inputList[i]!!.javaClass.componentType == String::class.java) {
            val astring: Array<String> = inputList[i++] as Array<String>

            for (s1: String in astring) {
                ++k
                j = s1.length
                s.append(s1)
            }
        } else {
            while (inputList[i] is String) {
                val s2: String = inputList[i++] as String
                ++k
                j = s2.length
                s.append(s2)
            }
        }

        val hashmap: HashMap<Char, ItemStack?> = HashMap<Char, ItemStack?>(inputList.size / 2, 1.0f)

        while (i < inputList.size) {
            val character: Char? = inputList[i] as Char?
            var itemstack1: ItemStack? = null

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

            if (hashmap.containsKey(c0)) {
                aitemstack[i1] = hashmap.get(c0)!!.copy()
            } else {
                aitemstack[i1] = null
            }
        }

        val shapedRecipes: ShapedRecipesGC = ShapedRecipesGC(j, k, (aitemstack as Array<ItemStack>), (output)!!)
        if (!adventureOnly) recipes.add(shapedRecipes)
        recipesAdventure.add(shapedRecipes)
        return shapedRecipes
    }

    @JvmStatic
    fun addShapelessRecipe(par1ItemStack: ItemStack, vararg par2ArrayOfObj: Any?) {
        val arraylist: ArrayList<Any?> = ArrayList<Any?>()
        val i: Int = par2ArrayOfObj.size

        for (object1: Any? in par2ArrayOfObj) {
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

        val toAdd: IRecipe = ShapelessOreRecipeGC(par1ItemStack, *arraylist.toTypedArray())
        if (!adventureOnly) recipes.add(toAdd)
        recipesAdventure.add(toAdd)
    }

    @JvmStatic
    fun addRecipeAdventure(output: ItemStack?, vararg inputList: Any?): ShapedRecipesGC {
        adventureOnly = true
        val returnValue: ShapedRecipesGC = addRecipe(output, *inputList)
        adventureOnly = false
        return returnValue
    }

    @JvmStatic
    fun addShapelessAdventure(par1ItemStack: ItemStack, vararg par2ArrayOfObj: Any?) {
        adventureOnly = true
        addShapelessRecipe(par1ItemStack, *par2ArrayOfObj)
        adventureOnly = false
    }

    @JvmStatic
    fun findMatchingRecipe(inventory: InventoryCrafting, par2World: World): ItemStack {
        var i: Int = 0
        var itemstack: ItemStack = ItemStack.EMPTY
        var itemstack1: ItemStack = ItemStack.EMPTY
        var j: Int

        j = 0
        while (j < inventory.getSizeInventory()) {
            val itemstack2: ItemStack = inventory.getStackInSlot(j)

            if (!itemstack2.isEmpty()) {
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
            val theRecipes: MutableList<IRecipe> = getRecipeList()

            j = 0
            while (j < theRecipes.size) {
                val irecipe: IRecipe = theRecipes.get(j)

                if (irecipe is ShapedRecipesGC && irecipe.matches(inventory, par2World)) {
                    return irecipe.getRecipeOutput().copy()
                } else if (irecipe is ShapelessOreRecipeGC && irecipe.matches(inventory, par2World)) {
                    return irecipe.getRecipeOutput().copy()
                }
                ++j
            }

            return ItemStack.EMPTY
        }
    }

    @JvmStatic
    fun getRecipeListAll(): MutableList<IRecipe> {
        val result: MutableList<IRecipe> = ArrayList<IRecipe>(recipesAdventure)
        val endList: MutableList<IRecipe> = getRecipeListHidden(true, true)
        result.removeIf { irecipe: IRecipe? -> endList.contains(irecipe) }
        var ice: IRecipe? = null
        val iceItem: Item = ItemStack(Blocks.ICE).getItem()
        for (test: IRecipe in result) {
            if (test.getRecipeOutput().getItem() === iceItem) {
                ice = test
                break
            }
        }
        if (ice != null) {
            result.remove(ice)
            result.add(ice)
        }
        result.addAll(endList)
        return result
    }

    @JvmStatic
    fun getRecipeListHidden(hideSteel: Boolean, hideAdventure: Boolean): MutableList<IRecipe> {
        if (!hideAdventure) return ArrayList<IRecipe>(0)

        val result: MutableList<IRecipe> = ArrayList<IRecipe>(recipesAdventure)
        result.removeIf { irecipe: IRecipe? -> recipes.contains(irecipe) }
        if (steelIngotsPresent && hideSteel) {
            val resultSteelless: MutableList<IRecipe> = ArrayList<IRecipe>(result.size)
            for (recipe: IRecipe in result) {
                val output: ItemStack = recipe.getRecipeOutput()
                if (output == null) continue  //Intentional ItemStack null check

                if (output.getItemDamage() == 9 && output.getItem() === GCItems.basicItem && recipe is ShapelessOreRecipeGC) {
                    if (recipe.matches((steelRecipeGC)!!)) {
                        continue
                    }
                }
                resultSteelless.add(recipe)
            }
            return resultSteelless
        }
        return result
    }

    @JvmStatic
    fun getRecipeList(): MutableList<IRecipe> {
        if (GalacticraftConfigAccess.getChallengeRecipes()) return recipesAdventure


        // Filter out the GC steel recipe in Hard Mode
        if (steelIngotsPresent && GalacticraftConfigAccess.getHardMode()) {
            val resultSteelless: MutableList<IRecipe> = ArrayList<IRecipe>(recipes.size)
            for (recipe: IRecipe in recipes) {
                val output: ItemStack = recipe.getRecipeOutput()
                if (output == null) continue  //Intentional ItemStack null check

                if (output.getItemDamage() == 9 && output.getItem() === GCItems.basicItem && recipe is ShapelessOreRecipeGC) {
                    if (recipe.matches((steelRecipeGC)!!)) {
                        continue
                    }
                }
                resultSteelless.add(recipe)
            }
            return resultSteelless
        }

        return recipes
    }

    @JvmStatic
    fun getRecipes(match: ItemStack): MutableList<IRecipe?> {
        val result: MutableList<IRecipe?> = (ArrayList<Any?>(getRecipeList()) as MutableList<IRecipe?>)
        result.removeIf { irecipe: IRecipe? -> !ItemStack.areItemStacksEqual(match, irecipe!!.getRecipeOutput()) }
        return result
    }

    /**
     * Caution: call this BEFORE the JEI plugin registers recipes - or else the removed recipe will still be shown in JEI.
     */
    @JvmStatic
    fun removeRecipe(match: ItemStack) {
        recipes.removeIf { irecipe: IRecipe? -> ItemStack.areItemStacksEqual(match, irecipe!!.getRecipeOutput()) }
        recipesAdventure.removeIf { irecipe: IRecipe? ->
            ItemStack.areItemStacksEqual(
                match,
                irecipe!!.getRecipeOutput()
            )
        }
    }

    @JvmStatic
    fun replaceRecipeIngredient(ingredient: ItemStack?, replacement: MutableList<ItemStack?>?) {
        if (ingredient == null) return

        for (recipe: IRecipe? in recipesAdventure) {
            if (recipe is IRecipeUpdatable) {
                recipe.replaceInput(ingredient, replacement)
            }
        }
    }

    @JvmStatic
    fun replaceRecipeIngredient(ingredient: ItemStack?) {
        if (ingredient == null) return

        for (recipe: IRecipe? in recipesAdventure) {
            if (recipe is IRecipeUpdatable) {
                recipe.replaceInput(ingredient)
            }
        }
    }
}
