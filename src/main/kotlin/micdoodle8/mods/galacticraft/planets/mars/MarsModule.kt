package micdoodle8.mods.galacticraft.planets.mars

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
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
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockSludge
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.dimension.TeleportTypeMars
import micdoodle8.mods.galacticraft.planets.mars.dimension.WorldProviderMars
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCreeperBoss
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityProjectileTNT
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySludgeling
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityTier2Rocket
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerElectrolyzer
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerGasLiquefier
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerLaunchController
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerMethaneSynthesizer
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerTerraformer
import micdoodle8.mods.galacticraft.planets.mars.items.ItemSchematicTier2
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import micdoodle8.mods.galacticraft.planets.mars.recipe.RecipeManagerMars
import micdoodle8.mods.galacticraft.planets.mars.schematic.SchematicCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.schematic.SchematicTier2Rocket
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityCryogenicChamber
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityDungeonSpawnerMars
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityElectrolyzer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityGasLiquefier
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityMethaneSynthesizer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntitySlimelingEgg
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityTerraformer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityTreasureChestMars
import micdoodle8.mods.galacticraft.planets.mars.world.gen.BiomeMars
import net.minecraft.block.Block
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
import java.lang.Exception
import java.lang.reflect.Method

open class MarsModule : IPlanetsModule {
    override fun preInit(event: FMLPreInitializationEvent?) {
        planetMars =
            Planet("mars").setParentSolarSystem(GalacticraftCore.solarSystemSol).setRingColorRGB(0.67f, 0.1f, 0.1f)
                .setPhaseShift(0.1667f).setRelativeSize(0.5319f)
                .setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(1.25f, 1.25f))
                .setRelativeOrbitTime(1.8811610076670317634173055859803f) as Planet

        MinecraftForge.EVENT_BUS.register(EventHandlerMars())

