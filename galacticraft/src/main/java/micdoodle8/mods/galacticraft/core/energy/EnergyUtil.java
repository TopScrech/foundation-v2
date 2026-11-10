package micdoodle8.mods.galacticraft.core.energy;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;

import micdoodle8.mods.galacticraft.api.item.IItemElectric;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorageTile;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseConductor;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.List;

public class EnergyUtil
{

    private static boolean isRFLoaded = EnergyConfigHandler.isRFAPILoaded();
    private static boolean isRF1Loaded = EnergyConfigHandler.isRFAPIv1Loaded();
    private static boolean isRF2Loaded = EnergyConfigHandler.isRFAPIv2Loaded();

    public static Class<?> clazzEnderIOCable = null;
    public static Class<?> clazzMFRRednetEnergyCable = null;
    public static Class<?> clazzRailcraftEngine = null;

    public static boolean initialisedCompatibilityMethods = EnergyUtil.initialiseCompatibilityMethods();

    public static TileEntity[] getAdjacentPowerConnections(TileEntity tile)
    {
        final TileEntity[] adjacentConnections = new TileEntity[6];

        BlockVec3 thisVec = new BlockVec3(tile);
        for (EnumFacing direction : EnumFacing.VALUES)
        {
            if (tile instanceof IConductor && !((IConductor)tile).canConnect(direction, NetworkType.POWER))
            {
                continue;
            }
            
            TileEntity tileEntity = thisVec.getTileEntityOnSide(tile.getWorld(), direction);

            if (tileEntity == null)
            {
                continue;
            }

            if (tileEntity instanceof IConnector)
            {
                if (((IConnector) tileEntity).canConnect(direction.getOpposite(), NetworkType.POWER))
                {
                    adjacentConnections[direction.ordinal()] = tileEntity;
                }
                continue;
            }

            if (isRFLoaded && tileEntity instanceof IEnergyConnection)
            {
                if (isRF2Loaded && (tileEntity instanceof IEnergyProvider || tileEntity instanceof IEnergyReceiver) || isRF1Loaded && tileEntity instanceof IEnergyHandler || clazzRailcraftEngine != null && clazzRailcraftEngine.isInstance(tileEntity))
                {
                    if (clazzMFRRednetEnergyCable != null && clazzMFRRednetEnergyCable.isInstance(tileEntity))
                    {
                        continue;
                    }

                    if (((IEnergyConnection) tileEntity).canConnectEnergy(direction.getOpposite()))
                    {
                        adjacentConnections[direction.ordinal()] = tileEntity;
                    }
                }
                continue;
            }
            
            if (hasCapability(tileEntity, net.minecraftforge.energy.CapabilityEnergy.ENERGY, direction.getOpposite()))
            {
                //Do not connect GC wires directly to power conduits
                if (clazzEnderIOCable != null && clazzEnderIOCable.isInstance(tileEntity))
                {
                    continue;
                }

                net.minecraftforge.energy.IEnergyStorage forgeEnergy = getCapability(tileEntity, net.minecraftforge.energy.CapabilityEnergy.ENERGY, direction.getOpposite());
                if (forgeEnergy != null && (forgeEnergy.canReceive() && !EnergyConfigHandler.disableFEOutput || forgeEnergy.canExtract() && !EnergyConfigHandler.disableFEInput))
                {
                    adjacentConnections[direction.ordinal()] = tileEntity;
                }
            }
        }

        return adjacentConnections;
    }
    /**
     * Similar to getAdjacentPowerConnections but specific to energy receivers only
     * Adds the adjacent power connections found to the passed acceptors, directions parameter Lists
     * (Note: an acceptor can therefore sometimes be entered in the Lists more than once, with a different direction each time:
     * this would represent GC wires connected to the acceptor on more than one side.)
     * 
     * @param conductor
     * @param connectedAcceptors
     * @param directions
     * @throws Exception
     */
    public static void setAdjacentPowerConnections(TileEntity conductor, List<Object> connectedAcceptors, List <EnumFacing> directions) throws Exception
    {
        final BlockVec3 thisVec = new BlockVec3(conductor);
        final World world = conductor.getWorld(); 
        for (EnumFacing direction : EnumFacing.VALUES)
        {
            TileEntity tileEntity = thisVec.getTileEntityOnSide(world, direction);
            
            if (tileEntity == null || tileEntity instanceof IConductor)  //world.getTileEntity will not have returned an invalid tile, invalid tiles are null
            {
                continue;
            }
            
            EnumFacing sideFrom = direction.getOpposite();

            if (tileEntity instanceof IElectrical)
            {
                if (((IElectrical) tileEntity).canConnect(sideFrom, NetworkType.POWER))
                {
                    connectedAcceptors.add(tileEntity);
                    directions.add(sideFrom);
                }
                continue;
            }

            if ((isRF2Loaded && tileEntity instanceof IEnergyReceiver) || (isRF1Loaded && tileEntity instanceof IEnergyHandler))
            {
                if (clazzMFRRednetEnergyCable != null && clazzMFRRednetEnergyCable.isInstance(tileEntity))
                {
                    continue;
                }

                if (((IEnergyConnection) tileEntity).canConnectEnergy(sideFrom))
                {
                    connectedAcceptors.add(tileEntity);
                    directions.add(sideFrom);
                }
                continue;
            }
            
            if (!EnergyConfigHandler.disableFEOutput && hasCapability(tileEntity, net.minecraftforge.energy.CapabilityEnergy.ENERGY, sideFrom))
            {
                if (clazzEnderIOCable != null && clazzEnderIOCable.isInstance(tileEntity))
                {
                    continue;
                }

                net.minecraftforge.energy.IEnergyStorage forgeEnergy = getCapability(tileEntity, net.minecraftforge.energy.CapabilityEnergy.ENERGY, sideFrom); 
                if (forgeEnergy != null && forgeEnergy.canReceive())
                {
                    connectedAcceptors.add(forgeEnergy);
                    directions.add(sideFrom);
                }
            }
        }
        return;
    }
    
