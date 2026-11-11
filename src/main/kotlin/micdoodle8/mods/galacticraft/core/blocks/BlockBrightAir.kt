package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.Constants
import foundation.block.FoundationBlockProperties
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.PushReaction

/** Invisible, replaceable air left by temporary light sources */
open class BlockBrightAir(properties: BlockBehaviour.Properties) : AirBlock(
    properties.replaceable().noCollision().noLootTable().air()
        .strength(0f, 600f).pushReaction(PushReaction.POPPED).lightLevel { 15 }
) {
    constructor(assetName: String) : this(FoundationBlockProperties.named(Constants.MOD_ID_CORE, assetName))
}
