package asmodeuscore.core.astronomy.gui.screen

import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.CelestialRingRenderEvent.Post
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre

import com.google.common.collect.Lists
import asmodeuscore.api.space.IExBody
import org.lwjgl.opengl.GL11
import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.astronomy.gui.screen.AC_GuiCelestialSelection
import asmodeuscore.api.space.ICelestialRegistry
import asmodeuscore.core.utils.ACCompatibilityManager
import asmodeuscore.core.utils.UtilsGC
import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.core.astronomy.SpaceData
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.Satellite
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.galaxies.Star
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

open class AC_GuiCelestialSelection constructor(
    mapMode: Boolean,
    possibleBodies: MutableList<CelestialBody?>?,
    canCreateStations: Boolean
) : GuiCelestialSelection(mapMode, possibleBodies, canCreateStations) {
    @JvmField
    protected var previewViewState: EnumView = EnumView.PREVIEW

    @JvmField
    protected var rand: Random = Random()
    private val galaxy: String? =
        ((GalacticraftCore.planetOverworld)!!.getParentSolarSystem())!!.getUnlocalizedParentGalaxyName()
    private val galaxylist: MutableList<String?> = ArrayList<String?>()
    private val starlist: MutableList<SolarSystem?> = ArrayList<SolarSystem?>()
    private var xImgOffset = 0.0
    private var yImgOffset = 0.0
    private val nebula_color =
        Vector3(this.rand.nextFloat().toDouble(), this.rand.nextFloat().toDouble(), this.rand.nextFloat().toDouble())
    private val nebula_img = this.rand.nextInt(2)
    private var enableGrid = true

    override fun initGui() {
        super.initGui()
        BORDER_SIZE = this.width / 65
        BORDER_EDGE_SIZE = BORDER_SIZE / 4
        this.refreshBodies()
    }

    override fun updateScreen() {
        super.updateScreen()
        if (AsmodeusConfig.enableDynamicImgOnGalaxyMap) {
            this.xImgOffset = this.xImgOffset + this.translation.x.toDouble()
            this.yImgOffset = this.yImgOffset + this.translation.y.toDouble()
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

    override fun drawScreen(mousePosX: Int, mousePosY: Int, partialTicks: Float) {
        super.drawScreen(mousePosX, mousePosY, partialTicks)
    }

    override fun setBlackBackground() {
        super.setBlackBackground()
        if (AsmodeusConfig.enableImgOnGalaxyMap) {
            GL11.glPushMatrix()
            this.enableGrid = false
            this.setImgBackground(guiImg)
            GL11.glPopMatrix()
        }
    }

    override fun drawGrid(gridSize: kotlin.Float, gridScale: kotlin.Float) {
        if (this.enableGrid) {
            super.drawGrid(gridSize, gridScale)
        }
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
            val alpha: Float = this.getAlpha(body)
            if (alpha > 0.0f) {
                when (count % 2) {
                    0 -> GL11.glColor4f(0.0f, 0.2857143f, 0.64285713f, alpha / 2.4f)
                    1 -> GL11.glColor4f(0.0f, 0.4f, 0.9f, alpha / 2.4f)
                }

                if (body == this.selectedBody) {
                    GL11.glColor4f(0.0f, 0.4f, 0.9f, 1.0f)
                }

                val preEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre =
                    CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre(body, systemOffset)
                MinecraftForge.EVENT_BUS.post(preEvent)
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
                    count++
                }

                val postEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Post =
                    CelestialBodyRenderEvent.CelestialRingRenderEvent.Post(body)
                MinecraftForge.EVENT_BUS.post(postEvent)
            }
        }

        GL11.glLineWidth(1.0f)
        if (this.selectedBody is Star) {
            this.drawAgeCircle()
        }
    }

    override fun drawButtons(mousePosX: Int, mousePosY: Int) {
        super.drawButtons(mousePosX, mousePosY)
        val LHS: Int = BORDER_SIZE + BORDER_EDGE_SIZE
        val RHS: Int = this.width - LHS
        val BOT: Int = this.height - LHS
        if (this.selectedBody != null) {
            this.mc.renderEngine.bindTexture(guiMain2)
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f)
            var sliderPos: Int = this.zoomTooltipPos
            if (this.zoomTooltipPos != 125) {
                sliderPos = Math.min(this.ticksSinceSelectionF * 8.0f, 125.0f) as Int
                this.zoomTooltipPos = sliderPos
            }

            val menuTopLeft: Int = LHS + 100
            val posX: Int = RHS - sliderPos
            this.drawTexturedModalRect(posX - 7, menuTopLeft + 13, 133, 120, 0, 79, 264, 182, true, false)
            this.drawTexturedModalRect(posX - 7, menuTopLeft + 153, 133, 17, 0, 472, 266, 40, true, false)
        }
    }

    private fun drawAgeCircle() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glLineWidth(2.0f)
        val theta: Float = 0.06981317f
        val cos: Float = cos(0.06981317).toFloat()
        val sin: Float = sin(0.06981317).toFloat()
        val body: CelestialBody? = this.selectedBody
        Vector3f(0.0f, 0.0f, 0.0f)
        val systemOffset: Vector3f = this.getCelestialBodyPosition(body as Star?)
        var x: Float = 1000.0f
        var y: Float = 0.0f
        GL11.glTranslatef(systemOffset.x, systemOffset.y, systemOffset.z)

        for (count_rings: Int in 1..4) {
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

    private fun setImgBackground(galaxy: ResourceLocation) {
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
            ResourceLocation.fromNamespaceAndPath("asmodeuscore", "textures/gui/galaxymap_nebula_" + this.nebula_img + ".png")
        this.mc.renderEngine.bindTexture(guiImg_2)
        GL11.glTranslated(this.xImgOffset * 1.5, this.yImgOffset * 1.5, 0.0)
        GL11.glColor4f(0.5f, 0.4f, this.nebula_color.floatZ(), 0.5f)
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
                sin((this.ticksTotalF / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()) as Double).toFloat() * distanceFromCenter
            val y: Float =
                cos((this.ticksTotalF / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()) as Double).toFloat() * distanceFromCenter
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
            } else if (cBody is ICelestialRegistry) {
                val parentVec: Vector3f =
                    this.getCelestialBodyPosition(((cBody as ICelestialRegistry).getParentSolarSystem())!!.getMainStar())
                return Vector3f.add(cBodyPos, parentVec, null)
            } else {
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

    private fun refreshBodies() {
        val player: EntityPlayerSP? = Minecraft.getInstance().player
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
                            this.starlist.add(solarSystem)
                        }

                        this.bodiesToRender.add(((solarSystem)!!.getMainStar())!!)
                    }
                } else {
                    if ((solarSystem)!!.getUnlocalizedParentGalaxyName() == this.galaxy) {
                        this.starlist.add(solarSystem)
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

    override fun getChildren(`object`: Any?): MutableList<CelestialBody> {
        return (this.getChildren(`object`, 0, 0, false) as MutableList<CelestialBody>)
    }

    protected open fun getChildren(`object`: Any?, start: Int, size: Int): MutableList<CelestialBody?> {
        return this.getChildren(`object`, start, size, false)
    }

    protected open fun getChildren(`object`: Any?, start: Int, size: Int, check: Boolean): MutableList<CelestialBody?> {
        val bodyList: MutableList<CelestialBody?> = Lists.newArrayList<CelestialBody?>()
        SpaceData.refreshGalaxies()
        if (`object` is Planet) {
            val moons: MutableList<Moon?> = (GalaxyRegistry.getMoonsForPlanet(`object`))!!
            if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
                for (moon: Moon? in moons) {
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
            val planets: MutableList<Planet?> = (GalaxyRegistry.getPlanetsForSolarSystem(`object`))!!
            if (ACCompatibilityManager.isPlanetProgressionLoaded()) {
                for (planet: Planet? in planets) {
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

        (((bodyList)!!)!!)!!.sortWith(Comparator { left, right -> left!!.compareTo(right!!) })
        if (!check) {
            return bodyList
        } else {
            val doneList: MutableList<CelestialBody?> = Lists.newArrayList<CelestialBody?>()
            var startPos: Int = start
            var getSize: Int = size
            if (start >= bodyList.size) {
                startPos = 0
            }

            if (size >= bodyList.size) {
                getSize = bodyList.size
            }

            for (i: Int in 0..<getSize) {
                doneList.add(i, bodyList.get(i + startPos))
            }

            return doneList
        }
    }

    companion object {
        private val guiMain2 = ResourceLocation.fromNamespaceAndPath("asmodeuscore", "textures/gui/celestialselection2.png")
        private val guiImg = ResourceLocation.fromNamespaceAndPath("asmodeuscore", "textures/gui/galaxymap_1.png")
    }
}
