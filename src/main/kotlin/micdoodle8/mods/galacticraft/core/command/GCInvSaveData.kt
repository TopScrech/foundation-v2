package micdoodle8.mods.galacticraft.core.command

import com.mojang.serialization.Codec
import foundation.persistence.FoundationItemStackPersistence
import foundation.persistence.FoundationSavedData
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import java.util.Locale

/** Server-owned equipment archives, retaining undecoded records until their owner restores them */
class GCInvSaveData : SavedData() {
    private var payload = CompoundTag()
    // Deliberately transient: the original command restored all pending saves after a server restart
    private val held = HashSet<String>()

    fun readFromNBT(tag: CompoundTag) {
        check(!tag.contains(RECORDS) || tag.getCompound(RECORDS).isPresent) { "Invalid equipment archive records" }
        payload = tag.copy()
        held.clear()
    }

    fun writeToNBT(tag: CompoundTag): CompoundTag = tag.merge(payload.copy())

    private data class Record(val key: String, val modern: Boolean)

    private fun record(name: String): Record? {
        val key = normalize(name)
        val records = payload.getCompoundOrEmpty(RECORDS)
        val matches = records.keySet().filter { normalize(it) == key }.map { Record(it, true) } +
            payload.keySet().filter { it != RECORDS && normalize(it) == key }.map { Record(it, false) }
        check(matches.size <= 1) { "Conflicting equipment archives for $name" }
        return matches.singleOrNull()
    }

    fun hasSaved(name: String): Boolean = record(name) != null

    /** Encode the complete snapshot before removing a single item from the live inventory */
    fun save(name: String, inventory: Container, registries: HolderLookup.Provider) {
        check(!hasSaved(name)) { "An equipment archive already exists for $name" }
        val items = ListTag()
        for (slot in 0 until inventory.containerSize) {
            val stack = inventory.getItem(slot)
            if (stack.isEmpty) continue
            val tag = FoundationItemStackPersistence.write(stack, registries)
            tag.putInt("Slot", slot)
            items.add(tag)
        }
        val key = normalize(name)
        val archive = CompoundTag().also {
            it.putInt("Size", inventory.containerSize)
            it.put("Items", items)
        }
        val records = payload.getCompoundOrEmpty(RECORDS).copy()
        records.put(key, archive)
        payload.put(RECORDS, records)
        held.add(key)
        setDirty()
        for (slot in 0 until inventory.containerSize) inventory.setItem(slot, ItemStack.EMPTY)
        inventory.setChanged()
    }

    fun queueRestore(name: String): Boolean {
        if (!hasSaved(name)) return false
        held.remove(normalize(name))
        return true
    }

    /** A failed migration leaves both the archive and live slots unchanged */
    fun restore(name: String, inventory: Container, registries: HolderLookup.Provider, automatic: Boolean = false): Boolean {
        val record = record(name) ?: return false
        if (automatic && normalize(name) in held) return false
        val size: Int
        val tags: ListTag
        if (record.modern) {
            val saved = payload.getCompoundOrEmpty(RECORDS).getCompound(record.key)
                .orElseThrow { IllegalArgumentException("Invalid equipment archive for $name") }
            size = saved.getInt("Size").orElseThrow { IllegalArgumentException("Missing equipment archive size for $name") }
            tags = saved.getList("Items").orElseThrow { IllegalArgumentException("Missing equipment archive items for $name") }
        } else {
            size = 6
            tags = payload.getList(record.key).orElseThrow { IllegalArgumentException("Invalid legacy equipment archive for $name") }
        }
        require(size in 0..inventory.containerSize) { "Equipment archive needs $size slots, inventory has ${inventory.containerSize}" }
        val stacks = Array(size) { ItemStack.EMPTY }
        val seen = HashSet<Int>()
        for (index in 0 until tags.size) {
            val entry = tags.getCompound(index).orElseThrow { IllegalArgumentException("Invalid archived equipment entry $index") }
            val slot = if (record.modern) {
                entry.getInt("Slot").orElseThrow { IllegalArgumentException("Missing archived equipment slot") }
            } else {
                val stored = entry.getByte("Slot").orElseThrow { IllegalArgumentException("Missing legacy equipment slot") }.toInt() and 255
                // Historical GCInv files stored slots 200 through 205, not a modern inventory index
                require(stored in 200..205) { "Invalid legacy equipment slot $stored" }
                stored - 200
            }
            require(slot in stacks.indices && seen.add(slot)) { "Invalid or duplicate archived equipment slot $slot" }
            val stack = FoundationItemStackPersistence.read(entry, registries)
            require(stack.count <= inventory.maxStackSize) { "Archived equipment exceeds the inventory stack limit" }
            stacks[slot] = stack
        }
        for (slot in stacks.indices) inventory.setItem(slot, stacks[slot])
        inventory.setChanged()
        if (record.modern) payload.getCompoundOrEmpty(RECORDS).remove(record.key) else payload.remove(record.key)
        held.remove(normalize(name))
        setDirty()
        return true
    }

    companion object {
        private const val RECORDS = "foundation:equipment_archives"
        @JvmField val SAVE_ID: String = Constants.GCDATAFOLDER + "GCInv_savefile"
        @JvmField val CODEC: Codec<GCInvSaveData> = CompoundTag.CODEC.xmap(
            { tag -> GCInvSaveData().also { it.readFromNBT(tag) } },
            { data -> data.writeToNBT(CompoundTag()) }
        )
        private fun normalize(name: String): String = name.lowercase(Locale.ROOT).also { require(it.isNotBlank()) }
        fun get(level: Level): GCInvSaveData = FoundationSavedData.getShared(level, SAVE_ID, ::GCInvSaveData, CODEC)
    }
}
