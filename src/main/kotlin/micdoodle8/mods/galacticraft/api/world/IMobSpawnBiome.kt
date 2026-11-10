package micdoodle8.mods.galacticraft.api.world

import java.util.LinkedList
import net.minecraft.world.biome.Biome.SpawnListEntry

import net.minecraft.world.biome.Biome
import java.util.*

/**
 * Implement this on any Galacticraft World Provider biome registered for a Celestial Body
 */
interface IMobSpawnBiome {
    fun initialiseMobLists(mobInfo: LinkedList<Biome.SpawnListEntry?>?)
}
