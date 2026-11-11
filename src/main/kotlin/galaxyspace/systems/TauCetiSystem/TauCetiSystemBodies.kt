package galaxyspace.systems.TauCetiSystem

import foundation.block.getStateFromMeta

import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Watergrass.EnumBlockDandelions

import galaxyspace.core.util.GSUtils
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.core.util.GSDimensions
import galaxyspace.GalaxySpace
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.api.IBodiesHandler
import asmodeuscore.api.IBodies
import galaxyspace.systems.TauCetiSystem.core.configs.TCConfigCore
import galaxyspace.systems.TauCetiSystem.TauCetiSystemBodies
import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.core.astronomy.BodiesRegistry.Galaxies
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.dimensions.WorldProviderTauCeti_F_WE
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.dimensions.TeleportTypeTauCeti_F
import galaxyspace.systems.TauCetiSystem.core.TCBlocks
import asmodeuscore.core.handler.ColorBlockHandler
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody
import asmodeuscore.api.dimension.IAdvancedSpace.StarType
import galaxyspace.core.proxy.ClientProxy
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Blocks.EnumBlockTauCetiF
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Watergrass
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Corals.EnumBlockCorals
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Corals
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.block.BlockLiquid
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.io.File

@IBodiesHandler
open class TauCetiSystemBodies : IBodies {
    override fun preInitialization() {
        TCConfigCore(File(foundation.bootstrap.FoundationBootstrap.configDirectory, "GalaxySpace/tau_ceti/core.conf"))
    }

    override fun preInit() {
        TauCetiSystem = BodiesRegistry.registerSolarSystem(
            GalaxySpace.ASSET_PREFIX,
            "tauceti",
            Galaxies.MILKYWAY,
            Vector3(-3.0, 3.0, 0.0),
            "tauceti",
            0.75f
        )
        GalaxyRegistry.registerSolarSystem((TauCetiSystem)!!)

        TauCeti_G = BodiesRegistry.registerExPlanet((TauCetiSystem)!!, "tauceti_g", GalaxySpace.ASSET_PREFIX, 0.25f)
        BodiesRegistry.setOrbitData((TauCeti_G)!!, Math.PI.toFloat(), 1.25f, 2f)
        GalaxyRegistry.registerPlanet((TauCeti_G)!!)

        TauCeti_H = BodiesRegistry.registerExPlanet((TauCetiSystem)!!, "tauceti_h", GalaxySpace.ASSET_PREFIX, 0.4f)
        BodiesRegistry.setOrbitData((TauCeti_H)!!, Math.PI.toFloat() / 2, 1.25f, 15f)
        GalaxyRegistry.registerPlanet((TauCeti_H)!!)

        TauCeti_E = BodiesRegistry.registerExPlanet((TauCetiSystem)!!, "tauceti_e", GalaxySpace.ASSET_PREFIX, 0.75f)
        BodiesRegistry.setOrbitData((TauCeti_E)!!, Math.PI.toFloat() * 3, 1.25f, 30f)
        GalaxyRegistry.registerPlanet((TauCeti_E)!!)

        TauCeti_F = BodiesRegistry.registerExPlanet((TauCetiSystem)!!, "tauceti_f", GalaxySpace.ASSET_PREFIX, 1.2f)
        BodiesRegistry.setOrbitData((TauCeti_F)!!, Math.PI.toFloat() / 4, 1.25f, 60f)
        BodiesRegistry.setPlanetData(TauCeti_F, 8.0f, 3f, 36000L, BodiesRegistry.calculateGravity(7.8f), false)
        BodiesRegistry.setProviderData(
            (TauCeti_F)!!,
            WorldProviderTauCeti_F_WE::class.java,
            TCConfigCore.Companion.dimensionIDTauCetiF,
            6,
            ACBiome.ACSpace
        )
        BodiesRegistry.setAtmosphere((TauCeti_F)!!, false, true, false, -0.2f, 3f, 1.0f)
        (TauCeti_F)!!.atmosphereComponent(EnumAtmosphericGas.OXYGEN).atmosphereComponent(EnumAtmosphericGas.CO2).atmosphereComponent(EnumAtmosphericGas.NITROGEN)
        GalaxyRegistry.registerPlanet((TauCeti_F)!!)


        GalacticraftRegistry.registerTeleportType(WorldProviderTauCeti_F_WE::class.java, TeleportTypeTauCeti_F())

        TCBlocks.initialize()

        ColorBlockHandler.addBlock(TCBlocks.TAUCETI_F_BLOCKS.getStateFromMeta(1))
    }

