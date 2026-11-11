package galaxyspace.systems.SolarSystem.moons.io.dimension

import foundation.block.getStateFromMeta

import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks.EnumIoBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.MapGenDungeonIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.CorridorIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomEmptyIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomSpawnerIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomChestIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomBossIo
import galaxyspace.systems.SolarSystem.moons.io.world.gen.dungeon.RoomTreasureIo
import com.google.common.collect.Lists
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.BiomeDecoratorIo
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.systems.SolarSystem.moons.io.dimension.ChunkProviderIo
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderIo constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val worldGenerators: MutableList<MapGenBaseMeta?>? = null

    private val caveGenerator =
        MapGenCaves(GSBlocks.IO_BLOCKS.getStateFromMeta(0), GSBlocks.IO_BLOCKS.getStateFromMeta(1))

    private val top: IBlockState = GSBlocks.IO_BLOCKS.defaultBlockState()
        .setValue(IoBlocks.BASIC_TYPE, EnumIoBlocks.IO_DUNGEON_TOP)
    private val floor: IBlockState = GSBlocks.IO_BLOCKS.defaultBlockState()
        .setValue(IoBlocks.BASIC_TYPE, EnumIoBlocks.IO_DUNGEON_FLOOR)
    private val bricks: IBlockState = GSBlocks.IO_BLOCKS.defaultBlockState()
        .setValue(IoBlocks.BASIC_TYPE, EnumIoBlocks.IO_DUNGEON_BRICKS)

    private val dungeonGenerator = MapGenDungeonIo(
        DungeonConfiguration(
            bricks,
            top,
            floor,
            30,
            30,
            35,
            7,
            7,
            CorridorIo::class.java,
            RoomEmptyIo::class.java,
            RoomSpawnerIo::class.java,
            RoomChestIo::class.java,
            RoomBossIo::class.java,
            RoomTreasureIo::class.java
        )
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
                getBiomesForGeneration()[0],
                this.getGrassBlock(),
                this.getDirtBlock()
            )
        )
        setBlocks(
            GenBlocks(
                this.worldObj.provider,
                getBiomesForGeneration()[1],
                GSBlocks.IO_BLOCKS.getStateFromMeta(2),
                this.getDirtBlock()
            )
        )
        setBlocks(
            GenBlocks(
                this.worldObj.provider,
                getBiomesForGeneration()[2],
                this.getGrassBlock(),
                this.getDirtBlock()
            )
        )
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorIo()
    }

    //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    override fun getBiomesForGeneration(): Array<Biome> {
        return ((arrayOf<Biome?>(ACBiome.ACSpace, ACBiome.ACSpaceLowHills, ACBiome.ACSpaceLowPlains) as Array<Biome>?))!!
    }

    override fun getCraterProbability(): Int {
        return 500
    }


    override fun getHeightModifier(): Double {
        return 25.0
    }

    override fun getMountainHeightModifier(): Double {
        return 30.0
    }

    override fun getWaterLevel(): Int {
        return 80
    }

    override fun getSmallFeatureHeightModifier(): Double {
        return 60.0
    }

    override fun getValleyHeightModifier(): Double {
        return 0.0
    }

    override fun getGrassBlock(): IBlockState {
        return GSBlocks.IO_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return GSBlocks.IO_BLOCKS.getStateFromMeta(1)
    }

    override fun getStoneBlock(): IBlockState {
        return GSBlocks.IO_BLOCKS.getStateFromMeta(1)
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

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
        if (GSConfigCore.enableDungeonsGeneration) this.dungeonGenerator.generate(this.worldObj, cX, cZ, primer)
    }

    override fun onPopulate(cX: Int, cZ: Int) {
        if (GSConfigCore.enableDungeonsGeneration) this.dungeonGenerator.generateStructure(
            this.worldObj,
            this.rand,
            ChunkPos(cX, cZ)
        )
    }

    override fun recreateStructures(chunkIn: Chunk, x: Int, z: Int) {
        if (GSConfigCore.enableDungeonsGeneration) this.dungeonGenerator.generate(this.worldObj, x, z, null)
    }
}
