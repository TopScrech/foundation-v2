package micdoodle8.mods.galacticraft.core.entities.player

import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.core.Direction as EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

open class CapabilityProviderStatsClient constructor(private val owner: EntityPlayerSP?) : ICapabilityProvider {
    private val statsCapability: GCPlayerStatsClient?

    init {
        this.statsCapability = (GCCapabilities.GC_STATS_CLIENT_CAPABILITY)!!.getDefaultInstance()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === GCCapabilities.GC_STATS_CLIENT_CAPABILITY
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (GCCapabilities.GC_STATS_CLIENT_CAPABILITY != null && capability === GCCapabilities.GC_STATS_CLIENT_CAPABILITY) {
            return ((GCCapabilities.GC_STATS_CLIENT_CAPABILITY)!!.cast<T?>(statsCapability))!!
        }

        return null
    }
}
