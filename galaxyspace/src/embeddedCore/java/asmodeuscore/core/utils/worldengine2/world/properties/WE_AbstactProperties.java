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

import java.util.ArrayList;

import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_ICreateChunkGen;
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_ICreateChunkGen_InXYZ;
import asmodeuscore.core.utils.worldengine2.world.gen.custom.abstracts.WE_ICreateChunkGen_InXZ;
import net.minecraftforge.fml.common.IWorldGenerator;


/**
 * Супер-класс для классов WE_WorldProperties и WE_BiomeProperties. Содержит необходимый для них класс динамических условий - GenReliefConditions.
 * 1) GenReliefConditions в WE_WorldProperties говорит генератору, когда и где он должен сгенерировать ландшафт по высоте слоев рельефа.
 * 2) GenReliefConditions в WE_BiomeProperties говорит генератору, когда и где он должен сгенерировать биом по высоте слоев биом-карты.
 * @author VamigA
 */
public abstract class WE_AbstactProperties implements WE_IPropsWithGensAndCons {
	/** Переменная с классом GenReliefConditions. Класс говорит генератору, когда и где он должен сгенерировать блок камня или биом. */
	public GenReliefConditions genConditions;
	
	/** Первая стадия генерации чанков: генераторы на каждый чанк. */
	public ArrayList<WE_ICreateChunkGen      > createChunkGen_List      ;
	/** Первая стадия генерации чанков: генераторы на каждый 256-блоковый слой [X, Z]. */
	public ArrayList<WE_ICreateChunkGen_InXZ > createChunkGen_InXZ_List ;
	/** Первая стадия генерации чанков: генераторы на каждый блок [X, Y, Z]. */
	public ArrayList<WE_ICreateChunkGen_InXYZ> createChunkGen_InXYZ_List;
	/** Вторая стадия генерации чанков: стандартные генераторы Minecraft Forge. */
	public ArrayList<IWorldGenerator> decorateChunkGen_List    ;
	
	/** Идентификаторы последних добавленных генераторов. */
	public int lastCCG, lastCCGXZ, lastCCGXYZ, lastDCG;
	
	/** Конструктор. */
	public WE_AbstactProperties() {
		genConditions = new GenReliefConditions();
		
		createChunkGen_List       = new ArrayList<WE_ICreateChunkGen      >();
		createChunkGen_InXZ_List  = new ArrayList<WE_ICreateChunkGen_InXZ >();
		createChunkGen_InXYZ_List = new ArrayList<WE_ICreateChunkGen_InXYZ>();
		decorateChunkGen_List     = new ArrayList<IWorldGenerator         >();
	}
	
	/**
	 * Добавляет генератор в список.
	 * @param g Генератор.
	 * @return Идентификатор генератора в списке.
	 */
	public int addCCG(WE_ICreateChunkGen g) {
		createChunkGen_List.add(g);
		return lastCCG = createChunkGen_List.size() - 1;
	}
	/**
	 * Возвращает последний добавленный в список генератор.
	 * @return Генератор.
	 */
	public WE_ICreateChunkGen getLastCCG() {
		return createChunkGen_List.get(lastCCG);
	}
	/** Очищает список генераторов. */
	public void clearCCG() {
		createChunkGen_List.clear();
	}
	
	/**
	 * Добавляет генератор в список.
	 * @param g Генератор.
	 * @return Идентификатор генератора в списке.
	 */
	public int addCCGXZ(WE_ICreateChunkGen_InXZ g) {
		createChunkGen_InXZ_List.add(g);
		return lastCCGXZ = createChunkGen_InXZ_List.size() - 1;
	}
	/**
	 * Возвращает последний добавленный в список генератор.
	 * @return Генератор.
	 */
	public WE_ICreateChunkGen_InXZ getLastCCGXZ() {
		return createChunkGen_InXZ_List.get(lastCCGXZ);
	}
	/** Очищает список генераторов. */
	public void clearCCGXZ() {
		createChunkGen_InXZ_List.clear();
	}
	
