package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.WorldGenTree_Jungle
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenAbstractTree
import net.minecraft.world.storage.loot.LootTableList
import net.minecraftforge.common.IPlantable
import java.util.Random
import kotlin.math.abs

open class WorldGenTree_Jungle constructor(
    private val wood: IBlockState,
    private val leaves: IBlockState,
    doBlockNotify: Boolean,
    private val type: Int,
    private val typeTree: Int
) : WorldGenAbstractTree(doBlockNotify) {
    protected open fun GetValidSpawnBlocks(): Array<IBlockState?> {
        return ((arrayOf<IBlockState>(
            BRBlocks.BARNARDA_C_GRASS.getDefaultState(),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0)
        ) as Array<IBlockState?>?))!!
    }

    constructor(wood: IBlockState, leaves: IBlockState, type: Int, typetree: Int) : this(
        wood,
        leaves,
        false,
        type,
        typetree
    )

    override fun generate(world: World, rand: Random, position: BlockPos): Boolean {
        var flag: Boolean = true

        if (position.getY() >= 1 && position.getY() + 1 <= 256) {
            var b0: Byte
            var k1: Int
            var block: Block
            var blockdown: Block?

            for (i1: Int in position.getY()..position.getY() + 1) {
                b0 = 1

                if (i1 == position.getY()) {
                    b0 = 0
                }

                if (i1 >= position.getY() + 1 - 2) {
                    b0 = 2
                }

                var j1: Int = position.getX() - b0
                while (j1 <= position.getX() + b0 && flag) {
                    k1 = position.getZ() - b0
                    while (k1 <= position.getZ() + b0 && flag) {
                        if (i1 >= 0 && i1 < 256) {
                            block = world.getBlockState(BlockPos(j1, i1, k1)).getBlock()

                            if (!this.isReplaceable(world, BlockPos(j1, i1, k1))) {
                                flag = false
                            }
                        } else {
                            flag = false
                        }
                        ++k1
                    }
                    ++j1
                }
            }

            if (!flag) {
                return false
            } else {
                val block2: Block = world.getBlockState(position.down()).getBlock()
                val isSoil: Boolean = block2.canSustainPlant(
                    world.getBlockState(position.down()), world,
                    position.down(), EnumFacing.UP, BRBlocks.BARNARDA_C_DANDELIONS as IPlantable
                )

                if (isSoil && position.getY() < 256 - 1) {
                    block2.onPlantGrow(world.getBlockState(position.down()), world, position.down(), position)
                    b0 = 3
                    val b1: Byte = 0
                    var l1: Int
                    var i2: Int
                    var j2: Int
                    var i3: Int

                    k1 = position.getY() - b0
                    while (k1 <= position.getY()) {
                        i3 = k1 - (position.getY())
                        l1 = b1 + 1 - i3 / 2

                        i2 = position.getX() - l1
                        while (i2 <= position.getX() + l1) {
                            j2 = i2 - position.getX()

                            for (k2: Int in position.getZ() - l1..position.getZ() + l1) {
                                val l2: Int = k2 - position.getZ()

                                if (abs(j2) != l1 || abs(l2) != l1 || rand.nextInt(2) != 0 && i3 != 0) {
                                    val block1: Block = world.getBlockState(BlockPos(i2, k1, k2)).getBlock()

                                    if (block1.isAir(
                                            world.getBlockState(BlockPos(i2, k1, k2)), world,
                                            BlockPos(i2, k1, k2)
                                        )
                                        || block1.isLeaves(
                                            world.getBlockState(BlockPos(i2, k1, k2)), world,
                                            BlockPos(i2, k1, k2)
                                        )
                                    ) {
                                        // this.setBlockAndNotifyAdequately(par1World, i2, k1, k2, this.leaves,
                                        // metadata);
                                    }
                                }
                            }
                            ++i2
                        }
                        ++k1
                    }

                    k1 = 0
                    while (k1 < 3) {
                        block = world.getBlockState(position.up(k1)).getBlock()

                        if (block.isAir(world.getBlockState(position.up(k1)), world, position.up(k1)) || block.isLeaves(
                                world.getBlockState(position.up(k1)),
                                world,
                                position.up(k1)
                            )
                        ) {
                            // this.setBlockAndNotifyAdequately(par1World, par3, par4 + k1, par5,// this.wood);
                            roots(world, rand, position.getX(), position.getY(), position.getZ())
                        }
                        ++k1
                    }
                }
                return true
            }
        }
        return false
    }

    private fun growVines(world: World, x: Int, y: Int, z: Int, flag: Int) {
        var y: Int = y
        this.setBlockAndNotifyAdequately(world, BlockPos(x, y, z), Blocks.VINE.getStateFromMeta(flag))
        var i1: Int = 4

        while (true) {
            --y

            if (world.isAirBlock(BlockPos(x, y, z)) || i1 <= 0) {
                return
            }
            this.setBlockAndNotifyAdequately(world, BlockPos(x, y, z), Blocks.VINE.getStateFromMeta(flag))
            --i1
        }
    }

    open fun LocationIsValidSpawn(world: World, x: Int, y: Int, z: Int): Boolean {
        val checkBlock: IBlockState = world.getBlockState(BlockPos(x, y - 1, z))
        val blockAbove: IBlockState = world.getBlockState(BlockPos(x, y, z))
        val blockBelow: IBlockState = world.getBlockState(BlockPos(x, y - 2, z))

        for (i: IBlockState? in GetValidSpawnBlocks()) {
            if (blockAbove !== Blocks.AIR.getDefaultState()) {
                return false
            }
            if (checkBlock === i) {
                return true
            } else if (checkBlock === Blocks.SNOW_LAYER.getDefaultState() && blockBelow === i) {
                return true
            } else if (checkBlock.getMaterial() === Material.PLANTS && blockBelow === i) {
                return true
            }
        }
        return false
    }

    private fun setBlockAndNotifyAdequately(world: World, x: Int, y: Int, z: Int, state: IBlockState) {
        setBlockAndNotifyAdequately(world, BlockPos(x, y, z), state)
    }

    private fun roots(par1World: World, rand: Random, i: Int, j: Int, k: Int) {
        if (this.type == 1) {
            setBlockAndNotifyAdequately(par1World, i + 0, j + 0, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 0, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 1, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 1, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 1, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 2, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 2, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 3, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 0, k + 0, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 0, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 0, k + 7, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 1, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 1, k + 6, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 6, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 3, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 3, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 3, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 4, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 4, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 5, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 5, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 0, k + 0, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 0, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 0, k + 7, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 1, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 1, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 1, k + 6, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 3, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 3, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 4, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 4, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 5, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 5, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 2, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 2, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 0, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 1, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 1, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 0, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 0, k + 4, wood)
        }
        if (this.type == 2) {
            // setBlockAndNotifyAdequately(par1World, i + 2, j + 0, k + 3, Blocks.chest, 0);

            setBlockAndNotifyAdequately(par1World, i + 0, j + 0, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 0, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 0, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 1, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 1, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 1, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 2, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 3, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 4, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 0, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 1, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 2, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 2, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 2, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 3, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 3, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 4, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 4, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 0, k + 0, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 1, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 2, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 3, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 3, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 5, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 5, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 6, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 6, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 0, k + 0, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 0, k + 6, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 1, k + 0, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 1, k + 6, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 0, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 6, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 3, k + 0, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 3, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 3, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 3, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 4, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 4, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 5, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 5, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 6, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 6, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 0, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 1, k + 5, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 4, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 3, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 3, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 0, k + 0, wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 0, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 0, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 1, k + 1, wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 1, k + 2, wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 1, k + 3, wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 2, k + 2, wood)

            setBlockAndNotifyAdequately(par1World, i + 2, j - 1, k + 3, wood)
            this.setBlockAndNotifyAdequately(
                par1World, BlockPos(i + 2, j, k + 3),
                Blocks.MOB_SPAWNER.getDefaultState()
            )
            val spawner: TileEntityMobSpawner? = par1World
                .getTileEntity(BlockPos(i + 2, j, k + 3)) as TileEntityMobSpawner?

            if (spawner != null) {
                spawner.getSpawnerBaseLogic().setEntityId(getMob(rand))
            }

            this.setBlockAndNotifyAdequately(
                par1World, BlockPos(i + 2, j - 2, k + 3),
                Blocks.CHEST.getDefaultState()
            )
            val chest: TileEntityChest? = par1World.getTileEntity(BlockPos(i + 2, j - 2, k + 3)) as TileEntityChest?
            if (chest != null) {
                chest.setLootTable(LootTableList.CHESTS_JUNGLE_TEMPLE, rand.nextLong())
            }
        }
        trees(par1World, i, j, k)
    }

    private fun trees(par1World: World, i: Int, j: Int, k: Int) {
        var i: Int = i
        var j: Int = j
        var k: Int = k
        j += 6

        if (typeTree == 1) {
            k -= 1
            // TODO �����
            setBlockAndNotifyAdequately(par1World, i + 2, j + 4, k + 3, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 5, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 6, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 9, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 0, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 0, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 1, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 1, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 2, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 3, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 3, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 4, k + 3, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 4, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 5, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 6, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 7, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 8, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 8, k + 6, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 9, k + 6, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 9, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 9, k + 8, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 0, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 0, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 1, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 1, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 3, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 3, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 4, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 4, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 5, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 5, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 6, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 6, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 7, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 7, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 8, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 8, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 8, this.wood)
            // setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 9, this.wood);
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 3, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 12, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 13, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 14, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 15, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 16, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 17, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 12, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 12, k + 3, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 17, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 17, k + 6, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 11, k + 1, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 12, k + 1, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 18, k + 6, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 18, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 19, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 11, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 18, k + 8, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 11, k + 3, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 12, k + 3, this.wood)

            // TODO ������
            setBlockAndNotifyAdequately(par1World, i + 0, j + 5, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 4, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 5, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 5, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 5, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 6, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 9, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 10, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 10, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 4, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 5, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 5, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 6, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 6, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 7, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 8, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 8, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 9, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 9, k + 8, this.leaves)

            setBlockAndNotifyAdequately(par1World, i + 2, j + 10, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 10, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 10, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 10, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 11, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 11, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 11, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 16, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 16, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 16, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 4, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 5, k + 1, this.leaves)

            setBlockAndNotifyAdequately(par1World, i + 3, j + 5, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 5, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 6, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 6, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 6, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 7, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 8, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 8, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 8, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 9, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 9, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 10, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 10, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 10, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 10, k + 9, this.leaves)
            // setBlockAndNotifyAdequately(par1World, i + 3, j + 10, k + 10, this.leaves);
            setBlockAndNotifyAdequately(par1World, i + 3, j + 11, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 11, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 12, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 16, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 16, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 16, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 16, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 17, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 17, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 17, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 17, k + 6, this.leaves)

            setBlockAndNotifyAdequately(par1World, i + 4, j + 4, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 5, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 8, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 8, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 8, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 8, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 9, this.leaves)
            // setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 10, this.leaves);
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 7, this.leaves)
            // setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 10, this.leaves);
            // setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 11, this.leaves);
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 9, this.leaves)

            // setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 10, this.leaves);
            setBlockAndNotifyAdequately(par1World, i + 4, j + 12, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 12, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 12, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 12, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 15, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 16, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 16, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 16, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 17, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 17, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 17, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 17, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 18, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 18, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 9, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 9, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 10, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 10, k + 9, this.leaves)
            // setBlockAndNotifyAdequately(par1World, i + 5, j + 10, k + 10, this.leaves);
            setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 0, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 8, this.leaves)

            // setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 10, this.leaves);
            setBlockAndNotifyAdequately(par1World, i + 5, j + 12, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 13, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 13, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 15, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 17, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 17, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 17, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 18, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 18, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 18, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 18, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 19, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 10, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 11, k + 0, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 12, k + 0, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 12, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 12, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 13, k + 0, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 13, k + 1, this.leaves)

            setBlockAndNotifyAdequately(par1World, i + 6, j + 13, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 13, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 14, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 16, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 16, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 17, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 17, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 17, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 17, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 17, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 18, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 18, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 18, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 19, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 19, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 19, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 20, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 10, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 10, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 11, k + 0, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 11, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 11, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 12, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 12, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 12, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 13, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 13, k + 3, this.leaves)

            setBlockAndNotifyAdequately(par1World, i + 7, j + 16, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 17, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 17, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 18, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 18, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 18, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 19, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 19, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 10, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 11, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 11, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 11, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 12, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 12, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 13, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 17, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 17, k + 9, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 18, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 18, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 18, k + 8, this.leaves)
            // setBlockAndNotifyAdequately(par1World, i + 9, j + 11, k + 3, this.leaves);
            // setBlockAndNotifyAdequately(par1World, i + 9, j + 12, k + 3, this.leaves);
        }
        if (typeTree == 2) {
            k -= 1
            i -= 1
            setBlockAndNotifyAdequately(par1World, i + 0, j + 13, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 0, j + 14, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 11, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 11, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 12, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 12, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 13, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 13, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 14, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 14, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 14, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 1, j + 15, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 10, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 10, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 11, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 11, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 12, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 12, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 13, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 13, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 14, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 14, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 15, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 15, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 15, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 9, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 10, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 10, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 11, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 11, k + 3, this.leaves)
            // setBlockAndNotifyAdequately(par1World, i + 3, j + 11, k + 4, Blocks.chest,
            // 5);
            setBlockAndNotifyAdequately(par1World, i + 3, j + 11, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 11, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 12, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 12, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 12, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 12, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 12, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 13, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 13, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 13, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 13, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 13, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 14, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 14, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 14, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 14, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 15, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 15, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 15, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 16, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 16, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 16, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 12, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 12, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 13, k + 0, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 13, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 13, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 14, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 14, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 15, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 15, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 16, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 16, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 16, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 16, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 17, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 9, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 10, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 10, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 12, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 12, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 13, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 13, k + 8, this.leaves)
            // setBlockAndNotifyAdequately(par1World, i + 5, j + 13, k + 9, this.leaves);
            setBlockAndNotifyAdequately(par1World, i + 5, j + 14, k + 1, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 14, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 15, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 15, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 15, k + 8, this.leaves)
            // setBlockAndNotifyAdequately(par1World, i + 5, j + 15, k + 9, this.leaves);
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 16, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 17, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 9, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 10, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 10, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 10, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 11, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 11, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 11, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 11, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 12, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 12, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 12, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 12, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 13, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 13, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 13, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 13, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 14, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 14, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 14, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 14, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 14, k + 8, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 15, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 15, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 15, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 15, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 16, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 16, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 16, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 10, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 10, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 11, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 12, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 12, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 12, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 13, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 13, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 13, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 14, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 14, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 14, k + 6, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 15, k + 2, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 15, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 15, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 15, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 15, k + 7, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 11, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 11, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 12, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 12, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 12, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 13, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 13, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 13, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 14, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 14, k + 5, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 15, k + 3, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 15, k + 4, this.leaves)
            setBlockAndNotifyAdequately(par1World, i + 8, j + 15, k + 5, this.leaves)
            // setBlockAndNotifyAdequately(par1World, i + 9, j + 13, k + 4, this.leaves);
            // setBlockAndNotifyAdequately(par1World, i + 9, j + 13, k + 5, this.leaves);
            setBlockAndNotifyAdequately(par1World, i + 2, j + 11, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 11, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 12, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 12, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 13, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 13, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 14, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 2, j + 14, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 10, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 10, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 15, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 3, j + 15, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 0, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 0, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 1, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 1, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 2, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 3, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 3, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 4, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 4, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 5, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 5, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 6, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 6, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 7, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 7, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 8, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 8, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 9, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 3, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 10, k + 6, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 11, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 12, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 12, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 13, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 13, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 14, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 14, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 15, k + 3, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 15, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 15, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 4, j + 15, k + 6, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 0, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 0, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 1, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 1, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 2, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 2, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 3, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 3, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 4, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 4, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 5, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 5, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 6, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 6, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 7, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 7, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 8, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 8, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 9, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 9, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 10, k + 3, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 10, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 10, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 10, k + 6, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 11, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 12, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 12, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 13, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 13, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 14, k + 2, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 14, k + 7, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 15, k + 3, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 15, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 15, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 5, j + 15, k + 6, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 10, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 10, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 15, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 6, j + 15, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 11, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 11, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 12, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 12, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 13, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 13, k + 5, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 14, k + 4, this.wood)
            setBlockAndNotifyAdequately(par1World, i + 7, j + 14, k + 5, this.wood)
        }
    }

    companion object {
        @JvmStatic
        private fun getMob(rand: Random): ResourceLocation {
            when (rand.nextInt(4)) {
                0 -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_spider")
                1 -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_creeper")
                2 -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_skeleton")
                3 -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_zombie")
                else -> return ResourceLocation(Constants.MOD_ID_CORE, "evolved_zombie")
            }
        }
    }
}
