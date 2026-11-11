package galaxyspace.core.client.gui

import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Random
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiConfirmOpenLink
import net.minecraft.client.gui.GuiLanguage
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.client.gui.GuiWorldSelection
import net.minecraft.client.gui.GuiYesNo
import net.minecraft.client.gui.GuiYesNoCallback

import asmodeuscore.core.utils.Utils
import com.google.common.base.Strings
import com.google.common.collect.Lists
import galaxyspace.GalaxySpace
import galaxyspace.core.util.GSThreadVersionCheck
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.I18n
import net.minecraft.realms.RealmsBridge
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.world.storage.ISaveFormat
import net.minecraft.world.storage.WorldInfo
import net.minecraftforge.fml.client.GuiModList
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.apache.commons.io.Charsets
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GLContext
import org.lwjgl.util.glu.Project
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.util.*
import kotlin.math.max

@OnlyIn(Dist.CLIENT)
open class GSGuiMainMenu constructor() : GuiScreen(), GuiYesNoCallback {
    /** Counts the number of screen updates.  */
    private val updateCounter: Float

    /** The splash message.  */
    private var splashText = "missingno"
    private var buttonResetDemo: GuiButton? = null

    /** Timer used to rotate the panorama, increases every tick.  */
    private var panoramaTimer = 0f

    /** Texture allocated for the current viewport of the main menu's panorama background.  */
    private var viewportTexture: DynamicTexture? = null
    private val theardLock = Any()
    private var field_92025_p: String?
    private var field_146972_A: String
    private var field_104024_v: String? = null

    private var field_92024_r = 0
    private var field_92023_s = 0
    private var field_92022_t = 0
    private var field_92021_u = 0
    private var field_92020_v = 0
    private var field_92019_w = 0
    private var backgroundTexture: ResourceLocation? = null

