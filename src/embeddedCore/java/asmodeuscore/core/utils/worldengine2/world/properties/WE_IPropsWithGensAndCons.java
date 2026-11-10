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


import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_ICreateChunkGen;
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_ICreateChunkGen_InXYZ;
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_ICreateChunkGen_InXZ;
import net.minecraftforge.fml.common.IWorldGenerator;

/**
 * ИНТЕРФЕЙС. Основа для настроек (мира и биома) со списками генераторов и с условиями генерации.
 * @author VamigA
 */
public interface WE_IPropsWithGensAndCons {
	/**
	 * Возвращает WE_ICreateChunkGen по идентификатору [i].
	 * @param i Идентификатор.
	 * @return WE_ICreateChunkGen - генератор.
	 */
	WE_ICreateChunkGen getCreateChunkGen(int i);
	/**
	 * Возвращает количество элементов в списке генераторов: WE_ICreateChunkGen.
	 * @return Размер списка.
	 */
	int sizeofCreateChunkGen();
	
	/**
	 * Возвращает WE_ICreateChunkGen_InXZ по идентификатору [i].
	 * @param i Идентификатор.
	 * @return WE_ICreateChunkGen_InXZ - генератор.
	 */
	WE_ICreateChunkGen_InXZ getCreateChunkGenInXZ(int i);
	/**
	 * Возвращает количество элементов в списке генераторов: WE_ICreateChunkGen_InXZ.
	 * @return Размер списка.
	 */
	int sizeofCreateChunkGenInXZ();
	
	/**
	 * Возвращает WE_ICreateChunkGen_InXYZ по идентификатору [i].
	 * @param i Идентификатор.
	 * @return WE_ICreateChunkGen_InXYZ - генератор.
	 */
	WE_ICreateChunkGen_InXYZ getCreateChunkGenInXYZ(int i);
	/**
	 * Возвращает количество элементов в списке генераторов: WE_ICreateChunkGen_InXYZ.
	 * @return Размер списка.
	 */
	int sizeofCreateChunkGenInXYZ();
	
	/**
	 * Возвращает Feature<?> по идентификатору [i].
	 * @param i Идентификатор.
	 * @return Feature<?> - генератор.
	 */
	IWorldGenerator getDecorateChunkGen(int i);
	/**
	 * Возвращает количество элементов в списке генераторов: Feature<?>.
	 * @return Размер списка.
	 */
	int sizeofDecorateChunkGen();
	
	/**
	 * Возвращает IGenReliefConditions для этого мира (для этих настроек).
	 * @return IGenReliefConditions - условия генерации.
	 */
	IGenReliefConditions getLayersConditions();
	/**
	 * ИНТЕРФЕЙС. Этот класс говорит генератору, когда и где он должен сгенерировать ландшафт или биом (динамическая версия конструкции "if").
	 * @author VamigA
	 */
	interface IGenReliefConditions {
		/**
		 * Генератор вызывает это с параметром. Функция должна сказать, следует ли генератору сгенерировать биом или блок камня при таких входных данных. Для генерации нужно выдать true.
		 * @param rLayersDataFromGen Данные высоты слоев рельефа или биом-карты. Индексы этого массива означают идентификаторы этого слоя рельефа или биом-карты.
		 *        В WE_WorldProperties еще одна ячейка массива в конце (rLayersDataFromGen[количество_слоев]) используется для нынешней высоты генерации.
		 * @return Право генерировать (true/false). Если true, то генератор сгенерирует биом или блок камня тут.
		 */
		boolean go(int[] rLayersDataFromGen);
	}
}