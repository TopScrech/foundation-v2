package micdoodle8.mods.galacticraft.core.energy.tile;

import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper;
import micdoodle8.mods.galacticraft.api.item.IItemElectric;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.grid.IElectricityNetwork;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil;
import micdoodle8.mods.miccore.Annotations;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;

import java.util.EnumSet;

public class TileBaseUniversalElectricalSource extends TileBaseUniversalElectrical
{
    /*
     * The main function to output energy each tick from a source.
     *
     * The source will attempt to produce into its outputDirections whatever energy
     * it has available, and will reduce its stored energy by the amount which is in fact used.
     *
     * Max output = this.storage.maxExtract.
     *
     * @return The amount of energy that was used.
     */
    public float produce()
    {
        this.storage.maxExtractRemaining = this.storage.maxExtract;
        float produced = this.extractEnergyGC(null, this.produce(false), false);
        this.storage.maxExtractRemaining -= produced;
        if (this.storage.maxExtractRemaining < 0)
        {
            this.storage.maxExtractRemaining = 0;
        }
        return produced;
    }

    /*
     * Function to produce energy each tick into the outputs of a source.
     * If simulate is true, no energy is in fact transferred.
     *
     * Note: even if simulate is false this does NOT reduce the source's own
     * energy storage by the amount produced, that needs to be done elsewhere
     * See this.produce() for an example.
     */
    public float produce(boolean simulate)
    {
        float amountProduced = 0;

        if (!this.world.isRemote)
        {
            EnumSet<EnumFacing> outputDirections = this.getElectricalOutputDirections();
//            outputDirections.remove(EnumFacing.UNKNOWN);

            BlockVec3 thisVec = new BlockVec3(this);
            for (EnumFacing direction : outputDirections)
            {
                TileEntity tileAdj = thisVec.getTileEntityOnSide(this.world, direction);

                if (tileAdj != null)
                {
                    float toSend = this.extractEnergyGC(null, Math.min(this.getEnergyStoredGC() - amountProduced, this.getEnergyStoredGC() / outputDirections.size()), true);
                    if (toSend <= 0)
                    {
                        continue;
                    }

                    if (tileAdj instanceof TileBaseConductor && ((TileBaseConductor)tileAdj).canConnect(direction.getOpposite(), NetworkType.POWER))
                    {
                        IElectricityNetwork network = ((IConductor) tileAdj).getNetwork();
                        if (network != null)
                        {
                            amountProduced += (toSend - network.produce(toSend, !simulate, this.tierGC, this));
                        }
                    }
                    else if (tileAdj instanceof TileBaseUniversalElectrical)
                    {
                        amountProduced += ((TileBaseUniversalElectrical) tileAdj).receiveElectricity(direction.getOpposite(), toSend, this.tierGC, !simulate);
                    }
                    else
                    {
                        amountProduced += EnergyUtil.otherModsEnergyTransfer(tileAdj, direction.getOpposite(), toSend, simulate);
                    }
                }
            }
        }

        return amountProduced;
    }

    /**
     * Recharges electric item.
     */
    public void recharge(ItemStack itemStack)
    {
        if (itemStack != null && itemStack.getCount() == 1)
        {
            Item item = itemStack.getItem();
            float maxExtractSave = this.storage.getMaxExtract();
            if (this.tierGC > 1)
            {
                this.storage.setMaxExtract(maxExtractSave * 2.5F);
            }
            float energyToCharge = this.storage.extractEnergyGC(this.storage.getMaxExtract(), true);

            if (item instanceof IItemElectric)
            {
                this.storage.extractEnergyGC(ElectricItemHelper.chargeItem(itemStack, energyToCharge), false);
            }
//            else if (EnergyConfigHandler.isRFAPILoaded() && item instanceof IEnergyContainerItem)
//            {
//                this.storage.extractEnergyGC(((IEnergyContainerItem)item).receiveEnergy(itemStack, (int) (energyToCharge * EnergyConfigHandler.TO_RF_RATIO), false) / EnergyConfigHandler.TO_RF_RATIO, false);
//            }

            //			else if (GCCoreCompatibilityManager.isTELoaded() && itemStack.getItem() instanceof IEnergyContainerItem)
            //			{
            //				int accepted = ((IEnergyContainerItem) itemStack.getItem()).receiveEnergy(itemStack, (int) Math.floor(this.getProvide(EnumFacing.UNKNOWN) * EnergyConfigHandler.TO_TE_RATIO), false);
            //				this.provideElectricity(accepted * EnergyConfigHandler.TE_RATIO, true);
            //			}

            if (this.tierGC > 1)
            {
                this.storage.setMaxExtract(maxExtractSave);
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing side)
    {

        return super.hasCapability(cap, side);
    }
    
    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing side)
    {

        return super.getCapability(cap, side);
    }
    
    @Override
    public float getProvide(EnumFacing direction)
    {

        if (this.getElectricalOutputDirections().contains(direction))
        {
            return this.storage.extractEnergyGC(Float.MAX_VALUE, true);
        }

        return 0F;
    }

    public EnumFacing getElectricOutputDirection()
    {
        return null;
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyProvider", modID = "")
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
    {
        if (EnergyConfigHandler.disableRFOutput)
        {
            return 0;
        }

        if (!this.getElectricalOutputDirections().contains(from))
        {
            return 0;
        }

        return MathHelper.floor(this.storage.extractEnergyGC(maxExtract / EnergyConfigHandler.TO_RF_RATIO, !simulate) * EnergyConfigHandler.TO_RF_RATIO);
    }
    
    //ForgeEnergy
    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        if (!canExtract())
            return 0;

        return MathHelper.floor(this.storage.extractEnergyGC(maxExtract / EnergyConfigHandler.TO_RF_RATIO, !simulate) * EnergyConfigHandler.TO_RF_RATIO);
    }

    //ForgeEnergy
    @Override
    public boolean canExtract()
    {
        return !EnergyConfigHandler.disableFEOutput;
    }
}
