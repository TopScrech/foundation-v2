package asmodeuscore.core.prefab.potions

import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.utils.ACDamageSource
import foundation.damage.FoundationDamageTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

open class Radiation(isBad: Boolean, color: Int) : MobEffect(if (isBad) MobEffectCategory.HARMFUL else MobEffectCategory.BENEFICIAL, color) {
    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        val interval = 20 shr amplifier
        return interval <= 0 || duration % interval == 0
    }

    fun damageAmount(amplifier: Int): Float = 3.0f * AsmodeusConfig.solarRadiationDamageRate

    override fun applyEffectTick(level: ServerLevel, living: LivingEntity, amplifier: Int): Boolean {
        if (living is Player) FoundationDamageTypes.hurt(living, ACDamageSource.solar, damageAmount(amplifier))
        return true
    }

    override fun getCurativeItems(): MutableList<ItemStack> = arrayListOf()
}
