package micdoodle8.mods.galacticraft.planets.mars

import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.api.client.IItemMeshDefinitionCustom
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.IPlanetsModuleClient
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockBasicMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockCavernousVine
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import micdoodle8.mods.galacticraft.planets.mars.client.SkyProviderMars
import micdoodle8.mods.galacticraft.planets.mars.client.fx.ParticleDrip
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiGasLiquefier
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiLaunchController
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiMethaneSynthesizer
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiSlimeling
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiSlimelingFeed
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiTerraformer
import micdoodle8.mods.galacticraft.planets.mars.client.gui.GuiWaterElectrolyzer
import micdoodle8.mods.galacticraft.planets.mars.client.render.entity.RenderCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.client.render.entity.RenderCreeperBoss
import micdoodle8.mods.galacticraft.planets.mars.client.render.entity.RenderLandingBalloons
import micdoodle8.mods.galacticraft.planets.mars.client.render.entity.RenderProjectileTNT
import micdoodle8.mods.galacticraft.planets.mars.client.render.entity.RenderSlimeling
import micdoodle8.mods.galacticraft.planets.mars.client.render.entity.RenderSludgeling
import micdoodle8.mods.galacticraft.planets.mars.client.render.entity.RenderTier2Rocket
import micdoodle8.mods.galacticraft.planets.mars.client.render.item.ItemModelRocketT2
import micdoodle8.mods.galacticraft.planets.mars.client.render.tile.TileEntityTreasureChestRenderer
import micdoodle8.mods.galacticraft.planets.mars.dimension.WorldProviderMars
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCreeperBoss
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityProjectileTNT
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySludgeling
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityTier2Rocket
import micdoodle8.mods.galacticraft.planets.mars.items.ItemSchematicTier2
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityElectrolyzer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityGasLiquefier
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityMethaneSynthesizer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityTerraformer
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityTreasureChestMars
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class MarsModuleClient : IPlanetsModuleClient {
    override fun preInit() {
        MinecraftForge.EVENT_BUS.register(this)

        RenderingRegistry.registerEntityRenderingHandler<EntitySludgeling>(
            EntitySludgeling::class.java,
            IRenderFactory { manager: RenderManager? -> RenderSludgeling((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntitySlimeling>(
            EntitySlimeling::class.java,
            IRenderFactory { manager: RenderManager? -> RenderSlimeling((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityCreeperBoss>(
            EntityCreeperBoss::class.java,
            IRenderFactory { manager: RenderManager? -> RenderCreeperBoss((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityProjectileTNT>(
            EntityProjectileTNT::class.java,
            IRenderFactory { manager: RenderManager? -> RenderProjectileTNT((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityCargoRocket>(
            EntityCargoRocket::class.java,
            IRenderFactory { manager: RenderManager? -> RenderCargoRocket((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityLandingBalloons>(
            EntityLandingBalloons::class.java,
            IRenderFactory { manager: RenderManager? -> RenderLandingBalloons((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityTier2Rocket>(
            EntityTier2Rocket::class.java,
            IRenderFactory { manager: RenderManager? -> RenderTier2Rocket((manager)!!) })
    }

    private fun addPlanetVariants(name: String, vararg variants: String?) {
        val itemBlockVariants: Item? = Item.REGISTRY.getObject(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID_PLANETS, name))
        val variants0: Array<ResourceLocation?> = arrayOfNulls<ResourceLocation>(variants.size)
        for (i: Int in variants.indices) {
            variants0[i] = ResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + variants[i])
        }
        ModelBakery.registerItemVariants(itemBlockVariants, *variants0)
    }

    override fun registerVariants() {
        addPlanetVariants(
            "mars",
            "ore_copper_mars",
            "ore_tin_mars",
            "ore_desh_mars",
            "ore_iron_mars",
            "cobblestone",
            "mars_surface",
            "mars_middle",
            "dungeon_brick",
            "desh_block",
            "mars_stone"
        )
        addPlanetVariants("cavern_vines", "vine_0", "vine_1", "vine_2")
        addPlanetVariants(
            "item_basic_mars",
            "raw_desh",
            "desh_stick",
            "ingot_desh",
            "reinforced_plate_t2",
            "slimeling_cargo",
            "compressed_desh",
            "fluid_manip"
        )
        addPlanetVariants("schematic", "schematic_rocket_t3", "schematic_rocket_cargo", "schematic_astro_miner")
        addPlanetVariants("slimeling_egg", "slimeling_egg_red", "slimeling_egg_blue", "slimeling_egg_yellow")
        addPlanetVariants("mars_machine", "terraformer", "cryogenic_chamber", "launch_controller")
        addPlanetVariants("mars_machine_t2", "gas_liquefier", "methane_synthesizer", "electrolyzer")

        val sludge: Item = Item.getItemFromBlock(MarsBlocks.blockSludge)
        ModelBakery.registerItemVariants(sludge, ResourceLocation.parse(GalacticraftPlanets.TEXTURE_PREFIX + "sludge"))
        ModelLoader.setCustomMeshDefinition(
            sludge,
            IItemMeshDefinitionCustom.create(IItemMeshDefinitionCustom { stack: ItemStack? -> sludgeLocation })
        )
        ModelLoader.setCustomStateMapper(MarsBlocks.blockSludge, object : StateMapperBase() {
            override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
                return sludgeLocation
            }
        })

        var modelResourceLocation: ModelResourceLocation =
            ModelResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "rocket_t2", "inventory")
        for (i: Int in 0..4) {
            ModelLoader.setCustomModelResourceLocation(MarsItems.rocketMars, i, modelResourceLocation)
        }

        modelResourceLocation = ModelResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "rocket_cargo", "inventory")
        for (i: Int in 11..14) {
            ModelLoader.setCustomModelResourceLocation(MarsItems.rocketMars, i, modelResourceLocation)
        }
    }

    override fun init(event: FMLInitializationEvent?) {
        MinecraftForge.EVENT_BUS.register(TickHandlerClient())
        registerBlockRenderers()
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    open fun loadTextures(event: TextureStitchEvent.Pre) {
        registerTexture(event, "rocket_t2")
        registerTexture(event, "cargo_rocket")
        registerTexture(event, "landing_balloon")
    }

    private fun registerTexture(event: TextureStitchEvent.Pre, texture: String) {
        event.getMap().registerSprite(ResourceLocation.parse(GalacticraftPlanets.TEXTURE_PREFIX + "blocks/" + texture))
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    open fun onModelBakeEvent(event: ModelBakeEvent?) {
        replaceModelDefault(
            event,
            "rocket_t2",
            "rocket_t2.obj",
            ImmutableList.of<String?>("Rocket"),
            ItemModelRocketT2::class.java,
            TRSRTransformation.identity()
        )
        replaceModelDefault(
            event,
            "rocket_cargo",
            "cargo_rocket.obj",
            ImmutableList.of<String?>("Rocket"),
            ItemModelCargoRocket::class.java,
            TRSRTransformation.identity()
        )
    }

    private fun replaceModelDefault(
        event: ModelBakeEvent?,
        resLoc: String,
        objLoc: String,
        visibleGroups: MutableList<String?>,
        clazz: Class<out ModelTransformWrapper>,
        parentState: IModelState?,
        vararg variants: String?
    ) {
        ClientUtil.replaceModel(
            GalacticraftPlanets.ASSET_PREFIX,
            (event)!!,
            resLoc,
            objLoc,
            visibleGroups,
            clazz,
            (parentState)!!,
            *(variants as Array<out String>)
        )
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
//            IModelCustom chamberModel = AdvancedModelLoader.loadModel(new ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "models/chamber.obj"));
//            IModelCustom cargoRocketModel = AdvancedModelLoader.loadModel(new ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "models/cargoRocket.obj"));
//
//        // Tile Entity Renderers
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityTreasureChestMars>(
            TileEntityTreasureChestMars::class.java,
            TileEntityTreasureChestRenderer()
        )

        //            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCryogenicChamber.class, new TileEntityCryogenicChamberRenderer(chamberModel));
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTerraformer.class, new TileEntityBubbleProviderRenderer<>(0.25F, 1.0F, 0.25F));
//
//        // Entities

        // Add Armor Renderer Prefix
//        RenderingRegistry.addNewArmourRendererPrefix("desh");
        ItemSchematicTier2.registerTextures()
    }

    override fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        if (side == Side.CLIENT) {
            val tile: TileEntity? = (world)!!.getBlockEntity(BlockPos(x, y, z))

            if (ID == GuiIdsPlanets.MACHINE_MARS) {
                if (tile is TileEntityTerraformer) {
                    return GuiTerraformer((player)!!.inventory, tile)
                } else if (tile is TileEntityLaunchController) {
                    return GuiLaunchController((player)!!.inventory, tile)
                } else if (tile is TileEntityElectrolyzer) {
                    return GuiWaterElectrolyzer((player)!!.inventory, tile)
                } else if (tile is TileEntityGasLiquefier) {
                    return GuiGasLiquefier((player)!!.inventory, tile)
                } else if (tile is TileEntityMethaneSynthesizer) {
                    return GuiMethaneSynthesizer((player)!!.inventory, tile)
                }
            }
        }

        return null
    }

    override fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, vararg extraData: Any?) {
        val mc: Minecraft? = Minecraft.getInstance()

        if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
            val dPosX: Double = mc.getRenderViewEntity()!!.posX - (position)!!.x
            val dPosY: Double = mc.getRenderViewEntity()!!.posY - (position)!!.y
            val dPosZ: Double = mc.getRenderViewEntity()!!.posZ - (position)!!.z
            var particle: Particle? = null
            val maxDistSqrd: Double = 64.0

            if (dPosX * dPosX + dPosY * dPosY + dPosZ * dPosZ < maxDistSqrd * maxDistSqrd) {
                if (particleID == "sludgeDrip") {
//                    particle = new EntityDropParticleFX(mc.world, position.x, position.y, position.z, Material.WATER); TODO
                } else if (particleID == "bacterialDrip") {
                    particle = ParticleDrip(mc.world, (position)!!.x, (position)!!.y, (position)!!.z)
                }
            }

            if (particle != null) {
                mc.effectRenderer.addEffect(particle)
            }
        }
    }

    override fun getGuiIDs(idList: MutableList<Int>?) {
        (idList)!!.add(GuiIdsPlanets.MACHINE_MARS)
    }

    open class TickHandlerClient {
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        open fun onClientTick(event: TickEvent.ClientTickEvent?) {
            val minecraft: Minecraft = Minecraft.getInstance()

            val world: WorldClient? = minecraft.world

            if (world != null) {
                if (world.provider is WorldProviderMars) {
                    if (world.provider.getSkyRenderer() == null) {
                        world.provider.setSkyRenderer(SkyProviderMars(world.provider as IGalacticraftWorldProvider))
                    }

                    if (world.provider.getCloudRenderer() == null) {
                        world.provider.setCloudRenderer(CloudRenderer())
                    }
                }
            }
        }
    }

    companion object {
        private val sludgeLocation = ModelResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "sludge", "fluid")

        @JvmStatic
        fun registerBlockRenderers() {
            for (blockBasic: BlockBasicMars.EnumBlockBasic in BlockBasicMars.EnumBlockBasic.entries) {
                ClientUtil.registerBlockJson(
                    GalacticraftPlanets.TEXTURE_PREFIX,
                    (MarsBlocks.marsBlock)!!,
                    blockBasic.getMeta(),
                    blockBasic.getName()
                )
            }

            for (vineType: BlockCavernousVine.EnumVineType in BlockCavernousVine.EnumVineType.entries) {
                ClientUtil.registerBlockJson(
                    GalacticraftPlanets.TEXTURE_PREFIX,
                    (MarsBlocks.vine)!!,
                    vineType.getMeta(),
                    vineType.getName()
                )
            }

            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.rock)!!, 0, "slimeling_egg_red")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.rock)!!, 1, "slimeling_egg_blue")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.rock)!!, 2, "slimeling_egg_yellow")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.creeperEgg)!!)
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.machine)!!, 0, "terraformer")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.machine)!!, 4, "cryogenic_chamber")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.machine)!!, 8, "launch_controller")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.machineT2)!!, 0, "gas_liquefier")
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (MarsBlocks.machineT2)!!,
                4,
                "methane_synthesizer"
            )
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.machineT2)!!, 8, "electrolyzer")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.treasureChestTier2)!!)
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.marsBricksStairs)!!)
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.marsCobblestoneStairs)!!)
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsBlocks.bossSpawner)!!)
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsItems.marsItemBasic)!!, 0, "raw_desh")
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsItems.marsItemBasic)!!, 1, "desh_stick")
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsItems.marsItemBasic)!!, 2, "ingot_desh")
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (MarsItems.marsItemBasic)!!,
                3,
                "reinforced_plate_t2"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (MarsItems.marsItemBasic)!!,
                4,
                "slimeling_cargo"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (MarsItems.marsItemBasic)!!,
                5,
                "compressed_desh"
            )
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsItems.marsItemBasic)!!, 6, "fluid_manip")
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (MarsItems.key)!!, 0, "key")
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (MarsItems.schematic)!!,
                0,
                "schematic_rocket_t3"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (MarsItems.schematic)!!,
                1,
                "schematic_rocket_cargo"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (MarsItems.schematic)!!,
                2,
                "schematic_astro_miner"
            )
        }

        @JvmStatic
        fun openSlimelingGui(slimeling: EntitySlimeling?, gui: Int) {
            when (gui) {
                0 -> Minecraft.getInstance().gui.setScreen(GuiSlimeling((slimeling)!!))
                1 -> Minecraft.getInstance().gui.setScreen(GuiSlimelingFeed((slimeling)!!))
            }
        }
    }
}
