package micdoodle8.mods.galacticraft.core

import net.minecraft.world.biome.Biome.SpawnListEntry
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.Mod.Instance

import api.player.server.ServerPlayerAPI
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import micdoodle8.mods.galacticraft.core.util.ThreadRequirementMissing
import micdoodle8.mods.galacticraft.core.util.ThreadVersionCheck
import java.io.IOException
import javax.imageio.ImageWriter
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.client.IGameScreen
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.Satellite
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.galaxies.Star
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo
import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.core.blocks.BlockGrating
import micdoodle8.mods.galacticraft.core.client.gui.GuiHandler
import micdoodle8.mods.galacticraft.core.client.screen.GameScreenBasic
import micdoodle8.mods.galacticraft.core.client.screen.GameScreenCelestial
import micdoodle8.mods.galacticraft.core.client.screen.GameScreenText
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.command.CommandGCEnergyUnits
import micdoodle8.mods.galacticraft.core.command.CommandGCHelp
import micdoodle8.mods.galacticraft.core.command.CommandGCHouston
import micdoodle8.mods.galacticraft.core.command.CommandGCInv
import micdoodle8.mods.galacticraft.core.command.CommandGCKit
import micdoodle8.mods.galacticraft.core.command.CommandJoinSpaceRace
import micdoodle8.mods.galacticraft.core.command.CommandKeepDim
import micdoodle8.mods.galacticraft.core.command.CommandPlanetTeleport
import micdoodle8.mods.galacticraft.core.command.CommandSpaceStationAddOwner
import micdoodle8.mods.galacticraft.core.command.CommandSpaceStationChangeOwner
import micdoodle8.mods.galacticraft.core.command.CommandSpaceStationRemoveOwner
import micdoodle8.mods.galacticraft.core.dimension.GCDimensions
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeMoon
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeOrbit
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeOverworld
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOverworldOrbit
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.energy.grid.ChunkPowerHandler
import micdoodle8.mods.galacticraft.core.entities.EntityAlienVillager
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedWitch
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import micdoodle8.mods.galacticraft.core.entities.EntityFlag
import micdoodle8.mods.galacticraft.core.entities.EntityHangingSchematic
import micdoodle8.mods.galacticraft.core.entities.EntityLander
import micdoodle8.mods.galacticraft.core.entities.EntityMeteor
import micdoodle8.mods.galacticraft.core.entities.EntityMeteorChunk
import micdoodle8.mods.galacticraft.core.entities.EntityParachest
import micdoodle8.mods.galacticraft.core.entities.EntitySkeletonBoss
import micdoodle8.mods.galacticraft.core.entities.EntityTier1Rocket
import micdoodle8.mods.galacticraft.core.entities.player.GCCapabilities
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerBaseMP
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.event.LootHandlerGC
import micdoodle8.mods.galacticraft.core.items.ItemSchematic
import micdoodle8.mods.galacticraft.core.network.ConnectionEvents
import micdoodle8.mods.galacticraft.core.network.ConnectionPacket
import micdoodle8.mods.galacticraft.core.network.GalacticraftChannelHandler
import micdoodle8.mods.galacticraft.core.proxy.CommonProxyCore
import micdoodle8.mods.galacticraft.core.recipe.RecipeManagerGC
import micdoodle8.mods.galacticraft.core.schematic.SchematicAdd
import micdoodle8.mods.galacticraft.core.schematic.SchematicMoonBuggy
import micdoodle8.mods.galacticraft.core.schematic.SchematicRocketT1
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLock
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLockController
import micdoodle8.mods.galacticraft.core.tile.TileEntityAluminumWire
import micdoodle8.mods.galacticraft.core.tile.TileEntityAluminumWireSwitch
import micdoodle8.mods.galacticraft.core.tile.TileEntityArclamp
import micdoodle8.mods.galacticraft.core.tile.TileEntityBuggyFueler
import micdoodle8.mods.galacticraft.core.tile.TileEntityBuggyFuelerSingle
import micdoodle8.mods.galacticraft.core.tile.TileEntityCargoLoader
import micdoodle8.mods.galacticraft.core.tile.TileEntityCargoUnloader
import micdoodle8.mods.galacticraft.core.tile.TileEntityCircuitFabricator
import micdoodle8.mods.galacticraft.core.tile.TileEntityCoalGenerator
import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting
import micdoodle8.mods.galacticraft.core.tile.TileEntityDeconstructor
import micdoodle8.mods.galacticraft.core.tile.TileEntityDish
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricFurnace
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricIngotCompressor
import micdoodle8.mods.galacticraft.core.tile.TileEntityEmergencyBox
import micdoodle8.mods.galacticraft.core.tile.TileEntityEnergyStorageModule
import micdoodle8.mods.galacticraft.core.tile.TileEntityFallenMeteor
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidPipe
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidTank
import micdoodle8.mods.galacticraft.core.tile.TileEntityFuelLoader
import micdoodle8.mods.galacticraft.core.tile.TileEntityIngotCompressor
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPadSingle
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti
import micdoodle8.mods.galacticraft.core.tile.TileEntityNasaWorkbench
import micdoodle8.mods.galacticraft.core.tile.TileEntityNull
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCollector
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCompressor
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDecompressor
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDetector
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDistributor
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenStorageModule
import micdoodle8.mods.galacticraft.core.tile.TileEntityPainter
import micdoodle8.mods.galacticraft.core.tile.TileEntityPanelLight
import micdoodle8.mods.galacticraft.core.tile.TileEntityParaChest
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlatform
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlayerDetector
import micdoodle8.mods.galacticraft.core.tile.TileEntityRefinery
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar
import micdoodle8.mods.galacticraft.core.tile.TileEntitySpaceStationBase
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry
import micdoodle8.mods.galacticraft.core.tile.TileEntityThruster
import micdoodle8.mods.galacticraft.core.tile.TileEntityTreasureChest
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.CreativeTabGC
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.MapUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback
import micdoodle8.mods.galacticraft.core.world.gen.BiomeMoon
import micdoodle8.mods.galacticraft.core.world.gen.BiomeOrbit
import micdoodle8.mods.galacticraft.core.world.gen.OreGenOtherMods
import micdoodle8.mods.galacticraft.core.world.gen.OverworldGenerator
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.recipe.RecipeManagerAsteroids
import micdoodle8.mods.galacticraft.planets.mars.recipe.RecipeManagerMars
import micdoodle8.mods.galacticraft.planets.venus.recipe.RecipeManagerVenus
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraft.world.DimensionType
import net.minecraft.world.WorldProvider
import net.minecraft.world.WorldProviderSurface
import net.minecraft.world.biome.Biome
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.ModMetadata
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLInterModComms
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent
import net.minecraftforge.fml.common.event.FMLServerStartedEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.registries.GameData
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryManager
import net.minecraftforge.server.permission.DefaultPermissionLevel
import net.minecraftforge.server.permission.PermissionAPI
import java.io.File
import java.nio.file.Files
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedList

