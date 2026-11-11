package micdoodle8.mods.galacticraft.planets.mars.client.gui

import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import net.minecraft.client.resources.sounds.SimpleSoundInstance as PositionedSoundRecord
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.input.Keyboard
import java.io.IOException
import kotlin.math.atan
import kotlin.math.floor

open class GuiSlimeling constructor(private val slimeling: EntitySlimeling) : GuiScreen() {
    private val xSize = 176
    private val ySize = 147

    @JvmField
    var nameField: GuiTextField? = null

    @JvmField
    var stayButton: GuiButton? = null

    private var invX = 0
    private var invY = 0
    private val invWidth = 18
    private val invHeight = 18

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()
        Keyboard.enableRepeatEvents(true)
        val i: Int = (this.width - this.xSize) / 2
        val j: Int = (this.height - this.ySize) / 2
        this.stayButton = GuiButton(0, i + 120, j + 122, 50, 20, "")
        this.stayButton!!.enabled = this.slimeling.isOwner(this.mc.player)
        this.stayButton!!.displayString =
            if (this.slimeling.isSitting()) GCCoreUtil.translate("gui.slimeling.button.follow") else GCCoreUtil.translate(
                "gui.slimeling.button.sit"
            )
        this.buttonList.add(this.stayButton)
        this.nameField = GuiTextField(0, this.fontRenderer, i + 44, j + 59, 103, 12)
        this.nameField!!.setText(this.slimeling.getName())
        this.nameField!!.setEnableBackgroundDrawing(false)
        this.nameField!!.setMaxStringLength(30)
        this.nameField!!.setFocused(this.slimeling.isOwner(this.mc.player))
        this.nameField!!.setCanLoseFocus(false)
        this.invX = i + 151
        this.invY = j + 76
    }

    override fun updateScreen() {
        if (this.slimeling.isOwner(this.mc.player)) {
            this.nameField!!.updateCursorCounter()
        }
        this.stayButton!!.displayString =
            if (this.slimeling.isSitting()) GCCoreUtil.translate("gui.slimeling.button.follow") else GCCoreUtil.translate(
                "gui.slimeling.button.sit"
            )
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    @Throws(IOException::class)
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (this.slimeling.isOwner(this.mc.player)) {
            if (this.nameField!!.textboxKeyTyped(typedChar, keyCode)) {
                this.slimeling.setName(this.nameField!!.getText())
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA,
                        GCCoreUtil.getDimensionID(this.slimeling.world),
                        arrayOf<Any>(this.slimeling.getEntityId(), 1, this.slimeling.getName())
                    )
                )
            }
        }
        super.keyTyped(typedChar, keyCode)
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.enabled) {
            when (button.id) {
                0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA,
                        GCCoreUtil.getDimensionID(this.slimeling.world),
                        arrayOf<Any>(this.slimeling.getEntityId(), 0, "")
                    )
                )
            }
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (this.slimeling.isOwner(this.mc.player)) {
            this.nameField!!.mouseClicked(mouseX, mouseY, mouseButton)
        }
        if (mouseX >= this.invX && mouseX < this.invX + this.invWidth && mouseY >= this.invY && mouseY < this.invY + this.invHeight) {
            this.mc.getSoundManager().play(PositionedSoundRecord.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimpleMars(
                    PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA,
                    GCCoreUtil.getDimensionID(this.slimeling.world),
                    arrayOf<Any>(this.slimeling.getEntityId(), 6, "")
                )
            )
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val i: Int = (this.width - this.xSize) / 2
        val j: Int = (this.height - this.ySize) / 2

        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 0f, -70.0f)
        drawRect(i, j, i + this.xSize, j + this.ySize - 20, -0x1000000)
        GlStateManager.popMatrix()

        val yOffset: Int = floor(30.0 * (1.0f - this.slimeling.getScale())).toInt()

        drawSlimelingOnGui(
            this.slimeling,
            this.width / 2,
            j + 62 - yOffset,
            70,
            (i + 51 - mouseX).toFloat(),
            (j + 75 - 50 - mouseY).toFloat()
        )

        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 0f, 150.0f)
        this.mc.renderEngine.bindTexture(slimelingPanelGui)
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize)
        this.drawTexturedModalRect(i + this.xSize - 15, j + 9, 176, 0, 9, 9)
        this.drawTexturedModalRect(i + this.xSize - 15, j + 22, 185, 0, 9, 9)
        this.drawTexturedModalRect(i + this.xSize - 15, j + 35, 194, 0, 9, 9)
        var str: String = "" + Math.round(this.slimeling.getColorRed() * 1000) / 10.0f + "% "
        this.drawString(
            this.fontRenderer,
            str,
            i + this.xSize - 15 - this.fontRenderer.getStringWidth(str),
            j + 10,
            ColorUtil.to32BitColor(255, 255, 0, 0)
        )
        str = "" + Math.round(this.slimeling.getColorGreen() * 1000) / 10.0f + "% "
        this.drawString(
            this.fontRenderer,
            str,
            i + this.xSize - 15 - this.fontRenderer.getStringWidth(str),
            j + 23,
            ColorUtil.to32BitColor(255, 0, 255, 0)
        )
        str = "" + Math.round(this.slimeling.getColorBlue() * 1000) / 10.0f + "% "
        this.drawString(
            this.fontRenderer,
            str,
            i + this.xSize - 15 - this.fontRenderer.getStringWidth(str),
            j + 36,
            ColorUtil.to32BitColor(255, 0, 0, 255)
        )

        this.mc.renderEngine.bindTexture(slimelingPanelGui)
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        this.drawTexturedModalRect(this.invX, this.invY, 176, 9, this.invWidth, this.invHeight)

        super.drawScreen(mouseX, mouseY, partialTicks)

        val dX: Int = -45
        val dY: Int = 65
        val startX: Int = -20 + i + 60
        val startY: Int = dY + j - 10
        val width: Int = this.xSize - 60
        val height: Int = 15
        drawRect(startX, startY, startX + width, startY + height, -0x5f5f60)
        drawRect(startX + 1, startY + 1, startX + width - 1, startY + height - 1, -0x1000000)
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.slimeling.name") + ": ",
            dX + i + 55,
            dY + j - 6,
            0x404040
        )
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.slimeling.owner") + ": " + this.slimeling.getOwnerUsername(),
            dX + i + 55,
            dY + j + 7,
            0x404040
        )
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.slimeling.kills") + ": " + this.slimeling.getKillCount(),
            dX + i + 55,
            dY + j + 20,
            0x404040
        )
        this.fontRenderer.drawString(
            GCCoreUtil.translate("gui.slimeling.scale") + ": " + Math.round(this.slimeling.getAge() / this.slimeling.MAX_AGE.toFloat() * 1000.0f) / 10.0f + "%",
            dX + i + 55,
            dY + j + 33,
            0x404040
        )
        str =
            "" + (if (this.slimeling.isSitting()) GCCoreUtil.translate("gui.slimeling.sitting") else GCCoreUtil.translate(
                "gui.slimeling.following"
            ))
        this.fontRenderer.drawString(str, i + 145 - this.fontRenderer.getStringWidth(str) / 2, j + 112, 0x404040)
        str =
            GCCoreUtil.translate("gui.slimeling.damage") + ": " + Math.round(this.slimeling.getDamage() * 100.0f) / 100.0f
        this.fontRenderer.drawString(str, dX + i + 55, dY + j + 33 + 13, 0x404040)
        str = GCCoreUtil.translate("gui.slimeling.food") + ": "
        this.fontRenderer.drawString(str, dX + i + 55, dY + j + 46 + 13, 0x404040)

        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.enableBlend()
        GlStateManager.enableLighting()
        GlStateManager.enableRescaleNormal()
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(
            ItemStack(this.slimeling.getFavoriteFood()),
            dX + i + 55 + this.fontRenderer.getStringWidth(str),
            dY + j + 41 + 14
        )
        GlStateManager.disableLighting()
        GlStateManager.disableBlend()
        this.nameField!!.drawTextBox()
        GlStateManager.popMatrix()
        val foodStack: ItemStack = ItemStack(this.slimeling.getFavoriteFood())

        if (!foodStack.isEmpty() && mouseX >= this.invX - 66 && mouseX < this.invX + this.invWidth - 68 && mouseY >= this.invY + 44 && mouseY < this.invY + this.invHeight + 42) {
            this.renderToolTip(foodStack, mouseX, mouseY)
        }
    }

    companion object {
        private val slimelingPanelGui =
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/slimeling_panel0.png")

        @JvmField
        var renderingOnGui: Boolean = false

        @JvmStatic
        fun drawSlimelingOnGui(slimeling: EntitySlimeling, x: Int, y: Int, scale: Int, mouseX: Float, mouseY: Float) {
            var mouseX: Float = mouseX
            var mouseY: Float = mouseY
            renderingOnGui = true
            GlStateManager.enableColorMaterial()
            GlStateManager.pushMatrix()
            GlStateManager.translate(x.toFloat(), y.toFloat(), 50.0f)
            GlStateManager.scale(-scale / 2.0f, scale / 2.0f, scale / 2.0f)
            GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
            val f2: Float = slimeling.renderYawOffset
            val f3: Float = slimeling.rotationYaw
            val f4: Float = slimeling.rotationPitch
            mouseX += 40f
            mouseY -= 20f
            GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f)
            RenderHelper.enableStandardItemLighting()
            GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f)
            GlStateManager.rotate(-(atan((mouseY / 40.0f).toDouble()).toFloat()) * 20.0f, 1.0f, 0.0f, 0.0f)
            slimeling.renderYawOffset = atan((mouseX / 40.0f).toDouble()).toFloat() * 20.0f
            slimeling.rotationYaw = atan((mouseX / 40.0f).toDouble()).toFloat() * 40.0f
            slimeling.rotationPitch = -(atan((mouseY / 40.0f).toDouble()).toFloat()) * 20.0f
            slimeling.rotationYawHead = slimeling.rotationYaw
            GlStateManager.translate(0.0f, slimeling.getYOffset().toFloat(), 0.0f)
            FMLClientHandler.instance().getClient().getRenderManager().playerViewY = 180.0f
            FMLClientHandler.instance().getClient().getRenderManager()
                .renderEntity(slimeling, 0.0, 0.0, 0.0, 0.0f, 1.0f, false)
            slimeling.renderYawOffset = f2
            slimeling.rotationYaw = f3
            slimeling.rotationPitch = f4
            GlStateManager.popMatrix()
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableRescaleNormal()
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
            GlStateManager.disableTexture2D()
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
            GlStateManager.color(1.0f, 1.0f, 1.0f)
            renderingOnGui = false
        }
    }
}
