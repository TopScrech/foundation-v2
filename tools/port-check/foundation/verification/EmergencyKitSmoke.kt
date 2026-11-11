package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationEquipmentItem
import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariants
import foundation.item.FoundationSteelTools
import foundation.persistence.FoundationItemStackPersistence
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import micdoodle8.mods.galacticraft.core.items.ItemEmergencyKit
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.PathPackResources
import net.minecraft.server.packs.VanillaPackResourcesBuilder
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.resources.MultiPackResourceManager
import net.minecraft.tags.TagLoader
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.level.block.Blocks
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional

fun verifyEmergencyKit(resources: Path, lookup: HolderLookup.Provider) {
    fun item(name: String) = FoundationItemProperties.registered(Identifier.parse("galacticraftcore:$name"))
    val location = PackLocationInfo("foundation-tools", Component.literal("Foundation tool checks"), PackSource.BUILT_IN, Optional.empty())
    val vanilla = VanillaPackResourcesBuilder().exposeNamespace("minecraft").pushJarResources().build(location)
    MultiPackResourceManager(PackType.SERVER_DATA, listOf(vanilla.fullResources(), PathPackResources(location, resources))).use { manager ->
        val blocks = BuiltInRegistries.BLOCK
        val blockTags = TagLoader.loadTagsForRegistry(manager, blocks.key(), TagLoader.ElementLookup<net.minecraft.core.Holder<net.minecraft.world.level.block.Block>> { id, _ -> blocks.get(id).map { it } })
        blocks.prepareTagReload(TagLoader.LoadResult(blocks.key(), blockTags)).apply()
        val items = BuiltInRegistries.ITEM
        val itemTags = TagLoader.loadTagsForRegistry(manager, items.key(), TagLoader.ElementLookup<net.minecraft.core.Holder<net.minecraft.world.item.Item>> { id, _ -> items.get(id).map { it } })
        items.prepareTagReload(TagLoader.LoadResult(items.key(), itemTags)).apply()
    }
    val pickaxe = ItemStack(item("steel_pickaxe"))
    check(pickaxe.maxDamage == 768 && pickaxe.maxStackSize == 1)
    check(pickaxe.get(DataComponents.ENCHANTABLE)?.value() == 8)
    val tool = requireNotNull(pickaxe.get(DataComponents.TOOL))
    check(tool.getMiningSpeed(Blocks.STONE.defaultBlockState()) == 5f)
    check(tool.getMiningSpeed(Blocks.DIRT.defaultBlockState()) == 1f)
    check(tool.isCorrectForDrops(Blocks.OBSIDIAN.defaultBlockState()))
    check(!tool.isCorrectForDrops(Blocks.DIRT.defaultBlockState()))
    check(tool.damagePerBlock() == 1)
    check(requireNotNull(pickaxe.get(DataComponents.REPAIRABLE)).items().size() == 0)
    check(FoundationSteelTools.MATERIAL.durability() == 768 && FoundationSteelTools.MATERIAL.attackDamageBonus() == 2f)
    val attributes = requireNotNull(pickaxe.get(DataComponents.ATTRIBUTE_MODIFIERS)).modifiers()
    check(attributes.single { it.attribute() == Attributes.ATTACK_DAMAGE }.modifier().amount() == 3.0)
    check(attributes.single { it.attribute() == Attributes.ATTACK_SPEED }.modifier().amount() == -2.8f.toDouble())

    val contents = ItemEmergencyKit.contents()
    check(contents.size == 9 && contents.all { it.count == 1 })
    check(contents[0].item === item("oxygen_mask") && contents[1].item === item("oxygen_gear"))
    check(contents[2].item === item("oxygen_tank_light_full") && contents[3].item === item("oxygen_tank_light_full"))
    check(contents[2] !== contents[3] && ItemOxygenTank.used(contents[2]) == 0)
    check(contents[4].item === item("steel_pickaxe"))
    check(contents[5].item === item("food") && FoundationItemVariants.metadata(contents[5]) == 3)
    check(contents[6].`is`(Items.POTION) && requireNotNull(contents[6].get(DataComponents.POTION_CONTENTS)).`is`(Potions.HEALING))
    check(contents[7].`is`(Items.POTION) && requireNotNull(contents[7].get(DataComponents.POTION_CONTENTS)).`is`(Potions.LONG_NIGHT_VISION))
    check(contents[8].item === item("parachute") && FoundationItemVariants.metadata(contents[8]) == 13)
    contents[2].set(DataComponents.CUSTOM_NAME, Component.literal("Independent tank"))
    check(!ItemEmergencyKit.contents()[2].has(DataComponents.CUSTOM_NAME))
    check(ItemEmergencyKit.getContents(-1) == null && ItemEmergencyKit.getContents(9) == null)

    for (occupied in listOf(false, true)) {
        val equipment = InventoryExtended()
        if (occupied) for (slot in 0..4) equipment.setItem(slot, ItemStack(Items.EMERALD))
        val given = ArrayList<ItemStack>()
        ItemEmergencyKit.distribute(
            { stack -> (stack.item as? FoundationEquipmentItem)?.equip(equipment, stack); stack },
            { given.add(it.copy()) }
        )
        if (occupied) {
            check((0..4).all { equipment.getItem(it).`is`(Items.EMERALD) })
            check(given.size == 9)
        } else {
            check((0..4).all { !equipment.getItem(it).isEmpty })
            check(equipment.getItem(2).item === item("oxygen_tank_light_full") && equipment.getItem(3).item === item("oxygen_tank_light_full"))
            check(FoundationItemVariants.metadata(equipment.getItem(4)) == 13)
            check(given.size == 4 && given[0].item === item("steel_pickaxe"))
        }
    }
    val recipe = ItemEmergencyKit.getRecipe()
    check(recipe.take(3) == listOf("EAB", "CID", "FGH"))
    for (index in 0..8) {
        check(recipe[index * 2 + 3] == 'A' + index)
        check(ItemStack.matches(recipe[index * 2 + 4] as ItemStack, requireNotNull(ItemEmergencyKit.getContents(index))))
    }
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    for (stack in ItemEmergencyKit.contents() + ItemStack(item("emergency_kit"))) {
        val restored = FoundationItemStackPersistence.read(FoundationItemStackPersistence.write(stack, lookup), lookup)
        check(ItemStack.matches(stack, restored))
        val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            ItemStack.STREAM_CODEC.encode(buffer, stack)
            check(ItemStack.matches(stack, ItemStack.STREAM_CODEC.decode(buffer)))
        } finally { buffer.release() }
    }
    for (name in listOf("steel_pickaxe", "emergency_kit")) {
        check(ItemStack(item(name)).get(DataComponents.ITEM_MODEL) == Identifier.parse("galacticraftcore:$name"))
        val json = JsonParser.parseString(Files.readString(resources.resolve("assets/galacticraftcore/items/$name.json")))
        val model = ItemModels.CODEC.parse(JsonOps.INSTANCE, json.asJsonObject["model"]).getOrThrow() as CuboidItemModelWrapper.Unbaked
        check(model.model() == Identifier.parse("galacticraftcore:item/$name"))
    }
    println("Passed native steel tool tag loading, mining and drop predicates, attributes, complete emergency kit contents, equipment dispatch, occupied slots, independent stacks, potion variants, save codecs, and packets")
}
