package galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks

import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks.EnumBlockProximaB
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Dandelions
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Grass
import galaxyspace.core.util.GSCreativeTabs
import net.minecraft.block.Block
import net.minecraft.block.IGrowable
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class Proxima_B_Grass constructor() : Block(Material.GRASS), IGrowable {
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

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockGrass in EnumBlockGrass.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        //Block block = worldIn.getBlockState(pos.up()).getBlock();
        return state //state.withProperty(SNOWY, Boolean.valueOf(block == Blocks.SNOW || block == Blocks.SNOW_LAYER));
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isRemote) {
            if (!worldIn.isAreaLoaded(
                    pos,
                    3
                )
            ) return  // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading

            if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up())
                    .getLightOpacity(worldIn, pos.up()) > 2
            ) {
                worldIn.setBlockState(
                    pos,
                    ACBlocks.PROXIMA_B_BLOCKS.getDefaultState().withProperty(
                        Proxima_B_Blocks.Companion.BASIC_TYPE,
                        EnumBlockProximaB.MUD
                    )
                )
            } else {
                if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
                    for (i: Int in 0..3) {
                        val blockpos: BlockPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1)

                        if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos)) {
                            return
                        }

                        val iblockstate: IBlockState = worldIn.getBlockState(blockpos.up())
                        val iblockstate1: IBlockState = worldIn.getBlockState(blockpos)
                        /*
                        if (iblockstate1.getBlock() ==  BRBlocks.BARNARDA_C_GRASS && iblockstate1.getValue(Barnarda_C_Blocks.BASIC_TYPE) == Barnarda_C_Blocks.EnumBlockBarnardaC.DIRT && worldIn.getLightFromNeighbors(blockpos.up()) >= 4 && iblockstate.getLightOpacity(worldIn, pos.up()) <= 2)
                        {
                            worldIn.setBlockState(blockpos, BRBlocks.BARNARDA_C_GRASS.getDefaultState());
                        }*/
                    }
                }
            }
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return ACBlocks.PROXIMA_B_BLOCKS.getItemDropped(
            ACBlocks.PROXIMA_B_BLOCKS.getDefaultState().withProperty(
                Proxima_B_Blocks.Companion.BASIC_TYPE,
                EnumBlockProximaB.MUD
            ), rand, fortune
        )
    }

    @SideOnly(Side.CLIENT)
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
        val blockpos: BlockPos = pos.up()

        for (i: Int in 0..127) {
            var blockpos1: BlockPos = blockpos
            var j: Int = 0

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isAirBlock(blockpos1)) {
                        /*if (rand.nextInt(8) == 0)
                        {
                            worldIn.getBiome(blockpos1).plantFlower(worldIn, rand, blockpos1);
                        }
                        else*/
                        run {
                            val iblockstate1: IBlockState = ACBlocks.PROXIMA_B_DANDELIONS.getDefaultState()
                                .withProperty(
                                    Proxima_B_Dandelions.Companion.BASIC_TYPE,
                                    Proxima_B_Dandelions.EnumBlockDandelions.GRASS_2
                                )
                            if ((ACBlocks.PROXIMA_B_DANDELIONS as Proxima_B_Dandelions).canPlaceBlockAt(
                                    worldIn,
                                    blockpos1
                                )
                            ) {
                                worldIn.setBlockState(blockpos1, iblockstate1, 3)
                            }
                        }
                    }

                    break
                }

                blockpos1 =
                    blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1)

                if (worldIn.getBlockState(blockpos1.down())
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

        override fun getName(): String {
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
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumBlockGrass.Companion.byMetadata(meta))!!)
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
