package micdoodle8.mods.galacticraft.core.energy.grid

import cofh.redstoneflux.api.IEnergyReceiver
import micdoodle8.mods.galacticraft.api.transmission.grid.IElectricityNetwork
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fml.common.FMLLog
import java.util.*
import kotlin.math.min

/**
 * A universal network that works with multiple energy systems.
 *
 * @author radfast, micdoodle8, Calclavia, Aidancbrady
 */
open class EnergyNetwork : IElectricityNetwork {
    private val isRF1Loaded = EnergyConfigHandler.isRFAPIv1Loaded() && !EnergyConfigHandler.disableRFOutput
    private val isRF2Loaded = EnergyConfigHandler.isRFAPIv2Loaded() && !EnergyConfigHandler.disableRFOutput

    private val isFELoaded = !EnergyConfigHandler.disableFEOutput

    private var tickDone = -1
    private var totalRequested = 0f
    private var totalStorageExcess = 0f
    private var totalEnergy = 0f
    private var totalSent = 0f
    private var doneScheduled = false
    private var spamstop = false
    private var loopPrevention = false

    @JvmField
    var networkTierGC: Int = 1
    private var producersTierGC = 1

    /*
     * connectedAcceptors is all the acceptors connected to this network
     * connectedDirections is the directions of those connections (from the point of view of the acceptor tile)
     *   Note: each position in those two linked lists matches
     *         so, an acceptor connected on two sides will be in connectedAcceptors twice
     */
    private val connectedAcceptors: MutableList<Any?> = LinkedList<Any?>()
    private val connectedDirections: MutableList<EnumFacing?> = LinkedList<EnumFacing?>()

    /*
     *  availableAcceptors is the acceptors which can receive energy (this tick)
     *  availableconnectedDirections is a map of those acceptors and the directions they will receive from (from the point of view of the acceptor tile)
     *    Note: each acceptor will only be included once in these collections
     *          (there is no point trying to put power into a machine twice from two different sides)
     */
    private val availableAcceptors: MutableSet<Any> = HashSet<Any>()
    private val availableconnectedDirections: MutableMap<Any?, EnumFacing?> = HashMap<Any?, EnumFacing?>()

    private val energyRequests: MutableMap<Any?,Float> = HashMap<Any?,Float>()
    private val ignoreAcceptors: MutableList<TileEntity?> = LinkedList<TileEntity?>()

    private val conductors: MutableSet<IConductor?> = HashSet<IConductor?>()

    override fun getTransmitters(): MutableSet<IConductor?> {
        return this.conductors
    }

    /**
     * Get the total energy request in this network
     *
     * @param ignoreTiles Tiles to ignore in the request calculations (NOTE: only used in initial (internal) check.
     * @return Amount of energy requested in this network
     */
    override fun getRequest(vararg ignoreTiles: TileEntity?): Float {
        if (tickCount != this.tickDone) {
            //Start the new tick - initialise everything
            this.ignoreAcceptors.clear()
            this.ignoreAcceptors.addAll(Arrays.asList<TileEntity?>(*ignoreTiles))
            this.doTickStartCalc()
        }
        return this.totalRequested - this.totalEnergy - this.totalSent
    }

