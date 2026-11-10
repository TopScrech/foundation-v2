package galaxyspace.systems.SolarSystem.moons.phobos.dimension

import galaxyspace.core.GSBlocks
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes
import com.google.common.collect.Lists
import galaxyspace.systems.SolarSystem.moons.phobos.world.gen.BiomeDecoratorPhobos
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import asmodeuscore.core.astronomy.dimension.world.gen.ChunkProviderSpaceLakes.GenType
import galaxyspace.systems.SolarSystem.moons.phobos.dimension.ChunkProviderPhobos
import asmodeuscore.core.astronomy.dimension.world.gen.MapGenCaves
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer


open class ChunkProviderPhobos constructor(par1World: World?, seed: Long, mapFeaturesEnabled: Boolean) :
    ChunkProviderSpaceLakes((par1World)!!, seed, mapFeaturesEnabled) {
    private val caveGenerator =
        MapGenCaves(GSBlocks.PHOBOS_BLOCKS.getStateFromMeta(0), GSBlocks.PHOBOS_BLOCKS.getStateFromMeta(1))

    override fun getWorldGenerators(): MutableList<MapGenBaseMeta>? {
        val generators: MutableList<MapGenBaseMeta?> = Lists.newArrayList<MapGenBaseMeta?>()
        generators.add(this.caveGenerator)
        return (generators as MutableList<MapGenBaseMeta>?)
    }

    override fun getBiomeGenerator(): BiomeDecoratorSpace {
        return BiomeDecoratorPhobos()
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
        return 15.0
    }

    override fun onChunkProvider(cX: Int, cZ: Int, primer: ChunkPrimer?) {
    }

    override fun onPopulate(cX: Int, cZ: Int) {
    }

    override fun getMountainHeightModifier(): Double {
        return 5.0
    }

    override fun getWaterLevel(): Int {
        return 70
    }

    override fun getSmallFeatureHeightModifier(): Double {
        return 10.0
    }

    override fun getValleyHeightModifier(): Double {
        return 1.0
    }

    override fun getGrassBlock(): IBlockState {
        return GSBlocks.PHOBOS_BLOCKS.getStateFromMeta(0)
    }

    override fun getDirtBlock(): IBlockState {
        return GSBlocks.PHOBOS_BLOCKS.getStateFromMeta(1)
    }

    override fun getStoneBlock(): IBlockState {
        return GSBlocks.PHOBOS_BLOCKS.getStateFromMeta(1)
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
        if (this.rand.nextInt(8) == 0 && (primer)!!.getBlockState(x, y - 1, z).getBlock() !== Blocks.AIR) {
            (primer)!!.setBlockState(x, y - 1, z, GSBlocks.SURFACE_ICE.getStateFromMeta(4))
        }
    }
}
