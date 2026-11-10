/*

 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.minecraftforge.common

import javax.annotation.Nonnull

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.util.CombatRules
import net.minecraft.util.DamageSource
import net.minecraft.util.NonNullList
import java.util.ArrayList
import java.util.Arrays
import kotlin.math.max

/**
 * This interface is to be implemented by ItemArmor classes. It will allow to
 * modify computation of damage and health loss. Computation will be called
 * before the actual armor computation, which can then be cancelled.
 *
 * @see ItemArmor
 */
interface ISpecialArmor {
    //TODO: Change 'int slot' to EnumArmorType
    /**
     * Retrieves the modifiers to be used when calculating armor damage.
     *
     * Armor will higher priority will have damage applied to them before
     * lower priority ones. If there are multiple pieces of armor with the
     * same priority, damage will be distributed between them based on there
     * absorption ratio.
     *
     * @param player The entity wearing the armor.
     * @param armor The ItemStack of the armor item itself.
     * @param source The source of the damage, which can be used to alter armor
     * properties based on the type or source of damage.
     * @param damage The total damage being applied to the entity
     * @param slot The armor slot the item is in.
     * @return A ArmorProperties instance holding information about how the armor effects damage.
     */
    fun getProperties(
        player: EntityLivingBase?,
        @Nonnull armor: ItemStack,
        source: DamageSource?,
        damage: Double,
        slot: Int
    ): ArmorProperties?

    /**
     * Get the displayed effective armor.
     *
     * @param player The player wearing the armor.
     * @param armor The ItemStack of the armor item itself.
     * @param slot The armor slot the item is in.
     * @return The number of armor points for display, 2 per shield.
     */
    fun getArmorDisplay(player: EntityPlayer?, @Nonnull armor: ItemStack, slot: Int): Int

    /**
     * Applies damage to the ItemStack. The mod is responsible for reducing the
     * item durability and stack size. If the stack is depleted it will be cleaned
     * up automatically.
     *
     * @param entity The entity wearing the armor
     * @param stack The ItemStack of the armor item itself.
     * @param source The source of the damage, which can be used to alter armor
     * properties based on the type or source of damage.
     * @param damage The amount of damage being applied to the armor
     * @param slot The armor slot the item is in.
     */
    fun damageArmor(entity: EntityLivingBase?, @Nonnull stack: ItemStack, source: DamageSource?, damage: Int, slot: Int)

    class ArmorProperties constructor(priority: Int, ratio: Double, max: Int) : Comparable<ArmorProperties> {
        @JvmField
        var Priority: Int = 0

        @JvmField
        var AbsorbMax: Int = Int.MAX_VALUE

        @JvmField
        var AbsorbRatio: Double = 0.0

        @JvmField
        var Armor: Double = 0.0 //Additional armor, separate from the armor added by vanilla attributes.

        @JvmField
        var Toughness: Double = 0.0 //Additional toughness, separate from the armor added by vanilla attributes.

        @JvmField
        var Slot: Int = 0

        //Left it in because I figured it'd be useful for modders developing custom armor.
        init {
            Priority = priority
            AbsorbRatio = ratio
            Armor = 0.0
            Toughness = 0.0
            AbsorbMax = max
        }

        override fun compareTo(o: ArmorProperties): Int {
            if (o.Priority != Priority) {
                return o.Priority - Priority
            }
            val left: Double = (if (AbsorbRatio == 0.0) 0.0 else AbsorbMax * 100.0 / AbsorbRatio)
            val right: Double = (if (o.AbsorbRatio == 0.0) 0.0 else o.AbsorbMax * 100.0 / o.AbsorbRatio)
            return (left - right).toInt()
        }

        override fun toString(): String {
            return String.format(
                "%d, %d, %f, %d",
                Priority,
                AbsorbMax,
                AbsorbRatio,
                (if (AbsorbRatio == 0.0) 0 else (AbsorbMax * 100.0 / AbsorbRatio).toInt())
            )
        }

        open fun copy(): ArmorProperties {
            val copy: ArmorProperties = ArmorProperties(Priority, AbsorbRatio, AbsorbMax)
            copy.Armor = Armor
            copy.Toughness = Toughness
            return copy
        }

