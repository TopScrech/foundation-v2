package micdoodle8.mods.galacticraft.core.util

import micdoodle8.mods.galacticraft.core.util.ThreadVersionCheck
import java.io.BufferedReader
import java.io.IOException
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

open class ThreadVersionCheck constructor() : Thread("Galacticraft Version Check Thread") {
    private var count = 0

    override fun run() {
        val sideToCheck: Side? = FMLCommonHandler.instance().getSide()

        if (sideToCheck == null || ConfigManagerCore.disableUpdateCheck) {
            return
        }

        while (this.count < 3 && remotePatchVer == -1) {
            var `in`: BufferedReader? = null
            try {
                val url: URL = URL("https://micdoodle8.com/galacticraft/version.html")
                val http: HttpURLConnection = url.openConnection() as HttpURLConnection
                http.addRequestProperty("User-Agent", "Mozilla/4.76")
                val streamReader: InputStreamReader = InputStreamReader(http.getInputStream())
                `in` = BufferedReader(streamReader)
                var str: String
                var str2: Array<String?>? = null

                while ((`in`.readLine().also { str = it }) != null) {
                    if (str.contains("Version")) {
                        str = str.replace("Version=", "")

                        str2 = str.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                        if (str2.size >= 3) {
                            remoteMajVer = str2[0]!!.toInt()
                            remoteMinVer = str2[1]!!.toInt()
                            remotePatchVer = str2[2]!!.toInt()
                        }

                        if (remoteMajVer == Constants.LOCALMAJVERSION && (remoteMinVer > Constants.LOCALMINVERSION || (remoteMinVer == Constants.LOCALMINVERSION && remotePatchVer > Constants.LOCALPATCHVERSION))) {
                            sleep(5000)

                            if (sideToCheck == Side.CLIENT) {
                                FMLClientHandler.instance()
                                    .getClient().player.sendMessage(TextComponentString(EnumColor.GREY.toString() + "New " + EnumColor.DARK_AQUA + Constants.MOD_NAME_SIMPLE + EnumColor.GREY + " version available! v" + remoteMajVer.toString() + "." + remoteMinVer.toString() + "." + remotePatchVer.toString() + ".xxx" + EnumColor.DARK_BLUE + " http://micdoodle8.com/"))
                            } else if (sideToCheck == Side.SERVER) {
                                GCLog.severe("New Galacticraft version available! v" + remoteMajVer.toString() + "." + remoteMinVer.toString() + "." + remotePatchVer.toString() + ".xxx" + " http://micdoodle8.com/")
                            }
                        }
                        break
                    }
                }

                `in`.close()
                streamReader.close()
            } catch (e: Exception) {
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }
                }
            }

            if (remotePatchVer == -1) {
                try {
                    GCLog.severe(GCCoreUtil.translate("newversion.failed.name"))
                    sleep(15000)
                } catch (e: InterruptedException) {
                }
            } else {
                GCLog.info(GCCoreUtil.translate("newversion.success.name") + " " + remoteMajVer + "." + remoteMinVer + "." + remotePatchVer)
            }

            this.count++
        }
    }

    companion object {
        @JvmField
        var INSTANCE: ThreadVersionCheck = ThreadVersionCheck()

        @JvmField
        var remoteMajVer: Int = 0

        @JvmField
        var remoteMinVer: Int = 0

        @JvmField
        var remotePatchVer: Int = -1

        @JvmStatic
        fun startCheck() {
            val thread: Thread = Thread(INSTANCE)
            thread.start()
        }
    }
}
