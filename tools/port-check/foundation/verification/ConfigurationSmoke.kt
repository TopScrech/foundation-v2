package foundation.verification

import galaxyspace.core.configs.GSConfigDimensions
import foundation.input.FoundationKeyCodes
import foundation.config.FoundationConfiguration
import foundation.config.FoundationConfigElement
import java.nio.file.Files

fun verifyConfigurationRoundTrip() {
    val directory = Files.createTempDirectory("foundation-config-check-")
    val file = directory.resolve("settings.conf")
    try {
        Files.writeString(file, """
            # Configuration file
            ~CONFIG_VERSION: 1

            general {
                # Keep imported IDs
                I:dimension=-28
                B:enabled=true
                S:pattern="\d+\w"
                S:unrecognized="keep me"
                D:coefficient=1.25
                S:"label=with<punctuation"="Moon \"base\""
                S:blocks <
                    minecraft:stone
                    ""
                    "#a real value"
                >
                nested {
                    I:count=7
                }
            }
        """.trimIndent())
        val config = FoundationConfiguration(file.toFile())
        check(config.get("general", "dimension", 0).getInt() == -28)
        check(config.get("general", "enabled", false).getBoolean())
        check(config.get("general", "pattern", "").getString() == "\\d+\\w")
        check(config.get("general", "coefficient", 0.0).getDouble() == 1.25)
        check(config.get("general.nested", "count", 0).getInt() == 7)
        val names = config.get("general", "blocks", emptyArray<String>()).getStringList()
        check(names.contentEquals(arrayOf("minecraft:stone", "", "#a real value")))
        check(config.get("general", "label=with<punctuation", "").getString() == "Moon \"base\"")
        config.get("general", "fresh", 42, "A new setting")
        check(config.hasChanged())
        config.save()
        check(!config.hasChanged())
        val reread = FoundationConfiguration(file.toFile())
        check(reread.get("general", "dimension", 0).getInt() == -28)
        check(reread.get("general", "blocks", emptyArray<String>()).getStringList().contentEquals(names))
        check(reread.get("general", "fresh", 0).getInt() == 42)
        check(reread.get("general", "unrecognized", "missing").getString() == "keep me")
        check(reread.get("general", "fresh", 0).getComment() == "A new setting")
        check(reread.moveProperty("general", "fresh", "general.nested"))
        check(!reread.hasKey("general", "fresh"))
        check(reread.get("general.nested", "fresh", 0).getInt() == 42)
        check(FoundationConfigElement(reread.getCategory("general")).getChildElements().any { it.getName() == "nested" })
        reread.save()
        val original = Files.readString(file)
        Files.writeString(file, "general {\nS:broken <\n")
        check(runCatching { reread.load() }.isFailure)
        check(reread.get("general", "dimension", 0).getInt() == -28)
        check(Files.readString(file) == "general {\nS:broken <\n")
        Files.writeString(file, original)
        check(Files.list(directory).use { it.count() } == 1L)
        check(FoundationKeyCodes.fromLegacyName("KEY_A") == 65)
        check(FoundationKeyCodes.fromLegacyName("KEY_0") == 48)
        check(FoundationKeyCodes.fromLegacyName("KEY_SPACE") == null)
        val moduleFile = directory.resolve("dimensions.conf")
        try {
            Files.writeString(moduleFile, "dimensions {\nI:dimensionIDMercury=-2005\n}\n")
            GSConfigDimensions(moduleFile.toFile())
            check(GSConfigDimensions.dimensionIDMercury == -2005)
            check(FoundationConfiguration(moduleFile.toFile()).get("dimensions", "dimensionIDMercury", 0).getInt() == -2005)
        } finally {
            Files.deleteIfExists(moduleFile)
        }
    } finally {
        Files.deleteIfExists(file)
        Files.deleteIfExists(directory)
    }
}
