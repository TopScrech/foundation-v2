package micdoodle8.mods.galacticraft.api.vector

import kotlin.math.sqrt

/**
 * Vector2 Class is used for defining objects in a 2D space.
 *
 * @author Calclavia
 */
open class Vector2 @JvmOverloads constructor(@JvmField var x: Double = 0.0, @JvmField var y: Double = 0.0) :
    Cloneable {
    /**
     * Returns the integer floor value.
     *
     * @return
     */
    open fun intX(): Int {
        return kotlin.math.floor(this.x).toInt()
    }

    open fun intY(): Int {
        return kotlin.math.floor(this.y).toInt()
    }

    /**
     * Makes a new copy of this Vector. Prevents variable referencing problems.
     */
    public override fun clone(): Vector2 {
        return Vector2(this.x, this.y)
    }

    open fun distanceTo(target: Vector2): Double {
        val xDifference: Double = this.x - target.x
        val yDifference: Double = this.y - target.y
        return sqrt(xDifference * xDifference + yDifference * yDifference)
    }

    open fun add(par1: Vector2): Vector2 {
        this.x += par1.x
        this.y += par1.y
        return this
    }

    open fun add(par1: Double): Vector2 {
        this.x += par1
        this.y += par1
        return this
    }

    open fun invert(): Vector2 {
        this.multiply(-1.0)
        return this
    }

    open fun multiply(amount: Double): Vector2 {
        this.x *= amount
        this.y *= amount
        return this
    }

    open fun round(): Vector2 {
        return Vector2(Math.round(this.x).toDouble(), Math.round(this.y).toDouble())
    }

    open fun ceil(): Vector2 {
        return Vector2(kotlin.math.ceil(this.x), kotlin.math.ceil(this.y))
    }

    open fun floor(): Vector2 {
        return Vector2(kotlin.math.floor(this.x), kotlin.math.floor(this.y))
    }

    override fun hashCode(): Int {
        return ("X:" + this.x + "Y:" + this.y).hashCode()
    }

    override fun equals(o: Any?): Boolean {
        if (o is Vector2) {
            val vector: Vector2 = o
            return this.x == vector.x && this.y == vector.y
        }

        return false
    }

    override fun toString(): String {
        return "Vector2 [" + this.x + "," + this.y + "]"
    }

    companion object {
        @JvmStatic
        fun distance(point1: Vector2, point2: Vector2): Double {
            val xDifference: Double = point1.x - point2.x
            val yDiference: Double = point1.y - point2.y
            return sqrt(xDifference * xDifference + yDiference * yDiference)
        }

        @JvmStatic
        fun slope(point1: Vector2, point2: Vector2): Double {
            val xDifference: Double = point1.x - point2.x
            val yDiference: Double = point1.y - point2.y
            return yDiference / xDifference
        }
    }
}
