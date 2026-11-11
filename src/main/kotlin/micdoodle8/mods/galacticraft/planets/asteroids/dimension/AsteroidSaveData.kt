package micdoodle8.mods.galacticraft.planets.asteroids.dimension

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.Level
import com.mojang.serialization.Codec
import foundation.persistence.FoundationSavedData

open class AsteroidSaveData constructor(@Suppress("UNUSED_PARAMETER") s: String?) : SavedData() {
    @JvmField
    var datacompound: NBTTagCompound

    init {
        this.datacompound = NBTTagCompound()
    }

    open fun readFromNBT(nbt: NBTTagCompound) {
        this.datacompound = nbt.getCompoundOrEmpty("asteroids")
    }

    open fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        nbt.put("asteroids", this.datacompound)
        return nbt
    }

    companion object {
        @JvmField
        val CODEC: Codec<AsteroidSaveData> = NBTTagCompound.CODEC.xmap(
            { tag -> AsteroidSaveData("").also { it.readFromNBT(tag) } },
            { data -> data.writeToNBT(NBTTagCompound()) }
        )

        @JvmStatic
        fun get(world: Level): AsteroidSaveData = FoundationSavedData.getShared(world, saveDataID, { AsteroidSaveData("") }, CODEC)

        @JvmField
        val saveDataID: String = Constants.GCDATAFOLDER + "GCAsteroidData"
    }
}
