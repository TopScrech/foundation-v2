package asmodeuscore.api.dimension;

import net.minecraft.world.WorldProvider;

public interface IProviderFog
{
	public float getFogDensity(int x, int y, int z);
	
	/**
	 * Now used {@link WorldProvider#getFogColor(float, float)}
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Deprecated	
	public default int getFogColor(int x, int y, int z) {return 0;};
}