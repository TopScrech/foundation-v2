package galaxyspace.systems.SolarSystem.moons.io.renderer.entities

import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import galaxyspace.systems.SolarSystem.moons.io.entities.EntityBossGhast
import kotlin.math.pow
import galaxyspace.systems.SolarSystem.moons.io.renderer.entities.RenderBossGhast
import net.minecraft.client.model.ModelGhast
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class RenderBossGhast constructor(renderManagerIn: RenderManager) :
    RenderLiving<EntityBossGhast>(renderManagerIn, ModelGhast(), 3.0f) {
    private val field_77068_a = 0

    override fun preRenderCallback(entity: EntityBossGhast, partialTicks: Float) {
        GL11.glScalef(4.0f, 4.0f, 4.0f)
        GlStateManager.rotate(
            (entity.deathTicks.toDouble().pow(2.0) / 5.0f + (entity.deathTicks.toDouble()
                .pow(2.0) / 5.0f - (entity.deathTicks - 1).toDouble().pow(2.0) / 5.0f) * partialTicks).toFloat(),
            0.0f,
            1.0f,
            0.0f
        )
    }

    override fun getEntityTexture(entity: EntityBossGhast): ResourceLocation? {
        return evolvedBossTextures
    }

    companion object {
        private val evolvedBossTextures =
            ResourceLocation(GalaxySpace.ASSET_PREFIX + ":" + "textures/model/boss_ghast.png")
    }
}
