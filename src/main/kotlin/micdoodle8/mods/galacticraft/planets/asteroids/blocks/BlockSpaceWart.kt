package micdoodle8.mods.galacticraft.planets.asteroids.blocks

import net.minecraft.block.BlockNetherWart

open class BlockSpaceWart constructor(assetName: String?) : BlockNetherWart() {
    init {
        this.setTickRandomly(false)
        this.setTranslationKey(assetName)
    }
}
