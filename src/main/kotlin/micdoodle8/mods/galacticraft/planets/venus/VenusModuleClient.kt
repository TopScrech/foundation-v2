package micdoodle8.mods.galacticraft.planets.venus

import micdoodle8.mods.galacticraft.api.client.IItemMeshDefinitionCustom
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.IPlanetsModuleClient
import micdoodle8.mods.galacticraft.planets.venus.client.TickHandlerClientVenus
import micdoodle8.mods.galacticraft.planets.venus.client.fx.ParticleAcidExhaust
import micdoodle8.mods.galacticraft.planets.venus.client.fx.ParticleAcidVapor
import micdoodle8.mods.galacticraft.planets.venus.client.gui.GuiCrashedProbe
import micdoodle8.mods.galacticraft.planets.venus.client.gui.GuiGeothermal
import micdoodle8.mods.galacticraft.planets.venus.client.gui.GuiSolarArrayController
import micdoodle8.mods.galacticraft.planets.venus.client.render.entity.RenderEntryPodVenus
import micdoodle8.mods.galacticraft.planets.venus.client.render.entity.RenderJuicer
import micdoodle8.mods.galacticraft.planets.venus.client.render.entity.RenderSpiderQueen
import micdoodle8.mods.galacticraft.planets.venus.client.render.entity.RenderWebShot
import micdoodle8.mods.galacticraft.planets.venus.client.render.tile.TileEntityTreasureChestRenderer
import micdoodle8.mods.galacticraft.planets.venus.entities.EntityEntryPodVenus
import micdoodle8.mods.galacticraft.planets.venus.entities.EntityJuicer
import micdoodle8.mods.galacticraft.planets.venus.entities.EntitySpiderQueen
import micdoodle8.mods.galacticraft.planets.venus.entities.EntityWebShot
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityCrashedProbe
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityGeothermalGenerator
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayController
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityTreasureChestVenus
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
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
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class VenusModuleClient : IPlanetsModuleClient {
    override fun preInit() {
        MinecraftForge.EVENT_BUS.register(this)
        RenderingRegistry.registerEntityRenderingHandler<EntityJuicer>(
            EntityJuicer::class.java,
            IRenderFactory { manager: RenderManager? -> RenderJuicer((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityEntryPodVenus>(
            EntityEntryPodVenus::class.java,
            IRenderFactory { manager: RenderManager? -> RenderEntryPodVenus((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntitySpiderQueen>(
            EntitySpiderQueen::class.java,
            IRenderFactory { manager: RenderManager? -> RenderSpiderQueen((manager)!!) })
        RenderingRegistry.registerEntityRenderingHandler<EntityWebShot>(
            EntityWebShot::class.java,
            IRenderFactory { manager: RenderManager? -> RenderWebShot((manager)!!) })
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
            "venus",
            "venus_rock_0",
            "venus_rock_1",
            "venus_rock_2",
            "venus_rock_3",
            "dungeon_brick_venus_1",
            "dungeon_brick_venus_2",
            "venus_ore_aluminum",
            "venus_ore_copper",
            "venus_ore_galena",
            "venus_ore_quartz",
            "venus_ore_silicon",
            "venus_ore_tin",
            "lead_block",
            "venus_ore_solar"
        )
        addPlanetVariants(
            "thermal_padding_t2",
            "thermal_helm_t2",
            "thermal_chestplate_t2",
            "thermal_leggings_t2",
            "thermal_boots_t2"
        )
        addPlanetVariants(
            "basic_item_venus",
            "shield_controller",
            "ingot_lead",
            "radioisotope_core",
            "thermal_fabric",
            "solar_dust",
            "solar_module_2",
            "thin_solar_wafer"
        )
        addPlanetVariants("web_torch", "web_torch_0", "web_torch_1")

        val sludge: Item = Item.getItemFromBlock(VenusBlocks.sulphuricAcid)
        ModelBakery.registerItemVariants(
            sludge,
            ResourceLocation.parse(GalacticraftPlanets.TEXTURE_PREFIX + "sulphuric_acid")
        )
        ModelLoader.setCustomMeshDefinition(
            sludge,
            IItemMeshDefinitionCustom.create(IItemMeshDefinitionCustom { stack: ItemStack? -> sulphuricAcidLocation })
        )
        ModelLoader.setCustomStateMapper(VenusBlocks.sulphuricAcid, object : StateMapperBase() {
            override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
                return sulphuricAcidLocation
            }
        })
    }

    override fun init(event: FMLInitializationEvent?) {
        MinecraftForge.EVENT_BUS.register(TickHandlerClientVenus())
        registerBlockRenderers()
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    open fun loadTextures(event: TextureStitchEvent.Pre) {
        registerTexture(event, "pod_flame")
        registerTexture(event, "web")
    }

    private fun registerTexture(event: TextureStitchEvent.Pre, texture: String) {
        event.getMap().registerSprite(ResourceLocation.parse(GalacticraftPlanets.TEXTURE_PREFIX + "blocks/" + texture))
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    open fun onModelBakeEvent(event: ModelBakeEvent?) {
    }

    private fun replaceModelDefault(
        event: ModelBakeEvent?,
        resLoc: String?,
        objLoc: String?,
        visibleGroups: MutableList<String?>?,
        clazz: Class<out ModelTransformWrapper>?,
        parentState: IModelState?,
        vararg variants: String?
    ) {
        ClientUtil.replaceModel(
            GalacticraftPlanets.ASSET_PREFIX,
            (event)!!,
            resLoc,
            (objLoc)!!,
            (visibleGroups)!!,
            clazz,
            (parentState)!!,
            *(variants as Array<out String>)
        )
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        ClientRegistry.bindTileEntitySpecialRenderer<TileEntityTreasureChestVenus>(
            TileEntityTreasureChestVenus::class.java,
            TileEntityTreasureChestRenderer()
        )
    }

    override fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        if (side == Side.CLIENT) {
            val tile: TileEntity? = (world)!!.getBlockEntity(BlockPos(x, y, z))

            if (ID == GuiIdsPlanets.MACHINE_VENUS) {
                if (tile is TileEntityGeothermalGenerator) {
                    return GuiGeothermal((player)!!.inventory, tile)
                } else if (tile is TileEntityCrashedProbe) {
                    return GuiCrashedProbe((player)!!.inventory, tile)
                } else if (tile is TileEntitySolarArrayController) {
                    return GuiSolarArrayController((player)!!.inventory, tile)
                }
            }
        }

        return null
    }

    override fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, vararg extraData: Any?) {
        val mc: Minecraft? = Minecraft.getInstance()

        if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
            val dX: Double = mc.getRenderViewEntity()!!.posX - (position)!!.x
            val dY: Double = mc.getRenderViewEntity()!!.posY - (position)!!.y
            val dZ: Double = mc.getRenderViewEntity()!!.posZ - (position)!!.z
            var particle: Particle? = null
            val viewDistance: Double = 64.0

            if (particleID == "acidVapor") {
                particle =
                    ParticleAcidVapor(mc.world, (position)!!.x, (position)!!.y, (position)!!.z, (motion)!!.x, (motion)!!.y, (motion)!!.z, 2.5f)
            }

            if (dX * dX + dY * dY + dZ * dZ < viewDistance * viewDistance) {
                if (particleID == "acidExhaust") {
                    particle = ParticleAcidExhaust(
                        mc.world,
                        (position)!!.x,
                        (position)!!.y,
                        (position)!!.z,
                        (motion)!!.x,
                        (motion)!!.y,
                        (motion)!!.z,
                        0.5f
                    )
                }
            }

            if (particle != null) {
                mc.effectRenderer.addEffect(particle)
            }
        }
    }

    override fun getGuiIDs(idList: MutableList<Int>?) {
        (idList)!!.add(GuiIdsPlanets.MACHINE_VENUS)
    }

    companion object {
        private val sulphuricAcidLocation =
            ModelResourceLocation(GalacticraftPlanets.TEXTURE_PREFIX + "sulphuric_acid", "fluid")

        @JvmStatic
        fun registerBlockRenderers() {
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.venusBlock)!!, 0, "venus_rock_0")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.venusBlock)!!, 1, "venus_rock_1")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.venusBlock)!!, 2, "venus_rock_2")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.venusBlock)!!, 3, "venus_rock_3")
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.venusBlock)!!,
                4,
                "dungeon_brick_venus_1"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.venusBlock)!!,
                5,
                "dungeon_brick_venus_2"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.venusBlock)!!,
                6,
                "venus_ore_aluminum"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.venusBlock)!!,
                7,
                "venus_ore_copper"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.venusBlock)!!,
                8,
                "venus_ore_galena"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.venusBlock)!!,
                9,
                "venus_ore_quartz"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.venusBlock)!!,
                10,
                "venus_ore_silicon"
            )
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.venusBlock)!!,
                11,
                "venus_ore_tin"
            )
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.venusBlock)!!, 12, "lead_block")
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.venusBlock)!!,
                13,
                "venus_ore_solar"
            )
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.spout)!!, 0, "spout")
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.treasureChestTier3)!!,
                0,
                "treasure_t3"
            )
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.torchWeb)!!, 0, "web_torch_0")
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.torchWeb)!!, 1, "web_torch_1")
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.geothermalGenerator)!!,
                0,
                "geothermal_generator"
            )
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.crashedProbe)!!)
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.scorchedRock)!!)
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.bossSpawner)!!)
            ClientUtil.registerBlockJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusBlocks.solarArrayModule)!!)
            ClientUtil.registerBlockJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusBlocks.solarArrayController)!!,
                0,
                "solar_array_controller"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusItems.thermalPaddingTier2)!!,
                0,
                "thermal_helm_t2"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusItems.thermalPaddingTier2)!!,
                1,
                "thermal_chestplate_t2"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusItems.thermalPaddingTier2)!!,
                2,
                "thermal_leggings_t2"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusItems.thermalPaddingTier2)!!,
                3,
                "thermal_boots_t2"
            )
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusItems.basicItem)!!,
                0,
                "shield_controller"
            )
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusItems.basicItem)!!, 1, "ingot_lead")
            ClientUtil.registerItemJson(
                GalacticraftPlanets.TEXTURE_PREFIX,
                (VenusItems.basicItem)!!,
                2,
                "radioisotope_core"
            )
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusItems.basicItem)!!, 3, "thermal_fabric")
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusItems.basicItem)!!, 4, "solar_dust")
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusItems.basicItem)!!, 5, "solar_module_2")
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusItems.basicItem)!!, 6, "thin_solar_wafer")
            ClientUtil.registerItemJson(GalacticraftPlanets.TEXTURE_PREFIX, (VenusItems.key)!!, 0, "key_t3")
        }
    }
}
