package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityBuggyFueler
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockLandingPadFull constructor(assetName: String?) : BlockAdvancedTile(Material.ROCK),
    IPartialSealableBlock, LegacyBlockMetadata {
    private val AABB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0)

    enum class EnumLandingPadFullType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ROCKET_PAD(0, "rocket"),
        BUGGY_PAD(1, "buggy");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumLandingPadFullType> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumLandingPadFullType? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.setResistance(10.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
        //        this.maxY = 0.25F;
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    override fun quantityDropped(par1Random: Random): Int {
        return 9
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val var9: TileEntity? = worldIn.getBlockEntity(pos)

        if (var9 is IMultiBlock) {
            (var9 as IMultiBlock).onDestroy(var9)
        }

        super.breakBlock(worldIn, pos, state)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(GCBlocks.landingPad)
    }

    override fun getBoundingBox(blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB {
//        switch (getMetaFromState(blockState))
//        {
//        case 0:
//            return new AxisAlignedBB(pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ,
//                    pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ); TODO
//        case 2:
//            return new AxisAlignedBB(pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ,
//                    pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ);
//        default:
//        }
        return this.AABB
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        for (x2: Int in -1..1) {
            for (z2: Int in -1..1) {
                if (!super.canPlaceBlockAt(worldIn, BlockPos(pos.getX() + x2, pos.getY(), pos.getZ() + z2))) {
                    return false
                }
            }
        }

        return true
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        when (getMetaFromState(state)) {
            0 -> return TileEntityLandingPad()
            1 -> return TileEntityBuggyFueler()
            else -> return null
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        worldIn.sendBlockUpdated(pos, state, state, 3)
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

    @OnlyIn(Dist.CLIENT)
    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return true
    }

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return direction == EnumFacing.UP
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        val metadata: Int = getMetaFromState(world.getBlockState(pos))
        return ItemStack(Item.getItemFromBlock(GCBlocks.landingPad), 1, metadata)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(
            PAD_TYPE,
            (EnumLandingPadFullType.Companion.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(PAD_TYPE) as EnumLandingPadFullType).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, PAD_TYPE)
    }

    companion object {
        @JvmField
        val PAD_TYPE: PropertyEnum<EnumLandingPadFullType> =
            PropertyEnum.create<EnumLandingPadFullType>("type", EnumLandingPadFullType::class.java)
    }
}
