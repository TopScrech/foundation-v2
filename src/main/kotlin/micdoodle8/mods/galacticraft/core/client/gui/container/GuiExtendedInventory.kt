package micdoodle8.mods.galacticraft.core.client.gui.container

import micdoodle8.mods.galacticraft.api.client.tabs.AbstractTab
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.screen.InventoryTabGalacticraft
import micdoodle8.mods.galacticraft.core.inventory.ContainerExtendedInventory
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.InventoryEffectRenderer
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.resources.Identifier as ResourceLocation
import kotlin.math.atan
import kotlin.math.sin

open class GuiExtendedInventory constructor(player: EntityPlayer, inventory: InventoryExtended?) :
    InventoryEffectRenderer(ContainerExtendedInventory(player, inventory)) {
    private var potionOffsetLast = 0
    private var initWithPotion = false

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        drawPlayerOnGui(this.mc, 33, 60, 29)
    }

    override fun initGui() {
        super.initGui()

        this.guiLeft = (this.width - this.xSize) / 2
        this.guiLeft += this.getPotionOffset()
        this.potionOffsetLast = this.getPotionOffsetNEI()

        val cornerX: Int = this.guiLeft
        val cornerY: Int = this.guiTop

        TabRegistry.updateTabValues(cornerX, cornerY, InventoryTabGalacticraft::class.java)
        TabRegistry.addTabsToList(this.buttonList)

        this.buttonList.add(GuiButton(0, this.guiLeft + 10, this.guiTop + 71, 7, 7, ""))
        this.buttonList.add(GuiButton(1, this.guiLeft + 51, this.guiTop + 71, 7, 7, ""))
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> rotation += 10.0f
            1 -> rotation -= 10.0f
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.getTextureManager().bindTexture(inventoryTexture)
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val newPotionOffset: Int = this.getPotionOffsetNEI()

        if (newPotionOffset < this.potionOffsetLast) {
            val diff: Int = newPotionOffset - this.potionOffsetLast
            this.potionOffsetLast = newPotionOffset
            this.guiLeft += diff

            for (k: Int in this.buttonList.indices) {
                val button: GuiButton = this.buttonList.get(k)

                if (button !is AbstractTab) {
                    button.x += diff
                }
            }
        }
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    //Instanced method of this to have the instance field initWithPotion
    open fun getPotionOffset(): Int {
        /*Disabled in 1.12.2 because a vanilla bug means potion offsets are currently not a thing
         *The vanilla bug is that GuiInventory.initGui() resets GuiLeft to the recipe book version of GuiLeft,
         *and in GuiRecipeBook.updateScreenPosition() it takes no account of potion offset even if the recipe book is inactive.

        // If at least one potion is active...
        if (this.hasActivePotionEffects)
        {
            this.initWithPotion = true;
            return 60 + TabRegistry.getPotionOffsetJEI() + getPotionOffsetNEI();
        }
         */
        // No potions, no offset needed
        this.initWithPotion = false
        return 0
    }

    //Instanced method of this to use the instance field initWithPotion
    open fun getPotionOffsetNEI(): Int {
        if (this.initWithPotion && TabRegistry.clazzNEIConfig != null) {
            try {
                // Check whether NEI is hidden and enabled
                val hidden: Any? = ((TabRegistry.clazzNEIConfig)!!.getMethod("isHidden").invoke(null))!!
                val enabled: Any? = ((TabRegistry.clazzNEIConfig)!!.getMethod("isEnabled").invoke(null))!!

                if (hidden is Boolean && enabled is Boolean) {
                    if (hidden || !enabled) {
                        // If NEI is disabled or hidden, offset the tabs by the standard 60
                        return 0
                    }
                    //Active NEI undoes the standard potion offset
                    return -60
                }
            } catch (e: Exception) {
            }
        }
        //No NEI, no change
        return 0
    }

    companion object {
        private val inventoryTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/inventory.png")
        private var rotation = 0.0f

        @JvmStatic
        fun drawPlayerOnGui(mc: Minecraft, x: Int, y: Int, scale: Int) {
            val rendermanager: RenderManager = Minecraft.getInstance().getRenderManager()
            GlStateManager.enableColorMaterial()
            GlStateManager.pushMatrix()
            GlStateManager.translate(x.toFloat(), y.toFloat(), 50.0f)
            GlStateManager.scale(-scale.toFloat(), scale.toFloat(), scale.toFloat())
            GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
            val f2: Float = mc.player.yBodyRot
            val f3: Float = mc.player.yRot
            val f4: Float = mc.player.xRot
            val f5: Float = mc.player.yHeadRot
            GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f)
            RenderHelper.enableStandardItemLighting()
            GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f)
            mc.player.yBodyRot = rotation
            mc.player.yRot = atan((32 / 40.0f).toDouble()).toFloat() * 40.0f
            mc.player.yRot = rotation
            mc.player.yHeadRot = mc.player.yRot
            mc.player.xRot = sin((Minecraft.getSystemTime() / 500.0f).toDouble()).toFloat() * 3.0f
            GlStateManager.translate(0.0f, mc.player.getYOffset().toFloat(), 0.0f)
            rendermanager.setPlayerViewY(180.0f)
            rendermanager.setRenderShadow(false)
            rendermanager.renderEntity(mc.player, 0.0, 0.0, 0.0, 0.0f, 1.0f, false)
            rendermanager.setRenderShadow(true)
            mc.player.yBodyRot = f2
            mc.player.yRot = f3
            mc.player.xRot = f4
            mc.player.yHeadRot = f5
            GlStateManager.popMatrix()
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableRescaleNormal()
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
            GlStateManager.disableTexture2D()
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
        }
    }
}
