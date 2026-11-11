package foundation.block

import net.minecraft.SharedConstants
import net.minecraft.core.Direction
import net.minecraft.nbt.NbtOps
import net.minecraft.util.datafix.DataFixers
import net.minecraft.util.datafix.fixes.BlockStateData
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.DoublePlantBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.level.block.state.properties.Property
import java.util.concurrent.ConcurrentHashMap

/** Vanilla flattening uses Mojang's explicit state table and subsequent data fixes */
object LegacyVanillaBlockStates {
    private val validFamilies = LegacyVanillaStateIds.values.mapTo(HashSet()) { it ushr 4 }
    private val doors = setOf(64, 71, 193, 194, 195, 196, 197)
    private data class Entry(val legacyId: Int, val metadata: Int, val state: BlockState) {
        val values: Map<Property<*>, String> = state.values.toList().associate { it.property() to it.valueName() }
    }
    private val decoded = ConcurrentHashMap<Int, BlockState>()

    fun state(legacyBlockId: Int, metadata: Int): BlockState {
        require(legacyBlockId in validFamilies && metadata in 0..15) { "Invalid legacy block state $legacyBlockId:$metadata" }
        require(legacyBlockId != 144) { "Legacy skull states require skull type and rotation from block entity data" }
        require(legacyBlockId != 175 || metadata and 8 == 0) { "The upper half of a legacy double plant requires its lower state" }
        val id = legacyBlockId * 16 + metadata
        return decoded.computeIfAbsent(id) {
            val current = DataFixers.getDataFixer().update(
                References.BLOCK_STATE, BlockStateData.getTag(id), 1451,
                SharedConstants.getCurrentVersion().dataVersion().version()
            ).convert(NbtOps.INSTANCE).value
            val migrated = BlockState.CODEC.parse(NbtOps.INSTANCE, current).getOrThrow()
            if (legacyBlockId !in doors) migrated else {
                val base = migrated.block.defaultBlockState()
                if (metadata and 8 != 0) base.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                    .setValue(DoorBlock.HINGE, if (metadata and 1 == 0) DoorHingeSide.LEFT else DoorHingeSide.RIGHT)
                    .setValue(DoorBlock.POWERED, metadata and 2 != 0)
                else base.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                    .setValue(DoorBlock.FACING, Direction.from2DDataValue(metadata and 3).counterClockWise)
                    .setValue(DoorBlock.OPEN, metadata and 4 != 0)
            }
        }
    }

    fun upperPlant(lower: BlockState): BlockState {
        require(lower.block is DoublePlantBlock && lower.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER)
        return lower.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
    }

    private val entries by lazy {
        LegacyVanillaStateIds.values.filterNot { it ushr 4 == 144 || (it ushr 4 == 175 && it and 8 != 0) }
            .map { id -> Entry(id ushr 4, id and 15, state(id ushr 4, id and 15)) }
    }
    private val families by lazy { entries.groupBy { it.legacyId } }
    private val byBlock by lazy { entries.groupBy { it.state.block } }

    private fun family(block: Block): Int? {
        val candidates = byBlock[block]?.groupBy { it.legacyId } ?: return null
        if (candidates.size == 1) return candidates.keys.single()
        val matchingDefault = candidates.filterValues { list -> list.any { it.state == block.defaultBlockState() } }
        if (matchingDefault.size == 1) return matchingDefault.keys.single()
        val possible = matchingDefault.ifEmpty { candidates }.keys.toList()
        val signatures = possible.map { id -> (0..15).map { state(id, it) } }
        check(signatures.distinct().size == 1) { "Ambiguous legacy family for ${block.descriptionId}, specify one of $possible" }
        return possible.first()
    }

    fun state(block: Block, metadata: Int): BlockState {
        val family = family(block)
        if (family != null) return state(family, metadata)
        check(block.stateDefinition.properties.isEmpty()) { "No legacy state mapping for ${block.descriptionId}" }
        return block.defaultBlockState()
    }

    fun metadata(block: Block, state: BlockState): Int {
        val family = family(block)
        if (family != null) return metadata(family, state)
        check(block === state.block && block.stateDefinition.properties.isEmpty()) { "No legacy metadata mapping for $state" }
        return 0
    }

    fun metadata(legacyBlockId: Int, state: BlockState): Int {
        if (legacyBlockId == 175 && state.block is DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) return 8
        var candidates = requireNotNull(families[legacyBlockId]) { "Unknown legacy family $legacyBlockId" }.filter { it.state.block === state.block }
        check(candidates.isNotEmpty()) { "State $state does not belong to legacy family $legacyBlockId" }
        if (legacyBlockId in doors) candidates = candidates.filter { it.state.getValue(DoorBlock.HALF) == state.getValue(DoorBlock.HALF) }
        // Computed shapes and connections are not encoded in metadata
        val properties = candidates.flatMap { it.values.keys }.distinct().filter { property ->
            candidates.map { it.values[property] }.distinct().size > 1
        }
        val values = state.values.toList().associate { it.property() to it.valueName() }
        return candidates.firstOrNull { entry ->
            entry.state.block === state.block && properties.all { values[it] == entry.values[it] }
        }?.metadata ?: error("No legacy metadata representation for $state in family $legacyBlockId")
    }
}
