package micdoodle8.mods.galacticraft.api.vector

import foundation.level.FoundationDimensionKeys
import foundation.level.FoundationLevels
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.floor
import kotlin.math.sqrt

/** A block position in a named dimension, independent of client/server numeric IDs */
open class BlockVec3Dim @JvmOverloads constructor(
    @JvmField var x: Int = 0,
    @JvmField var y: Int = 0,
    @JvmField var z: Int = 0,
    @JvmField var dim: ResourceKey<Level> = FoundationDimensionKeys.OVERWORLD
) : Cloneable {
    constructor(x: Int, y: Int, z: Int, legacyDimensionId: Int) :
        this(x, y, z, FoundationDimensionKeys.fromLegacyId(legacyDimensionId))
    constructor(entity: Entity) :
        this(floor(entity.x).toInt(), floor(entity.y).toInt(), floor(entity.z).toInt(), entity.level().dimension())
    constructor(blockEntity: BlockEntity) :
        this(blockEntity.blockPos, requireNotNull(blockEntity.level) { "Block entity is not attached to a level" }.dimension())
    constructor(pos: BlockPos, dimension: ResourceKey<Level>) : this(pos.x, pos.y, pos.z, dimension)
    constructor(pos: BlockPos, legacyDimensionId: Int) :
        this(pos, FoundationDimensionKeys.fromLegacyId(legacyDimensionId))
    constructor(tag: CompoundTag) :
        this(tag.getIntOr("x", 0), tag.getIntOr("y", 0), tag.getIntOr("z", 0), FoundationDimensionKeys.read(tag))

    public override fun clone(): BlockVec3Dim = BlockVec3Dim(x, y, z, dim)
    private fun coordinates(): BlockVec3 = BlockVec3(x, y, z)

    open fun getBlockState(): BlockState? = FoundationLevels.get(dim)?.let { coordinates().getBlockState(it) }
    open fun getBlockState_noChunkLoad(): BlockState? =
        FoundationLevels.get(dim)?.let { coordinates().getBlockState_noChunkLoad(it) }
    open fun getBlock(): Block? = getBlockState()?.block

    @JvmOverloads
    open fun modifyPositionFromSide(side: Direction, amount: Int = 1): BlockVec3Dim {
        x += side.stepX * amount
        y += side.stepY * amount
        z += side.stepZ * amount
        return this
    }

    open fun newVecSide(side: Int): BlockVec3Dim = clone().also {
        if (side in 0..5) it.modifyPositionFromSide(Direction.from3DDataValue(side))
    }

    override fun hashCode(): Int = (((z * 431 + x) * 379 + y) * 373 + dim.hashCode()) * 7
    override fun equals(other: Any?): Boolean =
        other is BlockVec3Dim && x == other.x && y == other.y && z == other.z && dim == other.dim
    override fun toString(): String = "BlockVec3 ${dim.identifier()}:[$x,$y,$z]"

    open fun getTileEntity(): BlockEntity? = FoundationLevels.get(dim)?.getBlockEntity(toBlockPos())
    open fun getTileEntityNoLoad(): BlockEntity? =
        FoundationLevels.get(dim)?.chunkSource?.getChunkNow(x shr 4, z shr 4)?.getBlockEntity(toBlockPos())
    open fun getBlockMetadata(): BlockState? = getBlockState()
    open fun writeToNBT(tag: CompoundTag): CompoundTag {
        coordinates().writeToNBT(tag)
        FoundationDimensionKeys.write(tag, dim)
        return tag
    }

    open fun getMagnitude(): Double = sqrt(getMagnitudeSquared().toDouble())
    open fun getMagnitudeSquared(): Int = x * x + y * y + z * z
    open fun setBlock(state: BlockState) {
        FoundationLevels.get(dim)?.setBlock(toBlockPos(), state, Block.UPDATE_ALL)
    }

    open fun blockExists(): Boolean = FoundationLevels.get(dim)?.chunkSource?.hasChunk(x shr 4, z shr 4) == true
    open fun distanceSquared(vector: BlockVec3): Int = coordinates().distanceSquared(vector)
    open fun toBlockPos(): BlockPos = BlockPos(x, y, z)

    companion object {
        @JvmField val INVALID_VECTOR: BlockVec3Dim =
            BlockVec3Dim(-1, -1, -1, FoundationDimensionKeys.parse("foundation:invalid"))
        @JvmStatic fun readFromNBT(tag: CompoundTag): BlockVec3Dim = BlockVec3Dim(tag)
    }
}
