package micdoodle8.mods.galacticraft.core.client.gui.screen

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.ibm.icu.text.ArabicShaping
import com.ibm.icu.text.ArabicShapingException
import com.ibm.icu.text.Bidi
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent
import micdoodle8.mods.galacticraft.api.galaxies.*
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe
import micdoodle8.mods.galacticraft.api.world.SpaceStationType
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient
import micdoodle8.mods.galacticraft.core.util.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.settings.GameSettings
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatAllowedCharacters
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldProvider
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.BufferUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import java.io.IOException
import java.nio.FloatBuffer
import java.util.*
import kotlin.Any
import kotlin.Boolean
import kotlin.Char
import kotlin.Exception
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Throws
import kotlin.arrayOf
import kotlin.code
import kotlin.collections.HashMap
import kotlin.collections.MutableCollection
import kotlin.collections.MutableIterator
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.indices
import kotlin.div
import kotlin.math.*
import kotlin.run
import kotlin.toString

open class GuiCelestialSelection constructor(
    @JvmField protected val mapMode: Boolean,
    @JvmField var possibleBodies: MutableList<CelestialBody?>?,
    canCreateStations: Boolean
) : GuiScreen() {
    protected enum class EnumView {
        PREVIEW,
        PROFILE
    }

    protected enum class EnumSelection {
        UNSELECTED,
        SELECTED,
        ZOOMED
    }

    @JvmField
    protected var zoom: Float = 0.0f

    @JvmField
    protected var planetZoom: Float = 0.0f

    @JvmField
    protected var doneZooming: Boolean = false

    @JvmField
    protected var preSelectZoom: Float = 0.0f

    @JvmField
    protected var preSelectPosition: Vector2f = Vector2f()

    @JvmField
    protected var ticksSinceSelection: Int = 0

    @JvmField
    protected var ticksSinceUnselection: Int = -1

    @JvmField
    protected var ticksSinceMenuOpen: Int = 0

    @JvmField
    protected var ticksTotal: Int = 0

    @JvmField protected var ticksTotalF: Float = 0f
    @JvmField protected var ticksSinceMenuOpenF: Float = 0f
    @JvmField protected var ticksSinceSelectionF: Float = 0f
    @JvmField protected var ticksSinceUnselectionF: Float = -1f


    @JvmField
    protected var animateGrandchildren: Int = 0

    @JvmField
    protected var position: Vector2f = Vector2f(0f, 0f)

    @JvmField
    protected var planetPosMap: MutableMap<CelestialBody?, Vector3f?> = Maps.newHashMap<CelestialBody?, Vector3f?>()

    @JvmField
    protected var celestialBodyTicks: MutableMap<CelestialBody?,Int> = Maps.newHashMap<CelestialBody?,Int>()

    @JvmField
    protected var selectedBody: CelestialBody? = null

    @JvmField
    protected var lastSelectedBody: CelestialBody? = null

    @JvmField
    protected var canCreateOffset: Int = 24

    @JvmField
    protected var viewState: EnumView = EnumView.PREVIEW

    @JvmField
    protected var selectionState: EnumSelection = EnumSelection.UNSELECTED

    @JvmField
    protected var zoomTooltipPos: Int = 0

    @JvmField
    protected var selectedParent: Any? = GalacticraftCore.solarSystemSol

    // Each home planet has a map of owner's names linked with their station data:
    @JvmField
    var spaceStationMap: MutableMap<Int, MutableMap<String?, StationDataGUI?>?>? =
        Maps.newHashMap<Int, MutableMap<String?, StationDataGUI?>?>()

    @JvmField
    protected var selectedStationOwner: String = ""

    @JvmField
    protected var spaceStationListOffset: Int = 0

    @JvmField
    protected var renamingSpaceStation: Boolean = false

    @JvmField
    protected var renamingString: String? = ""

    @JvmField
    protected var translation: Vector2f = Vector2f(0.0f, 0.0f)

    @JvmField
    protected var mouseDragging: Boolean = false

    @JvmField
    protected var lastMovePosX: Int = -1

    @JvmField
    protected var lastMovePosY: Int = -1

    @JvmField
    protected var errorLogged: Boolean = false

    @JvmField
    var canCreateStations: Boolean = false

    @JvmField
    protected var bodiesToRender: MutableList<CelestialBody> = Lists.newArrayList<CelestialBody>()

    init {
        this.canCreateStations = canCreateStations
    }

    override fun initGui() {
        for (planet: Planet? in (GalaxyRegistry.getRegisteredPlanets())!!.values) {
            this.celestialBodyTicks.put(planet, 0)
        }

        for (moon: Moon? in (GalaxyRegistry.getRegisteredMoons())!!.values) {
            this.celestialBodyTicks.put(moon, 0)
        }

        for (satellite: Satellite? in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
            this.celestialBodyTicks.put(satellite, 0)
        }

        BORDER_SIZE = this.width / 65
        BORDER_EDGE_SIZE = BORDER_SIZE / 4

        for (solarSystem in (GalaxyRegistry.getRegisteredSolarSystems())!!.values) {
            bodiesToRender.add(((solarSystem)!!.getMainStar())!!)
        }
        bodiesToRender.addAll((GalaxyRegistry.getRegisteredPlanets())!!.values)
        bodiesToRender.addAll((GalaxyRegistry.getRegisteredMoons())!!.values)
        bodiesToRender.addAll((GalaxyRegistry.getRegisteredSatellites())!!.values)
    }

    protected open fun getGrandparentName(): String {
        if (this.selectedParent is Planet) {
            val parentSolarSystem: SolarSystem? = (this.selectedParent as Planet).getParentSolarSystem()

            if (parentSolarSystem != null) {
                return parentSolarSystem.getLocalizedName()
            }
        } else if (this.selectedParent is IChildBody) {
            val parentPlanet: Planet? = (this.selectedParent as IChildBody).getParentPlanet()

            if (parentPlanet != null) {
                val parentSolarSystem: SolarSystem? = parentPlanet.getParentSolarSystem()

                if (parentSolarSystem != null) {
                    return parentSolarSystem.getLocalizedName()
                }
            }
        } else if (this.selectedParent is Star) {
            val parentSolarSystem: SolarSystem? = (this.selectedParent as Star).getParentSolarSystem()

            if (parentSolarSystem != null) {
                return parentSolarSystem.getLocalizedParentGalaxyName()
            }
        } else if (this.selectedParent is SolarSystem) {
            return (this.selectedParent as SolarSystem).getLocalizedParentGalaxyName()
        }

        return "Null"
    }

    protected open fun getSatelliteParentID(satellite: Satellite?): Int {
        return (satellite!!.getParentPlanet())!!.getDimensionID()
    }

    protected open fun getParentName(): String {
        if (this.selectedBody is Planet) {
            val parentSolarSystem: SolarSystem? = (this.selectedBody as Planet).getParentSolarSystem()

            if (parentSolarSystem != null) {
                return parentSolarSystem.getLocalizedName()
            }
        } else if (this.selectedBody is IChildBody) {
            val parentPlanet: Planet? = (this.selectedBody as IChildBody).getParentPlanet()

            if (parentPlanet != null) {
                return parentPlanet.getLocalizedName()
            }
        } else if (this.selectedBody is Star) {
            val parentSolarSystem: SolarSystem? = (this.selectedBody as Star).getParentSolarSystem()

            if (parentSolarSystem != null) {
                return parentSolarSystem.getLocalizedName()
            }
        } else if (this.selectedParent != null) {
            if (this.selectedParent is SolarSystem) {
                return (this.selectedParent as SolarSystem).getLocalizedName()
            }
        }

        return "Null"
    }

    protected open fun getScale(celestialBody: CelestialBody): Float {
        return 3.0f * celestialBody.getRelativeDistanceFromCenter().unScaledDistance * (if (celestialBody is Planet) 25.0f else 1.0f / 5.0f)
    }

    protected open fun getSiblings(celestialBody: CelestialBody?): MutableList<CelestialBody?> {
        val bodyList: MutableList<CelestialBody?> = Lists.newArrayList<CelestialBody?>()

        if (celestialBody is Planet) {
            val solarSystem: SolarSystem? = celestialBody.getParentSolarSystem()

            for (planet: Planet in (GalaxyRegistry.getRegisteredPlanets())!!.values) {
                val solarSystem1: SolarSystem? = planet.getParentSolarSystem()

                if (solarSystem1 != null && solarSystem1 == solarSystem) {
                    bodyList.add(planet)
                }
            }
        } else if (celestialBody is IChildBody) {
            val planet: Planet? = (celestialBody as IChildBody).getParentPlanet()

            for (moon: Moon in (GalaxyRegistry.getRegisteredMoons())!!.values) {
                val planet1: Planet? = moon.getParentPlanet()

                if (planet1 != null && planet1 == planet) {
                    bodyList.add(moon)
                }
            }

            for (sat: Satellite in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
                val planet1: Planet? = sat.getParentPlanet()

                if (planet1 != null && planet1 == planet) {
                    bodyList.add(sat)
                }
            }
        }

        bodyList!!.sortWith(Comparator { left, right -> left!!.compareTo(right!!) })

        return bodyList
    }

    protected open fun getChildren(`object`: Any?): MutableList<CelestialBody> {
        val bodyList: MutableList<CelestialBody> = Lists.newArrayList<CelestialBody>()

        if (`object` is Planet) {
            val moons: MutableList<Moon?>? = GalaxyRegistry.getMoonsForPlanet(`object`)
            bodyList.addAll(((moons)!! as Collection<CelestialBody>))
        } else if (`object` is SolarSystem) {
            val planets: MutableList<Planet?>? = GalaxyRegistry.getPlanetsForSolarSystem(`object`)
            bodyList.addAll(((planets)!! as Collection<CelestialBody>))
        }

        Collections.sort<CelestialBody?>(bodyList)

        return bodyList
    }

    protected open fun lerp(v0: Float, v1: Float, t: Float): Float {
        return v0 + t * (v1 - v0)
    }

    protected open fun lerpVec2(v0: Vector2f, v1: Vector2f, t: Float): Vector2f {
        return Vector2f(v0.x + t * (v1.x - v0.x), v0.y + t * (v1.y - v0.y))
    }

    protected open fun getZoomAdvanced(): Float {
        if (this.ticksTotal < 30) {
            val scale: Float = max(0.0f, min(this.ticksTotal / 30.0f, 1.0f))
            return this.lerp(-0.75f, 0.0f, scale.toDouble().pow(0.5).toFloat())
        }

        if (this.selectedBody == null || this.selectionState != EnumSelection.ZOOMED) {
            if (!this.doneZooming) {
                val unselectScale: Float =
                    this.lerp(this.zoom, this.preSelectZoom, max(0.0f, min(this.ticksSinceUnselection / 100.0f, 1.0f)))

                if (unselectScale <= this.preSelectZoom + 0.05f) {
                    this.zoom = this.preSelectZoom
                    //                    this.preSelectZoom = 0.0F;
                    this.ticksSinceUnselection = -1
                    this.doneZooming = true
                }

                return unselectScale
            }

            return this.zoom
        }

        if (!this.doneZooming) {
            val f: Float = this.lerp(this.zoom, 12f, max(0.0f, min((this.ticksSinceSelection - 20) / 40.0f, 1.0f)))

            if (f >= 11.95f) {
                this.doneZooming = true
            }

            return f
        }

        return 12 + this.planetZoom
    }

    protected open fun getTranslationAdvanced(partialTicks: Float): Vector2f {
        if (this.selectedBody == null) {
            if (this.ticksSinceUnselection > 0) {
                val f0: Float = max(0.0f, min((this.ticksSinceUnselection + partialTicks) / 100.0f, 1.0f))
                if (f0 >= 0.999999f) {
                    this.ticksSinceUnselection = 0
                }
                return this.lerpVec2(this.position, this.preSelectPosition, f0)
            }

            return Vector2f(this.position.x + translation.x, this.position.y + translation.y)
        }

        if (!this.isZoomed()) {
            if (this.selectedBody is IChildBody) {
                val posVec: Vector3f =
                    this.getCelestialBodyPosition((this.selectedBody as IChildBody).getParentPlanet())
                return Vector2f(posVec.x, posVec.y)
            }

            return Vector2f(this.position.x + translation.x, this.position.y + translation.y)
        }

        //        if (this.selectedBody instanceof Planet && this.lastSelectedBody instanceof IChildBody && ((IChildBody) this.lastSelectedBody).getParentPlanet() == this.selectedBody)
//        {
//            Vector3f posVec = this.getCelestialBodyPosition(this.selectedBody);
//            return new Vector2f(posVec.x, posVec.y);
//        }
        val pos: Vector2f = this.position

        if (this.lastSelectedBody != null) {
            val pos3: Vector3f = this.getCelestialBodyPosition(this.lastSelectedBody)
            pos.x = pos3.x
            pos.y = pos3.y
        }

        val posVec: Vector3f = this.getCelestialBodyPosition(this.selectedBody)
        return this.lerpVec2(
            pos,
            Vector2f(posVec.x, posVec.y),
            max(0.0f, min((this.ticksSinceSelection + partialTicks - 18) / 7.5f, 1.0f))
        )
    }

    @Throws(IOException::class)
    override fun keyTyped(keyChar: Char, keyID: Int) {
        // Override and do nothing, so it isn't possible to exit the GUI
        if (this.mapMode) {
            super.keyTyped(keyChar, keyID)
        }

        if (keyID == 1) {
            if (this.selectedBody != null) {
                this.unselectCelestialBody()
            }
        }

        if (this.renamingSpaceStation) {
            if (keyID == Keyboard.KEY_BACK) {
                if (this.renamingString != null && this.renamingString!!.length > 0) {
                    val toBeParsed: String = this.renamingString!!.substring(0, this.renamingString!!.length - 1)

                    if (this.isValid(toBeParsed)) {
                        this.renamingString = toBeParsed
                        //                        this.timeBackspacePressed = System.currentTimeMillis();
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
                    this.renamingString = this.renamingString!!.substring(
                        0,
                        min(this.renamingString.toString().length, MAX_SPACE_STATION_NAME_LENGTH)
                    )
                }
            } else if (this.isValid(this.renamingString + keyChar)) {
                this.renamingString = this.renamingString + keyChar
                this.renamingString =
                    this.renamingString!!.substring(0, min(this.renamingString!!.length, MAX_SPACE_STATION_NAME_LENGTH))
            }

            return
        }

        // Keyboard shortcut - teleport to dimension by pressing 'Enter'
        if (keyID == Keyboard.KEY_RETURN) {
            this.teleportToSelectedBody()
        }
    }

    open fun isValid(string: String): Boolean {
        return string.length > 0 && ChatAllowedCharacters.isAllowedCharacter(string.get(string.length - 1))
    }

    protected open fun canCreateSpaceStation(atBody: CelestialBody?): Boolean {
        if (this.mapMode || ConfigManagerCore.disableSpaceStationCreation || !this.canCreateStations) {
            return false
        }

        if (!atBody!!.getReachable() || (this.possibleBodies != null && !this.possibleBodies!!.contains(atBody))) {
            // If parent body is unreachable, the satellite is also unreachable
            return false
        }

        var foundRecipe: Boolean = false
        for (type: SpaceStationType in GalacticraftRegistry.getSpaceStationData()) {
            if (type.getWorldToOrbitID() == atBody.getDimensionID()) {
                foundRecipe = true
            }
        }

        if (!foundRecipe) {
            return false
        }

        if (!ClientProxyCore.clientSpaceStationID.containsKey(atBody.getDimensionID())) {
            return true
        }

        val resultID: Int = ClientProxyCore.clientSpaceStationID.get(atBody.getDimensionID())!!

        return !(resultID != 0 && resultID != -1)
    }

    protected open fun unselectCelestialBody() {
        this.selectionState = EnumSelection.UNSELECTED
        this.ticksSinceUnselection = 0
        this.lastSelectedBody = this.selectedBody
        this.selectedBody = null
        this.doneZooming = false
        this.selectedStationOwner = ""
        this.animateGrandchildren = 0
    }

    override fun updateScreen() {
        this.ticksSinceMenuOpen++
        this.ticksSinceMenuOpenF = this.ticksSinceMenuOpen.toFloat()
        this.ticksTotal++
        this.ticksTotalF = this.ticksTotal.toFloat()

        if (this.ticksSinceMenuOpen < 20) {
            Mouse.setGrabbed(false)
        }

        for (e: CelestialBody? in this.celestialBodyTicks.keys) {
//			if (!(e instanceof Planet && e == this.selectedBody) && !(e instanceof Planet && this.selectedBody instanceof IChildBody && GalaxyRegistry.getIChildBodysForPlanet((Planet) e).contains(this.selectedBody)))
            run {
                var i: Int? = this.celestialBodyTicks.get(e)
                if (i != null) {
                    i++
                }
                this.celestialBodyTicks.put(e, (i)!!)
            }
        }

        if (this.selectedBody != null) {
            this.ticksSinceSelection++
        this.ticksSinceSelectionF = this.ticksSinceSelection.toFloat()
        }

        if (this.selectedBody == null && this.ticksSinceUnselection >= 0) {
            this.ticksSinceUnselection++
        this.ticksSinceUnselectionF = this.ticksSinceUnselection.toFloat()
        }

        if (!this.renamingSpaceStation && (this.selectedBody == null || !this.isZoomed())) {
            if (GameSettings.isKeyDown(KeyHandlerClient.leftKey)) {
                translation.x += -2.0f
                translation.y += -2.0f
            }

            if (GameSettings.isKeyDown(KeyHandlerClient.rightKey)) {
                translation.x += 2.0f
                translation.y += 2.0f
            }

            if (GameSettings.isKeyDown(KeyHandlerClient.upKey)) {
                translation.x += 2.0f
                translation.y += -2.0f
            }

            if (GameSettings.isKeyDown(KeyHandlerClient.downKey)) {
                translation.x += -2.0f
                translation.y += 2.0f
            }
        }
    }

    protected open fun teleportToSelectedBody() {
        if (this.selectedBody != null) {
            if (this.selectedBody!!.getReachable() && this.possibleBodies != null && this.possibleBodies!!.contains(this.selectedBody)) {
                try {
                    val dimension: String
                    val dimensionID: Int

                    if (this.selectedBody is Satellite) {
                        if (this.spaceStationMap == null) {
                            GCLog.severe("Please report as a BUG: spaceStationIDs was null.")
                            return
                        }
                        val selectedSatellite: Satellite = this.selectedBody as Satellite
                        val mapping: Int? = this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!
                            .get(this.selectedStationOwner)!!.getStationDimensionID()
                        //No need to check lowercase as selectedStationOwner is taken from keys.
                        if (mapping == null) {
                            GCLog.severe("Problem matching player name in space station check: " + this.selectedStationOwner)
                            return
                        }
                        dimensionID = mapping
                        val spacestation: WorldProvider? = WorldUtil.getProviderForDimensionClient(dimensionID)
                        if (spacestation != null) {
                            dimension = "Space Station " + mapping
                        } else {
                            GCLog.severe("Failed to find a spacestation with dimension " + dimensionID)
                            return
                        }
                    } else {
                        dimensionID = this.selectedBody!!.getDimensionID()
                        dimension = (WorldUtil.getDimensionName((WorldUtil.getProviderForDimensionClient(dimensionID))!!))!!
                    }

                    if (dimension.contains("$")) {
                        this.mc.gameSettings.thirdPersonView = 0
                    }
                    (GalacticraftCore.packetPipeline)!!.sendToServer(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.S_TELEPORT_ENTITY,
                            GCCoreUtil.getDimensionID(mc.world),
                            arrayOf<Any>(dimension)
                        )
                    )
                    mc.displayGuiScreen(GuiTeleporting(dimensionID))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun handleInput() {
        this.translation.x = 0.0f
        this.translation.y = 0.0f
        super.handleInput()
    }

    override fun mouseClickMove(x: Int, y: Int, lastButtonClicked: Int, timeSinceMouseClick: Long) {
        super.mouseClickMove(x, y, lastButtonClicked, timeSinceMouseClick)

        if (mouseDragging && lastMovePosX != -1 && lastButtonClicked == 0) {
            val deltaX: Int = x - lastMovePosX
            val deltaY: Int = y - lastMovePosY

            translation.x += (deltaX - deltaY) * -0.4f * (if (ConfigManagerCore.invertMapMouseScroll) -1.0f else 1.0f) * ConfigManagerCore.mapMouseScrollSensitivity
            translation.y += (deltaY + deltaX) * -0.4f * (if (ConfigManagerCore.invertMapMouseScroll) -1.0f else 1.0f) * ConfigManagerCore.mapMouseScrollSensitivity
        }

        lastMovePosX = x
        lastMovePosY = y
    }

    override fun mouseReleased(x: Int, y: Int, button: Int) {
        super.mouseReleased(x, y, button)

        mouseDragging = false
        lastMovePosX = -1
        lastMovePosY = -1
    }

    @Throws(IOException::class)
    override fun mouseClicked(x: Int, y: Int, button: Int) {
        super.mouseClicked(x, y, button)
        var clickHandled: Boolean = false

        val LHS: Int = BORDER_SIZE + BORDER_EDGE_SIZE
        val RHS: Int = width - LHS
        val TOP: Int = LHS

        if (this.selectedBody != null && x > LHS && x < LHS + 88 && y > TOP && y < TOP + 13) {
            this.unselectCelestialBody()
            return
        }

        if (!this.mapMode) {
            if (x >= RHS - 95 && x < RHS && y > TOP + 181 + canCreateOffset && y < TOP + 182 + 12 + canCreateOffset) {
                if (this.selectedBody != null) {
                    val recipe: SpaceStationRecipe? =
                        WorldUtil.getSpaceStationRecipe(this.selectedBody!!.getDimensionID())
                    if (recipe != null && this.canCreateSpaceStation(this.selectedBody)) {
                        if (recipe.matches(this.mc.player, false) || this.mc.player.capabilities.isCreativeMode) {
                            (GalacticraftCore.packetPipeline)!!.sendToServer(
                                PacketSimple(
                                    PacketSimple.EnumSimplePacket.S_BIND_SPACE_STATION_ID,
                                    GCCoreUtil.getDimensionID(this.mc.world),
                                    arrayOf<Any>(this.selectedBody!!.getDimensionID())
                                )
                            )
                            //Zoom in on Overworld to show the new SpaceStation if not already zoomed
                            if (!this.isZoomed()) {
                                this.selectionState = EnumSelection.ZOOMED
                                this.preSelectZoom = this.zoom
                                this.preSelectPosition = this.position
                                this.ticksSinceSelection = 0
                                this.doneZooming = false
                            }
                            return
                        }

                        clickHandled = true
                    }
                }
            }
        }

        if (this.mapMode) {
            if (x > RHS - 88 && x < RHS && y > TOP && y < TOP + 13) {
                this.mc.displayGuiScreen(null)
                this.mc.setIngameFocus()
                clickHandled = true
            }
        }

        if (this.selectedBody != null && !this.mapMode) {
            if (x > RHS - 88 && x < RHS && y > TOP && y < TOP + 13) {
                if (this.selectedBody !is Satellite || this.selectedStationOwner != "") {
                    this.teleportToSelectedBody()
                }

                clickHandled = true
            }
        }

        // Need unscaled mouse coords
        val mouseX: Int = Mouse.getX()
        val mouseY: Int = Mouse.getY() * -1 + Minecraft.getMinecraft().displayHeight - 1

        if (this.selectedBody is Satellite) {
            if (this.renamingSpaceStation) {
                if (x >= width / 2 - 90 && x <= width / 2 + 90 && y >= this.height / 2 - 38 && y <= this.height / 2 + 38) {
                    // Apply
                    if (x >= width / 2 - 90 + 17 && x <= width / 2 - 90 + 17 + 72 && y >= this.height / 2 - 38 + 59 && y <= this.height / 2 - 38 + 59 + 12) {
                        val strName: String? = PlayerUtil.getName(this.mc.player)
                        //                        Integer spacestationID = this.spaceStationIDs.get(strName);
//                        if (spacestationID == null) spacestationID = this.spaceStationIDs.get(strName.toLowerCase());
                        val selectedSatellite: Satellite? = this.selectedBody as Satellite
                        var spacestationID: Int? =
                            this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!.get(strName)!!
                                .getStationDimensionID()
                        if (spacestationID == null) {
                            spacestationID = this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!
                                .get(strName!!.lowercase(java.util.Locale.getDefault()))!!.getStationDimensionID()
                        }
                        if (spacestationID != null) {
                            this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!.get(strName)!!
                                .setStationName(this.renamingString)
                            //	                    	this.spaceStationNames.put(strName, this.renamingString);
                            (GalacticraftCore.packetPipeline)!!.sendToServer(
                                PacketSimple(
                                    PacketSimple.EnumSimplePacket.S_RENAME_SPACE_STATION,
                                    GCCoreUtil.getDimensionID(this.mc.world),
                                    arrayOf<Any?>(this.renamingString, spacestationID)
                                )
                            )
                        }
                        this.renamingSpaceStation = false
                    }
                    // Cancel
                    if (x >= width / 2 && x <= width / 2 + 72 && y >= this.height / 2 - 38 + 59 && y <= this.height / 2 - 38 + 59 + 12) {
                        this.renamingSpaceStation = false
                    }
                    clickHandled = true
                }
            } else {
                this.drawTexturedModalRect(width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false)

                if (x >= width / 2 - 47 && x <= width / 2 - 47 + 94 && y >= TOP && y <= TOP + 11) {
                    if (this.selectedStationOwner.length != 0 && this.selectedStationOwner.equals(
                            PlayerUtil.getName(
                                this.mc.player
                            ), ignoreCase = true
                        )
                    ) {
                        this.renamingSpaceStation = true
                        this.renamingString = null
                        clickHandled = true
                    }
                }

                val selectedSatellite: Satellite? = this.selectedBody as Satellite
                val stationListSize: Int = this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!.size
                val max: Int = min((this.height / 2) / 14, stationListSize)

                var xPos: Int
                var yPos: Int

                // Up button
                xPos = RHS - 85
                yPos = TOP + 45

                if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                    if (this.spaceStationListOffset > 0) {
                        this.spaceStationListOffset--
                    }
                    clickHandled = true
                }

                // Down button
                xPos = RHS - 85
                yPos = TOP + 49 + max * 14

                if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                    if (max + spaceStationListOffset < stationListSize) {
                        this.spaceStationListOffset++
                    }
                    clickHandled = true
                }

                val it: MutableIterator<MutableMap.MutableEntry<String, StationDataGUI?>> =
                    (this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!.entries.iterator() as MutableIterator<MutableMap.MutableEntry<String, GuiCelestialSelection.StationDataGUI?>>)
                var i: Int = 0
                var j: Int = 0
                while (it.hasNext() && i < max) {
                    val e: MutableMap.MutableEntry<String, StationDataGUI?> = it.next()
                    if (j >= this.spaceStationListOffset) {
                        var xOffset: Int = 0

                        if (e.key.equals(this.selectedStationOwner, ignoreCase = true)) {
                            xOffset -= 5
                        }

                        xPos = RHS - 95 + xOffset
                        yPos = TOP + 50 + i * 14

                        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
                            this.selectedStationOwner = e.key
                            clickHandled = true
                        }
                        i++
                    }
                    j++
                }
            }
        }

        val xPos: Int = LHS + 2
        var yPos: Int = TOP + 10

        val planetZoomedMoon: Boolean = this.isZoomed() && this.selectedParent is Planet

        // Top yellow button e.g. Sol
        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12 && this.selectedParent is CelestialBody) {
            if (this.selectedBody == null) {
                this.preSelectZoom = this.zoom
                this.preSelectPosition = this.position
            }

            val selectionCountOld: EnumSelection? = this.selectionState

            if (this.isSelected()) {
                this.unselectCelestialBody()
            }

            if (selectionCountOld == EnumSelection.ZOOMED) {
                this.selectionState = EnumSelection.SELECTED
            }

            this.selectedBody = this.selectedParent as CelestialBody?
            this.ticksSinceSelection = 0
            this.selectionState = EnumSelection.entries[this.selectionState.ordinal + 1]
            if (this.isZoomed() && !planetZoomedMoon) {
                this.ticksSinceMenuOpen = 0
            }
            clickHandled = true
        }

        yPos += 22

        // First blue button - normally the Selected Body (but it's the parent planet if this is a moon)
        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
            if (planetZoomedMoon) {
                if (this.selectedBody == null) {
                    this.preSelectZoom = this.zoom
                    this.preSelectPosition = this.position
                }

                val selectionCountOld: EnumSelection? = this.selectionState
                if (this.isSelected()) {
                    this.unselectCelestialBody()
                }
                if (selectionCountOld == EnumSelection.ZOOMED) {
                    this.selectionState = EnumSelection.SELECTED
                }

                this.selectedBody = this.selectedParent as CelestialBody?
                this.ticksSinceSelection = 0
                this.selectionState = EnumSelection.entries[this.selectionState.ordinal + 1]
            }
            clickHandled = true
        }

        if (!clickHandled) {
            val children: MutableList<CelestialBody> =
                this.getChildren(if (this.isZoomed() && this.selectedParent !is Planet) this.selectedBody else this.selectedParent)

            yPos = TOP + 50
            for (child: CelestialBody in children) {
                clickHandled = this.testClicked(child, if (child == this.selectedBody) 5 else 0, yPos, x, y, false)
                yPos += 14

                if (!clickHandled && !this.isZoomed() && child == this.selectedBody) {
                    val grandchildren: MutableList<CelestialBody> = this.getChildren(child)
                    var gOffset: Int = 0
                    for (grandchild: CelestialBody? in grandchildren) {
                        if (gOffset + 14 > this.animateGrandchildren) {
                            break
                        }
                        clickHandled = this.testClicked(grandchild, 10, yPos, x, y, true)
                        yPos += 14
                        gOffset += 14
                        if (clickHandled) break
                    }
                    yPos += this.animateGrandchildren - gOffset
                }

                if (clickHandled) break
            }
        }

        if (!clickHandled) {
            for (e: MutableMap.MutableEntry<CelestialBody?, Vector3f?> in this.planetPosMap.entries) {
                val bodyClicked: CelestialBody = e.key!!
                if (this.selectedBody == null && bodyClicked is IChildBody) {
                    continue
                }

                val iconSize: Float = e.value!!.z // Z value holds size on-screen

                if (mouseX >= e.value!!.x - iconSize && mouseX <= e.value!!.x + iconSize && mouseY >= e.value!!.y - iconSize && mouseY <= e.value!!.y + iconSize) {
                    if (this.selectedBody !== bodyClicked || !this.isZoomed()) {
                        if (this.isSelected() && this.selectedBody !== bodyClicked) {
                            /*if (!(this.selectedBody instanceof IChildBody) || ((IChildBody) this.selectedBody).getParentPlanet() != bodyClicked)
                            {
//                                this.unselectCelestialBody();
                            }
                            else */
                            if (this.isZoomed()) {
                                this.selectionState = EnumSelection.SELECTED
                            }
                        }

                        if (bodyClicked !== this.selectedBody) {
                            this.lastSelectedBody = this.selectedBody
                            this.animateGrandchildren = 0
                            if (this.selectedBody !is IChildBody || (this.selectedBody as IChildBody).getParentPlanet() !== bodyClicked) {
                                // Only unzoom if the new selected body is not the child of the previously selected body
                                this.selectionState = EnumSelection.UNSELECTED
                            }
                        } else {
                            this.doneZooming = false
                            this.planetZoom = 0.0f
                        }

                        this.selectedBody = bodyClicked
                        this.ticksSinceSelection = 0
                        this.selectionState = EnumSelection.entries[this.selectionState.ordinal + 1]

                        if (bodyClicked is IChildBody) {
                            this.selectionState = EnumSelection.ZOOMED
                        }

                        if (this.isZoomed()) {
                            this.ticksSinceMenuOpen = 0
                        }

                        //Auto select if it's a spacestation and there is only a single entry
                        if (this.selectedBody is Satellite && this.spaceStationMap!!.get(this.getSatelliteParentID(this.selectedBody as Satellite))!!.size == 1) {
                            val it: MutableIterator<MutableMap.MutableEntry<String, StationDataGUI?>?> =
                                (this.spaceStationMap!!.get(this.getSatelliteParentID(this.selectedBody as Satellite?))!!.entries.iterator() as MutableIterator<MutableMap.MutableEntry<String, GuiCelestialSelection.StationDataGUI?>?>)
                            this.selectedStationOwner = it.next()!!.key
                        }

                        clickHandled = true
                        break
                    }
                }
            }
        }

        if (!clickHandled) {
            if (this.selectedBody != null) {
                this.unselectCelestialBody()
                this.planetZoom = 0.0f
            }

            mouseDragging = true
        }

        var selectedParent: Any? = this.selectedParent

        if (this.selectedBody is IChildBody) {
            selectedParent = (this.selectedBody as IChildBody).getParentPlanet()
        } else if (this.selectedBody is Planet) {
            selectedParent = (this.selectedBody as Planet).getParentSolarSystem()
        } else if (this.selectedBody == null) {
            selectedParent = GalacticraftCore.solarSystemSol
        }

        if (this.selectedParent !== selectedParent) {
            this.selectedParent = selectedParent
        }
    }

    protected open fun testClicked(
        body: CelestialBody?,
        xOffset: Int,
        yPos: Int,
        x: Int,
        y: Int,
        grandchild: Boolean
    ): Boolean {
        val xPos: Int = BORDER_SIZE + BORDER_EDGE_SIZE + 2 + xOffset
        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
            if (this.selectedBody !== body || !this.isZoomed()) {
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
                    // Selecting a different body
                    this.lastSelectedBody = this.selectedBody
                    this.selectionState = EnumSelection.SELECTED
                } else {
                    // Selecting the same body e.g. double-clicking
                    this.selectionState = EnumSelection.entries[this.selectionState.ordinal + 1]
                }

                this.selectedBody = body
                this.ticksSinceSelection = 0
                if (grandchild) this.selectionState = EnumSelection.ZOOMED
                if (this.isZoomed()) {
                    this.ticksSinceMenuOpen = 0
                }
                this.animateGrandchildren = 0
                return true
            }
        }
        return false
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun drawScreen(mousePosX: Int, mousePosY: Int, partialTicks: Float) {
        if (Mouse.hasWheel()) {
            val wheel: Float = Mouse.getDWheel() / (if (this.selectedBody == null) 500.0f else 250.0f)

            if (wheel != 0f) {
                if (this.selectedBody == null || (this.viewState == EnumView.PREVIEW && !this.isZoomed())) {
                    //Minimum zoom increased from 0.55F to 1F to allow zoom out to see other solar systems
                    this.zoom = min(max(this.zoom + wheel * ((this.zoom + 2.0f)) / 10.0f, -1.0f), 3f)
                } else {
                    this.planetZoom = min(max(this.planetZoom + wheel, -4.9f), 5f)
                }
            }
        }

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)

        val camMatrix: Matrix4f = Matrix4f()
        Matrix4f.translate(
            Vector3f(0.0f, 0.0f, -9000.0f),
            camMatrix,
            camMatrix
        ) // See EntityRenderer.java:setupOverlayRendering
        val viewMatrix: Matrix4f = Matrix4f()
        viewMatrix.m00 = 2.0f / width
        viewMatrix.m11 = 2.0f / -height
        viewMatrix.m22 = -2.0f / 9000.0f
        viewMatrix.m30 = -1.0f
        viewMatrix.m31 = 1.0f
        viewMatrix.m32 = -2.0f

        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glLoadIdentity()
        val fb: FloatBuffer = BufferUtils.createFloatBuffer(16 * java.lang.Float.SIZE)
        fb.rewind()
        viewMatrix.store(fb)
        fb.flip()
        GL11.glMultMatrix(fb)
        fb.clear()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glLoadIdentity()
        fb.rewind()
        camMatrix.store(fb)
        fb.flip()
        fb.clear()
        GL11.glMultMatrix(fb)

        this.setBlackBackground()

        GL11.glPushMatrix()
        val worldMatrix: Matrix4f = this.setIsometric(partialTicks)
        val gridSize: kotlin.Float = 7000f //194.4F;
        //TODO: Add dynamic map sizing, to allow the map to be small by default and expand when more distant solar systems are added.
        this.drawGrid(gridSize, height / 3 / 3.5f)
        this.drawCircles()
        GL11.glPopMatrix()

        val matrixMap: HashMap<CelestialBody?, Matrix4f?> = this.drawCelestialBodies(worldMatrix)

        this.planetPosMap.clear()

        for (e in matrixMap.entries) {
            val planetMatrix: Matrix4f? = e.value
            val matrix0: Matrix4f = Matrix4f.mul(viewMatrix, planetMatrix, planetMatrix)
            val x: Int = floor((matrix0.m30 * 0.5 + 0.5) * Minecraft.getMinecraft().displayWidth).toInt()
            val y: Int =
                floor(Minecraft.getMinecraft().displayHeight - (matrix0.m31 * 0.5 + 0.5) * Minecraft.getMinecraft().displayHeight).toInt()
            val vec: Vector2f = Vector2f(x.toFloat(), y.toFloat())

            val scaleVec: Matrix4f = Matrix4f()
            scaleVec.m00 = matrix0.m00
            scaleVec.m11 = matrix0.m11
            scaleVec.m22 = matrix0.m22
            val newVec: Vector4f = Matrix4f.transform(scaleVec, Vector4f(2f, -2f, 0f, 0f), null)
            val iconSize: kotlin.Float =
                (newVec.y * (Minecraft.getMinecraft().displayHeight / 2.0f)) * (if (e.key is Star) 2 else 1) * (if (e.key === this.selectedBody) 1.5f else 1.0f)

            this.planetPosMap.put(e.key, Vector3f(vec.x, vec.y, iconSize)) // Store size on-screen in Z-value for ease
        }

        this.drawSelectionCursor(fb, worldMatrix)

        try {
            this.drawButtons(mousePosX, mousePosY)
        } catch (e: Exception) {
            if (!this.errorLogged) {
                this.errorLogged = true
                GCLog.severe("Problem identifying planet or dimension in an add on for Galacticraft!")
                GCLog.severe("(The problem is likely caused by a dimension ID conflict.  Check configs for dimension clashes.  You can also try disabling Mars space station in configs.)")
                e.printStackTrace()
            }
        }

        this.drawBorder()
        GL11.glPopMatrix()

        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glLoadIdentity()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glLoadIdentity()
    }

    protected open fun drawSelectionCursor(fb: FloatBuffer, worldMatrix: Matrix4f?) {
        GL11.glPushMatrix()
        when (this.selectionState) {
            EnumSelection.SELECTED -> if (this.selectedBody != null) {
//                Matrix4f worldMatrix0 = new Matrix4f(worldMatrix);
//                Matrix4f.translate(this.getCelestialBodyPosition(this.selectedBody), worldMatrix0, worldMatrix0);
//                Matrix4f worldMatrix1 = new Matrix4f();
//                Matrix4f.rotate((float) Math.toRadians(45), new Vector3f(0, 0, 1), worldMatrix1, worldMatrix1);
//                Matrix4f.rotate((float) Math.toRadians(-55), new Vector3f(1, 0, 0), worldMatrix1, worldMatrix1);
//                worldMatrix1 = Matrix4f.mul(worldMatrix0, worldMatrix1, worldMatrix1);
//                fb.rewind();
//                worldMatrix1.store(fb);
//                fb.flip();
//                GL11.glMultMatrix(fb);
                setupMatrix(this.selectedBody, worldMatrix, fb)
                fb.clear()
                GL11.glScalef(1 / 15.0f, 1 / 15.0f, 1f)
                this.mc.renderEngine.bindTexture(guiMain0)
                val colMod: kotlin.Float =
                    if (this.getZoomAdvanced() < 4.9f) (sin((this.ticksSinceSelection / 2.0f).toDouble()) * 0.5f + 0.5f).toFloat() else 1.0f
                GL11.glColor4f(1.0f, 1.0f, 0.0f, 1 * colMod)
                val width: Int =
                    floor((getWidthForCelestialBody(this.selectedBody) / 2.0) * (if (this.selectedBody is IChildBody) 9.0 else 30.0)).toInt()

                this.drawTexturedModalRect(-width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false)
            }

            EnumSelection.ZOOMED -> if (this.selectedBody != null) {
//                Matrix4f worldMatrix0 = new Matrix4f(worldMatrix);
//                Matrix4f.translate(this.getCelestialBodyPosition(this.selectedBody), worldMatrix0, worldMatrix0);
//                Matrix4f worldMatrix1 = new Matrix4f();
//                Matrix4f.rotate((float) Math.toRadians(45), new Vector3f(0, 0, 1), worldMatrix1, worldMatrix1);
//                Matrix4f.rotate((float) Math.toRadians(-55), new Vector3f(1, 0, 0), worldMatrix1, worldMatrix1);
//                worldMatrix1 = Matrix4f.mul(worldMatrix0, worldMatrix1, worldMatrix1);
//                fb.rewind();
//                worldMatrix1.store(fb);
//                fb.flip();
//                GL11.glMultMatrix(fb);
                setupMatrix(this.selectedBody, worldMatrix, fb)
                fb.clear()
                val div: kotlin.Float = (this.zoom + 1.0f - this.planetZoom)
                val scale: kotlin.Float = max(0.3f, 1.5f / (this.ticksSinceSelection / 5.0f)) * 2.0f / div
                GL11.glScalef(scale, scale, 1f)
                this.mc.renderEngine.bindTexture(guiMain0)
                val colMod: kotlin.Float =
                    if (this.getZoomAdvanced() < 4.9f) (sin((this.ticksSinceSelection / 1.0f).toDouble()) * 0.5f + 0.5f).toFloat() else 1.0f
                GL11.glColor4f(0.4f, 0.8f, 1.0f, 1 * colMod)
                val width: Int = getWidthForCelestialBody(this.selectedBody) * 13
                this.drawTexturedModalRect(-width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false)
            }

            else -> {}
        }
        GL11.glPopMatrix()
    }

    protected open fun getCelestialBodyPosition(cBody: CelestialBody?): Vector3f {
        if (cBody == null) return Vector3f(0.0f, 0.0f, 0.0f)
        if (cBody is Star) {
            if (cBody.getTranslationKey()
                    .equals("star.sol", ignoreCase = true)
            )  //Return zero vector for Sol, different location for other solar systems
            {
                return Vector3f()
            }
            return ((cBody.getParentSolarSystem())!!.getMapPosition())!!.toVector3f()
        }

        val cBodyTicks: Int = this.celestialBodyTicks.get(cBody)!!
        val timeScale: kotlin.Float = if (cBody is Planet) 200.0f else 2.0f
        val distanceFromCenter: kotlin.Float = this.getScale(cBody)
        val cBodyPos: Vector3f = Vector3f(
            sin((cBodyTicks / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()).toDouble()).toFloat() * distanceFromCenter,
            cos((cBodyTicks / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()).toDouble()).toFloat() * distanceFromCenter,
            0f
        )

        if (cBody is Planet) {
            val parentVec: Vector3f = this.getCelestialBodyPosition((cBody.getParentSolarSystem())!!.getMainStar())
            return Vector3f.add(cBodyPos, parentVec, null)
        }

        if (cBody is IChildBody) {
            val parentVec: Vector3f = this.getCelestialBodyPosition((cBody as IChildBody).getParentPlanet())
            return Vector3f.add(cBodyPos, parentVec, null)
        }

        return cBodyPos
    }

    open fun getWidthForCelestialBody(celestialBody: CelestialBody?): Int {
        val zoomed: Boolean = celestialBody === this.selectedBody && this.selectionState == EnumSelection.SELECTED
        return if (celestialBody is Star) (if (zoomed) 12 else 8) else if (celestialBody is Planet) (if (zoomed) 6 else 4) else if (celestialBody is IChildBody) (if (zoomed) 6 else 4) else 2
    }

    open fun drawCelestialBodies(worldMatrix: Matrix4f?): HashMap<CelestialBody?, Matrix4f?> {
        GL11.glColor3f(1.0f, 1.0f, 1.0f)
        val fb: FloatBuffer = BufferUtils.createFloatBuffer(16 * java.lang.Float.SIZE)
        val matrixMap: HashMap<CelestialBody?, Matrix4f?> = Maps.newHashMap<CelestialBody?, Matrix4f?>()

        for (body: CelestialBody in bodiesToRender) {
            val hasParent: Boolean = body is IChildBody

            val alpha: kotlin.Float = getAlpha(body)

            if (alpha > 0.0f) {
                GlStateManager.pushMatrix()
                val worldMatrixLocal: Matrix4f = setupMatrix(body, worldMatrix, fb, if (hasParent) 0.25f else 1.0f)
                val preEvent: CelestialBodyRenderEvent.Pre = CelestialBodyRenderEvent.Pre(body, body.getBodyIcon(), 16)
                MinecraftForge.EVENT_BUS.post(preEvent)

                GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha)
                if (preEvent.celestialBodyTexture != null) {
                    this.mc.renderEngine.bindTexture(preEvent.celestialBodyTexture)
                }

                if (!preEvent.isCanceled()) {
                    val size: Int = getWidthForCelestialBody(body)
                    this.drawTexturedModalRect(
                        (-size / 2).toFloat(),
                        (-size / 2).toFloat(),
                        size.toFloat(),
                        size.toFloat(),
                        0f,
                        0f,
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

        return matrixMap
    }

    /**
     * Draws gray border around outside of gui
     */
    open fun drawBorder() {
        drawRect(0, 0, BORDER_SIZE, height, GREY2)
        drawRect(width - BORDER_SIZE, 0, width, height, GREY2)
        drawRect(0, 0, width, BORDER_SIZE, GREY2)
        drawRect(0, height - BORDER_SIZE, width, height, GREY2)
        drawRect(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE + BORDER_EDGE_SIZE, height - BORDER_SIZE, GREY0)
        drawRect(BORDER_SIZE, BORDER_SIZE, width - BORDER_SIZE, BORDER_SIZE + BORDER_EDGE_SIZE, GREY0)
        drawRect(width - BORDER_SIZE - BORDER_EDGE_SIZE, BORDER_SIZE, width - BORDER_SIZE, height - BORDER_SIZE, GREY1)
        drawRect(
            BORDER_SIZE + BORDER_EDGE_SIZE,
            height - BORDER_SIZE - BORDER_EDGE_SIZE,
            width - BORDER_SIZE,
            height - BORDER_SIZE,
            GREY1
        )
    }

    open fun drawButtons(mousePosX: Int, mousePosY: Int) {
        this.zLevel = 0.0f
        var handledSliderPos: Boolean = false

        val LHS: Int = BORDER_SIZE + BORDER_EDGE_SIZE
        val RHS: Int = width - LHS
        val TOP: Int = LHS
        val BOT: Int = height - LHS

        if (this.viewState == EnumView.PROFILE) {
            this.mc.renderEngine.bindTexture(guiMain0)
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
            this.drawTexturedModalRect(width / 2 - 43, TOP, 86, 15, 266, 0, 172, 29, false, false)
            var str: String = GCCoreUtil.translate("gui.message.catalog.name").uppercase(Locale.getDefault())
            this.fontRenderer.drawString(
                str,
                width / 2 - this.fontRenderer.getStringWidth(str) / 2,
                TOP + this.fontRenderer.FONT_HEIGHT / 2,
                WHITE
            )

            if (this.selectedBody != null) {
                this.mc.renderEngine.bindTexture(guiMain0)

                if (mousePosX > LHS && mousePosX < LHS + 88 && mousePosY > TOP && mousePosY < TOP + 13) {
                    GL11.glColor3f(3.0f, 0.0f, 0.0f)
                } else {
                    GL11.glColor3f(0.9f, 0.2f, 0.2f)
                }

                this.drawTexturedModalRect(LHS, TOP, 88, 13, 0, 392, 148, 22, false, false)
                str = GCCoreUtil.translate("gui.message.back.name").uppercase(Locale.getDefault())
                this.fontRenderer.drawString(
                    str,
                    LHS + 45 - this.fontRenderer.getStringWidth(str) / 2,
                    TOP + this.fontRenderer.FONT_HEIGHT / 2 - 2,
                    WHITE
                )

                this.mc.renderEngine.bindTexture(guiMain0)
                if (mousePosX > RHS - 88 && mousePosX < RHS && mousePosY > TOP && mousePosY < TOP + 13) {
                    GL11.glColor3f(0.0f, 3.0f, 0.0f)
                } else {
                    GL11.glColor3f(0.2f, 0.9f, 0.2f)
                }

                this.drawTexturedModalRect(RHS - 88, TOP, 88, 13, 0, 392, 148, 22, true, false)

                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
                this.drawTexturedModalRect(LHS, BOT - 13, 88, 13, 0, 392, 148, 22, false, true)
                this.drawTexturedModalRect(RHS - 88, BOT - 13, 88, 13, 0, 392, 148, 22, true, true)
                val menuTopLeft: Int = TOP - 115 + height / 2 - 4
                val posX: Int = LHS + min(this.ticksSinceSelection * 10, 133) - 134
                val posX2: Int = (LHS + min(this.ticksSinceSelection * 1.25f, 15f) - 15).toInt()
                val fontPosY: Int = menuTopLeft + BORDER_EDGE_SIZE + this.fontRenderer.FONT_HEIGHT / 2 - 2
                this.drawTexturedModalRect(posX, menuTopLeft + 12, 133, 196, 0, 0, 266, 392, false, false)

                //			str = this.selectedBody.getLocalizedName();
//			this.fontRenderer.drawString(str, posX + 20, fontPosY, GCCoreUtil.to32BitColor(255, 255, 255, 255));
                str = GCCoreUtil.translate("gui.message.daynightcycle.name") + ":"
                this.fontRenderer.drawString(str, posX + 5, fontPosY + 14, CYAN)
                str = GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".daynightcycle.0.name")
                this.fontRenderer.drawString(str, posX + 10, fontPosY + 25, WHITE)
                str = GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".daynightcycle.1.name")
                if (!str.isEmpty()) {
                    this.fontRenderer.drawString(str, posX + 10, fontPosY + 36, WHITE)
                }

                str = GCCoreUtil.translate("gui.message.surfacegravity.name") + ":"
                this.fontRenderer.drawString(str, posX + 5, fontPosY + 50, CYAN)
                str = GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".surfacegravity.0.name")
                this.fontRenderer.drawString(str, posX + 10, fontPosY + 61, WHITE)
                str = GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".surfacegravity.1.name")
                if (!str.isEmpty()) {
                    this.fontRenderer.drawString(str, posX + 10, fontPosY + 72, WHITE)
                }

                str = GCCoreUtil.translate("gui.message.surfacecomposition.name") + ":"
                this.fontRenderer.drawString(str, posX + 5, fontPosY + 88, CYAN)
                str =
                    GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".surfacecomposition.0.name")
                this.fontRenderer.drawString(str, posX + 10, fontPosY + 99, WHITE)
                str =
                    GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".surfacecomposition.1.name")
                if (!str.isEmpty()) {
                    this.fontRenderer.drawString(str, posX + 10, fontPosY + 110, WHITE)
                }

                str = GCCoreUtil.translate("gui.message.atmosphere.name") + ":"
                this.fontRenderer.drawString(str, posX + 5, fontPosY + 126, CYAN)
                str = GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".atmosphere.0.name")
                this.fontRenderer.drawString(str, posX + 10, fontPosY + 137, WHITE)
                str = GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".atmosphere.1.name")
                if (!str.isEmpty()) {
                    this.fontRenderer.drawString(str, posX + 10, fontPosY + 148, WHITE)
                }

                str = GCCoreUtil.translate("gui.message.meansurfacetemp.name") + ":"
                this.fontRenderer.drawString(str, posX + 5, fontPosY + 165, CYAN)
                str = GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".meansurfacetemp.0.name")
                this.fontRenderer.drawString(str, posX + 10, fontPosY + 176, WHITE)
                str = GCCoreUtil.translate("gui.message." + this.selectedBody!!.getName() + ".meansurfacetemp.1.name")
                if (!str.isEmpty()) {
                    this.fontRenderer.drawString(str, posX + 10, fontPosY + 187, WHITE)
                }

                this.mc.renderEngine.bindTexture(guiMain0)
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
                this.drawTexturedModalRect(posX2, menuTopLeft + 12, 17, 199, 439, 0, 32, 399, false, false)
                //			this.drawRectD(posX2 + 16.5, menuTopLeft + 13, posX + 131, menuTopLeft + 14, GCCoreUtil.to32BitColor(120, 0, (int) (0.6F * 255), 255));
            }
        } else {
            var str: String
            // Catalog:
            this.mc.renderEngine.bindTexture(guiMain0)
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
            this.drawTexturedModalRect(LHS, TOP, 74, 11, 0, 392, 148, 22, false, false)
            str = GCCoreUtil.translate("gui.message.catalog.name").uppercase(Locale.getDefault())
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.fontRenderer.drawString(str, LHS + 40 - fontRenderer.getStringWidth(str) / 2, TOP + 1, WHITE)

            val scale: Int = min(95f, this.ticksSinceMenuOpen * 12.0f).toInt()
            val planetZoomedNotMoon: Boolean = this.isZoomed() && this.selectedParent !is Planet


            // Parent frame:
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
            this.mc.renderEngine.bindTexture(guiMain0)
            this.drawTexturedModalRect(LHS - 95 + scale, TOP + 12, 95, 41, 0, 436, 95, 41, false, false)
            str = if (planetZoomedNotMoon) this.selectedBody!!.getLocalizedName() else this.getParentName()
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.fontRenderer.drawString(str, LHS + 9 - 95 + scale, TOP + 34, WHITE)
            GL11.glColor4f(1f, 1f, 0f, 1f)
            this.mc.renderEngine.bindTexture(guiMain0)

            // Grandparent frame:
            this.drawTexturedModalRect(LHS + 2 - 95 + scale, TOP + 14, 93, 17, 95, 436, 93, 17, false, false)
            str = if (planetZoomedNotMoon) this.getParentName() else this.getGrandparentName()
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            this.fontRenderer.drawString(str, LHS + 7 - 95 + scale, TOP + 16, GREY3)
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)

            val children: MutableList<CelestialBody> =
                this.getChildren(if (planetZoomedNotMoon) this.selectedBody else this.selectedParent)
            drawChildren(children, 0, 0, true)

            if (this.mapMode) {
                this.mc.renderEngine.bindTexture(guiMain0)
                GL11.glColor4f(1.0f, 0.0f, 0.0f, 1f)
                this.mc.renderEngine.bindTexture(guiMain0)
                this.drawTexturedModalRect(RHS - 74, TOP, 74, 11, 0, 392, 148, 22, true, false)
                str = GCCoreUtil.translate("gui.message.exit.name").uppercase(Locale.getDefault())
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                this.fontRenderer.drawString(str, RHS - 40 - fontRenderer.getStringWidth(str) / 2, TOP + 1, WHITE)
            }

            if (this.selectedBody != null) {
                // Right-hand bar (basic selectionState info)
                this.mc.renderEngine.bindTexture(guiMain1)
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)

                if (this.selectedBody is Satellite) {
                    val selectedSatellite: Satellite = this.selectedBody as Satellite
                    val stationListSize: Int =
                        this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!.size

                    this.mc.renderEngine.bindTexture(guiMain1)
                    val max: Int = min((this.height / 2) / 14, stationListSize)
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
                        GL11.glColor4f(0.65f, 0.65f, 0.65f, 1f)
                    } else {
                        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
                    }
                    this.drawTexturedModalRect(RHS - 85, TOP + 45, 61, 4, 0, 239, 61, 4, false, false)
                    if (max + spaceStationListOffset >= stationListSize) {
                        GL11.glColor4f(0.65f, 0.65f, 0.65f, 1f)
                    } else {
                        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
                    }
                    this.drawTexturedModalRect(RHS - 85, TOP + 49 + max * 14, 61, 4, 0, 239, 61, 4, false, true)
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)

                    if (this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!
                            .get(this.selectedStationOwner) == null
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
                        this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!.entries.iterator()
                    var i: Int = 0
                    var j: Int = 0
                    while (it.hasNext() && i < max) {
                        val e: MutableMap.MutableEntry<String?, StationDataGUI?> = it.next()

                        if (j >= this.spaceStationListOffset) {
                            this.mc.renderEngine.bindTexture(guiMain0)
                            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
                            var xOffset: Int = 0

                            if (e.key.equals(this.selectedStationOwner, ignoreCase = true)) {
                                xOffset -= 5
                            }

                            this.drawTexturedModalRect(
                                RHS - 95 + xOffset,
                                TOP + 50 + i * 14,
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
                            val str0: String? = e.value!!.getStationName()
                            var point: Int = 0
                            while (this.fontRenderer.getStringWidth(str) < 80 && point < str0!!.length) {
                                str = str + str0.substring(point, point + 1)
                                point++
                            }
                            if (this.fontRenderer.getStringWidth(str) >= 80) {
                                str = str.substring(0, str.length - 3)
                                str = str + "..."
                            }
                            this.fontRenderer.drawString(str, RHS - 88 + xOffset, TOP + 52 + i * 14, WHITE)
                            i++
                        }
                        j++
                    }
                } else {
                    this.drawTexturedModalRect(RHS - 96, TOP, 96, 139, 63, 0, 96, 139, false, false)
                }

                if (this.canCreateSpaceStation(this.selectedBody) && (this.selectedBody !is Satellite)) {
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
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
                    canCreateOffset = canCreateLength * this.fontRenderer.FONT_HEIGHT

                    this.drawTexturedModalRect(RHS - 95, TOP + 134, 93, 4, 159, 102, 93, 4, false, false)
                    for (barY: Int in 0..<canCreateLength) {
                        this.drawTexturedModalRect(
                            RHS - 95,
                            TOP + 138 + barY * this.fontRenderer.FONT_HEIGHT,
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
                    this.drawTexturedModalRect(
                        RHS - 95,
                        TOP + 138 + canCreateOffset,
                        93,
                        43,
                        159,
                        106,
                        93,
                        43,
                        false,
                        false
                    )
                    this.drawTexturedModalRect(RHS - 79, TOP + 129, 61, 4, 0, 170, 61, 4, false, false)

                    val recipe: SpaceStationRecipe? =
                        WorldUtil.getSpaceStationRecipe(this.selectedBody!!.getDimensionID())
                    if (recipe != null) {
                        GL11.glColor4f(0.0f, 1.0f, 0.1f, 1f)
                        var validInputMaterials: Boolean = true

                        var i: Int = 0
                        for (e: MutableMap.MutableEntry<Any?,Int> in recipe.getInput().entries) {
                            val next: Any? = e.key
                            val xPos: Int = (RHS - 95 + i * 93 / recipe.getInput().size.toDouble() + 5).toInt()
                            val yPos: Int = TOP + 154 + canCreateOffset

                            if (next is ItemStack) {
                                val amount: Int = getAmountInInventory(next)
                                RenderHelper.enableGUIStandardItemLighting()
                                val toRender: ItemStack = next.copy()
                                this.itemRender.renderItemAndEffectIntoGUI(toRender, xPos, yPos)
                                this.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, toRender, xPos, yPos, null)
                                RenderHelper.disableStandardItemLighting()
                                GL11.glEnable(GL11.GL_BLEND)

                                if (mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16) {
                                    GL11.glDepthMask(true)
                                    GL11.glEnable(GL11.GL_DEPTH_TEST)
                                    GL11.glPushMatrix()
                                    GL11.glTranslatef(0f, 0f, 300f)
                                    val k: Int = this.fontRenderer.getStringWidth(next.getDisplayName())
                                    var j2: Int = mousePosX - k / 2
                                    var k2: Int = mousePosY - 12
                                    val i1: Int = 8

                                    if (j2 + k > this.width) {
                                        j2 -= (j2 - this.width + k)
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
                                    val l1: Int = (k1 and 16711422) shr 1 or (k1 and -16777216)
                                    this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1)
                                    this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1)
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
                                val color: Int = if (valid or this.mc.player.capabilities.isCreativeMode) GREEN else RED
                                this.fontRenderer.drawString(
                                    str,
                                    xPos + 8 - this.fontRenderer.getStringWidth(str) / 2,
                                    TOP + 170 + canCreateOffset,
                                    color
                                )
                            } else if (next is MutableCollection<*>) {
                                val items: MutableCollection<ItemStack> = next as MutableCollection<ItemStack>

                                var amount: Int = 0

                                for (stack: ItemStack in items) {
                                    amount += getAmountInInventory(stack)
                                }

                                RenderHelper.enableGUIStandardItemLighting()

                                val it: MutableIterator<ItemStack?> = items.iterator()
                                var count: Int = 0
                                val toRenderIndex: Int = (this.ticksSinceMenuOpen / 20) % items.size
                                var toRender: ItemStack? = null
                                while (it.hasNext()) {
                                    val stack: ItemStack? = it.next()
                                    if (count == toRenderIndex) {
                                        toRender = stack
                                        break
                                    }
                                    count++
                                }

                                if (toRender == null) {
                                    continue
                                }

                                this.itemRender.renderItemAndEffectIntoGUI(toRender, xPos, yPos)
                                this.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, toRender, xPos, yPos, null)
                                RenderHelper.disableStandardItemLighting()
                                GL11.glEnable(GL11.GL_BLEND)

                                if (mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16) {
                                    GL11.glDepthMask(true)
                                    GL11.glEnable(GL11.GL_DEPTH_TEST)
                                    GL11.glPushMatrix()
                                    GL11.glTranslatef(0f, 0f, 300f)
                                    val k: Int = this.fontRenderer.getStringWidth(toRender.getDisplayName())
                                    var j2: Int = mousePosX - k / 2
                                    var k2: Int = mousePosY - 12
                                    val i1: Int = 8

                                    if (j2 + k > this.width) {
                                        j2 -= (j2 - this.width + k)
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
                                    val l1: Int = (k1 and 16711422) shr 1 or (k1 and -16777216)
                                    this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1)
                                    this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1)
                                    this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1)
                                    this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1)

                                    this.fontRenderer.drawString(toRender.getDisplayName(), j2, k2, WHITE)

                                    GL11.glPopMatrix()
                                }

                                str = "" + e.value
                                val valid: Boolean = amount >= e.value!!
                                if (!valid && validInputMaterials) {
                                    validInputMaterials = false
                                }
                                val color: Int = if (valid or this.mc.player.capabilities.isCreativeMode) GREEN else RED
                                this.fontRenderer.drawString(
                                    str,
                                    xPos + 8 - this.fontRenderer.getStringWidth(str) / 2,
                                    TOP + 170 + canCreateOffset,
                                    color
                                )
                            }

                            i++
                        }

                        if (validInputMaterials || this.mc.player.capabilities.isCreativeMode) {
                            GL11.glColor4f(0.0f, 1.0f, 0.1f, 1f)
                        } else {
                            GL11.glColor4f(1.0f, 0.0f, 0.0f, 1f)
                        }

                        this.mc.renderEngine.bindTexture(guiMain1)

                        if (!this.mapMode) {
                            if (mousePosX >= RHS - 95 && mousePosX <= RHS && mousePosY >= TOP + 182 + canCreateOffset && mousePosY <= TOP + 182 + 12 + canCreateOffset) {
                                this.drawTexturedModalRect(
                                    RHS - 95,
                                    TOP + 182 + canCreateOffset,
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
                        }

                        this.drawTexturedModalRect(
                            RHS - 95,
                            TOP + 182 + canCreateOffset,
                            93,
                            12,
                            0,
                            174,
                            93,
                            12,
                            false,
                            false
                        )

                        val color: Int = ((sin(this.ticksSinceMenuOpen / 5.0) * 0.5 + 0.5) * 255).toInt()
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.can_create_space_station.name"),
                            RHS - 48,
                            TOP + 137,
                            91,
                            ColorUtil.to32BitColor(255, color, 255, color),
                            true,
                            false
                        )

                        if (!mapMode) {
                            this.drawSplitString(
                                GCCoreUtil.translate("gui.message.create_ss.name").uppercase(Locale.getDefault()),
                                RHS - 48,
                                TOP + 185 + canCreateOffset,
                                91,
                                WHITE,
                                false,
                                false
                            )
                        }
                    } else {
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.cannot_create_space_station.name"),
                            RHS - 48,
                            TOP + 138,
                            91,
                            WHITE,
                            true,
                            false
                        )
                    }
                }

                // Catalog overlay
                this.mc.renderEngine.bindTexture(guiMain0)
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.3f - min(0.3f, this.ticksSinceSelection / 50.0f))
                this.drawTexturedModalRect(LHS, TOP, 74, 11, 0, 392, 148, 22, false, false)
                str = GCCoreUtil.translate("gui.message.catalog.name").uppercase(Locale.getDefault())
                this.fontRenderer.drawString(str, LHS + 40 - fontRenderer.getStringWidth(str) / 2, TOP + 1, WHITE)

                // Top bar title:
                this.mc.renderEngine.bindTexture(guiMain0)
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
                if (this.selectedBody is Satellite) {
                    if (this.selectedStationOwner.length == 0 || !this.selectedStationOwner.equals(
                            PlayerUtil.getName(
                                this.mc.player
                            ), ignoreCase = true
                        )
                    ) {
                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1f)
                    } else {
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1f)
                    }
                    this.drawTexturedModalRect(width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false)
                } else {
                    this.drawTexturedModalRect(width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false)
                }
                if (this.selectedBody!!.getTierRequirement() >= 0 && (this.selectedBody !is Satellite)) {
                    val canReach: Boolean
                    if (!this.selectedBody!!.getReachable() || (this.possibleBodies != null && !this.possibleBodies!!.contains(
                            this.selectedBody
                        ))
                    ) {
                        canReach = false
                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1f)
                    } else {
                        canReach = true
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1f)
                    }
                    this.drawTexturedModalRect(width / 2 - 30, TOP + 11, 30, 11, 0, 414, 60, 22, false, false)
                    this.drawTexturedModalRect(width / 2, TOP + 11, 30, 11, 128, 414, 60, 22, false, false)
                    str = GCCoreUtil.translateWithFormat(
                        "gui.message.tier.name",
                        if (this.selectedBody!!.getTierRequirement() == 0) "?" else this.selectedBody!!.getTierRequirement()
                    )
                    this.fontRenderer.drawString(
                        str,
                        width / 2 - this.fontRenderer.getStringWidth(str) / 2,
                        TOP + 13,
                        if (canReach) GREY4 else RED3
                    )
                }

                str = this.selectedBody!!.getLocalizedName()

                if (this.selectedBody is Satellite) {
                    str = GCCoreUtil.translate("gui.message.rename.name").uppercase(Locale.getDefault())
                }

                this.fontRenderer.drawString(str, width / 2 - this.fontRenderer.getStringWidth(str) / 2, TOP + 2, WHITE)

                // Catalog wedge:
                this.mc.renderEngine.bindTexture(guiMain0)
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
                this.drawTexturedModalRect(LHS + 4, TOP, 83, 12, 0, 477, 83, 12, false, false)

                if (!this.mapMode) {
                    if (!this.selectedBody!!.getReachable() || (this.possibleBodies != null && !this.possibleBodies!!.contains(
                            this.selectedBody
                        )) || (this.selectedBody is Satellite && this.selectedStationOwner == "")
                    ) {
                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1f)
                    } else {
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1f)
                    }

                    this.mc.renderEngine.bindTexture(guiMain0)
                    this.drawTexturedModalRect(RHS - 74, TOP, 74, 11, 0, 392, 148, 22, true, false)
                    str = GCCoreUtil.translate("gui.message.launch.name").uppercase(Locale.getDefault())
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    this.fontRenderer.drawString(str, RHS - 40 - fontRenderer.getStringWidth(str) / 2, TOP + 2, WHITE)
                }

                if (this.selectionState == EnumSelection.SELECTED && this.selectedBody !is Satellite) {
                    handledSliderPos = true

                    var sliderPos: Int = this.zoomTooltipPos
                    if (zoomTooltipPos != 38) {
                        sliderPos = min(this.ticksSinceSelection * 2, 38)
                        this.zoomTooltipPos = sliderPos
                    }

                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
                    this.mc.renderEngine.bindTexture(guiMain0)
                    this.drawTexturedModalRect(
                        RHS - 182,
                        height - BORDER_SIZE - BORDER_EDGE_SIZE - sliderPos,
                        83,
                        38,
                        512 - 166,
                        512 - 76,
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
                            RHS - 182 + 41,
                            height - BORDER_SIZE - BORDER_EDGE_SIZE + 2 - sliderPos,
                            79,
                            GREY5,
                            false,
                            false
                        )
                    } else if (!flag0 && flag1) {
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.click_again.1.name"),
                            RHS - 182 + 41,
                            height - BORDER_SIZE - BORDER_EDGE_SIZE + 6 - sliderPos,
                            79,
                            GREY5,
                            false,
                            false
                        )
                    } else if (flag0) {
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.click_again.2.name"),
                            RHS - 182 + 41,
                            height - BORDER_SIZE - BORDER_EDGE_SIZE + 6 - sliderPos,
                            79,
                            GREY5,
                            false,
                            false
                        )
                    } else {
                        this.drawSplitString(
                            GCCoreUtil.translate("gui.message.click_again.3.name"),
                            RHS - 182 + 41,
                            height - BORDER_SIZE - BORDER_EDGE_SIZE + 11 - sliderPos,
                            79,
                            GREY5,
                            false,
                            false
                        )
                    }
                }

                if (this.selectedBody is Satellite && renamingSpaceStation) {
                    this.drawDefaultBackground()
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
                    this.mc.renderEngine.bindTexture(guiMain1)
                    this.drawTexturedModalRect(
                        width / 2 - 90,
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
                        width / 2 - 90 + 4,
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
                        width / 2 - 90 + 8,
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
                        width / 2 - 90 + 17,
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
                        width / 2,
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
                        width / 2 - this.fontRenderer.getStringWidth(str) / 2,
                        this.height / 2 - 35,
                        WHITE
                    )
                    str = GCCoreUtil.translate("gui.message.apply.name")
                    this.fontRenderer.drawString(
                        str,
                        width / 2 - this.fontRenderer.getStringWidth(str) / 2 - 36,
                        this.height / 2 + 23,
                        WHITE
                    )
                    str = GCCoreUtil.translate("gui.message.cancel.name")
                    this.fontRenderer.drawString(
                        str,
                        width / 2 + 36 - this.fontRenderer.getStringWidth(str) / 2,
                        this.height / 2 + 23,
                        WHITE
                    )

                    if (this.renamingString == null) {
                        val selectedSatellite: Satellite? = this.selectedBody as Satellite
                        val playerName: String? = PlayerUtil.getName(this.mc.player)
                        this.renamingString =
                            this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!.get(playerName)!!
                                .getStationName()
                        if (this.renamingString == null) {
                            this.renamingString = this.spaceStationMap!!.get(getSatelliteParentID(selectedSatellite))!!
                                .get(playerName!!.lowercase(java.util.Locale.getDefault()))!!.getStationName()
                        }
                        if (this.renamingString == null) {
                            this.renamingString = ""
                        }
                    }

                    str = this.renamingString!!
                    var str0: String = this.renamingString!!

                    if ((this.ticksSinceMenuOpen / 10) % 2 == 0) {
                        str0 += "_"
                    }

                    this.fontRenderer.drawString(
                        str0,
                        width / 2 - this.fontRenderer.getStringWidth(str) / 2,
                        this.height / 2 - 17,
                        WHITE
                    )
                }

                //                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
