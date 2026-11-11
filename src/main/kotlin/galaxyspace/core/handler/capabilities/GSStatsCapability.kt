package galaxyspace.core.handler.capabilities

import galaxyspace.GalaxySpace
import galaxyspace.core.handler.capabilities.GSStatsCapability
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import java.lang.Exception
import java.lang.ref.WeakReference

open class GSStatsCapability : StatsCapability() {
    @JvmField
    var player: WeakReference<EntityPlayerMP>? = null

    @JvmField
    var buildFlags: Int = 0

    private var know_res = IntArray(256)
    private var isBarnardaSurvival = false

    override fun saveNBTData(nbt: NBTTagCompound?) {
        (nbt)!!.putIntArray("gs_knowledge_research", know_res)
        (nbt)!!.putBoolean("survival_barnarda", isBarnardaSurvival)
    }

    override fun loadNBTData(nbt: NBTTagCompound?) {
        try {
            this.know_res = (nbt)!!.getIntArray("gs_knowledge_research")
            this.isBarnardaSurvival = (nbt)!!.getBooleanOr("survival_barnarda", false)
        } catch (e: Exception) {
            GCLog.severe(
                "Found error in saved Galaxy Space player data for " + player!!.get()!!.getGameProfile()
                    .getName() + " - this should fix itself next relog."
            )
            e.printStackTrace()
        }

        (GalaxySpace.instance)!!.debug(
            "Finished loading Galaxy Space player data for " + player!!.get()!!.getGameProfile()
                .getName() + " : " + this.buildFlags
        )
    }

    override fun copyFrom(oldData: StatsCapability?, keepInv: Boolean) {
        if ((oldData)!!.getKnowledgeResearches() != null) this.know_res = (oldData.getKnowledgeResearches())!!

        this.isBarnardaSurvival = (oldData)!!.isBarnardaSurvivalMode()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun getPlayer(): WeakReference<EntityPlayerMP>? {
        return player
    }

    override fun setPlayer(player: WeakReference<EntityPlayerMP>?) {
        this.player = player
    }

    override fun getKnowledgeResearches(): IntArray? {
        return this.know_res
    }

    override fun setKnowledgeResearch(id: Int, k: Int) {
        this.know_res[id] = k
    }

    override fun isBarnardaSurvivalMode(): Boolean {
        return this.isBarnardaSurvival
    }

    override fun setBarnardaSurvivalMode() {
        this.isBarnardaSurvival = true
    }
}
