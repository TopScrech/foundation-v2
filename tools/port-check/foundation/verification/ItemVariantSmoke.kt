package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.client.renderer.item.SelectItemModel
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties
import net.minecraft.world.item.ItemDisplayContext
import foundation.item.FoundationItemComponents
import foundation.item.FoundationItemVariants
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.core.items.ItemMoon
import net.minecraft.ChatFormatting
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.component.CustomData
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.TextColor
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.Identifier
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemBasicAsteroids
import micdoodle8.mods.galacticraft.planets.mars.items.ItemBasicMars
import micdoodle8.mods.galacticraft.planets.venus.items.ItemBasicVenus
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemRocketParts
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemRocketModules
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemIngots
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemNuggets
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemCompressedPlates
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemHeavyDutyPlates
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry
import net.minecraftforge.registries.RegistryManager
import java.nio.file.Files
import java.nio.file.Path

fun verifyItemVariants(resourcesRoot: Path) {
    micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase.install()
    val components = BuiltInRegistries.DATA_COMPONENT_TYPE as MappedRegistry<DataComponentType<*>>
    components.unfreeze()
    val forgeComponents = RegistryManager.ACTIVE.getRegistry<DataComponentType<*>>(Registries.DATA_COMPONENT_TYPE)
    if (forgeComponents != null) {
        forgeComponents.unfreeze()
        for ((id, type) in FoundationItemComponents.types) forgeComponents.register(id, type)
        forgeComponents.freeze()
    } else for ((id, type) in FoundationItemComponents.types) Registry.register(components, id, type)
    components.freeze()

    val blocks = BuiltInRegistries.BLOCK as MappedRegistry<net.minecraft.world.level.block.Block>
    blocks.unfreeze()
    val items = BuiltInRegistries.ITEM as MappedRegistry<Item>
    val forgeItems = ForgeRegistries.ITEMS as ForgeRegistry<Item>
    items.unfreeze()
    forgeItems.unfreeze()
    forgeItems.register(Identifier.parse("galacticraftcore:parachute"), micdoodle8.mods.galacticraft.core.items.ItemParaChute("parachute"))
    forgeItems.register(Identifier.parse("galacticraftcore:oxygen_mask"), micdoodle8.mods.galacticraft.core.items.ItemOxygenMask("oxygen_mask"))
    forgeItems.register(Identifier.parse("galacticraftcore:oxygen_gear"), micdoodle8.mods.galacticraft.core.items.ItemOxygenGear("oxygen_gear"))
    for ((tier, name) in listOf(1 to "oxygen_tank_light_full", 2 to "oxygen_tank_med_full", 3 to "oxygen_tank_heavy_full")) {
        forgeItems.register(Identifier.parse("galacticraftcore:$name"), micdoodle8.mods.galacticraft.core.items.ItemOxygenTank(tier, name))
    }
    forgeItems.register(Identifier.parse("galacticraftcore:canister"), micdoodle8.mods.galacticraft.core.items.ItemCanister("canister"))
    forgeItems.register(Identifier.parse("galacticraftcore:food"), micdoodle8.mods.galacticraft.core.items.ItemFood("food"))
    forgeItems.register(Identifier.parse("galacticraftcore:steel_pickaxe"), micdoodle8.mods.galacticraft.core.items.ItemPickaxeGC("steel_pickaxe"))
    forgeItems.register(Identifier.parse("galacticraftcore:emergency_kit"), micdoodle8.mods.galacticraft.core.items.ItemEmergencyKit("emergency_kit"))
    forgeItems.register(Identifier.parse("galacticraftcore:steel_axe"), micdoodle8.mods.galacticraft.core.items.ItemAxeGC("steel_axe"))
    forgeItems.register(Identifier.parse("galacticraftcore:steel_shovel"), micdoodle8.mods.galacticraft.core.items.ItemSpadeGC("steel_shovel"))
    forgeItems.register(Identifier.parse("galacticraftcore:steel_hoe"), micdoodle8.mods.galacticraft.core.items.ItemHoeGC("steel_hoe"))
    forgeItems.register(Identifier.parse("galacticraftcore:steel_sword"), micdoodle8.mods.galacticraft.core.items.ItemSwordGC("steel_sword"))
    forgeItems.register(Identifier.parse("galacticraftplanets:titanium_pickaxe"), micdoodle8.mods.galacticraft.planets.asteroids.items.ItemPickaxeAsteroids("titanium_pickaxe"))
    forgeItems.register(Identifier.parse("galacticraftplanets:titanium_axe"), micdoodle8.mods.galacticraft.planets.asteroids.items.ItemAxeAsteroids("titanium_axe"))
    forgeItems.register(Identifier.parse("galacticraftplanets:titanium_shovel"), micdoodle8.mods.galacticraft.planets.asteroids.items.ItemSpadeAsteroids("titanium_shovel"))
    forgeItems.register(Identifier.parse("galacticraftplanets:titanium_hoe"), micdoodle8.mods.galacticraft.planets.asteroids.items.ItemHoeAsteroids("titanium_hoe"))
    forgeItems.register(Identifier.parse("galacticraftplanets:titanium_sword"), micdoodle8.mods.galacticraft.planets.asteroids.items.ItemSwordAsteroids("titanium_sword"))
    forgeItems.register(Identifier.parse("galacticraftcore:cheese_curd"), micdoodle8.mods.galacticraft.core.items.ItemCheese(1, 0.1f, false))
    forgeItems.register(Identifier.parse("galaxyspace:br_foods"), galaxyspace.systems.BarnardsSystem.core.prefab.items.ItemFoodBR())
    forgeItems.register(Identifier.parse("foundation_test:space_food"), object : Item(
        foundation.item.FoundationItemProperties.named("foundation_test", "space_food")
            .food(net.minecraft.world.food.FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).build())
    ), asmodeuscore.api.item.IItemSpaceFood {})
    forgeItems.register(Identifier.parse("galacticraftcore:key"), micdoodle8.mods.galacticraft.core.items.ItemKey("key"))
    forgeItems.register(Identifier.parse("galacticraftplanets:key"), micdoodle8.mods.galacticraft.planets.mars.items.ItemKeyMars())
    forgeItems.register(Identifier.parse("galacticraftplanets:key_t3"), micdoodle8.mods.galacticraft.planets.venus.items.ItemKeyVenus("key_t3"))
    forgeItems.register(Identifier.parse("galaxyspace:dungeon_keys"), galaxyspace.systems.SolarSystem.planets.overworld.items.ItemTierKeysChest())
    forgeItems.register(Identifier.parse("galacticraftcore:schematic"), micdoodle8.mods.galacticraft.core.items.ItemSchematic("schematic"))
    forgeItems.register(Identifier.parse("galacticraftplanets:schematic"), micdoodle8.mods.galacticraft.planets.mars.items.ItemSchematicTier2())
    forgeItems.register(Identifier.parse("galaxyspace:schematics"), galaxyspace.systems.SolarSystem.planets.overworld.items.ItemSchematics())
    forgeItems.register(Identifier.parse("galacticraftcore:battery"), micdoodle8.mods.galacticraft.core.items.ItemBattery("battery"))
    forgeItems.register(Identifier.parse("galacticraftcore:infinite_battery"), micdoodle8.mods.galacticraft.core.items.ItemBatteryInfinite("infinite_battery"))
    forgeItems.register(Identifier.parse("galacticraftplanets:atomic_battery"), micdoodle8.mods.galacticraft.planets.venus.items.ItemBatteryAtomic("atomic_battery"))
    for ((name, capacity) in listOf("advanced_battery" to 60000f, "modern_battery" to 120000f, "extra_battery" to 180000f, "ultimate_battery" to 300000f)) {
        forgeItems.register(Identifier.parse("galaxyspace:$name"), galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBattery(name, capacity))
    }
    val moon = ItemMoon("item_basic_moon")
    forgeItems.register(Identifier.parse("galacticraftcore:item_basic_moon"), moon)
    forgeItems.register(Identifier.parse("galacticraftcore:basic_item"), ItemBasic("basic_item"))
    forgeItems.register(Identifier.parse("galacticraftplanets:item_basic_asteroids"), ItemBasicAsteroids("item_basic_asteroids"))
    forgeItems.register(Identifier.parse("galacticraftplanets:item_basic_mars"), ItemBasicMars("item_basic_mars"))
    forgeItems.register(Identifier.parse("galacticraftplanets:basic_item_venus"), ItemBasicVenus("basic_item_venus"))
    forgeItems.register(Identifier.parse("galaxyspace:rocket_parts"), ItemRocketParts())
    forgeItems.register(Identifier.parse("galaxyspace:rocket_modules"), ItemRocketModules())
    forgeItems.register(Identifier.parse("galaxyspace:ingots"), ItemIngots())
    forgeItems.register(Identifier.parse("galaxyspace:nuggets"), ItemNuggets())
    forgeItems.register(Identifier.parse("galaxyspace:compressed_plates"), ItemCompressedPlates())
    forgeItems.register(Identifier.parse("galaxyspace:hdp"), ItemHeavyDutyPlates())
    forgeItems.register(Identifier.parse("foundation_test:damageable"), Item(foundation.item.FoundationItemProperties.named("foundation_test", "damageable").durability(100)))
    forgeItems.register(Identifier.parse("foundation_test:unported_variant"), Item(foundation.item.FoundationItemProperties.named("foundation_test", "unported_variant")))
    forgeItems.register(Identifier.parse("foundation_test:crafting_remainder"), object : Item(foundation.item.FoundationItemProperties.named("foundation_test", "crafting_remainder")) {
        override fun getCraftingRemainder(input: ItemStack): ItemStackTemplate {
            val remainder = FoundationItemVariants.stack(moon, 2, 2)
            remainder.set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().also { it.putLong("linkedUUIDMost", 12345L) }))
            return ItemStackTemplate.fromNonEmptyStack(remainder)
        }
    })
    forgeItems.register(Identifier.parse("foundation_test:failed_remainder"), object : Item(foundation.item.FoundationItemProperties.named("foundation_test", "failed_remainder")) {
        override fun getCraftingRemainder(input: ItemStack): ItemStackTemplate = error("Invalid test crafting remainder")
    })
    forgeItems.freeze()
    items.freeze()
    blocks.freeze()
    val lookup = net.minecraft.data.registries.VanillaRegistries.createWorldLookup()
    BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(lookup).forEach { it.apply() }
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    val ops = RegistryOps.create(NbtOps.INSTANCE, access)
    val language = JsonParser.parseString(Files.readString(resourcesRoot.resolve("assets/galacticraftcore/lang/en_us.json"))).asJsonObject
    ItemModels.bootstrap()
    SelectItemModelProperties.bootstrap()
    val itemFile = resourcesRoot.resolve("assets/galacticraftcore/items/item_basic_moon.json")
    val modelJson = JsonParser.parseString(Files.readString(itemFile)).asJsonObject.getAsJsonObject("model")
    val selector = ItemModels.CODEC.parse(JsonOps.INSTANCE, modelJson).getOrThrow() as SelectItemModel.Unbaked
    val stacks = moon.creativeStacks()
    check(stacks.size == 3)
    for ((metadata, stack) in stacks.withIndex()) {
        check(FoundationItemVariants.metadata(stack) == metadata)
        check(stack.get(DataComponents.CUSTOM_DATA) == null)
        check(!stack.isDamageableItem && stack.damageValue == 0)
        val definition = requireNotNull(moon.variant(metadata))
        val name = requireNotNull(stack.get(DataComponents.ITEM_NAME))
        check((name.contents as TranslatableContents).key == definition.translationKey)
        check(name.style.color == TextColor.fromLegacyFormat(ChatFormatting.BLUE))
        check(language.has(definition.translationKey))
        check(stack.get(DataComponents.ITEM_MODEL) == Identifier.parse("galacticraftcore:item_basic_moon"))
        val selectedValue = selector.unbakedSwitch().property().get(stack, null, null, 0, ItemDisplayContext.GUI)
        check(selectedValue == if (metadata == 0) null else metadata)
        val selected = selector.unbakedSwitch().cases().firstOrNull { selectedValue != null && it.values().contains(selectedValue) }?.model()
            ?: selector.fallback().orElseThrow()
        check((selected as CuboidItemModelWrapper.Unbaked).model() == definition.model)
        check(Files.exists(resourcesRoot.resolve("assets/${definition.model.namespace}/models/${definition.model.path}.json")))

        val encoded = ItemStack.CODEC.encodeStart(ops, stack).getOrThrow()
        val restored = ItemStack.CODEC.parse(ops, encoded).getOrThrow()
        check(ItemStack.isSameItemSameComponents(stack, restored))
        val copied = stack.copy()
        FoundationItemVariants.apply(copied, (metadata + 1) % 3)
        check(FoundationItemVariants.metadata(stack) == metadata)
        check(FoundationItemVariants.metadata(copied) == (metadata + 1) % 3)
        val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            ItemStack.STREAM_CODEC.encode(buffer, stack)
            val received = ItemStack.STREAM_CODEC.decode(buffer)
            check(ItemStack.isSameItemSameComponents(stack, received))
        } finally { buffer.release() }
    }
    // A component supplied directly by loading or a command also selects the correct name and model
    val direct = ItemStack(moon)
    direct.set(FoundationItemComponents.LEGACY_METADATA, 2)
    check((direct.hoverName.contents as TranslatableContents).key == "item.lunar_sapphire.name")
    check(selector.unbakedSwitch().property().get(direct, null, null, 0, ItemDisplayContext.GUI) == 2)
    val stack = FoundationItemVariants.stack(moon, 12, 2)
    check(stack.count == 12)
    val before = stack.copy()
    check(runCatching { FoundationItemVariants.apply(stack, 3) }.isFailure)
    check(ItemStack.isSameItemSameComponents(before, stack))
    check(FoundationItemVariants.stack(moon, 0, 2).isEmpty)
    check(FoundationItemVariants.apply(ItemStack.EMPTY, 2) === ItemStack.EMPTY)
    check(ItemStack(moon).get(DataComponents.CUSTOM_DATA) == null)
    val independent = FoundationItemVariants.stack(moon, 1, 2)
    val custom = CompoundTag().also { it.putLong("linkedUUIDMost", 12345L) }
    independent.set(DataComponents.CUSTOM_DATA, CustomData.of(custom))
    independent.set(DataComponents.MAX_STACK_SIZE, 1)
    independent.set(DataComponents.MAX_DAMAGE, 100)
    independent.set(DataComponents.DAMAGE, 17)
    FoundationItemVariants.apply(independent, 1)
    check(independent.damageValue == 17)
    check(independent.get(DataComponents.CUSTOM_DATA)?.copyTag() == custom)
    val roundTrip = ItemStack.CODEC.parse(ops, ItemStack.CODEC.encodeStart(ops, independent).getOrThrow()).getOrThrow()
    check(roundTrip.damageValue == 17 && FoundationItemVariants.metadata(roundTrip) == 1)
    check(roundTrip.get(DataComponents.CUSTOM_DATA)?.copyTag() == custom)
    check(moon.showDescription(2) && !moon.showDescription(0))
    check(moon.getSmeltingExperience(stacks[1]) == 1f)
}
