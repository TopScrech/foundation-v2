package galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.world.gen

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks.EnumBlockBarnardaC1
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Decorations.EnumBlockBarnardaC1Decoration
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Decorations
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.world.gen.WorldGenIcyDripstone
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.HashSet
import java.util.Random
import kotlin.math.abs
import kotlin.math.min

open class WorldGenIcyDripstone : WorldGenerator() {
    override fun generate(world: World, rand: Random, chunkPos: BlockPos): Boolean {
        val randPosX: Int = chunkPos.getX() + rand.nextInt(16) + 8
        val randPosZ: Int = chunkPos.getZ() + rand.nextInt(16) + 8

        for (y: Int in 100 downTo 46) {
            val pos: BlockPos = BlockPos(randPosX, y, randPosZ)

            if (world.canBlockSeeSky(pos)) continue

            if (isValidSurface(world, pos) && world.isAirBlock(pos.down())) {
                generateStalactite(world, rand, pos)
            }
        }

        for (y: Int in 20..59) {
            val pos: BlockPos = BlockPos(randPosX, y, randPosZ)

            if (isValidSurface(world, pos) && world.isAirBlock(pos.up())) {
                generateStalagmite(world, rand, pos)
            }
        }

        return true
    }

    private fun isValidSurface(world: World, pos: BlockPos): Boolean {
        return VALID_BLOCKS.contains(world.getBlockState(pos))
    }

    private fun calculateLength(world: World, pos: BlockPos, rand: Random, up: Boolean): Int {
        var length: Int = 0


        if (!up) while (world.isAirBlock(pos.down(length + 1))) {
            length++
        }
        else {
            while (world.isAirBlock(pos.up(length + 1))) {
                length++
            }
        }

        if (length < 0) length = 0

        return min(15, abs(length - rand.nextInt(length + 1)))
    }

    private fun generateStalactite(world: World, rand: Random, pos: BlockPos) {
        val length: Int = calculateLength(world, pos, rand, false)
        if (length < 3) return

        world.setBlockState(
            pos,
            BRBlocks.BARNARDA_C1_BLOCKS.getDefaultState().withProperty(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.ICY_SUBSURFACE
            ),
            2
        )
        world.setBlockState(
            pos.down(), BRBlocks.BARNARDA_C1_DECORATIONS.getDefaultState()
                .withProperty(
                    Barnarda_C1_Decorations.Companion.BASIC_TYPE,
                    EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_BASE
                )
                .withProperty(Barnarda_C1_Decorations.Companion.UP, true), 2
        )

        for (i: Int in 2..length) {
            if (i < length - 1) {
                world.setBlockState(
                    pos.down(i), BRBlocks.BARNARDA_C1_DECORATIONS.getDefaultState()
                        .withProperty(
                            Barnarda_C1_Decorations.Companion.BASIC_TYPE,
                            (EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_LARGE)
                        )
                        .withProperty(Barnarda_C1_Decorations.Companion.UP, true), 2
                )
            } else if (i == length - 1) {
                world.setBlockState(
                    pos.down(i), BRBlocks.BARNARDA_C1_DECORATIONS.getDefaultState()
                        .withProperty(
                            Barnarda_C1_Decorations.Companion.BASIC_TYPE,
                            EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_MEDIUM
                        )
                        .withProperty(Barnarda_C1_Decorations.Companion.UP, true), 2
                )
            } else {
                world.setBlockState(
                    pos.down(i), BRBlocks.BARNARDA_C1_DECORATIONS.getDefaultState()
                        .withProperty(
                            Barnarda_C1_Decorations.Companion.BASIC_TYPE,
                            (if (rand.nextBoolean()) EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_THIN else EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_SMALL)
                        )
                        .withProperty(Barnarda_C1_Decorations.Companion.UP, true), 2
                )
            }
        }
    }

    private fun generateStalagmite(world: World, rand: Random, pos: BlockPos) {
        val length: Int = calculateLength(world, pos, rand, true)
        if (length < 3) return

        world.setBlockState(
            pos,
            BRBlocks.BARNARDA_C1_BLOCKS.getDefaultState().withProperty(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.ICY_SUBSURFACE
            ),
            2
        )
        world.setBlockState(
            pos.up(), BRBlocks.BARNARDA_C1_DECORATIONS.getDefaultState()
                .withProperty(
                    Barnarda_C1_Decorations.Companion.BASIC_TYPE,
                    EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_BASE
                )
                .withProperty(Barnarda_C1_Decorations.Companion.UP, false), 2
        )


        for (i: Int in 2..length) {
            if (i < length - 1) {
                world.setBlockState(
                    pos.up(i), BRBlocks.BARNARDA_C1_DECORATIONS.getDefaultState()
                        .withProperty(
                            Barnarda_C1_Decorations.Companion.BASIC_TYPE,
                            EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_LARGE
                        )
                        .withProperty(Barnarda_C1_Decorations.Companion.UP, false), 2
                )
            } else if (i == length - 1) {
                world.setBlockState(
                    pos.up(i), BRBlocks.BARNARDA_C1_DECORATIONS.getDefaultState()
                        .withProperty(
                            Barnarda_C1_Decorations.Companion.BASIC_TYPE,
                            EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_MEDIUM
                        )
                        .withProperty(Barnarda_C1_Decorations.Companion.UP, false), 2
                )
            } else {
                world.setBlockState(
                    pos.up(i), BRBlocks.BARNARDA_C1_DECORATIONS.getDefaultState()
                        .withProperty(
                            Barnarda_C1_Decorations.Companion.BASIC_TYPE,
                            (if (rand.nextBoolean()) EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_THIN else EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_SMALL)
                        )
                        .withProperty(Barnarda_C1_Decorations.Companion.UP, false), 2
                )
            }
        }
    }

    companion object {
        private val VALID_BLOCKS: MutableSet<IBlockState?> = HashSet<IBlockState?>()

        init {
            VALID_BLOCKS.add(
                BRBlocks.BARNARDA_C1_BLOCKS.getDefaultState()
                    .withProperty(
                        Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                        EnumBlockBarnardaC1.SNOWY_SUBSURFACE
                    )
            )
            VALID_BLOCKS.add(
                BRBlocks.BARNARDA_C1_BLOCKS.getDefaultState()
                    .withProperty(
                        Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                        EnumBlockBarnardaC1.ICY_SUBSURFACE
                    )
            )
            VALID_BLOCKS.add(
                BRBlocks.BARNARDA_C1_BLOCKS.getDefaultState()
                    .withProperty(
                        Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                        EnumBlockBarnardaC1.STONE
                    )
            )
        }
    }
}
