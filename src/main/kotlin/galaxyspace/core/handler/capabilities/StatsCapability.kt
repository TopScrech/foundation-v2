package galaxyspace.core.handler.capabilities

import galaxyspace.core.handler.capabilities.GSCapabilityStatsHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import java.lang.ref.WeakReference

abstract class StatsCapability {
    abstract fun saveNBTData(nbt: NBTTagCompound?)
    abstract fun loadNBTData(nbt: NBTTagCompound?)
    abstract fun copyFrom(oldData: StatsCapability?, keepInv: Boolean)
    abstract fun getPlayer(): WeakReference<EntityPlayerMP>?
    abstract fun setPlayer(player: WeakReference<EntityPlayerMP>?)

    abstract fun getKnowledgeResearches(): IntArray?
    abstract fun setKnowledgeResearch(id: Int, k: Int)

    abstract fun isBarnardaSurvivalMode(): Boolean
    abstract fun setBarnardaSurvivalMode()

    companion object {
        @JvmStatic
        fun get(entity: Entity): StatsCapability? {
            return entity.getCapability<StatsCapability?>((GSCapabilityStatsHandler.GS_STATS_CAPABILITY)!!, null)
        }
    }
}
