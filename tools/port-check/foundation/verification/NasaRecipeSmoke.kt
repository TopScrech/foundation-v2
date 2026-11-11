package foundation.verification

import foundation.item.FoundationItemVariants
import micdoodle8.mods.galacticraft.core.inventory.InventoryRocketBench
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.DyeColor
import java.util.HashMap

fun verifyNasaRecipes() {
    val moon = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:item_basic_moon")))
    val ingredient = FoundationItemVariants.stack(moon, 1, 2)
    val output = FoundationItemVariants.stack(moon, 3, 1)
    val source = hashMapOf<Int, ItemStack?>(1 to ingredient, 2 to ItemStack.EMPTY)
    val recipe = NasaWorkbenchRecipe(output, source)
    source.clear()
    ingredient.count = 0
    output.count = 1
    check(recipe.getRecipeSize() == 2 && recipe.getRecipeOutput().count == 3)
    recipe.getRecipeOutput().count = 0
    requireNotNull(recipe.getRecipeInput()[1]).count = 0
    check(recipe.getRecipeOutput().count == 3 && requireNotNull(recipe.getRecipeInput()[1]).count == 1)
    val menu = object : AbstractContainerMenu(MenuType.CRAFTING, 0) {
        override fun quickMoveStack(player: Player, slot: Int): ItemStack = ItemStack.EMPTY
        override fun stillValid(player: Player): Boolean = true
    }
    val inventory = InventoryRocketBench(menu)
    check(!recipe.matches(inventory) && !recipe.matches(null))
    inventory.setItem(1, FoundationItemVariants.stack(moon, 7, 2).also {
        it.set(DataComponents.CUSTOM_NAME, Component.literal("Custom name"))
    })
    check(recipe.matches(inventory))
    inventory.setItem(1, FoundationItemVariants.stack(moon, 1, 0))
    check(!recipe.matches(inventory))
    inventory.setItem(1, FoundationItemVariants.stack(moon, 1, 2))
    inventory.setItem(2, ItemStack(Items.STONE))
    check(!recipe.matches(inventory))
    check(!NasaWorkbenchRecipe(ItemStack(Items.STONE), hashMapOf(18 to ItemStack.EMPTY)).matches(inventory))
    check(!NasaWorkbenchRecipe.matchesIngredient(ItemStack(Items.WOOL.pick(DyeColor.RED)), ItemStack(Items.WOOL.pick(DyeColor.BLUE))))
    check(!NasaWorkbenchRecipe.matchesIngredient(ItemStack(Items.STONE), null))
    val pickaxe = ItemStack(Items.DIAMOND_PICKAXE).also { it.damageValue = 42 }
    check(NasaWorkbenchRecipe.matchesIngredient(pickaxe, pickaxe.copy()))
    check(!NasaWorkbenchRecipe.matchesIngredient(pickaxe, ItemStack(Items.DIAMOND_PICKAXE)))
    check(runCatching { NasaWorkbenchRecipe(ItemStack.EMPTY, hashMapOf(-1 to ItemStack.EMPTY)) }.isFailure)
    check(runCatching { NasaWorkbenchRecipe(ItemStack.EMPTY, HashMap<Int, ItemStack?>().also { it[1] = null }) }.isFailure)
    println("Passed native NASA recipe layouts, variant and durability matching, empty slots, and independent recipe snapshots")
}
