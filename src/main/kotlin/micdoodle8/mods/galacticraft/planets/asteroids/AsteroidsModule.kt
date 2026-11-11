package micdoodle8.mods.galacticraft.planets.asteroids

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.command.CommandGCAstroMiner
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.CreativeTabGC
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.GCPlanetDimensions
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.IPlanetsModule
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.TeleportTypeAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityEntryPod
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityGrapple
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntitySmallAsteroid
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityTier3Rocket
import micdoodle8.mods.galacticraft.planets.asteroids.entities.player.AsteroidsPlayerHandler
import micdoodle8.mods.galacticraft.planets.asteroids.event.AsteroidsEventHandler
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerAstroMinerDock
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerShortRangeTelepad
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.asteroids.network.PacketSimpleAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.recipe.CanisterRecipes
import micdoodle8.mods.galacticraft.planets.asteroids.recipe.RecipeManagerAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.schematic.SchematicAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.schematic.SchematicTier3Rocket
import micdoodle8.mods.galacticraft.planets.asteroids.tick.AsteroidsTickHandlerServer
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamReceiver
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamReflector
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBaseSingle
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityTelepadFake
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.BiomeAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.ChunkProviderAsteroids
import micdoodle8.mods.galacticraft.planets.mars.MarsModule
import net.minecraft.world.level.block.Block
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.MinecraftForge
import foundation.config.FoundationConfiguration as Configuration
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.event.server.ServerStartedEvent as FMLServerStartedEvent
import net.minecraftforge.event.server.ServerStartingEvent as FMLServerStartingEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.oredict.RecipeSorter
import java.lang.Exception
import java.lang.reflect.Method

open class AsteroidsModule : IPlanetsModule {
    //public static Fluid fluidCO2Gas;
    private fun registerFluid(
        fluidName: String,
        density: Int,
        viscosity: Int,
        temperature: Int,
        gaseous: Boolean
    ): Fluid? {
        var returnFluid: Fluid? = FluidRegistry.getFluid(fluidName)
        if (returnFluid == null) {
            val texture: ResourceLocation =
                ResourceLocation.parse(GalacticraftPlanets.Companion.TEXTURE_PREFIX + "blocks/fluids/" + fluidName)
            FluidRegistry.registerFluid(
                Fluid(fluidName, texture, texture).setDensity(density).setViscosity(viscosity)
                    .setTemperature(temperature).setGaseous(gaseous)
            )
            returnFluid = FluidRegistry.getFluid(fluidName)
        }
        return returnFluid
    }

    override fun preInit() {
        planetAsteroids = Planet("asteroids").setParentSolarSystem(GalacticraftCore.solarSystemSol)

        playerHandler = AsteroidsPlayerHandler()
        MinecraftForge.EVENT_BUS.register(playerHandler)
        val eventHandler: AsteroidsEventHandler = AsteroidsEventHandler()
        MinecraftForge.EVENT_BUS.register(eventHandler)
        RecipeSorter.register(
            "galacticraftplanets:canisterRecipe",
            CanisterRecipes::class.java,
            RecipeSorter.Category.SHAPELESS,
            "after:minecraft:shapeless"
        )

        registerFluid("methane", 1, 11, 295, true)
        registerFluid("atmosphericgases", 1, 13, 295, true)
        registerFluid("liquidmethane", 450, 120, 109, false)
        //Data source for liquid methane: http://science.nasa.gov/science-news/science-at-nasa/2005/25feb_titan2/
        registerFluid("liquidoxygen", 1141, 140, 90, false)
        registerFluid("liquidnitrogen", 808, 130, 90, false)
        registerFluid("nitrogen", 1, 12, 295, true)
        registerFluid("carbondioxide", 2, 20, 295, true)
        registerFluid("argon", 1, 4, 295, true)
        registerFluid("liquidargon", 900, 100, 87, false)
        registerFluid("helium", 1, 1, 295, true)
        fluidMethaneGas = FluidRegistry.getFluid("methane")
        fluidAtmosphericGases = FluidRegistry.getFluid("atmosphericgases")
        fluidLiquidMethane = FluidRegistry.getFluid("liquidmethane")
        fluidLiquidOxygen = FluidRegistry.getFluid("liquidoxygen")
        fluidOxygenGas = FluidRegistry.getFluid("oxygen")
        fluidLiquidNitrogen = FluidRegistry.getFluid("liquidnitrogen")
        fluidLiquidArgon = FluidRegistry.getFluid("liquidargon")
        fluidNitrogenGas = FluidRegistry.getFluid("nitrogen")

        //AsteroidsModule.fluidCO2Gas = FluidRegistry.getFluid("carbondioxide");
        AsteroidBlocks.initBlocks()
        AsteroidBlocks.registerBlocks()
        AsteroidBlocks.setHarvestLevels()

        AsteroidsItems.initItems()

        planetAsteroids!!.setBiomeInfo(BiomeAsteroids.asteroid)
        //This enables Endermen on Asteroids in Asteroids Challenge mode
        (BiomeAsteroids.asteroid as BiomeAsteroids).resetMonsterListByMode(ConfigManagerCore.challengeMobDropsAndSpawning)

        //TODO: could also increase mob spawn frequency in Hard Mode on various dimensions e.g. Mars and Venus?

//        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(AsteroidsModule.fluidMethaneGas, 1000), new ItemStack(AsteroidsItems.methaneCanister, 1, 1), new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
//        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(AsteroidsModule.fluidLiquidOxygen, 1000), new ItemStack(AsteroidsItems.canisterLOX, 1, 1), new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
//        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(AsteroidsModule.fluidLiquidNitrogen, 1000), new ItemStack(AsteroidsItems.canisterLN2, 1, 1), new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
    }