    /**
     * Produce energy into the network
     *
     * @param energy      Amount of energy to send into the network
     * @param doReceive   Whether to put energy into the network (true) or just simulate (false)
     * @param ignoreTiles TileEntities to ignore for energy transfers.
     * @return Amount of energy REMAINING from the passed energy parameter
     */
    override fun produce(energy: Float, doReceive: Boolean, producerTier: Int, vararg ignoreTiles: TileEntity?): Float {
        if (this.loopPrevention) {
            return energy
        }

        if (energy > 0f) {
            if (tickCount != this.tickDone) {
                this.tickDone = tickCount
                //Start the new tick - initialise everything
                this.ignoreAcceptors.clear()
                this.ignoreAcceptors.addAll(Arrays.asList<TileEntity?>(*ignoreTiles))
                this.producersTierGC = 1
                this.doTickStartCalc()
            } else {
                this.ignoreAcceptors.addAll(Arrays.asList<TileEntity?>(*ignoreTiles))
            }

            if (!this.doneScheduled && this.totalRequested > 0.0f) {
                TickHandlerServer.scheduleNetworkTick(this)
                this.doneScheduled = true
            }

            //On a regular mid-tick produce(), just figure out how much is totalEnergy this tick and return the used amount
            //This will return 0 if totalRequested is 0 - for example a network with no acceptors
            val totalEnergyLast: Float = this.totalEnergy

            //Add the energy for distribution by this grid later this tick
            //Note: totalEnergy cannot exceed totalRequested
            if (doReceive) {
                this.totalEnergy += min(energy, this.totalRequested - totalEnergyLast)
                //The field producersTierGC will be the *highest* of any producers putting energy into the network this tick
                if (producerTier > this.producersTierGC) {
                    this.producersTierGC = producerTier
                }
            }

            if (this.totalRequested >= totalEnergyLast + energy) {
                return 0f //All the electricity will be used
            }
            if (totalEnergyLast >= this.totalRequested) {
                return energy //None of the electricity will be used
            }
            return totalEnergyLast + energy - this.totalRequested //Some of the electricity will be used
        }
        return energy
    }

    /**
     * Called on server tick end, from the Galacticraft Core tick handler.
     */
    open fun tickEnd() {
        this.doneScheduled = false
        this.loopPrevention = true

        //Finish the last tick if there was some to send and something to receive it
        if (this.totalEnergy > 0f) {
            //Call doTickStartCalc a second time in case anything has updated meanwhile
            this.doTickStartCalc()

            if (this.totalRequested > 0f) {
                this.totalSent = this.doProduce()
                if (this.totalSent < this.totalEnergy) {
                    //Any spare energy left is retained for the next tick
                    this.totalEnergy -= this.totalSent
                } else {
                    this.totalEnergy = 0f
                }
            } else {
                this.totalEnergy = 0f
            }
        } else {
            this.totalEnergy = 0f
        }

        this.loopPrevention = false
    }

    /**
     * Refreshes all tiles in network, and updates requested energy
     */
    private fun doTickStartCalc() {
        this.tickDone = tickCount
        this.totalSent = 0f
        this.refreshAcceptors()

        if (!EnergyUtil.initialisedCompatibilityMethods) {
            EnergyUtil.initialiseCompatibilityMethods()
        }

        if (this.conductors.size == 0) {
            return
        }

        this.loopPrevention = true

        this.availableAcceptors.clear()
        this.availableconnectedDirections.clear()
        this.energyRequests.clear()
        this.totalRequested = 0.0f
        this.totalStorageExcess = 0f

        if (!this.connectedAcceptors.isEmpty()) {
            var e: Float
            val acceptorDirection: MutableIterator<EnumFacing?> = this.connectedDirections.iterator()
            for (acceptor: Any? in this.connectedAcceptors) {
                //This tries all sides of the acceptor which are connected (see refreshAcceptors())
                val sideFrom: EnumFacing? = acceptorDirection.next()

                //But the grid will only put energy into the acceptor from one side - once it's in availableAcceptors
                if (!this.ignoreAcceptors.contains(acceptor) && !this.availableAcceptors.contains(acceptor)) {
                    e = 0.0f

                    if (acceptor is IElectrical) {
                        e = acceptor.getRequest(sideFrom)
                    } else if (isRF2Loaded && acceptor is IEnergyReceiver) {
                        e = acceptor.receiveEnergy(sideFrom, Int.MAX_VALUE, true) / EnergyConfigHandler.TO_RF_RATIO
                    } else if (isFELoaded && acceptor is IEnergyStorage) {
                        val forgeEnergy: IEnergyStorage = acceptor
                        if (forgeEnergy.canReceive()) {
                            e = forgeEnergy.receiveEnergy(Int.MAX_VALUE, true) / EnergyConfigHandler.TO_RF_RATIO
                        }
                    }

                    if (e > 0.0f) {
                        this.availableAcceptors.add(acceptor!!)
                        this.availableconnectedDirections.put(acceptor, sideFrom)
                        this.energyRequests.put(acceptor, e)
                        this.totalRequested += e
                        if (e > ENERGY_STORAGE_LEVEL) {
                            this.totalStorageExcess += e - ENERGY_STORAGE_LEVEL
                        }
                    }
                }
            }
        }

        this.loopPrevention = false
    }

