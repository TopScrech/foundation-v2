package foundation.config

import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Locale

/** Reads existing Forge-era settings without depending on the removed Forge configuration library */
class FoundationConfiguration(private val file: File) {
    @JvmField val isChild: Boolean = false
    private var categories = LinkedHashMap<String, FoundationConfigCategory>()
    private var changed = false
    private var reading = false
    private var version: String? = null
    init { load() }

    private fun touch() { if (!reading) changed = true }

    @Synchronized
    fun getCategory(name: String?): FoundationConfigCategory = category(categories, requireNotNull(name))

    private fun category(target: MutableMap<String, FoundationConfigCategory>, qualified: String): FoundationConfigCategory {
        val key = qualified.lowercase(Locale.ROOT)
        return target.getOrPut(key) {
            val split = key.lastIndexOf('.')
            val parent = if (split >= 0) category(target, key.substring(0, split)) else null
            FoundationConfigCategory(key.substring(split + 1), parent, ::touch).also { parent?.children?.add(it); touch() }
        }
    }

    @Synchronized
    fun get(category: String?, name: String?, defaultValue: Any?, comment: String? = null,
            minimum: Number? = null, maximum: Number? = null): FoundationConfigProperty {
        val key = requireNotNull(name) { "A configuration property needs a name" }
        val group = getCategory(category)
        val type = FoundationConfigProperty.infer(defaultValue)
        val isList = FoundationConfigProperty.isListValue(defaultValue)
        val defaults = FoundationConfigProperty.encode(defaultValue)
        val property = group[key] ?: FoundationConfigProperty(key, type, isList, defaults, ::touch).also { group[key] = it }
        if (property.type != type) { property.type = type; touch() }
        if (property.list != isList) property.set(defaultValue)
        property.defaults = defaults
        if (comment != null) property.setComment(comment)
        if (minimum != null) property.setMinValue(minimum)
        if (maximum != null) property.setMaxValue(maximum)
        return property
    }

    @Synchronized fun hasChanged(): Boolean = changed
    @Synchronized fun getCategoryNames(): Set<String> = categories.keys.toSet()
    @Synchronized fun hasKey(category: String?, key: String?): Boolean = categories[category?.lowercase(Locale.ROOT)]?.containsKey(key) == true
    @Synchronized fun setCategoryPropertyOrder(name: String?, order: List<String?>?) { getCategory(name).setPropertyOrder(order) }
    @Synchronized fun removeCategory(category: FoundationConfigCategory) {
        category.children.toList().forEach(::removeCategory)
        if (categories.remove(category.getQualifiedName()) != null) touch()
        category.parent?.children?.remove(category)
    }
    @Synchronized fun moveProperty(source: String?, key: String?, destination: String?): Boolean {
        val property = categories[source?.lowercase(Locale.ROOT)]?.get(key) ?: return false
        val target = getCategory(destination)
        if (target.containsKey(key)) return false
        categories[source?.lowercase(Locale.ROOT)]?.remove(key)
        target[key] = property
        return true
    }

