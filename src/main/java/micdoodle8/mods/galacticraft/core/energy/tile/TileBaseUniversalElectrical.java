package micdoodle8.mods.galacticraft.core.energy.tile;

import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper;
import micdoodle8.mods.galacticraft.api.item.IItemElectric;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.tile.ReceiverMode;
import micdoodle8.mods.miccore.Annotations.RuntimeInterface;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.EnumSet;


public abstract class TileBaseUniversalElectrical extends EnergyStorageTile implements net.minecraftforge.energy.IEnergyStorage
{

    //	@NetworkedField(targetSide = Side.CLIENT)
    //	public float energyStored = 0;

    @Override
    public double getPacketRange()
    {
        return 12.0D;
    }

    @Override
    public int getPacketCooldown()
    {
        return 3;
    }

    @Override
    public boolean isNetworkedTile()
    {
        return true;
    }

    public EnumSet<EnumFacing> getElectricalInputDirections()
    {
        return EnumSet.allOf(EnumFacing.class);
    }

    public EnumSet<EnumFacing> getElectricalOutputDirections()
    {
        return EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public float getRequest(EnumFacing direction)
    {
        if (this.getElectricalInputDirections().contains(direction) || direction == null)
        {
            return super.getRequest(direction);
        }

        return 0F;
    }

    @Override
    public float receiveElectricity(EnumFacing from, float receive, int tier, boolean doReceive)
    {
        if (this.getElectricalInputDirections().contains(from) || from == null)
        {
            return super.receiveElectricity(from, receive, tier, doReceive);
        }

        return 0F;
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
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        //		this.energyStored = nbt.getFloat("energyStored");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        //		nbt.setFloat("energyStored", this.energyStored);
        return nbt;
    }

    public void discharge(ItemStack itemStack)
    {
        if (itemStack != null)
        {
            Item item = itemStack.getItem();
            float energyToDischarge = this.getRequest(null);

            if (item instanceof IItemElectric)
            {
                this.storage.receiveEnergyGC(ElectricItemHelper.dischargeItem(itemStack, energyToDischarge));
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
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (EnergyConfigHandler.disableFEInput)
            return 0;

        return MathHelper.floor(super.receiveElectricity(null, maxReceive * EnergyConfigHandler.RF_RATIO, 1, !simulate) / EnergyConfigHandler.RF_RATIO);
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
        if (EnergyConfigHandler.disableFEInput)
            return 0;

        return MathHelper.floor(this.getEnergyStoredGC() / EnergyConfigHandler.RF_RATIO);
    }

    //ForgeEnergy
    @Override
    public int getMaxEnergyStored()
    {
        if (EnergyConfigHandler.disableFEInput)
            return 0;

        return MathHelper.floor(this.getMaxEnergyStoredGC() / EnergyConfigHandler.RF_RATIO);
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
        if (EnergyConfigHandler.disableRFInput)
        {
            return 0;
        }

        if (!this.getElectricalInputDirections().contains(from))
        {
            return 0;
        }

        return MathHelper.floor(super.receiveElectricity(from, maxReceive * EnergyConfigHandler.RF_RATIO, 1, !simulate) / EnergyConfigHandler.RF_RATIO);
    }

    @RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    public boolean canConnectEnergy(EnumFacing from)
    {
        return this.getElectricalInputDirections().contains(from) || this.getElectricalOutputDirections().contains(from);
    }

    @RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    public int getEnergyStored(EnumFacing from)
    {
        return MathHelper.floor(this.getEnergyStoredGC() / EnergyConfigHandler.RF_RATIO);
    }

    @RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyHandler", modID = "")
    public int getMaxEnergyStored(EnumFacing from)
    {
        return MathHelper.floor(this.getMaxEnergyStoredGC() / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public ReceiverMode getModeFromDirection(EnumFacing direction)
    {
        if (this.getElectricalInputDirections().contains(direction))
        {
            return ReceiverMode.RECEIVE;
        }
        else if (this.getElectricalOutputDirections().contains(direction))
        {
            return ReceiverMode.EXTRACT;
        }

        return null;
    }

    /*
     * Compatibility: call this if the facing metadata is updated
     */
    public void updateFacing()
    {

    }

    public static void onUseWrenchBlock(IBlockState state, World world, BlockPos pos, EnumFacing facing)
    {
        int metadata = state.getBlock().getMetaFromState(world.getBlockState(pos));
        int change = facing.rotateY().getHorizontalIndex();

        world.setBlockState(pos, state.getBlock().getStateFromMeta(metadata - (metadata % 4) + change), 3);

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBaseUniversalElectrical)
        {
            ((TileBaseUniversalElectrical) te).updateFacing();
        }
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            return this.getElectricalInputDirections().contains(facing);
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            return this.getElectricalInputDirections().contains(facing) ? (T) this : null;
        }
        return super.getCapability(capability, facing);
    }
}
