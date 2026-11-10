package galaxyspace.systems.BarnardsSystem

import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Decorations
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions.EnumBlockDandelions
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass.EnumBlockGrass
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody.ScalableDistance

import asmodeuscore.api.IBodiesHandler
import asmodeuscore.api.IBodies
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigDimensions
import galaxyspace.systems.BarnardsSystem.BarnardsSystemBodies
import asmodeuscore.core.astronomy.BodiesRegistry
import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.BodiesRegistry.Galaxies
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.dimension.WorldProviderBarnarda_C_WE
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.dimension.WorldProviderBarnarda_C1_WE
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c2.dimension.WorldProviderBarnarda_C2_WE
import galaxyspace.systems.BarnardsSystem.core.events.BRClientEventHandler
import galaxyspace.systems.BarnardsSystem.core.events.BREventHandler
import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.core.BRItems
import asmodeuscore.core.handler.ColorBlockHandler
import galaxyspace.core.util.GSDimensions
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Falling_Blocks.EnumFallingBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Glow_Dandelions.EnumBlockGlowDandelions
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Leaves.EnumBlockLeaves
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Ores.EnumBlockBarnardaCOres
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Ores
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks.EnumBlockBarnardaC1
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Decorations.EnumBlockBarnardaC1Decoration
import galaxyspace.systems.BarnardsSystem.core.prefab.items.ItemBasicBR
import galaxyspace.systems.BarnardsSystem.core.prefab.items.ItemFoodBR.BR_Food
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.recipes.CraftingRecipesBarnarda_C
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.recipes.CraftingRecipesBarnarda_C1
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody
import asmodeuscore.api.dimension.IAdvancedSpace.StarType
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.dimension.TeleportTypeBarnarda_C
import asmodeuscore.core.prefab.TeleportTypeBody
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Grass
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import galaxyspace.core.proxy.ClientProxy
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.block.BlockDoor
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.File

@IBodiesHandler
open class BarnardsSystemBodies : IBodies {
    override fun preInitialization(event: FMLPreInitializationEvent?) {
        BRConfigCore(File((event)!!.getModConfigurationDirectory(), "GalaxySpace/barnards/core.conf"))
        BRConfigDimensions(File((event)!!.getModConfigurationDirectory(), "GalaxySpace/barnards/dimensions.conf"))
    }

