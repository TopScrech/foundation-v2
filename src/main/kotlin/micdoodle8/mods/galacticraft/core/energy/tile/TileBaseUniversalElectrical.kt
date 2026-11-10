package micdoodle8.mods.galacticraft.core.energy.tile

import micdoodle8.mods.miccore.Annotations.RuntimeInterface
import java.util.EnumSet

import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.tile.ReceiverMode
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import java.util.*

abstract class TileBaseUniversalElectrical : EnergyStorageTile(), IEnergyStorage {
    //	@NetworkedField(targetSide = Side.CLIENT)
    //	public float energyStored = 0;
    override fun getPacketRange(): Double {
        return 12.0
    }

    override fun getPacketCooldown(): Int {
        return 3
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }

    open fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.allOf<EnumFacing?>(EnumFacing::class.java)
    }

    open fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun getRequest(direction: EnumFacing?): Float {
        if (this.getElectricalInputDirections().contains(direction) || direction == null) {
            return super.getRequest(direction)
        }

        return 0f
    }

    override fun receiveElectricity(from: EnumFacing?, receive: Float, tier: Int, doReceive: Boolean): Float {
        if (this.getElectricalInputDirections().contains(from) || from == null) {
            return super.receiveElectricity(from, receive, tier, doReceive)
        }

        return 0f
    }

    //	@Override
    //	public float receiveElectricity(EnumFacing from, ElectricityPack receive, boolean doReceive)
    //	{
    //		if (from == EnumFacing.UNKNOWN || this.getElectricalInputDirections().contains(from))
    //		{
    //			if (!doReceive)
    //			{
    //				return this.getRequest(from);
    //			}
    //
    //			return this.receiveElectricity(receive, doReceive);
    //		}
    //
    //		return 0F;
    //	}
    /**
     * A non-side specific version of receiveElectricity for you to optionally
     * use it internally.
     */
    //	public float receiveElectricity(ElectricityPack receive, boolean doReceive)
    //	{
    //		if (receive != null)
    //		{
    //			float prevEnergyStored = this.getEnergyStored();
    //			float newStoredEnergy = Math.min(this.getEnergyStored() + receive.getWatts(), this.getMaxEnergyStored());
    //
    //			if (doReceive)
    //			{
    //				this.setEnergyStored(newStoredEnergy);
    //			}
    //
    //			return Math.max(newStoredEnergy - prevEnergyStored, 0);
    //		}
    //
    //		return 0;
    //	}
    //	public float receiveElectricity(float energy, boolean doReceive)
    //	{
    //		return this.receiveElectricity(ElectricityPack.getFromWatts(energy, this.getVoltage()), doReceive);
    //	}
    //	@Override
    //	public void setEnergyStored(float energy)
    //	{
    //		this.energyStored = Math.max(Math.min(energy, this.getMaxEnergyStored()), 0);
    //	}
    //	@Override
    //	public float getEnergyStored()
    //	{
    //		return this.energyStored;
    //	}
    //	public boolean canConnect(EnumFacing direction, NetworkType type)
    //	{
    //		if (direction == null || direction.equals(EnumFacing.UNKNOWN) || type != NetworkType.POWER)
    //		{
    //			return false;
    //		}
    //
    //		return this.getElectricalInputDirections().contains(direction) || this.getElectricalOutputDirections().contains(direction);
    //	}
    //	@Override
    //	public float getVoltage()
    //	{
    //		return 0.120F;
    //	}
    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        //		this.energyStored = nbt.getFloat("energyStored");
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        //		nbt.setFloat("energyStored", this.energyStored);
        return nbt
    }

    fun discharge(itemStack: ItemStack?) {
        if (itemStack != null) {
            val item: Item = itemStack.getItem()
            val energyToDischarge: Float = this.getRequest(null)

            if (item is IItemElectric) {
                this.storage.receiveEnergyGC(ElectricItemHelper.dischargeItem(itemStack, energyToDischarge))
            }

            //            else if (EnergyConfigHandler.isRFAPILoaded() && item instanceof IEnergyContainerItem)
//            {
//                this.storage.receiveEnergyGC(((IEnergyContainerItem)item).extractEnergy(itemStack, (int) (energyToDischarge / EnergyConfigHandler.RF_RATIO), false) * EnergyConfigHandler.RF_RATIO);
//            }

            //			else if (GCCoreCompatibilityManager.isTELoaded() && itemStack.getItem() instanceof IEnergyContainerItem)
            //			{
            //				float given = ((IEnergyContainerItem) itemStack.getItem()).extractEnergy(itemStack, (int) Math.floor(this.getRequest(EnumFacing.UNKNOWN) * EnergyConfigHandler.TO_TE_RATIO), false);
            //				this.receiveElectricity(given * EnergyConfigHandler.TE_RATIO, true);
            //			}
        }
    }

    //ForgeEnergy
    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        if (EnergyConfigHandler.disableFEInput) return 0

        return MathHelper.floor(
            super.receiveElectricity(
                null,
                maxReceive * EnergyConfigHandler.RF_RATIO,
                1,
                !simulate
            ) / EnergyConfigHandler.RF_RATIO
        )
    }

    override fun canReceive(): Boolean {
        return !EnergyConfigHandler.disableFEInput
    }

    //ForgeEnergy
    override fun getEnergyStored(): Int {
        if (EnergyConfigHandler.disableFEInput) return 0

        return MathHelper.floor(this.getEnergyStoredGC() / EnergyConfigHandler.RF_RATIO)
    }

    //ForgeEnergy
    override fun getMaxEnergyStored(): Int {
        if (EnergyConfigHandler.disableFEInput) return 0

        return MathHelper.floor(this.getMaxEnergyStoredGC() / EnergyConfigHandler.RF_RATIO)
    }

    //ForgeEnergy
    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        return 0
    }

    //ForgeEnergy
    override fun canExtract(): Boolean {
        return false
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyReceiver", modID = "")
    fun receiveEnergy(from: EnumFacing?, maxReceive: Int, simulate: Boolean): Int {
        if (EnergyConfigHandler.disableRFInput) {
            return 0
        }

        if (!this.getElectricalInputDirections().contains(from)) {
            return 0
        }

        return MathHelper.floor(
            super.receiveElectricity(
                from,
                maxReceive * EnergyConfigHandler.RF_RATIO,
                1,
                !simulate
            ) / EnergyConfigHandler.RF_RATIO
        )
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    fun canConnectEnergy(from: EnumFacing?): Boolean {
        return this.getElectricalInputDirections().contains(from) || this.getElectricalOutputDirections().contains(from)
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    fun getEnergyStored(from: EnumFacing?): Int {
        return MathHelper.floor(this.getEnergyStoredGC() / EnergyConfigHandler.RF_RATIO)
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    fun getMaxEnergyStored(from: EnumFacing?): Int {
        return MathHelper.floor(this.getMaxEnergyStoredGC() / EnergyConfigHandler.RF_RATIO)
    }

    override fun getModeFromDirection(direction: EnumFacing?): ReceiverMode? {
        if (this.getElectricalInputDirections().contains(direction)) {
            return ReceiverMode.RECEIVE
        } else if (this.getElectricalOutputDirections().contains(direction)) {
            return ReceiverMode.EXTRACT
        }

        return null
    }

    /*
     * Compatibility: call this if the facing metadata is updated
     */
    open fun updateFacing() {
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === CapabilityEnergy.ENERGY) {
            return this.getElectricalInputDirections().contains(facing)
        }
        return super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityEnergy.ENERGY) {
            return if (this.getElectricalInputDirections().contains(facing)) this as T else null
        }
        return super.getCapability<T?>(capability, facing)
    }

    companion object {
        @JvmStatic
        fun onUseWrenchBlock(state: IBlockState, world: World, pos: BlockPos, facing: EnumFacing) {
            val metadata: Int = state.getBlock().getMetaFromState(world.getBlockState(pos))
            val change: Int = facing.rotateY().getHorizontalIndex()

            world.setBlockState(pos, state.getBlock().getStateFromMeta(metadata - (metadata % 4) + change), 3)

            val te: TileEntity? = world.getTileEntity(pos)
            if (te is TileBaseUniversalElectrical) {
                te.updateFacing()
            }
        }
    }
}
