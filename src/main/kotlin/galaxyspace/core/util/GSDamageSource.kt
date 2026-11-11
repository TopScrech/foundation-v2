package galaxyspace.core.util

import foundation.damage.FoundationDamageTypes
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

object GSDamageSource {
    @JvmField val acid: ResourceKey<DamageType> = FoundationDamageTypes.key("galaxyspace:acid")
}
