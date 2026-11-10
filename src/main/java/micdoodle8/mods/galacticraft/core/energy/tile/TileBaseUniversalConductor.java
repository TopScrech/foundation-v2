package micdoodle8.mods.galacticraft.core.energy.tile;


import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil;
import micdoodle8.mods.miccore.Annotations.RuntimeInterface;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public abstract class TileBaseUniversalConductor extends TileBaseConductor implements net.minecraftforge.energy.IEnergyStorage
{

    @Override
    public void onNetworkChanged()
    {
    }

    @Override
    public TileEntity[] getAdjacentConnections()
    {
        return EnergyUtil.getAdjacentPowerConnections(this);
    }

    @Override
    public void invalidate()
    {

        super.invalidate();
    }

    @Override
    public void onChunkUnload()
    {

        super.onChunkUnload();
    }
    
    //ForgeEnergy
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (this.getNetwork() == null || EnergyConfigHandler.disableFEInput)
        {
            return 0;
        }

        float receiveGC = maxReceive * EnergyConfigHandler.RF_RATIO;
        float sentGC = receiveGC - this.getNetwork().produce(receiveGC, !simulate, 1);
        return MathHelper.floor(sentGC / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public boolean canReceive()
    {
        return !EnergyConfigHandler.disableFEInput;
    }

    //ForgeEnergy
    @Override
    public int getEnergyStored()
    {
        return 0;
    }

    //ForgeEnergy
    @Override
    public int getMaxEnergyStored()
    {
        if (this.getNetwork() == null || EnergyConfigHandler.disableFEInput)
        {
            return 0;
        }

        return MathHelper.floor(this.getNetwork().getRequest(this) / EnergyConfigHandler.RF_RATIO);
    }

    //ForgeEnergy
    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
         return 0;
    }

    //ForgeEnergy
    @Override
    public boolean canExtract()
    {
        return false;
    }

    @RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyReceiver", modID = "")
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
    {
        if (this.getNetwork() == null)
        {
            return 0;
        }
        float receiveGC = maxReceive * EnergyConfigHandler.RF_RATIO;
        float sentGC = receiveGC - this.getNetwork().produce(receiveGC, !simulate, 1);
        return MathHelper.floor(sentGC / EnergyConfigHandler.RF_RATIO);
    }

    @RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyProvider", modID = "")
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
    {
        return 0;
    }

    @RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    public boolean canConnectEnergy(EnumFacing from)
    {

        return true;
    }

    @RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    public int getEnergyStored(EnumFacing from)
    {
        return 0;
    }

    @RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    public int getMaxEnergyStored(EnumFacing from)
    {
        if (this.getNetwork() == null)
        {
            return 0;
        }

        return MathHelper.floor(this.getNetwork().getRequest(this) / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            return (T) this;
        }

        return super.getCapability(capability, facing);
    }
}
