package galaxyspace.core.client.gui.book.pages

import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedList
import java.util.List
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem

import asmodeuscore.api.dimension.IAdvancedSpace
import asmodeuscore.api.space.IExBody
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.astronomy.gui.book.Page_WithScroll
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import galaxyspace.GalaxySpace
import micdoodle8.mods.galacticraft.api.galaxies.*
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.WorldProvider
import org.lwjgl.opengl.GL11
import java.util.*

open class Page_Systems constructor(private val system: SolarSystem) : Page_WithScroll() {
    enum class Mode {
        LIST,
        BODY,
        MOON
    }

    private var current_mode = Mode.LIST
    private val mc: Minecraft = Minecraft.getInstance()
    private var maxX = 0
    private var selected_body: CelestialBody? = null
    private var selected_moon: Moon? = null

    private val resources: MutableMap<CelestialBody?, Array<ItemStack?>?> =
        HashMap<CelestialBody?, Array<ItemStack?>?>()

    override fun titlePage(): String? {
        return system.getTranslationKey()
    }

    override fun iconTitle(): ResourceLocation? {
        return (this.system.getMainStar())!!.getBodyIcon()
    }

    override fun rawTitle(): Boolean {
        return true
    }

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        super.drawPage(x, y, font, mouseX, mouseY)