    override fun init(event: FMLInitializationEvent?) {
        AsteroidBlocks.oreDictRegistration()
        AsteroidsItems.oreDictRegistrations()

        (GalacticraftCore.galacticraftItemsTab as CreativeTabGC).setItemForTab(ItemStack(AsteroidsItems.astroMiner)) // Set creative tab item to Astro Miner

        this.registerMicroBlocks()
        SchematicRegistry.registerSchematicRecipe(SchematicTier3Rocket())
        SchematicRegistry.registerSchematicRecipe(SchematicAstroMiner())

        (GalacticraftCore.packetPipeline)!!.addDiscriminator(7, PacketSimpleAsteroids::class.java)

        val eventHandler: AsteroidsTickHandlerServer = AsteroidsTickHandlerServer()
        MinecraftForge.EVENT_BUS.register(eventHandler)

        this.registerEntities()

        RecipeManagerAsteroids.loadCompatibilityRecipes()

        planetAsteroids!!.setDimensionInfo(
            ConfigManagerAsteroids.Companion.dimensionIDAsteroids,
            WorldProviderAsteroids::class.java
        ).setTierRequired(3)
        planetAsteroids!!.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(1.375f, 1.375f))
            .setRelativeOrbitTime(45.0f).setPhaseShift((Math.random() * (2 * Math.PI)).toFloat())
        planetAsteroids!!.setBodyIcon(
            ResourceLocation(
                Constants.ASSET_PREFIX,
                "textures/gui/celestialbodies/asteroid.png"
            )
        )
        planetAsteroids!!.setAtmosphere(AtmosphereInfo(false, false, false, -1.5f, 0.05f, 0.0f))
        planetAsteroids!!.addChecklistKeys("equip_oxygen_suit", "craft_grapple_hook", "thermal_padding")

