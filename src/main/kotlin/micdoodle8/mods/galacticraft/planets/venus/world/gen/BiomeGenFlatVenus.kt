package micdoodle8.mods.galacticraft.planets.venus.world.gen

import java.util.Random

import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraftforge.common.BiomeDictionary
import java.util.*

open class BiomeGenFlatVenus constructor(properties: BiomeProperties?) : BiomeVenus(properties) {
    override fun registerTypes(b: Biome?) {
        BiomeDictionary.addTypes(
            b,
            BiomeDictionary.Type.HOT,
            BiomeDictionary.Type.DRY,
            BiomeDictionary.Type.DEAD,
            BiomeDictionary.Type.SANDY
        )
    }

    override fun genTerrainBlocks(
        worldIn: World,
        rand: Random,
        chunkPrimerIn: ChunkPrimer,
        p_180622_4_: Int,
        p_180622_5_: Int,
        p_180622_6_: Double
    ) {
        this.topBlock = (VenusBlocks.venusBlock)!!.defaultBlockState()
            .setValue(
                BlockBasicVenus.BASIC_TYPE_VENUS,
                BlockBasicVenus.EnumBlockBasicVenus.ROCK_HARD
            )
        this.fillerBlock = (VenusBlocks.venusBlock)!!.defaultBlockState()
            .setValue(
                BlockBasicVenus.BASIC_TYPE_VENUS,
                BlockBasicVenus.EnumBlockBasicVenus.ROCK_SOFT
            )
        super.generateBiomeTerrainVenus(worldIn, rand, chunkPrimerIn, p_180622_4_, p_180622_5_, p_180622_6_)
    }
}
