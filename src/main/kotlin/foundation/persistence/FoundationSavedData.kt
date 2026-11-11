package foundation.persistence

import com.mojang.serialization.Codec
import net.minecraft.SharedConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import net.minecraft.world.level.storage.LevelResource
import net.minecraft.world.level.storage.SavedDataStorage
import java.nio.file.Files
import java.nio.file.Path
import java.util.WeakHashMap

/** Per-level client mirrors and codec-backed server persistence for legacy named data */
object FoundationSavedData {
    private val clients = WeakHashMap<Level, MutableMap<Identifier, SavedData>>()

    fun identifier(legacyName: String): Identifier {
        // Hex preserves arbitrary old names without collisions or unsafe path characters
        val encoded = legacyName.toByteArray(Charsets.UTF_8).joinToString("") { "%02x".format(it.toInt() and 255) }
        return Identifier.fromNamespaceAndPath("foundation", "legacy/$encoded")
    }

    fun <T : SavedData> get(level: Level, legacyName: String, constructor: () -> T, codec: Codec<T>): T =
        get(level, legacyName, constructor, codec, false)

    /** Old World.loadData used storage shared by every dimension of a server */
    fun <T : SavedData> getShared(level: Level, legacyName: String, constructor: () -> T, codec: Codec<T>): T =
        get(level, legacyName, constructor, codec, true)

    // Forge explicitly supports null data fixers for mod-owned formats
    @Suppress("NULL_FOR_NONNULL_TYPE")
    private fun <T : SavedData> get(level: Level, legacyName: String, constructor: () -> T, codec: Codec<T>, shared: Boolean): T {
        val type = SavedDataType(identifier(legacyName), constructor, codec, null)
        if (level.isClientSide) {
            synchronized(clients) {
                @Suppress("UNCHECKED_CAST")
                return clients.getOrPut(level) { HashMap() }.getOrPut(type.id(), constructor) as T
            }
        }
        require(level is ServerLevel) { "Saved data requires a server level" }
        if (shared) {
            return load(level.server.dataStorage, level.server.getWorldPath(LevelResource.DATA), legacyName, type)
        }
        val directory = DimensionType.getStorageFolder(level.dimension(), level.server.getWorldPath(LevelResource.ROOT)).resolve("data")
        return load(level.dataStorage, directory, legacyName, type)
    }

    /** Imports a legacy data file only when its new namespaced counterpart is absent */
    @Suppress("NULL_FOR_NONNULL_TYPE")
    fun <T : SavedData> load(storage: SavedDataStorage, directory: Path, legacyName: String, type: SavedDataType<T>): T {
        storage.get(type)?.let { return it }
        val modernFile = type.id().withSuffix(".dat").resolveAgainst(directory)
        check(!Files.exists(modernFile)) { "Could not decode existing saved data $modernFile" }
        val root = directory.toAbsolutePath().normalize()
        // Galacticraft intentionally stored shared files beside data/ in galacticraft/
        val legacyRoot = if (legacyName.startsWith("../galacticraft/")) root.parent.resolve("galacticraft") else root
        val filename = legacyName.removePrefix("../galacticraft/")
        val legacyFile = legacyRoot.resolve("$filename.dat").normalize()
        require(legacyFile.startsWith(legacyRoot)) { "Legacy saved data escaped its data directory" }
        if (Files.exists(legacyFile)) {
            val tag = storage.readTagFromDisk(legacyFile, null, SharedConstants.getCurrentVersion().dataVersion().version())
            val data = type.codec().parse(NbtOps.INSTANCE, tag.getCompound("data").orElseThrow { IllegalStateException("Missing saved data payload in $legacyFile") }).getOrThrow()
            storage.set(type, data)
            return data
        }
        return storage.computeIfAbsent(type)
    }
}