        GalaxyRegistry.registerPlanet((planetAsteroids)!!)
        GalacticraftRegistry.registerTeleportType(WorldProviderAsteroids::class.java, TeleportTypeAsteroids())

        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_THERMAL_PADDING_T1_HELMET,
            EnumExtendedInventorySlot.THERMAL_HELMET,
            ItemStack(AsteroidsItems.thermalPadding, 1, 0)
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_THERMAL_PADDING_T1_CHESTPLATE,
            EnumExtendedInventorySlot.THERMAL_CHESTPLATE,
            ItemStack(AsteroidsItems.thermalPadding, 1, 1)
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_THERMAL_PADDING_T1_LEGGINGS,
            EnumExtendedInventorySlot.THERMAL_LEGGINGS,
            ItemStack(AsteroidsItems.thermalPadding, 1, 2)
        )
        GalacticraftRegistry.registerGear(
            Constants.GEAR_ID_THERMAL_PADDING_T1_BOOTS,
            EnumExtendedInventorySlot.THERMAL_BOOTS,
            ItemStack(AsteroidsItems.thermalPadding, 1, 3)
        )
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        GCPlanetDimensions.ASTEROIDS =
            WorldUtil.getDimensionTypeById(ConfigManagerAsteroids.Companion.dimensionIDAsteroids)
    }

    override fun serverStarting(event: FMLServerStartingEvent?) {
        (event)!!.registerServerCommand(CommandGCAstroMiner())
        ChunkProviderAsteroids.reset()
    }

    override fun serverInit(event: FMLServerStartedEvent?) {
        AsteroidsTickHandlerServer.restart()
    }

    override fun getGuiIDs(idList: MutableList<Int>?) {
        (idList)!!.add(GuiIdsPlanets.MACHINE_ASTEROIDS)
    }

    override fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        if (side == Side.SERVER) {
            val tile: TileEntity? = (world)!!.getBlockEntity(BlockPos(x, y, z))

            when (ID) {
                GuiIdsPlanets.MACHINE_ASTEROIDS -> {
                    if (tile is TileEntityShortRangeTelepad) {
                        return ContainerShortRangeTelepad((player)!!.inventory, tile, (player)!!)
                    }
                    if (tile is TileEntityMinerBase) {
                        return ContainerAstroMinerDock((player)!!.inventory, tile)
                    }
                }
            }
        }

        return null
    }

    private fun registerEntities() {
        this.registerCreatures()
        this.registerNonMobEntities()
        this.registerTileEntities()
    }

    private fun registerCreatures() {
    }

    private fun registerNonMobEntities() {
        MarsModule.registerGalacticraftNonMobEntity(EntitySmallAsteroid::class.java, "small_asteroid", 150, 3, true)
        MarsModule.registerGalacticraftNonMobEntity(EntityGrapple::class.java, "grapple_hook", 150, 1, true)
        MarsModule.registerGalacticraftNonMobEntity(EntityTier3Rocket::class.java, "rocket_t3", 150, 1, false)
        MarsModule.registerGalacticraftNonMobEntity(EntityEntryPod::class.java, "entry_pod", 150, 1, true)
        MarsModule.registerGalacticraftNonMobEntity(EntityAstroMiner::class.java, "astro_miner", 80, 1, true)
    }

    private fun registerMicroBlocks() {
        try {
            val clazz: Class<*>? = Class.forName("codechicken.microblock.MicroMaterialRegistry")
            if (clazz != null) {
                var registerMethod: Method? = null
                val methodz: Array<Method> = clazz.getMethods()
                for (m: Method in methodz) {
                    if (m.getName() == "registerMaterial") {
                        registerMethod = m
                        break
                    }
                }
                val clazzbm: Class<*> = Class.forName("codechicken.microblock.BlockMicroMaterial")
                registerMethod!!.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(AsteroidBlocks.blockBasic, 0),
                    "tile.asteroids_block.asteroid_rock_0"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(AsteroidBlocks.blockBasic, 1),
                    "tile.asteroids_block.asteroid_rock_1"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(AsteroidBlocks.blockBasic, 2),
                    "tile.asteroids_block.asteroid_rock_2"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(AsteroidBlocks.blockDenseIce, 0),
                    "tile.dense_ice"
                )
            }
        } catch (e: Exception) {
        }
    }

    private fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityBeamReflector::class.java, "GC Beam Reflector")
        GameRegistry.registerTileEntity(TileEntityBeamReceiver::class.java, "GC Beam Receiver")
        GameRegistry.registerTileEntity(TileEntityShortRangeTelepad::class.java, "GC Short Range Telepad")
        GameRegistry.registerTileEntity(TileEntityTelepadFake::class.java, "GC Fake Short Range Telepad")
        GameRegistry.registerTileEntity(TileEntityMinerBaseSingle::class.java, "GC Astro Miner Base Builder")
        GameRegistry.registerTileEntity(TileEntityMinerBase::class.java, "GC Astro Miner Base")
    }

    override fun getConfiguration(): Configuration? {
        return ConfigManagerAsteroids.Companion.config
    }

    override fun syncConfig() {
        ConfigManagerAsteroids.Companion.syncConfig(false, false)
    }

    companion object {
        @JvmField
        var planetAsteroids: Planet? = null

        @JvmField
        var playerHandler: AsteroidsPlayerHandler? = null

        @JvmField
        var fluidMethaneGas: Fluid? = null

        @JvmField
        var fluidOxygenGas: Fluid? = null

        @JvmField
        var fluidNitrogenGas: Fluid? = null

        @JvmField
        var fluidLiquidMethane: Fluid? = null

        @JvmField
        var fluidLiquidOxygen: Fluid? = null

        @JvmField
        var fluidLiquidNitrogen: Fluid? = null

        @JvmField
        var fluidLiquidArgon: Fluid? = null

        @JvmField
        var fluidAtmosphericGases: Fluid? = null
    }
}
