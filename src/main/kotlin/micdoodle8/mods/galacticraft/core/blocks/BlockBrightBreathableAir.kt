package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.FoundationBlockProperties
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BooleanProperty

/** Pressurized air illuminated by an arc lamp */
open class BlockBrightBreathableAir(properties: BlockBehaviour.Properties) : BlockBreathableAir(properties.lightLevel { 15 }) {
    constructor(assetName: String) : this(FoundationBlockProperties.named(Constants.MOD_ID_CORE, assetName))
    companion object {
        @JvmField val THERMAL: BooleanProperty = BlockBreathableAir.THERMAL
    }
}
