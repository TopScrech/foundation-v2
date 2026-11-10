package asmodeuscore.core.handler.capabilities

import asmodeuscore.core.handler.capabilities.ACCapabilityStatsHandler
import asmodeuscore.core.handler.capabilities.ACCapabilityProviderStats
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import java.lang.ref.WeakReference

open class ACCapabilityProviderStats constructor(private val owner: EntityPlayerMP?) :
    ICapabilitySerializable<NBTTagCompound?> {
    private val statsCapability: IStatsCapability?

    init {
        this.statsCapability = (ACCapabilityStatsHandler.AC_STATS_CAPABILITY)!!.getDefaultInstance()
        this.statsCapability!!.setPlayer(WeakReference<EntityPlayerMP>(this.owner))
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ACCapabilityStatsHandler.AC_STATS_CAPABILITY
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (ACCapabilityStatsHandler.AC_STATS_CAPABILITY != null && capability === ACCapabilityStatsHandler.AC_STATS_CAPABILITY) {
            return (ACCapabilityStatsHandler.AC_STATS_CAPABILITY)!!.cast<T?>(statsCapability)
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
