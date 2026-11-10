package micdoodle8.mods.galacticraft.api.world

import java.util.LinkedList

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAmbientCreature
import net.minecraft.entity.passive.EntityWaterMob
import net.minecraft.world.biome.Biome
import java.util.*

/**
 * This extension of BiomeGenBase contains the default initialiseMobLists()
 * called on CelestialBody registration to register mob spawn data
 */
abstract class BiomeGenBaseGC : Biome, IMobSpawnBiome {
    @JvmField
    val isAdaptiveBiome: Boolean

    protected constructor(var1: BiomeProperties) : super(var1) {
        this.setRegistryName(var1.biomeName)
        GalacticraftCore.biomesList.add(this)
        this.isAdaptiveBiome = false
    }

    protected constructor(properties: BiomeProperties, adaptive: Boolean) : super(properties) {
        this.setRegistryName(properties.biomeName)
        this.isAdaptiveBiome = adaptive
    }

    /**
     * Override this in your biomes
     * <br></br>
     * (Note: if adaptive biomes, only the FIRST to register the adaptive biome will have its
     * types registered in the BiomeDictionary - sorry, that's a Forge limitation.)
     */
    open fun registerTypes(registering: Biome?) {
    }

    /**
     * The default implementation in BiomeGenBaseGC will attempt to allocate each
     * SpawnListEntry in the CelestialBody's mobInfo to this biome's
     * Water, Cave, Monster or Creature lists according to whether the
     * spawnable entity's class is a subclass of EntityWaterMob, EntityAmbientCreature,
     * EntityMob or anything else (passive mobs or plain old EntityLiving).
     *
     * Override this if different behaviour is required.
     */
    override fun initialiseMobLists(mobInfo: LinkedList<Biome.SpawnListEntry?>?) {
        this.spawnableMonsterList.clear()
        this.spawnableWaterCreatureList.clear()
        this.spawnableCreatureList.clear()
        this.spawnableCaveCreatureList.clear()
        for (entry in (mobInfo)!!) {
            val mobClass: Class<*>? = (entry)!!.entityClass
            if (EntityWaterMob::class.java.isAssignableFrom(mobClass)) {
                this.spawnableWaterCreatureList.add(entry)
            } else if (EntityAmbientCreature::class.java.isAssignableFrom(mobClass)) {
                this.spawnableCaveCreatureList.add(entry)
            } else if (EntityMob::class.java.isAssignableFrom(mobClass)) {
                this.spawnableMonsterList.add(entry)
            } else {
                this.spawnableCreatureList.add(entry)
            }
        }
    }
}
