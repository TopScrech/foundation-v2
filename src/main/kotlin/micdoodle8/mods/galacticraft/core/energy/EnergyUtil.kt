package micdoodle8.mods.galacticraft.core.energy

import cofh.redstoneflux.api.IEnergyConnection
import cofh.redstoneflux.api.IEnergyContainerItem
import cofh.redstoneflux.api.IEnergyHandler
import cofh.redstoneflux.api.IEnergyProvider
import cofh.redstoneflux.api.IEnergyReceiver
import micdoodle8.mods.galacticraft.core.util.GCLog

import cofh.redstoneflux.api.*
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorageTile
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseConductor
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import kotlin.math.floor

object EnergyUtil {
    private val isRFLoaded = EnergyConfigHandler.isRFAPILoaded()
    private val isRF1Loaded = EnergyConfigHandler.isRFAPIv1Loaded()
    private val isRF2Loaded = EnergyConfigHandler.isRFAPIv2Loaded()

    @JvmField
    var clazzEnderIOCable: Class<*>? = null

    @JvmField
    var clazzMFRRednetEnergyCable: Class<*>? = null

    @JvmField
    var clazzRailcraftEngine: Class<*>? = null

    @JvmField
    var initialisedCompatibilityMethods: Boolean = initialiseCompatibilityMethods()