	/**
	 * Добавляет генератор в список.
	 * @param g Генератор.
	 * @return Идентификатор генератора в списке.
	 */
	public int addCCGXYZ(WE_ICreateChunkGen_InXYZ g) {
		createChunkGen_InXYZ_List.add(g);
		return lastCCGXYZ = createChunkGen_InXYZ_List.size() - 1;
	}
	/**
	 * Возвращает последний добавленный в список генератор.
	 * @return Генератор.
	 */
	public WE_ICreateChunkGen_InXYZ getLastCCGXYZ() {
		return createChunkGen_InXYZ_List.get(lastCCGXYZ);
	}
	/** Очищает список генераторов. */
	public void clearCCGXYZ() {
		createChunkGen_InXYZ_List.clear();
	}
	
	/**
	 * Добавляет генератор в список.
	 * @param g Генератор.
	 * @return Идентификатор генератора в списке.
	 */
	public int addDCG(IWorldGenerator g) {
		decorateChunkGen_List.add(g);
		return lastDCG = decorateChunkGen_List.size() - 1;
	}
	/**
	 * Возвращает последний добавленный в список генератор.
	 * @return Генератор.
	 */
	public IWorldGenerator getLastDCG() {
		return decorateChunkGen_List.get(lastDCG);
	}
	/** Очищает список генераторов. */
	public void clearDCG() {
		decorateChunkGen_List.clear();
	}
	
	/**
	 * Этот класс говорит генератору, когда и где он должен сгенерировать блок камня или биом. GenReliefConditions = динамическая версия конструкции "if".
	 * @author VamigA
	 */
	public class GenReliefConditions implements IGenReliefConditions {
		/** Список с примитивными условиями. */
		public ArrayList<PrimitiveCondition> conditions;
		/** Действия между примитивными условиями (0: [&&]; 1: [||]). */
		public ArrayList<Byte              >    actions;
		/** Константы для параметров. */
		public static final byte
			RC_ACTION_AND = 0,
			RC_ACTION_OR  = 1;
		
		/** Идентификатор последнего добавленного условия. */
		public int lastCon;
		
		/** Конструктор. */
		public GenReliefConditions() {
			conditions = new ArrayList<PrimitiveCondition>(); actions = new ArrayList<Byte>();
		}
		
		/**
		 * Добавляет примитивное условие.
		 * @param action Действие между двумя математическими конструкциями.
		 * @return Идентификатор в списке.
		 */
		public int addCon(byte action) {
			conditions.add(new PrimitiveCondition(action));
			return lastCon = conditions.size() - 1;
		}
		/**
		 * Возвращает последнее добавленное в список условие.
		 * @return Примитивное условие.
		 */
		public PrimitiveCondition getLast() {
			return conditions.get(lastCon);
		}
		/** Очищает список условий. */
		public void clearCon() {
			conditions.clear();
		}
		
		/**
		 * Добавляет действие. Количество действий должно быть ([количество условий] - 1).
		 * @param a Действие.
		 */
		public void addAct(byte a) {
			if(actions.size() > conditions.size() - 2) {
				System.out.println("[ERROR] GenReliefConditions.addAct(byte a): quantity of the actions must be ([conditions quantity] - 1).");
				throw new IllegalArgumentException("GenReliefConditions.addAct(byte a): quantity of the actions must be ([conditions quantity] - 1).");
			}else
				actions.add(a);
		}
		/** Очищает список действий. */
		public void clearAct() {
			actions.clear();
		}
		
		/**
		 * Выполняет эти примитивные условия. Получается большое условие! Формула:
		 * (((conditions[0] *actions[0]* conditions[1]) *actions[1]* conditions[2]) *actions[2]* conditions[3]) *actions[3]* conditions[4]...
		 * Как Вы видите, эта функция не соблюдает стандартный приоритет логических операций.
		 * @param rLayersDataFromGen Данные высоты слоев рельефа или биом-карты с генератора для класса MathConstruction. Индексы этого массива означают идентификаторы данных слоев рельефа или биом-карты.
		 *        В WE_WorldProperties еще одна ячейка массива в конце (rLayersDataFromGen[количество_слоев]) используется для нынешней высоты генерации.
		 * @return Результат большого условия.
		 */
		@Override
		public boolean go(int[] rLayersDataFromGen) {
			boolean res = conditions.get(0).go(rLayersDataFromGen);
			for(int i = 0; i < actions.size(); i++)
				switch(actions.get(i)) {
				case 0: res &= conditions.get(i + 1).go(rLayersDataFromGen); break;
				case 1: res |= conditions.get(i + 1).go(rLayersDataFromGen);
				}
			return res;
		}
		
