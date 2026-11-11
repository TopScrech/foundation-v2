package galaxyspace.systems.SolarSystem.planets.ceres.dimension

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import com.google.common.collect.Lists
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks.EnumCeresBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks
import galaxyspace.core.prefab.blocks.DungeonBlocks.EnumDungeonBlocks
import galaxyspace.core.prefab.blocks.DungeonBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.MapGenDungeonCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.CorridorCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomEmptyCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomSpawnerCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomChestCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomBossCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.dungeon.RoomTreasureCeres
import galaxyspace.systems.SolarSystem.planets.ceres.world.gen.BiomeDecoratorCeres
import galaxyspace.systems.SolarSystem.planets.ceres.dimension.ChunkProviderCeres
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves
import asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart.DungeonConfiguration
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderCeres constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val worldGenerators: MutableList<MapGenBaseMeta?>? = null

    private val caveGenerator =
        MapGenCaves(GSBlocks.CERES_BLOCKS.getStateFromMeta(0), GSBlocks.CERES_BLOCKS.getStateFromMeta(1))

    private val top: IBlockState = GSBlocks.CERES_BLOCKS.defaultBlockState()
        .setValue(
            CeresBlocks.Companion.BASIC_TYPE,
            EnumCeresBlocks.CERES_DUNGEON_TOP
        )
    private val floor: IBlockState = GSBlocks.CERES_BLOCKS.defaultBlockState()
        .setValue(
            CeresBlocks.Companion.BASIC_TYPE,
            EnumCeresBlocks.CERES_DUNGEON_FLOOR
        )
    private val bricks: IBlockState = GSBlocks.DUNGEON_BLOCKS.defaultBlockState()
        .setValue(DungeonBlocks.BASIC_TYPE, EnumDungeonBlocks.CERES_BRICKS)
    private val dungeonGenerator = MapGenDungeonCeres(
        DungeonConfiguration(
            bricks,
            top,
            floor,
            30,
            8,
            16,
            7,
            7,
            CorridorCeres::class.java,
            RoomEmptyCeres::class.java,
            RoomSpawnerCeres::class.java,
            RoomChestCeres::class.java,
            RoomBossCeres::class.java,
            RoomTreasureCeres::class.java
        )
    )

    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        generators.add(this.caveGenerator)
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorCeres()
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
        return GSBlocks.CERES_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return GSBlocks.CERES_BLOCKS.getStateFromMeta(1)
    }

    override fun getStoneBlock(): IBlockState {
        return GSBlocks.CERES_BLOCKS.getStateFromMeta(1)
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
}
