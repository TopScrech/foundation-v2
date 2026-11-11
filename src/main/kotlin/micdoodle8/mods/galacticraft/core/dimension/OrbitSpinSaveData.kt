package micdoodle8.mods.galacticraft.core.dimension

import com.mojang.serialization.Codec
import foundation.persistence.FoundationSavedData
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData

/** Shared station records, with a stable entry for each dimension */
open class OrbitSpinSaveData(@Suppress("UNUSED_PARAMETER") name: String?) : SavedData() {
    private var dimensions = CompoundTag()

    open fun readFromNBT(tag: CompoundTag) {
        dimensions = tag.copy()
    }

    open fun writeToNBT(tag: CompoundTag): CompoundTag {
        tag.merge(dimensions)
        return tag
    }

    fun forDimension(key: ResourceKey<Level>, legacyId: Int? = null, initialize: (CompoundTag) -> Unit = {}): Entry {
        val name = key.identifier().toString()
        val existing = dimensions.getCompound(name).orElse(null)
        if (existing != null) return Entry(this, existing)
        check(!dimensions.contains(name)) { "Invalid station spin record for $name" }
        val legacy = legacyId?.let { dimensions.getCompound(it.toString()).orElse(null) }
        val data = legacy?.copy() ?: CompoundTag().also(initialize)
        dimensions.put(name, data)
        setDirty()
        return Entry(this, data)
    }

    class Entry internal constructor(private val owner: OrbitSpinSaveData, @JvmField val datacompound: CompoundTag) {
        fun setDirty() = owner.setDirty()
    }

    companion object {
        @JvmField
        val saveDataID: String = Constants.GCDATAFOLDER + "GCSpinData"

        @JvmField
        val CODEC: Codec<OrbitSpinSaveData> = CompoundTag.CODEC.xmap(
            { tag -> OrbitSpinSaveData("").also { it.readFromNBT(tag) } },
            { data -> data.writeToNBT(CompoundTag()) }
        )

        @JvmStatic
        @JvmOverloads
        fun initWorldData(world: Level, legacyId: Int? = null, initialize: (CompoundTag) -> Unit = {}): Entry {
            val shared = FoundationSavedData.getShared(world, saveDataID, { OrbitSpinSaveData("") }, CODEC)
            return shared.forDimension(world.dimension(), legacyId, initialize)
        }
    }
}
