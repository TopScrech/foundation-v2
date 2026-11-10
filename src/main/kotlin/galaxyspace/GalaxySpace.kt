package galaxyspace

import foundation.FoundationMetadata

import java.util.ArrayList
import java.util.List
import galaxyspace.core.prefab.entities.EntityAstroWolf
import galaxyspace.core.prefab.entities.EntityCustomCargoRocket
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import galaxyspace.core.prefab.entities.EntityIceSpike
import galaxyspace.core.prefab.entities.EntityLaserBeam
import galaxyspace.core.prefab.entities.EntityMultiSeatRocketTest
import galaxyspace.core.prefab.entities.EntityTier4Rocket
import galaxyspace.core.prefab.entities.EntityTier5Rocket
import galaxyspace.core.prefab.entities.EntityTier6Rocket
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvCircuitFabricator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvLandingPad
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvLandingPadSingle
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvOxygenStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityEnergyPad
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityFuelGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasBurner
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasCollector
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGravitationModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicBase
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicFarm
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidExtractor
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidSeparator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModificationTable
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityPanelController
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityPlanetShield
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityRadiationStabiliser
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityResearchTable
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityRocketAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityUniversalRecycler
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindSolarPanel
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.Mod.Instance
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData

import asmodeuscore.AsmodeusCore
import asmodeuscore.api.IBodies
import asmodeuscore.api.space.IBookPage
import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.core.utils.BookUtils
import galaxyspace.api.IPage
import galaxyspace.core.GSBlocks
import galaxyspace.core.GSFluids
import galaxyspace.core.GSItems
import galaxyspace.core.GSPotions
import galaxyspace.core.client.gui.book.BookRegister
import galaxyspace.core.configs.*
import galaxyspace.core.events.GSEventHandler
import galaxyspace.core.handler.GSGuiHandler
import galaxyspace.core.handler.capabilities.GSCapabilityStatsHandler
import galaxyspace.core.network.packet.GalaxySpaceChannelHandler
import galaxyspace.core.prefab.entities.*
import galaxyspace.core.proxy.CommonProxy
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.util.GSThreadVersionCheck
import galaxyspace.core.util.researches.ResearchUtil
import galaxyspace.systems.SolarSystem.moons.enceladus.tile.TileEntityBlockCrystallTE
import galaxyspace.systems.SolarSystem.moons.io.entities.EntityBossGhast
import galaxyspace.systems.SolarSystem.moons.io.tile.TileEntityDungeonSpawnerIo
import galaxyspace.systems.SolarSystem.moons.io.tile.TileEntityTreasureChestIo
import galaxyspace.systems.SolarSystem.planets.ceres.entities.EntityBossBlaze
import galaxyspace.systems.SolarSystem.planets.ceres.tile.TileEntityDungeonSpawnerCeres
import galaxyspace.systems.SolarSystem.planets.ceres.tile.TileEntityTreasureChestCeres
import galaxyspace.systems.SolarSystem.planets.mars.entities.EntityMarsRover
import galaxyspace.systems.SolarSystem.planets.overworld.tile.*
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.CreativeTabGC
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.ModMetadata
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import java.io.File
import java.util.function.Consumer

@Mod(
    modid = GalaxySpace.Companion.MODID,
    version = FoundationMetadata.VERSION,
    dependencies = Constants.DEPENDENCIES_FORGE + "required-after:galacticraftcore; required-after:galacticraftplanets; required-after:asmodeuscore@[1.0.4,)",
    acceptedMinecraftVersions = "[1.12.2]",
    name = FoundationMetadata.NAME,
    guiFactory = "galaxyspace.core.client.gui.GSConfigGuiFactory"
)
open class GalaxySpace {
    //public static List<IBodies> bodies = new ArrayList<IBodies>();
    private val pages: MutableList<IBookPage?> = ArrayList<IBookPage?>()

