package foundation.recipe

import net.minecraft.resources.Identifier
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import micdoodle8.mods.galacticraft.core.recipe.IngredientAdvancedMetal

object FoundationIngredients {
    @JvmField val STACK_ID: Identifier = Identifier.parse("foundation:stack")

    fun install(context: FMLJavaModLoadingContext) {
        RegisterEvent.getBus(context.modBusGroup).addListener { event: RegisterEvent ->
            event.register(ForgeRegistries.Keys.INGREDIENT_SERIALIZERS, STACK_ID) { FoundationStackIngredient.SERIALIZER }
            event.register(ForgeRegistries.Keys.INGREDIENT_SERIALIZERS, IngredientAdvancedMetal.ID) { IngredientAdvancedMetal.SERIALIZER }
        }
    }
}
