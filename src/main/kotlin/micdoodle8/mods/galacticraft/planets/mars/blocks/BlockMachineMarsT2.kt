package micdoodle8.mods.galacticraft.planets.mars.blocks

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityElectrolyzer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityGasLiquefier
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityMethaneSynthesizer
import net.minecraft.block.SoundType
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
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class BlockMachineMarsT2 constructor(assetName: String?) : BlockTileGC(GCBlocks.machine), IShiftDescription,
    ISortableBlock {
    enum class EnumMachineType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        GAS_LIQUEFIER(GAS_LIQUEFIER_META, "gas_liquefier"),
        METHANE_SYNTHESIZER(METHANE_SYNTHESIZER_META, "methane_synthesizer"),
        ELECTROLYZER(ELECTROLYZER_META, "electrolyzer");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }
    }

    /*private IIcon iconMachineSide;
    private IIcon iconInput;
    private IIcon iconGasInput;
    private IIcon iconGasOutput;
    private IIcon iconWaterInput;

    private IIcon iconGasLiquefier;
    private IIcon iconMethaneSynthesizer;
    private IIcon iconElectrolyzer;*/
    init {
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    /*@Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("galacticraftasteroids:machine");
        this.iconInput = par1IconRegister.registerIcon("galacticraftasteroids:machine_input");

        this.iconMachineSide = par1IconRegister.registerIcon("galacticraftasteroids:machine_side_warning");
        this.iconGasInput = par1IconRegister.registerIcon("galacticraftasteroids:machine_oxygen_input_warning");
        this.iconGasOutput = par1IconRegister.registerIcon("galacticraftasteroids:machine_oxygen_output_warning");
        this.iconWaterInput = par1IconRegister.registerIcon("galacticraftasteroids:machine_water_input_warning");
        this.iconGasLiquefier = par1IconRegister.registerIcon("galacticraftasteroids:gasLiquefier");
        this.iconMethaneSynthesizer = par1IconRegister.registerIcon("galacticraftasteroids:methaneSynthesizer");
        this.iconElectrolyzer = par1IconRegister.registerIcon("galacticraftasteroids:electrolyzer");
    }*/
    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    /*@Override
    public IIcon getIcon(int side, int metadata)
    {
        if (side == 0)
        {
            return this.iconInput;
        }

        if (side == 1)
        {
            return this.blockIcon;
        }

        int metaside = (metadata & 3) + 2;
        metadata &= 12;

        if (metadata == BlockMachineMarsT2.GAS_LIQUEFIER)
        {
            if (side == metaside)
            {
                return this.iconGasInput;
            }

            //2->5 3->4 4->2 5->3
            if (7 - (metaside ^ (metaside > 3 ? 0 : 1)) == side)
            {
                return this.iconGasLiquefier;
            }
        }
        else if (metadata == BlockMachineMarsT2.METHANE_SYNTHESIZER)
        {
            if (side == metaside)
            {
                return this.iconGasInput;
            }

            if (side == (metaside ^ 1))
            {
                return this.iconGasOutput;
            }

            //2->5 3->4 4->2 5->3
            if (7 - (metaside ^ (metaside > 3 ? 0 : 1)) == side)
            {
                return this.iconMethaneSynthesizer;
            }
        }
        else if (metadata == BlockMachineMarsT2.ELECTROLYZER)
        {
            if (side == (metaside ^ 1))
            {
                return this.iconGasOutput;
            }

            //2->5 3->4 4->2 5->3
            if (7 - (metaside ^ (metaside > 3 ? 0 : 1)) == side)
            {
                return this.iconElectrolyzer;
            }
            
            if (side == metaside)
            {
            	return this.iconWaterInput;
            }
            
           	return this.iconGasOutput;
        }

        return this.iconMachineSide;
    }*/
    /**
     * Called when the block is placed in the world.
     */
    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val metadata: Int = state.getBlock().getMetaFromState(state)

        val angle: Int = MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) and 3
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()

        worldIn.setBlockState(pos, getStateFromMeta((metadata and 12) + change), 3)
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

    /**
     * Called when the block is right clicked by the player
     */
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
        (playerIn)!!.openGui(
            GalacticraftPlanets.instance,
            GuiIdsPlanets.MACHINE_MARS,
            worldIn,
            (pos)!!.getX(),
            (pos)!!.getY(),
            (pos)!!.getZ()
        )
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        var metadata: Int = state.getBlock().getMetaFromState(state)
        metadata = metadata and 12

        if (metadata == GAS_LIQUEFIER_META) {
            return TileEntityGasLiquefier()
        } else if (metadata == METHANE_SYNTHESIZER_META) {
            return TileEntityMethaneSynthesizer()
        } else if (metadata == ELECTROLYZER_META) {
            return TileEntityElectrolyzer()
        }

        return null
    }

    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, GAS_LIQUEFIER_META))
        list.add(ItemStack(this, 1, METHANE_SYNTHESIZER_META))
        list.add(ItemStack(this, 1, ELECTROLYZER_META))
    }

    override fun damageDropped(state: IBlockState): Int {
        return state.getBlock().getMetaFromState(state) and 12
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        val metadata: Int = this.getMetaFromState(world.getBlockState(pos)) and 12
        return ItemStack(this, 1, metadata)
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        val te: TileEntity? = worldIn.getTileEntity(pos)

        if (te is TileEntityGasLiquefier) {
            val tileEntity: TileEntityGasLiquefier = te

            if (tileEntity.processTicks > 0) {
                val x: Float = pos.getX() + 0.5f
                val y: Float = pos.getY() + 0.8f + 0.05f * rand.nextInt(3)
                val z: Float = pos.getZ() + 0.5f

                var i: Float = -0.41f + 0.16f * rand.nextFloat()
                while (i < 0.5f) {
                    if (rand.nextInt(3) == 0) {
                        (GalacticraftCore.proxy)!!.spawnParticle(
                            "whiteSmokeTiny",
                            Vector3((x + i).toDouble(), y.toDouble(), (z - 0.41f).toDouble()),
                            Vector3(0.0, -0.015, -0.0015),
                            arrayOf<Any?>()
                        )
                    }
                    if (rand.nextInt(3) == 0) {
                        (GalacticraftCore.proxy)!!.spawnParticle(
                            "whiteSmokeTiny",
                            Vector3((x + i).toDouble(), y.toDouble(), (z + 0.537f).toDouble()),
                            Vector3(0.0, -0.015, 0.0015),
                            arrayOf<Any?>()
                        )
                    }
                    if (rand.nextInt(3) == 0) {
                        (GalacticraftCore.proxy)!!.spawnParticle(
                            "whiteSmokeTiny",
                            Vector3((x - 0.41f).toDouble(), y.toDouble(), (z + i).toDouble()),
                            Vector3(-0.0015, -0.015, 0.0),
                            arrayOf<Any?>()
                        )
                    }
                    if (rand.nextInt(3) == 0) {
                        (GalacticraftCore.proxy)!!.spawnParticle(
                            "whiteSmokeTiny",
                            Vector3((x + 0.537f).toDouble(), y.toDouble(), (z + i).toDouble()),
                            Vector3(0.0015, -0.015, 0.0),
                            arrayOf<Any?>()
                        )
                    }
                    i += 0.167f
                }
            }
        }
    }

    override fun getShiftDescription(meta: Int): String? {
        when (meta) {
            ELECTROLYZER_META -> return GCCoreUtil.translate("tile.electrolyzer.description")
            GAS_LIQUEFIER_META -> return GCCoreUtil.translate("tile.gas_liquefier.description")
            METHANE_SYNTHESIZER_META -> return GCCoreUtil.translate("tile.methane_synthesizer.description")
        }
        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        val type: EnumMachineType = EnumMachineType.entries[meta / 4]
        return this.getDefaultState().withProperty(FACING, enumfacing)
            .withProperty(TYPE, type)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING) as EnumFacing).getHorizontalIndex() + (state.getValue(
            TYPE
        ) as EnumMachineType).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        const val GAS_LIQUEFIER_META: Int = 0

        const val METHANE_SYNTHESIZER_META: Int = 4

        const val ELECTROLYZER_META: Int = 8

        @JvmField
        val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

        @JvmField
        val TYPE: PropertyEnum<EnumMachineType> =
            PropertyEnum.create<EnumMachineType>("type", EnumMachineType::class.java)
    }
}
