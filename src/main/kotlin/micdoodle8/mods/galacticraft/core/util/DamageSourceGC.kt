package micdoodle8.mods.galacticraft.core.util

import foundation.damage.FoundationDamageTypes
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

object DamageSourceGC {
    @JvmField val spaceshipCrash: ResourceKey<DamageType> = FoundationDamageTypes.key("galacticraftcore:spaceship_crash")
    @JvmField val oxygenSuffocation: ResourceKey<DamageType> = FoundationDamageTypes.key("galacticraftcore:oxygen_suffocation")
    @JvmField val thermal: ResourceKey<DamageType> = FoundationDamageTypes.key("galacticraftcore:thermal")
    @JvmField val acid: ResourceKey<DamageType> = FoundationDamageTypes.key("galacticraftcore:sulphuric_acid")
}
