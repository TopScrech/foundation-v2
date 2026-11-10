package galaxyspace.systems.SolarSystem.planets.overworld.tile

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernStorageModule
import java.util.EnumSet
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockModernStorageModule
import micdoodle8.mods.galacticraft.api.item.IItemElectricBase
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityInventorySource
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import java.util.HashSet
import kotlin.math.floor

open class TileEntityModernStorageModule @JvmOverloads constructor(tier: Int = 1) :
    TileEntityInventorySource("tile.modern_storage_module.name"), ISidedInventory, IConnector {
    @JvmField
    val playersUsing: MutableSet<EntityPlayer> = HashSet<EntityPlayer>()

    @JvmField
    var scaledEnergyLevel: Int = 0

    @JvmField
    var lastScaledEnergyLevel: Int = 0
    private val lastEnergy = 0f

    init {
        this.setTier(tier)
    }

    private fun setTier(tier: Int) {
        BASE_CAPACITY = (5000000 + (5000000 * tier) / 10).toFloat()
        this.storage.setCapacity(BASE_CAPACITY)
        this.storage.setMaxExtract((1800 + (200 * tier)).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)
        this.setTierGC(tier)
    }

    override fun update() {
        /*
		float energy = this.storage.getEnergyStoredGC();
		if (this.getTierGC() == 1 && !this.world.isRemote) {
			if (this.lastEnergy - energy > this.storage.getMaxExtract() - 1) {
				// Deplete faster if being drained at maximum output
				this.storage.extractEnergyGC(25, false);
			}
		}
		this.lastEnergy = energy;
*/

        super.update()

        this.scaledEnergyLevel =
            floor(((this.getEnergyStoredGC() + 49) * 16 / this.getMaxEnergyStoredGC()).toDouble()).toInt()

        if (this.scaledEnergyLevel != this.lastScaledEnergyLevel) {
            this.world.notifyLightSet(this.getPos())
        }

        if (!this.world.isRemote) {
            this.recharge(this.getInventory().get(0))
            this.discharge(this.getInventory().get(1))
        }

        if (!this.world.isRemote) {
            this.produce()
        }

        this.lastScaledEnergyLevel = this.scaledEnergyLevel
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.inventory = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.getInventory())
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        if (this.tierGC == 1 && this.storage.getEnergyStoredGC() > BASE_CAPACITY) {
            this.storage.setEnergyStored(BASE_CAPACITY)
        }

        super.writeToNBT(nbt)

        ItemStackHelper.saveAllItems(nbt, this.getInventory())

        return nbt
    }

    override fun getInventoryStackLimit(): Int {
        return 1
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this
                && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5,
            this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun getSlotsForFace(side: EnumFacing?): IntArray {
        return IntArray(0)
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        return ItemElectricBase.isElectricItem(itemstack.getItem())
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        if (itemstack.getItem() is IItemElectricBase) {
            if (slotID == 0) {
                return (itemstack.getItem() as IItemElectricBase).getTransfer(itemstack) > 0
            } else if (slotID == 1) {
                return (itemstack.getItem() as IItemElectricBase).getElectricityStored(itemstack) > 0
            }
        }
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        if (itemstack.getItem() is IItemElectricBase) {
            if (slotID == 0) {
                return (itemstack.getItem() as IItemElectricBase).getTransfer(itemstack) <= 0
            } else if (slotID == 1) {
                return (itemstack.getItem() as IItemElectricBase).getElectricityStored(itemstack) <= 0
                        || this.getEnergyStoredGC() >= this.getMaxEnergyStoredGC()
            }
        }

        return false
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getElectricInputDirection())
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getElectricOutputDirection())
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null || type != NetworkType.POWER) {
            return false
        }

        return getElectricalInputDirections().contains(direction)
                || getElectricalOutputDirections().contains(direction)
    }

    open fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockModernStorageModule) {
            return (state.getValue(BlockModernStorageModule.FACING))
        }
        return EnumFacing.NORTH
    }

    open fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    override fun getElectricOutputDirection(): EnumFacing {
        return getFront().rotateYCCW()
    }

    companion object {
        private var BASE_CAPACITY = 5000000f
    }
}
