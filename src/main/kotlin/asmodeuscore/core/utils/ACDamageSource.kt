package asmodeuscore.core.utils

import asmodeuscore.core.utils.ACDamageSource
import net.minecraft.util.DamageSource

open class ACDamageSource constructor(damageType: String) : DamageSource(damageType) {
    companion object {
        @JvmField
        val solar: DamageSource = ACDamageSource("solar").setDamageBypassesArmor()

        @JvmField
        val pressure: DamageSource = ACDamageSource("pressure").setDamageBypassesArmor()

        @JvmField
        val heat: DamageSource = ACDamageSource("heat").setDamageBypassesArmor()

        @JvmField
        val cold: DamageSource = ACDamageSource("cold").setDamageBypassesArmor()
    }
}
