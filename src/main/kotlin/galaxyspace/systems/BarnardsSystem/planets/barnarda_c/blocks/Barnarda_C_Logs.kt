package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.core.BRItems
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Logs
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.prefab.items.ItemAxeGS
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class Barnarda_C_Logs constructor(name: String, harvestlevel: Int) : Block(Material.WOOD), ISortableBlock {
    init {
        setTranslationKey(name)
        setHarvestLevel("axe", harvestlevel)
        setRegistryName(name)
        setCreativeTab(GSCreativeTabs.GSBlocksTab)
        setHardness(2.0f)
        setSoundType(SoundType.WOOD)
        setDefaultState(this.getStateFromMeta(0))
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (!this.getTranslationKey().contains("barnarda_c_test_glow_log")) {
            list.add(ItemStack(this, 1, 0))
        }
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        if (this.getTranslationKey().contains("barnarda_c_test_glow_log")) return 5

        return 0
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        if (!this.getTranslationKey()
                .contains("barnarda_c_test_glow_log")
        ) return Item.getItemFromBlock(BRBlocks.BARNARDA_C_VIOLET_LOG.getDefaultState().getBlock())

        return super.getItemDropped(state, rand, fortune)
    }

    override fun onBlockActivated(
        world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer,
        hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float
    ): Boolean {
        if (!world.isRemote && world.getBlockState(pos) === BRBlocks.BARNARDA_C_VIOLET_GLOW_LOG.getStateFromMeta(0) && (player.getHeldItem(
                hand
            ).getItem() is ItemAxe || player.getHeldItem(hand).getItem() is ItemAxeGS)
        ) {
            world.setBlockState(pos, BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0))
            world.spawnEntity(EntityItem(world, player.posX, player.posY, player.posZ, ItemStack(BRItems.BASIC, 1, 2)))
            return false
        }
        return false
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val i: Int = 4
        val j: Int = 5

        if (worldIn.isAreaLoaded(pos.add(-5, -5, -5), pos.add(5, 5, 5))) {
            for (blockpos: BlockPos in BlockPos.getAllInBox(pos.add(-4, -4, -4), pos.add(4, 4, 4))) {
                val iblockstate: IBlockState = worldIn.getBlockState(blockpos)

                if (iblockstate.getBlock().isLeaves(iblockstate, worldIn, blockpos)) {
                    iblockstate.getBlock().beginLeavesDecay(iblockstate, worldIn, blockpos)
                }
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var `enumfacing$axis`: EnumAxis = EnumAxis.Y
        val i: Int = meta and 12

        if (i == 4) {
            `enumfacing$axis` = EnumAxis.X
        } else if (i == 8) {
            `enumfacing$axis` = EnumAxis.Z
        } else if (i == 12) {
            `enumfacing$axis` = EnumAxis.NONE
        }

        return this.getDefaultState().withProperty(LOG_AXIS, `enumfacing$axis`)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var i: Int = 0
        val `enumfacing$axis`: EnumAxis = state.getValue(LOG_AXIS) as EnumAxis

        if (`enumfacing$axis` == EnumAxis.X) {
            i = i or 4
        } else if (`enumfacing$axis` == EnumAxis.Z) {
            i = i or 8
        } else if (`enumfacing$axis` == EnumAxis.NONE) {
            i = i or 12
        }

        return i
    }

    override fun getStateForPlacement(
        worldIn: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase
    ): IBlockState {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
            .withProperty(LOG_AXIS, EnumAxis.Companion.fromFacingAxis(facing.getAxis()))
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        when (rot) {
            Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90 -> when (state.getValue(LOG_AXIS) as EnumAxis) {
                EnumAxis.X -> return state.withProperty(LOG_AXIS, EnumAxis.X)
                EnumAxis.Z -> return state.withProperty(LOG_AXIS, EnumAxis.Z)
                else -> return state
            }

            else -> return state
        }
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(LOG_AXIS))
    }

    override fun canSustainLeaves(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean {
        return true
    }

    override fun isWood(world: IBlockAccess, pos: BlockPos): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    enum class EnumAxis(private val serializedName: String) : IStringSerializable {
        X("x"),
        Y("y"),
        Z("z"),
        NONE("none");

        override fun toString(): String {
            return this.serializedName
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun fromFacingAxis(axis: EnumFacing.Axis): EnumAxis {
                when (axis) {
                    EnumFacing.Axis.X -> return X
                    EnumFacing.Axis.Y -> return Y
                    EnumFacing.Axis.Z -> return Z
                    else -> return NONE
                }
            }
        }
    }

    companion object {
        @JvmField
        val LOG_AXIS: PropertyEnum<EnumAxis> = PropertyEnum.create<EnumAxis>("axis", EnumAxis::class.java)
    }
}
