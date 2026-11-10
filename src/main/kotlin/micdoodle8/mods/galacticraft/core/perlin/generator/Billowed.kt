package micdoodle8.mods.galacticraft.core.perlin.generator

import micdoodle8.mods.galacticraft.core.perlin.FishyNoise
import micdoodle8.mods.galacticraft.core.perlin.NoiseModule
import java.util.Random
import kotlin.math.abs

open class Billowed constructor(seed: Long, private val numOctaves: Int, private val persistance: Float) :
    NoiseModule() {
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
        var curAmplitude: Float = this.amplitude
        for (n: Int in 0..<this.numOctaves) {
            `val` += abs(this.noiseGen.noise2d(i + this.offsetX, this.offsetY) * curAmplitude)
            i *= 2f
            curAmplitude *= this.persistance
        }
        return `val`
    }

    override fun getNoise(i: Float, j: Float): Float {
        var i: Float = i
        var j: Float = j
        i *= this.frequencyX
        j *= this.frequencyY
        if (this.numOctaves == 2) {
            return abs(
                this.noiseGen.noise2d(
                    i + this.offsetX,
                    j + this.offsetY
                ) * this.amplitude
            ) + abs(
                this.noiseGen.noise2d(
                    i + i + this.offsetX,
                    j + j + this.offsetY
                ) * this.amplitude * this.persistance
            )
        }

        var `val`: Float = 0f
        var curAmplitude: Float = this.amplitude
        for (n: Int in 0..<this.numOctaves) {
            `val` += abs(this.noiseGen.noise2d(i + this.offsetX, j + this.offsetY) * curAmplitude)
            i += i
            j += j
            curAmplitude *= this.persistance
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
        for (n: Int in 0..<this.numOctaves) {
            `val` += abs(this.noiseGen.noise3d(i + this.offsetX, j + this.offsetY, k + this.offsetZ) * this.amplitude)
            i *= 2f
            j *= 2f
            k *= 2f
        }
        return `val`
    }
}
