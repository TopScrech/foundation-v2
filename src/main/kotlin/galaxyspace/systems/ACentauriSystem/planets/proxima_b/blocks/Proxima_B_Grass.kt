package galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import foundation.level.offset

import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks.EnumBlockProximaB
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Dandelions
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Grass
import galaxyspace.core.util.GSCreativeTabs
import net.minecraft.world.level.block.Block
import net.minecraft.block.IGrowable
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class Proxima_B_Grass constructor() : Block(Material.GRASS), IGrowable, LegacyBlockMetadata {
    init {
        this.setTranslationKey("proxima_b_grasses")
        this.setHardness(0.8f)
        this.setSoundType(SoundType.GROUND)
        this.setHarvestLevel("shovel", 0)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSBlocksTab
    }


    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockGrass in EnumBlockGrass.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        //Block block = worldIn.getBlockState(pos.up()).getBlock();
        return state //state.setValue(SNOWY, Boolean.valueOf(block == Blocks.SNOW || block == Blocks.SNOW_LAYER));
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isClientSide) {
            if (!worldIn.isAreaLoaded(
                    pos,
                    3
                )
            ) return  // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading

            if (worldIn.getLightFromNeighbors(pos.above()) < 4 && worldIn.getBlockState(pos.above())
                    .getLightOpacity(worldIn, pos.above()) > 2
            ) {
                worldIn.setBlockAndUpdate(
                    pos,
                    ACBlocks.PROXIMA_B_BLOCKS.defaultBlockState().setValue(
                        Proxima_B_Blocks.Companion.BASIC_TYPE,
                        EnumBlockProximaB.MUD
                    )
                )
            } else {
                if (worldIn.getLightFromNeighbors(pos.above()) >= 9) {
                    for (i: Int in 0..3) {
                        val blockpos: BlockPos = pos.offset(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1)

                        if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos)) {
                            return
                        }

                        val iblockstate: IBlockState = worldIn.getBlockState(blockpos.above())
                        val iblockstate1: IBlockState = worldIn.getBlockState(blockpos)
                        /*
                        if (iblockstate1.getBlock() ==  BRBlocks.BARNARDA_C_GRASS && iblockstate1.getValue(Barnarda_C_Blocks.BASIC_TYPE) == Barnarda_C_Blocks.EnumBlockBarnardaC.DIRT && worldIn.getLightFromNeighbors(blockpos.up()) >= 4 && iblockstate.getLightOpacity(worldIn, pos.up()) <= 2)
                        {
                            worldIn.setBlockAndUpdate(blockpos, BRBlocks.BARNARDA_C_GRASS.defaultBlockState());
                        }*/
                    }
                }
            }
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return ACBlocks.PROXIMA_B_BLOCKS.getItemDropped(
            ACBlocks.PROXIMA_B_BLOCKS.defaultBlockState().setValue(
                Proxima_B_Blocks.Companion.BASIC_TYPE,
                EnumBlockProximaB.MUD
            ), rand, fortune
        )
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        return true
    }

    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean): Boolean {
        return true
    }

    override fun canUseBonemeal(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState): Boolean {
        return true
    }

    override fun grow(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState) {
        val blockpos: BlockPos = pos.above()

        for (i: Int in 0..127) {
            var blockpos1: BlockPos = blockpos
            var j: Int = 0

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isEmptyBlock(blockpos1)) {
                        /*if (rand.nextInt(8) == 0)
                        {
                            worldIn.getBiome(blockpos1).plantFlower(worldIn, rand, blockpos1);
                        }
                        else*/
                        run {
                            val iblockstate1: IBlockState = ACBlocks.PROXIMA_B_DANDELIONS.defaultBlockState()
                                .setValue(
                                    Proxima_B_Dandelions.Companion.BASIC_TYPE,
                                    Proxima_B_Dandelions.EnumBlockDandelions.GRASS_2
                                )
                            if ((ACBlocks.PROXIMA_B_DANDELIONS as Proxima_B_Dandelions).canPlaceBlockAt(
                                    worldIn,
                                    blockpos1
                                )
                            ) {
                                worldIn.setBlock(blockpos1, iblockstate1, 3)
                            }
                        }
                    }

                    break
                }

                blockpos1 =
                    blockpos1.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1)

                if (worldIn.getBlockState(blockpos1.below())
                        .getBlock() !== ACBlocks.PROXIMA_B_GRASS || worldIn.getBlockState(blockpos1).isNormalCube()
                ) {
                    break
                }

                ++j
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockGrass(private val meta: Int, private val serializedName: String) : IStringSerializable {
        GRASS(0, "proxima_b_grass");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockGrass? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumBlockGrass.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockGrass).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockGrass> =
            PropertyEnum.create<EnumBlockGrass>("type", EnumBlockGrass::class.java)
    }
}
