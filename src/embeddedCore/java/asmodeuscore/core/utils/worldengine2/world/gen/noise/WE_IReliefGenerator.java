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

package asmodeuscore.core.utils.worldengine2.world.gen.noise;

/**
 * Интерфейс для генераторов рельефа (шум значений, шум Перлина и так далее).
 * @author VamigA
 */
public interface WE_IReliefGenerator {
	/**
	 * Возвращает семя генератора.
	 * @return Семя.
	 */
	long getSeed();
	
	/**
	 * Задает семя генератора.
	 * @param seed Новое семя.
	 */
	void setSeed(long seed);
	
	/**
	 * Задает параметры генератора на этом биоме.
	 * @param pers Параметр Persistence этого рельефного слоя на этом биоме.
	 * @param sclY Параметр ScaleY этого рельефного слоя на этом биоме.
	 * @param hght Параметр Height этого рельефного слоя на этом биоме.
	 */
	void setBiomeProperties(double pers, double sclY, int hght);
	
	/**
	 * Выдает высоту на этом блоке (множественные вызовы этой функции и генерируют ландшафт).
	 * @param x Координата блока X.
	 * @param z Координата блока Z.
	 * @return Высота для карты высот.
	 */
	double genNoise2D(double x, double z);
}