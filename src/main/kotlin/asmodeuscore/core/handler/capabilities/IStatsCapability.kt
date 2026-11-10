package asmodeuscore.core.handler.capabilities

import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import java.lang.ref.WeakReference

interface IStatsCapability {
    fun saveNBTData(nbt: NBTTagCompound?)

    fun loadNBTData(nbt: NBTTagCompound?)

    fun copyFrom(oldData: IStatsCapability?, keepInv: Boolean)

    fun getPlayer(): WeakReference<EntityPlayerMP>?

    fun setPlayer(player: WeakReference<EntityPlayerMP>?)

    fun setEngineType(type: Engine_Type?)

    fun getEngineType(): Engine_Type?

    fun isSecondPassenger(): Boolean
    fun setSecondPassenger(b: Boolean)
}
