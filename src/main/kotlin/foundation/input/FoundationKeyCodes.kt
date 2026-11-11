package foundation.input

/** Printable GLFW key values are Unicode/ASCII, unlike the LWJGL 2 scan codes in 1.12 */
object FoundationKeyCodes {
    @JvmStatic fun fromLegacyName(name: String?): Int? {
        if (name == null || !name.startsWith("KEY_") || name.length != 5) return null
        val character = name.last()
        return if (character in 'A'..'Z' || character in '0'..'9') character.code else null
    }
}
