package foundation.schematic

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

/** Persisted wall anchor and legacy schematic registry index */
data class FoundationSchematicState(val position: BlockPos, val direction: Direction, val schematic: Int) {
    init {
        require(direction.axis.isHorizontal) { "Schematics must face a horizontal direction" }
        require(schematic >= 0) { "Negative schematic index" }
    }

    fun write(output: ValueOutput) {
        output.store("block_pos", BlockPos.CODEC, position)
        output.store("facing", Direction.LEGACY_ID_CODEC_2D, direction)
        output.putInt("schem", schematic)
    }

    companion object {
        fun read(input: ValueInput, entityPosition: BlockPos): FoundationSchematicState {
            val position = input.read("block_pos", BlockPos.CODEC).orElseGet {
                BlockPos(input.getIntOr("TileX", entityPosition.x), input.getIntOr("TileY", entityPosition.y), input.getIntOr("TileZ", entityPosition.z))
            }
            require(position.closerThan(entityPosition, 16.0)) { "Schematic anchor is too far from its saved entity position" }
            val direction = input.read("facing", Direction.LEGACY_ID_CODEC_2D).orElseGet {
                Direction.from2DDataValue(input.getIntOr("Facing", 0))
            }
            return FoundationSchematicState(position, direction, input.getIntOr("schem", 0))
        }

        /** The original 32-pixel square used the same half-block alignment as a 2 by 2 painting */
        fun bounds(position: BlockPos, direction: Direction): AABB {
            require(direction.axis.isHorizontal)
            val center = Vec3.atCenterOf(position).relative(direction, -0.46875)
                .relative(direction.counterClockWise, 0.5).relative(Direction.UP, 0.5)
            return AABB.ofSize(center, if (direction.axis == Direction.Axis.X) 0.0625 else 2.0, 2.0, if (direction.axis == Direction.Axis.Z) 0.0625 else 2.0)
        }
    }
}
