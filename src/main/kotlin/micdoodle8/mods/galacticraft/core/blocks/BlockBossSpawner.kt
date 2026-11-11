package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.entities.EntitySkeletonBoss
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner
import net.minecraft.world.level.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import java.util.Random

open class BlockBossSpawner constructor(assetName: String?) : Block(Material.ROCK), ITileEntityProvider {
    init {
        this.blockHardness = 1000000.0f
        this.blockResistance = 1000000.0f
        this.setTranslationKey(assetName)
    }

    //    @Override
    //    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    //    {
    //        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
    //    }
    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.INVISIBLE
    }

    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return false
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(Blocks.AIR)
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        return 0
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityDungeonSpawner<EntitySkeletonBoss>(EntitySkeletonBoss::class.java)
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack.EMPTY
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return null
    }

    override fun isCollidable(): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }
}
