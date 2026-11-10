package asmodeuscore.core.handler.capabilities

import asmodeuscore.AsmodeusCore
import asmodeuscore.core.handler.capabilities.ACCapabilityStatsHandler
import asmodeuscore.core.handler.capabilities.ACStatsCapability
import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import java.util.concurrent.Callable

object ACCapabilityStatsHandler {
    @JvmField
    @CapabilityInject(IStatsCapability::class)
    var AC_STATS_CAPABILITY: Capability<IStatsCapability?>? = null

    @JvmField
    val AC_PLAYER_PROPERTIES: ResourceLocation = ResourceLocation(AsmodeusCore.ASSET_PREFIX, "player_stats")

    @JvmField
    val AC_PLAYER_PROPERTIES_CLIENT: ResourceLocation =
        ResourceLocation(AsmodeusCore.ASSET_PREFIX, "player_stats_client")

    @JvmStatic
    fun register() {
        CapabilityManager.INSTANCE.register<IStatsCapability?>(
            IStatsCapability::class.java,
            object : Capability.IStorage<IStatsCapability> {
                override fun writeNBT(
                    capability: Capability<IStatsCapability?>,
                    instance: IStatsCapability?,
                    side: EnumFacing?
                ): NBTBase? {
                    return null
                }

                override fun readNBT(
                    capability: Capability<IStatsCapability?>,
                    instance: IStatsCapability?,
                    side: EnumFacing?,
                    nbt: NBTBase?
                ) {
                }
            },
            Callable { ACStatsCapability() })
    }
}
