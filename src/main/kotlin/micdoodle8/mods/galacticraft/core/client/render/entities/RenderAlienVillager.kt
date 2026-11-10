package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.client.model.ModelAlienVillager
import micdoodle8.mods.galacticraft.core.entities.EntityAlienVillager
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
open class RenderAlienVillager constructor(manager: RenderManager) :
    RenderLiving<EntityAlienVillager>(manager, ModelAlienVillager(0.0f), 0.5f) {
    private var texSwitch = false

    @JvmField
    protected var villagerModel: ModelAlienVillager?

    init {
        this.villagerModel = this.mainModel as ModelAlienVillager?
    }

    override fun preRenderCallback(villager: EntityAlienVillager, par2: Float) {
        var f1: Float = 0.9375f

        if (villager.getGrowingAge() < 0) {
            f1 = (f1 * 0.5).toFloat()
            this.shadowSize = 0.25f
        } else {
            this.shadowSize = 0.5f
        }

        if (texSwitch) {
            OverlaySensorGlasses.preRenderMobs()
        }

        GL11.glScalef(f1, f1, f1)
    }

    override fun getEntityTexture(par1Entity: EntityAlienVillager): ResourceLocation? {
        return if (texSwitch) OverlaySensorGlasses.altTexture else villagerTexture
    }

    override fun doRender(
        villager: EntityAlienVillager,
        par2: Double,
        par4: Double,
        par6: Double,
        par8: Float,
        par9: Float
    ) {
        super.doRender(villager, par2, par4, par6, par8, par9)
        if (OverlaySensorGlasses.overrideMobTexture()) {
            texSwitch = true
            super.doRender(villager, par2, par4, par6, par8, par9)
            texSwitch = false
        }
    }

    companion object {
        private val villagerTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/villager.png")
    }
}
