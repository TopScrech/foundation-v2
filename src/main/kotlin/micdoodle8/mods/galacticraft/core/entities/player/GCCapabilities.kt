package micdoodle8.mods.galacticraft.core.entities.player

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.nbt.Tag as NBTBase
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import java.util.concurrent.Callable

object GCCapabilities {
    @JvmField
    @CapabilityInject(GCPlayerStats::class)
    var GC_STATS_CAPABILITY: Capability<GCPlayerStats?>? = null

    @JvmField
    @CapabilityInject(GCPlayerStatsClient::class)
    var GC_STATS_CLIENT_CAPABILITY: Capability<GCPlayerStatsClient?>? = null

    @JvmField
    val GC_PLAYER_PROP: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "player_stats")

    @JvmField
    val GC_PLAYER_CLIENT_PROP: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "player_stats_client")

    @JvmStatic
    fun register() {
        CapabilityManager.INSTANCE.register<GCPlayerStats?>(
            GCPlayerStats::class.java,
            object : Capability.IStorage<GCPlayerStats> {
                override fun writeNBT(
                    capability: Capability<GCPlayerStats?>,
                    instance: GCPlayerStats,
                    side: EnumFacing?
                ): NBTBase? {
                    val nbt: NBTTagCompound = NBTTagCompound()
                    instance.saveNBTData(nbt)
                    return nbt
                }

                override fun readNBT(
                    capability: Capability<GCPlayerStats?>,
                    instance: GCPlayerStats,
                    side: EnumFacing?,
                    nbt: NBTBase?
                ) {
                    instance.loadNBTData(nbt as NBTTagCompound?)
                }
            },
            Callable { StatsCapability() })

        CapabilityManager.INSTANCE.register<GCPlayerStatsClient?>(
            GCPlayerStatsClient::class.java,
            object : Capability.IStorage<GCPlayerStatsClient> {
                override fun writeNBT(
                    capability: Capability<GCPlayerStatsClient?>,
                    instance: GCPlayerStatsClient?,
                    side: EnumFacing?
                ): NBTBase? {
                    return null
                }

                override fun readNBT(
                    capability: Capability<GCPlayerStatsClient?>,
                    instance: GCPlayerStatsClient?,
                    side: EnumFacing?,
                    nbt: NBTBase?
                ) {
                }
            },
            Callable { StatsClientCapability() })
    }
}
