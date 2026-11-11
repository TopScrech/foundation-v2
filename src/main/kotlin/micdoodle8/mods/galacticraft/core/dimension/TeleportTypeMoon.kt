package micdoodle8.mods.galacticraft.core.dimension

import java.util.Random

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.core.entities.EntityLander
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.world.entity.Entity
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.Level as World
import net.minecraft.server.level.ServerLevel as WorldServer
import java.util.*

open class TeleportTypeMoon : ITeleportType {
    override fun useParachute(): Boolean {
        return ConfigManagerCore.disableLander
    }

    override fun getPlayerSpawnLocation(world: WorldServer?, player: EntityPlayerMP?): Vector3? {
        if (player != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            var x: Double = stats.getCoordsTeleportedFromX()
            var z: Double = stats.getCoordsTeleportedFromZ()
            val limit: Int = ConfigManagerCore.otherPlanetWorldBorders - 2
            if (limit > 20) {
                if (x > limit) {
                    z *= limit / x
                    x = limit.toDouble()
                } else if (x < -limit) {
                    z *= -limit / x
                    x = -limit.toDouble()
                }
                if (z > limit) {
                    x *= limit / z
                    z = limit.toDouble()
                } else if (z < -limit) {
                    x *= -limit / z
                    z = -limit.toDouble()
                }
            }
            return Vector3(x, if (ConfigManagerCore.disableLander) 250.0 else 900.0, z)
        }

        return null
    }

    override fun getEntitySpawnLocation(world: WorldServer?, entity: Entity?): Vector3 {
        return Vector3((entity)!!.x, if (ConfigManagerCore.disableLander) 250.0 else 900.0, (entity)!!.z)
    }

    override fun getParaChestSpawnLocation(world: WorldServer?, player: EntityPlayerMP?, rand: Random?): Vector3? {
        if (ConfigManagerCore.disableLander) {
            val x: Double = ((rand)!!.nextDouble() * 2 - 1.0) * 4.0
            val z: Double = ((rand)!!.nextDouble() * 2 - 1.0) * 4.0
            return Vector3((player)!!.x + x, 220.0, (player)!!.z + z)
        }

        return null
    }

    override fun onSpaceDimensionChanged(newWorld: World?, player: EntityPlayerMP?, ridingAutoRocket: Boolean) {
        val stats: GCPlayerStats = (GCPlayerStats.get((player)!!))!!
        if (!ridingAutoRocket && !ConfigManagerCore.disableLander && stats.getTeleportCooldown() <= 0) {
            if ((player)!!.capabilities.isFlying) {
                (player)!!.capabilities.isFlying = false
            }

            val lander: EntityLander = EntityLander((player)!!)
            lander.setPosition((player)!!.x, (player)!!.y, (player)!!.z)

            if (!(newWorld)!!.isClientSide) {
                CompatibilityManager.forceLoadChunks(newWorld as WorldServer)
                lander.forceSpawn = true
                newWorld.addFreshEntity(lander)
                lander.setWorld(newWorld)
                newWorld.updateEntityWithOptionalForce(lander, true)
                (player)!!.startRiding(lander)
                CompatibilityManager.forceLoadChunksEnd(newWorld)
                GCLog.debug("Entering lander at : " + (player)!!.x + "," + (player)!!.z + " lander spawn at: " + lander.posX + "," + lander.posZ)
            }

            stats.setTeleportCooldown(10)
        }
    }

    override fun setupAdventureSpawn(player: EntityPlayerMP?) {
        // TODO Auto-generated method stub
    }
}
