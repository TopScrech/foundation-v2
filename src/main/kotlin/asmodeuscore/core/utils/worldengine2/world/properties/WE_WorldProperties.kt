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

import asmodeuscore.core.utils.Utils
import asmodeuscore.core.utils.worldengine2.world.biome.WE_Biome
import asmodeuscore.core.utils.worldengine2.world.gen.noise.WE_IReliefGenerator
import asmodeuscore.core.utils.worldengine2.world.gen.noise.WE_PerlinNoise
import asmodeuscore.core.utils.worldengine2.world.gen.noise.WE_ValueNoise
import asmodeuscore.core.utils.worldengine2.world.properties.WE_IWorldProperties.IGenBiomeMapLayer
import asmodeuscore.core.utils.worldengine2.world.properties.WE_IWorldProperties.IGenReliefLayer

/**
 * Класс с пользовательскими настройками мира, генератор читает их.
 * @author VamigA
 */
open class WE_WorldProperties constructor() : WE_AbstactProperties(), WE_IWorldProperties {
    /** Список с биомами WorldEngine.  */
    @JvmField
    var biomes: ArrayList<WE_Biome?>

    /** Список с рельефными слоями. Используйте переменную genConditions здесь, чтобы настроить заполнение блоками на этих слоях.  */
    @JvmField
    var reliefLayers: ArrayList<GenReliefLayer?>

    /** Список со слоями биомной карты. Используйте переменную genConditions в WE_BiomeProperties, чтобы настроить распределение биомов на этих слоях.  */
    @JvmField
    var bMapLayers: ArrayList<GenBiomeMapLayer?>

    /** Идентификатор биома: 1) Биом по умолчанию (установите его). 2) Последний добавленный биом (не редактируйте его).  */
    @JvmField
var defaultBiomeId: Int

    @JvmField
var lastBiome: Int = 0

    /** Конструктор. Не забудьте позже отметить условия генерации блоков ландшафта в genConditions здесь (суперкласс WE_AbstactProperties содержит его).  */
    init {
        reliefLayers = ArrayList<GenReliefLayer?>()
        bMapLayers = ArrayList<GenBiomeMapLayer?>()

        biomes = ArrayList<WE_Biome?>()
        defaultBiomeId = -1
    }

    /**
     * Добавляет биом в список.
     * @param biome Биом.
     * @return Идентификатор биома в списке.
     */
    open fun addBiome(biome: WE_Biome?): Int {
        biomes.add(biome)
        return (biomes.size - 1).also { lastBiome = it }
    }

    /** Устанавливает последний добавленный биом как биом по умолчанию.  */
    open fun setLastBiomeDefault() {
        defaultBiomeId = lastBiome
    }

    /**
     * Возвращает последний добавленный в список биом.
     * @return Биом.
     */
    open fun getLastBiome(): WE_Biome? {
        return biomes.get(lastBiome)
    }

    /** Очищает список биомов.  */
    open fun clearBiomes() {
        biomes.clear()
    }

    /**
     * Добавляет рельефный слой в список.
     * @param isPerlin Если true, то будет использован шум Перлина, если false - шум значений.
     * @param nOcts Количество октав.
     * @param sx Множитель масштаба по X всей волны.
     * @param sz Множитель масштаба по Z всей волны.
     * @param inter Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
     * @param iX Размер прямоугольника по X, в котором происходит интерполяция рельефа между биомами (в блоках).
     * @param iZ Размер прямоугольника по Z, в котором происходит интерполяция рельефа между биомами (в блоках).
     * @param iType То же самое, что и "inter", но только для интерполяции рельефа между биомами.
     * @param iDoIfVoid Аналог "interWithAnotherBiome". Используется, только если генерация зашла в биом, у которого нет этого рельефного слоя.
     */
    open fun addReliefLayer(
        isPerlin: Boolean,
        nOcts: Int,
        sx: Double,
        sz: Double,
        inter: Byte,
        iX: Int,
        iZ: Int,
        iType: Byte,
        iDoIfVoid: Boolean
    ) {
        reliefLayers.add(GenReliefLayer(isPerlin, nOcts, sx, sz, inter, iX, iZ, iType, iDoIfVoid))
    }

