package foundation.mixin

import foundation.effect.FoundationEffectCures
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/** Milk used the Forge curative-item contract in 1.12, rather than unconditional effect removal */
@Mixin(value = [ClearAllStatusEffectsConsumeEffect::class], remap = false)
abstract class MilkEffectCuresMixin {
    @Inject(method = ["apply"], at = [At("HEAD")], cancellable = true, remap = false)
    private fun foundationCureMilk(level: Level, stack: ItemStack, user: LivingEntity, callback: CallbackInfoReturnable<Boolean>) {
        if (stack.`is`(Items.MILK_BUCKET)) callback.returnValue = FoundationEffectCures.cure(user, stack)
    }
}
