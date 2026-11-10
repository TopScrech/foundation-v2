package micdoodle8.mods.galacticraft.planets.asteroids

import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.IPlanetsModuleClient
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.client.FluidTexturesGC
import micdoodle8.mods.galacticraft.planets.asteroids.client.fx.EntityFXTeleport
import micdoodle8.mods.galacticraft.planets.asteroids.client.gui.GuiAstroMinerDock
import micdoodle8.mods.galacticraft.planets.asteroids.client.gui.GuiShortRangeTelepad
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity.RenderAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity.RenderEntryPod
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity.RenderGrapple
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity.RenderSmallAsteroid
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity.RenderTier3Rocket
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelBeamReceiver
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelBeamReflector
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelGrapple
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelRocketT3
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelTelepad
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile.TileEntityBeamReceiverRenderer
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile.TileEntityBeamReflectorRenderer
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile.TileEntityMinerBaseRenderer
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile.TileEntityShortRangeTelepadRenderer
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityEntryPod
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityGrapple
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntitySmallAsteroid
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityTier3Rocket
import micdoodle8.mods.galacticraft.planets.asteroids.event.AsteroidsEventHandlerClient
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamReceiver
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamReflector
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad
import micdoodle8.mods.galacticraft.planets.mars.client.fx.EntityCryoFX
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class AsteroidsModuleClient : IPlanetsModuleClient {
    override fun preInit(event: FMLPreInitializationEvent?) {
        RenderingRegistry.registerEntityRenderingHandler<EntitySmallAsteroid>(
            EntitySmallAsteroid::class.java,
            IRenderFactory { manager: RenderManager? -> RenderSmallAsteroid((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityGrapple>(
            EntityGrapple::class.java,
            IRenderFactory { manager: RenderManager? -> RenderGrapple((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityEntryPod>(
            EntityEntryPod::class.java,
            IRenderFactory { manager: RenderManager? -> RenderEntryPod((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityTier3Rocket>(
            EntityTier3Rocket::class.java,
            IRenderFactory { manager: RenderManager? -> RenderTier3Rocket((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityAstroMiner>(
            EntityAstroMiner::class.java,
            IRenderFactory { manager: RenderManager? -> RenderAstroMiner((manager)!!) })
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun registerVariants() {
        addPlanetVariants(
            "asteroids_block",
            "asteroids_block",
            "asteroid_rock_1",
            "asteroid_rock_2",
            "ore_aluminum_asteroids",
            "ore_ilmenite_asteroids",
            "ore_iron_asteroids",
            "asteroid_deco",
            "titanium_block"
        )
        addPlanetVariants(
            "thermal_padding",
            "thermal_padding",
            "thermal_chestplate",
            "thermal_leggings",
            "thermal_boots"
        )
        addPlanetVariants(
            "item_basic_asteroids",
            "item_basic_asteroids",
            "engine_t2",
            "rocket_fins_t2",
            "shard_iron",
            "shard_titanium",
            "ingot_titanium",
            "compressed_titanium",
            "thermal_cloth",
            "beam_core",
            "dust_titanium"
        )
        addPlanetVariants("walkway", "walkway", "walkway_wire", "walkway_pipe")
        addPlanetVariants("strange_seed", "strange_seed", "strange_seed1")

        val receiver: Item = Item.getItemFromBlock(AsteroidBlocks.beamReceiver)
        var modelResourceLocation: ModelResourceLocation =
            ModelResourceLocation("galacticraftplanets:beam_receiver", "inventory")
        ModelLoader.setCustomModelResourceLocation(receiver, 0, modelResourceLocation)

        val reflector: Item = Item.getItemFromBlock(AsteroidBlocks.beamReflector)
        modelResourceLocation = ModelResourceLocation("galacticraftplanets:beam_reflector", "inventory")
        ModelLoader.setCustomModelResourceLocation(reflector, 0, modelResourceLocation)

        val teleporter: Item = Item.getItemFromBlock(AsteroidBlocks.shortRangeTelepad)
        modelResourceLocation = ModelResourceLocation("galacticraftplanets:telepad_short", "inventory")
        ModelLoader.setCustomModelResourceLocation(teleporter, 0, modelResourceLocation)

        modelResourceLocation = ModelResourceLocation("galacticraftplanets:grapple", "inventory")
        ModelLoader.setCustomModelResourceLocation(AsteroidsItems.grapple, 0, modelResourceLocation)

        modelResourceLocation = ModelResourceLocation("galacticraftplanets:rocket_t3", "inventory")
        for (i: Int in 0..4) {
            ModelLoader.setCustomModelResourceLocation(AsteroidsItems.tier3Rocket, i, modelResourceLocation)
        }

        modelResourceLocation = ModelResourceLocation("galacticraftplanets:astro_miner", "inventory")
        ModelLoader.setCustomModelResourceLocation(AsteroidsItems.astroMiner, 0, modelResourceLocation)
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun onModelBakeEvent(event: ModelBakeEvent?) {
        replaceModelDefault(
            event,
            "beam_receiver",
            "block/receiver.obj",
            ImmutableList.of<String?>("Main", "Receiver", "Ring"),
            ItemModelBeamReceiver::class.java,
            TRSRTransformation.identity(),
            "inventory",
            "facing=up",
            "facing=down",
            "facing=north",
            "facing=west",
            "facing=east",
            "facing=south"
        )
        replaceModelDefault(
            event,
            "beam_reflector",
            "block/reflector.obj",
            ImmutableList.of<String?>("Base", "Axle", "EnergyBlaster", "Ring"),
            ItemModelBeamReflector::class.java,
            TRSRTransformation.identity(),
            "inventory",
            "normal"
        )
        replaceModelDefault(
            event,
            "telepad_short",
            "block/telepad_short.obj",
            ImmutableList.of<String?>("Top", "Bottom", "Connector"),
            ItemModelTelepad::class.java,
            TRSRTransformation.identity(),
            "inventory",
            "normal"
        )
        replaceModelDefault(
            event,
            "grapple",
            "grapple.obj",
            ImmutableList.of<String?>("Grapple"),
            ItemModelGrapple::class.java,
            TRSRTransformation.identity()
        )
        replaceModelDefault(
            event,
            "rocket_t3",
            "tier3rocket.obj",
            ImmutableList.of<String?>("Boosters", "Cube", "NoseCone", "Rocket"),
            ItemModelRocketT3::class.java,
            TRSRTransformation.identity()
        )
        replaceModelDefault(
            event,
            "astro_miner",
            "astro_miner_inv.obj",
            ImmutableList.of<String?>("Hull_Center"),
            ItemModelAstroMiner::class.java,
            TRSRTransformation.identity()
        )
    }

    private fun replaceModelDefault(
        event: ModelBakeEvent?,
        resLoc: String,
        objLoc: String,
        visibleGroups: MutableList<String?>?,
        clazz: Class<out ModelTransformWrapper>,
        parentState: IModelState?,
        vararg variants: String?
    ) {
        ClientUtil.replaceModel(
            GalacticraftPlanets.Companion.ASSET_PREFIX,
            (event)!!,
            resLoc,
            objLoc,
            (visibleGroups)!!,
            clazz,
            (parentState)!!,
            *(variants as Array<out String>)
        )
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun loadTextures(event: TextureStitchEvent.Pre) {
        registerTexture(event, "minerbase")
        registerTexture(event, "beam_reflector")
        registerTexture(event, "beam_receiver")
        registerTexture(event, "telepad_short")
        registerTexture(event, "telepad_short0")
        registerTexture(event, "grapple")
        registerTexture(event, "tier3rocket")
        registerTexture(event, "astro_miner")
        registerTexture(event, "astro_miner_off")
        registerTexture(event, "astro_miner_fx")
        registerTexture(event, "space_pod")
        registerTexture(event, "fluids/argon")
        registerTexture(event, "fluids/atmosphericgases")
        registerTexture(event, "fluids/carbondioxide")
        registerTexture(event, "fluids/helium")
        registerTexture(event, "fluids/liquidargon")
        registerTexture(event, "fluids/liquidmethane")
        registerTexture(event, "fluids/liquidnitrogen")
        registerTexture(event, "fluids/liquidoxygen")
        registerTexture(event, "fluids/methane")
        registerTexture(event, "fluids/nitrogen")
    }

    private fun registerTexture(event: TextureStitchEvent.Pre, texture: String) {
        event.getMap()
            .registerSprite(ResourceLocation(GalacticraftPlanets.Companion.TEXTURE_PREFIX + "blocks/" + texture))
    }

    override fun init(event: FMLInitializationEvent?) {
        val clientEventHandler: AsteroidsEventHandlerClient = AsteroidsEventHandlerClient()
        MinecraftForge.EVENT_BUS.register(clientEventHandler)
        FluidTexturesGC.init()
        registerBlockRenderers()
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
//          RenderingRegistry.registerEntityRenderingHandler(EntityAstroMiner.class, (RenderManager manager) -> new RenderAstroMiner());
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityBeamReflector>(
            TileEntityBeamReflector::class.java,
            TileEntityBeamReflectorRenderer()
        )
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityBeamReceiver>(
            TileEntityBeamReceiver::class.java,
            TileEntityBeamReceiverRenderer()
        )
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityMinerBase>(
            TileEntityMinerBase::class.java,
            TileEntityMinerBaseRenderer()
        )
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityShortRangeTelepad>(
            TileEntityShortRangeTelepad::class.java,
            TileEntityShortRangeTelepadRenderer()
        )

        //        if (Loader.isModLoaded("craftguide"))
//        {
//            CraftGuideIntegration.register();
//        }
    }

    private fun addPlanetVariants(name: String, vararg variants: String?) {
        val itemBlockVariants: Item? = Item.REGISTRY.getObject(ResourceLocation(Constants.MOD_ID_PLANETS, name))
        val variants0: Array<ResourceLocation?> = arrayOfNulls<ResourceLocation>(variants.size)
        for (i: Int in variants.indices) {
            variants0[i] = ResourceLocation(GalacticraftPlanets.Companion.TEXTURE_PREFIX + variants[i])
        }
        ModelBakery.registerItemVariants(itemBlockVariants, *variants0)
    }

    override fun getGuiIDs(idList: MutableList<Int>?) {
        (idList)!!.add(GuiIdsPlanets.MACHINE_ASTEROIDS)
    }

    override fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        val tile: TileEntity? = (world)!!.getTileEntity(BlockPos(x, y, z))

        when (ID) {
            GuiIdsPlanets.MACHINE_ASTEROIDS -> {
                if (tile is TileEntityShortRangeTelepad) {
                    return GuiShortRangeTelepad((player)!!.inventory, tile)
                }
                if (tile is TileEntityMinerBase) {
                    return GuiAstroMinerDock((player)!!.inventory, tile)
                }
            }
        }

        return null
    }

    override fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, vararg extraData: Any?) {
        val mc: Minecraft? = FMLClientHandler.instance().getClient()

        if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
            val dX: Double = mc.getRenderViewEntity()!!.posX - (position)!!.x
            val dY: Double = mc.getRenderViewEntity()!!.posY - (position)!!.y
            val dZ: Double = mc.getRenderViewEntity()!!.posZ - (position)!!.z
            var particle: Particle? = null
            val viewDistance: Double = 64.0

            if (dX * dX + dY * dY + dZ * dZ < viewDistance * viewDistance) {
                if (particleID == "portalBlue") {
                    particle = EntityFXTeleport(
                        mc.world,
                        (position)!!,
                        (motion)!!,
                        extraData[0] as TileEntityShortRangeTelepad?,
                        (extraData[1] as kotlin.Boolean?)!!
                    )
                } else if (particleID == "cryoFreeze") {
                    particle = EntityCryoFX(mc.world, (position)!!, (motion)!!)
                }
            }

            if (particle != null) {
                mc.effectRenderer.addEffect(particle)
            }
        }
    }

    companion object {
        @JvmStatic
        fun registerBlockRenderers() {
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockBasic)!!,
                0,
                "asteroids_block"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockBasic)!!,
                1,
                "asteroid_rock_1"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockBasic)!!,
                2,
                "asteroid_rock_2"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockBasic)!!,
                3,
                "ore_aluminum_asteroids"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockBasic)!!,
                4,
                "ore_ilmenite_asteroids"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockBasic)!!,
                5,
                "ore_iron_asteroids"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockBasic)!!,
                6,
                "asteroid_deco"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockBasic)!!,
                7,
                "titanium_block"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockWalkway)!!,
                0,
                "walkway"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockWalkway)!!,
                1,
                "walkway_wire"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidBlocks.blockWalkway)!!,
                2,
                "walkway_pipe"
            )
            ClientUtil.registerBlockJson(GalacticraftPlanets.Companion.TEXTURE_PREFIX, (AsteroidBlocks.blockDenseIce)!!)
            ClientUtil.registerBlockJson(GalacticraftPlanets.Companion.TEXTURE_PREFIX, (AsteroidBlocks.blockMinerBase)!!)
            ClientUtil.registerBlockJson(GalacticraftPlanets.Companion.TEXTURE_PREFIX, (AsteroidBlocks.minerBaseFull)!!)
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.thermalPadding)!!,
                0,
                "thermal_padding"
            ) //helm
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.thermalPadding)!!,
                1,
                "thermal_chestplate"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.thermalPadding)!!,
                2,
                "thermal_leggings"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.thermalPadding)!!,
                3,
                "thermal_boots"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                5,
                "item_basic_asteroids"
            ) //reinforced_plate_t3
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                1,
                "engine_t2"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                2,
                "rocket_fins_t2"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                3,
                "shard_iron"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                4,
                "shard_titanium"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                0,
                "ingot_titanium"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                6,
                "compressed_titanium"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                7,
                "thermal_cloth"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                8,
                "beam_core"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.basicItem)!!,
                9,
                "dust_titanium"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.heavyNoseCone)!!,
                0,
                "heavy_nose_cone"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.strangeSeed)!!,
                0,
                "strange_seed"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.Companion.TEXTURE_PREFIX,
                (AsteroidsItems.strangeSeed)!!,
                1,
                "strange_seed1"
            )
        }
    }
}
