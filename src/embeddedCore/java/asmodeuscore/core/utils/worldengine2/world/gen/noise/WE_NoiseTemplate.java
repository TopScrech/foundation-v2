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
 * Это шаблон для шумовых классов, вроде: WE_PerlinNoise или WE_ValueNoise.
 * @author VamigA
 */
public abstract class WE_NoiseTemplate implements WE_IReliefGenerator {
	/** Persistence - амплитудный множитель для каждой октавы относительно предыдущей. Scale(X...Y...Z) - множители масштаба всей волны. */
	public double persistence, scaleX, scaleY, scaleZ;
	/** Octaves - количество октав. Height - будет просто суммировано к конечному результату. */
	public int octaves, height;
	/** Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep). */
	public byte iType;
	
	/** Псевдослучайный генератор. */
	public WE_WhiteNoise rand;
	
	/**
	 * Конструктор.
	 * @param gSeed Семя.
	 * @param gPersistence Стойкость (амплитудный множитель для каждой октавы относительно предыдущей).
	 * @param numOfOctaves Количество октав.
	 * @param scl_x Множитель масштаба по X всей волны.
	 * @param scl_y Множитель масштаба по Y всей волны.
	 * @param scl_z Множитель масштаба по Z всей волны.
	 * @param sum Высота (будет просто суммировано к конечному результату).
	 * @param interpolation Используемая функция сглаживания значений (0 - нет; 1 - smoothstep; 2 - smootherstep).
	 */
	public WE_NoiseTemplate(long gSeed, double gPersistence, int numOfOctaves, double scl_x, double scl_y, double scl_z, int sum, byte interpolation) {
		persistence = gPersistence; octaves = numOfOctaves; scaleX = scl_x; scaleY = scl_y; scaleZ = scl_z; height = sum; iType = interpolation;
		rand = new WE_WhiteNoise(gSeed);
	}
	
	/**
	 * Возвращает семя генератора.
	 * @return Семя.
	 */
	@Override
	public long getSeed() {
		return rand.seed;
	}
	
	/**
	 * Задает семя генератора.
	 * @param seed Новое семя.
	 */
	@Override
	public void setSeed(long seed) {
		rand.seed = seed;
	}
	
	/**
	 * Задает параметры генератора на этом биоме.
	 * @param pers Параметр Persistence этого рельефного слоя на этом биоме.
	 * @param sclY Параметр ScaleY этого рельефного слоя на этом биоме.
	 * @param hght Параметр Height этого рельефного слоя на этом биоме.
	 */
	@Override
	public void setBiomeProperties(double pers, double sclY, int hght) {
		persistence = pers; scaleY = sclY; height = hght;
	}
	
	/**
	 * Выдает высоту на этом блоке (множественные вызовы этой функции и генерируют ландшафт).
	 * @param x Координата блока X.
	 * @param z Координата блока Z.
	 * @return Высота для карты высот.
	 */
	@Override
	public double genNoise2D(double x, double z) {
		double result = 0.0, amplitudeMultiplier = 1.0, nowX = x, nowZ = z;
		for(int i = 0; i < octaves; i++) {
			result += noiseOctave2D(nowX / scaleX, nowZ / scaleZ) * amplitudeMultiplier;
			amplitudeMultiplier *= persistence;
			nowX *= 2.0; nowZ *= 2.0;
		}
		return (double)height + result * scaleY;
	}
	
	/**
	 * Шумовая функция (главная). Генерирует шум или одну из октав шума.
	 * @param x Координата блока X.
	 * @param z Координата блока Z.
	 * @return Высота шума (или, точнее, октавы) на этом месте.
	 */
	public abstract double noiseOctave2D(double x, double z);
}