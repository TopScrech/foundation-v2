package micdoodle8.mods.galacticraft.api.vector

import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3 as Vec3d

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.joml.Vector3f
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Vector3 Class is used for defining objects in a 3D space.
 *
 * @author Calclavia
 */
open class Vector3 constructor(
    @JvmField var x: Double,
    @JvmField var y: Double,
    @JvmField var z: Double
) : Cloneable {
    constructor() : this(0.0, 0.0, 0.0)

    constructor(vector: Vector3) : this(vector.x, vector.y, vector.z)

    constructor(amount: Double) : this(amount, amount, amount)

    constructor(par1: Entity) : this(par1.x, par1.y, par1.z)

    constructor(pos: BlockPos) : this(pos.getX().toDouble(), pos.getY().toDouble(), pos.getZ().toDouble())

    constructor(par1: TileEntity) : this(par1.blockPos)

    constructor(par1: Vec3d) : this(par1.x, par1.y, par1.z)

    constructor(hit: HitResult) : this(if (hit is BlockHitResult) Vec3d.atLowerCornerOf(hit.blockPos) else hit.location)

    constructor(direction: EnumFacing) : this(
        (direction.getStepX()).toDouble(),
        (direction.getStepY()).toDouble(),
        (direction.getStepZ()).toDouble()
    )

    /**
     * Loads a Vector3 from an NBT compound.
     */
    constructor(nbt: NBTTagCompound) : this(nbt.getDoubleOr("x", 0.0), nbt.getDoubleOr("y", 0.0), nbt.getDoubleOr("z", 0.0))

    constructor(vec: BlockVec3) : this(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())

    /**
     * Returns the coordinates as integers, ideal for block placement.
     */
    open fun intX(): Int {
        return kotlin.math.floor(this.x).toInt()
    }

    open fun intY(): Int {
        return kotlin.math.floor(this.y).toInt()
    }

    open fun intZ(): Int {
        return kotlin.math.floor(this.z).toInt()
    }

    open fun floatX(): Float {
        return this.x.toFloat()
    }

    open fun floatY(): Float {
        return this.y.toFloat()
    }

    open fun floatZ(): Float {
        return this.z.toFloat()
    }

    /**
     * Makes a new copy of this Vector. Prevents variable referencing problems.
     */
    public override fun clone(): Vector3 {
        return Vector3(this)
    }

    /**
     * Easy block access functions.
     *
     * @param world
     * @return
     */
    open fun getBlock(world: IBlockAccess): Block {
        return world.getBlockState(BlockPos(this.intX(), this.intY(), this.intZ())).getBlock()
    }

    open fun getBlockMetadata(world: IBlockAccess): IBlockState {
        return world.getBlockState(BlockPos(this.intX(), this.intY(), this.intZ()))
    }

    open fun getTileEntity(world: IBlockAccess): TileEntity? {
        return world.getBlockEntity(BlockPos(this.intX(), this.intY(), this.intZ()))
    }

    open fun setBlock(world: World, state: IBlockState, notify: Int): Boolean {
        return world.setBlock(BlockPos(this.intX(), this.intY(), this.intZ()), state, notify)
    }

    open fun setBlock(world: World, state: IBlockState): Boolean {
        return this.setBlock(world, state, 3)
    }

    /**
     * ---------------------- CONVERSION FUNCTIONS ----------------------------
     */
    /**
     * Converts this Vector3 into a Vector2 by dropping the Y axis.
     */
    open fun toVector2(): Vector2 {
        return Vector2(this.x, this.z)
    }

    /**
     * Converts this vector three into a Minecraft Vec3d object
     */
    open fun toVec3(): Vec3d {
        return Vec3d(this.x, this.y, this.z)
    }

    /**
     * Converts Vector3 into a EnumFacing.
     */
    open fun toEnumFacing(): EnumFacing? {
        for (direction: EnumFacing in EnumFacing.values()) {
            if (this.x == (direction.getStepX()).toDouble() && this.y == (direction.getStepY()).toDouble() && this.z == (direction.getStepZ()).toDouble()) {
                return direction
            }
        }

        return null
    }

    open fun getMagnitude(): Double {
        return sqrt(this.getMagnitudeSquared())
    }

    open fun getMagnitudeSquared(): Double {
        return this.x * this.x + this.y * this.y + this.z * this.z
    }

    open fun normalize(): Vector3 {
        val d: Double = this.getMagnitude()

        if (d != 0.0) {
            this.scale(1 / d)
        }

        return this
    }

    @Deprecated("")
    open fun distanceTo(vector3: Vector3): Double {
        return this.distance(vector3)
    }

    open fun distance(compare: Vector3): Double {
        val difference: Vector3 = this.clone().difference(compare)
        return difference.getMagnitude()
    }

    /**
     * Multiplies the vector by negative one.
     */
    open fun invert(): Vector3 {
        this.scale(-1.0)
        return this
    }

    open fun translate(par1: Vector3): Vector3 {
        this.x += par1.x
        this.y += par1.y
        this.z += par1.z
        return this
    }

    open fun translate(par1: Double): Vector3 {
        this.x += par1
        this.y += par1
        this.z += par1
        return this
    }

    @Deprecated("")
    open fun add(amount: Vector3): Vector3 {
        return this.translate(amount)
    }

    @Deprecated("")
    open fun add(amount: Double): Vector3 {
        return this.translate(amount)
    }

    @Deprecated("")
    open fun subtract(amount: Vector3): Vector3 {
        return this.translate(amount.clone().invert())
    }

    @Deprecated("")
    open fun subtract(amount: Double): Vector3 {
        return this.translate(-amount)
    }

    open fun difference(amount: Vector3): Vector3 {
        return this.translate(amount.clone().invert())
    }

    open fun difference(amount: Double): Vector3 {
        return this.translate(-amount)
    }

    open fun scale(amount: Double): Vector3 {
        this.x *= amount
        this.y *= amount
        this.z *= amount
        return this
    }

    open fun scale(amount: Vector3): Vector3 {
        this.x *= amount.x
        this.y *= amount.y
        this.z *= amount.z
        return this
    }

    @Deprecated("")
    open fun multiply(amount: Double): Vector3 {
        return this.scale(amount)
    }

    @Deprecated("")
    open fun multiply(amount: Vector3): Vector3 {
        return this.scale(amount)
    }

    open fun round(): Vector3 {
        return Vector3(Math.round(this.x).toDouble(), Math.round(this.y).toDouble(), Math.round(this.z).toDouble())
    }

    open fun ceil(): Vector3 {
        return Vector3(kotlin.math.ceil(this.x), kotlin.math.ceil(this.y), kotlin.math.ceil(this.z))
    }

    open fun floor(): Vector3 {
        return Vector3(kotlin.math.floor(this.x), kotlin.math.floor(this.y), kotlin.math.floor(this.z))
    }

    open fun toRound(): Vector3 {
        this.x = Math.round(this.x).toDouble()
        this.y = Math.round(this.y).toDouble()
        this.z = Math.round(this.z).toDouble()
        return this
    }

    open fun toCeil(): Vector3 {
        this.x = kotlin.math.ceil(this.x)
        this.y = kotlin.math.ceil(this.y)
        this.z = kotlin.math.ceil(this.z)
        return this
    }

    open fun toFloor(): Vector3 {
        this.x = kotlin.math.floor(this.x)
        this.y = kotlin.math.floor(this.y)
        this.z = kotlin.math.floor(this.z)
        return this
    }

    /**
     * Gets all entities inside of this position in block space.
     */
    open fun getEntitiesWithin(world: World, entityClass: Class<out Entity>): MutableList<Entity?> {
        val box = AxisAlignedBB(intX().toDouble(), intY().toDouble(), intZ().toDouble(),
            (intX() + 1).toDouble(), (intY() + 1).toDouble(), (intZ() + 1).toDouble())
        return world.getEntitiesOfClass(entityClass, box).mapTo(ArrayList()) { it }
    }

    /**
     * Gets a position relative to a position's side
     *
     * @return The position relative to the original position's side
     */
    open fun modifyPositionFromSide(side: EnumFacing, amount: Double): Vector3 {
        return this.translate(Vector3(side).scale(amount))
    }

    open fun modifyPositionFromSide(side: EnumFacing): Vector3 {
        this.modifyPositionFromSide(side, 1.0)
        return this
    }

    /**
     * Cross product functions
     *
     * @return The cross product between this vector and another.
     */
    open fun toCrossProduct(compare: Vector3): Vector3 {
        val newX: Double = this.y * compare.z - this.z * compare.y
        val newY: Double = this.z * compare.x - this.x * compare.z
        val newZ: Double = this.x * compare.y - this.y * compare.x
        this.x = newX
        this.y = newY
        this.z = newZ
        return this
    }

    open fun crossProduct(compare: Vector3): Vector3 {
        return this.clone().toCrossProduct(compare)
    }

    open fun xCrossProduct(): Vector3 {
        return Vector3(0.0, this.z, -this.y)
    }

    open fun zCrossProduct(): Vector3 {
        return Vector3(-this.y, this.x, 0.0)
    }

    open fun dotProduct(vec2: Vector3): Double {
        return this.x * vec2.x + this.y * vec2.y + this.z * vec2.z
    }

    /**
     * @return The perpendicular vector.
     */
    open fun getPerpendicular(): Vector3 {
        if (this.z == 0.0) {
            return this.zCrossProduct()
        }

        return this.xCrossProduct()
    }

    /**
     * @return True if this Vector3 is zero.
     */
    open fun isZero(): Boolean {
        return this.x == 0.0 && this.y == 0.0 && this.z == 0.0
    }

    /**
     * Rotate by a this vector around an axis.
     *
     * @return The new Vector3 rotation.
     */
    open fun rotate(angle: Float, axis: Vector3): Vector3 {
        return translateMatrix(getRotationMatrix(angle, axis), this.clone())
    }

    open fun getRotationMatrix(angle: Float): DoubleArray {
        var angle: Float = angle
        val matrix: DoubleArray = DoubleArray(16)
        val axis: Vector3 = this.clone().normalize()
        val x: Double = axis.x
        val y: Double = axis.y
        val z: Double = axis.z
        angle /= (180f / 3.1415927f)
        val cos: Float = cos(angle)
        val ocos: Float = 1.0f - cos
        val sin: Float = sin(angle)
        matrix[0] = x * x * ocos + cos
        matrix[1] = y * x * ocos + z * sin
        matrix[2] = x * z * ocos - y * sin
        matrix[4] = x * y * ocos - z * sin
        matrix[5] = y * y * ocos + cos
        matrix[6] = y * z * ocos + x * sin
        matrix[8] = x * z * ocos + y * sin
        matrix[9] = y * z * ocos - x * sin
        matrix[10] = z * z * ocos + cos
        matrix[15] = 1.0
        return matrix
    }

    /**
     * Rotates this Vector by a yaw, pitch and roll value.
     */
    /**
     * Rotates a point by a yaw and pitch around the anchor 0,0 by a specific
     * angle.
     */
    @JvmOverloads
    open fun rotate(yaw: Double, pitch: Double, roll: Double = 0.0) {
        val yawRadians: Double = Math.toRadians(yaw)
        val pitchRadians: Double = Math.toRadians(pitch)
        val rollRadians: Double = Math.toRadians(roll)

        val x: Double = this.x
        val y: Double = this.y
        val z: Double = this.z

        this.x =
            x * cos(yawRadians) * cos(pitchRadians) + z * (cos(yawRadians) * sin(pitchRadians) * sin(rollRadians) - sin(
                yawRadians
            ) * cos(rollRadians)) + y * (cos(yawRadians) * sin(pitchRadians) * cos(rollRadians) + sin(yawRadians) * sin(
                rollRadians
            ))
        this.z =
            x * sin(yawRadians) * cos(pitchRadians) + z * (sin(yawRadians) * sin(pitchRadians) * sin(rollRadians) + cos(
                yawRadians
            ) * cos(rollRadians)) + y * (sin(yawRadians) * sin(pitchRadians) * cos(rollRadians) - cos(yawRadians) * sin(
                rollRadians
            ))
        this.y =
            -x * sin(pitchRadians) + z * cos(pitchRadians) * sin(rollRadians) + y * cos(pitchRadians) * cos(rollRadians)
    }

    open fun rotate(yaw: Double) {
        val yawRadians: Double = Math.toRadians(yaw)

        val x: Double = this.x
        val z: Double = this.z

        if (yaw != 0.0) {
            this.x = x * cos(yawRadians) - z * sin(yawRadians)
            this.z = x * sin(yawRadians) + z * cos(yawRadians)
        }
    }

    /**
     * Gets the angle between this vector and another vector.
     *
     * @return Angle in radians
     */
    open fun getAngle(vec2: Vector3): Double {
        return anglePreNorm(this.clone().normalize(), vec2.clone().normalize())
    }

    open fun anglePreNorm(vec2: Vector3): Double {
        return acos(this.dotProduct(vec2).coerceIn(-1.0, 1.0))
    }

    /**
     * Saves this Vector3 to disk
     */
    open fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        nbt.putDouble("x", this.x)
        nbt.putDouble("y", this.y)
        nbt.putDouble("z", this.z)
        return nbt
    }

    @Deprecated("")
    open fun rayTraceEntities(
        world: World,
        rotationYaw: Float,
        rotationPitch: Float,
        collisionFlag: Boolean,
        reachDistance: Double
    ): EntityHitResult? {
        return this.rayTraceEntities(world, rotationYaw, rotationPitch, reachDistance)
    }

    open fun rayTraceEntities(
        world: World,
        rotationYaw: Float,
        rotationPitch: Float,
        reachDistance: Double
    ): EntityHitResult? {
        return this.rayTraceEntities(
            world,
            getDeltaPositionFromRotation(rotationYaw, rotationPitch).scale(reachDistance)
        )
    }

    /**
     * Does an entity raytrace.
     *
     * @param world  - The world object.
     * @param target - The rotation in terms of Vector3. Convert using
     * getDeltaPositionFromRotation()
     * @return The target hit.
     */
    open fun rayTraceEntities(world: World, target: Vector3): EntityHitResult? {
        val start = toVec3()
        val end = start.add(target.toVec3())
        if (!end.x.isFinite() || !end.y.isFinite() || !end.z.isFinite() || start == end) return null
        val bounds = AxisAlignedBB(start, end).inflate(1.1)
        var nearestDistance = start.distanceToSqr(end)
        var nearest: EntityHitResult? = null
        for (entity in world.getEntities(null, bounds) { it.isPickable && !it.isSpectator }) {
            val box = entity.boundingBox.inflate(entity.pickRadius.toDouble())
            val point = if (box.contains(start)) start else box.clip(start, end).orElse(null) ?: continue
            val distance = start.distanceToSqr(point)
            if (distance <= nearestDistance) {
                nearest = EntityHitResult(entity, point)
                nearestDistance = distance
            }
        }
        return nearest
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(this.x).append(this.y).append(this.z).hashCode()
    }

    override fun equals(o: Any?): Boolean {
        if (o is Vector3) {
            val vector3: Vector3 = o
            return EqualsBuilder().append(this.x, vector3.x).append(this.y, vector3.y).append(this.z, vector3.z)
                .isEquals()
        }

        return false
    }

    override fun toString(): String {
        return "Vector3 [" + this.x + "," + this.y + "," + this.z + "]"
    }

    open fun toVector3f(): Vector3f {
        return Vector3f(this.x.toFloat(), this.y.toFloat(), this.z.toFloat())
    }

    companion object {
        /**
         * Gets the distance between two vectors
         *
         * @return The distance
         */
        @JvmStatic
        fun distance(vec1: Vector3, vec2: Vector3): Double {
            return vec1.distance(vec2)
        }

        @JvmStatic
        fun translate(translate: Vector3, par1: Vector3): Vector3 {
            translate.x += par1.x
            translate.y += par1.y
            translate.z += par1.z
            return translate
        }

        @JvmStatic
        fun translate(translate: Vector3, par1: Double): Vector3 {
            translate.x += par1
            translate.y += par1
            translate.z += par1
            return translate
        }

        @JvmStatic
        fun scale(vec: Vector3, amount: Double): Vector3 {
            return vec.scale(amount)
        }

        @JvmStatic
        fun scale(vec: Vector3, amount: Vector3): Vector3 {
            return vec.scale(amount)
        }

        /**
         * Static versions of a lot of functions
         */
        @JvmStatic
        fun subtract(par1: Vector3, par2: Vector3): Vector3 {
            return Vector3(par1.x - par2.x, par1.y - par2.y, par1.z - par2.z)
        }

        @JvmStatic
        @Deprecated("")
        fun add(par1: Vector3, par2: Vector3): Vector3 {
            return Vector3(par1.x + par2.x, par1.y + par2.y, par1.z + par2.z)
        }

        @JvmStatic
        @Deprecated("")
        fun add(par1: Vector3, par2: Double): Vector3 {
            return Vector3(par1.x + par2, par1.y + par2, par1.z + par2)
        }

        @JvmStatic
        @Deprecated("")
        fun multiply(vec1: Vector3, vec2: Vector3): Vector3 {
            return Vector3(vec1.x * vec2.x, vec1.y * vec2.y, vec1.z * vec2.z)
        }

        @JvmStatic
        @Deprecated("")
        fun multiply(vec1: Vector3, vec2: Double): Vector3 {
            return Vector3(vec1.x * vec2, vec1.y * vec2, vec1.z * vec2)
        }

        @JvmStatic
        fun translateMatrix(matrix: DoubleArray, translation: Vector3): Vector3 {
            val x: Double =
                translation.x * matrix[0] + translation.y * matrix[1] + translation.z * matrix[2] + matrix[3]
            val y: Double =
                translation.x * matrix[4] + translation.y * matrix[5] + translation.z * matrix[6] + matrix[7]
            val z: Double =
                translation.x * matrix[8] + translation.y * matrix[9] + translation.z * matrix[10] + matrix[11]
            translation.x = x
            translation.y = y
            translation.z = z
            return translation
        }

        @JvmStatic
        fun getRotationMatrix(angle: Float, axis: Vector3): DoubleArray {
            return axis.getRotationMatrix(angle)
        }

        /**
         * Gets the delta look position based on the rotation yaw and pitch.
         * Minecraft coordinates are messed up. Y and Z are flipped. Yaw is
         * displaced by 90 degrees. Pitch is inversed.
         *
         * @param rotationYaw
         * @param rotationPitch
         */
        @JvmStatic
        fun getDeltaPositionFromRotation(rotationYaw: Float, rotationPitch: Float): Vector3 {
            val yaw = Math.toRadians(rotationYaw.toDouble())
            val pitch = Math.toRadians(rotationPitch.toDouble())
            val horizontal = cos(pitch)
            return Vector3(-sin(yaw) * horizontal, -sin(pitch), cos(yaw) * horizontal)
        }

        @JvmStatic
        fun getAngle(vec1: Vector3, vec2: Vector3): Double {
            return vec1.getAngle(vec2)
        }

        @JvmStatic
        fun anglePreNorm(vec1: Vector3, vec2: Vector3): Double {
            return acos(vec1.clone().dotProduct(vec2).coerceIn(-1.0, 1.0))
        }

        /**
         * Loads a Vector3 from an NBT compound.
         */
        @JvmStatic
        @Deprecated("")
        fun readFromNBT(nbt: NBTTagCompound): Vector3 {
            return Vector3(nbt)
        }

        @JvmStatic
        fun UP(): Vector3 {
            return Vector3(0.0, 1.0, 0.0)
        }

        @JvmStatic
        fun DOWN(): Vector3 {
            return Vector3(0.0, -1.0, 0.0)
        }

        @JvmStatic
        fun NORTH(): Vector3 {
            return Vector3(0.0, 0.0, -1.0)
        }

        @JvmStatic
        fun SOUTH(): Vector3 {
            return Vector3(0.0, 0.0, 1.0)
        }

        @JvmStatic
        fun WEST(): Vector3 {
            return Vector3(-1.0, 0.0, 0.0)
        }

        @JvmStatic
        fun EAST(): Vector3 {
            return Vector3(1.0, 0.0, 0.0)
        }
    }
}
