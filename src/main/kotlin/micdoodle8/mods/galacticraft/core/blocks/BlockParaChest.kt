package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityParaChest
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.passive.EntityOcelot
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.Container as IInventory
import net.minecraft.world.item.DyeColor as EnumDyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import java.util.Random

open class BlockParaChest constructor(assetName: String?) : BlockContainer(Material.WOOD), ITileEntityProvider,
    IShiftDescription, ISortableBlock, LegacyBlockMetadata {
    init {
        this.setHardness(3.0f)
        this.setSoundType(SoundType.WOOD)
        //        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        this.setTranslationKey(assetName)
        this.setDefaultState(
            this.blockState.getBaseState().setValue(FACING, EnumFacing.NORTH)
        )
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return NOT_CONNECTED_AABB
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
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

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
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
        return BlockFaceShape.UNDEFINED
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (worldIn.isClientSide) {
            return true
        } else {
            val iinventory: IInventory? = this.getInventory(worldIn, pos)

            if (iinventory != null && playerIn is EntityPlayerMP) {
                playerIn.openGui(GalacticraftCore.instance, -1, worldIn, pos.getX(), pos.getY(), pos.getZ())
            }

            return true
        }
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        val tileentitychest: TileEntityParaChest? = worldIn.getBlockEntity(pos) as TileEntityParaChest?

        if (tileentitychest != null) {
            tileentitychest.updateContainingBlockInfo()
        }
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileentitychest: TileEntityParaChest? = worldIn.getBlockEntity(pos) as TileEntityParaChest?

        if (tileentitychest != null) {
            val syncRandom: Random = GCCoreUtil.getRandom(pos)
            for (j1: Int in 0..<tileentitychest.getContainerSize()) {
                val itemstack: ItemStack = tileentitychest.getItem(j1)

                if (itemstack != null) {
                    val f: Float = syncRandom.nextFloat() * 0.8f + 0.1f
                    val f1: Float = syncRandom.nextFloat() * 0.8f + 0.1f
                    var entityitem: EntityItem

                    val f2: Float = syncRandom.nextFloat() * 0.8f + 0.1f
                    while (!itemstack.isEmpty()) {
                        entityitem = EntityItem(
                            worldIn,
                            (pos.getX() + f).toDouble(),
                            (pos.getY() + f1).toDouble(),
                            (pos.getZ() + f2).toDouble(),
                            itemstack.splitStack(syncRandom.nextInt(21) + 10)
                        )
                        val f3: Float = 0.05f
                        entityitem.motionX = (syncRandom.nextGaussian().toFloat() * f3).toDouble()
                        entityitem.motionY = (syncRandom.nextGaussian().toFloat() * f3 + 0.2f).toDouble()
                        entityitem.motionZ = (syncRandom.nextGaussian().toFloat() * f3).toDouble()
                        worldIn.addFreshEntity(entityitem)
                    }
                }
            }

            worldIn.updateComparatorOutputLevel(pos, null)
        }

        super.breakBlock(worldIn, pos, state)
    }

    open fun getInventory(par1World: World, pos: BlockPos): IInventory? {
        val `object`: Any? = par1World.getBlockEntity(pos)

        if (`object` == null) {
            return null
        } else if (par1World.isSideSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN)) {
            return null
        } else if (isOcelotBlockingChest(par1World, pos)) {
            return null
        } else {
            return `object` as IInventory
        }
    }

    override fun createNewTileEntity(par1World: World, meta: Int): TileEntity? {
        return TileEntityParaChest()
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var enumfacing: EnumFacing = EnumFacing.from3DDataValue(meta)

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH
        }

        return this.defaultBlockState().setValue(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, COLOR, FACING)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tile: TileEntity? = worldIn.getBlockEntity(pos)
        if (tile !is TileEntityParaChest) {
            return state
        }
        val chest: TileEntityParaChest = tile
        return state.setValue(COLOR, chest.color)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING

        @JvmField
        val COLOR: PropertyEnum<EnumDyeColor> = PropertyEnum.create<EnumDyeColor>("color", EnumDyeColor::class.java)

        @JvmField
        protected val NOT_CONNECTED_AABB: AxisAlignedBB = AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375)

        @JvmStatic
        fun isOcelotBlockingChest(par0World: World, pos: BlockPos): Boolean {
            val iterator: MutableIterator<*> = par0World.getEntitiesWithinAABB<EntityOcelot?>(
                EntityOcelot::class.java,
                AxisAlignedBB(
                    pos.getX().toDouble(),
                    (pos.getY() + 1).toDouble(),
                    pos.getZ().toDouble(),
                    (pos.getX() + 1).toDouble(),
                    (pos.getY() + 2).toDouble(),
                    (pos.getZ() + 1).toDouble()
                )
            ).iterator()
            var entityocelot: EntityOcelot

            do {
                if (!iterator.hasNext()) {
                    return false
                }

                entityocelot = iterator.next() as EntityOcelot
            } while (!entityocelot.isSitting())

            return true
        }
    }
}
