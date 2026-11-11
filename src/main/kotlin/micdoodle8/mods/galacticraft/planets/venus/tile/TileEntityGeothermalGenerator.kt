package micdoodle8.mods.galacticraft.planets.venus.tile

import net.minecraft.world.ContainerHelper
import java.util.EnumSet

import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockGeothermalGenerator
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraftforge.fml.LogicalSide as Side
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class TileEntityGeothermalGenerator constructor() : TileBaseUniversalElectricalSource(), IInventoryDefaults,
    ISidedInventory, IConnector, IDisableableMachine {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileBaseUniversalElectricalSource>.getDisplayName()

    private var validSpout = false

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disabled: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var disableCooldown: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var generateWatts: Int = 0

    init {
        this.storage.setMaxExtract(TileEntitySolar.MAX_GENERATE_WATTS.toFloat())
        this.storage.setMaxReceive(TileEntitySolar.MAX_GENERATE_WATTS.toFloat())
    }

    override fun update() {
        if (!this.world.isRemote) {
            this.receiveEnergyGC(null, this.generateWatts.toFloat(), false)
        }

        super.update()

        if (this.ticks % 20 == 0) {
            val below: BlockPos = this.getPos().down()
            val stateBelow: IBlockState = this.world.getBlockState(below)

            val lastValidSpout: Boolean = this.validSpout
            this.validSpout = false
            if (stateBelow.getBlock() === VenusBlocks.spout) {
                var pos1: BlockPos = below.below()
                while (this.getPos().getY() - pos1.getY() < 20) {
                    val state: IBlockState = this.world.getBlockState(pos1)
                    if (state.getBlock() === (VenusModule.sulphuricAcid)!!.getBlock()) {
                        this.validSpout = true
                        break
                    } else if (!state.getBlock().isAir(this.world.getBlockState(pos1), this.world, pos1)) {
                        // Not valid
                        break
                    }
                    pos1 = pos1.below()
                }
            }

            if (this.world.isRemote && this.validSpout != lastValidSpout) {
                // Update active texture
                val state: IBlockState = this.world.getBlockState(this.getPos())
                this.world.sendBlockUpdated(this.getPos(), state, state, 3)
            }
        }

        if (!this.world.isRemote) {
            this.recharge(this.stacks.get(0))

            if (this.disableCooldown > 0) {
                this.disableCooldown--
            }

            this.generateWatts = min(max(this.getGenerate(), 0), MAX_GENERATE_GJ_PER_TICK)
        } else {
            if (this.generateWatts > 0 && this.ticks % ((MAX_GENERATE_GJ_PER_TICK.toFloat() / (this.generateWatts + 1)).toInt() * 5 + 1) == 0) {
                val posX: Double = pos.getX() + 0.5
                val posY: Double = pos.getY() + 1.0
                val posZ: Double = pos.getZ() + 0.5
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX - 0.25, posY, posZ - 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX - 0.25, posY, posZ),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX - 0.25, posY, posZ + 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX, posY, posZ - 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle("acidExhaust", Vector3(posX, posY, posZ), Vector3(0.0, 0.025, 0.0))
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX, posY, posZ + 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX + 0.25, posY, posZ - 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX + 0.25, posY, posZ),
                    Vector3(0.0, 0.025, 0.0)
                )
                GalacticraftPlanets.spawnParticle(
                    "acidExhaust",
                    Vector3(posX + 0.25, posY, posZ + 0.25),
                    Vector3(0.0, 0.025, 0.0)
                )
            }
        }

        this.produce()
    }

    private fun getGenerate(): Int {
        if (this.getDisabled(0)) {
            return 0
        }

        if (!this.validSpout) {
            return 0
        }

        val diff: Int = MAX_GENERATE_GJ_PER_TICK - MIN_GENERATE_GJ_PER_TICK
        return floor((sin((this.ticks / 50.0f).toDouble()) * 0.5f + 0.5f) * diff + MIN_GENERATE_GJ_PER_TICK).toInt()
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        ItemStackHelper.saveAllItems(nbt, this.stacks)
        return nbt
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null || type != NetworkType.POWER) {
            return false
        }

        return direction == this.getElectricOutputDirection()
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    open fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockGeothermalGenerator) {
            return state.getValue(BlockGeothermalGenerator.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getFront().rotateY())
    }

    override fun getElectricOutputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.geothermal_generator.name")
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        if (this.disableCooldown == 0) {
            if (this.disabled != disabled && this.world.isRemote) {
                // Update active texture
                val state: IBlockState = this.world.getBlockState(this.getPos())
                this.world.sendBlockUpdated(this.getPos(), state, state, 3)
            }

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

    override fun stillValid(player: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && player.getDistanceSq(
            this.getPos().getX() + 0.5,
            this.getPos().getY() + 0.5,
            this.getPos().getZ() + 0.5
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

    open fun hasValidSpout(): Boolean {
        return validSpout
    }

    companion object {
        const val MAX_GENERATE_GJ_PER_TICK: Int = 200

        const val MIN_GENERATE_GJ_PER_TICK: Int = 30
    }
}
