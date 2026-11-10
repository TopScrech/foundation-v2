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
 * Это класс продвинутого двумерного буфера WorldEngine.
 * Нужен чисто для временного хранения и использования различной информации, которую затратно сгенерировать снова.
 * @param <ValueType> Тип значения, которое хранит данный буфер.
 * @author VamigA
</ValueType> */
open class WE_RegionBuffer<ValueType : Any?> constructor(allSize: Int, methode: ISmartMathFunction<ValueType?>) {
    /** 4 переменные, в которых хранятся 4 субрегиона. Подробнее - в описании класса SubRegion.  */
    @JvmField
var sr00: SubRegion

    @JvmField
var sr10: SubRegion

    @JvmField
var sr01: SubRegion

    @JvmField
var sr11: SubRegion

    /** Необходимые данные для обозначения положения региона, по которому хранятся данные, а size - размер субрегиона (их 4 в регионе).  */
    @JvmField
var currentX: Long

    @JvmField
var currentZ: Long

    @JvmField
var currentX0: Long = 0

    @JvmField
var currentZ0: Long = 0

    @JvmField
var currentX1: Long = 0

    @JvmField
var currentZ1: Long = 0

    @JvmField
var size: Long

    /** Функция, которая вызывается при отсутствии данных в нужной ячейке для записи их туда.  */
    @JvmField
    var f: ISmartMathFunction<ValueType?>

    /**
     * Конструктор.
     * @param allSize Размер всего региона, для удобства он делится на 4 и записывается в классе как размер субрегиона.
     * @param methode Функция, которая вызывается при отсутствии данных в нужной ячейке для записи их туда.
     */
    init {
        size = allSize.toLong() / 4L
        sr00 = SubRegion()
        sr10 = SubRegion()
        sr01 = SubRegion()
        sr11 = SubRegion()

        currentX = 0
        currentZ = 0
        recalcCoords(true, true)

        f = methode
    }

    /**
     * Главная "умная" функция класса, к которой и нужно обращаться за данными.
     * Она и находит нужную ячейку в этой сложной системе хранения данных, записывая туда данные при их отсутствии и выдавая их.
     * При необходимости, если запрашиваются данные за пределами региона, она и производит все нужные манипуляции по его преобразованию.
     * @param x Координата X, по которой запрашиваются данные.
     * @param z Координата Z, по которой запрашиваются данные.
     * @return Нужные данные типа, который был указан при создании класса как ValueType.
     */
    open fun get(x: Long, z: Long): ValueType? {
        if (x > currentX0 && z > currentZ0 && x <= currentX1 && z <= currentZ1) {
            var inSubReg_X: Int = ((x - 1L) % size).toInt()
            var inSubReg_Z: Int = ((z - 1L) % size).toInt()
            if (inSubReg_X < 0) inSubReg_X += size.toInt()
            if (inSubReg_Z < 0) inSubReg_Z += size.toInt()
            if (x <= currentX) {
                if (z <= currentZ) return sr00.returnData(inSubReg_X, inSubReg_Z, x, z)
                else return sr01.returnData(inSubReg_X, inSubReg_Z, x, z)
            } else if (z <= currentZ) return sr10.returnData(inSubReg_X, inSubReg_Z, x, z)
            else return sr11.returnData(inSubReg_X, inSubReg_Z, x, z)
        } else {
            val checkX1: Long = currentX0 - size
            val checkX2: Long = currentX0 + size / 2L
            val checkX3: Long = currentX1 - size / 2L
            val checkX4: Long = currentX1 + size
            val checkZ1: Long = currentZ0 - size
            val checkZ2: Long = currentZ0 + size / 2L
            val checkZ3: Long = currentZ1 - size / 2L
            val checkZ4: Long = currentZ1 + size
            if (x > checkX1 && z > checkZ1 && x <= checkX2 && z <= checkZ2) {
                sr11 = sr00
                sr00 = SubRegion()
                sr01 = SubRegion()
                sr10 = SubRegion()
                currentX = currentX0
                currentZ = currentZ0
                recalcCoords(true, true)
            } else if (x > checkX1 && z > checkZ3 && x <= checkX2 && z <= checkZ4) {
                sr10 = sr01
                sr01 = SubRegion()
                sr00 = SubRegion()
                sr11 = SubRegion()
                currentX = currentX0
                currentZ = currentZ1
                recalcCoords(true, true)
            } else if (x > checkX3 && z > checkZ1 && x <= checkX4 && z <= checkZ2) {
                sr01 = sr10
                sr10 = SubRegion()
                sr00 = SubRegion()
                sr11 = SubRegion()
                currentX = currentX1
                currentZ = currentZ0
                recalcCoords(true, true)
            } else if (x > checkX3 && z > checkZ3 && x <= checkX4 && z <= checkZ4) {
                sr00 = sr11
                sr11 = SubRegion()
                sr01 = SubRegion()
                sr10 = SubRegion()
                currentX = currentX1
                currentZ = currentZ1
                recalcCoords(true, true)
            } else if (x > checkX2 && z > checkZ1 && x <= checkX3 && z <= checkZ2) {
                sr01 = sr00
                sr11 = sr10
                sr00 = SubRegion()
                sr10 = SubRegion()
                currentZ = currentZ0
                recalcCoords(false, true)
            } else if (x > checkX2 && z > checkZ3 && x <= checkX3 && z <= checkZ4) {
                sr00 = sr01
                sr10 = sr11
                sr01 = SubRegion()
                sr11 = SubRegion()
                currentZ = currentZ1
                recalcCoords(false, true)
            } else if (x > checkX1 && z > checkZ2 && x <= checkX2 && z <= checkZ3) {
                sr10 = sr00
                sr11 = sr01
                sr00 = SubRegion()
                sr01 = SubRegion()
                currentX = currentX0
                recalcCoords(true, false)
            } else if (x > checkX3 && z > checkZ2 && x <= checkX4 && z <= checkZ3) {
                sr00 = sr10
                sr01 = sr11
                sr10 = SubRegion()
                sr11 = SubRegion()
                currentX = currentX1
                recalcCoords(true, false)
            } else {
                sr00 = SubRegion()
                sr10 = SubRegion()
                sr01 = SubRegion()
                sr11 = SubRegion()

                val preX1: Long
                val preZ1: Long
                val preX2: Long
                val preZ2: Long
                val interX: Long
                val interZ: Long
                if (x > 0) preX1 = ((x - 1L) / size) * size else preX1 = (x / size) * size - size
                if (z > 0) preZ1 = ((z - 1L) / size) * size else preZ1 = (z / size) * size - size
                preX2 = preX1 + size
                preZ2 = preZ1 + size
                interX = preX1 + size / 2L
                interZ = preZ1 + size / 2L
                if (x > preX1 && z > preZ1 && x <= interX && z <= interZ) {
                    currentX = preX1
                    currentZ = preZ1
                } else if (x > preX1 && z > interZ && x <= interX && z <= preZ2) {
                    currentX = preX1
                    currentZ = preZ2
                } else if (x > interX && z > preZ1 && x <= preX2 && z <= interZ) {
                    currentX = preX2
                    currentZ = preZ1
                } else if (x > interX && z > interZ && x <= preX2 && z <= preZ2) {
                    currentX = preX2
                    currentZ = preZ2
                }
                recalcCoords(true, true)
            }

            return get(x, z)
        }
    }

