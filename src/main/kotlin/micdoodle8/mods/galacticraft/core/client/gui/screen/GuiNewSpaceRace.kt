package micdoodle8.mods.galacticraft.core.client.gui.screen

import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox.ICheckBoxCallback
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementGradientList.ListElement
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.api.vector.Vector2
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.*
import micdoodle8.mods.galacticraft.core.client.model.ModelFlag
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager
import micdoodle8.mods.galacticraft.core.entities.EntityFlag
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.wrappers.FlagData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

open class GuiNewSpaceRace constructor(private val thePlayer: EntityPlayer) : GuiScreen(),
    GuiElementCheckbox.ICheckBoxCallback, GuiElementTextBox.ITextBoxCallback {
    enum class EnumSpaceRaceGui {
        MAIN,
        ADD_PLAYER,
        REMOVE_PLAYER,
        DESIGN_FLAG,
        CHANGE_TEAM_COLOR
    }

    private var ticksPassed = 0
    private var checkboxPaintbrush: GuiElementCheckbox? = null
    private var checkboxShowGrid: GuiElementCheckbox? = null
    private var checkboxEraser: GuiElementCheckbox? = null
    private var checkboxSelector: GuiElementCheckbox? = null
    private var checkboxColorSelector: GuiElementCheckbox? = null
    private var textBoxRename: GuiElementTextBox? = null
    private var initialized = false
    private var sliderColorR: GuiElementSlider? = null
    private var sliderColorG: GuiElementSlider? = null
    private var sliderColorB: GuiElementSlider? = null
    private var sliderBrushSize: GuiElementSlider? = null
    private var sliderEraserSize: GuiElementSlider? = null
    private var gradientListAddPlayers: GuiElementGradientList? = null
    private var gradientListRemovePlayers: GuiElementGradientList? = null
    private var currentState = EnumSpaceRaceGui.MAIN

    private var buttonFlag_width = 0
    private var buttonFlag_height = 0
    private var buttonFlag_xPosition = 0
    private var buttonFlag_yPosition = 0
    private var buttonFlag_hover = false

    private var buttonTeamColor_width = 0
    private var buttonTeamColor_height = 0
    private var buttonTeamColor_xPosition = 0
    private var buttonTeamColor_yPosition = 0
    private var buttonTeamColor_hover = false

    private var flagDesignerScale = Vector2()
    private var flagDesignerMinX = 0f
    private var flagDesignerMinY = 0f
    private var flagDesignerWidth = 0f
    private var flagDesignerHeight = 0f

    private var selectionMinX = 0
    private var selectionMaxX = 0
    private var selectionMinY = 0
    private var selectionMaxY = 0

    private val dummyFlag = EntityFlag(FMLClientHandler.instance().getClient().world)
    private val dummyModel = ModelFlag()

    private var spaceRaceData: SpaceRace? = null

    @JvmField
    var recentlyInvited: MutableMap<String?,Int> = HashMap<String?,Int>()

    private var lastMousePressed = false
    private var isDirty = true
    private var canEdit: Boolean

    init {
        val race: SpaceRace? = SpaceRaceManager.getSpaceRaceFromPlayer(PlayerUtil.getName(thePlayer))

        if (race != null) {
            this.spaceRaceData = race
        } else {
            val playerList: MutableList<String?> = ArrayList<String?>()
            playerList.add(PlayerUtil.getName(thePlayer))
            this.spaceRaceData = SpaceRace(playerList, SpaceRace.DEFAULT_NAME, FlagData(48, 32), Vector3(1.0, 1.0, 1.0))
        }

        this.mc = FMLClientHandler.instance().getClient()
        this.canEdit = canPlayerEdit()
    }

    private fun canPlayerEdit(): Boolean {
        return PlayerUtil.getName(this.mc.player) == (this.spaceRaceData!!.getPlayerNames())!!.get(0)
    }

    override fun initGui() {
        var sliderR: Float = 0f
        var sliderG: Float = 0f
        var sliderB: Float = 0f

        if (this.sliderColorR != null && this.sliderColorG != null && this.sliderColorB != null) {
            sliderR = this.sliderColorR!!.getSliderPos() / this.sliderColorR!!.getButtonHeight().toFloat()
            sliderG = this.sliderColorG!!.getSliderPos() / this.sliderColorG!!.getButtonHeight().toFloat()
            sliderB = this.sliderColorB!!.getSliderPos() / this.sliderColorB!!.getButtonHeight().toFloat()
        }

        super.initGui()
        this.buttonList.clear()

        if (this.initialized) {
            this.buttonFlag_width = 81
            this.buttonFlag_height = 58
            this.buttonFlag_xPosition = this.width / 2 - this.buttonFlag_width / 2
            this.buttonFlag_yPosition = this.height / 2 - this.height / 3 + 10

            this.buttonTeamColor_width = 45
            this.buttonTeamColor_height = 45
            this.buttonTeamColor_xPosition = this.buttonFlag_xPosition + this.buttonFlag_width + 10
            this.buttonTeamColor_yPosition =
                this.buttonFlag_yPosition + this.buttonFlag_height / 2 - this.buttonTeamColor_height / 2

            this.buttonList.add(
                GuiElementGradientButton(
                    0,
                    this.width / 2 - this.width / 3 + 15,
                    this.height / 2 - this.height / 4 - 15,
                    50,
                    15,
                    if (this.currentState == EnumSpaceRaceGui.MAIN) GCCoreUtil.translate("gui.space_race.create.close.name") else GCCoreUtil.translate(
                        "gui.space_race.create.back.name"
                    )
                )
            )

            var guiLeft: Int = 0
var guiBottom: Int = 0
var guiTop: Int = 0
var guiRight: Int = 0
var flagDesignerRight: Int = 0
var availWidth: Int = 0
var x1: Float = 0f
var x2: Float = 0f
var y1: Float = 0f
var height: Int = 0
when (this.currentState) {
                EnumSpaceRaceGui.MAIN -> {
                    this.textBoxRename = GuiElementTextBox(
                        1,
                        this,
                        this.width / 2 - 75,
                        this.buttonFlag_yPosition + this.buttonFlag_height + 10,
                        150,
                        16,
                        GCCoreUtil.translate("gui.space_race.create.rename.name"),
                        false,
                        25,
                        true
                    )
                    this.buttonList.add(this.textBoxRename)
                    if (this.canEdit) {
                        this.buttonList.add(
                            GuiElementGradientButton(
                                2,
                                this.width / 2 - 120,
                                this.textBoxRename!!.y + this.height / 10,
                                100,
                                this.height / 10,
                                GCCoreUtil.translate("gui.space_race.create.add_players.name")
                            )
                        )
                        this.buttonList.add(
                            GuiElementGradientButton(
                                3,
                                this.width / 2 - 120,
                                this.textBoxRename!!.y + this.height / 10 + this.height / 10 + this.height / 50,
                                100,
                                this.height / 10,
                                GCCoreUtil.translate("gui.space_race.create.rem_players.name")
                            )
                        )
                    }

                    val localStats: GuiElementGradientButton = GuiElementGradientButton(
                        4,
                        this.width / 2 + (if (this.canEdit) 20 else -50),
                        this.textBoxRename!!.y + this.height / 10,
                        100,
                        this.height / 10,
                        GCCoreUtil.translate("gui.space_race.create.server_stats.name")
                    )
                    val serverStats: GuiElementGradientButton = GuiElementGradientButton(
                        5,
                        this.width / 2 + (if (this.canEdit) 20 else -50),
                        this.textBoxRename!!.y + this.height / 10 + this.height / 10 + this.height / 50,
                        100,
                        this.height / 10,
                        GCCoreUtil.translate("gui.space_race.create.global_stats.name")
                    )
                    localStats.enabled = false
                    serverStats.enabled = false
                    this.buttonList.add(localStats)
                    this.buttonList.add(serverStats)
                }

                EnumSpaceRaceGui.ADD_PLAYER -> {
                    this.buttonList.add(
                        GuiElementGradientButton(
                            2,
                            this.width / 2 - this.width / 3 + 7,
                            this.height / 2 + this.height / 4 - 15,
                            64,
                            15,
                            GCCoreUtil.translate("gui.space_race.create.send_invite.name")
                        )
                    )
                    val xPos0: Int =
                        (this.buttonList.get(0) as GuiElementGradientButton).x + (this.buttonList.get(0) as GuiElementGradientButton).getButtonWidth() + 10
                    val xPos1: Int = this.width / 2 + this.width / 3 - 10
                    val yPos0: Int = this.height / 2 - this.height / 3 + 10
                    val yPos1: Int = this.height / 2 + this.height / 3 - 10
                    this.gradientListAddPlayers = GuiElementGradientList(xPos0, yPos0, xPos1 - xPos0, yPos1 - yPos0)
                }

                EnumSpaceRaceGui.REMOVE_PLAYER -> {
                    this.buttonList.add(
                        GuiElementGradientButton(
                            2,
                            this.width / 2 - this.width / 3 + 7,
                            this.height / 2 + this.height / 4 - 15,
                            64,
                            15,
                            GCCoreUtil.translate("gui.space_race.create.remove.name")
                        )
                    )
                    val xPos0b: Int =
                        (this.buttonList.get(0) as GuiElementGradientButton).x + (this.buttonList.get(0) as GuiElementGradientButton).getButtonWidth() + 10
                    val xPos1b: Int = this.width / 2 + this.width / 3 - 10
                    val yPos0b: Int = this.height / 2 - this.height / 3 + 10
                    val yPos1b: Int = this.height / 2 + this.height / 3 - 10
                    this.gradientListRemovePlayers =
                        GuiElementGradientList(xPos0b, yPos0b, xPos1b - xPos0b, yPos1b - yPos0b)
                }

                EnumSpaceRaceGui.DESIGN_FLAG -> {
                    guiBottom = this.height / 2 + this.height / 4
                    guiTop = this.height / 2 - this.height / 4
                    guiRight = this.width / 2 + this.width / 3
                    this.flagDesignerScale = Vector2((this.width / 130.0f).toDouble(), (this.height / 70.0f).toDouble())
                    this.flagDesignerMinX = this.width / 2 - (this.spaceRaceData!!.getFlagData())!!
                        .getWidth() * this.flagDesignerScale.x.toFloat() / 2
                    this.flagDesignerMinY = this.height / 2 - (this.spaceRaceData!!.getFlagData())!!
                        .getHeight() * this.flagDesignerScale.y.toFloat() / 2
                    this.flagDesignerWidth =
                        (this.spaceRaceData!!.getFlagData())!!.getWidth() * this.flagDesignerScale.x.toFloat()
                    this.flagDesignerHeight =
                        (this.spaceRaceData!!.getFlagData())!!.getHeight() * this.flagDesignerScale.y.toFloat()
                    flagDesignerRight = (this.flagDesignerMinX + this.flagDesignerWidth).toInt()
                    availWidth = ((guiRight - 10 - (this.flagDesignerMinX + this.flagDesignerWidth + 10)) / 3).toInt()
                    x1 = (flagDesignerRight + 10).toFloat()
                    x2 = (guiRight - 10).toFloat()
                    y1 = guiBottom - 10 - (x2 - x1)
                    height = (y1 - 10 - (guiTop + 10)).toInt()
                    this.sliderColorR = GuiElementSlider(
                        1,
                        flagDesignerRight + 10,
                        guiTop + 10,
                        availWidth,
                        height,
                        true,
                        Vector3(0.0, 0.0, 0.0),
                        Vector3(1.0, 0.0, 0.0)
                    )
                    this.sliderColorG = GuiElementSlider(
                        2,
                        flagDesignerRight + 11 + availWidth,
                        guiTop + 10,
                        availWidth,
                        height,
                        true,
                        Vector3(0.0, 0.0, 0.0),
                        Vector3(0.0, 1.0, 0.0)
                    )
                    this.sliderColorB = GuiElementSlider(
                        3,
                        flagDesignerRight + 12 + availWidth * 2,
                        guiTop + 10,
                        availWidth,
                        height,
                        true,
                        Vector3(0.0, 0.0, 0.0),
                        Vector3(0.0, 0.0, 1.0)
                    )
                    this.sliderColorR!!.setSliderPos(sliderR)
                    this.sliderColorG!!.setSliderPos(sliderG)
                    this.sliderColorB!!.setSliderPos(sliderB)
                    this.buttonList.add(this.sliderColorR)
                    this.buttonList.add(this.sliderColorG)
                    this.buttonList.add(this.sliderColorB)
                    this.checkboxPaintbrush = GuiElementCheckbox(
                        5,
                        this,
                        (this.flagDesignerMinX - 15).toInt(),
                        this.height / 2 - this.height / 4 + 10,
                        13,
                        13,
                        26,
                        26,
                        133,
                        0,
                        "",
                        4210752,
                        false
                    )
                    this.checkboxEraser = GuiElementCheckbox(
                        6,
                        this,
                        (this.flagDesignerMinX - 15).toInt(),
                        this.height / 2 - this.height / 4 + 25,
                        13,
                        13,
                        26,
                        26,
                        133,
                        52,
                        "",
                        4210752,
                        false
                    )
                    this.checkboxSelector = GuiElementCheckbox(
                        7,
                        this,
                        (this.flagDesignerMinX - 15).toInt(),
                        this.height / 2 - this.height / 4 + 40,
                        13,
                        13,
                        26,
                        26,
                        133,
                        78,
                        "",
                        4210752,
                        false
                    )
                    this.checkboxColorSelector = GuiElementCheckbox(
                        8,
                        this,
                        (this.flagDesignerMinX - 15).toInt(),
                        this.height / 2 - this.height / 4 + 55,
                        13,
                        13,
                        26,
                        26,
                        133,
                        104,
                        "",
                        4210752,
                        false
                    )
                    this.checkboxShowGrid = GuiElementCheckbox(
                        9,
                        this,
                        (this.flagDesignerMinX - 15).toInt(),
                        this.height / 2 - this.height / 4 + 90,
                        13,
                        13,
                        26,
                        26,
                        133,
                        26,
                        "",
                        4210752,
                        false
                    )
                    this.sliderBrushSize = GuiElementSlider(
                        10,
                        this.checkboxPaintbrush!!.x - 40,
                        this.checkboxPaintbrush!!.y,
                        35,
                        13,
                        false,
                        Vector3(0.34, 0.34, 0.34),
                        Vector3(0.34, 0.34, 0.34),
                        GCCoreUtil.translate("gui.space_race.create.brush_size.name")
                    )
                    this.sliderEraserSize = GuiElementSlider(
                        11,
                        this.checkboxEraser!!.x - 40,
                        this.checkboxEraser!!.y,
                        35,
                        13,
                        false,
                        Vector3(0.34, 0.34, 0.34),
                        Vector3(0.34, 0.34, 0.34),
                        GCCoreUtil.translate("gui.space_race.create.eraser_size.name")
                    )
                    this.sliderEraserSize!!.visible = false
                    this.buttonList.add(this.checkboxPaintbrush)
                    this.buttonList.add(this.checkboxShowGrid)
                    this.buttonList.add(this.checkboxEraser)
                    this.buttonList.add(this.checkboxSelector)
                    this.buttonList.add(this.checkboxColorSelector)
                    this.buttonList.add(this.sliderBrushSize)
                    this.buttonList.add(this.sliderEraserSize)
                }

                EnumSpaceRaceGui.CHANGE_TEAM_COLOR -> {
                    guiBottom = this.height / 2 + this.height / 4
                    guiTop = this.height / 2 - this.height / 4
                    guiLeft = this.width / 6
                    guiRight = this.width / 2 + this.width / 3
                    flagDesignerRight = guiLeft
                    availWidth = (guiRight - guiLeft - 100) / 3
                    x1 = (flagDesignerRight + 10).toFloat()
                    x2 = (guiLeft - 10).toFloat()
                    y1 = guiBottom - 10 - (x2 - x1)
                    height = (y1 - 10 - (guiTop + 30)).toInt()
                    this.sliderColorR = GuiElementSlider(
                        1,
                        flagDesignerRight + 25,
                        guiTop + 30,
                        availWidth,
                        height,
                        true,
                        Vector3(0.0, 0.0, 0.0),
                        Vector3(1.0, 0.0, 0.0)
                    )
                    this.sliderColorG = GuiElementSlider(
                        2,
                        flagDesignerRight + availWidth + 50,
                        guiTop + 30,
                        availWidth,
                        height,
                        true,
                        Vector3(0.0, 0.0, 0.0),
                        Vector3(0.0, 1.0, 0.0)
                    )
                    this.sliderColorB = GuiElementSlider(
                        3,
                        flagDesignerRight + availWidth * 2 + 75,
                        guiTop + 30,
                        availWidth,
                        height,
                        true,
                        Vector3(0.0, 0.0, 0.0),
                        Vector3(0.0, 0.0, 1.0)
                    )
                    this.sliderColorR!!.setSliderPos((this.spaceRaceData!!.getTeamColor())!!.floatX())
                    this.sliderColorG!!.setSliderPos((this.spaceRaceData!!.getTeamColor())!!.floatY())
                    this.sliderColorB!!.setSliderPos((this.spaceRaceData!!.getTeamColor())!!.floatZ())
                    this.buttonList.add(this.sliderColorR)
                    this.buttonList.add(this.sliderColorG)
                    this.buttonList.add(this.sliderColorB)
                }

                else -> {}
            }
        }
    }

    override fun onGuiClosed() {
        this.exitCurrentScreen(false)
    }

    @Throws(IOException::class)
    override fun keyTyped(keyChar: Char, keyID: Int) {
        if (this.textBoxRename != null && this.textBoxRename!!.keyTyped(keyChar, keyID)) {
            return
        }

        super.keyTyped(keyChar, keyID)
    }

    private fun exitCurrentScreen(close: Boolean) {
        if (this.currentState == EnumSpaceRaceGui.MAIN) {
            if (this.isDirty) {
                this.sendSpaceRaceData()
            }

            if (close) {
                if (this.canEdit) {
                    this.writeFlagToFile()
                }

                this.thePlayer.closeScreen()
            }
        } else {
            this.currentState = EnumSpaceRaceGui.MAIN
            this.initGui()
        }
    }

    override fun actionPerformed(buttonClicked: GuiButton) {
        when (buttonClicked.id) {
            0 -> {
                if (this.currentState == EnumSpaceRaceGui.CHANGE_TEAM_COLOR) {
                    this.markDirty()
                }

                this.exitCurrentScreen(true)
            }

            1 -> {}
            2 -> if (this.currentState == EnumSpaceRaceGui.MAIN && this.canEdit) {
                this.currentState = EnumSpaceRaceGui.ADD_PLAYER
                this.initGui()
            } else if (this.currentState == EnumSpaceRaceGui.ADD_PLAYER) {
                val playerToInvite: GuiElementGradientList.ListElement? =
                    this.gradientListAddPlayers!!.getSelectedElement()
                if (playerToInvite != null && !this.recentlyInvited.containsKey(playerToInvite.value)) {
                    val race: SpaceRace? = SpaceRaceManager.getSpaceRaceFromPlayer(PlayerUtil.getName(this.thePlayer))
                    if (race != null) {
                        (GalacticraftCore.packetPipeline)!!.sendToServer(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.S_INVITE_RACE_PLAYER,
                                GCCoreUtil.getDimensionID(mc.world),
                                arrayOf<Any?>(playerToInvite.value, race.getSpaceRaceID())
                            )
                        )
                        this.recentlyInvited.put(playerToInvite.value, 20 * 60)
                    }
                }
            } else if (this.currentState == EnumSpaceRaceGui.REMOVE_PLAYER) {
                val playerToRemove: GuiElementGradientList.ListElement? =
                    this.gradientListRemovePlayers!!.getSelectedElement()
                if (playerToRemove != null) {
                    val race: SpaceRace? = SpaceRaceManager.getSpaceRaceFromPlayer(PlayerUtil.getName(this.thePlayer))
                    if (race != null) {
                        (GalacticraftCore.packetPipeline)!!.sendToServer(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.S_REMOVE_RACE_PLAYER,
                                GCCoreUtil.getDimensionID(mc.world),
                                arrayOf<Any?>(playerToRemove.value, race.getSpaceRaceID())
                            )
                        )
                    }
                }
            }

            3 -> if (this.currentState == EnumSpaceRaceGui.MAIN && this.canEdit) {
                this.currentState = EnumSpaceRaceGui.REMOVE_PLAYER
                this.initGui()
            }

            else -> {}
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(x: Int, y: Int, clickIndex: Int) {
        super.mouseClicked(x, y, clickIndex)
    }

    override fun updateScreen() {
        super.updateScreen()

        if (this.ticksPassed % 100 == 0) {
            if (this.isDirty || this.ticksPassed == 0) {
                this.sendSpaceRaceData()
                this.isDirty = false
            } else {
                this.updateSpaceRaceData()
            }
        }

        ++this.ticksPassed

        for (e: MutableMap.MutableEntry<String?,Int> in HashSet<MutableMap.MutableEntry<String?,Int>>(this.recentlyInvited.entries)) {
            var timeLeft: Int = e.value!!
            if (--timeLeft < 0) {
                this.recentlyInvited.remove(e.key)
            } else {
                this.recentlyInvited.put(e.key, timeLeft)
            }
        }

        if (this.currentState == EnumSpaceRaceGui.ADD_PLAYER && this.gradientListAddPlayers != null && this.ticksPassed % 20 == 0) {
            val playerNames: MutableList<GuiElementGradientList.ListElement?> =
                ArrayList<GuiElementGradientList.ListElement?>()
            for (i: Int in this.thePlayer.world.playerEntities.indices) {
                val player: EntityPlayer = this.thePlayer.world.playerEntities.get(i) as EntityPlayer

                if (player.getDistanceSq(this.thePlayer) <= 25 * 25) {
                    val username: String? = PlayerUtil.getName(player)

                    if (!(this.spaceRaceData!!.getPlayerNames())!!.contains((username)!!)) {
                        playerNames.add(
                            GuiElementGradientList.ListElement(
                                username,
                                if (this.recentlyInvited.containsKey(username)) ColorUtil.to32BitColor(
                                    255,
                                    250,
                                    120,
                                    0
                                ) else ColorUtil.to32BitColor(255, 190, 190, 190)
                            )
                        )
                    }
                }
            }

            this.gradientListAddPlayers!!.updateListContents(playerNames)

            if (this.buttonList.size >= 2) {
                (this.buttonList.get(1) as GuiElementGradientButton).enabled =
                    this.gradientListAddPlayers!!.getSelectedElement() != null
            }
        }

        if (this.currentState == EnumSpaceRaceGui.REMOVE_PLAYER && this.gradientListRemovePlayers != null && this.ticksPassed % 20 == 0) {
            val playerNames: MutableList<GuiElementGradientList.ListElement?> =
                ArrayList<GuiElementGradientList.ListElement?>()
            for (i: Int in 1..<(this.spaceRaceData!!.getPlayerNames())!!.size) {
                val playerName: String? = (this.spaceRaceData!!.getPlayerNames())!!.get(i)
                playerNames.add(
                    GuiElementGradientList.ListElement(
                        playerName,
                        ColorUtil.to32BitColor(255, 190, 190, 190)
                    )
                )
            }

            this.gradientListRemovePlayers!!.updateListContents(playerNames)

            if (this.buttonList.size >= 2) {
                (this.buttonList.get(1) as GuiElementGradientButton).enabled =
                    this.gradientListRemovePlayers!!.getSelectedElement() != null
            }
        }

        if (this.currentState == EnumSpaceRaceGui.ADD_PLAYER && this.gradientListAddPlayers != null) {
            this.gradientListAddPlayers!!.update()
        }

        if (this.currentState == EnumSpaceRaceGui.REMOVE_PLAYER && this.gradientListRemovePlayers != null) {
            this.gradientListRemovePlayers!!.update()
        }

        if (!this.initialized) {
            return
        }

        if (this.currentState == EnumSpaceRaceGui.DESIGN_FLAG) {
            var x: Int = Mouse.getEventX() * this.width / this.mc.displayWidth
            val y: Int = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1

            if (this.canEdit && x >= this.flagDesignerMinX && y >= this.flagDesignerMinY && x <= this.flagDesignerMinX + this.flagDesignerWidth && y <= this.flagDesignerMinY + this.flagDesignerHeight) {
                val unScaledX: Int = floor((x - this.flagDesignerMinX) / this.flagDesignerScale.x).toInt()
                val unScaledY: Int = floor((y - this.flagDesignerMinY) / this.flagDesignerScale.y).toInt()

                if (Mouse.isButtonDown(0)) {
                    if (this.checkboxEraser!!.isSelected != null && (this.checkboxEraser!!.isSelected)!!) {
                        this.setColorWithBrushSize(
                            unScaledX,
                            unScaledY,
                            Vector3(255.0, 255.0, 255.0),
                            floor((this.sliderEraserSize!!.getNormalizedValue() * 10).toDouble()).toInt() + 1
                        )
                    } else if (this.checkboxColorSelector!!.isSelected != null && (this.checkboxColorSelector!!.isSelected)!!) {
                        val colorAt: Vector3 = (this.spaceRaceData!!.getFlagData())!!.getColorAt(unScaledX, unScaledY)
                        this.sliderColorR!!.setSliderPos(colorAt.floatX())
                        this.sliderColorG!!.setSliderPos(colorAt.floatY())
                        this.sliderColorB!!.setSliderPos(colorAt.floatZ())
                    } else if (this.checkboxPaintbrush!!.isSelected != null && (this.checkboxPaintbrush!!.isSelected)!!) {
                        this.setColorWithBrushSize(
                            unScaledX,
                            unScaledY,
                            Vector3(
                                this.sliderColorR!!.getColorValueD(),
                                this.sliderColorG!!.getColorValueD(),
                                this.sliderColorB!!.getColorValueD()
                            ),
                            floor((this.sliderBrushSize!!.getNormalizedValue() * 10).toDouble()).toInt() + 1
                        )
                    }
                }
            }

            if (this.checkboxSelector != null) {
                if (!this.lastMousePressed && Mouse.isButtonDown(0) && this.checkboxSelector!!.isSelected != null && (this.checkboxSelector!!.isSelected)!!) {
                    if (x >= this.flagDesignerMinX && y >= this.flagDesignerMinY && x <= this.flagDesignerMinX + this.flagDesignerWidth && y <= this.flagDesignerMinY + this.flagDesignerHeight) {
                        val unScaledX: Int = floor((x - this.flagDesignerMinX) / this.flagDesignerScale.x).toInt()
                        val unScaledY: Int = floor((y - this.flagDesignerMinY) / this.flagDesignerScale.y).toInt()
                        this.selectionMinX = unScaledX
                        this.selectionMinY = unScaledY
                    } else {
                        this.selectionMinY = -1
                        this.selectionMinX = this.selectionMinY
                    }
                } else if (this.lastMousePressed && !Mouse.isButtonDown(0) && this.checkboxSelector!!.isSelected != null && (this.checkboxSelector!!.isSelected)!!) {
                    if (this.selectionMinX != -1 && this.selectionMinY != -1 && x >= this.flagDesignerMinX && y >= this.flagDesignerMinY && x <= this.flagDesignerMinX + this.flagDesignerWidth && y <= this.flagDesignerMinY + this.flagDesignerHeight) {
                        val unScaledX: Int = floor((x - this.flagDesignerMinX) / this.flagDesignerScale.x).toInt()
                        val unScaledY: Int = floor((y - this.flagDesignerMinY) / this.flagDesignerScale.y).toInt()
                        this.selectionMaxX = min(unScaledX + 1, (this.spaceRaceData!!.getFlagData())!!.getWidth())
                        this.selectionMaxY = min(unScaledY + 1, (this.spaceRaceData!!.getFlagData())!!.getHeight())

                        if (this.selectionMinX > this.selectionMaxX) {
                            val temp: Int = this.selectionMaxX - 1
                            this.selectionMaxX = this.selectionMinX + 1
                            this.selectionMinX = temp
                        }

                        if (this.selectionMinY > this.selectionMaxY) {
                            val temp: Int = this.selectionMaxY - 1
                            this.selectionMaxY = this.selectionMinY + 1
                            this.selectionMinY = temp
                        }
                    } else {
                        this.selectionMaxY = -1
                        this.selectionMaxX = this.selectionMaxY
                    }
                }
            }

            if (this.sliderBrushSize != null && this.sliderBrushSize!!.visible) {
                this.sliderBrushSize!!.displayString =
                    GCCoreUtil.translate("gui.space_race.create.brush_radius.name") + ": " + (floor((this.sliderBrushSize!!.getNormalizedValue() * 10).toDouble()).toInt() + 1)
            }

            if (this.sliderEraserSize != null && this.sliderEraserSize!!.visible) {
                this.sliderEraserSize!!.displayString =
                    GCCoreUtil.translate("gui.space_race.create.eraser_radius.name") + ": " + (floor((this.sliderEraserSize!!.getNormalizedValue() * 10).toDouble()).toInt() + 1)
            }

            if (this.sliderColorR != null && this.sliderColorR!!.visible) {
                this.sliderColorR!!.displayString = floor(this.sliderColorR!!.getColorValueD()).toInt().toString()
            }

            if (this.sliderColorG != null && this.sliderColorG!!.visible) {
                this.sliderColorG!!.displayString = floor(this.sliderColorG!!.getColorValueD()).toInt().toString()
            }

            if (this.sliderColorB != null && this.sliderColorB!!.visible) {
                this.sliderColorB!!.displayString = floor(this.sliderColorB!!.getColorValueD()).toInt().toString()
            }
        } else if (this.currentState == EnumSpaceRaceGui.MAIN) {
            if (this.lastMousePressed && !Mouse.isButtonDown(0)) {
                if (this.buttonFlag_hover) {
                    this.currentState = EnumSpaceRaceGui.DESIGN_FLAG
                    this.initGui()
                }

                if (this.buttonTeamColor_hover) {
                    this.currentState = EnumSpaceRaceGui.CHANGE_TEAM_COLOR
                    this.initGui()
                }
            }
        }

        this.lastMousePressed = Mouse.isButtonDown(0)
    }

    private fun setColor(unScaledX: Int, unScaledY: Int, color: Vector3) {
        if (this.selectionMaxX - this.selectionMinX > 0 && this.selectionMaxY - this.selectionMinY > 0) {
            if (unScaledX >= this.selectionMinX && unScaledX <= this.selectionMaxX - 1 && unScaledY >= this.selectionMinY && unScaledY <= this.selectionMaxY - 1) {
                this.markDirty()
                (this.spaceRaceData!!.getFlagData())!!.setColorAt(unScaledX, unScaledY, color)
            }
        } else {
            this.markDirty()
            (this.spaceRaceData!!.getFlagData())!!.setColorAt(unScaledX, unScaledY, color)
        }
    }

    private fun setColorWithBrushSize(unScaledX: Int, unScaledY: Int, color: Vector3, brushSize: Int) {
        for (x: Int in unScaledX - brushSize + 1..<unScaledX + brushSize) {
            for (y: Int in unScaledY - brushSize + 1..<unScaledY + brushSize) {
                if (x >= 0 && x < (this.spaceRaceData!!.getFlagData())!!
                        .getWidth() && y >= 0 && y < (this.spaceRaceData!!.getFlagData())!!.getHeight()
                ) {
                    val relativeX: Float = x + 0.5f - (unScaledX + 0.5f)
                    val relativeY: Float = y + 0.5f - (unScaledY + 0.5f)

                    if (sqrt((relativeX * relativeX + relativeY * relativeY).toDouble()) <= brushSize) {
                        this.setColor(x, y, color)
                    }
                }
            }
        }
    }

    open fun updateSpaceRaceData() {
        val playerName: String? = PlayerUtil.getName(this.mc.player)
        val race: SpaceRace? = SpaceRaceManager.getSpaceRaceFromPlayer(playerName)

        if (race != null && !this.isDirty) {
            this.spaceRaceData = race
            this.canEdit = canPlayerEdit()

            if (this.textBoxRename!!.text != race.getTeamName()) {
                this.textBoxRename!!.text = race.getTeamName()
            }
        }
    }

    open fun sendSpaceRaceData() {
        if (this.canEdit) {
            val objList: MutableList<Any?> = ArrayList<Any?>()
            objList.add(this.spaceRaceData!!.getSpaceRaceID())
            objList.add(this.spaceRaceData!!.getTeamName())
            objList.add(this.spaceRaceData!!.getFlagData())
            objList.add(this.spaceRaceData!!.getTeamColor())
            objList.add((this.spaceRaceData!!.getPlayerNames())!!.toTypedArray<String?>())
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_START_NEW_SPACE_RACE,
                    GCCoreUtil.getDimensionID(mc.world),
                    objList
                )
            )
        }
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        this.drawDefaultBackground()

        if (this.initialized) {
            this.buttonFlag_hover =
                this.canEdit && this.currentState == EnumSpaceRaceGui.MAIN && par1 >= this.buttonFlag_xPosition && par2 >= this.buttonFlag_yPosition && par1 < this.buttonFlag_xPosition + this.buttonFlag_width && par2 < this.buttonFlag_yPosition + this.buttonFlag_height
            this.buttonTeamColor_hover =
                this.canEdit && this.currentState == EnumSpaceRaceGui.MAIN && par1 >= this.buttonTeamColor_xPosition && par2 >= this.buttonTeamColor_yPosition && par1 < this.buttonTeamColor_xPosition + this.buttonTeamColor_width && par2 < this.buttonTeamColor_yPosition + this.buttonTeamColor_height

            lateinit var tessellator: Tessellator
lateinit var worldRenderer: BufferBuilder
var x1: Float = 0f
var x2: Float = 0f
var y1: Float = 0f
var y2: Float = 0f
when (this.currentState) {
                EnumSpaceRaceGui.MAIN -> {
                    this.drawCenteredString(
                        this.fontRenderer,
                        GCCoreUtil.translate("gui.space_race.create.title.name"),
                        this.width / 2,
                        this.height / 2 - this.height / 3 - 15,
                        16777215
                    )
                    this.drawFlagButton()
                    this.drawColorButton()

                    val serverStats: GuiElementGradientButton =
                        this.buttonList.get(if (this.canEdit) 4 else 2) as GuiElementGradientButton
                    val localStats: GuiElementGradientButton =
                        this.buttonList.get(if (this.canEdit) 5 else 3) as GuiElementGradientButton

                    if (par1 > serverStats.x && par1 < serverStats.x + serverStats.width && par2 > serverStats.y && par2 < serverStats.y + serverStats.height) {
                        serverStats.displayString = GCCoreUtil.translate("gui.space_race.coming_soon")
                    } else {
                        serverStats.displayString = GCCoreUtil.translate("gui.space_race.create.server_stats.name")
                    }

                    if (par1 > localStats.x && par1 < localStats.x + localStats.width && par2 > localStats.y && par2 < localStats.y + localStats.height) {
                        localStats.displayString = GCCoreUtil.translate("gui.space_race.coming_soon")
                    } else {
                        localStats.displayString = GCCoreUtil.translate("gui.space_race.create.global_stats.name")
                    }
                }

                EnumSpaceRaceGui.ADD_PLAYER -> {
                    this.drawCenteredString(
                        this.fontRenderer,
                        GCCoreUtil.translate("gui.space_race.create.invite_player.name"),
                        this.width / 2,
                        this.height / 2 - this.height / 3 - 15,
                        16777215
                    )
                    this.drawCenteredString(
                        this.fontRenderer,
                        GCCoreUtil.translate("gui.space_race.create.player_radius.name"),
                        this.width / 2,
                        this.height / 2 + this.height / 3 + 3,
                        ColorUtil.to32BitColor(255, 180, 40, 40)
                    )
                }

                EnumSpaceRaceGui.REMOVE_PLAYER -> this.drawCenteredString(
                    this.fontRenderer,
                    GCCoreUtil.translate("gui.space_race.create.remove_player.name"),
                    this.width / 2,
                    this.height / 2 - this.height / 3 - 15,
                    16777215
                )

                EnumSpaceRaceGui.DESIGN_FLAG -> {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    GL11.glDisable(GL11.GL_TEXTURE_2D)
                    GL11.glEnable(GL11.GL_BLEND)
                    GL11.glDisable(GL11.GL_ALPHA_TEST)
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0)
                    GL11.glShadeModel(GL11.GL_SMOOTH)
                    tessellator = Tessellator.getInstance()
                    worldRenderer = tessellator.getBuffer()

                    var x: Int = 0
                    while (x < (this.spaceRaceData!!.getFlagData())!!.getWidth()) {
                        var y: Int = 0
                        while (y < (this.spaceRaceData!!.getFlagData())!!.getHeight()) {
                            val color: Vector3 = (this.spaceRaceData!!.getFlagData())!!.getColorAt(x, y)
                            GL11.glColor4f(color.floatX(), color.floatY(), color.floatZ(), 1.0f)
                            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                            worldRenderer.pos(
                                this.flagDesignerMinX + x * this.flagDesignerScale.x,
                                this.flagDesignerMinY + y * this.flagDesignerScale.y + 1 * this.flagDesignerScale.y,
                                0.0
                            ).endVertex()
                            worldRenderer.pos(
                                this.flagDesignerMinX + x * this.flagDesignerScale.x + 1 * this.flagDesignerScale.x,
                                this.flagDesignerMinY + y * this.flagDesignerScale.y + 1 * this.flagDesignerScale.y,
                                0.0
                            ).endVertex()
                            worldRenderer.pos(
                                this.flagDesignerMinX + x * this.flagDesignerScale.x + 1 * this.flagDesignerScale.x,
                                this.flagDesignerMinY + y * this.flagDesignerScale.y,
                                0.0
                            ).endVertex()
                            worldRenderer.pos(
                                this.flagDesignerMinX + x * this.flagDesignerScale.x,
                                this.flagDesignerMinY + y * this.flagDesignerScale.y,
                                0.0
                            ).endVertex()
                            tessellator.draw()
                            y++
                        }
                        x++
                    }

                    if (this.checkboxShowGrid != null && this.checkboxShowGrid!!.isSelected != null && (this.checkboxShowGrid!!.isSelected)!!) {
                        var x: Int = 0
                        while (x <= (this.spaceRaceData!!.getFlagData())!!.getWidth()) {
                            worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
                            worldRenderer.pos(
                                this.flagDesignerMinX + x * this.flagDesignerScale.x,
                                this.flagDesignerMinY.toDouble(),
                                this.zLevel.toDouble()
                            ).color(0.0f, 0.0f, 0.0f, 1.0f).endVertex()
                            worldRenderer.pos(
                                this.flagDesignerMinX + x * this.flagDesignerScale.x,
                                (this.flagDesignerMinY + this.flagDesignerHeight).toDouble(),
                                this.zLevel.toDouble()
                            ).color(0.0f, 0.0f, 0.0f, 1.0f).endVertex()
                            tessellator.draw()
                            x++
                        }

                        var y: Int = 0
                        while (y <= (this.spaceRaceData!!.getFlagData())!!.getHeight()) {
                            worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
                            worldRenderer.pos(
                                this.flagDesignerMinX.toDouble(),
                                this.flagDesignerMinY + y * this.flagDesignerScale.y,
                                this.zLevel.toDouble()
                            ).color(0.0f, 0.0f, 0.0f, 1.0f).endVertex()
                            worldRenderer.pos(
                                (this.flagDesignerMinX + this.flagDesignerWidth).toDouble(),
                                this.flagDesignerMinY + y * this.flagDesignerScale.y,
                                this.zLevel.toDouble()
                            ).color(0.0f, 0.0f, 0.0f, 1.0f).endVertex()
                            tessellator.draw()
                            y++
                        }
                    }

                    if (!(this.lastMousePressed && this.checkboxSelector!!.isSelected != null && (this.checkboxSelector!!.isSelected)!!) && this.selectionMaxX - this.selectionMinX > 0 && this.selectionMaxY - this.selectionMinY > 0) {
                        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)
                        val col: Float = (sin(this.ticksPassed * 0.3) * 0.4 + 0.1).toFloat()
                        worldRenderer.pos(
                            this.flagDesignerMinX + this.selectionMinX * this.flagDesignerScale.x,
                            this.flagDesignerMinY + this.selectionMinY * this.flagDesignerScale.y,
                            this.zLevel.toDouble()
                        ).color(col, col, col, 1.0f).endVertex()
                        worldRenderer.pos(
                            this.flagDesignerMinX + this.selectionMaxX * this.flagDesignerScale.x,
                            this.flagDesignerMinY + this.selectionMinY * this.flagDesignerScale.y,
                            this.zLevel.toDouble()
                        ).color(col, col, col, 1.0f).endVertex()
                        worldRenderer.pos(
                            this.flagDesignerMinX + this.selectionMaxX * this.flagDesignerScale.x,
                            this.flagDesignerMinY + this.selectionMaxY * this.flagDesignerScale.y,
                            this.zLevel.toDouble()
                        ).color(col, col, col, 1.0f).endVertex()
                        worldRenderer.pos(
                            this.flagDesignerMinX + this.selectionMinX * this.flagDesignerScale.x,
                            this.flagDesignerMinY + this.selectionMaxY * this.flagDesignerScale.y,
                            this.zLevel.toDouble()
                        ).color(col, col, col, 1.0f).endVertex()
                        worldRenderer.pos(
                            this.flagDesignerMinX + this.selectionMinX * this.flagDesignerScale.x,
                            this.flagDesignerMinY + this.selectionMinY * this.flagDesignerScale.y,
                            this.zLevel.toDouble()
                        ).color(col, col, col, 1.0f).endVertex()
                        tessellator.draw()
                    }

                    val guiRight: Int = this.width / 2 + this.width / 3
                    val guiBottom: Int = this.height / 2 + this.height / 4
                    x1 = this.sliderColorR!!.x.toFloat()
                    x2 = (guiRight - 10).toFloat()
                    y1 = guiBottom - 10 - (x2 - x1)
                    y2 = (guiBottom - 10).toFloat()

                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                    worldRenderer.pos(x2.toDouble(), y1.toDouble(), this.zLevel.toDouble()).color(0f, 0f, 0f, 1.0f)
                        .endVertex()
                    worldRenderer.pos(x1.toDouble(), y1.toDouble(), this.zLevel.toDouble()).color(0f, 0f, 0f, 1.0f)
                        .endVertex()
                    worldRenderer.pos(x1.toDouble(), y2.toDouble(), this.zLevel.toDouble()).color(0f, 0f, 0f, 1.0f)
                        .endVertex()
                    worldRenderer.pos(x2.toDouble(), y2.toDouble(), this.zLevel.toDouble()).color(0f, 0f, 0f, 1.0f)
                        .endVertex()
                    tessellator.draw()

                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                    worldRenderer.pos(x2.toDouble() - 1, y1.toDouble() + 1, this.zLevel.toDouble()).color(
                        this.sliderColorR!!.getNormalizedValue(),
                        this.sliderColorG!!.getNormalizedValue(),
                        this.sliderColorB!!.getNormalizedValue(),
                        1.0f
                    ).endVertex()
                    worldRenderer.pos(x1.toDouble() + 1, y1.toDouble() + 1, this.zLevel.toDouble()).color(
                        this.sliderColorR!!.getNormalizedValue(),
                        this.sliderColorG!!.getNormalizedValue(),
                        this.sliderColorB!!.getNormalizedValue(),
                        1.0f
                    ).endVertex()
                    worldRenderer.pos(x1.toDouble() + 1, y2.toDouble() - 1, this.zLevel.toDouble()).color(
                        this.sliderColorR!!.getNormalizedValue(),
                        this.sliderColorG!!.getNormalizedValue(),
                        this.sliderColorB!!.getNormalizedValue(),
                        1.0f
                    ).endVertex()
                    worldRenderer.pos(x2.toDouble() - 1, y2.toDouble() - 1, this.zLevel.toDouble()).color(
                        this.sliderColorR!!.getNormalizedValue(),
                        this.sliderColorG!!.getNormalizedValue(),
                        this.sliderColorB!!.getNormalizedValue(),
                        1.0f
                    ).endVertex()
                    tessellator.draw()

                    GL11.glShadeModel(GL11.GL_FLAT)
                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_ALPHA_TEST)
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                }

                EnumSpaceRaceGui.CHANGE_TEAM_COLOR -> {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    GL11.glDisable(GL11.GL_TEXTURE_2D)
                    GL11.glEnable(GL11.GL_BLEND)
                    GL11.glDisable(GL11.GL_ALPHA_TEST)
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0)
                    GL11.glShadeModel(GL11.GL_SMOOTH)
                    x1 = this.sliderColorG!!.x.toFloat()
                    x2 = (this.sliderColorG!!.x + this.sliderColorG!!.getButtonWidth()).toFloat()
                    y1 = (this.height / 2 - this.height / 3 + 5).toFloat()
                    y2 = (this.sliderColorG!!.y - 5).toFloat()
                    val xDiff: Float = x2 - x1
                    val yDiff: Float = y2 - y1

                    if (xDiff > yDiff) {
                        x1 = this.sliderColorG!!.x + this.sliderColorG!!.getButtonWidth() / 2 - yDiff / 2
                        x2 = this.sliderColorG!!.x + this.sliderColorG!!.getButtonWidth() / 2 + yDiff / 2
                    } else {
                        y2 = y1 + xDiff
                    }

                    tessellator = Tessellator.getInstance()
                    worldRenderer = tessellator.getBuffer()
                    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                    worldRenderer.pos(x2.toDouble() - 1, y1.toDouble() + 1, this.zLevel.toDouble()).color(
                        this.sliderColorR!!.getNormalizedValue(),
                        this.sliderColorG!!.getNormalizedValue(),
                        this.sliderColorB!!.getNormalizedValue(),
                        1.0f
                    ).endVertex()
                    worldRenderer.pos(x1.toDouble() + 1, y1.toDouble() + 1, this.zLevel.toDouble()).color(
                        this.sliderColorR!!.getNormalizedValue(),
                        this.sliderColorG!!.getNormalizedValue(),
                        this.sliderColorB!!.getNormalizedValue(),
                        1.0f
                    ).endVertex()
                    worldRenderer.pos(x1.toDouble() + 1, y2.toDouble() - 1, this.zLevel.toDouble()).color(
                        this.sliderColorR!!.getNormalizedValue(),
                        this.sliderColorG!!.getNormalizedValue(),
                        this.sliderColorB!!.getNormalizedValue(),
                        1.0f
                    ).endVertex()
                    worldRenderer.pos(x2.toDouble() - 1, y2.toDouble() - 1, this.zLevel.toDouble()).color(
                        this.sliderColorR!!.getNormalizedValue(),
                        this.sliderColorG!!.getNormalizedValue(),
                        this.sliderColorB!!.getNormalizedValue(),
                        1.0f
                    ).endVertex()
                    tessellator.draw()

                    this.spaceRaceData!!.setTeamColor(
                        Vector3(
                            this.sliderColorR!!.getNormalizedValue().toDouble(),
                            this.sliderColorG!!.getNormalizedValue().toDouble(),
                            this.sliderColorB!!.getNormalizedValue().toDouble()
                        )
                    )

                    GL11.glShadeModel(GL11.GL_FLAT)
                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_ALPHA_TEST)
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                }
            }
        }

        super.drawScreen(par1, par2, par3)

        if (this.currentState == EnumSpaceRaceGui.ADD_PLAYER && this.gradientListAddPlayers != null) {
            this.gradientListAddPlayers!!.draw(par1, par2)
        }

        if (this.currentState == EnumSpaceRaceGui.REMOVE_PLAYER && this.gradientListRemovePlayers != null) {
            this.gradientListRemovePlayers!!.draw(par1, par2)
        }

        for (button: GuiButton? in this.buttonList) {
            if (button is GuiElementSlider) {
                button.drawHoveringText()
            }
        }
    }

    private fun markDirty() {
        this.isDirty = true
    }

    private fun drawFlagButton() {
        GL11.glPushMatrix()
        GL11.glTranslatef(
            this.buttonFlag_xPosition + 2.9f,
            (this.buttonFlag_yPosition + this.buttonFlag_height + 1 - 4).toFloat(),
            0f
        )
        GL11.glScalef(74.0f, 74.0f, 1f)
        GL11.glTranslatef(0.0f, 0.36f, 1.0f)
        GL11.glScalef(1.0f, 1.0f, -1f)
        this.dummyFlag.flagData = this.spaceRaceData!!.getFlagData()
        this.dummyModel.renderFlag(this.dummyFlag, this.ticksPassed.toFloat())
        GL11.glColor3f(1f, 1f, 1f)
        GL11.glPopMatrix()

        GL11.glPushMatrix()
        GL11.glTranslatef(0.0f, 0.0f, 500.0f)
        val color: Int = if (this.buttonFlag_hover) 170 else 100
        if (this.canEdit) {
            val message: String = GCCoreUtil.translate("gui.space_race.create.customize.name")
            this.fontRenderer.drawString(
                message,
                (this.buttonFlag_xPosition + this.buttonFlag_width / 2 - this.fontRenderer.getStringWidth(message) / 2).toFloat(),
                (this.buttonFlag_yPosition + this.buttonFlag_height / 2 - 5).toFloat(),
                ColorUtil.to32BitColor(255, color, color, color),
                this.buttonFlag_hover
            )
        }
        GL11.glPopMatrix()

        if (this.buttonFlag_hover) {
            drawRect(
                this.buttonFlag_xPosition,
                this.buttonFlag_yPosition,
                this.buttonFlag_xPosition + this.buttonFlag_width,
                this.buttonFlag_yPosition + this.buttonFlag_height,
                ColorUtil.to32BitColor(255, 50, 50, 50)
            )
        }

        drawRect(
            this.buttonFlag_xPosition + this.buttonFlag_width - 1,
            this.buttonFlag_yPosition,
            this.buttonFlag_xPosition + this.buttonFlag_width,
            this.buttonFlag_yPosition + this.buttonFlag_height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.buttonFlag_xPosition,
            this.buttonFlag_yPosition,
            this.buttonFlag_xPosition + 1,
            this.buttonFlag_yPosition + this.buttonFlag_height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.buttonFlag_xPosition,
            this.buttonFlag_yPosition,
            this.buttonFlag_xPosition + this.buttonFlag_width,
            this.buttonFlag_yPosition + 1,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.buttonFlag_xPosition,
            this.buttonFlag_yPosition + this.buttonFlag_height - 1,
            this.buttonFlag_xPosition + this.buttonFlag_width,
            this.buttonFlag_yPosition + this.buttonFlag_height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
    }

    private fun drawColorButton() {
        drawRect(
            this.buttonTeamColor_xPosition + 2,
            this.buttonTeamColor_yPosition + 2,
            this.buttonTeamColor_xPosition + this.buttonTeamColor_width - 2,
            this.buttonTeamColor_yPosition + this.buttonTeamColor_height - 2,
            ColorUtil.to32BitColor(
                255,
                ((this.spaceRaceData!!.getTeamColor())!!.x * 255.0f).toInt(),
                ((this.spaceRaceData!!.getTeamColor())!!.y * 255.0f).toInt(),
                ((this.spaceRaceData!!.getTeamColor())!!.z * 255.0f).toInt()
            )
        )

        GL11.glPushMatrix()
        GL11.glTranslatef(0.0f, 0.0f, 500.0f)
        val color: Int = if (this.buttonTeamColor_hover) 170 else 100
        if (canEdit) {
            this.fontRenderer.drawString(
                GCCoreUtil.translate("gui.space_race.create.change_color.name.0"),
                (this.buttonTeamColor_xPosition + this.buttonTeamColor_width / 2 - this.fontRenderer.getStringWidth(
                    GCCoreUtil.translate("gui.space_race.create.change_color.name.0")
                ) / 2).toFloat(),
                (this.buttonTeamColor_yPosition + this.buttonTeamColor_height / 2 - 13).toFloat(),
                ColorUtil.to32BitColor(255, color, color, color),
                this.buttonTeamColor_hover
            )
        }
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.space_race.create.change_color.name.1"),
            (this.buttonTeamColor_xPosition + this.buttonTeamColor_width / 2 - this.fontRenderer.getStringWidth(
                GCCoreUtil.translate("gui.space_race.create.change_color.name.1")
            ) / 2).toFloat(),
            (this.buttonTeamColor_yPosition + this.buttonTeamColor_height / 2 - (if (canEdit) 3 else 9)).toFloat(),
            ColorUtil.to32BitColor(255, color, color, color),
            this.buttonTeamColor_hover
        )
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.space_race.create.change_color.name.2"),
            (this.buttonTeamColor_xPosition + this.buttonTeamColor_width / 2 - this.fontRenderer.getStringWidth(
                GCCoreUtil.translate("gui.space_race.create.change_color.name.2")
            ) / 2).toFloat(),
            (this.buttonTeamColor_yPosition + this.buttonTeamColor_height / 2 + (if (canEdit) 7 else 1)).toFloat(),
            ColorUtil.to32BitColor(255, color, color, color),
            this.buttonTeamColor_hover
        )
        GL11.glPopMatrix()

        if (this.buttonTeamColor_hover) {
            drawRect(
                this.buttonTeamColor_xPosition,
                this.buttonTeamColor_yPosition,
                this.buttonTeamColor_xPosition + this.buttonTeamColor_width,
                this.buttonTeamColor_yPosition + this.buttonTeamColor_height,
                ColorUtil.to32BitColor(55, 50, 50, 50)
            )
        }

        drawRect(
            this.buttonTeamColor_xPosition + this.buttonTeamColor_width - 1,
            this.buttonTeamColor_yPosition,
            this.buttonTeamColor_xPosition + this.buttonTeamColor_width,
            this.buttonTeamColor_yPosition + this.buttonTeamColor_height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.buttonTeamColor_xPosition,
            this.buttonTeamColor_yPosition,
            this.buttonTeamColor_xPosition + 1,
            this.buttonTeamColor_yPosition + this.buttonTeamColor_height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.buttonTeamColor_xPosition,
            this.buttonTeamColor_yPosition,
            this.buttonTeamColor_xPosition + this.buttonTeamColor_width,
            this.buttonTeamColor_yPosition + 1,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
        drawRect(
            this.buttonTeamColor_xPosition,
            this.buttonTeamColor_yPosition + this.buttonTeamColor_height - 1,
            this.buttonTeamColor_xPosition + this.buttonTeamColor_width,
            this.buttonTeamColor_yPosition + this.buttonTeamColor_height,
            ColorUtil.to32BitColor(255, 0, 0, 0)
        )
    }

    override fun drawWorldBackground(i: Int) {
        if (this.mc.world != null) {
            val scaleX: Int = min(this.ticksPassed * 14, this.width / 3)
            val scaleY: Int = min(this.ticksPassed * 14, this.height / 3)

            if (scaleX == this.width / 3 && scaleY == this.height / 3 && !this.initialized) {
                this.initialized = true
                this.initGui()
            }

            this.drawGradientRect(
                this.width / 2 - scaleX,
                this.height / 2 - scaleY,
                this.width / 2 + scaleX,
                this.height / 2 + scaleY,
                -1072689136,
                -804253680
            )
        } else {
            this.drawBackground(i)
        }
    }

    override fun onSelectionChanged(checkbox: GuiElementCheckbox?, newSelected: Boolean) {
        if (checkbox == this.checkboxEraser) {
            if (newSelected) {
                this.sliderEraserSize!!.visible = true

                if ((this.checkboxPaintbrush!!.isSelected)!!) {
                    this.sliderBrushSize!!.visible = false
                    this.checkboxPaintbrush!!.isSelected = false
                } else if ((this.checkboxSelector!!.isSelected)!!) {
                    this.checkboxSelector!!.isSelected = false
                } else if ((this.checkboxColorSelector!!.isSelected)!!) {
                    this.checkboxColorSelector!!.isSelected = false
                }
            } else {
                this.sliderEraserSize!!.visible = false
            }
        } else if (checkbox == this.checkboxPaintbrush) {
            if (newSelected) {
                this.sliderBrushSize!!.visible = true

                if ((this.checkboxEraser!!.isSelected)!!) {
                    this.sliderEraserSize!!.visible = false
                    this.checkboxEraser!!.isSelected = false
                } else if ((this.checkboxSelector!!.isSelected)!!) {
                    this.checkboxSelector!!.isSelected = false
                } else if ((this.checkboxColorSelector!!.isSelected)!!) {
                    this.checkboxColorSelector!!.isSelected = false
                }
            } else {
                this.sliderBrushSize!!.visible = false
            }
        } else if (checkbox == this.checkboxSelector) {
            if (newSelected) {
                if ((this.checkboxEraser!!.isSelected)!!) {
                    this.sliderEraserSize!!.visible = false
                    this.checkboxEraser!!.isSelected = false
                } else if ((this.checkboxPaintbrush!!.isSelected)!!) {
                    this.sliderBrushSize!!.visible = false
                    this.checkboxPaintbrush!!.isSelected = false
                } else if ((this.checkboxColorSelector!!.isSelected)!!) {
                    this.checkboxColorSelector!!.isSelected = false
                }
            }
        } else if (checkbox == this.checkboxColorSelector) {
            if (newSelected) {
                if ((this.checkboxEraser!!.isSelected)!!) {
                    this.sliderEraserSize!!.visible = false
                    this.checkboxEraser!!.isSelected = false
                } else if ((this.checkboxPaintbrush!!.isSelected)!!) {
                    this.sliderBrushSize!!.visible = false
                    this.checkboxPaintbrush!!.isSelected = false
                } else if ((this.checkboxSelector!!.isSelected)!!) {
                    this.checkboxSelector!!.isSelected = false
                }
            }
        }
    }

    override fun canPlayerEdit(checkbox: GuiElementCheckbox?, player: EntityPlayer?): Boolean {
        return true
    }

    override fun getInitiallySelected(checkbox: GuiElementCheckbox?): Boolean {
        return checkbox == this.checkboxPaintbrush
    }

    override fun onIntruderInteraction() {
    }

    override fun onIntruderInteraction(textBox: GuiElementTextBox?) {
    }

    override fun canPlayerEdit(textBox: GuiElementTextBox?, player: EntityPlayer?): Boolean {
        return this.canEdit
    }

    override fun onTextChanged(textBox: GuiElementTextBox?, newText: String?) {
        if (textBox === this.textBoxRename) {
            if (newText != this.spaceRaceData!!.getTeamName()) {
                this.spaceRaceData!!.setTeamName(newText)
                this.markDirty()
            }
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun getInitialText(textBox: GuiElementTextBox?): String? {
        if (textBox === this.textBoxRename) {
            return this.spaceRaceData!!.getTeamName()
        }

        return ""
    }

    override fun getTextColor(textBox: GuiElementTextBox?): Int {
        return ColorUtil.to32BitColor(255, 255, 255, 255)
    }

    private fun writeFlagToFile(): File? {
        try {
            val dirName: String? = Minecraft.getMinecraft().gameDir.getAbsolutePath()
            var directory: File = File(dirName, "assets")
            var success: Boolean = true
            if (!directory.exists()) {
                success = directory.mkdir()
            }
            if (success) {
                directory = File(directory, "flagCache")
                if (!directory.exists()) {
                    success = directory.mkdir()
                }

                if (success) {
                    val file: File = File(directory, this.spaceRaceData!!.getSpaceRaceID().toString() + ".png")
                    ImageIO.write((this.spaceRaceData!!.getFlagData())!!.toBufferedImage(), "png", file)
                    return file
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    companion object {
        @JvmField
        protected val texture: ResourceLocation = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/gui.png")
    }
}
