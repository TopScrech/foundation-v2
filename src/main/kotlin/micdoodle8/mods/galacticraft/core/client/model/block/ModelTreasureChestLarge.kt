package micdoodle8.mods.galacticraft.core.client.model.block

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.model.ModelLargeChest
import net.minecraft.client.model.ModelRenderer

open class ModelTreasureChestLarge constructor() : ModelLargeChest() {
    @JvmField
    var keyParts: Array<ModelRenderer>? = (arrayOfNulls<ModelRenderer>(6) as Array<ModelRenderer>?)

    init {
        this.textureWidth = 128
        this.textureHeight = 64
        this.keyParts!![4] = ModelRenderer(this, 50, 43)
        this.keyParts!![4].addBox(7f, 2f, -0.5f, 3, 1, 1)
        this.keyParts!![4].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![4].setTextureSize(128, 64)
        this.keyParts!![4].mirror = true
        this.keyParts!![3] = ModelRenderer(this, 39, 43)
        this.keyParts!![3].addBox(6f, 1f, -0.5f, 4, 1, 1)
        this.keyParts!![3].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![3].setTextureSize(128, 64)
        this.keyParts!![3].mirror = true
        this.keyParts!![2] = ModelRenderer(this, 14, 43)
        this.keyParts!![2].addBox(-0.5f, 0f, -0.5f, 11, 1, 1)
        this.keyParts!![2].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![2].setTextureSize(128, 64)
        this.keyParts!![2].mirror = true
        this.keyParts!![1] = ModelRenderer(this, 9, 43)
        this.keyParts!![1].addBox(-1.5f, -0.5f, -0.5f, 1, 2, 1)
        this.keyParts!![1].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![1].setTextureSize(128, 64)
        this.keyParts!![1].mirror = true
        this.keyParts!![0] = ModelRenderer(this, 0, 43)
        this.keyParts!![0].addBox(-4.5f, -1f, -0.5f, 3, 3, 1)
        this.keyParts!![0].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![0].setTextureSize(128, 64)
        this.keyParts!![0].mirror = true
        this.keyParts!![5] = ModelRenderer(this, 0, 0).setTextureSize(128, 64)
        this.keyParts!![5].addBox(-2.0f, -2.05f, -15.1f, 4, 4, 1, 0.0f)
        this.keyParts!![5].rotationPointX = 8.0f
        this.keyParts!![5].rotationPointY = 7.0f
        this.keyParts!![5].rotationPointZ = 15.0f
    }

    open fun renderAll(withKey: Boolean) {
        if (withKey) {
            for (nmtmr: ModelRenderer in this.keyParts!!) {
                if (nmtmr != this.keyParts!![5]) {
                    nmtmr.rotationPointX = 16.0f
                    nmtmr.rotationPointY = 7.0f
                    nmtmr.rotationPointZ = -2.0f
                    nmtmr.rotateAngleY = 3 * Constants.halfPI
                    nmtmr.rotateAngleX = -this.chestLid.rotateAngleX
                    nmtmr.render(0.0625f)
                }
            }
        }

        this.keyParts!![5].rotationPointX = 16.0f
        this.keyParts!![5].rotationPointY = 7.0f
        this.keyParts!![5].rotationPointZ = 15.0f
        this.keyParts!![5].rotateAngleX = 0f
        this.keyParts!![5].rotateAngleY = 0f
        this.keyParts!![5].render(0.0625f)

        super.renderAll()
    }
}
