package asmodeuscore.api.dimension;


import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;

public interface IAdvancedSpace extends IGalacticraftWorldProvider
{
	public enum TempBody
	{
		HOT, WARM, COMFORT, COOL, COLD, ICY
	}
	
	@Deprecated
	public enum StarClass {
		SUBDWARF, 
		DWARF, 
		SUBGIANT,
		GIANT,	
		SUPERGIANT,
		HYPERGIANT,
		BLACKHOLE;
	}
	

	@Deprecated
	public enum TypeBody
	{
		STAR, ASTEROID,	PLANET,	MOON, SATELLITE
	}
	
	public enum StarType {
		SUBDWARF, 
		DWARF, 
		SUBGIANT,
		GIANT,	
		SUPERGIANT,
		HYPERGIANT,
		BLACKHOLE;
	}
	
	public enum StarColor
	{
		BROWN(74, 2, 3), 
		RED(225, 2, 0), 
		ORANGE(255, 121, 0), 
		YELLOW(254, 230, 8), 
		WHITE(150, 150, 150), 
		LIGHTBLUE(54, 199, 254), 
		BLUE(11, 42, 241),
		
		M1(205, 88, 4), 
		M2(195, 136, 66), 
		M3(237, 212, 155),
		
		K1(236, 207, 153), 
		K2(242, 228, 194), 
		K3(242, 235, 224), 
		
		G1(245, 240, 233), 
		G2(242, 240, 235), 
		G3(235, 234, 230), 
		
		F1(243, 241, 238), 
		F2(240, 241, 240), 
		F3(237, 240, 242), 
		
		A1(221, 227, 234), 
		A2(225, 232, 243), 
		A3(202, 219, 235), 
		
		B1(221, 232, 246), 
		B2(210, 232, 249), 
		B3(200, 234, 253), 
		
		O1(208, 234, 254), 
		O2(173, 216, 252), 
		O3(197, 233, 255);	
		
		private Vec3i color;
		StarColor(int r, int g, int b)
		{
			this.color = new Vec3i(r, g, b);
		}
		
		public Vec3i getColor()
		{
			return color;
		}
		
	}	
	
	public enum ClassBody
	{				
		//PLANETS
		SELENA,	DESERT,	TERRA, OCEANIDE, GASGIANT, ICEGIANT, ASTEROID, TITAN, ICEWORLD;
	}
	
	
	
	public double getSolarWindMultiplier();	
	public default boolean isRetrogradeRotation() { return false; }
	public default float getThermalLevelModifier(EntityPlayer player) { return this.getThermalLevelModifier();};
}
