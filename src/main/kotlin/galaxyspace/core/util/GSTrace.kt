package galaxyspace.core.util

import galaxyspace.core.util.GSTrace
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

object GSTrace {
    @JvmStatic
    fun rayTrace(
        viewer: EntityLivingBase,
        world: World?,
        distance: Double,
        ticks: Float,
        offset: Vec3d?
    ): RayTraceResult? {
        return rayTrace(viewer, world, distance, ticks, offset, false, false)
    }

    @JvmStatic
    fun rayTrace(
        viewer: EntityLivingBase,
        world: World?,
        distance: Double,
        ticks: Float,
        offset: Vec3d?,
        checkBlockCollision: Boolean,
        onlySolid: Boolean
    ): RayTraceResult? {
        return rayTrace(viewer, world, distance, ticks, offset, checkBlockCollision, onlySolid, null)
    }

    @JvmStatic
    fun rayTrace(
        viewer: EntityLivingBase,
        world: World?,
        distance: Double,
        ticks: Float,
        offset: Vec3d?,
        checkBlockCollision: Boolean,
        onlySolid: Boolean,
        dir: Vec3d?
    ): RayTraceResult? {
        return rayTrace(
            getPosition(viewer, ticks),
            world,
            distance,
            ticks,
            offset,
            checkBlockCollision,
            onlySolid,
            dir,
            viewer
        )
    }

    @JvmStatic
    fun rayTrace(
        fromPos: Vec3d,
        world: World?,
        distance: Double,
        ticks: Float,
        offset: Vec3d?,
        checkBlockCollision: Boolean,
        onlySolid: Boolean,
        dir: Vec3d?,
        viewer: EntityLivingBase
    ): RayTraceResult? {
        var dir: Vec3d? = dir
        var objectMouseOver: RayTraceResult? = null
        var pointedEntity: Entity? = null

        if (world != null) {
            if (dir == null) {
                dir = viewer.getLook(ticks)
            }

            objectMouseOver =
                rayTraceForBlocks(fromPos, world, distance, ticks, offset, checkBlockCollision, onlySolid, (dir)!!)
            var d1: Double = distance
            var Vec3d: Vec3d = fromPos
            if (offset != null) {
                Vec3d = Vec3d.add(offset.x, offset.y, offset.z)
            }

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(Vec3d)
            }

            val Vec3d2: Vec3d = Vec3d.add((dir)!!.x * distance, (dir)!!.y * distance, (dir)!!.z * distance)
            var Vec3d3: Vec3d? = null
            val f1: Float = 1.0f
            val list: MutableList<*> = world.getEntitiesWithinAABBExcludingEntity(
                viewer,
                viewer.getEntityBoundingBox().grow((dir)!!.x * distance, (dir)!!.y * distance, (dir)!!.z * distance)
                    .expand(f1.toDouble(), f1.toDouble(), f1.toDouble())
            )
            var d2: Double = d1

            for (aList: Any? in list) {
                val entity: Entity = aList as Entity

                if (entity.canBeCollidedWith()) {
                    val f2: Float = entity.getCollisionBorderSize()
                    val axisalignedbb: AxisAlignedBB =
                        entity.getEntityBoundingBox().expand(f2.toDouble(), f2.toDouble(), f2.toDouble())
                    val movingobjectposition: RayTraceResult? = axisalignedbb.calculateIntercept(Vec3d, Vec3d2)

                    if (axisalignedbb.contains(Vec3d)) {
                        if (0.0 < d2 || d2 == 0.0) {
                            pointedEntity = entity
                            Vec3d3 = if (movingobjectposition == null) Vec3d else movingobjectposition.hitVec
                            d2 = 0.0
                        }
                    } else if (movingobjectposition != null) {
                        val d3: Double = Vec3d.distanceTo(movingobjectposition.hitVec)

                        if (d3 < d2 || d2 == 0.0) {
                            pointedEntity = entity
                            Vec3d3 = movingobjectposition.hitVec
                            d2 = d3
                        }
                    }
                }
            }

            if (pointedEntity != null && d2 < d1) {
                if (objectMouseOver != null) {
                    objectMouseOver.typeOfHit = RayTraceResult.Type.ENTITY
                    objectMouseOver.entityHit = pointedEntity
                    objectMouseOver.hitVec = Vec3d3
                } else {
                    objectMouseOver = RayTraceResult(pointedEntity, Vec3d3)
                }
            }
        }
        return objectMouseOver
    }

    @JvmStatic
    fun rayTraceForBlocks(
        viewer: EntityLivingBase,
        world: World,
        distance: Double,
        ticks: Float,
        offset: Vec3d?,
        collisionCheck: Boolean,
        onlySolid: Boolean
    ): RayTraceResult? {
        return rayTraceForBlocks(viewer, world, distance, ticks, offset, collisionCheck, onlySolid, null)
    }

    @JvmStatic
    fun rayTraceForBlocks(
        viewer: EntityLivingBase,
        world: World,
        distance: Double,
        ticks: Float,
        offset: Vec3d?,
        collisionCheck: Boolean,
        onlySolid: Boolean,
        dir: Vec3d?
    ): RayTraceResult? {
        return rayTraceForBlocks(
            getPosition(viewer, ticks),
            world,
            distance,
            ticks,
            offset,
            collisionCheck,
            onlySolid,
            if (dir == null) viewer.getLook(ticks) else dir
        )
    }

    @JvmStatic
    fun rayTraceForBlocks(
        fromPosition: Vec3d,
        world: World,
        distance: Double,
        ticks: Float,
        offset: Vec3d?,
        collisionCheck: Boolean,
        onlySolid: Boolean,
        dir: Vec3d
    ): RayTraceResult? {
        var Vec3d: Vec3d = Vec3d(fromPosition.x, fromPosition.y, fromPosition.z)
        if (offset != null) {
            Vec3d = Vec3d.add(offset.x, offset.y, offset.z)
        }
        val Vec3d2: Vec3d = Vec3d.add(dir.x * distance, dir.y * distance, dir.z * distance)
        return world.rayTraceBlocks(Vec3d, Vec3d2, collisionCheck, onlySolid, true)
    }

    @JvmStatic
    fun getPosition(entity: EntityLivingBase, p_70666_1_: Float): Vec3d {
        if (p_70666_1_ == 1.0f) {
            return Vec3d(entity.posX, entity.posY, entity.posZ)
        } else {
            val d0: Double = entity.prevPosX + (entity.posX - entity.prevPosX) * p_70666_1_.toDouble()
            val d1: Double = entity.prevPosY + (entity.posY - entity.prevPosY) * p_70666_1_.toDouble()
            val d2: Double = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * p_70666_1_.toDouble()
            return Vec3d(d0, d1, d2)
        }
    }
}
