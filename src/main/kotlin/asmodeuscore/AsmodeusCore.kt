package asmodeuscore

import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.Mod.Instance
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData

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
import asmodeuscore.core.utils.Utils
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.biome.Biome
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.ModMetadata
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.File
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

@Mod(
    modid = AsmodeusCore.Companion.MODID,
    version = AsmodeusCore.Companion.VERSION,
    dependencies = "required-after:forge@[14.23.1.2555,); after:planetprogression@[0.4.6,); after:galacticraftcore; after:galacticraftplanets;",
    acceptedMinecraftVersions = "[1.12.2]",
    name = AsmodeusCore.Companion.NAME,
    guiFactory = "asmodeuscore.core.client.gui.ACConfigGuiFactory"
)
open class AsmodeusCore {
    @Mod.EventHandler
    open fun construct(event: FMLConstructionEvent) {
        for (data: ASMDataTable.ASMData in event.getASMHarvestedData().getAll(IBodiesHandler::class.java.getName())) {
            val body: IBodies?
            try {
                body = Class.forName(data.getClassName()).newInstance() as IBodies?
                bodies.add((body)!!)
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        Collections.sort<IBodies>(bodies, object : Comparator<IBodies> {
            override fun compare(lhs: IBodies, rhs: IBodies): Int {
                val x: Int = rhs.getPriority()
                val y: Int = lhs.getPriority()
                return if (x < y) -1 else (if (x == y) 0 else 1)
            }
        })
    }

    @Mod.EventHandler
    open fun preInit(event: FMLPreInitializationEvent) {
        this.initModInfo(event.getModMetadata())

        AsmodeusConfig(File(event.getModConfigurationDirectory(), "AsmodeusCore/core.conf"))

        ACCapabilityStatsHandler.register()
        ACPotions.initialize()

        packetPipeline = AsmodeusChannelHandler.init()

        proxy!!.preload()
        proxy!!.register_event(AsmodeusEvent())
        /*
		for(IBodies list : AsmodeusCore.bodies)
		{
			list.preInitialization(event);
			
    		if(list.canRegister()) 
    			list.preInit(event);
		}
		*/
        //Galaxies NEW_GALAXY = EnumHelper.addEnum(Galaxies.class, "NEW_GALAXY", new Class<?>[]{String.class}, "new_galaxy");
    }

    @Mod.EventHandler
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

    @Mod.EventHandler
    open fun postInit(event: FMLPostInitializationEvent?) {
        proxy!!.postload()

        /*
    	for(IBodies list : AsmodeusCore.bodies)
    		if(list.canRegister()) 
    			list.postInit(event);
    	*/
    }

    @Mod.EventHandler
    open fun onServerStarting(event: FMLServerStartingEvent) {
        event.registerServerCommand(CommandDustStormToggle())
        event.registerServerCommand(CommandFrozenStormToggle())
        event.registerServerCommand(CommandMeteoricRainToggle())
    }

    private fun initModInfo(info: ModMetadata) {
        info.autogenerated = false
        info.modId = MODID
        info.name = NAME
        info.version = VERSION
        info.description = "Technical lib for BlesseNtumble (Vi[Told]) mods."
        info.authorList = mutableListOf<String?>("Vi[Told]")
    }

    @Mod.EventBusSubscriber(modid = MODID)
    object RegistrationHandler {
        @JvmStatic
        @SubscribeEvent
        fun registerBiomes(event: RegistryEvent.Register<Biome?>) {
            event.getRegistry().register(Utils.WE_Biome)
            event.getRegistry().register(Utils.WE_Biome_2)

            if (!ConfigManagerCore.disableBiomeTypeRegistrations) {
                BiomeDictionary.addTypes(
                    Utils.WE_Biome,
                    BiomeDictionary.Type.COLD,
                    BiomeDictionary.Type.DRY,
                    BiomeDictionary.Type.DEAD
                )
            }
        }
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
        @Mod.Instance(MODID)
        var INSTANCE: AsmodeusCore? = null

        @JvmField
        @SidedProxy(
            clientSide = "asmodeuscore.core.proxy.ClientProxy",
            serverSide = "asmodeuscore.core.proxy.CommonProxy"
        )
        var proxy: CommonProxy? = null
    }
}
