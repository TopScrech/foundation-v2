package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Falling_Blocks.EnumFallingBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_Dunes
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Falling_Blocks
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntityHusk
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class Barnarda_C_Dunes constructor(min: Double, max: Double) :
    WE_Biome(BiomeProperties("barnarda_c_dunes"), intArrayOf(grasscolor, 0x11FF66, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.8
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 72
        biomeInterpolateQuality = 15
        biomeTemerature = 0.8
        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        this.spawnableMonsterList.add(SpawnListEntry(EntityHusk::class.java, 10, 1, 3))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySpider::class.java, 10, 1, 1))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySkeleton::class.java, 10, 1, 2))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEnderman::class.java, 10, 1, 1))
        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        //standardBiomeLayers.add(Blocks.SANDSTONE, (byte)0, BRBlocks.BARNARDA_C_BLOCKS, (byte)1, -256, 0,   -4, -1,  true);
        standardBiomeLayers.add(
            BRBlocks.BARNARDA_C_FALLING_BLOCKS.getStateFromMeta(0),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -256,
            0,
            false
        )
        standardBiomeLayers.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        val randPosX: Int = x + (rand)!!.nextInt(16) + 8
        val randPosZ: Int = z + (rand)!!.nextInt(16) + 8
        val pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

        var cangen: Boolean = true
        val range: Int = 2
        for (pos1: BlockPos in BlockPos.getAllInBox(
            pos.add(-range, -range, -range),
            pos.add(range, range, range)
        )) if ((world)!!.getBlockState(pos1) === BRBlocks.BARNARDA_C_CACTUS.getDefaultState()) cangen = false

        for (pos1: BlockPos in BlockPos.getAllInBox(
            pos.add(-range, 0, -range),
            pos.add(range, 0, range)
        )) if ((world)!!.getBlockState(pos1) !== Blocks.AIR.getDefaultState()) cangen = false

        if (cangen && (world)!!.getBlockState(pos.down()) === BRBlocks.BARNARDA_C_FALLING_BLOCKS.getDefaultState()
                .withProperty(
                    Barnarda_C_Falling_Blocks.BASIC_TYPE,
                    EnumFallingBlockBarnardaC.SAND
                )
        ) {
            if ((rand)!!.nextInt(2) == 0) {
                for (size: Int in 0..<1 + (rand)!!.nextInt(3)) {
                    (world)!!.setBlockState(pos.up(size), BRBlocks.BARNARDA_C_CACTUS.getDefaultState())
                }
            } else {
                (world)!!.setBlockState(
                    pos,
                    BRBlocks.BARNARDA_C_DANDELIONS.getDefaultState()
                        .withProperty(
                            Barnarda_C_Dandelions.BASIC_TYPE,
                            Barnarda_C_Dandelions.EnumBlockDandelions.DESERT_DOWN
                        )
                )
                if ((rand)!!.nextInt(3) == 0) (world)!!.setBlockState(
                    pos.up(),
                    BRBlocks.BARNARDA_C_DANDELIONS.getDefaultState()
                        .withProperty(
                            Barnarda_C_Dandelions.BASIC_TYPE,
                            Barnarda_C_Dandelions.EnumBlockDandelions.DESERT_UP
                        )
                )
            }
        }
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x89AC76 else 0x822899
    }
}
