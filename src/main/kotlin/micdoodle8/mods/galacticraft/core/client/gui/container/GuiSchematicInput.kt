package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.recipe.ISchematicResultPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.inventory.ContainerSchematic
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import org.lwjgl.opengl.GL11

open class GuiSchematicInput constructor(par1InventoryPlayer: InventoryPlayer, pos: BlockPos) :
    GuiPositionedContainer(ContainerSchematic(par1InventoryPlayer, pos), pos), ISchematicResultPage {
    private var pageIndex = 0

    override fun initGui() {
        super.initGui()
        val schematicSlotDesc: MutableList<String?> = ArrayList<String?>()
        val nextButton: GuiButton
        schematicSlotDesc.add(GCCoreUtil.translate("gui.new_schematic.slot.desc.0"))
        schematicSlotDesc.add(GCCoreUtil.translate("gui.new_schematic.slot.desc.1"))
        schematicSlotDesc.add(GCCoreUtil.translate("gui.new_schematic.slot.desc.2"))
        schematicSlotDesc.add(GCCoreUtil.translate("gui.new_schematic.slot.desc.3"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 79,
                (this.height - this.ySize) / 2,
                18,
                18,
                schematicSlotDesc,
                this.width,
                this.height,
                this
            )
        )
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 130,
                this.height / 2 - 110,
                40,
                20,
                GCCoreUtil.translate("gui.button.back.name")
            )
        )
        this.buttonList.add(
            GuiButton(
                1,
                this.width / 2 - 130,
                this.height / 2 - 110 + 25,
                40,
                20,
                GCCoreUtil.translate("gui.button.next.name")
            ).also { nextButton = it })
        this.buttonList.add(
            GuiButton(
                2,
                this.width / 2 - 92 / 2,
                this.height / 2 - 52,
                92,
                20,
                GCCoreUtil.translate("gui.button.unlockschematic.name")
            )
        )
        nextButton.enabled = false
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        if (par1GuiButton.enabled) {
            when (par1GuiButton.id) {
                0 -> SchematicRegistry.flipToLastPage(this, this.pageIndex)
                1 -> SchematicRegistry.flipToNextPage(this, this.pageIndex)
                2 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_UNLOCK_NEW_SCHEMATIC,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any?>()
                    )
                )
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(GCCoreUtil.translate("gui.message.addnewsch.name"), 7, -22, 4210752)
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 8, 56, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(schematicInputTexture)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - 220) / 2
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, 220)
    }

    override fun setPageIndex(index: Int) {
        this.pageIndex = index
    }

    companion object {
        private val schematicInputTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/schematicpage.png")
    }
}
