package micdoodle8.mods.galacticraft.core.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer

open class ModelKey constructor() : ModelBase() {
    @JvmField
    var keyParts: Array<ModelRenderer>? = (arrayOfNulls<ModelRenderer>(5) as Array<ModelRenderer>?)

    init {
        this.textureWidth = 64
        this.textureHeight = 64
        this.keyParts!![4] = ModelRenderer(this, 50, 43)
        this.keyParts!![4].addBox(7f, 2f, -0.5f, 3, 1, 1)
        this.keyParts!![4].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![4].setTextureSize(64, 64)
        this.keyParts!![4].mirror = true
        this.keyParts!![3] = ModelRenderer(this, 39, 43)
        this.keyParts!![3].addBox(6f, 1f, -0.5f, 4, 1, 1)
        this.keyParts!![3].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![3].setTextureSize(64, 64)
        this.keyParts!![3].mirror = true
        this.keyParts!![2] = ModelRenderer(this, 14, 43)
        this.keyParts!![2].addBox(-0.5f, 0f, -0.5f, 11, 1, 1)
        this.keyParts!![2].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![2].setTextureSize(64, 64)
        this.keyParts!![2].mirror = true
        this.keyParts!![1] = ModelRenderer(this, 9, 43)
        this.keyParts!![1].addBox(-1.5f, -0.5f, -0.5f, 1, 2, 1)
        this.keyParts!![1].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![1].setTextureSize(64, 64)
        this.keyParts!![1].mirror = true
        this.keyParts!![0] = ModelRenderer(this, 0, 43)
        this.keyParts!![0].addBox(-4.5f, -1f, -0.5f, 3, 3, 1)
        this.keyParts!![0].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![0].setTextureSize(64, 64)
        this.keyParts!![0].mirror = true
    }

    open fun renderAll() {
        for (nmtmr: ModelRenderer in this.keyParts!!) {
            nmtmr.rotationPointX = -4.0f
            nmtmr.rotationPointY = 0.0f
            nmtmr.rotationPointZ = -2.0f
            nmtmr.render(0.0625f)
        }
    }
}
