package asmodeuscore.core.astronomy.gui.book

import asmodeuscore.core.utils.BookUtils
import asmodeuscore.api.space.IBookPage
import asmodeuscore.core.astronomy.gui.book.ACGuiGuideBook
import org.lwjgl.opengl.GL11
import java.io.IOException
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance as PositionedSoundRecord
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.input.Mouse

open class ACGuiGuideBook : GuiScreen {
    private val bookImageHeight = 250
    private val bookImageWidth = 450
    private var scroll = 0
    private var mode: Mode? = Mode.CATEGORIES
    private var category: String? = BookUtils.Book_Cateroies.GENERAL.getName()
    private var maxX = 0

    constructor()

    constructor(mode: Mode?, category: String?, page: IBookPage) {
        this.mode = mode
        this.category = category
        Companion.page = page
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(bookPageTexture)
        val offsetFromScreenLeft: Int = (this.width - 450) / 2
        val offsetFromScreenTop: Int = (this.height - 250) / 2
        this.drawTexturedModalRect(
            offsetFromScreenLeft.toFloat(),
            offsetFromScreenTop.toFloat(),
            450.0f,
            256.0f,
            0.0f,
            0.0f,
            400.0f,
            256.0f,
            false,
            false,
            512.0f,
            256.0f
        )
        var posY: Int = 0
        if (mouseX >= offsetFromScreenLeft + 25 && mouseX < offsetFromScreenLeft + 58 && mouseY >= offsetFromScreenTop + 18 && mouseY <= offsetFromScreenTop + 32
        ) {
            posY = 17
        }

        if (this.mode != Mode.CATEGORIES) {
            this.drawTexturedModalRect(
                (offsetFromScreenLeft + 25).toFloat(),
                (offsetFromScreenTop + 18).toFloat(),
                30.0f,
                15.0f,
                400.0f,
                posY.toFloat(),
                20.0f,
                15.0f,
                false,
                false,
                512.0f,
                256.0f
            )
            this.drawTexturedModalRect(
                (offsetFromScreenLeft + 40).toFloat(),
                (offsetFromScreenTop + 18).toFloat(),
                30.0f,
                15.0f,
                467.0f,
                posY.toFloat(),
                20.0f,
                15.0f,
                false,
                false,
                512.0f,
                256.0f
            )
            this.fontRenderer.drawString(
                GCCoreUtil.translate("book.button.back.name"),
                offsetFromScreenLeft + 39,
                offsetFromScreenTop + 22,
                16777215
            )
        }

        if (this.mode == Mode.CATEGORIES) {
            var i: Int = 0
            var offsetX: Int = 65
            var offsetY: Int = 0

            for (entry: MutableMap.MutableEntry<String?, ResourceLocation?> in BookUtils.GUIDE_BOOK_CATEGORIES.entries) {
                offsetY = i / 6 * 60
                val k: Int = if (i > i % 6) i - 6 * (i / 6) else i
                offsetX = 65 * k
                this.mc.getTextureManager().bindTexture(bookPageTexture)
                this.drawTexturedModalRect(
                    (offsetFromScreenLeft + 42 + offsetX).toFloat(),
                    (offsetFromScreenTop + 52 + offsetY).toFloat(),
                    36.0f,
                    35.0f,
                    460.0f,
                    34.0f,
                    22.0f,
                    24.0f,
                    false,
                    false,
                    512.0f,
                    256.0f
                )
                this.mc.getTextureManager().bindTexture(entry.value)
                this.drawTexturedModalRect(
                    (offsetFromScreenLeft + 51 + offsetX).toFloat(),
                    (offsetFromScreenTop + 60 + offsetY).toFloat(),
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
                this.fontRenderer
                    .drawSplitString(
                        GCCoreUtil.translate("book.category." + entry.key + ".name"),
                        offsetFromScreenLeft + offsetX + 60 - this.fontRenderer.getStringWidth(entry.key) / 2,
                        offsetFromScreenTop + 85 + offsetY,
                        50,
                        16777215
                    )
                i++
            }
        } else if (this.mode == Mode.PAGES) {
            this.mc.getTextureManager().bindTexture(bookPageTexture)
            val s: String = GCCoreUtil.translate("book.category." + this.category + ".name")
            this.fontRenderer.drawString(
                s,
                this.width / 2 - this.fontRenderer.getStringWidth(s) / 2,
                offsetFromScreenTop + 20,
                16777215
            )
            var getMaxScroll: Int = 0
            this.maxX = 0

            for (page in BookUtils.GUIDE_BOOK_PAGES) {
                if ((page)!!.getCategory() == this.category) {
                    val currX: Int = this.fontRenderer
                        .getStringWidth(
                            if ((page)!!.rawTitle()) GCCoreUtil.translate(((page)!!.titlePage())!!) else GCCoreUtil.translate(
                                "book.page." + (page)!!.titlePage()
                            )
                        )
                    this.maxX = if (this.maxX < currX) currX else this.maxX
                    getMaxScroll++
                }
            }

            getMaxScroll -= 7
            if (getMaxScroll > 8) {
                this.mc.getTextureManager().bindTexture(bookPageTexture)
                this.drawTexturedModalRect(
                    (offsetFromScreenLeft + 417).toFloat(),
                    (offsetFromScreenTop + 36).toFloat(),
                    17.0f,
                    186.0f,
                    492.0f,
                    0.0f,
                    14.0f,
                    185.0f,
                    false,
                    false,
                    512.0f,
                    256.0f
                )
                this.drawTexturedModalRect(
                    (offsetFromScreenLeft + 417).toFloat(),
                    (offsetFromScreenTop + 36).toFloat(),
                    17.0f,
                    28.0f,
                    400.0f,
                    68.0f,
                    15.0f,
                    28.0f,
                    false,
                    false,
                    512.0f,
                    256.0f
                )
                this.drawTexturedModalRect(
                    (offsetFromScreenLeft + 419).toFloat(),
                    (offsetFromScreenTop + 194).toFloat(),
                    16.0f,
                    28.0f,
                    400.0f,
                    68.0f,
                    14.0f,
                    28.0f,
                    true,
                    true,
                    512.0f,
                    256.0f
                )
                val pos: Int = if (getMaxScroll > 0) Math.round((118 / getMaxScroll).toFloat()) else getMaxScroll
                this.drawTexturedModalRect(
                    (offsetFromScreenLeft + 420).toFloat(),
                    (offsetFromScreenTop + 60 + this.scroll * pos).toFloat(),
                    15.0f,
                    28.0f,
                    429.0f,
                    68.0f,
                    14.0f,
                    28.0f,
                    false,
                    false,
                    512.0f,
                    256.0f
                )
            }

            if (Mouse.hasWheel()) {
                val dWheel: Int = Mouse.getDWheel()
                if (dWheel > 0 && this.scroll > 0) {
                    this.scroll--
                } else if (dWheel < 0 && this.scroll < getMaxScroll) {
                    this.scroll++
                }
            }

            var i: Int = 0
            val offsetX: Int = 21

            for (pagex in BookUtils.GUIDE_BOOK_PAGES) {
                if ((pagex)!!.getCategory() == this.category) {
                    if (i >= this.scroll && i <= 7 + this.scroll) {
                        this.mc.getTextureManager().bindTexture(bookPageTexture)
                        var var13: Int = 0
                        if (mouseX >= offsetFromScreenLeft + 44 && mouseX < offsetFromScreenLeft + 40 + 25 + this.maxX && mouseY >= offsetFromScreenTop + 41 + offsetX * (i - this.scroll) && mouseY <= offsetFromScreenTop + 41 + offsetX * (i - this.scroll) + 16) {
                            var13 = 17
                        }

                        this.drawTexturedModalRect(
                            (offsetFromScreenLeft + 40).toFloat(),
                            (offsetFromScreenTop + 41 + offsetX * (i - this.scroll)).toFloat(),
                            25.0f,
                            16.0f,
                            400.0f,
                            var13.toFloat(),
                            20.0f,
                            15.0f,
                            false,
                            false,
                            512.0f,
                            256.0f
                        )
                        this.drawTexturedModalRect(
                            (offsetFromScreenLeft + 40 + 16).toFloat(),
                            (offsetFromScreenTop + 41 + offsetX * (i - this.scroll)).toFloat(),
                            this.maxX.toFloat(),
                            16.0f,
                            410.0f,
                            var13.toFloat(),
                            70.0f,
                            15.0f,
                            false,
                            false,
                            512.0f,
                            256.0f
                        )
                        this.drawTexturedModalRect(
                            (offsetFromScreenLeft + 40 + this.maxX).toFloat(),
                            (offsetFromScreenTop + 41 + offsetX * (i - this.scroll)).toFloat(),
                            25.0f,
                            16.0f,
                            467.0f,
                            var13.toFloat(),
                            20.0f,
                            15.0f,
                            false,
                            false,
                            512.0f,
                            256.0f
                        )
                        if ((pagex)!!.iconTitle() != null) {
                            this.mc.getTextureManager().bindTexture(bookPageTexture)
                            this.drawTexturedModalRect(
                                (offsetFromScreenLeft + 20).toFloat(),
                                (offsetFromScreenTop + 41 + offsetX * (i - this.scroll)).toFloat(),
                                21.0f,
                                21.0f,
                                461.0f,
                                35.0f,
                                18.0f,
                                21.0f,
                                false,
                                false,
                                512.0f,
                                256.0f
                            )
                            this.mc.getTextureManager().bindTexture((pagex)!!.iconTitle())
                            this.drawTexturedModalRect(
                                (offsetFromScreenLeft + 25).toFloat(),
                                (offsetFromScreenTop + 45 + offsetX * (i - this.scroll)).toFloat(),
                                12.0f,
                                12.0f,
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

                        if ((pagex)!!.itemIcon() != ItemStack.EMPTY) {
                            this.mc.getTextureManager().bindTexture(bookPageTexture)
                            this.drawTexturedModalRect(
                                (offsetFromScreenLeft + 20).toFloat(),
                                (offsetFromScreenTop + 41 + offsetX * (i - this.scroll)).toFloat(),
                                21.0f,
                                21.0f,
                                461.0f,
                                35.0f,
                                18.0f,
                                21.0f,
                                false,
                                false,
                                512.0f,
                                256.0f
                            )
                            RenderHelper.enableGUIStandardItemLighting()
                            this.mc
                                .getRenderItem()
                                .renderItemAndEffectIntoGUI(
                                    (pagex)!!.itemIcon(),
                                    offsetFromScreenLeft + 23,
                                    offsetFromScreenTop + 43 + offsetX * (i - this.scroll)
                                )
                            RenderHelper.disableStandardItemLighting()
                        }

                        this.fontRenderer
                            .drawString(
                                if ((pagex)!!.rawTitle()) GCCoreUtil.translate(((pagex)!!.titlePage())!!) else GCCoreUtil.translate(
                                    "book.page." + (pagex)!!.titlePage() + ".name"
                                ),
                                offsetFromScreenLeft + 52,
                                offsetFromScreenTop + 45 + offsetX * (i - this.scroll),
                                16777215
                            )
                        i++
                    } else {
                        i++
                    }
                }
            }

            if (i == 0) {
                this.fontRenderer
                    .drawString(
                        "Whoops! Empty category! We will work on pages from this category!",
                        offsetFromScreenLeft + 52,
                        offsetFromScreenTop + 45 + offsetX * i,
                        16777215
                    )
            }
        } else if (this.mode == Mode.TEXT) {
            var s: String = GCCoreUtil.translate("book.page." + page!!.titlePage() + ".name")
            if (page!!.rawTitle()) {
                s = GCCoreUtil.translate((page!!.titlePage())!!)
            }

            this.fontRenderer.drawString(
                s,
                this.width / 2 - this.fontRenderer.getStringWidth(s) / 2,
                offsetFromScreenTop + 20,
                16777215
            )
            page!!.drawPage(offsetFromScreenLeft, offsetFromScreenTop, this.fontRenderer, mouseX, mouseY)
        }

        this.fontRenderer.drawString(
            EnumColor.ORANGE.toString() + "Book in Beta",
            offsetFromScreenLeft + 370,
            offsetFromScreenTop + 22,
            16777215
        )
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val offsetFromScreenLeft: Int = (this.width - 450) / 2
        val offsetFromScreenTop: Int = (this.height - 250) / 2
        if (this.mode == Mode.CATEGORIES) {
            var i: Int = 0
            var offsetX: Int = 65
            var offsetY: Int = 0

            for (entry: MutableMap.MutableEntry<String?, ResourceLocation?> in BookUtils.GUIDE_BOOK_CATEGORIES.entries) {
                offsetY = i / 6 * 60
                val k: Int = if (i > i % 6) i - 6 * (i / 6) else i
                offsetX = 65 * k
                if (mouseX >= offsetFromScreenLeft + 30 + offsetX && mouseX <= offsetFromScreenLeft + 70 + offsetX + 10 && mouseY >= offsetFromScreenTop + 50 + offsetY && mouseY <= offsetFromScreenTop + 100 + offsetY) {
                    this.category = entry.key
                    this.mode = Mode.PAGES
                    Minecraft.getInstance().getSoundManager().play(PositionedSoundRecord.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                }

                i++
            }
        } else if (this.mode == Mode.PAGES) {
            var i: Int = 0
            val offsetX: Int = 21

            for (entry in BookUtils.GUIDE_BOOK_PAGES) {
                if ((entry)!!.getCategory() == this.category) {
                    if (i >= this.scroll && i <= 7 + this.scroll) {
                        if (mouseX >= offsetFromScreenLeft + 44 && mouseX < offsetFromScreenLeft + 40 + 25 + this.maxX && mouseY >= offsetFromScreenTop + 40 + offsetX * (i - this.scroll) && mouseY <= offsetFromScreenTop + 56 + offsetX * (i - this.scroll)) {
                            try {
                                page = (entry)!!.javaClass.newInstance() as IBookPage
                            } catch (var12: IllegalAccessException) {
                                var12.printStackTrace()
                            } catch (var12: InstantiationException) {
                                var12.printStackTrace()
                            }

                            this.mode = Mode.TEXT
                            Minecraft.getInstance().getSoundManager().play(PositionedSoundRecord.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                        }

                        i++
                    } else {
                        i++
                    }
                }
            }

            if (mouseX >= offsetFromScreenLeft + 25 && mouseX < offsetFromScreenLeft + 58 && mouseY >= offsetFromScreenTop + 18 && mouseY <= offsetFromScreenTop + 32) {
                this.mode = Mode.CATEGORIES
                this.scroll = 0
                Minecraft.getInstance().getSoundManager().play(PositionedSoundRecord.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            }
        } else if (this.mode == Mode.TEXT) {
            page!!.mouseClick(mouseX, mouseY, mouseButton, offsetFromScreenLeft, offsetFromScreenTop)
            if (mouseX >= offsetFromScreenLeft + 25 && mouseX < offsetFromScreenLeft + 58 && mouseY >= offsetFromScreenTop + 18 && mouseY <= offsetFromScreenTop + 32) {
                if (!page!!.hookBackButton()) {
                    this.mode = Mode.PAGES
                }

                Minecraft.getInstance().getSoundManager().play(PositionedSoundRecord.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            }
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
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
            512.0f,
            512.0f
        )
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
        val texModX: Float = 1.0f / texSizeX
        val texModY: Float = 1.0f / texSizeY
        val tessellator: Tessellator = Tessellator.getInstance()
        val vertexbuffer: BufferBuilder = tessellator.getBuffer()
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX)
        val height0: Float = if (invertY) 0.0f else vHeight
        val height1: Float = if (invertY) vHeight else 0.0f
        val width0: Float = if (invertX) uWidth else 0.0f
        val width1: Float = if (invertX) 0.0f else uWidth
        vertexbuffer.pos(x.toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height0) * texModY).toDouble())
            .endVertex()
        vertexbuffer.pos((x + width).toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height0) * texModY).toDouble())
            .endVertex()
        vertexbuffer.pos((x + width).toDouble(), y.toDouble(), this.zLevel.toDouble())
            .tex(((u + width1) * texModX).toDouble(), ((v + height1) * texModY).toDouble())
            .endVertex()
        vertexbuffer.pos(x.toDouble(), y.toDouble(), this.zLevel.toDouble())
            .tex(((u + width0) * texModX).toDouble(), ((v + height1) * texModY).toDouble()).endVertex()
        tessellator.draw()
    }

    enum class Mode {
        CATEGORIES,
        PAGES,
        TEXT
    }

    companion object {
        private val bookPageTexture = ResourceLocation.fromNamespaceAndPath("asmodeuscore", "textures/gui/tablet.png")
        private var page: IBookPage? = null
    }
}
