package micdoodle8.mods.galacticraft.planets.venus.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.IShearable
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.Random

open class BlockTorchWeb constructor(assetName: String?) : Block(Material.CIRCUITS), IShearable, IShiftDescription,
    ISortableBlock, LegacyBlockMetadata {
    enum class EnumWebType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        WEB_0(0, "web_torch_0"),
        WEB_1(1, "web_torch_1");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumWebType> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumWebType? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setLightLevel(1.0f)
        this.setTranslationKey(assetName)
    }

    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, 0))
        list.add(ItemStack(this, 1, 1))
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        if (state.getValue(WEB_TYPE) == EnumWebType.WEB_1) {
            return AABB_WEB_TORCH
        }

        return AABB_WEB
    }

    //    @Override
    //    public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    //    {
    //        EnumWebType type = (EnumWebType)worldIn.getBlockState(pos).getValue(WEB_TYPE);
    //        float f = 0.15F;
    //
    //        if (type == EnumWebType.WEB_0)
    //        {
    //            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
    //        }
    //        else if (type == EnumWebType.WEB_1)
    //        {
    //            this.setBlockBounds(0.5F - f, 0.25F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
    //        }
    //
    //        return super.collisionRayTrace(worldIn, pos, start, end);
    //    }
    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        if (state.getValue(WEB_TYPE) == EnumWebType.WEB_1) {
            return 15
        }

        return 0
    }

    @OnlyIn(Dist.CLIENT)
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

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return null
    }

    open fun canBlockStay(world: World, pos: BlockPos, state: IBlockState): Boolean {
        val blockUp: IBlockState = world.getBlockState(pos.above())

        val meta: Int = this.getMetaFromState(state)

        if (meta == 0) {
            return blockUp.getMaterial().isSolid() || blockUp.getBlock() === this && blockUp.getValue(
                WEB_TYPE
            ) == EnumWebType.WEB_0
        } else {
            return blockUp.getBlock() === this && blockUp.getValue(WEB_TYPE) == EnumWebType.WEB_0
        }
    }

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        this.checkAndDropBlock(world, pos, state)
    }

    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        this.checkAndDropBlock(world, pos, state)
    }

    protected open fun checkAndDropBlock(world: World, pos: BlockPos, state: IBlockState) {
        if (!this.canBlockStay(world, pos, state)) {
            this.dropBlockAsItem(world, pos, state, 0)
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(Blocks.AIR)
    }

    override fun quantityDropped(rand: Random): Int {
        return 0
    }

    override fun isShearable(item: ItemStack, world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun onSheared(
        item: ItemStack,
        world: IBlockAccess,
        pos: BlockPos,
        fortune: Int
    ): MutableList<ItemStack?> {
        val ret: ArrayList<ItemStack?> = ArrayList<ItemStack?>()
        ret.add(ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos))))
        return ret
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(WEB_TYPE, (EnumWebType.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(WEB_TYPE) as EnumWebType).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, WEB_TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    companion object {
        @JvmField
        val WEB_TYPE: PropertyEnum<EnumWebType> = PropertyEnum.create<EnumWebType>("webtype", EnumWebType::class.java)

        @JvmField
        protected val AABB_WEB: AxisAlignedBB = AxisAlignedBB(0.35, 0.0, 0.35, 0.65, 1.0, 0.65)

        @JvmField
        protected val AABB_WEB_TORCH: AxisAlignedBB = AxisAlignedBB(0.35, 0.25, 0.35, 0.65, 1.0, 0.65)
    }
}