    public static float otherModsEnergyTransfer(TileEntity tileAdj, EnumFacing inputAdj, float toSend, boolean simulate)
    {
        if (isRF2Loaded && !EnergyConfigHandler.disableRFOutput && tileAdj instanceof IEnergyReceiver)
        {
            float sent = ((IEnergyReceiver) tileAdj).receiveEnergy(inputAdj, (int) Math.floor(toSend * EnergyConfigHandler.TO_RF_RATIO), simulate) / EnergyConfigHandler.TO_RF_RATIO;
//          GCLog.debug("Beam/storage offering RF2 up to " + toSend + " into pipe, it accepted " + sent);
            return sent;
        }
        else if (!EnergyConfigHandler.disableFEOutput && hasCapability(tileAdj, net.minecraftforge.energy.CapabilityEnergy.ENERGY, inputAdj))
        {
            net.minecraftforge.energy.IEnergyStorage forgeEnergy = getCapability(tileAdj, net.minecraftforge.energy.CapabilityEnergy.ENERGY, inputAdj);
            if (forgeEnergy != null && forgeEnergy.canReceive())
            {
                float sent = forgeEnergy.receiveEnergy((int) Math.floor(toSend * EnergyConfigHandler.TO_RF_RATIO), simulate) / EnergyConfigHandler.TO_RF_RATIO;
                return sent;
            }
        }

        return 0F;
    }

    public static float otherModsEnergyExtract(TileEntity tileAdj, EnumFacing inputAdj, float toPull, boolean simulate)
    {
        if (isRF2Loaded && !EnergyConfigHandler.disableRFInput && tileAdj instanceof IEnergyProvider)
        {
            float sent = ((IEnergyProvider) tileAdj).extractEnergy(inputAdj, (int) Math.floor(toPull * EnergyConfigHandler.TO_RF_RATIO), simulate) / EnergyConfigHandler.TO_RF_RATIO;
            return sent;
        }
        else if (!EnergyConfigHandler.disableFEInput && hasCapability(tileAdj, net.minecraftforge.energy.CapabilityEnergy.ENERGY, inputAdj))
        {
            net.minecraftforge.energy.IEnergyStorage forgeEnergy = getCapability(tileAdj, net.minecraftforge.energy.CapabilityEnergy.ENERGY, inputAdj);
            if (forgeEnergy != null && forgeEnergy.canExtract())
            {
                float sent = forgeEnergy.extractEnergy((int) Math.floor(toPull * EnergyConfigHandler.TO_RF_RATIO), simulate) / EnergyConfigHandler.TO_RF_RATIO;
                return sent;
            }
        }

        return 0F;
    }


