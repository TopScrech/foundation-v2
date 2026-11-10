package galaxyspace.systems.SolarSystem.planets.overworld.render.tile

import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.models.ModelSolarWindPanel
import galaxyspace.systems.SolarSystem.planets.overworld.render.tile.TileEntitySolarWindPanelRenderer
import org.lwjgl.opengl.GL12
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation

open class TileEntitySolarWindPanelRenderer : TileEntitySpecialRenderer<TileEntityWindSolarPanel>() {
    @JvmField
    var model: ModelSolarWindPanel = ModelSolarWindPanel()
    override fun render(
        panel: TileEntityWindSolarPanel,
        par2: Double,
        par4: Double,
        par6: Double,
        partialTickTime: Float,
        par9: Int,
        alpha: Float
    ) {
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0F, 240F);

        this.bindTexture(solarwindpanelTexture)

        GL11.glPushMatrix()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glTranslatef(par2.toFloat(), par4.toFloat(), par6.toFloat())

        GL11.glTranslatef(0.5f, 1.0f, 0.5f)
        this.model.renderPole()
        GL11.glTranslatef(0.0f, 1.5f, 0.0f)

        GL11.glRotatef(-180.0f, 0f, 0f, 1f)
        GL11.glRotatef(90.0f, 0f, 1f, 0f)

        val celestialAngle: Float = (panel.getWorld().getCelestialAngle(1.0f) - 0.784690560f) * 360.0f
        val celestialAngle2: Float = panel.getWorld().getCelestialAngle(1.0f) * 360.0f

        //GL11.glRotatef(panel.currentAngle - (celestialAngle - celestialAngle2), 1.0F, 0.0F, 0.0F);
        this.model.renderPanel()

        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        GL11.glPopMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    companion object {
        private val solarwindpanelTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/model/solarwind_panel.png")
    }
}
