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

import java.util.ArrayList;

import asmodeuscore.core.utils.Utils;
import asmodeuscore.core.utils.worldengine2.world.biome.WE_Biome;
import asmodeuscore.core.utils.worldengine2.world.gen.noise.WE_IReliefGenerator;
import asmodeuscore.core.utils.worldengine2.world.gen.noise.WE_PerlinNoise;
import asmodeuscore.core.utils.worldengine2.world.gen.noise.WE_ValueNoise;


/**
 * Класс с пользовательскими настройками мира, генератор читает их.
 * @author VamigA
 */
public class WE_WorldProperties extends WE_AbstactProperties implements WE_IWorldProperties {
	/** Список с биомами WorldEngine. */
	public ArrayList<WE_Biome        > biomes      ;
	/** Список с рельефными слоями. Используйте переменную genConditions здесь, чтобы настроить заполнение блоками на этих слоях. */
	public ArrayList<GenReliefLayer  > reliefLayers;
	/** Список со слоями биомной карты. Используйте переменную genConditions в WE_BiomeProperties, чтобы настроить распределение биомов на этих слоях. */
	public ArrayList<GenBiomeMapLayer> bMapLayers  ;
	
	/** Идентификатор биома: 1) Биом по умолчанию (установите его). 2) Последний добавленный биом (не редактируйте его). */
	public int defaultBiomeId, lastBiome;
	
	/** Конструктор. Не забудьте позже отметить условия генерации блоков ландшафта в genConditions здесь (суперкласс WE_AbstactProperties содержит его). */
	public WE_WorldProperties() {
		reliefLayers = new ArrayList<GenReliefLayer  >();
		bMapLayers   = new ArrayList<GenBiomeMapLayer>();
		
		biomes = new ArrayList<WE_Biome>();
		defaultBiomeId = -1;
	}
	
