package micdoodle8.mods.galacticraft.planets


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
import foundation.config.FoundationConfigElement as ConfigElement
import foundation.config.FoundationConfigProperty as Property
import foundation.config.FoundationConfigEntry as IConfigElement
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.event.server.ServerStartedEvent as FMLServerStartedEvent
import net.minecraftforge.event.server.ServerStartingEvent as FMLServerStartingEvent
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.LogicalSide as Side
import java.io.File
import java.util.ArrayList
import java.util.TreeMap

@Mod(Constants.MOD_ID_PLANETS)
open class GalacticraftPlanets(context: net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext) {

    private var GCPlanetsSource: File? = null

    open fun preInit() {
        GCPlanetsSource = foundation.bootstrap.FoundationBootstrap.sourceFile(javaClass)
        MinecraftForge.EVENT_BUS.register(this)

        //Initialise configs, converting mars.conf + asteroids.conf to planets.conf if necessary
        val oldMarsConf: File = File(foundation.bootstrap.FoundationBootstrap.configDirectory, "Galacticraft/mars.conf")
        val newPlanetsConf: File = File(foundation.bootstrap.FoundationBootstrap.configDirectory, "Galacticraft/planets.conf")
        var update: Boolean = false
        if (oldMarsConf.exists()) {
            oldMarsConf.renameTo(newPlanetsConf)
            update = true
        }
        this.configSyncStart()
        ConfigManagerMars(newPlanetsConf, update)
        ConfigManagerAsteroids(File(foundation.bootstrap.FoundationBootstrap.configDirectory, "Galacticraft/asteroids.conf"))
        ConfigManagerVenus(File(foundation.bootstrap.FoundationBootstrap.configDirectory, "Galacticraft/venus.conf"))
        this.configSyncEnd(true)

        commonModules.add(MarsModule())
        commonModules.add(AsteroidsModule())
        commonModules.add(VenusModule())
        proxy!!.preInit()
    }

    open fun init(event: FMLInitializationEvent?) {
        proxy!!.init(event)
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy)
    }

    open fun postInit(event: FMLPostInitializationEvent) {
        proxy!!.postInit(event)
        TileEntityDeconstructor.initialiseRecipeListPlanets()
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.DEDICATED_SERVER) this.loadLanguagePlanets("en_US")
    }

    open fun loadLanguagePlanets(lang: String?) {
        GCCoreUtil.loadLanguage((lang)!!, ASSET_PREFIX, (GCPlanetsSource)!!)
    }

    open fun serverStarting(event: FMLServerStartingEvent?) {
        proxy!!.serverStarting(event)
    }

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


    init {
        foundation.level.FoundationLevels.install()
        instance = this
        proxy = foundation.bootstrap.FoundationBootstrap.createProxy(PlanetsProxy::class.java, "micdoodle8.mods.galacticraft.planets.PlanetsProxyClient")
        preInit()
        foundation.bootstrap.FoundationBootstrap.attach(context, { init(it) }, { postInit(it) })
        net.minecraftforge.event.server.ServerStartingEvent.BUS.addListener { event -> serverStarting(event) }
        net.minecraftforge.event.server.ServerStartedEvent.BUS.addListener { event -> serverInit(event) }
    }


    companion object {
        const val NAME: String = "Galacticraft Planets"

        @JvmField
        var instance: GalacticraftPlanets? = null

        @JvmField
        var commonModules: MutableList<IPlanetsModule> = ArrayList<IPlanetsModule>()

        @JvmField
        var clientModules: MutableList<IPlanetsModuleClient> = ArrayList<IPlanetsModuleClient>()

        const val ASSET_PREFIX: String = "galacticraftplanets"

        @JvmField
        val TEXTURE_PREFIX: String = ASSET_PREFIX + ":"

        @JvmField
        var proxy: PlanetsProxy? = null

        @JvmField
        var propOrder: MutableMap<String?, MutableList<String?>?> = foundation.config.FoundationPlanetConfigOrder.properties

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
            foundation.config.FoundationPlanetConfigOrder.finish(prop, currentCat)
        }
    }
}
