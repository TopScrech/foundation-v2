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
package asmodeuscore.core.utils.worldengine2.world.properties

import java.util.ArrayList

import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_ICreateChunkGen
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_ICreateChunkGen_InXYZ
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_ICreateChunkGen_InXZ
import asmodeuscore.core.utils.worldengine2.world.properties.WE_IPropsWithGensAndCons.IGenReliefConditions
import net.minecraftforge.fml.common.IWorldGenerator

/**
 * Супер-класс для классов WE_WorldProperties и WE_BiomeProperties. Содержит необходимый для них класс динамических условий - GenReliefConditions.
 * 1) GenReliefConditions в WE_WorldProperties говорит генератору, когда и где он должен сгенерировать ландшафт по высоте слоев рельефа.
 * 2) GenReliefConditions в WE_BiomeProperties говорит генератору, когда и где он должен сгенерировать биом по высоте слоев биом-карты.
 * @author VamigA
 */
abstract class WE_AbstactProperties : WE_IPropsWithGensAndCons {
    /** Переменная с классом GenReliefConditions. Класс говорит генератору, когда и где он должен сгенерировать блок камня или биом.  */
    @JvmField
    var genConditions: GenReliefConditions

    /** Первая стадия генерации чанков: генераторы на каждый чанк.  */
    @JvmField
    var createChunkGen_List: ArrayList<WE_ICreateChunkGen?>

    /** Первая стадия генерации чанков: генераторы на каждый 256-блоковый слой [X, Z].  */
    @JvmField
    var createChunkGen_InXZ_List: ArrayList<WE_ICreateChunkGen_InXZ?>

    /** Первая стадия генерации чанков: генераторы на каждый блок [X, Y, Z].  */
    @JvmField
    var createChunkGen_InXYZ_List: ArrayList<WE_ICreateChunkGen_InXYZ?>

    /** Вторая стадия генерации чанков: стандартные генераторы Minecraft Forge.  */
    @JvmField
    var decorateChunkGen_List: ArrayList<IWorldGenerator?>

    /** Идентификаторы последних добавленных генераторов.  */
    @JvmField
var lastCCG: Int = 0

    @JvmField
var lastCCGXZ: Int = 0

    @JvmField
var lastCCGXYZ: Int = 0

    @JvmField
var lastDCG: Int = 0

    /** Конструктор.  */
    init {
        genConditions = GenReliefConditions()

        createChunkGen_List = ArrayList<WE_ICreateChunkGen?>()
        createChunkGen_InXZ_List = ArrayList<WE_ICreateChunkGen_InXZ?>()
        createChunkGen_InXYZ_List = ArrayList<WE_ICreateChunkGen_InXYZ?>()
        decorateChunkGen_List = ArrayList<IWorldGenerator?>()
    }

    /**
     * Добавляет генератор в список.
     * @param g Генератор.
     * @return Идентификатор генератора в списке.
     */
    fun addCCG(g: WE_ICreateChunkGen?): Int {
        createChunkGen_List.add(g)
        return (createChunkGen_List.size - 1).also { lastCCG = it }
    }

    /**
     * Возвращает последний добавленный в список генератор.
     * @return Генератор.
     */
    fun getLastCCG(): WE_ICreateChunkGen? {
        return createChunkGen_List.get(lastCCG)
    }

    /** Очищает список генераторов.  */
    fun clearCCG() {
        createChunkGen_List.clear()
    }

    /**
     * Добавляет генератор в список.
     * @param g Генератор.
     * @return Идентификатор генератора в списке.
     */
    fun addCCGXZ(g: WE_ICreateChunkGen_InXZ?): Int {
        createChunkGen_InXZ_List.add(g)
        return (createChunkGen_InXZ_List.size - 1).also { lastCCGXZ = it }
    }

    /**
     * Возвращает последний добавленный в список генератор.
     * @return Генератор.
     */
    fun getLastCCGXZ(): WE_ICreateChunkGen_InXZ? {
        return createChunkGen_InXZ_List.get(lastCCGXZ)
    }

    /** Очищает список генераторов.  */
    fun clearCCGXZ() {
        createChunkGen_InXZ_List.clear()
    }