		/**
		 * Класс для динамических примитивных условий, подобно *MathConstruction* [действие] *MathConstruction*.
		 * @author VamigA
		 */
		public class PrimitiveCondition {
			/** Массив с 2 элементами - математическими конструкциями для сравнения. */
			public MathConstruction[] twoMathConstructions;
			/** Сравнивающее действие между 2 математическими конструкциями (0: [<]; 1: [<=]; 2: [==]; 3: [!=]; 4: [>=]; 5: [>]). */
			public byte actionBetweenThem;
			/** Константы для параметров. */
			public static final byte
				PC_ACTION_LESS      = 0,
				PC_ACTION_LESSEQUAL = 1,
				PC_ACTION_EQUAL     = 2,
				PC_ACTION_NOTEQUAL  = 3,
				PC_ACTION_MOREEQUAL = 4,
				PC_ACTION_MORE      = 5;
			
			/**
			 * Конструктор.
			 * @param action Действие между 2 математическими конструкциями.
			 */
			public PrimitiveCondition(byte action) {
				twoMathConstructions = new MathConstruction[2];
				twoMathConstructions[0] = new MathConstruction(); actionBetweenThem = action; twoMathConstructions[1] = new MathConstruction();
			}
			
			/**
			 * Первый класс MathConstruction.
			 * @return Значение twoMathConstructions[0].
			 */
			public MathConstruction getFirstMath() {
				return twoMathConstructions[0];
			}
			/**
			 * Второй класс MathConstruction.
			 * @return Значение twoMathConstructions[1].
			 */
			public MathConstruction getSecondMath() {
				return twoMathConstructions[1];
			}
			
			/**
			 * Выполняет примитивное условие.
			 * @param rLayersDataFromGen Данные высоты слоев рельефа или биом-карты с генератора для класса MathConstruction. Индексы этого массива означают идентификаторы данных слоев рельефа или биом-карты.
			 *        В WE_WorldProperties еще одна ячейка массива в конце (rLayersDataFromGen[количество_слоев]) используется для нынешней высоты генерации.
			 * @return Результат условия.
			 */
			public boolean go(int[] rLayersDataFromGen) {
				switch(actionBetweenThem) {
				case 0: return twoMathConstructions[0].go(rLayersDataFromGen) <  twoMathConstructions[1].go(rLayersDataFromGen);
				case 1: return twoMathConstructions[0].go(rLayersDataFromGen) <= twoMathConstructions[1].go(rLayersDataFromGen);
				case 2: return twoMathConstructions[0].go(rLayersDataFromGen) == twoMathConstructions[1].go(rLayersDataFromGen);
				case 3: return twoMathConstructions[0].go(rLayersDataFromGen) != twoMathConstructions[1].go(rLayersDataFromGen);
				case 4: return twoMathConstructions[0].go(rLayersDataFromGen) >= twoMathConstructions[1].go(rLayersDataFromGen);
				case 5: return twoMathConstructions[0].go(rLayersDataFromGen) >  twoMathConstructions[1].go(rLayersDataFromGen);
				default: return false;
				}
			}
			
