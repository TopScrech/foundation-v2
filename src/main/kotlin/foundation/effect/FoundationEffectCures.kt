package foundation.effect

import foundation.item.FoundationItemVariants
import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

object FoundationEffectCures {
    fun curable(effects: Collection<MobEffectInstance>, stack: ItemStack): List<Holder<MobEffect>> =
        effects.filter { instance -> instance.effect.value().curativeItems.any { FoundationItemVariants.sameVariant(it, stack) } }
            .map { it.effect }

    fun cure(user: LivingEntity, stack: ItemStack): Boolean {
        if (user.level().isClientSide) return false
        var removed = false
        for (effect in curable(user.activeEffects, stack)) removed = user.removeEffect(effect) || removed
        return removed
    }
}
