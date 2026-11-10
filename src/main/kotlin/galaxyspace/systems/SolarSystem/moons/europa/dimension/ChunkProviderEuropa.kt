package galaxyspace.systems.SolarSystem.moons.europa.dimension

import galaxyspace.core.GSBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.MapGenRavineEuropa
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.MapGenSnowEuropa
import com.google.common.collect.Lists
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.BiomeDecoratorEuropa
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import galaxyspace.systems.SolarSystem.moons.europa.dimension.ChunkProviderEuropa
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderEuropa constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val worldGenerators: MutableList<MapGenBaseMeta?>? = null

    //private final MapGenCaves caveGenerator = new MapGenCaves(GSBlocks.CERES_BLOCKS, 0, 1, 1);
    private val ravineGenerator = MapGenRavineEuropa()
    private val snowGenerator = MapGenSnowEuropa()

    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        generators.add(this.ravineGenerator)
        generators.add(this.snowGenerator)
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorEuropa()
    }

    //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    override fun getBiomesForGeneration(): Array<Biome> {
        return ((arrayOf<Biome?>(ACBiome.ACSpace) as Array<Biome>?))!!
    }

    override fun getCraterProbability(): Int {
        return 900
    }


    override fun getHeightModifier(): Double {
        return 8.0
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun getMountainHeightModifier(): Double {
        return 0.0
    }

    override fun getWaterLevel(): Int {
        return 95
    }

    override fun getSmallFeatureHeightModifier(): Double {
        return 0.0
    }

    override fun getValleyHeightModifier(): Double {
        return 0.0
    }

    override fun getGrassBlock(): IBlockState {
        return GSBlocks.EUROPA_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return Blocks.PACKED_ICE.getDefaultState()
    }

    override fun getStoneBlock(): IBlockState {
        return Blocks.WATER.getDefaultState()
    }

    override fun enableBiomeGenBaseBlock(): Boolean {
        return false
    }

    override fun canGenerateWaterBlock(): Boolean {
        return false
    }

    override fun canGenerateIceBlock(): Boolean {
        return true
    }

    override fun getWaterBlock(): IBlockState? {
        return null
    }

    override fun getGenType(): GenType {
        return GenType.GC
    }

    override fun recreateStructures(chunkIn: Chunk, x: Int, z: Int) {
    }

    override fun getDirtLayerSize(): Double {
        return 40.0
    }

    override fun getIceBlock(): IBlockState {
        return GSBlocks.EUROPA_BLOCKS.getStateFromMeta(1)
    }
}
