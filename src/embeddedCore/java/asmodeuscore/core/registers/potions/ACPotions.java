package asmodeuscore.core.registers.potions;

import asmodeuscore.core.prefab.potions.Hypothermia;
import asmodeuscore.core.prefab.potions.Overheat;
import asmodeuscore.core.prefab.potions.Radiation;
import asmodeuscore.core.utils.Utils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;


public class ACPotions {
	
	public static Radiation radiation;
	public static Overheat overheat;
	public static Hypothermia hypothermia;
	
	public static void initialize()
	{
		initPotions();
	}
	
	private static void initPotions()
	{
		radiation = new Radiation(true, -4502242);
		overheat = new Overheat(true, Utils.getIntColor(150, 80, 0));
		hypothermia = new Hypothermia(true, Utils.getIntColor(30, 144, 255));
		
		ForgeRegistries.POTIONS.register(radiation);
		ForgeRegistries.POTIONS.register(overheat);
		ForgeRegistries.POTIONS.register(hypothermia);
	}	
	
}

