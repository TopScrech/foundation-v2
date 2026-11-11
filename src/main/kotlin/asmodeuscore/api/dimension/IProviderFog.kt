package asmodeuscore.api.dimension



interface IProviderFog {
    fun getFogDensity(x: Int, y: Int, z: Int): Float

    /**
     * Dimension fog colors use native environment attributes
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
