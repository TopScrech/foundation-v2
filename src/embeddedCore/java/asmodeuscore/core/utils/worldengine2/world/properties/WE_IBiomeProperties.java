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

package asmodeuscore.core.utils.worldengine2.world.properties;

import net.minecraft.world.biome.Biome.BiomeProperties;
/**
 * ИНТЕРФЕЙС. Класс с пользовательскими настройками биома, генератор читает их.
 * @author VamigA
 */
public interface WE_IBiomeProperties extends WE_IPropsWithGensAndCons {
	/**
	 * Возвращает ванильный объект класса BiomeProperties (если нужно изменить цвета, дать название...).
	 * @return Ванильный мусор.
	 */
	BiomeProperties getVanillaBiomeProperties();
	
	/**
	 * Возвращает IReliefLayerTerrainPropertiesInBiome по идентификатору [i].
	 * @param i Идентификатор.
	 * @return IReliefLayerTerrainPropertiesInBiome - настройки.
	 */
	IReliefLayerTerrainPropertiesInBiome getTerrainProps(int i);
	/**
	 * Возвращает количество элементов в списке настроек: IReliefLayerTerrainPropertiesInBiome.
	 * @return Размер списка.
	 */
	int sizeofTerrainProps();
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
		boolean getInterWithAnotherBiome();
		
		/**
		 * Параметр Persistence этого рельефного слоя на этом биоме.
		 * @return Этот параметр.
		 */
		double getPersistence();
		/**
		 * Параметр ScaleY этого рельефного слоя на этом биоме.
		 * @return Этот параметр.
		 */
		double getScaleY();
		/**
		 * Параметр Height этого рельефного слоя на этом биоме.
		 * @return Этот параметр.
		 */
		int getHeight();
	}
}