    @Synchronized
    fun load() {
        if (!file.exists()) return
        val parsed = LinkedHashMap<String, FoundationConfigCategory>()
        val parents = ArrayList<FoundationConfigCategory>()
        val comments = ArrayList<String>()
        var listProperty: FoundationConfigProperty? = null
        val listValues = ArrayList<String>()
        var parsedVersion: String? = null
        reading = true
        try {
            file.readLines(StandardCharsets.UTF_8).forEachIndexed { index, raw ->
                val line = raw.removePrefix("\uFEFF").trim()
                fun fail(message: String): Nothing = throw IOException("${file.path}:${index + 1}: $message")
                val activeList = listProperty
                if (activeList != null) {
                    if (line == ">") {
                        activeList.set(listValues.toTypedArray())
                        listValues.clear()
                        listProperty = null
                    } else if (line.isNotEmpty() && !line.startsWith('#')) listValues.add(decode(line))
                    return@forEachIndexed
                }
                if (line.isEmpty()) { comments.clear(); return@forEachIndexed }
                if (line.startsWith('#')) { comments.add(line.removePrefix("#").trimStart()); return@forEachIndexed }
                if (line.startsWith("~CONFIG_VERSION:")) { parsedVersion = line.substringAfter(':').trim(); return@forEachIndexed }
                if (line == "}") {
                    if (parents.isEmpty()) fail("Unexpected category closing brace")
                    parents.removeAt(parents.lastIndex)
                    comments.clear()
                    return@forEachIndexed
                }
                val propertyLine = PROPERTY.matchEntire(line)
                if (propertyLine != null) {
                    val group = parents.lastOrNull() ?: fail("Property outside a category")
                    val type = FoundationConfigProperty.Type.entries.firstOrNull { it.prefix == propertyLine.groupValues[1].single() }
                        ?: fail("Unknown property type")
                    val name = decode(propertyLine.groupValues[2].trim())
                    if (group.containsKey(name)) fail("Duplicate property $name")
                    val isList = propertyLine.groupValues[3] == "<"
                    val value = if (isList) emptyList() else listOf(decode(propertyLine.groupValues[4].trim()))
                    val property = FoundationConfigProperty(name, type, isList, value, ::touch)
                    property.setComment(comments.joinToString("\n"))
                    group[name] = property
                    if (isList) listProperty = property
                    comments.clear()
                    return@forEachIndexed
                }
                if (line.endsWith('{')) {
                    val name = decode(line.dropLast(1).trim())
                    if (name.isEmpty()) fail("Empty category name")
                    val qualified = parents.lastOrNull()?.let { it.getQualifiedName() + "." + name } ?: name
                    val group = category(parsed, qualified)
                    group.setComment(comments.joinToString("\n"))
                    parents.add(group)
                    comments.clear()
                    return@forEachIndexed
                }
                fail("Unrecognized configuration line")
            }
            if (listProperty != null || parents.isNotEmpty()) throw IOException("${file.path}: Unterminated configuration section")
            categories = parsed
            version = parsedVersion
            changed = false
        } finally {
            reading = false
        }
    }

    @Synchronized
    fun save() {
        val text = buildString {
            append("# Foundation configuration\n")
            version?.let { append("~CONFIG_VERSION: ").append(it).append('\n') }
            append('\n')
            fun writeCategory(category: FoundationConfigCategory, indent: String) {
                category.getComment().takeIf { it.isNotEmpty() }?.lines()?.forEach { append(indent).append("# ").append(it).append('\n') }
                append(indent).append(quote(category.getName())).append(" {\n")
                val inner = indent + "    "
                for (property in category.getOrderedValues()) {
                    property.getComment().takeIf { it.isNotEmpty() }?.lines()?.forEach { append(inner).append("# ").append(it).append('\n') }
                    append(inner).append(property.type.prefix).append(':').append(quote(property.getName()))
                    if (property.isList()) {
                        append(" <\n")
                        property.snapshot().forEach { append(inner).append("    ").append(quote(it)).append('\n') }
                        append(inner).append(">\n")
                    } else append('=').append(quote(property.getString())).append('\n')
                }
                category.children.forEach { writeCategory(it, inner) }
                append(indent).append("}\n\n")
            }
            categories.values.filter { it.parent == null }.forEach { writeCategory(it, "") }
        }
        val destination = file.toPath().toAbsolutePath()
        Files.createDirectories(destination.parent)
        val temporary = Files.createTempFile(destination.parent, destination.fileName.toString() + ".", ".tmp")
        try {
            Files.writeString(temporary, text, StandardCharsets.UTF_8)
            try {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING)
            }
            changed = false
        } finally {
            Files.deleteIfExists(temporary)
        }
    }

    companion object {
        const val CATEGORY_GENERAL: String = "general"
        const val CATEGORY_CLIENT: String = "client"
        private val PROPERTY = Regex("""([A-Z]):("(?:\\.|[^"\\])*"|[^=<]+?)\s*(=|<)(.*)""")
        private fun quote(value: String): String = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\""
        private fun decode(value: String): String {
            if (!value.startsWith('"')) return value
            require(value.length >= 2 && value.endsWith('"')) { "Unterminated quoted configuration value" }
            return buildString {
                var index = 1
                while (index < value.lastIndex) {
                    val character = value[index++]
                    if (character == '\\') {
                        require(index < value.lastIndex) { "Unterminated configuration escape" }
                        when (val escaped = value[index++]) {
                            'n' -> append('\n')
                            'r' -> append('\r')
                            't' -> append('\t')
                            '\\', '"' -> append(escaped)
                            else -> append('\\').append(escaped)
                        }
                    } else append(character)
                }
            }
        }
    }
}
