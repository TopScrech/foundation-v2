package micdoodle8.mods.galacticraft.core.entities.player

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import java.lang.ref.WeakReference

open class CapabilityProviderStats constructor(private val owner: EntityPlayerMP?) :
    ICapabilitySerializable<NBTTagCompound?> {
    private val statsCapability: GCPlayerStats?

    init {
        this.statsCapability = (GCCapabilities.GC_STATS_CAPABILITY)!!.getDefaultInstance()
        this.statsCapability!!.setPlayer(WeakReference<EntityPlayerMP>(this.owner))
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === GCCapabilities.GC_STATS_CAPABILITY
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (GCCapabilities.GC_STATS_CAPABILITY != null && capability === GCCapabilities.GC_STATS_CAPABILITY) {
            return ((GCCapabilities.GC_STATS_CAPABILITY)!!.cast<T?>(statsCapability))!!
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