    override fun init(event: FMLInitializationEvent?) {
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        val data: BodiesData = BodiesData(TypeBody.STAR).setStarType(StarType.DWARF).setStarColor(StarColor.YELLOW)
        data.setStarHabitableZone(1.1f, 0.22f)
        BodiesRegistry.registerBodyData(TauCetiSystem!!.getMainStar(), data)

        GSDimensions.TAU_CETI_F = WorldUtil.getDimensionTypeById(TCConfigCore.Companion.dimensionIDTauCetiF)
    }

    override fun registerRecipes() {
    }

    @OnlyIn(Dist.CLIENT)
    override fun registerRender() {
        var name: Array<String?> = arrayOfNulls<String>(EnumBlockTauCetiF.entries.size)
        for (blockBasic: EnumBlockTauCetiF in EnumBlockTauCetiF.entries) {
            if (blockBasic.getName() != null) name[blockBasic.getMeta()] = blockBasic.getName()
            ClientUtil.registerBlockJson(
                GalaxySpace.TEXTURE_PREFIX,
                TCBlocks.TAUCETI_F_BLOCKS,
                blockBasic.getMeta(),
                "tauceti/" + blockBasic.getName()
            )
        }

        name = arrayOfNulls<String>(TauCeti_F_Watergrass.EnumBlockDandelions.entries.size)
        for (blockBasic: TauCeti_F_Watergrass.EnumBlockDandelions in TauCeti_F_Watergrass.EnumBlockDandelions.entries) {
            if (blockBasic.getName() != null) name[blockBasic.getMeta()] = blockBasic.getName()
            ClientUtil.registerBlockJson(
                GalaxySpace.TEXTURE_PREFIX,
                TCBlocks.TAUCETI_F_WATERGRASS,
                blockBasic.getMeta(),
                "tauceti/" + blockBasic.getName()
            )
        }

        name = arrayOfNulls<String>(EnumBlockCorals.entries.size)
        for (blockBasic: EnumBlockCorals in EnumBlockCorals.entries) {
            if (blockBasic.getName() != null) name[blockBasic.getMeta()] = blockBasic.getName()
            ClientUtil.registerBlockJson(
                GalaxySpace.TEXTURE_PREFIX,
                TCBlocks.TAUCETI_F_CORALS,
                blockBasic.getMeta(),
                "tauceti/" + blockBasic.getName()
            )
        }
        if (GCCoreUtil.isDeobfuscated()) GSUtils.addBlockMetadataJsonFiles(
            TCBlocks.TAUCETI_F_CORALS,
            name,
            TauCeti_F_Corals.BASIC_TYPE.getName(),
            "tauceti/"
        )
    }

    @OnlyIn(Dist.CLIENT)
    override fun registerVariant() {
        var blocks: Array<String?> = arrayOfNulls<String>(EnumBlockTauCetiF.entries.size)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockTauCetiF.byMetadata(i))!!.getName()

        ClientProxy.addVariant("tauceti_f_blocks", "tauceti/", *blocks)

        blocks = arrayOfNulls<String>(TauCeti_F_Watergrass.EnumBlockDandelions.entries.size)
        for (i: Int in blocks.indices) blocks[i] = (TauCeti_F_Watergrass.EnumBlockDandelions.byMetadata(i))!!.getName()

        ClientProxy.addVariant("tauceti_f_watergrass", "tauceti/", *blocks)

        blocks = arrayOfNulls<String>(EnumBlockCorals.entries.size)
        for (i: Int in blocks.indices) blocks[i] = (EnumBlockCorals.byMetadata(i))!!.getName()

        ClientProxy.addVariant("tauceti_f_corals", "tauceti/", *blocks)

        ModelLoader.setCustomStateMapper(
            TCBlocks.TAUCETI_F_WATERGRASS,
            StateMap.Builder().ignore(BlockLiquid.LEVEL).build()
        )
        ModelLoader.setCustomStateMapper(
            TCBlocks.TAUCETI_F_CORALS,
            StateMap.Builder().ignore(BlockLiquid.LEVEL).build()
        )
    }

    override fun canRegister(): Boolean {
        return TCConfigCore.Companion.enableTauCetiSystems
    }

    companion object {
        @JvmField
        var TauCetiSystem: SolarSystem? = null

        @JvmField
var TauCeti_G: Planet? = null

        @JvmField
var TauCeti_H: Planet? = null

        @JvmField
var TauCeti_E: Planet? = null

        @JvmField
var TauCeti_F: Planet? = null
    }
}
