package micdoodle8.mods.galacticraft.core.fluid

import java.util.EnumSet
import java.util.HashSet

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.grid.IElectricityNetwork
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import java.util.*

/**
 * A helper class that provides additional useful functions to interact with the
 * ElectricityNetwork
 *
 * @author Calclavia
 */
object NetworkHelper {
    @JvmStatic
    fun getDirections(tileEntity: TileEntity?, type: NetworkType?): EnumSet<EnumFacing?> {
        val possibleSides: EnumSet<EnumFacing?> = EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)

        if (tileEntity is IConnector) {
            for (i: Int in 0..5) {
                val direction: EnumFacing? = EnumFacing.from3DDataValue(i)
                if ((tileEntity as IConnector).canConnect(direction, type)) {
                    possibleSides.add(direction)
                }
            }
        }

        return possibleSides
    }

    /**
     * @param tileEntity           - The TileEntity's sides.
     * @param approachingDirection - The directions that can be connected.
     * @return A list of networks from all specified sides. There will be no
     * repeated ElectricityNetworks and it will never return null.
     */
    @JvmStatic
    fun getNetworksFromMultipleSides(
        tileEntity: TileEntity,
        approachingDirection: EnumSet<EnumFacing?>
    ): MutableSet<IElectricityNetwork?> {
        val connectedNetworks: MutableSet<IElectricityNetwork?> = HashSet<IElectricityNetwork?>()

        val tileVec: BlockVec3 = BlockVec3(tileEntity)
        for (side: EnumFacing in EnumFacing.values()) {
            if (approachingDirection.contains(side)) {
                val outputConductor: TileEntity? = tileVec.getTileEntityOnSide(tileEntity.getLevel(), side)
                val electricityNetwork: IElectricityNetwork? = getElectricalNetworkFromTileEntity(outputConductor, side)

                if (electricityNetwork != null) {
                    connectedNetworks.add(electricityNetwork)
                }
            }
        }

        return connectedNetworks
    }

    /**
     * Tries to find the electricity network based in a tile entity and checks
     * to see if it is a conductor. All machines should use this function to
     * search for a connecting conductor around it.
     *
     * @param tileEntity        - The TileEntity conductor
     * @param approachDirection - The direction you are approaching this wire from.
     * @return The ElectricityNetwork or null if not found.
     */
    @JvmStatic
    fun getElectricalNetworkFromTileEntity(
        tileEntity: TileEntity?,
        approachDirection: EnumFacing
    ): IElectricityNetwork? {
        if (tileEntity != null) {
            if (tileEntity is INetworkProvider) {
                if (tileEntity is IConnector) {
                    if ((tileEntity as IConnector).canConnect(approachDirection.getOpposite(), NetworkType.POWER)) {
                        if ((tileEntity as INetworkProvider).getNetwork() is IElectricityNetwork) {
                            return (tileEntity as INetworkProvider).getNetwork() as IElectricityNetwork?
                        }
                    }
                } else {
                    if ((tileEntity as INetworkProvider).getNetwork() is IElectricityNetwork) {
                        return (tileEntity as INetworkProvider).getNetwork() as IElectricityNetwork?
                    }
                }
            }
        }

        return null
    }

    @JvmStatic
    fun getFluidNetworkFromTile(tileEntity: TileEntity?, approachDirection: EnumFacing): FluidNetwork? {
        if (tileEntity != null) {
            if (tileEntity is INetworkProvider) {
                if (tileEntity is IConnector) {
                    if ((tileEntity as IConnector).canConnect(approachDirection.getOpposite(), NetworkType.FLUID)) {
                        if ((tileEntity as INetworkProvider).getNetwork() is FluidNetwork) {
                            return (tileEntity as INetworkProvider).getNetwork() as FluidNetwork?
                        }
                    }
                } else {
                    if ((tileEntity as INetworkProvider).getNetwork() is FluidNetwork) {
                        return (tileEntity as INetworkProvider).getNetwork() as FluidNetwork?
                    }
                }
            }
        }

        return null
    } //    public static IHydrogenNetwork getHydrogenNetworkFromTileEntity(TileEntity tileEntity, EnumFacing approachDirection)
    //    {
    //        if (tileEntity != null)
    //        {
    //            if (tileEntity instanceof INetworkProvider)
    //            {
    //                if (tileEntity instanceof IConnector)
    //                {
    //                    if (((IConnector) tileEntity).canConnect(approachDirection.getOpposite(), NetworkType.HYDROGEN))
    //                    {
    //                        if (((INetworkProvider) tileEntity).getNetwork() instanceof IHydrogenNetwork)
    //                        {
    //                            return (IHydrogenNetwork) ((INetworkProvider) tileEntity).getNetwork();
    //                        }
    //                    }
    //                }
    //                else
    //                {
    //                    if (((INetworkProvider) tileEntity).getNetwork() instanceof IHydrogenNetwork)
    //                    {
    //                        return (IHydrogenNetwork) ((INetworkProvider) tileEntity).getNetwork();
    //                    }
    //                }
    //            }
    //        }
    //
    //        return null;
    //    }
}