    override fun preInit(event: FMLPreInitializationEvent?) {
        BarnardsSystem = BodiesRegistry.registerSolarSystem(
            GalaxySpace.ASSET_PREFIX,
            "barnards",
            Galaxies.MILKYWAY,
            Vector3(1.0, -2.0, 0.0),
            "barnarda_a",
            0.8f
        )
        GalaxyRegistry.registerSolarSystem((BarnardsSystem)!!)

        Barnarda_B = BodiesRegistry.registerExPlanet((BarnardsSystem)!!, "barnarda_b", GalaxySpace.ASSET_PREFIX, 0.25f)
        BodiesRegistry.setOrbitData((Barnarda_B)!!, Math.PI.toFloat(), 1.3f, 3.9f)
        GalaxyRegistry.registerPlanet((Barnarda_B)!!)

        Barnarda_B1 = BodiesRegistry.registerExMoon((Barnarda_B)!!, "barnarda_b1", GalaxySpace.ASSET_PREFIX, 15f)
        Barnarda_B1!!.setRingColorRGB(1.1f, 0.0f, 0.0f)
        BodiesRegistry.setOrbitData((Barnarda_B1)!!, Math.PI.toFloat() / 2, 1.0f, 105.5f)
        GalaxyRegistry.registerMoon((Barnarda_B1)!!)

        Barnarda_C = BodiesRegistry.registerExPlanet((BarnardsSystem)!!, "barnarda_c", GalaxySpace.ASSET_PREFIX, 0.7f)
        BodiesRegistry.setOrbitData((Barnarda_C)!!, Math.PI.toFloat() * 2, 1.0f, 6.9f)
        BodiesRegistry.setAtmosphere((Barnarda_C)!!, true, true, false, 0.0f, 1.0f, 1.0f)
        BodiesRegistry.setPlanetData(Barnarda_C, 3f, 24500, BodiesRegistry.calculateGravity(8.5f), false)
        BodiesRegistry.setProviderData(
            (Barnarda_C)!!,
            WorldProviderBarnarda_C_WE::class.java,
            BRConfigDimensions.Companion.dimensionIDBarnardaC,
            if (BRConfigCore.Companion.survivalModeOnBarnarda) 1 else 6,
            ACBiome.ACSpace,
            ACBiome.ACSpaceLowHills
        )
        Barnarda_C!!.atmosphereComponent(EnumAtmosphericGas.CO2).atmosphereComponent(EnumAtmosphericGas.OXYGEN)
            .atmosphereComponent(EnumAtmosphericGas.ARGON)
        GalaxyRegistry.registerPlanet((Barnarda_C)!!)

        Barnarda_C1 = BodiesRegistry.registerExMoon((Barnarda_C)!!, "barnarda_c1", GalaxySpace.ASSET_PREFIX, 10.75f)
        BodiesRegistry.setOrbitData((Barnarda_C1)!!, Math.PI.toFloat() / 2, 1.0f, 25.5f)
        BodiesRegistry.setAtmosphere((Barnarda_C1)!!, false, false, false, -1.3f, 3.0f, 0.0f)
        BodiesRegistry.setPlanetData(Barnarda_C1, 15f, 45000, BodiesRegistry.calculateGravity(6.5f), false)
        BodiesRegistry.setProviderData(
            (Barnarda_C1)!!,
            WorldProviderBarnarda_C1_WE::class.java,
            BRConfigDimensions.Companion.dimensionIDBarnardaC1,
            if (BRConfigCore.Companion.survivalModeOnBarnarda) 1 else 6,
            ACBiome.ACSpace
        )
        Barnarda_C1!!.atmosphereComponent(EnumAtmosphericGas.CO2).atmosphereComponent(EnumAtmosphericGas.NITROGEN)
            .atmosphereComponent(EnumAtmosphericGas.ARGON)
        GalaxyRegistry.registerMoon((Barnarda_C1)!!)

        Barnarda_C2 = BodiesRegistry.registerExMoon((Barnarda_C)!!, "barnarda_c2", GalaxySpace.ASSET_PREFIX, 19.75f)
        BodiesRegistry.setOrbitData((Barnarda_C2)!!, Math.PI.toFloat() / 2, 1.0f, 60.5f, 1.5f, 0.8f, 0.5f, 0f)
        if (GalaxySpace.debug) {
            BodiesRegistry.setAtmosphere((Barnarda_C2)!!, false, false, false, 3.0f, 0.0f, 0.0f)
            BodiesRegistry.setPlanetData(Barnarda_C2, 15f, 45000, BodiesRegistry.calculateGravity(6.5f), false)
            BodiesRegistry.setProviderData(
                (Barnarda_C2)!!,
                WorldProviderBarnarda_C2_WE::class.java,
                BRConfigDimensions.Companion.dimensionIDBarnardaC2,
                1
            )
        }
        GalaxyRegistry.registerMoon((Barnarda_C2)!!)

        Barnarda_ABelt = BodiesRegistry.registerExPlanet((BarnardsSystem)!!, "barnarda_d", GalaxySpace.ASSET_PREFIX, 1.25f)
        Barnarda_ABelt!!.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(1.25f, 1.0f))
            .setRingColorRGB(1.1f, 0.0f, 0.0f)
        BodiesRegistry.setOrbitData((Barnarda_ABelt)!!, Math.PI.toFloat() / 2, 1.0f, 105.9f)
        GalaxyRegistry.registerPlanet((Barnarda_ABelt)!!)

        Barnarda_E = BodiesRegistry.registerExPlanet((BarnardsSystem)!!, "barnarda_e", GalaxySpace.ASSET_PREFIX, 1.75f)
        BodiesRegistry.setOrbitData((Barnarda_E)!!, Math.PI.toFloat(), 1.0f, 35.9f)
        GalaxyRegistry.registerPlanet((Barnarda_E)!!)

        if ((event)!!.getSide() == Side.CLIENT) (GalaxySpace.proxy)!!.register_event(BRClientEventHandler())
        (GalaxySpace.proxy)!!.register_event(BREventHandler())

        BRBlocks.initialize()
        BRItems.initialize()

        registrycelestial()
        registryteleport()

