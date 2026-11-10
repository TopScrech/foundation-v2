package galaxyspace.systems.SolarSystem

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody.ScalableDistance

import galaxyspace.core.GSItems
import asmodeuscore.api.IBodiesHandler
import asmodeuscore.api.IBodies
import galaxyspace.systems.SolarSystem.SolarSystemBodies
import asmodeuscore.core.astronomy.BodiesRegistry
import galaxyspace.GalaxySpace
import galaxyspace.systems.SolarSystem.planets.mercury.dimension.WorldProviderMercury
import galaxyspace.core.configs.GSConfigDimensions
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.systems.SolarSystem.planets.ceres.dimension.WorldProviderCeres
import galaxyspace.systems.SolarSystem.planets.pluto.dimension.WorldProviderPluto
import asmodeuscore.core.prefab.celestialbody.ExPlanet
import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.WorldProviderKuiperBelt
import galaxyspace.systems.SolarSystem.planets.haumea.dimension.WorldProviderHaumea_WE
import galaxyspace.systems.SolarSystem.moons.phobos.dimension.WorldProviderPhobos
import galaxyspace.systems.SolarSystem.moons.io.dimension.WorldProviderIo
import galaxyspace.systems.SolarSystem.moons.europa.dimension.WorldProviderEuropa_WE
import galaxyspace.systems.SolarSystem.moons.ganymede.dimension.WorldProviderGanymede
import galaxyspace.systems.SolarSystem.moons.callisto.dimension.WorldProviderCallisto
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.systems.SolarSystem.moons.enceladus.dimension.WorldProviderEnceladus_WE
import galaxyspace.systems.SolarSystem.moons.enceladus.dimension.WorldProviderEnceladus
import galaxyspace.systems.SolarSystem.moons.titan.dimension.WorldProviderTitan
import galaxyspace.systems.SolarSystem.moons.miranda.dimension.WorldProviderMiranda
import galaxyspace.systems.SolarSystem.moons.triton.dimenson.WorldProviderTriton_WE
import galaxyspace.systems.SolarSystem.satellites.mars.dimension.WorldProviderMarsSS
import galaxyspace.systems.SolarSystem.satellites.venus.dimension.WorldProviderVenusSS
import galaxyspace.systems.SolarSystem.planets.test.dimension.WorldProviderTest_WE2
import galaxyspace.systems.SolarSystem.planets.mars.dimension.WorldProviderMars_WE
import galaxyspace.core.configs.GSConfigWorld
import asmodeuscore.core.astronomy.dimension.world.OreGenerator
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockOres.EnumBlockOres
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockOres
import galaxyspace.systems.SolarSystem.planets.overworld.world.gen.NickelGenerator
import galaxyspace.core.prefab.world.gen.WorldGenNaturalGas
import galaxyspace.systems.SolarSystem.planets.overworld.schematics.SchematicOxTank
import galaxyspace.systems.SolarSystem.planets.overworld.schematics.SchematicCone
import galaxyspace.systems.SolarSystem.planets.overworld.schematics.SchematicBody
import galaxyspace.systems.SolarSystem.planets.overworld.schematics.SchematicEngine
import galaxyspace.systems.SolarSystem.planets.overworld.schematics.SchematicBooster
import galaxyspace.systems.SolarSystem.planets.overworld.schematics.SchematicFins
import galaxyspace.core.util.GSConstants
import galaxyspace.core.util.GSDimensions
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody
import asmodeuscore.api.dimension.IAdvancedSpace.StarType
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemSchematics
import java.io.IOException
import asmodeuscore.core.prefab.celestialbody.ExMoon
import galaxyspace.systems.SolarSystem.planets.mercury.recipes.CraftingRecipesMercury
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.CraftingRecipesOverworld
import galaxyspace.systems.SolarSystem.planets.mars.recipes.CraftingRecipesMars
import galaxyspace.systems.SolarSystem.planets.haumea.recipes.CraftingRecipesHaumea
import galaxyspace.systems.SolarSystem.moons.phobos.recipes.CraftingRecipesPhobos
import galaxyspace.systems.SolarSystem.moons.io.recipes.CraftingRecipesIo
import galaxyspace.systems.SolarSystem.moons.europa.recipes.CraftingRecipesEuropa
import galaxyspace.systems.SolarSystem.moons.ganymede.recipes.CraftingRecipesGanymede
import galaxyspace.systems.SolarSystem.moons.miranda.recipes.CraftingRecipesMiranda
import galaxyspace.systems.SolarSystem.planets.mercury.dimension.TeleportTypeMercury
import galaxyspace.systems.SolarSystem.planets.ceres.dimension.TeleportTypeCeres
import galaxyspace.systems.SolarSystem.planets.pluto.dimension.TeleportTypePluto
import galaxyspace.systems.SolarSystem.planets.kuiperbelt.dimension.TeleportTypeKuiperBelt
import galaxyspace.systems.SolarSystem.planets.haumea.dimension.TeleportTypeHaumea
import asmodeuscore.core.prefab.TeleportTypeBody
import galaxyspace.systems.SolarSystem.moons.io.dimension.TeleportTypeIo
import galaxyspace.systems.SolarSystem.moons.europa.dimension.TeleportTypeEuropa
import galaxyspace.systems.SolarSystem.moons.ganymede.dimension.TeleportTypeGanymede
import galaxyspace.systems.SolarSystem.moons.callisto.dimension.TeleportTypeCallisto
import galaxyspace.systems.SolarSystem.moons.enceladus.dimension.TeleportTypeEnceladus
import galaxyspace.systems.SolarSystem.moons.titan.dimension.TeleportTypeTitan
import galaxyspace.systems.SolarSystem.moons.miranda.dimension.TeleportTypeMiranda
import galaxyspace.systems.SolarSystem.moons.triton.dimenson.TeleportTypeTriton
import galaxyspace.systems.SolarSystem.satellites.mars.dimension.TeleportTypeMarsSS
import galaxyspace.systems.SolarSystem.satellites.venus.dimension.TeleportTypeVenusSS
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic.SchematicConeRecipe
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic.SchematicBodyRecipe
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic.SchematicEngineRecipe
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic.SchematicBoosterRecipe
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic.SchematicFinsRecipe
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic.SchematicOxTankRecipe
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.schematic.SchematicTier2Recipe
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.Satellite
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.SpaceStationType
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.galacticraft.planets.asteroids.schematic.SchematicTier3Rocket
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import micdoodle8.mods.galacticraft.planets.mars.MarsModule
import micdoodle8.mods.galacticraft.planets.mars.dimension.TeleportTypeMars
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.mars.schematic.SchematicTier2Rocket
import micdoodle8.mods.galacticraft.planets.venus.ConfigManagerVenus
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import java.util.ArrayList
import java.util.HashMap

