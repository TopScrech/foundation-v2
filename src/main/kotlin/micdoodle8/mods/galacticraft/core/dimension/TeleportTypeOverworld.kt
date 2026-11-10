package micdoodle8.mods.galacticraft.core.dimension

import java.util.Random

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import java.util.*

open class TeleportTypeOverworld : ITeleportType {
    override fun useParachute(): Boolean {
        return true
    }

    override fun getPlayerSpawnLocation(world: WorldServer?, player: EntityPlayerMP?): Vector3? {
        if (player != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            return Vector3(stats.getCoordsTeleportedFromX(), 250.0, stats.getCoordsTeleportedFromZ())
        }

        return null
    }

    override fun getEntitySpawnLocation(world: WorldServer?, entity: Entity?): Vector3 {
        return Vector3((entity)!!.posX, 250.0, (entity)!!.posZ)
    }

    override fun getParaChestSpawnLocation(world: WorldServer?, player: EntityPlayerMP?, rand: Random?): Vector3 {
        val x: Double = ((rand)!!.nextDouble() * 2 - 1.0) * 5.0
        val z: Double = ((rand)!!.nextDouble() * 2 - 1.0) * 5.0

        return Vector3((player)!!.posX + x, 230.0, (player)!!.posZ + z)
    }

    override fun onSpaceDimensionChanged(newWorld: World?, player: EntityPlayerMP?, ridingAutoRocket: Boolean) {
    }

    override fun setupAdventureSpawn(player: EntityPlayerMP?) {
        // TODO Auto-generated method stub
    }
}
