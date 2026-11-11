package micdoodle8.mods.galacticraft.core.util

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint

import kotlin.math.pow
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.core.BlockPos
import net.minecraftforge.fml.common.network.NetworkRegistry
import kotlin.math.max
import kotlin.math.min

object ColorUtil {
    //Credit to Mark Ransom @ StackOverflow for the colorwheel idea
    @JvmField
    var red: Vector3 = Vector3(255.0, 0.0, 0.0)

    @JvmField
    var orange: Vector3 = Vector3(255.0, 160.0, 0.0)

    @JvmField
    var yellow: Vector3 = Vector3(255.0, 255.0, 0.0)

    @JvmField
    var green: Vector3 = Vector3(0.0, 255.0, 0.0)

    @JvmField
    var cyan: Vector3 = Vector3(0.0, 255.0, 255.0)

    @JvmField
    var blue: Vector3 = Vector3(0.0, 0.0, 255.0)

    @JvmField
    var magenta: Vector3 = Vector3(255.0, 0.0, 255.0)

    @JvmField
    var white: Vector3 = Vector3(255.0, 255.0, 255.0)

    @JvmField
    var black: Vector3 = Vector3(0.0, 0.0, 0.0)

    @JvmField
    var mud: Vector3 = Vector3(94.0, 81.0, 74.0)

    @JvmField
    var colorwheelAngles: DoubleArray =
        doubleArrayOf(-110.0, -30.0, 0.0, 60.0, 120.0, 180.0, 215.0, 250.0, 330.0, 360.0, 420.0, 480.0)

    @JvmField
    var colorwheelColors: Array<Vector3> =
        arrayOf<Vector3>(blue, magenta, red, orange, yellow, green, cyan, blue, magenta, red, orange, yellow)

    @JvmStatic
    private fun hue_to_rgb(deg: Double): Vector3? {
        var deg: Double = deg
        deg = deg % 360

        var previous_angle: Double = colorwheelAngles[1]

        for (i: Int in 2..<colorwheelAngles.size - 2) {
            val angle: Double = colorwheelAngles[i]
            if (deg <= angle) {
                return interpolateInArray(colorwheelColors, i, (angle - deg) / (angle - previous_angle))
            }
            previous_angle = angle
        }

        return null
    }

    @JvmStatic
    private fun rgb_to_hue(input: Vector3): Double {
        val maxCol: Double = max(max(input.x, input.y), input.z)
        if (maxCol <= 0) {
            return 0.0
        }
        val rgb: Vector3? = input.scale(255.0 / maxCol)

        var mindist: Double = 1024.0
        var mini: Int = 0

        for (i: Int in 2..<colorwheelAngles.size - 2) {
            val color: Vector3 = colorwheelColors[i]
            val separation: Double = color.distance((rgb)!!)
            if (separation < mindist) {
                mindist = separation
                mini = i
            }
        }

        val separation1: Double
        val separation2: Double
        separation1 = colorwheelColors[mini - 1].distance((rgb)!!)
        separation2 = colorwheelColors[mini + 1].distance((rgb)!!)

        var hue: Double
        if (separation1 < separation2) {
            val separationtot: Double = colorwheelColors[mini - 1].distance(colorwheelColors[mini])
            hue = interpolateInArray(colorwheelAngles, mini, mindist / separationtot)
            if (hue < 0) {
                hue += 360.0
            }
        } else {
            val separationtot: Double = colorwheelColors[mini + 1].distance(colorwheelColors[mini])
            hue = interpolateInArray(colorwheelAngles, mini + 1, separation2 / separationtot)
            if (hue > 360.0) {
                hue -= 360.0
            }
        }

        return hue
    }

    @JvmStatic
    private fun cubicInterpolate(y0: Double, y1: Double, y2: Double, y3: Double, mu: Double): Double {
        val a0: Double
        val a1: Double
        val a2: Double
        val a3: Double
        val mu2: Double

        mu2 = mu * mu
        a3 = y3 - y2 - y0 + y1
        a2 = y0 - y1 - a3
        a1 = y2 - y0
        a0 = y1

        return (a3 * mu * mu2 + a2 * mu2 + a1 * mu + a0)
    }

    @JvmStatic
    private fun interpolateInArray(array: Array<Vector3>, i: Int, mu: Double): Vector3 {
        val point0: Vector3 = array[i + 1]
        val point1: Vector3 = array[i]
        val point2: Vector3 = array[i - 1]
        val point3: Vector3 = array[i - 2]

        val x: Double = cubicInterpolate(point0.x, point1.x, point2.x, point3.x, mu)
        val y: Double = cubicInterpolate(point0.y, point1.y, point2.y, point3.y, mu)
        val z: Double = cubicInterpolate(point0.z, point1.z, point2.z, point3.z, mu)

        return Vector3(x, y, z)
    }

    @JvmStatic
    private fun interpolateInArray(array: DoubleArray, i: Int, mu: Double): Double {
        return cubicInterpolate(array[i + 1], array[i], array[i - 1], array[i - 2], mu)
    }

    @JvmStatic
    fun addColorsRealistically(color1: Vector3, color2: Vector3): Vector3 {
        var hue1: Double = rgb_to_hue(color1)
        var hue2: Double = rgb_to_hue(color2)
        if (hue1 - hue2 > 180.0) {
            hue2 += 360.0
        }
        if (hue2 - hue1 > 180.0) {
            hue1 += 360.0
        }
        var hueresult: Double = (hue1 + hue2) / 2
        if (hueresult > 360.0) {
            hueresult -= 360.0
        }

        //TODO: if hue1 and hue2 differ by close to 180degrees, add in some 'mud' color
        //TODO: add greyscale here
        return hue_to_rgb(hueresult)!!.scale(1 / 255.0)
    }

