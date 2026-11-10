package micdoodle8.mods.galacticraft.core.blocks

import appeng.api.parts.IPartHelper
import appeng.api.AEApi
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityAluminumWire
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidPipe
import micdoodle8.mods.galacticraft.core.tile.TileEntityNull
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.Exception
import java.lang.reflect.Method

open class BlockEnclosed constructor(assetName: String?) : Block(Material.CLAY), IPartialSealableBlock,
    ITileEntityProvider, IShiftDescription, ISortableBlock {
    enum class EnumEnclosedBlockType // Legacy variants retain their metadata for existing saves
        (private val meta: Int, private val serializedName: String) : IStringSerializable {
        //        copper(1, 1, 0.25F, 0.2D, 128),
        //        glass(0, 0, 0.25F, 0.025D, 8192),
        //        gold(2, 1, 0.1875F, 0.4D, 512),
        //        iron(3, 1, 0.375F, 0.8D, 2048),
        //        tin(1, 1, 0.25F, 0.2D, 32),
        //        detector(0, 2147483647, 0.5F, 0.5D, 8192),
        //        splitter(0, 2147483647, 0.5F, 0.5D, 8192);
        IC2_HV_CABLE(0, "enclosed_hv_cable"),
        OXYGEN_PIPE(1, "enclosed_fluid_pipe"),
        IC2_COPPER_CABLE(2, "enclosed_copper_cable"),
        IC2_GOLD_CABLE(3, "enclosed_gold_cable"),
        TE_CONDUIT(4, "enclosed_te_conduit"),  //CURRENTLY UNUSED
        IC2_GLASS_FIBRE_CABLE(5, "enclosed_glass_fibre_cable"),
        IC2_LV_CABLE(6, "enclosed_lv_cable"),
        BC_ITEM_STONEPIPE(7, "enclosed_pipe_items_stone"),
        BC_ITEM_COBBLESTONEPIPE(8, "enclosed_pipe_items_cobblestone"),
        BC_FLUIDS_STONEPIPE(9, "enclosed_pipe_fluids_stone"),
        BC_FLUIDS_COBBLESTONEPIPE(10, "enclosed_pipe_fluids_cobblestone"),
        BC_POWER_STONEPIPE(11, "enclosed_pipe_power_stone"),
        BC_POWER_GOLDPIPE(12, "enclosed_pipe_power_gold"),
        ME_CABLE(13, "enclosed_me_cable"),
        ALUMINUM_WIRE(14, "enclosed_aluminum_wire"),
        ALUMINUM_WIRE_HEAVY(15, "enclosed_heavy_aluminum_wire");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumEnclosedBlockType {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setResistance(0.2f)
        this.setHardness(0.4f)
        this.setSoundType(SoundType.STONE)
        this.setTranslationKey(assetName)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, EnumEnclosedBlockType.ALUMINUM_WIRE.getMeta()))
        list.add(ItemStack(this, 1, EnumEnclosedBlockType.ALUMINUM_WIRE_HEAVY.getMeta()))
        list.add(ItemStack(this, 1, EnumEnclosedBlockType.OXYGEN_PIPE.getMeta()))

        if (CompatibilityManager.isAppEngLoaded() || GCBlocks.registeringSorted) {
            list.add(ItemStack(this, 1, EnumEnclosedBlockType.ME_CABLE.getMeta()))
        }
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun damageDropped(state: IBlockState): Int {
        return state.getBlock().getMetaFromState(state)
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        val metadata: Int = state.getBlock().getMetaFromState(state)
        val tileEntity: TileEntity? = worldIn.getTileEntity(pos)

        if (metadata == EnumEnclosedBlockType.TE_CONDUIT.getMeta()) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
        } else if (metadata == EnumEnclosedBlockType.OXYGEN_PIPE.getMeta()) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos)

            if (tileEntity is INetworkConnection) {
                (tileEntity as INetworkConnection).refresh()
            }
        } else if (metadata <= 6) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
        } else if (metadata <= 12) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
        } else if (metadata <= EnumEnclosedBlockType.ME_CABLE.getMeta()) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
        } else if (metadata <= EnumEnclosedBlockType.ALUMINUM_WIRE.getMeta()) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
            if (tileEntity is IConductor) {
                (tileEntity as IConductor).refresh()
            }
        } else if (metadata <= EnumEnclosedBlockType.ALUMINUM_WIRE_HEAVY.getMeta()) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
            if (tileEntity is IConductor) {
                (tileEntity as IConductor).refresh()
            }
        }
    }

    override fun createNewTileEntity(world: World, metadata: Int): TileEntity? {
        if (metadata == EnumEnclosedBlockType.TE_CONDUIT.getMeta()) {
            //TODO
        } else if (metadata == EnumEnclosedBlockType.OXYGEN_PIPE.getMeta()) {
            return TileEntityFluidPipe()
        } else if (metadata <= 6) {
        } else if (metadata <= 12) {
        } else if (metadata <= EnumEnclosedBlockType.ME_CABLE.getMeta()) {
            if (CompatibilityManager.isAppEngLoaded()) {
                //Emulate Api.INSTANCE.partHelper().getCombinedInstance( TileCableBus.class )
                try {
                    val apiPart: IPartHelper = AEApi.instance().partHelper()
                    val classTileCableBus: Class<*>? = Class.forName("appeng.tile.networking.TileCableBus")
                    for (m: Method in apiPart.javaClass.getMethods()) {
                        if ("getCombinedInstance" == m.getName()) {
                            return (m.invoke(apiPart, classTileCableBus) as Class<*>).newInstance() as TileEntity?
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else if (metadata <= EnumEnclosedBlockType.ALUMINUM_WIRE.getMeta()) {
            return TileEntityAluminumWire(1)
        } else if (metadata <= EnumEnclosedBlockType.ALUMINUM_WIRE_HEAVY.getMeta()) {
            return TileEntityAluminumWire(2)
        }

        return TileEntityNull()
    }

    override fun isSealed(world: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return true
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val type: EnumEnclosedBlockType = EnumEnclosedBlockType.Companion.byMetadata(meta)
        return this.getDefaultState().withProperty(TYPE, type)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(TYPE) as EnumEnclosedBlockType).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.TRANSMITTER
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    companion object {
        @JvmField
        val TYPE: PropertyEnum<EnumEnclosedBlockType> =
            PropertyEnum.create<EnumEnclosedBlockType>("type", EnumEnclosedBlockType::class.java)
    }
}
