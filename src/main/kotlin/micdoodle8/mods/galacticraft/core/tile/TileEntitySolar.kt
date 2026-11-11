package micdoodle8.mods.galacticraft.core.tile

import net.minecraft.world.ContainerHelper
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType
import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.blocks.BlockSolar
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class TileEntitySolar @JvmOverloads constructor(tier: Int = 1) : TileBaseUniversalElectricalSource(),
    IMultiBlock, IDisableableMachine, IInventoryDefaults, ISidedInventory, IConnector {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileBaseUniversalElectricalSource>.getDisplayName()

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var solarStrength: Int = 0

    @JvmField
    var targetAngle: Float = 0f

    @JvmField
    var currentAngle: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disabled: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disableCooldown: Int = 0
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var generateWatts: Int = 0

    private var initialised = false
    private var initialisedMulti = false
    private var renderAABB: AxisAlignedBB? = null

    /*
     * @param tier: 1 = Basic Solar  2 = Advanced Solar
     */
    init {
        this.storage.setMaxExtract(MAX_GENERATE_WATTS.toFloat())
        this.storage.setMaxReceive(MAX_GENERATE_WATTS.toFloat())
        if (tier == 2) {
            this.storage.setCapacity(30000f)
        }
        this.setTierGC(tier)
        this.initialised = true
    }

    override fun update() {
        if (!this.initialised) {
            val metadata: Int = this.getBlockMetadata()
            if (metadata >= BlockSolar.ADVANCED_METADATA) {
                this.storage.setCapacity(30000f)
                this.setTierGC(2)
            }
            this.initialised = true
        }

        if (!this.initialisedMulti) {
            this.initialisedMulti = this.initialiseMultiTiles(this.getPos(), this.world)
        }

        if (!this.world.isRemote) {
            this.receiveEnergyGC(null, this.generateWatts.toFloat(), false)
        }

        super.update()

        if (!this.world.isRemote) {
            this.recharge(this.stacks.get(0))

            if (this.disableCooldown > 0) {
                this.disableCooldown--
            }

            if (!this.getDisabled(0) && this.ticks % 20 == 0) {
                this.solarStrength = 0

                if (this.world.isDaytime() && (this.world.provider is IGalacticraftWorldProvider || !this.world.isRaining() && !this.world.isThundering())) {
                    val distance: Double = 100.0
                    val sinA: Double = -sin((this.currentAngle - 77.5) / Constants.RADIANS_TO_DEGREES_D)
                    val cosA: Double = abs(cos((this.currentAngle - 77.5) / Constants.RADIANS_TO_DEGREES_D))

                    for (x: Int in -1..1) {
                        for (z: Int in -1..1) {
                            if (this.tierGC == 1) {
                                if (this.world.canBlockSeeSky(this.getPos().add(x, 2, z))) {
                                    var valid: Boolean = true

                                    for (y: Int in this.getPos().getY() + 3..255) {
                                        val state: IBlockState = this.world.getBlockState(
                                            BlockPos(
                                                this.getPos().getX() + x,
                                                y,
                                                this.getPos().getZ() + z
                                            )
                                        )

                                        if (state.getBlock().isOpaqueCube(state)) {
                                            valid = false
                                            break
                                        }
                                    }

                                    if (valid) {
                                        this.solarStrength++
                                    }
                                }
                            } else {
                                var valid: Boolean = true

                                val blockVec: BlockVec3 = BlockVec3(this).translate(x, 3, z)
                                var d: Double = 0.0
                                while (d < distance) {
                                    val blockAt: BlockVec3 =
                                        blockVec.clone().translate((d * sinA).toInt(), (d * cosA).toInt(), 0)
                                    val state: IBlockState? = blockAt.getBlockState(this.world)

                                    if (state == null) {
                                        break
                                    }

                                    if (state.getBlock().isOpaqueCube(state)) {
                                        valid = false
                                        break
                                    }
                                    d++
                                }

                                if (valid) {
                                    this.solarStrength++
                                }
                            }
                        }
                    }
                }
            }
        }

        val angle: Float = if (this.world.getCelestialAngle(1.0f) - 0.7845194f < 0) 1.0f - 0.7845194f else -0.7845194f
        var celestialAngle: Float = (this.world.getCelestialAngle(1.0f) + angle) * 360.0f
        if (this.world.provider !is WorldProviderSpaceStation) celestialAngle += 12.5f
        if (GalacticraftCore.isPlanetsLoaded && this.world.provider is WorldProviderVenus) celestialAngle =
            180f - celestialAngle
        celestialAngle %= 360f
        val isDaytime: Boolean =
            this.world.isDaytime() && (celestialAngle < 180.5f || celestialAngle > 359.5f) || this.world.provider is WorldProviderSpaceStation

        if (this.tierGC == 1) {
            if (!isDaytime || this.world.isRaining() || this.world.isThundering()) {
                this.targetAngle = 77.5f + 180.0f
            } else {
                this.targetAngle = 77.5f
            }
        } else {
            if (!isDaytime || this.world.isRaining() || this.world.isThundering()) {
                this.targetAngle = 77.5f + 180f
            } else if (celestialAngle > 27.5f && celestialAngle < 152.5f) {
                val difference: Float = this.targetAngle - celestialAngle + 12.5f

                this.targetAngle -= difference / 20.0f
            } else if (celestialAngle <= 27.5f || celestialAngle > 270f) {
                this.targetAngle = 15f
            } else if (celestialAngle >= 152.5f) {
                this.targetAngle = 140f
            }
        }

        val difference: Float = this.targetAngle - this.currentAngle

        this.currentAngle += difference / 20.0f

        if (!this.world.isRemote) {
            val generated: Int = this.getGenerate()
            if (generated > 0) {
                this.generateWatts = min(max(generated, 0), MAX_GENERATE_WATTS)
            } else {
                this.generateWatts = 0
            }
        }

        this.produce()
    }

    protected open fun initialiseMultiTiles(pos: BlockPos, world: World): Boolean {
        //Client can create its own fake blocks and tiles - no need for networking in 1.8+
        if (world.isClientSide) this.onCreate(world, pos)

        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(pos, (positions as MutableList<BlockPos?>?))
        var result: Boolean = true
        for (vecToAdd: BlockPos in positions) {
            val tile: TileEntity? = world.getBlockEntity(vecToAdd)
            if (tile is TileEntityMulti) {
                tile.mainBlockPosition = pos
            } else {
                result = false
            }
        }
        return result
    }

    open fun getGenerate(): Int {
        if (this.getDisabled(0)) {
            return 0
        }

        val angle: Float =
            if (this.world.getCelestialAngle(1.0f) - 0.784690560f < 0) 1.0f - 0.784690560f else -0.784690560f
        var celestialAngle: Float = (this.world.getCelestialAngle(1.0f) + angle) * 360.0f
        if (this.world.provider !is WorldProviderSpaceStation) celestialAngle += 12.5f

        if (GalacticraftCore.isPlanetsLoaded && this.world.provider is WorldProviderVenus) celestialAngle =
            180f - celestialAngle
        celestialAngle %= 360f

        val difference: Float = (180.0f - abs((this.currentAngle + 12.5f) % 180f - celestialAngle)) / 180.0f

        return MathHelper.floor(0.01f * difference * difference * (this.solarStrength * (abs(difference) * 500.0f)) * this.getSolarBoost())
    }

    open fun getSolarBoost(): Float {
        var result: Float =
            (if (this.world.provider is ISolarLevel) (this.world.provider as ISolarLevel).getSolarEnergyMultiplier() else 1.0).toFloat()
        if (GalacticraftCore.isPlanetsLoaded && this.world.provider is WorldProviderVenus) {
            if (this.pos.getY() > 90) {
                result += (this.pos.getY() - 90) / 1000f //Small improvement on Venus at higher altitudes
            }
        }
        return result
    }

    override fun onActivated(entityPlayer: EntityPlayer?): Boolean {
        return false // TODO
        //        return this.getBlockType().onBlockActivated(this.world, this.getPos(), this.world.getBlockState(getPos()), entityPlayer, EnumFacing.DOWN, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
    }

    //    @Override
    //    public boolean canUpdate()
    //    {
    //        return true;
    //    }
    override fun onCreate(world: World?, placedPosition: BlockPos?) {
        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(placedPosition, (positions as MutableList<BlockPos?>?))
        if (positions.size > 0) {
            (GCBlocks.fakeBlock as BlockMulti).makeFakeBlock(
                (world)!!,
                positions.get(0),
                placedPosition,
                BlockMulti.EnumBlockMultiType.SOLAR_PANEL_0.getMeta()
            )
            positions.removeAt(0)
        }
        (GCBlocks.fakeBlock as BlockMulti).makeFakeBlock((world)!!, positions, placedPosition, this.getMultiType())
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType {
        return if (this.getTierGC() == 1) BlockMulti.EnumBlockMultiType.SOLAR_PANEL_1 else BlockMulti.EnumBlockMultiType.SOLAR_PANEL_0
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        val buildHeight: Int = this.world.getHeight() - 1
        var y: Int = (placedPosition)!!.getY() + 1
        if (y > buildHeight) {
            return
        }
        (positions)!!.add(BlockPos((placedPosition)!!.getX(), y, (placedPosition)!!.getZ()))

        y++
        if (y > buildHeight) {
            return
        }
        for (x: Int in -1..1) {
            for (z: Int in -1..1) {
                (positions)!!.add(BlockPos((placedPosition)!!.getX() + x, y, (placedPosition)!!.getZ() + z))
            }
        }
    }

    override fun onDestroy(callingBlock: TileEntity?) {
        val thisBlock: BlockPos = getPos()
        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(thisBlock, (positions as MutableList<BlockPos?>?))

        for (pos: BlockPos in positions) {
            val stateAt: IBlockState = this.world.getBlockState(pos)

            if (stateAt.getBlock() === GCBlocks.fakeBlock) {
                val type: BlockMulti.EnumBlockMultiType =
                    stateAt.getValue(BlockMulti.MULTI_TYPE) as BlockMulti.EnumBlockMultiType
                if ((type == BlockMulti.EnumBlockMultiType.SOLAR_PANEL_0 || type == BlockMulti.EnumBlockMultiType.SOLAR_PANEL_1)) {
                    if (this.world.isRemote && this.world.rand.nextDouble() < 0.1) {
                        FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(
                            pos,
                            (GCBlocks.solarPanel)!!.defaultBlockState()
                        )
                    }

                    this.world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                }
            }
        }

        this.world.destroyBlock(getPos(), true)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.storage.setCapacity(nbt.getFloatOr("maxEnergy", 0f))
        this.currentAngle = nbt.getFloatOr("currentAngle", 0f)
        this.targetAngle = nbt.getFloatOr("targetAngle", 0f)
        this.setDisabled(0, nbt.getBooleanOr("disabled", false))
        this.disableCooldown = nbt.getIntOr("disabledCooldown", 0)

        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)

        this.initialised = false
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putFloat("maxEnergy", this.getMaxEnergyStoredGC())
        nbt.putFloat("currentAngle", this.currentAngle)
        nbt.putFloat("targetAngle", this.targetAngle)
        nbt.putInt("disabledCooldown", this.disableCooldown)
        nbt.putBoolean("disabled", this.getDisabled(0))

        ItemStackHelper.saveAllItems(nbt, this.stacks)

        return nbt
    }

    /*@Override
    public float getRequest(EnumFacing direction)
	{
		return 0;
	}
	*/
    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    open fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockSolar) {
            return state.getValue(BlockSolar.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getFront())
    }

    override fun getElectricOutputDirection(): EnumFacing {
        return getFront()
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(
                (getPos().getX() - 1).toDouble(),
                getPos().getY().toDouble(),
                (getPos().getZ() - 1).toDouble(),
                (getPos().getX() + 2).toDouble(),
                (getPos().getY() + 4).toDouble(),
                (getPos().getZ() + 2).toDouble()
            )
        }
        return this.renderAABB!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_LONG
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable(if (this.tierGC == 1) "container.solarbasic.name" else "container.solaradvanced.name")
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        if (this.disableCooldown == 0) {
            this.disabled = disabled
            this.disableCooldown = 20
        }
    }

    override fun getDisabled(index: Int): Boolean {
        return this.disabled
    }

    open fun getScaledElecticalLevel(i: Int): Int {
        return floor((this.getEnergyStoredGC() * i / this.getMaxEnergyStoredGC()).toDouble()).toInt()
    }

    override fun getContainerSize(): Int {
        return this.stacks.size
    }

    override fun getItem(var1: Int): ItemStack {
        return this.stacks.get(var1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        val oldstack: ItemStack = ContainerHelper.takeItem(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.setChanged()
        }
        return oldstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getMaxStackSize(): Int {
        return 1
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return slotID == 0
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem())
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null || type != NetworkType.POWER) {
            return false
        }

        return direction == this.getElectricOutputDirection()
    }

    companion object {
        const val MAX_GENERATE_WATTS: Int = 200
    }
}
