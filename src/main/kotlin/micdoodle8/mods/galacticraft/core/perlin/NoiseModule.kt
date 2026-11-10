package micdoodle8.mods.galacticraft.core.perlin


abstract class NoiseModule {
    @JvmField
    var frequencyX: Float = 1f

    @JvmField
    var frequencyY: Float = 1f

    @JvmField
    var frequencyZ: Float = 1f

    @JvmField
    var amplitude: Float = 1f

    abstract fun getNoise(i: Float): Float

    abstract fun getNoise(i: Float, j: Float): Float

    abstract fun getNoise(i: Float, j: Float, k: Float): Float

    fun setFrequency(frequency: Float) {
        this.frequencyX = frequency
        this.frequencyY = frequency
        this.frequencyZ = frequency
    }
}
