package galaxyspace.core.handler.capabilities

import galaxyspace.GalaxySpace
import galaxyspace.core.handler.capabilities.GSCapabilityStatsHandler
import galaxyspace.core.handler.capabilities.StatsCapabilityClient
import galaxyspace.core.handler.capabilities.GSStatsCapability
import galaxyspace.core.handler.capabilities.GSStatsCapabilityClient
import net.minecraft.nbt.Tag as NBTBase
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import java.util.concurrent.Callable

object GSCapabilityStatsHandler {
    @JvmField
    @CapabilityInject(StatsCapabilityClient::class)
    var GS_STATS_CAPABILITY_CLIENT: Capability<StatsCapabilityClient?>? = null

    @JvmField
    @CapabilityInject(StatsCapability::class)
    var GS_STATS_CAPABILITY: Capability<StatsCapability?>? = null

    @JvmField
    val GS_PLAYER_PROPERTIES_CLIENT: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "player_stats_client")

    @JvmField
    val GS_PLAYER_PROPERTIES: ResourceLocation = ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "player_stats")

    @JvmStatic
    fun register() {
        CapabilityManager.INSTANCE.register<StatsCapability?>(
            StatsCapability::class.java,
            object : Capability.IStorage<StatsCapability> {
                override fun writeNBT(
                    capability: Capability<StatsCapability?>,
                    instance: StatsCapability,
                    side: EnumFacing?
                ): NBTBase? {
                    val nbt: NBTTagCompound = NBTTagCompound()
                    instance.saveNBTData(nbt)
                    return nbt
                }

                override fun readNBT(
                    capability: Capability<StatsCapability?>,
                    instance: StatsCapability,
                    side: EnumFacing?,
                    nbt: NBTBase?
                ) {
                    instance.loadNBTData(nbt as NBTTagCompound?)
                }
            },
            Callable { GSStatsCapability() })

        CapabilityManager.INSTANCE.register<StatsCapabilityClient?>(
            StatsCapabilityClient::class.java,
            object : Capability.IStorage<StatsCapabilityClient> {
                override fun writeNBT(
                    capability: Capability<StatsCapabilityClient?>,
                    instance: StatsCapabilityClient?,
                    side: EnumFacing?
                ): NBTBase? {
                    return null
                }

                override fun readNBT(
                    capability: Capability<StatsCapabilityClient?>,
                    instance: StatsCapabilityClient?,
                    side: EnumFacing?,
                    nbt: NBTBase?
                ) {
                }
            },
            Callable { GSStatsCapabilityClient() })
    }
}
