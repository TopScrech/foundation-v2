package micdoodle8.mods.galacticraft.core.client.model

import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.model.ModelVillager
import net.minecraft.entity.Entity

open class ModelAlienVillager @JvmOverloads constructor(
    par1: Float,
    par2: Float = 0.0f,
    par3: Int = 64,
    par4: Int = 64
) : ModelVillager(par1, par2, 0, 0) {
    @JvmField
    var brain: ModelRenderer

    init {
        this.villagerHead = ModelRenderer(this).setTextureSize(par3, par4)
        this.villagerHead.setRotationPoint(0.0f, 0.0f + par2, 0.0f)
        this.villagerHead.setTextureOffset(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, par1 + 0.001f)
        this.villagerNose = ModelRenderer(this).setTextureSize(par3, par4)
        this.villagerNose.setRotationPoint(0.0f, par2 - 2.0f, 0.0f)
        this.villagerNose.setTextureOffset(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2, 4, 2, par1 + 0.002f)
        this.villagerHead.addChild(this.villagerNose)
        this.villagerBody = ModelRenderer(this).setTextureSize(par3, par4)
        this.villagerBody.setRotationPoint(0.0f, 0.0f + par2, 0.0f)
        this.villagerBody.setTextureOffset(16, 20).addBox(-4.0f, 0.0f, -3.0f, 8, 12, 6, par1 + 0.003f)
        this.villagerBody.setTextureOffset(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8, 18, 6, par1 + 0.5f + 0.004f)
        this.villagerArms = ModelRenderer(this).setTextureSize(par3, par4)
        this.villagerArms.setRotationPoint(0.0f, 0.0f + par2 + 2.0f, 0.0f)
        this.villagerArms.setTextureOffset(44, 22).addBox(-8.0f, -2.0f, -2.0f, 4, 8, 4, par1 + 0.005f)
        this.villagerArms.setTextureOffset(44, 22).addBox(4.0f, -2.0f, -2.0f, 4, 8, 4, par1 + 0.0001f)
        this.villagerArms.setTextureOffset(40, 38).addBox(-4.0f, 2.0f, -2.0f, 8, 4, 4, par1 + 0.0004f)
        this.rightVillagerLeg = ModelRenderer(this, 0, 22).setTextureSize(par3, par4)
        this.rightVillagerLeg.setRotationPoint(-2.0f, 12.0f + par2, 0.0f)
        this.rightVillagerLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, par1 + 0.0006f)
        this.leftVillagerLeg = ModelRenderer(this, 0, 22).setTextureSize(par3, par4)
        this.leftVillagerLeg.mirror = true
        this.leftVillagerLeg.setRotationPoint(2.0f, 12.0f + par2, 0.0f)
        this.leftVillagerLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, par1 + 0.0002f)
        this.brain = ModelRenderer(this).setTextureSize(par3, par4)
        this.brain.setRotationPoint(0.0f, 0.0f + par2, 0.0f)
        this.brain.setTextureOffset(32, 0).addBox(-4.0f, -16.0f, -4.0f, 8, 8, 8, par1 + 0.5f)
    }

    override fun render(
        par1Entity: Entity,
        par2: Float,
        par3: Float,
        par4: Float,
        par5: Float,
        par6: Float,
        par7: Float
    ) {
        super.render(par1Entity, par2, par3, par4, par5, par6, par7)

        this.brain.render(par7)
    }

    override fun setRotationAngles(
        par1: Float,
        par2: Float,
        par3: Float,
        par4: Float,
        par5: Float,
        par6: Float,
        par7Entity: Entity
    ) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity)

        this.brain.rotateAngleX = this.villagerHead.rotateAngleX
        this.brain.rotateAngleY = this.villagerHead.rotateAngleY
        this.brain.rotateAngleZ = this.villagerHead.rotateAngleZ
    }
}
