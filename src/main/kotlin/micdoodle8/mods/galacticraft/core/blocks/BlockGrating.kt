package micdoodle8.mods.galacticraft.core.blocks

import java.util.Set

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.JavaUtil
import micdoodle8.mods.galacticraft.core.util.PropertyObject
import net.minecraft.block.Block
import net.minecraft.block.BlockDoor
import net.minecraft.block.BlockDynamicLiquid
import net.minecraft.block.BlockLiquid
import net.minecraft.block.SoundType
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.Random
import java.util.function.Supplier

open class BlockGrating : Block, ISortableBlock, IPartialSealableBlock {
    private var adjacentSourceBlocks = 0
    private val liquidEquivalentDynamic: IBlockState?
    private val liquidEquivalentStatic: IBlockState?
    private var forgeFluid = false
    private var forgeBlock: BlockFluidBase? = null
    private val replaceWithFluidIntended: ThreadLocal<Boolean> =
        ThreadLocal.withInitial<Boolean>(Supplier { false })

    constructor(assetName: String?, material: Material) : super(material) {
        if (this.material === Material.LAVA) {
            this.liquidEquivalentStatic = Blocks.LAVA.getDefaultState()
            this.liquidEquivalentDynamic = Blocks.FLOWING_LAVA.getDefaultState()
            this.setTickRandomly(true)
        } else if (this.material === Material.WATER) {
            this.liquidEquivalentStatic = Blocks.WATER.getDefaultState()
            this.liquidEquivalentDynamic = Blocks.FLOWING_WATER.getDefaultState()
            this.setTickRandomly(true)
        } else {
            this.liquidEquivalentStatic = null
            this.liquidEquivalentDynamic = null
        }
        this.setHardness(0.5f)
        this.blockResistance = 15f
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    constructor(forge: Block) : super(forge.getMaterial(forge.getDefaultState())) {
        this.forgeFluid = true
        this.forgeBlock = forge as BlockFluidBase
        this.liquidEquivalentStatic = null
        this.liquidEquivalentDynamic = null
        this.setHardness(0.5f)
        this.blockResistance = 15f
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey("grating" + number)
        number++
    }

    override fun getLocalizedName(): String {
        return I18n.translateToLocal("tile.grating.name")
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.DECORATION
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
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

    override fun isSealed(world: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return false
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

    @SideOnly(Side.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        return true
    }

    override fun getBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return BOUNDING_BOX
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        if (JavaUtil.instance.isCalledBy(BlockDynamicLiquid::class.java, BlockFluidBase::class.java)) {
            return Item.getItemFromBlock(Blocks.AIR)
        }
        Thread.dumpStack()
        return Item.getItemFromBlock(GCBlocks.grating)
    }

    override fun getItem(worldIn: World, pos: BlockPos, state: IBlockState): ItemStack? {
        return ItemStack(Item.getItemFromBlock(GCBlocks.grating), 1, 0)
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(GCBlocks.grating), 1, 0)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockGratingExtendedState(
            this,
            arrayOf<IProperty<*>?>(BlockLiquid.LEVEL, BlockFluidBase.LEVEL),
            arrayOf<IUnlistedProperty<*>?>(BASE_STATE)
        )
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(BlockLiquid.LEVEL, meta)
            .withProperty(BlockFluidBase.LEVEL, meta)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BlockLiquid.LEVEL) as Int)
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        savedBlockAccess = world
        savedPos = pos
        return (state as IExtendedBlockState).withProperty(BASE_STATE, this.getLiquidBlock(state))
    }

    open fun getLiquidBlock(state: IBlockState): IBlockState? {
        if (this.forgeFluid && state.getPropertyKeys().contains(BlockFluidBase.LEVEL)) {
            val level: Int = state.getValue(BlockFluidBase.LEVEL)
            return this.forgeBlock!!.getDefaultState().withProperty(BlockFluidBase.LEVEL, level)
        } else if (this.material === Material.WATER || this.material === Material.LAVA) {
            val level: Int = state.getValue(BlockLiquid.LEVEL)
            return this.liquidEquivalentStatic!!.withProperty(BlockLiquid.LEVEL, level)
        }

        return null
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        val newBlock: IBlockState = world.getBlockState(pos)
        if (newBlock.getBlock() is BlockGrating) {
            return
        }
        if (Blocks.AIR === newBlock.getBlock() && this !== GCBlocks.grating) {
            world.setBlockState(pos, this.getLiquidBlock(state))
            this.dropBlockAsItem(world, pos, state, 0)
        }
        if (this.replaceWithFluidIntended.get()) {
            // Intended replacement with fluid block, called from this.removedByPlayer()
            this.replaceWithFluidIntended.set(false)
            return
        }
        if (newBlock.getBlock() is BlockFluidBase) {
            for (b: BlockGrating in forgeBlocks) {
                if (newBlock.getBlock() === b.forgeBlock) {
                    val level: Int = newBlock.getValue(BlockFluidBase.LEVEL)
                    val bs: IBlockState = b.getDefaultState().withProperty(BlockFluidBase.LEVEL, level)
                        .withProperty(BlockLiquid.LEVEL, level)
                    world.setBlockState(pos, bs)
                    return
                }
            }
            //If not a known Forge fluid block, drop grating item
            spawnAsEntity(world, pos, ItemStack(GCBlocks.grating))
            return
        }
        if (newBlock.getBlock() is BlockLiquid) {
            if (newBlock.getMaterial() === Material.WATER) {
                val level: Int = newBlock.getValue(BlockLiquid.LEVEL)
                val bs: IBlockState =
                    (GCBlocks.gratingWater)!!.getDefaultState().withProperty(BlockLiquid.LEVEL, level)
                world.setBlockState(pos, bs)
            } else if (newBlock.getMaterial() === Material.LAVA) {
                val level: Int = newBlock.getValue(BlockLiquid.LEVEL)
                val bs: IBlockState =
                    (GCBlocks.gratingLava)!!.getDefaultState().withProperty(BlockLiquid.LEVEL, level)
                world.setBlockState(pos, bs)
            }
        }
    }

    override fun removedByPlayer(
        state: IBlockState,
        world: World,
        pos: BlockPos,
        player: EntityPlayer,
        willHarvest: Boolean
    ): Boolean {
        this.onBlockHarvested(world, pos, state, player)
        if (this === GCBlocks.grating) {
            return world.setBlockState(pos, Blocks.AIR.getDefaultState(), if (world.isRemote) 11 else 3)
        }
        this.replaceWithFluidIntended.set(true)
        return world.setBlockState(pos, this.getLiquidBlock(state), if (world.isRemote) 11 else 3)
    }

    override fun getStateForPlacement(
        world: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase,
        hand: EnumHand
    ): IBlockState {
        val oldBlock: IBlockState = world.getBlockState(pos)
        if (oldBlock.getBlock() is BlockGrating) {
            return this.getStateFromMeta(meta)
        }
        if (oldBlock.getBlock() is BlockFluidBase) {
            for (b: BlockGrating in forgeBlocks) {
                if (oldBlock.getBlock() === b.forgeBlock) {
                    val level: Int = oldBlock.getValue(BlockFluidBase.LEVEL)
                    return b.getDefaultState().withProperty(BlockFluidBase.LEVEL, level)
                        .withProperty(BlockLiquid.LEVEL, level)
                }
            }
        } else if (oldBlock.getBlock() is BlockLiquid) {
            if (oldBlock.getMaterial() === Material.WATER) {
                val level: Int = oldBlock.getValue(BlockLiquid.LEVEL)
                return (GCBlocks.gratingWater)!!.getDefaultState().withProperty(BlockLiquid.LEVEL, level)
            } else if (oldBlock.getMaterial() === Material.LAVA) {
                val level: Int = oldBlock.getValue(BlockLiquid.LEVEL)
                return (GCBlocks.gratingLava)!!.getDefaultState().withProperty(BlockLiquid.LEVEL, level)
            }
        }
        return this.getStateFromMeta(meta)
    }

    override fun getMaterial(state: IBlockState): Material {
        //Disable conversion to plain stone (lava above water in BlockDynamicLiquid.updateTick) - also allows water centre source block to form above this
        //also allow endermen and others to teleport onto this (EntityLivingBase) and players to spawn on this (World)
        val found: Int = JavaUtil.instance.isCalledBySecond(
            BlockDynamicLiquid::class.java,
            EntityLivingBase::class.java,
            World::class.java
        )
        if (found > 0) {
            if (found > 1 || JavaUtil.instance.isCalledByThird(WorldServer::class.java)) {
                return Material.IRON
            }
        }

        return this.material
    }

    override fun isReplaceable(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        return false
    }

    // Pistons cannot move this
    public override fun getPushReaction(state: IBlockState?): EnumPushReaction {
        return EnumPushReaction.BLOCK
    }

    override fun isPassable(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        return false
    }

    override fun canCollideCheck(state: IBlockState, hitIfLiquid: Boolean): Boolean {
        return true
    }

    //---------------From BlockDynamicLiquid
    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        var state: IBlockState = state
        if (!this.material.isLiquid()) return
        if (this.forgeFluid) {
            if (this.forgeBlock is BlockFluidClassic) {
                this.updateTickForge(worldIn, pos, state, rand)
            }
            return
        }

        var i: Int = (state.getValue(BlockLiquid.LEVEL) as Int)
        var j: Int = 1

        if (this.material === Material.LAVA && !worldIn.provider.doesWaterVaporize()) {
            j = 2
        }

        var k: Int = this.tickRate(worldIn)

        if (i > 0) {
            var l: Int = -100
            this.adjacentSourceBlocks = 0

            for (enumfacing: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
                l = this.checkAdjacentBlock(worldIn, pos.offset(enumfacing), l)
            }

            var i1: Int = l + j

            if (i1 >= 8 || l < 0) {
                i1 = -1
            }

            val j1: Int = this.getDepth(worldIn.getBlockState(pos.up()))

            if (j1 >= 0) {
                if (j1 >= 8) {
                    i1 = j1
                } else {
                    i1 = j1 + 8
                }
            }

            if (this.adjacentSourceBlocks >= 2 && ForgeEventFactory.canCreateFluidSource(
                    worldIn,
                    pos,
                    state,
                    this.material === Material.WATER
                )
            ) {
                val iblockstate: IBlockState = worldIn.getBlockState(pos.down())

                if (iblockstate.getMaterial().isSolid()) {
                    i1 = 0
                } else if (iblockstate.getMaterial() === this.material && (iblockstate.getValue(BlockLiquid.LEVEL) as Int) == 0) {
                    i1 = 0
                }
            }

            if (this.material === Material.LAVA && i < 8 && i1 < 8 && i1 > i && rand.nextInt(4) != 0) {
                k *= 4
            }

            if (i1 == i) {
//                this.placeStaticBlock(worldIn, pos, state);
            } else {
                i = i1

                if (i1 < 0) {
                    worldIn.setBlockState(pos, (GCBlocks.grating)!!.getDefaultState(), 3)
                } else {
                    state = state.withProperty(BlockLiquid.LEVEL, i1)
                    worldIn.setBlockState(pos, state, 2)
                    worldIn.scheduleUpdate(pos, this, k)
                    worldIn.notifyNeighborsOfStateChange(pos, this, false)
                }
            }
        } else {
//            this.placeStaticBlock(worldIn, pos, state);
        }

        val down: BlockPos = pos.down()
        val iblockstate1: IBlockState = worldIn.getBlockState(down)

        if (this.canFlowInto(worldIn, down, iblockstate1)) {
            if (this.material === Material.LAVA && iblockstate1.getMaterial() === Material.WATER && (iblockstate1.getBlock() !is BlockGrating)) {
                worldIn.setBlockState(down, Blocks.STONE.getDefaultState())
                return
            }

            if (i >= 8) {
                this.tryFlowInto(worldIn, down, iblockstate1, i)
            } else {
                this.tryFlowInto(worldIn, down, iblockstate1, i + 8)
            }
        } else if (i >= 0 && (i == 0 || this.isBlocked(worldIn, down, iblockstate1))) {
            val set: MutableSet<EnumFacing> = this.getPossibleFlowDirections(worldIn, pos)
            var k1: Int = i + j

            if (i >= 8) {
                k1 = 1
            }

            if (k1 >= 8) {
                return
            }

            for (enumfacing1: EnumFacing in set) {
                this.tryFlowInto(worldIn, pos.offset(enumfacing1), worldIn.getBlockState(pos.offset(enumfacing1)), k1)
            }
        }
    }

    private fun tryFlowInto(worldIn: World, pos: BlockPos, state: IBlockState, level: Int) {
        if (this.canFlowInto(worldIn, pos, state)) {
            if (state.getMaterial() !== Material.AIR && state.getBlock() !is BlockGrating) {
                if (this.material === Material.LAVA) {
                } else {
                    if (state.getBlock() !== Blocks.SNOW_LAYER) state.getBlock().dropBlockAsItem(worldIn, pos, state, 0)
                }
            }

            worldIn.setBlockState(
                pos,
                this.liquidEquivalentDynamic!!.withProperty(BlockLiquid.LEVEL, level),
                3
            )
        }
    }

    private fun getSlopeDistance(worldIn: World, pos: BlockPos, distance: Int, calculateFlowCost: EnumFacing): Int {
        var i: Int = 1000

        for (enumfacing: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing != calculateFlowCost) {
                val blockpos: BlockPos = pos.offset(enumfacing)
                val iblockstate: IBlockState = worldIn.getBlockState(blockpos)

                if (!this.isBlocked(
                        worldIn,
                        blockpos,
                        iblockstate
                    ) && (iblockstate.getMaterial() !== this.material || (iblockstate.getValue(BlockLiquid.LEVEL) as Int) > 0)
                ) {
                    if (!this.isBlocked(worldIn, blockpos.down(), iblockstate)) {
                        return distance
                    }

                    if (distance < this.getSlopeFindDistance(worldIn)) {
                        val j: Int = this.getSlopeDistance(worldIn, blockpos, distance + 1, enumfacing.getOpposite())

                        if (j < i) {
                            i = j
                        }
                    }
                }
            }
        }

        return i
    }

    private fun getSlopeFindDistance(worldIn: World): Int {
        return if (this.material === Material.LAVA && !worldIn.provider.doesWaterVaporize()) 2 else 4
    }

    /**
     * This method returns a Set of EnumFacing
     */
    private fun getPossibleFlowDirections(worldIn: World, pos: BlockPos): MutableSet<EnumFacing> {
        var i: Int = 1000
        val set: MutableSet<EnumFacing> = EnumSet.noneOf<EnumFacing>(EnumFacing::class.java)

        for (enumfacing: EnumFacing in EnumFacing.Plane.HORIZONTAL) {
            val blockpos: BlockPos = pos.offset(enumfacing)
            val iblockstate: IBlockState = worldIn.getBlockState(blockpos)

            if (!this.isBlocked(
                    worldIn,
                    blockpos,
                    iblockstate
                ) && (iblockstate.getMaterial() !== this.material || (iblockstate.getValue(BlockLiquid.LEVEL) as Int) > 0)
            ) {
                val j: Int

                if (this.isBlocked(worldIn, blockpos.down(), worldIn.getBlockState(blockpos.down()))) {
                    j = this.getSlopeDistance(worldIn, blockpos, 1, enumfacing.getOpposite())
                } else {
                    j = 0
                }

                if (j < i) {
                    set.clear()
                }

                if (j <= i) {
                    set.add(enumfacing)
                    i = j
                }
            }
        }

        return set
    }

    private fun isBlocked(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        val bs: IBlockState = worldIn.getBlockState(pos)
        val block: Block = bs.getBlock()
        return if ((block !is BlockDoor) && block !== Blocks.STANDING_SIGN && block !== Blocks.LADDER && block !== Blocks.REEDS) (if (block.getMaterial(
                bs
            ) !== Material.PORTAL && block.getMaterial(bs) !== Material.STRUCTURE_VOID
        ) block.getMaterial(bs).blocksMovement() else true) else true
    }

    protected open fun checkAdjacentBlock(worldIn: World, pos: BlockPos, currentMinLevel: Int): Int {
        var i: Int = this.getDepth(worldIn.getBlockState(pos))

        if (i < 0) {
            return currentMinLevel
        } else {
            if (i == 0) {
                ++this.adjacentSourceBlocks
            }

            if (i >= 8) {
                i = 0
            }

            return if (currentMinLevel >= 0 && i >= currentMinLevel) currentMinLevel else i
        }
    }

    private fun canFlowInto(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        val material: Material = state.getMaterial()
        return material !== this.material && material !== Material.LAVA && !this.isBlocked(worldIn, pos, state)
    }

    //---------------From BlockLiquid etc
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (this.material.isLiquid()) {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (this.material.isLiquid()) {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
        }
    }

    override fun tickRate(worldIn: World): Int {
        if (this.forgeFluid) {
            return this.forgeBlock!!.tickRate(worldIn)
        }
        return if (this.material === Material.WATER) 5 else (if (this.material === Material.LAVA) (if (worldIn.provider.isNether()) 10 else 30) else 0)
    }

    protected open fun getDepth(p_189542_1_: IBlockState): Int {
        return if (p_189542_1_.getMaterial() === this.material) (p_189542_1_.getValue(BlockLiquid.LEVEL) as Int) else -1
    }

    //------------------From BlockFluidBase
    open fun updateTickForge(world: World, pos: BlockPos, state: IBlockState, rand: Random?) {
//        if (!isSourceBlock(world, pos) && ForgeEventFactory.canCreateFluidSource(world, pos, state, false))
//        {
//            int adjacentSourceBlocks =
//                    (isSourceBlock(world, pos.north()) ? 1 : 0) +
//                    (isSourceBlock(world, pos.south()) ? 1 : 0) +
//                    (isSourceBlock(world, pos.east()) ? 1 : 0) +
//                    (isSourceBlock(world, pos.west()) ? 1 : 0);
//            if (adjacentSourceBlocks >= 2 && (world.getBlockState(pos.down(1)).getMaterial().isSolid() || isSourceBlock(world, pos.down(1))))
//                world.setBlockState(pos, state.withProperty(BlockFluidBase.LEVEL, 0));
//        }

        var quantaPerBlock: Int = 8
        try {
            quantaPerBlock = fieldQuantaPerBlock!!.getInt(this.forgeBlock)
        } catch (e: Exception) {
        }
        val tickRate: Int = this.forgeBlock!!.tickRate(world)
        var quantaRemaining: Int = quantaPerBlock - state.getValue(BlockFluidBase.LEVEL)
        var expQuanta: Int = -101

        // check adjacent block levels if non-source
        if (quantaRemaining < quantaPerBlock) {
            if (testSame(world, pos.add(0, 1, 0)) ||
                testSame(world, pos.add(-1, 1, 0)) ||
                testSame(world, pos.add(1, 1, 0)) ||
                testSame(world, pos.add(0, 1, -1)) ||
                testSame(world, pos.add(0, 1, 1))
            ) {
                expQuanta = quantaPerBlock - 1
            } else {
                var maxQuanta: Int = -100
                maxQuanta = getLargerQuanta(world, pos.add(-1, 0, 0), maxQuanta)
                maxQuanta = getLargerQuanta(world, pos.add(1, 0, 0), maxQuanta)
                maxQuanta = getLargerQuanta(world, pos.add(0, 0, -1), maxQuanta)
                maxQuanta = getLargerQuanta(world, pos.add(0, 0, 1), maxQuanta)

                expQuanta = maxQuanta - 1
            }

            // decay calculation
            if (expQuanta != quantaRemaining) {
                quantaRemaining = expQuanta

                if (expQuanta <= 0) {
                    world.setBlockState(pos, (GCBlocks.grating)!!.getDefaultState(), 3)
                } else {
                    world.setBlockState(
                        pos,
                        state.withProperty(BlockFluidBase.LEVEL, quantaPerBlock - expQuanta),
                        2
                    )
                    world.scheduleUpdate(pos, this, tickRate)
                    world.notifyNeighborsOfStateChange(pos, this.forgeBlock, false)
                }
            }
        } else if (quantaRemaining >= quantaPerBlock) {
            world.setBlockState(pos, this.getDefaultState(), 2)
        }

        // Flow vertically if possible
        if (this.forgeBlock!!.canDisplace(world, pos.down(1))) {
            flowIntoBlock(world, pos.down(1), 1)
            return
        }

        // Flow outward if possible
        var flowMeta: Int = quantaPerBlock - quantaRemaining + 1
        if (flowMeta >= quantaPerBlock) {
            return
        }

        if (isSourceBlock(world, pos) || !(this.forgeBlock as BlockFluidClassic).isFlowingVertically(world, pos)) {
            val test: Block = world.getBlockState(pos.up(1)).getBlock()
            if (test === this || test === this.forgeBlock) {
                flowMeta = 1
            }

            val flowTo: BooleanArray?
            try {
                flowTo = gofd!!.invoke(this.forgeBlock, world, pos) as BooleanArray?

                if (flowTo!![0]) flowIntoBlock(world, pos.add(-1, 0, 0), flowMeta)
                if (flowTo[1]) flowIntoBlock(world, pos.add(1, 0, 0), flowMeta)
                if (flowTo[2]) flowIntoBlock(world, pos.add(0, 0, -1), flowMeta)
                if (flowTo[3]) flowIntoBlock(world, pos.add(0, 0, 1), flowMeta)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun testSame(world: World, pos: BlockPos): Boolean {
        val test: Block = world.getBlockState(pos).getBlock()
        return test === this || test === this.forgeBlock
    }

    private fun isSourceBlock(world: IBlockAccess, pos: BlockPos): Boolean {
        val state: IBlockState = world.getBlockState(pos)
        return state.getBlock() is BlockFluidBase && state.getValue(BlockFluidBase.LEVEL) == 0
    }

    protected open fun getLargerQuanta(world: IBlockAccess?, pos: BlockPos?, compare: Int): Int {
        val quantaRemaining: Int = this.forgeBlock!!.getQuantaValue(world, pos)
        if (quantaRemaining <= 0) {
            return compare
        }
        return if (quantaRemaining >= compare) quantaRemaining else compare
    }

    protected open fun flowIntoBlock(world: World, pos: BlockPos, meta: Int) {
        if (meta < 0) return
        if (this.forgeBlock!!.displaceIfPossible(world, pos)) {
            world.setBlockState(
                pos,
                this.forgeBlock!!.getBlockState().getBaseState().withProperty(BlockFluidBase.LEVEL, meta),
                3
            )
        }
    }

    companion object {
        @JvmField
        val BASE_STATE: PropertyObject<IBlockState?> =
            PropertyObject<IBlockState?>("held_state", IBlockState::class.java)

        @JvmField
        protected val BOUNDING_BOX: AxisAlignedBB = AxisAlignedBB(0.0, 0.875, 0.0, 1.0, 1.0, 1.0)

        @JvmField
        var savedBlockAccess: IBlockAccess? = null

        @JvmField
        var savedPos: BlockPos? = null

        @JvmField
        var forgeBlocks: MutableList<BlockGrating> = ArrayList<BlockGrating>()

        @JvmField
        var number: Int = 3 //That's Plain, Water, Lava - any more will be Forge Fluids
        private var gofd: Method? = null
        private var fieldQuantaPerBlock: Field? = null

        init {
            try {
                gofd = BlockFluidClassic::class.java.getDeclaredMethod(
                    "getOptimalFlowDirections",
                    World::class.java,
                    BlockPos::class.java
                )
                (gofd)!!.setAccessible(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                fieldQuantaPerBlock = BlockFluidBase::class.java.getDeclaredField("quantaPerBlock")
                (fieldQuantaPerBlock)!!.setAccessible(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun createForgeFluidVersion(b: Block?, blockRegistry: IForgeRegistry<Block?>) {
            if (b is BlockFluidBase) {
                val grating: BlockGrating = BlockGrating(b)
                grating.setLightLevel(b.getLightValue(b.getDefaultState()) / 15f)
                forgeBlocks.add(grating)
                GCBlocks.registerBlock(grating, null)
                blockRegistry.register(grating)
            }
        }

        @JvmStatic
        fun createForgeFluidVersions(blockRegistry: IForgeRegistry<Block?>) {
            val it: MutableIterator<Block?> = REGISTRY.iterator()
            while (it.hasNext()) {
                val test: Block? = it.next()
                if (test is BlockFluidBase) {
                    createForgeFluidVersion(test, blockRegistry)
                }
            }
        }

        @JvmStatic
        fun remapForgeVariants() {
            for (b: Block in forgeBlocks) {
                remapVariant(b)
            }
        }

        @JvmStatic
        @SideOnly(Side.CLIENT)
        fun remapVariant(grating: Block) {
            ModelLoader.setCustomStateMapper(grating, object : StateMapperBase() {
                override fun getModelResourceLocation(ignore: IBlockState): ModelResourceLocation {
                    return ModelResourceLocation("galacticraftcore:grating", "normal")
                }
            })
        }
    }
}