        if (!FluidRegistry.isFluidRegistered("bacterialsludge")) {
            val stillIcon: ResourceLocation =
                ResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "blocks/fluids/sludge_still")
            val flowingIcon: ResourceLocation =
                ResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "blocks/fluids/sludge_flow")
            sludgeGC = Fluid("bacterialsludge", stillIcon, flowingIcon).setDensity(800).setViscosity(1500)
            FluidRegistry.registerFluid(sludgeGC)
        } else {
            GCLog.info("Galacticraft sludge is not default, issues may occur.")
        }

        sludge = FluidRegistry.getFluid("bacterialsludge")

        if (sludge!!.getBlock() == null) {
            MarsBlocks.blockSludge = BlockSludge("sludge")
            (MarsBlocks.blockSludge as BlockSludge).setQuantaPerBlock(3)
            MarsBlocks.registerBlock(MarsBlocks.blockSludge, ItemBlockDesc::class.java)
            sludge!!.setBlock(MarsBlocks.blockSludge)
        } else {
            MarsBlocks.blockSludge = sludge!!.getBlock()
        }

        if (MarsBlocks.blockSludge != null) {
            FluidRegistry.addBucketForFluid(sludge) //Create a Universal Bucket AS WELL AS our type, this is needed to pull fluids out of other mods tanks
            MarsItems.bucketSludge = ItemBucketGC((MarsBlocks.blockSludge)!!, (sludge)!!).setTranslationKey("bucket_sludge")
            MarsItems.registerItem((MarsItems.bucketSludge)!!)
            EventHandlerGC.bucketList.put(MarsBlocks.blockSludge, MarsItems.bucketSludge)
        }

        MarsBlocks.initBlocks()
        MarsItems.initItems()

        planetMars!!.setBiomeInfo(BiomeMars.marsFlat)
    }

    override fun init(event: FMLInitializationEvent?) {
        MarsBlocks.oreDictRegistration()
        this.registerMicroBlocks()
        SchematicRegistry.registerSchematicRecipe(SchematicTier2Rocket())
        SchematicRegistry.registerSchematicRecipe(SchematicCargoRocket())

        (GalacticraftCore.packetPipeline)!!.addDiscriminator(6, PacketSimpleMars::class.java)

        this.registerTileEntities()
        this.registerCreatures()
        this.registerOtherEntities()

        planetMars!!.setBodyIcon(ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/mars.png"))
        planetMars!!.setDimensionInfo(ConfigManagerMars.Companion.dimensionIDMars, WorldProviderMars::class.java)
            .setTierRequired(2)
        planetMars!!.setAtmosphere(AtmosphereInfo(false, false, false, -1.0f, 0.3f, 0.1f))
        planetMars!!.atmosphereComponent(EnumAtmosphericGas.CO2).atmosphereComponent(EnumAtmosphericGas.ARGON)
            .atmosphereComponent(EnumAtmosphericGas.NITROGEN)
        planetMars!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedZombie::class.java, 8, 2, 3))
        planetMars!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSpider::class.java, 8, 2, 3))
        planetMars!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSkeleton::class.java, 8, 2, 3))
        planetMars!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedCreeper::class.java, 8, 2, 3))
        planetMars!!.addMobInfo(Biome.SpawnListEntry(EntityEvolvedEnderman::class.java, 10, 1, 4))
        planetMars!!.addChecklistKeys("equip_oxygen_suit", "thermal_padding")

        GalaxyRegistry.registerPlanet((planetMars)!!)
        GalacticraftRegistry.registerTeleportType(WorldProviderMars::class.java, TeleportTypeMars())
        GalacticraftRegistry.registerRocketGui(
            WorldProviderMars::class.java,
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/mars_rocket_gui.png")
        )
        GalacticraftRegistry.addDungeonLoot(2, ItemStack(MarsItems.schematic, 1, 0))
        GalacticraftRegistry.addDungeonLoot(2, ItemStack(MarsItems.schematic, 1, 1))
        GalacticraftRegistry.addDungeonLoot(2, ItemStack(MarsItems.schematic, 1, 2))

        (GalacticraftCore.proxy)!!.registerFluidTexture(
            sludge,
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/misc/underbecterial.png")
        )
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        RecipeManagerMars.loadCompatibilityRecipes()
        GCPlanetDimensions.MARS = WorldUtil.getDimensionTypeById(ConfigManagerMars.Companion.dimensionIDMars)
        ItemSchematicTier2.registerSchematicItems()
    }

    override fun serverStarting(event: FMLServerStartingEvent?) {
    }

    override fun serverInit(event: FMLServerStartedEvent?) {
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
                        .newInstance(MarsBlocks.marsBlock, 4),
                    "tile.mars.marscobblestone"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(MarsBlocks.marsBlock, 5),
                    "tile.mars.marsgrass"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(MarsBlocks.marsBlock, 6),
                    "tile.mars.marsdirt"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(MarsBlocks.marsBlock, 7),
                    "tile.mars.marsdungeon"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(MarsBlocks.marsBlock, 8),
                    "tile.mars.marsdeco"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(MarsBlocks.marsBlock, 9),
                    "tile.mars.marsstone"
                )
            }
        } catch (e: Exception) {
        }
    }

    open fun registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntitySlimelingEgg::class.java, "GC Slimeling Egg")
        GameRegistry.registerTileEntity(TileEntityTreasureChestMars::class.java, "GC Tier 2 Treasure Chest")
        GameRegistry.registerTileEntity(TileEntityTerraformer::class.java, "GC Planet Terraformer")
        GameRegistry.registerTileEntity(TileEntityCryogenicChamber::class.java, "GC Cryogenic Chamber")
        GameRegistry.registerTileEntity(TileEntityGasLiquefier::class.java, "GC Gas Liquefier")
        GameRegistry.registerTileEntity(TileEntityMethaneSynthesizer::class.java, "GC Methane Synthesizer")
        GameRegistry.registerTileEntity(TileEntityElectrolyzer::class.java, "GC Water Electrolyzer")
        GameRegistry.registerTileEntity(TileEntityDungeonSpawnerMars::class.java, "GC Mars Dungeon Spawner")
        GameRegistry.registerTileEntity(TileEntityLaunchController::class.java, "GC Launch Controller")
    }

    open fun registerCreatures() {
        this.registerGalacticraftCreature(
            EntitySludgeling::class.java,
            "sludgeling",
            ColorUtil.to32BitColor(255, 0, 50, 0),
            ColorUtil.to32BitColor(255, 0, 150, 0)
        )
        this.registerGalacticraftCreature(
            EntitySlimeling::class.java,
            "slimeling",
            ColorUtil.to32BitColor(255, 0, 50, 0),
            ColorUtil.to32BitColor(255, 0, 150, 0)
        )
        this.registerGalacticraftCreature(
            EntityCreeperBoss::class.java,
            "creeper_boss",
            ColorUtil.to32BitColor(255, 0, 50, 0),
            ColorUtil.to32BitColor(255, 0, 150, 0)
        )
    }

    open fun registerOtherEntities() {
        registerGalacticraftNonMobEntity(EntityTier2Rocket::class.java, "rocket_t2", 150, 1, false)
        registerGalacticraftNonMobEntity(EntityProjectileTNT::class.java, "projectile_tnt", 150, 1, true)
        registerGalacticraftNonMobEntity(EntityLandingBalloons::class.java, "landing_balloons", 150, 5, true)
        registerGalacticraftNonMobEntity(EntityCargoRocket::class.java, "rocket_cargo", 150, 1, false)
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

    override fun getGuiIDs(idList: MutableList<Int>?) {
        (idList)!!.add(GuiIdsPlanets.MACHINE_MARS)
    }

    override fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        if (side == Side.SERVER) {
            val pos: BlockPos = BlockPos(x, y, z)
            val tile: TileEntity? = (world)!!.getTileEntity(pos)

            if (ID == GuiIdsPlanets.MACHINE_MARS) {
                if (tile is TileEntityTerraformer) {
                    return ContainerTerraformer((player)!!.inventory, tile, (player)!!)
                } else if (tile is TileEntityLaunchController) {
                    return ContainerLaunchController((player)!!.inventory, tile, (player)!!)
                } else if (tile is TileEntityElectrolyzer) {
                    return ContainerElectrolyzer((player)!!.inventory, tile, (player)!!)
                } else if (tile is TileEntityGasLiquefier) {
                    return ContainerGasLiquefier((player)!!.inventory, tile, (player)!!)
                } else if (tile is TileEntityMethaneSynthesizer) {
                    return ContainerMethaneSynthesizer((player)!!.inventory, tile, (player)!!)
                }
            }
        }

        return null
    }

    override fun getConfiguration(): Configuration? {
        return ConfigManagerMars.Companion.config
    }

    override fun syncConfig() {
        ConfigManagerMars.Companion.syncConfig(false, false)
    }

    companion object {
        @JvmField
        var sludge: Fluid? = null

        @JvmField
        var sludgeGC: Fluid? = null

        @JvmField
        var sludgeMaterial: Material = MaterialLiquid(MapColor.FOLIAGE)

        @JvmField
        var planetMars: Planet? = null

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
