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

package asmodeuscore.core.utils.worldengine2.world.gen.custom;

import asmodeuscore.core.utils.worldengine2.world.biome.WE_BiomeProvider;
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_CreateChunkGen_InXZ;
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_GenData;
import asmodeuscore.core.utils.worldengine2.world.gen.noise.WE_PerlinNoise;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

/**
 * Основной стандартный пользовательский генератор WorldEngine: генератор ландшафта!
 * Создает карты высоты (GenReliefLayer), а затем устанавливает каменные блоки с водой на основе этих данных карт.
 * @author VamigA
 */
public class WE_TerrainGenerator extends WE_CreateChunkGen_InXZ {
	
	public IBlockState worldStoneBlock = Blocks.STONE.getDefaultState();
	public IBlockState worldSeaGenBlock = Blocks.WATER.getDefaultState();
	
	public boolean worldSeaGen = true;
	public int worldSeaGenMaxY = 63;
	/**
	 * Функция генерации.
	 * @param d Нынешние данные генератора.
	 */
	@Override
	public void generate(WE_GenData d) {
		WE_BiomeProvider we_provider = ((WE_BiomeProvider)d.provider.getWorld().getBiomeProvider());
		int j = 0;
		//for(int j = 0; j < we_provider.properties.sizeofReliefLayers(); j++) {
			for(int i = 0; i < we_provider.properties.sizeofBiomes(); i++) {
				if(getBiome(d) == we_provider.properties.getBiome(i)) {
					WE_PerlinNoise noise = ((WE_PerlinNoise)we_provider.properties.getReliefLayer(j).getReliefGenerator());

					noise.persistence = getBiome(d).properties.getTerrainProps(0).getPersistence();
					noise.scaleY = getBiome(d).properties.getTerrainProps(0).getScaleY();
					noise.height = getBiome(d).properties.getTerrainProps(0).getHeight();
										
					int x = d.chunkX << 4;
					int z = d.chunkZ << 4;
					
					int h = (int) Math.min(noise.genNoise2D(x + d.bx, z + d.bz), 255);
					
					for(int y = 0; y < 255; y++)
					{
						if((y == 0) || (y <= 5 && d.provider.getWorld().rand.nextInt(y) == 0)) {
							setBlockPrimer(d, Blocks.BEDROCK.getDefaultState(), y);	
							continue;
						}
							if(y <= h)
								setBlockPrimer(d, worldStoneBlock, y);
							//else if(worldSeaGen && y <= worldSeaGenMaxY)			
								//setBlockPrimer(d, worldSeaGenBlock, y);	
					}
				}
			}
		//}
		/*
		int h = (int)((WE_PerlinNoise)we_provider.properties.getReliefLayer(0).getReliefGenerator()).genNoise2D((d.chunkX << 4)+ d.bx, (d.chunkZ << 4) + d.bz);
		h = Math.min(h, 255);
		*/
		
					
			
		
	}
}