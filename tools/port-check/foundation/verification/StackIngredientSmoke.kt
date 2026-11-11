package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationItemVariants
import foundation.recipe.FoundationIngredients
import foundation.recipe.FoundationStackIngredient
import micdoodle8.mods.galacticraft.core.recipe.IngredientAdvancedMetal
import io.netty.buffer.Unpooled
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.FileToIdConverter
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.minecraftforge.common.crafting.ingredients.IIngredientSerializer
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry
import net.minecraftforge.registries.NewRegistryEvent
import net.minecraftforge.registries.RegistryBuilder
import net.minecraftforge.registries.RegistryManager
import net.minecraftforge.registries.holdersets.HolderSetType
import java.nio.file.Files
import java.nio.file.Path

fun verifyStackIngredients(resources: Path, lookup: HolderLookup.Provider) {
    if (ForgeRegistries.INGREDIENT_SERIALIZERS.get() == null || ForgeRegistries.HOLDER_SET_TYPES.get() == null) {
        NewRegistryEvent.BUS.addListener { event: NewRegistryEvent ->
            if (ForgeRegistries.INGREDIENT_SERIALIZERS.get() == null) event.create(RegistryBuilder<IIngredientSerializer<*>>()
                .setName(ForgeRegistries.Keys.INGREDIENT_SERIALIZERS.identifier())
                .disableSaving().disableSync())
            if (ForgeRegistries.HOLDER_SET_TYPES.get() == null) event.create(RegistryBuilder<HolderSetType>()
                .setName(ForgeRegistries.Keys.HOLDER_SET_TYPES.identifier())
                .disableSaving().disableSync())
        }
        RegistryManager.postNewRegistryEvent()
    }
    val serializers = ForgeRegistries.INGREDIENT_SERIALIZERS.get() as ForgeRegistry<IIngredientSerializer<*>>
    serializers.unfreeze()
    serializers.register(FoundationIngredients.STACK_ID, FoundationStackIngredient.SERIALIZER)
    serializers.register(IngredientAdvancedMetal.ID, IngredientAdvancedMetal.SERIALIZER)
    serializers.freeze()

    val ingots = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galaxyspace:ingots")))
    val source = FoundationItemVariants.stack(ingots, 7, 1)
    val ingredient = FoundationStackIngredient.of(source)
    source.count = 0
    check(ingredient.test(FoundationItemVariants.stack(ingots, 3, 1)))
    check(!ingredient.test(FoundationItemVariants.stack(ingots, 3, 0)))
    check(!ingredient.test(ItemStack.EMPTY) && !ingredient.isSimple)
    val custom = FoundationItemVariants.stack(ingots, 1, 1).also {
        it.set(DataComponents.CUSTOM_NAME, Component.literal("Custom name"))
        it.set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().also { tag -> tag.putInt("unrelated", 42) }))
    }
    check(ingredient.test(custom))
    val shown = (ingredient.display() as SlotDisplay.ItemStackSlotDisplay).stack().create()
    check(shown.count == 1 && FoundationItemVariants.metadata(shown) == 1)
    val pickaxe = ItemStack(Items.DIAMOND_PICKAXE).also { it.damageValue = 42 }
    val damaged = FoundationStackIngredient.of(pickaxe)
    check(damaged.test(pickaxe.copy()))
    check(!damaged.test(pickaxe.copy().also { it.damageValue = 43 }))
    check(ingredient == FoundationStackIngredient.of(FoundationItemVariants.stack(ingots, 1, 1)))
    check(ingredient != FoundationStackIngredient.of(FoundationItemVariants.stack(ingots, 1, 2)))
    check(runCatching { FoundationStackIngredient.of(ItemStack.EMPTY) }.isFailure)
    val anyMetal = Ingredient.of(ingots)
    check(!anyMetal.equals(ingredient) && !ingredient.equals(anyMetal))
    val asymmetric = ShapedRecipePattern.of(mapOf('A' to anyMetal, 'B' to ingredient), "AB")
    check(asymmetric.matches(CraftingInput.of(2, 1, listOf(
        FoundationItemVariants.stack(ingots, 1, 1), FoundationItemVariants.stack(ingots, 1, 0)
    ))))

    val ops = RegistryOps.create(JsonOps.INSTANCE, lookup)
    val encoded = Ingredient.CODEC.encodeStart(ops, ingredient).getOrThrow()
    val decoded = Ingredient.CODEC.parse(ops, encoded).getOrThrow()
    check(decoded is FoundationStackIngredient && decoded.test(custom))
    check(!decoded.test(FoundationItemVariants.stack(ingots, 1, 2)))
    val invalid = JsonParser.parseString("""{"type":"foundation:stack","stack":{"id":"galaxyspace:ingots","components":{"foundation:legacy_metadata":99}}}""")
    check(Ingredient.CODEC.parse(ops, invalid).error().isPresent)
    val access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
    val selectors = listOf(
        Triple("meteoric_iron_ingot", "galacticraftcore:item_basic_moon", 0),
        Triple("meteoric_iron_plate", "galacticraftcore:item_basic_moon", 1),
        Triple("desh_ingot", "galacticraftplanets:item_basic_mars", 2),
        Triple("desh_plate", "galacticraftplanets:item_basic_mars", 5),
        Triple("titanium_ingot", "galacticraftplanets:item_basic_asteroids", 0),
        Triple("titanium_plate", "galacticraftplanets:item_basic_asteroids", 6),
        Triple("lead_ingot", "galacticraftplanets:basic_item_venus", 1)
    )
    for ((metal, itemId, meta) in selectors) {
        val json = JsonParser.parseString("""{"type":"galacticraftcore:advanced_metal","metal":"$metal"}""")
        val parsed = Ingredient.CODEC.parse(ops, json).getOrThrow()
        val item = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId)))
        check(parsed.test(FoundationItemVariants.stack(item, 4, meta)))
        val wrongMeta = if (meta == 0) 1 else 0
        check(!parsed.test(FoundationItemVariants.stack(item, 1, wrongMeta)))
        val canonical = Ingredient.CODEC.encodeStart(ops, parsed).getOrThrow().asJsonObject
        check(canonical["type"].asString == "foundation:stack")
        check(Ingredient.CODEC.parse(ops, canonical).getOrThrow().test(FoundationItemVariants.stack(item, 1, meta)))
        val alias = IngredientAdvancedMetal.CODEC.codec().encodeStart(ops, parsed as FoundationStackIngredient).getOrThrow()
        check(alias.asJsonObject["metal"].asString == metal)
    }
    val unknown = JsonParser.parseString("""{"type":"galacticraftcore:advanced_metal","metal":"unknown"}""")
    check(Ingredient.CODEC.parse(ops, unknown).error().isPresent)
    val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
    try {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient)
        val received = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer)
        check(received.test(custom) && !received.test(FoundationItemVariants.stack(ingots, 1, 2)))
    } finally { buffer.release() }

    val nuggets = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galaxyspace:nuggets")))
    for ((meta, metal) in listOf("cobaltum", "magnesium", "nickel").withIndex()) {
        for (toNuggets in listOf(true, false)) {
            val suffix = if (toNuggets) "nuggets_from_ingot" else "ingot_from_nuggets"
            val file = FileToIdConverter.registry(Registries.RECIPE).idToFile(Identifier.parse("galaxyspace:${metal}_$suffix"))
            val json = JsonParser.parseString(Files.readString(resources.resolve("data/${file.namespace}/${file.path}")))
            val recipe = Recipe.CODEC.parse(ops, json).getOrThrow().value() as ShapedRecipe
            val pattern = ShapedRecipePattern.MAP_CODEC.codec().parse(ops, json).getOrThrow()
            val from = if (toNuggets) ingots else nuggets
            val to = if (toNuggets) nuggets else ingots
            val size = recipe.width * recipe.height
            val stacks = MutableList(size) { FoundationItemVariants.stack(from, 1, meta) }
            val input = CraftingInput.of(recipe.width, recipe.height, stacks)
            check(pattern.matches(input))
            val result = recipe.assemble(input)
            check(result.item === to && result.count == if (toNuggets) 9 else 1)
            check(FoundationItemVariants.metadata(result) == meta)
            stacks[0] = FoundationItemVariants.stack(from, 1, (meta + 1) % 3)
            check(!pattern.matches(CraftingInput.of(recipe.width, recipe.height, stacks)))
            val wire = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
            try {
                ShapedRecipe.STREAM_CODEC.encode(wire, recipe)
                val received = ShapedRecipe.STREAM_CODEC.decode(wire)
                check(received.ingredients.all { it.orElseThrow().test(FoundationItemVariants.stack(from, 1, meta)) })
                check(received.ingredients.none { it.orElseThrow().test(FoundationItemVariants.stack(from, 1, (meta + 1) % 3)) })
                check(ItemStack.isSameItemSameComponents(received.assemble(input), result))
            } finally { wire.release() }
        }
    }
    for ((name, outputId, metadata) in listOf(
        Triple("item_basic_mars_1", "galacticraftplanets:item_basic_mars", 1),
        Triple("basic_item_venus_3", "galacticraftplanets:basic_item_venus", 3)
    )) {
        val file = FileToIdConverter.registry(Registries.RECIPE).idToFile(Identifier.parse("galacticraftplanets:$name"))
        val json = JsonParser.parseString(Files.readString(resources.resolve("data/${file.namespace}/${file.path}")))
        val recipe = Recipe.CODEC.parse(ops, json).getOrThrow().value() as ShapedRecipe
        val pattern = ShapedRecipePattern.MAP_CODEC.codec().parse(ops, json).getOrThrow()
        val stacks = recipe.ingredients.map { (it.orElseThrow() as FoundationStackIngredient).matchingStack() }.toMutableList()
        val input = CraftingInput.of(recipe.width, recipe.height, stacks)
        check(pattern.matches(input))
        val result = recipe.assemble(input)
        check(BuiltInRegistries.ITEM.getKey(result.item) == Identifier.parse(outputId))
        check(result.count == 1 && FoundationItemVariants.metadata(result) == metadata)
        val first = stacks[0]
        stacks[0] = FoundationItemVariants.stack(first.item, 1, 0)
        check(!pattern.matches(CraftingInput.of(recipe.width, recipe.height, stacks)))
        val wire = RegistryFriendlyByteBuf(Unpooled.buffer(), access)
        try {
            ShapedRecipe.STREAM_CODEC.encode(wire, recipe)
            val received = ShapedRecipe.STREAM_CODEC.decode(wire)
            check(ItemStack.isSameItemSameComponents(received.assemble(input), result))
            check(!received.ingredients[0].orElseThrow().test(stacks[0]))
        } finally { wire.release() }
    }
    println("Passed native stack ingredient JSON and network codecs, seven advanced metal selectors, mirrored patterns, recipe displays, and eight native recipes")
}