    /** Очищает список рельефных слоев.  */
    open fun clearReliefLayers() {
        reliefLayers.clear()
    }

    /**
     * Добавляет слой карты в список.
     * @param isPerlin Если true, то будет использован шум Перлина, если false - шум значений.
     * @param gPers Стойкость (амплитудный множитель для каждой октавы относительно предыдущей).
     * @param nOcts Количество октав.
     * @param sx Множитель масштаба по X всей волны.
     * @param sy Множитель масштаба по Y всей волны.
     * @param sz Множитель масштаба по Z всей волны.
     * @param sum Высота (будет просто суммировано к конечному результату).
     * @param inter Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
     */
    open fun addMapLayer(
        isPerlin: Boolean,
        gPers: Double,
        nOcts: Int,
        sx: Double,
        sy: Double,
        sz: Double,
        sum: Int,
        inter: Byte
    ) {
        bMapLayers.add(GenBiomeMapLayer(isPerlin, gPers, nOcts, sx, sy, sz, sum, inter))
    }

    /** Очищает список слоев карты.  */
    open fun clearMapLayers() {
        bMapLayers.clear()
    }

    /**
     * Класс одного из рельефных слоев мира.
     * Для обычных миров достаточно одного слоя. Но, например, ад требует минимум 2 слоя (верхний и нижний рельеф).
     * @author VamigA
     */
    open inner class GenReliefLayer constructor(
        isPerlin: Boolean,
        nOcts: Int,
        sx: Double,
        sz: Double,
        inter: Byte,
        iX: Int,
        iZ: Int,
        iType: Byte,
        iDoIfVoid: Boolean
    ) : IGenReliefLayer {
        /** Шумовой класс рельефного слоя.  */
        @JvmField
        var reliefLayerNoise: WE_IReliefGenerator

        /** Размер прямоугольника, в котором происходит интерполяция рельефа между биомами (в блоках).  */
        @JvmField
        var interQuadSize: IntArray

        /** Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).  */
        @JvmField
        var interType: Byte

        /** Аналог "interWithAnotherBiome". Используется, только если генерация зашла в биом, у которого нет этого рельефного слоя.  */
        @JvmField
        var interDoIfVoid: Boolean

        /**
         * Конструктор.
         * @param isPerlin Если true, то будет использован шум Перлина, если false - шум значений.
         * @param nOcts Количество октав.
         * @param sx Множитель масштаба по X всей волны.
         * @param sz Множитель масштаба по Z всей волны.
         * @param inter Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
         * @param iX Размер прямоугольника по X, в котором происходит интерполяция рельефа между биомами (в блоках).
         * @param iZ Размер прямоугольника по Z, в котором происходит интерполяция рельефа между биомами (в блоках).
         * @param iType То же самое, что и "inter", но только для интерполяции рельефа между биомами.
         * @param iDoIfVoid Аналог "interWithAnotherBiome". Используется, только если генерация зашла в биом, у которого нет этого рельефного слоя.
         */
        init {
            reliefLayerNoise = if (isPerlin) WE_PerlinNoise(0L, 0.0, nOcts, sx, 0.0, sz, 0, inter) else WE_ValueNoise(
                0L,
                0.0,
                nOcts,
                sx,
                0.0,
                sz,
                0,
                inter
            )
            interQuadSize = IntArray(2)
            interQuadSize[0] = iX
            interQuadSize[1] = iZ
            interType = iType
            interDoIfVoid = iDoIfVoid
        }

        /**
         * Шумовой класс рельефного слоя (возвращает его).
         * @return WE_IReliefGenerator - генератор.
         */
        override fun getReliefGenerator(): WE_IReliefGenerator {
            return reliefLayerNoise
        }

        /**
         * Размер прямоугольника, в котором происходит интерполяция рельефа между биомами (в блоках).
         * @return Integer[0] - размер по X; Integer[1] - размер по Z.
         */
        override fun getInterQuadSize(): IntArray {
            return interQuadSize
        }

        /**
         * Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
         * @return Тип Byte.
         */
        override fun getInterType(): Byte {
            return interType
        }

        /**
         * Сглаживать ли рельеф на территории ЭТОГО БИОМА на стыках между другими биомами? Аналог переменной "interWithAnotherBiome".
         * (!) Используется генератором, только если генерация зашла в биом, у которого нет этого рельефного слоя.
         * @return Тип Boolean.
         */
        override fun getInterDoIfVoid(): Boolean {
            return interDoIfVoid
        }
    }

