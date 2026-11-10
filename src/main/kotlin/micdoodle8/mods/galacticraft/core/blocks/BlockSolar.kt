package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.floor

open class BlockSolar constructor(assetName: String?) : BlockTileGC(Material.IRON), IShiftDescription,
    IPartialSealableBlock, ISortableBlock {
    enum class EnumSolarType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        BASIC_SOLAR(0, "basic_solar"),
        ADVANCED_SOLAR(1, "advanced_solar"); // 3 for backwards compatibility

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
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

    @SideOnly(Side.CLIENT)
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
                    val posAt: BlockPos = pos.add(if (y == 2) x else 0, y, if (y == 2) z else 0)
                    val bs: IBlockState = worldIn.getBlockState(posAt)

                    if (bs.getMaterial() !== Material.AIR && !bs.getBlock().isReplaceable(worldIn, posAt)) {
                        return false
                    }
                }
            }
        }

        for (x: Int in -2..2) {
            for (z: Int in -2..2) {
                val posAt: BlockPos = pos.add(x, 0, z)
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
        val angle: Int = MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) and 3
        var change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()

        if (stack.getItemDamage() >= ADVANCED_METADATA) {
            change += ADVANCED_METADATA
        } else if (stack.getItemDamage() >= BASIC_METADATA) {
            change += BASIC_METADATA
        }

        worldIn.setBlockState(pos, getStateFromMeta(change), 3)

        BlockMulti.Companion.onPlacement(worldIn, pos, placer, this)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val var9: TileEntity? = worldIn.getTileEntity(pos)

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
        return this.getDefaultState().withProperty(FACING, enumfacing)
            .withProperty(TYPE, type)
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
        val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

        @JvmField
        val TYPE: PropertyEnum<EnumSolarType> = PropertyEnum.create<EnumSolarType>("type", EnumSolarType::class.java)
    }
}
