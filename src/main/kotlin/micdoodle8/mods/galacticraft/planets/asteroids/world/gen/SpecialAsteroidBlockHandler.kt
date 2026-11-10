package micdoodle8.mods.galacticraft.planets.asteroids.world.gen

import kotlin.math.pow
import java.util.ArrayList
import java.util.Random

open class SpecialAsteroidBlockHandler {
    @JvmField
    var asteroidBlocks: ArrayList<SpecialAsteroidBlock>

    constructor(vararg asteroidBlocks: SpecialAsteroidBlock?) {
        this.asteroidBlocks = ArrayList<SpecialAsteroidBlock>()
        for (asteroidBlock: SpecialAsteroidBlock in this.asteroidBlocks) {
            for (i: Int in 0..<asteroidBlock.probability) {
                this.asteroidBlocks.add(asteroidBlock)
            }
        }
    }

    constructor() {
        this.asteroidBlocks = ArrayList<SpecialAsteroidBlock>()
    }

    open fun addBlock(asteroidBlock: SpecialAsteroidBlock) {
        for (i: Int in 0..<asteroidBlock.probability) {
            this.asteroidBlocks.add(asteroidBlock)
        }
    }

    open fun getBlock(rand: Random, size: Int): SpecialAsteroidBlock {
        val s: Int = this.asteroidBlocks.size
        if (s < 10) {
            return this.asteroidBlocks.get(rand.nextInt(s))
        }

        val r: Double = rand.nextDouble()
        val index: Int = (s * r.pow((size + 5) * 0.05)).toInt()
        return this.asteroidBlocks.get(index)
    }
}
