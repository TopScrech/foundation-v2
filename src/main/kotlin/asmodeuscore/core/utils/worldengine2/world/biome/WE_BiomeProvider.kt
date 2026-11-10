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
package asmodeuscore.core.utils.worldengine2.world.biome

import asmodeuscore.core.utils.Utils
import asmodeuscore.core.utils.worldengine2.util.ISmartMathFunction
import asmodeuscore.core.utils.worldengine2.util.WE_RegionBuffer
import asmodeuscore.core.utils.worldengine2.world.properties.WE_IWorldProperties
import net.minecraft.init.Biomes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeProvider
import net.minecraftforge.fml.common.registry.ForgeRegistries
import kotlin.math.pow

/**
 * Отвечает за распределение биомов в мире, сгенерированном WorldEngine.
 * @author VamigA
 */
open class WE_BiomeProvider constructor(w: World, p: WE_IWorldProperties) : BiomeProvider(w.getWorldInfo()) {
    /** Настройки мира.  */
    @JvmField
    var properties: WE_IWorldProperties

    /**
     * Конструктор.
     * @param w Мир Minecraft.
     * @param p Настройки мира.
     */
    init {
        if (smart == null) smart =
            WE_RegionBuffer<WE_Biome?>(2048, ISmartMathFunction { x: Long, z: Long -> this.getWEBiomeAt(x, z) })

        properties = p
        for (l: Int in 0..<properties.sizeofReliefLayers()) ((properties.getReliefLayer(l))!!.getReliefGenerator())!!
            .setSeed((l.toDouble() * 347932.0 + w.getSeed().toDouble() + 54279127.0).pow(21.0).toLong() * 714L)
        for (l: Int in 0..<properties.sizeofBiomeMapLayers()) ((properties.getBiomeMapLayer(l))!!.getReliefGenerator())!!
            .setSeed((l.toDouble() * 175954.0 + w.getSeed().toDouble() + 35974855.0).pow(17.0).toLong() * 523L)

        if ((Utils.WE_Biome_2 as WE_Biome).forGame == null) if (ForgeRegistries.BIOMES.containsValue(Utils.WE_Biome_2)) (Utils.WE_Biome_2 as WE_Biome).forGame =
            Utils.WE_Biome_2
        else (Utils.WE_Biome_2 as WE_Biome).forGame = Biomes.PLAINS
        if ((properties.getDefaultBiome())!!.forGame == null) if (ForgeRegistries.BIOMES.containsValue(properties.getDefaultBiome() as Biome?)) (properties.getDefaultBiome())!!.forGame =
            properties.getDefaultBiome() as Biome?
        else (properties.getDefaultBiome())!!.forGame = (Utils.WE_Biome_2 as WE_Biome).forGame
        for (i: Int in 0..<properties.sizeofBiomes()) if ((properties.getBiome(i))!!.forGame == null) if (ForgeRegistries.BIOMES.containsValue(
                properties.getBiome(i) as Biome?
            )
        ) (properties.getBiome(i))!!.forGame = properties.getBiome(i) as Biome?
        else (properties.getBiome(i))!!.forGame = (properties.getDefaultBiome())!!.forGame
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
    override fun getBiome(pos: BlockPos): Biome? {
        return getBiome(pos, null)
    }

    override fun getBiome(pos: BlockPos, defaultBiome: Biome?): Biome {
        return (smart!!.get(
            pos.getX().toLong() * 4L,
            pos.getZ().toLong() * 4L
        )!!.forGame)!!
    }

    /**
     * Выдает генератору WorldEngine биом на координатах.
     * @param x Координата блока X.
     * @param z Координата блока Z.
     * @return Биом типа WE_Biome.
     */
    open fun getWEBiomeAt(x: Long, z: Long): WE_Biome? {
        val noiseLayersData: IntArray = IntArray(properties.sizeofBiomeMapLayers())
        for (i: Int in 0..<properties.sizeofBiomeMapLayers()) noiseLayersData[i] =
            Math.round(((properties.getBiomeMapLayer(i))!!.getReliefGenerator())!!.genNoise2D(x.toDouble(), z.toDouble()))
                .toInt()

        for (i: Int in 0..<properties.sizeofBiomes()) if (((properties.getBiome(i))!!.properties.getLayersConditions())!!
                .go(noiseLayersData)
        ) return properties.getBiome(i)

        return properties.getDefaultBiome()
    }

    companion object {
        /** Буфер биомов. Временно их хранит, так мы экономим такты процессора на расчетах.  */
        @JvmField
        var smart: WE_RegionBuffer<WE_Biome?>? = null
    }
}
