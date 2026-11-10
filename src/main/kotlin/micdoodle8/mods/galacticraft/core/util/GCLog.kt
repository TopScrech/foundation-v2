package micdoodle8.mods.galacticraft.core.util

import micdoodle8.mods.galacticraft.core.Constants
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.lang.Exception

object GCLog {
    private val log: Logger = LogManager.getFormatterLogger(Constants.MOD_NAME_SIMPLE)

    @JvmStatic
    fun info(message: String?) {
        log.info(message)
    }

    @JvmStatic
    fun severe(message: String?) {
        log.error(message)
    }

    @JvmStatic
    fun debug(message: String?) {
        if (ConfigManagerCore.enableDebug) {
            log.debug(message)
        }
    }

    @JvmStatic
    fun exception(e: Exception?) {
        log.catching(e)
    }
}
