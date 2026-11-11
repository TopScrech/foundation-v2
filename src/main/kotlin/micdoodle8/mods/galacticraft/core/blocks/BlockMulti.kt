package micdoodle8.mods.galacticraft.core.blocks

import net.minecraft.network.chat.Component

import foundation.block.LegacyBlockMetadata
import foundation.block.getStateFromMeta

import java.util.List

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.world.level.block.state.properties.IntegerProperty as PropertyInteger
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.particle.ParticleManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult

import net.minecraft.world.level.Explosion
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import java.util.LinkedList
import java.util.Random

open class BlockMulti constructor(assetName: String?) : BlockAdvanced(GCBlocks.machine), IPartialSealableBlock,
    ITileEntityProvider, LegacyBlockMetadata {
    enum class EnumBlockMultiType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        SOLAR_PANEL_0(0, "solar"),
        SPACE_STATION_BASE(1, "ss_base"),
        ROCKET_PAD(2, "rocket_pad"),
        NASA_WORKBENCH(3, "nasa_workbench"),
        SOLAR_PANEL_1(4, "solar_panel"),
        CRYO_CHAMBER(5, "cryo_chamber"),
        BUGGY_FUEL_PAD(6, "buggy_pad"),
        MINER_BASE(7, "miner_base"),  //UNUSED
        DISH_LARGE(8, "dish_large");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockMultiType> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockMultiType? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
        this.setResistance(1000000000000000.0f)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        when (state.getValue(MULTI_TYPE) as EnumBlockMultiType) {
            EnumBlockMultiType.SOLAR_PANEL_0, EnumBlockMultiType.SOLAR_PANEL_1 -> {
                val midPole: Boolean = source.getBlockState(pos.above()).getBlock() === this
                val topPole: Boolean = source.getBlockState(pos.below()).getBlock() === this
                if (topPole || midPole) return if (midPole) AABB_SOLAR_POLE else AABB_SOLAR_TOP
                else return AABB_SOLAR
            }

            EnumBlockMultiType.ROCKET_PAD, EnumBlockMultiType.BUGGY_FUEL_PAD -> return AABB_PAD
            else -> return FULL_BLOCK_AABB
        }
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
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

    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    //    {
    //        int meta = getMetaFromState(worldIn.getBlockState(pos));
    //
    //        if (meta == 2 || meta == 6)
    //        {
    //            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.2F, 1.0F);
    //        }
    //        else if (meta == 0 || meta == 4)
    //        {
    //            this.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, worldIn.getBlockState(pos.up()).getBlock() == this ? 1.0F : 0.6F, 0.7F);
    //        }
    //        else
    //        {
    //            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    //        }
    //    }
    //    @SuppressWarnings("rawtypes")
    //    @Override
    //    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity)
    //    {
    //        int meta = getMetaFromState(state);
    //
    //        if (meta == 2 || meta == 6)
    //        {
    //            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.2F, 1.0F);
    //            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //        }
    //        else if (meta == 0 || meta == 4)
    //        {
    //            this.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, worldIn.getBlockState(pos.up()).getBlock() == this ? 1.0F : 0.6F, 0.7F);
    //            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //        }
    //        /*else if (meta == 7)
    //        {
    //            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.38F, 1.0F);
    //            super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
    //        }*/
    //        else
    //        {
    //            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //        }
    //    }
    //
    //    @Override
    //    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getCollisionBoundingBox(worldIn, pos, state);
    //    }
    //
    //    @Override
    //    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getSelectedBoundingBox(worldIn, pos);
    //    }
    override fun canDropFromExplosion(par1Explosion: Explosion): Boolean {
        return false
    }

    open fun makeFakeBlock(worldObj: World, pos: BlockPos, mainBlock: BlockPos?, meta: Int) {
        worldObj.setBlock(pos, (GCBlocks.fakeBlock)!!.getStateFromMeta(meta), if (meta == 5) 3 else 0)
        worldObj.setTileEntity(pos, TileEntityMulti(mainBlock))
    }

    open fun makeFakeBlock(
        worldObj: World,
        posList: MutableCollection<BlockPos>,
        mainBlock: BlockPos?,
        type: EnumBlockMultiType
    ) {
        for (pos: BlockPos in posList) {
            worldObj.setBlock(
                pos,
                this.defaultBlockState().setValue(MULTI_TYPE, type),
                if (type == EnumBlockMultiType.CRYO_CHAMBER) 3 else 0
            )
            worldObj.setTileEntity(pos, TileEntityMulti(mainBlock))
        }
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        val tileEntity: TileEntity? = worldIn.getBlockEntity(pos)

        if (tileEntity is TileEntityMulti) {
            val mainBlockPosition: BlockPos? = tileEntity.mainBlockPosition

            if (mainBlockPosition != null && mainBlockPosition != pos) {
                return worldIn.getBlockState(mainBlockPosition).getBlock().getBlockHardness(blockState, worldIn, pos)
            }
        }

        return this.blockHardness
    }

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        val metadata: Int = getMetaFromState((worldIn)!!.getBlockState(pos))

        //Landing pad and refueling pad
        if (metadata == 2 || metadata == 6) {
            return direction == EnumFacing.DOWN
        }

        //Basic solar panel fixed top
        if (metadata == 4) {
            return direction == EnumFacing.UP
        }

        return false
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileEntity: TileEntity? = worldIn.getBlockEntity(pos)

        if (tileEntity is TileEntityMulti) {
            tileEntity.onBlockRemoval()
        }

        super.breakBlock(worldIn, pos, state)
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
        val tileEntity: TileEntityMulti? = (worldIn)!!.getBlockEntity(pos) as TileEntityMulti?
        if (tileEntity == null) {
            return false
        }
        return tileEntity.onBlockActivated(worldIn, pos, playerIn)
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
        val tileEntity: TileEntityMulti? = (world)!!.getBlockEntity(pos) as TileEntityMulti?
        return tileEntity!!.onBlockWrenched((world)!!, pos, (entityPlayer)!!, (hand)!!, (side)!!, hitX, hitY, hitZ)
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    override fun quantityDropped(par1Random: Random): Int {
        return 0
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun createNewTileEntity(var1: World, meta: Int): TileEntity? {
        return null
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        val tileEntity: TileEntity? = world.getBlockEntity(pos)
        if (tileEntity is TileEntityMulti) {
            val mainBlockPosition: BlockPos? = tileEntity.mainBlockPosition

            if (mainBlockPosition != null && mainBlockPosition != pos) {
                val mainBlockState: IBlockState = world.getBlockState(mainBlockPosition)

                if (Blocks.AIR !== mainBlockState.getBlock()) {
                    return mainBlockState.getBlock()
                        .getPickBlock(mainBlockState, target, world, mainBlockPosition, player)
                }
            }
        }

        return ItemStack.EMPTY
    }

    override fun getBedDirection(state: IBlockState, world: IBlockAccess, pos: BlockPos): EnumFacing {
        val tileEntity: TileEntity? = world.getBlockEntity(pos)
        if (tileEntity is TileEntityMulti) {
            val mainBlockPosition: BlockPos? = tileEntity.mainBlockPosition

            if (mainBlockPosition != null && mainBlockPosition != pos) {
                val mainState: IBlockState = world.getBlockState(mainBlockPosition)
                return mainState.getBlock().getBedDirection(mainState, world, mainBlockPosition)
            }
        }

        return EnumFacing.UP // TODO
    }

    override fun isBed(state: IBlockState, world: IBlockAccess, pos: BlockPos, player: Entity?): Boolean {
        val tileEntity: TileEntity? = world.getBlockEntity(pos)
        if (tileEntity is TileEntityMulti) {
            val mainBlockPosition: BlockPos? = tileEntity.mainBlockPosition

            if (mainBlockPosition != null && mainBlockPosition != pos) {
                val mainState: IBlockState = world.getBlockState(mainBlockPosition)
                return mainState.getBlock().isBed(state, world, mainBlockPosition, player)
            }
        }

        return super.isBed(state, world, pos, player)
    }

    override fun setBedOccupied(world: IBlockAccess, pos: BlockPos, player: EntityPlayer, occupied: Boolean) {
        val tileEntity: TileEntity? = world.getBlockEntity(pos)
        val mainBlockPosition: BlockPos? = (tileEntity as TileEntityMulti).mainBlockPosition

        if (mainBlockPosition != null && mainBlockPosition != pos) {
            world.getBlockState(mainBlockPosition).getBlock().setBedOccupied(world, mainBlockPosition, player, occupied)
        } else {
            super.setBedOccupied(world, pos, player, occupied)
        }
    }

    override fun getBedSpawnPosition(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        player: EntityPlayer?
    ): BlockPos? {
        if (world !is World) {
            return null
        }
        var tries: Int = 3
        val worldIn: World = world
        val tileEntity: TileEntity? = worldIn.getBlockEntity(pos)
        val mainBlockPosition: BlockPos = ((tileEntity as TileEntityMulti).mainBlockPosition)!!
        val cryoChamber: IBlockState = worldIn.getBlockState(mainBlockPosition)
        var enumfacing: EnumFacing = EnumFacing.NORTH
        if (GalacticraftCore.isPlanetsLoaded && cryoChamber.getBlock() === MarsBlocks.machine) {
            enumfacing = cryoChamber.getValue(BlockMachineMars.FACING) as EnumFacing
        }
        val i: Int = pos.getX()
        val j: Int = pos.getY()
        val k: Int = pos.getZ()

        for (l: Int in 0..1) {
            val i1: Int = i - enumfacing.getStepX() * l - 1
            val j1: Int = k - enumfacing.getStepZ() * l - 1
            val k1: Int = i1 + 2
            val l1: Int = j1 + 2

            for (i2: Int in i1..k1) {
                for (j2: Int in j1..l1) {
                    val blockpos: BlockPos = BlockPos(i2, j, j2)

                    if (hasRoomForPlayer(worldIn, blockpos)) {
                        if (tries <= 0) {
                            return blockpos
                        }

                        --tries
                    }
                }
            }
        }

        return null
    }

    override fun addHitEffects(
        state: IBlockState,
        worldObj: World,
        target: RayTraceResult,
        manager: ParticleManager
    ): Boolean {
        val tileEntity: TileEntity? = worldObj.getBlockEntity(target.getBlockPos())

        if (tileEntity is TileEntityMulti) {
            val mainBlockPosition: BlockPos? = tileEntity.mainBlockPosition

            if (mainBlockPosition != null && mainBlockPosition != target.getBlockPos()) {
                manager.addBlockHitEffects(mainBlockPosition, target)
            }
        }

        return super.addHitEffects(state, worldObj, target, manager)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(
            MULTI_TYPE,
            (EnumBlockMultiType.Companion.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(MULTI_TYPE).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, MULTI_TYPE, RENDER_TYPE)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val type: EnumBlockMultiType = state.getValue(MULTI_TYPE)
        var renderType: Int = 0

        when (type) {
            EnumBlockMultiType.CRYO_CHAMBER -> {
                val stateAbove: IBlockState = worldIn.getBlockState(pos.above())
                val tile: TileEntityMulti? = worldIn.getBlockEntity(pos) as TileEntityMulti?
                if (stateAbove.getBlock() === this && (stateAbove.getValue(MULTI_TYPE)) == EnumBlockMultiType.CRYO_CHAMBER) {
                    renderType = 0
                } else {
                    renderType = 4
                }
                if (tile != null && tile.mainBlockPosition != null && GalacticraftCore.isPlanetsLoaded) {
                    val stateMain: IBlockState = worldIn.getBlockState(tile.mainBlockPosition)
                    if (stateMain.getBlock() === MarsBlocks.machine && stateMain.getValue(
                            BlockMachineMars.TYPE
                        ) == BlockMachineMars.EnumMachineType.CRYOGENIC_CHAMBER
                    ) {
                        val dir: EnumFacing = stateMain.getValue(BlockMachineMars.FACING)
                        renderType += dir.getHorizontalIndex()
                    }
                }
            }

            else -> {}
        }

        return state.setValue(RENDER_TYPE, renderType)
    }

    companion object {
        @JvmField
        val MULTI_TYPE: PropertyEnum<EnumBlockMultiType> =
            PropertyEnum.create<EnumBlockMultiType>("type", EnumBlockMultiType::class.java)

        @JvmField
        val RENDER_TYPE: PropertyInteger = PropertyInteger.create("rendertype", 0, 7)

        @JvmField
        protected val AABB_PAD: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0)

        @JvmField
        protected val AABB_SOLAR: AxisAlignedBB = AxisAlignedBB(0.0, 0.2, 0.0, 1.0, 0.8, 1.0)

        @JvmField
        protected val AABB_SOLAR_POLE: AxisAlignedBB = AxisAlignedBB(0.3, 0.0, 0.3, 0.7, 1.0, 0.7)

        @JvmField
        protected val AABB_SOLAR_TOP: AxisAlignedBB = AxisAlignedBB(0.3, 0.0, 0.3, 0.7, 0.6, 0.7)

        @JvmStatic
        private fun hasRoomForPlayer(worldIn: World, pos: BlockPos): Boolean {
            return worldIn.getBlockState(pos.below())
                .isSideSolid(worldIn, pos.below(), EnumFacing.UP) && !worldIn.getBlockState(pos).getMaterial()
                .isSolid() && !worldIn.getBlockState(pos.above()).getMaterial().isSolid()
        }

        @JvmStatic
        fun onPlacement(worldIn: World, pos: BlockPos, placer: EntityLivingBase?, callingBlock: Block) {
            val tile: TileEntity? = worldIn.getBlockEntity(pos)

            if (tile is IMultiBlock) {
                var validSpot: Boolean = true
                val toCheck: MutableList<BlockPos> = LinkedList<BlockPos>()
                (tile as IMultiBlock).getPositions(pos, (toCheck as MutableList<BlockPos?>?))
                for (toTest: BlockPos in toCheck) {
                    val blockAt: IBlockState = worldIn.getBlockState(toTest)
                    if (!blockAt.getBlock().isReplaceable(worldIn, toTest)) {
                        validSpot = false
                        break
                    }
                }

                if (!validSpot) {
                    worldIn.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())

                    if (!worldIn.isClientSide && placer is EntityPlayerMP) {
                        val player: EntityPlayerMP = placer
                        player.sendSystemMessage(Component.literal(EnumColor.RED.toString() + GCCoreUtil.translate("gui.warning.noroom")))
                        if (!player.capabilities.isCreativeMode) {
                            val nasaWorkbench: ItemStack = ItemStack(callingBlock, 1, 0)
                            val entityitem: EntityItem? = player.dropItem(nasaWorkbench, false)
                            entityitem!!.setPickupDelay(0)
                            entityitem.setOwner(player.getName())
                        }
                    }

                    return
                }

                (tile as IMultiBlock).onCreate(worldIn, pos)
            }
        }
    }
}
