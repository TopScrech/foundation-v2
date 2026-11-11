package galaxyspace.systems.SolarSystem.planets.pluto.dimension

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import com.google.common.collect.Lists
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.systems.SolarSystem.planets.pluto.world.gen.BiomeDecoratorPluto
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import galaxyspace.systems.SolarSystem.planets.pluto.dimension.ChunkProviderPluto
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderPluto constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val caveGenerator = MapGenCaves(
        GSBlocks.PLUTO_BLOCKS.getStateFromMeta(0),
        GSBlocks.PLUTO_BLOCKS.getStateFromMeta(4),
        GSBlocks.PLUTO_BLOCKS.getStateFromMeta(5)
    )

    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        generators.add(this.caveGenerator)
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    init {
        setBlocks(
            GenBlocks(
                this.worldObj.provider,
                ACBiome.ACSpace,
                GSBlocks.PLUTO_BLOCKS.getStateFromMeta(0),
                this.getDirtBlock()
            )
        )
        setBlocks(
            GenBlocks(
                this.worldObj.provider,
                ACBiome.ACSpaceLowPlains,
                GSBlocks.PLUTO_BLOCKS.getStateFromMeta(1),
                this.getDirtBlock()
            )
        )
        setBlocks(
            GenBlocks(
                this.worldObj.provider,
                ACBiome.ACSpaceMidPlains,
                GSBlocks.PLUTO_BLOCKS.getStateFromMeta(2),
                this.getDirtBlock()
            )
        )
        setBlocks(
            GenBlocks(
                this.worldObj.provider,
                ACBiome.ACSpaceLowHills,
                GSBlocks.PLUTO_BLOCKS.getStateFromMeta(3),
                this.getDirtBlock()
            )
        )
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorPluto()
    }

    //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    override fun getBiomesForGeneration(): Array<Biome> {
        return ((arrayOf<Biome?>(
            ACBiome.ACSpace,
            ACBiome.ACSpaceLowPlains,
            ACBiome.ACSpaceMidPlains,
            ACBiome.ACSpaceLowHills
        ) as Array<Biome>?))!!
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
        return GSBlocks.PLUTO_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return GSBlocks.PLUTO_BLOCKS.getStateFromMeta(4)
    }

    override fun getStoneBlock(): IBlockState {
        return GSBlocks.PLUTO_BLOCKS.getStateFromMeta(5)
    }

    override fun enableBiomeGenBaseBlock(): Boolean {
        return true
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