    /**
     * Test whether an energy connection can be made to a tile using other mods' energy methods.
     * <p>
     * Parameters:
     *
     * @param tileAdj  - the tile under test, it might be an energy tile from another mod
     * @param inputAdj - the energy input side for that tile which is under test
     */
    public static boolean otherModCanReceive(TileEntity tileAdj, EnumFacing inputAdj)
    {
        if (tileAdj instanceof TileBaseConductor || tileAdj instanceof EnergyStorageTile)
        {
            return false;  //Do not try using other mods' methods to connect to GC's own tiles
        }

        if (isRF1Loaded && tileAdj instanceof IEnergyHandler || isRF2Loaded && tileAdj instanceof IEnergyReceiver)
        {
            return ((IEnergyConnection) tileAdj).canConnectEnergy(inputAdj);
        }
        else if (hasCapability(tileAdj, net.minecraftforge.energy.CapabilityEnergy.ENERGY, inputAdj))
        {
            return (getCapability(tileAdj, net.minecraftforge.energy.CapabilityEnergy.ENERGY, inputAdj).canReceive());
        }

        return false;
    }

    public static boolean otherModCanProduce(TileEntity tileAdj, EnumFacing side)
    {
        if (tileAdj instanceof TileBaseConductor || tileAdj instanceof EnergyStorageTile)
        {
            return false;  //Do not try using other mods' methods to connect to GC's own tiles
        }

        if (hasCapability(tileAdj, net.minecraftforge.energy.CapabilityEnergy.ENERGY, side))
        {
            return (getCapability(tileAdj, net.minecraftforge.energy.CapabilityEnergy.ENERGY, side).canExtract());
        }

        return false;
    }

    public static boolean initialiseCompatibilityMethods()
    {

        try
        {
            clazzEnderIOCable = Class.forName("crazypants.enderio.conduits.conduit.TileConduitBundle");
        }
        catch (Exception e)
        {
        }
        try
        {
            clazzMFRRednetEnergyCable = Class.forName("powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy");
        }
        catch (Exception e)
        {
        }
        try
        {
            clazzRailcraftEngine = Class.forName("mods.railcraft.common.blocks.machine.beta.TileEngine");
        }
        catch (Exception e)
        {
        }

        return true;
    }
    
    public static boolean isElectricItem(Item item)
    {
        if (item instanceof IItemElectric)
            return true;
        
        if (item == null)
            return false;

        if (EnergyConfigHandler.isRFAPILoaded())
        {
            if (item instanceof IEnergyContainerItem)
                return true;
        }
                    
        return false;
    }
    
    public static boolean isChargedElectricItem(ItemStack stack)
    {
        if (stack.isEmpty())
            return false;

        Item item = stack.getItem();
        if (item instanceof IItemElectric)
        {
            return ((IItemElectric) item).getElectricityStored(stack) > 0;
        }

        if (item == Items.AIR)
            return false;

        if (EnergyConfigHandler.isRFAPILoaded())
        {
            if (item instanceof IEnergyContainerItem)
                return ((IEnergyContainerItem)item).getEnergyStored(stack) > 0;
        }

        return false;
    }

    public static boolean isFillableElectricItem(ItemStack stack)
    {
        if (stack.isEmpty())
            return false;

        Item item = stack.getItem();
        if (item instanceof IItemElectric)
        {
            return ((IItemElectric) item).getElectricityStored(stack) < ((IItemElectric) item).getMaxElectricityStored(stack);
        }

        if (item == Items.AIR)
            return false;

        if (EnergyConfigHandler.isRFAPILoaded())
        {
            if (item instanceof IEnergyContainerItem)
                return ((IEnergyContainerItem)item).getEnergyStored(stack) < ((IEnergyContainerItem)item).getMaxEnergyStored(stack);
        }

        return false;
    }
    
    public static boolean hasCapability(ICapabilityProvider provider, Capability<?> capability, EnumFacing side)
    {
        return (provider == null || capability == null) ? false : provider.hasCapability(capability, side);
    }

    public static <T> T getCapability(ICapabilityProvider provider, Capability<T> capability, EnumFacing side)
    {
        return (provider == null || capability == null) ? null : provider.getCapability(capability, side);
    }

            }
