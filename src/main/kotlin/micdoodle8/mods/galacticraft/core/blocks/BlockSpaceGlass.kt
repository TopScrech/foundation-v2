package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.item.IPaintable
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.JavaUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.Property as IProperty
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.Random

open class BlockSpaceGlass constructor(
    assetName: String?, //public static final PropertyInteger PLACING  = PropertyInteger.create("placing", 0, 2);
    //This will define whether originally placed by the player facing NS - EW - or UD
    @JvmField val type: GlassType, //frameValue corresponds to the damage of the placing item
    private val frame: GlassFrame, base: Block?
) : Block(Material.GLASS), IPartialSealableBlock, IShiftDescription, ISortableBlock, IPaintable, LegacyBlockMetadata {
    @JvmField
    var color: Int = 0xFFFFFF
    private val baseBlock: Block
    private val isClient: Boolean
    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(MODEL, ROTATION))
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        //The plain block variety produces items carrying all variants as damage values
        //Other block varieties have no corresponding ItemBlock (see registration in GCBlocks)
        if (this.frame == GlassFrame.PLAIN) {
            for (i: Int in GlassFrame.entries.toTypedArray().indices) list.add(ItemStack(this, 1, i))
        }
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.DECORATION
    }

    override fun getStateForPlacement(
        worldIn: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase
    ): IBlockState {
        //This allows the different item damage values to place different instances of the block - the instances of the block are constructed in GCBlocks
        var meta: Int = meta
        if (meta >= GlassFrame.entries.size) meta = 0
        when (GlassFrame.entries[meta]) {
            GlassFrame.TIN_DECO -> {
                when (this.type) {
                    GlassType.STRONG -> return (GCBlocks.spaceGlassTinStrong)!!.defaultBlockState()
                    GlassType.VANILLA -> return (GCBlocks.spaceGlassTinVanilla)!!.defaultBlockState()
                    else -> return (GCBlocks.spaceGlassTinClear)!!.defaultBlockState()
                }
            }

            GlassFrame.PLAIN -> return this.defaultBlockState()
            else -> return this.defaultBlockState()
        }
        //TODO: Add PLACING direction
    }

    override fun damageDropped(state: IBlockState): Int {
        return this.frame.ordinal
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(this.baseBlock)
        //TODO: override getItemDropped() to return null if we make a broken Space Glass variant...
        // (if doing that, also update EntityAstroMiner.getPickBlock()
    }

    override fun getDrops(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ): MutableList<ItemStack?> {
        val ret: ArrayList<ItemStack?> = ArrayList<ItemStack?>(1)
        ret.add(ItemStack(this.getItemDropped(state, Random(0), fortune), 1, this.frame.ordinal))
        return ret
    }

    override fun canSilkHarvest(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Boolean {
        return true
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
        return BlockFaceShape.UNDEFINED
    }

    override fun isSealed(world: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return (direction)!!.ordinal > 1
    }

    override fun getMaterial(state: IBlockState): Material {
        if (this.isClient && JavaUtil.instance.isCalledBySpecific((clazz)!!)) {
            return Material.WATER
        }
        return this.material
    }

    @OnlyIn(Dist.CLIENT)
    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return true
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.TRANSLUCENT
    }

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.SOLID
    }

    override fun addCollisionBoxToList(
        state: IBlockState,
        worldIn: World,
        pos: BlockPos,
        mask: AxisAlignedBB,
        list: MutableList<AxisAlignedBB?>,
        entityIn: Entity?,
        p_185477_7_: Boolean
    ) {
        val above: IBlockState = worldIn.getBlockState(pos.above())
        val below: IBlockState = worldIn.getBlockState(pos.below())
        val north: IBlockState = worldIn.getBlockState(pos.north())
        val south: IBlockState = worldIn.getBlockState(pos.south())
        val west: IBlockState = worldIn.getBlockState(pos.west())
        val east: IBlockState = worldIn.getBlockState(pos.east())

        val connectN: Boolean = this.canPaneConnectToBlock(north, state)
        val connectS: Boolean = this.canPaneConnectToBlock(south, state)
        val connectW: Boolean = this.canPaneConnectToBlock(west, state)
        val connectE: Boolean = this.canPaneConnectToBlock(east, state)

        val plateD: Boolean = this.buildSolidSide(below, state)
        val plateU: Boolean = this.buildSolidSide(above, state)
        var plateN: Boolean = this.buildSolidSide(north, state)
        var plateS: Boolean = this.buildSolidSide(south, state)
        var plateW: Boolean = this.buildSolidSide(west, state)
        var plateE: Boolean = this.buildSolidSide(east, state)

        //No plate on the sides which glass is facing
        if (connectN || connectS) {
            plateE = false
            plateW = plateE
        }
        if (connectW || connectE) {
            plateS = false
            plateN = plateS
        }

        //Singleton
        if (!connectE && !connectW && !connectN && !connectS) {
            var prefEW: Boolean = false
            if (below.getBlock() === this) {
                prefEW = (isConnectedEW(below, worldIn, pos.below()) || isPreferenceEW(below, worldIn, pos.below()))
            } else if (above.getBlock() === this) {
                prefEW = (isConnectedEW(above, worldIn, pos.above()) || isPreferenceEW(above, worldIn, pos.above()))
            }

            if (this.isPreferenceEW(state, worldIn, pos)) prefEW = true

            if (prefEW) {
                plateS = false
                plateN = plateS
            } else {
                plateE = false
                plateW = plateE
            }
        }

        val posGlass: Double = 0.375
        val posBase: Double = 0.225
        var xMin: Double = if (plateU || plateD || plateN || plateS) posBase else posGlass
        var xMax: Double = if (plateU || plateD || plateN || plateS) 1.0 - posBase else 1.0 - posGlass
        var yMin: Double = 0.0 //plateE || plateW || plateN || plateS ? posBase : 0.375D;
        var yMax: Double = 1.0 //plateE || plateW || plateN || plateS ? 0.775D : 0.625D;
        var zMin: Double = if (plateU || plateD || plateE || plateW) posBase else posGlass
        var zMax: Double = if (plateU || plateD || plateE || plateW) 1.0 - posBase else 1.0 - posGlass

        if (plateW || connectW) xMin = 0.0
        if (plateE || connectE) xMax = 1.0
        if (plateN || connectN) zMin = 0.0
        if (plateS || connectS) zMax = 1.0

        //Special for corner diagonals
        if ((connectW xor connectE) && (connectN xor connectS) && !(plateU && plateD)) {
            val diag: Double = 0.25
            if (connectW) {
                xMin = diag - 0.01
                xMax = diag
            } else {
                xMin = 1.0 - diag
                xMax = 1.01 - diag
            }
            if (connectN) {
                zMin = diag - 0.01
                zMax = diag
            } else {
                zMin = 1.0 - diag
                zMax = 1.01 - diag
            }
        }

        xMin += pos.getX().toDouble()
        xMax += pos.getX().toDouble()
        yMin += pos.getY().toDouble()
        yMax += pos.getY().toDouble()
        zMin += pos.getZ().toDouble()
        zMax += pos.getZ().toDouble()
        val axisalignedbb: AxisAlignedBB = AxisAlignedBB(xMin, yMin, zMin, xMax, yMax, zMax)
        if (axisalignedbb != null && mask.intersects(axisalignedbb)) {
            list.add(axisalignedbb)
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSelectedBoundingBox(bs: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        return this.getBoundingBox(bs, worldIn, pos).offset(pos)
    }

    override fun getBoundingBox(bs: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        val state: IBlockState = worldIn.getBlockState(pos)
        val above: IBlockState = worldIn.getBlockState(pos.above())
        val below: IBlockState = worldIn.getBlockState(pos.below())
        val north: IBlockState = worldIn.getBlockState(pos.north())
        val south: IBlockState = worldIn.getBlockState(pos.south())
        val west: IBlockState = worldIn.getBlockState(pos.west())
        val east: IBlockState = worldIn.getBlockState(pos.east())

        val connectedN: Boolean = this.canPaneConnectToBlock(north, state)
        val connectedS: Boolean = this.canPaneConnectToBlock(south, state)
        val connectedW: Boolean = this.canPaneConnectToBlock(west, state)
        val connectedE: Boolean = this.canPaneConnectToBlock(east, state)

        val plateD: Boolean = this.buildSolidSide(below, state)
        val plateU: Boolean = this.buildSolidSide(above, state)
        var plateN: Boolean = this.buildSolidSide(north, state)
        var plateS: Boolean = this.buildSolidSide(south, state)
        var plateW: Boolean = this.buildSolidSide(west, state)
        var plateE: Boolean = this.buildSolidSide(east, state)

        //No plate on the sides which glass is facing
        if (connectedN || connectedS) {
            plateE = false
            plateW = plateE
        }
        if (connectedW || connectedE) {
            plateS = false
            plateN = plateS
        }

        //Singleton
        if (!connectedE && !connectedW && !connectedN && !connectedS) {
            var prefEW: Boolean = false
            if (below.getBlock() === this) {
                prefEW = (isConnectedEW(below, worldIn, pos.below()) || isPreferenceEW(below, worldIn, pos.below()))
            } else if (above.getBlock() === this) {
                prefEW = (isConnectedEW(above, worldIn, pos.above()) || isPreferenceEW(above, worldIn, pos.above()))
            }

            if (this.isPreferenceEW(state, worldIn, pos)) prefEW = true

            if (prefEW) {
                plateS = false
                plateN = plateS
            } else {
                plateE = false
                plateW = plateE
            }
        }

        var offset: Int = 0
        var base: Int = 1
        val solids: Int =
            (if (plateD) 1 else 0) + (if (plateU) 1 else 0) + (if (plateN) 1 else 0) + (if (plateS) 1 else 0) + (if (plateW) 1 else 0) + (if (plateE) 1 else 0)
        if (solids > 2 || (plateU && plateD) || (plateW && plateE) || (plateN && plateS)) {
            base = 0
        }

        if (connectedW || plateW) {
            offset = 2
        }
        if (connectedN || plateN) {
            offset += 4
        }
        if (connectedE || plateE) {
            offset += 8
        }
        if (connectedS || plateS) {
            offset += 16
        }


        // See 1.10 version for the logic
        return BOUNDING_BOXES[offset + base]
    }

    init {
        this.isClient =
            (GalacticraftCore.proxy)!!.javaClass.getName() == "micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore"
        this.baseBlock = if (base == null) this else base
        this.color = frame.getDefaultColor()
        this.setTranslationKey(assetName)
        this.setSoundType(SoundType.GLASS)
        this.hasTileEntity = true
        this.setDefaultState(
            this.blockState.getBaseState().setValue(MODEL, GlassModel.STANDARD_PANE)
                .setValue(ROTATION, GlassRotation.N)
        )
    }

    override fun isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, dir: EnumFacing): Boolean {
        val thisBlock: IBlockState = world.getBlockState(pos)
        if ((dir == EnumFacing.NORTH || dir == EnumFacing.SOUTH) && this.isConnectedEW(
                thisBlock,
                world,
                pos
            )
        ) return false
        if ((dir == EnumFacing.WEST || dir == EnumFacing.EAST) && this.isConnectedNS(
                thisBlock,
                world,
                pos
            )
        ) return false
        val otherBlock: IBlockState = world.getBlockState(pos.offset(dir.getOpposite()))
        return this.buildSolidSide(otherBlock, thisBlock)
    }

    protected open fun isConnectedEW(state: IBlockState?, worldIn: IBlockAccess, pos: BlockPos): Boolean {
        val west: IBlockState = worldIn.getBlockState(pos.west())
        val east: IBlockState = worldIn.getBlockState(pos.east())
        return this.canPaneConnectToBlock(west, state) || this.canPaneConnectToBlock(east, state)
    }

    protected open fun isConnectedEWRecursive(
        state: IBlockState?,
        worldIn: IBlockAccess,
        pos: BlockPos,
        dir: EnumFacing
    ): Boolean {
        val connectN: Boolean = this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()), state)
        val connectS: Boolean = this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()), state)
        val connectW: Boolean = this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()), state)
        val connectE: Boolean = this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()), state)
        if (connectN || connectS || connectW || connectE) {
            return (connectW || connectE) && !connectN && !connectS
        }
        val next: BlockPos = pos.offset(dir, 1)
        val nextState: IBlockState = worldIn.getBlockState(next)
        if (nextState.getBlock() === this) {
            return this.isConnectedEWRecursive(nextState, worldIn, next, dir)
        }
        return dir == EnumFacing.DOWN && this.isPreferenceEW(state, worldIn, pos)
    }

    protected open fun isConnectedNS(state: IBlockState?, worldIn: IBlockAccess, pos: BlockPos): Boolean {
        val north: IBlockState = worldIn.getBlockState(pos.north())
        val south: IBlockState = worldIn.getBlockState(pos.south())
        return this.canPaneConnectToBlock(north, state) || this.canPaneConnectToBlock(south, state)
    }

    protected open fun isConnectedNSRecursive(
        state: IBlockState?,
        worldIn: IBlockAccess,
        pos: BlockPos,
        dir: EnumFacing
    ): Boolean {
        val connectN: Boolean = this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()), state)
        val connectS: Boolean = this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()), state)
        val connectW: Boolean = this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()), state)
        val connectE: Boolean = this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()), state)
        if (connectN || connectS || connectW || connectE) {
            return (connectN || connectS) && !connectW && !connectE
        }
        val next: BlockPos = pos.offset(dir, 1)
        val nextState: IBlockState = worldIn.getBlockState(next)
        if (nextState.getBlock() === this) {
            return this.isConnectedNSRecursive(nextState, worldIn, next, dir)
        }
        return dir == EnumFacing.DOWN && !this.isPreferenceEW(state, worldIn, pos)
    }

    protected open fun isPreferenceEW(state: IBlockState?, worldIn: IBlockAccess, pos: BlockPos): Boolean {
        val north: IBlockState = worldIn.getBlockState(pos.north())
        val south: IBlockState = worldIn.getBlockState(pos.south())
        val west: IBlockState = worldIn.getBlockState(pos.west())
        val east: IBlockState = worldIn.getBlockState(pos.east())
        val solidsEW: Int = (if (west.getBlock()
                .isSideSolid(worldIn.getBlockState(pos.west()), worldIn, pos.west(), EnumFacing.EAST)
        ) 1 else 0) + (if (east.getBlock()
                .isSideSolid(worldIn.getBlockState(pos.east()), worldIn, pos.east(), EnumFacing.WEST)
        ) 1 else 0)
        val solidsNS: Int = (if (north.getBlock()
                .isSideSolid(worldIn.getBlockState(pos.north()), worldIn, pos.north(), EnumFacing.SOUTH)
        ) 1 else 0) + (if (south.getBlock()
                .isSideSolid(worldIn.getBlockState(pos.south()), worldIn, pos.south(), EnumFacing.NORTH)
        ) 1 else 0)
        return solidsEW > solidsNS
    }

    protected open fun canPaneConnectToBlock(off: IBlockState, blockState: IBlockState?): Boolean {
        return off.getBlock() === this
    }

    protected open fun buildSolidSide(off: IBlockState, blockState: IBlockState?): Boolean {
        return off.getBlock() !== this
    }

    protected open fun buildSolidSideUD(
        pos: BlockPos,
        testUD: EnumFacing,
        worldIn: IBlockAccess,
        blockState: IBlockState?
    ): Boolean {
        val offPos: BlockPos = pos.offset(testUD, 1)
        val off: IBlockState = worldIn.getBlockState(offPos)
        if (off.getBlock() !== this) return true
        val connThis: Int = (if (isConnectedEW(blockState, worldIn, pos)) 2 else 0) + (if (isConnectedNS(
                blockState,
                worldIn,
                pos
            )
        ) 1 else 0)
        val connOther: Int =
            (if (isConnectedEW(off, worldIn, offPos)) 2 else 0) + (if (isConnectedNS(off, worldIn, offPos)) 1 else 0)
        if (connThis == 0 && connOther == 0) return false
        val trueThis: Int = identifyHorizConnections(worldIn, pos, blockState)
        val trueOther: Int = identifyHorizConnections(worldIn, offPos, off)

        //Singles (no horizontal connections) -> does it match the plane of the one above or below?
        if (connThis == 0 && (((trueOther and 3) == 0) && trueThis != 3 || ((trueOther and 12) == 0) && trueThis == 3)) {
            return false
        }
        if (connOther == 0 && (((trueThis and 3) == 0) && trueOther != 3 || ((trueThis and 12) == 0) && trueOther == 3)) {
            return false
        }

        //Non-matching planes of connection, including all singles not already dealt with -> solid side
        if (connThis != connOther) return true

        //One side of connection only - and matches plane of the one above/below -> no solid side
        if (trueThis < 3 || trueThis == 4 || trueThis == 8) {
            return false
        }
        if (trueOther < 3 || trueOther == 4 || trueOther == 8) {
            return false
        }


        //Special cases: T junctions above/below 2-way corners, or similar -> solid side because the glass can't connect properly
        if ((trueThis and 7) != (trueOther and 7)) {
            return true
        }
        if ((trueThis and 11) != (trueOther and 11)) {
            return true
        }
        if ((trueThis and 13) != (trueOther and 13)) {
            return true
        }
        if ((trueThis and 14) != (trueOther and 14)) {
            return true
        }

        //Anything still left, it's matching planes of connection and not a special case -> no solid side
        return false
    }

    private fun identifyHorizConnections(worldIn: IBlockAccess, pos: BlockPos, state: IBlockState?): Int {
        val north: IBlockState = worldIn.getBlockState(pos.north())
        val south: IBlockState = worldIn.getBlockState(pos.south())
        val west: IBlockState = worldIn.getBlockState(pos.west())
        val east: IBlockState = worldIn.getBlockState(pos.east())

        val connectN: Boolean = this.canPaneConnectToBlock(north, state)
        val connectS: Boolean = this.canPaneConnectToBlock(south, state)
        val connectW: Boolean = this.canPaneConnectToBlock(west, state)
        val connectE: Boolean = this.canPaneConnectToBlock(east, state)

        val connections: Int =
            (if (connectN) 1 else 0) + (if (connectS) 1 else 0) + (if (connectW) 1 else 0) + (if (connectE) 1 else 0)

        if (!connectN && !connectS && !connectW && !connectE) {
            val above: IBlockState = worldIn.getBlockState(pos.above())
            val below: IBlockState = worldIn.getBlockState(pos.below())

            if (below.getBlock() === this && this.isConnectedEWRecursive(below, worldIn, pos.below(), EnumFacing.DOWN)) {
                return 3
            } else if (above.getBlock() === this && this.isConnectedEWRecursive(
                    above,
                    worldIn,
                    pos.above(),
                    EnumFacing.UP
                )
            ) {
                return 3
            }
            if (below.getBlock() === this && this.isConnectedNSRecursive(below, worldIn, pos.below(), EnumFacing.DOWN)) {
                return 12
            } else if (above.getBlock() === this && this.isConnectedNSRecursive(
                    above,
                    worldIn,
                    pos.above(),
                    EnumFacing.UP
                )
            ) {
                return 12
            }
            if (this.isPreferenceEW(state, worldIn, pos)) {
                return 3
            }
        }

        return (if (connectN) 8 else 0) + (if (connectS) 4 else 0) + (if (connectW) 2 else 0) + (if (connectE) 1 else 0)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val above: IBlockState = worldIn.getBlockState(pos.above())
        val below: IBlockState = worldIn.getBlockState(pos.below())
        val north: IBlockState = worldIn.getBlockState(pos.north())
        val south: IBlockState = worldIn.getBlockState(pos.south())
        val west: IBlockState = worldIn.getBlockState(pos.west())
        val east: IBlockState = worldIn.getBlockState(pos.east())

        val connectN: Boolean = this.canPaneConnectToBlock(north, state)
        val connectS: Boolean = this.canPaneConnectToBlock(south, state)
        val connectW: Boolean = this.canPaneConnectToBlock(west, state)
        val connectE: Boolean = this.canPaneConnectToBlock(east, state)

        val plateD: Boolean = buildSolidSideUD(pos, EnumFacing.DOWN, worldIn, state)
        val plateU: Boolean = buildSolidSideUD(pos, EnumFacing.UP, worldIn, state)
        val plateN: Boolean = this.buildSolidSide(north, state)
        val plateS: Boolean = this.buildSolidSide(south, state)
        val plateW: Boolean = this.buildSolidSide(west, state)
        val plateE: Boolean = this.buildSolidSide(east, state)

        val connections: Int =
            (if (connectN) 1 else 0) + (if (connectS) 1 else 0) + (if (connectW) 1 else 0) + (if (connectE) 1 else 0)
        var rot: GlassRotation = GlassRotation.N
        var cornerPiece: Int = 0

        if (connections == 4) return getModel(state, 4, rot, plateD, false, false, plateU, 0)

        if (connections == 3) {
            if (!connectE) rot = GlassRotation.S
            else if (!connectN) rot = GlassRotation.E
            else if (!connectS) rot = GlassRotation.W

            return getModel(state, 3, rot, plateD, false, false, plateU, 0)
        }

        if (connections == 0) {
            if (below.getBlock() === this && this.isConnectedEWRecursive(below, worldIn, pos.below(), EnumFacing.DOWN)) {
                rot = GlassRotation.E
            } else if (above.getBlock() === this && this.isConnectedEWRecursive(
                    above,
                    worldIn,
                    pos.above(),
                    EnumFacing.UP
                )
            ) {
                rot = GlassRotation.E
            }
            if (below.getBlock() === this && this.isConnectedNSRecursive(below, worldIn, pos.below(), EnumFacing.DOWN)) {
            } else if (above.getBlock() === this && this.isConnectedNSRecursive(
                    above,
                    worldIn,
                    pos.above(),
                    EnumFacing.UP
                )
            ) {
            } else if (this.isPreferenceEW(state, worldIn, pos)) {
                rot = GlassRotation.E
            }

            return getModel(state, 1, rot, plateD, plateS, plateN, plateU, 0)
        }


//        EnumFacing testUD = EnumFacing.UP;
//        if (plateU && !plateD)           
//        {
//            testUD = EnumFacing.DOWN;
//        }
//        if (plateD && !plateU)           
//        {
//            testUD = EnumFacing.UP;
//        }

        // Two glass connections in a flat plane - this also covers connected flat on one side only
        if (connectW && connectE || connections == 1 && (connectW || connectE)) {
            rot = GlassRotation.E
            if (connectW && !plateU && buildSolidSideUD(pos.west(), EnumFacing.UP, worldIn, west)) {
                cornerPiece++
            }
            if (connectW && !plateD && buildSolidSideUD(pos.west(), EnumFacing.DOWN, worldIn, west)) {
                cornerPiece += 5
            }
            if (connectE && !plateU && buildSolidSideUD(pos.east(), EnumFacing.UP, worldIn, east)) {
                cornerPiece++
                if (connectW) rot = GlassRotation.W
            }
            if (connectE && !plateD && buildSolidSideUD(pos.east(), EnumFacing.DOWN, worldIn, east)) {
                cornerPiece += 5
                if (connectW) rot = GlassRotation.W
            }
            return getModel(state, 1, rot, plateD, plateW, plateE, plateU, cornerPiece)
        }

        if (connectN && connectS || connections == 1) {
            if (connectN && !plateU && buildSolidSideUD(pos.north(), EnumFacing.UP, worldIn, north)) {
                if (!connectS) {
                    cornerPiece = cornerPiece or 5
                    if (!plateD) rot = GlassRotation.S
                } else {
                    cornerPiece = cornerPiece or 1
                    rot = GlassRotation.S
                }
            }
            if (connectN && !plateD && buildSolidSideUD(pos.north(), EnumFacing.DOWN, worldIn, north)) {
                cornerPiece++
                if (connectS) rot = GlassRotation.S
            }
            if (connectS && !plateU && buildSolidSideUD(pos.south(), EnumFacing.UP, worldIn, south)) {
                if (cornerPiece == 0 && !plateD) {
                    cornerPiece = 5
                    rot = GlassRotation.S
                } else if ((cornerPiece and 3) < 2) {
                    cornerPiece++
                }
            }
            if (connectS && !plateD && buildSolidSideUD(pos.south(), EnumFacing.DOWN, worldIn, south)) {
                if ((cornerPiece and 3) < 2) {
                    cornerPiece++
                } else {
                    cornerPiece = cornerPiece or 5
                    //rot = GlassRotation.S;
                }
            }
            return getModel(state, 1, rot, plateD, plateS, plateN, plateU, cornerPiece)
        }

        //It must be two glass connections on a corner
        if (connectW && connectS) rot = GlassRotation.S
        else if (connectS && connectE) rot = GlassRotation.E
        else if (connectN && connectW) rot = GlassRotation.W

        return getModel(state, 2, rot, plateD, false, false, plateU, 0)
    }

    private fun getModel(
        state: IBlockState,
        model: Int,
        rot: GlassRotation,
        plateD: Boolean,
        plateL: Boolean,
        plateR: Boolean,
        plateU: Boolean,
        cornerPiece: Int
    ): IBlockState {
        var x: Int = 0
        var y: Int = rot.ordinal
        if (y % 2 == 1) y = y xor 2
        val solids: Int =
            (if (plateD) 1 else 0) + (if (plateU) 1 else 0) + (if (plateR) 1 else 0) + (if (plateL) 1 else 0)
        when (model) {
            1 -> {
                if (solids == 4) return state.setValue(MODEL, GlassModel.STANDARD_S4)
                    .setValue(ROTATION, GlassRotation.Companion.get(y, 0))

                if (solids == 3) {
                    if (!plateD) x = if (y % 2 == 1) 3 else 1
                    else if (!plateR) {
                        x = 2
                        y = y xor 2
                    } else if (!plateU) x = if (y % 2 == 1) 1 else 3
                    return state.setValue(MODEL, GlassModel.STANDARD_S3)
                        .setValue(ROTATION, GlassRotation.Companion.get(y, x))
                }

                if (plateD && plateU) return state.setValue(
                    MODEL,
                    GlassModel.STANDARD_S2A
                ).setValue(ROTATION, GlassRotation.Companion.get(y, 0))

                if (plateL && plateR) return state.setValue(
                    MODEL,
                    GlassModel.STANDARD_S2A
                ).setValue(ROTATION, GlassRotation.Companion.get(y, 1))

                if (plateD && (plateR || plateL)) {
                    return state.setValue(
                        MODEL,
                        if (cornerPiece > 0) GlassModel.STANDARD_S2B else GlassModel.STANDARD_S2
                    ).setValue(
                        ROTATION,
                        GlassRotation.Companion.get(if (plateL) (y xor 2) else y, 0)
                    )
                }

                if (plateU && (plateR || plateL)) {
                    return state.setValue(
                        MODEL,
                        if (cornerPiece > 0) GlassModel.STANDARD_S2B else GlassModel.STANDARD_S2
                    ).setValue(
                        ROTATION,
                        GlassRotation.Companion.get(if (plateL) (y xor 2) else y, 2)
                    )
                }

                if (plateU || plateD) return state.setValue(
                    MODEL,
                    getCornerModel(cornerPiece)
                ).setValue(
                    ROTATION,
                    GlassRotation.Companion.get(y, if (plateU || cornerPiece > 4) 2 else 0)
                )

                if (plateR || plateL) return state.setValue(
                    MODEL,
                    getCornerModel(cornerPiece)
                ).setValue(
                    ROTATION,
                    GlassRotation.Companion.get(if (plateL) (y xor 2) else y, 1 + (if (cornerPiece > 4) 2 else 0))
                )

                return state.setValue(MODEL, GlassModel.STANDARD_PANE)
                    .setValue(ROTATION, GlassRotation.Companion.get(y, 0))
            }

            2 -> {
                if (plateU && plateD) return state.setValue(MODEL, GlassModel.CORNER_S2)
                    .setValue(ROTATION, GlassRotation.Companion.get(y, 0))
                if (plateD) return state.setValue(MODEL, GlassModel.CORNER_S)
                    .setValue(ROTATION, GlassRotation.Companion.get(y, 0))
                if (plateU) return state.setValue(MODEL, GlassModel.CORNER_S)
                    .setValue(ROTATION, GlassRotation.Companion.get((y + 3) % 4, 2))

                return state.setValue(MODEL, GlassModel.CORNER)
                    .setValue(ROTATION, GlassRotation.Companion.get(y, 0))
            }

            3 -> {
                if (plateD && plateU) return state.setValue(
                    MODEL,
                    GlassModel.T_JUNCTION_S2
                ).setValue(ROTATION, GlassRotation.Companion.get(y, 0))
                if (plateD) return state.setValue(MODEL, GlassModel.T_JUNCTION_S)
                    .setValue(ROTATION, GlassRotation.Companion.get(y, 0))
                if (plateU) return state.setValue(MODEL, GlassModel.T_JUNCTION_S)
                    .setValue(ROTATION, GlassRotation.Companion.get(y xor 2, 2))

                return state.setValue(MODEL, GlassModel.T_JUNCTION)
                    .setValue(ROTATION, GlassRotation.Companion.get(y, x))
            }

            4 -> {
                if (plateD && plateU) return state.setValue(
                    MODEL,
                    GlassModel.CROSSROADS_S2
                ).setValue(ROTATION, GlassRotation.Companion.get(y, 0))
                if (plateD) return state.setValue(MODEL, GlassModel.CROSSROADS_S)
                    .setValue(ROTATION, GlassRotation.Companion.get(y, 0))
                if (plateU) return state.setValue(MODEL, GlassModel.CROSSROADS_S)
                    .setValue(ROTATION, GlassRotation.Companion.get(y xor 2, 2))
                return state.setValue(MODEL, GlassModel.CROSSROADS)
                    .setValue(ROTATION, GlassRotation.Companion.get(y, x))
            }

            else -> return state.setValue(MODEL, GlassModel.CROSSROADS)
                .setValue(ROTATION, GlassRotation.Companion.get(y, x))
        }
    }

    private fun getCornerModel(cornerPiece: Int): GlassModel {
        if ((cornerPiece and 2) == 2) return GlassModel.STANDARD_S1BB
        return if (cornerPiece > 0) GlassModel.STANDARD_S1B else GlassModel.STANDARD_S1
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return 0
    }

    override fun setColor(newColor: Int, p: EntityPlayer?, side: Side?): Int {
        if (newColor >= 0 && this.color != newColor) {
            this.color = newColor
            return 1
        }
        return 0
    }

    override fun getShiftDescription(meta: Int): String? {
        when (this.type) {
            GlassType.CLEAR -> return GCCoreUtil.translate("tile.space_glass.clear.description")
            GlassType.VANILLA -> return GCCoreUtil.translate("tile.space_glass.description")
            GlassType.STRONG -> return GCCoreUtil.translate("tile.space_glass.strong.description")
            else -> return GCCoreUtil.translate("tile.space_glass.strong.description")
        }
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    enum class GlassModel(private val serializedName: String) : IStringSerializable {
        STANDARD_PANE("standard"),
        CORNER("corner"),
        T_JUNCTION("joint"),
        CROSSROADS("joinx"),
        STANDARD_S1("standards1"),
        STANDARD_S1B("standards1b"),
        STANDARD_S1BB("standards1bb"),
        STANDARD_S2("standards2"),
        STANDARD_S2A("standards2a"),
        STANDARD_S2B("standards2b"),
        STANDARD_S3("standards3"),
        STANDARD_S4("standards4"),
        CORNER_S("corner_s"),
        CORNER_S2("corner_s2"),
        T_JUNCTION_S("joint_s"),
        T_JUNCTION_S2("joint_s2"),
        CROSSROADS_S("joinx_s"),
        CROSSROADS_S2("joinx_s2");

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }
    }

    enum class GlassRotation(private val serializedName: String) : IStringSerializable {
        N("0"),
        E("y"),
        S("yy"),
        W("yyy"),
        UN("u"),
        UE("uyyy"),
        US("uyy"),
        UW("uy"),
        UUN("uuyy"),
        UUE("uuyyy"),
        UUS("uu"),
        UUW("uuy"),
        DN("d"),
        DE("dy"),
        DS("dyy"),
        DW("dyyy");

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun get(i: Int): GlassRotation {
                return entries[i]
            }

            @JvmStatic
            fun get(y: Int, x: Int): GlassRotation {
                val i: Int = y + x * 4
                return entries[i]
            }
        }
    }

    enum class GlassFrame(private val serializedName: String, private val defaultColor: Int) : IStringSerializable {
        PLAIN("plain", 0xfafaf7),
        TIN_DECO("tin_deco", 0xFFFFFF); //TODO - more frame textures can be added in future - maybe only on Asteroids?

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        fun getDefaultColor(): Int {
            return this.defaultColor
        }
    }

    enum class GlassType(private val serializedName: String) : IStringSerializable {
        VANILLA("vanilla"),
        CLEAR("clear"),
        STRONG("strong");

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }
    }

    companion object {
        @JvmField
        val MODEL: PropertyEnum<GlassModel> = PropertyEnum.create<GlassModel>("modeltype", GlassModel::class.java)

        @JvmField
        val ROTATION: PropertyEnum<GlassRotation> =
            PropertyEnum.create<GlassRotation>("rot", GlassRotation::class.java)

        private val clazz: Class<*>? = Blocks.WATER.javaClass.getSuperclass()

        @JvmField
        protected val BOUNDING_BOXES: Array<AxisAlignedBB> = arrayOf<AxisAlignedBB>(
            AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75),  //Widen to the frame width, if frames on opposite sides
            AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625),  //The glass width
            AxisAlignedBB(0.0, 0.0, 0.25, 0.75, 1.0, 0.75),  //0001
            AxisAlignedBB(0.0, 0.0, 0.375, 0.625, 1.0, 0.625),  //0001
            AxisAlignedBB(0.25, 0.0, 0.0, 0.75, 1.0, 0.75),  //0010
            AxisAlignedBB(0.375, 0.0, 0.0, 0.625, 1.0, 0.625),  //0010
            AxisAlignedBB(0.0, 0.0, 0.0, 0.75, 1.0, 0.75),  //0011
            AxisAlignedBB(0.0, 0.0, 0.0, 0.625, 1.0, 0.625),  //0011
            AxisAlignedBB(0.25, 0.0, 0.25, 1.0, 1.0, 0.75),  //0100
            AxisAlignedBB(0.375, 0.0, 0.375, 1.0, 1.0, 0.625),  //0100
            AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 0.75),  //0101
            AxisAlignedBB(0.0, 0.0, 0.375, 1.0, 1.0, 0.625),  //0101
            AxisAlignedBB(0.25, 0.0, 0.0, 1.0, 1.0, 0.75),  //0110
            AxisAlignedBB(0.375, 0.0, 0.0, 1.0, 1.0, 0.625),  //0110
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.75),  //0111
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.625),  //0111
            AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 1.0),  //1000
            AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 1.0),  //1000
            AxisAlignedBB(0.0, 0.0, 0.25, 0.75, 1.0, 1.0),  //1001
            AxisAlignedBB(0.0, 0.0, 0.375, 0.625, 1.0, 1.0),  //1001
            AxisAlignedBB(0.25, 0.0, 0.0, 0.75, 1.0, 1.0),  //1010
            AxisAlignedBB(0.375, 0.0, 0.0, 0.625, 1.0, 1.0),  //1010
            AxisAlignedBB(0.0, 0.0, 0.0, 0.75, 1.0, 1.0),  //1011
            AxisAlignedBB(0.0, 0.0, 0.0, 0.625, 1.0, 1.0),  //1011
            AxisAlignedBB(0.25, 0.0, 0.25, 1.0, 1.0, 1.0),  //1100
            AxisAlignedBB(0.375, 0.0, 0.375, 1.0, 1.0, 1.0),  //1100
            AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 1.0),  //1101
            AxisAlignedBB(0.0, 0.0, 0.375, 1.0, 1.0, 1.0),  //1101
            AxisAlignedBB(0.25, 0.0, 0.0, 1.0, 1.0, 1.0),  //1110
            AxisAlignedBB(0.375, 0.0, 0.0, 1.0, 1.0, 1.0),  //1110
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),  //1111
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0) //1111
        )

        @JvmStatic
        @OnlyIn(Dist.CLIENT)
        fun updateGlassColors(color1: Int, color2: Int, color3: Int) {
            var changes: Int = 0
            changes += (GCBlocks.spaceGlassVanilla)!!.setColor(color1, null, Side.CLIENT)
            changes += (GCBlocks.spaceGlassClear)!!.setColor(color2, null, Side.CLIENT)
            changes += (GCBlocks.spaceGlassStrong)!!.setColor(color3, null, Side.CLIENT)

            if (changes > 0) updateClientRender()
        }

        @JvmStatic
        @OnlyIn(Dist.CLIENT)
        fun updateClientRender() {
            Minecraft.getInstance().renderGlobal.loadRenderers()
            //TODO: improve performance, this is re-rendering ALL chunks on client
            //Can we somehow limit this to chunks containing BlockSpaceGlass?
            //or else: don't do all the chunk redrawing at once, queue them?
        }
    }
}
