package foundation.config

import java.util.TreeMap

/** Shared ordering for the combined Mars, Asteroids, and Venus configuration file */
object FoundationPlanetConfigOrder {
    @JvmField val properties: MutableMap<String?, MutableList<String?>?> = TreeMap()
    @JvmStatic fun finish(property: FoundationConfigProperty, category: String?) {
        val key = requireNotNull(category) { "Planet configuration category is missing" }
        val names = properties.getOrPut(key) { ArrayList() }!!
        if (property.getName() !in names) names.add(property.getName())
    }
}
