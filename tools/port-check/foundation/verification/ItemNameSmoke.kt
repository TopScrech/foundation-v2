package foundation.verification

import com.google.gson.JsonParser
import foundation.item.FoundationItemProperties
import foundation.persistence.FoundationItemStackPersistence
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import java.nio.file.Files
import java.nio.file.Path

fun verifyPlainItemNames(resources: Path, lookup: HolderLookup.Provider) {
    val standard = listOf(
        "galacticraftcore:oxygen_mask", "galacticraftcore:oxygen_gear", "galacticraftcore:oxygen_tank_light_full",
        "galacticraftcore:oxygen_tank_med_full", "galacticraftcore:oxygen_tank_heavy_full",
        "galacticraftcore:steel_pickaxe", "galacticraftcore:steel_axe", "galacticraftcore:steel_hoe",
        "galacticraftcore:steel_shovel", "galacticraftcore:steel_sword", "galacticraftcore:cheese_curd", "galacticraftcore:emergency_kit",
        "galacticraftplanets:titanium_pickaxe", "galacticraftplanets:titanium_axe", "galacticraftplanets:titanium_hoe",
        "galacticraftplanets:titanium_shovel", "galacticraftplanets:titanium_sword"
    ).map { name -> name to "item.${Identifier.parse(name).path}.name" }
    val profiles = standard + listOf(
        "galacticraftcore:key" to "item.key.t1.name",
        "galacticraftplanets:key" to "item.key.t2.name",
        "galacticraftplanets:key_t3" to "item.key.t3.name"
    )
    for ((name, translation) in profiles) {
        val id = Identifier.parse(name)
        val stack = ItemStack(FoundationItemProperties.registered(id))
        check((stack.hoverName.contents as TranslatableContents).key == translation) { "Incorrect native item description for $name" }
        check((requireNotNull(stack.get(DataComponents.ITEM_NAME)).contents as TranslatableContents).key == translation)
        val language = JsonParser.parseString(Files.readString(resources.resolve("assets/${id.namespace}/lang/en_us.json"))).asJsonObject
        check(language.has(translation) && language[translation].asString.isNotBlank())
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Retained custom item name"))
        check(stack.hoverName.string == "Retained custom item name")
        val restored = FoundationItemStackPersistence.read(FoundationItemStackPersistence.write(stack, lookup), lookup)
        check(restored.hoverName.string == "Retained custom item name")
    }
    println("Passed twenty native plain-item description keys, retained language entries, and custom name persistence")
}
