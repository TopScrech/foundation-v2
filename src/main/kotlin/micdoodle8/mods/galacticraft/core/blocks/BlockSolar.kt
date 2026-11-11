package micdoodle8.mods.galacticraft.core.blocks

import foundation.item.legacyItemValue

import foundation.block.LegacyBlockMetadata

import foundation.level.offset

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.floor

open class BlockSolar constructor(assetName: String?) : BlockTileGC(Material.IRON), IShiftDescription,
    IPartialSealableBlock, ISortableBlock, LegacyBlockMetadata {
    enum class EnumSolarType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        BASIC_SOLAR(0, "basic_solar"),
        ADVANCED_SOLAR(1, "advanced_solar"); // 3 for backwards compatibility

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumSolarType {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, BASIC_METADATA))
        list.add(ItemStack(this, 1, ADVANCED_METADATA))
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        for (y: Int in 1..2) {
            for (x: Int in -1..1) {
                for (z: Int in -1..1) {
                    val posAt: BlockPos = pos.offset(if (y == 2) x else 0, y, if (y == 2) z else 0)
                    val bs: IBlockState = worldIn.getBlockState(posAt)

                    if (bs.getMaterial() !== Material.AIR && !bs.getBlock().isReplaceable(worldIn, posAt)) {
                        return false
                    }
                }
            }
        }

        for (x: Int in -2..2) {
            for (z: Int in -2..2) {
                val posAt: BlockPos = pos.offset(x, 0, z)
                val block: Block = worldIn.getBlockState(posAt).getBlock()

                if (block === this) {
                    return false
                }
            }
        }

        return true
        // return new BlockVec3(x1, y1, z1).newVecSide(side ^ 1).getBlock(world) != GCBlocks.fakeBlock; TODO
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val angle: Int = MathHelper.floor(placer.yRot * 4.0f / 360.0f + 0.5) and 3
        var change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()

        if (stack.legacyItemValue >= ADVANCED_METADATA) {
            change += ADVANCED_METADATA
        } else if (stack.legacyItemValue >= BASIC_METADATA) {
            change += BASIC_METADATA
        }

        worldIn.setBlock(pos, getStateFromMeta(change), 3)

        BlockMulti.Companion.onPlacement(worldIn, pos, placer, this)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val var9: TileEntity? = worldIn.getBlockEntity(pos)

        if (var9 is TileEntitySolar) {
            var9.onDestroy(var9)
        }

        super.breakBlock(worldIn, pos, state)
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
        val state: IBlockState = (world)!!.getBlockState(pos)
        TileBaseUniversalElectrical.onUseWrenchBlock(state, (world)!!, (pos)!!, state.getValue(FACING))
        return true
    }

    override fun onMachineActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        (entityPlayer)!!.openGui(GalacticraftCore.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
        return true
    }

    override fun damageDropped(state: IBlockState): Int {
        if (getMetaFromState(state) >= ADVANCED_METADATA) {
            return ADVANCED_METADATA
        } else {
            return BASIC_METADATA
        }
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        if (getMetaFromState(state) >= ADVANCED_METADATA) {
            return TileEntitySolar(2)
        } else {
            return TileEntitySolar(1)
        }
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getShiftDescription(meta: Int): String? {
        when (meta) {
            BASIC_METADATA -> return GCCoreUtil.translate("tile.solar_basic.description")
            ADVANCED_METADATA -> return GCCoreUtil.translate("tile.solar_adv.description")
        }
        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        val type: EnumSolarType = EnumSolarType.Companion.byMetadata(floor(meta / 4.0).toInt())
        return this.defaultBlockState().setValue(FACING, enumfacing)
            .setValue(TYPE, type)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING) as EnumFacing).getHorizontalIndex() + (state.getValue(
            TYPE
        ) as EnumSolarType).getMeta() * 4
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        const val BASIC_METADATA: Int = 0

        const val ADVANCED_METADATA: Int = 4

        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING

        @JvmField
        val TYPE: PropertyEnum<EnumSolarType> = PropertyEnum.create<EnumSolarType>("type", EnumSolarType::class.java)
    }
}
