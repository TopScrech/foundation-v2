package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList

import galaxyspace.GalaxySpace
import galaxyspace.api.IResearch
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.handler.capabilities.GSStatsCapabilityClient
import galaxyspace.core.handler.capabilities.StatsCapabilityClient
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.core.util.researches.ResearchUtil
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerResearchTable
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityResearchTable
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.io.IOException

open class GuiResearchTable //this.xSize = 250;
//this.ySize = 350; 
constructor(inventoryPlayer: InventoryPlayer?, private val tile: TileEntityResearchTable) :
    GuiTileBase(ContainerResearchTable(inventoryPlayer, tile), -1, -1) {
    @JvmField
    var know_res: MutableList<IResearch?> = ArrayList<IResearch?>()

    public override fun initGui() {
        super.initGui()

        updateRes()
    }

    private fun updateRes() {
        /*StatsCapabilityClient gs_stats = galaxyspace.core.handler.capabilities.StatsCapabilityClient.get(mc.player);
		
		for(IResearch res : ResearchUtil.getReserachList())
		{
			for(int i = 0; i < ResearchUtil.getReserachList().size(); i++)
			{
				if(res.getID() == gs_stats.getKnowledgeResearches()[i] && gs_stats.getKnowledgeResearches()[i] > 0)
					know_res.add(res);	
			}
		}*/
    }

    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)
        //this.fontRenderer.drawString(EnumColor.WHITE + this.tile.getName(), 86 - (this.fontRenderer.getStringWidth(this.tile.getName()) / 2), 1, 4210752);
        //this.fontRenderer.drawString(EnumColor.WHITE + GCCoreUtil.translate("container.inventory"), 15, this.ySize - 88, 4210752);
    }

    protected override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3)

        /*
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int containerWidth = (this.width - this.xSize) / 2;
		int containerHeight = (this.height - this.ySize) / 2;
		this.drawRect(containerWidth, containerHeight, containerWidth+this.xSize, containerHeight+this.ySize, GSUtils.getColor(0, 41, 69, 255));
		//this.drawModalRectWithCustomSizedTexture(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize, 780, 500); // Base Gui		

		*/
    }

    override fun drawScreen(mousePosX: Int, mousePosY: Int, partialTicks: Float) {
        super.drawScreen(mousePosX, mousePosY, partialTicks)
        /*int containerWidth = (this.width - this.xSize) / 2;
		int containerHeight = (this.height - this.ySize) / 2;
		RenderHelper.disableStandardItemLighting();
		int i = 0;		
		for(IResearch res : ResearchUtil.getReserachList()) {
			drawWindow(containerWidth + res.getPosX() * 6, containerHeight + res.getPosY() * 3, partialTicks, res, mousePosX, mousePosY);
			drawString(mc.fontRenderer, res.getID() + ", ", containerWidth + 10 * i++, containerHeight + 30, 0xFFFFFF);
		}
		
		RenderHelper.enableGUIStandardItemLighting();*/
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        super.mouseClicked(mouseX, mouseY, button)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        //if(button == 0)
        for (res in (ResearchUtil.getReserachList())!!) {
            val x: Int = containerWidth + (res)!!.getPosX() * 6
            val y: Int = containerHeight + (res)!!.getPosY() * 3
            if (mouseX >= x && mouseX < x + 25 && mouseY >= y && mouseY < y + 25) {
                if (button == 0) (GalaxySpace.packetPipeline)!!.sendToServer(
                    GSPacketSimple(
                        GSEnumSimplePacket.S_UPDATE_RESEARCH_USER,
                        mc.world,
                        arrayOf<Any>((res)!!.getID())
                    )
                )
                else (GalaxySpace.packetPipeline)!!.sendToServer(
                    GSPacketSimple(
                        GSEnumSimplePacket.S_UPDATE_RESEARCH_USER,
                        mc.world,
                        arrayOf<Any>(-1)
                    )
                )
                //updateRes();
            }
        }
    }

    private fun drawWindow(posX: Int, posY: Int, ticks: Float, res: IResearch, mousePosX: Int, mousePosY: Int) {
        var isResearched: Boolean = false
        this.mc.renderEngine.bindTexture(iconsTexture)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val gs_stats: StatsCapabilityClient = (galaxyspace.core.handler.capabilities.StatsCapabilityClient.get(mc.player))!!


        var x: Int = res.getTextureID()
        val y: Int = res.getTextureID() / 8
        if (res.getTextureID() > 8 * (y + 1)) x = 8 * y - res.getTextureID() + 1


        drawScaledCustomSizeModalRect(
            posX + 16,
            posY - 16,
            (32 * x).toFloat(),
            (32 * y).toFloat(),
            32,
            32,
            24,
            24,
            256f,
            256f
        )

        for (i: Int in (gs_stats.getKnowledgeResearches())!!) if (i == res.getID()) isResearched = true

        if (isResearched) GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f)

        fontRenderer.drawSplitString(
            res.getName(),
            ((posX + 30) - fontRenderer.getStringWidth(res.getName()) / 2),
            posY + 10,
            180,
            0xFFFFFF
        )
        fontRenderer.drawSplitString(
            res.getNeedExperience().toString() + " EXP",
            ((posX + 30) - fontRenderer.getStringWidth(res.getNeedExperience().toString() + " EXP") / 2),
            posY + 20,
            180,
            0xFFFFFF
        )

        if (!isResearched && mousePosX >= posX + 15 && mousePosX < posX + 40 && mousePosY >= posY - 15 && mousePosY < posY + 10) {
            var i: Int = 0
            GL11.glPushMatrix()
            GL11.glTranslatef(0f, 0f, 300f)
            this.drawToolTip(mousePosX + 18, mousePosY + 20, "", (res.getNeedItems())!!.size * 18 + 100, 25)
            this.fontRenderer.drawString("Need Items:", mousePosX - 10, mousePosY + 8, 0xFFFFFF)

            for (s in (res.getNeedItems())!!) {
                RenderHelper.enableGUIStandardItemLighting()
                this.mc.getRenderItem().renderItemAndEffectIntoGUI(s, mousePosX + i * 18 - 35, mousePosY + 16)
                RenderHelper.disableStandardItemLighting()
                i++
            }
            GL11.glPopMatrix()
        }
    }

    private fun drawToolTip(
        mousePosX: Int,
        mousePosY: Int,
        tooltip: String,
        width: Int = this.fontRenderer.getStringWidth(tooltip),
        height: Int = 8
    ) {
        //GL11.glDepthMask(true);
        //GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix()
        //GL11.glTranslatef(0, 0, 300);
        val k: Int = width
        var j2: Int = mousePosX - k / 2
        var k2: Int = mousePosY - 12
        val i1: Int = height

        if (j2 + k > this.width) {
            j2 -= (j2 - this.width + k)
        }

        if (k2 + i1 + 6 > this.height) {
            k2 = this.height - i1 - 6
        }

        val j1: Int = ColorUtil.to32BitColor(190, 0, 94, 153)
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

        this.fontRenderer.drawSplitString(tooltip, j2, k2, 150, ColorUtil.to32BitColor(255, 255, 255, 255))

        GL11.glPopMatrix()

        //GL11.glDepthMask(false);
    }

    override fun isModuleSupport(): Boolean {
        return false
    }

    override fun getName(): String? {
        return tile.getName()
    }

    override fun getBatterySlot(): Slot? {
        return null
    }

    companion object {
        private val iconsTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/researches/research_icons.png")
    }
}
