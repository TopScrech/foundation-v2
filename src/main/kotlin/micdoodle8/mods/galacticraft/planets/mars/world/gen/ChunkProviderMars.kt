package micdoodle8.mods.galacticraft.planets.mars.world.gen

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.DungeonConfiguration
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockBasicMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer

open class ChunkProviderMars constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpace((par1World)!!, seed, mapFeaturesEnabled) {
    private val marsBiomeDecorator = BiomeDecoratorMars()
    private val caveGenerator = MapGenCavernMars()
    private val cavernGenerator = MapGenCaveMars()

    private val dungeonGenerator: MapGenDungeon = MapGenDungeonMars(
        DungeonConfiguration(
            (MarsBlocks.marsBlock)!!.getDefaultState()
                .withProperty(
                    BlockBasicMars.BASIC_TYPE,
                    BlockBasicMars.EnumBlockBasic.DUNGEON_BRICK
                ), 30, 8, 16, 7, 7, RoomBossMars::class.java, RoomTreasureMars::class.java
        )
    )

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return this.marsBiomeDecorator
    }

    override fun getBiomesForGeneration(): Array<Biome?> {
        return arrayOf<Biome?>(BiomeAdaptive.biomeDefault)
    }

    override fun getSeaLevel(): Int {
        return 93
    }

    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        generators.add(this.caveGenerator)
        generators.add(this.cavernGenerator)
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    override fun getGrassBlock(): BlockMetaPair {
        return BiomeMars.Companion.BLOCK_TOP
    }

    override fun getDirtBlock(): BlockMetaPair {
        return BiomeMars.Companion.BLOCK_FILL
    }

    override fun getStoneBlock(): BlockMetaPair {
        return BiomeMars.Companion.BLOCK_LOWER
    }

    override fun getHeightModifier(): Double {
        return 12.0
    }

    override fun getSmallFeatureHeightModifier(): Double {
        return 26.0
    }

    override fun getMountainHeightModifier(): Double {
        return 95.0
    }

    override fun getValleyHeightModifier(): Double {
        return 50.0
    }

    override fun getCraterProbability(): Int {
        return 2000
    }

    override fun onChunkProvide(cX: Int, cZ: Int, primer: ChunkPrimer?) {
        this.dungeonGenerator.generate(this.world, cX, cZ, primer)
    }

    override fun onPopulate(cX: Int, cZ: Int) {
        this.dungeonGenerator.generateStructure(this.world, this.rand, ChunkPos(cX, cZ))
    }

    override fun recreateStructures(chunk: Chunk, x: Int, z: Int) {
        this.dungeonGenerator.generate(this.world, x, z, null)
    }
}