@Mod(
    modid = Constants.MOD_ID_CORE,
    name = GalacticraftCore.Companion.NAME,
    version = Constants.COMBINEDVERSION,
    useMetadata = true,
    acceptedMinecraftVersions = Constants.MCVERSION,
    dependencies = Constants.DEPENDENCIES_FORGE + Constants.DEPENDENCIES_MICCORE + Constants.DEPENDENCIES_MODS,
    guiFactory = "micdoodle8.mods.galacticraft.core.client.gui.screen.ConfigGuiFactoryCore"
)
open class GalacticraftCore {
    private var GCCoreSource: File? = null

    @Mod.EventHandler
    open fun preInit(event: FMLPreInitializationEvent) {
        GCCoreSource = event.getSourceFile()
        GCCapabilities.register()

        this.initModInfo(event.getModMetadata())
        isPlanetsLoaded = Loader.isModLoaded(Constants.MOD_ID_PLANETS)
        GCCoreUtil.nextID = 0

        if (CompatibilityManager.isSmartMovingLoaded || CompatibilityManager.isWitcheryLoaded) {
            isHeightConflictingModInstalled = true
        }

        solarSystemSol = SolarSystem("sol", "milky_way").setMapPosition(Vector3(0.0, 0.0, 0.0))
        planetOverworld = Planet("overworld").setParentSolarSystem(solarSystemSol).setRingColorRGB(0.1f, 0.9f, 0.6f)
            .setPhaseShift(0.0f) as Planet
        moonMoon = Moon("moon").setParentPlanet(planetOverworld).setRelativeSize(0.2667f)
            .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(13f, 13f))
            .setRelativeOrbitTime(1 / 0.01f) as Moon
        satelliteSpaceStation =
            Satellite("spacestation.overworld").setParentBody(planetOverworld).setRelativeSize(0.2667f)
                .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(9f, 9f))
                .setRelativeOrbitTime(1 / 0.05f) as Satellite

        MinecraftForge.EVENT_BUS.register(EventHandlerGC())
        handler = GCPlayerHandler()
        MinecraftForge.EVENT_BUS.register(handler)
        proxy!!.preInit(event)

        ConnectionPacket.bus = NetworkRegistry.INSTANCE.newEventDrivenChannel(ConnectionPacket.CHANNEL)
        ((ConnectionPacket.bus)!!.register(ConnectionPacket()))!!

        ConfigManagerCore.initialize(File(event.getModConfigurationDirectory(), Constants.CONFIG_FILE))
        EnergyConfigHandler.setDefaultValues(File(event.getModConfigurationDirectory(), Constants.POWER_CONFIG_FILE))
        ChunkLoadingCallback.loadConfig(File(event.getModConfigurationDirectory(), Constants.CHUNKLOADER_CONFIG_FILE))

        galacticraftBlocksTab = CreativeTabGC(CreativeTabs.getNextID(), "galacticraft_blocks", null, null)
        galacticraftItemsTab = CreativeTabGC(CreativeTabs.getNextID(), "galacticraft_items", null, null)

        GCFluids.registerOilandFuel()

        if (CompatibilityManager.PlayerAPILoaded) {
            ServerPlayerAPI.register(Constants.MOD_ID_CORE, GCPlayerBaseMP::class.java)
        }

        GCBlocks.initBlocks()
        GCItems.initItems()

        GCFluids.registerFluids()

        //Force initialisation of GC biome types in preinit (after config load) - this helps BiomeTweaker by initialising mod biomes in a fixed order during mod loading
        satelliteSpaceStation!!.setBiomeInfo(BiomeOrbit.space)
        moonMoon!!.setBiomeInfo(BiomeMoon.moonFlat)
    }

    @Mod.EventHandler
    open fun init(event: FMLInitializationEvent?) {
        galacticraftBlocksTab!!.setItemForTab(ItemStack(Item.getItemFromBlock(GCBlocks.machineBase2)))
        galacticraftItemsTab!!.setItemForTab(ItemStack(GCItems.rocketTier1))

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            GCBlocks.finalizeSort()
            GCItems.finalizeSort()
        }

        proxy!!.init(event)

        packetPipeline = GalacticraftChannelHandler.init()

        val starSol: Star = Star("sol").setParentSolarSystem(solarSystemSol).setTierRequired(-1) as Star
        starSol.setBodyIcon(ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/sun.png"))
        solarSystemSol!!.setMainStar(starSol)

        planetOverworld!!.setBodyIcon(
            ResourceLocation(
                Constants.ASSET_PREFIX,
                "textures/gui/celestialbodies/earth.png"
            )
        )
        planetOverworld!!.setDimensionInfo(ConfigManagerCore.idDimensionOverworld, WorldProvider::class.java, false)
            .setTierRequired(1)
        planetOverworld!!.atmosphereComponent(EnumAtmosphericGas.NITROGEN)
            .atmosphereComponent(EnumAtmosphericGas.OXYGEN).atmosphereComponent(EnumAtmosphericGas.ARGON)
            .atmosphereComponent(EnumAtmosphericGas.WATER)
        planetOverworld!!.addChecklistKeys("equip_parachute")

        moonMoon!!.setDimensionInfo(ConfigManagerCore.idDimensionMoon, WorldProviderMoon::class.java).setTierRequired(1)
        moonMoon!!.setBodyIcon(ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/moon.png"))
        moonMoon!!.setAtmosphere(AtmosphereInfo(false, false, false, 0.0f, 0.0f, 0.0f))
        moonMoon!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedZombie::class.java, 8, 2, 3))
        moonMoon!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSpider::class.java, 8, 2, 3))
        moonMoon!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSkeleton::class.java, 8, 2, 3))
        moonMoon!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedCreeper::class.java, 8, 2, 3))
        moonMoon!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedEnderman::class.java, 10, 1, 4))
        moonMoon!!.addChecklistKeys("equip_oxygen_suit")

        //Satellites must always have a WorldProvider implementing IOrbitDimension
        satelliteSpaceStation!!.setDimensionInfo(
            ConfigManagerCore.idDimensionOverworldOrbit,
            ConfigManagerCore.idDimensionOverworldOrbitStatic,
            WorldProviderOverworldOrbit::class.java
        ).setTierRequired(1)
        satelliteSpaceStation!!.setBodyIcon(
            ResourceLocation(
                Constants.ASSET_PREFIX,
                "textures/gui/celestialbodies/space_station.png"
            )
        )
        satelliteSpaceStation!!.setAtmosphere(AtmosphereInfo(false, false, false, 0.0f, 0.1f, 0.02f))
        satelliteSpaceStation!!.addChecklistKeys("equip_oxygen_suit", "create_grapple")

        ForgeChunkManager.setForcedChunkLoadingCallback(instance, ChunkLoadingCallback())
        MinecraftForge.EVENT_BUS.register(ConnectionEvents())

        SchematicRegistry.registerSchematicRecipe(SchematicRocketT1())
        SchematicRegistry.registerSchematicRecipe(SchematicMoonBuggy())
        SchematicRegistry.registerSchematicRecipe(SchematicAdd())
        ChunkPowerHandler.initiate()
        LootHandlerGC.registerAll()

        this.registerCreatures()
        this.registerOtherEntities()
        this.registerTileEntities()

        GalaxyRegistry.registerSolarSystem((solarSystemSol)!!)
        GalaxyRegistry.registerPlanet((planetOverworld)!!)
        GalaxyRegistry.registerMoon((moonMoon)!!)
        GalaxyRegistry.registerSatellite((satelliteSpaceStation)!!)
        GCDimensions.ORBIT = GalacticraftRegistry.registerDimension(
            "Space Station",
            "_orbit",
            ConfigManagerCore.idDimensionOverworldOrbit,
            WorldProviderOverworldOrbit::class.java,
            false
        )
        if (GCDimensions.ORBIT == null) {
            GCLog.severe("Failed to register space station dimension type with ID " + ConfigManagerCore.idDimensionOverworldOrbit)
        }
        GCDimensions.ORBIT_KEEPLOADED = GalacticraftRegistry.registerDimension(
            "Space Station",
            "_orbit",
            ConfigManagerCore.idDimensionOverworldOrbitStatic,
            WorldProviderOverworldOrbit::class.java,
            true
        )
        if (GCDimensions.ORBIT_KEEPLOADED == null) {
            GCLog.severe("Failed to register space station dimension type with ID " + ConfigManagerCore.idDimensionOverworldOrbitStatic)
        }
        GalacticraftRegistry.registerTeleportType(WorldProviderSurface::class.java, TeleportTypeOverworld())
        GalacticraftRegistry.registerTeleportType(WorldProviderOverworldOrbit::class.java, TeleportTypeOrbit())
        GalacticraftRegistry.registerTeleportType(WorldProviderMoon::class.java, TeleportTypeMoon())
        GalacticraftRegistry.registerRocketGui(
            WorldProviderOverworldOrbit::class.java,
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/overworld_rocket_gui.png")
        )
        GalacticraftRegistry.registerRocketGui(
            WorldProviderSurface::class.java,
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/overworld_rocket_gui.png")
        )
        GalacticraftRegistry.registerRocketGui(
            WorldProviderMoon::class.java,
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/moon_rocket_gui.png")
        )
        GalacticraftRegistry.addDungeonLoot(1, ItemStack(GCItems.schematic, 1, 0))
        GalacticraftRegistry.addDungeonLoot(1, ItemStack(GCItems.schematic, 1, 1))

        if (ConfigManagerCore.enableCopperOreGen) {
            GameRegistry.registerWorldGenerator(OverworldGenerator((GCBlocks.basicBlock)!!, 5, 24, 0, 75, 7), 4)
        }

        if (ConfigManagerCore.enableTinOreGen) {
            GameRegistry.registerWorldGenerator(OverworldGenerator((GCBlocks.basicBlock)!!, 6, 22, 0, 60, 7), 4)
        }

        if (ConfigManagerCore.enableAluminumOreGen) {
            GameRegistry.registerWorldGenerator(OverworldGenerator((GCBlocks.basicBlock)!!, 7, 18, 0, 45, 7), 4)
        }

        if (ConfigManagerCore.enableSiliconOreGen) {
            GameRegistry.registerWorldGenerator(OverworldGenerator((GCBlocks.basicBlock)!!, 8, 3, 0, 25, 7), 4)
        }

        FMLInterModComms.sendMessage("OpenBlocks", "donateUrl", "http://www.patreon.com/micdoodle8")
        registerCoreGameScreens()

        GCFluids.registerLegacyFluids()
        GCFluids.registerDispenserBehaviours()

        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_MASK, EnumExtendedInventorySlot.MASK, GCItems.oxMask)
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_OXYGEN_GEAR,
            EnumExtendedInventorySlot.GEAR,
            GCItems.oxygenGear
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_OXYGEN_TANK_LIGHT,
            EnumExtendedInventorySlot.LEFT_TANK,
            GCItems.oxTankLight
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_OXYGEN_TANK_LIGHT,
            EnumExtendedInventorySlot.RIGHT_TANK,
            GCItems.oxTankLight
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_OXYGEN_TANK_MEDIUM,
            EnumExtendedInventorySlot.LEFT_TANK,
            GCItems.oxTankMedium
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_OXYGEN_TANK_MEDIUM,
            EnumExtendedInventorySlot.RIGHT_TANK,
            GCItems.oxTankMedium
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_OXYGEN_TANK_HEAVY,
            EnumExtendedInventorySlot.LEFT_TANK,
            GCItems.oxTankHeavy
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_OXYGEN_TANK_HEAVY,
            EnumExtendedInventorySlot.RIGHT_TANK,
            GCItems.oxTankHeavy
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_OXYGEN_TANK_INFINITE,
            EnumExtendedInventorySlot.LEFT_TANK,
            GCItems.oxygenCanisterInfinite
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_OXYGEN_TANK_INFINITE,
            EnumExtendedInventorySlot.RIGHT_TANK,
            GCItems.oxygenCanisterInfinite
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_PARACHUTE,
            EnumExtendedInventorySlot.PARACHUTE,
            GCItems.parachute
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_FREQUENCY_MODULE,
            EnumExtendedInventorySlot.FREQUENCY_MODULE,
            ItemStack(GCItems.basicItem, 1, 19)
        )

        proxy!!.registerFluidTexture(
            GCFluids.fluidOil,
            ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/underoil.png")
        )
        proxy!!.registerFluidTexture(
            GCFluids.fluidFuel,
            ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/underfuel.png")
        )

        PermissionAPI.registerNode(
            Constants.PERMISSION_CREATE_STATION,
            DefaultPermissionLevel.ALL,
            "Allows players to create space stations"
        )

        //        switch (this.getSlotIndex())
