package asmodeuscore.core.utils;

import net.minecraft.util.DamageSource;

public class ACDamageSource extends DamageSource{
	
	public static final DamageSource solar =  new ACDamageSource("solar").setDamageBypassesArmor();
	public static final DamageSource pressure = new ACDamageSource("pressure").setDamageBypassesArmor();
	public static final DamageSource heat = new ACDamageSource("heat").setDamageBypassesArmor();
	public static final DamageSource cold = new ACDamageSource("cold").setDamageBypassesArmor();
	
    public ACDamageSource(String damageType)
    {
        super(damageType);
    }
}
