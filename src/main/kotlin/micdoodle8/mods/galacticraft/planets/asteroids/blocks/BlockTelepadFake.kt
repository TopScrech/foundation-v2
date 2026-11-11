package micdoodle8.mods.galacticraft.planets.asteroids.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvancedTile
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityTelepadFake
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockDirectional
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.properties.BooleanProperty as PropertyBool
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.particle.ParticleManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import java.util.Random

open class BlockTelepadFake constructor(assetName: String?) : BlockAdvancedTile(GCBlocks.machine),
    ITileEntityProvider, LegacyBlockMetadata {
    init {
        this.setSoundType(SoundType.METAL)
        //        this.setBlockTextureName(Constants.TEXTURE_PREFIX + assetName);
        this.setTranslationKey(assetName)
        //        this.setBlockTextureName(Constants.TEXTURE_PREFIX + "launch_pad");
        this.setResistance(1000000000000000.0f)
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return if (state.getValue(TOP)) AABB_TOP else AABB_BOTTOM
    }

    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    //    {
    //        IBlockState state = world.getBlockState(pos);
    //        boolean top = state.getValue(TOP);
    //
    //        if (top)
    //        {
    //            this.setBlockBounds(0.0F, 0.55F, 0.0F, 1.0F, 1.0F, 1.0F);
    //        }
    //        else
    //        {
    //            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.2F, 1.0F);
    //        }
    //    }
    //    @Override
    //    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    //    {
    //        boolean top = state.getValue(TOP);
    //
    //        if (top)
    //        {
    //            this.setBlockBounds(0.0F, 0.55F, 0.0F, 1.0F, 1.0F, 1.0F);
    //            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //        }
    //        else
    //        {
    //            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.2F, 1.0F);
    //            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //        }
    //    }
    //
    //    @Override
    //    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getCollisionBoundingBox(worldIn, pos, state);
    //    }
    //
    //    @Override
    //    @SideOnly(Side.CLIENT)
    //    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getSelectedBoundingBox(worldIn, pos);
    //    }
    override fun canDropFromExplosion(par1Explosion: Explosion): Boolean {
        return false
    }

    open fun makeFakeBlock(worldObj: World, pos: BlockPos, mainBlock: BlockPos?, state: IBlockState) {
        worldObj.setBlock(pos, state, 3)
        (worldObj.getBlockEntity(pos) as TileEntityTelepadFake).setMainBlock(mainBlock)
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        val tileEntity: TileEntity? = worldIn.getBlockEntity(pos)

        if (tileEntity is TileEntityTelepadFake) {
            val mainBlockPosition: BlockPos? = tileEntity.mainBlockPosition

            if (mainBlockPosition != null) {
                return worldIn.getBlockState(mainBlockPosition).getBlock()
                    .getBlockHardness(worldIn.getBlockState(mainBlockPosition), worldIn, mainBlockPosition)
            }
        }

        return this.blockHardness
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileEntity: TileEntity? = worldIn.getBlockEntity(pos)

        if (tileEntity is TileEntityTelepadFake) {
            tileEntity.onBlockRemoval()
        }

        super.breakBlock(worldIn, pos, state)
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
        val tileEntity: TileEntityTelepadFake? = worldIn.getBlockEntity(pos) as TileEntityTelepadFake?
        return tileEntity!!.onActivated(playerIn)
    }

    override fun quantityDropped(par1Random: Random): Int {
        return 0
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.INVISIBLE
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

    override fun createNewTileEntity(var1: World, meta: Int): TileEntity? {
        return TileEntityTelepadFake()
    }

    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        val tileEntity: TileEntity? = world.getBlockEntity(pos)
        val mainBlockPosition: BlockPos? = (tileEntity as TileEntityTelepadFake).mainBlockPosition

        if (mainBlockPosition != null) {
            val mainBlockID: Block = world.getBlockState(mainBlockPosition).getBlock()

            if (Blocks.AIR !== mainBlockID) {
                return mainBlockID.getPickBlock(
                    world.getBlockState(mainBlockPosition),
                    target,
                    world,
                    mainBlockPosition,
                    player
                )
            }
        }

        return ItemStack.EMPTY
    }

    override fun getBedDirection(state: IBlockState, world: IBlockAccess, pos: BlockPos): EnumFacing {
        val tileEntity: TileEntity? = world.getBlockEntity(pos)
        val mainBlockPosition: BlockPos? = (tileEntity as TileEntityTelepadFake).mainBlockPosition

        if (mainBlockPosition != null) {
            return world.getBlockState(pos).getBlock()
                .getBedDirection(world.getBlockState(mainBlockPosition), world, mainBlockPosition)
        }

        return getActualState(world.getBlockState(pos), world, pos).getValue(BlockDirectional.FACING)
    }

    override fun isBed(state: IBlockState, world: IBlockAccess, pos: BlockPos, player: Entity?): Boolean {
        val tileEntity: TileEntity? = world.getBlockEntity(pos)
        val mainBlockPosition: BlockPos? = (tileEntity as TileEntityTelepadFake).mainBlockPosition

        if (mainBlockPosition != null) {
            return world.getBlockState(pos).getBlock()
                .isBed(world.getBlockState(mainBlockPosition), world, mainBlockPosition, player)
        }

        return super.isBed(state, world, pos, player)
    }

    override fun setBedOccupied(world: IBlockAccess, pos: BlockPos, player: EntityPlayer, occupied: Boolean) {
        val tileEntity: TileEntity? = world.getBlockEntity(pos)
        val mainBlockPosition: BlockPos? = (tileEntity as TileEntityTelepadFake).mainBlockPosition

        if (mainBlockPosition != null) {
            world.getBlockState(pos).getBlock().setBedOccupied(world, mainBlockPosition, player, occupied)
        } else {
            super.setBedOccupied(world, pos, player, occupied)
        }
    }

    override fun addHitEffects(
        state: IBlockState,
        worldObj: World,
        target: RayTraceResult,
        manager: ParticleManager
    ): Boolean {
        val tileEntity: TileEntity? = worldObj.getBlockEntity(target.getBlockPos())

        if (tileEntity is TileEntityTelepadFake) {
            val mainBlockPosition: BlockPos? = tileEntity.mainBlockPosition

            if (mainBlockPosition != null) {
                manager.addBlockHitEffects(mainBlockPosition, target)
            }
        }

        return super.addHitEffects(state, worldObj, target, manager)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, TOP, CONNECTABLE)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(TOP, meta % 2 == 1)
            .setValue(CONNECTABLE, meta > 1)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (if (state.getValue(TOP)) 1 else 0) + (if (state.getValue(CONNECTABLE)) 2 else 0)
    }

    companion object {
        @JvmField
        val TOP: PropertyBool = PropertyBool.create("top")

        @JvmField
        val CONNECTABLE: PropertyBool = PropertyBool.create("connectable")

        @JvmField
        protected val AABB_TOP: AxisAlignedBB = AxisAlignedBB(0.0, 0.55, 0.0, 1.0, 1.0, 1.0)

        @JvmField
        protected val AABB_BOTTOM: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.2, 1.0)
    }
}
