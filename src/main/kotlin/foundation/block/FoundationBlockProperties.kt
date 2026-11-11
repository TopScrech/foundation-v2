package foundation.block

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.state.BlockBehaviour

/** Block IDs must be available while current Minecraft constructs the block and its states */
object FoundationBlockProperties {
    @JvmStatic fun named(namespace: String, path: String): BlockBehaviour.Properties =
        BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(namespace, path)))
}
