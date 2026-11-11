package micdoodle8.mods.galacticraft.core.client.gui.screen

import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox.ICheckBoxCallback
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementGradientButton
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox
import micdoodle8.mods.galacticraft.core.client.model.ModelFlag
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager
import micdoodle8.mods.galacticraft.core.entities.EntityFlag
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.wrappers.FlagData
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import java.io.IOException
import kotlin.math.min

open class GuiJoinSpaceRace constructor(player: EntityPlayerSP) : GuiScreen(),
    GuiElementCheckbox.ICheckBoxCallback, GuiElementTextBox.ITextBoxCallback {
    private var ticksPassed = 0
    private val thePlayer: EntityPlayer
    private var initialized = false

    private var buttonFlag_height = 0
    private var buttonFlag_xPosition = 0
    private var buttonFlag_yPosition = 0

    private val dummyFlag = EntityFlag(FMLClientHandler.instance().getClient().world)
    private val dummyModel = ModelFlag()

    private var spaceRaceData: SpaceRace? = null

    init {
        this.thePlayer = player
        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(player))!!

        val race: SpaceRace? = SpaceRaceManager.getSpaceRaceFromID(stats.getSpaceRaceInviteTeamID())

        if (race != null) {
            this.spaceRaceData = race
        } else {
            val playerList: MutableList<String?> = ArrayList<String?>()
            playerList.add(PlayerUtil.getName(player))
            this.spaceRaceData = SpaceRace(playerList, SpaceRace.DEFAULT_NAME, FlagData(48, 32), Vector3(1.0, 1.0, 1.0))
        }
    }

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()

        if (this.initialized) {
            val var5: Int = (this.width - this.width / 4) / 2
            val var6: Int = (this.height - this.height / 4) / 2

            val buttonFlag_width: Int = 81
            this.buttonFlag_height = 58
            this.buttonFlag_xPosition = this.width / 2 - buttonFlag_width / 2
            this.buttonFlag_yPosition = this.height / 2 - this.height / 3 + 10

            this.buttonList.add(
                GuiElementGradientButton(
                    0,
                    this.width / 2 - this.width / 3 + 15,
                    this.height / 2 - this.height / 4 - 15,
                    50,
                    15,
                    GCCoreUtil.translate("gui.space_race.create.close.name")
                )
            )
            val width: Int = (var5 / 1.0f).toInt()
            this.buttonList.add(
                GuiElementGradientButton(
                    1,
                    this.width / 2 - width / 2,
                    this.buttonFlag_yPosition + this.buttonFlag_height + 60,
                    width,
                    20,
                    GCCoreUtil.translateWithFormat("gui.space_race.join.name", this.spaceRaceData!!.getTeamName())
                )
            )
        }
    }

    override fun actionPerformed(buttonClicked: GuiButton) {
        when (buttonClicked.id) {
            0 -> this.thePlayer.closeScreen()
            1 -> {
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_ADD_RACE_PLAYER,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any?>(PlayerUtil.getName(this.thePlayer), this.spaceRaceData!!.getSpaceRaceID())
                    )
                )
                this.thePlayer.closeScreen()
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
        ++this.ticksPassed

        if (!this.initialized) {
        }
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        this.drawDefaultBackground()
        val var5: Int = (this.width - this.width / 4) / 2
        val var6: Int = (this.height - this.height / 4) / 2

        if (this.initialized) {
            this.drawCenteredString(
                this.fontRenderer,
                GCCoreUtil.translate("gui.space_race.join.title.name"),
                this.width / 2,
                this.height / 2 - this.height / 3 - 15,
                16777215
            )
            this.drawFlagButton(par1, par2)
            this.drawCenteredString(
                this.fontRenderer,
                GCCoreUtil.translate("gui.space_race.join.owner.name") + ": " + (this.spaceRaceData!!.getPlayerNames())!!
                    .get(0),
                this.width / 2,
                this.buttonFlag_yPosition + this.buttonFlag_height + 25,
                ColorUtil.to32BitColor(255, 150, 150, 150)
            )
            this.drawCenteredString(
                this.fontRenderer,
                GCCoreUtil.translateWithFormat(
                    "gui.space_race.join.member_count.name",
                    (this.spaceRaceData!!.getPlayerNames())!!.size
                ),
                this.width / 2,
                this.buttonFlag_yPosition + this.buttonFlag_height + 40,
                ColorUtil.to32BitColor(255, 150, 150, 150)
            )
            GL11.glPushMatrix()
            GL11.glTranslatef(
                (this.width / 2).toFloat(),
                (this.buttonFlag_yPosition + this.buttonFlag_height + 5 + FMLClientHandler.instance()
                    .getClient().fontRenderer.FONT_HEIGHT / 2).toFloat(),
                0f
            )
            GL11.glScalef(1.5f, 1.5f, 1.0f)
            GL11.glTranslatef(
                (-this.width / 2).toFloat(),
                ((-(this.buttonFlag_yPosition + this.buttonFlag_height + 5)) - FMLClientHandler.instance()
                    .getClient().fontRenderer.FONT_HEIGHT / 2).toFloat(),
                0f
            )
            this.drawCenteredString(
                this.fontRenderer,
                this.spaceRaceData!!.getTeamName(),
                this.width / 2,
                this.buttonFlag_yPosition + this.buttonFlag_height + 5,
                ColorUtil.to32BitColor(255, 100, 150, 20)
            )
            GL11.glPopMatrix()
        }

        super.drawScreen(par1, par2, par3)
    }

    private fun drawFlagButton(mouseX: Int, mouseY: Int) {
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
    }

    override fun canPlayerEdit(checkbox: GuiElementCheckbox?, player: EntityPlayer?): Boolean {
        return true
    }

    override fun getInitiallySelected(checkbox: GuiElementCheckbox?): Boolean {
        return false
    }

    override fun onIntruderInteraction() {
    }

    override fun onIntruderInteraction(textBox: GuiElementTextBox?) {
    }

    override fun canPlayerEdit(textBox: GuiElementTextBox?, player: EntityPlayer?): Boolean {
        return true
    }

    override fun onTextChanged(textBox: GuiElementTextBox?, newText: String?) {
    }

    override fun getInitialText(textBox: GuiElementTextBox?): String {
        return ""
    }

    override fun getTextColor(textBox: GuiElementTextBox?): Int {
        return ColorUtil.to32BitColor(255, 255, 255, 255)
    }

    companion object {
        @JvmField
        protected val texture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/gui.png")
    }
}
