package galaxyspace.systems.SolarSystem.planets.mercury.dimension

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import galaxyspace.core.prefab.world.gen.MapGenCavesGS
import com.google.common.collect.Lists
import galaxyspace.systems.SolarSystem.planets.mercury.world.gen.BiomeDecoratorMercury
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderMercury constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val worldGenerators: MutableList<MapGenBaseMeta?>? = null

    private val caveGenerator = MapGenCavesGS(
        GSBlocks.MERCURY_BLOCKS.getStateFromMeta(0),
        GSBlocks.MERCURY_BLOCKS.getStateFromMeta(1),
        GSBlocks.MERCURY_BLOCKS.getStateFromMeta(2),
        Blocks.LAVA.defaultBlockState(),
        20
    )

    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        generators.add(this.caveGenerator)
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorMercury()
    }

    //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    override fun getBiomesForGeneration(): Array<Biome> {
        return ((arrayOf<Biome?>(BiomeAdaptive.biomeDefault) as Array<Biome>?))!!
    }

    override fun getCraterProbability(): Int {
        return 300
    }


    override fun getHeightModifier(): Double {
        return 20.0
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun getMountainHeightModifier(): Double {
        return 0.0
    }

    override fun getWaterLevel(): Int {
        return 80
    }

    override fun getSmallFeatureHeightModifier(): Double {
        return 1.0
    }

    override fun getValleyHeightModifier(): Double {
        return 0.0
    }

    override fun getGrassBlock(): IBlockState {
        return GSBlocks.MERCURY_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return GSBlocks.MERCURY_BLOCKS.getStateFromMeta(1)
    }

    override fun getStoneBlock(): IBlockState {
        return GSBlocks.MERCURY_BLOCKS.getStateFromMeta(2)
    }

    override fun enableBiomeGenBaseBlock(): Boolean {
        return false
    }

    override fun canGenerateWaterBlock(): Boolean {
        return false
    }

    override fun canGenerateIceBlock(): Boolean {
        return false
    }

    override fun getWaterBlock(): IBlockState? {
        return null
    }

    override fun getGenType(): GenType {
        return GenType.GC
    }

    override fun recreateStructures(chunkIn: Chunk, x: Int, z: Int) {
    }
}
