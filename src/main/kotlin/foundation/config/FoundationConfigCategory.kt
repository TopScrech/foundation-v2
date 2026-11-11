package foundation.config

/** Ordered categories retain unknown properties until a module deliberately removes them */
class FoundationConfigCategory internal constructor(
    private val name: String,
    internal val parent: FoundationConfigCategory?,
    private val changed: () -> Unit
) : LinkedHashMap<String?, FoundationConfigProperty>() {
    internal val children = ArrayList<FoundationConfigCategory>()
    private var order: List<String> = emptyList()
    private var comment = ""
    private var languageKey = ""
    private var restartMinecraft = false
    private var restartWorld = false

    fun getName(): String = name
    fun getQualifiedName(): String = parent?.let { it.getQualifiedName() + "." + name } ?: name
    fun getComment(): String = comment
    fun setComment(value: String?): FoundationConfigCategory { if (comment != value.orEmpty()) { comment = value.orEmpty(); changed() }; return this }
    fun setLanguageKey(value: String?): FoundationConfigCategory { languageKey = value.orEmpty(); return this }
    fun getLanguageKey(): String = languageKey
    fun setRequiresMcRestart(value: Boolean): FoundationConfigCategory { restartMinecraft = value; return this }
    fun requiresMcRestart(): Boolean = restartMinecraft
    fun setRequiresWorldRestart(value: Boolean): FoundationConfigCategory { restartWorld = value; return this }
    fun requiresWorldRestart(): Boolean = restartWorld || restartMinecraft
    fun setPropertyOrder(value: List<String?>?): FoundationConfigCategory { order = value.orEmpty().filterNotNull().distinct(); return this }
    fun getOrderedValues(): List<FoundationConfigProperty> =
        order.mapNotNull { this[it] } + entries.filter { it.key !in order }.map { it.value }

    override fun put(key: String?, value: FoundationConfigProperty): FoundationConfigProperty? {
        requireNotNull(key) { "A configuration property needs a name" }
        val previous = super.put(key, value)
        if (previous !== value) changed()
        return previous
    }
    override fun putAll(from: Map<out String?, FoundationConfigProperty>) { from.forEach { (key, value) -> put(key, value) } }
    override fun remove(key: String?): FoundationConfigProperty? = super.remove(key).also { if (it != null) changed() }
    override fun clear() { if (isNotEmpty()) { super.clear(); changed() } }
}
