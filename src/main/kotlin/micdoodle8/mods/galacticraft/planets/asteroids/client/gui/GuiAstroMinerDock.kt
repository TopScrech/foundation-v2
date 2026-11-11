package micdoodle8.mods.galacticraft.planets.asteroids.client.gui

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiCargoLoader
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerAstroMinerDock
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.floor
import kotlin.math.min

open class GuiAstroMinerDock constructor(playerInventory: InventoryPlayer?, dock: TileEntityMinerBase) :
    GuiContainerGC(ContainerAstroMinerDock((playerInventory)!!, dock)) {
    private val tile: TileEntityMinerBase
    private var recallButton: GuiButton? = null
    private val electricInfoRegion = GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 233,
        (this.height - this.ySize) / 2 + 31,
        10,
        68,
        ArrayList<String?>(),
        this.width,
        this.height,
        this
    )
    private var extraLines = false

    init {
        this.xSize = 256
        this.ySize = 221
        this.tile = dock
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.recallButton!!.enabled = true

        if (this.tile.linkedMinerID == null) {
            this.recallButton!!.enabled = false
        } else {
            if (this.tile.linkedMinerDataAIState < EntityAstroMiner.Companion.AISTATE_TRAVELLING || this.tile.linkedMinerDataAIState == EntityAstroMiner.Companion.AISTATE_DOCKING) {
                this.recallButton!!.enabled = false
            }
        }
        this.recallButton!!.displayString = GCCoreUtil.translate("gui.button.recall.name")
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun initGui() {
        super.initGui()
        val xPos: Int = (this.width - this.xSize) / 2
        val yPos: Int = (this.height - this.ySize) / 2
        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        electricityDesc.add(
            EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.energy_storage.desc.1") + (floor(
                this.tile.getEnergyStoredGC().toDouble()
            ).toInt().toString() + " / " + floor(this.tile.getMaxEnergyStoredGC().toDouble()).toInt())
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc
        this.electricInfoRegion.xPosition = xPos + 233
        this.electricInfoRegion.yPosition = yPos + 29
        this.electricInfoRegion.parentWidth = this.width
        this.electricInfoRegion.parentHeight = this.height
        this.infoRegions.add(this.electricInfoRegion)
        val batterySlotDesc: MutableList<String?> = ArrayList<String?>()
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.0"))
        batterySlotDesc.add(GCCoreUtil.translate("gui.battery_slot.desc.1"))
        this.infoRegions.add(
            GuiElementInfoRegion(
                xPos + 230,
                yPos + 108,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this
            )
        )
        this.buttonList.add(
            GuiButton(
                0,
                xPos + 173,
                yPos + 195,
                76,
                20,
                GCCoreUtil.translate("gui.button.recall.name")
            ).also { this.recallButton = it })
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.enabled) {
            when (button.id) {
                0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                        GCCoreUtil.getDimensionID(this.mc.world),
                        arrayOf<Any>(this.tile.getPos(), 0)
                    )
                )

                else -> {}
            }
        }
    }

    private fun getDeltaString(num: Int): String {
        return if (num > 0) "+" + num else "" + num
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        this.fontRenderer.drawString(this.tile.getName(), 7, 6, 4210752)
        this.fontRenderer.drawString(this.getStatus(), 177, 141, 4210752)

        if (this.extraLines) {
            this.fontRenderer.drawString(
                "\u0394x: " + this.getDeltaString(this.tile.linkedMinerDataDX),
                186,
                152,
                2536735
            )
        }
        if (this.extraLines) {
            this.fontRenderer.drawString(
                "\u0394y: " + this.getDeltaString(this.tile.linkedMinerDataDY),
                186,
                162,
                2536735
            )
        }
        if (this.extraLines) {
            this.fontRenderer.drawString(
                "\u0394z: " + this.getDeltaString(this.tile.linkedMinerDataDZ),
                186,
                172,
                2536735
            )
        }
        if (this.extraLines) {
            this.fontRenderer.drawString(
                GCCoreUtil.translate("gui.miner.mined") + ": " + this.tile.linkedMinerDataCount,
                177,
                183,
                2536735
            )
        }
        this.fontRenderer.drawString(GCCoreUtil.translate("container.inventory"), 7, this.ySize - 92, 4210752)
    }

    private fun getStatus(): String {
        this.extraLines = false

        when (this.tile.linkedMinerDataAIState) {
            -3 -> return ""
            -2 -> return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.miner.out_of_range")
            EntityAstroMiner.Companion.AISTATE_OFFLINE -> return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.miner.offline")
            EntityAstroMiner.Companion.AISTATE_STUCK -> {
                this.extraLines = true
                return EnumColor.RED.toString() + GCCoreUtil.translate("gui.miner.stuck")
            }

            EntityAstroMiner.Companion.AISTATE_ATBASE -> return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate(
                "gui.miner.docked"
            )

            EntityAstroMiner.Companion.AISTATE_TRAVELLING -> {
                this.extraLines = true
                return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.miner.travelling")
            }

            EntityAstroMiner.Companion.AISTATE_MINING -> {
                this.extraLines = true
                return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.miner.mining")
            }

            EntityAstroMiner.Companion.AISTATE_RETURNING -> {
                this.extraLines = true
                return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.miner.returning")
            }

            EntityAstroMiner.Companion.AISTATE_DOCKING -> return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate(
                "gui.miner.docking"
            )
        }
        return ""
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        val xPos: Int = (this.width - this.xSize) / 2
        val yPos: Int = (this.height - this.ySize) / 2
        this.mc.getTextureManager().bindTexture(dockGui)
        this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize)
        val electricityDesc: MutableList<String?> = ArrayList<String?>()
        electricityDesc.add(GCCoreUtil.translate("gui.energy_storage.desc.0"))
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.tile.getEnergyStoredGC(),
            this.tile.getMaxEnergyStoredGC(),
            electricityDesc
        )
        this.electricInfoRegion.tooltipStrings = electricityDesc

        this.mc.getTextureManager().bindTexture(GuiCargoLoader.loaderTexture)

        if (this.tile.getEnergyStoredGC() > 0) {
            this.drawTexturedModalRect(xPos + 233, yPos + 17, 176, 0, 11, 10)
        }

        val level: Int = min(this.tile.getScaledElecticalLevel(66), 66)
        this.drawColorModalRect(xPos + 234, yPos + 29 + 66 - level, 8, level, 0xc1aa24)
    }

    private fun drawColorModalRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
        worldRenderer.pos((x + 0).toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .color((color shr 16 and 255) / 255.0f, (color shr 8 and 255) / 255.0f, (color and 255) / 255.0f, 1.0f)
            .endVertex()
        worldRenderer.pos((x + width).toDouble(), (y + height).toDouble(), this.zLevel.toDouble())
            .color((color shr 16 and 255) / 255.0f, (color shr 8 and 255) / 255.0f, (color and 255) / 255.0f, 1.0f)
            .endVertex()
        worldRenderer.pos((x + width).toDouble(), (y + 0).toDouble(), this.zLevel.toDouble())
            .color((color shr 16 and 255) / 255.0f, (color shr 8 and 255) / 255.0f, (color and 255) / 255.0f, 1.0f)
            .endVertex()
        worldRenderer.pos((x + 0).toDouble(), (y + 0).toDouble(), this.zLevel.toDouble())
            .color((color shr 16 and 255) / 255.0f, (color shr 8 and 255) / 255.0f, (color and 255) / 255.0f, 1.0f)
            .endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.blendFunc(770, 771)
    }

    companion object {
        private val dockGui =
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/gui_astro_miner_dock.png")
    }
}
