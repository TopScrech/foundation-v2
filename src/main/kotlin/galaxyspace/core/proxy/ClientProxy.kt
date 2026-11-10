package galaxyspace.core.proxy

import galaxyspace.core.prefab.blocks.DungeonBlocks
import galaxyspace.systems.SolarSystem.moons.callisto.blocks.CallistoBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks
import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks
import galaxyspace.systems.SolarSystem.moons.ganymede.blocks.GanymedeBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks
import galaxyspace.systems.SolarSystem.moons.titan.blocks.TitanBlocks
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks
import galaxyspace.systems.SolarSystem.planets.haumea.blocks.HaumeaBlocks
import galaxyspace.systems.SolarSystem.planets.mars.blocks.MarsOresBlocks
import galaxyspace.systems.SolarSystem.planets.mercury.blocks.MercuryBlocks
import galaxyspace.systems.SolarSystem.planets.pluto.blocks.PlutoBlocks
import net.minecraftforge.client.event.TextureStitchEvent.Pre

import galaxyspace.GalaxySpace
import galaxyspace.core.GSFluids
import galaxyspace.core.handler.GSSkyProviderHandler
import galaxyspace.core.events.GSClientTickHandler
import galaxyspace.core.handler.GSMapHandler
import galaxyspace.core.client.GSKeyHandlerClient
import galaxyspace.core.client.models.BakedModelGlowBlockOverlay
import galaxyspace.core.client.render.entity.layers.LayerOxygenTank
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemSchematics
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.render.tile.TileEntityModernSolarPanelRenderer
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.render.tile.TileEntityWindGeneratorRenderer
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicFarm
import galaxyspace.systems.SolarSystem.planets.overworld.render.tile.TileEntityHydroponicFarmRenderer
import galaxyspace.systems.SolarSystem.planets.ceres.tile.TileEntityTreasureChestCeres
import galaxyspace.systems.SolarSystem.moons.io.tile.TileEntityTreasureChestIo
import galaxyspace.systems.SolarSystem.moons.enceladus.tile.TileEntityBlockCrystallTE
import galaxyspace.systems.SolarSystem.moons.enceladus.tile.TileEntityBlockCrystall
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.render.tile.TileEntitySolarWindPanelRenderer
import javax.vecmath.Quat4f
import com.google.common.collect.ImmutableList
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemRendererHydroponicFarm
import galaxyspace.core.client.render.item.ItemModelRocketT4
import galaxyspace.core.client.render.item.ItemModelRocketT5
import galaxyspace.core.client.render.item.ItemModelRocketT6
import galaxyspace.systems.SolarSystem.planets.mars.render.item.ItemModelMarsRover
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemRendererJetpack
import galaxyspace.core.client.models.BakedModelFullbright
import galaxyspace.core.client.models.BakedModelBrightFour
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.mercury.blocks.MercuryBlocks.EnumBlockMercury
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockFutureGlasses.EnumBlockFutureGlass
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockOres.EnumBlockOres
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockDecoMetals.EnumBlockDecoMetals
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockMetals.EnumBlockMetals
import galaxyspace.systems.SolarSystem.planets.mars.blocks.MarsOresBlocks.EnumMarsOresBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks.EnumCeresBlocks
import galaxyspace.systems.SolarSystem.planets.pluto.blocks.PlutoBlocks.EnumPlutoBlocks
import galaxyspace.systems.SolarSystem.planets.haumea.blocks.HaumeaBlocks.EnumHaumeaBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks.EnumIoBlocks
import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks.EnumEuropaBlocks
import galaxyspace.systems.SolarSystem.moons.ganymede.blocks.GanymedeBlocks.EnumGanymedeBlocks
import galaxyspace.systems.SolarSystem.moons.callisto.blocks.CallistoBlocks.EnumCallistoBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks.EnumEnceladusBlocks
import galaxyspace.systems.SolarSystem.moons.titan.blocks.TitanBlocks.EnumTitanBlocks
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks.EnumMirandaBlocks
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks.EnumTritonBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockMachineFrames.EnumBlockMachineFrames
import galaxyspace.core.prefab.blocks.DungeonBlocks.EnumDungeonBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockSurfaceIce.EnumBlockIce
import galaxyspace.systems.SolarSystem.moons.phobos.blocks.PhobosBlocks
import galaxyspace.systems.SolarSystem.moons.phobos.blocks.PhobosBlocks.EnumPhobosBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS
import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemIngots
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemHeavyDutyPlates
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemCompressedPlates
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemRocketModules
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemRocketParts
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemTierKeysChest
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemUpgrades
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemThermalPaddingBase
import asmodeuscore.api.IBodies
import asmodeuscore.AsmodeusCore
import galaxyspace.core.client.fx.GSEffectHandler
import galaxyspace.core.prefab.entities.EntityTier4Rocket
import galaxyspace.core.client.render.entity.RenderRockets
import galaxyspace.core.prefab.entities.EntityTier5Rocket
import galaxyspace.core.prefab.entities.EntityTier6Rocket
import galaxyspace.core.prefab.entities.EntityMultiSeatRocketTest
import galaxyspace.core.prefab.entities.EntityCustomCargoRocket
import galaxyspace.core.client.render.entity.RenderCargoRockets
import galaxyspace.systems.SolarSystem.planets.ceres.entities.EntityBossBlaze
import galaxyspace.systems.SolarSystem.planets.ceres.renderer.entities.RenderBossBlaze
import galaxyspace.core.prefab.entities.EntityIceSpike
import galaxyspace.core.client.render.entity.RenderIceSpike
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import galaxyspace.core.client.render.entity.RenderEvolvedColdBlaze
import galaxyspace.systems.SolarSystem.moons.io.entities.EntityBossGhast
import galaxyspace.systems.SolarSystem.moons.io.renderer.entities.RenderBossGhast
import galaxyspace.systems.SolarSystem.planets.mars.entities.EntityMarsRover
import galaxyspace.systems.SolarSystem.planets.mars.render.entities.RenderMarsRover
import galaxyspace.core.prefab.entities.EntityAstroWolf
import galaxyspace.core.client.render.entity.RenderAstroWolf
import com.google.common.collect.Maps
import galaxyspace.core.client.render.entity.layers.LayerThermalPadding
import galaxyspace.core.client.render.tile.TileEntityTreasureChestRenderer
import micdoodle8.mods.galacticraft.api.client.IItemMeshDefinitionCustom
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.item.ItemModelCargoRocket
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.HashMap
import javax.vecmath.Vector3f

