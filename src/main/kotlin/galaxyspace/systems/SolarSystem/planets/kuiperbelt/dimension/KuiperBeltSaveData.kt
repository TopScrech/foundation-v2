package galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension

import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.KuiperBeltSaveData
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.WorldSavedData

open class KuiperBeltSaveData constructor(s: String?) : WorldSavedData(saveDataID) {
    @JvmField
    var datacompound: NBTTagCompound

    init {
        this.datacompound = NBTTagCompound()
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        this.datacompound = nbt.getCompoundTag("kuiperbelt")
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        nbt.setTag("kuiperbelt", this.datacompound)
        return nbt
    }

    companion object {
        @JvmField
        val saveDataID: String = Constants.GCDATAFOLDER + "GSKuiperBeltData"
    }
}
