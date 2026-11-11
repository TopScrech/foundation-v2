package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class RenderEvolvedSpider constructor(manager: RenderManager) :
    RenderLiving<EntityEvolvedSpider>(manager, ModelEvolvedSpider(), 1.0f) {
    private var texSwitch = false

    override fun getEntityTexture(par1Entity: EntityEvolvedSpider): ResourceLocation? {
        return if (texSwitch) OverlaySensorGlasses.altTexture else spiderTexture
    }

    override fun preRenderCallback(par1EntityLiving: EntityEvolvedSpider, par2: Float) {
        GL11.glScalef(1.2f, 1.2f, 1.2f)
        if (texSwitch) {
            GL11.glTranslatef(0.0f, -0.03f, 0.0f)
            OverlaySensorGlasses.preRenderMobs()
        }
    }

    override fun doRender(
        entity: EntityEvolvedSpider,
        par2: Double,
        par4: Double,
        par6: Double,
        par8: Float,
        par9: Float
    ) {
        super.doRender(entity, par2, par4, par6, par8, par9)
        if (OverlaySensorGlasses.overrideMobTexture()) {
            texSwitch = true
            super.doRender(entity, par2, par4, par6, par8, par9)
            texSwitch = false
        }
    }

    override fun getDeathMaxRotation(par1EntityLiving: EntityEvolvedSpider): Float {
        return 180.0f
    }

    companion object {
        private val spiderTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/spider.png")
    }
}
