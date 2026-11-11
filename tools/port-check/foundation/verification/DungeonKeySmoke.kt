package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationItemComponents
import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariants
import foundation.persistence.FoundationItemStackPersistence
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemTierKeysChest
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.api.item.IKeyItem
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemKey
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import java.nio.file.Files
import java.nio.file.Path

fun verifyDungeonKeys(resources: Path, lookup: HolderLookup.Provider) {
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    val profiles = listOf(
        Triple("galacticraftcore:key", 0, 1), Triple("galacticraftplanets:key", 0, 2),
        Triple("galacticraftplanets:key_t3", 0, 3), Triple("galaxyspace:dungeon_keys", 0, 4), Triple("galaxyspace:dungeon_keys", 1, 5)
    )
    for ((name, metadata, tier) in profiles) {
        val id = Identifier.parse(name)
        val item = FoundationItemProperties.registered(id)
        val stack = if (item is ItemTierKeysChest) FoundationItemVariants.stack(item, 1, metadata) else ItemStack(item)
        val key = item as IKeyItem
        check(key.getTier(stack) == tier && stack.maxStackSize == 1 && !stack.isDamageableItem)
        check((item as ISortableItem).getCategory(metadata) == EnumSortCategoryItem.KEYS)
        if (item is ItemKey) {
            check(item.creativeStacks().size == 1 && key.getTier(item.creativeStacks().single()) == tier)
            check((stack.hoverName.contents as TranslatableContents).key == "item.key.t$tier.name") { "Unexpected key name for $name: ${stack.hoverName}" }
            check(stack.get(DataComponents.ITEM_MODEL) == id)
            val json = JsonParser.parseString(Files.readString(resources.resolve("assets/${id.namespace}/items/${id.path}.json")))
            val model = ItemModels.CODEC.parse(JsonOps.INSTANCE, json.asJsonObject["model"]).getOrThrow() as CuboidItemModelWrapper.Unbaked
            check(model.model() == Identifier.fromNamespaceAndPath(id.namespace, "item/${id.path}"))
        }
        val old = CompoundTag().also {
            it.putString("id", name); it.putByte("Count", 1); it.putShort("Damage", metadata.toShort())
            it.put("tag", CompoundTag().also { data -> data.putString("owner", "Retained owner") })
        }
        val imported = FoundationItemStackPersistence.read(old, lookup)
        check(key.getTier(imported) == tier && FoundationItemVariants.metadata(imported) == metadata)
        check(imported.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getStringOr("owner", "") == "Retained owner")
        imported.set(DataComponents.CUSTOM_NAME, Component.literal("Named dungeon key"))
        val restored = FoundationItemStackPersistence.read(FoundationItemStackPersistence.write(imported, lookup), lookup)
        check(ItemStack.matches(imported, restored) && key.getTier(restored) == tier)
        val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            ItemStack.STREAM_CODEC.encode(buffer, restored)
            val decoded = ItemStack.STREAM_CODEC.decode(buffer)
            check(ItemStack.matches(restored, decoded) && key.getTier(decoded) == tier)
        } finally { buffer.release() }
    }
    val item = FoundationItemProperties.registered(Identifier.parse("galaxyspace:dungeon_keys")) as ItemTierKeysChest
    check(item.creativeStacks().map { item.getTier(it) } == listOf(4, 5))
    val bad = ItemStack(item).also { it.set(FoundationItemComponents.LEGACY_METADATA, 99) }
    check(item.getTier(bad) == 0 && item.getTier(ItemStack.EMPTY) == 0 && item.getTier(null) == 0)
    check(item.getTier(ItemStack(Items.DIAMOND)) == 0)
    check(runCatching { FoundationItemVariants.stack(item, 1, 2) }.isFailure)
    check(!FoundationItemVariants.sameVariant(FoundationItemVariants.stack(item, 1, 0), FoundationItemVariants.stack(item, 1, 1)))
    println("Passed five native dungeon key tiers, sorting, creative stacks, tier-preserving legacy imports, names and custom data, model codecs, saves, packets, and invalid-key handling")
}
