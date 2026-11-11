package galaxyspace.core.prefab.blocks

import net.minecraft.network.chat.Component

import foundation.block.LegacyBlockMetadata
import foundation.block.getStateFromMeta

import galaxyspace.core.prefab.blocks.GSBlockMulti
import galaxyspace.core.GSBlocks
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvanced
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.world.level.block.state.properties.IntegerProperty as PropertyInteger
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.particle.ParticleManager
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

open class GSBlockMulti constructor() : BlockAdvanced(GCBlocks.machine), IPartialSealableBlock,
    ITileEntityProvider, LegacyBlockMetadata {
    enum class EnumBlockMultiType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ADVANCED_ROCKET_PAD(0, "advanced_rocket_pad"),
        ADVANCED_ROCKET_PAD_1(1, "advanced_rocket_pad_1");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockMultiType? {
                return entries[meta]
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey("fake_block")
        this.setResistance(Int.MAX_VALUE.toFloat())
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        when (state.getValue(MULTI_TYPE) as EnumBlockMultiType) {
            EnumBlockMultiType.ADVANCED_ROCKET_PAD, EnumBlockMultiType.ADVANCED_ROCKET_PAD_1 -> return AABB_PAD
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

    override fun canDropFromExplosion(par1Explosion: Explosion): Boolean {
        return false
    }

    @Deprecated("")
    open fun makeFakeBlock(worldObj: World, pos: BlockPos, mainBlock: BlockPos?, meta: Int) {
        worldObj.setBlock(pos, GSBlocks.FAKE_BLOCK.getStateFromMeta(meta), 0)
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
                0
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
        return tileEntity!!.onBlockWrenched(world, pos, (entityPlayer)!!, (hand)!!, (side)!!, hitX, hitY, hitZ)
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
        val renderType: Int = 0

        when (type) {
            else -> {}
        }

        return state.setValue(RENDER_TYPE, renderType)
    }

    companion object {
        @JvmField
        val MULTI_TYPE: PropertyEnum<EnumBlockMultiType> =
            PropertyEnum.create<EnumBlockMultiType>("type", EnumBlockMultiType::class.java)

        @JvmField
        val RENDER_TYPE: PropertyInteger = PropertyInteger.create("rendertype", 0, 1)

        @JvmField
        protected val AABB_PAD: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0)

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
