package foundation.verification

import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemTierKeysChest
import micdoodle8.mods.galacticraft.core.items.ItemSchematic
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemSchematics
import micdoodle8.mods.galacticraft.core.items.ItemFood
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationItemVariants
import foundation.item.FoundationItemComponents
import foundation.item.FoundationVariantItem
import foundation.persistence.FoundationItemStackPersistence
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemRocketParts
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemRocketModules
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemHeavyDutyPlates
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemBasicAsteroids
import micdoodle8.mods.galacticraft.planets.mars.items.ItemBasicMars
import micdoodle8.mods.galacticraft.planets.venus.items.ItemBasicVenus
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.client.renderer.item.SelectItemModel
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer

fun verifyMaterialVariants(resources: Path, lookup: HolderLookup.Provider) {
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    for ((id, count) in listOf(
        "galacticraftcore:basic_item" to 21,
        "galacticraftcore:food" to 10,
        "galaxyspace:br_foods" to 1,
        "galaxyspace:dungeon_keys" to 2,
        "galacticraftcore:schematic" to 2,
        "galacticraftplanets:schematic" to 3,
        "galaxyspace:schematics" to 7,
        "galacticraftcore:canister" to 2,
        "galacticraftcore:parachute" to 16,
        "galacticraftplanets:item_basic_asteroids" to 10,
        "galacticraftplanets:item_basic_mars" to 7,
        "galacticraftplanets:basic_item_venus" to 7,
        "galaxyspace:rocket_parts" to 20,
        "galaxyspace:rocket_modules" to 11,
        "galaxyspace:ingots" to 3,
        "galaxyspace:nuggets" to 3,
        "galaxyspace:compressed_plates" to 5,
        "galaxyspace:hdp" to 3
    )) {
        val key = Identifier.parse(id)
        val item = requireNotNull(BuiltInRegistries.ITEM.getValue(key))
        val variants = item as FoundationVariantItem
        val creative = FoundationItemVariants.creativeStacks(item)
        val visibleMetadata = when (item) {
            is ItemBasic -> (0 until count).filter { it !in 15..18 }
            is ItemFood -> listOf(0, 1, 2, 3, 9, 4, 5, 6, 7, 8)
            is ItemSchematics -> ((if (galaxyspace.core.configs.GSConfigCore.enableAdvancedRocketCraft) 0 else 5) until 7).toList()
            else -> (0 until count).toList()
        }
        check(creative.map(FoundationItemVariants::metadata) == visibleMetadata)
        val modelJson = JsonParser.parseString(Files.readString(resources.resolve("assets/${key.namespace}/items/${key.path}.json")))
        val selector = ItemModels.CODEC.parse(JsonOps.INSTANCE, modelJson.asJsonObject["model"]).getOrThrow() as SelectItemModel.Unbaked
        val language = JsonParser.parseString(Files.readString(resources.resolve("assets/${key.namespace}/lang/en_us.json"))).asJsonObject
        for (metadata in 0 until count) {
            val stack = FoundationItemVariants.stack(item, 1, metadata)
            val definition = requireNotNull(variants.variant(metadata))
            check(FoundationItemVariants.metadata(stack) == metadata && stack.damageValue == 0 && !stack.isDamageableItem)
            check((stack.hoverName.contents as TranslatableContents).key == definition.translationKey)
            check(language.has(definition.translationKey) || JsonParser.parseString(Files.readString(resources.resolve("assets/galacticraftcore/lang/en_us.json"))).asJsonObject.has(definition.translationKey))
            val direct = ItemStack(item)
            direct.set(FoundationItemComponents.LEGACY_METADATA, metadata)
            check((direct.hoverName.contents as TranslatableContents).key == definition.translationKey)
            direct.set(DataComponents.ITEM_NAME, Component.literal("Retained name"))
            check(item.getName(direct).string == "Retained name")
            val value = selector.unbakedSwitch().property().get(stack, null, null, 0, ItemDisplayContext.GUI)
            check(value == if (metadata == 0) null else metadata)
            val selected = selector.unbakedSwitch().cases().firstOrNull { value != null && it.values().contains(value) }?.model()
                ?: selector.fallback().orElseThrow()
            check((selected as CuboidItemModelWrapper.Unbaked).model() == definition.model)
            val asset = resources.resolve("assets/${definition.model.namespace}/models/${definition.model.path}.json")
            check(Files.exists(asset))
            val model = JsonParser.parseString(Files.readString(asset)).asJsonObject
            for (texture in model.getAsJsonObject("textures").entrySet()) {
                val reference = texture.value.asString
                if (reference.startsWith("#")) continue
                val textureId = Identifier.parse(reference)
                if (textureId.namespace != "minecraft") check(Files.exists(resources.resolve("assets/${textureId.namespace}/textures/${textureId.path}.png")))
            }
            val saved = FoundationItemStackPersistence.write(stack.copyWithCount(3), lookup)
            val restored = FoundationItemStackPersistence.read(saved, lookup)
            check(restored.count == 3 && ItemStack.isSameItemSameComponents(stack, restored))
            val legacy = CompoundTag().also {
                it.putString("id", id)
                it.putByte("Count", 7)
                it.putShort("Damage", metadata.toShort())
                it.put("tag", CompoundTag().also { custom -> custom.putInt("preserved", 42) })
            }
            val before = legacy.copy()
            val imported = FoundationItemStackPersistence.read(legacy, lookup)
            check(legacy == before && imported.count == 7 && imported.damageValue == 0)
            check(FoundationItemVariants.metadata(imported) == metadata)
            check(imported.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getIntOr("preserved", 0) == 42)
            val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
            try {
                ItemStack.STREAM_CODEC.encode(buffer, imported)
                val received = ItemStack.STREAM_CODEC.decode(buffer)
                check(received.count == 7 && ItemStack.isSameItemSameComponents(imported, received))
            } finally { buffer.release() }
            val lines = ArrayList<Component>()
            if (item is ItemBasicVenus) {
                check(item.showDescription(metadata) == (metadata == 0))
                check(item.getShiftDescription(metadata).isNotEmpty() == (metadata == 0))
            } else item.appendHoverText(stack, Item.TooltipContext.EMPTY, TooltipDisplay.DEFAULT, Consumer { lines.add(it) }, TooltipFlag.NORMAL)
            if (item is ItemBasic && metadata in 15..18) {
                check((lines.single().contents as TranslatableContents).key == "item.basic_item.${ItemBasic.names[metadata]}.name")
                check(!stack.has(DataComponents.FOOD))
            } else if (item is ItemBasic && metadata == ItemBasic.FREQUENCY_MODULE) {
                check(lines.map { (it.contents as TranslatableContents).key } == listOf("gui.frequency_module.desc.0", "gui.frequency_module.desc.1"))
            } else if (item is ItemFood && metadata in 0..3) {
                check((lines.single().contents as TranslatableContents).key == "item.basic_item.${ItemFood.names[metadata]}.name")
            } else if (item is ItemFood && metadata == 8) {
                check((lines.single().contents as TranslatableContents).key == "item.food.cheeseburger.desc")
            } else if (item is ItemSchematic) {
                check(lines.size == 1 && (lines.single().contents as TranslatableContents).key.isNotBlank())
            } else if (item is ItemTierKeysChest) {
                check((lines.single().contents as TranslatableContents).key == "gui.tier${metadata + 4}.desc")
            } else if (item is ItemRocketParts) {
                check((lines.single().contents as TranslatableContents).key == "gui.tier${metadata / 5 + 3}.desc")
            } else if (item is ItemRocketModules) {
                check((lines[0].contents as TranslatableContents).key == "gui.module.desc")
                check(lines.size == if (metadata >= 8) 2 else 1)
                if (metadata >= 8) check((lines[1].contents as TranslatableContents).key == "gui.tier${metadata - 4}.desc")
            } else if (item is ItemHeavyDutyPlates) {
                check((lines.single().contents as TranslatableContents).key == "gui.tier${metadata + 4}.desc")
            } else if (item is ItemBasicAsteroids && metadata == 5) {
                check((lines.single().contents as TranslatableContents).key == "item.tier3.desc")
            } else if (item is ItemBasicMars && metadata == 3) {
                check((lines.single().contents as TranslatableContents).key == "item.tier2.desc")
            } else check(lines.isEmpty())
        }
        check(runCatching { FoundationItemVariants.stack(item, 1, count) }.isFailure)
    }
    val asteroid = BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftplanets:item_basic_asteroids")) as ItemBasicAsteroids
    check(asteroid.variant(0)?.model == Identifier.parse("galacticraftplanets:item/ingot_titanium"))
    check(asteroid.variant(5)?.model == Identifier.parse("galacticraftplanets:item/item_basic_asteroids"))
    check(asteroid.getSmeltingExperience(FoundationItemVariants.stack(asteroid, 1, 5)) == 2f)
    check(asteroid.getSmeltingExperience(FoundationItemVariants.stack(asteroid, 1, 6)) == 1f)
    val parts = BuiltInRegistries.ITEM.getValue(Identifier.parse("galaxyspace:rocket_parts")) as ItemRocketParts
    check(parts.variant(19)?.model == Identifier.parse("galaxyspace:item/rocket_parts/rocket_stabiliser_tier_6"))
    val mars = BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftplanets:item_basic_mars")) as ItemBasicMars
    check(mars.getSmeltingExperience(FoundationItemVariants.stack(mars, 1, 3)) == 1.5f)
    check(mars.getSmeltingExperience(FoundationItemVariants.stack(mars, 1, 5)) == 1f)
    val basic = BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:basic_item")) as ItemBasic
    for (meta in 6..11) check(basic.getSmeltingExperience(FoundationItemVariants.stack(basic, 1, meta)) == 1f)
    println("Passed 133 material, parachute, food, canister, key, and schematic variants, legacy imports, native persistence, network codecs, tooltips, model selectors, and referenced textures")
}
