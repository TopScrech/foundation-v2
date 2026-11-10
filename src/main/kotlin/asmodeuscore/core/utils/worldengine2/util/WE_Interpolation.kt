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
package asmodeuscore.core.utils.worldengine2.util

/**
 * Класс интерполяции. Содержит математические функции для выполнения интерполяции.
 * @author VamigA
 */
object WE_Interpolation {
    /** Константы для параметров.  */
    const val I_VALUEFUNC_NONE: Byte = 0

    const val I_VALUEFUNC_SMOOTHSTEP: Byte = 1

    const val I_VALUEFUNC_SMOOTHERSTEP: Byte = 2

    /**
     * Функция Smoothstep. Прямую на графике преобразует в красивую кривую, уплощенную на концах (0.0 и 1.0).
     * Подробнее: https://en.wikipedia.org/wiki/Smoothstep.
     * @param n Некоторое значение с 0.0 до 1.0.
     * @return Результат вычислений.
     */
    @JvmStatic
    fun smoothstep(n: Double): Double {
        return n * n * (3.0 - 2.0 * n)
    }

    /**
     * Функция Smootherstep. Прямую на графике преобразует в красивую кривую, уплощенную на концах (0.0 и 1.0). Лучше, чем Smoothstep.
     * Подробнее: https://en.wikipedia.org/wiki/Smoothstep (здесь написано и про Smootherstep).
     * @param n Некоторое значение с 0.0 до 1.0.
     * @return Результат вычислений.
     */
    @JvmStatic
    fun smootherstep(n: Double): Double {
        return n * n * n * (n * (n * 6.0 - 15.0) + 10.0)
    }

    /**
     * Выбирает, как обработать значение [n] перед интерполяцией по переменной [i].
     * @param n Некоторое значение с 0.0 до 1.0.
     * @param i Используемая функция (0 - нет; 1 - smoothstep; 2 - smootherstep).
     * @return Результат вычислений.
     */
    @JvmStatic
    fun autoSmooth(n: Double, i: Byte): Double {
        when (i) {
            I_VALUEFUNC_SMOOTHERSTEP -> return smootherstep(n)
            I_VALUEFUNC_SMOOTHSTEP -> return smoothstep(n)
            I_VALUEFUNC_NONE -> return n
            else -> return n
        }
    }

    /**
     * Вспомогательная математическая функция - собственно, линейная интерполяция (дает прямую): примитивная формула для дальнейших расчетов.
     * Подробнее: https://en.wikipedia.org/wiki/Linear_interpolation.
     * @param a Значение A.
     * @param b Значение B.
     * @param n Некоторое значение с 0.0 до 1.0 (из A в B).
     * @return Результат вычислений.
     */
    @JvmStatic
    fun lerp(a: Double, b: Double, n: Double): Double {
        return a + (b - a) * n
    }

    /**
     * Билинейная интерполяция - главная функция! Вызывайте ее для сглаживания значений внутри вершин квадрата.
     * Подробнее: https://en.wikipedia.org/wiki/Bilinear_interpolation.
     * @param value_tl Значение левой верхней вершины.
     * @param value_tr Значение правой верхней вершины.
     * @param value_bl Значение левой нижней вершины.
     * @param value_br Значение правой нижней вершины.
     * @param pInQuadX Координата X нужной точки (места, где мы хотим сгладить) в квадрате (с 0.0 до 1.0).
     * @param pInQuadZ Координата Z нужной точки (места, где мы хотим сгладить) в квадрате (с 0.0 до 1.0).
     * @param i Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
     * @return Результат вычислений.
     */
    @JvmStatic
    fun bilinearInterpolation2D(
        value_tl: Double,
        value_tr: Double,
        value_bl: Double,
        value_br: Double,
        pInQuadX: Double,
        pInQuadZ: Double,
        i: Byte
    ): Double {
        return lerp(
            lerp(value_tl, value_tr, autoSmooth(pInQuadX, i)),
            lerp(value_bl, value_br, autoSmooth(pInQuadX, i)),
            autoSmooth(pInQuadZ, i)
        )
    }
}