    /**
     * Complete the energy transfer. Called internally on server tick end.
     *
     * @return Amount of energy SENT to all acceptors
     */
    private fun doProduce(): Float {
        var sent: Float = 0.0f

        if (!this.availableAcceptors.isEmpty()) {
            var energyNeeded: Float = this.totalRequested
            val energyAvailable: Float = this.totalEnergy
            var reducor: Float = 1.0f
            var energyStorageReducor: Float = 1.0f

            if (energyNeeded > energyAvailable) {
                //If not enough energy, try reducing what goes into energy storage (if any)
                energyNeeded -= this.totalStorageExcess
                //If there's still not enough, put the minimum into energy storage (if any) and, anyhow, reduce everything proportionately
                if (energyNeeded > energyAvailable) {
                    energyStorageReducor = 0f
                    reducor = energyAvailable / energyNeeded
                } else {
                    //Energyavailable exceeds the total needed but only if storage does not fill all in one go - this is a common situation
                    energyStorageReducor = (energyAvailable - energyNeeded) / this.totalStorageExcess
                }
            }

            var currentSending: Float
            var sentToAcceptor: Float
            val tierProduced: Int = min(this.producersTierGC, this.networkTierGC)

            var debugTE: Any? = null
            try {
                for (tileEntity: Any in this.availableAcceptors) {
                    debugTE = tileEntity
                    //Exit the loop if there is no energy left at all (should normally not happen, should be some even for the last acceptor)
                    if (sent >= energyAvailable) {
                        break
                    }

                    //The base case is to give each acceptor what it is requesting
                    currentSending = this.energyRequests.get(tileEntity)!!

                    //If it's an energy store, we may need to damp it down if energyStorageReducor is less than 1
                    if (currentSending > ENERGY_STORAGE_LEVEL) {
                        currentSending =
                            ENERGY_STORAGE_LEVEL + (currentSending - ENERGY_STORAGE_LEVEL) * energyStorageReducor
                    }

                    //Reduce everything proportionately if there is not enough energy for all needs
                    currentSending *= reducor

                    if (currentSending > energyAvailable - sent) {
                        currentSending = energyAvailable - sent
                    }

                    val sideFrom: EnumFacing? = this.availableconnectedDirections.get(tileEntity)

                    if (tileEntity is IElectrical) {
                        sentToAcceptor = tileEntity.receiveElectricity(sideFrom, currentSending, tierProduced, true)
                    } else if (isRF2Loaded && tileEntity is IEnergyReceiver) {
                        val currentSendinginRF: Int =
                            if (currentSending >= Int.MAX_VALUE / EnergyConfigHandler.TO_RF_RATIO) Int.MAX_VALUE else (currentSending * EnergyConfigHandler.TO_RF_RATIO).toInt()
                        sentToAcceptor = tileEntity.receiveEnergy(
                            sideFrom,
                            currentSendinginRF,
                            false
                        ) / EnergyConfigHandler.TO_RF_RATIO
                    } else if (isFELoaded && tileEntity is IEnergyStorage) {
                        val currentSendinginRF: Int =
                            if (currentSending >= Int.MAX_VALUE / EnergyConfigHandler.TO_RF_RATIO) Int.MAX_VALUE else (currentSending * EnergyConfigHandler.TO_RF_RATIO).toInt()
                        sentToAcceptor =
                            tileEntity.receiveEnergy(currentSendinginRF, false) / EnergyConfigHandler.TO_RF_RATIO
                    } else {
                        sentToAcceptor = 0f
                    }

                    if (sentToAcceptor / currentSending > 1.002f && sentToAcceptor > 0.01f) {
                        if (!this.spamstop) {
                            FMLLog.info("Energy network: acceptor took too much energy, offered " + currentSending + ", took " + sentToAcceptor + ". " + tileEntity.toString())
                            this.spamstop = true
                        }
                        sentToAcceptor = currentSending
                    }

                    sent += sentToAcceptor
                }
            } catch (e: Exception) {
                GCLog.severe("DEBUG Energy network loop issue, please report this")
                if (debugTE is TileEntity) {
                    GCLog.severe("Problem was likely caused by tile in dim " + GCCoreUtil.getDimensionID(debugTE.getWorld()) + " at " + debugTE.getPos() + " Type:" + debugTE.javaClass.getSimpleName())
                }
            }
        }

        if (tickCount % 200 == 0) {
            this.spamstop = false
        }

        var returnvalue: Float = sent
        if (returnvalue > this.totalEnergy) {
            returnvalue = this.totalEnergy
        }
        if (returnvalue < 0f) {
            returnvalue = 0f
        }
        return returnvalue
    }