open class ClientProxy : CommonProxy() {
    override fun preload(event: FMLPreInitializationEvent?) {
        register_event(GSSkyProviderHandler())
        register_event(GSClientTickHandler())
        register_event(GSMapHandler())
        //register_event(new ColorBlockHandler());
        register_event(GSKeyHandlerClient())

        ClientRegistry.registerKeyBinding(GSKeyHandlerClient.toggleHelmet)
        ClientRegistry.registerKeyBinding(GSKeyHandlerClient.toggleChest)
        ClientRegistry.registerKeyBinding(GSKeyHandlerClient.toggleLegs)
        ClientRegistry.registerKeyBinding(GSKeyHandlerClient.toggleBoots)

        //ClientProxy.setupCapes();
        registerEntityRenderers()

        glow_blocks.put(
            "barnarda_c_glow_plant_1", BakedModelGlowBlockOverlay(BakedModelGlowBlockOverlay.QuadType.CROSS)
                .addTexture(EnumFacing.NORTH, "galaxyspace:blocks/barnardssystem/barnarda_c/glow_plant_1_glow")
                .setBrightness(250)
        )

        glow_blocks.put(
            "barnarda_c_glow_plant_2", BakedModelGlowBlockOverlay(BakedModelGlowBlockOverlay.QuadType.CROSS)
                .addTexture(EnumFacing.NORTH, "galaxyspace:blocks/barnardssystem/barnarda_c/glow_plant_2_glow")
                .setBrightness(250)
        )

        glow_blocks.put(
            "barnarda_c_glow_plant_3", BakedModelGlowBlockOverlay(BakedModelGlowBlockOverlay.QuadType.CROSS)
                .addTexture(EnumFacing.NORTH, "galaxyspace:blocks/barnardssystem/barnarda_c/glow_plant_3_glow")
                .setBrightness(250)
        )

        glow_blocks.put(
            "barnarda_c_glow_plant_4", BakedModelGlowBlockOverlay(BakedModelGlowBlockOverlay.QuadType.CROSS)
                .addTexture(EnumFacing.NORTH, "galaxyspace:blocks/barnardssystem/barnarda_c/glow_plant_4_glow")
                .setBrightness(250)
        )

        glow_blocks.put(
            "modification_table", BakedModelGlowBlockOverlay(BakedModelGlowBlockOverlay.QuadType.BLOCK)
                .addTexture(EnumFacing.UP, "galaxyspace:blocks/overworld/modification_table_top_glow_1")
                .setBrightness(150)
        )

        /*glow_blocks.put("barnarda_c_test_glow_log", new BakedModelGlowBlockOverlay(BakedModelGlowBlockOverlay.QuadType.BLOCK)
				.addTexture(EnumFacing.NORTH, "galaxyspace:blocks/barnardssystem/barnarda_c/log_oak_layer")
				.addTexture(EnumFacing.SOUTH, "galaxyspace:blocks/barnardssystem/barnarda_c/log_oak_layer")
				.addTexture(EnumFacing.EAST, "galaxyspace:blocks/barnardssystem/barnarda_c/log_oak_layer")
				.addTexture(EnumFacing.WEST, "galaxyspace:blocks/barnardssystem/barnarda_c/log_oak_layer")
				.setBrightness(250));*/
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun load() {
        Minecraft.getMinecraft().getRenderManager().getSkinMap().get("default")!!
            .addLayer<AbstractClientPlayer?, LayerOxygenTank?>(LayerOxygenTank())
        Minecraft.getMinecraft().getRenderManager().getSkinMap().get("slim")!!
            .addLayer<AbstractClientPlayer?, LayerOxygenTank?>(LayerOxygenTank())

        Minecraft.getMinecraft().getRenderManager().getSkinMap().get("default")!!
            .addLayer<EntityLivingBase?, LayerThermalPadding?>(
                LayerThermalPadding(
                    Minecraft.getMinecraft().getRenderManager().playerRenderer
                )
            )
        Minecraft.getMinecraft().getRenderManager().getSkinMap().get("slim")!!
            .addLayer<EntityLivingBase?, LayerThermalPadding?>(
                LayerThermalPadding(
                    Minecraft.getMinecraft().getRenderManager().playerRenderer
                )
            )


        //GS_MUSIC = EnumHelper.addEnum(MusicTicker.MusicType.class, "GS_MUSIC", new Class[] { SoundEvent.class, Integer.TYPE, Integer.TYPE }, GSSounds.music, 12000, 24000);

        /*
    	ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySolarWind.class, new TileEntitySolarWindPanelRenderer());
    	ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDuraluminumWire.class, new TileEntityDuraluminumWireRenderer());
     	
    	//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFuelGenerator.class, new TileEntityGeneratorRenderer());
    	ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThermodynamicReactor.class, new TileEntityThermodynamicReactorRenderer());
     	 */
    }

    override fun postload() {
        /*try {
            Field ftc = Minecraft.getMinecraft().getClass().getDeclaredField(GCCoreUtil.isDeobfuscated() ? "mcMusicTicker" : "field_147126_aw");
            ftc.setAccessible(true);
            ftc.set(Minecraft.getMinecraft(), new GSMusicTicker(Minecraft.getMinecraft()));
        } catch (Exception e) {e.printStackTrace();} 
    	*/
        ItemSchematics.registerTextures()

        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityModernSolarPanel>(
            TileEntityModernSolarPanel::class.java,
            TileEntityModernSolarPanelRenderer()
        )
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityWindGenerator>(
            TileEntityWindGenerator::class.java,
            TileEntityWindGeneratorRenderer()
        )
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityHydroponicFarm>(
            TileEntityHydroponicFarm::class.java,
            TileEntityHydroponicFarmRenderer()
        )
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityTreasureChestCeres>(
            TileEntityTreasureChestCeres::class.java,
            TileEntityTreasureChestRenderer(
                ResourceLocation(
                    GalaxySpace.ASSET_PREFIX,
                    "textures/model/treasure_ceres.png"
                )
            )
        )
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityTreasureChestIo>(
            TileEntityTreasureChestIo::class.java,
            TileEntityTreasureChestRenderer(
                ResourceLocation(
                    GalaxySpace.ASSET_PREFIX,
                    "textures/model/treasure_io.png"
                )
            )
        )
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityBlockCrystallTE>(
            TileEntityBlockCrystallTE::class.java,
            TileEntityBlockCrystall()
        )
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityWindSolarPanel>(
            TileEntityWindSolarPanel::class.java,
            TileEntitySolarWindPanelRenderer()
        )
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun onModelBakeEvent(event: ModelBakeEvent) {
        val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(30f, 225f, 0f))
        replaceModelDefault(
            event,
            "hydroponic_farm",
            "hydroponic_farm.obj",
            ImmutableList.of<String?>("ferma_2"),
            ItemRendererHydroponicFarm::class.java,
            TRSRTransformation(Vector3f(0.7f, 0.1f, 0.0f), rot, Vector3f(0.2604f, 0.2604f, 0.2604f), Quat4f()),
            "inventory",
            "normal"
        )
        replaceModelDefault(
            event,
            "rockets/rocket_t4",
            "tier4rocket.obj",
            ImmutableList.of<String?>("Base"),
            ItemModelRocketT4::class.java,
            TRSRTransformation.identity()
        )
        //replaceModelDefault(event, "rockets/rocket_t4", "tier4rocketGS.obj", ImmutableList.of("Base", "NoseCone", "Rocket", "Booster1", "Booster2", "Booster3", "Booster4"), ItemModelRocketT4.class, TRSRTransformation.identity());
        replaceModelDefault(
            event,
            "rockets/rocket_t5",
            "tier5rocket.obj",
            ImmutableList.of<String?>("Base"),
            ItemModelRocketT5::class.java,
            TRSRTransformation.identity()
        )
        replaceModelDefault(
            event,
            "rockets/rocket_t6",
            "tier6rocket.obj",
            ImmutableList.of<String?>("Base"),
            ItemModelRocketT6::class.java,
            TRSRTransformation.identity()
        )
        replaceModelDefault(
            event,
            "rockets/rocket_cargo",
            "cargo_rocket.obj",
            ImmutableList.of<String?>("Rocket"),
            ItemModelCargoRocket::class.java,
            TRSRTransformation.identity()
        )
        replaceModelDefault(
            event,
            "mars_rover",
            "mars_rover.obj",
            ImmutableList.of<String?>(
                "base",
                "ltR",
                "ltL",
                "ltR.001",
                "ltL.001",
                "frontbase",
                "backbase",
                "frontWheelL",
                "frontWheelR",
                "backWheelL",
                "backWheelR"
            ),
            ItemModelMarsRover::class.java,
            TRSRTransformation.identity()
        )

        replaceModelDefault(
            event,
            "armor/jetpack",
            "jetpack.obj",
            ImmutableList.of<String?>("wing1", "wing2", "corp"),
            ItemRendererJetpack::class.java,
            TRSRTransformation.identity()
        )


        //replaceModelDefault(event, "tools/matter_manipulator", "matter_manipulator.obj", ImmutableList.of("Up", "Down", "Mid"), ItemRendererMatterManipulator.class, TRSRTransformation.identity());
        for (resource: ModelResourceLocation in event.getModelRegistry().getKeys()) {
            if (resource.getNamespace() == GalaxySpace.MODID) {
                if (!FMLClientHandler.instance().hasOptifine()) {
                    if (resource.getPath() == "dungeon_blocks") {
                        if (resource.getVariant().contains("ceres_bricks")) {
                            event.getModelRegistry().putObject(
                                resource,
                                BakedModelFullbright(
                                    event.getModelRegistry().getObject(resource),
                                    "galaxyspace:blocks/solarsystem/ceres/ceres_bricks_layer",
                                    250,
                                    0.45
                                )
                            )
                            continue
                        }

                        if (resource.getVariant().contains("io_bricks")) {
                            event.getModelRegistry().putObject(
                                resource,
                                BakedModelFullbright(
                                    event.getModelRegistry().getObject(resource),
                                    "galaxyspace:blocks/solarsystem/io/io_bricks_layer",
                                    250,
                                    1.0
                                )
                            )
                            continue
                        }
                    }
                    if (resource.getPath() == "barnarda_c_test_glow_log") {
                        val top: String = "galaxyspace:blocks/barnardssystem/barnarda_c/log_oak_top"
                        event.getModelRegistry().putObject(
                            resource,
                            BakedModelBrightFour(
                                event.getModelRegistry().getObject(resource),
                                "galaxyspace:blocks/barnardssystem/barnarda_c/log_oak_layer",
                                top,
                                250,
                                1.0
                            )
                        )
                        continue
                    }

                    for (glow in glow_blocks.entries) {
                        if (resource.getPath() == glow.key || resource.getVariant().contains(glow.key!!)) {
                            val overlay: BakedModelGlowBlockOverlay = (glow.value)!!
                            overlay.setModel(event.getModelRegistry().getObject(resource))
                            event.getModelRegistry().putObject(resource, overlay)
                        }
                    }
                }
                /*if(resource.getVariant().contains("barnarda_c_glow_plant")) {
						String top = "galaxyspace:blocks/barnardssystem/barnarda_c/glow_plant_1_glow";
						event.getModelRegistry().putObject(resource, new BakedModelGlowBlockOverlay(event.getModelRegistry().getObject(resource), top, 250, 1.0D));
						continue;
					}*/
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun loadTextures(event: TextureStitchEvent.Pre?) {
        (GalaxySpace.proxy)!!.registerTexture(event, "model/modern_solarpanel")
        (GalaxySpace.proxy)!!.registerTexture(event, "model/windmill_straight")
        (GalaxySpace.proxy)!!.registerTexture(event, "model/armor/spacesuit")
        (GalaxySpace.proxy)!!.registerTexture(event, "model/armor/jetpack")
        (GalaxySpace.proxy)!!.registerTexture(event, "model/hydroponic_farm")
        //GalaxySpace.proxy.registerTexture(event, "model/matter_manipulator");
        (GalaxySpace.proxy)!!.registerTexture(event, "model/rocket_tier_4")
        (GalaxySpace.proxy)!!.registerTexture(event, "model/rocket_tier_4_launch")
        (GalaxySpace.proxy)!!.registerTexture(event, "model/rocket_tier_5")
        (GalaxySpace.proxy)!!.registerTexture(event, "model/rocket_tier_6")
        (GalaxySpace.proxy)!!.registerTexture(event, "model/cargo_rocket")
        (GalaxySpace.proxy)!!.registerTexture(event, "model/mars_rover")


        (GalaxySpace.proxy)!!.registerTexture(event, "blocks/solarsystem/ceres/ceres_bricks_layer")
        (GalaxySpace.proxy)!!.registerTexture(event, "blocks/solarsystem/io/io_bricks_layer")
        (GalaxySpace.proxy)!!.registerTexture(event, "blocks/barnardssystem/barnarda_c/log_oak_layer")
        //GalaxySpace.proxy.registerTexture(event, "blocks/barnardssystem/barnarda_c/glow_plant_1_glow");
        for (glow: MutableMap.MutableEntry<String?, BakedModelGlowBlockOverlay?> in glow_blocks.entries) {
            glow.value!!.getSprites().forEach { (k: EnumFacing?, v: String?) ->
                (GalaxySpace.proxy)!!.registerTexture(
                    event,
                    v!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                )
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun onStitch(event: TextureStitchEvent.Post) {
        GSUtils.initFluidTextures(event.getMap())
    }

    override fun registerTexture(event: TextureStitchEvent.Pre?, texture: String?) {
        (event)!!.getMap().registerSprite(ResourceLocation(GalaxySpace.TEXTURE_PREFIX + texture))
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
            GalaxySpace.ASSET_PREFIX,
            (event)!!,
            resLoc,
            objLoc,
            (visibleGroups)!!,
            clazz,
            (parentState)!!,
            *(variants as Array<out String>)
        )
    }

    override fun registerRender() {
        var name: Array<String?> = arrayOfNulls<String>(4)

        //BLOCKS META
        for (blockBasic: EnumBlockMercury in EnumBlockMercury.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.MERCURY_BLOCKS,
            blockBasic.getMeta(),
            blockBasic.getName()
        )

        for (blockBasic: EnumBlockFutureGlass in EnumBlockFutureGlass.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.FUTURE_GLASS_COLORED,
            blockBasic.getMeta(),
            blockBasic.getName()
        )

        for (block: EnumBlockOres in EnumBlockOres.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.OVERWORLD_ORES,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumBlockDecoMetals in EnumBlockDecoMetals.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.DECO_METALS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumBlockMetals in EnumBlockMetals.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.METALS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumMarsOresBlocks in EnumMarsOresBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.MARS_ORES,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumCeresBlocks in EnumCeresBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.CERES_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumPlutoBlocks in EnumPlutoBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.PLUTO_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumHaumeaBlocks in EnumHaumeaBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.HAUMEA_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumIoBlocks in EnumIoBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.IO_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumEuropaBlocks in EnumEuropaBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.EUROPA_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumGanymedeBlocks in EnumGanymedeBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.GANYMEDE_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumCallistoBlocks in EnumCallistoBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.CALLISTO_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumEnceladusBlocks in EnumEnceladusBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.ENCELADUS_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumTitanBlocks in EnumTitanBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.TITAN_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumMirandaBlocks in EnumMirandaBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.MIRANDA_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumTritonBlocks in EnumTritonBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.TRITON_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumBlockMachineFrames in EnumBlockMachineFrames.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.MACHINE_FRAMES,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumDungeonBlocks in EnumDungeonBlocks.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.DUNGEON_BLOCKS,
            block.getMeta(),
            block.getName()
        )

        for (block: EnumBlockIce in EnumBlockIce.entries.toTypedArray()) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSBlocks.SURFACE_ICE,
            block.getMeta(),
            block.getName()
        )

        name = arrayOfNulls<String>(EnumPhobosBlocks.entries.toTypedArray().size)
        for (block: EnumPhobosBlocks in EnumPhobosBlocks.entries.toTypedArray()) {
            if (block.getName() != null) name[block.getMeta()] = block.getName()
            ClientUtil.registerBlockJson(
                GalaxySpace.TEXTURE_PREFIX,
                GSBlocks.PHOBOS_BLOCKS,
                block.getMeta(),
                block.getName()
            )
        }

        if (GCCoreUtil.isDeobfuscated()) GSUtils.addBlockMetadataJsonFiles(
            GSBlocks.PHOBOS_BLOCKS,
            name,
            PhobosBlocks.BASIC_TYPE.getName(),
            ""
        )


        //--------------------------- BLOCKS -----------------------------------
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.ASSEMBLER)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.FUEL_GENERATOR)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.MODERN_SOLAR_PANEL)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.FUTURE_GLASS_BASIC)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.EUROPA_GEYSER)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.EUROPA_UWGEYSER)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.WIND_GENERATOR)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.ROCKET_ASSEMBLER)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.UNIVERSAL_RECYCLER)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.LIQUID_EXTRACTOR)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.LIQUID_SEPARATOR)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.HYDROPONIC_BASE)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.HYDROPONIC_FARM)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.GRAVITATION_MODULE)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.BOSS_SPAWNER_CERES)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.BOSS_SPAWNER_IO)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.TREASURE_CHEST_TIER_4, 0, "treasure_t4")
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.TREASURE_CHEST_TIER_5, 0, "treasure_t5")
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.RADIATION_STABILISER)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.ENCELADUS_CRYSTAL)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.PANEL_CONTROLLER)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.SINGLE_SOLARPANEL)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.MODERN_SINGLE_SOLARPANEL)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.PLANET_SHIELD)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.MODERN_STORAGE_MODULE)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.ADVANCED_LANDING_PAD_SINGLE)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.ADVANCED_LANDING_PAD)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.FAKE_BLOCK)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.MODIFICATION_TABLE)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.GAS_BURNER)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.OXYGEN_STORAGE_MODULE)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.SOLARWIND_PANEL)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.ADVANCED_CIRCUIT_FABRICATOR)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.ENERGY_PAD)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.GAS_COLLECTOR)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.GAS_GENERATOR)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.DRY_LEAVES)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, GSBlocks.ADVANCED_WIND_GENERATOR)

        if (GCCoreUtil.isDeobfuscated()) {
            //GSUtils.addBlockJsonFiles(GSBlocks.SOLARWIND_PANEL, "overworld/");
            //GSUtils.addBlockJsonFiles(GSBlocks.ENERGY_PAD, "overworld/");
        }

        // -------------------------- ITEMS ------------------------------------
        var i: Int = 0
        for (basic: BasicItems in BasicItems.entries.toTypedArray()) {
            if (basic.getName() == "null") {
                i++
                continue
            }
            ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.BASIC, i++, "basic/" + basic.getName())
        }

        i = 0
        for (ingots: String? in ItemIngots.names) {
            ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.INGOTS, i, "ingots/" + ingots)
            ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.NUGGETS, i++, "nuggets/" + ingots)
        }

        i = 0
        for (hdp: String? in ItemHeavyDutyPlates.names) ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.HDP,
            i++,
            "hdp/" + hdp
        )

        i = 0
        for (plates: String? in ItemCompressedPlates.names) ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COMPRESSED_PLATES,
            i++,
            "compressed_plates/" + plates
        )

        i = 0
        for (modules: String? in ItemRocketModules.names) ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.ROCKET_MODULES,
            i++,
            "rocket_modules/" + modules
        )

        i = 0
        for (parts: String? in ItemRocketParts.names) ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.ROCKET_PARTS,
            i++,
            "rocket_parts/" + parts
        )

        i = 0
        for (keys: String? in ItemTierKeysChest.keys) ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.DUNGEON_KEYS,
            i++,
            "keys/" + keys
        )

        i = 0
        for (upgrades: String? in ItemUpgrades.names) ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.UPGRADES,
            i++,
            "upgrades/" + upgrades
        )

        i = 0
        for (thermal_padding: String? in ItemThermalPaddingBase.names) {
            ClientUtil.registerItemJson(
                GalaxySpace.TEXTURE_PREFIX,
                GSItems.THERMAL_PADDING_3,
                i,
                "thermal_padding/" + thermal_padding + "_t" + (GSItems.THERMAL_PADDING_3 as ItemThermalPaddingBase).getThermalStrength()
            )
            ClientUtil.registerItemJson(
                GalaxySpace.TEXTURE_PREFIX,
                GSItems.THERMAL_PADDING_4,
                i,
                "thermal_padding/" + thermal_padding + "_t" + (GSItems.THERMAL_PADDING_4 as ItemThermalPaddingBase).getThermalStrength()
            )
            i++
        }

        ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.OXYGENTANK_TIER_4)
        ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.OXYGENTANK_TIER_5)
        ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.OXYGENTANK_TIER_6)
        ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.OXYGENTANK_TIER_EPP)

        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.ADVANCED_BATTERY,
            0,
            "batteries/" + GSItems.ADVANCED_BATTERY.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.MODERN_BATTERY,
            0,
            "batteries/" + GSItems.MODERN_BATTERY.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.EXTRA_BATTERY,
            0,
            "batteries/" + GSItems.EXTRA_BATTERY.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.ULTIMATE_BATTERY,
            0,
            "batteries/" + GSItems.ULTIMATE_BATTERY.getTranslationKey().substring(5)
        )

        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SPACE_SUIT_HELMET,
            0,
            "armor/" + GSItems.SPACE_SUIT_HELMET.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SPACE_SUIT_BODY,
            0,
            "armor/" + GSItems.SPACE_SUIT_BODY.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SPACE_SUIT_LEGGINS,
            0,
            "armor/" + GSItems.SPACE_SUIT_LEGGINS.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SPACE_SUIT_BOOTS,
            0,
            "armor/" + GSItems.SPACE_SUIT_BOOTS.getTranslationKey().substring(5)
        )

        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SPACE_SUIT_LIGHT_HELMET,
            0,
            "armor/" + GSItems.SPACE_SUIT_LIGHT_HELMET.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SPACE_SUIT_LIGHT_BODY,
            0,
            "armor/" + GSItems.SPACE_SUIT_LIGHT_BODY.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SPACE_SUIT_LIGHT_LEGGINS,
            0,
            "armor/" + GSItems.SPACE_SUIT_LIGHT_LEGGINS.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SPACE_SUIT_LIGHT_BOOTS,
            0,
            "armor/" + GSItems.SPACE_SUIT_LIGHT_BOOTS.getTranslationKey().substring(5)
        )

        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.JETPACK,
            0,
            "armor/" + GSItems.JETPACK.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.MATTER_MANIPULATOR,
            0,
            "tools/" + GSItems.MATTER_MANIPULATOR.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.GEOLOGICAL_SCANNER,
            0,
            "tools/" + GSItems.GEOLOGICAL_SCANNER.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.GAS_EXTRACTOR,
            0,
            "tools/" + GSItems.GAS_EXTRACTOR.getTranslationKey().substring(5)
        )

        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COBALT_HELMET,
            0,
            "armor/" + GSItems.COBALT_HELMET.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COBALT_CHEST,
            0,
            "armor/" + GSItems.COBALT_CHEST.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COBALT_LEGS,
            0,
            "armor/" + GSItems.COBALT_LEGS.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COBALT_BOOTS,
            0,
            "armor/" + GSItems.COBALT_BOOTS.getTranslationKey().substring(5)
        )

        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COBALT_SWORD,
            0,
            "tools/" + GSItems.COBALT_SWORD.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COBALT_AXE,
            0,
            "tools/" + GSItems.COBALT_AXE.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COBALT_PICKAXE,
            0,
            "tools/" + GSItems.COBALT_PICKAXE.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COBALT_SPADE,
            0,
            "tools/" + GSItems.COBALT_SPADE.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.COBALT_HOE,
            0,
            "tools/" + GSItems.COBALT_HOE.getTranslationKey().substring(5)
        )

        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.PLASMA_SWORD,
            0,
            "tools/" + GSItems.PLASMA_SWORD.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.PLASMA_AXE,
            0,
            "tools/" + GSItems.PLASMA_AXE.getTranslationKey().substring(5)
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.PLASMA_PICKAXE,
            0,
            "tools/" + GSItems.PLASMA_PICKAXE.getTranslationKey().substring(5)
        )

        ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.SCHEMATICS, 0, "schematics/" + "schematic_cone")
        ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.SCHEMATICS, 1, "schematics/" + "schematic_body")
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SCHEMATICS,
            2,
            "schematics/" + "schematic_engine"
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SCHEMATICS,
            3,
            "schematics/" + "schematic_booster"
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SCHEMATICS,
            4,
            "schematics/" + "schematic_stabiliser"
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SCHEMATICS,
            5,
            "schematics/" + "schematic_oxygen_tank"
        )
        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            GSItems.SCHEMATICS,
            6,
            "schematics/" + "schematic_port_nuclear_reactor"
        )

        ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, GSItems.MARS_ROVER, 0, "mars_rover")

        if (GCCoreUtil.isDeobfuscated()) {
            //GSUtils.addItemJsonFiles(GSItems.JETPACK, "armor/", GSItems.JETPACK.getUnlocalizedName().substring(5));
            //GSUtils.addItemJsonFiles(GSItems.PLASMA_SWORD, "tools/", GSItems.PLASMA_SWORD.getUnlocalizedName().substring(5));
            //GSUtils.addItemJsonFiles(GSItems.COBALT_HOE, "tools/", GSItems.COBALT_HOE.getUnlocalizedName().substring(5));
            //GSUtils.addItemJsonFiles(GSItems.COBALT_LEGS, "armor/", GSItems.COBALT_LEGS.getUnlocalizedName().substring(5));
            //GSUtils.addItemJsonFiles(GSItems.COBALT_BOOTS, "armor/", GSItems.COBALT_BOOTS.getUnlocalizedName().substring(5));
            GSUtils.addItemMetadataJsonFiles(GSItems.BASIC, ItemBasicGS.getEnumNames(), "basic/")
            GSUtils.addItemMetadataJsonFiles(GSItems.ROCKET_MODULES, (ItemRocketModules.names as Array<String?>), "rocket_modules/")
        }

        for (list in AsmodeusCore.bodies) if ((list)!!.canRegister()) (list)!!.registerRender()
    }

    override fun registerVariants() {
        //Blocks
        var blocks: Array<String?> =
            arrayOfNulls<String>(EnumBlockFutureGlass.entries.toTypedArray().size)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockFutureGlass.byMetadata(i))!!.getName()

        addVariant("futureglass", "", *blocks)
        addVariant(
            "mercuryblocks",
            "",
            "mercury_surface",
            "mercury_subsurface",
            "mercury_stone",
            "mercury_nickel_ore",
            "mercury_iron_ore",
            "mercury_magnesium_ore"
        )
        addVariant("gsores", "", "cobaltum_ore", "nickel_ore", "uranium_ore")
        addVariant(
            "decoblocks",
            "",
            "deco_cobaltum_1",
            "deco_magnesium_1",
            "deco_nickel_1",
            "deco_copper_1",
            "deco_cobaltum_2",
            "deco_magnesium_2",
            "deco_nickel_2",
            "deco_copper_2",
            "deco_aluminum_1",
            "deco_aluminum_2"
        )
        addVariant("blocksmetals", "", "cobalt_block", "nickel_block", "magnesium_block")
        addVariant(
            "marsores",
            "",
            "mars_diamond",
            "mars_gold",
            "mars_coal",
            "mars_redstone",
            "mars_silicon",
            "mars_aluminum"
        )
        addVariant(
            "ceresblocks",
            "",
            "ceres_grunt",
            "ceres_subgrunt",
            "ceres_dolomite_ore",
            "ceres_meteoriciron_ore",
            "ceres_dungeon_top",
            "ceres_dungeon_floor"
        )
        addVariant(
            "plutoblocks",
            "",
            "pluto_grunt_1",
            "pluto_grunt_2",
            "pluto_grunt_3",
            "pluto_grunt_4",
            "pluto_subgrunt",
            "pluto_stone"
        )
        addVariant(
            "phobosblocks",
            "",
            "phobos_regolite",
            "phobos_stone",
            "phobos_iron_ore",
            "phobos_meteoriciron_ore",
            "phobos_nickel_ore",
            "phobos_desh_ore"
        )
        addVariant(
            "ioblocks",
            "",
            "io_grunt",
            "io_stone",
            "io_ash",
            "io_copper_ore",
            "io_sulfur_ore",
            "io_volcanic_ore",
            "io_lava_geyser",
            "io_sulfur_geyser",
            "io_top",
            "io_floor",
            "io_dungeon_bricks"
        )
        //addVariant("europablocks", "", "europa_grunt", "europa_stone", "europa_brown_ice", "europa_emerald_ore", "europa_silicon_ore", "europa_aluminum_ore");
        addVariant(
            "ganymedeblocks",
            "",
            "ganymede_grunt",
            "ganymede_stone",
            "ganymede_magnesium_ore",
            "ganymede_titanium_ore"
        )
        addVariant("callistoblocks", "", "callisto_grunt", "callisto_stone")
        addVariant("enceladusblocks", "", "enceladus_snow", "enceladus_grunt", "enceladus_coal_ore")


        blocks = arrayOfNulls<String>(EnumEuropaBlocks.entries.toTypedArray().size)
        for (i: Int in blocks.indices) blocks[i] = (EnumEuropaBlocks.byMetadata(i))!!.getName()

        addVariant("europablocks", "", *blocks)

        blocks = arrayOfNulls<String>(EnumTitanBlocks.entries.toTypedArray().size)
        for (i: Int in blocks.indices) blocks[i] = (EnumTitanBlocks.byMetadata(i))!!.getName()

        addVariant("titanblocks", "", *blocks)

        blocks = arrayOfNulls<String>(EnumMirandaBlocks.entries.toTypedArray().size)
        for (i: Int in blocks.indices) blocks[i] = (EnumMirandaBlocks.byMetadata(i))!!.getName()

        addVariant("mirandablocks", "", *blocks)

        blocks = arrayOfNulls<String>(EnumTritonBlocks.entries.toTypedArray().size)
        for (i: Int in blocks.indices) blocks[i] = (EnumTritonBlocks.byMetadata(i))!!.getName()

        addVariant("tritonblocks", "", *blocks)

        blocks = arrayOfNulls<String>(EnumHaumeaBlocks.entries.toTypedArray().size)
        for (i: Int in blocks.indices) blocks[i] = (EnumHaumeaBlocks.byMetadata(i))!!.getName()

        addVariant("haumeablocks", "", *blocks)


        blocks = arrayOfNulls<String>(EnumBlockIce.entries.toTypedArray().size)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockIce.byMetadata(i))!!.getName()
        addVariant("surface_ice", "", *blocks)

        addVariant("machineframes", "", "basic_frame", "advance_frame", "modern_frame")

        addVariant("dungeon_blocks", "", "ceres_bricks", "io_bricks")

        //addVariant("advanced_landing_pad", "", "rocket_pad");

        //Fluids
        var sludge: Item = Item.getItemFromBlock(GSFluids.BLOCK_LEMETHANE)
        registerLiquid(sludge, (GSFluids.BLOCK_LEMETHANE)!!, "liquid_ethanemethane", LEMethaneLocation)

        sludge = Item.getItemFromBlock(GSFluids.BLOCK_HELIUM3)
        registerLiquid(sludge, (GSFluids.BLOCK_HELIUM3)!!, "liquid_helium", HeliumLocation)

        sludge = Item.getItemFromBlock(GSFluids.BLOCK_HELIUM_HYDROGEN)
        registerLiquid(sludge, (GSFluids.BLOCK_HELIUM_HYDROGEN)!!, "liquid_heliumhydrogen", HeliumHydrogenLocation)

        sludge = Item.getItemFromBlock(GSFluids.BLOCK_ETHANE)
        registerLiquid(sludge, (GSFluids.BLOCK_ETHANE)!!, "liquid_ethane", EthaneLocation)

        sludge = Item.getItemFromBlock(GSFluids.BLOCK_NATURE_GAS)
        registerLiquid(sludge, (GSFluids.BLOCK_NATURE_GAS)!!, "liquid_naturegas", NatureGasLocation)


        //Items
        addVariant("cobalt_sword", "tools/", "cobalt_sword")
        addVariant("cobalt_axe", "tools/", "cobalt_axe")
        addVariant("cobalt_pickaxe", "tools/", "cobalt_pickaxe")
        addVariant("cobalt_spade", "tools/", "cobalt_spade")
        addVariant("cobalt_hoe", "tools/", "cobalt_hoe")

        addVariant("plasma_sword", "tools/", "plasma_sword")
        addVariant("plasma_axe", "tools/", "plasma_axe")
        addVariant("plasma_pickaxe", "tools/", "plasma_pickaxe")

        addVariant("advanced_battery", "batteries/", "advanced_battery")
        addVariant("modern_battery", "batteries/", "modern_battery")
        addVariant("extra_battery", "batteries/", "extra_battery")
        addVariant("ultimate_battery", "batteries/", "ultimate_battery")

        addVariant("space_suit_head", "armor/", "space_suit_head")
        addVariant("space_suit_chest", "armor/", "space_suit_chest")
        addVariant("space_suit_legs", "armor/", "space_suit_legs")
        addVariant("space_suit_feet", "armor/", "space_suit_feet")

        addVariant("space_suit_light_head", "armor/", "space_suit_light_head")
        addVariant("space_suit_light_chest", "armor/", "space_suit_light_chest")
        addVariant("space_suit_light_legs", "armor/", "space_suit_light_legs")
        addVariant("space_suit_light_feet", "armor/", "space_suit_light_feet")

        addVariant("jetpack", "armor/", "jetpack")
        //addVariant("matter_manipulator", "tools/", "matter_manipulator");
        addVariant("geo_scanner", "tools/", "geo_scanner")
        addVariant("gas_extractor", "tools/", "gas_extractor")

        addVariant("cobalt_helmet", "armor/", "cobalt_helmet")
        addVariant("cobalt_chest", "armor/", "cobalt_chest")
        addVariant("cobalt_legs", "armor/", "cobalt_legs")
        addVariant("cobalt_boots", "armor/", "cobalt_boots")


        addVariant("gs_basic", "basic/", *ItemBasicGS.getEnumNames())
        addVariant("ingots", "ingots/", *ItemIngots.names)
        addVariant("nuggets", "nuggets/", *ItemIngots.names)
        addVariant("hdp", "hdp/", *ItemHeavyDutyPlates.names)
        addVariant("compressed_plates", "compressed_plates/", *ItemCompressedPlates.names)
        addVariant("rocket_modules", "rocket_modules/", *ItemRocketModules.names)
        addVariant("rocket_parts", "rocket_parts/", *ItemRocketParts.names)
        addVariant(
            "schematics",
            "schematics/",
            "schematic_cone",
            "schematic_body",
            "schematic_engine",
            "schematic_booster",
            "schematic_stabiliser",
            "schematic_oxygen_tank",
            "schematic_port_nuclear_reactor"
        )
        addVariant("dungeon_keys", "keys/", *ItemTierKeysChest.keys)
        addVariant("upgrades", "upgrades/", *ItemUpgrades.names)

        addVariant(
            "thermal_padding_t3",
            "thermal_padding/",
            "thermal_helm_t3",
            "thermal_chestplate_t3",
            "thermal_leggings_t3",
            "thermal_boots_t3"
        )
        addVariant(
            "thermal_padding_t4",
            "thermal_padding/",
            "thermal_helm_t4",
            "thermal_chestplate_t4",
            "thermal_leggings_t4",
            "thermal_boots_t4"
        )

        var modelResourceLocation: ModelResourceLocation =
            ModelResourceLocation("galaxyspace:rockets/rocket_t4", "inventory")
        for (i: Int in 0..4) {
            ModelLoader.setCustomModelResourceLocation(GSItems.ROCKET_TIER_4, i, modelResourceLocation)
        }

        modelResourceLocation = ModelResourceLocation("galaxyspace:rockets/rocket_t5", "inventory")
        for (i: Int in 0..4) {
            ModelLoader.setCustomModelResourceLocation(GSItems.ROCKET_TIER_5, i, modelResourceLocation)
        }

        modelResourceLocation = ModelResourceLocation("galaxyspace:rockets/rocket_t6", "inventory")
        for (i: Int in 0..4) {
            ModelLoader.setCustomModelResourceLocation(GSItems.ROCKET_TIER_6, i, modelResourceLocation)
        }

        modelResourceLocation = ModelResourceLocation("galaxyspace:rockets/rocket_cargo", "inventory")
        for (i: Int in 0..4) {
            ModelLoader.setCustomModelResourceLocation(GSItems.ROCKET_FLUID_CRAGO, i, modelResourceLocation)
        }

        modelResourceLocation = ModelResourceLocation("galaxyspace:mars_rover", "inventory")
        //for (int i = 0; i < 5; ++i)
        //{
        ModelLoader.setCustomModelResourceLocation(GSItems.MARS_ROVER, 0, modelResourceLocation)

        // }
        ModelLoader.setCustomModelResourceLocation(
            Item.getItemFromBlock(GSBlocks.DUNGEON_BLOCKS),
            0,
            ModelResourceLocation("galaxyspace:ceres_bricks", "inventory")
        )
        ModelLoader.setCustomModelResourceLocation(
            GSItems.JETPACK,
            0,
            ModelResourceLocation("galaxyspace:armor/jetpack", "inventory")
        )

        ModelLoader.setCustomModelResourceLocation(
            GSItems.MATTER_MANIPULATOR,
            0,
            ModelResourceLocation("galaxyspace:tools/matter_manipulator", "inventory")
        )

        for (list in AsmodeusCore.bodies) if ((list)!!.canRegister()) (list)!!.registerVariant()
    }


    override fun registerItemRenderer(item: Item?, meta: Int, id: String?) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation((item)!!.getRegistryName(), id))
    }

    override fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, otherInfo: Array<Any?>?) {
        GSEffectHandler.spawnParticle((particleID)!!, (position)!!, (motion)!!, *(otherInfo)!!)
    }

    override fun register_event(obj: Any?) {
        MinecraftForge.EVENT_BUS.register(obj)
    }

    companion object {
        @JvmField
        var GScapeMap: MutableMap<String?, String?> = HashMap<String?, String?>()

        @JvmField
        var GScapesMap: MutableMap<String?, ResourceLocation?> = Maps.newHashMap<String?, ResourceLocation?>()

        @JvmField
        var mc: Minecraft? = FMLClientHandler.instance().getClient()

        //public static MusicTicker.MusicType GS_MUSIC;
        private val LEMethaneLocation =
            ModelResourceLocation(GalaxySpace.TEXTURE_PREFIX + "liquid_ethanemethane", "fluid")
        private val HeliumLocation = ModelResourceLocation(GalaxySpace.TEXTURE_PREFIX + "liquid_helium", "fluid")
        private val HeliumHydrogenLocation =
            ModelResourceLocation(GalaxySpace.TEXTURE_PREFIX + "liquid_heliumhydrogen", "fluid")
        private val EthaneLocation = ModelResourceLocation(GalaxySpace.TEXTURE_PREFIX + "liquid_ethane", "fluid")
        private val NatureGasLocation = ModelResourceLocation(GalaxySpace.TEXTURE_PREFIX + "liquid_naturegas", "fluid")

        private val glow_blocks: MutableMap<String?, BakedModelGlowBlockOverlay?> =
            HashMap<String?, BakedModelGlowBlockOverlay?>()

        @JvmStatic
        fun addVariant(name: String, folder: String?, vararg variants: String?) {
            val itemBlockVariants: Item? = Item.REGISTRY.getObject(ResourceLocation(GalaxySpace.MODID, name))
            val variants0: Array<ResourceLocation?> = arrayOfNulls<ResourceLocation>(variants.size)
            for (i: Int in variants.indices) {
                variants0[i] = ResourceLocation(GalaxySpace.MODID + ":" + folder + variants[i])
            }
            ModelBakery.registerItemVariants(itemBlockVariants, *variants0)
        }

        @JvmStatic
        fun registerLiquid(sludge: Item, liquid_block: Block, liquid_name: String?, model: ModelResourceLocation) {
            ModelBakery.registerItemVariants(sludge, ResourceLocation(GalaxySpace.TEXTURE_PREFIX + liquid_name))
            ModelLoader.setCustomMeshDefinition(
                sludge,
                IItemMeshDefinitionCustom.create(IItemMeshDefinitionCustom { stack: ItemStack? -> model })
            )
            ModelLoader.setCustomStateMapper(liquid_block, object : StateMapperBase() {
                override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
                    return model
                }
            })
        }

        @JvmStatic
        fun registerEntityRenderers() {
            RenderingRegistry.registerEntityRenderingHandler<EntityTier4Rocket>(
                EntityTier4Rocket::class.java,
                IRenderFactory { manager: RenderManager? -> RenderRockets((manager)!!, "tier4rocket", "Base") })
            //RenderingRegistry.registerEntityRenderingHandler(EntityTier4Rocket.class, (RenderManager manager) -> new RenderRockets(manager, "tier4rocketGS", "Base", "NoseCone", "Rocket", "Booster1", "Booster2", "Booster3", "Booster4"));
            RenderingRegistry.registerEntityRenderingHandler<EntityTier5Rocket>(
                EntityTier5Rocket::class.java,
                IRenderFactory { manager: RenderManager? -> RenderRockets((manager)!!, "tier5rocket", "Base") })
            RenderingRegistry.registerEntityRenderingHandler<EntityTier6Rocket>(
                EntityTier6Rocket::class.java,
                IRenderFactory { manager: RenderManager? -> RenderRockets((manager)!!, "tier6rocket", "Base") })
            RenderingRegistry.registerEntityRenderingHandler<EntityMultiSeatRocketTest>(
                EntityMultiSeatRocketTest::class.java,
                IRenderFactory { manager: RenderManager? -> RenderRockets((manager)!!, "tier4rocket", "Base") })
            RenderingRegistry.registerEntityRenderingHandler<EntityCustomCargoRocket>(
                EntityCustomCargoRocket::class.java,
                IRenderFactory { manager: RenderManager? -> RenderCargoRockets((manager)!!, "rockets/rocket_cargo") })
            RenderingRegistry.registerEntityRenderingHandler<EntityBossBlaze>(
                EntityBossBlaze::class.java,
                IRenderFactory { manager: RenderManager? -> RenderBossBlaze((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityIceSpike>(
                EntityIceSpike::class.java,
                IRenderFactory { manager: RenderManager? -> RenderIceSpike((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityEvolvedColdBlaze>(
                EntityEvolvedColdBlaze::class.java,
                IRenderFactory { manager: RenderManager? -> RenderEvolvedColdBlaze((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityBossGhast>(
                EntityBossGhast::class.java,
                IRenderFactory { manager: RenderManager? -> RenderBossGhast((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityMarsRover>(
                EntityMarsRover::class.java,
                IRenderFactory { manager: RenderManager? -> RenderMarsRover((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityAstroWolf>(
                EntityAstroWolf::class.java,
                IRenderFactory { manager: RenderManager? -> RenderAstroWolf((manager)!!) })

            //RenderingRegistry.registerEntityRenderingHandler(EntityLaserBeam.class, (RenderManager manager) -> new RenderLaserBeam(manager));
        }
    }
}
