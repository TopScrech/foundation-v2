package micdoodle8.mods.galacticraft.planets.venus.entities.ai

import com.google.common.collect.Sets
import net.minecraft.block.Block
import net.minecraft.block.BlockDoor
import net.minecraft.block.BlockFence
import net.minecraft.block.BlockFenceGate
import net.minecraft.block.BlockRailBase
import net.minecraft.block.BlockWall
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.init.Blocks
import net.minecraft.pathfinding.NodeProcessor
import net.minecraft.pathfinding.PathNodeType
import net.minecraft.pathfinding.PathPoint
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import java.util.EnumSet
import kotlin.math.max

open class WalkNodeProcessorCeiling : NodeProcessor() {
    private var avoidsWater = 0f

    override fun init(sourceIn: IBlockAccess, mob: EntityLiving) {
        super.init(sourceIn, mob)
        this.avoidsWater = mob.getPathPriority(PathNodeType.WATER)
    }

    override fun postProcess() {
        this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater)
        super.postProcess()
    }

    override fun getStart(): PathPoint {
        var i: Int

        if (this.getCanSwim() && this.entity.isInWater()) {
            i = this.entity.getEntityBoundingBox().minY.toInt()
            val `blockpos$mutableblockpos`: BlockPos.MutableBlockPos =
                BlockPos.MutableBlockPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ))

            var block: Block = this.blockaccess.getBlockState(`blockpos$mutableblockpos`).getBlock()
            while (block === Blocks.FLOWING_WATER || block === Blocks.WATER) {
                ++i
                `blockpos$mutableblockpos`.setPos(
                    MathHelper.floor(this.entity.posX),
                    i,
                    MathHelper.floor(this.entity.posZ)
                )
                block = this.blockaccess.getBlockState(`blockpos$mutableblockpos`).getBlock()
            }
        } else if (this.entity.onGround) {
            i = MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5)
        } else {
            var blockpos: BlockPos

            blockpos = BlockPos(this.entity)
            while ((this.blockaccess.getBlockState(blockpos)
                    .getMaterial() === Material.AIR || this.blockaccess.getBlockState(blockpos).getBlock()
                    .isPassable(this.blockaccess, blockpos)) && blockpos.getY() > 0
            ) {
                blockpos = blockpos.down()
            }

            i = blockpos.up().getY()
        }

        val blockpos2: BlockPos = BlockPos(this.entity)
        val pathnodetype1: PathNodeType = this.getPathNodeType(this.entity, blockpos2.getX(), i, blockpos2.getZ())

        if (this.entity.getPathPriority(pathnodetype1) < 0.0f) {
            val set: MutableSet<BlockPos> = Sets.newHashSet<BlockPos>()
            set.add(
                BlockPos(
                    this.entity.getEntityBoundingBox().minX,
                    i.toDouble(),
                    this.entity.getEntityBoundingBox().minZ
                )
            )
            set.add(
                BlockPos(
                    this.entity.getEntityBoundingBox().minX,
                    i.toDouble(),
                    this.entity.getEntityBoundingBox().maxZ
                )
            )
            set.add(
                BlockPos(
                    this.entity.getEntityBoundingBox().maxX,
                    i.toDouble(),
                    this.entity.getEntityBoundingBox().minZ
                )
            )
            set.add(
                BlockPos(
                    this.entity.getEntityBoundingBox().maxX,
                    i.toDouble(),
                    this.entity.getEntityBoundingBox().maxZ
                )
            )

            for (blockpos1: BlockPos in set) {
                val pathnodetype: PathNodeType = this.getPathNodeType(this.entity, blockpos1)

                if (this.entity.getPathPriority(pathnodetype) >= 0.0f) {
                    return this.openPoint(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ())
                }
            }
        }

        return this.openPoint(blockpos2.getX(), i, blockpos2.getZ())
    }

    override fun getPathPointToCoords(x: Double, y: Double, z: Double): PathPoint {
        return this.openPoint(
            MathHelper.floor(x - (this.entity.width / 2.0f).toDouble()),
            MathHelper.floor(y),
            MathHelper.floor(z - (this.entity.width / 2.0f).toDouble())
        )
    }

    override fun findPathOptions(
        pathOptions: Array<PathPoint?>,
        currentPoint: PathPoint,
        targetPoint: PathPoint,
        maxDistance: Float
    ): Int {
        var i: Int = 0
        var j: Int = 0
        val pathnodetype: PathNodeType =
            this.getPathNodeType(this.entity, currentPoint.x, currentPoint.y + 1, currentPoint.z)

        if (this.entity.getPathPriority(pathnodetype) >= 0.0f) {
            j = MathHelper.floor(max(1.0f, this.entity.stepHeight))
        }

        val blockpos: BlockPos = (BlockPos(currentPoint.x, currentPoint.y, currentPoint.z)).down()
        val d0: Double = currentPoint.y.toDouble() - (1.0 - this.blockaccess.getBlockState(blockpos)
            .getBoundingBox(this.blockaccess, blockpos).maxY)
        val pathpoint: PathPoint? =
            this.getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z + 1, j, d0, EnumFacing.SOUTH)
        val pathpoint1: PathPoint? =
            this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z, j, d0, EnumFacing.WEST)
        val pathpoint2: PathPoint? =
            this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z, j, d0, EnumFacing.EAST)
        val pathpoint3: PathPoint? =
            this.getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z - 1, j, d0, EnumFacing.NORTH)

        if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint
        }

        if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint1
        }

        if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint2
        }

        if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint3
        }

        val flag: Boolean =
            pathpoint3 == null || pathpoint3.nodeType == PathNodeType.OPEN || pathpoint3.costMalus != 0.0f
        val flag1: Boolean = pathpoint == null || pathpoint.nodeType == PathNodeType.OPEN || pathpoint.costMalus != 0.0f
        val flag2: Boolean =
            pathpoint2 == null || pathpoint2.nodeType == PathNodeType.OPEN || pathpoint2.costMalus != 0.0f
        val flag3: Boolean =
            pathpoint1 == null || pathpoint1.nodeType == PathNodeType.OPEN || pathpoint1.costMalus != 0.0f

        if (flag && flag3) {
            val pathpoint4: PathPoint? =
                this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z - 1, j, d0, EnumFacing.NORTH)

            if (pathpoint4 != null && !pathpoint4.visited && pathpoint4.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint4
            }
        }

        if (flag && flag2) {
            val pathpoint5: PathPoint? =
                this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1, j, d0, EnumFacing.NORTH)

            if (pathpoint5 != null && !pathpoint5.visited && pathpoint5.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint5
            }
        }

        if (flag1 && flag3) {
            val pathpoint6: PathPoint? =
                this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1, j, d0, EnumFacing.SOUTH)

            if (pathpoint6 != null && !pathpoint6.visited && pathpoint6.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint6
            }
        }

        if (flag1 && flag2) {
            val pathpoint7: PathPoint? =
                this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z + 1, j, d0, EnumFacing.SOUTH)

            if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint7
            }
        }

        return i
    }

    private fun getSafePoint(
        x: Int,
        y: Int,
        z: Int,
        p_186332_4_: Int,
        p_186332_5_: Double,
        facing: EnumFacing
    ): PathPoint? {
        var y: Int = y
        var pathpoint: PathPoint? = null
        val blockpos: BlockPos = BlockPos(x, y, z)
        val blockpos1: BlockPos = blockpos.down()
        val d0: Double = y.toDouble() - (1.0 - this.blockaccess.getBlockState(blockpos1)
            .getBoundingBox(this.blockaccess, blockpos1).maxY)

        if (d0 - p_186332_5_ > 1.125) {
            return null
        } else {
            var pathnodetype: PathNodeType = this.getPathNodeType(this.entity, x, y, z)
            var f: Float = this.entity.getPathPriority(pathnodetype)
            val d1: Double = this.entity.width.toDouble() / 2.0

            if (f >= 0.0f) {
                pathpoint = this.openPoint(x, y, z)
                pathpoint.nodeType = pathnodetype
                pathpoint.costMalus = max(pathpoint.costMalus, f)
            }

            if (pathnodetype == PathNodeType.WALKABLE) {
                return pathpoint
            } else {
                if (pathpoint == null && p_186332_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
                    pathpoint = this.getSafePoint(x, y + 1, z, p_186332_4_ - 1, p_186332_5_, facing)

                    if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.entity.width < 1.0f) {
                        val d2: Double = (x - facing.getXOffset()).toDouble() + 0.5
                        val d3: Double = (z - facing.getZOffset()).toDouble() + 0.5
                        val axisalignedbb: AxisAlignedBB = AxisAlignedBB(
                            d2 - d1,
                            y.toDouble() + 0.001,
                            d3 - d1,
                            d2 + d1,
                            (y.toFloat() + this.entity.height).toDouble(),
                            d3 + d1
                        )
                        val axisalignedbb1: AxisAlignedBB =
                            this.blockaccess.getBlockState(blockpos).getBoundingBox(this.blockaccess, blockpos)
                        val axisalignedbb2: AxisAlignedBB = axisalignedbb.expand(0.0, axisalignedbb1.maxY - 0.002, 0.0)

                        if (this.entity.world.collidesWithAnyBlock(axisalignedbb2)) {
                            pathpoint = null
                        }
                    }
                }

                if (pathnodetype == PathNodeType.OPEN) {
                    val axisalignedbb3: AxisAlignedBB = AxisAlignedBB(
                        x.toDouble() - d1 + 0.5,
                        y.toDouble() + 0.001,
                        z.toDouble() - d1 + 0.5,
                        x.toDouble() + d1 + 0.5,
                        (y.toFloat() + this.entity.height).toDouble(),
                        z.toDouble() + d1 + 0.5
                    )

                    if (this.entity.world.collidesWithAnyBlock(axisalignedbb3)) {
                        return null
                    }

                    if (this.entity.width >= 1.0f) {
                        val pathnodetype1: PathNodeType = this.getPathNodeType(this.entity, x, y - 1, z)

                        if (pathnodetype1 == PathNodeType.BLOCKED) {
                            pathpoint = this.openPoint(x, y, z)
                            pathpoint.nodeType = PathNodeType.WALKABLE
                            pathpoint.costMalus = max(pathpoint.costMalus, f)
                            return pathpoint
                        }
                    }

                    var i: Int = 0

                    while (y > 0 && pathnodetype == PathNodeType.OPEN) {
                        --y

                        if (i++ >= this.entity.getMaxFallHeight()) {
                            return null
                        }

                        pathnodetype = this.getPathNodeType(this.entity, x, y, z)
                        f = this.entity.getPathPriority(pathnodetype)

                        if (pathnodetype != PathNodeType.OPEN && f >= 0.0f) {
                            pathpoint = this.openPoint(x, y, z)
                            pathpoint.nodeType = pathnodetype
                            pathpoint.costMalus = max(pathpoint.costMalus, f)
                            break
                        }

                        if (f < 0.0f) {
                            return null
                        }
                    }
                }

                return pathpoint
            }
        }
    }

    override fun getPathNodeType(
        blockaccessIn: IBlockAccess,
        x: Int,
        y: Int,
        z: Int,
        entitylivingIn: EntityLiving,
        xSize: Int,
        ySize: Int,
        zSize: Int,
        canBreakDoorsIn: Boolean,
        canEnterDoorsIn: Boolean
    ): PathNodeType {
        val enumset: EnumSet<PathNodeType> = EnumSet.noneOf<PathNodeType>(PathNodeType::class.java)
        var pathnodetype: PathNodeType = PathNodeType.BLOCKED
        val d0: Double = entitylivingIn.width.toDouble() / 2.0
        val blockpos: BlockPos = BlockPos(entitylivingIn)

        for (i: Int in 0..<xSize) {
            for (j: Int in 0..<ySize) {
                for (k: Int in 0..<zSize) {
                    val l: Int = i + x
                    val i1: Int = j + y
                    val j1: Int = k + z
                    var pathnodetype1: PathNodeType = this.getPathNodeType(blockaccessIn, l, i1, j1)

                    if (pathnodetype1 == PathNodeType.DOOR_WOOD_CLOSED && canBreakDoorsIn && canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.WALKABLE
                    }

                    if (pathnodetype1 == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.BLOCKED
                    }

                    if (pathnodetype1 == PathNodeType.RAIL && (blockaccessIn.getBlockState(blockpos)
                            .getBlock() !is BlockRailBase) && (blockaccessIn.getBlockState(blockpos.down())
                            .getBlock() !is BlockRailBase)
                    ) {
                        pathnodetype1 = PathNodeType.FENCE
                    }

                    if (i == 0 && j == 0 && k == 0) {
                        pathnodetype = pathnodetype1
                    }

                    enumset.add(pathnodetype1)
                }
            }
        }

        if (enumset.contains(PathNodeType.FENCE)) {
            return PathNodeType.FENCE
        } else {
            var pathnodetype2: PathNodeType = PathNodeType.BLOCKED

            for (pathnodetype3: PathNodeType in enumset) {
                if (entitylivingIn.getPathPriority(pathnodetype3) < 0.0f) {
                    return pathnodetype3
                }

                if (entitylivingIn.getPathPriority(pathnodetype3) >= entitylivingIn.getPathPriority(pathnodetype2)) {
                    pathnodetype2 = pathnodetype3
                }
            }

            if (pathnodetype == PathNodeType.OPEN && entitylivingIn.getPathPriority(pathnodetype2) == 0.0f) {
                return PathNodeType.OPEN
            } else {
                return pathnodetype2
            }
        }
    }

    private fun getPathNodeType(entitylivingIn: EntityLiving, pos: BlockPos): PathNodeType {
        return this.getPathNodeType(entitylivingIn, pos.getX(), pos.getY(), pos.getZ())
    }

    private fun getPathNodeType(entitylivingIn: EntityLiving, x: Int, y: Int, z: Int): PathNodeType {
        return this.getPathNodeType(
            this.blockaccess,
            x,
            y,
            z,
            entitylivingIn,
            this.entitySizeX,
            this.entitySizeY,
            this.entitySizeZ,
            this.getCanOpenDoors(),
            this.getCanEnterDoors()
        )
    }

    override fun getPathNodeType(blockaccessIn: IBlockAccess, x: Int, y: Int, z: Int): PathNodeType {
        var pathnodetype: PathNodeType = this.getPathNodeTypeRaw(blockaccessIn, x, y, z)

        if (pathnodetype == PathNodeType.OPEN && y >= 1) {
            val block: Block = blockaccessIn.getBlockState(BlockPos(x, y - 1, z)).getBlock()
            val pathnodetype1: PathNodeType = this.getPathNodeTypeRaw(blockaccessIn, x, y - 1, z)
            pathnodetype =
                if (pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA) PathNodeType.WALKABLE else PathNodeType.OPEN

            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block === Blocks.MAGMA) {
                pathnodetype = PathNodeType.DAMAGE_FIRE
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
                pathnodetype = PathNodeType.DAMAGE_CACTUS
            }
        }

        val `blockpos$pooledmutableblockpos`: BlockPos.PooledMutableBlockPos = BlockPos.PooledMutableBlockPos.retain()

        if (pathnodetype == PathNodeType.WALKABLE) {
            for (j: Int in -1..1) {
                for (i: Int in -1..1) {
                    if (j != 0 || i != 0) {
                        val block1: Block =
                            blockaccessIn.getBlockState(`blockpos$pooledmutableblockpos`.setPos(j + x, y, i + z))
                                .getBlock()

                        if (block1 === Blocks.CACTUS) {
                            pathnodetype = PathNodeType.DANGER_CACTUS
                        } else if (block1 === Blocks.FIRE) {
                            pathnodetype = PathNodeType.DANGER_FIRE
                        }
                    }
                }
            }
        }

        `blockpos$pooledmutableblockpos`.release()
        return pathnodetype
    }

    private fun getPathNodeTypeRaw(
        p_189553_1_: IBlockAccess,
        p_189553_2_: Int,
        p_189553_3_: Int,
        p_189553_4_: Int
    ): PathNodeType {
        val blockpos: BlockPos = BlockPos(p_189553_2_, p_189553_3_, p_189553_4_)
        val iblockstate: IBlockState = p_189553_1_.getBlockState(blockpos)
        val block: Block = iblockstate.getBlock()
        val material: Material = iblockstate.getMaterial()
        return if (material === Material.AIR) PathNodeType.OPEN else (if (block !== Blocks.TRAPDOOR && block !== Blocks.IRON_TRAPDOOR && block !== Blocks.WATERLILY) (if (block === Blocks.FIRE) PathNodeType.DAMAGE_FIRE else (if (block === Blocks.CACTUS) PathNodeType.DAMAGE_CACTUS else (if (block is BlockDoor && material === Material.WOOD && !(iblockstate.getValue(
                BlockDoor.OPEN
            ) as Boolean)
        ) PathNodeType.DOOR_WOOD_CLOSED else (if (block is BlockDoor && material === Material.IRON && !(iblockstate.getValue(
                BlockDoor.OPEN
            ) as Boolean)
        ) PathNodeType.DOOR_IRON_CLOSED else (if (block is BlockDoor && (iblockstate.getValue(BlockDoor.OPEN) as Boolean)) PathNodeType.DOOR_OPEN else (if (block is BlockRailBase) PathNodeType.RAIL else (if ((block !is BlockFence) && (block !is BlockWall) && (block !is BlockFenceGate || (iblockstate.getValue(
                BlockFenceGate.OPEN
            ) as Boolean))
        ) (if (material === Material.WATER) PathNodeType.WATER else (if (material === Material.LAVA) PathNodeType.LAVA else (if (block.isPassable(
                p_189553_1_,
                blockpos
            )
        ) PathNodeType.OPEN else PathNodeType.BLOCKED))) else PathNodeType.FENCE))))))) else PathNodeType.TRAPDOOR)
    }
}
