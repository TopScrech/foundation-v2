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

import asmodeuscore.core.utils.worldengine2.util.WE_Interpolation;

/**
 * Класс шума Перлина WorldEngine.
 * Подробнее: https://en.wikipedia.org/wiki/Perlin_noise; https://habr.com/post/342906/; https://habr.com/post/265775/.
 * @author VamigA
 */
public class WE_PerlinNoise extends WE_NoiseTemplate {
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
	public WE_PerlinNoise(long gSeed, double gPersistence, int numOfOctaves, double scl_x, double scl_y, double scl_z, int sum, byte interpolation) {
		super(gSeed, gPersistence, numOfOctaves, scl_x, scl_y, scl_z, sum, interpolation);
		rand.makeSmartForVectors();
	}
	
	/**
	 * Скалярное произведение векторов. Подробнее: https://en.wikipedia.org/wiki/Dot_product.
	 * @param vec1 Вектор 1.
	 * @param vec2 Вектор 2.
	 */
	public Double dot2D(Double[] vec1, Double[] vec2) {
		return vec1[0] * vec2[0] + vec1[1] * vec2[1];
	}
	
	/**
	 * Шумовая функция (главная). Генерирует шум или одну из октав шума.
	 * @param x Координата блока X.
	 * @param z Координата блока Z.
	 * @return Высота шума (или, точнее, октавы) на этом месте.
	 */
	@Override
	public double noiseOctave2D(double x, double z) {
		long squareStartX = (long)x, squareStartZ = (long)z, xs = 1L, zs = 1L; if(Math.abs(x) != x) xs = -1L; if(Math.abs(z) != z) zs = -1L;
		double pointInQuadX = Math.abs(x) - (double)Math.abs(squareStartX), pointInQuadZ = Math.abs(z) - (double)Math.abs(squareStartZ);
		
		Double[]
		topLeft     = rand.smartVec.get(squareStartX     , squareStartZ     ),
		topRight    = rand.smartVec.get(squareStartX + xs, squareStartZ     ),
		bottomLeft  = rand.smartVec.get(squareStartX     , squareStartZ + zs),
		bottomRight = rand.smartVec.get(squareStartX + xs, squareStartZ + zs),
		
		distanceToTopLeft     = new Double[] { (double)xs * pointInQuadX             , (double)zs * pointInQuadZ              },
		distanceToTopRight    = new Double[] { (double)xs * pointInQuadX - (double)xs, (double)zs * pointInQuadZ              },
		distanceToBottomLeft  = new Double[] { (double)xs * pointInQuadX             , (double)zs * pointInQuadZ - (double)zs },
		distanceToBottomRight = new Double[] { (double)xs * pointInQuadX - (double)xs, (double)zs * pointInQuadZ - (double)zs };
		
		Double
		dotTopLeft     = dot2D(topLeft    , distanceToTopLeft    ),
		dotTopRight    = dot2D(topRight   , distanceToTopRight   ),
		dotBottomLeft  = dot2D(bottomLeft , distanceToBottomLeft ),
		dotBottomRight = dot2D(bottomRight, distanceToBottomRight);
		
		return WE_Interpolation.bilinearInterpolation2D(dotTopLeft, dotTopRight, dotBottomLeft, dotBottomRight, pointInQuadX, pointInQuadZ, iType);
	}
}