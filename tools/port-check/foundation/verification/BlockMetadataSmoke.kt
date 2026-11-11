package foundation.verification

import foundation.block.LegacyVanillaBlockStates
import foundation.block.LegacyVanillaStateIds
import foundation.block.getStateFromMeta
import foundation.block.getMetaFromState
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.DoublePlantBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraft.world.level.block.state.properties.StairsShape

fun verifyLegacyBlockMetadata() {
    val legacy = LegacyVanillaBlockStates
    check(legacy.state(1, 1).block === Blocks.GRANITE)
    check(legacy.state(1, 3).block === Blocks.DIORITE)
    check(legacy.state(1, 5).block === Blocks.ANDESITE)
    check(legacy.state(5, 5).block === Blocks.DARK_OAK_PLANKS)
    check(legacy.state(35, 14).block === Blocks.WOOL.pick(net.minecraft.world.item.DyeColor.RED))
    val top = legacy.state(44, 8)
    val double = legacy.state(43, 0)
    check(top.block === Blocks.SMOOTH_STONE_SLAB && top.getValue(SlabBlock.TYPE) == SlabType.TOP)
    check(double.block === Blocks.SMOOTH_STONE_SLAB && double.getValue(SlabBlock.TYPE) == SlabType.DOUBLE)
    check(Blocks.STONE.getStateFromMeta(3).block === Blocks.DIORITE)
    check(Blocks.STONE.getMetaFromState(Blocks.GRANITE.defaultBlockState()) == 1)
    check(Blocks.FURNACE.getMetaFromState(legacy.state(61, 2)) == 2)
    check(Blocks.SMOOTH_STONE_SLAB.getStateFromMeta(0).getValue(SlabBlock.TYPE) == SlabType.BOTTOM)
    val stairs = legacy.state(67, 0).setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)
    check(legacy.metadata(67, stairs) == 0)
    val fence = legacy.state(85, 0).setValue(BlockStateProperties.NORTH, true)
    check(legacy.metadata(85, fence) == 0)
    for (meta in 0..15) {
        val door = legacy.state(64, meta)
        val expected = if (meta and 8 != 0) 8 or (meta and 3) else meta
        check(legacy.metadata(64, door) == expected)
        if (meta and 8 != 0) {
            val joined = door.setValue(DoorBlock.FACING, Direction.WEST).setValue(DoorBlock.OPEN, true)
            check(legacy.metadata(64, joined) == expected)
        }
    }
    val lower = legacy.state(175, 4)
    val upper = legacy.upperPlant(lower)
    check(lower.block === Blocks.ROSE_BUSH && upper.block === lower.block)
    check(upper.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER)
    check(legacy.metadata(175, upper) == 8)
    val colors = listOf(Blocks.WOOL, Blocks.STAINED_GLASS, Blocks.DYED_TERRACOTTA,
        Blocks.STAINED_GLASS_PANE, Blocks.CARPET, Blocks.CONCRETE, Blocks.CONCRETE_POWDER)
    for (collection in colors) for (metadata in 0..15) {
        val state = collection.getStateFromMeta(metadata)
        check(state.block === collection.pick(net.minecraft.world.item.DyeColor.byId(metadata)))
        check(collection.getMetaFromState(state) == metadata)
    }
    check(runCatching { Blocks.BED.getStateFromMeta(3) }.isFailure)
    check(runCatching { legacy.state(253, 0) }.isFailure)
    check(runCatching { legacy.state(144, 1) }.isFailure)
    check(runCatching { legacy.state(175, 8) }.isFailure)
    check(runCatching { Blocks.SCULK_SENSOR.getStateFromMeta(0) }.isFailure)
    var checked = 0
    for (id in LegacyVanillaStateIds.values) {
        val family = id ushr 4
        val metadata = id and 15
        if (family == 144 || (family == 175 && metadata and 8 != 0)) continue
        val state = legacy.state(family, metadata)
        val restored = legacy.state(family, legacy.metadata(family, state))
        check(restored == state) { "Legacy state $family:$metadata changed on round trip: $state -> $restored" }
        checked++
    }
    println("Passed $checked explicit vanilla metadata state round trips")
}
