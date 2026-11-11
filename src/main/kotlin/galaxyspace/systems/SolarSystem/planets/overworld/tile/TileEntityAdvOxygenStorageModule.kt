package galaxyspace.systems.SolarSystem.planets.overworld.tile

import foundation.item.legacyItemValue

import net.minecraft.world.ContainerHelper
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvOxygenStorageModule
import java.util.EnumSet
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockOxygenStorageModule
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import galaxyspace.core.prefab.tile.TileEntityInventoryOxygen
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import java.util.HashSet
import kotlin.math.floor
import kotlin.math.min

open class TileEntityAdvOxygenStorageModule @JvmOverloads constructor(tier: Int = 1) :
    TileEntityInventoryOxygen(GSBlocks.OXYGEN_STORAGE_MODULE.getTranslationKey() + ".name", OXYGEN_CAPACITY * tier, 40),
    ISidedInventory {
    @JvmField
    val playersUsing: MutableSet<EntityPlayer> = HashSet<EntityPlayer>()

    @JvmField
    var scaledOxygenLevel: Int = 0
    private var lastScaledOxygenLevel = 0

    // private NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);
    init {
        this.storage.setCapacity(0f)
        this.storage.setMaxExtract(0f)
        this.inventory = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)
    }

    override fun update() {
        if (!this.world.isRemote) {
            val oxygenItemStack: ItemStack = this.getItem(0)
            if (oxygenItemStack != null && oxygenItemStack.getItem() is IItemOxygenSupply) {
                val oxygenItem: IItemOxygenSupply = oxygenItemStack.getItem() as IItemOxygenSupply
                val oxygenDraw: Int = floor(
                    min(
                        this.oxygenPerTick * 2.5f,
                        (this.getMaxOxygenStored() - this.getOxygenStored()).toFloat()
                    ).toDouble()
                ).toInt()
                this.setOxygenStored(getOxygenStored() + oxygenItem.discharge(oxygenItemStack, oxygenDraw))
                if (this.getOxygenStored() > this.getMaxOxygenStored()) {
                    this.setOxygenStored(this.getOxygenStored())
                }
            }
        }

        super.update()

        this.scaledOxygenLevel = this.getScaledOxygenLevel(16)

        if (this.scaledOxygenLevel != this.lastScaledOxygenLevel) {
            this.world.notifyLightSet(this.getPos())
        }

        this.lastScaledOxygenLevel = this.scaledOxygenLevel

        this.produceOxygen(getFront().rotateY().getOpposite())
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.inventory = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.getInventory())
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        ItemStackHelper.saveAllItems(nbt, this.getInventory())
        return nbt
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun shouldPullEnergy(): Boolean {
        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return false
    }

    override fun getElectricInputDirection(): EnumFacing? {
        return null
    }

    override fun getBatteryInSlot(): ItemStack {
        return ItemStack.EMPTY
    }

    override fun shouldUseOxygen(): Boolean {
        return false
    }

    override fun getOxygenProvide(direction: EnumFacing?): Int {
        return if (this.getOxygenOutputDirections().contains(direction)) min(
            OUTPUT_PER_TICK,
            this.getOxygenStored()
        ) else 0
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockOxygenStorageModule) {
            return (state.getValue(BlockOxygenStorageModule.FACING))
        }
        return EnumFacing.NORTH
    }

    override fun getContainerSize(): Int {
        return this.getInventory().size
    }

    override fun getItem(var1: Int): ItemStack {
        return this.getInventory().get(var1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.getInventory(), index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        val oldstack: ItemStack = ContainerHelper.takeItem(this.getInventory(), index)
        if (!oldstack.isEmpty()) {
            this.setChanged()
        }
        return oldstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.getInventory().set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        return slotID == 0 && itemstack != null && itemstack.getItem() is IItemOxygenSupply
    }

    //ISidedInventory
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        if (slotID == 0 && this.canPlaceItem(slotID, itemstack)) {
            return itemstack.legacyItemValue < itemstack.getItem().getMaxDamage()
        }
        return false
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (slotID == 0 && itemstack != null) {
            return FluidUtil.isEmptyContainer(itemstack)
        }
        return false
    }

    //IFluidHandler methods - to allow this to accept Liquid Oxygen
    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        return super.canDrain(from, fluid)
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        return super.drain(from, resource, doDrain)
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        return super.drain(from, maxDrain, doDrain)
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        return super.canFill(from, fluid)
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        return super.fill(from, resource, doFill)
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        return super.getTankInfo(from)
    }

    override fun getOxygenInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getFront().rotateY())
    }

    override fun getOxygenOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getFront().rotateYCCW())
    }

    companion object {
        const val OUTPUT_PER_TICK: Int = 600

        const val OXYGEN_CAPACITY: Int = 240000
    }
}