//        {
//        case 0:
//            return itemstack.getItem() instanceof ItemOxygenMask;
//        case 1:
//            return itemstack.getItem() == GCItems.oxygenGear;
//        case 2:
//        case 3:
//            return itemstack.getItem() instanceof ItemOxygenTank || itemstack.getItem() instanceof ItemCanisterOxygenInfinite;
//        case 4:
//            return itemstack.getItem() instanceof ItemParaChute;
//        case 5:
//            return itemstack.getItem() == GCItems.basicItem && itemstack.getItemDamage() == 19;
//        case 6:
//            return this.thermalArmorSlotValid(itemstack, 0);
//        case 7:
//            return this.thermalArmorSlotValid(itemstack, 1);
//        case 8:
//            return this.thermalArmorSlotValid(itemstack, 2);
//        case 9:
//            return this.thermalArmorSlotValid(itemstack, 3);
//        case 10:
//            return this.shieldControllerSlotValid(itemstack);
//        }
    }

    @Mod.EventHandler
    open fun postInit(event: FMLPostInitializationEvent) {
        planetMercury = makeDummyPlanet("mercury", solarSystemSol)
        if (planetMercury != null) {
            planetMercury!!.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(1.45f)
                .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(0.5f, 0.5f))
                .setRelativeOrbitTime(0.24096385542168674698795180722892f)
        }
        planetVenus = makeDummyPlanet("venus", solarSystemSol)
        if (planetVenus != null) {
            planetVenus!!.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(2.0f)
                .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(0.75f, 0.75f))
                .setRelativeOrbitTime(0.61527929901423877327491785323111f)
        }
        planetMars = makeDummyPlanet("mars", solarSystemSol)
        if (planetMars != null) {
            planetMars!!.setRingColorRGB(0.67f, 0.1f, 0.1f).setPhaseShift(0.1667f).setRelativeSize(0.5319f)
                .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(1.25f, 1.25f))
                .setRelativeOrbitTime(1.8811610076670317634173055859803f)
        }
        planetJupiter = makeDummyPlanet("jupiter", solarSystemSol)
        if (planetJupiter != null) {
            planetJupiter!!.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(Math.PI.toFloat())
                .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(1.5f, 1.5f))
                .setRelativeOrbitTime(11.861993428258488499452354874042f)
        }
        planetSaturn = makeDummyPlanet("saturn", solarSystemSol)
        if (planetSaturn != null) {
            planetSaturn!!.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(5.45f)
                .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(1.75f, 1.75f))
                .setRelativeOrbitTime(29.463307776560788608981380065717f)
        }
        planetUranus = makeDummyPlanet("uranus", solarSystemSol)
        if (planetUranus != null) {
            planetUranus!!.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(1.38f)
                .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(2.0f, 2.0f))
                .setRelativeOrbitTime(84.063526834611171960569550930997f)
        }
        planetNeptune = makeDummyPlanet("neptune", solarSystemSol)
        if (planetNeptune != null) {
            planetNeptune!!.setRingColorRGB(0.1f, 0.9f, 0.6f).setPhaseShift(1.0f)
                .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(2.25f, 2.25f))
                .setRelativeOrbitTime(164.84118291347207009857612267251f)
        }

        MinecraftForge.EVENT_BUS.register(OreGenOtherMods())

        proxy!!.postInit(event)

        val cBodyList: ArrayList<CelestialBody> = ArrayList<CelestialBody>()
        cBodyList.addAll((GalaxyRegistry.getRegisteredPlanets())!!.values)
        cBodyList.addAll((GalaxyRegistry.getRegisteredMoons())!!.values)

        for (body: CelestialBody in cBodyList) {
            if (body.shouldAutoRegister()) {
                val id: Int = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, body.getDimensionID())
                //It's important this is done in the same order as planets will be registered by WorldUtil.registerPlanet();
                val type: DimensionType? = GalacticraftRegistry.registerDimension(
                    body.getTranslationKey(),
                    body.getDimensionSuffix(),
                    body.getDimensionID(),
                    (body.getWorldProvider())!!,
                    body.getForceStaticLoad() || id < 0
                )
                if (type != null) {
                    body.initialiseMobSpawns()
                } else {
                    body.setUnreachable()
                    GCLog.severe("Tried to register dimension for body: " + body.getTranslationKey() + " hit conflict with ID " + body.getDimensionID())
                }
            }

            if (body.getSurfaceBlocks() != null) {
                TransformerHooks.spawnListAE2_GC.addAll((body.getSurfaceBlocks())!!)
            }
        }

        GCDimensions.MOON = WorldUtil.getDimensionTypeById(ConfigManagerCore.idDimensionMoon)

        CompatibilityManager.checkForCompatibleMods()
        RecipeManagerGC.loadCompatibilityRecipes()
        TileEntityDeconstructor.initialiseRecipeList()
        ItemSchematic.registerSchematicItems()
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, GuiHandler())
        MinecraftForge.EVENT_BUS.register(TickHandlerServer())
        GalaxyRegistry.refreshGalaxies()

        GalacticraftRegistry.registerScreen(GameScreenText()) //Screen API demo

        //Note: add-ons can register their own screens in postInit by calling GalacticraftRegistry.registerScreen(IGameScreen) like this.
        //[Called on both client and server: do not include any client-specific code in the new game screen's constructor method.]
        try {
            jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next()
            writeParam = jpgWriter!!.getDefaultWriteParam()
            writeParam!!.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
            writeParam!!.setCompressionQuality(1.0f)
            enableJPEG = true
        } catch (e: UnsatisfiedLinkError) {
            GCLog.severe("Error initialising JPEG compressor - this is likely caused by OpenJDK - see https://wiki.micdoodle8.com/wiki/Compatibility#For_clients_running_OpenJDK")
            e.printStackTrace()
        }

        if (event.getSide() == Side.SERVER) {
            this.loadLanguageCore("en_US")
        }
    }

    open fun loadLanguageCore(lang: String?) {
        GCCoreUtil.loadLanguage((lang)!!, Constants.ASSET_PREFIX, (this.GCCoreSource)!!)
    }

    @Mod.EventHandler
    open fun serverAboutToStart(event: FMLServerAboutToStartEvent?) {
        TickHandlerServer.restart()
    }

    @Mod.EventHandler
    open fun serverInit(event: FMLServerStartedEvent?) {
        if (ThreadRequirementMissing.INSTANCE == null) {
            ThreadRequirementMissing.beginCheck(GCCoreUtil.getEffectiveSide())
        }

        ThreadVersionCheck.startCheck()
        BlockVec3.chunkCacheDim = Int.MAX_VALUE
    }

    @Mod.EventHandler
    open fun onServerStarting(event: FMLServerStartingEvent) {
        GCCoreUtil.notifyStarted(event.getServer())
        val worldFolder: File? = DimensionManager.getCurrentSaveRootDirectory()
        moveLegacyGCFileLocations(worldFolder)

        event.registerServerCommand(CommandSpaceStationAddOwner())
        event.registerServerCommand(CommandSpaceStationChangeOwner())
        event.registerServerCommand(CommandSpaceStationRemoveOwner())
        event.registerServerCommand(CommandPlanetTeleport())
        event.registerServerCommand(CommandKeepDim())
        event.registerServerCommand(CommandGCInv())
        event.registerServerCommand(CommandGCHelp())
        event.registerServerCommand(CommandGCKit())
        event.registerServerCommand(CommandGCHouston())
        event.registerServerCommand(CommandGCEnergyUnits())
        event.registerServerCommand(CommandJoinSpaceRace())

        WorldUtil.initialiseDimensionNames()
        WorldUtil.registerSpaceStations(event.getServer(), File(worldFolder, "galacticraft"))

        val cBodyList: ArrayList<CelestialBody> = ArrayList<CelestialBody>()
        cBodyList.addAll((GalaxyRegistry.getRegisteredPlanets())!!.values)
        cBodyList.addAll((GalaxyRegistry.getRegisteredMoons())!!.values)

        for (body: CelestialBody in cBodyList) {
            if (body.shouldAutoRegister()) {
                if (!WorldUtil.registerPlanet(body.getDimensionID(), body.getReachable(), 0)) {
                    body.setUnreachable()
                }
            }
        }
    }

    private fun moveLegacyGCFileLocations(worldFolder: File?) {
        val destFolder: File = File(worldFolder, "galacticraft")
        if (!destFolder.exists()) {
            if (!destFolder.mkdirs()) return
        }
        val dataFolder: File = File(worldFolder, "data")
        if (!dataFolder.exists()) return

        moveGCFile(File(dataFolder, "GCAsteroidData.dat"), destFolder)
        moveGCFile(File(dataFolder, "GCSpaceRaceData.dat"), destFolder)
        moveGCFile(File(dataFolder, "GCSpinData.dat"), destFolder)
        moveGCFile(File(dataFolder, "GCInv_savefile.dat"), destFolder)
        val names: Array<String> = dataFolder.list()
        for (name: String in names) {
            if (name.startsWith("spacestation_") && name.endsWith(".dat")) {
                moveGCFile(File(dataFolder, name), destFolder)
            }
        }
    }

    private fun moveGCFile(file: File, destFolder: File) {
        if (file.exists()) {
            val destPath: File = File(destFolder, file.getName())
            if (destPath.exists()) {
                GCLog.info("Deleting duplicate Galacticraft data file: " + file.getName())
                file.delete()
                return
            }
            try {
                Files.move(file.toPath(), destPath.toPath())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Mod.EventHandler
    open fun onServerStopping(var1: FMLServerStoppingEvent?) {
        MapUtil.saveMapProgress()
    }

    @Mod.EventHandler
    open fun onServerStop(var1: FMLServerStoppedEvent?) {
        // Unregister dimensions
        WorldUtil.unregisterPlanets()
        WorldUtil.unregisterSpaceStations()
        GCCoreUtil.notifyStarted(null)
    }

    private fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityTreasureChest::class.java, "GC Treasure Chest")
        GameRegistry.registerTileEntity(TileEntityOxygenDistributor::class.java, "GC Air Distributor")
        GameRegistry.registerTileEntity(TileEntityOxygenCollector::class.java, "GC Air Collector")
        GameRegistry.registerTileEntity(TileEntityFluidPipe::class.java, "GC Oxygen Pipe")
        GameRegistry.registerTileEntity(TileEntityAirLock::class.java, "GC Air Lock Frame")
        GameRegistry.registerTileEntity(TileEntityRefinery::class.java, "GC Refinery")
        GameRegistry.registerTileEntity(TileEntityNasaWorkbench::class.java, "GC NASA Workbench")
        GameRegistry.registerTileEntity(TileEntityDeconstructor::class.java, "GC Deconstructor")
        GameRegistry.registerTileEntity(TileEntityOxygenCompressor::class.java, "GC Air Compressor")
        GameRegistry.registerTileEntity(TileEntityFuelLoader::class.java, "GC Fuel Loader")
        GameRegistry.registerTileEntity(TileEntityLandingPadSingle::class.java, "GC Landing Pad")
        GameRegistry.registerTileEntity(TileEntityLandingPad::class.java, "GC Landing Pad Full")
        GameRegistry.registerTileEntity(TileEntitySpaceStationBase::class.java, "GC Space Station")
        GameRegistry.registerTileEntity(TileEntityMulti::class.java, "GC Dummy Block")
        GameRegistry.registerTileEntity(TileEntityOxygenSealer::class.java, "GC Air Sealer")
        GameRegistry.registerTileEntity(TileEntityDungeonSpawner::class.java, "GC Dungeon Boss Spawner")
        GameRegistry.registerTileEntity(TileEntityOxygenDetector::class.java, "GC Oxygen Detector")
        GameRegistry.registerTileEntity(TileEntityBuggyFueler::class.java, "GC Buggy Fueler")
        GameRegistry.registerTileEntity(TileEntityBuggyFuelerSingle::class.java, "GC Buggy Fueler Single")
        GameRegistry.registerTileEntity(TileEntityCargoLoader::class.java, "GC Cargo Loader")
        GameRegistry.registerTileEntity(TileEntityCargoUnloader::class.java, "GC Cargo Unloader")
        GameRegistry.registerTileEntity(TileEntityParaChest::class.java, "GC Parachest Tile")
        GameRegistry.registerTileEntity(TileEntitySolar::class.java, "GC Solar Panel")
        GameRegistry.registerTileEntity(TileEntityDish::class.java, "GC Radio Telescope")
        GameRegistry.registerTileEntity(TileEntityCrafting::class.java, "GC Magnetic Crafting Table")
        GameRegistry.registerTileEntity(TileEntityEnergyStorageModule::class.java, "GC Energy Storage Module")
        GameRegistry.registerTileEntity(TileEntityCoalGenerator::class.java, "GC Coal Generator")
        GameRegistry.registerTileEntity(TileEntityElectricFurnace::class.java, "GC Electric Furnace")
        GameRegistry.registerTileEntity(TileEntityAluminumWire::class.java, "GC Aluminum Wire")
        GameRegistry.registerTileEntity(TileEntityAluminumWireSwitch::class.java, "GC Switchable Aluminum Wire")
        GameRegistry.registerTileEntity(TileEntityFallenMeteor::class.java, "GC Fallen Meteor")
        GameRegistry.registerTileEntity(TileEntityIngotCompressor::class.java, "GC Ingot Compressor")
        GameRegistry.registerTileEntity(TileEntityElectricIngotCompressor::class.java, "GC Electric Ingot Compressor")
        GameRegistry.registerTileEntity(TileEntityCircuitFabricator::class.java, "GC Circuit Fabricator")
        GameRegistry.registerTileEntity(TileEntityAirLockController::class.java, "GC Air Lock Controller")
        GameRegistry.registerTileEntity(TileEntityOxygenStorageModule::class.java, "GC Oxygen Storage Module")
        GameRegistry.registerTileEntity(TileEntityOxygenDecompressor::class.java, "GC Oxygen Decompressor")
        GameRegistry.registerTileEntity(TileEntityThruster::class.java, "GC Space Station Thruster")
        GameRegistry.registerTileEntity(TileEntityArclamp::class.java, "GC Arc Lamp")
        GameRegistry.registerTileEntity(TileEntityScreen::class.java, "GC View Screen")
        GameRegistry.registerTileEntity(TileEntityPanelLight::class.java, "GC Panel Lighting")
        GameRegistry.registerTileEntity(TileEntityTelemetry::class.java, "GC Telemetry Unit")
        GameRegistry.registerTileEntity(TileEntityPainter::class.java, "GC Painter")
        GameRegistry.registerTileEntity(TileEntityFluidTank::class.java, "GC Fluid Tank")
        GameRegistry.registerTileEntity(TileEntityPlayerDetector::class.java, "GC Player Detector")
        GameRegistry.registerTileEntity(TileEntityPlatform::class.java, "GC Platform")
        GameRegistry.registerTileEntity(TileEntityEmergencyBox::class.java, "GC Emergency Post")
        GameRegistry.registerTileEntity(TileEntityNull::class.java, "GC Null Tile")
    }

    private fun registerCreatures() {
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedSpider::class.java, "evolved_spider", 3419431, 11013646)
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedZombie::class.java, "evolved_zombie", 44975, 7969893)
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedCreeper::class.java, "evolved_creeper", 894731, 0)
        GCCoreUtil.registerGalacticraftCreature(
            EntityEvolvedSkeleton::class.java,
            "evolved_skeleton",
            12698049,
            4802889
        )
        GCCoreUtil.registerGalacticraftCreature(
            EntitySkeletonBoss::class.java,
            "evolved_skeleton_boss",
            12698049,
            4802889
        )
        GCCoreUtil.registerGalacticraftCreature(
            EntityAlienVillager::class.java,
            "alien_villager",
            ColorUtil.to32BitColor(255, 103, 145, 181),
            12422002
        )
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedEnderman::class.java, "evolved_enderman", 1447446, 0)
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedWitch::class.java, "evolved_witch", 3407872, 5349438)
    }

    private fun registerOtherEntities() {
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityTier1Rocket::class.java, "rocket_t1", 150, 1, false)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityMeteor::class.java, "meteor", 150, 5, true)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityBuggy::class.java, "buggy", 150, 5, true)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityFlag::class.java, "gcflag", 150, 5, true)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityParachest::class.java, "para_chest", 150, 5, true)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityLander::class.java, "lander", 150, 5, false)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityMeteorChunk::class.java, "meteor_chunk", 150, 5, true)
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityCelestialFake::class.java, "celestial_screen", 150, 5, false)
        GCCoreUtil.registerGalacticraftNonMobEntity(
            EntityHangingSchematic::class.java,
            "hanging_schematic",
            150,
            5,
            false
        )
    }

    private fun makeDummyPlanet(name: String, system: SolarSystem?): Planet? {
        // Loop through all planets to make sure it's not registered as a reachable dimension first
        for (body: CelestialBody? in ArrayList<Planet?>((GalaxyRegistry.getRegisteredPlanets())!!.values)) {
            if (body is Planet && name == body.getName()) {
                if (body.getParentSolarSystem() === system) {
                    return null
                }
            }
        }

        val planet: Planet = Planet(name).setParentSolarSystem(system)
        planet.setBodyIcon(ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/" + name + ".png"))
        GalaxyRegistry.registerPlanet(planet)
        return planet
    }

    private fun initModInfo(info: ModMetadata) {
        info.autogenerated = false
        info.modId = Constants.MOD_ID_CORE
        info.name = NAME
        info.version = Constants.COMBINEDVERSION
        info.description = "An advanced space travel mod for Minecraft!"
        info.url = "https://micdoodle8.com/"
        info.authorList =
            mutableListOf<String?>("micdoodle8", "radfast", "EzerArch", "fishtaco", "SpaceViking", "SteveKunG")
        info.logoFile = "assets/galacticraftcore/galacticraft_logo.png"
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID_CORE)
    object RegistrationHandler {
        @JvmStatic
        @SubscribeEvent
        fun registerBlocks(event: RegistryEvent.Register<Block?>) {
            GCBlocks.registerBlocks(event.getRegistry())
            CompatibilityManager.registerMicroBlocks()
        }

        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item?>) {
            // First, the final steps of block registration
            val blockRegistry: IForgeRegistry<Block?>? = RegistryManager.ACTIVE.getRegistry<Block?>(GameData.BLOCKS)
            GCBlocks.doOtherModsTorches((blockRegistry)!!)
            BlockGrating.createForgeFluidVersions((blockRegistry)!!)

            GCItems.registerItems(event.getRegistry())

            //RegisterSorted for blocks cannot be run until all the items have been registered 
            if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
                for (item: Item? in itemListTrue) {
                    GCItems.registerSorted((item)!!)
                }
                for (block: Block? in blocksList) {
                    GCBlocks.registerSorted((block)!!)
                }
            }

            GCBlocks.oreDictRegistrations()
            GCItems.oreDictRegistrations()
        }

        @JvmStatic
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun registerRecipes(event: RegistryEvent.Register<IRecipe?>?) {
            RecipeManagerGC.addUniversalRecipes()
            RecipeManagerGC.setConfigurableRecipes()
            if (isPlanetsLoaded) {
                RecipeManagerAsteroids.addUniversalRecipes()
                RecipeManagerMars.addUniversalRecipes()
                RecipeManagerVenus.addUniversalRecipes()
            }
        }

        @JvmStatic
        @SubscribeEvent
        fun registerModels(event: ModelRegistryEvent?) {
            proxy!!.registerVariants()
            if (isPlanetsLoaded) {
                (GalacticraftPlanets.proxy)!!.registerVariants()
            }
        }

        @JvmStatic
        @SubscribeEvent
        fun registerBiomes(event: RegistryEvent.Register<Biome?>) {
            // First, final steps of item registration
            handler!!.registerTorchTypes()
            handler!!.registerItemChanges()

            for (biome: BiomeGenBaseGC in biomesList) {
                event.getRegistry().register(biome)
                if (!ConfigManagerCore.disableBiomeTypeRegistrations) {
                    biome.registerTypes(biome)
                }
            }
        }

        @JvmStatic
        @SubscribeEvent
        fun registerSounds(event: RegistryEvent.Register<SoundEvent?>) {
            if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
                GCSounds.registerSounds(event.getRegistry())
            }
        }
    }

    companion object {
        const val NAME: String = "Galacticraft Core"

        @JvmField
        @SidedProxy(
            clientSide = "micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore",
            serverSide = "micdoodle8.mods.galacticraft.core.proxy.CommonProxyCore"
        )
        var proxy: CommonProxyCore? = null

        @JvmField
        @Mod.Instance(Constants.MOD_ID_CORE)
        var instance: GalacticraftCore? = null

        @JvmField
        var isPlanetsLoaded: Boolean = false

        @JvmField
        var isHeightConflictingModInstalled: Boolean = false

        @JvmField
        var packetPipeline: GalacticraftChannelHandler? = null

        @JvmField
        var handler: GCPlayerHandler? = null

        @JvmField
        var galacticraftBlocksTab: CreativeTabGC? = null

        @JvmField
        var galacticraftItemsTab: CreativeTabGC? = null

        @JvmField
        var solarSystemSol: SolarSystem? = null

        @JvmField
        var planetMercury: Planet? = null

        @JvmField
        var planetVenus: Planet? = null

        @JvmField
        var planetMars: Planet? = null //Used only if GCPlanets not loaded

        @JvmField
        var planetOverworld: Planet? = null

        @JvmField
        var planetJupiter: Planet? = null

        @JvmField
        var planetSaturn: Planet? = null

        @JvmField
        var planetUranus: Planet? = null

        @JvmField
        var planetNeptune: Planet? = null

        @JvmField
        var moonMoon: Moon? = null

        @JvmField
        var satelliteSpaceStation: Satellite? = null

        @JvmField
        var itemList: LinkedList<ItemStack?> = LinkedList<ItemStack?>()

        @JvmField
        var itemListTrue: LinkedList<Item?> = LinkedList<Item?>()

        @JvmField
        var blocksList: LinkedList<Block?> = LinkedList<Block?>()

        @JvmField
        var biomesList: LinkedList<BiomeGenBaseGC> = LinkedList<BiomeGenBaseGC>()

        @JvmField
        var jpgWriter: ImageWriter? = null

        @JvmField
        var writeParam: ImageWriteParam? = null

        @JvmField
        var enableJPEG: Boolean = false

        init {
            FluidRegistry.enableUniversalBucket()
        }

        @JvmStatic
        private fun registerCoreGameScreens() {
            if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
                val rendererBasic: IGameScreen = GameScreenBasic()
                val rendererCelest: IGameScreen = GameScreenCelestial()
                GalacticraftRegistry.registerScreen(rendererBasic) //Type 0 - blank
                GalacticraftRegistry.registerScreen(rendererBasic) //Type 1 - local satellite view
                GalacticraftRegistry.registerScreen(rendererCelest) //Type 2 - solar system
                GalacticraftRegistry.registerScreen(rendererCelest) //Type 3 - local planet
                GalacticraftRegistry.registerScreen(rendererCelest) //Type 4 - render test
            } else {
                GalacticraftRegistry.registerScreensServer(5)
            }
        }
    }
}
