package micdoodle8.mods.galacticraft.api.recipe

import java.util.ArrayList
import java.util.HashMap

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import kotlin.math.min

open class SpaceStationRecipe
constructor(objMap: HashMap<Any?,Int>) {
    private val input = HashMap<Any?,Int>(4, 1.0f)

    /**
     * @param objMap a map of the items required. Each entry should be an object of
     * ItemStack, Item/Block or String(OreDict) and the amount of
     * that item required
     */
    init {
        for (obj: Any? in objMap.keys) {
            val amount: Int? = objMap.get(obj)

            if (obj is ItemStack) {
                this.input.put(obj.copy(), (amount)!!)
            } else if (obj is Item) {
                this.input.put(ItemStack(obj), (amount)!!)
            } else if (obj is Block) {
                this.input.put(ItemStack(obj), (amount)!!)
            } else if (obj is String) {
                val stacks: MutableList<ItemStack?>? = OreDictionary.getOres(obj)
                this.input.put(stacks, (amount)!!)
            } else if (obj is ArrayList<*>) {
                this.input.put(obj, (amount)!!)
            } else {
                throw RuntimeException("INVALID SPACE STATION RECIPE")
            }
        }
    }

    open fun getRecipeSize(): Int {
        return this.input.size
    }

    open fun matches(player: EntityPlayer, remove: Boolean): Boolean {
        val required: HashMap<Any?,Int> = HashMap<Any?,Int>()
        required.putAll(this.input)

        val req: MutableIterator<Any?> = this.input.keys.iterator()

        while (req.hasNext()) {
            val next: Any? = req.next()

            val amountRequired: Int = required.get(next)!!
            var amountInInv: Int = 0

            for (x: Int in 0..<player.inventory.getSizeInventory()) {
                val slot: ItemStack = player.inventory.getStackInSlot(x)

                if (slot != null && slot.getCount() > 0)  //Intentional ItemStack null check
                {
                    if (next is ItemStack) {
                        if (checkItemEquals(next, slot)) {
                            amountInInv += slot.getCount()
                        }
                    } else if (next is MutableList<*>) {
                        for (item: ItemStack in next as MutableList<ItemStack>) {
                            if (checkItemEquals(item, slot)) {
                                amountInInv += slot.getCount()
                            }
                        }
                    }
                }
            }

            if (amountInInv >= amountRequired) {
                required.remove(next)
            }
        }

        if (required.isEmpty() && remove) {
            this.removeItems(player)
        }

        return required.isEmpty()
    }

    open fun removeItems(player: EntityPlayer) {
        val required: HashMap<Any?,Int> = HashMap<Any?,Int>(this.input)

        val req: MutableIterator<Any?> = required.keys.iterator()

        while (req.hasNext()) {
            val next: Any? = req.next()

            val amountRequired: Int = required.get(next)!!
            var amountRemoved: Int = 0

            InventoryLoop@ for (x: Int in 0..<player.inventory.getSizeInventory()) {
                val slot: ItemStack = player.inventory.getStackInSlot(x)

                if (slot != null && slot.getCount() > 0)  //Intentional ItemStack null check
                {
                    val amountRemaining: Int = amountRequired - amountRemoved

                    if (next is ItemStack) {
                        if (checkItemEquals(next, slot)) {
                            val amountToRemove: Int = min(slot.getCount(), amountRemaining)
                            var newStack: ItemStack = slot.copy()
                            newStack.shrink(amountToRemove)

                            if (newStack.getCount() <= 0) {
                                newStack = ItemStack.EMPTY
                            }

                            player.inventory.setInventorySlotContents(x, newStack)
                            amountRemoved += amountToRemove
                            if (amountRemoved == amountRequired) break
                        }
                    } else if (next is MutableList<*>) {
                        for (item: ItemStack in next as MutableList<ItemStack>) {
                            if (checkItemEquals(item, slot)) {
                                val amountToRemove: Int = min(slot.getCount(), amountRemaining)
                                var newStack: ItemStack = slot.copy()
                                newStack.shrink(amountToRemove)

                                if (newStack.getCount() <= 0) {
                                    newStack = ItemStack.EMPTY
                                }

                                player.inventory.setInventorySlotContents(x, newStack)
                                amountRemoved += amountToRemove
                                if (amountRemoved == amountRequired) break@InventoryLoop
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should
     * never manipulate the values in this array as it will effect the recipe
     * itself.
     *
     * @return The recipes input vales.
     */
    open fun getInput(): HashMap<Any?,Int> {
        return this.input
    }

    companion object {
        @JvmStatic
        fun checkItemEquals(target: ItemStack, input: ItemStack): Boolean {
            return target.getItem() === input.getItem() && (target.getItemDamage() == OreDictionary.WILDCARD_VALUE || target.getItemDamage() == input.getItemDamage())
        }
    }
}
