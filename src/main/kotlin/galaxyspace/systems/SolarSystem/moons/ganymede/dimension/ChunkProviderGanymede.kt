package galaxyspace.systems.SolarSystem.moons.ganymede.dimension

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import com.google.common.collect.Lists
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves.BlockGen
import galaxyspace.systems.SolarSystem.moons.ganymede.world.gen.MapGenSnowGanymede
import galaxyspace.systems.SolarSystem.moons.ganymede.world.gen.BiomeDecoratorGanymede
import galaxyspace.systems.SolarSystem.moons.ganymede.dimension.ChunkProviderGanymede
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderGanymede constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val worldGenerators: MutableList<MapGenBaseMeta?>? = null


    private val grass = BlockGen(getGrassBlock(), true)
    private val subgrunt = BlockGen(getDirtBlock(), false)
    private val stone = BlockGen(getStoneBlock(), false)


    private val caveGenerator = MapGenCaves(grass, subgrunt, stone)
    private val snowGenerator = MapGenSnowGanymede()


    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        generators.add(this.caveGenerator)
        generators.add(this.snowGenerator)
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorGanymede()
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
        return 25.0
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun getMountainHeightModifier(): Double {
        return 45.0
    }

    override fun getWaterLevel(): Int {
        return 70
    }

    override fun getSmallFeatureHeightModifier(): Double {
        return 35.0
    }

    override fun getValleyHeightModifier(): Double {
        return 35.0
    }

    override fun getGrassBlock(): IBlockState {
        return GSBlocks.GANYMEDE_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return GSBlocks.SURFACE_ICE.getStateFromMeta(4)
    }

    override fun getStoneBlock(): IBlockState {
        return GSBlocks.GANYMEDE_BLOCKS.getStateFromMeta(1)
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

    override fun getCraterAdditions(primer: ChunkPrimer?, x: Int, y: Int, z: Int) {
        /*if(this.rand.nextInt(8) == 0 && primer.getBlockState(x, y - 1, z).getBlock() != Blocks.AIR)
		{
			primer.setBlockState(x, y - 1, z, GSBlocks.SURFACE_ICE.defaultBlockState());
		}*/
    }
}
