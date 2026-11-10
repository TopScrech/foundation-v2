package micdoodle8.mods.galacticraft.core.perlin.generator

import micdoodle8.mods.galacticraft.core.perlin.FishyNoise
import micdoodle8.mods.galacticraft.core.perlin.NoiseModule
import java.util.Random

open class RidgedMulti constructor(seed: Long, private val numOctaves: Int) : NoiseModule() {
    private val noiseGen: FishyNoise
    private val offsetX: Float
    private val offsetY: Float
    private val offsetZ: Float

    init {
        val rand: Random = Random(seed)
        this.offsetX = rand.nextFloat() / 2 + 0.01f
        this.offsetY = rand.nextFloat() / 2 + 0.01f
        this.offsetZ = rand.nextFloat() / 2 + 0.01f
        this.noiseGen = FishyNoise(seed)
    }

    override fun getNoise(i: Float): Float {
        var i: Float = i
        i *= this.frequencyX
        var `val`: Float = 0f
        var weight: Float = 1.0f
        val offset: Float = 1.0f
        val gain: Float = 2.0f
        for (n: Int in 0..<this.numOctaves) {
            var noise: Float = this.absolute(this.noiseGen.noise2d(i + this.offsetX, this.offsetY))
            noise = offset - noise
            noise *= noise
            noise *= weight

            weight = noise * gain

            if (weight > 1f) {
                weight = 1f
            }

            if (weight < 0f) {
                weight = 0f
            }

            `val` += noise

            i *= 2f
        }
        return `val`
    }

    override fun getNoise(i: Float, j: Float): Float {
        var i: Float = i
        var j: Float = j
        i *= this.frequencyX
        j *= this.frequencyY
        var `val`: Float = 0f
        var weight: Float = 1.0f
        val offset: Float = 1.0f
        val gain: Float = 2.0f
        for (n: Int in 0..<this.numOctaves) {
            var noise: Float = this.absolute(this.noiseGen.noise2d(i + this.offsetX, j + this.offsetY))
            noise = offset - noise
            noise *= noise
            noise *= weight

            weight = noise * gain

            if (weight > 1f) {
                weight = 1f
            }

            if (weight < 0f) {
                weight = 0f
            }

            `val` += noise

            i *= 2f
            j *= 2f
        }
        return `val`
    }

    override fun getNoise(i: Float, j: Float, k: Float): Float {
        var i: Float = i
        var j: Float = j
        var k: Float = k
        i *= this.frequencyX
        j *= this.frequencyY
        k *= this.frequencyZ
        var `val`: Float = 0f
        var weight: Float = 1.0f
        val offset: Float = 1.0f
        val gain: Float = 2.0f
        for (n: Int in 0..<this.numOctaves) {
            var noise: Float =
                this.absolute(this.noiseGen.noise3d(i + this.offsetX, j + this.offsetY, k + this.offsetZ))
            noise = offset - noise
            noise *= noise
            noise *= weight

            weight = noise * gain

            if (weight > 1f) {
                weight = 1f
            }

            if (weight < 0f) {
                weight = 0f
            }

            `val` += noise

            i *= 2f
            j *= 2f
            k *= 2f
        }
        return `val`
    }

    private fun absolute(d: Float): Float {
        var d: Float = d
        if (d < 0) {
            d = -d
        }
        return d
    }
}
