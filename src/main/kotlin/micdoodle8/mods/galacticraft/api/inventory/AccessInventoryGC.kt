package micdoodle8.mods.galacticraft.api.inventory

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import java.lang.Exception
import java.lang.reflect.Method

/**
 * A static method for other mods to access the Galacticraft
 * extended inventory.
 *
 * Call: AccessInventoryGC.getGCInventoryForPlayer(player)
 */
object AccessInventoryGC {
    private var playerStatsClass: Class<*>? = null
    private var getStats: Method? = null
    private var getExtendedInventory: Method? = null

    @JvmStatic
    fun getGCInventoryForPlayer(player: EntityPlayerMP?): IInventoryGC? {
        try {
            if (playerStatsClass == null || getStats == null || getExtendedInventory == null) {
                playerStatsClass = Class.forName("micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats")
                getStats = playerStatsClass!!.getMethod("get", Entity::class.java)
                getExtendedInventory = playerStatsClass!!.getMethod("getExtendedInventory")
            }

            val stats: Any? = getStats!!.invoke(null, player)
            if (stats == null) {
                return null
            }
            return getExtendedInventory!!.invoke(stats) as IInventoryGC?
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
