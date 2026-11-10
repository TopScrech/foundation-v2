package micdoodle8.mods.galacticraft.core.dimension

import java.util.Random

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import java.util.*

open class TeleportTypeOrbit : ITeleportType {
    override fun useParachute(): Boolean {
        return false
    }

    override fun getPlayerSpawnLocation(world: WorldServer?, player: EntityPlayerMP?): Vector3 {
        return Vector3(0.5, 65.0, 0.5)
    }

    override fun getEntitySpawnLocation(world: WorldServer?, player: Entity?): Vector3 {
        return Vector3(0.5, 65.0, 0.5)
    }

    override fun getParaChestSpawnLocation(world: WorldServer?, player: EntityPlayerMP?, rand: Random?): Vector3 {
        return Vector3(-8.5, 90.0, -1.5)
    }

    override fun onSpaceDimensionChanged(newWorld: World?, player: EntityPlayerMP?, ridingAutoRocket: Boolean) {
    }

    override fun setupAdventureSpawn(player: EntityPlayerMP?) {
        // TODO Auto-generated method stub
    }
}
