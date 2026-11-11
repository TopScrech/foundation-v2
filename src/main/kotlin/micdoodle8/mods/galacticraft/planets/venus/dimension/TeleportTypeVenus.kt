package micdoodle8.mods.galacticraft.planets.venus.dimension

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.planets.venus.entities.EntityEntryPodVenus
import net.minecraft.world.entity.Entity
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.Level as World
import net.minecraft.server.level.ServerLevel as WorldServer
import java.util.Random

open class TeleportTypeVenus : ITeleportType {
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
            return Vector3(x, 900.0, z)
        }

        return null
    }

    override fun getEntitySpawnLocation(world: WorldServer?, entity: Entity?): Vector3 {
        return Vector3((entity)!!.x, 900.0, (entity)!!.z)
    }

    override fun getParaChestSpawnLocation(world: WorldServer?, player: EntityPlayerMP?, rand: Random?): Vector3? {
        return null
    }

    override fun onSpaceDimensionChanged(newWorld: World?, player: EntityPlayerMP?, ridingAutoRocket: Boolean) {
        if (!ridingAutoRocket && player != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!

            if (stats.getTeleportCooldown() <= 0) {
                if (player.capabilities.isFlying) {
                    player.capabilities.isFlying = false
                }

                if (!(newWorld)!!.isClientSide) {
                    val entryPod: EntityEntryPodVenus = EntityEntryPodVenus(player)

                    CompatibilityManager.forceLoadChunks(newWorld as WorldServer)
                    entryPod.forceSpawn = true
                    newWorld.addFreshEntity(entryPod)
                    CompatibilityManager.forceLoadChunksEnd(newWorld)
                }

                stats.setTeleportCooldown(10)
            }
        }
    }

    override fun setupAdventureSpawn(player: EntityPlayerMP?) {
        // TODO Auto-generated method stub
    }
}
