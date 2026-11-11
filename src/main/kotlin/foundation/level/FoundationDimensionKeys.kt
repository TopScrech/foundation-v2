package foundation.level

import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import java.util.concurrent.ConcurrentHashMap

/** Stable dimension keys, including explicit aliases for imported 1.12 save data */
object FoundationDimensionKeys {
    @JvmField val OVERWORLD: ResourceKey<Level> = parse("minecraft:overworld")
    @JvmField val NETHER: ResourceKey<Level> = parse("minecraft:the_nether")
    @JvmField val END: ResourceKey<Level> = parse("minecraft:the_end")

    private val legacyIds = ConcurrentHashMap<Int, ResourceKey<Level>>().apply {
        put(0, OVERWORLD)
        put(-1, NETHER)
        put(1, END)
    }

    fun registerLegacyId(id: Int, key: ResourceKey<Level>) {
        val existing = legacyIds.putIfAbsent(id, key)
        require(existing == null || existing == key) {
            "Legacy dimension $id maps to both ${existing?.identifier()} and ${key.identifier()}"
        }
    }

    fun fromLegacyId(id: Int): ResourceKey<Level> = requireNotNull(legacyIds[id]) {
        "No configured dimension key for legacy dimension ID $id"
    }

    fun parse(value: String): ResourceKey<Level> {
        val identifier = requireNotNull(Identifier.tryParse(value)) { "Invalid dimension identifier: $value" }
        return ResourceKey.create(Registries.DIMENSION, identifier)
    }

    fun read(tag: CompoundTag): ResourceKey<Level> {
        val key = tag.getString("dimension").orElse(null) ?: tag.getString("dim").orElse(null)
        if (key != null) return parse(key)
        return fromLegacyId(tag.getIntOr("dim", 0))
    }

    fun write(tag: CompoundTag, key: ResourceKey<Level>) {
        tag.putString("dimension", key.identifier().toString())
        tag.remove("dim")
    }
}
