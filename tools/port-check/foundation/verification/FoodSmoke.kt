package foundation.verification

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariants
import foundation.persistence.FoundationItemStackPersistence
import micdoodle8.mods.galacticraft.core.items.ItemFood
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import kotlin.math.abs

fun verifyFood(lookup: HolderLookup.Provider) {
    val item = FoundationItemProperties.registered(Identifier.parse("galacticraftcore:food")) as ItemFood
    val nutrition = listOf(8, 8, 4, 2, 2, 4, 2, 4, 14, 8)
    val modifiers = listOf(0.3f, 0.6f, 0.3f, 0.3f, 0.1f, 0.8f, 0.3f, 0.6f, 1f, 0.6f)
    for (metadata in 0..9) {
        val stack = FoundationItemVariants.stack(item, 3, metadata)
        val food = requireNotNull(stack.get(DataComponents.FOOD))
        check(food.nutrition() == nutrition[metadata])
        check(abs(food.saturation() - nutrition[metadata] * modifiers[metadata] * 2) < 0.00001f)
        check(!food.canAlwaysEat())
        val consumable = requireNotNull(stack.get(DataComponents.CONSUMABLE))
        check(consumable.consumeTicks() == 32 && consumable.animation() == ItemUseAnimation.EAT)
        check(!stack.has(DataComponents.USE_REMAINDER)) // Original canned containers dropped at the eater's position
        val before = stack.copy()
        val remainder = item.consumptionRemainder(stack)
        check(ItemStack.matches(before, stack))
        if (metadata in 0..3 || metadata == 9) {
            check(remainder.item === FoundationItemProperties.registered(Identifier.parse("galacticraftcore:canister")))
            check(remainder.count == 1 && FoundationItemVariants.metadata(remainder) == 0)
            remainder.shrink(1)
            check(item.consumptionRemainder(stack).count == 1)
        } else check(remainder.isEmpty)
        val old = CompoundTag().also {
            it.putString("id", "galacticraftcore:food"); it.putByte("Count", 3); it.putShort("Damage", metadata.toShort())
        }
        val imported = FoundationItemStackPersistence.read(old, lookup)
        check(imported.get(DataComponents.FOOD) == food && FoundationItemVariants.metadata(imported) == metadata)
        // Reassigning a variant updates its food component without conflating it with damage
        FoundationItemVariants.apply(stack, (metadata + 1) % 10)
        check(stack.get(DataComponents.FOOD)?.nutrition() == nutrition[(metadata + 1) % 10] && stack.damageValue == 0)
    }
    check(item.consumptionRemainder(ItemStack.EMPTY).isEmpty)
    check(runCatching { ItemFood.foodProperties(10) }.isFailure)
    check(runCatching { FoundationItemProperties.registered(Identifier.parse("foundation:missing_food_container")) }.isFailure)
    println("Passed ten food nutrition and saturation profiles, native eating components, legacy food imports, component reassignment, and independent canned container drops")
}