        ColorBlockHandler.addBlock(BRBlocks.BARNARDA_C_GRASS.getDefaultState())
        ColorBlockHandler.addLeavesBlock(Blocks.LEAVES.getDefaultState())
        ColorBlockHandler.addLeavesBlock(Blocks.LEAVES2.getDefaultState())
        ColorBlockHandler.addLeavesBlock(Blocks.VINE.getDefaultState())
        ColorBlockHandler.addLeavesBlock(BRBlocks.BARNARDA_C_LEAVES.getDefaultState())
        ColorBlockHandler.addBlockWithColor(BRBlocks.BARNARDA_C_WATER_GRASS.getDefaultState(), 0x985cff)
        ColorBlockHandler.addWaterBlock(Blocks.WATER.getDefaultState())
        ColorBlockHandler.addWaterBlock(Blocks.FLOWING_WATER.getDefaultState())
    }

    override fun init(event: FMLInitializationEvent?) {
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        GSDimensions.BARNARDA_C = WorldUtil.getDimensionTypeById(BRConfigDimensions.Companion.dimensionIDBarnardaC)
        GSDimensions.BARNARDA_C1 = WorldUtil.getDimensionTypeById(BRConfigDimensions.Companion.dimensionIDBarnardaC1)
        GSDimensions.BARNARDA_C2 = WorldUtil.getDimensionTypeById(BRConfigDimensions.Companion.dimensionIDBarnardaC2)
    }

    @SideOnly(Side.CLIENT)
    override fun registerRender() {
        //	if(BRConfigCore.enableBarnardsSystems) {

        for (blockBasic: EnumBlockBarnardaC in EnumBlockBarnardaC.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRBlocks.BARNARDA_C_BLOCKS,
            blockBasic.getMeta(),
            "barnarda/" + blockBasic.getName()
        )

        for (blockBasic: EnumFallingBlockBarnardaC in EnumFallingBlockBarnardaC.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRBlocks.BARNARDA_C_FALLING_BLOCKS,
            blockBasic.getMeta(),
            "barnarda/" + blockBasic.getName()
        )

        for (blockBasic: Barnarda_C_Dandelions.EnumBlockDandelions in Barnarda_C_Dandelions.EnumBlockDandelions.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRBlocks.BARNARDA_C_DANDELIONS,
            blockBasic.getMeta(),
            "barnarda/" + blockBasic.getName()
        )

        for (blockBasic: EnumBlockGlowDandelions in EnumBlockGlowDandelions.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRBlocks.BARNARDA_C_GLOW_DANDELIONS,
            blockBasic.getMeta(),
            "barnarda/" + blockBasic.getName()
        )

        for (blockBasic: Barnarda_C_Grass.EnumBlockGrass in Barnarda_C_Grass.EnumBlockGrass.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRBlocks.BARNARDA_C_GRASS,
            blockBasic.getMeta(),
            "barnarda/" + blockBasic.getName()
        )

        for (blockBasic: EnumBlockLeaves in EnumBlockLeaves.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRBlocks.BARNARDA_C_LEAVES,
            blockBasic.getMeta(),
            "barnarda/" + blockBasic.getName()
        )

        val name: Array<String?> = arrayOfNulls<String>(EnumBlockBarnardaCOres.entries.size)
        for (blockBasic: EnumBlockBarnardaCOres in EnumBlockBarnardaCOres.entries) {
            if (blockBasic.getName() != null) name[blockBasic.getMeta()] = blockBasic.getName()
            ClientUtil.registerBlockJson(
                GalaxySpace.TEXTURE_PREFIX,
                BRBlocks.BARNARDA_C_ORES,
                blockBasic.getMeta(),
                "barnarda/" + blockBasic.getName()
            )
        }
        GSUtils.addBlockMetadataJsonFiles(
            BRBlocks.BARNARDA_C_ORES,
            name,
            Barnarda_C_Ores.Companion.BASIC_TYPE.getName(),
            "barnarda/"
        )

        for (blockBasic: EnumBlockBarnardaC1 in EnumBlockBarnardaC1.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRBlocks.BARNARDA_C1_BLOCKS,
            blockBasic.getMeta(),
            "barnarda/" + blockBasic.getName()
        )

        for (blockBasic: EnumBlockBarnardaC1Decoration in EnumBlockBarnardaC1Decoration.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRBlocks.BARNARDA_C1_DECORATIONS,
            blockBasic.getMeta(),
            "barnarda/" + blockBasic.getName()
        )

        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "barnarda/", BRBlocks.BARNARDA_C_FARMLAND)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "barnarda/", BRBlocks.BARNARDA_C_WATER_GRASS)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "barnarda/", BRBlocks.BARNARDA_C_VIOLET_LOG)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "barnarda/", BRBlocks.BARNARDA_C_VIOLET_GLOW_LOG)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "barnarda/", BRBlocks.BARNARDA_C_BIRCH_LOG)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "barnarda/", BRBlocks.BARNARDA_C_CACTUS)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "barnarda/", BRBlocks.BARNARDA_C_VIOLET_TRAPDOOR)

        var i: Int = 0
        for (basic: String in ItemBasicBR.Companion.names) {
            if (basic == "null") {
                i++
                continue
            }
            ClientUtil.registerItemJson(GalaxySpace.TEXTURE_PREFIX, BRItems.BASIC, i++, "barnarda/basic/" + basic)
        }
        i = 0
        for (food: BR_Food in BR_Food.entries) ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRItems.FOODS,
            i++,
            "barnarda/foods/" + food.getName()
        )

        ClientUtil.registerItemJson(
            GalaxySpace.TEXTURE_PREFIX,
            BRItems.VIOLET_DOOR,
            0,
            "barnarda/" + BRItems.VIOLET_DOOR.getTranslationKey().substring(5)
        )

        if (GCCoreUtil.isDeobfuscated()) {
            //GSUtils.addBlockJsonFiles(BRBlocks.BARNARDA_C_WATER_GRASS, "barnarda/");
            //GSUtils.addItemMetadataJsonFiles(BRItems.BASIC, ItemBasicBR.names, "barnarda/basic/");
            //GSUtils.addBlockMetadataJsonFiles(BRBlocks.BARNARDA_C_GRASS, name, Barnarda_C_Grass.BASIC_TYPE.getName(), "barnarda/");
        }
    }

    @SideOnly(Side.CLIENT)
    override fun registerVariant() {
        var blocks: Array<String> = (arrayOfNulls<String>(EnumBlockBarnardaC.entries.size) as Array<String>)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockBarnardaC.Companion.byMetadata(i))!!.getName()

        ClientProxy.addVariant("barnarda_c_blocks", "barnarda/", *blocks)

        blocks = (arrayOfNulls<String>(EnumFallingBlockBarnardaC.entries.size) as Array<String>)
        for (i: Int in blocks.indices) blocks[i] = (EnumFallingBlockBarnardaC.Companion.byMetadata(i))!!.getName()

        ClientProxy.addVariant("barnarda_c_falling_blocks", "barnarda/", *blocks)

        ClientProxy.addVariant("barnarda_c_test_log", "barnarda/", "barnarda_c_test_log")
        ClientProxy.addVariant("barnarda_c_birch_log", "barnarda/", "barnarda_c_birch_log")
        ClientProxy.addVariant("barnarda_c_cactus", "barnarda/", "barnarda_c_cactus")
        ClientProxy.addVariant("barnarda_c_test_glow_log", "barnarda/", "barnarda_c_test_glow_log")
        ClientProxy.addVariant("barnarda_c_farmland", "barnarda/", "barnarda_c_farmland")
        ClientProxy.addVariant("barnarda_c_water_grass", "barnarda/", "barnarda_c_water_grass")
        ClientProxy.addVariant("barnarda_c_violet_door", "barnarda/", "barnarda_c_violet_door")
        ClientProxy.addVariant("barnarda_c_violet_trapdoor", "barnarda/", "barnarda_c_violet_trapdoor")

        blocks = (arrayOfNulls<String>(Barnarda_C_Dandelions.EnumBlockDandelions.entries.size) as Array<String>)
        for (i: Int in blocks.indices) blocks[i] =
            (Barnarda_C_Dandelions.EnumBlockDandelions.Companion.byMetadata(i))!!.getName()

        ClientProxy.addVariant("barnarda_c_dandelions", "barnarda/", *blocks)

        blocks = (arrayOfNulls<String>(EnumBlockGlowDandelions.entries.size) as Array<String>)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockGlowDandelions.Companion.byMetadata(i))!!.getName()

        ClientProxy.addVariant("barnarda_c_glow_dandelions", "barnarda/", *blocks)

        blocks = (arrayOfNulls<String>(Barnarda_C_Grass.EnumBlockGrass.entries.size) as Array<String>)
        for (i: Int in blocks.indices) blocks[i] = (Barnarda_C_Grass.EnumBlockGrass.Companion.byMetadata(i))!!.getName()

        ClientProxy.addVariant("barnarda_c_grasses", "barnarda/", *blocks)

        blocks = (arrayOfNulls<String>(EnumBlockLeaves.entries.size) as Array<String>)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockLeaves.Companion.byMetadata(i))!!.getName()

        ClientProxy.addVariant("barnarda_c_leaves", "barnarda/", *blocks)

        blocks = (arrayOfNulls<String>(EnumBlockBarnardaCOres.entries.size) as Array<String>)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockBarnardaCOres.Companion.byMetadata(i))!!.getName()

        ClientProxy.addVariant("barnarda_c_ores", "barnarda/", *blocks)

        blocks = (arrayOfNulls<String>(EnumBlockBarnardaC1.entries.size) as Array<String>)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockBarnardaC1.Companion.byMetadata(i))!!.getName()

        ClientProxy.addVariant("barnarda_c1_blocks", "barnarda/", *blocks)

        blocks = (arrayOfNulls<String>(EnumBlockBarnardaC1Decoration.entries.size) as Array<String>)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockBarnardaC1Decoration.Companion.byMetadata(i))!!.getName()

        ClientProxy.addVariant("barnarda_c1_decorations", "barnarda/", *blocks)

        //ModelLoader.setCustomStateMapper(BRBlocks.BARNARDA_C_REEDS, new StateMap.Builder().ignore(BlockLiquid.LEVEL).build());
        ClientProxy.addVariant("br_basic", "barnarda/basic/", *ItemBasicBR.Companion.names)

        val foods: Array<String> = (arrayOfNulls<String>(BR_Food.entries.size) as Array<String>)
        for (i: Int in foods.indices) foods[i] = (BR_Food.Companion.byMetadata(i))!!.getName()
        ClientProxy.addVariant("br_foods", "barnarda/foods/", *foods)

        ModelLoader.setCustomStateMapper(
            BRBlocks.BARNARDA_C_VIOLET_DOOR,
            StateMap.Builder().ignore(BlockDoor.POWERED).build()
        )
    }

    override fun registerRecipes() {
        CraftingRecipesBarnarda_C.loadRecipes()
        CraftingRecipesBarnarda_C1.loadRecipes()
    }

    override fun canRegister(): Boolean {
        return BRConfigCore.Companion.enableBarnardsSystems
    }

    override fun registerItems(event: RegistryEvent.Register<Item?>?) {
        BRBlocks.oreDictRegistration()
        BRItems.oreDictRegistration()
    }

    companion object {
        @JvmField
        var BarnardsSystem: SolarSystem? = null

        @JvmField
var Barnarda_B: Planet? = null

        @JvmField
var Barnarda_C: Planet? = null

        @JvmField
var Barnarda_ABelt: Planet? = null

        @JvmField
var Barnarda_E: Planet? = null

        @JvmField
var Barnarda_B1: Moon? = null

        @JvmField
var Barnarda_C1: Moon? = null

        @JvmField
var Barnarda_C2: Moon? = null

        @JvmStatic
        private fun registrycelestial() {
            val data: BodiesData = BodiesData(TypeBody.STAR).setStarType(StarType.DWARF).setStarColor(StarColor.ORANGE)
            data.setStarHabitableZone(0.7f, 0.1f)
            BodiesRegistry.registerBodyData(BarnardsSystem!!.getMainStar(), data)
        }

        @JvmStatic
        private fun registryteleport() {
            GalacticraftRegistry.registerTeleportType(WorldProviderBarnarda_C_WE::class.java, TeleportTypeBarnarda_C())


            GalacticraftRegistry.registerTeleportType(WorldProviderBarnarda_C1_WE::class.java, TeleportTypeBody())
            if (GalaxySpace.debug) {
                GalacticraftRegistry.registerTeleportType(WorldProviderBarnarda_C2_WE::class.java, TeleportTypeBody())
            }
        }
    }
}
