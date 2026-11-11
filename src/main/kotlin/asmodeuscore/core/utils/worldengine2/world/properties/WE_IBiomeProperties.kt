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

import net.minecraft.world.biome.Biome.BiomeProperties

import net.minecraft.world.level.biome.Biome

/**
 * ИНТЕРФЕЙС. Класс с пользовательскими настройками биома, генератор читает их.
 * @author VamigA
 */
interface WE_IBiomeProperties : WE_IPropsWithGensAndCons {
    /**
     * Возвращает ванильный объект класса BiomeProperties (если нужно изменить цвета, дать название...).
     * @return Ванильный мусор.
     */
    fun getVanillaBiomeProperties(): Biome.BiomeProperties?

    /**
     * Возвращает IReliefLayerTerrainPropertiesInBiome по идентификатору [i].
     * @param i Идентификатор.
     * @return IReliefLayerTerrainPropertiesInBiome - настройки.
     */
    fun getTerrainProps(i: Int): IReliefLayerTerrainPropertiesInBiome?

    /**
     * Возвращает количество элементов в списке настроек: IReliefLayerTerrainPropertiesInBiome.
     * @return Размер списка.
     */
    fun sizeofTerrainProps(): Int

    /**
     * ИНТЕРФЕЙС. Настройки рельефного слоя (GenReliefLayer) в этом биоме.
     * @author VamigA
     */
    interface IReliefLayerTerrainPropertiesInBiome {
        /**
         * Генератор сглаживает рельеф на стыках между биомами путем интерполяции (смотрите: GenReliefLayer).
         * Если выдаст false, генератор не изменит рельефа на территории ЭТОГО БИОМА на стыках между другими биомами.
         * Если выдаст true, генератор также сгладит рельеф на территории ЭТОГО БИОМА на стыках между другими биомами.
         * @return Этот параметр.
         */
        fun getInterWithAnotherBiome(): Boolean

        /**
         * Параметр Persistence этого рельефного слоя на этом биоме.
         * @return Этот параметр.
         */
        fun getPersistence(): Double

        /**
         * Параметр ScaleY этого рельефного слоя на этом биоме.
         * @return Этот параметр.
         */
        fun getScaleY(): Double

        /**
         * Параметр Height этого рельефного слоя на этом биоме.
         * @return Этот параметр.
         */
        fun getHeight(): Int
    }
}
