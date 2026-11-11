package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityEmergencyBox
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.BooleanProperty as PropertyBool
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class BlockEmergencyBox constructor(assetName: String?) : BlockAdvancedTile(Material.IRON), IShiftDescription,
    ISortableBlock, IPartialSealableBlock, LegacyBlockMetadata {
    init {
        this.setHardness(4.0f)
        this.setResistance(70f)
        this.setSoundType(SoundType.METAL)
        this.setLightLevel(1.0f)
        this.setTranslationKey(assetName)
        this.setDefaultState(this.blockState.getBaseState().setValue(KIT, false))
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, KIT)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(KIT, meta % 2 == 1)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (if (state.getValue(KIT)) 1 else 0)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, 0))
        list.add(ItemStack(this, 1, 1))
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(this, 1, this.getMetaFromState(state))
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return if (face == EnumFacing.UP || face == EnumFacing.DOWN) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityEmergencyBox()
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun onMachineActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        player: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val tile: TileEntity? = (world)!!.getBlockEntity(pos)
        if (tile is TileEntityEmergencyBox) {
            if (!(world)!!.isClientSide) {
                tile.click((player)!!, (side)!!, (state)!!.getValue(KIT))
            }
            return true
        }
        return false
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun isSealed(world: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return (direction)!!.ordinal < 2
    }

    override fun isSideSolid(state: IBlockState, world: IBlockAccess, pos: BlockPos, direction: EnumFacing): Boolean {
        return direction.ordinal < 2
    }

    companion object {
        @JvmField
        val KIT: PropertyBool = PropertyBool.create("kit")
    }
}
