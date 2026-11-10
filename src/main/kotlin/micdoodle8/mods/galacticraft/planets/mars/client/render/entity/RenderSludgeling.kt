package micdoodle8.mods.galacticraft.planets.mars.client.render.entity

import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.client.model.ModelSludgeling
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySludgeling
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class RenderSludgeling constructor(renderManager: RenderManager) :
    RenderLiving<EntitySludgeling>(renderManager, ModelSludgeling(), 0.3f) {
    private var texSwitch = false

    override fun getDeathMaxRotation(par1EntityLiving: EntitySludgeling): Float {
        return 180.0f
    }

    override fun doRender(
        entity: EntitySludgeling,
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

    override fun getEntityTexture(entity: EntitySludgeling): ResourceLocation? {
        return if (texSwitch) OverlaySensorGlasses.altTexture else sludgelingTexture
    }

    override fun preRenderCallback(entity: EntitySludgeling, par2: Float) {
        super.preRenderCallback(entity, par2)
        if (texSwitch) {
            OverlaySensorGlasses.preRenderMobs()
        }
    }

    companion object {
        private val sludgelingTexture =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/model/sludgeling.png")
    }
}
