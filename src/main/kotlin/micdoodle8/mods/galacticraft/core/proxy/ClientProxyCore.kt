package micdoodle8.mods.galacticraft.core.proxy

import java.io.IOException
import com.google.common.collect.Sets
import com.google.common.collect.Lists
import api.player.client.ClientPlayerAPI
import javax.vecmath.Quat4f
import com.google.common.collect.ImmutableList
import java.io.BufferedReader
import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.api.client.IItemMeshDefinitionCustom
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabVanilla
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedDetector
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedRedstone
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedRepeater
import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed
import micdoodle8.mods.galacticraft.core.blocks.BlockFallenMeteor
import micdoodle8.mods.galacticraft.core.blocks.BlockGrating
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenDetector
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass
import micdoodle8.mods.galacticraft.core.client.DynamicTextureProper
import micdoodle8.mods.galacticraft.core.client.EventHandlerClient
import micdoodle8.mods.galacticraft.core.client.fx.EffectHandler
import micdoodle8.mods.galacticraft.core.client.gui.screen.InventoryTabGalacticraft
import micdoodle8.mods.galacticraft.core.client.model.ModelRocketTier1
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.client.model.block.ModelGrating
import micdoodle8.mods.galacticraft.core.client.model.block.ModelPanelLightBase
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderAlienVillager
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderBuggy
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEntityFake
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedCreeper
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedEnderman
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedSkeletonBoss
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedSpider
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedWitch
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderEvolvedZombie
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderFlag
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderLander
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderMeteor
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderMeteorChunk
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderParaChest
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderTier1Rocket
import micdoodle8.mods.galacticraft.core.client.render.item.ItemModelBuggy
import micdoodle8.mods.galacticraft.core.client.render.item.ItemModelFlag
import micdoodle8.mods.galacticraft.core.client.render.item.ItemModelRocket
import micdoodle8.mods.galacticraft.core.client.render.item.ItemModelWorkbench
import micdoodle8.mods.galacticraft.core.client.render.item.TextureDungeonFinder
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityArclampRenderer
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityDishRenderer
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityEmergencyBoxRenderer
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityFluidPipeRenderer
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityFluidTankRenderer
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityPanelLightRenderer
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityPlatformRenderer
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityScreenRenderer
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntitySolarPanelRenderer
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityTreasureChestRenderer
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.client.sounds.MusicTickerGC
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
import micdoodle8.mods.galacticraft.core.entities.EntityLander
import micdoodle8.mods.galacticraft.core.entities.EntityMeteor
import micdoodle8.mods.galacticraft.core.entities.EntityMeteorChunk
import micdoodle8.mods.galacticraft.core.entities.EntityParachest
import micdoodle8.mods.galacticraft.core.entities.EntitySkeletonBoss
import micdoodle8.mods.galacticraft.core.entities.EntityTier1Rocket
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerBaseSP
import micdoodle8.mods.galacticraft.core.entities.player.IPlayerClient
import micdoodle8.mods.galacticraft.core.entities.player.PlayerClient
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import micdoodle8.mods.galacticraft.core.items.ItemFood
import micdoodle8.mods.galacticraft.core.items.ItemSchematic
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.tile.TileEntityArclamp
import micdoodle8.mods.galacticraft.core.tile.TileEntityDish
import micdoodle8.mods.galacticraft.core.tile.TileEntityEmergencyBox
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidPipe
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidTank
import micdoodle8.mods.galacticraft.core.tile.TileEntityPanelLight
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlatform
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar
import micdoodle8.mods.galacticraft.core.tile.TileEntityTreasureChest
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.wrappers.BlockMetaList
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import micdoodle8.mods.galacticraft.core.wrappers.PartialCanister
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.MusicTicker
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.network.INetHandler
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.core.BlockPos
import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.minecraftforge.fml.client.registry.RenderingRegistry
import foundation.logging.FoundationLog as FMLLog
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.reflect.Field
import java.net.URL
import java.net.URLConnection
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.vecmath.Vector3f

