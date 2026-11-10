package galaxyspace.systems.SolarSystem.moons.enceladus.dimension

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.core.GSBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import com.google.common.collect.Lists
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves
import galaxyspace.systems.SolarSystem.moons.enceladus.world.gen.MapGenRavineEnceladus
import galaxyspace.systems.SolarSystem.moons.enceladus.world.gen.BiomeDecoratorEnceladus
import galaxyspace.systems.SolarSystem.moons.enceladus.dimension.ChunkProviderEnceladus
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderEnceladus constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val worldGenerators: MutableList<MapGenBaseMeta?>? = null

    private val caveGenerator =
        MapGenCaves(GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(0), GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(1))
    private val ravineGenerator = MapGenRavineEnceladus()

    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        generators.add(this.caveGenerator)
        generators.add(this.ravineGenerator)
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorEnceladus()
    }

    //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    override fun getBiomesForGeneration(): Array<Biome> {
        return ((arrayOf<Biome?>(ACBiome.ACSpace) as Array<Biome>?))!!
    }

    override fun getCraterProbability(): Int {
        return 500
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
        return 90
    }

    override fun getSmallFeatureHeightModifier(): Double {
        return 55.0
    }

    override fun getValleyHeightModifier(): Double {
        return 50.0
    }

    override fun getGrassBlock(): IBlockState {
        return GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return Blocks.PACKED_ICE.getDefaultState()
    }

    override fun getStoneBlock(): IBlockState {
        return GSBlocks.ENCELADUS_BLOCKS.getStateFromMeta(1)
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
}
