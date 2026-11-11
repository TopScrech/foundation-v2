package foundation.item

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

object FoundationItemProperties {
    fun registered(id: Identifier): Item {
        val item = requireNotNull(BuiltInRegistries.ITEM.getValue(id)) { "Missing registered item $id" }
        check(BuiltInRegistries.ITEM.getKey(item) == id) { "Missing registered item $id" }
        return item
    }

    fun named(namespace: String, path: String): Item.Properties = Item.Properties()
        .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, path)))
        .overrideDescription("item.$path.name")
}
