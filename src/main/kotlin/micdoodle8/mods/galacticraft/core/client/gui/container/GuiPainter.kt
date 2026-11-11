package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.inventory.ContainerPainter
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tile.TileEntityPainter
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class GuiPainter constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityPainter
) : GuiContainerGC(ContainerPainter((par1InventoryPlayer)!!, tileEntity)) {
    private var buttonApplyPaint: GuiButton? = null

    init {
        this.ySize = 186
    }

    override fun initGui() {
        super.initGui()
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 + 4,
                this.height / 2 - 48,
                76,
                20,
                GCCoreUtil.translate("gui.button.paintapply.name")
            ).also { this.buttonApplyPaint = it })
        this.buttonList.add(
            GuiButton(
                1,
                this.width / 2 - 80,
                this.height / 2 - 48,
                76,
                20,
                GCCoreUtil.translate("gui.button.paintmix.name")
            ).also { this.buttonApplyPaint = it })
        this.buttonList.add(
            GuiButton(
                2,
                this.width / 2 - 80,
                this.height / 2 - 48 + 22,
                76,
                20,
                GCCoreUtil.translate("gui.button.paintreset.name")
            ).also { this.buttonApplyPaint = it })
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        (GalacticraftCore.packetPipeline)!!.sendToServer(
            PacketSimple(
                PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                GCCoreUtil.getDimensionID(this.mc.world),
                arrayOf<Any>(this.tileEntity.getPos(), par1GuiButton.id)
            )
        )
        tileEntity.buttonPressed(par1GuiButton.id, this.mc.player, Side.CLIENT)
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(this.tileEntity.getName(), 39, 6, 4210752)
        val displayText: String = ""

        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752)
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.renderEngine.bindTexture(painterTexture)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val guiLeft: Int = (this.width - this.xSize) / 2
        val guiBottom: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(guiLeft, guiBottom, 0, 0, this.xSize, this.ySize)

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        ColorUtil.setGLColor(tileEntity.guiColor)
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldrenderer: BufferBuilder = tessellator.getBuffer()
        val x: Int = guiLeft + this.xSize / 2 - 9
        val y: Int = guiBottom + this.ySize / 2 - 69
        val height: Int = 18
        val width: Int = 18
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos((x + 0f).toDouble(), (y + height).toDouble(), this.zLevel.toDouble()).endVertex()
        worldrenderer.pos((x + width).toDouble(), (y + height).toDouble(), this.zLevel.toDouble()).endVertex()
        worldrenderer.pos((x + width).toDouble(), (y + 0).toDouble(), this.zLevel.toDouble()).endVertex()
        worldrenderer.pos((x + 0f).toDouble(), (y + 0).toDouble(), this.zLevel.toDouble()).endVertex()
        tessellator.draw()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
    }

    companion object {
        private val painterTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/painter.png")
    }
}