open class ClientProxyCore : CommonProxyCore(), IResourceManagerReloadListener {
    override fun preInit() {
        registerEntityRenderers()
        ModelLoaderRegistry.registerLoader(OBJLoaderGC.instance)
        OBJLoaderGC.instance.addDomain(Constants.ASSET_PREFIX)

        if (CompatibilityManager.PlayerAPILoaded) {
            ClientPlayerAPI.register(Constants.MOD_ID_CORE, GCPlayerBaseSP::class.java)
        }
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun init(event: FMLInitializationEvent?) {
        MUSIC_TYPE_MARS = EnumHelper.addEnum<MusicTicker.MusicType?>(
            MusicTicker.MusicType::class.java,
            "MARS_JC",
            arrayOf<Class<*>>(SoundEvent::class.java, Integer.TYPE, Integer.TYPE),
            GCSounds.music,
            12000,
            24000
        )
        registerHandlers()
        registerTileEntityRenderers()
        updateCapeList()
        registerInventoryJsons()

        Minecraft.getInstance().getBlockColors().registerBlockColorHandler(
            IBlockColor { state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, tintIndex: Int ->
                BlockFallenMeteor.colorMultiplier(
                    world,
                    pos
                )
            }, GCBlocks.fallenMeteor
        )

        Minecraft.getInstance().getBlockColors().registerBlockColorHandler(
            IBlockColor { state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, tintIndex: Int ->
                val b: Block = state!!.getBlock()
                if (b is BlockSpaceGlass) b.color else 0xFFFFFF
            }, *arrayOf<Block?>(GCBlocks.spaceGlassVanilla, GCBlocks.spaceGlassClear, GCBlocks.spaceGlassStrong)
        )

        Minecraft.getInstance().getBlockColors().registerBlockColorHandler(
            IBlockColor { state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, tintIndex: Int ->
                if (world != null && pos != null) {
                    val tile: TileEntity? = world.getBlockEntity(pos)
                    if (tile is TileEntityPanelLight) {
                        val baseState: IBlockState = tile.getBaseBlock()
                        return@IBlockColor Minecraft.getInstance().getBlockColors()
                            .colorMultiplier(baseState, world, pos, tintIndex)
                    }
                }
                0xFFFFFF
            }, GCBlocks.panelLighting
        )
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        registerInventoryTabs()
        ItemSchematic.registerTextures()

        MinecraftForge.EVENT_BUS.register(TabRegistry())

        if (!CompatibilityManager.RenderPlayerAPILoaded) {
            try {
                var field: Field =
                    RenderManager::class.java.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "playerRenderer" else "field_178637_m")
                field.setAccessible(true)
                field.set(Minecraft.getInstance().getRenderManager(), RenderPlayerGC())

                field =
                    RenderManager::class.java.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "skinMap" else "field_178636_l")
                field.setAccessible(true)
                val skinMap: MutableMap<String?, RenderPlayer?> = field.get(
                    Minecraft.getInstance().getRenderManager()
                ) as MutableMap<String?, RenderPlayer?>
                skinMap.put("default", RenderPlayerGC((skinMap.get("default"))!!, false))
                skinMap.put("slim", RenderPlayerGC((skinMap.get("slim"))!!, true))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        (Minecraft.getInstance().getResourceManager() as IReloadableResourceManager).registerReloadListener(this)

        try {
            val ftc: Field =
                Minecraft.getInstance().javaClass.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "mcMusicTicker" else "field_147126_aw")
            ftc.setAccessible(true)
            ftc.set(Minecraft.getInstance(), MusicTickerGC(Minecraft.getInstance()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        var lang: String? = Minecraft.getInstance().gameSettings.language
        GCLog.debug("Reloading entity names for language " + lang)
        if (lang == null) {
            lang = "en_US"
        }
        (GalacticraftCore.instance)!!.loadLanguageCore(lang)
        if (GalacticraftCore.isPlanetsLoaded && !GCCoreUtil.langDisable) {
            (GalacticraftPlanets.instance)!!.loadLanguagePlanets(lang)
        }
    }

    override fun postRegisterItem(item: Item?) {
        if (!(item)!!.getHasSubtypes()) {
            itemsToRegisterJson.add(item)
        }
    }

    override fun registerVariants() {
        addVariants()

        val fuel: Item = Item.getItemFromBlock(GCBlocks.fuel)
        ModelBakery.registerItemVariants(fuel, ResourceLocation.parse("galacticraftcore:fuel"))
        ModelLoader.setCustomMeshDefinition(
            fuel,
            IItemMeshDefinitionCustom.create(IItemMeshDefinitionCustom { stack: ItemStack? -> fuelLocation })
        )
        ModelLoader.setCustomStateMapper(GCBlocks.fuel, object : StateMapperBase() {
            override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
                return fuelLocation
            }
        })
        val oil: Item = Item.getItemFromBlock(GCBlocks.crudeOil)
        ModelBakery.registerItemVariants(oil, ResourceLocation.parse("galacticraftcore:oil"))
        ModelLoader.setCustomMeshDefinition(
            oil,
            IItemMeshDefinitionCustom.create(IItemMeshDefinitionCustom { stack: ItemStack? -> oilLocation })
        )
        ModelLoader.setCustomStateMapper(GCBlocks.crudeOil, object : StateMapperBase() {
            override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
                return oilLocation
            }
        })

        val nasaWorkbench: Item = Item.getItemFromBlock(GCBlocks.nasaWorkbench)
        var modelResourceLocation: ModelResourceLocation =
            ModelResourceLocation("galacticraftcore:rocket_workbench", "inventory")
        ModelLoader.setCustomModelResourceLocation(nasaWorkbench, 0, modelResourceLocation)

        modelResourceLocation = ModelResourceLocation("galacticraftcore:rocket_t1", "inventory")
        for (i: Int in 0..4) {
            ModelLoader.setCustomModelResourceLocation(GCItems.rocketTier1, i, modelResourceLocation)
        }

        for (i: Int in 0..3) {
            modelResourceLocation =
                ModelResourceLocation("galacticraftcore:buggy" + (if (i > 0) "_" + i else ""), "inventory")
            ModelLoader.setCustomModelResourceLocation(GCItems.buggy, i, modelResourceLocation)
        }

        //        for (PartialCanister container : ClientProxyCore.canisters)
//        {
//            modelResourceLocation = new ModelResourceLocation(container.getModID() + ":" + container.getBaseName() + "_0", "inventory");
//            for (int i = 0; i < container.getItem().getMaxDamage(); ++i)
//            {
//                ModelLoader.setCustomModelResourceLocation(container.getItem(), i, modelResourceLocation);
//            }
//        }
        modelResourceLocation = ModelResourceLocation("galacticraftcore:flag", "inventory")
        ModelLoader.setCustomModelResourceLocation(GCItems.flag, 0, modelResourceLocation)
        ModelLoader.setCustomStateMapper(
            GCBlocks.oxygenDetector,
            StateMap.Builder().ignore(BlockOxygenDetector.ACTIVE).build()
        )
        ModelLoader.setCustomStateMapper(
            GCBlocks.panelLighting,
            StateMap.Builder().ignore(BlockPanelLighting.TYPE).build()
        )
        ModelLoader.setCustomStateMapper(
            GCBlocks.grating,
            StateMap.Builder().ignore(BlockLiquid.LEVEL).ignore(BlockFluidBase.LEVEL).build()
        )
        BlockGrating.remapVariant((GCBlocks.gratingWater)!!)
        BlockGrating.remapVariant((GCBlocks.gratingLava)!!)
        BlockGrating.remapForgeVariants()
        ModelLoader.setCustomStateMapper(
            GCBlocks.concealedRedstone,
            StateMap.Builder().ignore(BlockConcealedRedstone.POWER).build()
        )
        ModelLoader.setCustomStateMapper(
            GCBlocks.concealedRepeater_Powered,
            StateMap.Builder()
                .ignore(net.minecraft.block.BlockHorizontal.FACING, net.minecraft.block.BlockRedstoneRepeater.DELAY, net.minecraft.block.BlockRedstoneRepeater.LOCKED)
                .build()
        )
        ModelLoader.setCustomStateMapper(
            GCBlocks.concealedRepeater_Unpowered,
            StateMap.Builder()
                .ignore(net.minecraft.block.BlockHorizontal.FACING, net.minecraft.block.BlockRedstoneRepeater.DELAY, net.minecraft.block.BlockRedstoneRepeater.LOCKED)
                .build()
        )
        ModelLoader.setCustomStateMapper(
            GCBlocks.concealedDetector,
            StateMap.Builder().ignore(BlockConcealedDetector.FACING, BlockConcealedDetector.DETECTED).build()
        )
    }

    override fun getClientWorld(): World? {
        return mc.world
    }

    override fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, otherInfo: Array<Any?>?) {
        EffectHandler.spawnParticle((particleID)!!, (position)!!, (motion)!!, *(otherInfo)!!)
    }

