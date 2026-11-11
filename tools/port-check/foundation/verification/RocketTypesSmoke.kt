package foundation.verification

import com.google.gson.JsonParser
import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType
import net.minecraft.locale.Language
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.FormattedCharSequence
import java.nio.file.Files
import java.nio.file.Path

fun verifyRocketTypes(resources: Path) {
    val original = Language.getInstance()
    val entries = JsonParser.parseString(Files.readString(resources.resolve("assets/galacticraftcore/lang/en_us.json"))).asJsonObject
    val translations = (0..3).associate { index ->
        val key = "gui.rocket_type.$index"
        key to requireNotNull(entries[key]).asString
    }
    fun language(values: Map<String, String>) = object : Language() {
        override fun getOrDefault(key: String, fallback: String): String = values[key] ?: original.getOrDefault(key, fallback)
        override fun has(key: String): Boolean = key in values || original.has(key)
        override fun isDefaultRightToLeft(): Boolean = original.isDefaultRightToLeft
        override fun getVisualOrder(text: FormattedText): FormattedCharSequence = original.getVisualOrder(text)
        override fun getLanguageData(): Map<String, String> = original.languageData + values
    }
    try {
        Language.inject(language(translations))
        val types = EnumRocketType.entries
        check(types.map { it.getIndex() } == listOf(0, 1, 2, 3, 4))
        check(types.map { it.getInventorySpace() } == listOf(2, 20, 38, 56, 2))
        check(types.map { it.getPreFueled() } == listOf(false, false, false, false, true))
        check(types.first().getTooltip().isEmpty())
        check(types.drop(1).map { it.getTooltip() } == translations.values.toList())
        val refreshed = translations.mapValues { (_, value) -> "Reloaded: $value" }
        Language.inject(language(refreshed))
        check(types.drop(1).map { it.getTooltip() } == refreshed.values.toList())
        check(types.first().getTooltip().isEmpty())
    } finally {
        Language.inject(original)
    }
    println("Passed five native rocket type indexes, inventory capacities, prefueled settings, retained translations, and language reloads")
}