			/**
			 * Класс для динамических математических конструкций. Как примитивный калькулятор.
			 * @author VamigA
			 */
			public class MathConstruction {
				/** Числа для расчетов. Тут нужно целое число или класс ReliefLayerData (вместо класса будут подставлены данные высоты слоев рельефа или биом-карты на [X, Y]). */
				public ArrayList<Object> numbers;
				/** Действия между числами (0: [*]; 1: [/]; 2: [%]; 3: [+]; 4: [-]; 5: [<<]; 6: [>>]; 7: [&]; 8: [^]; 9: [|]). Их количество должно быть ([количество чисел] - 1). */
				public ArrayList<Byte  > actions;
				/** Константы для параметров. */
				public static final byte
					MC_ACTION_MULTIPLY   = 0,
					MC_ACTION_DIVIDE     = 1,
					MC_ACTION_DIVIDEMOD  = 2,
					MC_ACTION_ADD        = 3,
					MC_ACTION_SUBTRACT   = 4,
					MC_ACTION_SHIFTLEFT  = 5,
					MC_ACTION_SHIFTRIGHT = 6,
					MC_ACTION_BITAND     = 7,
					MC_ACTION_BITXOR     = 8,
					MC_ACTION_BITOR      = 9;
				
				/** Конструктор. */
				public MathConstruction() {
					numbers = new ArrayList<Object>(); actions = new ArrayList<Byte>();
				}
				
				/**
				 * Добавляет число.
				 * @param n Число.
				 * @param isIdForData Является ли это число идентификатором слоя рельефа или биом-карты?
				 */
				public void addNum(int n, boolean isIdForData) {
					if(isIdForData)
						numbers.add(new ReliefLayerData(n));
					else
						numbers.add(                    n );
				}
				/** Очищает список чисел. */
				public void clearNum() {
					numbers.clear();
				}
				
				/**
				 * Добавляет действие. Количество действий должно быть ([количество чисел] - 1).
				 * @param a Действие.
				 */
				public void addAct(byte a) {
					if(actions.size() > numbers.size() - 2) {
						System.out.println("[ERROR] MathConstruction.addAct(byte a): quantity of the actions must be ([numbers quantity] - 1).");
						throw new IllegalArgumentException("MathConstruction.addAct(byte a): quantity of the actions must be ([numbers quantity] - 1).");
					}else
						actions.add(a);
				}
				/** Очищает список действий. */
				public void clearAct() {
					actions.clear();
				}
				
				/**
				 * Возвращает целое значение с массива "чисел". Нужно ввиду наличия данных класса ReliefLayerData в нем.
				 * @param i Индекс "числа" в массиве.
				 * @param rLayersDataFromGen Данные высоты слоев рельефа или биом-карты с генератора. Индексы этого массива означают идентификаторы данных слоев рельефа или биом-карты.
				 *        В WE_WorldProperties еще одна ячейка массива в конце (rLayersDataFromGen[количество_слоев]) используется для нынешней высоты генерации.
				 * @return Нужное число.
				 */
				public int getNumber(int i, int[] rLayersDataFromGen) {
					if(numbers.get(i).getClass() == ReliefLayerData.class)
						return rLayersDataFromGen[((ReliefLayerData)numbers.get(i)).layerID];
					else
						return (int)numbers.get(i);
				}
				
				/**
				 * Выполняет эти математические операции с числами в этой математической конструкции. Формула:
				 * (((numbers[0] *actions[0]* numbers[1]) *actions[1]* numbers[2]) *actions[2]* numbers[3]) *actions[3]* numbers[4]...
				 * Как Вы видите, эта функция не соблюдает стандартный приоритет математических операций.
				 * @param rLayersDataFromGen Данные высоты слоев рельефа или биом-карты с генератора. Индексы этого массива означают идентификаторы данных слоев рельефа или биом-карты.
				 *        В WE_WorldProperties еще одна ячейка массива в конце (rLayersDataFromGen[количество_слоев]) используется для нынешней высоты генерации.
				 * @return Результат математических операций.
				 */
				public int go(int[] rLayersDataFromGen) {
					int res = getNumber(0, rLayersDataFromGen);
					for(int i = 0; i < actions.size(); i++)
						switch(actions.get(i)) {
						case 0: res  *= getNumber(i + 1, rLayersDataFromGen); break;
						case 1: res  /= getNumber(i + 1, rLayersDataFromGen); break;
						case 2: res  %= getNumber(i + 1, rLayersDataFromGen); break;
						case 3: res  += getNumber(i + 1, rLayersDataFromGen); break;
						case 4: res  -= getNumber(i + 1, rLayersDataFromGen); break;
						case 5: res <<= getNumber(i + 1, rLayersDataFromGen); break;
						case 6: res >>= getNumber(i + 1, rLayersDataFromGen); break;
						case 7: res  &= getNumber(i + 1, rLayersDataFromGen); break;
						case 8: res  ^= getNumber(i + 1, rLayersDataFromGen); break;
						case 9: res  |= getNumber(i + 1, rLayersDataFromGen);
						}
					return res;
				}
				
