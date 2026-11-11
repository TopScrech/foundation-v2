package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationItemProperties
import foundation.persistence.FoundationItemStackPersistence
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemSwordAsteroids
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Blocks
import java.nio.file.Files
import java.nio.file.Path

fun verifyTitaniumTools(resources: Path, lookup: HolderLookup.Provider) {
    data class Profile(val name: String, val durability: Int, val damage: Float, val speed: Float, val enchantment: Int)
    val profiles = listOf(
        Profile("titanium_pickaxe", 760, 5f, -2.8f, 16),
        Profile("titanium_axe", 768, 6f, -3f, 8),
        Profile("titanium_shovel", 760, 5.5f, -3f, 16),
        Profile("titanium_hoe", 760, 0f, 1f, 0),
        // The cached Forge 1.12 HashMultimap/AttributeModifier reference retained superclass modifiers
        Profile("titanium_sword", 760, 7f, -2.4f, 16)
    )
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    val enchantments = lookup.lookupOrThrow(Registries.ENCHANTMENT)
    for ((name, durability, damage, speed, enchantment) in profiles) {
        val item = FoundationItemProperties.registered(Identifier.parse("galacticraftplanets:$name"))
        val stack = ItemStack(item)
        check(stack.maxDamage == durability && stack.maxStackSize == 1)
        val attributes = requireNotNull(stack.get(DataComponents.ATTRIBUTE_MODIFIERS)).modifiers()
        check(attributes.single { it.attribute() == Attributes.ATTACK_DAMAGE }.modifier().amount() == damage.toDouble())
        check(attributes.single { it.attribute() == Attributes.ATTACK_SPEED }.modifier().amount() == speed.toDouble())
        if (enchantment == 0) check(!stack.has(DataComponents.ENCHANTABLE))
        else check(stack.get(DataComponents.ENCHANTABLE)?.value() == enchantment)
        check(enchantments.getOrThrow(Enchantments.UNBREAKING).value().isSupportedItem(stack))
        val key = if (name == "titanium_sword") Enchantments.SHARPNESS else Enchantments.EFFICIENCY
        check(enchantments.getOrThrow(key).value().isSupportedItem(stack))
        check(requireNotNull(stack.get(DataComponents.REPAIRABLE)).items().size() == 0)
        val damaged = stack.copy().also { it.damageValue = 357 }
        check(ItemStack.matches(damaged, FoundationItemStackPersistence.read(FoundationItemStackPersistence.write(damaged, lookup), lookup)))
        val legacy = CompoundTag().also {
            it.putString("id", "galacticraftplanets:$name"); it.putByte("Count", 1); it.putShort("Damage", 357)
        }
        check(FoundationItemStackPersistence.read(legacy, lookup).damageValue == 357)
        val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            ItemStack.STREAM_CODEC.encode(buffer, damaged)
            check(ItemStack.matches(damaged, ItemStack.STREAM_CODEC.decode(buffer)))
        } finally { buffer.release() }
        val json = JsonParser.parseString(Files.readString(resources.resolve("assets/galacticraftplanets/items/$name.json")))
        val model = ItemModels.CODEC.parse(JsonOps.INSTANCE, json.asJsonObject["model"]).getOrThrow() as CuboidItemModelWrapper.Unbaked
        check(model.model() == Identifier.parse("galacticraftplanets:item/$name"))
        val asset = resources.resolve("assets/galacticraftplanets/models/item/$name.json")
        val textures = JsonParser.parseString(Files.readString(asset)).asJsonObject.getAsJsonObject("textures")
        for ((_, value) in textures.entrySet()) {
            val id = Identifier.parse(value.asString)
            check(Files.exists(resources.resolve("assets/${id.namespace}/textures/${id.path}.png")))
        }
    }
    fun tool(name: String) = requireNotNull(ItemStack(FoundationItemProperties.registered(Identifier.parse("galacticraftplanets:$name"))).get(DataComponents.TOOL))
    check(tool("titanium_pickaxe").getMiningSpeed(Blocks.OBSIDIAN.defaultBlockState()) == 14f)
    check(tool("titanium_pickaxe").isCorrectForDrops(Blocks.OBSIDIAN.defaultBlockState()))
    check(tool("titanium_shovel").getMiningSpeed(Blocks.DIRT.defaultBlockState()) == 14f)
    check(tool("titanium_axe").getMiningSpeed(Blocks.OAK_LOG.defaultBlockState()) == 5f)
    check(tool("titanium_axe").getMiningSpeed(Blocks.DANDELION.defaultBlockState()) == 5f)
    val sword = FoundationItemProperties.registered(Identifier.parse("galacticraftplanets:titanium_sword")) as ItemSwordAsteroids
    check(sword.getAttackDamage() == 6f)
    for (hardness in listOf(Float.NEGATIVE_INFINITY, -1f, -0f, 0f, Float.NaN)) check(ItemSwordAsteroids.blockDamage(hardness) == 0)
    for (hardness in listOf(Float.MIN_VALUE, 0.1f, 0.2001f)) check(ItemSwordAsteroids.blockDamage(hardness) == 1)
    for (hardness in listOf(Math.nextUp(0.2001f), 1f, 50f, Float.POSITIVE_INFINITY)) check(ItemSwordAsteroids.blockDamage(hardness) == 2)
    println("Passed five native titanium tool profiles, legacy axe steel stats, effective sword attributes, hoe speed and enchantability, mining and drop predicates, sword hardness cost boundaries, model textures, saves, and packets")
}