    /**
     * Перерасчитывает граничные координаты при изменении главных координат региона.
     * @param doX Перерасчитать ли граничные координаты X?
     * @param doZ Перерасчитать ли граничные координаты Z?
     */
    open fun recalcCoords(doX: Boolean, doZ: Boolean) {
        if (doX) {
            currentX0 = currentX - size
            currentX1 = currentX + size
        }
        if (doZ) {
            currentZ0 = currentZ - size
            currentZ1 = currentZ + size
        }
    }

    /**
     * Буфер работает по принципу "умного" двумерного массива: вся область хранения - регион - делится еще на 4 субрегиона.
     * Это позволяет при переходе к ближайшему региону сохранить до 25-50% информации с предыдущего в новом регионе.
     * И только если генерация ушла далеко от предыдущего региона, только тогда создается полностью чистый новый регион.
     * @author VamigA
     */
    open inner class SubRegion constructor() {
        /** Массив субрегиона, в котором и хранятся нужные данные. К нему напрямую и идет обращение.  */
        @JvmField
        var array: Array<Array<Any?>?>

        /** Не всегда можно понять только по значению в массиве, записаны ли туда данные. Этот массив и дает об этом понять.  */
        @JvmField
        var isValue: Array<BooleanArray?>

        /** Конструктор. Инициализирует массивы.  */
        init {
            array = Array<Array<Any?>?>(size.toInt()) { arrayOfNulls<Any>(size.toInt()) }
            isValue = Array<BooleanArray?>(size.toInt()) { BooleanArray(size.toInt()) }
        }

        /**
         * Проверяет, есть ли данные в данной ячейке и выдает их. Если нет данных - обращается за ними и записывает их в ячейку.
         * @param inSubReg_X Координата X внутри субрегиона, то есть индекс массива субрегиона.
         * @param inSubReg_Z Координата Z внутри субрегиона, то есть индекс массива субрегиона.
         * @param x Общая координата X. Нужна при обращении за данными по этой координате при их отсутствии.
         * @param z Общая координата Z. Нужна при обращении за данными по этой координате при их отсутствии.
         * @return Нужные данные типа, который был указан при создании класса как ValueType.
         */
        open fun returnData(inSubReg_X: Int, inSubReg_Z: Int, x: Long, z: Long): ValueType? {
            if (!isValue[inSubReg_X]!![inSubReg_Z]) {
                isValue[inSubReg_X]!![inSubReg_Z] = true
                array[inSubReg_X]!![inSubReg_Z] = f.g(x, z)
            }
            return array[inSubReg_X]!![inSubReg_Z] as ValueType?
        }
    }
}