    @Mod.EventHandler
    open fun preInit(event: FMLPreInitializationEvent) {
        GSConfigDimensions(File(event.getModConfigurationDirectory(), "GalaxySpace/dimensions.conf"))
        GSConfigSchematics(File(event.getModConfigurationDirectory(), "GalaxySpace/schematics.conf"))
        GSConfigCore(File(event.getModConfigurationDirectory(), "GalaxySpace/core.conf"))
        GSConfigEnergy(File(event.getModConfigurationDirectory(), "GalaxySpace/energy.conf"))
        GSConfigWorld(File(event.getModConfigurationDirectory(), "GalaxySpace/world.conf"))

        this.initModInfo(event.getModMetadata())

        debug = GSConfigCore.enableDebug
        log = event.getModLog()

        BodiesRegistry.setMaxTier(6)

        GSBlocks.initialize()
        GSFluids.initialize()
        GSItems.initialize()
        GSPotions.initialize()

        proxy!!.preload(event)
        proxy!!.register_event(GSEventHandler())

        for (list in AsmodeusCore.bodies) {
            (list)!!.preInitialization(event)

            if ((list)!!.canRegister()) (list)!!.preInit(event)
        }

        if (event.getSide() == Side.CLIENT) for (data: ASMDataTable.ASMData in event.getAsmData()
            .getAll(IPage::class.java.getName())) {
            val page: IBookPage?
            try {
                page = Class.forName(data.getClassName()).newInstance() as IBookPage?
                pages.add(page)
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        GSCapabilityStatsHandler.register()
    }

    @Mod.EventHandler
    open fun init(event: FMLInitializationEvent) {
        proxy!!.registerRender()
        proxy!!.load()


        //proxy.register_event(new GSOreGenOtherMods());
        //proxy.register_event(new AchEvent());
        packetPipeline = GalaxySpaceChannelHandler.init()
        this.registerEntities()
        ResearchUtil.initResearches()

        //AchievementList.load();

        // TODO Register Planets: -------------------------------
        for (list in AsmodeusCore.bodies) if ((list)!!.canRegister()) (list)!!.init(event)


        if (event.getSide() == Side.CLIENT) {
            BookRegister.registerCatergories()
            pages.forEach(Consumer { page: IBookPage? -> BookUtils.addGuideBookPage((page)!!) })
        }

        GSCreativeTabs.GSBlocksTab =
            CreativeTabGC(CreativeTabs.getNextID(), "galaxyspace_blocks", ItemStack(GSBlocks.ASSEMBLER), null)
        GSCreativeTabs.GSItemsTab =
            CreativeTabGC(CreativeTabs.getNextID(), "galaxyspace_items", ItemStack(GSItems.INGOTS), null)
        GSCreativeTabs.GSArmorTab =
            CreativeTabGC(CreativeTabs.getNextID(), "galaxyspace_armor", ItemStack(GSItems.SPACE_SUIT_BODY), null)
        GSCreativeTabs.GSVehiclesTab =
            CreativeTabGC(CreativeTabs.getNextID(), "galaxyspace_rocket", ItemStack(GSItems.ROCKET_TIER_4), null)
    }

    @Mod.EventHandler
    open fun postInit(event: FMLPostInitializationEvent?) {
        proxy!!.postload()


        for (list in AsmodeusCore.bodies) if ((list)!!.canRegister()) (list)!!.postInit(event)

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, GSGuiHandler())
        GSThreadVersionCheck.startCheck()
    }

    private fun registerEntities() {
        this.registerCreatures()
        this.registerNonMobEntities()
        this.registerTileEntities()
    }

    open fun registerCreatures() {
        val back: Int = -16777216
        val fore: Int = -8388608
        GCCoreUtil.registerGalacticraftCreature(EntityBossBlaze::class.java, "evolved_boss_blaze", back, fore)
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedColdBlaze::class.java, "evolved_coldblaze", back, fore)
        GCCoreUtil.registerGalacticraftCreature(EntityBossGhast::class.java, "evolved_boss_ghast", back, fore)
        GCCoreUtil.registerGalacticraftCreature(EntityAstroWolf::class.java, "astro_wolf", back, fore)
        //GCCoreUtil.registerGalacticraftCreature(EntityEvolvedFireCreeper.class, "EvolvedFireCreeper", back, fore);
        //GCCoreUtil.registerGalacticraftCreature(EntityEvolvedFireSkeleton.class, "EvolvedFireSkeleton", back, fore);
        //GCCoreUtil.registerGalacticraftCreature(EntityEvolvedFireSpider.class, "EvolvedFireSpider", back, fore);    	
    }