    /**
     * Добавляет генератор в список.
     * @param g Генератор.
     * @return Идентификатор генератора в списке.
     */
    fun addCCGXYZ(g: WE_ICreateChunkGen_InXYZ?): Int {
        createChunkGen_InXYZ_List.add(g)
        return (createChunkGen_InXYZ_List.size - 1).also { lastCCGXYZ = it }
    }

    /**
     * Возвращает последний добавленный в список генератор.
     * @return Генератор.
     */
    fun getLastCCGXYZ(): WE_ICreateChunkGen_InXYZ? {
        return createChunkGen_InXYZ_List.get(lastCCGXYZ)
    }

    /** Очищает список генераторов.  */
    fun clearCCGXYZ() {
        createChunkGen_InXYZ_List.clear()
    }

    /**
     * Добавляет генератор в список.
     * @param g Генератор.
     * @return Идентификатор генератора в списке.
     */
    fun addDCG(g: IWorldGenerator?): Int {
        decorateChunkGen_List.add(g)
        return (decorateChunkGen_List.size - 1).also { lastDCG = it }
    }

    /**
     * Возвращает последний добавленный в список генератор.
     * @return Генератор.
     */
    fun getLastDCG(): IWorldGenerator? {
        return decorateChunkGen_List.get(lastDCG)
    }

    /** Очищает список генераторов.  */
    fun clearDCG() {
        decorateChunkGen_List.clear()
    }

    /**
     * Этот класс говорит генератору, когда и где он должен сгенерировать блок камня или биом. GenReliefConditions = динамическая версия конструкции "if".
     * @author VamigA
     */
    open class GenReliefConditions constructor() : IGenReliefConditions {
        /** Список с примитивными условиями.  */
        @JvmField
        var conditions: ArrayList<PrimitiveCondition?>

        /** Действия между примитивными условиями (0: [&&]; 1: [||]).  */
        @JvmField
        var actions: ArrayList<Byte>

        /** Идентификатор последнего добавленного условия.  */
        @JvmField
        var lastCon: Int = 0

        /** Конструктор.  */
        init {
            conditions = ArrayList<PrimitiveCondition?>()
            actions = ArrayList<Byte>()
        }

        /**
         * Добавляет примитивное условие.
         * @param action Действие между двумя математическими конструкциями.
         * @return Идентификатор в списке.
         */
        open fun addCon(action: Byte): Int {
            conditions.add(PrimitiveCondition(action))
            return (conditions.size - 1).also { lastCon = it }
        }

        /**
         * Возвращает последнее добавленное в список условие.
         * @return Примитивное условие.
         */
        open fun getLast(): PrimitiveCondition {
            return conditions.get(lastCon)!!
        }

        /** Очищает список условий.  */
        open fun clearCon() {
            conditions.clear()
        }

        /**
         * Добавляет действие. Количество действий должно быть ([количество условий] - 1).
         * @param a Действие.
         */
        open fun addAct(a: Byte) {
            if (actions.size > conditions.size - 2) {
                println("[ERROR] GenReliefConditions.addAct(byte a): quantity of the actions must be ([conditions quantity] - 1).")
                throw IllegalArgumentException("GenReliefConditions.addAct(byte a): quantity of the actions must be ([conditions quantity] - 1).")
            } else actions.add(a)
        }

        /** Очищает список действий.  */
        open fun clearAct() {
            actions.clear()
        }

        /**
         * Выполняет эти примитивные условия. Получается большое условие! Формула:
         * (((conditions[0] *actions[0]* conditions[1]) *actions[1]* conditions[2]) *actions[2]* conditions[3]) *actions[3]* conditions[4]...
         * Как Вы видите, эта функция не соблюдает стандартный приоритет логических операций.
         * @param rLayersDataFromGen Данные высоты слоев рельефа или биом-карты с генератора для класса MathConstruction. Индексы этого массива означают идентификаторы данных слоев рельефа или биом-карты.
         * В WE_WorldProperties еще одна ячейка массива в конце (rLayersDataFromGen[количество_слоев]) используется для нынешней высоты генерации.
         * @return Результат большого условия.
         */
        override fun go(rLayersDataFromGen: IntArray?): Boolean {
            var res: Boolean = conditions.get(0)!!.go((rLayersDataFromGen)!!)
            for (i: Int in actions.indices) when ((actions.get(i)).toInt()) {
                0 -> res = res and conditions.get(i + 1)!!.go((rLayersDataFromGen)!!)
                1 -> res = res or conditions.get(i + 1)!!.go((rLayersDataFromGen)!!)
            }
            return res
        }

