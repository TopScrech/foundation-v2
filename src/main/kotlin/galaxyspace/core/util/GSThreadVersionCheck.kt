package galaxyspace.core.util

import galaxyspace.GalaxySpace
import galaxyspace.core.util.GSUtils
import java.io.BufferedReader
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.util.GSThreadVersionCheck
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap

open class GSThreadVersionCheck constructor() : Thread("Galaxy Space Version Check Thread") {
    private var count = 0

    private val URLMF = "http://minecraftforum.net/forums/thread/2412548"
    private val URLMC = "https://forum.micdoodle8.com/index.php?threads/1-7-10-galaxy-space-stable.5298/"
    private val URLVK = "https://vk.com/addon.galaxyspace?w=page-83247747_51934589"

    override fun run() {
        val sideToCheck: Side? = FMLCommonHandler.instance().getSide()
        if (sideToCheck == null) {
            return
        }

        while (this.count < 1) {
            try {
                val url: URL =
                    URL("https://raw.githubusercontent.com/BlesseNtumble/GalaxySpace/1.12.2/1.12.2/version.txt")
                val http: HttpURLConnection = url.openConnection() as HttpURLConnection
                http.addRequestProperty("User-Agent", "Mozilla/4.76")
                val `in`: BufferedReader = BufferedReader(InputStreamReader(http.getInputStream()))
                var str: String
                var str2: Array<String?>? = null

                while ((`in`.readLine().also { str = it }) != null) {
                    if (str.contains("Version") && GSConfigCore.enableCheckVersion) {
                        str = str.replace("Version=", "")
                        str2 = str.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                        if (str2.size == 3) {
                            remoteMajVer = str2[0]!!.toInt()
                            remoteMinVer = str2[1]!!.toInt()
                            remoteBuildVer = str2[2]!!.toInt()
                        }


                        if (remoteMajVer >= GalaxySpace.major_version && remoteMinVer >= GalaxySpace.minor_version && remoteBuildVer > GalaxySpace.build_version) {
                            newversion = true
                            sleep(5000)

                            if (sideToCheck == Side.CLIENT) {
                                GSUtils.changelog.clear()
                                GSUtils.start()

                                FMLClientHandler.instance()
                                    .getClient().player.sendMessage(TextComponentString(EnumColor.GREY.toString() + "New " + EnumColor.DARK_AQUA + GalaxySpace.NAME + EnumColor.GREY + " version available! v" + remoteMajVer.toString() + "." + remoteMinVer.toString() + "." + remoteBuildVer.toString() + EnumColor.DARK_BLUE + " http://micdoodle8.com/"))
                            } else if (sideToCheck == Side.SERVER) {
                                GalaxySpace.info("New version available! v" + remoteMajVer.toString() + "." + remoteMinVer.toString() + "." + remoteBuildVer.toString() + " ")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            }

            /*if (remoteBuildVer == 0)
			{
				try
				{
					GalaxySpace.severe(StatCollector.translateToLocal("gs.failed.name"));
					Thread.sleep(15000);
				}
				catch (InterruptedException e) {}
			}
			else
			{*/
            GalaxySpace.info(GCCoreUtil.translate("gs.success.name") + " " + remoteMajVer + "." + remoteMinVer + "." + remoteBuildVer)
            //}
            this.count++
        }
    }


    companion object {
        @JvmField
        var INSTANCE: GSThreadVersionCheck = GSThreadVersionCheck()

        @JvmField
        var GSIP: MutableMap<String?, String?> = HashMap<String?, String?>()

        @JvmField
        var remoteMajVer: Int = 0

        @JvmField
        var remoteMinVer: Int = 0

        @JvmField
        var remoteBuildVer: Int = 0

        @JvmField
        var information: String? = null

        @JvmField
        var newversion: Boolean = false

        @JvmStatic
        fun startCheck() {
            val thread: Thread = Thread(INSTANCE)
            thread.start()
        }
    }
}
