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

import foundation.math.LegacyTerrainMath

import asmodeuscore.core.utils.worldengine2.util.ISmartMathFunction
import asmodeuscore.core.utils.worldengine2.util.WE_RegionBuffer
import kotlin.math.pow

/**
 * Отвечает за псевдослучайную генерацию белого шума. Из этих данных и исходят генераторы WorldEngine при работе.
 * Возвращает дробные числа в диапазоне от -1.0 до 1.0 на основании лишь координат X и Z, а также "семени".
 * При одинаковых входных данных выходные данные всегда останутся теми же - это очень важно.
 * @author VamigA
 */
open class WE_WhiteNoise constructor(genSeed: Long) {
    /** Семя - число, на основании которого и ведутся расчеты.  */
    @JvmField
    var seed: Long

    /** Буфер значений от функции gen2D(x, z). Временно их хранит, так мы экономим такты процессора на расчетах.  */
    @JvmField
    var smartVal: WE_RegionBuffer<Double>? = null

    /** Буфер векторов от функции vecGen2D(x, z). Временно их хранит, так мы экономим такты процессора на расчетах.  */
    @JvmField
    var smartVec: WE_RegionBuffer<Array<Double>?>? = null

    /**
     * Конструктор.
     * @param genSeed Семя.
     */
    init {
        seed = genSeed.toDouble().pow(11.0).toLong() * 171L + 51484313L
    }

    /**
     * Главная функция - рассчитывает и выводит неизменное псевдослучайное число на основании входных данных.
     * @param x Координата X в мире, типа Long.
     * @param z Координата Z в мире, типа Long.
     * @return Псевдослучайное число типа Double в диапазоне от -1.0 до 1.0.
     */
    open fun gen2D(x: Long, z: Long): Double {
        var n: Long = seed + x * 4L + z * 341L
        n = (n shl 13L.toInt()) xor n
        return 1.0 - ((n * (n * n * 15731L + 789221L) + 1376312589L) and 2147483647L).toDouble() / 1073741824.0
    }

    /**
     * Преобразует результат предыдущей функции в угол; выдает координаты единичного вектора, наклоненного под этим углом.
     * @param x Координата X в мире, типа Long.
     * @param z Координата Z в мире, типа Long.
     * @return Координаты X и Z (определяют лишь его наклон) вектора длиной 1 в массиве типа Double[2].
     */
    open fun vecGen2D(x: Long, z: Long): Array<Double> {
        val angle: Double = gen2D(x, z) * Math.PI
        return arrayOf<Double>(LegacyTerrainMath.cos(angle.toFloat()).toDouble(), LegacyTerrainMath.sin(angle.toFloat()).toDouble())
    }

    /** Добавляет буфер для временного хранения значений.  */
    open fun makeSmartForValues() {
        smartVal = WE_RegionBuffer<Double>(1024, ISmartMathFunction { x: Long, z: Long -> this.gen2D(x, z) })
    }

    /** Добавляет буфер для временного хранения векторов.  */
    open fun makeSmartForVectors() {
        smartVec =
            WE_RegionBuffer<Array<Double>?>(1024, ISmartMathFunction { x: Long, z: Long -> this.vecGen2D(x, z) })
    }
}
