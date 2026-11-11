package asmodeuscore.core.utils

import foundation.damage.FoundationDamageTypes
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

object ACDamageSource {
    @JvmField val solar: ResourceKey<DamageType> = FoundationDamageTypes.key("asmodeuscore:solar")
    @JvmField val pressure: ResourceKey<DamageType> = FoundationDamageTypes.key("asmodeuscore:pressure")
    @JvmField val heat: ResourceKey<DamageType> = FoundationDamageTypes.key("asmodeuscore:heat")
    @JvmField val cold: ResourceKey<DamageType> = FoundationDamageTypes.key("asmodeuscore:cold")
}
