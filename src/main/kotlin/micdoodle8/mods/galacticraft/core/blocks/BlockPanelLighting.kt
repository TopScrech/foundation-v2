package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.api.item.IPaintable
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityPanelLight
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PropertyObject
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList

open class BlockPanelLighting constructor(assetName: String?) : BlockAdvancedTile(Material.IRON), ISortableBlock,
    IShiftDescription, IPaintable {
    enum class PanelType(private val serializedName: String, private val light: Int) : IStringSerializable {
        SQUARE("square", 11),
        SPOTS("spots", 7),
        LINEAR("linear", 9),
        SF("sf", 2),
        SFDIAG(
            "sfdiag",
            2
        ); //IF ADDING TO THIS ENUM, MAKE SURE TO CHANGE DEFINITION OF PacketSimple.C_UPDATE_STATS!!!!!!

        override fun getName(): String {
            return this.serializedName
        }

        fun getLight(): Int {
            return this.light
        }
    }

    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
        this.setDefaultState(this.blockState.getBaseState())
    }

    override fun createBlockState(): BlockStateContainer {
        return ExtendedBlockState(this, arrayOf<IProperty<*>?>(TYPE), arrayOf<IUnlistedProperty<*>?>(BASE_STATE))
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (i: Int in 0..<PANELTYPES_LENGTH) {
            list.add(ItemStack(this, 1, i))
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        return this.getMetaFromState(state)
    }

    override fun getDrops(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ): MutableList<ItemStack?> {
        val result: ArrayList<ItemStack?> = ArrayList<ItemStack?>()
        result.add(ItemStack(this.getItemDropped(state, null, fortune), 1, this.getMetaFromState(state)))
        return result
    }

    override fun getStateForPlacement(
        worldIn: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        damage: Int,
        placer: EntityLivingBase
    ): IBlockState {
        return this.getStateFromMeta(damage)
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        var damage: Int = stack.getItemDamage()
        if (damage >= PANELTYPES_LENGTH) damage = 0
        val tile: TileEntity? = worldIn.getTileEntity(pos)
        if (tile is TileEntityPanelLight && placer is EntityPlayer) {
            tile.initialise(
                damage,
                EnumFacing.getDirectionFromEntityLiving(pos, placer),
                placer,
                worldIn.isRemote,
                superState[damage]
            )
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
        val tile: TileEntity? = (world)!!.getTileEntity(pos)
        if (tile !is TileEntityPanelLight) {
            return false
        }
        val bs: IBlockState = (world)!!.getBlockState(pos)
        if (bs.getBlock() !is BlockPanelLighting) {
            return false
        }
        val type: PanelType = bs.getValue(TYPE) as PanelType
        val tilegood: TileEntityPanelLight = tile
        val metadata: Int = tilegood.meta
        if (metadata < 8 && (type == PanelType.LINEAR || type == PanelType.SF) || metadata < 24 && type == PanelType.SFDIAG) {
            tilegood.meta += 8
            return true
        }

        var metaDir: Int = ((metadata and 7) + 1) % 6
        //DOWN->UP->NORTH->*EAST*->*SOUTH*->WEST
        //0->1 1->2 2->5 3->4 4->0 5->3 
        if (metaDir == 3)  //after north
        {
            metaDir = 5
        } else if (metaDir == 0) {
            metaDir = 3
        } else if (metaDir == 5) {
            metaDir = 0
        }

        tilegood.meta = metaDir
        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityPanelLight()
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate("tile.panel_lighting.description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.DECORATION
    }

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        return true
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var meta: Int = meta
        if (meta >= PANELTYPES_LENGTH) meta = 0
        return this.getDefaultState().withProperty(TYPE, PanelType.entries[meta])
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(TYPE) as PanelType).ordinal
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        var state: IBlockState = state
        val tile: TileEntity? = world.getTileEntity(pos)
        if (tile is TileEntityPanelLight) {
            state = (state as IExtendedBlockState).withProperty(BASE_STATE, tile.getBaseBlock())
        }

        return state
    }

    override fun getLightValue(bs: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        if (bs.getBlock() !is BlockPanelLighting) {
            return 0
        }
        if (world is World && RedstoneUtil.isBlockReceivingRedstone(world, pos)) {
            return 0
        }
        return (bs.getValue(TYPE) as PanelType).getLight()
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        worldIn.checkLightFor(EnumSkyBlock.BLOCK, pos)
    }

    override fun setColor(color: Int, p: EntityPlayer?, side: Side?): Int {
        if (side == Side.CLIENT) {
            Companion.color = ColorUtil.lighten(ColorUtil.lightenFully(color, 255), 0.1f)
        } else {
            val stats: GCPlayerStats = (GCPlayerStats.get((p)!!))!!
            stats.setPanelLightingColor(color)
        }
        return 1
    }

    companion object {
        @JvmField
        val TYPE: PropertyEnum<PanelType> = PropertyEnum.create<PanelType>("type", PanelType::class.java)

        @JvmField
        val BASE_STATE: PropertyObject<IBlockState?> =
            PropertyObject<IBlockState?>("held_state", IBlockState::class.java)

        @JvmField
        var color: Int = 0xf0f0e0

        @JvmField
        val PANELTYPES_LENGTH: Int = PanelType.entries.size
        private val superState = arrayOfNulls<IBlockState>(PANELTYPES_LENGTH) // - only used clientSide

        @JvmStatic
        @SideOnly(value = Side.CLIENT)
        fun updateClient(type: Int, state: IBlockState?) {
            var type: Int = type
            if (type >= PANELTYPES_LENGTH) type = 0
            superState[type] = state
        }

        @JvmStatic
        @SideOnly(value = Side.CLIENT)
        fun updateClient(data: MutableList<Any?>) {
            var state: IBlockState?
            for (i: Int in 0..<PANELTYPES_LENGTH) {
                state = TileEntityPanelLight.readBlockState((data.get(i + i + 1) as String?)!!, (data.get(i + i + 2) as Int?)!!)
                if (state.getBlock() === Blocks.AIR) {
                    state = null
                }
                superState[i] = state
            }
            color =
                (data.get(2 * micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting.Companion.PANELTYPES_LENGTH + 1) as kotlin.Int?)!!
        }

        @JvmStatic
        fun getNetworkedData(result: Array<Any?>, panel_lighting: Array<IBlockState?>) {
            //IF CHANGING THIS, MAKE SURE TO CHANGE DEFINITION OF PacketSimple.C_UPDATE_STATS
            var block: Block
            var bs: IBlockState?
            for (i: Int in 0..<PANELTYPES_LENGTH) {
                bs = panel_lighting[i]
                if (bs == null) bs = Blocks.AIR.getDefaultState()
                block = (bs)!!.getBlock()
                result[i + i + 1] = net.minecraft.block.Block.REGISTRY.getNameForObject(block).toString()
                result[i + i + 2] = block.getMetaFromState(bs)
            }
        }
    }
}
