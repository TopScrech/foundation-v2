package micdoodle8.mods.galacticraft.core.client.model

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderParaChest
import net.minecraft.client.model.ModelChest
import net.minecraft.client.model.ModelRenderer
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler

open class ModelParaChest @JvmOverloads constructor(par1: Float = 0.0f) : ModelChest() {
    @JvmField
    var parachute: Array<ModelRenderer?> = arrayOfNulls<ModelRenderer>(3)

    @JvmField
    var parachuteStrings: Array<ModelRenderer?> = arrayOfNulls<ModelRenderer>(4)

    init {
        this.parachute[0] = ModelRenderer(this, 0, 0).setTextureSize(256, 256)
        this.parachute[0]!!.addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, par1)
        this.parachute[0]!!.setRotationPoint(15.0f, 4.0f, 0.0f)
        this.parachute[1] = ModelRenderer(this, 0, 42).setTextureSize(256, 256)
        this.parachute[1]!!.addBox(-20.0f, -45.0f, -20.0f, 40, 2, 40, par1)
        this.parachute[1]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.parachute[2] = ModelRenderer(this, 0, 0).setTextureSize(256, 256)
        this.parachute[2]!!.addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, par1)
        this.parachute[2]!!.setRotationPoint(11f, -11f, 0.0f)

        this.parachuteStrings[0] = ModelRenderer(this, 100, 0).setTextureSize(256, 256)
        this.parachuteStrings[0]!!.addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, par1)
        this.parachuteStrings[0]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.parachuteStrings[1] = ModelRenderer(this, 100, 0).setTextureSize(256, 256)
        this.parachuteStrings[1]!!.addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, par1)
        this.parachuteStrings[1]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.parachuteStrings[2] = ModelRenderer(this, 100, 0).setTextureSize(256, 256)
        this.parachuteStrings[2]!!.addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, par1)
        this.parachuteStrings[2]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.parachuteStrings[3] = ModelRenderer(this, 100, 0).setTextureSize(256, 256)
        this.parachuteStrings[3]!!.addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, par1)
        this.parachuteStrings[3]!!.setRotationPoint(0.0f, 0.0f, 0.0f)
    }

    override fun renderAll() {
        this.chestLid.rotateAngleX = Math.PI.toFloat()
        this.chestBelow.rotateAngleX = Math.PI.toFloat()
        this.chestKnob.rotateAngleX = Math.PI.toFloat()

        this.chestLid.rotationPointX = 2.0f
        this.chestLid.rotationPointY = 7.0f
        this.chestLid.rotationPointZ = -6.0f
        this.chestKnob.rotationPointX = 9.0f
        this.chestKnob.rotationPointY = 7.0f
        this.chestKnob.rotationPointZ = -6.0f
        this.chestBelow.rotationPointX = 2.0f
        this.chestBelow.rotationPointY = 8.0f
        this.chestBelow.rotationPointZ = 8.0f

        var i: Int

        i = 0
        while (i < this.parachute.size) {
            this.parachute[i]!!.render(0.0625f)
            i++
        }

        i = 0
        while (i < this.parachuteStrings.size) {
            this.parachuteStrings[i]!!.render(0.0625f)
            i++
        }

        this.parachute[0]!!.rotateAngleY = (0 / Constants.RADIANS_TO_DEGREES)
        this.parachute[2]!!.rotateAngleY = -(0 / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[0]!!.rotateAngleY = (0 / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[1]!!.rotateAngleY = (0 / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[2]!!.rotateAngleY = -(0 / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[3]!!.rotateAngleY = -(0 / Constants.RADIANS_TO_DEGREES)

        this.parachute[0]!!.setRotationPoint(-5.85f, -11.0f, 2.0f)
        this.parachute[1]!!.setRotationPoint(9f, -7f, 2.0f)
        this.parachute[2]!!.setRotationPoint(-2.15f, 4.0f, 2.0f)
        this.parachute[0]!!.rotateAngleZ = (210f / Constants.RADIANS_TO_DEGREES)
        this.parachute[1]!!.rotateAngleZ = (180f / Constants.RADIANS_TO_DEGREES)
        this.parachute[2]!!.rotateAngleZ = -(210f / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[0]!!.rotateAngleZ = ((155f + 180f) / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[0]!!.rotateAngleX = (23f / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[0]!!.setRotationPoint(9.0f, 3.0f, 2.0f)
        this.parachuteStrings[1]!!.rotateAngleZ = ((155f + 180f) / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[1]!!.rotateAngleX = -(23f / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[1]!!.setRotationPoint(9.0f, 3.0f, 2.0f)

        this.parachuteStrings[2]!!.rotateAngleZ = -((155f + 180f) / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[2]!!.rotateAngleX = (23f / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[2]!!.setRotationPoint(9.0f, 3.0f, 2.0f)
        this.parachuteStrings[3]!!.rotateAngleZ = -((155f + 180f) / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[3]!!.rotateAngleX = -(23f / Constants.RADIANS_TO_DEGREES)
        this.parachuteStrings[3]!!.setRotationPoint(9.0f, 3.0f, 2.0f)

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderParaChest.parachestTexture)

        super.renderAll()
    }

    companion object {
        private val grayParachuteTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/parachute/gray.png")
    }
}
