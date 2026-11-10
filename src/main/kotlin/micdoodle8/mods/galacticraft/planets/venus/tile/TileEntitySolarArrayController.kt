package micdoodle8.mods.galacticraft.planets.venus.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField
import java.util.Arrays
import java.util.EnumSet

import com.google.common.collect.Sets
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockSolarArrayController
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.Vec3i
import net.minecraftforge.fml.relauncher.Side
import java.util.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

open class TileEntitySolarArrayController constructor() : TileBaseUniversalElectricalSource(), IDisableableMachine,
    IInventoryDefaults, ISidedInventory, IConnector {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileBaseUniversalElectricalSource>.getDisplayName()

    private var solarStrength = 0

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
    private val solarArray: MutableSet<ITransmitter?> = Sets.newHashSet<ITransmitter?>()

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var connectedInfo: Int = 0

    private var initialised = false

    init {
        this.storage.setMaxExtract(MAX_GENERATE_WATTS.toFloat())
        this.storage.setMaxReceive(MAX_GENERATE_WATTS.toFloat())
        this.storage.setCapacity(50000f)
        this.initialised = true
    }

    override fun update() {
        if (!this.initialised) {
            this.storage.setCapacity(50000f)
            this.initialised = true
        }

        if (!this.world.isRemote) {
            this.receiveEnergyGC(null, this.generateWatts.toFloat(), false)

            val outputDirections: EnumSet<EnumFacing> = EnumSet.noneOf<EnumFacing>(EnumFacing::class.java)
            outputDirections.addAll(Arrays.asList<EnumFacing?>(*EnumFacing.HORIZONTALS))
            outputDirections.removeAll(this.getElectricalOutputDirections())

            val thisVec: BlockVec3 = BlockVec3(this)
            solarArray.clear()
            for (direction: EnumFacing in outputDirections) {
                val tileAdj: TileEntity? = thisVec.getTileEntityOnSide(this.world, direction)

                if (tileAdj != null) {
                    if (tileAdj is INetworkProvider) {
                        if (tileAdj is ITransmitter) {
                            if ((tileAdj as ITransmitter).canConnect(
                                    direction.getOpposite(),
                                    NetworkType.SOLAR_MODULE
                                )
                            ) {
                                if ((tileAdj as INetworkProvider).getNetwork() is SolarModuleNetwork) {
                                    solarArray.addAll((((tileAdj as INetworkProvider).getNetwork() as SolarModuleNetwork).getTransmitters())!!)
                                }
                            }
                        } else {
                            if ((tileAdj as INetworkProvider).getNetwork() is SolarModuleNetwork) {
                                solarArray.addAll((((tileAdj as INetworkProvider).getNetwork() as SolarModuleNetwork).getTransmitters())!!)
                            }
                        }
                    }
                }
            }
        }

        super.update()

        if (!this.world.isRemote) {
            this.recharge(this.stacks.get(0))

            if (this.disableCooldown > 0) {
                this.disableCooldown--
            }

            if (!this.getDisabled(0) && this.ticks % 20 == 0) {
                this.solarStrength = 0
                var arraySizeWithinRange: Int = 0
                if (this.world.isDaytime() && (this.world.provider is IGalacticraftWorldProvider || !this.world.isRaining() && !this.world.isThundering())) {
                    for (transmitter: ITransmitter? in solarArray) {
                        val tile: TileEntity = transmitter as TileEntity
                        val diff: Vec3i = tile.getPos().subtract(this.getPos())
                        if (abs(diff.getX()) <= 16 && diff.getY() == 0 && abs(diff.getZ()) <= 16) {
                            arraySizeWithinRange++
                            if (this.world.canBlockSeeSky(tile.getPos())) {
                                var valid: Boolean = true

                                for (y: Int in this.getPos().getY() + 1..255) {
                                    val state: IBlockState = this.world.getBlockState(this.getPos().add(0, y, 0))

                                    if (state.getBlock().isOpaqueCube(state)) {
                                        valid = false
                                        break
                                    }
                                }

                                if (valid) {
                                    this.solarStrength++
                                }
                            }
                        }
                    }
                }

                connectedInfo = solarStrength shl 16 or arraySizeWithinRange
            }
        }

        val angle: Float = if (this.world.getCelestialAngle(1.0f) - 0.7845194f < 0) 1.0f - 0.7845194f else -0.7845194f
        var celestialAngle: Float = (this.world.getCelestialAngle(1.0f) + angle) * 360.0f
        if (this.world.provider !is WorldProviderSpaceStation) celestialAngle += 12.5f
        if (this.world.provider is WorldProviderVenus) celestialAngle = 180f - celestialAngle
        celestialAngle %= 360f
        val isDaytime: Boolean =
            this.world.isDaytime() && (celestialAngle < 180.5f || celestialAngle > 359.5f) || this.world.provider is WorldProviderSpaceStation

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

    open fun getGenerate(): Int {
        if (this.getDisabled(0)) {
            return 0
        }

        return floor((solarStrength * 6.3f * this.getSolarBoost()).toDouble()).toInt()
    }

    open fun getSolarBoost(): Float {
        var result: Float =
            (if (this.world.provider is ISolarLevel) (this.world.provider as ISolarLevel).getSolarEnergyMultiplier() else 1.0).toFloat()
        if (this.world.provider is WorldProviderSpaceStation) {
            // 10% boost for new solar on space stations
            result *= 1.1f
        }
        if (this.world.provider is WorldProviderVenus) {
            if (this.pos.getY() > 90) {
                result += (this.pos.getY() - 90) / 1000f //Small improvement on Venus at higher altitudes
            }
        }
        return result
    }

    override fun getPacketRange(): Double {
        return 20.0
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.storage.setCapacity(nbt.getFloat("maxEnergy"))
        this.setDisabled(0, nbt.getBoolean("disabled"))
        this.disableCooldown = nbt.getInteger("disabledCooldown")

        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)

        this.initialised = false
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setFloat("maxEnergy", this.getMaxEnergyStoredGC())
        nbt.setInteger("disabledCooldown", this.disableCooldown)
        nbt.setBoolean("disabled", this.getDisabled(0))

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
        if (state.getBlock() is BlockSolarArrayController) {
            return state.getValue(BlockSolarArrayController.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getFront())
    }

    override fun getElectricOutputDirection(): EnumFacing {
        return getFront()
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.solar_array_controller.name")
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

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    override fun getStackInSlot(var1: Int): ItemStack {
        return this.stacks.get(var1)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
        }

        return itemstack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.markDirty()
        }
        return oldstack
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.markDirty()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getInventoryStackLimit(): Int {
        return 1
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return this.isItemValidForSlot(slotID, itemstack)
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return slotID == 0
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem())
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        return type == NetworkType.POWER && direction == this.getElectricOutputDirection() ||
                type == NetworkType.SOLAR_MODULE && direction != this.getElectricOutputDirection() && (direction)!!.getAxis() != EnumFacing.Axis.Y
    }

    open fun getPossibleArraySize(): Int {
        return connectedInfo and 0xFFFF
    }

    open fun getActualArraySize(): Int {
        return (connectedInfo shr 16) and 0xFFFF
    }

    companion object {
        const val MAX_GENERATE_WATTS: Int = 1000
    }
}