        companion object {
            private const val DEBUG = false //Only enable this if you wish to be spammed with debugging information.


            /**
             * Gathers and applies armor reduction to damage being dealt to a entity.
             *
             * @param entity The Entity being damage
             * @param inventory An array of armor items
             * @param source The damage source type
             * @param damage The total damage being done
             * @return The left over damage that has not been absorbed by the armor
             */
            @JvmStatic
            fun applyArmor(
                entity: EntityLivingBase,
                inventory: NonNullList<ItemStack>,
                source: DamageSource,
                damage: Double
            ): Float {
                var damage: Double = damage
                if (source.isUnblockable()) {
                    return damage.toFloat()
                }

                if (DEBUG) {
                    println("Start: " + damage)
                }

                var totalArmor: Double = entity.getTotalArmorValue().toDouble()
                var totalToughness: Double =
                    entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()

                val dmgVals: ArrayList<ArmorProperties?> = ArrayList<ArmorProperties?>()
                for (slot: Int in inventory.indices) {
                    val stack: ItemStack = inventory.get(slot)

                    if (stack.isEmpty()) {
                        continue
                    }

                    var prop: ArmorProperties? = null
                    if (stack.getItem() is ISpecialArmor) {
                        val armor: ISpecialArmor = stack.getItem() as ISpecialArmor
                        prop = armor.getProperties(entity, stack, source, damage, slot)!!.copy()
                        totalArmor += prop.Armor
                        totalToughness += prop.Toughness
                    } else if (stack.getItem() is ItemArmor) {
                        val armor: ItemArmor = stack.getItem() as ItemArmor
                        prop = ArmorProperties(0, 0.0, Int.MAX_VALUE)
                        prop.Armor = armor.damageReduceAmount.toDouble()
                        prop.Toughness = armor.toughness.toDouble()
                    }
                    if (prop != null) {
                        prop.Slot = slot
                        dmgVals.add(prop)
                    }
                }
                if (dmgVals.size > 0) {
                    val props: Array<ArmorProperties> = ((dmgVals.toTypedArray<ArmorProperties?>() as Array<ISpecialArmor.ArmorProperties>?))!!
                    StandardizeList(props, damage)
                    var level: Int = props[0].Priority
                    var ratio: Double = 0.0
                    for (prop: ArmorProperties in props) {
                        if (level != prop.Priority) {
                            damage -= (damage * ratio)
                            ratio = 0.0
                            level = prop.Priority
                        }
                        ratio += prop.AbsorbRatio

                        val absorb: Double = damage * prop.AbsorbRatio
                        if (absorb > 0) {
                            val stack: ItemStack = inventory.get(prop.Slot)
                            val itemDamage: Int = max(1.0, absorb).toInt()
                            if (stack.getItem() is ISpecialArmor) {
                                (stack.getItem() as ISpecialArmor).damageArmor(
                                    entity,
                                    stack,
                                    source,
                                    itemDamage,
                                    prop.Slot
                                )
                            } else {
                                if (DEBUG) {
                                    println("Item: " + stack.toString() + " Absorbed: " + absorb + " Damaged: " + itemDamage)
                                }
                                stack.damageItem(itemDamage, entity)
                            }
                            if (stack.isEmpty()) {
                                /*if (entity instanceof EntityPlayer)
                            {
                                stack.onItemDestroyedByUse((EntityPlayer)entity);
                            }*/
                                inventory.set(prop.Slot, ItemStack.EMPTY)
                            }
                        }
                    }
                    damage -= (damage * ratio)
                }
                if (damage > 0 && (totalArmor > 0 || totalToughness > 0)) {
                    val armorDamage: Double = max(1.0, damage / 4.0f)

                    for (i: Int in inventory.indices) {
                        if (inventory.get(i).getItem() is ItemArmor) {
                            inventory.get(i).damageItem(armorDamage.toInt(), entity)

                            if (inventory.get(i).getCount() == 0) {
                                inventory.set(i, ItemStack.EMPTY)
                            }
                        }
                    }
                    damage = CombatRules.getDamageAfterAbsorb(
                        damage.toFloat(),
                        totalArmor.toFloat(),
                        totalToughness.toFloat()
                    ).toDouble()
                }
                if (DEBUG) {
                    println("Return: " + (damage).toInt() + " " + damage)
                }
                return (damage).toFloat()
            }

            /**
             * Sorts and standardizes the distribution of damage over armor.
             *
             * @param armor The armor information
             * @param damage The total damage being received
             */
            @JvmStatic
            private fun StandardizeList(armor: Array<ArmorProperties>, damage: Double) {
                var damage: Double = damage
                Arrays.sort(armor)

                var start: Int = 0
                var total: Double = 0.0
                var priority: Int = armor[0].Priority
                var pStart: Int = 0
                var pChange: Boolean = false
                var pFinished: Boolean = false

                if (DEBUG) {
                    for (prop: ArmorProperties? in armor) {
                        println(prop)
                    }
                    println("========================")
                }

                var x: Int = 0
                while (x < armor.size) {
                    total += armor[x].AbsorbRatio
                    if (x == armor.size - 1 || armor[x].Priority != priority) {
                        if (armor[x].Priority != priority) {
                            total -= armor[x].AbsorbRatio
                            x--
                            pChange = true
                        }
                        if (total > 1) {
                            var y: Int = start
                            while (y <= x) {
                                val newRatio: Double = armor[y].AbsorbRatio / total
                                if (newRatio * damage > armor[y].AbsorbMax) {
                                    armor[y].AbsorbRatio = armor[y].AbsorbMax.toDouble() / damage
                                    total = 0.0
                                    for (z: Int in pStart..y) {
                                        total += armor[z].AbsorbRatio
                                    }
                                    start = y + 1
                                    x = y
                                    break
                                } else {
                                    armor[y].AbsorbRatio = newRatio
                                    pFinished = true
                                }
                                y++
                            }
                            if (pChange && pFinished) {
                                damage -= (damage * total)
                                total = 0.0
                                start = x + 1
                                priority = armor[start].Priority
                                pStart = start
                                pChange = false
                                pFinished = false
                                if (damage <= 0) {
                                    for (y: Int in x + 1..<armor.size) {
                                        armor[y].AbsorbRatio = 0.0
                                    }
                                    break
                                }
                            }
                        } else {
                            for (y: Int in start..x) {
                                total -= armor[y].AbsorbRatio
                                if (damage * armor[y].AbsorbRatio > armor[y].AbsorbMax) {
                                    armor[y].AbsorbRatio = armor[y].AbsorbMax.toDouble() / damage
                                }
                                total += armor[y].AbsorbRatio
                            }
                            damage -= (damage * total)
                            total = 0.0
                            if (x != armor.size - 1) {
                                start = x + 1
                                priority = armor[start].Priority
                                pStart = start
                                pChange = false
                                if (damage <= 0) {
                                    for (y: Int in x + 1..<armor.size) {
                                        armor[y].AbsorbRatio = 0.0
                                    }
                                    break
                                }
                            }
                        }
                    }
                    x++
                }
                if (DEBUG) {
                    for (prop: ArmorProperties? in armor) {
                        println(prop)
                    }
                }
            }
        }
    }
}
