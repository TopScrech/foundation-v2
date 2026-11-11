package micdoodle8.mods.galacticraft.core.client.model

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.world.entity.Entity
import net.minecraft.resources.Identifier as ResourceLocation

open class ModelPlayerGC constructor(var1: Float, smallArms: Boolean) : ModelPlayer(var1, smallArms) {
    init {
        if (smallArms) {
            this.bipedLeftArm = ModelRenderer(this, 32, 48)
            this.bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 3, 12, 4, var1)
            this.bipedLeftArm.setRotationPoint(5.0f, 2.5f, 0.0f)
            this.bipedRightArm = ModelRenderer(this, 40, 16)
            this.bipedRightArm.addBox(-2.0f, -2.0f, -2.0f, 3, 12, 4, var1)
            this.bipedRightArm.setRotationPoint(-5.0f, 2.5f, 0.0f)
        } else {
            this.bipedLeftArm = ModelRenderer(this, 32, 48)
            this.bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, var1)
            this.bipedLeftArm.setRotationPoint(5.0f, 2.0f, 0.0f)
            this.bipedRightArm = ModelRenderer(this, 40, 16)
            this.bipedRightArm.addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, var1)
            this.bipedRightArm.setRotationPoint(-5.0f, 2.0f, 0.0f)
        }
    }

    override fun render(entityIn: Entity, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, scale: Float) {
        GlStateManager.pushMatrix()
        super.render(entityIn, f1, f2, f3, f4, f5, scale)
        GlStateManager.popMatrix()
    }

    companion object {
        @JvmField
        val oxygenMaskTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/oxygen.png")

        @JvmField
        val playerTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/player.png")
    }
}
