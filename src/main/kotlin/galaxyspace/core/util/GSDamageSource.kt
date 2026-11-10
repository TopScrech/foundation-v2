package galaxyspace.core.util

import galaxyspace.core.util.GSDamageSource
import net.minecraft.util.DamageSource

open class GSDamageSource constructor(damageType: String) : DamageSource(damageType) {
    companion object {
        @JvmField
        val acid: GSDamageSource = GSDamageSource("acid").setDamageIsAbsolute() as GSDamageSource
    }
}
