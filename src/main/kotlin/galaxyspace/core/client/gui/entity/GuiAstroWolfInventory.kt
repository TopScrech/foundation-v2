package galaxyspace.core.client.gui.entity

import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.entities.EntityAstroWolf
import galaxyspace.core.prefab.inventory.ContainerAstroWolf
import galaxyspace.core.util.GSUtils
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class GuiAstroWolfInventory constructor(player: EntityPlayer, private val wolf: EntityAstroWolf) :
    GuiContainer(ContainerAstroWolf(player.inventory, wolf, player)) {
    private var invX = 0
    private var invY = 0
    private val invWidth = 18
    private val invHeight = 18

    /** The mouse x-position recorded during the last rendered frame.  */
    private var mousePosx = 0f

    /** The mouse y-position recorded during the last renderered frame.  */
    private var mousePosY = 0f

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

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        this.mousePosx = mouseX.toFloat()
        this.mousePosY = mouseY.toFloat()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        this.mc.renderEngine.bindTexture(wolfGui)
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize)
        val name: String = "entity.astro_wolf.name"
        this.drawString(
            fontRenderer,
            GCCoreUtil.translate(name),
            containerWidth + this.xSize / 2 - name.length - 5,
            containerHeight + 3,
            0xFFFFFF
        )

        this.drawString(
            fontRenderer,
            GCCoreUtil.translate("item.oxygen_mask.name") + ": " + localeBoolean(
                !this.wolf.wolfInventory.getStackInSlot(0).isEmpty()
            ),
            containerWidth + 80,
            containerHeight + 20,
            0xFFFFFF
        )
        this.drawString(
            fontRenderer,
            GCCoreUtil.translate("gui.message.thermal_status.name")
                .substring(0, 7) + ": " + localeBoolean(!this.wolf.wolfInventory.getStackInSlot(1).isEmpty()),
            containerWidth + 80,
            containerHeight + 30,
            0xFFFFFF
        )

        var oxygen: String = ""
        if (!this.wolf.wolfInventory.getStackInSlot(2).isEmpty()) oxygen =
            GCCoreUtil.translate("gui.oxygen_storage.desc.1") + ": " + EnumColor.BRIGHT_GREEN + (this.wolf.wolfInventory.getStackInSlot(
                2
            ).getMaxDamage() - this.wolf.wolfInventory.getStackInSlot(2).getItemDamage())
        else oxygen =
            GCCoreUtil.translate("gui.oxygen_storage.desc.1") + ": " + EnumColor.DARK_RED + localeBoolean(false)

        this.drawString(fontRenderer, oxygen, containerWidth + 80, containerHeight + 40, 0xFFFFFF)

        this.drawString(
            fontRenderer,
            Math.round(this.wolf.getHealth()).toString() + "/" + Math.round(this.wolf.getMaxHealth()),
            containerWidth + 35,
            containerHeight + 20,
            0xFFFFFF
        )

        GlStateManager.pushMatrix()
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        GuiInventory.drawEntityOnScreen(
            containerWidth + 51,
            containerHeight + 60,
            25,
            (containerWidth + 51).toFloat() - this.mousePosx,
            (containerHeight + 75 - 50).toFloat() - this.mousePosY,
            this.wolf
        )


        // Slots       
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        this.mc.renderEngine.bindTexture(wolfGui)
        for (i: Int in this.inventorySlots.inventorySlots.indices) {
            val x: Int = this.inventorySlots.getSlot(i).xPos
            val y: Int = this.inventorySlots.getSlot(i).yPos

            GL11.glPushMatrix()
            if (this.inventorySlots.getSlot(i).inventory !is InventoryPlayer) this.drawTexturedModalRect(
                containerWidth + x - 1,
                containerHeight + y - 1,
                176,
                0,
                18,
                18
            )
            GL11.glPopMatrix()
        }
        GSUtils.renderDebugGui(this, containerWidth, containerHeight)
        GlStateManager.popMatrix()
    }

    private fun localeBoolean(bol: Boolean): String {
        return if (bol) EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.message.yes") else EnumColor.RED.toString() + GCCoreUtil.translate(
            "gui.message.no"
        )
    }

    companion object {
        //private static final Minecraft mc = Minecraft.getMinecraft();
        private val wolfGui = ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/wolf_inventory.png")
    }
}