        /**
         * Класс для динамических примитивных условий, подобно *MathConstruction* [действие] *MathConstruction*.
         * @author VamigA
         */
        open class PrimitiveCondition constructor(action: Byte) {
            /** Массив с 2 элементами - математическими конструкциями для сравнения.  */
            @JvmField
            var twoMathConstructions: Array<MathConstruction?>

            /** Сравнивающее действие между 2 математическими конструкциями (0: [<]; 1: [<=]; 2: [==]; 3: [!=]; 4: [>=]; 5: [>]).  */
            @JvmField
            var actionBetweenThem: Byte

            /**
             * Конструктор.
             * @param action Действие между 2 математическими конструкциями.
             */
            init {
                twoMathConstructions = arrayOfNulls<MathConstruction>(2)
                twoMathConstructions[0] = MathConstruction()
                actionBetweenThem = action
                twoMathConstructions[1] = MathConstruction()
            }

            /**
             * Первый класс MathConstruction.
             * @return Значение twoMathConstructions[0].
             */
            open fun getFirstMath(): MathConstruction? {
                return twoMathConstructions[0]
            }

            /**
             * Второй класс MathConstruction.
             * @return Значение twoMathConstructions[1].
             */
            open fun getSecondMath(): MathConstruction? {
                return twoMathConstructions[1]
            }

            /**
             * Выполняет примитивное условие.
             * @param rLayersDataFromGen Данные высоты слоев рельефа или биом-карты с генератора для класса MathConstruction. Индексы этого массива означают идентификаторы данных слоев рельефа или биом-карты.
             * В WE_WorldProperties еще одна ячейка массива в конце (rLayersDataFromGen[количество_слоев]) используется для нынешней высоты генерации.
             * @return Результат условия.
             */
            open fun go(rLayersDataFromGen: IntArray): Boolean {
                when ((actionBetweenThem).toInt()) {
                    0 -> return twoMathConstructions[0]!!.go(rLayersDataFromGen) < twoMathConstructions[1]!!.go(
                        rLayersDataFromGen
                    )

                    1 -> return twoMathConstructions[0]!!.go(rLayersDataFromGen) <= twoMathConstructions[1]!!.go(
                        rLayersDataFromGen
                    )

                    2 -> return twoMathConstructions[0]!!.go(rLayersDataFromGen) == twoMathConstructions[1]!!.go(
                        rLayersDataFromGen
                    )

                    3 -> return twoMathConstructions[0]!!.go(rLayersDataFromGen) != twoMathConstructions[1]!!.go(
                        rLayersDataFromGen
                    )

                    4 -> return twoMathConstructions[0]!!.go(rLayersDataFromGen) >= twoMathConstructions[1]!!.go(
                        rLayersDataFromGen
                    )

                    5 -> return twoMathConstructions[0]!!.go(rLayersDataFromGen) > twoMathConstructions[1]!!.go(
                        rLayersDataFromGen
                    )

                    else -> return false
                }
            }

            /**
             * Класс для динамических математических конструкций. Как примитивный калькулятор.
             * @author VamigA
             */
            open class MathConstruction constructor() {
                /** Числа для расчетов. Тут нужно целое число или класс ReliefLayerData (вместо класса будут подставлены данные высоты слоев рельефа или биом-карты на [X, Y]).  */
                @JvmField
                var numbers: ArrayList<Any?>

                /** Действия между числами (0: [*]; 1: [/]; 2: [%]; 3: [+]; 4: [-]; 5: [<<]; 6: [>>]; 7: [&]; 8: [^]; 9: [|]). Их количество должно быть ([количество чисел] - 1).  */
                @JvmField
                var actions: ArrayList<Byte>

                /** Конструктор.  */
                init {
                    numbers = ArrayList<Any?>()
                    actions = ArrayList<Byte>()
                }

