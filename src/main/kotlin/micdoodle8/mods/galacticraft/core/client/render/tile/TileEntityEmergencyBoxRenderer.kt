package micdoodle8.mods.galacticraft.core.client.render.tile

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.blocks.BlockEmergencyBox
import micdoodle8.mods.galacticraft.core.tile.TileEntityEmergencyBox
import net.minecraft.block.state.IBlockState
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.max

@SideOnly(Side.CLIENT)
open class TileEntityEmergencyBoxRenderer : TileEntitySpecialRenderer<TileEntityEmergencyBox>() {
    open inner class Flap constructor() : ModelBase() {
        @JvmField
        var model: ModelRenderer

        @JvmField
        var angle: Float = 0.0f

        init {
            this.textureWidth = 32
            this.textureHeight = 32
            this.model = ModelRenderer(this, 0, 0)
            this.model.addBox(-6f, -6f, 0f, 12, 6, 1)
            this.model.setRotationPoint(0f, 6f, -7f)
            this.model.setTextureSize(this.textureWidth, this.textureHeight)
            this.model.mirror = true
        }

        private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
            model.rotateAngleX = x
            model.rotateAngleY = y
            model.rotateAngleZ = z
        }

        open fun render() {
            this.setRotation(this.model, angle / Constants.RADIANS_TO_DEGREES, 0f, 0f)
            this.model.render(1f / 16f)
        }
    }

    open inner class Plinth constructor() : ModelBase() {
        @JvmField
        var model: ModelRenderer

        init {
            this.textureWidth = 16
            this.textureHeight = 16
            this.model = ModelRenderer(this, 0, 0)
            this.model.addBox(-6f, -7f, -6f, 12, 1, 12)
            this.model.setRotationPoint(0f, 0f, 0f)
            this.model.setTextureSize(this.textureWidth, this.textureHeight)
            this.model.mirror = true
        }

        open fun render(height: Float) {
            this.model.setRotationPoint(0f, height, 0f)
            this.model.render(1f / 16f)
        }
    }

    open inner class Mask constructor() : ModelBase() {
        @JvmField
        var model: ModelRenderer

        init {
            this.textureWidth = 128
            this.textureHeight = 64
            this.model = ModelRenderer(this, 0, 0)
            this.model.addBox(-8.0f, -4f, -8.0f, 16, 16, 16, 1.0f)
            this.model.setRotationPoint(0f, 0f, 0f)
            this.model.setTextureSize(this.textureWidth, this.textureHeight)
            this.model.mirror = true
        }

        open fun render(height: Float) {
            this.model.setRotationPoint(0f, height * MASKSCALE, 0f)
            this.model.render(1f / 16f / MASKSCALE)
        }
    }

    open inner class Tank constructor() : ModelBase() {
        @JvmField
        var model: ModelRenderer

        init {
            this.textureWidth = 128
            this.textureHeight = 64
            this.model = ModelRenderer(this, 0, 0)
            this.model.setTextureOffset(4, 0) // Green tank
            this.model.addBox(-1.5f, 0f, -1.5f, 3, 7, 3, 1.0f)
            this.model.setRotationPoint(0f, 0f, 0f)
            this.model.setTextureSize(this.textureWidth, this.textureHeight)
            this.model.mirror = true
        }

        open fun render(height: Float) {
            this.model.setRotationPoint(0f, height * MASKSCALE, 0f)
            this.model.render(1f / 16f / MASKSCALE)
        }
    }

    open inner class Pack constructor() : ModelBase() {
        @JvmField
        var model: ModelRenderer

        init {
            this.textureWidth = 256
            this.textureHeight = 256
            this.model = ModelRenderer(this, 0, 0)
            this.model.setTextureOffset(50, 50)
            this.model.addBox(-6f, -11f, -10f, 12, 1, 20, 1.0f)
            this.model.setRotationPoint(0f, 0f, 0f)
            this.model.setTextureSize(this.textureWidth, this.textureHeight)
            this.model.mirror = true
        }

        open fun render(height: Float) {
            this.model.setRotationPoint(0f, height * 2f, 0f)
            this.model.render(1f / 32f)
        }
    }

    private val flapA: Flap = Flap()
    private val flapB: Flap = Flap()
    private val flapC: Flap = Flap()
    private val flapD: Flap = Flap()
    private val plat: Plinth = Plinth()
    private val mask: Mask = Mask()
    private val tank: Tank = Tank()
    private val pack: Pack = Pack()

    override fun render(
        tileEntity: TileEntityEmergencyBox,
        d: Double,
        d1: Double,
        d2: Double,
        f: Float,
        par9: Int,
        alpha: Float
    ) {
        val b: IBlockState = tileEntity.getWorld().getBlockState(tileEntity.getPos())
        if (b.getBlock() !is BlockEmergencyBox) return
        GlStateManager.pushMatrix()
        GlStateManager.translate(d.toFloat() + 0.5f, d1.toFloat() + 0.5f, d2.toFloat() + 0.5f)

        flapA.angle = tileEntity.getAngleA(f)
        flapB.angle = tileEntity.getAngleB(f)
        flapC.angle = tileEntity.getAngleC(f)
        flapD.angle = tileEntity.getAngleD(f)
        val height: Float = max(max(flapA.angle, flapB.angle), max(flapC.angle, flapD.angle)) / 90f

        if (height > 0f && b.getValue(BlockEmergencyBox.KIT)) {
            GlStateManager.pushMatrix()
            this.bindTexture(packTexture)
            this.pack.render(height)
            GlStateManager.rotate(180f, 1f, 0f, 0f)
            this.bindTexture(oxygenMaskTexture)
            GlStateManager.translate(0.0f, 0.0f, -0.07f)
            this.mask.render(-height)
            this.bindTexture(oxygenTankTexture)
            GlStateManager.translate(0.1f, 0.11f, 0.3f)
            this.tank.render(-height)
            GlStateManager.translate(-0.2f, 0f, 0f)
            this.tank.render(-height)
            GlStateManager.popMatrix()
        }

        this.bindTexture(boxTexture)
        this.plat.render(height)
        this.bindTexture(flapTexture)
        this.flapA.render()
        GlStateManager.rotate(90f, 0f, 1f, 0f)
        this.flapB.render()
        GlStateManager.rotate(90f, 0f, 1f, 0f)
        this.flapC.render()
        GlStateManager.rotate(90f, 0f, 1f, 0f)
        this.flapD.render()
        GlStateManager.rotate(180f, 1f, 0f, 0f)
        this.flapB.render()
        GlStateManager.rotate(90f, 0f, 1f, 0f)
        this.flapA.render()
        GlStateManager.rotate(90f, 0f, 1f, 0f)
        this.flapD.render()
        GlStateManager.rotate(90f, 0f, 1f, 0f)
        this.flapC.render()
        GlStateManager.popMatrix()
    }

    companion object {
        private const val MASKSCALE = 3f

        private val boxTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/blocks/emergency_box.png")
        private val flapTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/blocks/emergency_box_flap.png")
        private val packTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/parachute/red.png")
        private val oxygenMaskTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/oxygen.png")
        private val oxygenTankTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/player.png")
    }
}
