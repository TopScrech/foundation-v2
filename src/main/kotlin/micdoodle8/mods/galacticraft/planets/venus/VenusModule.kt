package micdoodle8.mods.galacticraft.planets.venus

import net.minecraft.world.biome.Biome.SpawnListEntry

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import micdoodle8.mods.galacticraft.core.items.ItemBucketGC
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.GCPlanetDimensions
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.IPlanetsModule
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockSulphuricAcid
import micdoodle8.mods.galacticraft.planets.venus.dimension.TeleportTypeVenus
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus
import micdoodle8.mods.galacticraft.planets.venus.entities.EntityEntryPodVenus
import micdoodle8.mods.galacticraft.planets.venus.entities.EntityJuicer
import micdoodle8.mods.galacticraft.planets.venus.entities.EntitySpiderQueen
import micdoodle8.mods.galacticraft.planets.venus.entities.EntityWebShot
import micdoodle8.mods.galacticraft.planets.venus.event.EventHandlerVenus
import micdoodle8.mods.galacticraft.planets.venus.inventory.ContainerCrashedProbe
import micdoodle8.mods.galacticraft.planets.venus.inventory.ContainerGeothermal
import micdoodle8.mods.galacticraft.planets.venus.inventory.ContainerSolarArrayController
import micdoodle8.mods.galacticraft.planets.venus.recipe.RecipeManagerVenus
import micdoodle8.mods.galacticraft.planets.venus.tick.VenusTickHandlerServer
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityCrashedProbe
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityDungeonSpawnerVenus
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityGeothermalGenerator
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayController
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayModule
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySpout
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityTreasureChestVenus
import micdoodle8.mods.galacticraft.planets.venus.world.gen.BiomeVenus
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialLiquid
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartedEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side

open class VenusModule : IPlanetsModule {
    override fun preInit(event: FMLPreInitializationEvent?) {
        planetVenus =
            Planet("venus").setParentSolarSystem(GalacticraftCore.solarSystemSol).setRingColorRGB(0.1f, 0.9f, 0.6f)
                .setPhaseShift(2.0f).setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(0.75f, 0.75f))
                .setRelativeOrbitTime(0.61527929901423877327491785323111f) as Planet
        MinecraftForge.EVENT_BUS.register(EventHandlerVenus())

