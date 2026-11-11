package micdoodle8.mods.galacticraft.planets.asteroids.world.gen

import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.world.level.biome.Biome
import net.minecraftforge.common.BiomeDictionary
import java.util.LinkedList

open class BiomeAsteroids private constructor(properties: BiomeProperties) : BiomeGenBaseGC(properties, true) {
    init {
        this.spawnableMonsterList.clear()
        this.spawnableWaterCreatureList.clear()
        this.spawnableCreatureList.clear()
        this.spawnableCaveCreatureList.clear()
        this.resetMonsterListByMode(ConfigManagerCore.challengeMobDropsAndSpawning)
    }

    override fun registerTypes(b: Biome?) {
        //Currently unused for Asteroids due to adaptive biomes system
        BiomeDictionary.addTypes(
            b,
            BiomeDictionary.Type.COLD,
            BiomeDictionary.Type.DRY,
            BiomeDictionary.Type.DEAD,
            BiomeDictionary.Type.SPOOKY
        )
    }

    open fun resetMonsterListByMode(challengeMode: Boolean) {
        this.spawnableMonsterList.clear()
        this.spawnableMonsterList.add(SpawnListEntry(EntityEvolvedZombie::class.java, 3000, 1, 3))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSpider::class.java, 2000, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSkeleton::class.java, 1500, 1, 1))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEvolvedCreeper::class.java, 2000, 1, 1))
        if (challengeMode) this.spawnableMonsterList.add(SpawnListEntry(EntityEvolvedEnderman::class.java, 250, 1, 1))
    }

    override fun initialiseMobLists(mobInfo: LinkedList<Biome.SpawnListEntry?>?) {
    }

    override fun getSpawningChance(): Float {
        return 0.01f
    }

    companion object {
        @JvmField
        val asteroid: Biome = BiomeAsteroids(BiomeProperties("Asteroids").setRainfall(0.0f))
    }
}
