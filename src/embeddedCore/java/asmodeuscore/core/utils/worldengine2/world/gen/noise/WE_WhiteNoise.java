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

import asmodeuscore.core.utils.worldengine2.util.WE_RegionBuffer;
import net.minecraft.util.math.MathHelper;

/**
 * Отвечает за псевдослучайную генерацию белого шума. Из этих данных и исходят генераторы WorldEngine при работе.
 * Возвращает дробные числа в диапазоне от -1.0 до 1.0 на основании лишь координат X и Z, а также "семени".
 * При одинаковых входных данных выходные данные всегда останутся теми же - это очень важно.
 * @author VamigA
 */
public class WE_WhiteNoise {
	/** Семя - число, на основании которого и ведутся расчеты. */
	public long seed;
	
	/** Буфер значений от функции gen2D(x, z). Временно их хранит, так мы экономим такты процессора на расчетах. */
	public WE_RegionBuffer<Double  > smartVal;
	/** Буфер векторов от функции vecGen2D(x, z). Временно их хранит, так мы экономим такты процессора на расчетах. */
	public WE_RegionBuffer<Double[]> smartVec;
	
	/**
	 * Конструктор.
	 * @param genSeed Семя.
	 */
	public WE_WhiteNoise(long genSeed) {
		seed = (long)Math.pow(genSeed, 11L) * 171L + 51484313L;
	}
	
	/**
	 * Главная функция - рассчитывает и выводит неизменное псевдослучайное число на основании входных данных.
	 * @param x Координата X в мире, типа Long.
	 * @param z Координата Z в мире, типа Long.
	 * @return Псевдослучайное число типа Double в диапазоне от -1.0 до 1.0.
	 */
	public Double gen2D(long x, long z) {
		long n = seed + x * 4L + z * 341L; n = (n << 13L) ^ n;
	    return 1.0 - (double)((n * (n * n * 15731L + 789221L) + 1376312589L) & 2147483647L) / 1073741824.0;
	}
	
	/**
	 * Преобразует результат предыдущей функции в угол; выдает координаты единичного вектора, наклоненного под этим углом.
	 * @param x Координата X в мире, типа Long.
	 * @param z Координата Z в мире, типа Long.
	 * @return Координаты X и Z (определяют лишь его наклон) вектора длиной 1 в массиве типа Double[2].
	 */
	public Double[] vecGen2D(long x, long z) {
		double angle = gen2D(x, z) * Math.PI;
		return new Double[] { (double)MathHelper.cos((float)angle), (double)MathHelper.sin((float)angle) };
	}
	
	/** Добавляет буфер для временного хранения значений. */
	public void makeSmartForValues () {
		smartVal = new WE_RegionBuffer<Double  >(1024, this::gen2D   );
	}
	
	/** Добавляет буфер для временного хранения векторов. */
	public void makeSmartForVectors() {
		smartVec = new WE_RegionBuffer<Double[]>(1024, this::vecGen2D);
	}
}