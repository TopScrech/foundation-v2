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

/**
 * Это шаблон для шумовых классов, вроде: WE_PerlinNoise или WE_ValueNoise.
 * @author VamigA
 */
abstract class WE_NoiseTemplate(
    gSeed: Long,
    /** Persistence - амплитудный множитель для каждой октавы относительно предыдущей. Scale(X...Y...Z) - множители масштаба всей волны.  */
    @JvmField
var persistence: Double,
    /** Octaves - количество октав. Height - будет просто суммировано к конечному результату.  */
    @JvmField
var octaves: Int,
    @JvmField
var scaleX: Double,
    @JvmField
var scaleY: Double,
    @JvmField
var scaleZ: Double,
    @JvmField
var height: Int,
    /** Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).  */
    @JvmField var iType: Byte
) : WE_IReliefGenerator {
    /** Псевдослучайный генератор.  */
    @JvmField
    var rand: WE_WhiteNoise

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
        rand = WE_WhiteNoise(gSeed)
    }

    /**
     * Возвращает семя генератора.
     * @return Семя.
     */
    override fun getSeed(): Long {
        return rand.seed
    }

    /**
     * Задает семя генератора.
     * @param seed Новое семя.
     */
    override fun setSeed(seed: Long) {
        rand.seed = seed
    }

    /**
     * Задает параметры генератора на этом биоме.
     * @param pers Параметр Persistence этого рельефного слоя на этом биоме.
     * @param sclY Параметр ScaleY этого рельефного слоя на этом биоме.
     * @param hght Параметр Height этого рельефного слоя на этом биоме.
     */
    override fun setBiomeProperties(pers: Double, sclY: Double, hght: Int) {
        persistence = pers
        scaleY = sclY
        height = hght
    }

    /**
     * Выдает высоту на этом блоке (множественные вызовы этой функции и генерируют ландшафт).
     * @param x Координата блока X.
     * @param z Координата блока Z.
     * @return Высота для карты высот.
     */
    override fun genNoise2D(x: Double, z: Double): Double {
        var result: Double = 0.0
        var amplitudeMultiplier: Double = 1.0
        var nowX: Double = x
        var nowZ: Double = z
        for (i: Int in 0..<octaves) {
            result += noiseOctave2D(nowX / scaleX, nowZ / scaleZ) * amplitudeMultiplier
            amplitudeMultiplier *= persistence
            nowX *= 2.0
            nowZ *= 2.0
        }
        return height.toDouble() + result * scaleY
    }

    /**
     * Шумовая функция (главная). Генерирует шум или одну из октав шума.
     * @param x Координата блока X.
     * @param z Координата блока Z.
     * @return Высота шума (или, точнее, октавы) на этом месте.
     */
    abstract fun noiseOctave2D(x: Double, z: Double): Double
}
