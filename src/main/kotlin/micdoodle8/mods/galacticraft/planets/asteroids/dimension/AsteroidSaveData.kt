package micdoodle8.mods.galacticraft.planets.asteroids.dimension

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.storage.WorldSavedData

open class AsteroidSaveData constructor(s: String?) : WorldSavedData(saveDataID) {
    @JvmField
    var datacompound: NBTTagCompound

    init {
        this.datacompound = NBTTagCompound()
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        this.datacompound = nbt.getCompoundTag("asteroids")
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        nbt.setTag("asteroids", this.datacompound)
        return nbt
    }

    companion object {
        @JvmField
        val saveDataID: String = Constants.GCDATAFOLDER + "GCAsteroidData"
    }
}