                /**
                 * Добавляет число.
                 * @param n Число.
                 * @param isIdForData Является ли это число идентификатором слоя рельефа или биом-карты?
                 */
                open fun addNum(n: Int, isIdForData: Boolean) {
                    if (isIdForData) numbers.add(ReliefLayerData(n))
                    else numbers.add(n)
                }

                /** Очищает список чисел.  */
                open fun clearNum() {
                    numbers.clear()
                }

                /**
                 * Добавляет действие. Количество действий должно быть ([количество чисел] - 1).
                 * @param a Действие.
                 */
                open fun addAct(a: Byte) {
                    if (actions.size > numbers.size - 2) {
                        println("[ERROR] MathConstruction.addAct(byte a): quantity of the actions must be ([numbers quantity] - 1).")
                        throw IllegalArgumentException("MathConstruction.addAct(byte a): quantity of the actions must be ([numbers quantity] - 1).")
                    } else actions.add(a)
                }

                /** Очищает список действий.  */
                open fun clearAct() {
                    actions.clear()
                }

                /**
                 * Возвращает целое значение с массива "чисел". Нужно ввиду наличия данных класса ReliefLayerData в нем.
                 * @param i Индекс "числа" в массиве.
                 * @param rLayersDataFromGen Данные высоты слоев рельефа или биом-карты с генератора. Индексы этого массива означают идентификаторы данных слоев рельефа или биом-карты.
                 * В WE_WorldProperties еще одна ячейка массива в конце (rLayersDataFromGen[количество_слоев]) используется для нынешней высоты генерации.
                 * @return Нужное число.
                 */
                open fun getNumber(i: Int, rLayersDataFromGen: IntArray): Int {
                    if (numbers.get(i)!!.javaClass == ReliefLayerData::class.java) return rLayersDataFromGen[(numbers.get(
                        i
                    ) as ReliefLayerData).layerID]
                    else return numbers.get(i) as Int
                }

                /**
                 * Выполняет эти математические операции с числами в этой математической конструкции. Формула:
                 * (((numbers[0] *actions[0]* numbers[1]) *actions[1]* numbers[2]) *actions[2]* numbers[3]) *actions[3]* numbers[4]...
                 * Как Вы видите, эта функция не соблюдает стандартный приоритет математических операций.
                 * @param rLayersDataFromGen Данные высоты слоев рельефа или биом-карты с генератора. Индексы этого массива означают идентификаторы данных слоев рельефа или биом-карты.
                 * В WE_WorldProperties еще одна ячейка массива в конце (rLayersDataFromGen[количество_слоев]) используется для нынешней высоты генерации.
                 * @return Результат математических операций.
                 */
                open fun go(rLayersDataFromGen: IntArray): Int {
                    var res: Int = getNumber(0, rLayersDataFromGen)
                    for (i: Int in actions.indices) when ((actions.get(i)).toInt()) {
                        0 -> res *= getNumber(i + 1, rLayersDataFromGen)
                        1 -> res /= getNumber(i + 1, rLayersDataFromGen)
                        2 -> res %= getNumber(i + 1, rLayersDataFromGen)
                        3 -> res += getNumber(i + 1, rLayersDataFromGen)
                        4 -> res -= getNumber(i + 1, rLayersDataFromGen)
                        5 -> res = res shl getNumber(i + 1, rLayersDataFromGen)
                        6 -> res = res shr getNumber(i + 1, rLayersDataFromGen)
                        7 -> res = res and getNumber(i + 1, rLayersDataFromGen)
                        8 -> res = res xor getNumber(i + 1, rLayersDataFromGen)
                        9 -> res = res or getNumber(i + 1, rLayersDataFromGen)
                    }
                    return res
                }

                /**
                 * Это маленький класс, который говорит MathConstruction, что numbers[i] с ним - это именно данные высоты слоя рельефа или биом-карты.
                 * В WE_WorldProperties еще одна ячейка массива в конце используется для нынешней высоты генерации.
                 * @author VamigA
                 */
                open inner class ReliefLayerData
                /**
                 * Конструктор.
                 * @param id Необходимый идентификатор слоя рельефа или биом-карты. Нам нужны сами данные высоты этих слоев.
                 * В WE_WorldProperties еще одна ячейка массива в конце (id = количество слоев) используется для нынешней высоты генерации.
                 */ constructor(
                    /**
                     * Необходимый идентификатор слоя рельефа или биом-карты в настройках, в списках "reliefLayers" или "bMapLayers".
                     * В WE_WorldProperties еще одна ячейка массива в конце (layerID = количество слоев) используется для нынешней высоты генерации.
                     */
                    @JvmField var layerID: Int
                )