    /**
     * Refresh validity of each conductor in the network
     */
    open fun refreshWithChecks() {
        var tierfound: Int = Int.MAX_VALUE
        val it: MutableIterator<IConductor?> = this.conductors.iterator()
        while (it.hasNext()) {
            val conductor: IConductor? = it.next()

            if (conductor == null) {
                it.remove()
                continue
            }

            val tile: TileEntity = conductor as TileEntity
            val world: World = tile.getWorld()
            //Remove any conductors in unloaded chunks
            if (tile.isInvalid() || world == null || !world.isBlockLoaded(tile.getPos())) {
                it.remove()
                continue
            }

            if (conductor !== world.getTileEntity(tile.getPos())) {
                it.remove()
                continue
            }

            if (conductor.getTierGC() < tierfound) {
                tierfound = conductor.getTierGC()
            }

            if (conductor.getNetwork() !== this) {
                conductor.setNetwork(this)
                conductor.onNetworkChanged()
            }
        }

        //This will set the network tier to 2 if all the conductors are tier 2
        if (tierfound == Int.MAX_VALUE) {
            tierfound = 1
        }
        this.networkTierGC = tierfound
    }

    override fun refresh() {
        var tierfound: Int = Int.MAX_VALUE
        val it: MutableIterator<IConductor?> = this.conductors.iterator()
        while (it.hasNext()) {
            val conductor: IConductor? = it.next()

            if (conductor == null) {
                it.remove()
                continue
            }

            val tile: TileEntity = conductor as TileEntity
            val world: World = tile.getWorld()
            //Remove any conductors in unloaded chunks
            if (tile.isInvalid() || world == null) {
                it.remove()
                continue
            }

            if (conductor.getTierGC() < tierfound) {
                tierfound = conductor.getTierGC()
            }

            if (conductor.getNetwork() !== this) {
                conductor.setNetwork(this)
                conductor.onNetworkChanged()
            }
        }

        //This will set the network tier to 2 if all the conductors are tier 2, etc
        if (tierfound == Int.MAX_VALUE) {
            tierfound = 1
        }
        this.networkTierGC = tierfound
    }

    /**
     * Refresh all energy acceptors in the network
     */
    private fun refreshAcceptors() {
        this.connectedAcceptors.clear()
        this.connectedDirections.clear()

        this.refreshWithChecks()

        try {
            val conductorsCopy: LinkedList<IConductor?> = LinkedList<IConductor?>()
            conductorsCopy.addAll(this.conductors)
            //This prevents concurrent modifications if something in the loop causes chunk loading
            //(Chunk loading can change the network if new conductors are found)
            for (conductor: IConductor? in conductorsCopy) {
                EnergyUtil.setAdjacentPowerConnections(
                    (conductor as TileEntity?)!!,
                    this.connectedAcceptors,
                    this.connectedDirections
                )
            }
        } catch (e: Exception) {
            FMLLog.severe("GC Aluminium Wire: Error when testing whether another mod's tileEntity can accept energy.")
            e.printStackTrace()
        }
    }

