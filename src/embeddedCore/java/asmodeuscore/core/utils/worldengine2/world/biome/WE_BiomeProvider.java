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

package asmodeuscore.core.utils.worldengine2.world.biome;

import com.google.common.collect.Lists;

import asmodeuscore.core.utils.Utils;
import asmodeuscore.core.utils.worldengine2.util.WE_RegionBuffer;
import asmodeuscore.core.utils.worldengine2.world.properties.WE_IWorldProperties;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.fml.common.registry.ForgeRegistries;


/**
 * Отвечает за распределение биомов в мире, сгенерированном WorldEngine.
 * @author VamigA
 */
public class WE_BiomeProvider extends BiomeProvider {
	/** Настройки мира. */
	public WE_IWorldProperties properties;
	
	/** Буфер биомов. Временно их хранит, так мы экономим такты процессора на расчетах. */
	public static WE_RegionBuffer<WE_Biome> smart = null;
	
	/**
	 * Конструктор.
	 * @param w Мир Minecraft.
	 * @param p Настройки мира.
	 */
	public WE_BiomeProvider(World w, WE_IWorldProperties p) {
		super(w.getWorldInfo());
        
		if(smart == null)
			smart = new WE_RegionBuffer<WE_Biome>(2048, this::getWEBiomeAt);
		
		properties = p;
		for(int l = 0; l < properties.sizeofReliefLayers  (); l++)
			properties.getReliefLayer  (l).getReliefGenerator().setSeed((long)Math.pow((double)l * 347932.0 + (double)w.getSeed() + 54279127.0, 21.0) * 714L);
		for(int l = 0; l < properties.sizeofBiomeMapLayers(); l++)
			properties.getBiomeMapLayer(l).getReliefGenerator().setSeed((long)Math.pow((double)l * 175954.0 + (double)w.getSeed() + 35974855.0, 17.0) * 523L);
		
		if(((WE_Biome)Utils.WE_Biome_2).forGame == null)
			if(ForgeRegistries.BIOMES.containsValue(Utils.WE_Biome_2))
				((WE_Biome)Utils.WE_Biome_2).forGame = Utils.WE_Biome_2;
			else
				((WE_Biome)Utils.WE_Biome_2).forGame = Biomes.PLAINS;
		if(properties.getDefaultBiome().forGame == null)
			if(ForgeRegistries.BIOMES.containsValue((Biome)properties.getDefaultBiome()))
				properties.getDefaultBiome().forGame = (Biome)properties.getDefaultBiome();
			else
				properties.getDefaultBiome().forGame =((WE_Biome)Utils.WE_Biome_2).forGame;
		for(int i = 0; i < properties.sizeofBiomes(); i++)
			if(properties.getBiome(i).forGame == null)
				if(ForgeRegistries.BIOMES.containsValue((Biome)properties.getBiome(i)))
					properties.getBiome(i).forGame = (Biome)properties.getBiome(i);
				else
					properties.getBiome(i).forGame = properties.getDefaultBiome().forGame;
	}
	
	/**
	 * Ванильная функция. Выдает самой игре ее биом.
	 * @param x Координата блока X, деленная на 4.
	 * @param y Координата блока Y, деленная на 4.
	 * @param z Координата блока Z, деленная на 4.
	 * @return Биом ванильного (Biome) типа.
	 */
	/*@Override
	public Biome getNoiseBiome(int x, int y, int z) {
		return smart.get((long)x * 4L, (long)z * 4L).forGame;
	}*/
	
	@Override
	public Biome getBiome(BlockPos pos)
	{
		return getBiome(pos, null);
	}
	
	@Override
	public Biome getBiome(BlockPos pos, Biome defaultBiome)
    {
        return smart.get((long)pos.getX() * 4L, (long)pos.getZ() * 4L).forGame;
    }
	
	/**
	 * Выдает генератору WorldEngine биом на координатах.
	 * @param x Координата блока X.
	 * @param z Координата блока Z.
	 * @return Биом типа WE_Biome.
	 */
	public WE_Biome getWEBiomeAt(long x, long z) {
		int[] noiseLayersData = new int[properties.sizeofBiomeMapLayers()];
		for(int i = 0; i < properties.sizeofBiomeMapLayers(); i++)
			noiseLayersData[i] = (int)Math.round(properties.getBiomeMapLayer(i).getReliefGenerator().genNoise2D((double)x, (double)z));
		
		for(int i = 0; i < properties.sizeofBiomes(); i++)
			if(properties.getBiome(i).properties.getLayersConditions().go(noiseLayersData))
				return properties.getBiome(i);
		
		return properties.getDefaultBiome();
	}

}