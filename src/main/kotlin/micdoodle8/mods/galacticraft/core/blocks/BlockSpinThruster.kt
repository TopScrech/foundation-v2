package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityThruster
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class BlockSpinThruster constructor(assetName: String?) : BlockAdvanced(Material.CIRCUITS), IShiftDescription,
    ITileEntityProvider, ISortableBlock {
    init {
        this.setHardness(0.1f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
        this.setDefaultState(
            this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)
        )
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        when (state.getValue(FACING)) {
            EnumFacing.EAST -> return EAST_AABB
            EnumFacing.WEST -> return WEST_AABB
            EnumFacing.SOUTH -> return SOUTH_AABB
            EnumFacing.NORTH -> return NORTH_AABB
            else -> return NORTH_AABB
        }
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

    override fun canPlaceBlockAt(world: World, pos: BlockPos): Boolean {
        return world.isSideSolid(pos.west(), EnumFacing.EAST, true) || world.isSideSolid(
            pos.east(),
            EnumFacing.WEST,
            true
        ) || world.isSideSolid(pos.north(), EnumFacing.SOUTH, true) || world.isSideSolid(
            pos.south(),
            EnumFacing.NORTH,
            true
        )
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
        if (facing.getAxis().isHorizontal() && this.canBlockStay(world, pos, facing)) {
            return this.getDefaultState().withProperty(FACING, facing)
        } else {
            for (enumfacing: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
                if (this.canBlockStay(world, pos, enumfacing)) {
                    return this.getDefaultState().withProperty(FACING, enumfacing)
                }
            }
            return this.getDefaultState()
        }
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (this.getMetaFromState(state) == 0) {
            this.onBlockAdded(worldIn, pos, state)
        }
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        val metadata: Int = this.getMetaFromState(state)

        val baseBlock: BlockPos
        when (metadata) {
            1 -> baseBlock = pos.offset(EnumFacing.WEST)
            2 -> baseBlock = pos.offset(EnumFacing.EAST)
            3 -> baseBlock = pos.offset(EnumFacing.NORTH)
            4 -> baseBlock = pos.offset(EnumFacing.SOUTH)
            else -> return
        }

        if (!worldIn.isRemote) {
            if (worldIn.provider is WorldProviderSpaceStation) {
                (worldIn.provider as WorldProviderSpaceStation).getSpinManager().refresh(baseBlock, true)
            }
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        val enumfacing: EnumFacing = state.getValue(FACING)

        if (!this.canBlockStay(worldIn, pos, enumfacing)) {
            this.dropBlockAsItem(worldIn, pos, state, 0)
            worldIn.setBlockToAir(pos)
        }
        if (!worldIn.isRemote) {
            if (worldIn.provider is WorldProviderSpaceStation) {
                (worldIn.provider as WorldProviderSpaceStation).getSpinManager().refresh(pos, true)
            }
        }
    }

    protected open fun canBlockStay(world: World, pos: BlockPos, facing: EnumFacing): Boolean {
        return world.isSideSolid(pos.offset(facing.getOpposite()), facing, true)
    }

    //    @Override
    //    public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    //    {
    //        float var8 = 0.3F;
    //
    //        EnumFacing facing = worldIn.getBlockState(pos).getValue(BlockMachine.FACING);
    //
    //        switch (facing)
    //        {
    //        case NORTH:
    //            this.setBlockBounds(0.5F - var8, 0.2F, 1.0F - var8 * 2.0F, 0.5F + var8, 0.8F, 1.0F);
    //            break;
    //        case EAST:
    //            this.setBlockBounds(0.0F, 0.2F, 0.5F - var8, var8 * 2.0F, 0.8F, 0.5F + var8);
    //            break;
    //        case SOUTH:
    //            this.setBlockBounds(0.5F - var8, 0.2F, 0.0F, 0.5F + var8, 0.8F, var8 * 2.0F);
    //            break;
    //        case WEST:
    //            this.setBlockBounds(1.0F - var8 * 2.0F, 0.2F, 0.5F - var8, 1.0F, 0.8F, 0.5F + var8);
    //            break;
    //        }
    //
    //        return super.collisionRayTrace(worldIn, pos, start, end);
    //    }
    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        //TODO this is torch code as a placeholder, still need to adjust positioning and particle type
        //Also make small thrust sounds
        if (worldIn.provider is WorldProviderSpaceStation) {
            if ((worldIn.provider as WorldProviderSpaceStation).getSpinManager().thrustersFiring || rand.nextInt(80) == 0) {
                val var6: Int = this.getMetaFromState(stateIn) and 7
                val var7: Double = (pos.getX() + 0.5f).toDouble()
                val var9: Double = (pos.getY() + 0.7f).toDouble()
                val var11: Double = (pos.getZ() + 0.5f).toDouble()
                val var13: Double = 0.2199999988079071
                val var15: Double = 0.27000001072883606

                if (var6 == 1) {
                    worldIn.spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        var7 - var15,
                        var9 + var13,
                        var11,
                        0.0,
                        0.0,
                        0.0
                    )
                } else if (var6 == 2) {
                    worldIn.spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        var7 + var15,
                        var9 + var13,
                        var11,
                        0.0,
                        0.0,
                        0.0
                    )
                } else if (var6 == 3) {
                    worldIn.spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        var7,
                        var9 + var13,
                        var11 - var15,
                        0.0,
                        0.0,
                        0.0
                    )
                } else if (var6 == 4) {
                    worldIn.spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        var7,
                        var9 + var13,
                        var11 + var15,
                        0.0,
                        0.0,
                        0.0
                    )
                }
            }
        }
    }

    override fun onUseWrench(
        world: World?,
        pos: BlockPos?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val currentFacing: EnumFacing = (world)!!.getBlockState(pos).getValue(FACING)
        var nextFacing: EnumFacing = currentFacing.rotateY()
        while (true) {
            if (this.canBlockStay((world)!!, (pos)!!.offset(nextFacing.getOpposite()), nextFacing)) {
                (world)!!.setBlockState(pos, this.getStateFromMeta(nextFacing.getHorizontalIndex()), 2)
                break
            }

            if (nextFacing == currentFacing) {
                break
            }
            nextFacing = nextFacing.rotateY()
        }

        if ((world)!!.provider is WorldProviderSpaceStation && !(world)!!.isRemote) {
            val worldOrbital: WorldProviderSpaceStation = (world)!!.provider as WorldProviderSpaceStation
            worldOrbital.getSpinManager().refresh(pos, true)
        }
        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityThruster()
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (!worldIn.isRemote) {
            val facing: Int = this.getMetaFromState(state) and 8
            if (worldIn.provider is WorldProviderSpaceStation) {
                val worldOrbital: WorldProviderSpaceStation = worldIn.provider as WorldProviderSpaceStation
                worldOrbital.getSpinManager().removeThruster(pos, facing == 0)
                worldOrbital.getSpinManager().updateSpinSpeed()
            }
        }
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta)
        return this.getDefaultState().withProperty(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(FACING).getHorizontalIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

        @JvmField
        protected val NORTH_AABB: AxisAlignedBB = AxisAlignedBB(0.2, 0.2, 0.4, 0.8, 0.8, 1.0)

        @JvmField
        protected val SOUTH_AABB: AxisAlignedBB = AxisAlignedBB(0.2, 0.2, 0.0, 0.8, 0.8, 0.6)

        @JvmField
        protected val WEST_AABB: AxisAlignedBB = AxisAlignedBB(0.4, 0.2, 0.2, 1.0, 0.8, 0.8)

        @JvmField
        protected val EAST_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.2, 0.2, 0.6, 0.8, 0.8)
    }
}
