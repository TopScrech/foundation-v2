package micdoodle8.mods.galacticraft.core.blocks

import foundation.item.legacyItemValue

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityCargoLoader
import micdoodle8.mods.galacticraft.core.tile.TileEntityCargoUnloader
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
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

open class BlockCargoLoader constructor(assetName: String?) : BlockAdvancedTile(Material.ROCK), IShiftDescription,
    ISortableBlock, LegacyBlockMetadata {
    enum class EnumLoaderType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        CARGO_LOADER(METADATA_CARGO_LOADER, "cargo_loader"),
        CARGO_UNLOADER(METADATA_CARGO_UNLOADER, "cargo_unloader");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }
    }

    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, METADATA_CARGO_LOADER))
        list.add(ItemStack(this, 1, METADATA_CARGO_UNLOADER))
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)

        val tileEntity: TileEntity? = worldIn.getBlockEntity(pos)

        if (tileEntity != null) {
            if (tileEntity is TileEntityCargoLoader) {
                tileEntity.checkForCargoEntity()
            } else if (tileEntity is TileEntityCargoUnloader) {
                tileEntity.checkForCargoEntity()
            }
        }
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
        (playerIn)!!.openGui(GalacticraftCore.Companion.instance, -1, worldIn, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        if (getMetaFromState(state) < METADATA_CARGO_UNLOADER) {
            return TileEntityCargoLoader()
        } else {
            return TileEntityCargoUnloader()
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
        val state: IBlockState = (world)!!.getBlockState(pos)
        TileBaseUniversalElectrical.onUseWrenchBlock(state, (world)!!, (pos)!!, state.getValue(FACING))
        return true
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

        if (stack.legacyItemValue >= METADATA_CARGO_UNLOADER) {
            change += METADATA_CARGO_UNLOADER
        } else if (stack.legacyItemValue >= METADATA_CARGO_LOADER) {
            change += METADATA_CARGO_LOADER
        }

        worldIn.setBlock(pos, getStateFromMeta(change), 3)
        WorldUtil.markAdjacentPadForUpdate(worldIn, pos)
    }

    public override fun onPlayerDestroy(worldIn: World?, pos: BlockPos?, state: IBlockState?) {
        super.onPlayerDestroy(worldIn, pos, state)
        WorldUtil.markAdjacentPadForUpdate((worldIn)!!, (pos)!!)
    }

    override fun damageDropped(state: IBlockState): Int {
        return (state.getValue(TYPE) as EnumLoaderType).getMeta()
    }

    override fun getShiftDescription(meta: Int): String? {
        when (meta) {
            METADATA_CARGO_LOADER -> return GCCoreUtil.translate("tile.cargo_loader.description")
            METADATA_CARGO_UNLOADER -> return GCCoreUtil.translate("tile.cargo_unloader.description")
        }
        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        val type: EnumLoaderType =
            if (meta >= METADATA_CARGO_UNLOADER) EnumLoaderType.CARGO_UNLOADER else EnumLoaderType.CARGO_LOADER

        return this.defaultBlockState().setValue(FACING, enumfacing)
            .setValue(TYPE, type)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING) as EnumFacing).getHorizontalIndex() + (state.getValue(
            TYPE
        ) as EnumLoaderType).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        val TYPE: PropertyEnum<EnumLoaderType> =
            PropertyEnum.create<EnumLoaderType>("type", EnumLoaderType::class.java)

        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING

        const val METADATA_CARGO_LOADER: Int = 0

        const val METADATA_CARGO_UNLOADER: Int = 4
    }
}
