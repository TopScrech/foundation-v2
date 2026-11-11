package galaxyspace.systems.SolarSystem.moons.europa.world.gen

import foundation.block.getMetaFromState

import galaxyspace.core.GSBlocks
import galaxyspace.core.configs.GSConfigWorld
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.BiomeDecoratorEuropa
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator

open class BiomeDecoratorEuropa constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null

    private val OreGenIce: WorldGenerator
    private val OreGenBrownIce: WorldGenerator

    private val OreGenEmerald: WorldGenerator
    private val OreGenSilicon: WorldGenerator
    private val OreGenAluminum: WorldGenerator

    init {
        //this.nickelGen = new WorldGenMinableMeta(GSBlocks.MERCURY_BLOCKS, 4, 3, true, GSBlocks.MERCURY_BLOCKS, 2);

        this.OreGenIce = WorldGenMinableMeta(Blocks.PACKED_ICE, 20, 0, true, Blocks.WATER, 0)

        this.OreGenBrownIce = WorldGenMinableMeta(GSBlocks.EUROPA_BLOCKS, 40, 2, true, Blocks.PACKED_ICE, 0)
        this.OreGenEmerald = WorldGenMinableMeta(GSBlocks.EUROPA_BLOCKS, 4, 3, true, GSBlocks.EUROPA_BLOCKS, 1)
        this.OreGenSilicon = WorldGenMinableMeta(GSBlocks.EUROPA_BLOCKS, 4, 4, true, GSBlocks.EUROPA_BLOCKS, 1)
        this.OreGenAluminum = WorldGenMinableMeta(GSBlocks.EUROPA_BLOCKS, 6, 5, true, GSBlocks.EUROPA_BLOCKS, 1)
    }

    override fun decorate() {
        if (GSConfigWorld.enableOresGeneration) {
            this.generateOre(2, this.OreGenIce, 10, 60)
            this.generateOre(16, this.OreGenBrownIce, 40, 75)

            this.generateOre(2, this.OreGenEmerald, 0, 30)
            this.generateOre(4, this.OreGenSilicon, 0, 30)
            this.generateOre(8, this.OreGenAluminum, 0, 30)
        }

        for (i: Int in 0..39) {
            val randPosX: Int = posX + (rand)!!.nextInt(16) + 8
            val randPosY: Int = (rand)!!.nextInt(70)
            val randPosZ: Int = posZ + (rand)!!.nextInt(16) + 8

            val pos: BlockPos = BlockPos(randPosX, randPosY + 1, randPosZ)
            val pos1: BlockPos = BlockPos(randPosX, randPosY, randPosZ)
            val pos2: BlockPos = BlockPos(randPosX, randPosY - 1, randPosZ)

            if (this.getCurrentWorld()!!.isEmptyBlock(pos) || this.getCurrentWorld()!!.getBlockState(pos)
                    .getBlock() === Blocks.WATER
            ) {
                val state: IBlockState = this.getCurrentWorld()!!.getBlockState(pos1)
                if (this.getCurrentWorld()!!.getBlockState(pos1).getBlock() === Blocks.PACKED_ICE) {
                    this.getCurrentWorld()!!.setBlockAndUpdate(pos1, GSBlocks.EUROPA_UWGEYSER.defaultBlockState())
                }
            }
        }

        for (i: Int in 0..39) {
            val randPosX: Int = posX + (rand)!!.nextInt(16) + 8
            val randPosY: Int = (rand)!!.nextInt(100)
            val randPosZ: Int = posZ + (rand)!!.nextInt(16) + 8

            val pos: BlockPos = BlockPos(randPosX, randPosY + 1, randPosZ)
            val pos1: BlockPos = BlockPos(randPosX, randPosY - 1, randPosZ)
            val pos2: BlockPos = BlockPos(randPosX, randPosY, randPosZ)

            if (this.getCurrentWorld()!!.isEmptyBlock(pos)) {
                if (this.getCurrentWorld()!!.getBlockState(pos1).getBlock() === Blocks.PACKED_ICE) {
                    this.getCurrentWorld()!!.setBlockAndUpdate(pos1, GSBlocks.EUROPA_UWGEYSER.defaultBlockState())
                }

                val state: IBlockState = this.getCurrentWorld()!!.getBlockState(pos1)

                if (state.getBlock() === GSBlocks.EUROPA_BLOCKS &&
                    this.getCurrentWorld()!!.getBlockState(pos1).getBlock().getMetaFromState(state) == 0
                ) {
                    this.getCurrentWorld()!!.setBlockAndUpdate(pos2, GSBlocks.EUROPA_GEYSER.defaultBlockState())
                }
            }
        }
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
