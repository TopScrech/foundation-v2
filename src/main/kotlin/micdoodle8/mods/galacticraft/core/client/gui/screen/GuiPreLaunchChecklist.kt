package micdoodle8.mods.galacticraft.core.client.gui.screen

import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckboxPreLaunch
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class GuiPreLaunchChecklist constructor(
    private val checklistKeys: MutableMap<String?, MutableList<String?>?>,
    tagCompound: NBTTagCompound?
) : GuiScreen(), GuiElementCheckboxPreLaunch.ICheckBoxCallback {
    private val bookImageWidth = 192
    private val bookImageHeight = 192
    private var currPage = 0
    private var bookTotalPages = 0
    private var buttonNextPage: NextPageButton? = null
    private var buttonPreviousPage: NextPageButton? = null
    private val tagCompound: NBTTagCompound
    private val checkboxToKeyMap: MutableMap<Int, String?> = Maps.newHashMap<Int, String?>()

    init {
        this.tagCompound = if (tagCompound != null) tagCompound else NBTTagCompound()
    }

    override fun initGui() {
        this.buttonList.clear()
        this.checkboxToKeyMap.clear()

        val i: Int = (this.width - this.bookImageWidth) / 2
        val j: Int = 2
        this.buttonList.add(NextPageButton(0, i + 120, j + 154, true).also { this.buttonNextPage = it })
        this.buttonList.add(NextPageButton(1, i + 38, j + 154, false).also { this.buttonPreviousPage = it })

        var yPos: Int = 25
        var index: Int = 2
        var page: Int = 0

        for (e in this.checklistKeys.entries) {
            val title: String = e.key!!
            val checkboxes: MutableList<String?> = (e.value)!!
            var element: GuiElementCheckboxPreLaunch =
                GuiElementCheckboxPreLaunch(index, this, this.width / 2 - 73 + 11, yPos, GCCoreUtil.translate(title), 0)
            var size: Int = element.willFit(152 - yPos)
            if (size >= 0) {
                if (page == this.currPage) {
                    this.buttonList.add(element)
                    this.checkboxToKeyMap.put(element.id, title)
                    index++
                }
                yPos += size + mc.fontRenderer.FONT_HEIGHT / 2
            } else {
                page++
                yPos = 25
                size = element.willFit(152 - yPos)
                element = GuiElementCheckboxPreLaunch(
                    index,
                    this,
                    this.width / 2 - 73 + 11,
                    yPos,
                    GCCoreUtil.translate(title),
                    0
                )

                if (page == this.currPage) {
                    this.buttonList.add(element)
                    this.checkboxToKeyMap.put(element.id, title)
                    index++
                }
                yPos += size + mc.fontRenderer.FONT_HEIGHT / 2
            }

            for (checkbox: String? in checkboxes) {
                element = GuiElementCheckboxPreLaunch(
                    index,
                    this,
                    this.width / 2 - 73 + 16,
                    yPos,
                    GCCoreUtil.translate("checklist." + checkbox + ".key"),
                    0
                )
                size = element.willFit(152 - yPos)
                if (size >= 0) {
                    if (page == this.currPage) {
                        this.buttonList.add(element)
                        this.checkboxToKeyMap.put(element.id, title + "." + checkbox + ".key")
                        index++
                    }
                    yPos += size + mc.fontRenderer.FONT_HEIGHT / 2
                } else {
                    page++
                    yPos = 25
                    size = element.willFit(152 - yPos)
                    element = GuiElementCheckboxPreLaunch(
                        index,
                        this,
                        this.width / 2 - 73 + 16,
                        yPos,
                        GCCoreUtil.translate("checklist." + checkbox + ".key"),
                        0
                    )

                    if (page == this.currPage) {
                        this.buttonList.add(element)
                        this.checkboxToKeyMap.put(element.id, title + "." + checkbox + ".key")
                        index++
                    }
                    yPos += size + mc.fontRenderer.FONT_HEIGHT / 2
                }
            }
        }

        bookTotalPages = page + 1
        this.updateButtons()
    }

    private fun updateButtons() {
        this.buttonNextPage!!.visible = this.currPage < this.bookTotalPages - 1
        this.buttonPreviousPage!!.visible = this.currPage > 0
    }

    private fun onDataChange() {
        for (button: GuiButton? in this.buttonList) {
            if (button is GuiElementCheckboxPreLaunch) {
                this.tagCompound.putBoolean(this.checkboxToKeyMap.get(button.id), (button.isSelected)!!)
            }
        }

        // Send changed tag compound to server
        (GalacticraftCore.packetPipeline)!!.sendToServer(
            PacketSimple(
                PacketSimple.EnumSimplePacket.S_UPDATE_CHECKLIST,
                GCCoreUtil.getDimensionID(mc.player.world),
                arrayOf<Any?>(this.tagCompound)
            )
        )

        // Update client item
        val stack: ItemStack = mc.player.getHeldItem(EnumHand.MAIN_HAND /* TODO Support off-hand use */)
        var tagCompound: NBTTagCompound? = stack.getTagCompound()
        if (tagCompound == null) {
            tagCompound = NBTTagCompound()
        }
        tagCompound.put("checklistData", this.tagCompound)
        stack.setTagCompound(tagCompound)
    }

    override fun actionPerformed(buttonClicked: GuiButton) {
        if (buttonClicked === this.buttonNextPage) {
            this.currPage++
            this.initGui()
        } else if (buttonClicked === this.buttonPreviousPage) {
            this.currPage--
            this.initGui()
        }
    }

    override fun updateScreen() {
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(bookGuiTexture)
        val i: Int = (this.width - this.bookImageWidth) / 2
        val j: Int = 2
        this.drawTexturedModalRect(i, j, 0, 0, this.bookImageWidth, this.bookImageHeight)

        super.drawScreen(par1, par2, par3)
    }

    override fun onSelectionChanged(element: GuiElementCheckboxPreLaunch?, newSelected: Boolean) {
        // Set all 'sub-boxes' to the new selection
        var started: Boolean = false
        for (i: Int in 2..<this.buttonList.size) {
            val button: GuiButton = this.buttonList.get(i)
            if (started) {
                if (button.x > (element)!!.x) {
                    (button as GuiElementCheckboxPreLaunch).isSelected = newSelected
                } else {
                    break
                }
            } else if (button === element) {
                started = true
            }
        }

        this.onDataChange()
    }

    override fun canPlayerEdit(checkbox: GuiElementCheckboxPreLaunch?, player: EntityPlayer?): Boolean {
        return true
    }

    override fun getInitiallySelected(checkbox: GuiElementCheckboxPreLaunch?): Boolean {
        return this.tagCompound.getBoolean(this.checkboxToKeyMap.get((checkbox)!!.id))
    }

    override fun onIntruderInteraction() {
    }

    @OnlyIn(Dist.CLIENT)
    open class NextPageButton constructor(id: Int, x: Int, y: Int, private val forward: Boolean) :
        GuiButton(id, x, y, 23, 13, "") {
        override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partial: Float) {
            if (this.visible) {
                val flag: Boolean =
                    mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                mc.getTextureManager().bindTexture(bookGuiTexture)
                var i: Int = 0
                var j: Int = 192

                if (flag) {
                    i += 23
                }

                if (!this.forward) {
                    j += 13
                }

                this.drawTexturedModalRect(this.x, this.y, i, j, 23, 13)
            }
        }
    }

    companion object {
        private val bookGuiTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/checklist_book.png")
    }
}
