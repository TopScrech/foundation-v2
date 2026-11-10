package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.ModelMeteor
import micdoodle8.mods.galacticraft.core.entities.EntityMeteor
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class RenderMeteor constructor(manager: RenderManager) : Render<EntityMeteor>(manager) {
    private val modelMeteor: ModelMeteor

    init {
        this.shadowSize = 1f
        this.modelMeteor = ModelMeteor()
    }

    override fun getEntityTexture(par1Entity: EntityMeteor): ResourceLocation? {
        return meteorTexture
    }

    override fun doRender(meteor: EntityMeteor, par2: Double, par4: Double, par6: Double, par8: Float, par9: Float) {
        GL11.glPushMatrix()
        GL11.glTranslatef(par2.toFloat(), par4.toFloat(), par6.toFloat())
        GL11.glRotatef(par8, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(par8, 1.0f, 0.0f, 0.0f)
        val f: Float = meteor.getSize().toFloat()
        GL11.glScalef(f / 2, f / 2, f / 2)
        this.bindEntityTexture(meteor)
        this.modelMeteor.render(meteor, 0.0f, 0.0f, -0.5f, 0.0f, 0.0f, 0.1f)
        GL11.glPopMatrix()
    }

    companion object {
        private val meteorTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/meteor.png")
    }
}
