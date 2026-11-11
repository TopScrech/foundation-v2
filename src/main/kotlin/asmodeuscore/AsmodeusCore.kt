package asmodeuscore


import asmodeuscore.AsmodeusCore
import asmodeuscore.api.IBodiesHandler
import asmodeuscore.api.IBodies
import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.handler.capabilities.ACCapabilityStatsHandler
import asmodeuscore.core.registers.potions.ACPotions
import asmodeuscore.core.network.packet.AsmodeusChannelHandler
import asmodeuscore.core.event.AsmodeusEvent
import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.core.astronomy.SpaceData
import asmodeuscore.core.prefab.entities.EntityAdvMeteor
import asmodeuscore.core.command.CommandDustStormToggle
import asmodeuscore.core.command.CommandFrozenStormToggle
import asmodeuscore.core.command.CommandMeteoricRainToggle
import asmodeuscore.core.astronomy.BodiesRegistry.Galaxies
import asmodeuscore.core.proxy.CommonProxy
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.event.RegisterCommandsEvent
import java.io.File
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

@Mod(AsmodeusCore.MODID)
open class AsmodeusCore(context: net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext) {

    open fun construct() {
        bodies.addAll(foundation.bootstrap.FoundationBootstrap.discover(IBodiesHandler::class.java, IBodies::class.java))
        bodies.sortByDescending { it.getPriority() }
    }

    open fun preInit() {

        AsmodeusConfig(File(foundation.bootstrap.FoundationBootstrap.configDirectory, "AsmodeusCore/core.conf"))

        ACCapabilityStatsHandler.register()

        packetPipeline = AsmodeusChannelHandler.init()

        proxy!!.preload()
        proxy!!.register_event(AsmodeusEvent())
        /*
		for(IBodies list : AsmodeusCore.bodies)
		{
			list.preInitialization();
			
    		if(list.canRegister()) 
                list.preInit();
		}
		*/
        //Galaxies NEW_GALAXY = EnumHelper.addEnum(Galaxies.class, "NEW_GALAXY", new Class<?>[]{String.class}, "new_galaxy");
    }

    open fun init(event: FMLInitializationEvent?) {
        proxy!!.load()
        BodiesRegistry.registerCelestialBody(SpaceData.Companion.getRegisteredCelestialBodies())
        SpaceData.Companion.refreshGalaxies()

        GCCoreUtil.registerGalacticraftNonMobEntity(EntityAdvMeteor::class.java, "adv_meteor", 150, 5, true)
        /*
    	for(IBodies list : AsmodeusCore.bodies)
    		if(list.canRegister()) 
    			list.init(event);*/
    }

    open fun postInit(event: FMLPostInitializationEvent?) {
        proxy!!.postload()

        /*
    	for(IBodies list : AsmodeusCore.bodies)
    		if(list.canRegister()) 
    			list.postInit(event);
    	*/
    }

    open fun registerCommands(event: RegisterCommandsEvent) {
        CommandDustStormToggle.register(event.getDispatcher())
        CommandFrozenStormToggle.register(event.getDispatcher())
        CommandMeteoricRainToggle.register(event.getDispatcher())
    }



    init {
        foundation.level.FoundationSpaceEnvironment.install(context)
        ACPotions.install(context)
        asmodeuscore.core.utils.ACAttributePlayer.install(context)
        foundation.level.FoundationLevels.install()
        INSTANCE = this
        proxy = foundation.bootstrap.FoundationBootstrap.createProxy(CommonProxy::class.java, "asmodeuscore.core.proxy.ClientProxy")
        construct()
        preInit()
        foundation.biome.FoundationBiomeTags.install { !ConfigManagerCore.disableBiomeTypeRegistrations }
        foundation.bootstrap.FoundationBootstrap.attach(context, { init(it) }, { postInit(it) })
        RegisterCommandsEvent.BUS.addListener { event -> registerCommands(event) }
    }


    companion object {
        const val major_version: Int = 1

        const val minor_version: Int = 0

        const val build_version: Int = 4

        const val NAME: String = "AsmodeusCore"

        const val MODID: String = "asmodeuscore"

        const val VERSION: String = "$major_version.$minor_version.$build_version"

        @JvmField
        val ASSET_PREFIX: String = MODID

        @JvmField
        val TEXTURE_PREFIX: String = ASSET_PREFIX + ":"

        @JvmField
        var packetPipeline: AsmodeusChannelHandler? = null

        @JvmField
        var bodies: MutableList<IBodies> = ArrayList<IBodies>()

        //---------------------------------------------
        @JvmField
        var INSTANCE: AsmodeusCore? = null

        @JvmField
        var proxy: CommonProxy? = null
    }
}
