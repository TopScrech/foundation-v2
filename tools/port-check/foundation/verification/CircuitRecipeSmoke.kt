package foundation.verification

import foundation.item.FoundationItemVariants
import foundation.item.FoundationRecipeStacks
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData

fun verifyCircuitRecipes() {
    val moon = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:item_basic_moon")))
    val sapphire = FoundationItemVariants.stack(moon, 1, 2)
    val output = ItemStack(Items.DIAMOND, 2)
    val source = mutableListOf<Any?>(ItemStack(Items.DIAMOND), listOf(sapphire, ItemStack(Items.EMERALD)), sapphire, ItemStack(Items.REDSTONE), null)
    val input = mutableListOf(ItemStack(Items.DIAMOND, 5), sapphire.copyWithCount(3), sapphire.copy(), ItemStack(Items.REDSTONE), ItemStack.EMPTY)
    CircuitFabricatorRecipes.addRecipe(output, source)
    source.clear()
    output.count = 1
    sapphire.count = 0
    check(CircuitFabricatorRecipes.getOutputForInput(input).count == 2)
    CircuitFabricatorRecipes.getOutputForInput(input).count = 0
    CircuitFabricatorRecipes.getOutput(0).count = 0
    (CircuitFabricatorRecipes.getRecipes()[0][0] as ItemStack).count = 0
    check(CircuitFabricatorRecipes.getOutputForInput(input).count == 2)
    check(CircuitFabricatorRecipes.slotValidItems.size == 5 && CircuitFabricatorRecipes.slotValidItems[4].isEmpty())
    input[1] = ItemStack(Items.EMERALD)
    check(CircuitFabricatorRecipes.getOutputForInput(input).count == 2)
    input[2] = FoundationItemVariants.stack(moon, 1, 1)
    check(CircuitFabricatorRecipes.getOutputForInput(input).isEmpty)
    input[2] = FoundationItemVariants.stack(moon, 1, 2)
    input[4] = ItemStack(Items.STONE)
    check(CircuitFabricatorRecipes.getOutputForInput(input).isEmpty)
    input[4] = ItemStack.EMPTY
    check(CircuitFabricatorRecipes.getOutputForInput(mutableListOf()).isEmpty)

    fun tagged(value: Boolean, extra: Boolean = false): ItemStack = input[2].copy().also { stack ->
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().also {
            it.putBoolean("NoPlacing", value)
            if (extra) it.putInt("Extra", 1)
        }))
    }
    check(FoundationRecipeStacks.matches(input[2], tagged(true)))
    check(FoundationRecipeStacks.matches(tagged(false), input[2]))
    check(!FoundationRecipeStacks.matches(input[2], tagged(true, true)))
    check(!FoundationRecipeStacks.matches(tagged(true), tagged(false)))
    input[2] = tagged(true)
    check(CircuitFabricatorRecipes.getOutputForInput(input).count == 2)
    input[2] = tagged(true, true)
    check(CircuitFabricatorRecipes.getOutputForInput(input).isEmpty)

    val replacement = mutableListOf<ItemStack?>(ItemStack(Items.COAL), ItemStack(Items.CHARCOAL))
    CircuitFabricatorRecipes.replaceRecipeIngredient(FoundationItemVariants.stack(moon, 1, 2), replacement)
    replacement.clear()
    input[1] = ItemStack(Items.CHARCOAL)
    input[2] = ItemStack(Items.COAL)
    check(CircuitFabricatorRecipes.getOutputForInput(input).count == 2)
    check(CircuitFabricatorRecipes.slotValidItems[2].none { it?.item === moon })
    val count = CircuitFabricatorRecipes.getRecipes().size
    check(runCatching { CircuitFabricatorRecipes.addRecipe(ItemStack(Items.STONE), mutableListOf(null)) }.isFailure)
    check(runCatching { CircuitFabricatorRecipes.addRecipe(ItemStack(Items.STONE), mutableListOf(null, null, null, null, listOf("invalid"))) }.isFailure)
    check(runCatching { CircuitFabricatorRecipes.replaceRecipeIngredient(input[0], mutableListOf(null)) }.isFailure)
    check(CircuitFabricatorRecipes.getRecipes().size == count)
    CircuitFabricatorRecipes.removeRecipe(ItemStack(Items.DIAMOND, 2))
    check(CircuitFabricatorRecipes.getRecipes().isEmpty())
    check(CircuitFabricatorRecipes.slotValidItems.all { it.isEmpty() })
    println("Passed circuit recipe alternatives, optional slots, legacy custom data, replacement, removal, and snapshot isolation")
}
