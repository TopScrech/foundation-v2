package galaxyspace.systems.SolarSystem.planets.ceres.renderer.entities

import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import galaxyspace.systems.SolarSystem.planets.ceres.entities.EntityBossBlaze
import kotlin.math.pow
import galaxyspace.systems.SolarSystem.planets.ceres.renderer.entities.RenderBossBlaze
import net.minecraft.client.model.ModelBlaze
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class RenderBossBlaze constructor(renderManagerIn: RenderManager) :
    RenderLiving<EntityBossBlaze>(renderManagerIn, ModelBlaze(), 4.0f) {
    private val field_77068_a = 0

    override fun preRenderCallback(entity: EntityBossBlaze, partialTicks: Float) {
        GL11.glScalef(4.0f, 4.0f, 4.0f)
        GlStateManager.rotate(
            (entity.deathTicks.toDouble().pow(2.0) / 5.0f + (entity.deathTicks.toDouble()
                .pow(2.0) / 5.0f - (entity.deathTicks - 1).toDouble().pow(2.0) / 5.0f) * partialTicks).toFloat(),
            0.0f,
            1.0f,
            0.0f
        )
    }

    override fun getEntityTexture(entity: EntityBossBlaze): ResourceLocation? {
        return evolvedBossTextures
    }

    companion object {
        private val evolvedBossTextures =
            ResourceLocation(GalaxySpace.ASSET_PREFIX + ":" + "textures/model/boss_blaze.png")
    }
}
