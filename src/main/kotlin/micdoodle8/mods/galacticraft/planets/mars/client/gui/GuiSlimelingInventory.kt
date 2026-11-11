package micdoodle8.mods.galacticraft.planets.mars.client.gui

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerSlimeling
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.client.resources.sounds.SimpleSoundInstance as PositionedSoundRecord
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import java.io.IOException
import kotlin.math.floor

open class GuiSlimelingInventory constructor(player: EntityPlayer, private val slimeling: EntitySlimeling) :
    GuiContainer(ContainerSlimeling(player.inventory, slimeling, FMLClientHandler.instance().getClient().player)) {
    private var invX = 0
    private var invY = 0
    private val invWidth = 18
    private val invHeight = 18

    init {
        this.xSize = 176
        this.ySize = 210
    }

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.invX = var5 + 151
        this.invY = var6 + 108
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        if (par1GuiButton.enabled) {
            when (par1GuiButton.id) {
                0 -> {}
            }
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(px: Int, py: Int, par3: Int) {
        if (px >= this.invX && px < this.invX + this.invWidth && py >= this.invY && py < this.invY + this.invHeight) {
            this.mc.getSoundManager().play(PositionedSoundRecord.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            this.mc.displayGuiScreen(GuiSlimeling(this.slimeling))
        }

        super.mouseClicked(px, py, par3)
    }

    override fun drawDefaultBackground() {
    }

    override fun drawGuiContainerBackgroundLayer(f: Float, i: Int, j: Int) {
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2

        GlStateManager.pushMatrix()
        drawRect(var5, var6, var5 + this.xSize, var6 + this.ySize, -0x1000000)
        GlStateManager.popMatrix()

        val yOffset: Int = floor(30.0 * (1.0f - this.slimeling.getScale())).toInt()

        GuiSlimeling.Companion.drawSlimelingOnGui(
            this.slimeling,
            this.width / 2,
            var6 + 62 - yOffset,
            70,
            (var5 + 51 - i).toFloat(),
            (var6 + 75 - 50 - j).toFloat()
        )


        GlStateManager.translate(0f, 0f, 100f)

        GlStateManager.pushMatrix()
        this.mc.renderEngine.bindTexture(slimelingPanelGui)
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize)
        this.drawTexturedModalRect(var5 + this.xSize - 15, var6 + 9, 176, 0, 9, 9)
        this.drawTexturedModalRect(var5 + this.xSize - 15, var6 + 22, 185, 0, 9, 9)
        this.drawTexturedModalRect(var5 + this.xSize - 15, var6 + 35, 194, 0, 9, 9)
        var str: String = "" + Math.round(this.slimeling.getColorRed() * 1000) / 10.0f + "% "
        this.drawString(
            this.fontRenderer,
            str,
            var5 + this.xSize - 15 - this.fontRenderer.getStringWidth(str),
            var6 + 10,
            ColorUtil.to32BitColor(255, 255, 0, 0)
        )
        str = "" + Math.round(this.slimeling.getColorGreen() * 1000) / 10.0f + "% "
        this.drawString(
            this.fontRenderer,
            str,
            var5 + this.xSize - 15 - this.fontRenderer.getStringWidth(str),
            var6 + 23,
            ColorUtil.to32BitColor(255, 0, 255, 0)
        )
        str = "" + Math.round(this.slimeling.getColorBlue() * 1000) / 10.0f + "% "
        this.drawString(
            this.fontRenderer,
            str,
            var5 + this.xSize - 15 - this.fontRenderer.getStringWidth(str),
            var6 + 36,
            ColorUtil.to32BitColor(255, 0, 0, 255)
        )

        this.mc.renderEngine.bindTexture(slimelingPanelGui)
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        this.drawTexturedModalRect(this.invX, this.invY, 176, 27, this.invWidth, this.invHeight)
        this.drawTexturedModalRect(var5 + 8, var6 + 8, 176, 9, 18, 18)
        this.drawTexturedModalRect(var5 + 8, var6 + 29, 176, 9, 18, 18)

        val stack: ItemStack = this.slimeling.slimelingInventory.getItem(1)

        if (stack != null && stack.getItem() === MarsItems.marsItemBasic && stack.legacyItemValue == 4) {
            val offsetX: Int = 7
            val offsetY: Int = 53

            for (y: Int in 0..2) {
                for (x: Int in 0..8) {
                    this.drawTexturedModalRect(var5 + offsetX + x * 18, var6 + offsetY + y * 18, 176, 9, 18, 18)
                }
            }
        }

        GlStateManager.popMatrix()
    }

    companion object {
        private val slimelingPanelGui =
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/slimeling_panel2.png")
    }
}
