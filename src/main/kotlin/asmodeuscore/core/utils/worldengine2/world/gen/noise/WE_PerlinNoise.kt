/*
 * ////////////////////////////////////-
 * //#===============================//= Version: 2.0.0.1152 or later.
 * //#=-------| WorldEngine |-------=//= By Vamig Aliev (vk.com/win_vista).
 * //#===============================//= Part of VamigA_core (vk.com/vamiga).
 * ////////////////////////////////////-
 * 
 * Copyright (C) 2020 Vamig Aliev, all rights reserved.
 * Licensed under the GNU LGPL 3 or later.
 * 
 * This file is part of WorldEngine.
 * 
 * WorldEngine  is  free  software:  you can  redistribute  it  and/or  modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the  Free  Software  Foundation,  either  version  3  of  the  License,  or
 * (at your option) any later version.
 * 
 * WorldEngine   is   distributed  in  the  hope  that  it  will   be   useful,
 * but   WITHOUT   ANY  WARRANTY;  without  even  the  implied   warranty   of
 * MERCHANTABILITY   or   FITNESS   FOR   A  PARTICULAR   PURPOSE.   See   the
 * GNU Lesser General Public License for more details.
 * 
 * You  should  have received a copy of the GNU Lesser General Public  License
 * along with WorldEngine. If not, see <https://www.gnu.org/licenses/>.
 */
package asmodeuscore.core.utils.worldengine2.world.gen.noise

import asmodeuscore.core.utils.worldengine2.util.WE_Interpolation
import kotlin.math.abs

/**
 * Класс шума Перлина WorldEngine.
 * Подробнее: https://en.wikipedia.org/wiki/Perlin_noise; https://habr.com/post/342906/; https://habr.com/post/265775/.
 * @author VamigA
 */
open class WE_PerlinNoise constructor(
    gSeed: Long,
    gPersistence: Double,
    numOfOctaves: Int,
    scl_x: Double,
    scl_y: Double,
    scl_z: Double,
    sum: Int,
    interpolation: Byte
) : WE_NoiseTemplate(gSeed, gPersistence, numOfOctaves, scl_x, scl_y, scl_z, sum, interpolation) {
    /**
     * Конструктор.
     * @param gSeed Семя.
     * @param gPersistence Стойкость (амплитудный множитель для каждой октавы относительно предыдущей).
     * @param numOfOctaves Количество октав.
     * @param scl_x Множитель масштаба по X всей волны.
     * @param scl_y Множитель масштаба по Y всей волны.
     * @param scl_z Множитель масштаба по Z всей волны.
     * @param sum Высота (будет просто суммировано к конечному результату).
     * @param interpolation Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
     */
    init {
        rand.makeSmartForVectors()
    }

    /**
     * Скалярное произведение векторов. Подробнее: https://en.wikipedia.org/wiki/Dot_product.
     * @param vec1 Вектор 1.
     * @param vec2 Вектор 2.
     */
    open fun dot2D(vec1: Array<Double>, vec2: Array<Double>): Double {
        return vec1[0]!! * vec2[0]!! + vec1[1]!! * vec2[1]!!
    }

    /**
     * Шумовая функция (главная). Генерирует шум или одну из октав шума.
     * @param x Координата блока X.
     * @param z Координата блока Z.
     * @return Высота шума (или, точнее, октавы) на этом месте.
     */
    override fun noiseOctave2D(x: Double, z: Double): Double {
        val squareStartX: Long = x.toLong()
        val squareStartZ: Long = z.toLong()
        var xs: Long = 1L
        var zs: Long = 1L
        if (abs(x) != x) xs = -1L
        if (abs(z) != z) zs = -1L
        val pointInQuadX: Double = abs(x) - abs(squareStartX).toDouble()
        val pointInQuadZ: Double = abs(z) - abs(squareStartZ).toDouble()

        val topLeft: Array<Double> = ((rand.smartVec)!!.get(squareStartX, squareStartZ))!!
        val topRight: Array<Double> = ((rand.smartVec)!!.get(squareStartX + xs, squareStartZ))!!
        val bottomLeft: Array<Double> = ((rand.smartVec)!!.get(squareStartX, squareStartZ + zs))!!
        val bottomRight: Array<Double> = ((rand.smartVec)!!.get(squareStartX + xs, squareStartZ + zs))!!

        val distanceToTopLeft: Array<Double> =
            arrayOf<Double>(xs.toDouble() * pointInQuadX, zs.toDouble() * pointInQuadZ)
        val distanceToTopRight: Array<Double> =
            arrayOf<Double>(xs.toDouble() * pointInQuadX - xs.toDouble(), zs.toDouble() * pointInQuadZ)
        val distanceToBottomLeft: Array<Double> =
            arrayOf<Double>(xs.toDouble() * pointInQuadX, zs.toDouble() * pointInQuadZ - zs.toDouble())
        val distanceToBottomRight: Array<Double> =
            arrayOf<Double>(xs.toDouble() * pointInQuadX - xs.toDouble(), zs.toDouble() * pointInQuadZ - zs.toDouble())

        val dotTopLeft: Double = dot2D(topLeft, distanceToTopLeft)
        val dotTopRight: Double = dot2D(topRight, distanceToTopRight)
        val dotBottomLeft: Double = dot2D(bottomLeft, distanceToBottomLeft)
        val dotBottomRight: Double = dot2D(bottomRight, distanceToBottomRight)

        return WE_Interpolation.bilinearInterpolation2D(
            dotTopLeft,
            dotTopRight,
            dotBottomLeft,
            dotBottomRight,
            pointInQuadX,
            pointInQuadZ,
            iType
        )
    }
}
