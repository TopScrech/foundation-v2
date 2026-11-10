package micdoodle8.mods.galacticraft.planets.venus.entities.ai

import micdoodle8.mods.galacticraft.planets.venus.entities.EntityJuicer
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityChicken
import net.minecraft.pathfinding.PathFinder
import net.minecraft.pathfinding.PathNavigate
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.abs
import kotlin.math.sqrt

open class PathNavigateCeiling constructor(entity: EntityJuicer, worldIn: World) : PathNavigate(entity, worldIn) {
    @JvmField
    protected var ceilingNodeProcessor: WalkNodeProcessorCeiling? = null

    override fun getPathFinder(): PathFinder {
        this.ceilingNodeProcessor = WalkNodeProcessorCeiling()
        return PathFinder(this.ceilingNodeProcessor)
    }

    override fun canNavigate(): Boolean {
        return this.entity.onGround || this.entity.isRiding() && this.entity is EntityZombie && this.entity.getRidingEntity() is EntityChicken
    }

    override fun getEntityPosition(): Vec3d {
        return Vec3d(this.entity.posX, this.getPathablePosY().toDouble(), this.entity.posZ)
    }

    private fun getPathablePosY(): Int {
        return (this.entity.getEntityBoundingBox().minY + 0.5).toInt()
    }

    override fun isDirectPathBetweenPoints(current: Vec3d, target: Vec3d, sizeX: Int, sizeY: Int, sizeZ: Int): Boolean {
        var sizeX: Int = sizeX
        var sizeZ: Int = sizeZ
        var i: Int = MathHelper.floor(current.x)
        var j: Int = MathHelper.floor(current.z)
        var d0: Double = target.x - current.x
        var d1: Double = target.z - current.z
        val d2: Double = d0 * d0 + d1 * d1

        if (d2 < 1.0E-8) {
            return false
        } else {
            val d3: Double = 1.0 / sqrt(d2)
            d0 = d0 * d3
            d1 = d1 * d3
            sizeX = sizeX + 2
            sizeZ = sizeZ + 2

            if (!this.isSafeToStandAt(i, current.y.toInt(), j, sizeX, sizeY, sizeZ, current, d0, d1)) {
                return false
            } else {
                sizeX = sizeX - 2
                sizeZ = sizeZ - 2
                val d4: Double = 1.0 / abs(d0)
                val d5: Double = 1.0 / abs(d1)
                var d6: Double = (i).toDouble() - current.x
                var d7: Double = (j).toDouble() - current.z

                if (d0 >= 0.0) {
                    ++d6
                }

                if (d1 >= 0.0) {
                    ++d7
                }

                d6 = d6 / d0
                d7 = d7 / d1
                val k: Int = if (d0 < 0.0) -1 else 1
                val l: Int = if (d1 < 0.0) -1 else 1
                val i1: Int = MathHelper.floor(target.x)
                val j1: Int = MathHelper.floor(target.z)
                var k1: Int = i1 - i
                var l1: Int = j1 - j

                while (k1 * k > 0 || l1 * l > 0) {
                    if (d6 < d7) {
                        d6 += d4
                        i += k
                        k1 = i1 - i
                    } else {
                        d7 += d5
                        j += l
                        l1 = j1 - j
                    }

                    if (!this.isSafeToStandAt(i, current.y.toInt(), j, sizeX, sizeY, sizeZ, current, d0, d1)) {
                        return false
                    }
                }

                return true
            }
        }
    }

    private fun isSafeToStandAt(
        x: Int,
        y: Int,
        z: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        currentPos: Vec3d,
        distanceX: Double,
        distanceZ: Double
    ): Boolean {
        val i: Int = x - sizeX / 2
        val j: Int = z - sizeZ / 2

        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, currentPos, distanceX, distanceZ)) {
            return false
        } else {
            for (k: Int in i..<i + sizeX) {
                for (l: Int in j..<j + sizeZ) {
                    val d0: Double = k.toDouble() + 0.5 - currentPos.x
                    val d1: Double = l.toDouble() + 0.5 - currentPos.z

                    if (d0 * distanceX + d1 * distanceZ >= 0.0) {
                        val state: IBlockState = this.world.getBlockState(BlockPos(k, y + 1, l))
                        val material: Material = state.getMaterial()

                        if (material === Material.AIR) {
                            return false
                        }

                        if (material === Material.WATER && !this.entity.isInWater()) {
                            return false
                        }

                        if (material === Material.LAVA) {
                            return false
                        }
                    }
                }
            }

            return true
        }
    }

    private fun isPositionClear(
        minX: Int,
        minY: Int,
        minZ: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        currentPos: Vec3d,
        distanceX: Double,
        distanceZ: Double
    ): Boolean {
        for (blockpos: BlockPos in BlockPos.getAllInBox(
            BlockPos(minX, minY, minZ),
            BlockPos(minX + sizeX - 1, minY + sizeY - 1, minZ + sizeZ - 1)
        )) {
            val d0: Double = blockpos.getX().toDouble() + 0.5 - currentPos.x
            val d1: Double = blockpos.getZ().toDouble() + 0.5 - currentPos.z

            if (d0 * distanceX + d1 * distanceZ >= 0.0) {
                val block: Block = this.world.getBlockState(blockpos).getBlock()

                if (!block.isPassable(this.world, blockpos)) {
                    return false
                }
            }
        }

        return true
    }
}
