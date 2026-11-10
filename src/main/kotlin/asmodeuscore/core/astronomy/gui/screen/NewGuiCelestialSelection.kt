package asmodeuscore.core.astronomy.gui.screen

import java.util.Map
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.Post
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.Pre
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection.StationDataGUI
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import com.google.common.collect.Lists
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.api.dimension.IAdvancedSpace
import asmodeuscore.api.space.IExBody
import org.lwjgl.opengl.GL11
import java.io.IOException
import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.api.space.ICelestialRegistry
import asmodeuscore.core.utils.ACCompatibilityManager
import asmodeuscore.core.utils.UtilsGC
import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.core.astronomy.SpaceData
import asmodeuscore.core.astronomy.gui.screen.GuiCustomTeleporting
import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection
import asmodeuscore.core.astronomy.BodiesData
import java.nio.FloatBuffer
import org.lwjgl.BufferUtils
import asmodeuscore.AsmodeusCore
import asmodeuscore.core.network.packet.ACPacketSimple
import asmodeuscore.core.proxy.ClientProxy
import asmodeuscore.core.utils.Utils
import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.Satellite
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.galaxies.Star
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.api.world.SpaceStationType
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.settings.GameSettings
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraft.world.WorldProvider
import net.minecraft.world.WorldProviderSurface
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import java.lang.Exception
import java.nio.Buffer
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.HashMap
import java.util.LinkedList
import java.util.Locale
import java.util.Random
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class NewGuiCelestialSelection constructor(
    mapMode: Boolean,
    possibleBodies: MutableList<CelestialBody?>?,
    canCreateStations: Boolean,
    @JvmField var data: SpaceData?
) : GuiCelestialSelection(mapMode, possibleBodies, canCreateStations) {
    @JvmField
    protected var catalogViewState: EnumView = EnumView.GS
    private var galaxy: String? =
        ((GalacticraftCore.planetOverworld)!!.getParentSolarSystem())!!.getUnlocalizedParentGalaxyName()
    private val galaxylist: MutableList<String?> = ArrayList<String?>()
    private val starlist: MutableList<SolarSystem> = ArrayList<SolarSystem>()
    private var showStarList = false
    private var showGalaxyList = false
    private val enableNewTierSystem: Boolean
    private val isometx: Double
    private val isometz = 45.0
    private val mindistance = 900.0
    private var currenttier = 0
    private var tierneed: Int
    private var timer = 0.0
    private var traveltime = 0.0
    private var enableTraveltime = false
    private var coef: Int
    private var canTravel = true
    private var small_page = 0
    private var engine: Engine_Type?
    private var fuelSet = 0
    private var fuelRocketLevel = 0
    private var xImgOffset = 0.0
    private var yImgOffset = 0.0
    private var scroll = 0
    private val max_scroll = 8
    private var nebula_color: Vec3d
    private var nebula_img = 0
    private var isSecondPassenger = false
    private val celestialBodyTiers: MutableMap<CelestialBody?,Int>

    init {
        this.enableNewTierSystem = AsmodeusConfig.enableNewTierSystem
        this.isometx = if (AsmodeusConfig.enable2DGalaxyMap) 0.0 else 55.0
        this.tierneed = -1
        this.engine = Engine_Type.FUEL_ENGINE
        this.nebula_color = Vec3d(1.0, 1.0, 1.0)
        this.celestialBodyTiers = HashMap<CelestialBody?,Int>()
        var tier: Int = 0
        if (this.data != null) {
            tier = ((this.data)!!.getValue("rocket_tier") as kotlin.Int?)!!
            this.engine = this.data!!.getValue("rocket_engine") as Engine_Type?
            this.fuelRocketLevel = ((this.data)!!.getValue("rocket_count_fuel") as kotlin.Int?)!!
            this.isSecondPassenger = ((this.data)!!.getValue("second_passenger") as kotlin.Boolean?)!!
        }

        for (config_body in (AsmodeusConfig.celestialBodyTiers)!!) {
            val split_config: Array<String?> =
                (config_body)!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val body_config: CelestialBody? = GalaxyRegistry.getCelestialBodyFromUnlocalizedName(split_config[0])
            val tier_config: Int = split_config[1]!!.toInt()
            this.celestialBodyTiers.put(body_config, tier_config)
        }

        this.currenttier = tier
        this.coef = AsmodeusConfig.speedTimeTravel - 1 + tier
        if (this.engine != null) {
            when (this.engine) {
                Engine_Type.FUEL_ENGINE -> {}
                Engine_Type.ION_ENGINE -> this.coef += 4
                Engine_Type.PLASMA_ENGINE -> this.coef += 8
                Engine_Type.SUBLIGHT_ENGINE -> this.coef += 100
                Engine_Type.BLACKHOLE_ENGINE -> this.coef += 1000
                else -> {}
            }
        }

        val rand: Random = Random()
        this.nebula_img = rand.nextInt(2)
        this.nebula_color = Vec3d(rand.nextFloat().toDouble(), rand.nextFloat().toDouble(), rand.nextFloat().toDouble())
    }

    override fun initGui() {
        BORDER_SIZE = this.width / 65
        BORDER_EDGE_SIZE = BORDER_SIZE / 4
        this.refreshBodies()
    }

    override fun updateScreen() {
        this.ticksSinceMenuOpen++
        this.ticksTotal++
        if (this.ticksSinceMenuOpen < 20) {
            Mouse.setGrabbed(false)
        }

        if (this.selectedBody != null) {
            this.ticksSinceSelection++
        }

        if (this.selectedBody == null && this.ticksSinceUnselection >= 0) {
            this.ticksSinceUnselection++
        }

        if (!this.renamingSpaceStation && (this.selectedBody == null || !this.isZoomed())) {
            if (GameSettings.isKeyDown(KeyHandlerClient.leftKey)) {
                this.translation.x += -2.0f
                this.translation.y += -2.0f
                if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
                    this.xImgOffset += -1.0
                }
            }

            if (GameSettings.isKeyDown(KeyHandlerClient.rightKey)) {
                this.translation.x += 2.0f
                this.translation.y += 2.0f
                if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
                    this.xImgOffset++
                }
            }

            if (GameSettings.isKeyDown(KeyHandlerClient.upKey)) {
                this.translation.x += 2.0f
                this.translation.y += -2.0f
                if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
                    this.yImgOffset += -1.0
                }
            }

            if (GameSettings.isKeyDown(KeyHandlerClient.downKey)) {
                this.translation.x += -2.0f
                this.translation.y += 2.0f
                if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
                    this.yImgOffset++
                }
            }
        }

        if (this.xImgOffset > 20.0) {
            this.xImgOffset = 20.0
        }

        if (this.xImgOffset < -150.0) {
            this.xImgOffset = -150.0
        }

        if (this.yImgOffset > 20.0) {
            this.yImgOffset = 20.0
        }

        if (this.yImgOffset < -80.0) {
            this.yImgOffset = -80.0
        }
    }

    private fun refreshBodies() {
        val player: EntityPlayerSP? = Minecraft.getMinecraft().player
        this.bodiesToRender.clear()
        this.galaxylist.clear()
        this.starlist.clear()

        for (solarSystem in (GalaxyRegistry.getRegisteredSolarSystems())!!.values) {
            if (!this.galaxylist.contains((solarSystem)!!.getUnlocalizedParentGalaxyName())) {
                this.galaxylist.add((solarSystem)!!.getUnlocalizedParentGalaxyName())
            }

            if ((solarSystem)!!.getUnlocalizedParentGalaxyName() == this.galaxy) {
                if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
                    if (UtilsGC.hasUnlocks(player, solarSystem)) {
                        if ((solarSystem)!!.getUnlocalizedParentGalaxyName() == this.galaxy) {
                            this.starlist.add((solarSystem)!!)
                        }

                        this.bodiesToRender.add(((solarSystem)!!.getMainStar())!!)
                    }
                } else {
                    if ((solarSystem)!!.getUnlocalizedParentGalaxyName() == this.galaxy) {
                        this.starlist.add((solarSystem)!!)
                    }

                    this.bodiesToRender.add(((solarSystem)!!.getMainStar())!!)
                }
            }
        }

        for (planet: Planet in (GalaxyRegistry.getRegisteredPlanets())!!.values) {
            if ((planet.getParentSolarSystem())!!.getUnlocalizedParentGalaxyName() == this.galaxy) {
                if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
                    if (UtilsGC.isReasearched(player, planet)) {
                        this.bodiesToRender.add(planet)
                    }
                } else {
                    this.bodiesToRender.add(planet)
                }
            }
        }

        for (moon: Moon in (GalaxyRegistry.getRegisteredMoons())!!.values) {
            if (((moon.getParentPlanet())!!.getParentSolarSystem())!!.getUnlocalizedParentGalaxyName() == this.galaxy) {
                if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
                    if (UtilsGC.isReasearched(player, moon.getParentPlanet()) && UtilsGC.isReasearched(player, moon)) {
                        this.bodiesToRender.add(moon)
                    }
                } else {
                    this.bodiesToRender.add(moon)
                }
            }
        }

        for (satellite: Satellite in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
            if (((satellite.getParentPlanet())!!.getParentSolarSystem())!!.getUnlocalizedParentGalaxyName() == this.galaxy) {
                if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
                    if (UtilsGC.isReasearched(player, satellite.getParentPlanet())) {
                        this.bodiesToRender.add(satellite)
                    }
                } else {
                    this.bodiesToRender.add(satellite)
                }
            }
        }

        for (bodies in BodiesRegistry.getBodies()) {
            for (body: CelestialBody? in (bodies)!!.values) {
                if (body is ICelestialRegistry) {
                    if (body is IChildBody) {
                        if ((((body as IChildBody).getParentPlanet())!!.getParentSolarSystem())!!
                                .getUnlocalizedParentGalaxyName() == this.galaxy
                            && (body as ICelestialRegistry).canRegistry()
                        ) {
                            this.bodiesToRender.add(body)
                        }
                    } else if (((body as ICelestialRegistry).getParentSolarSystem())!!
                            .getUnlocalizedParentGalaxyName() == this.galaxy
                        && (body as ICelestialRegistry).canRegistry()
                    ) {
                        this.bodiesToRender.add(body)
                    }
                }
            }
        }

        Collections.sort<SolarSystem?>(this.starlist, object : Comparator<SolarSystem> {
            override fun compare(lhs: SolarSystem, rhs: SolarSystem): Int {
                return lhs.getName().compareTo(rhs.getName())
            }
        })
    }

    override fun drawButtons(mousePosX: Int, mousePosY: Int) {
        val travel_modifier: Int = 200
        if (this.coef < 1) {
            this.coef = 1
        }

        if (!this.mapMode && this.ticksSinceSelection % 1 == 0 && !this.mc.player.capabilities.isCreativeMode && this.timer < this.traveltime && this.enableTraveltime) {
            this.timer = this.timer + (1 * this.coef).toDouble()
        }

        if (this.selectedBody != null) {
            var dist: Double = 0.0
            var idcel: Float = 0.0f
            var star: SolarSystem = (GalacticraftCore.solarSystemSol)!!
            this.canTravel = true
            if (this.selectedBody is Planet) {
                if (this.mc.player.world.provider !is WorldProviderSurface && this.mc.player.world.provider is IGalacticraftWorldProvider) {
                    val body: CelestialBody? =
                        (this.mc.player.world.provider as IGalacticraftWorldProvider).getCelestialBody()
                    if (body is Planet) {
                        star = (body.getParentSolarSystem())!!
                        idcel = body.getRelativeDistanceFromCenter().unScaledDistance
                        dist =
                            abs(floor(((idcel - (this.selectedBody)!!.getRelativeDistanceFromCenter().unScaledDistance) * 100.0f).toDouble())) / 100.0
                    }

                    if (body is IChildBody) {
                        star = (((body as IChildBody).getParentPlanet())!!.getParentSolarSystem())!!
                        idcel = ((body as IChildBody).getParentPlanet())!!.getRelativeDistanceFromCenter().unScaledDistance
                        dist =
                            (abs(floor(((idcel - (this.selectedBody)!!.getRelativeDistanceFromCenter().unScaledDistance) * 100.0f).toDouble())) / 100.0
                                    + (body.getRelativeDistanceFromCenter().unScaledDistance / 100.0f).toDouble())
                    }

                    if (this.selectedBody === body) {
                        dist = 0.0
                    }
                } else {
                    idcel = (GalacticraftCore.planetOverworld)!!.getRelativeDistanceFromCenter().unScaledDistance
                    star = (GalacticraftCore.solarSystemSol)!!
                    dist =
                        abs(floor(((idcel - (this.selectedBody)!!.getRelativeDistanceFromCenter().unScaledDistance) * 100.0f).toDouble())) / 100.0
                }

                val test: SolarSystem = ((this.selectedBody as Planet).getParentSolarSystem())!!
                val distothersystem: Double =
                    (floor(Vector3.distance((star.getMapPosition())!!, (test.getMapPosition())!!) / 100.0) * 100.0
                            - abs(floor(((idcel + (this.selectedBody)!!.getRelativeDistanceFromCenter().unScaledDistance) * 100.0f).toDouble())))
                var time: Double = if (star === test) Math.round(dist * 70.0).toDouble() else distothersystem
                if (test != star) {
                    time *= 10.0
                    if (this.engine!!.getID() < Engine_Type.SUBLIGHT_ENGINE.getID() && ACCompatibilityManager.isGalaxySpaceLoaded()
                        && AsmodeusConfig.enableSublightEngine
                    ) {
                        this.canTravel = false
                    }
                }

                if (star.getUnlocalizedParentGalaxyName() != test.getUnlocalizedParentGalaxyName()) {
                    time *= 10.0
                    if (this.engine!!.getID() < Engine_Type.SUBLIGHT_ENGINE.getID() && ACCompatibilityManager.isGalaxySpaceLoaded()
                        && AsmodeusConfig.enableSublightEngine
                    ) {
                        this.canTravel = false
                    }
                }

                this.traveltime = ((if (time <= 0.0) 10 else time.toInt()) * travel_modifier).toDouble()

                for (i: Int in 0..BodiesRegistry.getMaxTier()) {
                    if (dist >= 0.2 * i.toDouble() && (this.selectedBody)!!.getReachable()) {
                        if (star.getUnlocalizedParentGalaxyName() == test.getUnlocalizedParentGalaxyName()) {
                            if (star === test) {
                                this.tierneed = if (i == BodiesRegistry.getMaxTier()) i else i + 1
                            } else {
                                this.tierneed = BodiesRegistry.getMaxTier()
                            }
                        } else {
                            this.tierneed = BodiesRegistry.getMaxTier()
                        }
                    }
                }

                val fuelcount: Int =
                    Math.round(this.fuelRocketLevel.toDouble() - this.fuelRocketLevel.toDouble() * (dist / this.currenttier.toDouble()))
                        .toInt()
                this.fuelSet = fuelcount
            }

            if (this.selectedBody is IChildBody) {
                if (this.mc.player.world.provider !is WorldProviderSurface && this.mc.player.world.provider is IGalacticraftWorldProvider) {
                    val bodyx: CelestialBody? =
                        (this.mc.player.world.provider as IGalacticraftWorldProvider).getCelestialBody()
                    idcel = 0.0f
                    if (bodyx is Planet) {
                        star = (bodyx.getParentSolarSystem())!!
                        idcel = bodyx.getRelativeDistanceFromCenter().unScaledDistance
                        dist = (abs(
                            floor(
                                ((idcel - ((this.selectedBody as IChildBody).getParentPlanet())!!
                                    .getRelativeDistanceFromCenter().unScaledDistance) * 100.0f).toDouble()
                            )
                        ) / 100.0
                                + ((this.selectedBody)!!.getRelativeDistanceFromCenter().unScaledDistance / 100.0f).toDouble())
                    } else if (bodyx is IChildBody) {
                        star = (((bodyx as IChildBody).getParentPlanet())!!.getParentSolarSystem())!!
                        idcel = ((bodyx as IChildBody).getParentPlanet())!!.getRelativeDistanceFromCenter().unScaledDistance
                        dist = (abs(
                            floor(
                                ((idcel - ((this.selectedBody as IChildBody).getParentPlanet())!!
                                    .getRelativeDistanceFromCenter().unScaledDistance) * 100.0f).toDouble()
                            )
                        ) / 100.0
                                + ((this.selectedBody)!!.getRelativeDistanceFromCenter().unScaledDistance / 100.0f).toDouble())
                    }

                    if (this.selectedBody === bodyx) {
                        dist = 0.0
                    }
                } else {
                    idcel = (GalacticraftCore.planetOverworld)!!.getRelativeDistanceFromCenter().unScaledDistance
                    star = (GalacticraftCore.solarSystemSol)!!
                    dist = (abs(
                        floor(
                            ((idcel - ((this.selectedBody as IChildBody).getParentPlanet())!!
                                .getRelativeDistanceFromCenter().unScaledDistance) * 100.0f).toDouble()
                        )
                    ) / 100.0
                            + ((this.selectedBody)!!.getRelativeDistanceFromCenter().unScaledDistance / 100.0f).toDouble())
                }

                val testx: SolarSystem = (((this.selectedBody as IChildBody).getParentPlanet())!!.getParentSolarSystem())!!
                var timex: Double = if (star === testx) Math.round(dist * 70.0).toDouble() else
                    (floor(
                        Vector3.distance(
                            (star.getMapPosition())!!,
                            (testx.getMapPosition())!!
                        ) / 200.0
                    ) * floor(
                        ((((this.selectedBody as IChildBody).getParentPlanet())!!
                            .getRelativeDistanceFromCenter().unScaledDistance + idcel) * 100.0f).toDouble()
                    ))
                if (testx != star) {
                    timex *= 10.0
                    if (this.engine!!.getID() < Engine_Type.SUBLIGHT_ENGINE.getID() && ACCompatibilityManager.isGalaxySpaceLoaded()
                        && AsmodeusConfig.enableSublightEngine
                    ) {
                        this.canTravel = false
                    }
                }

                if (star.getUnlocalizedParentGalaxyName() != testx.getUnlocalizedParentGalaxyName()) {
                    timex *= 1000.0
                    if (this.engine!!.getID() < Engine_Type.SUBLIGHT_ENGINE.getID() && ACCompatibilityManager.isGalaxySpaceLoaded()
                        && AsmodeusConfig.enableSublightEngine
                    ) {
                        this.canTravel = false
                    }
                }

                this.traveltime = ((if (timex <= 0.0) 10 else timex.toInt()) * travel_modifier).toDouble()

                for (ix: Int in 0..BodiesRegistry.getMaxTier()) {
                    if (dist >= 0.25 * ix.toDouble() && (this.selectedBody)!!.getReachable()) {
                        if (star.getUnlocalizedParentGalaxyName() == testx.getUnlocalizedParentGalaxyName()) {
                            if (star === testx) {
                                this.tierneed = if (ix == BodiesRegistry.getMaxTier()) ix else ix + 1
                            } else {
                                this.tierneed = BodiesRegistry.getMaxTier()
                            }
                        } else {
                            this.tierneed = BodiesRegistry.getMaxTier()
                        }
                    }
                }

                val fuelcount: Int =
                    Math.round(this.fuelRocketLevel.toDouble() - this.fuelRocketLevel.toDouble() * (dist / this.currenttier.toDouble()))
                        .toInt()
                this.fuelSet = fuelcount
            }

            if (this.fuelSet < 0) {
                this.canTravel = false
            }

            if (this.mc.player.capabilities.isCreativeMode) {
                this.canTravel = true
            }

            if (this.celestialBodyTiers.containsKey(this.selectedBody)) {
                this.tierneed = this.celestialBodyTiers.get(this.selectedBody)!!
            }
        }

        this.zLevel = 0.0f
        var handledSliderPos: Boolean = false
        val LHS: Int = BORDER_SIZE + BORDER_EDGE_SIZE
        val RHS: Int = this.width - LHS
        val TOP: Int = LHS
        val BOT: Int = this.height - LHS
        if (this.catalogViewState == EnumView.GS) {
            this.mc.renderEngine.bindTexture(guiMain0)
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
            this.drawTexturedModalRect(LHS, LHS, 74, 11, 0, 392, 148, 22, false, false)
            var str: String = GCCoreUtil.translate("gui.message.catalog.name").uppercase(Locale.getDefault())
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.fontRenderer.drawString(str, LHS + 40 - this.fontRenderer.getStringWidth(str) / 2, LHS + 1, WHITE)
            val scale: Int = Math.min(95.0f, this.ticksSinceMenuOpenF * 12.0f).toInt()
            val planetZoomedNotMoon: Boolean = this.isZoomed() && this.selectedParent !is Planet
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
            this.mc.renderEngine.bindTexture(guiMain0)
            this.drawTexturedModalRect(LHS - 95 + scale, LHS + 12, 95, 41, 0, 436, 95, 41, false, false)
            GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f)
            if ((GalaxyRegistry.getRegisteredSolarSystems())!!.size > 1) {
                this.drawTexturedModalRect(LHS + 2 - 13 + scale, LHS + 37, 5, 5, 110, 448, 5, 5, false, true)
            }

            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
            str = if (planetZoomedNotMoon) (this.selectedBody)!!.getLocalizedName() else this.getParentName()
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.fontRenderer.drawString(str, LHS + 9 - 95 + scale, LHS + 34, YELLOW)
            GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f)
            this.mc.renderEngine.bindTexture(guiMain0)
            this.drawTexturedModalRect(LHS + 2 - 95 + scale, LHS + 14, 93, 17, 95, 436, 93, 17, false, false)
            if (this.galaxylist.size > 1) {
                this.drawTexturedModalRect(LHS + 2 - 13 + scale, LHS + 19, 5, 5, 110, 448, 5, 5, false, true)
            }

            str = if (planetZoomedNotMoon) this.getParentName() else this.getGrandparentName()
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.fontRenderer.drawString(str, LHS + 7 - 95 + scale, LHS + 16, LIGHTBLUE)
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
            if (this.showGalaxyList) {
                val y: Int = 19

                for (ixx: Int in this.galaxylist.indices) {
                    val galaxy: ResourceLocation?
                    if (BodiesRegistry.getGalaxy(this.galaxylist.get(ixx)!!.replace("galaxy.", "")) != null
                        && (BodiesRegistry.getGalaxy(this.galaxylist.get(ixx)!!.replace("galaxy.", "")))!!
                            .getImage() != null
                    ) {
                        galaxy = (BodiesRegistry.getGalaxy(this.galaxylist.get(ixx)!!.replace("galaxy.", "")))!!.getImage()
                    } else {
                        galaxy = ResourceLocation(
                            "asmodeuscore:textures/gui/galaxy/" + this.galaxylist.get(ixx)!!
                                .lowercase(Locale.getDefault()) + ".png"
                        )
                    }

                    if (galaxy != null) {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                        this.mc.renderEngine.bindTexture(galaxy)
                        this.drawTexturedModalRect(
                            (LHS + 18 + scale + 70 * ixx).toFloat(),
                            (TOP + 4).toFloat(),
                            37.0f,
                            40.0f,
                            0.0f,
                            0.0f,
                            100.0f,
                            100.0f,
                            false,
                            false,
                            100.0f,
                            100.0f
                        )
                    }

                    this.mc.renderEngine.bindTexture(guiMain2)
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                    this.drawTexturedModalRect(
                        LHS + 15 + scale + 70 * ixx,
                        TOP + 4,
                        43,
                        40,
                        270,
                        80,
                        85,
                        75,
                        false,
                        false
                    )
                    this.mc.renderEngine.bindTexture(guiMain0)
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, scale.toFloat() / 95.0f)
                    this.drawTexturedModalRect(
                        LHS + 5 + scale + 70 * ixx,
                        TOP + 14 + 35,
                        43,
                        12,
                        95,
                        464,
                        43,
                        12,
                        false,
                        false
                    )
                    this.drawTexturedModalRect(
                        LHS + 5 + scale + 43 + 70 * ixx,
                        TOP + 14 + 35,
                        23,
                        12,
                        165,
                        464,
                        23,
                        12,
                        false,
                        false
                    )
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                    str = GCCoreUtil.translate((this.galaxylist.get(ixx))!!)
                    this.fontRenderer.drawString(str, LHS + 10 + scale + 70 * ixx, TOP + 14 + 37, YELLOW)
                }
            }

            if (this.showStarList) {
                var l: Int = 0
                var posX: Int = 0

                for (s: SolarSystem in this.starlist) {
                    val y: Int = 33
                    this.mc.renderEngine.bindTexture(guiMain2)
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, scale.toFloat() / 95.0f)
                    this.drawTexturedModalRect(
                        LHS + posX * 150 + 3 + scale,
                        TOP + 30 * l + 8,
                        140,
                        30,
                        275,
                        160,
                        140,
                        30,
                        false,
                        false
                    )
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                    val starx: ResourceLocation = ((s.getMainStar())!!.getBodyIcon())!!
                    this.mc.renderEngine.bindTexture(starx)
                    this.drawTexturedModalRect(
                        (LHS + posX * 150 + 14 + scale).toFloat(),
                        (TOP + 30 * l + 15).toFloat(),
                        16.0f,
                        16.0f,
                        0.0f,
                        0.0f,
                        16.0f,
                        16.0f,
                        false,
                        false,
                        16.0f,
                        16.0f
                    )
                    str = s.getLocalizedName()
                    this.fontRenderer.drawString(str, LHS + posX * 150 + 43 + scale, TOP + 30 * l + 19, YELLOW)
                    if (++l % 7 == 0) {
                        posX++
                        l = 0
                    }
                }
            }

            val children: MutableList<CelestialBody> = this.getChildren(
                if (planetZoomedNotMoon) this.selectedBody else this.selectedParent,
                this.scroll,
                this.max_scroll
            )
            this.drawChildren(children, 0, 0, true)
            if (this.mapMode) {
                this.mc.renderEngine.bindTexture(guiMain0)
                GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f)
                this.mc.renderEngine.bindTexture(guiMain0)
                this.drawTexturedModalRect(RHS - 74, TOP, 74, 11, 0, 392, 148, 22, true, false)
                str = GCCoreUtil.translate("gui.message.exit.name").uppercase(Locale.getDefault())
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                this.fontRenderer.drawString(str, RHS - 40 - this.fontRenderer.getStringWidth(str) / 2, TOP + 1, WHITE)
            }

            val menuTopLeft1: Int = BOT - 22
            val posX1: Int = RHS - 138
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
            this.mc.renderEngine.bindTexture(guiMain0)
            this.drawTexturedModalRect(posX1 + 98, menuTopLeft1 - 22, 40, 12, 0, 456, 40, 12, true, false)
            this.drawTexturedModalRect(posX1 + 53, menuTopLeft1 - 22, 45, 12, 50, 456, 45, 12, true, false)
            str = if (ClientProxy.smallInfoOnMap) "Small Mode" else "Normal Mode"
            GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f)
            this.fontRenderer.drawString(str, posX1 + 61, menuTopLeft1 - 20, 16777215)
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
            this.mc.renderEngine.bindTexture(guiMain0)
            this.drawTexturedModalRect(posX1 + 98, menuTopLeft1 - 8, 40, 12, 0, 456, 40, 12, true, false)
            this.drawTexturedModalRect(posX1 + 53, menuTopLeft1 - 8, 45, 12, 50, 456, 45, 12, true, false)
            str = if (ClientProxy.enableInfoOnMap) "Disable Info" else "Enable Info"
            GL11.glColor4f(
                if (ClientProxy.enableInfoOnMap) 1.0f else 0.0f,
                if (ClientProxy.enableInfoOnMap) 0.0f else 1.0f,
                0.0f,
                1.0f
            )
            this.fontRenderer.drawString(str, posX1 + 61, menuTopLeft1 - 6, 16777215)
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
            this.mc.renderEngine.bindTexture(guiMain0)
            this.drawTexturedModalRect(posX1 + 98, menuTopLeft1 + 6, 40, 12, 0, 456, 40, 12, true, false)
            this.drawTexturedModalRect(posX1 + 53, menuTopLeft1 + 6, 45, 12, 50, 456, 45, 12, true, false)
            str = if (!ClientProxy.renderRingZones) "Enable Zones" else "Disable Zones"
            GL11.glColor4f(
                if (ClientProxy.renderRingZones) 1.0f else 0.0f,
                if (ClientProxy.renderRingZones) 0.0f else 1.0f,
                0.0f,
                1.0f
            )
            this.fontRenderer.drawString(str, posX1 + 61, menuTopLeft1 + 8, 16777215)
            if (this.selectedBody != null) {
                this.mc.renderEngine.bindTexture(guiMain1)
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                if (this.selectedBody is Satellite) {
                    val selectedSatellite: Satellite = this.selectedBody as Satellite
                    val stationListSize: Int =
                        ((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellite)) as MutableMap<*, *>).size
                    this.mc.renderEngine.bindTexture(guiMain1)
                    val max: Int = min(this.height / 2 / 14, stationListSize)
                    this.drawTexturedModalRect(
                        RHS - 95,
                        TOP,
                        95,
                        53,
                        if (this.selectedStationOwner.length == 0) 95 else 0,
                        186,
                        95,
                        53,
                        false,
                        false
                    )
                    if (this.spaceStationListOffset <= 0) {
                        GL11.glColor4f(0.65f, 0.65f, 0.65f, 1.0f)
                    } else {
                        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                    }

                    this.drawTexturedModalRect(RHS - 85, TOP + 45, 61, 4, 0, 239, 61, 4, false, false)
                    if (max + this.spaceStationListOffset >= stationListSize) {
                        GL11.glColor4f(0.65f, 0.65f, 0.65f, 1.0f)
                    } else {
                        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                    }

                    this.drawTexturedModalRect(RHS - 85, TOP + 49 + max * 14, 61, 4, 0, 239, 61, 4, false, true)
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                    if (((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellite)) as MutableMap<*, *>).get(
                            this.selectedStationOwner
                        ) == null
                    ) {
                        str = GCCoreUtil.translate("gui.message.select_ss.name")
                        this.drawSplitString(str, RHS - 47, TOP + 20, 91, WHITE, false, false)
                    } else {
                        str = GCCoreUtil.translate("gui.message.ss_owner.name")
                        this.fontRenderer.drawString(str, RHS - 85, TOP + 18, WHITE)
                        str = this.selectedStationOwner
                        this.fontRenderer.drawString(
                            str,
                            RHS - 47 - this.fontRenderer.getStringWidth(str) / 2,
                            TOP + 30,
                            WHITE
                        )
                    }

                    val it: MutableIterator<MutableMap.MutableEntry<String?, StationDataGUI?>> =
                        (((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellite)) as MutableMap<*, *>).entries.iterator() as MutableIterator<MutableMap.MutableEntry<String?, GuiCelestialSelection.StationDataGUI?>>)
                    var ixx: Int = 0

                    var j: Int = 0
                    while (it.hasNext() && ixx < max) {
                        val e: MutableMap.MutableEntry<String?, StationDataGUI?> = it.next()
                        if (j >= this.spaceStationListOffset) {
                            this.mc.renderEngine.bindTexture(guiMain0)
                            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                            var xOffset: Int = 0
                            if (e.key.equals(this.selectedStationOwner, ignoreCase = true)) {
                                xOffset -= 5
                            }

                            this.drawTexturedModalRect(
                                RHS - 95 + xOffset,
                                TOP + 50 + ixx * 14,
                                93,
                                12,
                                95,
                                464,
                                93,
                                12,
                                true,
                                false
                            )
                            str = ""
                            val str0: String = (e.value!!.getStationName())!!

                            var point: Int = 0
                            while (this.fontRenderer.getStringWidth(str) < 80 && point < str0.length) {
                                str = str + str0.substring(point, point + 1)
                                point++
                            }

                            if (this.fontRenderer.getStringWidth(str) >= 80) {
                                str = str.substring(0, str.length - 3)
                                str = str + "..."
                            }

                            this.fontRenderer.drawString(str, RHS - 88 + xOffset, TOP + 52 + ixx * 14, WHITE)
                            ixx++
                        }
                        j++
                    }
                } else if (ClientProxy.enableInfoOnMap) {
                    this.mc.renderEngine.bindTexture(guiMain2)
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                    var y: Int = 100
                    var sliderPos: Int = this.zoomTooltipPos
                    if (this.zoomTooltipPos != 125) {
                        sliderPos = Math.min(this.ticksSinceSelectionF * 8.0f, 125.0f).toInt()
                        this.zoomTooltipPos = sliderPos
                    }

                    var menuTopLeft: Int = LHS + y
                    val posX: Int = RHS - sliderPos
                    val posX2: Int = RHS - 18
                    if (!(this.selectedBody)!!.getReachable()) {
                        if (ClientProxy.smallInfoOnMap) {
                            menuTopLeft = this.height - BORDER_EDGE_SIZE - 250
                        }

                        this.drawTexturedModalRect(posX - 7, menuTopLeft + 12, 133, 31, 0, 79, 266, 62, true, false)
                        this.drawTexturedModalRect(posX - 7, menuTopLeft + 43, 133, 37, 0, 442, 266, 70, true, false)
                    } else if (!ClientProxy.smallInfoOnMap) {
                        this.drawTexturedModalRect(posX - 7, menuTopLeft + 13, 133, 237, 0, 79, 266, 433, true, false)
                    } else {
                        menuTopLeft = this.height - BORDER_EDGE_SIZE - 250
                        this.drawTexturedModalRect(posX - 7, menuTopLeft + 13, 133, 80, 0, 79, 266, 142, true, false)
                        this.drawTexturedModalRect(posX - 7, menuTopLeft + 93, 133, 17, 0, 472, 266, 40, true, false)
                        this.mc.renderEngine.bindTexture(guiMain0)
                        this.drawTexturedModalRect(posX + 5, menuTopLeft + 112, 30, 11, 0, 414, 60, 22, false, false)
                        this.drawTexturedModalRect(posX + 35, menuTopLeft + 112, 50, 11, 128, 414, 40, 22, false, false)
                        this.drawTexturedModalRect(posX + 85, menuTopLeft + 112, 30, 11, 128, 414, 60, 22, false, false)
                        this.fontRenderer.drawString("<-", posX + 13, menuTopLeft + 114, CYAN)
                        this.fontRenderer.drawString(
                            "Page: " + (this.small_page + 1),
                            posX + 45,
                            menuTopLeft + 114,
                            CYAN
                        )
                        this.fontRenderer.drawString("->", posX + 100, menuTopLeft + 114, CYAN)
                        this.mc.renderEngine.bindTexture(guiMain2)
                    }

                    y += 15
                    str = GCCoreUtil.translate("gui.message.generalinformation")
                    if (!ClientProxy.smallInfoOnMap) {
                        this.fontRenderer.drawString(str, posX, TOP + y, CYAN)
                    } else if (this.small_page == 0 || this.selectedBody is Star) {
                        this.fontRenderer.drawString(str, posX, BOT - 225, CYAN)
                    }

                    if ((this.selectedBody)!!.getReachable()) {
                        str = GCCoreUtil.translate("gui.message.physicalparameters")
                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 40, CYAN)
                        } else if (this.small_page == 0) {
                            this.fontRenderer.drawString(str, posX, BOT - 185, CYAN)
                        }

                        str = GCCoreUtil.translate("gui.message.atmosphericparameters")
                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 82, CYAN)
                        } else if (this.small_page == 1) {
                            this.fontRenderer.drawString(str, posX, BOT - 225, CYAN)
                        }

                        str = GCCoreUtil.translate("gui.message.atmosphericcomponents")
                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 166, CYAN)
                        } else if (this.small_page == 2) {
                            this.fontRenderer.drawString(str, posX, BOT - 225, CYAN)
                        }
                    }

                    val data: BodiesData? = BodiesRegistry.getData(this.selectedBody)
                    if (this.selectedBody is Star) {
                        str =
                            GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.star.name")
                    }

                    if (this.selectedBody is Planet) {
                        str =
                            GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.planet.name")
                    }

                    if (this.selectedBody is Moon) {
                        str =
                            GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.moon.name")
                    }

                    if (this.selectedBody is Satellite) {
                        str =
                            GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.satellite.name")
                    }

                    if ((this.selectedBody)!!.getTierRequirement() == -2) {
                        str =
                            GCCoreUtil.translate("gui.message.type") + " " + GCCoreUtil.translate("gui.info.blackhole.name")
                    }

                    if (data != null && data.getType() != null) {
                        str = GCCoreUtil.translate("gui.message.type") + " " + BodiesRegistry.getLocale((data.getType())!!)
                    }

                    if (!ClientProxy.smallInfoOnMap) {
                        this.fontRenderer.drawString(str, posX, TOP + y + 10, WHITE)
                    } else if (this.small_page == 0 || this.selectedBody is Star) {
                        this.fontRenderer.drawString(str, posX, BOT - 225 + 10, WHITE)
                    }

                    if (this.selectedBody is Planet) {
                        this.galaxy =
                            ((this.selectedBody as Planet).getParentSolarSystem())!!.getUnlocalizedParentGalaxyName()
                    } else if (this.selectedBody is IChildBody) {
                        this.galaxy = (((this.selectedBody as IChildBody).getParentPlanet())!!.getParentSolarSystem())!!
                            .getUnlocalizedParentGalaxyName()
                    }

                    val dim: WorldProvider = (WorldUtil.getProviderForDimensionClient((this.selectedBody)!!.getDimensionID()))!!
                    str = GCCoreUtil.translate("gui.message.class") + " " + BodiesRegistry.classPlanet(
                        (this.selectedBody)!!,
                        dim
                    )
                    if (!ClientProxy.smallInfoOnMap) {
                        this.fontRenderer.drawString(str, posX, TOP + y + 20, WHITE)
                    } else if (this.small_page == 0 || this.selectedBody is Star) {
                        this.fontRenderer.drawString(str, posX, BOT - 225 + 20, WHITE)
                    }

                    if ((this.selectedBody)!!.getReachable()) {
                        str =
                            GCCoreUtil.translate("gui.message.hasdungeons") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if ((this.selectedBody)!!.getReachable() && this.selectedBody !== GalacticraftCore.planetOverworld) {
                            str = (GCCoreUtil.translate("gui.message.hasdungeons")
                                    + " "
                                    + Utils.localeBoolean((dim as IGalacticraftWorldProvider).getDungeonSpacing() > 0))
                        }

                        if (this.selectedBody === GalacticraftCore.planetOverworld) {
                            str = GCCoreUtil.translate("gui.message.hasdungeons") + " " + Utils.localeBoolean(true)
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 29, WHITE)
                        } else if (this.small_page == 0) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 30, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.gravity") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if ((this.selectedBody)!!.getReachable() && this.selectedBody !== GalacticraftCore.planetOverworld) {
                            var grav: Float = (0.1f - (dim as IGalacticraftWorldProvider).getGravity()) * 1000.0f
                            if ((dim as IGalacticraftWorldProvider).getGravity() == 0.0f) {
                                grav = 0.0f
                            }

                            str = GCCoreUtil.translate("gui.message.gravity") + " " + grav.toInt() + "%"
                        } else if (this.selectedBody === GalacticraftCore.planetOverworld) {
                            str = GCCoreUtil.translate("gui.message.gravity") + " 100%"
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 38 + 12, WHITE)
                        } else if (this.small_page == 0) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 38 + 12, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.daylength") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if ((this.selectedBody)!!.getReachable() && this.selectedBody !== GalacticraftCore.planetOverworld) {
                            if (dim is WorldProviderSpace) {
                                str = (GCCoreUtil.translate("gui.message.daylength")
                                        + " "
                                        + dim.getDayLength() / 1000L + "h "
                                        + dim.getDayLength() % 100L + "m")
                            } else if (dim is WE_WorldProviderSpace) {
                                str = (GCCoreUtil.translate("gui.message.daylength")
                                        + " "
                                        + dim.getDayLength() / 1000L + "h "
                                        + dim.getDayLength() % 100L + "m")
                            } else if (this.selectedBody === GalacticraftCore.planetOverworld) {
                                str = GCCoreUtil.translate("gui.message.daylength") + " 24h 0m"
                            }
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 38 + 22, WHITE)
                        } else if (this.small_page == 0) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 38 + 22, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.atmopressure") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if (this.selectedBody is IExBody) {
                            str =
                                GCCoreUtil.translate("gui.message.atmopressure") + " " + (this.selectedBody as IExBody).getAtmosphericPressure() + " P"
                        } else if (this.selectedBody === GalacticraftCore.planetOverworld) {
                            str = GCCoreUtil.translate("gui.message.atmopressure") + " P 1"
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 78 + 14, WHITE)
                        } else if (this.small_page == 1) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 10, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.temperature") + " " + GCCoreUtil.translate("gui.message.unknown")
                        var temp: Float = 0.0f

                        try {
                            temp = ((dim as IGalacticraftWorldProvider).getCelestialBody())!!.atmosphere.thermalLevel()
                        } catch (var40: Exception) {
                        }

                        temp =
                            if (temp == 0.0f) BodiesRegistry.getDefaultDergrees.toFloat() else temp * BodiesRegistry.getDefaultDergrees.toFloat()
                        if ((this.selectedBody)!!.getReachable()) {
                            str = GCCoreUtil.translate("gui.message.temperature") + " " + temp + " C"
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 78 + 24, WHITE)
                        } else if (this.small_page == 1) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 20, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.windenergy") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if ((this.selectedBody)!!.getReachable() && this.selectedBody !== GalacticraftCore.planetOverworld && dim is IAdvancedSpace) {
                            str = (GCCoreUtil.translate("gui.message.windenergy")
                                    + " "
                                    + Math.round((dim as IAdvancedSpace).getSolarWindMultiplier() * 1000.0)
                                .toFloat() / 1000.0f)
                        } else if (this.selectedBody === GalacticraftCore.planetOverworld) {
                            str = GCCoreUtil.translate("gui.message.windenergy") + " " + 1.0
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 76 + 36, WHITE)
                        } else if (this.small_page == 1) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 30, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.solarenergy") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if (dim is ISolarLevel) {
                            val boost: Float =
                                Math.round(((dim as ISolarLevel).getSolarEnergyMultiplier() - 1.0) * 1000.0)
                                    .toFloat() / 10.0f
                            str = GCCoreUtil.translate("gui.message.solarenergy") + " " + boost + "%"
                        }

                        if (this.selectedBody === GalacticraftCore.planetOverworld) {
                            str = GCCoreUtil.translate("gui.message.solarenergy") + " 0%"
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 76 + 48, WHITE)
                        } else if (this.small_page == 1) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 40, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.windspeed") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if (dim is IGalacticraftWorldProvider) {
                            str = (GCCoreUtil.translate("gui.message.windspeed")
                                    + " "
                                    + ((dim as IGalacticraftWorldProvider).getCelestialBody())!!.atmosphere.windLevel()
                                    + " W/l")
                        }

                        if (this.selectedBody === GalacticraftCore.planetOverworld) {
                            str = GCCoreUtil.translate("gui.message.windspeed") + " 1.0 W/l"
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 74 + 60, WHITE)
                        } else if (this.small_page == 1) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 50, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.solarradiation") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if (this.selectedBody is IExBody) {
                            str =
                                GCCoreUtil.translate("gui.message.solarradiation") + " " + (this.selectedBody as IExBody).isSolarRadiation()
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 78 + 66, WHITE)
                        } else if (this.small_page == 1) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 60, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.breathableatmo") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if ((this.selectedBody)!!.getReachable() && this.selectedBody !== GalacticraftCore.planetOverworld) {
                            str =
                                GCCoreUtil.translate("gui.message.breathableatmo") + " " + Utils.localeBoolean((this.selectedBody)!!.atmosphere.isBreathable())
                        }

                        if (this.selectedBody === GalacticraftCore.planetOverworld) {
                            str = GCCoreUtil.translate("gui.message.breathableatmo") + " " + Utils.localeBoolean(true)
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 77 + 99, WHITE)
                        } else if (this.small_page == 2) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 10, WHITE)
                        }

                        str =
                            GCCoreUtil.translate("gui.message.corrosiveatmo") + " " + GCCoreUtil.translate("gui.message.unknown")
                        if ((this.selectedBody)!!.getReachable() && this.selectedBody !== GalacticraftCore.planetOverworld) {
                            str =
                                GCCoreUtil.translate("gui.message.corrosiveatmo") + " " + Utils.localeBoolean((this.selectedBody)!!.atmosphere.isCorrosive())
                        }

                        if (this.selectedBody === GalacticraftCore.planetOverworld) {
                            str = GCCoreUtil.translate("gui.message.corrosiveatmo") + " " + Utils.localeBoolean(false)
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 76 + 110, WHITE)
                        } else if (this.small_page == 2) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 20, WHITE)
                        }

                        if ((this.selectedBody)!!.getReachable() && !(this.selectedBody)!!.atmosphere.hasNoGases()) {
                            str = (this.selectedBody)!!.atmosphere.composition.toString()
                        } else {
                            str = GCCoreUtil.translate("gui.message.noatmosphere")
                        }

                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, TOP + y + 75 + 121, WHITE)
                        } else if (this.small_page == 2) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 30, WHITE)
                        }
                    }

                    val travel: String =
                        (this.traveltime / 100.0).toInt().toString() + "h " + (this.traveltime % 100.0).toInt() + "m"
                    str = GCCoreUtil.translate("gui.message.traveltime") + " " + travel
                    if ((this.selectedBody)!!.getReachable()) {
                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(
                                str,
                                posX,
                                TOP + y + 74 + 143,
                                ColorUtil.to32BitColor(255, 255, 255, 255)
                            )
                        } else if (this.small_page == 2) {
                            this.fontRenderer.drawString(
                                str,
                                posX,
                                BOT - 225 + 80,
                                ColorUtil.to32BitColor(255, 255, 255, 255)
                            )
                        }
                    }

                    if (this.selectedBody !is Star) {
                        if (this.selectedBody is Planet && data != null && data.getType() != IAdvancedSpace.TypeBody.STAR) {
                            str =
                                GCCoreUtil.translate("gui.message.moons") + " " + this.getChildren(this.selectedBody).size
                            if (!(this.selectedBody)!!.getReachable()) {
                                if (ClientProxy.smallInfoOnMap) {
                                    this.fontRenderer.drawString(str, posX, BOT - 225 + 29 + 10, WHITE)
                                } else {
                                    this.fontRenderer.drawString(str, posX, TOP + y + 38, WHITE)
                                }
                            } else if (!ClientProxy.smallInfoOnMap) {
                                this.fontRenderer.drawString(str, posX, TOP + y + 76 + 132, WHITE)
                            } else if (this.small_page == 2) {
                                this.fontRenderer.drawString(str, posX, BOT - 225 + 70, WHITE)
                            }
                        }
                    } else {
                        var x: Int = 0

                        for (bodyxx: CelestialBody in this.getChildren(this.selectedParent)) {
                            if (bodyxx.getTierRequirement() >= 0) {
                                x++
                            }
                        }

                        str = GCCoreUtil.translate("gui.message.planets") + " " + x
                        if (ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(str, posX, BOT - 225 + 29 + 10, WHITE)
                        } else {
                            this.fontRenderer.drawString(str, posX, TOP + y + 38, WHITE)
                        }
                    }

                    str = GCCoreUtil.translate("gui.message.fuelset") + " " + this.fuelSet
                    if ((this.selectedBody)!!.getTierRequirement() != -1 && (this.selectedBody)!!.getReachable() && !this.mapMode) {
                        if (!ClientProxy.smallInfoOnMap) {
                            this.fontRenderer.drawString(
                                str,
                                posX,
                                TOP + y + 83 + 143,
                                if (this.fuelSet < 0) RED else WHITE
                            )
                        } else if (this.small_page == 2) {
                            this.fontRenderer.drawString(
                                str,
                                posX,
                                BOT - 225 + 40,
                                if (this.fuelSet < 0) RED else WHITE
                            )
                        }
                    }

                    if (this.zoomTooltipPos != 38) {
                        sliderPos = Math.min(this.ticksSinceSelectionF * 2.0f, 38.0f).toInt()
                        this.zoomTooltipPos = sliderPos
                    }

                    if ((this.selectedBody !is Star) && this.traveltime * travel_modifier.toDouble() > this.mindistance * travel_modifier.toDouble() * 70.0 && ACCompatibilityManager.isGalaxySpaceLoaded()
                        && AsmodeusConfig.enableSublightEngine
                    ) {
                        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                        this.mc.renderEngine.bindTexture(guiMain0)
                        this.drawTexturedModalRect(
                            RHS - 230,
                            BORDER_SIZE + BORDER_EDGE_SIZE + sliderPos - 39,
                            120,
                            38,
                            346,
                            436,
                            166,
                            76,
                            false,
                            true
                        )
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                        str = GCCoreUtil.translate("gui.message.engine")
                        this.fontRenderer
                            .drawString(str, RHS - 200, BORDER_SIZE + BORDER_EDGE_SIZE + sliderPos - 35, GREY4)
                        str = GCCoreUtil.translate(
                            "gui.engine_type." + Engine_Type.SUBLIGHT_ENGINE.getName().lowercase(Locale.getDefault())
                        )
                        val color: Int =
                            if (this.engine!!.getID() < Engine_Type.SUBLIGHT_ENGINE.getID() && !this.mc.player.capabilities.isCreativeMode) RED else GREEN
                        this.fontRenderer
                            .drawString(str, RHS - 206, BORDER_SIZE + BORDER_EDGE_SIZE + sliderPos - 20, color)
                    }
                }

                val datax: BodiesData? = BodiesRegistry.getData(this.selectedBody)
                if (this.enableTraveltime) {
                    this.drawTransitBar(this.getScaledTravelTime(138))
                }

                if (this.canCreateSpaceStation(this.selectedBody) && this.selectedBody !is Satellite) {
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                    this.mc.renderEngine.bindTexture(guiMain1)
                    val canCreateLength: Int = max(
                        0,
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.can_create_space_station.name"),
                            0,
                            0,
                            91,
                            0,
                            true,
                            true
                        ) - 2
                    )
                    this.canCreateOffset = canCreateLength * this.fontRenderer.FONT_HEIGHT
                    this.drawTexturedModalRect(RHS - 95, TOP + 16, 93, 4, 159, 102, 93, 4, false, false)

                    for (barY: Int in 0..<canCreateLength) {
                        this.drawTexturedModalRect(
                            RHS - 95,
                            TOP + 20 + barY * this.fontRenderer.FONT_HEIGHT,
                            93,
                            this.fontRenderer.FONT_HEIGHT,
                            159,
                            106,
                            93,
                            this.fontRenderer.FONT_HEIGHT,
                            false,
                            false
                        )
                    }

                    this.drawTexturedModalRect(RHS - 69, TOP + 12, 61, 4, 0, 170, 61, 4, false, false)
                    val recipe: SpaceStationRecipe? =
                        WorldUtil.getSpaceStationRecipe((this.selectedBody)!!.getDimensionID())
                    var h: Int = 1
                    if (recipe != null) {
                        h = Math.round(ceil((recipe.getRecipeSize().toFloat() / 4.0f).toDouble()).toFloat() - 1.0f) * 25
                    }

                    this.drawTexturedModalRect(
                        RHS - 95,
                        TOP + 20 + this.canCreateOffset,
                        93,
                        43 + h,
                        159,
                        106,
                        93,
                        43,
                        false,
                        false
                    )
                    if (recipe != null) {
                        GL11.glColor4f(0.0f, 1.0f, 0.1f, 1.0f)
                        var validInputMaterials: Boolean = true
                        var ixx: Int = 0
                        val yOff: Int = 0
                        val var140: MutableIterator<*> = recipe.getInput().entries.iterator()

                        label919@ while (true) {
                            while (true) {
                                if (!var140.hasNext()) {
                                    if (!validInputMaterials && !this.mc.player.capabilities.isCreativeMode) {
                                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f)
                                    } else {
                                        GL11.glColor4f(0.0f, 1.0f, 0.1f, 1.0f)
                                    }

                                    this.mc.renderEngine.bindTexture(guiMain1)
                                    this.canCreateOffset = (this.canCreateOffset.toLong() + Math.round(
                                        ceil(
                                            (recipe.getRecipeSize().toFloat() / 4.0f).toDouble()
                                        ) - 1.0
                                    ) * 25L + 1L
                                            ).toInt()
                                    if (!this.mapMode && mousePosX >= RHS - 95 && mousePosX <= RHS && mousePosY >= TOP + 62 + this.canCreateOffset && mousePosY <= TOP + 62 + 12 + this.canCreateOffset) {
                                        this.drawTexturedModalRect(
                                            RHS - 95,
                                            TOP + 62 + this.canCreateOffset,
                                            93,
                                            12,
                                            0,
                                            174,
                                            93,
                                            12,
                                            false,
                                            false
                                        )
                                    }

                                    this.drawTexturedModalRect(
                                        RHS - 95,
                                        TOP + 62 + this.canCreateOffset,
                                        93,
                                        12,
                                        0,
                                        174,
                                        93,
                                        12,
                                        false,
                                        false
                                    )
                                    val color: Int =
                                        ((sin(this.ticksSinceSelectionF.toDouble() / 5.0) * 0.5 + 0.5) * 255.0).toInt()
                                    this.drawSplitString(
                                        GCCoreUtil.translate("gui.message.can_create_space_station.name"),
                                        RHS - 48,
                                        TOP + 18,
                                        91,
                                        ColorUtil.to32BitColor(255, color, 255, color),
                                        true,
                                        false
                                    )
                                    if (!this.mapMode) {
                                        this.drawSplitString(
                                            GCCoreUtil.translate("gui.message.create_ss.name")
                                                .uppercase(Locale.getDefault()),
                                            RHS - 48,
                                            TOP + 65 + this.canCreateOffset,
                                            91,
                                            WHITE,
                                            false,
                                            false
                                        )
                                    }
                                    break@label919
                                }

                                val e: MutableMap.MutableEntry<Any?,Int> =
                                    var140.next() as MutableMap.MutableEntry<Any?,Int>
                                val next: Any? = e.key
                                val xPos: Int = RHS - 95 + ixx % 4 * 93 / 4 + 5
                                val yPos: Int =
                                    TOP + 35 + this.canCreateOffset + 24 * (if (ixx >= 8) 2 else (if (ixx >= 4) 1 else 0))
                                if (next is ItemStack) {
                                    val amount: Int = this.getAmountInInventory(next)
                                    RenderHelper.enableGUIStandardItemLighting()
                                    val toRender: ItemStack = next.copy()
                                    this.itemRender.renderItemAndEffectIntoGUI(toRender, xPos, yPos)
                                    this.itemRender.renderItemOverlayIntoGUI(
                                        this.mc.fontRenderer,
                                        toRender,
                                        xPos,
                                        yPos,
                                        null
                                    )
                                    RenderHelper.disableStandardItemLighting()
                                    GL11.glEnable(3042)
                                    if (mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16) {
                                        GL11.glDepthMask(true)
                                        GL11.glEnable(2929)
                                        GL11.glPushMatrix()
                                        GL11.glTranslatef(0.0f, 0.0f, 300.0f)
                                        val k: Int = this.fontRenderer.getStringWidth(next.getDisplayName())
                                        var j2: Int = mousePosX - k / 2
                                        var k2: Int = mousePosY - 12
                                        val i1: Int = 8
                                        if (j2 + k > this.width) {
                                            j2 -= j2 - this.width + k
                                        }

                                        if (k2 + i1 + 6 > this.height) {
                                            k2 = this.height - i1 - 6
                                        }

                                        val j1: Int = ColorUtil.to32BitColor(190, 0, 153, 255)
                                        this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1)
                                        this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1)
                                        this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1)
                                        this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1)
                                        this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1)
                                        val k1: Int = ColorUtil.to32BitColor(170, 0, 153, 255)
                                        val l1: Int = (k1 and 16711422) shr 1 or (k1 and -0x1000000)
                                        this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1)
                                        this.drawGradientRect(
                                            j2 + k + 2,
                                            k2 - 3 + 1,
                                            j2 + k + 3,
                                            k2 + i1 + 3 - 1,
                                            k1,
                                            l1
                                        )
                                        this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1)
                                        this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1)
                                        this.fontRenderer.drawString(next.getDisplayName(), j2, k2, WHITE)
                                        GL11.glPopMatrix()
                                    }

                                    str = "" + e.value
                                    val valid: Boolean = amount >= e.value!!
                                    if (!valid && validInputMaterials) {
                                        validInputMaterials = false
                                    }

                                    val color: Int =
                                        if (valid or this.mc.player.capabilities.isCreativeMode) GREEN else RED
                                    this.fontRenderer.drawString(
                                        str,
                                        xPos + 8 - this.fontRenderer.getStringWidth(str) / 2,
                                        yPos + 15,
                                        color
                                    )
                                    break
                                }

                                if (next !is MutableCollection<*>) {
                                    break
                                }

                                val items: MutableCollection<ItemStack?> = next as MutableCollection<ItemStack?>
                                var amountx: Int = 0

                                for (stack: ItemStack? in items) {
                                    amountx += this.getAmountInInventory((stack)!!)
                                }

                                RenderHelper.enableGUIStandardItemLighting()
                                val it: MutableIterator<ItemStack?> = items.iterator()
                                var count: Int = 0
                                val toRenderIndex: Int =
                                    (this.ticksSinceSelectionF / 20.0f % items.size.toFloat()).toInt()

                                var toRenderx: ItemStack?
                                toRenderx = null
                                while (it.hasNext()) {
                                    val stack: ItemStack? = it.next()
                                    if (count == toRenderIndex) {
                                        toRenderx = stack
                                        break
                                    }
                                    count++
                                }

                                if (toRenderx != null) {
                                    this.itemRender.renderItemAndEffectIntoGUI(toRenderx, xPos, yPos)
                                    this.itemRender.renderItemOverlayIntoGUI(
                                        this.mc.fontRenderer,
                                        toRenderx,
                                        xPos,
                                        yPos,
                                        null
                                    )
                                    RenderHelper.disableStandardItemLighting()
                                    GL11.glEnable(3042)
                                    if (mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16) {
                                        GL11.glDepthMask(true)
                                        GL11.glEnable(2929)
                                        GL11.glPushMatrix()
                                        GL11.glTranslatef(0.0f, 0.0f, 300.0f)
                                        val kx: Int = this.fontRenderer.getStringWidth(toRenderx.getDisplayName())
                                        var j2x: Int = mousePosX - kx / 2
                                        var k2x: Int = mousePosY - 12
                                        val i1x: Int = 8
                                        if (j2x + kx > this.width) {
                                            j2x -= j2x - this.width + kx
                                        }

                                        if (k2x + i1x + 6 > this.height) {
                                            k2x = this.height - i1x - 6
                                        }

                                        val j1: Int = ColorUtil.to32BitColor(190, 0, 153, 255)
                                        this.drawGradientRect(j2x - 3, k2x - 4, j2x + kx + 3, k2x - 3, j1, j1)
                                        this.drawGradientRect(
                                            j2x - 3,
                                            k2x + i1x + 3,
                                            j2x + kx + 3,
                                            k2x + i1x + 4,
                                            j1,
                                            j1
                                        )
                                        this.drawGradientRect(j2x - 3, k2x - 3, j2x + kx + 3, k2x + i1x + 3, j1, j1)
                                        this.drawGradientRect(j2x - 4, k2x - 3, j2x - 3, k2x + i1x + 3, j1, j1)
                                        this.drawGradientRect(
                                            j2x + kx + 3,
                                            k2x - 3,
                                            j2x + kx + 4,
                                            k2x + i1x + 3,
                                            j1,
                                            j1
                                        )
                                        val k1: Int = ColorUtil.to32BitColor(170, 0, 153, 255)
                                        val l1: Int = (k1 and 16711422) shr 1 or (k1 and -0x1000000)
                                        this.drawGradientRect(
                                            j2x - 3,
                                            k2x - 3 + 1,
                                            j2x - 3 + 1,
                                            k2x + i1x + 3 - 1,
                                            k1,
                                            l1
                                        )
                                        this.drawGradientRect(
                                            j2x + kx + 2,
                                            k2x - 3 + 1,
                                            j2x + kx + 3,
                                            k2x + i1x + 3 - 1,
                                            k1,
                                            l1
                                        )
                                        this.drawGradientRect(j2x - 3, k2x - 3, j2x + kx + 3, k2x - 3 + 1, k1, k1)
                                        this.drawGradientRect(
                                            j2x - 3,
                                            k2x + i1x + 2,
                                            j2x + kx + 3,
                                            k2x + i1x + 3,
                                            l1,
                                            l1
                                        )
                                        this.fontRenderer.drawString(toRenderx.getDisplayName(), j2x, k2x, WHITE)
                                        GL11.glPopMatrix()
                                    }

                                    str = "" + e.value
                                    val valid: Boolean = amountx >= e.value!!
                                    if (!valid && validInputMaterials) {
                                        validInputMaterials = false
                                    }

                                    val color: Int =
                                        if (valid or this.mc.player.capabilities.isCreativeMode) GREEN else RED
                                    this.fontRenderer.drawString(
                                        str,
                                        xPos + 8 - this.fontRenderer.getStringWidth(str) / 2,
                                        yPos + 15,
                                        color
                                    )
                                    break
                                }
                            }

                            ixx++
                        }
                    } else {
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.cannot_create_space_station.name"),
                            RHS - 48,
                            TOP + 65,
                            91,
                            WHITE,
                            true,
                            false
                        )
                    }
                }

                this.mc.renderEngine.bindTexture(guiMain0)
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.3f - Math.min(0.3f, this.ticksSinceSelectionF / 50.0f))
                this.drawTexturedModalRect(LHS, TOP, 74, 11, 0, 392, 148, 22, false, false)
                str = GCCoreUtil.translate("gui.message.catalog.name").uppercase(Locale.getDefault())
                this.fontRenderer.drawString(str, LHS + 40 - this.fontRenderer.getStringWidth(str) / 2, TOP + 1, WHITE)
                this.mc.renderEngine.bindTexture(guiMain0)
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                if (this.selectedBody is Satellite) {
                    if (this.selectedStationOwner.length != 0 && this.selectedStationOwner.equals(
                            PlayerUtil.getName(
                                this.mc.player
                            ), ignoreCase = true
                        )
                    ) {
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f)
                    } else {
                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f)
                    }

                    this.drawTexturedModalRect(this.width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false)
                } else {
                    this.drawTexturedModalRect(this.width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false)
                }

                if ((this.selectedBody)!!.getTierRequirement() >= 0 && this.selectedBody !is Satellite) {
                    val canReach: Boolean
                    if ((this.selectedBody)!!.getReachable()
                        && (if (this.enableNewTierSystem)
                            this.currenttier >= this.tierneed
                        else
                            this.possibleBodies == null || (this.possibleBodies)!!.contains(this.selectedBody)
                                )
                    ) {
                        canReach = true
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f)
                    } else {
                        canReach = false
                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f)
                    }

                    this.drawTexturedModalRect(this.width / 2 - 30, TOP + 11, 30, 11, 0, 414, 60, 22, false, false)
                    this.drawTexturedModalRect(this.width / 2, TOP + 11, 30, 11, 128, 414, 60, 22, false, false)
                    if (!this.enableNewTierSystem) {
                        str = GCCoreUtil.translateWithFormat(
                            "gui.message.tier.name",
                            *arrayOf<Any?>(if ((this.selectedBody)!!.getTierRequirement() == 0) "?" else (this.selectedBody)!!.getTierRequirement())
                        )
                    } else {
                        str = GCCoreUtil.translateWithFormat(
                            "gui.message.tier.name",
                            *arrayOf<Any?>(if (this.tierneed > 0 && (this.selectedBody)!!.getReachable()) this.tierneed else "?")
                        )
                    }

                    this.fontRenderer.drawString(
                        str,
                        this.width / 2 - this.fontRenderer.getStringWidth(str) / 2,
                        TOP + 13,
                        if (canReach) GREY4 else RED3
                    )
                }

                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                str = (this.selectedBody)!!.getLocalizedName()
                if (this.selectedBody is Satellite) {
                    str = GCCoreUtil.translate("gui.message.rename.name").uppercase(Locale.getDefault())
                }

                this.fontRenderer.drawString(
                    str,
                    this.width / 2 - this.fontRenderer.getStringWidth(str) / 2,
                    TOP + 2,
                    WHITE
                )
                this.mc.renderEngine.bindTexture(guiMain0)
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                this.drawTexturedModalRect(LHS + 4, TOP, 83, 12, 0, 477, 83, 12, false, false)
                if (!this.mapMode) {
                    if (this.canTravel
                        && (this.selectedBody)!!.getReachable()
                        && (if (this.enableNewTierSystem)
                            this.currenttier >= this.tierneed
                        else
                            this.possibleBodies == null || (this.possibleBodies)!!.contains(this.selectedBody)
                                )
                        && (this.selectedBody !is Satellite || this.selectedStationOwner != "")
                    ) {
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f)
                    } else {
                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f)
                    }

                    this.mc.renderEngine.bindTexture(guiMain0)
                    this.drawTexturedModalRect(RHS - 74, TOP, 74, 11, 0, 392, 148, 22, true, false)
                    str = GCCoreUtil.translate("gui.message.launch.name").uppercase(Locale.getDefault())
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    this.fontRenderer.drawString(
                        str,
                        RHS - 40 - this.fontRenderer.getStringWidth(str) / 2,
                        TOP + 2,
                        WHITE
                    )
                }

                if (this.selectionState == EnumSelection.SELECTED && this.selectedBody !is Satellite) {
                    handledSliderPos = true
                    var sliderPosx: Int = this.zoomTooltipPos
                    if (this.zoomTooltipPos != 38) {
                        sliderPosx = Math.min(this.ticksSinceSelectionF * 2.0f, 38.0f).toInt()
                        this.zoomTooltipPos = sliderPosx
                    }

                    val xOffsetx: Int = 222
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                    this.mc.renderEngine.bindTexture(guiMain0)
                    this.drawTexturedModalRect(
                        RHS - xOffsetx,
                        this.height - BORDER_SIZE - BORDER_EDGE_SIZE - sliderPosx,
                        83,
                        38,
                        346,
                        436,
                        166,
                        76,
                        true,
                        false
                    )
                    val flag0: Boolean = (GalaxyRegistry.getSatellitesForCelestialBody(this.selectedBody))!!.size > 0
                    val flag1: Boolean =
                        this.selectedBody is Planet && (GalaxyRegistry.getMoonsForPlanet(this.selectedBody as Planet))!!.size > 0
                    if (flag0 && flag1) {
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.click_again.0.name"),
                            RHS - xOffsetx + 41,
                            this.height - BORDER_SIZE - BORDER_EDGE_SIZE + 2 - sliderPosx,
                            79,
                            GREY5,
                            false,
                            false
                        )
                    } else if (!flag0 && flag1) {
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.click_again.1.name"),
                            RHS - xOffsetx + 41,
                            this.height - BORDER_SIZE - BORDER_EDGE_SIZE + 6 - sliderPosx,
                            79,
                            GREY5,
                            false,
                            false
                        )
                    } else if (flag0) {
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.click_again.2.name"),
                            RHS - xOffsetx + 41,
                            this.height - BORDER_SIZE - BORDER_EDGE_SIZE + 6 - sliderPosx,
                            79,
                            GREY5,
                            false,
                            false
                        )
                    } else {
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.click_again.3.name"),
                            RHS - xOffsetx + 41,
                            this.height - BORDER_SIZE - BORDER_EDGE_SIZE + 11 - sliderPosx,
                            79,
                            GREY5,
                            false,
                            false
                        )
                    }
                }

                if (this.selectedBody is Satellite && this.renamingSpaceStation) {
                    this.drawDefaultBackground()
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
                    this.mc.renderEngine.bindTexture(guiMain1)
                    this.drawTexturedModalRect(
                        this.width / 2 - 90,
                        this.height / 2 - 38,
                        179,
                        67,
                        159,
                        0,
                        179,
                        67,
                        false,
                        false
                    )
                    this.drawTexturedModalRect(
                        this.width / 2 - 90 + 4,
                        this.height / 2 - 38 + 2,
                        171,
                        10,
                        159,
                        92,
                        171,
                        10,
                        false,
                        false
                    )
                    this.drawTexturedModalRect(
                        this.width / 2 - 90 + 8,
                        this.height / 2 - 38 + 18,
                        161,
                        13,
                        159,
                        67,
                        161,
                        13,
                        false,
                        false
                    )
                    this.drawTexturedModalRect(
                        this.width / 2 - 90 + 17,
                        this.height / 2 - 38 + 59,
                        72,
                        12,
                        159,
                        80,
                        72,
                        12,
                        true,
                        false
                    )
                    this.drawTexturedModalRect(
                        this.width / 2,
                        this.height / 2 - 38 + 59,
                        72,
                        12,
                        159,
                        80,
                        72,
                        12,
                        false,
                        false
                    )
                    str = GCCoreUtil.translate("gui.message.assign_name.name")
                    this.fontRenderer.drawString(
                        str,
                        this.width / 2 - this.fontRenderer.getStringWidth(str) / 2,
                        this.height / 2 - 35,
                        WHITE
                    )
                    str = GCCoreUtil.translate("gui.message.apply.name")
                    this.fontRenderer.drawString(
                        str,
                        this.width / 2 - this.fontRenderer.getStringWidth(str) / 2 - 36,
                        this.height / 2 + 23,
                        WHITE
                    )
                    str = GCCoreUtil.translate("gui.message.cancel.name")
                    this.fontRenderer.drawString(
                        str,
                        this.width / 2 + 36 - this.fontRenderer.getStringWidth(str) / 2,
                        this.height / 2 + 23,
                        WHITE
                    )
                    if (this.renamingString == null) {
                        val selectedSatellitex: Satellite = this.selectedBody as Satellite
                        val playerName: String? = PlayerUtil.getName(this.mc.player)
                        this.renamingString =
                            (((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellitex)) as MutableMap<*, *>).get(
                                playerName
                            ) as StationDataGUI)
                                .getStationName()
                        if (this.renamingString == null) {
                            this.renamingString =
                                (((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellitex)) as MutableMap<*, *>)
                                    .get(playerName!!.lowercase(Locale.getDefault())) as StationDataGUI)
                                    .getStationName()
                        }

                        if (this.renamingString == null) {
                            this.renamingString = ""
                        }
                    }

                    str = (this.renamingString)!!
                    var str0: String = (this.renamingString)!!
                    if (this.ticksSinceSelectionF / 10.0f % 2.0f === 0.0f) {
                        str0 = str0 + "_"
                    }

                    this.fontRenderer.drawString(
                        str0,
                        this.width / 2 - this.fontRenderer.getStringWidth(str) / 2,
                        this.height / 2 - 17,
                        WHITE
                    )
                }
            }

            str = EnumColor.ORANGE.toString() + "AsmodeusCore Galaxy Map"
            this.fontRenderer
                .drawString(
                    str,
                    LHS + 5,
                    this.height - BORDER_SIZE - BORDER_EDGE_SIZE - 10,
                    ColorUtil.to32BitColor(255, 255, 255, 255)
                )
            if (!handledSliderPos) {
                this.zoomTooltipPos = 0
            }
        }
    }

    override fun drawChildren(
        children: MutableList<CelestialBody>,
        xOffsetBase: Int,
        yOffsetPrior: Int,
        recursive: Boolean
    ): Int {
        var xOffsetBase: Int = xOffsetBase
        xOffsetBase += BORDER_SIZE + BORDER_EDGE_SIZE
        val yOffsetBase: Int = BORDER_SIZE + BORDER_EDGE_SIZE + 50 + yOffsetPrior
        var yOffset: Int = 0
        val size: Int = children.size

        for (i: Int in 0..<size) {
            val child: CelestialBody = children.get(i)
            val xOffset: Int = xOffsetBase + (if (child == this.selectedBody) 5 else 0)
            val scale: Int =
                Math.min(95.0f, Math.max(0.0f, this.ticksSinceMenuOpenF * 25.0f - (95 * i).toFloat())).toInt()
            val data: BodiesData? = BodiesRegistry.getData(child)
            this.mc.renderEngine.bindTexture(guiMain0)
            val brightness: Float = if (child == this.selectedBody) 0.2f else 0.0f
            if (child.getReachable()) {
                if (child == this.selectedBody) {
                    GL11.glColor4f(0.0f, 1.0f, 1.0f, scale.toFloat() / 95.0f)
                } else {
                    GL11.glColor4f(0.0f, 0.6f + brightness, 0.0f, scale.toFloat() / 95.0f)
                }
            } else {
                var checked: Boolean = false
                if (child is Planet) {
                    for (moon in (GalaxyRegistry.getMoonsForPlanet(child))!!) {
                        if ((moon)!!.getReachable()) {
                            checked = true
                            break
                        }
                    }
                }

                if (data != null && data.getType() == IAdvancedSpace.TypeBody.STAR) {
                    GL11.glColor4f(0.0f, 0.4f, 0.6f + brightness, scale.toFloat() / 95.0f)
                } else if (child is Planet && checked) {
                    GL11.glColor4f(0.6f + brightness, 0.6f, 0.0f, scale.toFloat() / 95.0f)
                } else {
                    GL11.glColor4f(0.6f + brightness, 0.0f, 0.0f, scale.toFloat() / 95.0f)
                }
            }

            this.drawTexturedModalRect(3 + xOffset, yOffsetBase + yOffset + 1, 86, 10, 0, 489, 86, 10, false, false)
            GL11.glColor4f(3.0f * brightness, 0.6f + 2.0f * brightness, 1.0f, scale.toFloat() / 95.0f)
            this.drawTexturedModalRect(2 + xOffset, yOffsetBase + yOffset, 93, 12, 95, 464, 93, 12, false, false)
            if (scale > 0) {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                val color: Int = 14737632
                GlStateManager.pushMatrix()
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                this.mc.renderEngine.bindTexture(child.getBodyIcon())
                this.drawTexturedModalRect(
                    (xOffset + 7).toFloat(),
                    (yOffsetBase + yOffset + 2).toFloat(),
                    8.0f,
                    8.0f,
                    0.0f,
                    0.0f,
                    32.0f,
                    32.0f,
                    false,
                    false,
                    32.0f,
                    32.0f
                )
                GlStateManager.popMatrix()
                this.fontRenderer.drawString(child.getLocalizedName(), 17 + xOffset, yOffsetBase + yOffset + 2, color)
            }

            yOffset += 14
            if (recursive && child == this.selectedBody) {
                val grandchildren: MutableList<CelestialBody> = this.getChildren(child, 0, this.max_scroll)
                if (grandchildren.size > 0) {
                    if (this.animateGrandchildren == 14 * grandchildren.size) {
                        yOffset += this.drawChildren(grandchildren, 10, yOffset, false)
                    } else {
                        if (this.animateGrandchildren >= 14) {
                            val partial: MutableList<CelestialBody> = LinkedList<CelestialBody>()

                            for (j: Int in 0..<this.animateGrandchildren / 14) {
                                partial.add(grandchildren.get(j))
                            }

                            this.drawChildren(partial, 10, yOffset, false)
                        }

                        yOffset += this.animateGrandchildren
                        this.animateGrandchildren += 2
                    }
                }
            }
        }

        if (ClientProxy.smallInfoOnMap && recursive) {
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
            this.mc.renderEngine.bindTexture(guiMain2)
            this.drawTexturedModalRect(BORDER_SIZE + 18, yOffsetBase + yOffset - 1, 62, 4, 280, 74, 62, 4, false, true)
            GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f)
            val planetZoomedNotMoon: Boolean = this.isZoomed() && this.selectedParent !is Planet
            if (this.scroll != 0) {
                this.drawTexturedModalRect(BORDER_SIZE + 47, yOffsetBase - 4, 7, 3, 344, 74, 7, 3, false, false)
            }

            if (this.scroll != this.getChildren(if (planetZoomedNotMoon) this.selectedBody else this.selectedParent).size - this.max_scroll) {
                this.drawTexturedModalRect(BORDER_SIZE + 47, yOffsetBase + yOffset, 7, 3, 344, 74, 7, 3, false, true)
            }
        }

        return yOffset
    }

    override fun mouseClicked(x: Int, y: Int, button: Int) {
        var clickHandled: Boolean = false
        val LHS: Int = BORDER_SIZE + BORDER_EDGE_SIZE
        val RHS: Int = this.width - LHS
        val TOP: Int = LHS
        val BOT: Int = this.height - LHS
        if (x > RHS - 85 && x < RHS - 5 && y > this.height - LHS - 44 && y < this.height - LHS - 33) {
            ClientProxy.smallInfoOnMap = !ClientProxy.smallInfoOnMap
        }

        if (x > RHS - 85 && x < RHS - 5 && y > this.height - LHS - 30 && y < this.height - LHS - 19) {
            ClientProxy.enableInfoOnMap = !ClientProxy.enableInfoOnMap
        }

        if (x > RHS - 85 && x < RHS - 5 && y > this.height - LHS - 16 && y < this.height - LHS - 5) {
            ClientProxy.renderRingZones = !ClientProxy.renderRingZones
        }

        if (x > LHS + 6 && x < LHS + 93 && y > LHS + 12 && y < LHS + 26 && !this.enableTraveltime) {
            if (this.galaxylist.size > 1) {
                this.showGalaxyList = !this.showGalaxyList
            }

            if (this.showStarList) {
                this.showStarList = false
            }
        }

        if (x > LHS + 6 && x < LHS + 93 && y > LHS + 32 && y < LHS + 43 && !this.enableTraveltime) {
            if ((GalaxyRegistry.getRegisteredSolarSystems())!!.size > 1) {
                this.showStarList = !this.showStarList
            }

            if (this.showGalaxyList) {
                this.showGalaxyList = false
            }
        }

        if (this.showStarList) {
            var l: Int = 0
            var posX: Int = 0

            for (s: SolarSystem in this.starlist) {
                if (x > LHS + posX * 150 + 100 && x < LHS + posX * 150 + 193 && y > TOP + 30 * l + 8 && y < TOP + 30 * l + 37) {
                    this.selectedParent = s
                    this.showStarList = false
                    this.selectedBody = s.getMainStar()
                    this.selectionState = EnumSelection.SELECTED
                    this.zoom = -0.2f
                    if (ClientProxy.smallInfoOnMap) {
                        this.scroll = 0
                    }

                    this.position = Vector2f(
                        this.getCelestialBodyPosition(this.selectedBody).x,
                        this.getCelestialBodyPosition(this.selectedBody).y
                    )
                    clickHandled = true
                    if (this.selectedBody is IChildBody) {
                        this.galaxy = (((this.selectedBody as IChildBody).getParentPlanet())!!.getParentSolarSystem())!!
                            .getUnlocalizedParentGalaxyName()
                    }

                    if (this.selectedBody is Planet) {
                        this.galaxy =
                            ((this.selectedBody as Planet).getParentSolarSystem())!!.getUnlocalizedParentGalaxyName()
                    }

                    if (this.selectedBody is Star) {
                        this.galaxy =
                            ((this.selectedBody as Star).getParentSolarSystem())!!.getUnlocalizedParentGalaxyName()
                    }
                }

                if (++l % 7 == 0) {
                    posX++
                    l = 0
                }
            }
        }

        if (this.showGalaxyList) {
            for (i: Int in this.galaxylist.indices) {
                if (x > LHS + 100 + 70 * i && x < LHS + 193 + 70 * i && y > TOP + 4 && y < TOP + 60) {
                    this.galaxy = this.galaxylist.get(i)
                    this.zoom = -1.0f
                    this.showGalaxyList = false
                    this.starlist.clear()
                    this.refreshBodies()
                    val var24: MutableIterator<*> = this.starlist.iterator()
                    if (var24.hasNext()) {
                        val s: SolarSystem = var24.next() as SolarSystem
                        this.selectedBody = s.getMainStar()
                        this.selectionState = EnumSelection.SELECTED
                        this.zoom = -0.2f
                        this.position = Vector2f(
                            this.getCelestialBodyPosition(this.selectedBody).x,
                            this.getCelestialBodyPosition(this.selectedBody).y
                        )
                        clickHandled = true
                    }
                }
            }
        }

        if (!this.enableTraveltime && this.selectedBody != null && x > LHS && x < LHS + 88 && y > TOP && y < TOP + 13) {
            this.unselectCelestialBody()
        } else {
            if (!this.mapMode && x >= RHS - 95 && x < RHS && y > TOP + 62 + this.canCreateOffset && y < TOP + 62 + 12 + this.canCreateOffset && this.selectedBody != null) {
                val recipe: SpaceStationRecipe? = WorldUtil.getSpaceStationRecipe((this.selectedBody)!!.getDimensionID())
                if (recipe != null && this.canCreateSpaceStation(this.selectedBody)) {
                    if (recipe.matches(this.mc.player, false) || this.mc.player.capabilities.isCreativeMode) {
                        (GalacticraftCore.packetPipeline)!!
                            .sendToServer(
                                PacketSimple(
                                    PacketSimple.EnumSimplePacket.S_BIND_SPACE_STATION_ID,
                                    GCCoreUtil.getDimensionID(this.mc.world),
                                    arrayOf<Any>((this.selectedBody)!!.getDimensionID())
                                )
                            )
                        if (!this.isZoomed()) {
                            this.selectionState = EnumSelection.ZOOMED
                            this.preSelectZoom = this.zoom
                            this.preSelectPosition = this.position
                            this.ticksSinceSelectionF = 0.0f
                            this.ticksSinceSelection = 0
                            this.doneZooming = false
                        }

                        return
                    }

                    clickHandled = true
                }
            }

            if (this.mapMode && x > RHS - 88 && x < RHS && y > TOP && y < TOP + 13) {
                this.mc.displayGuiScreen(null)
                this.mc.setIngameFocus()
                clickHandled = true
            }

            if (this.selectedBody != null && !this.mapMode && (this.selectedBody !is Star) && x > RHS - 88 && x < RHS && y > TOP && y < TOP + 13) {
                if (this.selectedBody !is Satellite || this.selectedStationOwner != "") {
                    if (!this.mc.player.capabilities.isCreativeMode && this.currenttier >= this.tierneed && this.enableNewTierSystem) {
                        this.enableTraveltime = this.canTravel
                    } else {
                        this.teleportToSelectedBody()
                    }
                }

                clickHandled = true
            }

            val mouseX: Int = Mouse.getX()
            val mouseY: Int = Mouse.getY() * -1 + Minecraft.getMinecraft().displayHeight - 1
            if (this.selectedBody is Satellite) {
                if (this.renamingSpaceStation) {
                    if (x >= this.width / 2 - 90 && x <= this.width / 2 + 90 && y >= this.height / 2 - 38 && y <= this.height / 2 + 38) {
                        if (x >= this.width / 2 - 90 + 17 && x <= this.width / 2 - 90 + 17 + 72 && y >= this.height / 2 - 38 + 59 && y <= this.height / 2 - 38 + 59 + 12) {
                            val strName: String? = PlayerUtil.getName(this.mc.player)
                            val selectedSatellite: Satellite = this.selectedBody as Satellite
                            var spacestationID: Int? =
                                (((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellite)) as MutableMap<*, *>).get(
                                    strName
                                ) as StationDataGUI)
                                    .getStationDimensionID()
                            if (spacestationID == null) {
                                spacestationID =
                                    (((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellite)) as MutableMap<*, *>)
                                        .get(strName!!.lowercase(Locale.getDefault())) as StationDataGUI)
                                        .getStationDimensionID()
                            }

                            if (spacestationID != null) {
                                (((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellite)) as MutableMap<*, *>).get(
                                    strName
                                ) as StationDataGUI)
                                    .setStationName(this.renamingString)
                                (GalacticraftCore.packetPipeline)!!
                                    .sendToServer(
                                        PacketSimple(
                                            PacketSimple.EnumSimplePacket.S_RENAME_SPACE_STATION,
                                            GCCoreUtil.getDimensionID(this.mc.world),
                                            arrayOf<Any?>(this.renamingString, spacestationID)
                                        )
                                    )
                            }

                            this.renamingSpaceStation = false
                        }

                        if (x >= this.width / 2 && x <= this.width / 2 + 72 && y >= this.height / 2 - 38 + 59 && y <= this.height / 2 - 38 + 59 + 12) {
                            this.renamingSpaceStation = false
                        }

                        clickHandled = true
                    }
                } else {
                    this.drawTexturedModalRect(this.width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false)
                    if (x >= this.width / 2 - 47 && x <= this.width / 2 - 47 + 94 && y >= TOP && y <= TOP + 11 && this.selectedStationOwner.length != 0 && this.selectedStationOwner.equals(
                            PlayerUtil.getName(this.mc.player),
                            ignoreCase = true
                        )
                    ) {
                        this.renamingSpaceStation = true
                        this.renamingString = null
                        clickHandled = true
                    }

                    val selectedSatellitex: Satellite = this.selectedBody as Satellite
                    val stationListSize: Int =
                        ((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellitex)) as MutableMap<*, *>).size
                    val max: Int = min(this.height / 2 / 14, stationListSize)
                    var xPos: Int = RHS - 85
                    var yPos: Int = TOP + 45
                    if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                        if (this.spaceStationListOffset > 0) {
                            this.spaceStationListOffset--
                        }

                        clickHandled = true
                    }

                    xPos = RHS - 85
                    yPos = TOP + 49 + max * 14
                    if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                        if (max + this.spaceStationListOffset < stationListSize) {
                            this.spaceStationListOffset++
                        }

                        clickHandled = true
                    }

                    val it: MutableIterator<MutableMap.MutableEntry<String?, StationDataGUI?>> =
                        (((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellitex)) as MutableMap<*, *>)
                            .entries
                            .iterator() as MutableIterator<MutableMap.MutableEntry<String?, GuiCelestialSelection.StationDataGUI?>>)
                    var ix: Int = 0

                    var j: Int = 0
                    while (it.hasNext() && ix < max) {
                        val e: MutableMap.MutableEntry<String?, StationDataGUI?> = it.next()
                        if (j >= this.spaceStationListOffset) {
                            var xOffset: Int = 0
                            if (e.key.equals(this.selectedStationOwner, ignoreCase = true)) {
                                xOffset -= 5
                            }

                            xPos = RHS - 95 + xOffset
                            yPos = TOP + 50 + ix * 14
                            if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
                                this.selectedStationOwner = (e.key)!!
                                clickHandled = true
                            }

                            ix++
                        }
                        j++
                    }
                }
            }

            val xPosx: Int = LHS + 2
            var yPosx: Int = TOP + 10
            val planetZoomedMoon: Boolean = this.isZoomed() && this.selectedParent is Planet
            if (x >= xPosx && x <= xPosx + 93 && y >= yPosx && y <= yPosx + 12 && this.selectedParent is CelestialBody) {
                if (this.selectedBody == null) {
                    this.preSelectZoom = this.zoom
                    this.preSelectPosition = this.position
                }

                val selectionCountOld: EnumSelection? = this.selectionState
                if (!this.enableTraveltime && this.isSelected()) {
                    this.unselectCelestialBody()
                }

                if (selectionCountOld == EnumSelection.ZOOMED) {
                    this.selectionState = EnumSelection.SELECTED
                }

                this.selectedBody = this.selectedParent as CelestialBody?
                this.ticksSinceSelectionF = 0.0f
                this.ticksSinceSelection = 0
                this.selectionState = EnumSelection.entries[this.selectionState.ordinal + 1]
                if (this.isZoomed() && !planetZoomedMoon) {
                    this.ticksSinceMenuOpenF = 0.0f
                    this.ticksSinceMenuOpen = 0
                }

                clickHandled = true
            }

            yPosx += 22
            if (x >= xPosx && x <= xPosx + 93 && y >= yPosx && y <= yPosx + 12) {
                if (planetZoomedMoon) {
                    if (this.selectedBody == null) {
                        this.preSelectZoom = this.zoom
                        this.preSelectPosition = this.position
                    }

                    val selectionCountOldx: EnumSelection? = this.selectionState
                    if (!this.enableTraveltime && this.isSelected()) {
                        this.unselectCelestialBody()
                    }

                    if (selectionCountOldx == EnumSelection.ZOOMED) {
                        this.selectionState = EnumSelection.SELECTED
                    }

                    this.selectedBody = this.selectedParent as CelestialBody?
                    this.ticksSinceSelectionF = 0.0f
                    this.ticksSinceSelection = 0
                    this.selectionState = EnumSelection.entries[this.selectionState.ordinal + 1]
                }

                clickHandled = true
            }

            if (!clickHandled) {
                val children: MutableList<CelestialBody> = this.getChildren(
                    if (this.isZoomed()) this.selectedBody else this.selectedParent,
                    this.scroll,
                    this.max_scroll
                )
                yPosx = TOP + 50

                for (child: CelestialBody in children) {
                    clickHandled = this.testClicked(child, if (child == this.selectedBody) 5 else 0, yPosx, x, y, false)
                    yPosx += 14
                    if (!clickHandled && !this.isZoomed() && child == this.selectedBody) {
                        val grandchildren: MutableList<CelestialBody> = this.getChildren(child, 0, this.max_scroll)
                        var gOffset: Int = 0

                        for (grandchild: CelestialBody? in grandchildren) {
                            if (gOffset + 14 > this.animateGrandchildren) {
                                break
                            }

                            clickHandled = this.testClicked(grandchild, 10, yPosx, x, y, true)
                            yPosx += 14
                            gOffset += 14
                            if (clickHandled) {
                                break
                            }
                        }

                        yPosx += this.animateGrandchildren - gOffset
                    }

                    if (clickHandled) {
                        break
                    }
                }
            }

            if (!clickHandled) {
                for (e: MutableMap.MutableEntry<CelestialBody?, Vector3f?> in this.planetPosMap.entries) {
                    val bodyClicked: CelestialBody? = e.key
                    if (this.selectedBody != null || bodyClicked !is IChildBody) {
                        val iconSize: Float = e.value!!.z
                        if (mouseX.toFloat() >= e.value!!.x - iconSize && mouseX.toFloat() <= e.value!!.x + iconSize && mouseY.toFloat() >= e.value!!.y - iconSize && mouseY.toFloat() <= e.value!!.y + iconSize && (this.selectedBody !== bodyClicked || !this.isZoomed())) {
                            if (this.isSelected() && this.selectedBody !== bodyClicked) {
                                if (this.isZoomed()) {
                                    this.selectionState = EnumSelection.SELECTED
                                }

                                if (ClientProxy.smallInfoOnMap) {
                                    this.scroll = 0
                                }
                            }

                            if (bodyClicked !== this.selectedBody) {
                                this.lastSelectedBody = this.selectedBody
                                this.animateGrandchildren = 0
                                if (this.selectedBody !is IChildBody || (this.selectedBody as IChildBody).getParentPlanet() !== bodyClicked) {
                                    this.selectionState = EnumSelection.UNSELECTED
                                }
                            } else {
                                this.doneZooming = false
                                this.planetZoom = 0.0f
                            }

                            if (!this.enableTraveltime) {
                                this.selectedBody = bodyClicked
                                this.ticksSinceSelectionF = 0.0f
                                this.ticksSinceSelection = 0
                                this.selectionState = EnumSelection.entries[this.selectionState.ordinal + 1]
                            }

                            if (this.isZoomed()) {
                                this.ticksSinceMenuOpenF = 0.0f
                                this.ticksSinceMenuOpen = 0
                            }

                            if (this.selectedBody is Satellite
                                && ((this.spaceStationMap)!!.get(this.getSatelliteParentID(this.selectedBody as Satellite)) as MutableMap<*, *>).size == 1
                            ) {
                                val it: MutableIterator<MutableMap.MutableEntry<String?, StationDataGUI?>?> =
                                    (((this.spaceStationMap)!!.get(this.getSatelliteParentID(this.selectedBody as Satellite?)) as MutableMap<*, *>)
                                        .entries
                                        .iterator() as MutableIterator<MutableMap.MutableEntry<String?, GuiCelestialSelection.StationDataGUI?>?>)
                                this.selectedStationOwner = (it.next()!!.key)!!
                            }

                            clickHandled = true
                            break
                        }
                    }
                }
            }

            if (!clickHandled) {
                if (!this.enableTraveltime && this.selectedBody != null) {
                    if (x > RHS - 35 && x < RHS - 10 && y > BOT - 130 && y < BOT - 130 + 15) {
                        val data: BodiesData? = BodiesRegistry.getData(this.selectedBody)
                        val k: Int = 2
                        if (this.small_page < k) {
                            this.small_page++
                        }
                    } else if (x <= RHS - 120 || x >= RHS - 90 || y <= BOT - 130 || y >= BOT - 130 + 15) {
                        this.unselectCelestialBody()
                    } else if (this.small_page > 0) {
                        this.small_page--
                    }

                    this.planetZoom = 0.0f
                }

                this.mouseDragging = true
            }

            var selectedParent: Any? = this.selectedParent
            if (this.selectedBody is IChildBody) {
                selectedParent = (this.selectedBody as IChildBody).getParentPlanet()
            } else if (this.selectedBody is Planet) {
                selectedParent = (this.selectedBody as Planet).getParentSolarSystem()
            } else if (this.selectedBody is Star) {
                selectedParent = (this.selectedBody as Star).getParentSolarSystem()
            } else if (this.selectedBody == null) {
                if (this.lastSelectedBody is Planet) {
                    selectedParent = (this.lastSelectedBody as Planet).getParentSolarSystem()
                }

                if (this.lastSelectedBody is IChildBody) {
                    selectedParent = ((this.lastSelectedBody as IChildBody).getParentPlanet())!!.getParentSolarSystem()
                }
            }

            if (this.selectedParent !== selectedParent) {
                this.selectedParent = selectedParent
                this.ticksSinceMenuOpenF = 0.0f
                this.ticksSinceMenuOpen = 0
            }
        }
    }

    override fun testClicked(
        body: CelestialBody?,
        xOffset: Int,
        yPos: Int,
        x: Int,
        y: Int,
        grandchild: Boolean
    ): Boolean {
        val xPos: Int = BORDER_SIZE + BORDER_EDGE_SIZE + 2 + xOffset
        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12 && (this.selectedBody !== body || !this.isZoomed())) {
            if (this.selectedBody == null) {
                this.preSelectZoom = this.zoom
                this.preSelectPosition = this.position
            }

            val selectionCountOld: EnumSelection? = this.selectionState
            if (selectionCountOld == EnumSelection.ZOOMED) {
                this.selectionState = EnumSelection.SELECTED
            }

            this.doneZooming = false
            this.planetZoom = 0.0f
            if (body !== this.selectedBody) {
                this.lastSelectedBody = this.selectedBody
                this.selectionState = EnumSelection.SELECTED
                if (ClientProxy.smallInfoOnMap) {
                    this.scroll = 0
                }
            } else if (!this.enableTraveltime) {
                this.selectionState = EnumSelection.entries[this.selectionState.ordinal + 1]
            }

            if (!this.enableTraveltime) {
                this.selectedBody = body
                this.ticksSinceSelectionF = 0.0f
                this.ticksSinceSelection = 0
                this.small_page = 0
                if (grandchild) {
                    this.selectionState = EnumSelection.ZOOMED
                }
            }

            if (this.isZoomed()) {
                this.ticksSinceMenuOpenF = 0.0f
                this.ticksSinceMenuOpen = 0
            }

            this.animateGrandchildren = 0
            return true
        } else {
            return false
        }
    }

    override fun drawScreen(mousePosX: Int, mousePosY: Int, partialTicks: Float) {
        this.ticksSinceMenuOpenF += partialTicks
        this.ticksTotalF += partialTicks
        if (this.selectedBody != null) {
            this.ticksSinceSelectionF += partialTicks
        }

        if (this.selectedBody == null && this.ticksSinceUnselectionF >= 0.0f) {
            this.ticksSinceUnselectionF += partialTicks
        }

        if (Mouse.hasWheel()) {
            val wheel: Float = Mouse.getDWheel().toFloat() / (if (this.selectedBody == null) 500.0f else 250.0f)
            if (wheel != 0.0f) {
                if (mousePosX < 105 && ClientProxy.smallInfoOnMap) {
                    val planetZoomedNotMoon: Boolean = this.isZoomed() && this.selectedParent !is Planet
                    if (wheel > 0.0f && this.scroll > 0) {
                        this.scroll--
                    } else if (wheel < 0.0f
                        && this.scroll < this.getChildren(if (planetZoomedNotMoon) this.selectedBody else this.selectedParent).size - this.max_scroll
                    ) {
                        this.scroll++
                    }
                } else if (this.selectedBody != null && this.isZoomed()) {
                    this.planetZoom = min(max(this.planetZoom + wheel, -4.9f), 5.0f)
                } else {
                    this.zoom = min(max(this.zoom + wheel * (this.zoom + 2.0f) / 10.0f, -1.095f), 3.0f)
                }
            }
        }

        GL11.glPushMatrix()
        GL11.glEnable(3042)
        val camMatrix: Matrix4f = Matrix4f()
        Matrix4f.translate(Vector3f(0.0f, 0.0f, -9000.0f), camMatrix, camMatrix)
        val viewMatrix: Matrix4f = Matrix4f()
        viewMatrix.m00 = 2.0f / this.width.toFloat()
        viewMatrix.m11 = 2.0f / (-this.height).toFloat()
        viewMatrix.m22 = -2.2222222E-4f
        viewMatrix.m30 = -1.0f
        viewMatrix.m31 = 1.0f
        viewMatrix.m32 = -2.0f
        GL11.glMatrixMode(5889)
        GL11.glLoadIdentity()
        val fb: FloatBuffer = BufferUtils.createFloatBuffer(512)
        (fb as Buffer).rewind()
        viewMatrix.store(fb)
        (fb as Buffer).flip()
        GL11.glMultMatrix(fb)
        (fb as Buffer).clear()
        GL11.glMatrixMode(5888)
        GL11.glLoadIdentity()
        (fb as Buffer).rewind()
        camMatrix.store(fb)
        (fb as Buffer).flip()
        (fb as Buffer).clear()
        GL11.glMultMatrix(fb)
        this.setBlackBackground()
        var drawGrid: Boolean = true
        if (AsmodeusConfig.enableImgOnGalaxyMap) {
            GL11.glPushMatrix()
            this.setImgBackground(guiImg)
            drawGrid = false
            GL11.glPopMatrix()
        }

        GL11.glPushMatrix()
        val worldMatrix: Matrix4f = this.setIsometric(partialTicks)
        val gridSize: Float = 14000.0f
        if (drawGrid) {
            this.drawGrid(gridSize, (this.height / 3).toFloat() / 3.5f)
        }

        if (this.selectedBody is Star) {
            this.drawAgeCircle(worldMatrix)
        }

        this.drawCircles()
        this.drawVortex()
        GL11.glPopMatrix()
        val matrixMap: HashMap<CelestialBody?, Matrix4f?> = this.drawCelestialBodies(worldMatrix)
        this.planetPosMap.clear()

        for (e in matrixMap.entries) {
            val planetMatrix: Matrix4f = (e.value)!!
            val matrix0: Matrix4f = Matrix4f.mul(viewMatrix, planetMatrix, planetMatrix)
            val x: Int =
                floor((matrix0.m30.toDouble() * 0.5 + 0.5) * Minecraft.getMinecraft().displayWidth.toDouble()).toInt()
            val y: Int = floor(
                Minecraft.getMinecraft().displayHeight.toDouble() - (matrix0.m31.toDouble() * 0.5 + 0.5) * Minecraft.getMinecraft().displayHeight.toDouble()
            ).toInt()
            val vec: Vector2f = Vector2f(x.toFloat(), y.toFloat())
            val scaleVec: Matrix4f = Matrix4f()
            scaleVec.m00 = matrix0.m00
            scaleVec.m11 = matrix0.m11
            scaleVec.m22 = matrix0.m22
            val newVec: Vector4f = Matrix4f.transform(scaleVec, Vector4f(2.0f, -2.0f, 0.0f, 0.0f), null)
            val iconSize: Float = (newVec.y
                    * (Minecraft.getMinecraft().displayHeight.toFloat() / 2.0f)
                    * (if (e.key is Star) 2 else 1).toFloat() * (if (e.key === this.selectedBody) 1.5f else 1.0f))
            this.planetPosMap.put(e.key, Vector3f(vec.x, vec.y, iconSize))
        }

        this.drawSelectionCursor(fb, worldMatrix)

        try {
            this.drawButtons(mousePosX, mousePosY)
        } catch (var21: Exception) {
            if (!this.errorLogged) {
                this.errorLogged = true
                GCLog.severe("Problem identifying planet or dimension in an add on for Galacticraft!")
                GCLog.severe(
                    "(The problem is likely caused by a dimension ID conflict.  Check configs for dimension clashes.  You can also try disabling Mars space station in configs.)"
                )
                var21.printStackTrace()
            }
        }

        this.drawBorder()
        GL11.glPopMatrix()
        GL11.glMatrixMode(5889)
        GL11.glLoadIdentity()
        GL11.glMatrixMode(5888)
        GL11.glLoadIdentity()
    }

    override fun drawSelectionCursor(fb: FloatBuffer, worldMatrix: Matrix4f?) {
        GL11.glPushMatrix()
        when (this.selectionState) {
            EnumSelection.SELECTED -> if (this.selectedBody != null) {
                this.setupMatrix(this.selectedBody, worldMatrix, fb)
                (fb as Buffer).clear()
                GL11.glScalef(0.06666667f, 0.06666667f, 1.0f)
                this.mc.renderEngine.bindTexture(guiMain0)
                val colMod: Float =
                    if (this.getZoomAdvanced() < 4.9f) (sin((this.ticksSinceSelectionF / 2.0f).toDouble()) * 0.5 + 0.5).toFloat() else 1.0f
                GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f * colMod)
                val width: Int = floor(
                    this.getWidthForCelestialBody(this.selectedBody)
                        .toDouble() / 2.0 * (if (this.selectedBody is IChildBody) 9.0 else 30.0)
                ).toInt()
                this.drawTexturedModalRect(-width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false)
                GL11.glPushMatrix()
                val size: Float = if (this.selectedBody is IChildBody) 2.0f else 12.0f
                GL11.glScalef(size, size, size)
                val name: String = (this.selectedBody)!!.getLocalizedName()
                val white: Int = Utils.getIntColor(255, 255, 255, 255)
                val yellow: Int = Utils.getIntColor(255, 255, 0, 255)
                this.drawString(
                    this.fontRenderer,
                    name,
                    4 + this.getWidthForCelestialBody(this.selectedBody),
                    -5,
                    if (this.selectedBody is Star) yellow else white
                )
                GL11.glPopMatrix()
            }

            EnumSelection.ZOOMED -> if (this.selectedBody != null) {
                this.setupMatrix(this.selectedBody, worldMatrix, fb)
                (fb as Buffer).clear()
                val div: Float = this.zoom + 1.0f - this.planetZoom
                val scale: Float = Math.max(0.3f, 1.5f / (this.ticksSinceSelectionF / 5.0f)) * 2.0f / div
                GL11.glScalef(scale, scale, 1.0f)
                this.mc.renderEngine.bindTexture(guiMain0)
                val colMod: Float =
                    if (this.getZoomAdvanced() < 4.9f) (sin((this.ticksSinceSelectionF / 1.0f).toDouble()) * 0.5 + 0.5).toFloat() else 1.0f
                GL11.glColor4f(0.4f, 0.8f, 1.0f, 1.0f * colMod)
                val width: Int = this.getWidthForCelestialBody(this.selectedBody) * 13
                this.drawTexturedModalRect(-width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false)
            }
        
else -> {}
}

        GL11.glPopMatrix()
    }

    override fun getWidthForCelestialBody(celestialBody: CelestialBody?): Int {
        val zoomed: Boolean = celestialBody === this.selectedBody && this.selectionState == EnumSelection.SELECTED
        var size: Float = (celestialBody)!!.getRelativeSize()
        if (size.toDouble() < 0.9 && celestialBody is IChildBody) {
            size = 1.0f
        }

        if (size > 5.0f) {
            size = 5.0f
        }

        if (size < 0.3f) {
            size = 0.3f
        }

        return (if (celestialBody is Star)
            (if (zoomed) 12.0f * size else 8.0f * size)
        else
            (if (celestialBody is Planet)
                (if (zoomed) 6.0f * size else 4.0f * size)
            else
                (if (celestialBody is IChildBody) (if (zoomed) 6.0f * size else 4.0f * size) else 2.0f)
                    )
                ).toInt()
    }

    override fun drawCelestialBodies(worldMatrix: Matrix4f?): HashMap<CelestialBody?, Matrix4f?> {
        GL11.glColor3f(1.0f, 1.0f, 1.0f)
        val fb: FloatBuffer = BufferUtils.createFloatBuffer(512)
        val matrixMap: HashMap<CelestialBody?, Matrix4f?> = Maps.newHashMap<CelestialBody?, Matrix4f?>()

        for (body: CelestialBody in this.bodiesToRender) {
            val hasParent: Boolean = body is IChildBody
            val alpha: Float = this.getAlpha(body)
            if (body is Satellite && alpha > 0.0f || body !is Satellite && alpha >= 0.0f) {
                GlStateManager.pushMatrix()
                val worldMatrixLocal: Matrix4f = this.setupMatrix(body, worldMatrix, fb, if (hasParent) 0.25f else 1.0f)
                val preEvent: CelestialBodyRenderEvent.Pre = CelestialBodyRenderEvent.Pre(body, body.getBodyIcon(), 16)
                MinecraftForge.EVENT_BUS.post(preEvent)
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                if (preEvent.celestialBodyTexture != null) {
                    this.mc.renderEngine.bindTexture(preEvent.celestialBodyTexture)
                }

                if (!preEvent.isCanceled()) {
                    val size: Int = this.getWidthForCelestialBody(body)
                    var xOffset: Float = 0.0f
                    var yOffset: Float = 0.0f
                    if (size % 2 != 0) {
                        xOffset = if (body.getRelativeSize() < 1.0f) -0.5f else 0.0f
                        yOffset = if (body.getRelativeSize() < 1.0f) -0.5f else 0.0f
                    }

                    val data: BodiesData? = BodiesRegistry.getData(body)
                    if (body is Star || data != null && data.getType() == IAdvancedSpace.TypeBody.STAR && data.getStarColor() != null) {
                        GL11.glPushMatrix()
                        GL11.glShadeModel(7425)
                        GL11.glDisable(3553)
                        GL11.glEnable(3008)
                        GL11.glAlphaFunc(516, 0.0f)
                        var r: Float = 1.0f
                        var g: Float = 1.0f
                        var b: Float = 0.8f
                        var a: Float = 0.7f
                        val f10: Float = (size * 4).toFloat() * min(2.0f - this.zoom / 2.0f, 2.0f)
                        if (data != null && data.getType() == IAdvancedSpace.TypeBody.STAR) {
                            if (data.getStarColor() != null) {
                                r = (data.getStarColor())!!.getColor().getX().toFloat() / 255.0f
                                g = (data.getStarColor())!!.getColor().getY().toFloat() / 255.0f
                                b = (data.getStarColor())!!.getColor().getZ().toFloat() / 255.0f
                                a = 0.7f
                            }

                            if (data.getStarType() == IAdvancedSpace.StarType.BLACKHOLE) {
                                a = 0.0f
                                b = 0.0f
                                g = 0.0f
                                r = 0.0f
                            }
                        }

                        val xSize: Float = 0.0f
                        val ySize: Float = 0.0f
                        GL11.glColor4f(r, g, b, a)
                        GL11.glDisable(2884)
                        GL11.glBegin(6)
                        GL11.glVertex2d(xSize.toDouble(), ySize.toDouble())

                        var angle: Int = 0
                        while (angle <= 360) {
                            if (angle % 120 == 0) {
                                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f)
                            }

                            val x: Double = f10.toDouble() * cos(angle.toDouble() * Math.PI / 180.0)
                            val y: Double = f10.toDouble() * sin(angle.toDouble() * Math.PI / 180.0)
                            GL11.glVertex2d(x + xSize.toDouble(), y + ySize.toDouble())
                            angle += 60
                        }

                        GL11.glEnd()
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                        GL11.glDisable(3008)
                        GL11.glShadeModel(7424)
                        GL11.glPopMatrix()
                    }

                    this.drawTexturedModalRect(
                        xOffset + (-size / 2).toFloat(),
                        yOffset + (-size / 2).toFloat(),
                        size.toFloat(),
                        size.toFloat(),
                        0.0f,
                        0.0f,
                        preEvent.textureSize.toFloat(),
                        preEvent.textureSize.toFloat(),
                        false,
                        false,
                        preEvent.textureSize.toFloat(),
                        preEvent.textureSize.toFloat()
                    )
                    matrixMap.put(body, worldMatrixLocal)
                }

                val postEvent: CelestialBodyRenderEvent.Post = CelestialBodyRenderEvent.Post(body)
                MinecraftForge.EVENT_BUS.post(postEvent)
                GlStateManager.popMatrix()
            }
        }

        for (bodyx: CelestialBody in this.bodiesToRender) {
            val hasParent: Boolean = bodyx is IChildBody
            val alpha: Float = this.getAlpha(bodyx)
            if (bodyx is Satellite && alpha > 0.0f || bodyx !is Satellite && alpha >= 0.0f) {
                GlStateManager.pushMatrix()
                val worldMatrixLocalx: Matrix4f =
                    this.setupMatrix(bodyx, worldMatrix, fb, if (hasParent) 0.25f else 1.0f)
                if (this.selectionState == EnumSelection.UNSELECTED && !hasParent || this.selectionState == EnumSelection.ZOOMED && hasParent) {
                    var scale: Float = 0.0f
                    if (this.zoom + 0.5f > 0.0f) {
                        scale = min(this.zoom + 0.5f, 0.7f)
                    }

                    GL11.glScalef(scale, scale, 0.0f)
                    val name: String = bodyx.getLocalizedName()
                    val white: Int = Utils.getIntColor(255, 255, 255, 255)
                    val yellow: Int = Utils.getIntColor(255, 255, 0, 255)
                    this.drawString(
                        this.fontRenderer,
                        name,
                        4 + this.getWidthForCelestialBody(bodyx),
                        -5,
                        if (bodyx is Star) yellow else white
                    )
                    if (ClientProxyCore.playerHead != null) {
                        if (this.selectionState == EnumSelection.UNSELECTED && this.mc.world.provider is IGalacticraftWorldProvider
                            && (this.mc.world.provider as IGalacticraftWorldProvider).getCelestialBody() is IChildBody
                        ) {
                            if (bodyx === ((this.mc.world.provider as IGalacticraftWorldProvider).getCelestialBody() as IChildBody).getParentPlanet()) {
                                GlStateManager.enableBlend()
                                GlStateManager.blendFunc(770, 771)
                                GlStateManager.scale(0.25f, 0.25f, 0.25f)
                                this.mc.renderEngine.bindTexture(ClientProxyCore.playerHead)
                                this.drawTexturedModalRect(
                                    20 + this.fontRenderer.getStringWidth(name) * 5,
                                    -18,
                                    32,
                                    32,
                                    32,
                                    32
                                )
                            }
                        } else if (this.mc.world.provider is IGalacticraftWorldProvider
                            && bodyx !== GalacticraftCore.planetOverworld && (this.mc.world.provider as IGalacticraftWorldProvider).getCelestialBody() === bodyx
                        ) {
                            GlStateManager.enableBlend()
                            GlStateManager.blendFunc(770, 771)
                            GlStateManager.scale(0.25f, 0.25f, 0.25f)
                            this.mc.renderEngine.bindTexture(ClientProxyCore.playerHead)
                            this.drawTexturedModalRect(
                                20 + this.fontRenderer.getStringWidth(name) * 5,
                                -18,
                                32,
                                32,
                                32,
                                32
                            )
                        } else if (this.mc.world.provider is WorldProviderSurface && bodyx === GalacticraftCore.planetOverworld) {
                            GlStateManager.enableBlend()
                            GlStateManager.blendFunc(770, 771)
                            GlStateManager.scale(0.25f, 0.25f, 0.25f)
                            this.mc.renderEngine.bindTexture(ClientProxyCore.playerHead)
                            this.drawTexturedModalRect(
                                20 + this.fontRenderer.getStringWidth(name) * 5,
                                -18,
                                32,
                                32,
                                32,
                                32
                            )
                        }
                    }
                }

                GlStateManager.popMatrix()
            }
        }

        return matrixMap
    }

    override fun setupMatrix(body: CelestialBody?, worldMatrix: Matrix4f?, fb: FloatBuffer, scaleXZ: kotlin.Float): Matrix4f {
        val worldMatrix0: Matrix4f = Matrix4f(worldMatrix)
        Matrix4f.translate(this.getCelestialBodyPosition(body), worldMatrix0, worldMatrix0)
        var worldMatrix1: Matrix4f = Matrix4f()
        Matrix4f.rotate(Math.toRadians(this.isometz).toFloat(), Vector3f(0.0f, 0.0f, 1.0f), worldMatrix1, worldMatrix1)
        Matrix4f.rotate(Math.toRadians(-this.isometx).toFloat(), Vector3f(1.0f, 0.0f, 0.0f), worldMatrix1, worldMatrix1)
        if (scaleXZ != 1.0f) {
            Matrix4f.scale(Vector3f(scaleXZ, scaleXZ, 1.0f), worldMatrix1, worldMatrix1)
        }

        worldMatrix1 = Matrix4f.mul(worldMatrix0, worldMatrix1, worldMatrix1)
        (fb as Buffer).rewind()
        worldMatrix1.store(fb)
        (fb as Buffer).flip()
        GL11.glMultMatrix(fb)
        return worldMatrix1
    }

    override fun mouseClickMove(x: Int, y: Int, lastButtonClicked: Int, timeSinceMouseClick: Long) {
        if (this.mouseDragging && this.lastMovePosX != -1 && lastButtonClicked == 0) {
            val deltaX: Int = x - this.lastMovePosX
            val deltaY: Int = y - this.lastMovePosY
            var scollMultiplier: Float = -abs(this.zoom)
            if (this.zoom == -1.0f) {
                scollMultiplier = -10.5f
            } else if (this.zoom >= -0.25f && this.zoom <= 0.15f) {
                scollMultiplier = -0.2f
            }

            if (this.zoom >= 0.15f) {
                scollMultiplier = -0.25f
            }

            this.translation.x = (this.translation.x
                    + ((deltaX - deltaY).toFloat() * scollMultiplier
                    * (if (ConfigManagerCore.invertMapMouseScroll) -1.0f else 1.0f)
                    * ConfigManagerCore.mapMouseScrollSensitivity
                    * 0.2f))
            this.translation.y = (this.translation.y
                    + ((deltaY + deltaX).toFloat() * scollMultiplier
                    * (if (ConfigManagerCore.invertMapMouseScroll) -1.0f else 1.0f)
                    * ConfigManagerCore.mapMouseScrollSensitivity
                    * 0.2f))
            if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
                this.xImgOffset += (deltaX.toFloat() * (scollMultiplier / 4.0f)).toDouble()
                this.yImgOffset += (deltaY.toFloat() * (scollMultiplier / 4.0f)).toDouble()
            }
        }

        this.lastMovePosX = x
        this.lastMovePosY = y
    }

    override fun mouseReleased(x: Int, y: Int, button: Int) {
        super.mouseReleased(x, y, button)
        this.mouseDragging = false
        this.lastMovePosX = -1
        this.lastMovePosY = -1
    }

    open fun setImgBackground(galaxy: ResourceLocation) {
        GL11.glEnable(2929)
        GL11.glDepthMask(false)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(3008)
        GL11.glEnable(3553)
        GL11.glTranslated(-40.0, -40.0, 0.0)
        GL11.glTranslated(this.xImgOffset, this.yImgOffset, 0.0)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(galaxy)
        val width: Int = Display.getWidth()
        val height: Int = Display.getHeight()
        this.drawTexturedModalRect(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat(),
            0.0f,
            0.0f,
            (width * 2).toFloat(),
            (height * 2).toFloat(),
            false,
            false,
            1024.0f,
            1024.0f
        )
        val guiImg_2: ResourceLocation =
            ResourceLocation("asmodeuscore", "textures/gui/galaxymap_nebula_" + this.nebula_img + ".png")
        this.mc.renderEngine.bindTexture(guiImg_2)
        GL11.glTranslated(this.xImgOffset * 1.5, this.yImgOffset * 1.5, 0.0)
        GL11.glColor4f(0.5f, 0.4f, this.nebula_color.z.toFloat(), 0.5f)
        this.drawTexturedModalRect(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat(),
            0.0f,
            0.0f,
            1024.0f,
            1024.0f,
            false,
            false,
            1024.0f,
            1024.0f
        )
        GL11.glDepthMask(true)
        GL11.glDisable(3553)
        GL11.glDisable(2929)
        GL11.glDisable(3008)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    override fun setIsometric(partialTicks: kotlin.Float): Matrix4f {
        val mat0: Matrix4f = Matrix4f()
        Matrix4f.translate(Vector3f(this.width.toFloat() / 2.0f, (this.height / 2).toFloat(), 0.0f), mat0, mat0)
        Matrix4f.rotate(Math.toRadians(this.isometx).toFloat(), Vector3f(1.0f, 0.0f, 0.0f), mat0, mat0)
        Matrix4f.rotate(Math.toRadians(-this.isometz).toFloat(), Vector3f(0.0f, 0.0f, 1.0f), mat0, mat0)
        val zoomLocal: Float = this.getZoomAdvanced()
        this.zoom = zoomLocal
        Matrix4f.scale(Vector3f(1.1f + zoomLocal, 1.1f + zoomLocal, 1.1f + zoomLocal), mat0, mat0)
        val cBodyPos: Vector2f = this.getTranslationAdvanced(partialTicks)
        this.position = this.getTranslationAdvanced(partialTicks)
        Matrix4f.translate(Vector3f(-cBodyPos.x, -cBodyPos.y, 0.0f), mat0, mat0)
        val fb: FloatBuffer = BufferUtils.createFloatBuffer(16)
        (fb as Buffer).rewind()
        mat0.store(fb)
        (fb as Buffer).flip()
        GL11.glMultMatrix(fb)
        return mat0
    }

    override fun getAlpha(body: CelestialBody?): Float {
        var alpha: Float = 1.0f
        if (body is IChildBody) {
            val selected: Boolean = body === this.selectedBody
                    || (body as IChildBody).getParentPlanet() === this.selectedBody && this.selectionState != EnumSelection.SELECTED
            val ready: Boolean = this.lastSelectedBody != null || this.ticksSinceSelection > 35
            val isSibling: Boolean = this.getSiblings(this.selectedBody).contains(body)
            val isPossible: Boolean =
                body !is Satellite || this.possibleBodies != null && (this.possibleBodies)!!.contains(body)
            if ((selected || isSibling) && isPossible) {
                if (this.isZoomed() && (!selected || !ready) && !isSibling) {
                    alpha = min(max((this.ticksSinceSelection - 30).toFloat() / 15.0f, 0.0f), 1.0f)
                }
            } else {
                alpha = 0.0f
            }
        } else {
            val isSelected: Boolean = this.selectedBody === body
            val isChildSelected: Boolean = this.selectedBody is IChildBody
            val isOwnChildSelected: Boolean =
                isChildSelected && (this.selectedBody as IChildBody).getParentPlanet() === body
            if (!isSelected && !isOwnChildSelected && (this.isZoomed() || isChildSelected)) {
                if (this.lastSelectedBody == null && this.selectedBody !is IChildBody) {
                    alpha = 0.4f - min(this.ticksSinceSelection.toFloat() / 25.0f, 0.4f)
                } else {
                    alpha = 0.0f
                }
            }
        }

        return alpha
    }

    override fun drawCircles() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glLineWidth(2.0f)
        var count: Int = 0
        val theta: Float = 0.06981317f
        val cos: Float = cos(0.06981317).toFloat()
        val sin: Float = sin(0.06981317).toFloat()

        for (body: CelestialBody in this.bodiesToRender) {
            var systemOffset: Vector3f = Vector3f(0.0f, 0.0f, 0.0f)
            if (body is IChildBody) {
                systemOffset = this.getCelestialBodyPosition((body as IChildBody).getParentPlanet())
            } else if (body is Planet) {
                systemOffset = this.getCelestialBodyPosition((body.getParentSolarSystem())!!.getMainStar())
            } else if (body is Star) {
                systemOffset = this.getCelestialBodyPosition(body)
            } else if (body is ICelestialRegistry && body !is IChildBody) {
                systemOffset =
                    this.getCelestialBodyPosition(((body as ICelestialRegistry).getParentSolarSystem())!!.getMainStar())
            }

            val orbit_ext_x: Float = if (body is IExBody) (body as IExBody).getXOrbitEccentricity() else 1.0f
            val orbit_ext_y: Float = if (body is IExBody) (body as IExBody).getYOrbitEccentricity() else 1.0f
            val orbit_offset_x: Float = if (body is IExBody) (body as IExBody).getXOrbitOffset() else 0.0f
            val orbit_offset_y: Float = if (body is IExBody) (body as IExBody).getYOrbitOffset() else 0.0f
            var x: Float = this.getScale(body)
            var y: Float = 0.0f
            var alpha: Float = this.getAlpha(body)
            alpha *= this.zoom + 0.8f
            if (alpha > 0.0f) {
                when (count++ % 2) {
                    0 -> GL11.glColor4f(0.0f, 0.2857143f, 0.64285713f, alpha / 1.4f)
                    1 -> GL11.glColor4f(0.0f, 0.4f, 0.9f, alpha / 2.4f)
                }

                if (body == this.selectedBody) {
                    GL11.glColor4f(0.0f, 0.4f, 0.9f, 1.0f)
                }

                val preEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre =
                    CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre(
                        body, systemOffset
                    )
                if (ClientProxy.renderRingZones) {
                    MinecraftForge.EVENT_BUS.post(preEvent)
                }

                if (!preEvent.isCanceled()) {
                    GL11.glTranslatef(systemOffset.x, systemOffset.y, systemOffset.z)
                    GL11.glBegin(2)

                    for (i: Int in 0..89) {
                        GL11.glVertex2f(
                            (x + orbit_offset_x) * (if (orbit_ext_x > 0.0f) orbit_ext_x else 1.0f),
                            (y + orbit_offset_y) * (if (orbit_ext_y > 0.0f) orbit_ext_y else 1.0f)
                        )
                        val temp: Float = x
                        x = cos * x - sin * y
                        y = sin * temp + cos * y
                    }

                    GL11.glEnd()
                    GL11.glTranslatef(-systemOffset.x, -systemOffset.y, -systemOffset.z)
                }

                val postEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Post =
                    CelestialBodyRenderEvent.CelestialRingRenderEvent.Post(
                        body
                    )
                MinecraftForge.EVENT_BUS.post(postEvent)
            }
        }

        GL11.glLineWidth(1.0f)
    }

    private fun drawAgeCircle(worldMatrix: Matrix4f) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glLineWidth(2.0f)
        val theta: Float = 0.06981317f
        val cos: Float = cos(0.06981317).toFloat()
        val sin: Float = sin(0.06981317).toFloat()
        val body: CelestialBody? = this.selectedBody
        var systemOffset: Vector3f = Vector3f(0.0f, 0.0f, 0.0f)
        if (body is Star) {
            systemOffset = this.getCelestialBodyPosition(body)
        }

        var x: Float = 1000.0f
        var y: Float = 0.0f
        GL11.glTranslatef(systemOffset.x, systemOffset.y, systemOffset.z)

        for (count_rings: Int in 1..8) {
            x = (1000 * count_rings).toFloat()
            GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.5f)
            GL11.glBegin(2)

            for (i: Int in 0..89) {
                GL11.glVertex2f(x * 1.0f, y * 1.0f)
                val temp: Float = x
                x = cos * x - sin * y
                y = sin * temp + cos * y
            }

            GL11.glEnd()
            GlStateManager.pushMatrix()
            GL11.glEnable(3553)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.fontRenderer.drawString(
                ((x / 1000.0f).toInt() * 5).toString() + " l.y.",
                x.toInt(),
                y.toInt(),
                16777215
            )
            GL11.glDisable(3553)
            GlStateManager.popMatrix()
        }

        GL11.glTranslatef(-systemOffset.x, -systemOffset.y, -systemOffset.z)
        GL11.glLineWidth(1.0f)
    }

    private fun drawVortex() {
        for (body: CelestialBody? in this.bodiesToRender) {
            GlStateManager.pushMatrix()
            Vector3f(0.0f, 0.0f, 0.0f)
            val data: BodiesData? = BodiesRegistry.getData(body)
            if (body is Star && data != null && data.getStarType() == IAdvancedSpace.StarType.BLACKHOLE) {
                val systemOffset: Vector3f = this.getCelestialBodyPosition(body)
                val size: Float = this.getWidthForCelestialBody(body).toFloat() * 4.0f
                val xOffset: Float = systemOffset.x
                val yOffset: Float = systemOffset.y
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                val n: Float = size / 2.0f
                GlStateManager.translate(xOffset + -size / 2.0f + n, yOffset + -size / 2.0f + n, 0.0f)
                GlStateManager.rotate(this.ticksTotalF % 360.0f * 2.0f, 0.0f, 0.0f, 1.0f)
                GlStateManager.translate(-(xOffset + -size / 2.0f) - n, -(yOffset + -size / 2.0f) - n, 0.0f)
                this.mc.renderEngine.bindTexture(vortexTexture)
                this.drawTexturedModalRect(
                    xOffset + -size / 2.0f,
                    yOffset + -size / 2.0f,
                    size,
                    size,
                    0.0f,
                    0.0f,
                    16.0f,
                    16.0f,
                    false,
                    false,
                    16.0f,
                    16.0f
                )
            }

            GlStateManager.popMatrix()
        }
    }

    override fun getCelestialBodyPosition(cBody: CelestialBody?): Vector3f {
        if (cBody == null) {
            return Vector3f(0.0f, 0.0f, 0.0f)
        } else if (cBody is Star) {
            return if (cBody.getTranslationKey()
                    .equals("star.sol", ignoreCase = true)
            ) Vector3f() else ((cBody.getParentSolarSystem())!!.getMapPosition())!!.toVector3f()
        } else {
            val timeScale: Float = if (cBody is Planet) 200.0f else 2.0f
            val orbit_ext_x: Float = if (cBody is IExBody) (cBody as IExBody).getXOrbitEccentricity() else 1.0f
            val orbit_ext_y: Float = if (cBody is IExBody) (cBody as IExBody).getYOrbitEccentricity() else 1.0f
            val orbit_offset_x: Float = if (cBody is IExBody) (cBody as IExBody).getXOrbitOffset() else 0.0f
            val orbit_offset_y: Float = if (cBody is IExBody) (cBody as IExBody).getYOrbitOffset() else 0.0f
            val distanceFromCenter: Float = this.getScale(cBody)
            val x: Float =
                sin((this.ticksTotalF / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()).toDouble()).toFloat() * distanceFromCenter
            val y: Float =
                cos((this.ticksTotalF / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()).toDouble()).toFloat() * distanceFromCenter
            val cBodyPos: Vector3f = Vector3f(
                (x + orbit_offset_x) * (if (orbit_ext_x > 0.0f) orbit_ext_x else 1.0f),
                (y + orbit_offset_y) * (if (orbit_ext_y > 0.0f) orbit_ext_y else 1.0f),
                0.0f
            )
            if (cBody is Planet) {
                val parentVec: Vector3f = this.getCelestialBodyPosition((cBody.getParentSolarSystem())!!.getMainStar())
                return Vector3f.add(cBodyPos, parentVec, null)
            } else if (cBody is IChildBody) {
                val parentVec: Vector3f = this.getCelestialBodyPosition((cBody as IChildBody).getParentPlanet())
                return Vector3f.add(cBodyPos, parentVec, null)
            } else {
                if (cBody is ICelestialRegistry) {
                    val body: ICelestialRegistry = cBody as ICelestialRegistry
                    if (body.getParentSolarSystem() != null) {
                        val parentVec: Vector3f =
                            this.getCelestialBodyPosition((body.getParentSolarSystem())!!.getMainStar())
                        return Vector3f.add(cBodyPos, parentVec, null)
                    }
                }

                return cBodyPos
            }
        }
    }

    override fun getScale(celestialBody: CelestialBody): Float {
        var scale: Float = 0.2f
        if (celestialBody is Planet) {
            scale = 25.0f
        }

        if (celestialBody is ICelestialRegistry && celestialBody as ICelestialRegistry !is IChildBody) {
            scale = 25.0f
        }

        return 3.0f * celestialBody.getRelativeDistanceFromCenter().unScaledDistance * scale
    }

    @Throws(IOException::class)
    override fun keyTyped(keyChar: Char, keyID: Int) {
        if (this.mapMode) {
            super.keyTyped(keyChar, keyID)
        }

        if (keyID == 1 && !this.enableTraveltime && this.selectedBody != null) {
            this.unselectCelestialBody()
        }

        if (this.renamingSpaceStation) {
            if (keyID == 14) {
                if (this.renamingString != null && (this.renamingString)!!.length > 0) {
                    val toBeParsed: String = (this.renamingString)!!.substring(0, (this.renamingString)!!.length - 1)
                    if (this.isValid(toBeParsed)) {
                        this.renamingString = toBeParsed
                    } else {
                        this.renamingString = ""
                    }
                }
            } else if (keyChar.code == 22) {
                var pastestring: String = getClipboardString()
                if (pastestring == null) {
                    pastestring = ""
                }

                if (this.isValid(this.renamingString + pastestring)) {
                    this.renamingString = this.renamingString + pastestring
                    this.renamingString =
                        (this.renamingString)!!.substring(0, min(this.renamingString.toString().length, 32))
                }
            } else if (this.isValid(this.renamingString + keyChar)) {
                this.renamingString = this.renamingString + keyChar
                this.renamingString = (this.renamingString)!!.substring(0, min((this.renamingString)!!.length, 32))
            }
        } else {
            if (keyID == 28 && !this.mc.player.capabilities.isCreativeMode && this.currenttier >= this.tierneed && this.enableNewTierSystem) {
                if (this.canTravel) {
                    this.enableTraveltime = true
                } else {
                    this.teleportToSelectedBody()
                }
            }
        }
    }

    override fun teleportToSelectedBody() {
        if (this.selectedBody != null && (this.selectedBody)!!.getReachable()
            && (if (this.enableNewTierSystem) this.currenttier >= this.tierneed else this.possibleBodies != null && (this.possibleBodies)!!.contains(
                this.selectedBody
            ))
        ) {
            try {
                val dimension: String
                val dimensionID: Int
                if (this.selectedBody is Satellite) {
                    if (this.spaceStationMap == null) {
                        GCLog.severe("Please report as a BUG: spaceStationIDs was null.")
                        return
                    }

                    val selectedSatellite: Satellite = this.selectedBody as Satellite
                    val mapping: Int? =
                        (((this.spaceStationMap)!!.get(this.getSatelliteParentID(selectedSatellite)) as MutableMap<*, *>).get(
                            this.selectedStationOwner
                        ) as StationDataGUI)
                            .getStationDimensionID()
                    if (mapping == null) {
                        GCLog.severe("Problem matching player name in space station check: " + this.selectedStationOwner)
                        return
                    }

                    dimensionID = mapping
                    val spacestation: WorldProvider? = WorldUtil.getProviderForDimensionClient(dimensionID)
                    if (spacestation == null) {
                        GCLog.severe("Failed to find a spacestation with dimension " + dimensionID)
                        return
                    }

                    dimension = "Space Station " + mapping
                } else {
                    dimensionID = (this.selectedBody)!!.getDimensionID()
                    dimension = (WorldUtil.getDimensionName((WorldUtil.getProviderForDimensionClient(dimensionID))!!))!!
                }

                if (dimension.contains("$")) {
                    this.mc.gameSettings.thirdPersonView = 0
                }

                (AsmodeusCore.packetPipeline)!!
                    .sendToServer(
                        ACPacketSimple(
                            ACPacketSimple.ACEnumSimplePacket.S_TELEPORT_ENTITY,
                            GCCoreUtil.getDimensionID(this.mc.world),
                            dimensionID,
                            this.fuelSet
                        )
                    )
                this.mc.displayGuiScreen(GuiCustomTeleporting(dimensionID))
            } catch (var6: Exception) {
                var6.printStackTrace()
            }
        }
    }

    protected open fun drawTransitBar(length: Int) {
        val menuTopLeft: Int = BORDER_SIZE + BORDER_EDGE_SIZE
        this.mc.renderEngine.bindTexture(guiMain2)
        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
        this.drawTexturedModalRect(this.width / 2 - 113, menuTopLeft + 15, 225, 67, 0, 0, 225, 67, false, false)
        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
        this.drawTexturedModalRect(this.width / 2 - 50, menuTopLeft + 57, 0 + length, 10, 269, 0, 138, 10, false, false)
        var str: String
        if (this.mc.player.world.provider !is WorldProviderSurface && this.mc.player.world.provider is IGalacticraftWorldProvider) {
            val body: CelestialBody = ((this.mc.player.world.provider as IGalacticraftWorldProvider).getCelestialBody())!!
            this.drawBodyOnGUI(body, this.width / 2 - 76, menuTopLeft + 54, 16, 16)
            str = GCCoreUtil.translate(body.getTranslationKey())
        } else {
            this.drawBodyOnGUI(GalacticraftCore.planetOverworld, this.width / 2 - 76, menuTopLeft + 54, 16, 16)
            str = GCCoreUtil.translate((GalacticraftCore.planetOverworld)!!.getTranslationKey())
        }

        this.fontRenderer
            .drawString(
                str,
                this.width / 2 - 105,
                BORDER_SIZE + BORDER_EDGE_SIZE + 35,
                ColorUtil.to32BitColor(255, 255, 255, 255)
            )
        this.drawBodyOnGUI(this.selectedBody, this.width / 2 + 59, menuTopLeft + 54, 16, 16)
        str = GCCoreUtil.translate((this.selectedBody)!!.getTranslationKey())
        this.fontRenderer
            .drawString(
                str,
                this.width / 2 + 50,
                BORDER_SIZE + BORDER_EDGE_SIZE + 35,
                ColorUtil.to32BitColor(255, 255, 255, 255)
            )
        str = "Boost: x" + this.coef
        this.fontRenderer
            .drawString(
                str,
                this.width / 2 - (this.fontRenderer.getStringWidth(str) - 25),
                BORDER_SIZE + BORDER_EDGE_SIZE + 48,
                ColorUtil.to32BitColor(255, 0, 255, 255)
            )
        val time: Double = this.traveltime - this.timer
        str = (time / 100.0).toInt().toString() + "h " + (time % 59.0).toInt() + "m"
        this.fontRenderer
            .drawString(
                str,
                this.width / 2 - (this.fontRenderer.getStringWidth(str) - 25),
                BORDER_SIZE + BORDER_EDGE_SIZE + 59,
                ColorUtil.to32BitColor(255, 0, 255, 255)
            )
        if (this.timer >= this.traveltime) {
            this.teleportToSelectedBody()
            this.enableTraveltime = false
        }
    }

    protected open fun getScaledTravelTime(barLength: Int): Int {
        val relative: Double = this.timer / this.traveltime
        return (relative * barLength.toDouble()).toInt()
    }

    protected open fun drawBodyOnGUI(body: CelestialBody?, x: Int, y: Int, w: Int, h: Int) {
        if (body != null) {
            this.mc.renderEngine.bindTexture(body.getBodyIcon())
            this.drawFullSizedTexturedRect(x, y, w, h)
        }
    }

    open fun drawFullSizedTexturedRect(x: Int, y: Int, width: Int, height: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(x.toDouble(), (y + height).toDouble(), this.zLevel.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos((x + width).toDouble(), (y + height).toDouble(), this.zLevel.toDouble()).tex(1.0, 0.0)
            .endVertex()
        worldRenderer.pos((x + width).toDouble(), y.toDouble(), this.zLevel.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(x.toDouble(), y.toDouble(), this.zLevel.toDouble()).tex(0.0, 1.0).endVertex()
        tessellator.draw()
    }

    override fun canCreateSpaceStation(atBody: CelestialBody?): Boolean {
        if (!this.mapMode && !ConfigManagerCore.disableSpaceStationCreation && this.canCreateStations) {
            if (if (this.enableNewTierSystem) this.currenttier >= this.tierneed else this.possibleBodies == null || (this.possibleBodies)!!.contains(
                    atBody
                )
            ) {
                var foundRecipe: Boolean = false

                for (type: SpaceStationType in GalacticraftRegistry.getSpaceStationData()) {
                    if (type.getWorldToOrbitID() == (atBody)!!.getDimensionID()) {
                        foundRecipe = true
                    }
                }

                if (!foundRecipe) {
                    return false
                } else if (!ClientProxyCore.clientSpaceStationID.containsKey((atBody)!!.getDimensionID())) {
                    return true
                } else {
                    val resultID: Int = ClientProxyCore.clientSpaceStationID.get((atBody)!!.getDimensionID()) as Int
                    return resultID == 0 || resultID == -1
                }
            } else {
                return false
            }
        } else {
            return false
        }
    }

    protected open fun getChildren(`object`: Any?, start: Int, size: Int): MutableList<CelestialBody> {
        val bodyList: MutableList<CelestialBody> = Lists.newArrayList<CelestialBody>()
        SpaceData.refreshGalaxies()
        if (`object` is Planet) {
            val moons: MutableList<Moon> = ((GalaxyRegistry.getMoonsForPlanet(`object`) as MutableList<Moon>?))!!
            if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
                for (moon: Moon in moons) {
                    if (UtilsGC.isReasearched(this.mc.player, moon)) {
                        bodyList.add(moon)
                    }
                }
            } else {
                bodyList.addAll(moons)
            }

            for (body: CelestialBody? in (SpaceData.getMoonsForPlanet(`object`))!!) {
                if (body is ICelestialRegistry && body is IChildBody) {
                    bodyList.add(body)
                }
            }
        } else if (`object` is SolarSystem) {
            val planets: MutableList<Planet> = ((GalaxyRegistry.getPlanetsForSolarSystem(`object`) as MutableList<Planet>?))!!
            if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
                for (planet: Planet in planets) {
                    if (UtilsGC.isReasearched(this.mc.player, planet)) {
                        bodyList.add(planet)
                    }
                }
            } else {
                bodyList.addAll(planets)
            }

            for (bodyx: CelestialBody? in (SpaceData.getBodiesForSolarSystem(`object`))!!) {
                if (bodyx is ICelestialRegistry && bodyx !is IChildBody) {
                    bodyList.add(bodyx)
                }
            }
        }

        Collections.sort<CelestialBody?>(bodyList)
        if (ClientProxy.smallInfoOnMap) {
            val doneList: MutableList<CelestialBody> = Lists.newArrayList<CelestialBody>()
            var startPos: Int = start
            var getSize: Int = size
            if (start >= bodyList.size) {
                startPos = 0
            }

            if (size >= bodyList.size) {
                getSize = bodyList.size
            }

            for (i: Int in 0..<getSize) {
                if (bodyList.size >= i + startPos) {
                    doneList.add(i, bodyList.get(i + startPos))
                }
            }

            return doneList
        } else {
            return bodyList
        }
    }

    protected enum class EnumView {
        PREVIEW,
        PROFILE,
        GS
    }

    companion object {
        @JvmField
        protected val LIGHTBLUE: Int = ColorUtil.to32BitColor(255, 0, 255, 255)

        @JvmField
        protected val YELLOW: Int = ColorUtil.to32BitColor(255, 255, 255, 0)

        @JvmField
        var guiMain2: ResourceLocation = ResourceLocation("asmodeuscore", "textures/gui/celestialselection2.png")

        @JvmField
        var guiImg: ResourceLocation = ResourceLocation("asmodeuscore", "textures/gui/galaxymap_1.png")

        @JvmField
        var vortexTexture: ResourceLocation =
            ResourceLocation("asmodeuscore", "textures/gui/celestialbodies/vortex.png")
    }
}
