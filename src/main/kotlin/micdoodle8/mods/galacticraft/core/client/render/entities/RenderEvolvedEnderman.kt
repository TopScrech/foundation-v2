package micdoodle8.mods.galacticraft.core.client.render.entities

import java.util.Random

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedEnderman
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.LayerEvolvedEndermanEyes
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.LayerEvolvedEndermanHeldBlock
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.*

@OnlyIn(Dist.CLIENT)
open class RenderEvolvedEnderman constructor(manager: RenderManager) :
    RenderLiving<EntityEvolvedEnderman>(manager, ModelEvolvedEnderman(), 0.5f) {
    private val endermanModel: ModelEvolvedEnderman
    private val rnd = Random()
    private var texSwitch = false

    init {
        this.endermanModel = super.mainModel as ModelEvolvedEnderman
        this.addLayer<EntityEvolvedEnderman?, LayerEvolvedEndermanEyes?>(LayerEvolvedEndermanEyes(this))
        this.addLayer<EntityEvolvedEnderman?, LayerEvolvedEndermanHeldBlock?>(LayerEvolvedEndermanHeldBlock(this))
    }

    override fun getEntityTexture(entity: EntityEvolvedEnderman): ResourceLocation? {
        return if (texSwitch) OverlaySensorGlasses.altTexture else endermanTexture
    }

    override fun doRender(
        entity: EntityEvolvedEnderman,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        var x: Double = x
        var z: Double = z
        val iblockstate: IBlockState? = entity.getHeldBlockState()
        this.endermanModel.isCarrying = iblockstate != null
        this.endermanModel.isAttacking = entity.isScreaming()

        if (entity.isScreaming()) {
            val d3: Double = 0.02
            x += this.rnd.nextGaussian() * d3
            z += this.rnd.nextGaussian() * d3
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks)
        if (OverlaySensorGlasses.overrideMobTexture()) {
            texSwitch = true
            super.doRender(entity, x, y, z, entityYaw, partialTicks)
            texSwitch = false
        }
    }

    override fun preRenderCallback(entity: EntityEvolvedEnderman, partialTickTime: Float) {
        if (texSwitch) {
            OverlaySensorGlasses.preRenderMobs()
        }
    }

    companion object {
        private val endermanTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/evolved_enderman.png")
    }
}