@IBodiesHandler
open class SolarSystemBodies : IBodies {
    /*
	 * Sun Distance: 0.0F
	 * Mercury Distance: 0.5F
	 * Venus Distance: 0.74F
	 * Overworld Distance: 1.0F
	 		*Moon Distance: 13.0F	 
	 * Mars Distance: 1.25F
	 		*Phobos Distance: 8.0F
	 		*Deimos Distance: 16.0F 
	 * Ceres Distance: 1.7F
	 * Asteroids Distance: 1.75F
	 * Jupiter Distance: 2.0F
	 		*Io Distance: 10.0F
	 		*Europa Distance: 15.0F
	 		*Ganymede Distance: 20.0F
	 		*Callisto Distance: 30.0F
	 * Saturn Distance: 2.25F
	 		*Enceladus Distance: 15.0F
	 		*Titan Distance: 35.0F 
	 * Uranus Distance: 2.5F
	 * Neptune Distance: 2.75F
	 * Pluto Distance: 3.0F
	 * Kuiper Belt Distance: 3.25F
	 * Haumea Distance: 3.5F
	 * Makemake Distance: 3.75F
	 * Eris Distance: 4.0F
	 */
    override fun preInit(event: FMLPreInitializationEvent?) {
        val sol: SolarSystem? = GalacticraftCore.solarSystemSol

        val mercury: Planet = BodiesRegistry.registerExPlanet((sol)!!, "mercury", GalaxySpace.ASSET_PREFIX, 0.5f)
        planetMercury = mercury
        BodiesRegistry.setOrbitData(mercury, 1.45f, 0.5f, 0.24f, 0f, 0f, 4f, 0f)
        BodiesRegistry.setPlanetData(mercury, 0f, 176000L, BodiesRegistry.calculateGravity(3.8f), true)
        BodiesRegistry.setAtmosphere(mercury, false, false, false, 4.0f, 0.0f, 0.0f)
        BodiesRegistry.setProviderData(
            mercury,
            WorldProviderMercury::class.java,
            GSConfigDimensions.dimensionIDMercury,
            3,
            ACBiome.ACSpace
        )
        if (GSConfigDimensions.enableMercury) GalaxyRegistry.registerPlanet(mercury)

        planetCeres = BodiesRegistry.registerExPlanet((sol)!!, "ceres", GalaxySpace.ASSET_PREFIX, 1.7f)
        planetCeres!!.setRingColorRGB(0f, 0f, 0f)
        BodiesRegistry.setOrbitData((planetCeres)!!, 2.0f, 0.5f, 15.0f)
        BodiesRegistry.setPlanetData(planetCeres, 0f, 10000L, BodiesRegistry.calculateGravity(2.37f), true)
        BodiesRegistry.setAtmosphere((planetCeres)!!, false, false, false, -4.0f, 0.0f, 0.0f)
        BodiesRegistry.setProviderData(
            (planetCeres)!!,
            WorldProviderCeres::class.java,
            GSConfigDimensions.dimensionIDCeres,
            3,
            ACBiome.ACSpace
        )
        if (GSConfigDimensions.enableCeres) GalaxyRegistry.registerPlanet((planetCeres)!!)

        planetJupiter = BodiesRegistry.registerExPlanet((sol)!!, "jupiter", GalaxySpace.ASSET_PREFIX, 2.0f)
        BodiesRegistry.setOrbitData((planetJupiter)!!, Math.PI.toFloat(), 2.0f, 11.86f)
        //BodiesRegistry.setPlanetData(planetJupiter, 0F, 10000L, BodiesRegistry.calculateGravity(2.37F), true);
        //BodiesRegistry.setAtmosphere(planetJupiter, false, false, false, -5.0F, 0.0F, 0.0F);
        //BodiesRegistry.setProviderData(planetJupiter, WorldProviderCeres.class, GSConfigDimensions.dimensionIDJupiter, 100, ACBiome.ACSpace);
        //planetJupiter.setUnreachable();
        if (GSConfigDimensions.enableJupiter) GalaxyRegistry.registerPlanet((planetJupiter)!!)
        else if (!GSConfigDimensions.enableJupiterMoons) planetJupiter =
            GalaxyRegistry.getCelestialBodyFromUnlocalizedName("jupiter") as Planet?

        planetSaturn = BodiesRegistry.registerExPlanet((sol)!!, "saturn", GalaxySpace.ASSET_PREFIX, 2.25f)
        BodiesRegistry.setOrbitData((planetSaturn)!!, Math.PI.toFloat() / 2, 1.5f, 29.46f)
        if (GSConfigDimensions.enableSaturn) GalaxyRegistry.registerPlanet((planetSaturn)!!)
        else if (!GSConfigDimensions.enableSaturnMoons) planetSaturn =
            GalaxyRegistry.getCelestialBodyFromUnlocalizedName("saturn") as Planet?

        planetUranus = BodiesRegistry.registerExPlanet((sol)!!, "uranus", GalaxySpace.ASSET_PREFIX, 2.5f)
        BodiesRegistry.setOrbitData((planetUranus)!!, Math.PI.toFloat() / 4, 1.2f, 84.06f)
        if (GSConfigDimensions.enableUranus) GalaxyRegistry.registerPlanet((planetUranus)!!)
        else if (!GSConfigDimensions.enableUranusMoons) planetUranus =
            GalaxyRegistry.getCelestialBodyFromUnlocalizedName("uranus") as Planet?

        planetNeptune = BodiesRegistry.registerExPlanet((sol)!!, "neptune", GalaxySpace.ASSET_PREFIX, 2.75f)
        BodiesRegistry.setOrbitData((planetNeptune)!!, Math.PI.toFloat(), 1.2f, 164.84f)
        if (GSConfigDimensions.enableNeptune) GalaxyRegistry.registerPlanet((planetNeptune)!!)
        else if (!GSConfigDimensions.enableNeptuneMoons) planetNeptune =
            GalaxyRegistry.getCelestialBodyFromUnlocalizedName("neptune") as Planet?

        planetPluto = BodiesRegistry.registerExPlanet((sol)!!, "pluto", GalaxySpace.ASSET_PREFIX, 3.0f)
        BodiesRegistry.setOrbitData((planetPluto)!!, 0.0f, 0.5f, 250.0f, 0f, 0f, 25f, 0f)
        BodiesRegistry.setPlanetData(planetPluto, 0f, 98000L, BodiesRegistry.calculateGravity(2.62f), true)
        BodiesRegistry.setAtmosphere((planetPluto)!!, false, false, false, -7.8f, 0.0f, 0.0f)
        BodiesRegistry.setProviderData(
            (planetPluto)!!,
            WorldProviderPluto::class.java,
            GSConfigDimensions.dimensionIDPluto,
            6,
            ACBiome.ACSpace,
            ACBiome.ACSpaceLowPlains,
            ACBiome.ACSpaceLowHills,
            ACBiome.ACSpaceMidPlains
        )
        planetPluto!!.atmosphereComponent(EnumAtmosphericGas.NITROGEN)
        if (GSConfigDimensions.enablePluto) GalaxyRegistry.registerPlanet((planetPluto)!!)
        else planetPluto = GalaxyRegistry.getCelestialBodyFromUnlocalizedName("pluto") as Planet?

        planetKuiperBelt = BodiesRegistry.registerExPlanet((sol)!!, "kuiperbelt", GalaxySpace.ASSET_PREFIX, 3.25f)
            .setRingColorRGB(1.1f, 0.0f, 0.0f) as ExPlanet
        planetKuiperBelt!!.setClassPlanet(ClassBody.ASTEROID)
        planetKuiperBelt!!.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(3.5f, 3.2f))
        BodiesRegistry.setOrbitData((planetKuiperBelt)!!, 1.0f, 1.0f, 300f)
        BodiesRegistry.setAtmosphere((planetKuiperBelt)!!, false, false, false, -8.2f, 0.0f, 0.0f)
        BodiesRegistry.setPlanetData(planetKuiperBelt, 0f, 0, 0f, true)
        BodiesRegistry.setProviderData(
            (planetKuiperBelt)!!,
            WorldProviderKuiperBelt::class.java,
            GSConfigDimensions.dimensionIDKuiperBelt,
            6,
            ACBiome.ACSpace
        )
        if (GSConfigDimensions.enableKuiperBelt) GalaxyRegistry.registerPlanet((planetKuiperBelt)!!)

