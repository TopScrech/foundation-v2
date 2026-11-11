package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLock
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLockController
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
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
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World

open class BlockAirLockFrame constructor(assetName: String?) : BlockAdvancedTile(Material.ROCK), IShiftDescription,
    ISortableBlock, LegacyBlockMetadata {
    enum class EnumAirLockType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        AIR_LOCK_FRAME(0, "air_lock_frame"),
        AIR_LOCK_CONTROLLER(1, "air_lock_controller");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumAirLockType> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumAirLockType? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setDefaultState(
            this.blockState.getBaseState()
                .setValue(AIR_LOCK_TYPE, EnumAirLockType.AIR_LOCK_FRAME)
        )
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, EnumAirLockType.AIR_LOCK_FRAME.getMeta()))
        list.add(ItemStack(this, 1, EnumAirLockType.AIR_LOCK_CONTROLLER.getMeta()))
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return true
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)

        val tile: TileEntity? = worldIn.getBlockEntity(pos)

        if (tile is TileEntityAirLockController && placer is EntityPlayer) {
            tile.ownerName = (PlayerUtil.getName(placer))!!
        }
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        if ((state.getValue(AIR_LOCK_TYPE) as EnumAirLockType).getMeta() == EnumAirLockType.AIR_LOCK_FRAME.getMeta()) {
            return TileEntityAirLock()
        } else {
            return TileEntityAirLockController()
        }
    }

    override fun canConnectRedstone(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing?
    ): Boolean {
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
        val tile: TileEntity? = (world)!!.getBlockEntity(pos)

        if (((state)!!.getValue(AIR_LOCK_TYPE) as EnumAirLockType).getMeta() == EnumAirLockType.AIR_LOCK_CONTROLLER.getMeta() && tile is TileEntityAirLockController) {
            (entityPlayer)!!.openGui(GalacticraftCore.Companion.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
            return true
        }

        return false
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile: TileEntity? = worldIn.getBlockEntity(pos)

        if (tile is TileEntityAirLockController) {
            tile.unsealAirLock()
        }

        super.breakBlock(worldIn, pos, state)
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(AIR_LOCK_TYPE, (EnumAirLockType.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(AIR_LOCK_TYPE) as EnumAirLockType).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, AIR_LOCK_TYPE)
    }

    override fun getShiftDescription(itemDamage: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(itemDamage: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        val AIR_LOCK_TYPE: PropertyEnum<EnumAirLockType> =
            PropertyEnum.create<EnumAirLockType>("airlocktype", EnumAirLockType::class.java)
    }
}
