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

import asmodeuscore.core.utils.worldengine2.world.biome.WE_Biome;
import asmodeuscore.core.utils.worldengine2.world.gen.noise.WE_IReliefGenerator;

/**
 * ИНТЕРФЕЙС. Класс с пользовательскими настройками мира, генератор читает их.
 * @author VamigA
 */
public interface WE_IWorldProperties extends WE_IPropsWithGensAndCons {
	/**
	 * Возвращает WE_Biome по идентификатору [i].
	 * @param i Идентификатор.
	 * @return WE_Biome - биом.
	 */
	WE_Biome getBiome(int i);
	/**
	 * Возвращает WE_Biome по умолчанию (если условия в этом месте не подходят для какого-либо биома, то здесь устанавливается биом по умолчанию).
	 * @return WE_Biome - биом.
	 */
	WE_Biome getDefaultBiome();
	/**
	 * Возвращает количество элементов в списке биомов: WE_Biome.
	 * @return Размер списка.
	 */
	int sizeofBiomes();
	
	/**
	 * Возвращает IGenReliefLayer по идентификатору [i].
	 * @param i Идентификатор.
	 * @return IGenReliefLayer - слой.
	 */
	IGenReliefLayer getReliefLayer(int i);
	/**
	 * Возвращает количество элементов в списке слоев: IGenReliefLayer.
	 * @return Размер списка.
	 */
	int sizeofReliefLayers();
	/**
	 * ИНТЕРФЕЙС. Класс одного из рельефных слоев мира.
	 * Для обычных миров достаточно одного слоя. Но, например, ад требует минимум 2 слоя (верхний и нижний рельеф).
	 * @author VamigA
	 */
	interface IGenReliefLayer {
		/**
		 * Шумовой класс рельефного слоя (возвращает его).
		 * @return WE_IReliefGenerator - генератор.
		 */
		WE_IReliefGenerator getReliefGenerator();
		
		/**
		 * Размер прямоугольника, в котором происходит интерполяция рельефа между биомами (в блоках).
		 * @return Integer[0] - размер по X; Integer[1] - размер по Z.
		 */
		int[] getInterQuadSize();
		/**
		 * Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
		 * @return Тип Byte.
		 */
		byte getInterType();
		/**
		 * Сглаживать ли рельеф на территории ЭТОГО БИОМА на стыках между другими биомами? Аналог переменной "interWithAnotherBiome".
		 * (!) Используется генератором, только если генерация зашла в биом, у которого нет этого рельефного слоя.
		 * @return Тип Boolean.
		 */
		boolean getInterDoIfVoid();
	}
	
	/**
	 * Возвращает IGenBiomeMapLayer по идентификатору [i].
	 * @param i Идентификатор.
	 * @return IGenBiomeMapLayer - слой.
	 */
	IGenBiomeMapLayer getBiomeMapLayer(int i);
	/**
	 * Возвращает количество элементов в списке слоев: IGenBiomeMapLayer.
	 * @return Размер списка.
	 */
	int sizeofBiomeMapLayers();
	/**
	 * ИНТЕРФЕЙС. Класс одного из слоев биомной карты мира. Ванильный Minecraft использует для распределения биомов аналогичный класс - GenLayer.
	 * Так, например, можно использовать первый GenBiomeMapLayer для океана/поверхности, второй - для температуры, третий - для ID биома, четвертый - для рек.
	 * @author VamigA
	 */
	interface IGenBiomeMapLayer {
		/**
		 * Шумовой класс слоя биомной карты (возвращает его).
		 * @return WE_IReliefGenerator - генератор.
		 */
		WE_IReliefGenerator getReliefGenerator();
	}
}