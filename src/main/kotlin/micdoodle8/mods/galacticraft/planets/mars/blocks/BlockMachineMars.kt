package micdoodle8.mods.galacticraft.planets.mars.blocks

import net.minecraftforge.common.ForgeChunkManager.Type

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.world.IChunkLoader
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityCryogenicChamber
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityTerraformer
import net.minecraft.block.SoundType
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
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
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random
import kotlin.math.floor

open class BlockMachineMars constructor(assetName: String?) : BlockTileGC(GCBlocks.machine), IShiftDescription,
    ISortableBlock, IPartialSealableBlock {
    enum class EnumMachineType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        TERRAFORMER(0, "terraformer"),
        CRYOGENIC_CHAMBER(1, "cryogenic_chamber"),
        LAUNCH_CONTROLLER(2, "launch_controller");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumMachineType {
                return entries[meta % 3]
            }
        }
    }

    init {
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val var9: TileEntity? = worldIn.getTileEntity(pos)

        if (var9 is IMultiBlock) {
            (var9 as IMultiBlock).onDestroy(var9)
        }

        super.breakBlock(worldIn, pos, state)
    }

    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val metadata: Int = getMetaFromState(state)

        val angle: Int = MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) and 3
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()

        worldIn.setBlockState(pos, getStateFromMeta((metadata and 12) + change), 3)

        when (metadata and 12) {
            CRYOGENIC_CHAMBER_METADATA -> BlockMulti.onPlacement(worldIn, pos, placer, this)
            LAUNCH_CONTROLLER_METADATA -> {
                WorldUtil.markAdjacentPadForUpdate(worldIn, pos)
                val var8: TileEntity? = worldIn.getTileEntity(pos)
                if (var8 is IChunkLoader && !worldIn.isRemote && ConfigManagerMars.Companion.launchControllerChunkLoad && placer is EntityPlayer) {
                    (var8 as IChunkLoader).setOwnerName(placer.getName())
                    (var8 as IChunkLoader).onTicketLoaded(
                        ForgeChunkManager.requestTicket(
                            GalacticraftCore.instance,
                            var8.getWorld(),
                            ForgeChunkManager.Type.NORMAL
                        ), true
                    )
                } else if (var8 is TileEntityLaunchController && placer is EntityPlayer) {
                    var8.setOwnerName(placer.getName())
                }
            }
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
        val type: EnumMachineType = (state)!!.getValue(TYPE) as EnumMachineType
        if (type == EnumMachineType.LAUNCH_CONTROLLER) {
            (playerIn)!!.openGui(
                GalacticraftPlanets.instance,
                GuiIdsPlanets.MACHINE_MARS,
                worldIn,
                (pos)!!.getX(),
                (pos)!!.getY(),
                (pos)!!.getZ()
            )
            return true
        } else if (type == EnumMachineType.CRYOGENIC_CHAMBER) {
            ((worldIn)!!.getTileEntity(pos) as IMultiBlock).onActivated(playerIn)
            return true
        } else {
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
        val type: EnumMachineType = state.getValue(TYPE) as EnumMachineType
        return if (type == EnumMachineType.CRYOGENIC_CHAMBER) BlockFaceShape.UNDEFINED else BlockFaceShape.SOLID
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        val type: EnumMachineType = state.getValue(TYPE) as EnumMachineType
        if (type == EnumMachineType.LAUNCH_CONTROLLER) {
            return TileEntityLaunchController()
        }
        if (type == EnumMachineType.CRYOGENIC_CHAMBER) {
            return TileEntityCryogenicChamber()
        } else {
            return TileEntityTerraformer()
        }
    }

    override fun removedByPlayer(
        state: IBlockState,
        world: World,
        pos: BlockPos,
        player: EntityPlayer,
        willHarvest: Boolean
    ): Boolean {
        if (getMetaFromState(world.getBlockState(pos)) >= LAUNCH_CONTROLLER_METADATA) {
            WorldUtil.markAdjacentPadForUpdate(world, pos)
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest)
    }

    open fun getTerraformer(): ItemStack {
        return ItemStack(this, 1, TERRAFORMER_METADATA)
    }

    open fun getChamber(): ItemStack {
        return ItemStack(this, 1, CRYOGENIC_CHAMBER_METADATA)
    }

    open fun getLaunchController(): ItemStack {
        return ItemStack(this, 1, LAUNCH_CONTROLLER_METADATA)
    }

    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(this.getTerraformer())
        list.add(this.getChamber())
        list.add(this.getLaunchController())
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumMachineType = state.getValue(TYPE) as EnumMachineType
        if (type == EnumMachineType.LAUNCH_CONTROLLER) {
            return LAUNCH_CONTROLLER_METADATA
        } else if (type == EnumMachineType.CRYOGENIC_CHAMBER) {
            return CRYOGENIC_CHAMBER_METADATA
        } else {
            return TERRAFORMER_METADATA
        }
    }

    override fun isBed(state: IBlockState, world: IBlockAccess, pos: BlockPos, player: Entity?): Boolean {
        return world.getBlockState(pos).getValue(TYPE) == EnumMachineType.CRYOGENIC_CHAMBER
    }

    override fun getBedSpawnPosition(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        player: EntityPlayer?
    ): BlockPos? {
        return pos.up()
    }

    override fun setBedOccupied(world: IBlockAccess, pos: BlockPos, player: EntityPlayer, occupied: Boolean) {
        val tile: TileEntity? = world.getTileEntity(pos)

        if (tile is TileEntityCryogenicChamber) {
            tile.isOccupied = true
        }
    }

    override fun getBedDirection(state: IBlockState, world: IBlockAccess, pos: BlockPos): EnumFacing {
        return state.getValue(FACING)
    }

    override fun getShiftDescription(meta: Int): String? {
        when (meta) {
            CRYOGENIC_CHAMBER_METADATA -> return GCCoreUtil.translate("tile.cryo_chamber.description")
            LAUNCH_CONTROLLER_METADATA -> return GCCoreUtil.translate("tile.launch_controller.description")
            TERRAFORMER_METADATA -> return GCCoreUtil.translate("tile.terraformer.description")
        }
        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        val type: EnumMachineType = EnumMachineType.Companion.byMetadata(floor(meta / 4.0).toInt())
        return this.getDefaultState().withProperty(FACING, enumfacing)
            .withProperty(TYPE, type)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getHorizontalIndex() + (state.getValue(TYPE) as EnumMachineType).getMeta() * 4
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, TYPE)
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(state: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        if (state.getValue(TYPE) == EnumMachineType.CRYOGENIC_CHAMBER) {
            GalacticraftPlanets.spawnParticle(
                "cryoFreeze",
                Vector3(
                    pos.getX() + 0.3 + rand.nextDouble() * 0.4,
                    pos.getY().toDouble(),
                    pos.getZ() + 0.3 + rand.nextDouble() * 0.4
                ),
                Vector3(0.0, 0.05 + rand.nextDouble() * 0.01, 0.0)
            )
            GalacticraftPlanets.spawnParticle(
                "cryoFreeze",
                Vector3(
                    pos.getX() + 0.3 + rand.nextDouble() * 0.4,
                    pos.getY().toDouble(),
                    pos.getZ() + 0.3 + rand.nextDouble() * 0.4
                ),
                Vector3(0.0, 0.05 + rand.nextDouble() * 0.01, 0.0)
            )
            GalacticraftPlanets.spawnParticle(
                "cryoFreeze",
                Vector3(
                    pos.getX() + 0.3 + rand.nextDouble() * 0.4,
                    pos.getY().toDouble(),
                    pos.getZ() + 0.3 + rand.nextDouble() * 0.4
                ),
                Vector3(0.0, 0.05 + rand.nextDouble() * 0.01, 0.0)
            )

            GalacticraftPlanets.spawnParticle(
                "cryoFreeze",
                Vector3(
                    pos.getX() + 0.3 + rand.nextDouble() * 0.4,
                    (pos.getY() + 2.9f).toDouble(),
                    pos.getZ() + 0.3 + rand.nextDouble() * 0.4
                ),
                Vector3(0.0, -0.05 - rand.nextDouble() * 0.01, 0.0)
            )
            GalacticraftPlanets.spawnParticle(
                "cryoFreeze",
                Vector3(
                    pos.getX() + 0.3 + rand.nextDouble() * 0.4,
                    (pos.getY() + 2.9f).toDouble(),
                    pos.getZ() + 0.3 + rand.nextDouble() * 0.4
                ),
                Vector3(0.0, -0.05 - rand.nextDouble() * 0.01, 0.0)
            )
            GalacticraftPlanets.spawnParticle(
                "cryoFreeze",
                Vector3(
                    pos.getX() + 0.3 + rand.nextDouble() * 0.4,
                    (pos.getY() + 2.9f).toDouble(),
                    pos.getZ() + 0.3 + rand.nextDouble() * 0.4
                ),
                Vector3(0.0, -0.05 - rand.nextDouble() * 0.01, 0.0)
            )
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    override fun isSealed(world: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return (world)!!.getBlockState(pos).getValue(TYPE) != EnumMachineType.CRYOGENIC_CHAMBER
    }

    companion object {
        const val TERRAFORMER_METADATA: Int = 0

        const val CRYOGENIC_CHAMBER_METADATA: Int = 4

        const val LAUNCH_CONTROLLER_METADATA: Int = 8

        @JvmField
        val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

        @JvmField
        val TYPE: PropertyEnum<EnumMachineType> =
            PropertyEnum.create<EnumMachineType>("type", EnumMachineType::class.java)
    }
}
