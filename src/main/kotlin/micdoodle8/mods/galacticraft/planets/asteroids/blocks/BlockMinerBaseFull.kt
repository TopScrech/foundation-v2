package micdoodle8.mods.galacticraft.planets.asteroids.blocks

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.Random

open class BlockMinerBaseFull constructor(assetName: String?) : BlockTileGC(Material.ROCK) {
    init {
        this.blockHardness = 3.0f
        this.setResistance(35f)
        this.setTranslationKey(assetName)
        this.setSoundType(SoundType.METAL)
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.INVISIBLE
    }

    override fun damageDropped(state: IBlockState): Int {
        return 0
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        return 1
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
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

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TileEntityMinerBase()
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
        return this.getStateFromMeta(0)
        //TODO
        //return this.getMetadataFromAngle(world, x, y, z, side);
    }

    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        //TODO
        /*
    	if (this.getMetadataFromAngle(world, x, y, z, side) != -1)
        {
            return true;
        }
    	 */

        return true
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileEntity: TileEntity? = worldIn.getBlockEntity(pos)

        if (tileEntity is TileEntityMinerBase) {
            tileEntity.onBlockRemoval()
        }

        super.breakBlock(worldIn, pos, state)
    }

    override fun onMachineActivated(
        worldIn: World?,
        pos: BlockPos?,
        state: IBlockState?,
        playerIn: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val tileEntity: TileEntity? = (worldIn)!!.getBlockEntity(pos)
        if (tileEntity is TileEntityMinerBase) {
            return tileEntity.onActivated(playerIn)
        } else {
            return false
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(AsteroidBlocks.blockMinerBase)
    }

    override fun getDrops(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ): MutableList<ItemStack?> {
        val ret: ArrayList<ItemStack?> = ArrayList<ItemStack?>()
        ret.add(ItemStack(Item.getItemFromBlock(AsteroidBlocks.blockMinerBase), 8, 0))
        return ret
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(AsteroidBlocks.blockMinerBase), 1, 0)
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
        val te: TileEntity? = (world)!!.getBlockEntity(pos)
        if (te is TileEntityMinerBase) {
            te.updateFacing()
        }
        return true
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }
}