    //private int number;
    init {
        this.field_146972_A = field_96138_a
        var bufferedreader: BufferedReader? = null
        //number = rand.nextInt(3);
        try {
            val arraylist: ArrayList<String?> = ArrayList<String?>()
            bufferedreader = BufferedReader(
                InputStreamReader(
                    Minecraft.getInstance().getResourceManager().getResource(splashTexts).getInputStream(),
                    Charsets.UTF_8
                )
            )
            var s: String?

            while ((bufferedreader.readLine().also { s = it }) != null) {
                s = s!!.trim { it <= ' ' }

                if (!(s)!!.isEmpty()) {
                    arraylist.add(s)
                }
            }

            if (!arraylist.isEmpty()) {
                do {
                    this.splashText = arraylist.get(rand.nextInt(arraylist.size)) as String
                } while (this.splashText.hashCode() == 125780783)
            }
        } catch (ioexception1: IOException) {
        } finally {
            if (bufferedreader != null) {
                try {
                    bufferedreader.close()
                } catch (ioexception: IOException) {
                }
            }
        }

        this.updateCounter = rand.nextFloat()
        this.field_92025_p = ""

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.field_92025_p = I18n.format("title.oldgl1", *arrayOfNulls<Any>(0))
            this.field_146972_A = I18n.format("title.oldgl2", *arrayOfNulls<Any>(0))
            this.field_104024_v = "https://help.mojang.com/customer/portal/articles/325948?ref=game"
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    override fun keyTyped(p_73869_1_: Char, p_73869_2_: Int) {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    override fun initGui() {
        this.viewportTexture = DynamicTexture(256, 256)
        this.backgroundTexture =
            this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture)
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTime(Date())

        if (calendar.get(2) + 1 == 11 && calendar.get(5) == 9) {
            this.splashText = "Happy birthday, ez!"
        } else if (calendar.get(2) + 1 == 6 && calendar.get(5) == 1) {
            this.splashText = "Happy birthday, Notch!"
        } else if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
            this.splashText = "Merry X-mas!"
        } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
            this.splashText = "Happy new year!"
        } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!"
        }

        //boolean flag = true;
        val i: Int = this.height / 2 - 118

        if (this.mc.isDemo()) {
            this.addDemoButtons(i, 24)
        } else {
            this.addSingleplayerMultiplayerButtons(i, 24)
        }


        val l: Int
        l = this.width - 150

        this.buttonList.add(
            GSGuiButton(
                0,
                l,
                i + 71 + 24 * 3,
                100,
                20,
                I18n.format("menu.options", *arrayOfNulls<Any>(0))
            )
        )
        this.buttonList.add(
            GSGuiButton(
                4,
                l,
                i + 71 + 24 * 4,
                100,
                20,
                I18n.format("menu.quit", *arrayOfNulls<Any>(0))
            )
        )


        //this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, i + 72 + 12));
        //Object object = this.theardLock;
        synchronized(this.theardLock) {
            this.field_92023_s = this.fontRenderer.getStringWidth(this.field_92025_p)
            this.field_92024_r = this.fontRenderer.getStringWidth(this.field_146972_A)
            val j: Int = max(this.field_92023_s, this.field_92024_r)
            this.field_92022_t = (this.width - j) / 2
            this.field_92021_u = (this.buttonList.get(0) as GuiButton).y - 24
            this.field_92020_v = this.field_92022_t + j
            this.field_92019_w = this.field_92021_u + 24
        }
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */
    private fun addSingleplayerMultiplayerButtons(p_73969_1_: Int, p_73969_2_: Int) {
        val i: Int
        i = this.width - 150
        this.buttonList.add(
            GSGuiButton(
                1,
                i,
                (p_73969_1_ + p_73969_2_) + 50,
                100,
                20,
                I18n.format("menu.singleplayer", *arrayOfNulls<Any>(0))
            )
        )
        this.buttonList.add(
            GSGuiButton(
                2,
                i,
                (p_73969_1_ + p_73969_2_ * 2) + 49,
                100,
                20,
                I18n.format("menu.multiplayer", *arrayOfNulls<Any>(0))
            )
        )
        if (GSThreadVersionCheck.newversion) this.buttonList.add(
            GSGuiButton(
                3,
                this.width / 2 - 65,
                80,
                150,
                20,
                I18n.format("menu.update", *arrayOfNulls<Any>(0))
            )
        )
        val realmsButton: GuiButton =
            GuiButton(14, i, p_73969_1_ + p_73969_2_ * 2, I18n.format("menu.online", *arrayOfNulls<Any>(0)))
        val j: Int = 5
        val fmlModButton: GuiButton = GSGuiButton(6, i, p_73969_1_ + p_73969_2_ * j, 100, 20, "Mods")
        realmsButton.width = 98
        //this.buttonList.add(realmsButton);
        this.buttonList.add(fmlModButton)

        this.buttonList.add(
            GSGuiButton(
                7,
                this.width - 65,
                this.height - 25,
                60,
                20,
                I18n.format("VK Group", *arrayOfNulls<Any>(0))
            )
        )
        this.buttonList.add(
            GSGuiButton(
                8,
                this.width - 65,
                this.height - 48,
                60,
                20,
                I18n.format("Discord", *arrayOfNulls<Any>(0))
            )
        )
        //this.buttonList.add(new GSGuiButton(9, this.width - 70, this.height - 22, 60, 20, I18n.format("GC Forum", new Object[0])));
    }

    /**
     * Adds Demo buttons on Main Menu for players who are playing Demo.
     */
    private fun addDemoButtons(p_73972_1_: Int, p_73972_2_: Int) {
        this.buttonList.add(
            GuiButton(
                11,
                this.width / 2 - 100,
                p_73972_1_,
                I18n.format("menu.playdemo", *arrayOfNulls<Any>(0))
            )
        )
        this.buttonList.add(
            GuiButton(
                12,
                this.width / 2 - 100,
                p_73972_1_ + p_73972_2_ * 1,
                I18n.format("menu.resetdemo", *arrayOfNulls<Any>(0))
            ).also { this.buttonResetDemo = it })
        val isaveformat: ISaveFormat = this.mc.getSaveLoader()
        val worldinfo: WorldInfo? = isaveformat.getWorldInfo("Demo_World")

        if (worldinfo == null) {
            this.buttonResetDemo!!.enabled = false
        }
    }

    override fun actionPerformed(p_146284_1_: GuiButton) {
        if (p_146284_1_.id == 0) {
            this.mc.displayGuiScreen(GuiOptions(this, this.mc.gameSettings))
        }

        if (p_146284_1_.id == 3) {
            this.urlopen("https://www.curseforge.com/minecraft/mc-mods/galaxy-space-addon-for-galacticraft")
        }

        if (p_146284_1_.id == 5) {
            this.mc.displayGuiScreen(GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()))
        }

        if (p_146284_1_.id == 1) {
            this.mc.displayGuiScreen(GuiWorldSelection(this))
        }

        if (p_146284_1_.id == 2) {
            this.mc.displayGuiScreen(GuiMultiplayer(this))
            //FMLClientHandler.instance().connectToServer(new GuiMultiplayer(this), new ServerData("SpaceTechnology", "46.174.49.40:25681"));
        }

        if (p_146284_1_.id == 14) {
            this.func_140005_i()
        }

        if (p_146284_1_.id == 4) {
            this.mc.shutdown()
        }

        if (p_146284_1_.id == 6) {
            this.mc.displayGuiScreen(GuiModList(this))
        }

        /*
        if (p_146284_1_.id == 11)
        {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.DEMO_WORLD_SETTINGS);
        }*/
        if (p_146284_1_.id == 12) {
            val isaveformat: ISaveFormat = this.mc.getSaveLoader()
            val worldinfo: WorldInfo? = isaveformat.getWorldInfo("Demo_World")

            if (worldinfo != null) {
                this.mc.displayGuiScreen(
                    GuiYesNo(
                        this,
                        I18n.format("selectWorld.deleteQuestion", *arrayOfNulls<Any>(0)),
                        "\'" + worldinfo.getWorldName() + "\' " + I18n.format(
                            "selectWorld.deleteWarning",
                            *arrayOfNulls<Any>(0)
                        ),
                        I18n.format("selectWorld.deleteButton", *arrayOfNulls<Any>(0)),
                        I18n.format("gui.cancel", *arrayOfNulls<Any>(0)),
                        12
                    )
                )
            }
        }

        if (p_146284_1_.id == 7) {
            this.urlopen("https://vk.com/addon.galaxyspace")
        }

        if (p_146284_1_.id == 8) {
            this.urlopen("https://discordapp.com/invite/8EHZdFZ")
        }

        if (p_146284_1_.id == 9) {
            this.urlopen("https://forum.micdoodle8.com/index.php?threads/1-7-10-galaxy-space-stable.5298/")
        }
    }

    private fun urlopen(url: String) {
        try {
            val oclass: Class<*> = Class.forName("java.awt.Desktop")
            val `object`: Any? =
                oclass.getMethod("getDesktop", *arrayOfNulls<Class<*>>(0)).invoke(null as Any?, *arrayOfNulls<Any>(0))
            oclass.getMethod("browse", *arrayOf<Class<*>>(URI::class.java)).invoke(`object`, *arrayOf<Any>(URI(url)))
        } catch (throwable: Throwable) {
            logger.error("Couldn\'t open link", throwable)
        }
    }

    private fun func_140005_i() {
        val realmsbridge: RealmsBridge = RealmsBridge()
        realmsbridge.switchToRealms(this)
    }

    override fun confirmClicked(p_73878_1_: Boolean, p_73878_2_: Int) {
        if (p_73878_1_ && p_73878_2_ == 12) {
            val isaveformat: ISaveFormat = this.mc.getSaveLoader()
            isaveformat.flushCache()
            isaveformat.deleteWorldDirectory("Demo_World")
            this.mc.displayGuiScreen(this)
        } else if (p_73878_2_ == 13) {
            if (p_73878_1_) {
                try {
                    val oclass: Class<*> = Class.forName("java.awt.Desktop")
                    val `object`: Any? = oclass.getMethod("getDesktop", *arrayOfNulls<Class<*>>(0))
                        .invoke(null as Any?, *arrayOfNulls<Any>(0))
                    oclass.getMethod("browse", *arrayOf<Class<*>>(URI::class.java))
                        .invoke(`object`, *arrayOf<Any>(URI(this.field_104024_v)))
                } catch (throwable: Throwable) {
                    logger.error("Couldn\'t open link", throwable)
                }
            }

            this.mc.displayGuiScreen(this)
        }
    }

    /**
     * Draws the main menu panorama
     */
    private fun drawPanorama(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        GlStateManager.matrixMode(5889)
        GlStateManager.pushMatrix()
        GlStateManager.loadIdentity()
        Project.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f)
        GlStateManager.matrixMode(5888)
        GlStateManager.pushMatrix()
        GlStateManager.loadIdentity()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.disableCull()
        GlStateManager.depthMask(false)
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        val i: Int = 8

        for (j: Int in 0..63) {
            GlStateManager.pushMatrix()
            val f: Float = ((j % 8).toFloat() / 8.0f - 0.5f) / 64.0f
            val f1: Float = ((j / 8).toFloat() / 8.0f - 0.5f) / 64.0f
            val f2: Float = 0.0f
            GlStateManager.translate(f, f1, 0.0f)
            GlStateManager.rotate(MathHelper.sin(this.panoramaTimer / 400.0f) * 25.0f + 20.0f, 1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(-this.panoramaTimer * 0.1f, 0.0f, 1.0f, 0.0f)

            for (k: Int in 0..5) {
                GlStateManager.pushMatrix()

                if (k == 1) {
                    GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f)
                }

                if (k == 2) {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
                }

                if (k == 3) {
                    GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f)
                }

                if (k == 4) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f)
                }

                if (k == 5) {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
                }

                this.mc.getTextureManager().bindTexture(PANORAMA_PATHS[k])
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
                val l: Int = 255 / (j + 1)
                bufferbuilder.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, l).endVertex()
                bufferbuilder.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, l).endVertex()
                bufferbuilder.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, l).endVertex()
                bufferbuilder.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, l).endVertex()
                tessellator.draw()
                GlStateManager.popMatrix()
            }

            GlStateManager.popMatrix()
            GlStateManager.colorMask(true, true, true, false)
        }

        bufferbuilder.setTranslation(0.0, 0.0, 0.0)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.matrixMode(5889)
        GlStateManager.popMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.popMatrix()
        GlStateManager.depthMask(true)
        GlStateManager.enableCull()
        GlStateManager.enableDepth()
    }

    /**
     * Rotate and blurs the skybox view in the main menu
     */
    private fun rotateAndBlurSkybox() {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture)
        GlStateManager.glTexParameteri(3553, 10241, 9729)
        GlStateManager.glTexParameteri(3553, 10240, 9729)
        GlStateManager.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        GlStateManager.colorMask(true, true, true, false)
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        GlStateManager.disableAlpha()
        val i: Int = 3

        for (j: Int in 0..<i) {
            val f: Float = 1.0f / (j + 1).toFloat()
            val k: Int = this.width
            val l: Int = this.height
            val f1: Float = (j - 1).toFloat() / 256.0f
            bufferbuilder.pos(k.toDouble(), l.toDouble(), this.zLevel.toDouble()).tex((0.0f + f1).toDouble(), 1.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
            bufferbuilder.pos(k.toDouble(), 0.0, this.zLevel.toDouble()).tex((1.0f + f1).toDouble(), 1.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
            bufferbuilder.pos(0.0, 0.0, this.zLevel.toDouble()).tex((1.0f + f1).toDouble(), 0.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
            bufferbuilder.pos(0.0, l.toDouble(), this.zLevel.toDouble()).tex((0.0f + f1).toDouble(), 0.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
        }

        tessellator.draw()
        GlStateManager.enableAlpha()
        GlStateManager.colorMask(true, true, true, true)
    }

    /**
     * Renders the skybox in the main menu
     */
    private fun renderSkybox(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.mc.getFramebuffer().unbindFramebuffer()
        GlStateManager.viewport(0, 0, 256, 256)
        this.drawPanorama(mouseX, mouseY, partialTicks)
        this.rotateAndBlurSkybox()
        this.rotateAndBlurSkybox()
        this.rotateAndBlurSkybox()

        this.mc.getFramebuffer().bindFramebuffer(true)
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight)
        val f: Float = 120.0f / (if (this.width > this.height) this.width else this.height).toFloat()
        val f1: Float = this.height.toFloat() * f / 256.0f
        val f2: Float = this.width.toFloat() * f / 256.0f
        val i: Int = this.width
        val j: Int = this.height
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        bufferbuilder.pos(0.0, j.toDouble(), this.zLevel.toDouble()).tex((0.5f - f1).toDouble(), (0.5f + f2).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        bufferbuilder.pos(i.toDouble(), j.toDouble(), this.zLevel.toDouble())
            .tex((0.5f - f1).toDouble(), (0.5f - f2).toDouble()).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        bufferbuilder.pos(i.toDouble(), 0.0, this.zLevel.toDouble()).tex((0.5f + f1).toDouble(), (0.5f - f2).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        bufferbuilder.pos(0.0, 0.0, this.zLevel.toDouble()).tex((0.5f + f1).toDouble(), (0.5f + f2).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        tessellator.draw()
    }

    /**
     * Draws the screen and all the components in it.
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.panoramaTimer += partialTicks

        GlStateManager.disableAlpha()
        this.renderSkybox(mouseX, mouseY, partialTicks)
        GlStateManager.enableAlpha()

        /*
        Calendar calendar = Calendar.getInstance();
        
        if (calendar.get(2) + 1 == 10 && calendar.get(5) >= 30 && calendar.get(5) <= 31 || calendar.get(2) + 1 == 11 && calendar.get(5) <= 1)
        {
        	mc.renderEngine.bindTexture(background[3]);
        }
        else mc.renderEngine.bindTexture(background[number]);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(0 + 0, 0 + height, zLevel).tex(0,1).endVertex();
        vertexbuffer.pos(0 + width, 0 + height, zLevel).tex(1, 1).endVertex();
        vertexbuffer.pos(0 + width, 0 + 0, zLevel).tex(1,0).endVertex();
        vertexbuffer.pos(0 + 0, 0 + 0, zLevel).tex(0, 0).endVertex();
		tessellator.draw();
		*/
        //rotateAndBlurSkybox();
        val short1: Short = 274
        val k: Int = this.width / 2 - short1 / 2
        val b0: Byte = 30
        //this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        //this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        this.mc.getTextureManager().bindTexture(minecraftTitleTextures)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        if (this.updateCounter.toDouble() < 1.0E-4) {
            this.drawTexturedModalRect(k + 0, b0 + 0, 0, 0, 99, 44)
            this.drawTexturedModalRect(k + 99, b0 + 0, 129, 0, 27, 44)
            this.drawTexturedModalRect(k + 99 + 26, b0 + 0, 126, 0, 3, 44)
            this.drawTexturedModalRect(k + 99 + 26 + 3, b0 + 0, 99, 0, 26, 44)
            this.drawTexturedModalRect(k + 155, b0 + 0, 0, 45, 155, 44)
        } else {
            this.drawTexturedModalRect(k + 0, b0 + 0, 0, 0, 155, 44)
            this.drawTexturedModalRect(k + 155, b0 + 0, 0, 45, 155, 44)
        }


        this.mc.getTextureManager().bindTexture(edition)
        drawModalRectWithCustomSizedTexture(k + 88, 67, 0.0f, 0.0f, 98, 14, 128.0f, 16.0f)

        val brandings: MutableList<String?> = Lists.reverse<String?>(FMLCommonHandler.instance().getBrandings(true))

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glColor4f(0.0f, 0.6f, 1.0f, 0.8f)
        this.mc.getTextureManager().bindTexture(mainmenu1)
        this.drawTexturedModalRect(
            (this.width - 181).toFloat(),
            (this.height / 2 - 110).toFloat(),
            189f,
            224f,
            299f,
            60f,
            222f,
            374f,
            false,
            false,
            512f,
            512f
        )


        //Shadow
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1f)
        this.drawTexturedModalRect(
            (this.width - 59).toFloat(),
            (this.height / 2 - 87).toFloat(),
            290f,
            50f,
            440f,
            5f,
            290f,
            50f,
            false,
            false,
            512f,
            512f
        )
        this.drawTexturedModalRect(
            (this.width - 188).toFloat(),
            (this.height / 2 - 87).toFloat(),
            180f,
            50f,
            250f,
            5f,
            180f,
            50f,
            false,
            false,
            512f,
            512f
        )

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        this.drawTexturedModalRect(
            (this.width - 191).toFloat(),
            (this.height / 2 - 90).toFloat(),
            180f,
            50f,
            250f,
            5f,
            180f,
            50f,
            false,
            false,
            512f,
            512f
        )

        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1f)
        this.drawTexturedModalRect(
            (this.width - 61).toFloat(),
            (this.height / 2 - 90).toFloat(),
            290f,
            50f,
            440f,
            5f,
            290f,
            50f,
            false,
            false,
            512f,
            512f
        )

        GL11.glColor4f(0.0f, 0.6f, 1.0f, 0.8f)

        //this.drawTexturedModalRect(-10, this.height - 20 * brandings.size(), 220, 112, 299, 60, 222, 374 / 2 - 50, true, false, 512, 512);
        this.drawTexturedModalRect(
            (this.width - 220).toFloat(),
            (this.height - 55).toFloat(),
            194f,
            (224 / 2 - 30).toFloat(),
            299f,
            60f,
            222f,
            (374 / 2 - 50).toFloat(),
            false,
            false,
            512f,
            512f
        )
        this.drawTexturedModalRect(
            (this.width - 34).toFloat(),
            (this.height - 73).toFloat(),
            35f,
            (224 / 2 - 30).toFloat(),
            359f,
            60f,
            60f,
            (374 / 2 - 50).toFloat(),
            false,
            false,
            512f,
            512f
        )


        //this.drawTexturedModalRect(-20, this.height - 225, 119, 84, 299, 60, 122, 114, true, false, 512, 512);
        //this.drawTexturedModalRect(-20, this.height - 141, 119, 54, 299, 330, 122, 84, true, false, 512, 512);
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()

        GL11.glPushMatrix()
        GL11.glTranslatef((this.width / 2 + 90).toFloat(), 70.0f, 0.0f)
        GL11.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f)
        var f1: Float =
            1.8f - MathHelper.abs(MathHelper.sin((Minecraft.getSystemTime() % 1000L).toFloat() / 1000.0f * Math.PI.toFloat() * 2.0f) * 0.1f)
        f1 = f1 * 100.0f / (this.fontRenderer.getStringWidth(this.splashText) + 32).toFloat()
        GL11.glScalef(f1, f1, f1)
        // this.drawCenteredString(this.fontRendererObj, this.splashText, 0, -8, -512);
        GL11.glPopMatrix()

        val s1: String = "Copyright Mojang AB. Do not distribute!"
        val brandings_new: MutableList<String?> = ArrayList<String?>()
        brandings_new.add(brandings.get(0))
        brandings_new.add(brandings.get(1))
        if (brandings.size > 3) brandings_new.add(brandings.get(3) + " | " + brandings.get(2))
        else brandings_new.add(brandings.get(2))
        for (i: Int in brandings_new.indices) {
            val brd: String = brandings_new.get(i)!!
            if (!Strings.isNullOrEmpty(brd)) {
                this.drawString(
                    this.fontRenderer,
                    brd,
                    this.width - 185,
                    this.height - (10 + i * (this.fontRenderer.FONT_HEIGHT + 1)),
                    16777215
                )
            }
        }

        //ForgeHooksClient.renderMainMenu(this, fontRendererObj, width, height);
        this.drawString(this.fontRenderer, s1, 2, this.height - 10, -1)

        this.drawString(
            this.fontRenderer,
            "Ver. " + GalaxySpace.Companion.VERSION,
            this.width - this.fontRenderer.getStringWidth(GalaxySpace.Companion.VERSION) - 35,
            this.height / 2 - 53,
            -1
        )

        this.drawString(
            this.fontRenderer,
            "AsmodeusCore",
            this.width - 130,
            this.height - 56,
            Utils.getIntColorWHC(0, 250, 200, 255)
        )
        var version: String = "0.0.0"
        for (mod: ModContainer in Loader.instance().getModList()) {
            if (mod.getName().contains("AsmodeusCore")) {
                version = mod.getVersion()
                break
            }
        }
        this.drawString(
            this.fontRenderer,
            "Ver. " + version,
            this.width - version.length * 5 - 100,
            this.height - 46,
            -1
        )

        if (this.field_92025_p != null && this.field_92025_p!!.length > 0) {
            drawRect(
                this.field_92022_t - 2,
                this.field_92021_u - 2,
                this.field_92020_v + 2,
                this.field_92019_w - 1,
                1428160512
            )
            this.drawString(this.fontRenderer, this.field_92025_p, this.field_92022_t, this.field_92021_u, -1)
            this.drawString(
                this.fontRenderer,
                this.field_146972_A,
                (this.width - this.field_92024_r) / 2,
                (this.buttonList.get(0) as GuiButton).y - 12,
                -1
            )
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    /**
     * Called when the mouse is clicked.
     */
    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        //Object object = this.theardLock;
        synchronized(this.theardLock) {
            if (this.field_92025_p!!.length > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
                val guiconfirmopenlink: GuiConfirmOpenLink = GuiConfirmOpenLink(this, this.field_104024_v, 13, true)
                guiconfirmopenlink.disableSecurityWarning()
                this.mc.displayGuiScreen(guiconfirmopenlink)
            }
        }
        /*
        if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height)
        {
            this.mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
        } */
    }


    open fun drawTexturedModalRect(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        u: Float,
        v: Float,
        uWidth: Float,
        vHeight: Float,
        invertX: Boolean,
        invertY: Boolean,
        texSizeX: Float,
        texSizeY: Float
    ) {
        // GL11.glShadeModel(GL11.GL_FLAT);
        // GL11.glEnable(GL11.GL_BLEND);
        //GL11.glEnable(GL11.GL_ALPHA_TEST);
        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        val texModX: Float = 1f / texSizeX
        val texModY: Float = 1f / texSizeY
        val tessellator: Tessellator = Tessellator.getInstance()
        val vertexbuffer: BufferBuilder = tessellator.getBuffer()
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX)
        val height0: Float = if (invertY) 0f else vHeight
        val height1: Float = if (invertY) vHeight else 0f
        val width0: Float = if (invertX) uWidth else 0f
        val width1: Float = if (invertX) 0f else uWidth
        vertexbuffer.pos(x.toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        vertexbuffer.pos((x + width).toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height0) * texModY).toDouble()).endVertex()
        vertexbuffer.pos((x + width).toDouble(), y.toDouble(), this.zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        vertexbuffer.pos(x.toDouble(), y.toDouble(), this.zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        tessellator.draw()
    }

    companion object {
        private val logger: Logger = LogManager.getLogger()

        /** The RNG used by the Main Menu Screen.  */
        private val rand = Random()
        private val splashTexts = ResourceLocation.parse("texts/splashes.txt")
        private val minecraftTitleTextures = ResourceLocation.parse("textures/gui/title/minecraft.png")
        private val mainmenu1 = ResourceLocation.parse(GalaxySpace.Companion.ASSET_PREFIX + ":" + "textures/gui/guimenu.png")
        private val edition = ResourceLocation.parse("textures/gui/title/edition.png")

        /** An array of all the paths to the panorama pictures.  */
        private val background: Array<ResourceLocation?> = ((arrayOf<ResourceLocation>(
            ResourceLocation.parse(GalaxySpace.Companion.ASSET_PREFIX + ":" + "textures/gui/backgrounds/bg1.png"),
            ResourceLocation.parse(GalaxySpace.Companion.ASSET_PREFIX + ":" + "textures/gui/backgrounds/bg2.png"),
            ResourceLocation.parse(GalaxySpace.Companion.ASSET_PREFIX + ":" + "textures/gui/backgrounds/bg3.png"),
            ResourceLocation.parse(GalaxySpace.Companion.ASSET_PREFIX + ":" + "textures/gui/backgrounds/bgH.png")
        ) as Array<ResourceLocation?>?))!!

        private val PANORAMA_PATHS: Array<ResourceLocation?> = ((arrayOf<ResourceLocation>(
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/gui/backgrounds/panorama/panorama_0.png"),
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/gui/backgrounds/panorama/panorama_1.png"),
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/gui/backgrounds/panorama/panorama_2.png"),
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/gui/backgrounds/panorama/panorama_3.png"),
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/gui/backgrounds/panorama/panorama_4.png"),
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/gui/backgrounds/panorama/panorama_5.png")
        ) as Array<ResourceLocation?>?))!!


        @JvmField
        val field_96138_a: String =
            "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information."
    }
}
