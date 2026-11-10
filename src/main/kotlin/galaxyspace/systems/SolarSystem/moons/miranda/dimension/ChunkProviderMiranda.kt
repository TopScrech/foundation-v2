package galaxyspace.systems.SolarSystem.moons.miranda.dimension

import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import galaxyspace.core.GSBlocks
import com.google.common.collect.Lists
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks.EnumMirandaBlocks
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves.BlockGen
import galaxyspace.systems.SolarSystem.moons.miranda.world.gen.MapGenIceMiranda
import galaxyspace.systems.SolarSystem.moons.miranda.world.gen.BiomeDecoratorMiranda
import galaxyspace.systems.SolarSystem.moons.miranda.dimension.ChunkProviderMiranda
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.block.state.IBlockState
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderMiranda constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val grass = BlockGen(
        GSBlocks.MIRANDA_BLOCKS.getDefaultState().withProperty(
            MirandaBlocks.Companion.BASIC_TYPE,
            EnumMirandaBlocks.MIRANDA_GRUNT
        ), true
    )
    private val subgrunt = BlockGen(
        GSBlocks.MIRANDA_BLOCKS.getDefaultState().withProperty(
            MirandaBlocks.Companion.BASIC_TYPE,
            EnumMirandaBlocks.MIRANDA_SUBGRUNT
        ), false
    )
    private val stone = BlockGen(
        GSBlocks.MIRANDA_BLOCKS.getDefaultState().withProperty(
            MirandaBlocks.Companion.BASIC_TYPE,
            EnumMirandaBlocks.MIRANDA_STONE
        ), false
    )

    private val grass_2 = BlockGen(
        GSBlocks.MIRANDA_BLOCKS.getDefaultState().withProperty(
            MirandaBlocks.Companion.BASIC_TYPE,
            EnumMirandaBlocks.MIRANDA_GRUNT_2
        ), true
    )
    private val subgrunt_2 = BlockGen(
        GSBlocks.MIRANDA_BLOCKS.getDefaultState().withProperty(
            MirandaBlocks.Companion.BASIC_TYPE,
            EnumMirandaBlocks.MIRANDA_SUBGRUNT_2
        ), false
    )
    private val stone_2 = BlockGen(
        GSBlocks.MIRANDA_BLOCKS.getDefaultState().withProperty(
            MirandaBlocks.Companion.BASIC_TYPE,
            EnumMirandaBlocks.MIRANDA_STONE_2
        ), false
    )

    private val grass_3 = BlockGen(
        GSBlocks.MIRANDA_BLOCKS.getDefaultState().withProperty(
            MirandaBlocks.Companion.BASIC_TYPE,
            EnumMirandaBlocks.MIRANDA_GRUNT_3
        ), true
    )
    private val subgrunt_3 = BlockGen(
        GSBlocks.MIRANDA_BLOCKS.getDefaultState().withProperty(
            MirandaBlocks.Companion.BASIC_TYPE,
            EnumMirandaBlocks.MIRANDA_SUBGRUNT_3
        ), false
    )
    private val stone_3 = BlockGen(
        GSBlocks.MIRANDA_BLOCKS.getDefaultState().withProperty(
            MirandaBlocks.Companion.BASIC_TYPE,
            EnumMirandaBlocks.MIRANDA_STONE_3
        ), false
    )

    private val caveGenerator =
        MapGenCaves(grass, subgrunt, stone, grass_2, subgrunt_2, stone_2, grass_3, subgrunt_3, stone_3)
    private val iceGenerator = MapGenIceMiranda()

    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        generators.add(this.caveGenerator)
        generators.add(this.iceGenerator)
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    init {
        setBlocks(
            GenBlocks(
                this.worldObj.provider,
                getBiomesForGeneration()[0],
                this.getGrassBlock(),
                this.getDirtBlock()
            )
        )
        setBlocks(
            GenBlocks(
                this.worldObj.provider,
                getBiomesForGeneration()[1],
                GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(9),
                GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(11)
            ).setCustomStone(GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(13))
        )
        setBlocks(
            GenBlocks(
                this.worldObj.provider,
                getBiomesForGeneration()[2],
                GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(10),
                GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(12)
            ).setCustomStone(GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(14))
        )
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorMiranda()
    }

    //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    override fun getBiomesForGeneration(): Array<Biome> {
        return ((arrayOf<Biome?>(ACBiome.ACSpace, ACBiome.ACSpaceLowPlains, ACBiome.ACSpaceMidHills) as Array<Biome>?))!!
    }

    override fun getCraterProbability(): Int {
        return 300
    }


    override fun getHeightModifier(): Double {
        return 55.0
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
        return 25.0
    }

    override fun getValleyHeightModifier(): Double {
        return 25.0
    }

    override fun getGrassBlock(): IBlockState {
        return GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(1)
    }

    override fun getStoneBlock(): IBlockState {
        return GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(2)
    }

    override fun enableBiomeGenBaseBlock(): Boolean {
        return true
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