	/**
	 * Добавляет биом в список.
	 * @param biome Биом.
	 * @return Идентификатор биома в списке.
	 */
	public int addBiome(WE_Biome biome) {
		biomes.add(biome);
		return lastBiome = biomes.size() - 1;
	}
	/** Устанавливает последний добавленный биом как биом по умолчанию. */
	public void setLastBiomeDefault() {
		defaultBiomeId = lastBiome;
	}
	/**
	 * Возвращает последний добавленный в список биом.
	 * @return Биом.
	 */
	public WE_Biome getLastBiome() {
		return biomes.get(lastBiome);
	}
	/** Очищает список биомов. */
	public void clearBiomes() {
		biomes.clear();
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
	public void addReliefLayer(boolean isPerlin, int nOcts, double sx, double sz, byte inter, int iX, int iZ, byte iType, boolean iDoIfVoid) {
		reliefLayers.add(new GenReliefLayer(isPerlin, nOcts, sx, sz, inter, iX, iZ, iType, iDoIfVoid));
	}
	/** Очищает список рельефных слоев. */
	public void clearReliefLayers() {
		reliefLayers.clear();
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
	public void addMapLayer(boolean isPerlin, double gPers, int nOcts, double sx, double sy, double sz, int sum, byte inter) {
		bMapLayers.add(new GenBiomeMapLayer(isPerlin, gPers, nOcts, sx, sy, sz, sum, inter));
	}
	/** Очищает список слоев карты. */
	public void clearMapLayers() {
		bMapLayers.clear();
	}
	
	/**
	 * Класс одного из рельефных слоев мира.
	 * Для обычных миров достаточно одного слоя. Но, например, ад требует минимум 2 слоя (верхний и нижний рельеф).
	 * @author VamigA
	 */
	public class GenReliefLayer implements IGenReliefLayer {
		/** Шумовой класс рельефного слоя. */
		public WE_IReliefGenerator reliefLayerNoise;
		/** Размер прямоугольника, в котором происходит интерполяция рельефа между биомами (в блоках). */
		public int[] interQuadSize;
		/** Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep). */
		public byte interType;
		/** Аналог "interWithAnotherBiome". Используется, только если генерация зашла в биом, у которого нет этого рельефного слоя. */
		public boolean interDoIfVoid;
		
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
		public GenReliefLayer(boolean isPerlin, int nOcts, double sx, double sz, byte inter, int iX, int iZ, byte iType, boolean iDoIfVoid) {
			reliefLayerNoise = isPerlin ?
				new WE_PerlinNoise(0L, 0.0, nOcts, sx, 0.0, sz, 0, inter) :
				new  WE_ValueNoise(0L, 0.0, nOcts, sx, 0.0, sz, 0, inter);
			interQuadSize = new int[2]; interQuadSize[0] = iX; interQuadSize[1] = iZ; interType = iType; interDoIfVoid = iDoIfVoid;
		}
		
		/**
		 * Шумовой класс рельефного слоя (возвращает его).
		 * @return WE_IReliefGenerator - генератор.
		 */
		@Override
		public WE_IReliefGenerator getReliefGenerator() {
			return reliefLayerNoise;
		}
		
		/**
		 * Размер прямоугольника, в котором происходит интерполяция рельефа между биомами (в блоках).
		 * @return Integer[0] - размер по X; Integer[1] - размер по Z.
		 */
		@Override
		public int[] getInterQuadSize() {
			return interQuadSize;
		}
		/**
		 * Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
		 * @return Тип Byte.
		 */
		@Override
		public byte getInterType() {
			return interType;
		}
		/**
		 * Сглаживать ли рельеф на территории ЭТОГО БИОМА на стыках между другими биомами? Аналог переменной "interWithAnotherBiome".
		 * (!) Используется генератором, только если генерация зашла в биом, у которого нет этого рельефного слоя.
		 * @return Тип Boolean.
		 */
		@Override
		public boolean getInterDoIfVoid() {
			return interDoIfVoid;
		}
	}
	
	/**
	 * Класс одного из слоев биомной карты мира. Ванильный Minecraft использует для распределения биомов аналогичный класс - GenLayer.
	 * Так, например, можно использовать первый GenBiomeMapLayer для океана/поверхности, второй - для температуры, третий - для ID биома, четвертый - для рек.
	 * @author VamigA
	 */
	public class GenBiomeMapLayer implements IGenBiomeMapLayer {
		/** Шумовой класс слоя биомной карты. */
		public WE_IReliefGenerator biomeMapLayerNoise;
		
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
		public GenBiomeMapLayer(boolean isPerlin, double gPers, int nOcts, double sx, double sy, double sz, int sum, byte inter) {
			biomeMapLayerNoise = isPerlin ?
				new WE_PerlinNoise(0L, gPers, nOcts, sx, sy, sz, sum, inter) :
				new  WE_ValueNoise(0L, gPers, nOcts, sx, sy, sz, sum, inter);
		}
		
		/**
		 * Шумовой класс слоя биомной карты (возвращает его).
		 * @return WE_IReliefGenerator - генератор.
		 */
		@Override
		public WE_IReliefGenerator getReliefGenerator() {
			return biomeMapLayerNoise;
		}
	}
	
	/**
	 * Возвращает WE_Biome по идентификатору [i].
	 * @param i Идентификатор.
	 * @return WE_Biome - биом.
	 */
	@Override
	public WE_Biome getBiome(int i) {
		return biomes.get(i);
	}
	/**
	 * Возвращает WE_Biome по умолчанию (если условия в этом месте не подходят для какого-либо биома, то здесь устанавливается биом по умолчанию).
	 * @return WE_Biome - биом.
	 */
	@Override
	public WE_Biome getDefaultBiome() {
		return defaultBiomeId >= 0 && defaultBiomeId < sizeofBiomes() ? getBiome(defaultBiomeId) : (WE_Biome) Utils.WE_Biome_2;
		
	}
	/**
	 * Возвращает количество элементов в списке биомов: WE_Biome.
	 * @return Размер списка.
	 */
	@Override
	public int sizeofBiomes() {
		return biomes.size();
	}
	
	/**
	 * Возвращает IGenReliefLayer по идентификатору [i].
	 * @param i Идентификатор.
	 * @return IGenReliefLayer - слой.
	 */
	@Override
	public IGenReliefLayer getReliefLayer(int i) {
		return reliefLayers.get(i);
	}
	/**
	 * Возвращает количество элементов в списке слоев: IGenReliefLayer.
	 * @return Размер списка.
	 */
	@Override
	public int sizeofReliefLayers() {
		return reliefLayers.size();
	}
	
	/**
	 * Возвращает IGenBiomeMapLayer по идентификатору [i].
	 * @param i Идентификатор.
	 * @return IGenBiomeMapLayer - слой.
	 */
	@Override
	public IGenBiomeMapLayer getBiomeMapLayer(int i) {
		return bMapLayers.get(i);
	}
	/**
	 * Возвращает количество элементов в списке слоев: IGenBiomeMapLayer.
	 * @return Размер списка.
	 */
	@Override
	public int sizeofBiomeMapLayers() {
		return bMapLayers.size();
	}
}