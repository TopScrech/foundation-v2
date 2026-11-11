package foundation.verification

import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariants
import foundation.schematic.FoundationSchematicEntities
import foundation.schematic.FoundationSchematicMesh
import foundation.schematic.FoundationSchematicState
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemSchematics
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.items.ItemSchematic
import micdoodle8.mods.galacticraft.planets.mars.items.ItemSchematicTier2
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.MappedRegistry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.level.storage.TagValueOutput
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistry
import java.nio.file.Files
import java.nio.file.Path

fun verifySchematics(resources: Path, lookup: HolderLookup.Provider) {
    val registry = BuiltInRegistries.ENTITY_TYPE as MappedRegistry<EntityType<*>>
    val forge = ForgeRegistries.ENTITY_TYPES as ForgeRegistry<EntityType<*>>
    registry.unfreeze(); forge.unfreeze()
    forge.register(FoundationSchematicEntities.ID, FoundationSchematicEntities.TYPE)
    forge.freeze(); registry.freeze()
    check(BuiltInRegistries.ENTITY_TYPE.getValue(FoundationSchematicEntities.ID) === FoundationSchematicEntities.TYPE)
    check(FoundationSchematicEntities.TYPE.dimensions.width() == 2f && FoundationSchematicEntities.TYPE.dimensions.height() == 2f)
    check(FoundationSchematicEntities.TYPE.clientTrackingRange() == 10 && FoundationSchematicEntities.TYPE.updateInterval() == 5)

    SchematicRegistry.schematicItems.clear(); SchematicRegistry.textures.clear()
    ItemSchematic.registerSchematicItems(); ItemSchematicTier2.registerSchematicItems(); ItemSchematics.registerSchematicItems()
    ItemSchematic.registerTextures(); ItemSchematicTier2.registerTextures(); ItemSchematics.registerTextures()
    check(SchematicRegistry.schematicItems.size == 12 && SchematicRegistry.textures.size == 12)
    for ((id, count, offset) in listOf(Triple("galacticraftcore:schematic", 2, 0), Triple("galacticraftplanets:schematic", 3, 2), Triple("galaxyspace:schematics", 7, 5))) {
        val item = FoundationItemProperties.registered(Identifier.parse(id)) as ItemSchematic
        for (metadata in 0 until count) {
            val stack = SchematicRegistry.getSchematicItem(offset + metadata)
            check(stack.item === item && FoundationItemVariants.metadata(stack) == metadata)
            if (item !is ItemSchematics) check(item.hangingIndex(metadata) == offset + metadata)
            val texture = requireNotNull(SchematicRegistry.getSchematicTexture(offset + metadata))
            check(Files.exists(resources.resolve("assets/${texture.namespace}/${texture.path}"))) { "Missing schematic texture $texture" }
            stack.shrink(1)
            check(SchematicRegistry.getSchematicItem(offset + metadata).count == 1)
        }
    }
    val galaxy = FoundationItemProperties.registered(Identifier.parse("galaxyspace:schematics")) as ItemSchematics
    val oldSetting = GSConfigCore.enableAdvancedRocketCraft
    try {
        GSConfigCore.enableAdvancedRocketCraft = false
        check(galaxy.creativeStacks().map(FoundationItemVariants::metadata) == listOf(5, 6))
        GSConfigCore.enableAdvancedRocketCraft = true
        check(galaxy.creativeStacks().map(FoundationItemVariants::metadata) == (0..6).toList())
    } finally { GSConfigCore.enableAdvancedRocketCraft = oldSetting }

    val position = BlockPos(-37, 65, 84)
    for (direction in listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
        val state = FoundationSchematicState(position, direction, 4)
        val problems = ProblemReporter.Collector()
        val output = TagValueOutput.createWithContext(problems, lookup)
        state.write(output)
        val restored = FoundationSchematicState.read(TagValueInput.create(problems, lookup, output.buildResult()), position)
        check(state == restored && problems.isEmpty()) { problems.report }
        val legacy = CompoundTag().also {
            it.putInt("TileX", position.x); it.putInt("TileY", position.y); it.putInt("TileZ", position.z)
            it.putByte("Facing", direction.get2DDataValue().toByte()); it.putInt("schem", 4)
        }
        val before = legacy.copy()
        check(FoundationSchematicState.read(TagValueInput.create(problems, lookup, legacy), position) == state)
        check(legacy == before && problems.isEmpty())
        val bounds = FoundationSchematicState.bounds(position, direction)
        check(bounds.ysize == 2.0)
        check(if (direction.axis == Direction.Axis.X) bounds.xsize == 0.0625 && bounds.zsize == 2.0 else bounds.zsize == 0.0625 && bounds.xsize == 2.0)
        check(bounds.minY == position.y.toDouble() && bounds.maxY == position.y + 2.0)
    }
    check(runCatching { FoundationSchematicState.bounds(position, Direction.UP) }.isFailure)
    check(runCatching { FoundationSchematicState(position, Direction.NORTH, -1) }.isFailure)
    val tooFar = CompoundTag().also { it.putInt("TileX", 1000); it.putInt("TileY", 65); it.putInt("TileZ", 84) }
    check(runCatching { FoundationSchematicState.read(TagValueInput.create(ProblemReporter.DISCARDING, lookup, tooFar), position) }.isFailure)
    val mesh = FoundationSchematicMesh.vertices
    check(mesh.size == 96 && mesh.map { it.lightIndex }.toSet() == setOf(0, 1, 2, 3))
    check(mesh.minOf { it.x } == -1f && mesh.maxOf { it.x } == 1f)
    check(mesh.minOf { it.y } == -1f && mesh.maxOf { it.y } == 1f)
    check(mesh.minOf { it.z } == -0.03125f && mesh.maxOf { it.z } == 0.03125f)
    check(mesh.all { it.u in 0f..1f && it.v in 0f..1f && it.nx * it.nx + it.ny * it.ny + it.nz * it.nz == 1f })
    for (quad in mesh.chunked(4)) check(quad.map { it.lightIndex }.distinct().size == 1)

    val originalPages = ArrayList(SchematicRegistry.schematicRecipes)
    try {
        SchematicRegistry.schematicRecipes.clear()
        val required = SchematicRegistry.getSchematicItem(1)
        val page = object : ISchematicPage {
            override fun getPageID(): Int = 4321
            override fun getGuiID(): Int = 1234
            override fun getRequiredItem(): ItemStack = required
            override fun compareTo(other: ISchematicPage): Int = getPageID().compareTo(other.getPageID())
            override fun getResultScreen(player: Player?, pos: BlockPos?): Screen? = null
            override fun getResultContainer(player: Player?, pos: BlockPos?): AbstractContainerMenu? = null
        }
        SchematicRegistry.registerSchematicRecipe(page)
        val named = required.copyWithCount(7).also {
            it.set(DataComponents.CUSTOM_NAME, Component.literal("Named blueprint"))
            it.set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().also { data -> data.putInt("retained", 42) }))
        }
        check(SchematicRegistry.getMatchingRecipeForItemStack(named) === page)
        check(SchematicRegistry.getMatchingRecipeForItemStack(SchematicRegistry.getSchematicItem(0)) == null)
        check(SchematicRegistry.getMatchingRecipeForItemStack(ItemStack.EMPTY) == null)
        check(SchematicRegistry.getMatchingRecipeForID(4321) === page)
    } finally { SchematicRegistry.schematicRecipes.clear(); SchematicRegistry.schematicRecipes.addAll(originalPages) }
    println("Passed twelve schematic item and texture mappings, native entity type registration, legacy and native anchor persistence, four-direction bounds, 96-vertex geometry, creative settings, independent pick stacks, and metadata-only page lookup")
}
