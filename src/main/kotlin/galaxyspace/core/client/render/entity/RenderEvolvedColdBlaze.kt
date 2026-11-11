package galaxyspace.core.client.render.entity

import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import net.minecraft.client.model.ModelBlaze
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class RenderEvolvedColdBlaze constructor(renderManagerIn: RenderManager) :
    RenderLiving<EntityEvolvedColdBlaze>(renderManagerIn, ModelBlaze(), 1.0f) {
    override fun getEntityTexture(entity: EntityEvolvedColdBlaze): ResourceLocation? {
        return mobTextures
    }

    companion object {
        private val mobTextures =
            ResourceLocation.parse(GalaxySpace.ASSET_PREFIX + ":" + "textures/model/evolved_coldblaze.png")
    }
}
