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
 * Класс шума значений WorldEngine. Весьма примитивный шум, к слову.
 * Подробнее: https://en.wikipedia.org/wiki/Value_noise; https://habr.com/post/142592/ (<= На самом деле, в статье описывается именно этот шум).
 * @author VamigA
 */
open class WE_ValueNoise constructor(
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
        rand.makeSmartForValues()
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

        val topLeft: Double = ((rand.smartVal)!!.get(squareStartX, squareStartZ))!!
        val topRight: Double = ((rand.smartVal)!!.get(squareStartX + xs, squareStartZ))!!
        val bottomLeft: Double = ((rand.smartVal)!!.get(squareStartX, squareStartZ + zs))!!
        val bottomRight: Double = ((rand.smartVal)!!.get(squareStartX + xs, squareStartZ + zs))!!

        return WE_Interpolation.bilinearInterpolation2D(
            topLeft,
            topRight,
            bottomLeft,
            bottomRight,
            pointInQuadX,
            pointInQuadZ,
            iType
        )
    }
}