//                GL11.glColor4f(0.0F, 0.6F, 1.0F, 1);
            }
        }

        if (!handledSliderPos) {
            this.zoomTooltipPos = 0
        }
    }

    /**
     * Draws child bodies (when appropriate) on the left-hand interface
     */
    protected open fun drawChildren(
        children: MutableList<CelestialBody>,
        xOffsetBase: Int,
        yOffsetPrior: Int,
        recursive: Boolean
    ): Int {
        var xOffsetBase: Int = xOffsetBase
        xOffsetBase += BORDER_SIZE + BORDER_EDGE_SIZE
        val yOffsetBase: Int = BORDER_SIZE + BORDER_EDGE_SIZE + 50 + yOffsetPrior
        var yOffset: Int = 0
        for (i: Int in children.indices) {
            val child: CelestialBody = children.get(i)
            val xOffset: Int = xOffsetBase + (if (child == this.selectedBody) 5 else 0)
            val scale: Int = min(95.0f, max(0.0f, (this.ticksSinceMenuOpen * 25.0f) - 95 * i)).toInt()

            this.mc.renderEngine.bindTexture(guiMain0)
            val brightness: kotlin.Float = if (child == this.selectedBody) 0.2f else 0.0f
            if (child.getReachable()) {
                GL11.glColor4f(0.0f, 0.6f + brightness, 0.0f, scale / 95.0f)
            } else {
                GL11.glColor4f(0.6f + brightness, 0.0f, 0.0f, scale / 95.0f)
            }
            this.drawTexturedModalRect(3 + xOffset, yOffsetBase + yOffset + 1, 86, 10, 0, 489, 86, 10, false, false)
            //            GL11.glColor4f(5 * brightness, 0.6F + 2 * brightness, 1.0F - 4 * brightness, scale / 95.0F);
            GL11.glColor4f(3 * brightness, 0.6f + 2 * brightness, 1.0f, scale / 95.0f)
            this.drawTexturedModalRect(2 + xOffset, yOffsetBase + yOffset, 93, 12, 95, 464, 93, 12, false, false)

            if (scale > 0) {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                val color: Int = 14737632
                this.fontRenderer.drawString(child.getLocalizedName(), 7 + xOffset, yOffsetBase + yOffset + 2, color)
            }

            yOffset += 14
            if (recursive && child == this.selectedBody) {
                val grandchildren: MutableList<CelestialBody> = this.getChildren(child)
                if (grandchildren.size > 0) {
                    if (this.animateGrandchildren == 14 * grandchildren.size) {
                        yOffset += drawChildren(grandchildren, 10, yOffset, false)
                    } else {
                        if (this.animateGrandchildren >= 14) {
                            val partial: MutableList<CelestialBody> = LinkedList<CelestialBody>()
                            for (j: Int in 0..<this.animateGrandchildren / 14) {
                                partial.add(grandchildren.get(j))
                            }
                            drawChildren(partial, 10, yOffset, false)
                        }
                        yOffset += this.animateGrandchildren
                        this.animateGrandchildren += 2
                    }
                }
            }
        }
        return yOffset
    }

    protected open fun getAmountInInventory(stack: ItemStack): Int {
        var amountInInv: Int = 0

        for (x: Int in 0..<FMLClientHandler.instance().getClientPlayerEntity().inventory.getSizeInventory()) {
            val slot: ItemStack = FMLClientHandler.instance().getClientPlayerEntity().inventory.getStackInSlot(x)

            if (slot != null) {
                if (SpaceStationRecipe.checkItemEquals(stack, slot)) {
                    amountInInv += slot.getCount()
                }
            }
        }

        return amountInInv
    }

    open fun drawSplitString(
        par1Str: String,
        par2: Int,
        par3: Int,
        par4: Int,
        par5: Int,
        small: Boolean,
        simulate: Boolean
    ): Int {
        return this.renderSplitString(par1Str, par2, par3, par4, par5, small, simulate)
    }

    protected open fun renderSplitString(
        par1Str: String,
        par2: Int,
        par3: Int,
        par4: Int,
        par6: Int,
        small: Boolean,
        simulate: Boolean
    ): Int {
        var par3: Int = par3
        if (small) {
            val list: MutableList<*> = this.fontRenderer.listFormattedStringToWidth(par1Str, par4)

            val iterator: MutableIterator<*> = list.iterator()
            while (iterator.hasNext()) {
                val s1: String = iterator.next() as String
                if (!simulate) {
                    this.renderStringAligned(s1, par2, par3, par4, par6)
                }
                par3 += this.fontRenderer.FONT_HEIGHT
            }

            return list.size
        } else {
            val list: MutableList<*> = this.fontRenderer.listFormattedStringToWidth(par1Str, par4)

            val iterator: MutableIterator<*> = list.iterator()
            while (iterator.hasNext()) {
                val s1: String = iterator.next() as String
                if (!simulate) {
                    this.renderStringAligned(s1, par2, par3, par4, par6)
                }
                par3 += this.fontRenderer.FONT_HEIGHT
            }

            return list.size
        }
    }

    protected open fun renderStringAligned(par1Str: String, par2: Int, par3: Int, par4: Int, par5: Int) {
        var par2: Int = par2
        if (this.fontRenderer.getBidiFlag()) {
            val i1: Int = this.fontRenderer.getStringWidth(this.bidiReorder(par1Str))
            par2 = par2 + par4 - i1
        }

        this.fontRenderer.drawString(
            par1Str,
            (par2 - this.fontRenderer.getStringWidth(par1Str) / 2).toFloat(),
            par3.toFloat(),
            par5,
            false
        )
    }

    protected open fun bidiReorder(p_147647_1_: String): String? {
        try {
            val bidi: Bidi = Bidi((ArabicShaping(8)).shape(p_147647_1_), 127)
            bidi.setReorderingMode(0)
            return bidi.writeReordered(2)
        } catch (arabicshapingexception: ArabicShapingException) {
            return p_147647_1_
        }
    }

    open fun drawTexturedModalRect(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        u: Int,
        v: Int,
        uWidth: Int,
        vHeight: Int,
        invertX: Boolean,
        invertY: Boolean
    ) {
        this.drawTexturedModalRect(
            x.toFloat(),
            y.toFloat(),
            width.toFloat(),
            height.toFloat(),
            u.toFloat(),
            v.toFloat(),
            uWidth.toFloat(),
            vHeight.toFloat(),
            invertX,
            invertY,
            512f,
            512f
        )
    }

    open fun drawTexturedModalRect(
        x: kotlin.Float,
        y: kotlin.Float,
        width: kotlin.Float,
        height: kotlin.Float,
        u: kotlin.Float,
        v: kotlin.Float,
        uWidth: kotlin.Float,
        vHeight: kotlin.Float,
        invertX: Boolean,
        invertY: Boolean,
        texSizeX: kotlin.Float,
        texSizeY: kotlin.Float
    ) {
        GL11.glShadeModel(GL11.GL_FLAT)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        val texModX: kotlin.Float = 1f / texSizeX
        val texModY: kotlin.Float = 1f / texSizeY
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        val height0: kotlin.Float = if (invertY) 0f else vHeight
        val height1: kotlin.Float = if (invertY) vHeight else 0f
        val width0: kotlin.Float = if (invertX) uWidth else 0f
        val width1: kotlin.Float = if (invertX) 0f else uWidth
        worldRenderer.pos(x.toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        worldRenderer.pos((x + width).toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        worldRenderer.pos((x + width).toDouble(), y.toDouble(), this.zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        worldRenderer.pos(x.toDouble(), y.toDouble(), this.zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        tessellator.draw()
    }

    open fun setBlackBackground() {
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(0.0, height.toDouble(), -90.0).endVertex()
        worldRenderer.pos(width.toDouble(), height.toDouble(), -90.0).endVertex()
        worldRenderer.pos(width.toDouble(), 0.0, -90.0).endVertex()
        worldRenderer.pos(0.0, 0.0, -90.0).endVertex()
        tessellator.draw()
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    /**
     * Rotates/translates/scales to appropriate values before drawing celestial bodies
     */
    open fun setIsometric(partialTicks: kotlin.Float): Matrix4f {
        val mat0: Matrix4f = Matrix4f()
        Matrix4f.translate(Vector3f(width / 2.0f, (height / 2).toFloat(), 0f), mat0, mat0)
        Matrix4f.rotate(Math.toRadians(55.0).toFloat(), Vector3f(1f, 0f, 0f), mat0, mat0)
        Matrix4f.rotate(Math.toRadians(-45.0).toFloat(), Vector3f(0f, 0f, 1f), mat0, mat0)
        val zoomLocal: kotlin.Float = this.getZoomAdvanced()
        this.zoom = zoomLocal
        Matrix4f.scale(Vector3f(1.1f + zoomLocal, 1.1f + zoomLocal, 1.1f + zoomLocal), mat0, mat0)
        val cBodyPos: Vector2f = this.getTranslationAdvanced(partialTicks)
        this.position = this.getTranslationAdvanced(partialTicks)
        Matrix4f.translate(Vector3f(-cBodyPos.x, -cBodyPos.y, 0f), mat0, mat0)
        val fb: FloatBuffer = BufferUtils.createFloatBuffer(16)
        fb.rewind()
        mat0.store(fb)
        fb.flip()
        GL11.glMultMatrix(fb)
        return mat0
    }

    /**
     * Draw background grid
     */
    open fun drawGrid(gridSize: kotlin.Float, gridScale: kotlin.Float) {
        var gridSize: kotlin.Float = gridSize
        GL11.glColor4f(0.0f, 0.2f, 0.5f, 0.55f)

        GL11.glBegin(GL11.GL_LINES)

        gridSize += gridScale / 2
        var v: kotlin.Float = -gridSize
        while (v <= gridSize) {
            GL11.glVertex3f(v, -gridSize, -0.0f)
            GL11.glVertex3f(v, gridSize, -0.0f)
            GL11.glVertex3f(-gridSize, v, -0.0f)
            GL11.glVertex3f(gridSize, v, -0.0f)
            v += gridScale
        }

        GL11.glEnd()
    }

    /**
     * Draw orbit circles on gui
     */
    open fun drawCircles() {
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glLineWidth(3f)
        var count: Int = 0

        val theta: kotlin.Float = (2 * Math.PI / 90).toFloat()
        val cos: kotlin.Float = cos(theta.toDouble()).toFloat()
        val sin: kotlin.Float = sin(theta.toDouble()).toFloat()

        for (body: CelestialBody in bodiesToRender) {
            var systemOffset: Vector3f = Vector3f(0.0f, 0.0f, 0.0f)
            if (body is IChildBody) {
                systemOffset = this.getCelestialBodyPosition((body as IChildBody).getParentPlanet())
            } else if (body is Planet) {
                systemOffset = this.getCelestialBodyPosition((body.getParentSolarSystem())!!.getMainStar())
            }

            var x: kotlin.Float = this.getScale(body)
            var y: kotlin.Float = 0f

            val alpha: kotlin.Float = getAlpha(body)

            if (alpha > 0.0f) {
                when (count % 2) {
                    0 -> GL11.glColor4f(0.0f / 1.4f, 0.6f / 1.4f, 1.0f / 1.4f, alpha / 1.4f)
                    1 -> GL11.glColor4f(0.3f / 1.4f, 0.8f / 1.4f, 1.0f / 1.4f, alpha / 1.4f)
                }

                val preEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre =
                    CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre(body, systemOffset)
                MinecraftForge.EVENT_BUS.post(preEvent)

                if (!preEvent.isCanceled()) {
                    GL11.glTranslatef(systemOffset.x, systemOffset.y, systemOffset.z)

                    GL11.glBegin(GL11.GL_LINE_LOOP)

                    var temp: kotlin.Float
                    for (i: Int in 0..89) {
                        GL11.glVertex2f(x, y)

                        temp = x
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

        GL11.glLineWidth(1f)
    }

    /**
     * Returns the transparency of the selected body.
     *
     * Hidden bodies will return 0.0, opaque bodies will return 1.0, and ones fading in/out will pass between those two values
     */
    open fun getAlpha(body: CelestialBody?): kotlin.Float {
        var alpha: kotlin.Float = 1.0f

        if (body is IChildBody) {
            val selected: Boolean =
                body === this.selectedBody || ((body as IChildBody).getParentPlanet() === this.selectedBody && this.selectionState != EnumSelection.SELECTED)
            val ready: Boolean = this.lastSelectedBody != null || this.ticksSinceSelection > 35
            val isSibling: Boolean = getSiblings(this.selectedBody).contains(body)
            val isPossible: Boolean =
                body !is Satellite || (this.possibleBodies != null && this.possibleBodies!!.contains(body))
            if ((!selected && !isSibling) || !isPossible) {
                alpha = 0.0f
            } else if (this.isZoomed() && ((!selected || !ready) && !isSibling)) {
                alpha = min(max((this.ticksSinceSelection - 30) / 15.0f, 0.0f), 1.0f)
            }
        } else {
            val isSelected: Boolean = this.selectedBody === body
            val isChildSelected: Boolean = this.selectedBody is IChildBody
            val isOwnChildSelected: Boolean =
                isChildSelected && (this.selectedBody as IChildBody).getParentPlanet() === body

            if (!isSelected && !isOwnChildSelected && (this.isZoomed() || isChildSelected)) {
                if (this.lastSelectedBody != null || this.selectedBody is IChildBody) {
                    alpha = 0.0f
                } else {
                    alpha = 1.0f - min(this.ticksSinceSelection / 25.0f, 1.0f)
                }
            }
        }

        return alpha
    }

    open class StationDataGUI
    constructor(private var stationName: String?, private val stationDimensionID: Int?) {
        open fun getStationName(): String? {
            return stationName
        }

        open fun setStationName(stationName: String?) {
            this.stationName = stationName
        }

        open fun getStationDimensionID(): Int? {
            return stationDimensionID
        }
    }

    protected open fun isZoomed(): Boolean {
        return this.selectionState == EnumSelection.ZOOMED
    }

    protected open fun isSelected(): Boolean {
        return this.selectionState != EnumSelection.UNSELECTED
    }

    protected open fun setupMatrix(body: CelestialBody?, worldMatrix: Matrix4f?, fb: FloatBuffer): Matrix4f {
        return setupMatrix(body, worldMatrix, fb, 1.0f)
    }

    protected open fun setupMatrix(
        body: CelestialBody?,
        worldMatrix: Matrix4f?,
        fb: FloatBuffer,
        scaleXZ: kotlin.Float
    ): Matrix4f {
        val worldMatrix0: Matrix4f = Matrix4f(worldMatrix)
        Matrix4f.translate(this.getCelestialBodyPosition(body), worldMatrix0, worldMatrix0)
        var worldMatrix1: Matrix4f = Matrix4f()
        Matrix4f.rotate(Math.toRadians(45.0).toFloat(), Vector3f(0f, 0f, 1f), worldMatrix1, worldMatrix1)
        Matrix4f.rotate(Math.toRadians(-55.0).toFloat(), Vector3f(1f, 0f, 0f), worldMatrix1, worldMatrix1)
        if (scaleXZ != 1.0f) {
            Matrix4f.scale(Vector3f(scaleXZ, scaleXZ, 1.0f), worldMatrix1, worldMatrix1)
        }
        worldMatrix1 = Matrix4f.mul(worldMatrix0, worldMatrix1, worldMatrix1)
        fb.rewind()
        worldMatrix1.store(fb)
        fb.flip()
        GL11.glMultMatrix(fb)

        return worldMatrix1
    }

    companion object {
        public const val MAX_SPACE_STATION_NAME_LENGTH: Int = 32

        @JvmField
        public var guiMain0: ResourceLocation =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialselection.png")

        @JvmField
        public var guiMain1: ResourceLocation =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialselection1.png")

        @JvmField
        public var BORDER_SIZE: Int = 0

        @JvmField
        public var BORDER_EDGE_SIZE: Int = 0

        // String colours
        @JvmField
        public val WHITE: Int = ColorUtil.to32BitColor(255, 255, 255, 255)

        @JvmField
        public val GREY5: Int = ColorUtil.to32BitColor(255, 150, 150, 150)

        @JvmField
        public val GREY4: Int = ColorUtil.to32BitColor(255, 140, 140, 140)

        @JvmField
        public val GREY3: Int = ColorUtil.to32BitColor(255, 120, 120, 120)

        @JvmField
        public val GREY2: Int = ColorUtil.to32BitColor(255, 100, 100, 100)

        @JvmField
        public val GREY1: Int = ColorUtil.to32BitColor(255, 80, 80, 80)

        @JvmField
        public val GREY0: Int = ColorUtil.to32BitColor(255, 40, 40, 40)

        @JvmField
        public val GREEN: Int = ColorUtil.to32BitColor(255, 0, 255, 0)

        @JvmField
        public val RED: Int = ColorUtil.to32BitColor(255, 255, 0, 0)

        @JvmField
        public val RED3: Int = ColorUtil.to32BitColor(255, 255, 100, 100)

        @JvmField
        public val CYAN: Int = ColorUtil.to32BitColor(255, 150, 200, 255)
    }
}
