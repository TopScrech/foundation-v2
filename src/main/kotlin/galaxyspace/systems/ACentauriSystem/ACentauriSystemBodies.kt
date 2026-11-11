package galaxyspace.systems.ACentauriSystem

import galaxyspace.GalaxySpace
import galaxyspace.core.util.GSUtils
import asmodeuscore.api.IBodies
import asmodeuscore.api.IBodiesHandler
import galaxyspace.systems.ACentauriSystem.core.configs.ACConfigCore
import galaxyspace.systems.ACentauriSystem.core.configs.ACConfigDimensions
import galaxyspace.systems.ACentauriSystem.ACentauriSystemBodies
import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.core.astronomy.BodiesRegistry.Galaxies
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.dimension.WorldProviderProxima_B_WE
import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.core.util.GSDimensions
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks.EnumBlockProximaB
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.recipes.CraftingRecipesProximaB
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody
import asmodeuscore.api.dimension.IAdvancedSpace.StarType
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.dimension.TeleportTypeProxima_B
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Dandelions
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Grass
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.core.proxy.ClientProxy
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.galaxies.Star
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.world.item.Item
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import java.io.File

@IBodiesHandler
open class ACentauriSystemBodies : IBodies {
    override fun preInitialization() {
        ACConfigCore(File(foundation.bootstrap.FoundationBootstrap.configDirectory, "GalaxySpace/alpha_centauri/core.conf"))
        ACConfigDimensions(File(foundation.bootstrap.FoundationBootstrap.configDirectory, "GalaxySpace/alpha_centauri/dimensions.conf"))
    }

    override fun canRegister(): Boolean {
        return ACConfigCore.Companion.enableACentauriSystems
    }

    override fun preInit() {
        aCentauriSystem = BodiesRegistry.registerSolarSystem(
            GalaxySpace.ASSET_PREFIX,
            "acentauri",
            Galaxies.MILKYWAY,
            Vector3(1.5, 0.0, 0.0),
            "centauri_a",
            1.3f
        )
        GalaxyRegistry.registerSolarSystem((aCentauriSystem)!!)

        centauri_b = BodiesRegistry.registerExPlanet((aCentauriSystem)!!, "centauri_b", GalaxySpace.ASSET_PREFIX, 0.3f)
        centauri_b!!.setRingColorRGB(0.0f, 0.0f, 0.0f)
        BodiesRegistry.setOrbitData((centauri_b)!!, Math.PI.toFloat(), 1.5f, 1000f)
        GalaxyRegistry.registerPlanet((centauri_b)!!)

        ProximaSystem = BodiesRegistry.registerSolarSystem(
            GalaxySpace.ASSET_PREFIX,
            "proxima",
            Galaxies.MILKYWAY,
            Vector3(1.7, -0.2, 0.0),
            "proxima",
            0.8f
        )
        GalaxyRegistry.registerSolarSystem((ProximaSystem)!!)

        proxima_b = BodiesRegistry.registerExPlanet((ProximaSystem)!!, "proxima_b", GalaxySpace.ASSET_PREFIX, 0.4f)
        BodiesRegistry.setAtmosphere((proxima_b)!!, false, true, false, -0.05f, 0.4f, 0.0f)
        BodiesRegistry.setOrbitData((proxima_b)!!, Math.PI.toFloat() * 3, 1.2f, 8.0f)
        BodiesRegistry.setPlanetData(proxima_b, 2f, 25000L * 7, BodiesRegistry.calculateGravity(8.0f), true)
        BodiesRegistry.setProviderData(
            (proxima_b)!!,
            WorldProviderProxima_B_WE::class.java,
            ACConfigDimensions.Companion.dimensionIDProxima_B,
            6
        )
        proxima_b!!.setRingColorRGB(0.0f, 0.4f, 0.9f)
        proxima_b!!.atmosphere.composition.add(EnumAtmosphericGas.CO2)
        proxima_b!!.atmosphere.composition.add(EnumAtmosphericGas.OXYGEN)
        if (ACConfigDimensions.Companion.enableProxima_B) GalaxyRegistry.registerPlanet((proxima_b)!!)

        proxima_c = BodiesRegistry.registerExPlanet((ProximaSystem)!!, "proxima_c", GalaxySpace.ASSET_PREFIX, 1.25f)
        BodiesRegistry.setOrbitData((proxima_c)!!, Math.PI.toFloat() / 2, 1.5f, 150f)
        if (ACConfigDimensions.Companion.enableProxima_C) GalaxyRegistry.registerPlanet((proxima_c)!!)

        proxima_d = BodiesRegistry.registerExPlanet((ProximaSystem)!!, "proxima_d", GalaxySpace.ASSET_PREFIX, 0.25f)
        BodiesRegistry.setOrbitData((proxima_d)!!, Math.PI.toFloat() / 3, 0.8f, 3.0f)
        if (ACConfigDimensions.Companion.enableProxima_D) GalaxyRegistry.registerPlanet((proxima_d)!!)


        /*
        proxima_b = (Planet) BodiesHelper.registerPlanet(ProximaSystem, "proxima_b", GalaxySpace.ASSET_PREFIX, WorldProviderProxima_B_WE.class, ACConfigDimensions.dimensionIDProxima_B, 6, (float) Math.PI*3, 1.2F, 0.25F, 1.1F, ACBiome.ACSpace)
        		.setRingColorRGB(0.0F, 0.4F, 0.9F).atmosphereComponent(EnumAtmosphericGas.CO2);//.atmosphereComponent(EnumAtmosphericGas.OXYGEN);
        proxima_b.setAtmosphere(new AtmosphereInfo(false, true, false, 0.5F, 0.4F, 0.0F));
		  
        proxima_c = (Planet) BodiesHelper.registerPlanet(
        			ProximaSystem, 
        			"proxima_c", 
        			GalaxySpace.ASSET_PREFIX, 
        			null, -1, -1, (float) Math.PI*4, 1.9F, 0.75F, 15.2F);
        */
        //if(ACConfigCore.enableACentauriSystems) {
        ACBlocks.initialize()
        registrycelestial()
        registryteleport()
        // }
    }

