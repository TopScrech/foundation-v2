package galaxyspace.core.network.trackers

import galaxyspace.GalaxySpace
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.core.network.packet.GSPacketSimple
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import galaxyspace.core.network.trackers.MultiSeatRocketDriverTracker
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.ScheduledDimensionChange
import net.minecraft.entity.player.EntityPlayerMP
import java.util.ArrayList

/**
 * RUS: Следит за действиями "водителя" многоместной ракеты и уведомляет пассажиров о выбранном водителем измерении
 * EN: Tracking the multi-seats rocket 'driver' actions and notifying 'passengers' about driver's selected dimension
 * @author gug2
 */
open class MultiSeatRocketDriverTracker {
    private val DRIVER_PASSENGERS_TRACKED: MutableMap<EntityPlayerMP?, ArrayList<EntityPlayerMP>?> =
        Maps.newHashMap<EntityPlayerMP?, ArrayList<EntityPlayerMP>?>()

    private fun addTrackedDriver(driver: EntityPlayerMP) {
        if (!DRIVER_PASSENGERS_TRACKED.containsKey(driver)) {
            DRIVER_PASSENGERS_TRACKED.put(driver, Lists.newArrayList<EntityPlayerMP>())

            GalaxySpace.info("add driver to tracking " + driver.toString())
        }
    }

    open fun addPassengerToTrackedDriver(driver: EntityPlayerMP, passenger: EntityPlayerMP) {
        addTrackedDriver(driver)

        DRIVER_PASSENGERS_TRACKED.get(driver)!!.add(passenger)

        GalaxySpace.info("add passenger to driver's tracker " + passenger.toString())
    }

    open fun releaseTrackedDriver(driver: EntityPlayerMP) {
        if (DRIVER_PASSENGERS_TRACKED.containsKey(driver)) {
            GalaxySpace.info(
                "released driver " + driver.toString() + " with passengers " + DRIVER_PASSENGERS_TRACKED.get(
                    driver
                )
            )

            DRIVER_PASSENGERS_TRACKED.remove(driver)
        }
        GalaxySpace.info("there is no driver to release! Trying release driver " + driver.toString())
    }

    open fun update() {
        for (trackedEntry in DRIVER_PASSENGERS_TRACKED.entries) {
            val driverStats: GCPlayerStats = (GCPlayerStats.get((trackedEntry.key)!!))!!


            // if driver has been teleported to new dimension
            if (driverStats.getTeleportCooldown() > 0) {
                for (passenger in trackedEntry.value!!) {
                    // send passenger to same dimension
                    TickHandlerServer.scheduleNewDimensionChange(
                        ScheduledDimensionChange(
                            passenger,
                            trackedEntry.key!!.world.provider.getDimension()
                        )
                    )
                    GalaxySpace.info("schedule dimension change for " + passenger.toString() + " (call by driver " + trackedEntry.key.toString() + ")")


                    // notify all clients that 'wait for driver' gui must be closed
                    (GalaxySpace.packetPipeline)!!.sendTo(
                        GSPacketSimple(
                            GSEnumSimplePacket.C_CLOSE_WAITFORDRIVER_GUI,
                            GCCoreUtil.getDimensionID((passenger)!!.world),
                            *arrayOf<Any?>()
                        ), passenger
                    )
                    GalaxySpace.info("notified " + passenger.toString() + " that gui must be closed")
                }

                releaseTrackedDriver(trackedEntry.key!!)
            }
        }
    }

    companion object {
        private val INSTANCE = MultiSeatRocketDriverTracker()

        @JvmStatic
        fun get(): MultiSeatRocketDriverTracker {
            return INSTANCE
        }
    }
}
