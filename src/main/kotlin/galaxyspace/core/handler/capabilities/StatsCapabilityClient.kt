package galaxyspace.core.handler.capabilities

import galaxyspace.core.handler.capabilities.GSCapabilityStatsHandler
import galaxyspace.core.handler.capabilities.StatsCapabilityClient
import net.minecraft.entity.Entity

abstract class StatsCapabilityClient {
    abstract fun getKnowledgeResearches(): IntArray?
    abstract fun setKnowledgeResearch(id: Int, k: Int)

    companion object {
        @JvmStatic
        fun get(entity: Entity): StatsCapabilityClient? {
            return entity.getCapability<StatsCapabilityClient?>(
                (GSCapabilityStatsHandler.GS_STATS_CAPABILITY_CLIENT)!!,
                null
            )
        }
    }
}
