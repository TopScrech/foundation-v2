package asmodeuscore.core.handler.capabilities

import asmodeuscore.AsmodeusCore
import asmodeuscore.core.handler.capabilities.ACCapabilityStatsHandler
import asmodeuscore.core.handler.capabilities.ACStatsCapability
import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.world.entity.Entity
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import java.lang.Exception
import java.lang.ref.WeakReference

open class ACStatsCapability : IStatsCapability {
    @JvmField
    var player: WeakReference<EntityPlayerMP>? = null

    @JvmField
    var buildFlags: Int = 0

    @JvmField
    var rocket_engine: Engine_Type? = Engine_Type.FUEL_ENGINE
    private var isSecondPassenger = false

    override fun saveNBTData(nbt: NBTTagCompound?) {
        (nbt)!!.putInt("rocket_engine", getEngineType()!!.getID())
        (nbt)!!.putBoolean("SecondPassenger", isSecondPassenger)
    }

    override fun loadNBTData(nbt: NBTTagCompound?) {
        try {
            this.rocket_engine = Engine_Type.byID((nbt)!!.getIntOr("rocket_engine", 0))
            this.isSecondPassenger = (nbt)!!.getBooleanOr("SecondPassenger", false)
        } catch (e: Exception) {
            GCLog.severe(
                "Found error in saved AsmodeusCore player data for " + player!!.get()!!.getGameProfile()
                    .getName() + " - this should fix itself next relog."
            )
            e.printStackTrace()
        }

        GCLog.debug(
            "Finished loading AsmodeusCore player data for " + player!!.get()!!.getGameProfile()
                .getName() + " : " + this.buildFlags
        )
    }

    override fun copyFrom(oldData: IStatsCapability?, keepInv: Boolean) {
        //this.radiationLevel = oldData.getRadiationLevel();
        this.rocket_engine = (oldData)!!.getEngineType()
        this.isSecondPassenger = (oldData)!!.isSecondPassenger()
    }

    override fun getPlayer(): WeakReference<EntityPlayerMP>? {
        return player
    }

    override fun setPlayer(player: WeakReference<EntityPlayerMP>?) {
        this.player = player
    }

    override fun setEngineType(type: Engine_Type?) {
        this.rocket_engine = type
    }

    override fun getEngineType(): Engine_Type? {
        return this.rocket_engine
    }

    override fun isSecondPassenger(): Boolean {
        return isSecondPassenger
    }

    override fun setSecondPassenger(b: Boolean) {
        this.isSecondPassenger = b
    }

    companion object {
        @JvmStatic
        fun get(entity: Entity): IStatsCapability? {
            return entity.getCapability<IStatsCapability?>((ACCapabilityStatsHandler.AC_STATS_CAPABILITY)!!, null)
        }
    }
}