				/**
				 * Это маленький класс, который говорит MathConstruction, что numbers[i] с ним - это именно данные высоты слоя рельефа или биом-карты.
				 * В WE_WorldProperties еще одна ячейка массива в конце используется для нынешней высоты генерации.
				 * @author VamigA
				 */
				public class ReliefLayerData {
					/**
					 * Необходимый идентификатор слоя рельефа или биом-карты в настройках, в списках "reliefLayers" или "bMapLayers".
					 * В WE_WorldProperties еще одна ячейка массива в конце (layerID = количество слоев) используется для нынешней высоты генерации.
					 */
					public int layerID;
					
					/**
					 * Конструктор.
					 * @param id Необходимый идентификатор слоя рельефа или биом-карты. Нам нужны сами данные высоты этих слоев.
					 *        В WE_WorldProperties еще одна ячейка массива в конце (id = количество слоев) используется для нынешней высоты генерации.
					 */
					public ReliefLayerData(int id) {
						layerID = id;
					}
				}
			}
		}
	}
	
	/**
	 * Возвращает WE_ICreateChunkGen по идентификатору [i].
	 * @param i Идентификатор.
	 * @return WE_ICreateChunkGen - генератор.
	 */
	@Override
	public WE_ICreateChunkGen getCreateChunkGen(int i) {
		return createChunkGen_List.get(i);
	}
	/**
	 * Возвращает количество элементов в списке генераторов: WE_ICreateChunkGen.
	 * @return Размер списка.
	 */
	@Override
	public int sizeofCreateChunkGen() {
		return createChunkGen_List.size();
	}
	
	/**
	 * Возвращает WE_ICreateChunkGen_InXZ по идентификатору [i].
	 * @param i Идентификатор.
	 * @return WE_ICreateChunkGen_InXZ - генератор.
	 */
	@Override
	public WE_ICreateChunkGen_InXZ getCreateChunkGenInXZ(int i) {
		return createChunkGen_InXZ_List.get(i);
	}
	/**
	 * Возвращает количество элементов в списке генераторов: WE_ICreateChunkGen_InXZ.
	 * @return Размер списка.
	 */
	@Override
	public int sizeofCreateChunkGenInXZ() {
		return createChunkGen_InXZ_List.size();
	}
	
	/**
	 * Возвращает WE_ICreateChunkGen_InXYZ по идентификатору [i].
	 * @param i Идентификатор.
	 * @return WE_ICreateChunkGen_InXYZ - генератор.
	 */
	@Override
	public WE_ICreateChunkGen_InXYZ getCreateChunkGenInXYZ(int i) {
		return createChunkGen_InXYZ_List.get(i);
	}
	/**
	 * Возвращает количество элементов в списке генераторов: WE_ICreateChunkGen_InXYZ.
	 * @return Размер списка.
	 */
	@Override
	public int sizeofCreateChunkGenInXYZ() {
		return createChunkGen_InXYZ_List.size();
	}
	
	/**
	 * Возвращает Feature<?> по идентификатору [i].
	 * @param i Идентификатор.
	 * @return Feature<?> - генератор.
	 */
	@Override
	public IWorldGenerator getDecorateChunkGen(int i) {
		return decorateChunkGen_List.get(i);
	}
	/**
	 * Возвращает количество элементов в списке генераторов: Feature<?>.
	 * @return Размер списка.
	 */
	@Override
	public int sizeofDecorateChunkGen() {
		return decorateChunkGen_List.size();
	}
	
	/**
	 * Возвращает IGenReliefConditions для этого мира (для этих параметров).
	 * @return IGenReliefConditions - условия генерации.
	 */
	@Override
	public IGenReliefConditions getLayersConditions() {
		return genConditions;
	}
}