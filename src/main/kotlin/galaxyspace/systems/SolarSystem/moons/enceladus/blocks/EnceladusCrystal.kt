package galaxyspace.systems.SolarSystem.moons.enceladus.blocks

import foundation.block.LegacyBlockMetadata

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.moons.enceladus.tile.TileEntityBlockCrystallTE
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusCrystal
import net.minecraft.world.level.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World
import java.util.Random

open class EnceladusCrystal constructor() : Block(Material.GLASS), ITileEntityProvider, LegacyBlockMetadata {
    init {
        this.setTranslationKey("enceladus_crystal")
        this.setHardness(2.0f)
        this.setSoundType(SoundType.GLASS)
        this.setLightLevel(3.0f)
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityBlockCrystallTE()
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(this), 1, 0)
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val angle: Int = MathHelper.floor(placer.yRot * 4.0f / 360.0f + 0.5) and 3
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()
        worldIn.setBlock(pos, getStateFromMeta(change), 3)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        when (getMetaFromState(state)) {
            else -> return GSItems.BASIC
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        when (getMetaFromState(state)) {
            else -> return 8
        }
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.INVISIBLE
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getStateForPlacement(
        world: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase,
        hand: EnumHand
    ): IBlockState {
        return this.defaultBlockState().setValue(FACING, placer.getHorizontalFacing())
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        return this.defaultBlockState().setValue(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(FACING).getHorizontalIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }


    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
    }
}
