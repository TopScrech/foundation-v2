package galaxyspace.core.handler.capabilities

import galaxyspace.core.handler.capabilities.GSCapabilityStatsHandler
import galaxyspace.core.handler.capabilities.StatsCapabilityClient
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.core.Direction as EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

open class GSCapabilityProviderStatsClient constructor(private val owner: EntityPlayerSP?) : ICapabilityProvider {
    private val statsCapability: StatsCapabilityClient?

    init {
        this.statsCapability = (GSCapabilityStatsHandler.GS_STATS_CAPABILITY_CLIENT)!!.getDefaultInstance()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === GSCapabilityStatsHandler.GS_STATS_CAPABILITY_CLIENT
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (GSCapabilityStatsHandler.GS_STATS_CAPABILITY_CLIENT != null && capability === GSCapabilityStatsHandler.GS_STATS_CAPABILITY_CLIENT) {
            return (GSCapabilityStatsHandler.GS_STATS_CAPABILITY_CLIENT)!!.cast<T?>(statsCapability)
        }

        return null
    }
}