        if (current_mode == Mode.LIST) {
            planets.clear()

            for (planet in (GalaxyRegistry.getPlanetsForSolarSystem(this.system))!!) {
                //if(planet.getParentSolarSystem().equals(GalacticraftCore.solarSystemSol))
                planets.add((planet)!!)
            }

            var posY: Int = 0

            for (s: CelestialBody in planets) {
                val currX: Int = (font)!!.getStringWidth(s.getLocalizedName())
                maxX = if (maxX < currX) currX else maxX
            }

            val offsetX: Int = 21
            //scroll = 0;
            for (i: Int in getScroll()..<planets.size) {
                if (i < 8 + getScroll()) {
                    val yOffset: Int = i - getScroll()
                    this.mc.getTextureManager().bindTexture(bookPageTexture)
                    posY = 0
                    if (mouseX >= x + 44 && mouseX < x + 40 + 25 + maxX && mouseY >= y + 41 + (offsetX * yOffset) && mouseY <= y + 41 + (offsetX * yOffset) + 16) posY =
                        17

                    this.mc.getTextureManager().bindTexture(bookPageTexture)
                    drawTexturedModalRect(
                        (x + 20).toFloat(),
                        (y + 41 + (offsetX * yOffset)).toFloat(),
                        21f,
                        21f,
                        461f,
                        35f,
                        18f,
                        21f,
                        false,
                        false,
                        512f,
                        256f
                    )

                    this.mc.getTextureManager().bindTexture(planets.get(i).getBodyIcon())
                    drawTexturedModalRect(
                        (x + 25).toFloat(),
                        (y + 45 + (offsetX * yOffset)).toFloat(),
                        12f,
                        12f,
                        0f,
                        0f,
                        16f,
                        16f,
                        false,
                        false,
                        16f,
                        16f
                    )

                    this.mc.getTextureManager().bindTexture(bookPageTexture)
                    drawTexturedModalRect(
                        (x + 40).toFloat(),
                        (y + 41 + (offsetX * yOffset)).toFloat(),
                        25f,
                        16f,
                        400f,
                        posY.toFloat(),
                        20f,
                        15f,
                        false,
                        false,
                        512f,
                        256f
                    )
                    drawTexturedModalRect(
                        (x + 40 + 16).toFloat(),
                        (y + 41 + (offsetX * yOffset)).toFloat(),
                        maxX.toFloat(),
                        16f,
                        410f,
                        posY.toFloat(),
                        70f,
                        15f,
                        false,
                        false,
                        512f,
                        256f
                    )
                    drawTexturedModalRect(
                        (x + 40 + maxX).toFloat(),
                        (y + 41 + (offsetX * yOffset)).toFloat(),
                        25f,
                        16f,
                        467f,
                        posY.toFloat(),
                        20f,
                        15f,
                        false,
                        false,
                        512f,
                        256f
                    )

                    (font)!!.drawString(planets.get(i).getLocalizedName(), x + 52, y + 46 + (offsetX * yOffset), 0xFFFFFF)
                }
            }

            this.mc.getTextureManager().bindTexture(starTexture)
            this.drawTexturedModalRect(
                (x + 80 + maxX).toFloat(),
                (y + 45).toFloat(),
                60f,
                60f,
                0f,
                0f,
                256f,
                256f,
                false,
                false,
                256f,
                256f
            )

            this.mc.getTextureManager().bindTexture((this.system.getMainStar())!!.getBodyIcon())
            drawTexturedModalRect(
                (x + 80 + maxX + 30 - 6).toFloat(),
                (y + 45 + 30 - 6).toFloat(),
                12f,
                12f,
                0f,
                0f,
                16f,
                16f,
                false,
                false,
                16f,
                16f
            )

            drawText(
                GCCoreUtil.translate("book.page." + (this.system.getMainStar())!!.getTranslationKey() + ".desc"),
                x + 110 + maxX,
                y - 40,
                0,
                200,
                (font)!!
            )
        } else if (current_mode == Mode.BODY) {
            var offsetX: Int = 0
            var posY: Int = 17
            var i: Int = 1

            this.mc.getTextureManager().bindTexture(bookPageTexture)
            drawTexturedModalRect(
                (x + 20).toFloat(),
                (y + 41 + offsetX).toFloat(),
                21f,
                21f,
                461f,
                35f,
                18f,
                21f,
                false,
                false,
                512f,
                256f
            )

            this.mc.getTextureManager().bindTexture(selected_body!!.getBodyIcon())
            drawTexturedModalRect(
                (x + 25).toFloat(),
                (y + 45 + offsetX).toFloat(),
                12f,
                12f,
                0f,
                0f,
                16f,
                16f,
                false,
                false,
                16f,
                16f
            )

            this.mc.getTextureManager().bindTexture(bookPageTexture)
            drawTexturedModalRect(
                (x + 40).toFloat(),
                (y + 41 + offsetX).toFloat(),
                25f,
                16f,
                400f,
                posY.toFloat(),
                20f,
                15f,
                false,
                false,
                512f,
                256f
            )
            drawTexturedModalRect(
                (x + 40 + 16).toFloat(),
                (y + 41 + offsetX).toFloat(),
                maxX.toFloat(),
                16f,
                410f,
                posY.toFloat(),
                70f,
                15f,
                false,
                false,
                512f,
                256f
            )
            drawTexturedModalRect(
                (x + 40 + maxX).toFloat(),
                (y + 41 + offsetX).toFloat(),
                25f,
                16f,
                467f,
                posY.toFloat(),
                20f,
                15f,
                false,
                false,
                512f,
                256f
            )

            (font)!!.drawString(selected_body!!.getLocalizedName(), x + 52, y + 46 + offsetX, 0xFFFFFF)


            if (selected_body is Planet) {
                offsetX = 21
                for (moon in (GalaxyRegistry.getMoonsForPlanet(selected_body as Planet))!!) {
                    posY = 0
                    if (mouseX >= x + 44 && mouseX < x + 40 + 25 + maxX && mouseY >= y + 41 + (offsetX * i) && mouseY <= y + 41 + (offsetX * i) + 16) posY =
                        17

                    this.mc.getTextureManager().bindTexture(bookPageTexture)
                    drawTexturedModalRect(
                        (x + 20).toFloat(),
                        (y + 41 + (offsetX * i)).toFloat(),
                        21f,
                        21f,
                        461f,
                        35f,
                        18f,
                        21f,
                        false,
                        false,
                        512f,
                        256f
                    )

                    this.mc.getTextureManager().bindTexture((moon)!!.getBodyIcon())
                    drawTexturedModalRect(
                        (x + 25).toFloat(),
                        (y + 45 + (offsetX * i)).toFloat(),
                        12f,
                        12f,
                        0f,
                        0f,
                        16f,
                        16f,
                        false,
                        false,
                        16f,
                        16f
                    )

                    this.mc.getTextureManager().bindTexture(bookPageTexture)
                    drawTexturedModalRect(
                        (x + 40).toFloat(),
                        (y + 41 + offsetX * i).toFloat(),
                        25f,
                        16f,
                        400f,
                        posY.toFloat(),
                        20f,
                        15f,
                        false,
                        false,
                        512f,
                        256f
                    )
                    drawTexturedModalRect(
                        (x + 40 + 16).toFloat(),
                        (y + 41 + offsetX * i).toFloat(),
                        maxX.toFloat(),
                        16f,
                        410f,
                        posY.toFloat(),
                        70f,
                        15f,
                        false,
                        false,
                        512f,
                        256f
                    )
                    drawTexturedModalRect(
                        (x + 40 + maxX).toFloat(),
                        (y + 41 + offsetX * i).toFloat(),
                        25f,
                        16f,
                        467f,
                        posY.toFloat(),
                        20f,
                        15f,
                        false,
                        false,
                        512f,
                        256f
                    )

                    (font)!!.drawSplitString((moon)!!.getLocalizedName(), x + 52, y + 46 + offsetX * i, 260, 0xFFFFFF)

                    i++
                }
            }

            if (selected_body != null) {
                (font)!!.drawSplitString(
                    GCCoreUtil.translate(
                        "book.page." + system.getTranslationKey() + "." + selected_body!!.getName()
                            .lowercase(Locale.getDefault())
                    ), x + 80 + maxX, y + 46, 260, 0xFFFFFF
                )

                val offsetY: Int = ((font)!!.getStringWidth(
                    GCCoreUtil.translate(
                        "book.page." + system.getTranslationKey() + "." + selected_body!!.getName()
                            .lowercase(Locale.getDefault())
                    )
                ) / 260) * (font)!!.FONT_HEIGHT
                var offsetY_Scroll: Int = offsetY

                offsetY_Scroll -= getScroll() * 10

                val dim: WorldProvider? = WorldUtil.getProviderForDimensionClient(this.selected_body!!.getDimensionID())

                var j: Int = 0
                if (dim is IGalacticraftWorldProvider) {
                    val gcdim: IGalacticraftWorldProvider = dim as IGalacticraftWorldProvider


                    val infos: MutableList<String?> = ArrayList<String?>()


                    if (this.resources.containsKey(selected_body)) {
                        var k: Int = 0
                        for (stacks: ItemStack? in this.resources.get(selected_body)!!) {
                            this.mc.renderEngine.bindTexture(bookPageTexture)
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                            this.drawTexturedModalRect(
                                (x + 78 + maxX + (k * 22)).toFloat(),
                                (y + 70 + offsetY).toFloat(),
                                20f,
                                20f,
                                452f,
                                94f,
                                20f,
                                20f,
                                false,
                                false,
                                576f,
                                250f
                            )
                            this.drawItemStack(
                                mc,
                                (stacks)!!,
                                x + 80 + maxX + (k++ * 22),
                                y + 72 + offsetY,
                                mouseX,
                                mouseY,
                                true
                            )
                        }
                    }

                    var grav: Float = (0.1f - (dim as IGalacticraftWorldProvider).getGravity()) * 1000
                    if ((dim as IGalacticraftWorldProvider).getGravity() == 0.0f) grav = 0.0f

                    if (dim is WorldProviderSpace) infos.add(GCCoreUtil.translate("gui.message.daylength") + " " + (gcdim as WorldProviderSpace).getDayLength() / 1000 + "h " + gcdim.getDayLength() % 1000 + "m")
                    if (dim is WE_WorldProviderSpace) infos.add(GCCoreUtil.translate("gui.message.daylength") + " " + (gcdim as WE_WorldProviderSpace).getDayLength() / 1000 + "h " + gcdim.getDayLength() % 1000 + "m")
                    infos.add("Distance from star: " + selected_body!!.getRelativeDistanceFromCenter().unScaledDistance)
                    infos.add(GCCoreUtil.translate("gui.message.gravity") + " " + grav.toInt() + "%")
                    infos.add(GCCoreUtil.translate("gui.message.temperature") + " " + (gcdim.getCelestialBody())!!.atmosphere.thermalLevel() * 20 + " C")
                    infos.add(GCCoreUtil.translate("gui.message.windspeed") + " " + (gcdim.getCelestialBody())!!.atmosphere.windLevel())
                    if (dim is ISolarLevel) infos.add(
                        GCCoreUtil.translate("gui.message.solarenergy") + " " + Math.round(
                            ((dim as ISolarLevel).getSolarEnergyMultiplier() - 1) * 1000
                        ) / 10.0f + "%"
                    )

                    if (gcdim is IAdvancedSpace) infos.add(
                        GCCoreUtil.translate("gui.message.windenergy") + " " + Math.round(
                            (dim as IAdvancedSpace).getSolarWindMultiplier() * 1000
                        ) / 1000f
                    )


                    //BodiesData data = BodiesRegistry.getData().get(selected_body);
                    if (gcdim.getCelestialBody() is IExBody) {
                        val body: IExBody = gcdim.getCelestialBody() as IExBody
                        infos.add(GCCoreUtil.translate("gui.message.atmopressure") + " " + body.getAtmosphericPressure())
                        infos.add(GCCoreUtil.translate("gui.message.solarradiation") + " " + body.isSolarRadiation())
                    }

                    if (selected_body is IExBody) {
                        infos.add(GCCoreUtil.translate("gui.message.atmopressure") + " " + (selected_body as IExBody).getAtmosphericPressure())
                        infos.add(GCCoreUtil.translate("gui.message.solarradiation") + " " + (selected_body as IExBody).isSolarRadiation())
                    }

                    infos.add(GCCoreUtil.translate("gui.message.breathableatmo") + " " + gcdim.hasBreathableAtmosphere())

                    var k: Int = getScroll()
                    while (k < infos.size) {
                        if (!infos.get(k)!!.isEmpty() && k < 8 + getScroll()) {
                            this.mc.getTextureManager().bindTexture(bookPageTexture)
                            drawTexturedModalRect(
                                (x + 78 + maxX).toFloat(),
                                (y + 100 + (k * 12) + offsetY_Scroll).toFloat(),
                                136f,
                                13f,
                                401f,
                                119f,
                                85f,
                                15f,
                                false,
                                false,
                                512f,
                                256f
                            )

                            (font)!!.drawString(infos.get(k), x + 84 + maxX, y + 102 + (k * 12) + offsetY_Scroll, 0xFFFFFF)
                        }
                        k++
                        j++
                    }
                }


                //if(!selected_body.getReachable()) return;								
            }
        } else if (current_mode == Mode.MOON) {
            this.mc.getTextureManager().bindTexture(bookPageTexture)
            drawTexturedModalRect(
                (x + 20).toFloat(),
                (y + 41).toFloat(),
                21f,
                21f,
                461f,
                35f,
                18f,
                21f,
                false,
                false,
                512f,
                256f
            )

            this.mc.getTextureManager().bindTexture(selected_moon!!.getBodyIcon())
            drawTexturedModalRect(
                (x + 25).toFloat(),
                (y + 45).toFloat(),
                12f,
                12f,
                0f,
                0f,
                16f,
                16f,
                false,
                false,
                16f,
                16f
            )

            this.mc.getTextureManager().bindTexture(bookPageTexture)
            drawTexturedModalRect(
                (x + 40).toFloat(),
                (y + 41).toFloat(),
                25f,
                16f,
                400f,
                17f,
                20f,
                15f,
                false,
                false,
                512f,
                256f
            )
            drawTexturedModalRect(
                (x + 40 + 16).toFloat(),
                (y + 41).toFloat(),
                maxX.toFloat(),
                16f,
                410f,
                17f,
                70f,
                15f,
                false,
                false,
                512f,
                256f
            )
            drawTexturedModalRect(
                (x + 40 + maxX).toFloat(),
                (y + 41).toFloat(),
                25f,
                16f,
                467f,
                17f,
                20f,
                15f,
                false,
                false,
                512f,
                256f
            )

            (font)!!.drawString(selected_moon!!.getLocalizedName(), x + 52, y + 46, 0xFFFFFF)

            var j: Int = 0
            if (selected_moon != null) {
                (font)!!.drawString(
                    GCCoreUtil.translate(
                        "book.page." + system.getTranslationKey() + "." + selected_moon!!.getName()
                            .lowercase(Locale.getDefault())
                    ), x + 80 + maxX, y + 46, 0xFFFFFF
                )

                val offsetY: Int = ((font)!!.getStringWidth(
                    GCCoreUtil.translate(
                        "book.page." + system.getTranslationKey() + "." + selected_moon!!.getName()
                            .lowercase(Locale.getDefault())
                    )
                ) / 260) * (font)!!.FONT_HEIGHT
                var offsetY_Scroll: Int = offsetY

                offsetY_Scroll -= getScroll() * 10

                val dim: WorldProvider? = WorldUtil.getProviderForDimensionClient(this.selected_moon!!.getDimensionID())

                if (dim is IGalacticraftWorldProvider) {
                    val gcdim: IGalacticraftWorldProvider = dim as IGalacticraftWorldProvider


                    if (this.resources.containsKey(selected_moon)) {
                        var k: Int = 0
                        for (stacks: ItemStack? in this.resources.get(selected_moon)!!) {
                            this.mc.renderEngine.bindTexture(bookPageTexture)
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                            if (k < 12) {
                                this.drawTexturedModalRect(
                                    (x + 78 + maxX + (k * 22)).toFloat(),
                                    (y + 70 + offsetY).toFloat(),
                                    20f,
                                    20f,
                                    452f,
                                    94f,
                                    20f,
                                    20f,
                                    false,
                                    false,
                                    576f,
                                    250f
                                )
                                this.drawItemStack(
                                    mc,
                                    (stacks)!!,
                                    x + 80 + maxX + (k * 22),
                                    y + 72 + offsetY,
                                    mouseX,
                                    mouseY,
                                    true
                                )
                            } else {
                                this.drawTexturedModalRect(
                                    (x + 78 + maxX + ((k - 12) * 22)).toFloat(),
                                    (y + 70 + offsetY + 22).toFloat(),
                                    20f,
                                    20f,
                                    452f,
                                    94f,
                                    20f,
                                    20f,
                                    false,
                                    false,
                                    576f,
                                    250f
                                )
                                this.drawItemStack(
                                    mc,
                                    (stacks)!!,
                                    x + 80 + maxX + ((k - 12) * 22),
                                    y + 72 + offsetY + 22,
                                    mouseX,
                                    mouseY,
                                    true
                                )
                            }
                            k++
                        }
                    }

                    val infos: MutableList<String?> = ArrayList<String?>()

                    var grav: Float = (0.1f - (dim as IGalacticraftWorldProvider).getGravity()) * 1000
                    if ((dim as IGalacticraftWorldProvider).getGravity() == 0.0f) grav = 0.0f

                    if (dim is WorldProviderSpace) infos.add(GCCoreUtil.translate("gui.message.daylength") + " " + (gcdim as WorldProviderSpace).getDayLength() / 1000 + "h " + gcdim.getDayLength() % 1000 + "m")
                    if (dim is WE_WorldProviderSpace) infos.add(GCCoreUtil.translate("gui.message.daylength") + " " + (gcdim as WE_WorldProviderSpace).getDayLength() / 1000 + "h " + gcdim.getDayLength() % 1000 + "m")
                    infos.add("Distance from planet: " + selected_moon!!.getRelativeDistanceFromCenter().unScaledDistance)
                    infos.add(GCCoreUtil.translate("gui.message.gravity") + " " + grav.toInt() + "%")
                    infos.add(GCCoreUtil.translate("gui.message.temperature") + " " + (gcdim.getCelestialBody())!!.atmosphere.thermalLevel() * 20 + " C")
                    infos.add(GCCoreUtil.translate("gui.message.windspeed") + " " + (gcdim.getCelestialBody())!!.atmosphere.windLevel())
                    if (dim is ISolarLevel) infos.add(
                        GCCoreUtil.translate("gui.message.solarenergy") + " " + Math.round(
                            ((dim as ISolarLevel).getSolarEnergyMultiplier() - 1) * 1000
                        ) / 10.0f + "%"
                    )

                    if (gcdim is IAdvancedSpace) infos.add(
                        GCCoreUtil.translate("gui.message.windenergy") + " " + Math.round(
                            (dim as IAdvancedSpace).getSolarWindMultiplier() * 1000
                        ) / 1000f
                    )

                    if (gcdim.getCelestialBody() is IExBody) {
                        val body: IExBody = gcdim.getCelestialBody() as IExBody
                        infos.add(GCCoreUtil.translate("gui.message.atmopressure") + " " + body.getAtmosphericPressure())
                        infos.add(GCCoreUtil.translate("gui.message.solarradiation") + " " + body.isSolarRadiation())
                    }

                    infos.add(GCCoreUtil.translate("gui.message.breathableatmo") + " " + gcdim.hasBreathableAtmosphere())
                    var k: Int = getScroll()
                    while (k < infos.size) {
                        if (!infos.get(k)!!.isEmpty() && k < 8 + getScroll()) {
                            this.mc.getTextureManager().bindTexture(bookPageTexture)
                            drawTexturedModalRect(
                                (x + 78 + maxX).toFloat(),
                                (y + 115 + (k * 12) + offsetY_Scroll).toFloat(),
                                146f,
                                13f,
                                401f,
                                119f,
                                85f,
                                15f,
                                false,
                                false,
                                512f,
                                256f
                            )

                            (font)!!.drawString(infos.get(k), x + 84 + maxX, y + 117 + (k * 12) + offsetY_Scroll, 0xFFFFFF)
                        }
                        k++
                        j++
                    }
                }
            }
        }
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, x: Int, y: Int) {
        super.mouseClick(mouseX, mouseY, mouseButton, x, y)

        val offsetX: Int = 21
        if (current_mode == Mode.LIST) for (i: Int in getScroll()..<planets.size) {
            if (i < 8 + getScroll()) {
                val yOffset: Int = i - getScroll()
                if (mouseX >= x + 44 && mouseX < x + 40 + 25 + maxX && mouseY >= y + 41 + (offsetX * yOffset) && mouseY <= y + 41 + (offsetX * yOffset) + 16) {
                    this.selected_body = planets.get(i)
                    current_mode = Mode.BODY
                }
            }
        }
        else if (current_mode == Mode.BODY) {
            for (i: Int in 1..<(GalaxyRegistry.getMoonsForPlanet(selected_body as Planet?))!!.size + 1) {
                if (mouseX >= x + 44 && mouseX < x + 40 + 25 + maxX && mouseY >= y + 41 + (offsetX * i) && mouseY <= y + 41 + (offsetX * i) + 16) {
                    this.selected_moon = (GalaxyRegistry.getMoonsForPlanet(selected_body as Planet?))!!.get(i - 1)
                    current_mode = Mode.MOON
                    this.setScroll(0)
                }
            }
        }
    }

    override fun hookBackButton(): Boolean {
        if (current_mode == Mode.BODY) {
            current_mode = Mode.LIST
            this.setScroll(0)
            return true
        } else if (current_mode == Mode.MOON) {
            current_mode = Mode.BODY
            setScroll(0)
            return true
        }
        return false
    }

    override fun getCategory(): String {
        return Book_Cateroies.BODIES.getName()
    }

    override fun getMaxScroll(): Int {
        if (current_mode == Mode.LIST && planets.size > 8) return planets.size % 8

        if (current_mode == Mode.BODY || current_mode == Mode.MOON) return 5

        return 0
    }

    protected open fun setResources(body: CelestialBody?, vararg stack: ItemStack?) {
        /*List<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(new ItemStack(GSBlocks.MERCURY_BLOCKS, 1, 3));
		stacks.add(new ItemStack(GSBlocks.MERCURY_BLOCKS, 1, 4));
		stacks.add(new ItemStack(GSBlocks.MERCURY_BLOCKS, 1, 5));	*/
        this.resources.put(body, (stack as Array<ItemStack?>?))
    }

    companion object {
        private val starTexture = ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/gui/book/star_bg.png")

        private val planets: MutableList<CelestialBody> = LinkedList<CelestialBody>()
    }
}
