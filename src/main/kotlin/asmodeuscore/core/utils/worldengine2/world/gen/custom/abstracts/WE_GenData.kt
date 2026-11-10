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
package asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts

import asmodeuscore.core.utils.worldengine2.world.biome.WE_Biome
import asmodeuscore.core.utils.worldengine2.world.gen.WE_ChunkProvider
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkPrimer

/**
 * Класс с данными главного генератора для передачи их пользовательским генераторам первой стадии генерации чанков.
 * @author VamigA
 */
open class WE_GenData
/**
 * Конструктор.
 * @param gen Главный генератор чанков WorldEngine.
 * @param wld Сам генерируемый мир.
 * @param chk Сам генерируемый чанк.
 * @param bms Массив биомов чанка от WorldEngine.
 * @param rel Данные высот слоев рельефа в этом чанке.
 * @param bxp Локальная координата X блока в этом чанке.
 * @param byp Локальная координата Y блока в этом чанке.
 * @param bzp Локальная координата Z блока в этом чанке.
 */ constructor(
    /** Главный генератор чанков WorldEngine.  */
    @JvmField var provider: WE_ChunkProvider?,
    /** Сам генерируемый мир. Здесь можно взять World или Random.  */
    @JvmField var world: World?,
    @JvmField var primer: ChunkPrimer?,
    /** Сам генерируемый чанк. Отсюда можно установить блок или получить его на координатах, а также узнать биомы.  */
    @JvmField
var chunkX: Int,
    @JvmField
var chunkZ: Int,
    /** Массив биомов чанка от WorldEngine.  */
    @JvmField var biomes: Array<Array<WE_Biome?>?>?,
    /** Данные высот слоев рельефа в этом чанке.  */
    @JvmField var relief: Array<Array<IntArray?>?>?,
    /** Локальные координаты блока в этом чанке (0...15; Y: 0...255). Они не всегда используются.  */
    @JvmField
var bx: Int,
    @JvmField
var by: Int,
    @JvmField
var bz: Int
)