    @JvmStatic
    fun getAdjacentPowerConnections(tile: TileEntity): Array<TileEntity?> {
        val adjacentConnections: Array<TileEntity?> = arrayOfNulls<TileEntity>(6)

        val thisVec: BlockVec3 = BlockVec3(tile)
        for (direction: EnumFacing in EnumFacing.VALUES) {
            if (tile is IConductor && !(tile as IConductor).canConnect(direction, NetworkType.POWER)) {
                continue
            }

            val tileEntity: TileEntity? = thisVec.getTileEntityOnSide(tile.getWorld(), direction)

            if (tileEntity == null) {
                continue
            }

            if (tileEntity is IConnector) {
                if ((tileEntity as IConnector).canConnect(direction.getOpposite(), NetworkType.POWER)) {
                    adjacentConnections[direction.ordinal] = tileEntity
                }
                continue
            }

            if (isRFLoaded && tileEntity is IEnergyConnection) {
                if (isRF2Loaded && (tileEntity is IEnergyProvider || tileEntity is IEnergyReceiver) || isRF1Loaded && tileEntity is IEnergyHandler || clazzRailcraftEngine != null && clazzRailcraftEngine!!.isInstance(
                        tileEntity
                    )
                ) {
                    if (clazzMFRRednetEnergyCable != null && clazzMFRRednetEnergyCable!!.isInstance(tileEntity)) {
                        continue
                    }

                    if ((tileEntity as IEnergyConnection).canConnectEnergy(direction.getOpposite())) {
                        adjacentConnections[direction.ordinal] = tileEntity
                    }
                }
                continue
            }

            if (hasCapability(tileEntity, CapabilityEnergy.ENERGY, direction.getOpposite())) {
                //Do not connect GC wires directly to power conduits
                if (clazzEnderIOCable != null && clazzEnderIOCable!!.isInstance(tileEntity)) {
                    continue
                }

                val forgeEnergy: IEnergyStorage? =
                    getCapability<IEnergyStorage?>(tileEntity, CapabilityEnergy.ENERGY, direction.getOpposite())
                if (forgeEnergy != null && (forgeEnergy.canReceive() && !EnergyConfigHandler.disableFEOutput || forgeEnergy.canExtract() && !EnergyConfigHandler.disableFEInput)) {
                    adjacentConnections[direction.ordinal] = tileEntity
                }
            }
        }

        return adjacentConnections
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
    @JvmStatic
    @Throws(Exception::class)
    fun setAdjacentPowerConnections(
        conductor: TileEntity,
        connectedAcceptors: MutableList<Any?>,
        directions: MutableList<EnumFacing?>
    ) {
        val thisVec: BlockVec3 = BlockVec3(conductor)
        val world: World = conductor.getWorld()
        for (direction: EnumFacing in EnumFacing.VALUES) {
            val tileEntity: TileEntity? = thisVec.getTileEntityOnSide(world, direction)

            if (tileEntity == null || tileEntity is IConductor)  //world.getTileEntity will not have returned an invalid tile, invalid tiles are null
            {
                continue
            }

            val sideFrom: EnumFacing = direction.getOpposite()

            if (tileEntity is IElectrical) {
                if ((tileEntity as IElectrical).canConnect(sideFrom, NetworkType.POWER)) {
                    connectedAcceptors.add(tileEntity)
                    directions.add(sideFrom)
                }
                continue
            }

            if ((isRF2Loaded && tileEntity is IEnergyReceiver) || (isRF1Loaded && tileEntity is IEnergyHandler)) {
                if (clazzMFRRednetEnergyCable != null && clazzMFRRednetEnergyCable!!.isInstance(tileEntity)) {
                    continue
                }

                if ((tileEntity as IEnergyConnection).canConnectEnergy(sideFrom)) {
                    connectedAcceptors.add(tileEntity)
                    directions.add(sideFrom)
                }
                continue
            }

            if (!EnergyConfigHandler.disableFEOutput && hasCapability(tileEntity, CapabilityEnergy.ENERGY, sideFrom)) {
                if (clazzEnderIOCable != null && clazzEnderIOCable!!.isInstance(tileEntity)) {
                    continue
                }

                val forgeEnergy: IEnergyStorage? =
                    getCapability<IEnergyStorage?>(tileEntity, CapabilityEnergy.ENERGY, sideFrom)
                if (forgeEnergy != null && forgeEnergy.canReceive()) {
                    connectedAcceptors.add(forgeEnergy)
                    directions.add(sideFrom)
                }
            }
        }
        return
    }

    @JvmStatic
    fun otherModsEnergyTransfer(tileAdj: TileEntity?, inputAdj: EnumFacing?, toSend: Float, simulate: Boolean): Float {
        if (isRF2Loaded && !EnergyConfigHandler.disableRFOutput && tileAdj is IEnergyReceiver) {
            val sent: Float = (tileAdj as IEnergyReceiver).receiveEnergy(
                inputAdj,
                floor((toSend * EnergyConfigHandler.TO_RF_RATIO).toDouble()).toInt(),
                simulate
            ) / EnergyConfigHandler.TO_RF_RATIO
            //          GCLog.debug("Beam/storage offering RF2 up to " + toSend + " into pipe, it accepted " + sent);
            return sent
        } else if (!EnergyConfigHandler.disableFEOutput && hasCapability(tileAdj, CapabilityEnergy.ENERGY, inputAdj)) {
            val forgeEnergy: IEnergyStorage? =
                getCapability<IEnergyStorage?>(tileAdj, CapabilityEnergy.ENERGY, inputAdj)
            if (forgeEnergy != null && forgeEnergy.canReceive()) {
                val sent: Float = forgeEnergy.receiveEnergy(
                    floor((toSend * EnergyConfigHandler.TO_RF_RATIO).toDouble()).toInt(),
                    simulate
                ) / EnergyConfigHandler.TO_RF_RATIO
                return sent
            }
        }

        return 0f
    }

    @JvmStatic
    fun otherModsEnergyExtract(tileAdj: TileEntity?, inputAdj: EnumFacing?, toPull: Float, simulate: Boolean): Float {
        if (isRF2Loaded && !EnergyConfigHandler.disableRFInput && tileAdj is IEnergyProvider) {
            val sent: Float = (tileAdj as IEnergyProvider).extractEnergy(
                inputAdj,
                floor((toPull * EnergyConfigHandler.TO_RF_RATIO).toDouble()).toInt(),
                simulate
            ) / EnergyConfigHandler.TO_RF_RATIO
            return sent
        } else if (!EnergyConfigHandler.disableFEInput && hasCapability(tileAdj, CapabilityEnergy.ENERGY, inputAdj)) {
            val forgeEnergy: IEnergyStorage? =
                getCapability<IEnergyStorage?>(tileAdj, CapabilityEnergy.ENERGY, inputAdj)
            if (forgeEnergy != null && forgeEnergy.canExtract()) {
                val sent: Float = forgeEnergy.extractEnergy(
                    floor((toPull * EnergyConfigHandler.TO_RF_RATIO).toDouble()).toInt(),
                    simulate
                ) / EnergyConfigHandler.TO_RF_RATIO
                return sent
            }
        }

        return 0f
    }


    /**
     * Test whether an energy connection can be made to a tile using other mods' energy methods.
     *
     *
     * Parameters:
     *
     * @param tileAdj  - the tile under test, it might be an energy tile from another mod
     * @param inputAdj - the energy input side for that tile which is under test
     */
    @JvmStatic
    fun otherModCanReceive(tileAdj: TileEntity?, inputAdj: EnumFacing?): Boolean {
        if (tileAdj is TileBaseConductor || tileAdj is EnergyStorageTile) {
            return false //Do not try using other mods' methods to connect to GC's own tiles
        }

        if (isRF1Loaded && tileAdj is IEnergyHandler || isRF2Loaded && tileAdj is IEnergyReceiver) {
            return (tileAdj as IEnergyConnection).canConnectEnergy(inputAdj)
        } else if (hasCapability(tileAdj, CapabilityEnergy.ENERGY, inputAdj)) {
            return (micdoodle8.mods.galacticraft.core.energy.EnergyUtil.getCapability<IEnergyStorage?>(
                tileAdj,
                net.minecraftforge.energy.CapabilityEnergy.ENERGY,
                inputAdj
            )!!.canReceive())
        }

        return false
    }

    @JvmStatic
    fun otherModCanProduce(tileAdj: TileEntity?, side: EnumFacing?): Boolean {
        if (tileAdj is TileBaseConductor || tileAdj is EnergyStorageTile) {
            return false //Do not try using other mods' methods to connect to GC's own tiles
        }

        if (hasCapability(tileAdj, CapabilityEnergy.ENERGY, side)) {
            return (micdoodle8.mods.galacticraft.core.energy.EnergyUtil.getCapability<IEnergyStorage?>(
                tileAdj,
                net.minecraftforge.energy.CapabilityEnergy.ENERGY,
                side
            )!!.canExtract())
        }

        return false
    }

    @JvmStatic
    fun initialiseCompatibilityMethods(): Boolean {
        try {
            clazzEnderIOCable = Class.forName("crazypants.enderio.conduits.conduit.TileConduitBundle")
        } catch (e: Exception) {
        }
        try {
            clazzMFRRednetEnergyCable =
                Class.forName("powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy")
        } catch (e: Exception) {
        }
        try {
            clazzRailcraftEngine = Class.forName("mods.railcraft.common.blocks.machine.beta.TileEngine")
        } catch (e: Exception) {
        }

        return true
    }

    @JvmStatic
    fun isElectricItem(item: Item?): Boolean {
        if (item is IItemElectric) return true

        if (item == null) return false

        if (EnergyConfigHandler.isRFAPILoaded()) {
            if (item is IEnergyContainerItem) return true
        }

        return false
    }

    @JvmStatic
    fun isChargedElectricItem(stack: ItemStack): Boolean {
        if (stack.isEmpty()) return false

        val item: Item = stack.getItem()
        if (item is IItemElectric) {
            return (item as IItemElectric).getElectricityStored(stack) > 0
        }

        if (item === Items.AIR) return false

        if (EnergyConfigHandler.isRFAPILoaded()) {
            if (item is IEnergyContainerItem) return (item as IEnergyContainerItem).getEnergyStored(stack) > 0
        }

        return false
    }

    @JvmStatic
    fun isFillableElectricItem(stack: ItemStack): Boolean {
        if (stack.isEmpty()) return false

        val item: Item = stack.getItem()
        if (item is IItemElectric) {
            return (item as IItemElectric).getElectricityStored(stack) < (item as IItemElectric).getMaxElectricityStored(
                stack
            )
        }

        if (item === Items.AIR) return false

        if (EnergyConfigHandler.isRFAPILoaded()) {
            if (item is IEnergyContainerItem) return (item as IEnergyContainerItem).getEnergyStored(stack) < (item as IEnergyContainerItem).getMaxEnergyStored(
                stack
            )
        }

        return false
    }

    @JvmStatic
    fun hasCapability(provider: ICapabilityProvider?, capability: Capability<*>, side: EnumFacing?): Boolean {
        return if (provider == null || capability == null) false else provider.hasCapability(capability, side)
    }

    @JvmStatic
    fun <T> getCapability(provider: ICapabilityProvider?, capability: Capability<T?>, side: EnumFacing?): T? {
        return if (provider == null || capability == null) null else provider.getCapability<T?>(capability, side)
    }
}