                companion object {
                    /** Константы для параметров.  */
                    const val MC_ACTION_MULTIPLY: Byte = 0

                    const val MC_ACTION_DIVIDE: Byte = 1

                    const val MC_ACTION_DIVIDEMOD: Byte = 2

                    const val MC_ACTION_ADD: Byte = 3

                    const val MC_ACTION_SUBTRACT: Byte = 4

                    const val MC_ACTION_SHIFTLEFT: Byte = 5

                    const val MC_ACTION_SHIFTRIGHT: Byte = 6

                    const val MC_ACTION_BITAND: Byte = 7

                    const val MC_ACTION_BITXOR: Byte = 8

                    const val MC_ACTION_BITOR: Byte = 9
                }
            }

            companion object {
                /** Константы для параметров.  */
                const val PC_ACTION_LESS: Byte = 0

                const val PC_ACTION_LESSEQUAL: Byte = 1

                const val PC_ACTION_EQUAL: Byte = 2

                const val PC_ACTION_NOTEQUAL: Byte = 3

                const val PC_ACTION_MOREEQUAL: Byte = 4

                const val PC_ACTION_MORE: Byte = 5
            }
        }

        companion object {
            /** Константы для параметров.  */
            const val RC_ACTION_AND: Byte = 0

            const val RC_ACTION_OR: Byte = 1
        }
    }

    /**
     * Возвращает WE_ICreateChunkGen по идентификатору [i].
     * @param i Идентификатор.
     * @return WE_ICreateChunkGen - генератор.
     */
    override fun getCreateChunkGen(i: Int): WE_ICreateChunkGen? {
        return createChunkGen_List.get(i)
    }

    /**
     * Возвращает количество элементов в списке генераторов: WE_ICreateChunkGen.
     * @return Размер списка.
     */
    override fun sizeofCreateChunkGen(): Int {
        return createChunkGen_List.size
    }

    /**
     * Возвращает WE_ICreateChunkGen_InXZ по идентификатору [i].
     * @param i Идентификатор.
     * @return WE_ICreateChunkGen_InXZ - генератор.
     */
    override fun getCreateChunkGenInXZ(i: Int): WE_ICreateChunkGen_InXZ? {
        return createChunkGen_InXZ_List.get(i)
    }

    /**
     * Возвращает количество элементов в списке генераторов: WE_ICreateChunkGen_InXZ.
     * @return Размер списка.
     */
    override fun sizeofCreateChunkGenInXZ(): Int {
        return createChunkGen_InXZ_List.size
    }

    /**
     * Возвращает WE_ICreateChunkGen_InXYZ по идентификатору [i].
     * @param i Идентификатор.
     * @return WE_ICreateChunkGen_InXYZ - генератор.
     */
    override fun getCreateChunkGenInXYZ(i: Int): WE_ICreateChunkGen_InXYZ? {
        return createChunkGen_InXYZ_List.get(i)
    }

    /**
     * Возвращает количество элементов в списке генераторов: WE_ICreateChunkGen_InXYZ.
     * @return Размер списка.
     */
    override fun sizeofCreateChunkGenInXYZ(): Int {
        return createChunkGen_InXYZ_List.size
    }

    /**
     * Возвращает Feature по идентификатору [i].
     * @param i Идентификатор.
     * @return Feature - генератор.
     */
    override fun getDecorateChunkGen(i: Int): IWorldGenerator? {
        return decorateChunkGen_List.get(i)
    }

    /**
     * Возвращает количество элементов в списке генераторов: Feature.
     * @return Размер списка.
     */
    override fun sizeofDecorateChunkGen(): Int {
        return decorateChunkGen_List.size
    }

    /**
     * Возвращает IGenReliefConditions для этого мира (для этих параметров).
     * @return IGenReliefConditions - условия генерации.
     */
    override fun getLayersConditions(): IGenReliefConditions {
        return genConditions
    }
}
