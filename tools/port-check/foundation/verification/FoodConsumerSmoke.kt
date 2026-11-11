package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationFoods
import foundation.item.FoundationItemProperties
import foundation.item.FoundationItemVariants
import foundation.persistence.FoundationItemStackPersistence
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.world.food.FoodData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs

fun verifyFoodConsumers(resources: Path, lookup: HolderLookup.Provider) {
    fun item(id: String) = FoundationItemProperties.registered(Identifier.parse(id))
    val cheese = ItemStack(item("galacticraftcore:cheese_curd"), 2)
    val yellow = FoundationItemVariants.stack(item("galaxyspace:br_foods"), 1, 0)
    check(requireNotNull(FoundationFoods.properties(cheese)).nutrition() == 1)
    check(abs(requireNotNull(FoundationFoods.properties(cheese)).saturation() - 0.2f) < 0.00001f)
    check(requireNotNull(FoundationFoods.properties(yellow)).nutrition() == 4)
    check(abs(requireNotNull(FoundationFoods.properties(yellow)).saturation() - 3.2f) < 0.00001f)
    for (stack in listOf(cheese, yellow)) {
        check(stack.has(DataComponents.CONSUMABLE) && !stack.has(DataComponents.USE_REMAINDER))
        val saved = FoundationItemStackPersistence.write(stack, lookup)
        check(ItemStack.matches(stack, FoundationItemStackPersistence.read(saved, lookup)))
        val old = CompoundTag().also {
            it.putString("id", if (stack === cheese) "galacticraftcore:cheese_curd" else "galaxyspace:br_foods")
            it.putByte("Count", 1); it.putShort("Damage", 0)
        }
        check(FoundationFoods.properties(FoundationItemStackPersistence.read(old, lookup)) == FoundationFoods.properties(stack))
        check(FoundationFoods.requiresBreathableAir(stack))
    }
    val canned = FoundationItemVariants.stack(item("galacticraftcore:food"), 2, 3)
    check(!FoundationFoods.requiresBreathableAir(canned))
    check(!FoundationFoods.requiresBreathableAir(ItemStack(item("foundation_test:space_food"))))
    check(FoundationFoods.requiresBreathableAir(ItemStack(Items.BREAD)))
    check(!FoundationFoods.requiresBreathableAir(ItemStack(Items.DIAMOND)))
    check(!FoundationFoods.requiresBreathableAir(ItemStack.EMPTY))
    check(FoundationFoods.wolfHealingNutrition(ItemStack(Items.BEEF)) == 3)
    check(FoundationFoods.wolfHealingNutrition(ItemStack(Items.ROTTEN_FLESH)) == 4)
    check(FoundationFoods.wolfHealingNutrition(cheese) == null && FoundationFoods.wolfHealingNutrition(canned) == null)
    check(FoundationFoods.wolfHealingNutrition(yellow) == null && FoundationFoods.wolfHealingNutrition(ItemStack.EMPTY) == null)

    val hunger = FoodData().also { it.foodLevel = 10; it.setSaturation(0f) }
    val bread = ItemStack(Items.BREAD)
    val stacks = listOf(ItemStack.EMPTY, ItemStack(Items.DIAMOND), cheese, bread)
    check(FoundationFoods.consumeFirst(stacks, hunger))
    check(hunger.foodLevel == 11 && abs(hunger.saturationLevel - 0.2f) < 0.00001f)
    check(cheese.count == 1 && bread.count == 1)
    check(FoundationFoods.consumeFirst(stacks, hunger) && cheese.isEmpty && hunger.foodLevel == 12)
    check(FoundationFoods.consumeFirst(stacks, hunger) && bread.isEmpty && hunger.foodLevel == 17)
    check(!FoundationFoods.consumeFirst(stacks, hunger))
    hunger.foodLevel = 19
    check(FoundationFoods.consumeFirst(listOf(yellow), hunger) && yellow.isEmpty && hunger.foodLevel == 20)
    val full = canned.copy()
    check(!FoundationFoods.consumeFirst(listOf(full), hunger) && full.count == 2)
    hunger.foodLevel = 10
    val cannedAuto = canned.copy()
    check(FoundationFoods.consumeFirst(listOf(cannedAuto), hunger) && hunger.foodLevel == 12 && cannedAuto.count == 1)
    check(cannedAuto.get(DataComponents.USE_REMAINDER) == null)
    check(!FoundationFoods.consumeFirst(emptyList(), hunger))
    check(galaxyspace.systems.BarnardsSystem.core.prefab.items.ItemFoodBR.BR_Food.byMetadata(-1) == null)
    check(runCatching { FoundationItemVariants.stack(item("galaxyspace:br_foods"), 1, 1) }.isFailure)
    val json = JsonParser.parseString(Files.readString(resources.resolve("assets/galacticraftcore/items/cheese_curd.json")))
    val model = ItemModels.CODEC.parse(JsonOps.INSTANCE, json.asJsonObject["model"]).getOrThrow() as CuboidItemModelWrapper.Unbaked
    check(model.model() == Identifier.parse("galacticraftcore:item/cheese_curd"))
    println("Passed native cheese and Barnard food profiles, legacy imports, autofeeding hunger and saturation changes, inventory priority, full-hunger protection, oxygen food rules, wolf meat healing eligibility, and invalid variants")
}
