package asmodeuscore.api.dimension

import net.minecraft.world.WorldProvider


interface IProviderFog {
    fun getFogDensity(x: Int, y: Int, z: Int): Float

    /**
     * Now used [WorldProvider.getFogColor]
     * @param x
     * @param y
     * @param z
     * @return
     */
    @Deprecated("")
    fun getFogColor(x: Int, y: Int, z: Int): Int {
        return 0
    }
}
