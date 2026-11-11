package micdoodle8.mods.galacticraft.core.client.render.tile

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.block.ModelSolarPanel
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

open class TileEntitySolarPanelRenderer : TileEntitySpecialRenderer<TileEntitySolar>() {
    @JvmField
    var model: ModelSolarPanel = ModelSolarPanel()

    override fun render(
        panel: TileEntitySolar,
        par2: Double,
        par4: Double,
        par6: Double,
        partialTickTime: Float,
        par9: Int,
        alpha: Float
    ) {
        var doSkyRotation: Boolean = false
        if (panel.tierGC == 2) {
            this.bindTexture(solarPanelAdvTexture)
            doSkyRotation = panel.getWorld().provider is WorldProviderSpaceStation
        } else {
            this.bindTexture(solarPanelTexture)
        }

        GL11.glPushMatrix()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glTranslatef(par2.toFloat(), par4.toFloat(), par6.toFloat())

        GL11.glTranslatef(0.5f, 1.0f, 0.5f)
        if (doSkyRotation) {
            GL11.glPushMatrix()
            GL11.glRotatef((panel.getWorld().provider as WorldProviderSpaceStation).getSkyRotation(), 0.0f, 1.0f, 0.0f)
            this.model.renderPole()
            GL11.glPopMatrix()
        } else this.model.renderPole()

        GL11.glTranslatef(0.0f, 1.5f, 0.0f)

        GL11.glRotatef(180.0f, 0f, 0f, 1f)
        GL11.glRotatef(-90.0f, 0f, 1f, 0f)

        val celestialAngle: Float = (panel.getWorld().getCelestialAngle(1.0f) - 0.784690560f) * 360.0f
        val celestialAngle2: Float = panel.getWorld().getCelestialAngle(1.0f) * 360.0f

        if (doSkyRotation) {
            GL11.glRotatef((panel.getWorld().provider as WorldProviderSpaceStation).getSkyRotation(), 0.0f, -1.0f, 0.0f)
        }

        GL11.glRotatef(panel.currentAngle - (celestialAngle - celestialAngle2), 1.0f, 0.0f, 0.0f)

        this.model.renderPanel()

        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        GL11.glPopMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    companion object {
        private val solarPanelTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/solar_panel_basic.png")
        private val solarPanelAdvTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/solar_panel_advanced.png")
    }
}
