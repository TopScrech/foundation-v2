package galaxyspace.core.handler.capabilities

import galaxyspace.core.handler.capabilities.StatsCapabilityClient
import galaxyspace.core.handler.capabilities.GSStatsCapabilityClient

open class GSStatsCapabilityClient : StatsCapabilityClient() {
    private val know_res = IntArray(256)

    override fun getKnowledgeResearches(): IntArray {
        return know_res
    }

    override fun setKnowledgeResearch(id: Int, k: Int) {
        this.know_res[id] = k
    }
}
