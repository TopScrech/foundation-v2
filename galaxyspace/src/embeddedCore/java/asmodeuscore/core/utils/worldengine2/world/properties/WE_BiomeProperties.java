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

import net.minecraft.world.biome.Biome.BiomeProperties;


/**
 * Класс с пользовательскими настройками биома, генератор читает их.
 * @author VamigA
 */
public class WE_BiomeProperties extends WE_AbstactProperties implements WE_IBiomeProperties {
	/** Ванильный мусор. */
	public BiomeProperties vanillaShit;
	
	/**
	 * Список со свойствами рельефа в этом биоме для каждого рельефного слоя в классе WE_WorldProperties.
	 * layersTProps[i] здесь будет применен для reliefLayers[i] в классе WE_WorldProperties. Но можно создать и меньше свойств рельефа, чем самих слоев.
	 */
	public ArrayList<ReliefLayerTerrainPropertiesInBiome> layersTProps;
	
	/** Конструктор. Не забудьте позже отметить условия генерации биома в genConditions здесь (суперкласс WE_AbstactProperties содержит его). */
	public WE_BiomeProperties() {
		layersTProps = new ArrayList<ReliefLayerTerrainPropertiesInBiome>();
		/*
		RainType rt;
		switch(CommonConfig.cfgWEBiomePRType  .get()) {
			default:
			case "none": rt = RainType.NONE; break;
			case "rain": rt = RainType.RAIN; break;
			case "snow": rt = RainType.SNOW; break;
		}
		
		Category c ;
		switch(CommonConfig.cfgWEBiomePCategor.get()) {
			default:
			case "none"         : c = Category.NONE         ; break;
			case "taiga"        : c = Category.TAIGA        ; break;
			case "extreme_hills": c = Category.EXTREME_HILLS; break;
			case "jungle"       : c = Category.JUNGLE       ; break;
			case "mesa"         : c = Category.MESA         ; break;
			case "plains"       : c = Category.PLAINS       ; break;
			case "savanna"      : c = Category.SAVANNA      ; break;
			case "icy"          : c = Category.ICY          ; break;
			case "the_end"      : c = Category.THEEND       ; break;
			case "beach"        : c = Category.BEACH        ; break;
			case "forest"       : c = Category.FOREST       ; break;
			case "ocean"        : c = Category.OCEAN        ; break;
			case "desert"       : c = Category.DESERT       ; break;
			case "river"        : c = Category.RIVER        ; break;
			case "swamp"        : c = Category.SWAMP        ; break;
			case "mushroom"     : c = Category.MUSHROOM     ; break;
			case "nether"       : c = Category.NETHER       ; break;
		}
		*/
		vanillaShit = new BiomeProperties("-=|WorldEngine 2|=-");
		/*vanillaShit
		vanillaShit.surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG);
		vanillaShit.precipitation (rt                                                          );
		vanillaShit.category      (c                                                           );
		vanillaShit.depth         (CommonConfig.cfgWEBiomePBaseHei.get().floatValue()          );
		vanillaShit.scale         (CommonConfig.cfgWEBiomePHVariat.get().floatValue()          );
		vanillaShit.temperature   (CommonConfig.cfgWEBiomePTemp   .get().floatValue()          );
		vanillaShit.downfall      (CommonConfig.cfgWEBiomePRain   .get().floatValue()          );
		vanillaShit.waterColor    (CommonConfig.cfgWEBiomePWColor .get()                       );
		vanillaShit.waterFogColor (CommonConfig.cfgWEBiomePWFColor.get()                       );
		vanillaShit.parent        (CommonConfig.cfgWEBiomePBRgName.get()                       );*/
	}
	
	/**
	 * Добавляет свойства рельефа в этом биоме в список.
	 * @param doInter Сглаживать ли рельеф на территории ЭТОГО БИОМА на стыках между другими биомами?
	 * @param pers Параметр Persistence этого рельефного слоя на этом биоме.
	 * @param sclY Параметр ScaleY этого рельефного слоя на этом биоме.
	 * @param hght Параметр Height этого рельефного слоя на этом биоме.
	 */
	public void add(boolean doInter, double pers, double sclY, int hght) {
		layersTProps.add(new ReliefLayerTerrainPropertiesInBiome(doInter, pers, sclY, hght));
	}
	/** Очищает список свойств рельефа в этом биоме. */
	public void clear() {
		layersTProps.clear();
	}
	
	/**
	 * Настройки рельефного слоя (GenReliefLayer) в этом биоме.
	 * @author VamigA
	 */
	public class ReliefLayerTerrainPropertiesInBiome implements IReliefLayerTerrainPropertiesInBiome {
		/** Сглаживать ли рельеф на территории ЭТОГО БИОМА на стыках между другими биомами? */
		public boolean interWithAnotherBiome;
		/** Параметры Persistence и ScaleY этого рельефного слоя на этом биоме. */
		public double persistence, scaleY;
		/** Параметр Height этого рельефного слоя на этом биоме. */
		public int height;
		
		/**
		 * Конструктор.
		 * @param doInter Сглаживать ли рельеф на территории ЭТОГО БИОМА на стыках между другими биомами?
		 * @param pers Параметр Persistence этого рельефного слоя на этом биоме.
		 * @param sclY Параметр ScaleY этого рельефного слоя на этом биоме.
		 * @param hght Параметр Height этого рельефного слоя на этом биоме.
		 */
		public ReliefLayerTerrainPropertiesInBiome(boolean doInter, double pers, double sclY, int hght) {
			interWithAnotherBiome = doInter; persistence = pers; scaleY = sclY; height = hght;
		}
		
		/**
		 * Генератор сглаживает рельеф на стыках между биомами путем интерполяции (смотрите: GenReliefLayer).
		 * Если выдаст false, генератор не изменит рельефа на территории ЭТОГО БИОМА на стыках между другими биомами.
		 * Если выдаст true, генератор также сгладит рельеф на территории ЭТОГО БИОМА на стыках между другими биомами.
		 * @return Этот параметр.
		 */
		@Override
		public boolean getInterWithAnotherBiome() {
			return interWithAnotherBiome;
		}
		
		/**
		 * Параметр Persistence этого рельефного слоя на этом биоме.
		 * @return Этот параметр.
		 */
		@Override
		public double getPersistence() {
			return persistence;
		}
		/**
		 * Параметр ScaleY этого рельефного слоя на этом биоме.
		 * @return Этот параметр.
		 */
		@Override
		public double getScaleY() {
			return scaleY;
		}
		/**
		 * Параметр Height этого рельефного слоя на этом биоме.
		 * @return Этот параметр.
		 */
		@Override
		public int getHeight() {
			return height;
		}
	}
	
	/**
	 * Возвращает ванильный объект класса BiomeProperties (если нужно изменить цвета, дать название...).
	 * @return Ванильный мусор.
	 */
	@Override
	public BiomeProperties getVanillaBiomeProperties() {
		return vanillaShit;
	}
	
	/**
	 * Возвращает IReliefLayerTerrainPropertiesInBiome по идентификатору [i].
	 * @param i Идентификатор.
	 * @return IReliefLayerTerrainPropertiesInBiome - настройки.
	 */
	@Override
	public IReliefLayerTerrainPropertiesInBiome getTerrainProps(int i) {
		return layersTProps.get(i);
	}
	/**
	 * Возвращает количество элементов в списке настроек: IReliefLayerTerrainPropertiesInBiome.
	 * @return Размер списка.
	 */
	@Override
	public int sizeofTerrainProps() {
		return layersTProps.size();
	}
}