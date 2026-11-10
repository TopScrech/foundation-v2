package micdoodle8.mods.galacticraft.core.client.model.block

import net.minecraft.client.model.ModelChest
import net.minecraft.client.model.ModelRenderer

open class ModelParaChestTile constructor() : ModelChest() {
    @JvmField
    var keyParts: Array<ModelRenderer>? = (arrayOfNulls<ModelRenderer>(3) as Array<ModelRenderer>?)

    init {
        this.keyParts!![0] = ModelRenderer(this, 60, 61)
        this.keyParts!![0].setTextureSize(64, 64)
        this.keyParts!![0].addBox(-1.0f, -6.0f, 0.0f, 1, 1, 1)
        this.keyParts!![0].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![0].mirror = true
        this.keyParts!![0].rotationPointX = 7.0f
        this.keyParts!![0].rotationPointY = 7.0f
        this.keyParts!![0].rotationPointZ = 7.5f
        this.keyParts!![1] = ModelRenderer(this, 53, 57)
        this.keyParts!![1].setTextureSize(64, 64)
        this.keyParts!![1].addBox(-1.0f, -6.0f, 0.0f, 4, 1, 1)
        this.keyParts!![1].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![1].mirror = true
        this.keyParts!![1].rotationPointX = 7.0f
        this.keyParts!![1].rotationPointY = 6.0f
        this.keyParts!![1].rotationPointZ = 7.5f
        this.keyParts!![2] = ModelRenderer(this, 60, 61)
        this.keyParts!![2].setTextureSize(64, 64)
        this.keyParts!![2].addBox(-1.0f, -6.0f, 0.0f, 1, 1, 1)
        this.keyParts!![2].setRotationPoint(0f, 0f, 0f)
        this.keyParts!![2].mirror = true
        this.keyParts!![2].rotationPointX = 10.0f
        this.keyParts!![2].rotationPointY = 7.0f
        this.keyParts!![2].rotationPointZ = 7.5f
    }

    open fun renderAll(lidUp: Boolean) {
        if (lidUp) {
            for (m: ModelRenderer in this.keyParts!!) {
                m.render(0.0625f)
            }
        }

        super.renderAll()
    }
}
