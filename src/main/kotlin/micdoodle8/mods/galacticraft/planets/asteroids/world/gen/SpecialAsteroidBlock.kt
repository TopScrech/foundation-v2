package micdoodle8.mods.galacticraft.planets.asteroids.world.gen

import net.minecraft.block.Block
import java.util.ArrayList

open class SpecialAsteroidBlock
constructor(
    @JvmField var block: Block?,
    @JvmField var meta: Byte,
    @JvmField var probability: Int, //Arbitrary scale from 0 to 1;
    @JvmField var thickness: Double
) {
    @JvmField
    var index: Int

    init {
        this.index = register.size
        register.add(this)
    }

    companion object {
        @JvmField
        var register: ArrayList<SpecialAsteroidBlock?> = ArrayList<SpecialAsteroidBlock?>()
    }
}
