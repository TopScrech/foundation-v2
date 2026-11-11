package foundation.config

/** Typed values in Foundation's retained, human-readable .conf format */
class FoundationConfigProperty internal constructor(
    private val name: String,
    internal var type: Type,
    internal var list: Boolean,
    initial: List<String>,
    private val changed: () -> Unit
) {
    enum class Type(val prefix: Char) { STRING('S'), INTEGER('I'), BOOLEAN('B'), DOUBLE('D'), COLOR('H'), MOD_ID('M') }
    private var values = initial.toList()
    internal var defaults = initial.toList()
    private var comment = ""
    private var languageKey = ""
    private var restartMinecraft = false
    private var restartWorld = false
    private var showInGui = true
    private var minimum: Double? = null
    private var maximum: Double? = null

    fun getName(): String = name
    fun getType(): Type = type
    fun isList(): Boolean = list
    fun getString(): String = values.firstOrNull().orEmpty()
    fun getStringList(): Array<String?> = values.map { it as String? }.toTypedArray()
    fun getInt(defaultValue: Int? = null): Int = (getString().toIntOrNull()
        ?: defaultValue ?: defaults.firstOrNull()?.toIntOrNull() ?: 0)
        .coerceIn(minimum?.toInt() ?: Int.MIN_VALUE, maximum?.toInt() ?: Int.MAX_VALUE)
    fun getDouble(defaultValue: Double? = null): Double =
        (getString().toDoubleOrNull()?.takeIf { it.isFinite() }
            ?: defaultValue ?: defaults.firstOrNull()?.toDoubleOrNull()?.takeIf { it.isFinite() } ?: 0.0)
            .coerceIn(minimum ?: -Double.MAX_VALUE, maximum ?: Double.MAX_VALUE)
    fun getBoolean(defaultValue: Boolean? = null): Boolean = getString().lowercase().toBooleanStrictOrNull()
        ?: defaultValue ?: defaults.firstOrNull()?.lowercase()?.toBooleanStrictOrNull() ?: false
    fun getIntList(): IntArray = values.mapIndexed { index, value ->
        value.toIntOrNull() ?: defaults.getOrNull(index)?.toIntOrNull() ?: 0
    }.toIntArray()
    fun getDoubleList(): DoubleArray = values.mapIndexed { index, value ->
        value.toDoubleOrNull() ?: defaults.getOrNull(index)?.toDoubleOrNull() ?: 0.0
    }.toDoubleArray()
    fun getBooleanList(): BooleanArray = values.mapIndexed { index, value ->
        value.lowercase().toBooleanStrictOrNull() ?: defaults.getOrNull(index)?.lowercase()?.toBooleanStrictOrNull() ?: false
    }.toBooleanArray()

    fun set(value: Any?): FoundationConfigProperty {
        val updated = encode(value)
        if (values != updated || list != isListValue(value)) {
            values = updated
            list = isListValue(value)
            changed()
        }
        return this
    }

    fun setComment(value: String?): FoundationConfigProperty {
        val next = value.orEmpty()
        if (comment != next) { comment = next; changed() }
        return this
    }
    fun getComment(): String = comment
    fun setLanguageKey(value: String?): FoundationConfigProperty { languageKey = value.orEmpty(); return this }
    fun getLanguageKey(): String = languageKey
    fun setRequiresMcRestart(value: Boolean): FoundationConfigProperty { restartMinecraft = value; return this }
    fun requiresMcRestart(): Boolean = restartMinecraft
    fun setRequiresWorldRestart(value: Boolean): FoundationConfigProperty { restartWorld = value; return this }
    fun requiresWorldRestart(): Boolean = restartWorld || restartMinecraft
    fun setShowInGui(value: Boolean): FoundationConfigProperty { showInGui = value; return this }
    fun showInGui(): Boolean = showInGui
    fun setMinValue(value: Number): FoundationConfigProperty { minimum = value.toDouble(); return this }
    fun setMaxValue(value: Number): FoundationConfigProperty { maximum = value.toDouble(); return this }
    fun setToDefault(): FoundationConfigProperty {
        if (values != defaults) { values = defaults.toList(); changed() }
        return this
    }
    fun isDefault(): Boolean = values == defaults
    internal fun snapshot(): List<String> = values.toList()

    companion object {
        internal fun isListValue(value: Any?): Boolean = value is Array<*> || value is IntArray || value is DoubleArray || value is BooleanArray || value is FloatArray || value is Iterable<*>
        internal fun encode(value: Any?): List<String> = when (value) {
            is Array<*> -> value.map { it?.toString().orEmpty() }
            is IntArray -> value.map { it.toString() }
            is DoubleArray -> value.map { it.toString() }
            is BooleanArray -> value.map { it.toString() }
            is FloatArray -> value.map { it.toString() }
            is Iterable<*> -> value.map { it?.toString().orEmpty() }
            else -> listOf(value?.toString().orEmpty())
        }
        internal fun infer(value: Any?): Type = when (value) {
            is Boolean, is BooleanArray -> Type.BOOLEAN
            is Byte, is Short, is Int, is Long, is IntArray -> Type.INTEGER
            is Float, is Double, is DoubleArray, is FloatArray -> Type.DOUBLE
            else -> Type.STRING
        }
    }
}