        planetHaumea = BodiesRegistry.registerExPlanet((sol)!!, "haumea", GalaxySpace.ASSET_PREFIX, 3.5f)
        BodiesRegistry.setOrbitData((planetHaumea)!!, Math.PI.toFloat() + 1.64f, 1.0f, 392.9f, 0f, 0f, -55f, 0f)
        BodiesRegistry.setAtmosphere((planetHaumea)!!, false, false, false, -8.0f, 0.0f, 0.0f)
        BodiesRegistry.setPlanetData(planetHaumea, 0f, 12000, BodiesRegistry.calculateGravity(2.02f), true)
        BodiesRegistry.setProviderData(
            (planetHaumea)!!,
            WorldProviderHaumea_WE::class.java,
            GSConfigDimensions.dimensionIDHaumea,
            6,
            ACBiome.ACSpace
        )
        if (GSConfigDimensions.enableHaumea) GalaxyRegistry.registerPlanet((planetHaumea)!!)

        phobosMars = BodiesRegistry.registerExMoon((MarsModule.planetMars)!!, "phobos", GalaxySpace.ASSET_PREFIX, 8.0f)
        BodiesRegistry.setOrbitData((phobosMars)!!, 1.0f, 0.0017f, 100f)
        BodiesRegistry.setAtmosphere((phobosMars)!!, false, false, false, -1.6f, 0.0f, 0.0f)
        BodiesRegistry.setProviderData(
            (phobosMars)!!,
            WorldProviderPhobos::class.java,
            GSConfigDimensions.dimensionIDPhobos,
            2,
            ACBiome.ACSpace
        )
        BodiesRegistry.setPlanetData(phobosMars, 0f, 12000, 0.066f, true)
        if (GSConfigDimensions.enablePhobos) GalaxyRegistry.registerMoon((phobosMars)!!)

        if (GSConfigDimensions.enableJupiter || GSConfigDimensions.enableJupiterMoons) {
            ioJupiter = BodiesRegistry.registerExMoon((planetJupiter)!!, "io", GalaxySpace.ASSET_PREFIX, 10f)
            BodiesRegistry.setOrbitData((ioJupiter)!!, 1.0f, 0.0017f, 50f)
            BodiesRegistry.setAtmosphere((ioJupiter)!!, false, false, false, -5.8f, 0.0f, 0.0f)
            BodiesRegistry.setPlanetData(ioJupiter, 0f, 42000, 0.052f, true)
            BodiesRegistry.setProviderData(
                (ioJupiter)!!,
                WorldProviderIo::class.java,
                GSConfigDimensions.dimensionIDIo,
                4,
                ACBiome.ACSpace,
                ACBiome.ACSpaceLowHills,
                ACBiome.ACSpaceLowPlains
            )
            if (GSConfigDimensions.enableIo) GalaxyRegistry.registerMoon((ioJupiter)!!)

            europaJupiter = BodiesRegistry.registerExMoon((planetJupiter)!!, "europa", GalaxySpace.ASSET_PREFIX, 15f)
            BodiesRegistry.setOrbitData((europaJupiter)!!, Math.PI.toFloat(), 0.0017f, 100f)
            BodiesRegistry.setAtmosphere((europaJupiter)!!, false, false, false, -5.8f, 0.0f, 0.0f)
            BodiesRegistry.setPlanetData(europaJupiter, 0.2f, 0f, 58000L, 0.062f, true)
            BodiesRegistry.setProviderData(
                (europaJupiter)!!,
                WorldProviderEuropa_WE::class.java,
                GSConfigDimensions.dimensionIDEuropa,
                4
            )
            if (GSConfigDimensions.enableEuropa) GalaxyRegistry.registerMoon((europaJupiter)!!)

            ganymedeJupiter = BodiesRegistry.registerExMoon((planetJupiter)!!, "ganymede", GalaxySpace.ASSET_PREFIX, 20f)
            BodiesRegistry.setOrbitData((ganymedeJupiter)!!, Math.PI.toFloat() / 2, 0.0017f, 150f)
            BodiesRegistry.setAtmosphere((ganymedeJupiter)!!, false, false, false, -5.8f, 0.0f, 0.0f)
            BodiesRegistry.setPlanetData(ganymedeJupiter, 0f, 102000L, 0.057f, false)
            BodiesRegistry.setProviderData(
                (ganymedeJupiter)!!,
                WorldProviderGanymede::class.java,
                GSConfigDimensions.dimensionIDGanymede,
                4
            )
            if (GSConfigDimensions.enableGanymede) GalaxyRegistry.registerMoon((ganymedeJupiter)!!)

            callistoJupiter = BodiesRegistry.registerExMoon((planetJupiter)!!, "callisto", GalaxySpace.ASSET_PREFIX, 30f)
            BodiesRegistry.setOrbitData((callistoJupiter)!!, Math.PI.toFloat() / 3, 0.0017f, 200f)
            BodiesRegistry.setAtmosphere((callistoJupiter)!!, false, false, false, -5.8f, 0.0f, 0.0f)
            BodiesRegistry.setPlanetData(callistoJupiter, 0f, 154000L, 0.054f, false)
            BodiesRegistry.setProviderData(
                (callistoJupiter)!!,
                WorldProviderCallisto::class.java,
                GSConfigDimensions.dimensionIDCallisto,
                4
            )
            if (GSConfigDimensions.enableCallisto) GalaxyRegistry.registerMoon((callistoJupiter)!!)
        }
        if (GSConfigDimensions.enableSaturn || GSConfigDimensions.enableSaturnMoons) {
            enceladusSaturn = BodiesRegistry.registerExMoon((planetSaturn)!!, "enceladus", GalaxySpace.ASSET_PREFIX, 15f)
            BodiesRegistry.setOrbitData((enceladusSaturn)!!, Math.PI.toFloat() / 3, 0.0017f, 50f)
            BodiesRegistry.setAtmosphere((enceladusSaturn)!!, false, false, false, -8.0f, 0.0f, 0.0f)
            BodiesRegistry.setPlanetData(enceladusSaturn, 0f, 32000L, 0.058f, true)
            BodiesRegistry.setProviderData(
                (enceladusSaturn)!!,
                if (GSConfigCore.enableWorldEngine) WorldProviderEnceladus_WE::class.java else WorldProviderEnceladus::class.java,
                GSConfigDimensions.dimensionIDEnceladus,
                5
            )
            if (GSConfigDimensions.enableEnceladus) GalaxyRegistry.registerMoon((enceladusSaturn)!!)

            titanSaturn = BodiesRegistry.registerExMoon((planetSaturn)!!, "titan", GalaxySpace.ASSET_PREFIX, 35f)
            BodiesRegistry.setClassBody(titanSaturn, ClassBody.TITAN)
            BodiesRegistry.setOrbitData((titanSaturn)!!, Math.PI.toFloat() / 5, 0.0017f, 280f)
            BodiesRegistry.setAtmosphere((titanSaturn)!!, false, true, false, -5.9f, 1.0f, 0.0f)
            BodiesRegistry.setPlanetData(titanSaturn, 0f, 105500L, 0.058f, false)
            BodiesRegistry.setProviderData(
                (titanSaturn)!!,
                WorldProviderTitan::class.java,
                GSConfigDimensions.dimensionIDTitan,
                5
            )
            titanSaturn!!.atmosphereComponent(EnumAtmosphericGas.NITROGEN)
            if (GSConfigDimensions.enableTitan) GalaxyRegistry.registerMoon((titanSaturn)!!)
        }
        if (GSConfigDimensions.enableUranus || GSConfigDimensions.enableUranusMoons) {
            mirandaUranus = BodiesRegistry.registerExMoon((planetUranus)!!, "miranda", GalaxySpace.ASSET_PREFIX, 10f)
            BodiesRegistry.setOrbitData((mirandaUranus)!!, Math.PI.toFloat(), 0.0017f, 20f)
            BodiesRegistry.setAtmosphere((mirandaUranus)!!, false, false, false, -7.2f, 0.0f, 0.0f)
            BodiesRegistry.setPlanetData(mirandaUranus, 0f, 33500L, 0.057f, true)
            BodiesRegistry.setProviderData(
                (mirandaUranus)!!,
                WorldProviderMiranda::class.java,
                GSConfigDimensions.dimensionIDMiranda,
                5,
                ACBiome.ACSpace,
                ACBiome.ACSpaceLowPlains,
                ACBiome.ACSpaceMidHills
            )
            if (GSConfigDimensions.enableMiranda) GalaxyRegistry.registerMoon((mirandaUranus)!!)
        }
        if (GSConfigDimensions.enableNeptune || GSConfigDimensions.enableNeptuneMoons) {
            tritonNeptune = BodiesRegistry.registerExMoon((planetNeptune)!!, "triton", GalaxySpace.ASSET_PREFIX, 25f)
            BodiesRegistry.setOrbitData((tritonNeptune)!!, Math.PI.toFloat(), 0.0017f, -200f)
            BodiesRegistry.setAtmosphere((tritonNeptune)!!, false, false, false, -7.9f, 0.0f, 0.0f)
            BodiesRegistry.setPlanetData(tritonNeptune, 0f, 145200L, 0.059f, true)
            BodiesRegistry.setProviderData(
                (tritonNeptune)!!,
                WorldProviderTriton_WE::class.java,
                GSConfigDimensions.dimensionIDTriton,
                6
            )
            if (GSConfigDimensions.enableTriton) GalaxyRegistry.registerMoon((tritonNeptune)!!)
        }

