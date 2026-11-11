package foundation.damage

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity

/** Damage holders belong to the current world's registries, never a process-wide cached world */
object FoundationDamageTypes {
    fun key(name: String): ResourceKey<DamageType> = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.parse(name))

    fun source(lookup: HolderLookup.Provider, type: ResourceKey<DamageType>): DamageSource =
        DamageSource(lookup.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type))

    fun hurt(entity: Entity, type: ResourceKey<DamageType>, amount: Float): Boolean {
        val level = entity.level() as? ServerLevel ?: return false
        return entity.hurtServer(level, source(level.registryAccess(), type), amount)
    }
}