    /**
     * Класс одного из слоев биомной карты мира. Ванильный Minecraft использует для распределения биомов аналогичный класс - GenLayer.
     * Так, например, можно использовать первый GenBiomeMapLayer для океана/поверхности, второй - для температуры, третий - для ID биома, четвертый - для рек.
     * @author VamigA
     */
    open inner class GenBiomeMapLayer constructor(
        isPerlin: Boolean,
        gPers: Double,
        nOcts: Int,
        sx: Double,
        sy: Double,
        sz: Double,
        sum: Int,
        inter: Byte
    ) : IGenBiomeMapLayer {
        /** Шумовой класс слоя биомной карты.  */
        @JvmField
        var biomeMapLayerNoise: WE_IReliefGenerator

        /**
         * Конструктор.
         * @param isPerlin Если true, то будет использован шум Перлина, если false - шум значений.
         * @param gPers Стойкость (амплитудный множитель для каждой октавы относительно предыдущей).
         * @param nOcts Количество октав.
         * @param sx Множитель масштаба по X всей волны.
         * @param sy Множитель масштаба по Y всей волны.
         * @param sz Множитель масштаба по Z всей волны.
         * @param sum Высота (будет просто суммировано к конечному результату).
         * @param inter Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
         */
        init {
            biomeMapLayerNoise =
                if (isPerlin) WE_PerlinNoise(0L, gPers, nOcts, sx, sy, sz, sum, inter) else WE_ValueNoise(
                    0L,
                    gPers,
                    nOcts,
                    sx,
                    sy,
                    sz,
                    sum,
                    inter
                )
        }

        /**
         * Шумовой класс слоя биомной карты (возвращает его).
         * @return WE_IReliefGenerator - генератор.
         */
        override fun getReliefGenerator(): WE_IReliefGenerator {
            return biomeMapLayerNoise
        }
    }

    /**
     * Возвращает WE_Biome по идентификатору [i].
     * @param i Идентификатор.
     * @return WE_Biome - биом.
     */
    override fun getBiome(i: Int): WE_Biome? {
        return biomes.get(i)
    }

    /**
     * Возвращает WE_Biome по умолчанию (если условия в этом месте не подходят для какого-либо биома, то здесь устанавливается биом по умолчанию).
     * @return WE_Biome - биом.
     */
    override fun getDefaultBiome(): WE_Biome? {
        return if (defaultBiomeId >= 0 && defaultBiomeId < sizeofBiomes()) getBiome(defaultBiomeId) else Utils.WE_Biome_2 as WE_Biome?
    }

    /**
     * Возвращает количество элементов в списке биомов: WE_Biome.
     * @return Размер списка.
     */
    override fun sizeofBiomes(): Int {
        return biomes.size
    }

    /**
     * Возвращает IGenReliefLayer по идентификатору [i].
     * @param i Идентификатор.
     * @return IGenReliefLayer - слой.
     */
    override fun getReliefLayer(i: Int): IGenReliefLayer {
        return reliefLayers.get(i)!!
    }

    /**
     * Возвращает количество элементов в списке слоев: IGenReliefLayer.
     * @return Размер списка.
     */
    override fun sizeofReliefLayers(): Int {
        return reliefLayers.size
    }

    /**
     * Возвращает IGenBiomeMapLayer по идентификатору [i].
     * @param i Идентификатор.
     * @return IGenBiomeMapLayer - слой.
     */
    override fun getBiomeMapLayer(i: Int): IGenBiomeMapLayer {
        return bMapLayers.get(i)!!
    }

    /**
     * Возвращает количество элементов в списке слоев: IGenBiomeMapLayer.
     * @return Размер списка.
     */
    override fun sizeofBiomeMapLayers(): Int {
        return bMapLayers.size
    }
}
