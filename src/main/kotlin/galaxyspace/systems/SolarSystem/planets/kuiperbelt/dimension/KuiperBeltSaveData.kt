package galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension

import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.KuiperBeltSaveData
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.Level
import com.mojang.serialization.Codec
import foundation.persistence.FoundationSavedData

open class KuiperBeltSaveData constructor(@Suppress("UNUSED_PARAMETER") s: String?) : SavedData() {
    @JvmField
    var datacompound: NBTTagCompound

    init {
        this.datacompound = NBTTagCompound()
    }

    open fun readFromNBT(nbt: NBTTagCompound) {
        this.datacompound = nbt.getCompoundOrEmpty("kuiperbelt")
    }

    open fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        nbt.put("kuiperbelt", this.datacompound)
        return nbt
    }

    companion object {
        @JvmField
        val CODEC: Codec<KuiperBeltSaveData> = NBTTagCompound.CODEC.xmap(
            { tag -> KuiperBeltSaveData("").also { it.readFromNBT(tag) } },
            { data -> data.writeToNBT(NBTTagCompound()) }
        )

        @JvmStatic
        fun get(world: Level): KuiperBeltSaveData = FoundationSavedData.getShared(world, saveDataID, { KuiperBeltSaveData("") }, CODEC)

        @JvmField
        val saveDataID: String = Constants.GCDATAFOLDER + "GSKuiperBeltData"
    }
}
