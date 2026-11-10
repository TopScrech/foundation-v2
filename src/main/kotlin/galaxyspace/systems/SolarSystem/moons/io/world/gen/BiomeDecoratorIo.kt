package galaxyspace.systems.SolarSystem.moons.io.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks.EnumIoBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks
import galaxyspace.systems.SolarSystem.moons.io.world.gen.BiomeDecoratorIo
import galaxyspace.core.configs.GSConfigWorld
import asmodeuscore.core.astronomy.dimension.world.gen.WorldGenLakes
import galaxyspace.systems.SolarSystem.moons.io.world.gen.feature.WorldGenVaporPool
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.fluids.FluidRegistry

open class BiomeDecoratorIo constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null
    private val OreGenCopper: WorldGenerator
    private val OreGenSulfur: WorldGenerator

    private val lavalakesGen: WorldGenerator
    private val sulfurlakesGen: WorldGenerator

    private val ioLavaLakesPerChunk = 5
    private val ioSulfurLakesPerChunk = 10


    init {
        this.lavalakesGen = WorldGenLakes(
            Blocks.FLOWING_LAVA,
            0,
            GSBlocks.IO_BLOCKS,
            0
        ).addAdditions(GSBlocks.IO_BLOCKS.getStateFromMeta(5), 85)
        this.sulfurlakesGen =
            WorldGenLakes(FluidRegistry.getFluid("sulphuricacid").getBlock(), 0, GSBlocks.IO_BLOCKS, 0)

        this.OreGenCopper = WorldGenMinableMeta(GSBlocks.IO_BLOCKS, 4, 3, true, GSBlocks.IO_BLOCKS, 1)
        this.OreGenSulfur = WorldGenMinableMeta(GSBlocks.IO_BLOCKS, 3, 4, true, GSBlocks.IO_BLOCKS, 1)
    }

    override fun decorate() {
        if (GSConfigWorld.enableOresGeneration) {
            this.generateOre(24, this.OreGenCopper, 10, 60)
            this.generateOre(12, this.OreGenSulfur, 20, 60)
        }


        val x: Int = posX + (rand)!!.nextInt(16) + 8
        val z: Int = posZ + (rand)!!.nextInt(16) + 8


        if ((rand)!!.nextInt(this.ioLavaLakesPerChunk) == 0) {
            val y: Int = this.getCurrentWorld()!!.getTopSolidOrLiquidBlock(BlockPos(x, 0, z)).getY()
            val state: IBlockState = this.getCurrentWorld()!!.getBlockState(BlockPos(x, y, z).down())

            if (y <= 90 && state === GSBlocks.IO_BLOCKS.getStateFromMeta(0)) this.lavalakesGen.generate(
                this.getCurrentWorld(),
                this.rand,
                BlockPos(x, y, z)
            )
        }

        if ((rand)!!.nextInt(this.ioSulfurLakesPerChunk) == 0) {
            val y: Int = this.getCurrentWorld()!!.getTopSolidOrLiquidBlock(BlockPos(x, 0, z)).getY()
            val state: IBlockState = this.getCurrentWorld()!!.getBlockState(BlockPos(x, y, z).down())

            if (y <= 90 && state === GSBlocks.IO_BLOCKS.getStateFromMeta(0)) this.sulfurlakesGen.generate(
                this.getCurrentWorld(),
                this.rand,
                BlockPos(x, y, z)
            )
        }

        if ((rand)!!.nextInt(10) == 0) {
            val y: Int = this.getCurrentWorld()!!.getTopSolidOrLiquidBlock(BlockPos(x, 0, z)).getY()

            val state: IBlockState = this.getCurrentWorld()!!.getBlockState(BlockPos(x, y, z).down())

            if (state === GSBlocks.IO_BLOCKS.getStateFromMeta(2)) {
                this.getCurrentWorld()!!.setBlockState(BlockPos(x, y - 1, z), Blocks.FLOWING_LAVA.getDefaultState(), 2)
                if ((rand)!!.nextInt(2) == 0) this.getCurrentWorld()!!.setBlockState(
                    BlockPos(x, y, z),
                    GSBlocks.IO_BLOCKS.getDefaultState()
                        .withProperty(IoBlocks.BASIC_TYPE, EnumIoBlocks.IO_LAVA_GEYSER),
                    3
                )
                this.getCurrentWorld()!!
                    .setBlockState(BlockPos(x, y + 1 + (rand)!!.nextInt(1), z), Blocks.FLOWING_LAVA.getDefaultState(), 2)
            }

            if (state.getMaterial() === Material.LAVA) {
                this.getCurrentWorld()!!.setBlockState(
                    BlockPos(x, y - 1, z),
                    GSBlocks.IO_BLOCKS.getDefaultState()
                        .withProperty(IoBlocks.BASIC_TYPE, EnumIoBlocks.IO_LAVA_GEYSER),
                    3
                )
                this.getCurrentWorld()!!.setBlockState(BlockPos(x, y - 2, z), Blocks.LAVA.getDefaultState(), 2)
            }
        }

        if ((this.rand)!!.nextInt(150) == 0) {
            val l2: Int =
                this.getCurrentWorld()!!.getTopSolidOrLiquidBlock(BlockPos(x, 0, z)).getY() - 10 - (this.rand)!!.nextInt(5)
            (WorldGenVaporPool()).generate((this.getCurrentWorld())!!, (this.rand)!!, BlockPos(x, l2, z))
        }
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