    /**
     * Combine this network with another electricitynetwork
     *
     * @param network Network to merge with
     * @return The final, joined network
     */
    override fun merge(network: IElectricityNetwork?): IElectricityNetwork {
        if (network != null && network !== this) {
            val thisNetwork: MutableSet<IConductor?> = this.conductors
            val thatNetwork: MutableSet<IConductor?> = (network.getTransmitters())!!
            if (thisNetwork.size >= thatNetwork.size) {
                thisNetwork.addAll(thatNetwork)
                this.refresh()
                if (network is EnergyNetwork) {
                    network.destroy()
                }
                return this
            } else {
                thatNetwork.addAll(thisNetwork)
                network.refresh()
                this.destroy()
                return network
            }
        }

        return this
    }

    private fun destroy() {
        this.conductors.clear()
        this.connectedAcceptors.clear()
        this.availableAcceptors.clear()
        this.totalEnergy = 0f
        this.totalRequested = 0f
        TickHandlerServer.removeNetworkTick(this)
    }

    override fun split(splitPoint: IConductor?) {
        if (splitPoint is TileEntity) {
            this.getTransmitters().remove(splitPoint)
            splitPoint.setNetwork(null)

            //If the size of the residual network is 1, it should simply be preserved 
            if (this.getTransmitters().size > 1) {
                val world: World = (splitPoint as TileEntity).getWorld()

                if (this.getTransmitters().size > 0) {
                    val nextToSplit: Array<TileEntity> = (arrayOfNulls<TileEntity>(6) as Array<TileEntity>)
                    val toDo: BooleanArray = booleanArrayOf(true, true, true, true, true, true)
                    var tileEntity: TileEntity?

                    val xCoord: Int = (splitPoint as TileEntity).getPos().getX()
                    val yCoord: Int = (splitPoint as TileEntity).getPos().getY()
                    val zCoord: Int = (splitPoint as TileEntity).getPos().getZ()

                    for (j: Int in 0..5) {
                        when (j) {
                            0 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().down())
                            1 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().up())
                            2 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().north())
                            3 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().south())
                            4 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().west())
                            5 -> tileEntity = world.getTileEntity((splitPoint as TileEntity).getPos().east())
                            else ->                             //Not reachable, only to prevent uninitiated compile errors
                                tileEntity = null
                        }

                        if (tileEntity is IConductor) {
                            nextToSplit[j] = tileEntity
                        } else {
                            toDo[j] = false
                        }
                    }

                    for (i1: Int in 0..5) {
                        if (toDo[i1]) {
                            val connectedBlockA: TileEntity? = nextToSplit[i1]
                            val finder: NetworkFinder =
                                NetworkFinder(world, BlockVec3((connectedBlockA)!!), BlockVec3(splitPoint as TileEntity))
                            val partNetwork: MutableList<IConductor?> = finder.exploreNetwork()

                            //Mark any others still to do in the nextToSplit array which are connected to this, as dealt with
                            for (i2: Int in i1 + 1..5) {
                                val connectedBlockB: TileEntity? = nextToSplit[i2]

                                if (toDo[i2]) {
                                    if ((partNetwork as Collection<Any?>).contains(connectedBlockB)) {
                                        toDo[i2] = false
                                    }
                                }
                            }

                            //Now make the new network from partNetwork
                            val newNetwork: EnergyNetwork = EnergyNetwork()
                            newNetwork.getTransmitters().addAll(partNetwork)
                            newNetwork.refreshWithChecks()
                        }
                    }

                    this.destroy()
                }
            } else if (this.getTransmitters().size == 0) {
                this.destroy()
            }
        }
    }

    override fun toString(): String {
        return "EnergyNetwork[" + this.hashCode() + "|Wires:" + this.getTransmitters().size + "|Acceptors:" + this.connectedAcceptors.size + "]"
    }

    companion object {
        /* Re-written by radfast for better performance
     *
     * Imagine a 30 producer, 80 acceptor network...
     *
     *   Before: it would have called the inner loop in produce() 2400 times each tick.  Not good!
     *
     *   After: the inner loop runs 80 times - part of it is in doTickStartCalc() at/near the tick start, and part of it is in doProduce() at the end of the tick
     */
        @JvmField
        var tickCount: Int = 0

        //This is an energy per tick which exceeds what any normal machine will request, so the requester must be an energy storage - for example, a battery or an energy cube
        private const val ENERGY_STORAGE_LEVEL = 200f
    }
}