        marsSpaceStation = Satellite("spacestation.mars").setParentBody(MarsModule.planetMars)
        marsSpaceStation!!.setRelativeSize(0.2667f)
        marsSpaceStation!!.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(5.5f, 5.5f))
        marsSpaceStation!!.setRelativeOrbitTime(20.0f)
        if (GSConfigDimensions.enableMarsSpaceStation) {
            marsSpaceStation!!.setTierRequired((MarsModule.planetMars)!!.getTierRequirement())
            marsSpaceStation!!.setDimensionInfo(
                GSConfigDimensions.idDimensionMarsOrbit,
                GSConfigDimensions.idDimensionMarsOrbitStatic,
                WorldProviderMarsSS::class.java
            )
            marsSpaceStation!!.setBodyIcon(ResourceLocation("galacticraftcore:textures/gui/celestialbodies/space_station.png"))
            marsSpaceStation!!.setBiomeInfo(ACBiome.ACSpace)
            marsSpaceStation!!.setAtmosphere(AtmosphereInfo(false, false, false, -1.8f, 0.0f, 0.0f))
        }

        venusSpaceStation = Satellite("spacestation.venus").setParentBody(VenusModule.planetVenus)
        venusSpaceStation!!.setRelativeSize(0.2667f)
        venusSpaceStation!!.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(5.5f, 5.5f))
        venusSpaceStation!!.setRelativeOrbitTime(20.0f)
        if (GSConfigDimensions.enableVenusSpaceStation) {
            venusSpaceStation!!.setTierRequired((VenusModule.planetVenus)!!.getTierRequirement())
            venusSpaceStation!!.setDimensionInfo(
                GSConfigDimensions.idDimensionVenusOrbit,
                GSConfigDimensions.idDimensionVenusOrbitStatic,
                WorldProviderVenusSS::class.java
            )
            venusSpaceStation!!.setBodyIcon(ResourceLocation("galacticraftcore:textures/gui/celestialbodies/space_station.png"))
            venusSpaceStation!!.addChecklistKeys("equip_oxygen_suit", "equip_parachute")
            venusSpaceStation!!.setBiomeInfo(ACBiome.ACSpace)
            venusSpaceStation!!.atmosphereComponent(EnumAtmosphericGas.CO2)
                .atmosphereComponent(EnumAtmosphericGas.NITROGEN)
            venusSpaceStation!!.setAtmosphere(AtmosphereInfo(false, false, false, 0.8f, 0.4f, 0.0f))
        }

        /*
		jupiterSpaceStation = new Satellite("spacestation.jupiter").setParentBody(planetJupiter);
		jupiterSpaceStation.setRelativeSize(0.2667F);
		jupiterSpaceStation.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(5.5F, 5.5F));
		jupiterSpaceStation.setRelativeOrbitTime(20.0F);
		if(GSConfigDimensions.enableJupiterSpaceStation) {
			jupiterSpaceStation.setTierRequired(VenusModule.planetVenus.getTierRequirement());
			jupiterSpaceStation.setDimensionInfo(GSConfigDimensions.idDimensionJupiterOrbit, GSConfigDimensions.idDimensionJupiterOrbitStatic, WorldProviderJupiterSS.class);
			jupiterSpaceStation.setBodyIcon(new ResourceLocation("galacticraftcore:textures/gui/celestialbodies/space_station.png"));
			jupiterSpaceStation.addChecklistKeys("equip_oxygen_suit", "equip_parachute");
			jupiterSpaceStation.setBiomeInfo(ACBiome.ACSpace);
			jupiterSpaceStation.atmosphereComponent(EnumAtmosphericGas.CO2).atmosphereComponent(EnumAtmosphericGas.NITROGEN);
			jupiterSpaceStation.setAtmosphere(new AtmosphereInfo(false, false, false, 0.8F, 0.4F, 0.0F));
		}
			*/
        //GalaxyRegistry.getRegisteredPlanets().remove(MarsModule.planetMars.getName());
        //GalaxyRegistry.getRegisteredPlanetIDs().remove(MarsModule.planetMars.getName());
        if (GalaxySpace.debug) {
            planetTest = BodiesRegistry.registerExPlanet((sol)!!, "test_planet", GalaxySpace.ASSET_PREFIX, 5.0f)
            BodiesRegistry.setOrbitData((planetTest)!!, Math.PI.toFloat() + 1.64f, 1.0f, 392.9f)
            BodiesRegistry.setAtmosphere((planetTest)!!, false, false, false, -12.0f, 0.0f, 0.0f)
            BodiesRegistry.setPlanetData(planetTest, 0f, 12000, BodiesRegistry.calculateGravity(2.02f), true)
            BodiesRegistry.setProviderData(
                (planetTest)!!,
                WorldProviderTest_WE2::class.java,
                GSConfigDimensions.dimensionIDTest,
                6,
                ACBiome.ACSpace
            )
            //GalaxyRegistry.registerPlanet(planetTest);
        }
        registryteleport()
    }

    override fun init(event: FMLInitializationEvent?) {
        // TODO Rings ---------------------------------

        (VenusModule.planetVenus)!!.setAtmosphere(AtmosphereInfo(false, true, true, 5.0f, 2.0f, 54.0f))
        (VenusModule.planetVenus)!!.atmosphereComponent(EnumAtmosphericGas.CO2)
            .atmosphereComponent(EnumAtmosphericGas.NITROGEN)

        (VenusModule.planetVenus)!!.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(0.745f, 0.74f))
            .setRingColorRGB(0.0f, 0.4f, 0.9f)
        (GalacticraftCore.satelliteSpaceStation)!!.setRingColorRGB(0.0f, 0.4f, 0.9f)
        //GalacticraftCore.planetOverworld.setRingColorRGB(0.0F, 1.1F, 0.0F);
        (GalacticraftCore.moonMoon)!!.setRingColorRGB(0.0f, 0.4f, 0.9f)
        (MarsModule.planetMars)!!.setRingColorRGB(0.0f, 0.4f, 0.9f)
        if (GSConfigCore.enableWorldEngine && GSConfigCore.enableMarsWorldEngine) {
            (MarsModule.planetMars)!!.setDimensionInfo(
                ConfigManagerMars.dimensionIDMars,
                WorldProviderMars_WE::class.java,
                true
            )
            (MarsModule.planetMars)!!.setAtmosphere(AtmosphereInfo(false, false, false, -2.0f, 1.0f, 0.1f))
            (MarsModule.planetMars)!!.atmosphereComponent(EnumAtmosphericGas.CO2)
                .atmosphereComponent(EnumAtmosphericGas.ARGON).atmosphereComponent(EnumAtmosphericGas.NITROGEN)
        }
        (AsteroidsModule.planetAsteroids)!!.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(1.75f, 1.75f))

        // --------------------------------------------
        // TODO Overworld -----------------------------
        if (GSConfigWorld.enableOverworldOres) {
            if (!CompatibilityManager.isTConstructLoaded) GameRegistry.registerWorldGenerator(
                OreGenerator(
                    GSBlocks.OVERWORLD_ORES.getDefaultState()
                        .withProperty(BlockOres.BASIC_TYPE, EnumBlockOres.COBALT),
                    6,
                    0,
                    60,
                    5,
                    Blocks.STONE.getDefaultState(),
                    0
                ), 0
            )

            //GameRegistry.registerWorldGenerator(new OreGenerator(GSBlocks.OVERWORLD_ORES.getDefaultState().withProperty(BlockOres.BASIC_TYPE, BlockOres.EnumBlockOres.NICKEL), 6, 0, 45, 4, Blocks.STONE.getDefaultState(), 0), 1);
            GameRegistry.registerWorldGenerator(NickelGenerator(), 1)
        }
        if (GSConfigWorld.enableNatureGasGen) GameRegistry.registerWorldGenerator(
            WorldGenNaturalGas(
                6,
                35,
                45,
                Blocks.STONE.getDefaultState(),
                10,
                0
            ), 1
        )

        // --------------------------------------------
        /*	if(GSConfigCore.enableOresGeneration)
		{
			// TODO Moon ----------------------------------
			GameRegistry.registerWorldGenerator(new OreGenerator(GSBlocks.OVERWORLD_ORES.getStateFromMeta(2), 4, 0, 45, 4, GCBlocks.blockMoon.getStateFromMeta(4), ConfigManagerCore.idDimensionMoon), 4);
			// --------------------------------------------
		}*/
        registerDungeonLoot()


        //GalaxyRegistry.getRegisteredPlanets().forEach((s, p) -> GalaxySpace.debug("Planet: " + s  + " | " + p.getWorldProvider()));
        SchematicRegistry.registerSchematicRecipe(SchematicOxTank())

        if (GSConfigCore.enableAdvancedRocketCraft) {
            SchematicRegistry.registerSchematicRecipe(SchematicCone())
            SchematicRegistry.registerSchematicRecipe(SchematicBody())
            SchematicRegistry.registerSchematicRecipe(SchematicEngine())
            SchematicRegistry.registerSchematicRecipe(SchematicBooster())
            SchematicRegistry.registerSchematicRecipe(SchematicFins())

            for (page: ISchematicPage? in SchematicRegistry.schematicRecipes) {
                if (page is SchematicTier2Rocket) {
                    SchematicRegistry.schematicRecipes.remove(page)
                    break
                }
            }

            for (page: ISchematicPage? in SchematicRegistry.schematicRecipes) {
                if (page is SchematicTier3Rocket) {
                    SchematicRegistry.schematicRecipes.remove(page)
                    break
                }
            }
            SchematicRegistry.registerSchematicRecipe(galaxyspace.systems.SolarSystem.planets.overworld.schematics.SchematicTier2Rocket())

            GCItems.hiddenItems.add(ItemStack(MarsItems.schematic, 1, 0).getItem())
        }

        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_OXYGEN_TANK_4,
            EnumExtendedInventorySlot.LEFT_TANK,
            GSItems.OXYGENTANK_TIER_4
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_OXYGEN_TANK_4,
            EnumExtendedInventorySlot.RIGHT_TANK,
            GSItems.OXYGENTANK_TIER_4
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_OXYGEN_TANK_5,
            EnumExtendedInventorySlot.LEFT_TANK,
            GSItems.OXYGENTANK_TIER_5
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_OXYGEN_TANK_5,
            EnumExtendedInventorySlot.RIGHT_TANK,
            GSItems.OXYGENTANK_TIER_5
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_OXYGEN_TANK_6,
            EnumExtendedInventorySlot.LEFT_TANK,
            GSItems.OXYGENTANK_TIER_6
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_OXYGEN_TANK_6,
            EnumExtendedInventorySlot.RIGHT_TANK,
            GSItems.OXYGENTANK_TIER_6
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_OXYGEN_TANK_EPP,
            EnumExtendedInventorySlot.LEFT_TANK,
            GSItems.OXYGENTANK_TIER_EPP
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_OXYGEN_TANK_EPP,
            EnumExtendedInventorySlot.RIGHT_TANK,
            GSItems.OXYGENTANK_TIER_EPP
        )

        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_TEMP_SHIELD_CONTROLLER,
            EnumExtendedInventorySlot.SHIELD_CONTROLLER,
            ItemStack(GSItems.BASIC, 1, 16)
        )

        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_THERMAL_PADDING_T3_HELMET,
            EnumExtendedInventorySlot.THERMAL_HELMET,
            ItemStack(GSItems.THERMAL_PADDING_3, 1, 0)
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_THERMAL_PADDING_T3_CHESTPLATE,
            EnumExtendedInventorySlot.THERMAL_CHESTPLATE,
            ItemStack(GSItems.THERMAL_PADDING_3, 1, 1)
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_THERMAL_PADDING_T3_LEGGINGS,
            EnumExtendedInventorySlot.THERMAL_LEGGINGS,
            ItemStack(GSItems.THERMAL_PADDING_3, 1, 2)
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_THERMAL_PADDING_T3_BOOTS,
            EnumExtendedInventorySlot.THERMAL_BOOTS,
            ItemStack(GSItems.THERMAL_PADDING_3, 1, 3)
        )

        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_THERMAL_PADDING_T4_HELMET,
            EnumExtendedInventorySlot.THERMAL_HELMET,
            ItemStack(GSItems.THERMAL_PADDING_4, 1, 0)
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_THERMAL_PADDING_T4_CHESTPLATE,
            EnumExtendedInventorySlot.THERMAL_CHESTPLATE,
            ItemStack(GSItems.THERMAL_PADDING_4, 1, 1)
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_THERMAL_PADDING_T4_LEGGINGS,
            EnumExtendedInventorySlot.THERMAL_LEGGINGS,
            ItemStack(GSItems.THERMAL_PADDING_4, 1, 2)
        )
        GalacticraftRegistry.registerGear(
            GSConstants.GEAR_ID_THERMAL_PADDING_T4_BOOTS,
            EnumExtendedInventorySlot.THERMAL_BOOTS,
            ItemStack(GSItems.THERMAL_PADDING_4, 1, 3)
        )

        if (GSConfigDimensions.enableVenusSpaceStation) {
            GSDimensions.VENUS_SS = GalacticraftRegistry.registerDimension(
                "Venus Space Station",
                "_venus_orbit",
                GSConfigDimensions.idDimensionVenusOrbit,
                WorldProviderVenusSS::class.java,
                false
            )
            if (GSDimensions.VENUS_SS == null) {
                (GalaxySpace.instance)!!.debug("Failed to register space station dimension type with ID " + GSConfigDimensions.idDimensionVenusOrbit)
            }
            GSDimensions.VENUS_SS_KEEPLOADED = GalacticraftRegistry.registerDimension(
                "Venus Space Station",
                "_venus_orbit",
                GSConfigDimensions.idDimensionVenusOrbitStatic,
                WorldProviderVenusSS::class.java,
                true
            )
        }

        if (GSConfigDimensions.enableMarsSpaceStation) {
            GSDimensions.MARS_SS = GalacticraftRegistry.registerDimension(
                "Mars Space Station",
                "_mars_orbit",
                GSConfigDimensions.idDimensionMarsOrbit,
                WorldProviderMarsSS::class.java,
                false
            )
            if (GSDimensions.MARS_SS == null) {
                (GalaxySpace.instance)!!.debug("Failed to register space station dimension type with ID " + GSConfigDimensions.idDimensionMarsOrbit)
            }
            GSDimensions.MARS_SS_KEEPLOADED = GalacticraftRegistry.registerDimension(
                "Mars Space Station",
                "_mars_orbit",
                GSConfigDimensions.idDimensionMarsOrbitStatic,
                WorldProviderMarsSS::class.java,
                true
            )
        }


        val data: BodiesData = BodiesData(TypeBody.STAR).setStarType(StarType.DWARF).setStarColor(StarColor.YELLOW)
        data.setStarHabitableZone(1.0f, 0.22f)
        BodiesRegistry.registerBodyData((GalacticraftCore.solarSystemSol)!!.getMainStar(), data)
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        if (GSConfigCore.enableUnreachable) {
            //MARS

            deimosMars = registerDummyMoon("deimos", MarsModule.planetMars, 16.0f)
            if (deimosMars != null) BodiesRegistry.setOrbitData((deimosMars)!!, 1.0f, 0.0017f, 300f)
            if (GSConfigDimensions.enableSaturn || GSConfigDimensions.enableSaturnMoons) {
                //SATURN MOONS
                mimasSaturn = registerDummyMoon("mimas", planetSaturn, 10f)
                if (mimasSaturn != null) BodiesRegistry.setOrbitData((mimasSaturn)!!, Math.PI.toFloat() / 2, 0.0017f, 20f)

                tethysSaturn = registerDummyMoon("tethys", planetSaturn, 20f)
                if (tethysSaturn != null) BodiesRegistry.setOrbitData((tethysSaturn)!!, Math.PI.toFloat(), 0.0017f, 120f)

                dioneSaturn = registerDummyMoon("dione", planetSaturn, 25f)
                if (dioneSaturn != null) BodiesRegistry.setOrbitData((dioneSaturn)!!, Math.PI.toFloat() / 4, 0.0017f, 180f)

                rheyaSaturn = registerDummyMoon("rheya", planetSaturn, 30f)
                if (rheyaSaturn != null) BodiesRegistry.setOrbitData((rheyaSaturn)!!, Math.PI.toFloat() / 3, 0.0017f, 220f)

                iapetusSaturn = registerDummyMoon("iapetus", planetSaturn, 40f)
                if (iapetusSaturn != null) BodiesRegistry.setOrbitData((iapetusSaturn)!!, Math.PI.toFloat(), 0.0017f, 350f)
            }
            if (GSConfigDimensions.enableUranus || GSConfigDimensions.enableUranusMoons) {
                //URANUS
                arielUranus = registerDummyMoon("ariel", planetUranus, 15f)
                if (arielUranus != null) BodiesRegistry.setOrbitData((arielUranus)!!, Math.PI.toFloat() / 2, 0.0017f, 50f)

                umbrielUranus = registerDummyMoon("umbriel", planetUranus, 20f)
                if (umbrielUranus != null) BodiesRegistry.setOrbitData(
                    (umbrielUranus)!!,
                    Math.PI.toFloat() / 3,
                    0.0017f,
                    120f
                )

                titaniaUranus = registerDummyMoon("titania", planetUranus, 25f)
                if (titaniaUranus != null) BodiesRegistry.setOrbitData(
                    (titaniaUranus)!!,
                    Math.PI.toFloat() / 4,
                    0.0017f,
                    180f
                )

                oberonUranus = registerDummyMoon("oberon", planetUranus, 30f)
                if (oberonUranus != null) BodiesRegistry.setOrbitData(
                    (oberonUranus)!!,
                    Math.PI.toFloat() / 4,
                    0.0017f,
                    220f
                )
            }
            if (GSConfigDimensions.enableNeptune || GSConfigDimensions.enableNeptuneMoons) {
                proteusNeptune = registerDummyMoon("proteus", planetNeptune, 10f)
                if (proteusNeptune != null) BodiesRegistry.setOrbitData((proteusNeptune)!!, Math.PI.toFloat(), 0.0017f, 50f)
            }
            if (GSConfigDimensions.enablePluto) {
                //PLUTO
                charonPluto = registerDummyMoon("charon", planetPluto, 15f)
                if (charonPluto != null) BodiesRegistry.setOrbitData((charonPluto)!!, Math.PI.toFloat(), 0.0017f, 50f)
            }
        }

        ItemSchematics.registerSchematicItems()

        GSDimensions.MERCURY = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDMercury)
        GSDimensions.CERES = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDCeres)
        GSDimensions.PLUTO = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDPluto)

        GSDimensions.PHOBOS = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDPhobos)
        GSDimensions.IO = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDIo)
        GSDimensions.EUROPA = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDEuropa)
        GSDimensions.GANYMEDE = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDGanymede)
        GSDimensions.CALLISTO = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDCallisto)

        GSDimensions.ENCELADUS = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDEnceladus)
        GSDimensions.TITAN = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDTitan)
        GSDimensions.MIRANDA = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDMiranda)
        GSDimensions.TRITON = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDTriton)

        GSDimensions.KUIPER_BELT = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDKuiperBelt)
        GSDimensions.HAUMEA = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDHaumea)

        GSDimensions.TEST = WorldUtil.getDimensionTypeById(GSConfigDimensions.dimensionIDTest)


        if ((event)!!.getSide() == Side.CLIENT) {
            for (bodies in (GalaxyRegistry.getRegisteredPlanets())!!.entries) {
                val img: ResourceLocation =
                    ResourceLocation(GalaxySpace.TEXTURE_PREFIX + "textures/gui/backgrounds/bg_" + bodies.value!!.getName() + ".png")

                try {
                    if (Minecraft.getMinecraft().getResourceManager().getResource(img) != null) {
                        BodiesRegistry.addTeleportData(bodies.value!!.getDimensionID(), img)
                    }
                } catch (e: IOException) {
                }
            }

            for (bodies in (GalaxyRegistry.getRegisteredMoons())!!.entries) {
                val img: ResourceLocation =
                    ResourceLocation(GalaxySpace.TEXTURE_PREFIX + "textures/gui/backgrounds/bg_" + bodies.value!!.getName() + ".png")

                try {
                    if (Minecraft.getMinecraft().getResourceManager().getResource(img) != null) {
                        BodiesRegistry.addTeleportData(bodies.value!!.getDimensionID(), img)
                    }
                } catch (e: IOException) {
                }
            }
        }

        registrycelestial()
        GalaxyRegistry.refreshGalaxies()
    }


    private fun registerDummyMoon(name: String, parent: Planet?, distancefromcenter: Float): ExMoon? {
        for (body: CelestialBody? in ArrayList<Moon?>((GalaxyRegistry.getRegisteredMoons())!!.values)) {
            if (body is Moon && name == body.getName()) {
                if (body.getParentPlanet() === parent) {
                    return null
                }
            }
        }

        val moon: ExMoon = BodiesRegistry.registerExMoon((parent)!!, name, GalaxySpace.ASSET_PREFIX, distancefromcenter)
        GalaxyRegistry.registerMoon(moon)

        return moon
    }

    override fun registerRender() {
    }

    override fun registerVariant() {
    }

    override fun registerRecipes() {
        registerRecipesWorkBench()

        CraftingRecipesMercury.loadRecipes()
        CraftingRecipesOverworld.loadRecipes()
        CraftingRecipesMars.loadRecipes()
        CraftingRecipesHaumea.loadRecipes()
        CraftingRecipesPhobos.loadRecipes()
        CraftingRecipesIo.loadRecipes()
        CraftingRecipesEuropa.loadRecipes()
        CraftingRecipesGanymede.loadRecipes()
        CraftingRecipesMiranda.loadRecipes()


        val spaceStationRequirements: HashMap<Any?,Int> = HashMap<Any?,Int>(6, 1.0f)
        spaceStationRequirements.put("ingotTin", 32)
        spaceStationRequirements.put("ingotCopper", 64)
        spaceStationRequirements.put(ItemStack(GCItems.basicItem, 1, ItemBasic.WAFER_ADVANCED), 1)
        spaceStationRequirements.put(Items.IRON_INGOT, 24)
        spaceStationRequirements.put(ItemStack(GSItems.HDP, 1, 0), 10)
        spaceStationRequirements.put(ItemStack(GSItems.BASIC, 1, 6), 10)

        if (GSConfigDimensions.enableVenusSpaceStation) GalacticraftRegistry.registerSpaceStation(
            SpaceStationType(
                GSConfigDimensions.idDimensionVenusOrbit,
                ConfigManagerVenus.dimensionIDVenus, SpaceStationRecipe(spaceStationRequirements)
            )
        )

        spaceStationRequirements.clear()
        spaceStationRequirements.put("ingotTin", 32)
        spaceStationRequirements.put("ingotDesh", 64)
        spaceStationRequirements.put(ItemStack(GCItems.basicItem, 1, ItemBasic.WAFER_ADVANCED), 1)
        spaceStationRequirements.put(Items.IRON_INGOT, 24)
        spaceStationRequirements.put(ItemStack(GSItems.HDP, 1, 0), 10)
        spaceStationRequirements.put(ItemStack(GSItems.BASIC, 1, 6), 10)

        if (GSConfigDimensions.enableMarsSpaceStation) GalacticraftRegistry.registerSpaceStation(
            SpaceStationType(
                GSConfigDimensions.idDimensionMarsOrbit,
                ConfigManagerMars.dimensionIDMars, SpaceStationRecipe(spaceStationRequirements)
            )
        )
    }

    override fun canRegister(): Boolean {
        return true
    }

    override fun getPriority(): Int {
        return 100
    }


    companion object {
        @JvmField
        var planetMercury: Planet? = GalacticraftCore.planetMercury

        @JvmField
        var planetCeres: Planet? = null

        @JvmField
        var planetJupiter: Planet? = GalacticraftCore.planetJupiter

        @JvmField
        var planetSaturn: Planet? = GalacticraftCore.planetSaturn

        @JvmField
        var planetUranus: Planet? = GalacticraftCore.planetUranus

        @JvmField
        var planetNeptune: Planet? = GalacticraftCore.planetNeptune

        @JvmField
        var planetPluto: Planet? = null

        @JvmField
        var planetKuiperBelt: ExPlanet? = null

        @JvmField
        var planetHaumea: Planet? = null

        @JvmField
        var planetMakemake: Planet? = null

        @JvmField
        var planetEris: Planet? = null

        @JvmField
        var planetDeeDee: Planet? = null

        @JvmField
        var planetTest: Planet? = null

        @JvmField
        var phobosMars: Moon? = null

        @JvmField
        var deimosMars: Moon? = null

        @JvmField
        var ioJupiter: Moon? = null

        @JvmField
        var europaJupiter: Moon? = null

        @JvmField
        var ganymedeJupiter: Moon? = null

        @JvmField
        var callistoJupiter: Moon? = null

        @JvmField
        var mimasSaturn: Moon? = null

        @JvmField
        var enceladusSaturn: Moon? = null

        @JvmField
        var tethysSaturn: Moon? = null

        @JvmField
        var dioneSaturn: Moon? = null

        @JvmField
        var rheyaSaturn: Moon? = null

        @JvmField
        var titanSaturn: Moon? = null

        @JvmField
        var iapetusSaturn: Moon? = null

        @JvmField
        var mirandaUranus: Moon? = null

        @JvmField
        var arielUranus: Moon? = null

        @JvmField
        var umbrielUranus: Moon? = null

        @JvmField
        var titaniaUranus: Moon? = null

        @JvmField
        var oberonUranus: Moon? = null

        @JvmField
        var proteusNeptune: Moon? = null

        @JvmField
        var tritonNeptune: Moon? = null

        @JvmField
        var charonPluto: Moon? = null

        @JvmField
        var venusSpaceStation: Satellite? = null

        @JvmField
        var marsSpaceStation: Satellite? = null

        @JvmField
        var jupiterSpaceStation: Satellite? = null

        @JvmStatic
        private fun registerDungeonLoot() {
            //SCHEMATIC BOX
            if (GSConfigCore.enableAdvancedRocketCraft) {
                GalacticraftRegistry.addDungeonLoot(1, ItemStack(GSItems.BASIC, 1, 10)) //MOON
                GalacticraftRegistry.addDungeonLoot(2, ItemStack(GSItems.BASIC, 1, 10)) //MARS
            }
            //EPO
            GalacticraftRegistry.addDungeonLoot(1, ItemStack(GSItems.SCHEMATICS, 1, 5))

            /*	GalacticraftRegistry.addDungeonLoot(2, new ItemStack(GSItems.PLASMA_SWORD, 1, 0));
		GalacticraftRegistry.addDungeonLoot(2, new ItemStack(GSItems.PLASMA_PICKAXE, 1, 0));
		GalacticraftRegistry.addDungeonLoot(2, new ItemStack(GSItems.PLASMA_AXE, 1, 0));

		GalacticraftRegistry.addDungeonLoot(3, new ItemStack(GSItems.PLASMA_SWORD, 1, 0));
		GalacticraftRegistry.addDungeonLoot(3, new ItemStack(GSItems.PLASMA_PICKAXE, 1, 0));
		GalacticraftRegistry.addDungeonLoot(3, new ItemStack(GSItems.PLASMA_AXE, 1, 0));
*/
            GalacticraftRegistry.addDungeonLoot(4, ItemStack(GSItems.BASIC, 1, 16))
            GalacticraftRegistry.addDungeonLoot(4, ItemStack(GSItems.BASIC, 1, 18))

            //GalacticraftRegistry.addDungeonLoot(4, new ItemStack(GSItems.GEOLOGICAL_SCANNER, 1, 100));
            GalacticraftRegistry.addDungeonLoot(5, ItemStack(GSItems.ROCKET_MODULES, 1, 4))
            GalacticraftRegistry.addDungeonLoot(5, ItemStack(GSItems.ROCKET_MODULES, 1, 6))
        }

        @JvmStatic
        private fun registrycelestial() {
            var data: BodiesData = BodiesData(null,  /*BodiesRegistry.calculateGravity(8.88F),*/92,  /*182000,*/false)
            BodiesRegistry.registerBodyData(VenusModule.planetVenus, data)

            data = BodiesData(null,  /*BodiesRegistry.calculateGravity(10.0F),*/1,  /*24000,*/false)
            BodiesRegistry.registerBodyData(GalacticraftCore.planetOverworld, data)

            data = BodiesData(null,  /*BodiesRegistry.calculateGravity(5.37F),*/0,  /*24660,*/false)
            BodiesRegistry.registerBodyData(MarsModule.planetMars, data)

            data = BodiesData(TypeBody.ASTEROID, ClassBody.ASTEROID, 0, true)
            BodiesRegistry.registerBodyData(AsteroidsModule.planetAsteroids, data)

            ////MOONS
            data = BodiesData(TypeBody.MOON, null, 0, GSConfigCore.enableSolarRadiationOnMoon)
            BodiesRegistry.registerBodyData(GalacticraftCore.moonMoon, data)

            data = BodiesData(TypeBody.SATELLITE, null, 0, true)
            BodiesRegistry.registerBodyData(marsSpaceStation, data)

            data = BodiesData(TypeBody.MOON)
            BodiesRegistry.registerBodyData(phobosMars, data)

            if (GSConfigCore.enableUnreachable) {
                val unreachableData: BodiesData = BodiesData(TypeBody.MOON)
                BodiesRegistry.registerBodyData(oberonUranus, unreachableData)
                BodiesRegistry.registerBodyData(proteusNeptune, unreachableData)
                BodiesRegistry.registerBodyData(mimasSaturn, unreachableData)
                BodiesRegistry.registerBodyData(tethysSaturn, unreachableData)
                BodiesRegistry.registerBodyData(dioneSaturn, unreachableData)
                BodiesRegistry.registerBodyData(rheyaSaturn, unreachableData)
                BodiesRegistry.registerBodyData(iapetusSaturn, unreachableData)
                BodiesRegistry.registerBodyData(arielUranus, unreachableData)
                BodiesRegistry.registerBodyData(umbrielUranus, unreachableData)
                BodiesRegistry.registerBodyData(titaniaUranus, unreachableData)
                BodiesRegistry.registerBodyData(charonPluto, unreachableData)
            }
        }

        @JvmStatic
        private fun registryteleport() {
            GalacticraftRegistry.registerTeleportType(WorldProviderMercury::class.java, TeleportTypeMercury())

            if (GSConfigCore.enableWorldEngine && GSConfigCore.enableMarsWorldEngine) GalacticraftRegistry.registerTeleportType(
                WorldProviderMars_WE::class.java,
                TeleportTypeMars()
            )


            GalacticraftRegistry.registerTeleportType(WorldProviderCeres::class.java, TeleportTypeCeres())
            GalacticraftRegistry.registerTeleportType(WorldProviderPluto::class.java, TeleportTypePluto())
            GalacticraftRegistry.registerTeleportType(WorldProviderKuiperBelt::class.java, TeleportTypeKuiperBelt())
            GalacticraftRegistry.registerTeleportType(WorldProviderHaumea_WE::class.java, TeleportTypeHaumea())

            GalacticraftRegistry.registerTeleportType(WorldProviderTest_WE2::class.java, TeleportTypeBody())

            /*GalacticraftRegistry.registerTeleportType(WorldProviderMakemake.class, new TeleportTypeMakemake());*/
            //GalacticraftRegistry.registerTeleportType(WorldProviderEris.class, new TeleportTypeEris());

            //if(GalaxySpace.debug) GalacticraftRegistry.registerTeleportType(WorldProviderZTest.class, new WorldProviderZTest());
            GalacticraftRegistry.registerTeleportType(WorldProviderPhobos::class.java, TeleportTypeBody())

            //GalacticraftRegistry.registerTeleportType(WorldProviderDeimos.class, new WorldProviderDeimos());
            GalacticraftRegistry.registerTeleportType(WorldProviderIo::class.java, TeleportTypeIo())
            GalacticraftRegistry.registerTeleportType(WorldProviderEuropa_WE::class.java, TeleportTypeEuropa())
            GalacticraftRegistry.registerTeleportType(WorldProviderGanymede::class.java, TeleportTypeGanymede())
            GalacticraftRegistry.registerTeleportType(WorldProviderCallisto::class.java, TeleportTypeCallisto())

            GalacticraftRegistry.registerTeleportType(
                if (GSConfigCore.enableWorldEngine) WorldProviderEnceladus_WE::class.java else WorldProviderEnceladus::class.java,
                TeleportTypeEnceladus()
            )
            GalacticraftRegistry.registerTeleportType(WorldProviderTitan::class.java, TeleportTypeTitan())

            GalacticraftRegistry.registerTeleportType(WorldProviderMiranda::class.java, TeleportTypeMiranda())

            /*GalacticraftRegistry.registerTeleportType(WorldProviderOberon.class, new WorldProviderOberon());
		
		GalacticraftRegistry.registerTeleportType(WorldProviderProteus.class, new WorldProviderProteus());*/
            GalacticraftRegistry.registerTeleportType(WorldProviderTriton_WE::class.java, TeleportTypeTriton())


            if (GSConfigDimensions.enableMarsSpaceStation) {
                GalaxyRegistry.registerSatellite((marsSpaceStation)!!)
                GalacticraftRegistry.registerTeleportType(WorldProviderMarsSS::class.java, TeleportTypeMarsSS())
            }
            if (GSConfigDimensions.enableVenusSpaceStation) {
                GalaxyRegistry.registerSatellite((venusSpaceStation)!!)

                GalacticraftRegistry.registerTeleportType(WorldProviderVenusSS::class.java, TeleportTypeVenusSS())
            }
        }

        @JvmStatic
        private fun registerRecipesWorkBench() {
            SchematicConeRecipe.registerRecipeWorkBench()
            SchematicBodyRecipe.registerRecipeWorkBench()
            SchematicEngineRecipe.registerRecipeWorkBench()
            SchematicBoosterRecipe.registerRecipeWorkBench()
            SchematicFinsRecipe.registerRecipeWorkBench()
            SchematicOxTankRecipe.registerRecipeWorkBench()
            if (GSConfigCore.enableAdvancedRocketCraft) SchematicTier2Recipe.registerRecipeWorkBench()

            //GalaxySpace.debug("Reg schem recipe");
        }
    }
}