        if (!FluidRegistry.isFluidRegistered("sulphuricacid")) {
            val stillIcon: ResourceLocation =
                ResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "blocks/fluids/sulphuric_acid_still")
            val flowingIcon: ResourceLocation =
                ResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "blocks/fluids/sulphuric_acid_flow")
            sulphuricAcidGC = Fluid("sulphuricacid", stillIcon, flowingIcon).setDensity(6229).setViscosity(1400)
            FluidRegistry.registerFluid(sulphuricAcidGC)
        } else {
            GCLog.info("Galacticraft sulphuric acid is not default, issues may occur.")
        }

        sulphuricAcid = FluidRegistry.getFluid("sulphuricacid")

        if (sulphuricAcid!!.getBlock() == null) {
            VenusBlocks.sulphuricAcid = BlockSulphuricAcid("sulphuric_acid")
            (VenusBlocks.sulphuricAcid as BlockSulphuricAcid).setQuantaPerBlock(5)
            VenusBlocks.registerBlock(VenusBlocks.sulphuricAcid, ItemBlockDesc::class.java)
            sulphuricAcid!!.setBlock(VenusBlocks.sulphuricAcid)
        } else {
            VenusBlocks.sulphuricAcid = sulphuricAcid!!.getBlock()
        }

        if (VenusBlocks.sulphuricAcid != null) {
            FluidRegistry.addBucketForFluid(sulphuricAcid) //Create a Universal Bucket AS WELL AS our type, this is needed to pull fluids out of other mods tanks
            VenusItems.bucketSulphuricAcid =
                ItemBucketGC((VenusBlocks.sulphuricAcid)!!, (sulphuricAcid)!!).setTranslationKey("bucket_sulphuric_acid")
            VenusItems.registerItem((VenusItems.bucketSulphuricAcid)!!)
            EventHandlerGC.bucketList.put(VenusBlocks.sulphuricAcid, VenusItems.bucketSulphuricAcid)
        }

        VenusBlocks.initBlocks()
        VenusItems.initItems()

        planetVenus!!.setBiomeInfo(BiomeVenus.venusFlat, BiomeVenus.venusMountain, BiomeVenus.venusValley)
    }

    override fun init(event: FMLInitializationEvent?) {
        VenusBlocks.oreDictRegistration()
        this.registerMicroBlocks()

        //        GalacticraftCore.packetPipeline.addDiscriminator(8, PacketSimpleVenus.class);
        this.registerTileEntities()
        this.registerCreatures()
        this.registerOtherEntities()

        planetVenus!!.setBodyIcon(ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/venus.png"))
        planetVenus!!.setDimensionInfo(ConfigManagerVenus.Companion.dimensionIDVenus, WorldProviderVenus::class.java)
            .setTierRequired(3)
        planetVenus!!.setAtmosphere(AtmosphereInfo(false, true, true, 5.0f, 0.3f, 54.0f))
        planetVenus!!.atmosphereComponent(EnumAtmosphericGas.CO2).atmosphereComponent(EnumAtmosphericGas.NITROGEN)
        planetVenus!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedZombie::class.java, 8, 2, 3))
        planetVenus!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSpider::class.java, 8, 2, 3))
        planetVenus!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSkeleton::class.java, 8, 2, 3))
        planetVenus!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedCreeper::class.java, 8, 2, 3))
        planetVenus!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedEnderman::class.java, 10, 1, 4))
        planetVenus!!.addChecklistKeys("equip_oxygen_suit", "equip_shield_controller", "thermal_padding_t2")

        GalaxyRegistry.registerPlanet((planetVenus)!!)
        GalacticraftRegistry.registerTeleportType(WorldProviderVenus::class.java, TeleportTypeVenus())
        GalacticraftRegistry.registerRocketGui(
            WorldProviderVenus::class.java,
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/venus_rocket_gui.png")
        )
        GalacticraftRegistry.addDungeonLoot(3, ItemStack(VenusItems.volcanicPickaxe, 1, 0))
        GalacticraftRegistry.addDungeonLoot(3, ItemStack(VenusItems.basicItem, 1, 0))

        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_SHIELD_CONTROLLER,
            EnumExtendedInventorySlot.SHIELD_CONTROLLER,
            ItemStack(VenusItems.basicItem, 1, 0)
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_THERMAL_PADDING_T2_HELMET,
            EnumExtendedInventorySlot.THERMAL_HELMET,
            ItemStack(VenusItems.thermalPaddingTier2, 1, 0)
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_THERMAL_PADDING_T2_CHESTPLATE,
            EnumExtendedInventorySlot.THERMAL_CHESTPLATE,
            ItemStack(VenusItems.thermalPaddingTier2, 1, 1)
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_THERMAL_PADDING_T2_LEGGINGS,
            EnumExtendedInventorySlot.THERMAL_LEGGINGS,
            ItemStack(VenusItems.thermalPaddingTier2, 1, 2)
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_THERMAL_PADDING_T2_BOOTS,
            EnumExtendedInventorySlot.THERMAL_BOOTS,
            ItemStack(VenusItems.thermalPaddingTier2, 1, 3)
        )

        (GalacticraftCore.proxy)!!.registerFluidTexture(
            sulphuricAcid,
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/misc/underacid.png")
        )

        MinecraftForge.EVENT_BUS.register(VenusTickHandlerServer())
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        RecipeManagerVenus.loadCompatibilityRecipes()

        GCPlanetDimensions.VENUS = WorldUtil.getDimensionTypeById(ConfigManagerVenus.Companion.dimensionIDVenus)
    }

    override fun serverStarting(event: FMLServerStartingEvent?) {
    }

    override fun serverInit(event: FMLServerStartedEvent?) {
    }

    private fun registerMicroBlocks() {
    }

    open fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntitySpout::class.java, "GC Venus Spout")
        GameRegistry.registerTileEntity(TileEntityDungeonSpawnerVenus::class.java, "GC Venus Dungeon Spawner")
        GameRegistry.registerTileEntity(TileEntityTreasureChestVenus::class.java, "GC Tier 3 Treasure Chest")
        GameRegistry.registerTileEntity(TileEntityGeothermalGenerator::class.java, "GC Geothermal Generator")
        GameRegistry.registerTileEntity(TileEntityCrashedProbe::class.java, "GC Crashed Probe")
        GameRegistry.registerTileEntity(TileEntitySolarArrayModule::class.java, "GC Solar Array Module")
        GameRegistry.registerTileEntity(TileEntitySolarArrayController::class.java, "GC Solar Array Controller")
    }

    open fun registerCreatures() {
        this.registerGalacticraftCreature(
            EntityJuicer::class.java,
            "juicer",
            ColorUtil.to32BitColor(180, 180, 50, 0),
            ColorUtil.to32BitColor(255, 0, 2, 0)
        )
        this.registerGalacticraftCreature(
            EntitySpiderQueen::class.java,
            "spider_queen",
            ColorUtil.to32BitColor(180, 180, 50, 0),
            ColorUtil.to32BitColor(255, 0, 2, 0)
        )
    }

    open fun registerOtherEntities() {
        registerGalacticraftNonMobEntity(EntityEntryPodVenus::class.java, "entry_pod_venus", 150, 1, true)
        registerGalacticraftNonMobEntity(EntityWebShot::class.java, "web_shot", 150, 1, true)
    }

    override fun getGuiIDs(idList: MutableList<Int>?) {
        (idList)!!.add(GuiIdsPlanets.MACHINE_VENUS)
    }

    override fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        val pos: BlockPos = BlockPos(x, y, z)
        val tile: TileEntity? = (world)!!.getTileEntity(pos)

        if (ID == GuiIdsPlanets.MACHINE_VENUS) {
            if (tile is TileEntityGeothermalGenerator) {
                return ContainerGeothermal((player)!!.inventory, tile)
            } else if (tile is TileEntityCrashedProbe) {
                return ContainerCrashedProbe((player)!!.inventory, tile)
            } else if (tile is TileEntitySolarArrayController) {
                return ContainerSolarArrayController((player)!!.inventory, tile)
            }
        }

        return null
    }

    override fun getConfiguration(): Configuration? {
        return ConfigManagerVenus.Companion.config
    }

    override fun syncConfig() {
        ConfigManagerVenus.Companion.syncConfig(false, false)
    }

    open fun registerGalacticraftCreature(clazz: Class<out Entity>?, name: String, back: Int, fore: Int) {
        registerGalacticraftNonMobEntity(clazz, name, 80, 3, true)
        val nextEggID: Int = GCCoreUtil.getNextValidID()
        if (nextEggID < 65536) {
            val resourcelocation: ResourceLocation = ResourceLocation(Constants.MOD_ID_PLANETS, name)
            //            name = Constants.MOD_ID_PLANETS + "." + name;
//            net.minecraftforge.fml.common.registry.EntityEntry entry = new net.minecraftforge.fml.common.registry.EntityEntry(clazz, name);
//            net.minecraftforge.fml.common.registry.GameData.getEntityRegistry().register(nextEggID, resourcelocation, entry);
            EntityList.ENTITY_EGGS.put(resourcelocation, EntityList.EntityEggInfo(resourcelocation, back, fore))
        }
    }

    companion object {
        @JvmField
        var planetVenus: Planet? = null

        @JvmField
        var sulphuricAcid: Fluid? = null

        @JvmField
        var sulphuricAcidGC: Fluid? = null

        @JvmField
        var acidMaterial: Material = MaterialLiquid(MapColor.EMERALD)

        @JvmStatic
        fun registerGalacticraftNonMobEntity(
            var0: Class<out Entity>?,
            var1: String,
            trackingDistance: Int,
            updateFreq: Int,
            sendVel: Boolean
        ) {
            val registryName: ResourceLocation = ResourceLocation(Constants.MOD_ID_PLANETS, var1)
            EntityRegistry.registerModEntity(
                registryName,
                var0,
                var1,
                GCCoreUtil.nextInternalID(),
                GalacticraftPlanets.instance,
                trackingDistance,
                updateFreq,
                sendVel
            )
        }
    }
}