    @JvmStatic
    fun addColorsRealistically(color1: Int, color2: Int): Int {
        val result: Vector3 = addColorsRealistically(toVec3(color1), toVec3(color2))
        return fromVec3(result)
    }

    @JvmStatic
    fun addColorsBasic(color1: Int, color2: Int): Int {
        var g: Int = color1 shr 8
        var r: Int = g shr 8
        g = g and 255
        var b: Int = color1 and 255
        var gg: Int = color2 shr 8
        val rr: Int = gg shr 8
        gg = gg and 255
        val bb: Int = color2 and 255

        r = (r + rr) / 2
        g = (g + gg) / 2
        b = (b + bb) / 2

        r = r shl 16
        g = g shl 8
        return r or g or b
    }


    @JvmStatic
    fun toVec3(col: Int): Vector3 {
        var gg: Int = col shr 8
        val rr: Int = gg shr 8
        gg = gg and 255
        val bb: Int = col and 255
        return Vector3((rr / 255f).toDouble(), (gg / 255f).toDouble(), (bb / 255f).toDouble())
    }

    @JvmStatic
    fun fromVec3(result: Vector3): Int {
        var r: Int = (result.x * 255.0 + 0.499).toInt()
        var g: Int = (result.y * 255.0 + 0.499).toInt()
        val b: Int = (result.z * 255.0 + 0.499).toInt()
        r = r shl 16
        g = g shl 8
        return r or g or b
    }

    @JvmStatic
    fun to32BitColor(a: Int, r: Int, g: Int, b: Int): Int {
        var a: Int = a
        var r: Int = r
        var g: Int = g
        a = a shl 24
        r = r shl 16
        g = g shl 8

        return a or r or g or b
    }

    @JvmStatic
    fun to32BitColorB(r: Byte, g: Byte, b: Byte): Int {
        val rr: Int = (r.toInt() and 255) shl 16
        val gg: Int = (g.toInt() and 255) shl 8

        return rr or gg or (b.toInt() and 255)
    }

    @JvmStatic
    fun lighten(col: Int, factor: Float): Int {
        var gg: Int = col shr 8
        var rr: Int = gg shr 8
        gg = gg and 255
        var bb: Int = col and 255
        rr = (rr * (1f + factor)).toInt()
        gg = (gg * (1f + factor)).toInt()
        bb = (bb * (1f + factor)).toInt()
        if (rr > 255) {
            rr = 255
        }
        if (gg > 255) {
            gg = 255
        }
        if (bb > 255) {
            bb = 255
        }
        return rr shl 16 or (gg shl 8) or bb
    }

    /**
     * Lighten to the specified intensity
     * @param col
     * @return
     */
    @JvmStatic
    fun lightenFully(col: Int, intensity: Int): Int {
        if (intensity == 0) return 0
        var gg: Int = col shr 8
        var rr: Int = gg shr 8
        gg = gg and 255
        var bb: Int = col and 255
        rr = 255 - rr
        gg = 255 - gg
        bb = 255 - bb
        val greyInvert: Double = min(rr, min(gg, bb)).toDouble()
        var delta: Double = max(rr, max(gg, bb)).toDouble()
        delta = (delta - greyInvert) / delta
        if (greyInvert >= intensity) {
            return col
        }
        val factor: Double = (intensity - greyInvert) / intensity / delta.pow(0.6)
        rr -= 24 //this -24 and math.pow(delta 0.6) found empirically, there's no science here!
        gg -= 24
        bb -= 24
        rr = (rr * factor).toInt()
        gg = (gg * factor).toInt()
        bb = (bb * factor).toInt()
        if (rr > 255) {
            rr = 255
        }
        if (gg > 255) {
            gg = 255
        }
        if (bb > 255) {
            bb = 255
        }
        rr = 255 - rr
        gg = 255 - gg
        bb = 255 - bb
        if (rr > 255) {
            rr = 255
        }
        if (gg > 255) {
            gg = 255
        }
        if (bb > 255) {
            bb = 255
        }
        return rr shl 16 or (gg shl 8) or bb
    }

    @JvmStatic
    fun toGreyscale(col: Int): Int {
        val gg: Int = col shr 8
        var grey: Int = gg shr 8
        grey += gg and 255
        grey += col and 255
        grey /= 3
        return grey shl 16 or (grey shl 8) or (grey and 255)
    }

    @JvmStatic
    fun sendUpdatedColorsToPlayer(stats: GCPlayerStats) {
        val dimID: Int = (stats.getPlayer())!!.get()!!.dimension
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_RECOLOR_ALL_GLASS,
                dimID,
                arrayOf<Any?>(stats.getGlassColor1(), stats.getGlassColor2(), stats.getGlassColor3())
            ), (stats.getPlayer())!!.get()
        )
    }

    @JvmStatic
    fun updateColorsForArea(dimID: Int, pos: BlockPos, range: Int, color1: Int, color2: Int, color3: Int) {
        (GalacticraftCore.packetPipeline)!!.sendToAllAround(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_RECOLOR_ALL_GLASS,
                dimID,
                arrayOf<Any>(color1, color2, color3)
            ),
            NetworkRegistry.TargetPoint(
                dimID,
                pos.getX().toDouble(),
                pos.getY().toDouble(),
                pos.getZ().toDouble(),
                range.toDouble()
            )
        )
    }

    @JvmStatic
    fun setGLColor(col: Int) {
        var gg: Int = col shr 8
        val rr: Int = gg shr 8
        gg = gg and 255
        val bb: Int = col and 255
        GlStateManager.color(rr / 255f, gg / 255f, bb / 255f, 1.0f)
    }
}
