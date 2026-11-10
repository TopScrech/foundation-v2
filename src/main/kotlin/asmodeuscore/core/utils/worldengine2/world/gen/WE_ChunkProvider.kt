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
package asmodeuscore.core.utils.worldengine2.world.gen

import java.util.Arrays
import net.minecraft.init.Blocks
import net.minecraft.world.biome.Biome.SpawnListEntry

import asmodeuscore.core.utils.Utils
import asmodeuscore.core.utils.worldengine2.world.biome.WE_Biome
import asmodeuscore.core.utils.worldengine2.world.biome.WE_BiomeProvider
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_GenData
import asmodeuscore.core.utils.worldengine2.world.properties.WE_IWorldProperties
import net.minecraft.block.BlockFalling
import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldEntitySpawner
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.terraingen.PopulateChunkEvent
import java.util.*

/**
 * Главный генератор WorldEngine. Активирует другие, более мелкие, генераторы.
 * @param <C> Настройки генератора. Не используются в WorldEngine.
 * @author VamigA
</C> */
open class WE_ChunkProvider//super(w, new WE_BiomeProvider(w, p), null);
/**
 * Конструктор.
 * @param w Мир Minecraft.
 * @param p Настройки мира.
 */ constructor(private val world: World, private val properties: WE_IWorldProperties?) : IChunkGenerator {
    open fun getProperties(): WE_IWorldProperties? {
        return this.properties
    }

    open fun getWorld(): World? {
        return this.world
    }

    /**
     * Первая стадия генерации чанков. Генерирует основной ландшафт.
     * @param w Мир Minecraft.
     * @param c Чанк Minecraft.
     */
    override fun generateChunk(chunkX: Int, chunkZ: Int): Chunk {
        val primer: ChunkPrimer = ChunkPrimer()
        setBlocksInChunk(chunkX, chunkZ, primer)

        val p: WE_BiomeProvider = world.getBiomeProvider() as WE_BiomeProvider
        val biomes: Array<Array<WE_Biome?>?> = Array<Array<WE_Biome?>?>(16) { arrayOfNulls<WE_Biome>(16) }

        for (x: Int in 0..15) for (z: Int in 0..15) biomes[x]!![z] =
            (WE_BiomeProvider.Companion.smart)!!.get(chunkX.toLong() or x.toLong(), chunkZ.toLong() or z.toLong())

        val rData: Array<Array<IntArray?>?> =
            Array<Array<IntArray?>?>(16) { Array<IntArray?>(16) { IntArray(p.properties.sizeofReliefLayers() + 1) } }
        /*
		for(int i = 0; i < p.properties.sizeofCreateChunkGen(); i++)
			p.properties.getCreateChunkGen(i).generate(new WE_GenData(this, world, primer, chunkX, chunkZ, biomes, rData, 0, 0, 0));
		
		WE_Biome r = biomes[world.rand.nextInt(16)][world.rand.nextInt(16)];
		for(int i = 0; i < r.properties.sizeofCreateChunkGen(); i++)
			r.properties.getCreateChunkGen(i).generate(new WE_GenData(this, world, primer, chunkX, chunkZ, biomes, rData, 0, 0, 0));
		*/
        for (x: Int in 0..15) for (z: Int in 0..15) {
            for (i: Int in 0..<p.properties.sizeofCreateChunkGenInXZ()) (p.properties.getCreateChunkGenInXZ(i))!!
                .generate(WE_GenData(this, world, primer, chunkX, chunkZ, biomes, rData, x, 0, z))
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
        val chunk: Chunk = Chunk(this.world, primer, chunkX, chunkZ)
        val arraybiome: ByteArray = ByteArray(256)
        Arrays.fill(arraybiome, Biome.getIdForBiome(Utils.WE_Biome_2).toByte())
        chunk.setBiomeArray(arraybiome)

        chunk.generateSkylightMap()
        return chunk
    }

    open fun setBlocksInChunk(x: Int, z: Int, primer: ChunkPrimer?) {
        for (i: Int in 0..3) {
            for (l: Int in 0..3) {
                for (i2: Int in 0..31) {
                    for (j2: Int in 0..7) {
                        for (k2: Int in 0..3) {
                            for (l2: Int in 0..3) {
                                if (i2 * 8 + j2 < 64) {
                                    //primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, Blocks.WATER.getDefaultState());
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    override fun populate(chunkX: Int, chunkZ: Int) {
        BlockFalling.fallInstantly = true
        val x: Int = chunkX shl 4
        val z: Int = chunkZ shl 4
        //-//
        val flag: Boolean = false
        world.rand.setSeed(chunkX.toLong() * 434673L * world.rand.nextLong() + chunkZ.toLong() * 585694L * world.rand.nextLong() + 745849494845L)

        MinecraftForge.EVENT_BUS.post(PopulateChunkEvent.Pre(this, world, world.rand, chunkX, chunkZ, flag))


        /////
        //=//
        /////
        val p: WE_BiomeProvider = world.getBiomeProvider() as WE_BiomeProvider
        val biomes: Array<Array<WE_Biome>?> = Array<Array<WE_Biome>?>(16) { (arrayOfNulls<WE_Biome>(16) as Array<WE_Biome>?) }

        for (xX: Int in 0..15) for (zZ: Int in 0..15) biomes[xX]!![zZ] =
            ((WE_BiomeProvider.Companion.smart)!!.get(chunkX.toLong() or xX.toLong(), chunkZ.toLong() or zZ.toLong()))!!

        for (i: Int in 0..<p.properties.sizeofDecorateChunkGen()) (p.properties.getDecorateChunkGen(i))!!
            .generate(world.rand, x, z, world, this, world.getChunkProvider())

        val r: WE_Biome? = biomes[world.rand.nextInt(16)]!![world.rand.nextInt(16)]
        for (i: Int in 0..<r!!.properties.sizeofDecorateChunkGen()) (r.properties.getDecorateChunkGen(i))!!
            .generate(world.rand, x, z, world, this, world.getChunkProvider())

        /*
		b.decorateBiome(worldObj, rand, x, z);
		wp.onPopulate(chunkX, chunkZ);*/
        WorldEntitySpawner.performWorldGenSpawning(this.world, r, x + 8, z + 8, 16, 16, world.rand)


        MinecraftForge.EVENT_BUS.post(PopulateChunkEvent.Post(this, world, world.rand, chunkX, chunkZ, flag))
        //-//
        BlockFalling.fallInstantly = false
    }

    override fun getPossibleCreatures(
        creatureType: EnumCreatureType,
        pos: BlockPos
    ): MutableList<Biome.SpawnListEntry?>? {
        //WE_Biome biome = WE_Biome.getBiomeAt(this, pos.getX(), pos.getZ());
        val biome: WE_Biome = world.getBiomeProvider().getBiome(pos) as WE_Biome

        //WE_Biome biome = WE_Biome.getNoiseBiome(pos.getX(), pos.getZ());
        if (biome != null) return biome.getSpawnableList(creatureType)

        return null
    }

    override fun generateStructures(chunkIn: Chunk, x: Int, z: Int): Boolean {
        return false
    }

    override fun getNearestStructurePos(
        worldIn: World,
        structureName: String,
        position: BlockPos,
        findUnexplored: Boolean
    ): BlockPos? {
        return null
    }

    override fun isInsideStructure(worldIn: World, structureName: String, pos: BlockPos): Boolean {
        return false
    }

    override fun recreateStructures(chunkIn: Chunk, x: Int, z: Int) {
    }
}