    override fun init(event: FMLInitializationEvent?) {
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        GSDimensions.PROXIMA_B = WorldUtil.getDimensionTypeById(ACConfigDimensions.Companion.dimensionIDProxima_B)
    }

    override fun registerRender() {
        //if(ACConfigCore.enableACentauriSystems) {

        for (blockBasic: EnumBlockProximaB in EnumBlockProximaB.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            ACBlocks.PROXIMA_B_BLOCKS,
            blockBasic.getMeta(),
            "proxima/" + blockBasic.getName()
        )



        for (blockBasic: Proxima_B_Dandelions.EnumBlockDandelions in Proxima_B_Dandelions.EnumBlockDandelions.entries) ClientUtil.registerBlockJson(
            GalaxySpace.TEXTURE_PREFIX,
            ACBlocks.PROXIMA_B_DANDELIONS,
            blockBasic.getMeta(),
            "proxima/" + blockBasic.getName()
        )


        val name: Array<String?> = arrayOfNulls<String>(Proxima_B_Grass.EnumBlockGrass.entries.size)
        for (blockBasic: Proxima_B_Grass.EnumBlockGrass in Proxima_B_Grass.EnumBlockGrass.entries) {
            if (blockBasic.getName() != null) name[blockBasic.getMeta()] = blockBasic.getName()
            ClientUtil.registerBlockJson(
                GalaxySpace.TEXTURE_PREFIX,
                ACBlocks.PROXIMA_B_GRASS,
                blockBasic.getMeta(),
                "proxima/" + blockBasic.getName()
            )
        }

        //if(GCCoreUtil.isDeobfuscated()) 
        //GSUtils.addBlockMetadataJsonFiles(ACBlocks.PROXIMA_B_GRASS, name, Proxima_B_Grass.BASIC_TYPE.getName(), "proxima/");
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "proxima/", ACBlocks.PROXIMA_B_LOG_1)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "proxima/", ACBlocks.PROXIMA_B_LOG_2)
        ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX + "proxima/", ACBlocks.PROXIMA_B_MUSHROOM_LOG)


        //}
        /*	
		String[] name = new String[EnumBlockProximaB.values().length];
		for(EnumBlockProximaB block : EnumBlockProximaB.values())
		{
			if(block.getName() != null) name[block.getMeta()] = block.getName();
			ClientUtil.registerBlockJson(GalaxySpace.TEXTURE_PREFIX, ACBlocks.PROXIMA_B_BLOCKS, block.getMeta(), block.getName());
		}
		
		if(GCCoreUtil.isDeobfuscated()) 
			GSUtils.addBlockMetadataJsonFiles(ACBlocks.PROXIMA_B_BLOCKS, name, Proxima_B_Blocks.BASIC_TYPE.getName(), "");
		*/
    }

    override fun registerVariant() {
        //if(ACConfigCore.enableACentauriSystems) {
        var blocks: Array<String?> = arrayOfNulls<String>(EnumBlockProximaB.entries.size)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockProximaB.Companion.byMetadata(i))!!.getName()

        ClientProxy.Companion.addVariant("proxima_b_blocks", "proxima/", *blocks)
        ClientProxy.Companion.addVariant("proxima_b_log_1", "proxima/", "proxima_b_log_1")
        ClientProxy.Companion.addVariant("proxima_b_log_2", "proxima/", "proxima_b_log_2")
        ClientProxy.Companion.addVariant("proxima_b_mushroom_log", "proxima/", "proxima_b_mushroom_log")

        blocks = arrayOfNulls<String>(Proxima_B_Dandelions.EnumBlockDandelions.entries.size)
        for (i: Int in blocks.indices) blocks[i] =
            (Proxima_B_Dandelions.EnumBlockDandelions.Companion.byMetadata(i))!!.getName()

        ClientProxy.Companion.addVariant("proxima_b_dandelions", "proxima/", *blocks)

        blocks = arrayOfNulls<String>(Proxima_B_Grass.EnumBlockGrass.entries.size)
        for (i: Int in blocks.indices) blocks[i] = (Proxima_B_Grass.EnumBlockGrass.Companion.byMetadata(i))!!.getName()

        ClientProxy.Companion.addVariant("proxima_b_grasses", "proxima/", *blocks)
        //}
    }

    override fun registerRecipes() {
        //if(ACConfigCore.enableACentauriSystems) {
        CraftingRecipesProximaB.loadRecipes()
        //}
    }

    override fun registerItems() {
        ACBlocks.oreDictRegistration()
    }

    companion object {
        @JvmField
        var aCentauriSystem: SolarSystem? = null

        @JvmField
        var ProximaSystem: SolarSystem? = null

        @JvmField
        var aCentauri: Star? = null

        @JvmField
        var centauri_b: Planet? = null

        @JvmField
        var proxima: Star? = null

        @JvmField
        var proxima_b: Planet? = null

        @JvmField
        var proxima_c: Planet? = null

        @JvmField
        var proxima_d: Planet? = null

        @JvmStatic
        private fun registrycelestial() {
            var data: BodiesData = BodiesData(TypeBody.STAR).setStarType(StarType.DWARF).setStarColor(StarColor.YELLOW)
            BodiesRegistry.registerBodyData(centauri_b, data)

            data = BodiesData(TypeBody.STAR).setStarType(StarType.DWARF).setStarColor(StarColor.YELLOW)
            BodiesRegistry.registerBodyData(aCentauriSystem!!.getMainStar(), data)

            data = BodiesData(TypeBody.STAR).setStarType(StarType.DWARF).setStarColor(StarColor.ORANGE)
            data.setStarHabitableZone(0.4f, 0.1f)
            BodiesRegistry.registerBodyData(ProximaSystem!!.getMainStar(), data)
        }

        @JvmStatic
        private fun registryteleport() {
            GalacticraftRegistry.registerTeleportType(WorldProviderProxima_B_WE::class.java, TeleportTypeProxima_B())
        }
    }
}
