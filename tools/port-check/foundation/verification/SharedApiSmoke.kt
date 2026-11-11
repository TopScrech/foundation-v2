package foundation.verification

import micdoodle8.mods.galacticraft.api.event.wgen.GCCoreEventPopulate
import micdoodle8.mods.galacticraft.api.event.ZeroGravityEvent
import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent
import micdoodle8.mods.galacticraft.api.transmission.grid.Pathfinder
import micdoodle8.mods.galacticraft.api.transmission.grid.IPathCallBack
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorage
import foundation.level.FoundationDimensionKeys
import foundation.level.offset
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import kotlin.math.abs

private fun near(actual: Double, expected: Double, tolerance: Double = 1e-6) {
    check(abs(actual - expected) <= tolerance) { "Expected $expected, got $actual" }
}

fun main() {
    verifyLegacyNbtTypes()
    verifyTerrainNoise()
    val position = BlockPos(-17, -64, 31)
    check(position.offset(0.0, 0.0, 0.0) === position)
    check(position.offset(-0.2, 0.8, 1.25) == BlockPos(-18, -64, 32))
    check(position.offset(1, -0.2, 0) == BlockPos(-16, -65, 31))
    check(position.offset(1, 2, 3) == BlockPos(-16, -62, 34))
    val coordinates = BlockVec3(-17, -64, 31)
    check(coordinates.toBlockPos() == BlockPos(-17, -64, 31))
    check(BlockVec3(coordinates.writeToNBT(CompoundTag())) == coordinates)
    check(BlockVec3.readFromNBT(CompoundTag(), "target") == null)
    val partial = CompoundTag().also { it.putInt("target_x", 7) }
    check(BlockVec3.readFromNBT(partial, "target") == null)
    check(BlockVec3.readFromNBT(coordinates.writeToNBT(CompoundTag(), "target"), "target") == coordinates)
    for (direction in Direction.values()) {
        val adjacent = coordinates.newVecSide(direction.get3DDataValue())
        check(adjacent.toBlockPos() == coordinates.toBlockPos().relative(direction))
        check(coordinates.toBlockPos() == BlockPos(-17, -64, 31))
        check(adjacent.sideDoneBits and (1 shl direction.opposite.get3DDataValue()) != 0)
    }

    val point = Vector3(-1.25, -63.75, 32.5)
    val saved = Vector3(point.writeToNBT(CompoundTag()))
    check(saved == point)
    check(point.intX() == -2 && point.intY() == -64 && point.intZ() == 32)
    val rotated = Vector3(1.0, 0.0, 0.0)
    rotated.rotate(90.0)
    near(rotated.x, 0.0)
    near(rotated.z, 1.0)
    near(rotated.getMagnitude(), 1.0)
    for (pitch in listOf(-90f, -45f, 0f, 45f, 90f)) {
        val look = Vector3.getDeltaPositionFromRotation(0f, pitch)
        near(look.getMagnitude(), 1.0)
    }
    val straightUp = Vector3.getDeltaPositionFromRotation(0f, -90f)
    near(straightUp.x, 0.0)
    near(straightUp.y, 1.0)
    near(straightUp.z, 0.0)
    near(Vector3(1.0 + 1e-12, 0.0, 0.0).anglePreNorm(Vector3(1.0, 0.0, 0.0)), 0.0)
    val moon = FoundationDimensionKeys.parse("galacticraftcore:moon")
    FoundationDimensionKeys.registerLegacyId(-28, moon)
    val legacy = CompoundTag().also { it.putInt("dim", -28); it.putInt("y", -64) }
    val imported = BlockVec3Dim(legacy)
    check(imported.dim == moon && imported.y == -64)
    val modern = imported.writeToNBT(CompoundTag())
    check(modern.getString("dimension").orElseThrow() == "galacticraftcore:moon")
    check(!modern.contains("dim"))
    check(BlockVec3Dim(modern) == imported)
    check(BlockVec3Dim(0, -64, 0, -28).dim == moon)
    check(runCatching { FoundationDimensionKeys.fromLegacyId(123456) }.isFailure)
    check(runCatching { FoundationDimensionKeys.registerLegacyId(-28, FoundationDimensionKeys.parse("galaxyspace:europa")) }.isFailure)
    var decorationCalls = 0
    val listener = GCCoreEventPopulate.Pre.BUS.addListener { event: GCCoreEventPopulate.Pre ->
        check(event.pos == BlockPos(16, -64, -32))
        decorationCalls++
    }
    try {
        check(!GCCoreEventPopulate.Pre(null, null, BlockPos(16, -64, -32)).post())
        GCCoreEventPopulate.Post(null, null, BlockPos.ZERO).post()
        check(decorationCalls == 1)
    } finally {
        GCCoreEventPopulate.Pre.BUS.removeListener(listener)
    }
    check(!ZeroGravityEvent.InFreefall.BUS.hasListeners())
    check(!GCCoreOxygenSuffocationEvent.Pre.BUS.hasListeners())
    val endpoint = 20_000
    val search = Pathfinder(object : IPathCallBack {
        override fun getConnectedNodes(finder: Pathfinder?, currentNode: BlockVec3?): MutableSet<BlockVec3?> {
            val node = requireNotNull(currentNode)
            return linkedSetOf<BlockVec3?>().also { neighbors ->
                if (node.x > 0) neighbors.add(BlockVec3(node.x - 1, 0, 0))
                if (node.x < endpoint) neighbors.add(BlockVec3(node.x + 1, 0, 0))
            }
        }
        override fun onSearch(finder: Pathfinder?, node: BlockVec3?): Boolean {
            if (node?.x != endpoint) return false
            requireNotNull(finder).results.add(node)
            return true
        }
    })
    check(search.findNodes(BlockVec3()))
    check(search.closedSet.size == endpoint + 1)
    check(search.results == setOf(BlockVec3(endpoint, 0, 0)))
    search.reset()
    check(search.closedSet.isEmpty() && search.results.isEmpty())
    check(!search.findNodes(null))
    val storage = EnergyStorage(100f, 40f, 20f)
    near(storage.receiveEnergyGC(80f, true).toDouble(), 40.0)
    near(storage.getEnergyStoredGC().toDouble(), 0.0)
    near(storage.receiveEnergyGC(80f, false).toDouble(), 40.0)
    near(storage.extractEnergyGC(30f, true).toDouble(), 20.0)
    near(storage.getEnergyStoredGC().toDouble(), 40.0)
    near(storage.extractEnergyGC(30f, false).toDouble(), 20.0)
    val energyTag = storage.writeToNBT(CompoundTag())
    near(EnergyStorage(100f).readFromNBT(energyTag).getEnergyStoredGC().toDouble(), 20.0)
    check(storage.receiveEnergyGC(-10f, false) == 0f)
    check(storage.extractEnergyGC(-10f, false) == 0f)
    check(storage.receiveEnergyGC(Float.NaN, false) == 0f)
    near(storage.getEnergyStoredGC().toDouble(), 20.0)
    val corruptEnergy = CompoundTag().also { it.putFloat("EnergyF", Float.NaN) }
    near(storage.readFromNBT(corruptEnergy).getEnergyStoredGC().toDouble(), 0.0)
    storage.setEnergyStored(80f)
    storage.setCapacity(50f)
    near(storage.getEnergyStoredGC().toDouble(), 50.0)
    verifyConfigurationRoundTrip()
    verifyPlanetConfigurationMigration()
    println("Passed shared API, persistence, event bus, network, energy, and configuration checks")
}
