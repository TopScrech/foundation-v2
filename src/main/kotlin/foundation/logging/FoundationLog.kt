package foundation.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import java.util.Locale

/** Retains the modules' existing diagnostic calls using the current logging runtime */
object FoundationLog {
    private val logger = LogManager.getLogger("Foundation")
    private fun format(message: String?, arguments: Array<out Any?>): String =
        if (arguments.isEmpty()) message.orEmpty() else String.format(Locale.ROOT, message.orEmpty(), *arguments)

    @JvmStatic fun log(level: Level, cause: Throwable?, message: String?, vararg arguments: Any?) {
        logger.log(level, format(message, arguments), cause)
    }
    @JvmStatic fun info(message: String?, vararg arguments: Any?) { logger.info(format(message, arguments)) }
    @JvmStatic fun severe(message: String?, vararg arguments: Any?) { logger.error(format(message, arguments)) }
    @JvmStatic fun warning(message: String?, vararg arguments: Any?) { logger.warn(format(message, arguments)) }
    @JvmStatic fun fine(message: String?, vararg arguments: Any?) { logger.debug(format(message, arguments)) }
}
