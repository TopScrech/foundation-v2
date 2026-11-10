package galaxyspace.core.handler.capabilities

import galaxyspace.core.handler.capabilities.GSCapabilityStatsHandler
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import java.lang.ref.WeakReference

open class GSCapabilityProviderStats constructor(private val owner: EntityPlayerMP?) :
    ICapabilitySerializable<NBTTagCompound?> {
    private val statsCapability: StatsCapability?

    init {
        this.statsCapability = (GSCapabilityStatsHandler.GS_STATS_CAPABILITY)!!.getDefaultInstance()
        this.statsCapability!!.setPlayer(WeakReference<EntityPlayerMP>(this.owner))
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === GSCapabilityStatsHandler.GS_STATS_CAPABILITY
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (GSCapabilityStatsHandler.GS_STATS_CAPABILITY != null && capability === GSCapabilityStatsHandler.GS_STATS_CAPABILITY) {
            return (GSCapabilityStatsHandler.GS_STATS_CAPABILITY)!!.cast<T?>(statsCapability)
        }

        return null
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt: NBTTagCompound = NBTTagCompound()
        statsCapability!!.saveNBTData(nbt)
        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        statsCapability!!.loadNBTData(nbt)
    }
}
