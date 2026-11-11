package foundation.verification

import com.mojang.serialization.JsonOps
import foundation.level.StructureBoundsAccumulator
import foundation.level.horizontalStructureBounds
import io.netty.buffer.Unpooled
import net.minecraft.core.Direction
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.Random

fun verifyStructureBounds() {
    fun coordinates(box: BoundingBox) = listOf(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())
    fun roundTrip(box: BoundingBox) {
        val json = BoundingBox.CODEC.encodeStart(JsonOps.INSTANCE, box).getOrThrow()
        check(BoundingBox.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow() == box)
        val bytes = Unpooled.buffer()
        try {
            BoundingBox.STREAM_CODEC.encode(bytes, box)
            check(BoundingBox.STREAM_CODEC.decode(bytes) == box && !bytes.isReadable)
        } finally { bytes.release() }
    }
    val accumulator = StructureBoundsAccumulator()
    check(accumulator.bounds() == null)
    accumulator.include(-17, -64, 31)
    val snapshot = requireNotNull(accumulator.bounds())
    check(coordinates(snapshot) == listOf(-17, -64, 31, -17, -64, 31))
    accumulator.include(47, 319, -11)
    accumulator.include(-1, 6, 0)
    val accumulated = requireNotNull(accumulator.bounds())
    check(coordinates(accumulated) == listOf(-17, -64, -11, 47, 319, 31))
    check(snapshot.minZ() == 31 && snapshot.maxX() == -17)
    check(accumulated.isInside(-17, -64, -11) && accumulated.isInside(47, 319, 31))
    check(!accumulated.isInside(48, 319, 31))
    roundTrip(accumulated)
    val flat = horizontalStructureBounds(-32, -17, 15, 31)
    check(flat.minY() == 1 && flat.maxY() == 512 && flat.xSpan == 48 && flat.zSpan == 49)
    check(flat.intersects(horizontalStructureBounds(15, 31, 16, 32)))
    check(!flat.intersects(horizontalStructureBounds(16, 32, 17, 33)))
    roundTrip(flat)

    // The optional cached reference uses the original Forge 1.12 classes, not a second copy of our formulas
    val reference = System.getProperty("foundation.legacyForgeJar")?.let { LegacyStructureBounds(Path.of(it)) }
    reference.use { legacy ->
        val random = Random(0xF0263L)
        var hash = -3750763034362895579L
        fun record(values: List<Int>) { for (v in values) hash = (hash xor v.toLong()) * 1099511628211L }
        repeat(2048) {
            val foot = IntArray(3) { random.nextInt(2000001) - 1000000 }
            val offset = IntArray(3) { random.nextInt(41) - 20 }
            val size = IntArray(3) { random.nextInt(65) + 1 }
            val horizontal = horizontalStructureBounds(foot[0], foot[2], foot[0] + size[0], foot[2] + size[2])
            val horizontalCoordinates = coordinates(horizontal)
            legacy?.let { check(horizontalCoordinates == it.horizontal(foot[0], foot[2], foot[0] + size[0], foot[2] + size[2])) }
            record(horizontalCoordinates)
            for (direction in Direction.values()) {
                val box = BoundingBox.orientBox(foot[0], foot[1], foot[2], offset[0], offset[1], offset[2], size[0], size[1], size[2], direction)
                val actual = coordinates(box)
                legacy?.let { check(actual == it.oriented(foot + offset + size, direction.name)) { "Structure orientation changed for $direction" } }
                check(box.xSpan > 0 && box.ySpan > 0 && box.zSpan > 0)
                record(actual)
                val copy = box.moved(0, 0, 0)
                copy.move(3, -2, 7)
                check(box.minX() + 3 == copy.minX() && box.maxY() - 2 == copy.maxY() && box.maxZ() + 7 == copy.maxZ())
            }
        }
        check(hash == -4699144498855204776L) { "Structure geometry differed from the original Forge corpus: $hash" }
        println("Structure geometry reference hash: $hash; original Forge comparison: ${legacy != null}")
    }
    println("Passed native structure bounds, inclusive intersections, empty and populated room accumulation, independent snapshots, codecs, and orientation corpus")
}

private class LegacyStructureBounds(jar: Path) : AutoCloseable {
    private val loader = URLClassLoader(arrayOf(jar.toUri().toURL()), StructureBoundsAccumulator::class.java.classLoader)
    private val boxClass = loader.loadClass("net.minecraft.world.gen.structure.StructureBoundingBox")
    private val directionClass = loader.loadClass("net.minecraft.util.EnumFacing")
    private val horizontal = boxClass.getConstructor(*Array(4) { Int::class.javaPrimitiveType })
    private val orient = boxClass.getMethod("getComponentToAddBoundingBox", *Array(9) { Int::class.javaPrimitiveType }, directionClass)
    private val fields = listOf("minX", "minY", "minZ", "maxX", "maxY", "maxZ").map { boxClass.getField(it) }
    private fun coordinates(box: Any): List<Int> = fields.map { it.getInt(box) }
    fun horizontal(minX: Int, minZ: Int, maxX: Int, maxZ: Int): List<Int> = coordinates(horizontal.newInstance(minX, minZ, maxX, maxZ))
    fun oriented(arguments: IntArray, direction: String): List<Int> = coordinates(orient.invoke(null, *arguments.toTypedArray(), directionClass.getField(direction).get(null)))
    override fun close() = loader.close()
}
