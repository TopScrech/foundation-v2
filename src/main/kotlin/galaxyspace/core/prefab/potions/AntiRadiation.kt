package galaxyspace.core.prefab.potions

import asmodeuscore.core.utils.ACAttributePlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.ai.attributes.AttributeInstance

open class AntiRadiation(isBad: Boolean, color: Int) : MobEffect(if (isBad) MobEffectCategory.HARMFUL else MobEffectCategory.BENEFICIAL, color) {
    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        val interval = 20 shr amplifier
        return interval <= 0 || duration % interval == 0
    }

    override fun applyEffectTick(level: ServerLevel, living: LivingEntity, amplifier: Int): Boolean {
        if (living is Player) {
            val radiation = living.getAttribute(ACAttributePlayer.RADIATION_LVL)
            if (radiation != null) return reduceRadiation(radiation)
        }
        return true
    }

    fun reduceRadiation(radiation: AttributeInstance): Boolean {
        if (radiation.value <= 0) return false
        radiation.baseValue = radiation.value - 1
        return true
    }
}
