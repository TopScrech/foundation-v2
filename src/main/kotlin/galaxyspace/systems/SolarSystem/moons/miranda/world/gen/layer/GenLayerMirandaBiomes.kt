package galaxyspace.systems.SolarSystem.moons.miranda.world.gen.layer

import galaxyspace.systems.SolarSystem.moons.miranda.world.gen.layer.GenLayerMirandaBiomes
import galaxyspace.systems.SolarSystem.SolarSystemBodies
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.miccore.IntCache
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.layer.GenLayer

open class GenLayerMirandaBiomes : GenLayer {
    constructor(l: Long, parent: GenLayer?) : super(l) {
        //biomes = this.getBiomes(body);
        for (i: Int in (body.getBiomes())!!.indices) biomes[i] = (body.getBiomes())!!.get(i)

        this.parent = parent
    }

    constructor(l: Long) : super(l) {
        //biomes = this.getBiomes(body);
        for (i: Int in (body.getBiomes())!!.indices) biomes[i] = (body.getBiomes())!!.get(i)
    }

    override fun getInts(x: Int, z: Int, width: Int, depth: Int): IntArray {
        val dest: IntArray = IntCache.getIntCache(width * depth)

        for (k: Int in 0..<depth) {
            for (i: Int in 0..<width) {
                initChunkSeed((x + i).toLong(), (z + k).toLong())
                dest[i + k * width] = Biome.getIdForBiome(biomes[nextInt(biomes.size)])
            }
        }

        return dest
    } /*
	private Biome[] getBiomes(CelestialBody body)
	{
		return new Biome[body.getBiomes().size()];
	}*/

    companion object {
        private val body: CelestialBody = (SolarSystemBodies.mirandaUranus)!!

        private val biomes = arrayOfNulls<Biome>((body.getBiomes())!!.size)
    }
}
