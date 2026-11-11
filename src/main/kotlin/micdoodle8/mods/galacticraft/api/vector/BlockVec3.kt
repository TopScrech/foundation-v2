package micdoodle8.mods.galacticraft.api.vector

import net.minecraft.CrashReport
import net.minecraft.ReportedException
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.floor
import kotlin.math.sqrt

/** Mutable integer coordinates used by the oxygen and power network searches */
open class BlockVec3 @JvmOverloads constructor(
    @JvmField var x: Int = 0,
    @JvmField var y: Int = 0,
    @JvmField var z: Int = 0
) : Cloneable {
    @JvmField var sideDoneBits: Int = 0

    constructor(pos: BlockPos) : this(pos.x, pos.y, pos.z)
    constructor(entity: Entity) : this(floor(entity.x).toInt(), floor(entity.y).toInt(), floor(entity.z).toInt())
    constructor(blockEntity: BlockEntity) : this(blockEntity.blockPos)
    constructor(tag: CompoundTag) : this(tag.getIntOr("x", 0), tag.getIntOr("y", 0), tag.getIntOr("z", 0))

    public override fun clone(): BlockVec3 = BlockVec3(x, y, z)
    open fun toBlockPos(): BlockPos = BlockPos(x, y, z)

    /** Read a block, loading its chunk when necessary, using the level's actual height limits */
    open fun getBlockState(world: Level): BlockState? {
        val pos = toBlockPos()
        if (!world.isInWorldBounds(pos)) return null
        return reportReadFailure { world.getBlockState(pos) }
    }

    /** Unloaded chunks act as an impermeable boundary for the oxygen sealer */
    open fun getBlockState_noChunkLoad(world: Level): BlockState? {
        val pos = toBlockPos()
        if (!world.isInWorldBounds(pos)) return null
        return reportReadFailure {
            world.chunkSource.getChunkNow(x shr 4, z shr 4)?.getBlockState(pos)
                ?: Blocks.BEDROCK.defaultBlockState()
        }
    }

    open fun getBlockStateSafe_noChunkLoad(world: Level): BlockState? = getBlockState_noChunkLoad(world)
    open fun getBlockState(world: BlockGetter): BlockState = world.getBlockState(toBlockPos())

    private inline fun reportReadFailure(read: () -> BlockState): BlockState = try {
        read()
    } catch (cause: Throwable) {
        val report = CrashReport.forThrowable(cause, "Oxygen sealer: exception reading block state")
        report.addCategory("Requested block coordinates").setDetail("Location", toBlockPos().toString())
        throw ReportedException(report)
    }

    open fun translate(vector: BlockVec3): BlockVec3 = translate(vector.x, vector.y, vector.z)
    open fun translate(dx: Int, dy: Int, dz: Int): BlockVec3 {
        x += dx
        y += dy
        z += dz
        return this
    }

    open fun subtract(vector: BlockVec3): BlockVec3 = translate(-vector.x, -vector.y, -vector.z)
    open fun scale(amount: Int): BlockVec3 {
        x *= amount
        y *= amount
        z *= amount
        return this
    }

    @JvmOverloads
    open fun modifyPositionFromSide(side: Direction, amount: Int = 1): BlockVec3 =
        translate(side.stepX * amount, side.stepY * amount, side.stepZ * amount)

    open fun newVecSide(side: Int): BlockVec3 {
        val vector = clone()
        vector.sideDoneBits = (1 shl (side xor 1)) + (side shl 6)
        if (side in 0..5) vector.modifyPositionFromSide(Direction.from3DDataValue(side))
        return vector
    }

    override fun hashCode(): Int = ((y * 379 + x) * 373 + z) * 7
    override fun equals(other: Any?): Boolean = other is BlockVec3 && x == other.x && y == other.y && z == other.z
    override fun toString(): String = "[$x,$y,$z]"

    open fun getTileEntity(world: BlockGetter): BlockEntity? = world.getBlockEntity(toBlockPos())

    open fun getTileEntityOnSide(world: Level, side: Direction): BlockEntity? {
        val pos = toBlockPos().relative(side)
        return world.chunkSource.getChunkNow(pos.x shr 4, pos.z shr 4)?.getBlockEntity(pos)
    }

    open fun getTileEntityOnSide(world: Level, side: Int): BlockEntity? =
        if (side in 0..5) getTileEntityOnSide(world, Direction.from3DDataValue(side)) else null

    open fun blockOnSideHasSolidFace(world: Level, side: Int): Boolean {
        if (side !in 0..5) return false
        val direction = Direction.from3DDataValue(side)
        val pos = toBlockPos().relative(direction)
        return world.getBlockState(pos).isFaceSturdy(world, pos, direction.opposite)
    }

    open fun getBlockOnSide(world: Level, side: Int): Block? {
        if (side !in 0..5) return null
        val pos = toBlockPos().relative(Direction.from3DDataValue(side))
        return world.chunkSource.getChunkNow(pos.x shr 4, pos.z shr 4)?.getBlockState(pos)?.block
    }

    /** State properties replace the numeric metadata used by 1.12 */
    open fun getBlockMetadata(world: BlockGetter): BlockState = getBlockState(world)

    open fun distanceTo(vector: BlockVec3): Int = floor(sqrt(distanceSquared(vector).toDouble())).toInt()
    open fun distanceSquared(vector: BlockVec3): Int {
        val dx = vector.x - x
        val dy = vector.y - y
        val dz = vector.z - z
        return dx * dx + dy * dy + dz * dz
    }

    open fun writeToNBT(tag: CompoundTag): CompoundTag {
        tag.putInt("x", x)
        tag.putInt("y", y)
        tag.putInt("z", z)
        return tag
    }

    open fun writeToNBT(tag: CompoundTag, prefix: String?): CompoundTag {
        tag.putInt("${prefix}_x", x)
        tag.putInt("${prefix}_y", y)
        tag.putInt("${prefix}_z", z)
        return tag
    }

    open fun getMagnitude(): Double = sqrt(getMagnitudeSquared().toDouble())
    open fun getMagnitudeSquared(): Int = x * x + y * y + z * z
    open fun setBlock(world: Level, state: BlockState) {
        world.setBlock(toBlockPos(), state, Block.UPDATE_ALL)
    }

    open fun blockExists(world: Level): Boolean = world.chunkSource.hasChunk(x shr 4, z shr 4)
    open fun setSideDone(side: Int) {
        sideDoneBits = sideDoneBits or (1 shl side)
    }

    open fun getTileEntityForce(world: Level): BlockEntity? = world.getChunk(x shr 4, z shr 4).getBlockEntity(toBlockPos())
    open fun midPoint(): Vector3 = Vector3(x + 0.5, y + 0.5, z + 0.5)

    companion object {
        @JvmField val INVALID_VECTOR: BlockVec3 = BlockVec3(-1, -1, -1)
        @JvmStatic fun add(vector: BlockVec3, amount: BlockVec3): BlockVec3 =
            BlockVec3(vector.x + amount.x, vector.y + amount.y, vector.z + amount.z)
        @JvmStatic fun readFromNBT(tag: CompoundTag): BlockVec3 = BlockVec3(tag)
        @JvmStatic fun readFromNBT(tag: CompoundTag, prefix: String?): BlockVec3? {
            val x = tag.getInt("${prefix}_x").orElse(null) ?: return null
            val y = tag.getInt("${prefix}_y").orElse(null) ?: return null
            val z = tag.getInt("${prefix}_z").orElse(null) ?: return null
            return BlockVec3(x, y, z)
        }
    }
}
