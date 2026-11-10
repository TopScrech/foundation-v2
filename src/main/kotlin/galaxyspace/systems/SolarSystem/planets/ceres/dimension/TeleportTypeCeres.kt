package galaxyspace.systems.SolarSystem.planets.ceres.dimension

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.core.entities.EntityLander
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import java.util.Random

open class TeleportTypeCeres : ITeleportType {
    override fun useParachute(): Boolean {
        return false
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
        return null
    }

    override fun onSpaceDimensionChanged(world: World?, player: EntityPlayerMP?, ridingAutoRocket: Boolean) {
        if (!ridingAutoRocket && player != null && (GCPlayerStats.get(player))!!.getTeleportCooldown() <= 0) {
            if (player.capabilities.isFlying) {
                player.capabilities.isFlying = false
            }

            if (!(world)!!.isRemote) {
                val lander: EntityLanderBase = EntityLander(player)

                CompatibilityManager.forceLoadChunks(world as WorldServer)
                lander.forceSpawn = true
                world.spawnEntity(lander)

                CompatibilityManager.forceLoadChunksEnd(world)
            }

            (GCPlayerStats.get(player))!!.setTeleportCooldown(10)
        }
    }

    override fun setupAdventureSpawn(player: EntityPlayerMP?) {
    }
}
