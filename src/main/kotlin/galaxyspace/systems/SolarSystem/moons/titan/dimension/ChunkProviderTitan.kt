package galaxyspace.systems.SolarSystem.moons.titan.dimension

import galaxyspace.core.GSBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import com.google.common.collect.Lists
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import galaxyspace.core.GSFluids
import galaxyspace.systems.SolarSystem.moons.titan.world.gen.BiomeDecoratorTitan
import galaxyspace.systems.SolarSystem.moons.titan.dimension.ChunkProviderTitan
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.block.state.IBlockState
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderTitan constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val worldGenerators: MutableList<MapGenBaseMeta?>? = null

    //private final MapGenCaves caveGenerator = new MapGenCaves(GSBlocks.CALLISTO_BLOCKS, 0, 1, 1);
    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        //generators.add(this.ravineGenerator);
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorTitan()
    }

    //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    override fun getBiomesForGeneration(): Array<Biome> {
        return ((arrayOf<Biome?>(ACBiome.ACSpace) as Array<Biome>?))!!
    }

    override fun getCraterProbability(): Int {
        return 0
    }


    override fun getHeightModifier(): Double {
        return 20.0
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun getMountainHeightModifier(): Double {
        return 10.0
    }

    override fun getWaterLevel(): Int {
        return 75
    }

    override fun getTerrainLevel(): Int {
        return 64
    }

    override fun getSmallFeatureHeightModifier(): Double {
        return 10.0
    }

    override fun getValleyHeightModifier(): Double {
        return 0.0
    }

    override fun getGrassBlock(): IBlockState {
        return GSBlocks.TITAN_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return GSBlocks.TITAN_BLOCKS.getStateFromMeta(1)
    }

    override fun getStoneBlock(): IBlockState {
        return GSBlocks.TITAN_BLOCKS.getStateFromMeta(2)
    }

    override fun enableBiomeGenBaseBlock(): Boolean {
        return false
    }

    override fun canGenerateWaterBlock(): Boolean {
        return true
    }

    override fun canGenerateIceBlock(): Boolean {
        return true
    }

    override fun getWaterBlock(): IBlockState {
        return (GSFluids.BLOCK_LEMETHANE)!!.getDefaultState()
    }

    override fun getGenType(): GenType {
        return GenType.VANILLA
    }

    override fun recreateStructures(chunkIn: Chunk, x: Int, z: Int) {
    }
}
