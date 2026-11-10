package micdoodle8.mods.galacticraft.planets

import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.Mod.Instance

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.tile.TileEntityDeconstructor
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import micdoodle8.mods.galacticraft.planets.mars.MarsModule
import micdoodle8.mods.galacticraft.planets.venus.ConfigManagerVenus
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.ConfigElement
import net.minecraftforge.common.config.Property
import net.minecraftforge.fml.client.config.IConfigElement
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.ModMetadata
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartedEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import java.io.File
import java.util.ArrayList
import java.util.TreeMap

@Mod(
    modid = Constants.MOD_ID_PLANETS,
    name = GalacticraftPlanets.Companion.NAME,
    version = Constants.COMBINEDVERSION,
    useMetadata = true,
    acceptedMinecraftVersions = Constants.MCVERSION,
    dependencies = "required-after:" + Constants.MOD_ID_CORE + ";",
    guiFactory = "micdoodle8.mods.galacticraft.planets.ConfigGuiFactoryPlanets"
)
open class GalacticraftPlanets {
    private var GCPlanetsSource: File? = null

    @Mod.EventHandler
    open fun preInit(event: FMLPreInitializationEvent) {
        GCPlanetsSource = event.getSourceFile()
        this.initModInfo(event.getModMetadata())
        MinecraftForge.EVENT_BUS.register(this)

        //Initialise configs, converting mars.conf + asteroids.conf to planets.conf if necessary
        val oldMarsConf: File = File(event.getModConfigurationDirectory(), "Galacticraft/mars.conf")
        val newPlanetsConf: File = File(event.getModConfigurationDirectory(), "Galacticraft/planets.conf")
        var update: Boolean = false
        if (oldMarsConf.exists()) {
            oldMarsConf.renameTo(newPlanetsConf)
            update = true
        }
        this.configSyncStart()
        ConfigManagerMars(newPlanetsConf, update)
        ConfigManagerAsteroids(File(event.getModConfigurationDirectory(), "Galacticraft/asteroids.conf"))
        ConfigManagerVenus(File(event.getModConfigurationDirectory(), "Galacticraft/venus.conf"))
        this.configSyncEnd(true)

        commonModules.add(MarsModule())
        commonModules.add(AsteroidsModule())
        commonModules.add(VenusModule())
        proxy!!.preInit(event)
    }

    @Mod.EventHandler
    open fun init(event: FMLInitializationEvent?) {
        proxy!!.init(event)
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy)
    }

    @Mod.EventHandler
    open fun postInit(event: FMLPostInitializationEvent) {
        proxy!!.postInit(event)
        TileEntityDeconstructor.initialiseRecipeListPlanets()
        if (event.getSide() == Side.SERVER) this.loadLanguagePlanets("en_US")
    }

    open fun loadLanguagePlanets(lang: String?) {
        GCCoreUtil.loadLanguage((lang)!!, ASSET_PREFIX, (GCPlanetsSource)!!)
    }

    @Mod.EventHandler
    open fun serverStarting(event: FMLServerStartingEvent?) {
        proxy!!.serverStarting(event)
    }

    @Mod.EventHandler
    open fun serverInit(event: FMLServerStartedEvent?) {
        proxy!!.serverInit(event)
    }

    @SubscribeEvent
    open fun onConfigChanged(event: ConfigChangedEvent) {
        if (event.getModID() == Constants.MOD_ID_PLANETS) {
            this.configSyncStart()
            for (module: IPlanetsModule in commonModules) {
                module.syncConfig()
            }
            this.configSyncEnd(false)
        }
    }

    private fun configSyncEnd(load: Boolean) {
        //Cleanup older GC config files
        ConfigManagerCore.cleanConfig(ConfigManagerMars.config, propOrder)

        //Always save - this is last to be called both at load time and at mid-game
        if ((ConfigManagerMars.config)!!.hasChanged()) {
            (ConfigManagerMars.config)!!.save()
        }
    }

    private fun configSyncStart() {
        propOrder.clear()
    }

    private fun initModInfo(info: ModMetadata) {
        info.autogenerated = false
        info.modId = Constants.MOD_ID_PLANETS
        info.name = NAME
        info.version = Constants.COMBINEDVERSION
        info.description = "Planets addon for Galacticraft."
        info.url = "https://micdoodle8.com/"
        info.authorList =
            mutableListOf<String?>("micdoodle8", "radfast", "EzerArch", "fishtaco", "SpaceViking", "SteveKunG")
        info.logoFile = "assets/galacticraftplanets/galacticraft_logo.png"
    }

    companion object {
        const val NAME: String = "Galacticraft Planets"

        @JvmField
        @Mod.Instance(Constants.MOD_ID_PLANETS)
        var instance: GalacticraftPlanets? = null

        @JvmField
        var commonModules: MutableList<IPlanetsModule> = ArrayList<IPlanetsModule>()

        @JvmField
        var clientModules: MutableList<IPlanetsModuleClient> = ArrayList<IPlanetsModuleClient>()

        const val ASSET_PREFIX: String = "galacticraftplanets"

        @JvmField
        val TEXTURE_PREFIX: String = ASSET_PREFIX + ":"

        @JvmField
        @SidedProxy(
            clientSide = "micdoodle8.mods.galacticraft.planets.PlanetsProxyClient",
            serverSide = "micdoodle8.mods.galacticraft.planets.PlanetsProxy"
        )
        var proxy: PlanetsProxy? = null

        @JvmField
        var propOrder: MutableMap<String?, MutableList<String?>?> = TreeMap<String?, MutableList<String?>?>()

        @JvmStatic
        fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, vararg extraData: Any?) {
            for (module: IPlanetsModuleClient in clientModules) {
                module.spawnParticle(particleID, position, motion, *extraData)
            }
        }

        @JvmStatic
        fun getConfigElements(): MutableList<IConfigElement?> {
            val list: MutableList<IConfigElement?> = ArrayList<IConfigElement?>()


            //Get the last planet to be configured only, as all will reference and re-use the same planets.conf config file
            val module: IPlanetsModule = commonModules.get(commonModules.size - 1)
            list.addAll(
                ConfigElement(
                    (module.getConfiguration())!!.getCategory(Constants.CONFIG_CATEGORY_ENTITIES)
                ).getChildElements()
            )
            list.addAll(
                ConfigElement(
                    (module.getConfiguration())!!.getCategory(Constants.CONFIG_CATEGORY_ACHIEVEMENTS)
                ).getChildElements()
            )
            list.addAll(
                ConfigElement(
                    (module.getConfiguration())!!.getCategory(Constants.CONFIG_CATEGORY_GENERAL)
                ).getChildElements()
            )
            list.addAll(
                ConfigElement(
                    (module.getConfiguration())!!.getCategory(Constants.CONFIG_CATEGORY_WORLDGEN)
                ).getChildElements()
            )
            list.addAll(
                ConfigElement(
                    (module.getConfiguration())!!.getCategory(Constants.CONFIG_CATEGORY_DIMENSIONS)
                ).getChildElements()
            )
            list.addAll(
                ConfigElement(
                    (module.getConfiguration())!!.getCategory(Constants.CONFIG_CATEGORY_SCHEMATIC)
                ).getChildElements()
            )

            return list
        }

        @JvmStatic
        fun finishProp(prop: Property, currentCat: String?) {
            if (propOrder.get(currentCat) == null) {
                propOrder.put(currentCat, ArrayList<String?>())
            }
            propOrder.get(currentCat)!!.add(prop.getName())
        }
    }
}