    override fun getWorldForID(dimensionID: Int): World? {
        if (GCCoreUtil.getEffectiveSide() == Side.SERVER) {
            return WorldUtil.getWorldForDimensionServer(dimensionID)
        }

        val world: World? = mc.world

        if (world != null && GCCoreUtil.getDimensionID(world) == dimensionID) {
            return world
        }

        return null
    }

    override fun getPlayerFromNetHandler(handler: INetHandler?): EntityPlayer? {
        if (handler is NetHandlerPlayServer) {
            return handler.player
        } else {
            return FMLClientHandler.instance().getClientPlayerEntity()
        }
    }

    override fun unregisterNetwork(fluidNetwork: FluidNetwork?) {
        super.unregisterNetwork(fluidNetwork)

        if (!GCCoreUtil.getEffectiveSide().isServer()) {
            TickHandlerClient.Companion.removeFluidNetwork(fluidNetwork)
        }
    }

    override fun registerNetwork(fluidNetwork: FluidNetwork?) {
        super.registerNetwork(fluidNetwork)

        if (!GCCoreUtil.getEffectiveSide().isServer()) {
            TickHandlerClient.Companion.addFluidNetwork((fluidNetwork)!!)
        }
    }

    override fun isPaused(): Boolean {
        if (Minecraft.getInstance().isSingleplayer() && !Minecraft.getInstance()
                .getIntegratedServer()!!.getPublic()
        ) {
            val screen: GuiScreen? = Minecraft.getInstance().gui.screen()

            if (screen != null) {
                return screen.doesGuiPauseGame()
            }
        }

        return false
    }

    @SubscribeEvent
    open fun onTextureStitchedPre(event: TextureStitchEvent.Pre) {
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:blocks/assembly"))
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:model/rocket_t1"))
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:model/buggy_main"))
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:model/buggy_storage"))
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:model/buggy_wheels"))
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:model/flag0"))
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:model/frequency_module"))
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:blocks/fluids/oxygen_gas"))
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:blocks/fluids/hydrogen_gas"))
        event.getMap().registerSprite(ResourceLocation.parse("galacticraftcore:blocks/bubble"))
        TextureDungeonFinder("galacticraftcore:items/dungeonfinder").register(event.getMap())
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    open fun onModelBakeEvent(event: ModelBakeEvent) {
        //Specified transformations only take effect on the "inventory" variant, not other variants.
        val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(30f, 225f, 0f))
        replaceModelDefault(
            event,
            "rocket_workbench",
            "block/workbench.obj",
            ImmutableList.of<String?>("Cube"),
            ItemModelWorkbench::class.java,
            TRSRTransformation(Vector3f(0.7f, 0.1f, 0.0f), rot, Vector3f(0.2604f, 0.2604f, 0.2604f), Quat4f()),
            "inventory",
            "normal"
        )
        replaceModelDefault(
            event,
            "rocket_t1",
            "rocket_t1.obj",
            ImmutableList.of<String?>("Rocket"),
            ItemModelRocket::class.java,
            TRSRTransformation.identity()
        )

        for (i: Int in 0..3) {
            var objects: ImmutableList<String?>? = ImmutableList.of<String?>(
                "MainBody",
                "RadarDish_Dish",
                "Wheel_Back_Left",
                "Wheel_Back_Right",
                "Wheel_Front_Left",
                "Wheel_Front_Right"
            )
            when (i) {
                0 -> {}
                1 -> objects = ImmutableList.of<String?>(
                    "MainBody",
                    "RadarDish_Dish",
                    "Wheel_Back_Left",
                    "Wheel_Back_Right",
                    "Wheel_Front_Left",
                    "Wheel_Front_Right",
                    "CargoLeft"
                )

                2 -> objects = ImmutableList.of<String?>(
                    "MainBody",
                    "RadarDish_Dish",
                    "Wheel_Back_Left",
                    "Wheel_Back_Right",
                    "Wheel_Front_Left",
                    "Wheel_Front_Right",
                    "CargoLeft",
                    "CargoMid"
                )

                3 -> objects = ImmutableList.of<String?>(
                    "MainBody",
                    "RadarDish_Dish",
                    "Wheel_Back_Left",
                    "Wheel_Back_Right",
                    "Wheel_Front_Left",
                    "Wheel_Front_Right",
                    "CargoLeft",
                    "CargoMid",
                    "CargoRight"
                )
            }
            replaceModelDefault(
                event,
                "buggy" + (if (i > 0) "_" + i else ""),
                "buggy_inv.obj",
                objects,
                ItemModelBuggy::class.java,
                TRSRTransformation.identity()
            )
        }

        replaceModelDefault(
            event,
            "flag",
            "flag.obj",
            ImmutableList.of<String?>("Flag", "Pole"),
            ItemModelFlag::class.java,
            TRSRTransformation.identity()
        )
        var blockLoc: ModelResourceLocation =
            ModelResourceLocation(Constants.ASSET_PREFIX + ":panel_lighting", "normal")
        var defaultLoc: ModelResourceLocation
        if (GalacticraftCore.isPlanetsLoaded) {
            defaultLoc = ModelResourceLocation(
                GalacticraftPlanets.ASSET_PREFIX + ":asteroids_block",
                "basictypeasteroids=asteroid_deco"
            )
        } else {
            defaultLoc = ModelResourceLocation(Constants.ASSET_PREFIX + ":basic_block_core", "basictype=deco_block_1")
        }
        event.getModelRegistry().putObject(blockLoc, ModelPanelLightBase(defaultLoc))
        defaultLoc = ModelResourceLocation(Constants.ASSET_PREFIX + ":grating", "normal")
        event.getModelRegistry().putObject(defaultLoc, ModelGrating(defaultLoc, event.getModelManager()))
        for (i: Int in 1..<BlockGrating.number) {
            blockLoc = ModelResourceLocation(Constants.ASSET_PREFIX + ":grating" + i, "normal")
            event.getModelRegistry().putObject(blockLoc, ModelGrating(defaultLoc, event.getModelManager()))
        }
        //
//        for (PartialCanister container : ClientProxyCore.canisters)
//        {
//            for (int i = 0; i < container.getTextureCount(); ++i)
//            {
//                ModelResourceLocation modelResourceLocation = new ModelResourceLocation(container.getModID() + ":" + container.getBaseName() + "_" + i, "inventory");
//                IBakedModel object = event.modelRegistry.getObject(modelResourceLocation);
//                ItemLiquidCanisterModel modelFinal = new ItemLiquidCanisterModel(object);
//                event.modelRegistry.putObject(modelResourceLocation, modelFinal);
//            }
//        }
    }

