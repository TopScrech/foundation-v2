package micdoodle8.mods.galacticraft.core.dimension

import java.util.Random

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.core.entities.EntityLander
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World
import net.minecraft.world.WorldServer
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
        return Vector3((entity)!!.posX, if (ConfigManagerCore.disableLander) 250.0 else 900.0, (entity)!!.posZ)
    }

    override fun getParaChestSpawnLocation(world: WorldServer?, player: EntityPlayerMP?, rand: Random?): Vector3? {
        if (ConfigManagerCore.disableLander) {
            val x: Double = ((rand)!!.nextDouble() * 2 - 1.0) * 4.0
            val z: Double = ((rand)!!.nextDouble() * 2 - 1.0) * 4.0
            return Vector3((player)!!.posX + x, 220.0, (player)!!.posZ + z)
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
            lander.setPosition((player)!!.posX, (player)!!.posY, (player)!!.posZ)

            if (!(newWorld)!!.isRemote) {
                CompatibilityManager.forceLoadChunks(newWorld as WorldServer)
                lander.forceSpawn = true
                newWorld.spawnEntity(lander)
                lander.setWorld(newWorld)
                newWorld.updateEntityWithOptionalForce(lander, true)
                (player)!!.startRiding(lander)
                CompatibilityManager.forceLoadChunksEnd(newWorld)
                GCLog.debug("Entering lander at : " + (player)!!.posX + "," + (player)!!.posZ + " lander spawn at: " + lander.posX + "," + lander.posZ)
            }

            stats.setTeleportCooldown(10)
        }
    }

    override fun setupAdventureSpawn(player: EntityPlayerMP?) {
        // TODO Auto-generated method stub
    }
}
