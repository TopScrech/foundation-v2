package micdoodle8.mods.galacticraft.planets.venus.tile

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * A universal network that works with multiple energy systems.
 *
 * @author radfast, micdoodle8, Calclavia, Aidancbrady
 */
open class SolarModuleNetwork : IGridNetwork<SolarModuleNetwork?, ITransmitter?, TileEntity?> {
    private val transmitters: MutableSet<ITransmitter> = HashSet<ITransmitter>()

    constructor()

    constructor(toMerge: MutableCollection<SolarModuleNetwork?>) {
        for (network: SolarModuleNetwork? in toMerge) {
            if (network != null) {
                for (transmitter: ITransmitter in network.transmitters) {
                    transmitter.setNetwork(this)
                    this.transmitters.add(transmitter)
                }
                network.unregister()
            }
        }

        this.register()
    }


    /**
     * Refresh validity of each conductor in the network
     */
    open fun refreshWithChecks() {
        val it: MutableIterator<ITransmitter?> = this.transmitters.iterator()
        while (it.hasNext()) {
            val transmitter: ITransmitter? = it.next()

            if (transmitter == null) {
                it.remove()
                continue
            }

            val tile: TileEntity = transmitter as TileEntity
            val world: World = tile.getWorld()
            //Remove any transmitters in unloaded chunks
            if (tile.isInvalid() || world == null || !world.isBlockLoaded(tile.getPos())) {
                it.remove()
                continue
            }

            if (transmitter !== world.getTileEntity(tile.getPos())) {
                it.remove()
                continue
            }

            if (transmitter.getNetwork() !== this) {
                transmitter.setNetwork(this)
                transmitter.onNetworkChanged()
            }
        }
    }

    override fun refresh() {
        val it: MutableIterator<ITransmitter?> = this.transmitters.iterator()
        while (it.hasNext()) {
            val conductor: ITransmitter? = it.next()

            if (conductor == null) {
                it.remove()
                continue
            }

            val tile: TileEntity = conductor as TileEntity
            val world: World = tile.getWorld()
            //Remove any transmitters in unloaded chunks
            if (tile.isInvalid() || world == null) {
                it.remove()
                continue
            }

            if (conductor.getNetwork() !== this) {
                conductor.setNetwork(this)
                conductor.onNetworkChanged()
            }
        }
    }

    /**
     * Refresh all energy acceptors in the network
     */
    private fun refreshAcceptors() {
        this.refreshWithChecks()

        //        try
//        {
//            LinkedList<ITransmitter> conductorsCopy = new LinkedList<>();
//            conductorsCopy.addAll(this.transmitters);
//            //This prevents concurrent modifications if something in the loop causes chunk loading
//            //(Chunk loading can change the network if new transmitters are found)
//            for (ITransmitter conductor : conductorsCopy)
//            {
//                EnergyUtil.setAdjacentPowerConnections((TileEntity) conductor, this.connectedAcceptors, this.connectedDirections);
//            }
//        }
//        catch (Exception e)
//        {
//            FMLLog.severe("GC Aluminium Wire: Error when testing whether another mod's tileEntity can accept energy.");
//            e.printStackTrace();
//        }
    }

    override fun merge(network: SolarModuleNetwork?): SolarModuleNetwork {
        if (network != null && network !== this) {
            val newNetwork: SolarModuleNetwork =
                SolarModuleNetwork(Lists.newArrayList<SolarModuleNetwork?>(this, network))
            newNetwork.refresh()
            return newNetwork
        }

        return this
    }

    private fun destroy() {
        this.transmitters.clear()
    }

    override fun split(splitPoint: ITransmitter?) {
        if (splitPoint is TileEntity) {
            (this.getTransmitters())!!.remove(splitPoint)
            splitPoint.setNetwork(null)

            //If the size of the residual network is 1, it should simply be preserved
            if ((this.getTransmitters())!!.size > 1) {
                val world: World = (splitPoint as TileEntity).getWorld()

                if ((this.getTransmitters())!!.size > 0) {
                    val nextToSplit: Array<ITransmitter?> = arrayOfNulls<ITransmitter>(6)
                    val toDo: BooleanArray = booleanArrayOf(true, true, true, true, true, true)
                    var tileEntity: TileEntity?

                    for (j: Int in 0..5) {
                        when (j) {
                            0 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().down())
                            1 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().up())
                            2 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().north())
                            3 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().south())
                            4 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().west())
                            5 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().east())
                            else ->                                 //Not reachable, only to prevent uninitiated compile errors
                                tileEntity = null
                        }

                        if (tileEntity is ITransmitter) {
                            nextToSplit[j] = tileEntity as ITransmitter
                        } else {
                            toDo[j] = false
                        }
                    }

                    for (i1: Int in 0..5) {
                        if (toDo[i1]) {
                            val connectedBlockA: ITransmitter? = nextToSplit[i1]
                            val finder: NetworkFinderSolar = NetworkFinderSolar(
                                world,
                                BlockVec3((connectedBlockA as TileEntity?)!!),
                                BlockVec3(splitPoint as TileEntity)
                            )
                            val partNetwork: MutableList<ITransmitter?> = finder.exploreNetwork()

                            //Mark any others still to do in the nextToSplit array which are connected to this, as dealt with
                            for (i2: Int in i1 + 1..5) {
                                val connectedBlockB: ITransmitter? = nextToSplit[i2]

                                if (toDo[i2]) {
                                    if (partNetwork.contains(connectedBlockB)) {
                                        toDo[i2] = false
                                    }
                                }
                            }

                            //Now make the new network from partNetwork
                            val newNetwork: SolarModuleNetwork = SolarModuleNetwork()
                            (newNetwork.getTransmitters())!!.addAll(partNetwork)
                            newNetwork.refreshWithChecks()
                            newNetwork.register()
                        }
                    }

                    this.destroy()
                }
            } else if ((this.getTransmitters())!!.size == 0) {
                this.destroy()
            }
        }

        if (this.transmitters.isEmpty()) {
            this.unregister()
        }
    }

    open fun register() {
        (GalacticraftPlanets.proxy)!!.registerNetwork(this)
    }

    open fun unregister() {
        (GalacticraftPlanets.proxy)!!.unregisterNetwork(this)
    }

    open fun addTransmitter(transmitter: ITransmitter) {
        this.transmitters.add(transmitter)
        this.refresh()
    }

    open fun removeTransmitter(transmitter: ITransmitter?) {
        this.transmitters.remove(transmitter)
    }

    override fun getTransmitters(): MutableSet<ITransmitter?>? {
        return (transmitters as MutableSet<ITransmitter?>?)
    }

    override fun toString(): String {
        return "SolarModuleNetwork[" + this.hashCode() + "|Wires:" + (this.getTransmitters())!!.size + "]"
    }
}