    /**
     * Specified parentState transformations only take effect on the "inventory" variant, not other variants
     * Make sure that identity gives the correct model for other variants!
     * Used for example by the NASA Workbench: transform the model for inventory but not for normal model
     */
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
            Constants.ASSET_PREFIX,
            (event)!!,
            resLoc,
            objLoc,
            (visibleGroups)!!,
            clazz,
            (parentState)!!,
            *(variants as Array<out String>)
        )
    }

    override fun registerCanister(container: PartialCanister?) {
        canisters.add((container)!!)
    }

    override fun registerFluidTexture(fluid: Fluid?, submergedTexture: ResourceLocation?) {
        submergedTextures.put(fluid, submergedTexture)
    }

    open class EventSpecialRender constructor(@JvmField val partialTicks: Float) : Event()

    override fun getGearData(player: EntityPlayer): PlayerGearData? {
        val gearData: PlayerGearData? = playerItemData.get(player.getName())

        if (gearData == null) {
            val id: String = (PlayerUtil.getName(player))!!

            if (!gearDataRequests.contains(id)) {
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_REQUEST_GEAR_DATA,
                        GCCoreUtil.getDimensionID(player.level()),
                        arrayOf<Any?>(id)
                    )
                )
                gearDataRequests.add(id)
            }
        }

        return gearData
    }

    companion object {
        @JvmField
        var flagRequestsSent: MutableList<String?> = ArrayList<String?>()

        @JvmField
        var valueableBlocks: MutableSet<BlockVec3?> = Sets.newHashSet<BlockVec3?>()

        @JvmField
        var detectableBlocks: HashSet<BlockMetaList?> = Sets.newHashSet<BlockMetaList?>()

        @JvmField
        var leakTrace: MutableList<BlockVec3?>? = null

        @JvmField
        var playerItemData: MutableMap<String?, PlayerGearData?> = Maps.newHashMap<String?, PlayerGearData?>()

        @JvmField
        var playerPosX: Double = 0.0

        @JvmField
        var playerPosY: Double = 0.0

        @JvmField
        var playerPosZ: Double = 0.0

        @JvmField
        var playerRotationYaw: Float = 0f

        @JvmField
        var playerRotationPitch: Float = 0f

        @JvmField
        var lastSpacebarDown: Boolean = false

        @JvmField
        var clientSpaceStationID: HashMap<Int,Int> = Maps.newHashMap<Int,Int>()

        @JvmField
        var MUSIC_TYPE_MARS: MusicTicker.MusicType? = null

        @JvmField
        var galacticraftItem: EnumRarity? = EnumHelper.addRarity("GCRarity", TextFormatting.BLUE, "Space")

        @JvmField
        var capeMap: MutableMap<String?, ResourceLocation?> = HashMap<String?, ResourceLocation?>()

        @JvmField
        var dummyInventory: InventoryExtended = InventoryExtended()

        @JvmField
        var submergedTextures: MutableMap<Fluid?, ResourceLocation?> = Maps.newHashMap<Fluid?, ResourceLocation?>()

        @JvmField
        var playerClientHandler: IPlayerClient = PlayerClient()

        @JvmField
        var mc: Minecraft = Minecraft.getInstance()

        @JvmField
        var gearDataRequests: MutableList<String?> = Lists.newArrayList<String?>()

        @JvmField
        var overworldTextureClient: DynamicTextureProper? = null

        @JvmField
        var overworldTextureWide: DynamicTextureProper? = null

        @JvmField
        var overworldTextureLarge: DynamicTextureProper? = null

        @JvmField
        var overworldTextureRequestSent: Boolean = false

        @JvmField
        var overworldTexturesValid: Boolean = false

        @JvmField
        var PLAYER_Y_OFFSET: Float = 1.6200000047683716f

        @JvmField
        var playerHead: ResourceLocation? = null

        @JvmField
        val saturnRingTexture: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/saturn_rings.png")

        @JvmField
        val uranusRingTexture: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/uranus_rings.png")
        private val itemsToRegisterJson: MutableList<Item?> = Lists.newArrayList<Item?>()
        private val fuelLocation = ModelResourceLocation(Constants.TEXTURE_PREFIX + "fuel", "fluid")
        private val oilLocation = ModelResourceLocation(Constants.TEXTURE_PREFIX + "oil", "fluid")
        private val canisters: MutableList<PartialCanister> = Lists.newArrayList<PartialCanister>()

        @JvmStatic
        fun registerEntityRenderers() {
            RenderingRegistry.registerEntityRenderingHandler<EntityTier1Rocket>(
                EntityTier1Rocket::class.java,
                IRenderFactory { manager: RenderManager? ->
                    RenderTier1Rocket(
                        (manager)!!,
                        ModelRocketTier1(),
                        Constants.ASSET_PREFIX,
                        "rocket_t1"
                    )
                })
            RenderingRegistry.registerEntityRenderingHandler<EntityEvolvedSpider>(
                EntityEvolvedSpider::class.java,
                IRenderFactory { manager: RenderManager? -> RenderEvolvedSpider((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityEvolvedZombie>(
                EntityEvolvedZombie::class.java,
                IRenderFactory { manager: RenderManager? -> RenderEvolvedZombie((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityEvolvedCreeper>(
                EntityEvolvedCreeper::class.java,
                IRenderFactory { manager: RenderManager? -> RenderEvolvedCreeper((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityEvolvedSkeleton>(
                EntityEvolvedSkeleton::class.java,
                IRenderFactory { manager: RenderManager? -> RenderEvolvedSkeleton((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntitySkeletonBoss>(
                EntitySkeletonBoss::class.java,
                IRenderFactory { manager: RenderManager? -> RenderEvolvedSkeletonBoss((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityMeteor>(
                EntityMeteor::class.java,
                IRenderFactory { manager: RenderManager? -> RenderMeteor((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityFlag>(
                EntityFlag::class.java,
                IRenderFactory { manager: RenderManager? -> RenderFlag((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityParachest>(
                EntityParachest::class.java,
                IRenderFactory { manager: RenderManager? -> RenderParaChest((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityAlienVillager>(
                EntityAlienVillager::class.java,
                IRenderFactory { manager: RenderManager? -> RenderAlienVillager((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityLander>(
                EntityLander::class.java,
                IRenderFactory { manager: RenderManager? -> RenderLander((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityCelestialFake>(
                EntityCelestialFake::class.java,
                IRenderFactory { manager: RenderManager? -> RenderEntityFake((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityBuggy>(
                EntityBuggy::class.java,
                IRenderFactory { manager: RenderManager? -> RenderBuggy((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityMeteorChunk>(
                EntityMeteorChunk::class.java,
                IRenderFactory { manager: RenderManager? -> RenderMeteorChunk((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityEvolvedEnderman>(
                EntityEvolvedEnderman::class.java,
                IRenderFactory { manager: RenderManager? -> RenderEvolvedEnderman((manager)!!) })
            RenderingRegistry.registerEntityRenderingHandler<EntityEvolvedWitch>(
                EntityEvolvedWitch::class.java,
                IRenderFactory { manager: RenderManager? -> RenderEvolvedWitch((manager)!!) })
        }

        @JvmStatic
        private fun registerHandlers() {
            val tickHandlerClient: TickHandlerClient = TickHandlerClient()
            MinecraftForge.EVENT_BUS.register(tickHandlerClient)
            MinecraftForge.EVENT_BUS.register(KeyHandlerClient())
            ClientRegistry.registerKeyBinding(KeyHandlerClient.Companion.galaxyMap)
            ClientRegistry.registerKeyBinding(KeyHandlerClient.Companion.openFuelGui)
            ClientRegistry.registerKeyBinding(KeyHandlerClient.Companion.toggleAdvGoggles)
            MinecraftForge.EVENT_BUS.register(EventHandlerClient())
        }

        @JvmStatic
        private fun registerTileEntityRenderers() {
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntityTreasureChest>(
                TileEntityTreasureChest::class.java,
                TileEntityTreasureChestRenderer()
            )
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntitySolar>(
                TileEntitySolar::class.java,
                TileEntitySolarPanelRenderer()
            )
            //        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOxygenDistributor.class, new TileEntityBubbleProviderRenderer<>(0.25F, 0.25F, 1.0F));
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntityScreen>(
                TileEntityScreen::class.java,
                TileEntityScreenRenderer()
            )
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntityFluidTank>(
                TileEntityFluidTank::class.java,
                TileEntityFluidTankRenderer()
            )
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntityFluidPipe>(
                TileEntityFluidPipe::class.java,
                TileEntityFluidPipeRenderer()
            )
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntityDish>(
                TileEntityDish::class.java,
                TileEntityDishRenderer()
            )
            //            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThruster.class, new TileEntityThrusterRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntityArclamp>(
                TileEntityArclamp::class.java,
                TileEntityArclampRenderer()
            )
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntityPanelLight>(
                TileEntityPanelLight::class.java,
                TileEntityPanelLightRenderer()
            )
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntityPlatform>(
                TileEntityPlatform::class.java,
                TileEntityPlatformRenderer()
            )
            ClientRegistry.bindTileEntitySpecialRenderer<TileEntityEmergencyBox>(
                TileEntityEmergencyBox::class.java,
                TileEntityEmergencyBoxRenderer()
            )
            //            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidPipe.class, new TileEntityOxygenPipeRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOxygenStorageModule.class, new TileEntityMachineRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCircuitFabricator.class, new TileEntityMachineRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElectricIngotCompressor.class, new TileEntityMachineRenderer());
        }

        @JvmStatic
        private fun registerInventoryJsons() {
            for (toReg: Item? in itemsToRegisterJson) {
                ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (toReg)!!)
            }

            ClientUtil.registerItemJson(
                Constants.TEXTURE_PREFIX,
                (GCItems.canister)!!,
                0,
                "canister"
            ) //This was canister_tin
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.canister)!!, 1, "canister_copper")
            ClientUtil.registerItemJson(
                Constants.TEXTURE_PREFIX,
                (GCItems.rocketEngine)!!,
                0,
                "engine"
            ) //This was tier1engine
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.rocketEngine)!!, 1, "tier1booster")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 0, "parachute")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 1, "parachute_black")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 2, "parachute_blue")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 3, "parachute_lime")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 4, "parachute_brown")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 5, "parachute_darkblue")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 6, "parachute_darkgray")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 7, "parachute_darkgreen")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 8, "parachute_gray")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 9, "parachute_magenta")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 10, "parachute_orange")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 11, "parachute_pink")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 12, "parachute_purple")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 13, "parachute_red")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 14, "parachute_teal")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.parachute)!!, 15, "parachute_yellow")
            ClientUtil.registerItemJson(
                Constants.TEXTURE_PREFIX,
                (GCItems.schematic)!!,
                0,
                "schematic"
            ) //This was schematic_buggy
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.schematic)!!, 1, "schematic_rocket_t2")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.key)!!, 0, "key")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.partBuggy)!!, 0, "buggymat") //This was wheel
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.partBuggy)!!, 1, "seat")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.partBuggy)!!, 2, "storage")
            ClientUtil.registerItemJson(
                Constants.TEXTURE_PREFIX,
                (GCItems.basicItem)!!,
                0,
                "basic_item"
            ) //This was solar_module_0
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 1, "solar_module_1")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 2, "raw_silicon")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 3, "ingot_copper")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 4, "ingot_tin")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 5, "ingot_aluminum")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 6, "compressed_copper")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 7, "compressed_tin")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 8, "compressed_aluminum")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 9, "compressed_steel")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 10, "compressed_bronze")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 11, "compressed_iron")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 12, "wafer_solar")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 13, "wafer_basic")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 14, "wafer_advanced")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 15, "food")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 16, "dehydrated_carrot")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 17, "dehydrated_melon")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 18, "dehydrated_potato")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 19, "frequency_module")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.basicItem)!!, 20, "ambient_thermal_controller")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.foodItem)!!, 0, "food")
            for (j: Int in 1..<ItemFood.names.size) {
                ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.foodItem)!!, j, ItemFood.names[j])
            }
            ClientUtil.registerItemJson(
                Constants.TEXTURE_PREFIX,
                (GCItems.itemBasicMoon)!!,
                0,
                "item_basic_moon"
            ) //This was meteoric_iron_ingot
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.itemBasicMoon)!!, 1, "compressed_meteoric_iron")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.itemBasicMoon)!!, 2, "lunar_sapphire")
            if (CompatibilityManager.useAluDust()) ClientUtil.registerItemJson(
                Constants.TEXTURE_PREFIX,
                (GCItems.ic2compat)!!,
                0,
                "ic2compat"
            )

            for (container: PartialCanister in canisters) {
                for (i: Int in 0..(container.getItem())!!.getMaxDamage()) {
                    val levels: Int = container.getTextureCount() - 1
                    val fillLevel: Int = levels - levels * i / (container.getItem())!!.getMaxDamage()
                    ClientUtil.registerItemJson(
                        container.getModID() + ":",
                        (container.getItem())!!,
                        i,
                        container.getBaseName() + (if (fillLevel > 0) "_" + fillLevel else "")
                    )
                }
            }

            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.meteorChunk)!!, 0, "meteor_chunk")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.meteorChunk)!!, 1, "meteor_chunk_hot")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.buggy)!!, 0, "buggy")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.buggy)!!, 1, "buggy_1")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.buggy)!!, 2, "buggy_2")
            ClientUtil.registerItemJson(Constants.TEXTURE_PREFIX, (GCItems.buggy)!!, 3, "buggy_3")

            // Blocks
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.breatheableAir)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.brightAir)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.brightBreatheableAir)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.brightLamp)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.treasureChestTier1)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.landingPad)!!, 0, "landing_pad")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.landingPad)!!, 1, "buggy_pad")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.unlitTorch)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.unlitTorchLit)!!)
            for (torch: Block? in GCBlocks.otherModTorchesLit) {
                ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (torch)!!)
            }
            for (torch: Block? in GCBlocks.otherModTorchesUnlit) {
                ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (torch)!!)
            }
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.oxygenDistributor)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.oxygenPipe)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.oxygenPipePull)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.oxygenCollector)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.oxygenCompressor)!!, 0, "oxygen_compressor")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.oxygenCompressor)!!, 4, "oxygen_decompressor")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.oxygenSealer)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.oxygenDetector)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.nasaWorkbench)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.radioTelescope)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.fallenMeteor)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 3, "deco_block_0")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 4, "deco_block_1")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 5, "ore_copper_gc")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 6, "ore_tin_gc")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 7, "ore_aluminum_gc")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 8, "ore_silicon")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 9, "block_copper_gc")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 10, "block_tin_gc")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 11, "block_aluminum_gc")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 12, "block_meteoric_iron_gc")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.basicBlock)!!, 13, "block_silicon_gc")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.airLockFrame)!!, 0, "air_lock_frame")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.airLockFrame)!!, 1, "air_lock_controller")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.airLockSeal)!!, 0, "air_lock_seal")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.spaceGlassClear)!!, 0, "space_glass_clear")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.spaceGlassVanilla)!!, 0, "space_glass_vanilla")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.spaceGlassStrong)!!, 0, "space_glass_strong")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.spaceGlassClear)!!, 1, "space_glass_tin_clear")
            ClientUtil.registerBlockJson(
                Constants.TEXTURE_PREFIX,
                (GCBlocks.spaceGlassVanilla)!!,
                1,
                "space_glass_tin_vanilla"
            )
            ClientUtil.registerBlockJson(
                Constants.TEXTURE_PREFIX,
                (GCBlocks.spaceGlassStrong)!!,
                1,
                "space_glass_tin_strong"
            )
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.crafting)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.refinery)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.fuelLoader)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.landingPadFull)!!, 0, "landing_pad_full")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.landingPadFull)!!, 1, "buggy_pad_full")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.spaceStationBase)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.fakeBlock)!!)
            for (type: BlockEnclosed.EnumEnclosedBlockType in micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed.EnumEnclosedBlockType.entries.toTypedArray()) {
                ClientUtil.registerBlockJson(
                    Constants.TEXTURE_PREFIX,
                    (GCBlocks.sealableBlock)!!,
                    type.getMeta(),
                    if (type == BlockEnclosed.EnumEnclosedBlockType.ALUMINUM_WIRE) "enclosed" else type.getName()
                )
            }
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.cargoLoader)!!, 0, "cargo")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.cargoLoader)!!, 4, "cargo_unloader")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.parachest)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.solarPanel)!!, 0, "solar")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.solarPanel)!!, 4, "advanced_solar")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineBase)!!, 0, "coal_generator")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineBase)!!, 12, "ingot_compressor")
            ClientUtil.registerBlockJson(
                Constants.TEXTURE_PREFIX,
                (GCBlocks.machineBase2)!!,
                0,
                "electric_ingot_compressor"
            )
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineBase2)!!, 4, "circuit_fabricator")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineBase2)!!, 8, "oxygen_storage_module")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineBase2)!!, 12, "deconstructor")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineTiered)!!, 0, "energy_storage")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineTiered)!!, 4, "electric_furnace")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineTiered)!!, 8, "cluster_storage")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineTiered)!!, 12, "arc_furnace")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.machineBase3)!!, 0, "painter")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.aluminumWire)!!, 0, "aluminum_wire")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.aluminumWire)!!, 1, "aluminum_wire_heavy")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.aluminumWire)!!, 2, "aluminum_wire_switch")
            ClientUtil.registerBlockJson(
                Constants.TEXTURE_PREFIX,
                (GCBlocks.aluminumWire)!!,
                3,
                "aluminum_wire_switch_heavy"
            )
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.panelLighting)!!, 0, "panel_lighting")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.panelLighting)!!, 1, "panel_lighting_1")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.panelLighting)!!, 2, "panel_lighting_2")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.panelLighting)!!, 3, "panel_lighting_3")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.panelLighting)!!, 4, "panel_lighting_4")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.platform)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.emergencyBox)!!, 0, "emergency_box")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.emergencyBox)!!, 1, "emergency_box_full")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.grating)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.glowstoneTorch)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.blockMoon)!!, 0, "ore_copper_moon")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.blockMoon)!!, 1, "ore_tin_moon")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.blockMoon)!!, 2, "ore_cheese_moon")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.blockMoon)!!, 3, "moon_dirt_moon")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.blockMoon)!!, 4, "basic_block_moon")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.blockMoon)!!, 5, "moon_turf")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.blockMoon)!!, 6, "ore_sapphire_moon")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.blockMoon)!!, 14, "moon_dungeon_brick")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.cheeseBlock)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.spinThruster)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.screen)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.telemetry)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.fluidTank)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.slabGCHalf)!!, 0, "slab_tin_1")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.slabGCHalf)!!, 1, "slab_tin_2")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.slabGCHalf)!!, 2, "slab_moon_stone")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.slabGCHalf)!!, 3, "slab_moon_dungeon_brick")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.slabGCHalf)!!, 4, "slab_mars_cobblestone")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.slabGCHalf)!!, 5, "slab_mars_dungeon_brick")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.slabGCHalf)!!, 6, "slab_asteroid")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.tinStairs1)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.tinStairs2)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.moonStoneStairs)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.moonBricksStairs)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.wallGC)!!, 0, "wall_tin_1")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.wallGC)!!, 1, "wall_tin_2")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.wallGC)!!, 2, "wall_moon_stone")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.wallGC)!!, 3, "wall_moon_dungeon_brick")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.wallGC)!!, 4, "wall_mars_cobblestone")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.wallGC)!!, 5, "wall_mars_dungeon_brick")
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.bossSpawner)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.concealedRedstone)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.concealedRepeater_Powered)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.concealedRepeater_Unpowered)!!)
            ClientUtil.registerBlockJson(Constants.TEXTURE_PREFIX, (GCBlocks.concealedDetector)!!)
            //TODO: doubleslabs, fluids - and all the remaining meta-dependent block models (e.g. machine, machine2) have no 'inventory' variant for the meta-less block...
        }

        @JvmStatic
        private fun addVariants() {
            //BlockItem variants: 
            addCoreVariant("air_lock_frame", "air_lock_frame", "air_lock_controller")
            addCoreVariant(
                "basic_block_core",
                "deco_block_0",
                "deco_block_1",
                "ore_copper_gc",
                "ore_tin_gc",
                "ore_aluminum_gc",
                "ore_silicon",
                "block_copper_gc",
                "block_tin_gc",
                "block_aluminum_gc",
                "block_meteoric_iron_gc",
                "block_silicon_gc"
            )
            addCoreVariant("air_lock_frame", "air_lock_frame", "air_lock_controller")
            addCoreVariant("landing_pad", "landing_pad", "buggy_pad")
            addCoreVariant("oxygen_compressor", "oxygen_compressor", "oxygen_decompressor")
            addCoreVariant("cargo", "cargo", "cargo_unloader")
            addCoreVariant(
                "enclosed",
                "enclosed_hv_cable",
                "enclosed_fluid_pipe",
                "enclosed_copper_cable",
                "enclosed_gold_cable",
                "enclosed_te_conduit",
                "enclosed_glass_fibre_cable",
                "enclosed_lv_cable",
                "enclosed_pipe_items_stone",
                "enclosed_pipe_items_cobblestone",
                "enclosed_pipe_fluids_stone",
                "enclosed_pipe_fluids_cobblestone",
                "enclosed_pipe_power_stone",
                "enclosed_pipe_power_gold",
                "enclosed_me_cable",
                "enclosed",
                "enclosed_heavy_aluminum_wire"
            )
            addCoreVariant("solar", "advanced_solar", "solar")
            addCoreVariant("machine", "coal_generator", "ingot_compressor")
            addCoreVariant(
                "machine2",
                "circuit_fabricator",
                "oxygen_storage_module",
                "electric_ingot_compressor",
                "deconstructor"
            )
            addCoreVariant("machine3", "painter")
            addCoreVariant("machine_tiered", "energy_storage", "electric_furnace", "cluster_storage", "arc_furnace")
            addCoreVariant(
                "basic_block_moon",
                "ore_copper_moon",
                "ore_tin_moon",
                "ore_cheese_moon",
                "moon_dirt_moon",
                "basic_block_moon",
                "moon_turf",
                "ore_sapphire_moon",
                "moon_dungeon_brick"
            )
            addCoreVariant(
                "aluminum_wire",
                "aluminum_wire",
                "aluminum_wire_heavy",
                "aluminum_wire_switch",
                "aluminum_wire_switch_heavy"
            )
            addCoreVariant(
                "slab_gc_half",
                "slab_tin_1",
                "slab_tin_2",
                "slab_moon_stone",
                "slab_moon_dungeon_brick",
                "slab_mars_cobblestone",
                "slab_mars_dungeon_brick",
                "slab_asteroid"
            )
            addCoreVariant(
                "wall_gc",
                "wall_tin_1",
                "wall_tin_2",
                "wall_moon_stone",
                "wall_moon_dungeon_brick",
                "wall_mars_cobblestone",
                "wall_mars_dungeon_brick"
            )
            addCoreVariant("space_glass_clear", "space_glass_clear", "space_glass_tin_clear")
            addCoreVariant("space_glass_vanilla", "space_glass_vanilla", "space_glass_tin_vanilla")
            addCoreVariant("space_glass_strong", "space_glass_strong", "space_glass_tin_strong")
            addCoreVariant(
                "panel_lighting",
                "panel_lighting",
                "panel_lighting_1",
                "panel_lighting_2",
                "panel_lighting_3",
                "panel_lighting_4"
            )
            addCoreVariant("emergency_box", "emergency_box", "emergency_box_full")

            //Item variants: best if the damage=0 variant has the registered item name, to avoid ModelLoader errors for the #inventory variant
            addCoreVariant("canister", "canister", "canister_copper")
            addCoreVariant("engine", "engine", "tier1booster")
            addCoreVariant(
                "parachute",
                "parachute",
                "parachute_black",
                "parachute_blue",
                "parachute_lime",
                "parachute_brown",
                "parachute_darkblue",
                "parachute_darkgray",
                "parachute_darkgreen",
                "parachute_gray",
                "parachute_magenta",
                "parachute_orange",
                "parachute_pink",
                "parachute_purple",
                "parachute_red",
                "parachute_teal",
                "parachute_yellow"
            )
            addCoreVariant("schematic", "schematic", "schematic_rocket_t2")
            addCoreVariant("key", "key")
            addCoreVariant("buggymat", "buggymat", "seat", "storage")
            addCoreVariant(
                "basic_item",
                "basic_item",
                "solar_module_1",
                "raw_silicon",
                "ingot_copper",
                "ingot_tin",
                "ingot_aluminum",
                "compressed_copper",
                "compressed_tin",
                "compressed_aluminum",
                "compressed_steel",
                "compressed_bronze",
                "compressed_iron",
                "wafer_solar",
                "wafer_basic",
                "wafer_advanced",
                "food",
                "dehydrated_carrot",
                "dehydrated_melon",
                "dehydrated_potato",
                "frequency_module",
                "ambient_thermal_controller"
            )
            addCoreVariant(
                "food",
                "food",
                "dehydrated_carrot",
                "dehydrated_melon",
                "dehydrated_potato",
                ItemFood.names[4],
                ItemFood.names[5],
                ItemFood.names[6],
                ItemFood.names[7],
                ItemFood.names[8],
                ItemFood.names[9]
            )
            addCoreVariant("item_basic_moon", "item_basic_moon", "compressed_meteoric_iron", "lunar_sapphire")
            addCoreVariant("meteor_chunk", "meteor_chunk", "meteor_chunk_hot")
            addCoreVariant("buggy", "buggy", "buggy_1", "buggy_2", "buggy_3")
            if (CompatibilityManager.useAluDust()) addCoreVariant("ic2compat", "ic2compat")

            for (container: PartialCanister in canisters) {
                val variants: Array<String?> = arrayOfNulls<String>(container.getTextureCount())
                for (i: Int in 0..<container.getTextureCount()) {
                    variants[i] = container.getBaseName() + (if (i > 0) "_" + i else "")
                }
                ClientUtil.addVariant((container.getModID())!!, (container.getBaseName())!!, *variants)
            }
        }

        @JvmStatic
        private fun addCoreVariant(name: String, vararg variants: String?) {
            ClientUtil.addVariant(Constants.MOD_ID_CORE, name, *variants)
        }

        @JvmStatic
        private fun updateCapeList() {
            val timeout: Int = 10000
            val capeListUrl: URL

            try {
                capeListUrl = URL("https://raw.github.com/micdoodle8/Galacticraft/master/capes.txt")
            } catch (e: IOException) {
                FMLLog.severe("Error getting capes list URL")
                if (ConfigManagerCore.enableDebug) e.printStackTrace()
                return
            }

            val connection: URLConnection

            try {
                connection = capeListUrl.openConnection()
            } catch (e: IOException) {
                if (ConfigManagerCore.enableDebug) e.printStackTrace()
                return
            }

            connection.setConnectTimeout(timeout)
            connection.setReadTimeout(timeout)
            val stream: InputStream

            try {
                stream = connection.getInputStream()
            } catch (e: IOException) {
                if (ConfigManagerCore.enableDebug) e.printStackTrace()
                return
            }

            val streamReader: InputStreamReader = InputStreamReader(stream)
            val reader: BufferedReader = BufferedReader(streamReader)

            var line: String?
            try {
                while ((reader.readLine().also { line = it }) != null) {
                    if (line!!.contains(":")) {
                        val splitLocation: Int = ((line)!!.indexOf(":"))!!
                        val username: String = ((line)!!.substring(0, splitLocation))!!
                        capeMap.put(
                            username,
                            ResourceLocation(
                                Constants.ASSET_PREFIX,
                                "textures/misc/capes/" + convertCapeString(((line)!!.substring(splitLocation + 1))!!) + ".png"
                            )
                        )
                    }
                }
            } catch (e: IOException) {
                if (ConfigManagerCore.enableDebug) e.printStackTrace()
            } finally {
                try {
                    reader.close()
                } catch (e: IOException) {
                    if (ConfigManagerCore.enableDebug) e.printStackTrace()
                }
            }
        }

        @JvmStatic
        private fun convertCapeString(capeName: String): String {
            val underscoreCase: StringBuilder = StringBuilder()
            for (i: Int in 0..<capeName.length) {
                var c: Char = capeName.get(i)
                if (!Character.isLowerCase(c)) {
                    underscoreCase.append("_")
                    c = c.lowercaseChar()
                }
                underscoreCase.append(c)
            }
            return underscoreCase.toString()
        }

        @JvmStatic
        fun registerInventoryTabs() {
            if (TabRegistry.getTabList().size == 0) {
                TabRegistry.registerTab(InventoryTabVanilla())
            }

            TabRegistry.registerTab(InventoryTabGalacticraft())
        }
    }
}
