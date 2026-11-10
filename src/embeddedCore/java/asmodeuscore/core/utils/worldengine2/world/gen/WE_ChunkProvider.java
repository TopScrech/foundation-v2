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

package asmodeuscore.core.utils.worldengine2.world.gen;

import java.util.Arrays;
import java.util.List;

import asmodeuscore.core.utils.Utils;
import asmodeuscore.core.utils.worldengine2.world.biome.WE_Biome;
import asmodeuscore.core.utils.worldengine2.world.biome.WE_BiomeProvider;
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_GenData;
import asmodeuscore.core.utils.worldengine2.world.properties.WE_IWorldProperties;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;


/**
 * Главный генератор WorldEngine. Активирует другие, более мелкие, генераторы.
 * @param <C> Настройки генератора. Не используются в WorldEngine.
 * @author VamigA
 */
public class WE_ChunkProvider implements IChunkGenerator {
	
	private WE_IWorldProperties properties;
	private World world;
	/**
	 * Конструктор.
	 * @param w Мир Minecraft.
	 * @param p Настройки мира.
	 */
	public WE_ChunkProvider(World w, WE_IWorldProperties p) {
		//super(w, new WE_BiomeProvider(w, p), null);
		world = w;
		properties = p;
	}
	
	public WE_IWorldProperties getProperties() { return this.properties; }
	public World getWorld() { return this.world; }
	/**
	 * Первая стадия генерации чанков. Генерирует основной ландшафт.
	 * @param w Мир Minecraft.
	 * @param c Чанк Minecraft.
	 */
	
	@Override
	public Chunk generateChunk(int chunkX, int chunkZ) {
		 
		ChunkPrimer primer = new ChunkPrimer();		
		setBlocksInChunk(chunkX, chunkZ, primer);
		
		WE_BiomeProvider p = (WE_BiomeProvider)world.getBiomeProvider();
		WE_Biome[][] biomes = new WE_Biome[16][16];

		for(int x = 0; x < 16; x++)
			for(int z = 0; z < 16; z++)
				biomes[x][z] = p.smart.get(chunkX | (long)x, chunkZ | (long)z);
		
		int[][][] rData = new int[16][16][p.properties.sizeofReliefLayers() + 1];
		/*
		for(int i = 0; i < p.properties.sizeofCreateChunkGen(); i++)
			p.properties.getCreateChunkGen(i).generate(new WE_GenData(this, world, primer, chunkX, chunkZ, biomes, rData, 0, 0, 0));
		
		WE_Biome r = biomes[world.rand.nextInt(16)][world.rand.nextInt(16)];
		for(int i = 0; i < r.properties.sizeofCreateChunkGen(); i++)
			r.properties.getCreateChunkGen(i).generate(new WE_GenData(this, world, primer, chunkX, chunkZ, biomes, rData, 0, 0, 0));
		*/
		for(int x = 0; x < 16; x++)
			for(int z = 0; z < 16; z++) {
				
				for(int i = 0; i < p.properties.sizeofCreateChunkGenInXZ(); i++)
					p.properties.getCreateChunkGenInXZ(i).generate(new WE_GenData(this, world, primer, chunkX, chunkZ, biomes, rData, x, 0, z));
			/*
				for(int i = 0; i < biomes[x][z].properties.sizeofCreateChunkGenInXZ(); i++)
					biomes[x][z].properties.getCreateChunkGenInXZ(i).generate(new WE_GenData(this, world, primer, chunkX, chunkZ, biomes, rData, x, 0, z));
				
				for(int y = 0; y < 256; y++) {
					
					for(int i = 0; i < p.properties.sizeofCreateChunkGenInXYZ(); i++)
						p.properties.getCreateChunkGenInXYZ(i).generate(new WE_GenData(this, world, primer, chunkX, chunkZ, biomes, rData, x, y, z));
					
					for(int i = 0; i < biomes[x][z].properties.sizeofCreateChunkGenInXYZ(); i++)
						biomes[x][z].properties.getCreateChunkGenInXYZ(i).generate(new WE_GenData(this, world, primer, chunkX, chunkZ, biomes, rData, x, y, z));
				}
			*/
			}
		Chunk chunk = new Chunk(this.world, primer, chunkX, chunkZ);
		byte[] arraybiome = new byte[256];
		Arrays.fill(arraybiome, (byte) Biome.getIdForBiome(Utils.WE_Biome_2));
		chunk.setBiomeArray(arraybiome);
		
		chunk.generateSkylightMap();
        return chunk;
	}
	
	public void setBlocksInChunk(int x, int z, ChunkPrimer primer)
    {
        for (int i = 0; i < 4; ++i)
        {

            for (int l = 0; l < 4; ++l)
            {          

                for (int i2 = 0; i2 < 32; ++i2)
                {                  


                    for (int j2 = 0; j2 < 8; ++j2)
                    {                     

                        for (int k2 = 0; k2 < 4; ++k2)
                        {                           

                            for (int l2 = 0; l2 < 4; ++l2)
                            {
                            	if (i2 * 8 + j2 < 64)
                                {
                                    //primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, Blocks.WATER.getDefaultState());
                                }
                            }

                        }

                    }
                }
            }
        }
    }

	
	@Override
	public void populate(int chunkX, int chunkZ) {
		BlockFalling.fallInstantly =  true;
		int x = chunkX << 4;
        int z = chunkZ << 4;
		//-//
		boolean flag = false;
		world.rand.setSeed((long)chunkX * 434673L * world.rand.nextLong() + (long)chunkZ * 585694L * world.rand.nextLong() + 745849494845L);

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre (this, world, world.rand, chunkX, chunkZ, flag));
		
		/////
		//=//
		/////
		
		WE_BiomeProvider p = (WE_BiomeProvider)world.getBiomeProvider();
		WE_Biome[][] biomes = new WE_Biome[16][16];
		
		for(int xX = 0; xX < 16; xX++)
			for(int zZ = 0; zZ < 16; zZ++)
				biomes[xX][zZ] = p.smart.get(chunkX | (long)xX, chunkZ | (long)zZ);
		
		for(int i = 0; i < p.properties.sizeofDecorateChunkGen(); i++)
			p.properties.getDecorateChunkGen(i).generate(world.rand, x, z, world, this, world.getChunkProvider());
		
		WE_Biome r = biomes[world.rand.nextInt(16)][world.rand.nextInt(16)];
		for(int i = 0; i < r.properties.sizeofDecorateChunkGen(); i++)
			r.properties.getDecorateChunkGen(i).generate(world.rand, x, z, world, this, world.getChunkProvider());

		/*
		b.decorateBiome(worldObj, rand, x, z);
		wp.onPopulate(chunkX, chunkZ);*/
		WorldEntitySpawner.performWorldGenSpawning(this.world, r, x + 8, z + 8, 16, 16, world.rand);

		
		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(this, world, world.rand, chunkX, chunkZ, flag));
		//-//
		BlockFalling.fallInstantly = false;
	}
	
	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		//WE_Biome biome = WE_Biome.getBiomeAt(this, pos.getX(), pos.getZ());
		WE_Biome biome = (WE_Biome) world.getBiomeProvider().getBiome(pos);
		//WE_Biome biome = WE_Biome.getNoiseBiome(pos.getX(), pos.getZ());
		
		if(biome != null) 
			return biome.getSpawnableList(creatureType);				
		
		return null;
	}
	
	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}
	
	@Override
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
		return null;
	}
	
	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		return false;
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		
	}	
}