    private fun registerNonMobEntities() {
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityTier4Rocket::class.java, "rocket_tier_4", 150, 1, false)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityTier5Rocket::class.java, "rocket_tier_5", 150, 1, false)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityTier6Rocket::class.java, "rocket_tier_6", 150, 1, false)
        GCCoreUtil.registerGalacticraftNonMobEntity(
            EntityMultiSeatRocketTest::class.java,
            "rocket_multi_seat",
            150,
            1,
            false
        )
        GCCoreUtil.registerGalacticraftNonMobEntity(
            EntityCustomCargoRocket::class.java,
            "rocket_fluid_cargo",
            150,
            1,
            false
        )
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityIceSpike::class.java, "ice_spike", 40, 100, true)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityMarsRover::class.java, "mars_rover", 150, 1, false)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityLaserBeam::class.java, "laser_beam", 40, 100, false)
        //GCCoreUtil.registerGalacticraftNonMobEntity(GSEntityMeteor.class, "GS Meteor", 150, 5, true);    	
    }

    private fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityAssembler::class.java, "GS Assembler")
        GameRegistry.registerTileEntity(TileEntityFuelGenerator::class.java, "GS Fuel Generator")
        GameRegistry.registerTileEntity(TileEntityModernSolarPanel::class.java, "GS Modern Solar Panel")
        GameRegistry.registerTileEntity(TileEntityWindGenerator::class.java, "GS Wind Generator")
        GameRegistry.registerTileEntity(TileEntityRocketAssembler::class.java, "GS Rocket Assembler")
        GameRegistry.registerTileEntity(TileEntityUniversalRecycler::class.java, "GS Universal Recycler")
        GameRegistry.registerTileEntity(TileEntityLiquidExtractor::class.java, "GS Liquid Extractor")
        GameRegistry.registerTileEntity(TileEntityLiquidSeparator::class.java, "GS Liquid Separator")
        GameRegistry.registerTileEntity(TileEntityHydroponicBase::class.java, "GS Hydroponic Base")
        GameRegistry.registerTileEntity(TileEntityHydroponicFarm::class.java, "GS Hydroponic Farm")
        GameRegistry.registerTileEntity(TileEntityGravitationModule::class.java, "GS Gravitation Module")
        GameRegistry.registerTileEntity(TileEntityBlockCrystallTE::class.java, "GS Crystal")
        GameRegistry.registerTileEntity(TileEntityTreasureChestCeres::class.java, "GS Treasure Ceres")
        GameRegistry.registerTileEntity(TileEntityDungeonSpawnerCeres::class.java, "GS Spawner Boss Ceres")
        GameRegistry.registerTileEntity(TileEntityTreasureChestIo::class.java, "GS Treasure Io")
        GameRegistry.registerTileEntity(TileEntityDungeonSpawnerIo::class.java, "GS Spawner Boss Io")
        GameRegistry.registerTileEntity(TileEntityRadiationStabiliser::class.java, "GS Radiation Stabiliser")
        GameRegistry.registerTileEntity(TileEntityPanelController::class.java, "GS Panel Controller")
        GameRegistry.registerTileEntity(TileEntityPlanetShield::class.java, "GS Planet Shield")
        GameRegistry.registerTileEntity(TileEntityModernStorageModule::class.java, "GS Advanced Storage Module")
        GameRegistry.registerTileEntity(TileEntityAdvLandingPad::class.java, "GS Advanced Landing Pad Full")
        GameRegistry.registerTileEntity(TileEntityAdvLandingPadSingle::class.java, "GS Advanced Landing Pad")
        GameRegistry.registerTileEntity(TileEntityModificationTable::class.java, "GS Modification Table")
        GameRegistry.registerTileEntity(TileEntityGasBurner::class.java, "GS Gas Burner")
        GameRegistry.registerTileEntity(TileEntityAdvOxygenStorageModule::class.java, "GS Oxygen Storage Module")
        GameRegistry.registerTileEntity(TileEntityWindSolarPanel::class.java, "GS Solar Wind Panel")
        GameRegistry.registerTileEntity(TileEntityAdvCircuitFabricator::class.java, "GS Advanced Circuit Fabricator")
        GameRegistry.registerTileEntity(TileEntityEnergyPad::class.java, "GS Energy Pad")
        GameRegistry.registerTileEntity(TileEntityResearchTable::class.java, "GS Research Table")
        GameRegistry.registerTileEntity(TileEntityGasCollector::class.java, "GS Gas Collector")
        GameRegistry.registerTileEntity(TileEntityGasGenerator::class.java, "GS Gas Generator")

        /*    	
    	GameRegistry.registerTileEntity(TileEntityAdvOxygenSealer.class, "GS Oxygen Sealer");
    	GameRegistry.registerTileEntity(TileEntityAdvFuelLoader.class, "GS Fuel Loader");
    	
    	GameRegistry.registerTileEntity(TileEntityThermodynamicReactor.class, "GS Thermodynamic Reactor");*/
    }

    open fun debug(message: Any) {
        if (debug && log != null) {
            log!!.log(Level.INFO, "[DEBUG]: " + message.toString())
        }
    }

    private fun initModInfo(info: ModMetadata) {
        info.autogenerated = false
        info.modId = MODID
        info.name = FoundationMetadata.NAME
        info.version = FoundationMetadata.VERSION
        info.description = FoundationMetadata.DESCRIPTION
        info.authorList = mutableListOf<String?>("Vi[Told]#1308", "AstroMaster#5045")
    }

    @Mod.EventBusSubscriber(modid = MODID)
    object RegistrationHandler {
        @JvmStatic
        @SubscribeEvent
        fun registerModels(event: ModelRegistryEvent?) {
            proxy!!.registerVariants()
        }

        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item?>?) {
            GSBlocks.oreDictRegistration()
            GSItems.oreDictRegistration()

            for (list in AsmodeusCore.bodies) if ((list)!!.canRegister()) (list)!!.registerItems(event)
        }

        @JvmStatic
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun registerRecipes(event: RegistryEvent.Register<IRecipe?>?) {
            for (list in AsmodeusCore.bodies) if ((list)!!.canRegister()) (list)!!.registerRecipes()
        }
    }

    companion object {
        const val major_version: Int = 2

        const val minor_version: Int = 2

        const val build_version: Int = 0

        const val NAME: String = "GalaxySpace"

        const val MODID: String = "galaxyspace"

        const val VERSION: String = "$major_version.$minor_version.$build_version"

        @JvmField
        val ASSET_PREFIX: String = MODID

        @JvmField
        val TEXTURE_PREFIX: String = ASSET_PREFIX + ":"

        @JvmField
        var debug: Boolean = false

        @JvmField
        var packetPipeline: GalaxySpaceChannelHandler? = null
        private var log: Logger? = null

        //---------------------------------------------
        @JvmField
        @Mod.Instance(MODID)
        var instance: GalaxySpace? = null

        @JvmField
        @SidedProxy(
            clientSide = "galaxyspace.core.proxy.ClientProxy",
            serverSide = "galaxyspace.core.proxy.CommonProxy"
        )
        var proxy: CommonProxy? = null

        init {
            FluidRegistry.enableUniversalBucket()
        }


        @JvmStatic
        fun info(message: String?) {
            if (log != null) {
                log!!.log(Level.INFO, "[INFO]: " + message)
            }
        }
    }
}
