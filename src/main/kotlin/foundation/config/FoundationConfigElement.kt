package foundation.config

/** A client-independent configuration tree consumed by Foundation's configuration screens */
interface FoundationConfigEntry {
    fun getName(): String
    fun getComment(): String
    fun getLanguageKey(): String
    fun getProperty(): FoundationConfigProperty?
    fun getChildElements(): MutableList<FoundationConfigEntry>
}

class FoundationConfigElement private constructor(
    private val category: FoundationConfigCategory?,
    private val property: FoundationConfigProperty?
) : FoundationConfigEntry {
    constructor(category: FoundationConfigCategory) : this(category, null)
    constructor(property: FoundationConfigProperty) : this(null, property)
    override fun getName(): String = property?.getName() ?: requireNotNull(category).getName()
    override fun getComment(): String = property?.getComment() ?: requireNotNull(category).getComment()
    override fun getLanguageKey(): String = property?.getLanguageKey() ?: requireNotNull(category).getLanguageKey()
    override fun getProperty(): FoundationConfigProperty? = property
    override fun getChildElements(): MutableList<FoundationConfigEntry> = buildList {
        category?.getOrderedValues()?.filter { it.showInGui() }?.forEach { add(FoundationConfigElement(it)) }
        category?.children?.forEach { add(FoundationConfigElement(it)) }
    }.toMutableList()
}
