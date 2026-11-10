package galaxyspace.systems.SolarSystem.planets.overworld.models

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity

/**
 * ModelSolarPanel - Either Mojang or a mod author
 * Created using Tabula 4.1.1
 */
open class ModelSolarWindPanel constructor() : ModelBase() {
    @JvmField
    var Pole: ModelRenderer

    @JvmField
    var FrameFront: ModelRenderer

    @JvmField
    var FrameRight: ModelRenderer

    @JvmField
    var FrameLeft: ModelRenderer

    @JvmField
    var FrameBack: ModelRenderer

    @JvmField
    var FrameFR: ModelRenderer

    @JvmField
    var FrameFL: ModelRenderer

    @JvmField
    var FrameBL: ModelRenderer

    @JvmField
    var FrameBR: ModelRenderer

    @JvmField
    var FrameBRFLB: ModelRenderer

    @JvmField
    var FrameBRFLF: ModelRenderer

    @JvmField
    var FrameFRBLB: ModelRenderer

    @JvmField
    var FrameFRBLF: ModelRenderer

    @JvmField
    var a0F: ModelRenderer

    @JvmField
    var a1F: ModelRenderer

    @JvmField
    var a2F: ModelRenderer

    @JvmField
    var a3F: ModelRenderer

    @JvmField
    var a4F: ModelRenderer

    @JvmField
    var a5F: ModelRenderer

    @JvmField
    var a6F: ModelRenderer

    @JvmField
    var a7F: ModelRenderer

    @JvmField
    var a8F: ModelRenderer

    @JvmField
    var a9F: ModelRenderer

    @JvmField
    var a10F: ModelRenderer

    @JvmField
    var a11F: ModelRenderer

    @JvmField
    var a12F: ModelRenderer

    @JvmField
    var a13F: ModelRenderer

    @JvmField
    var a14F: ModelRenderer

    @JvmField
    var a15F: ModelRenderer

    @JvmField
    var a16F: ModelRenderer

    @JvmField
    var a17F: ModelRenderer

    @JvmField
    var a18F: ModelRenderer

    @JvmField
    var a19F: ModelRenderer

    @JvmField
    var a20F: ModelRenderer

    @JvmField
    var b0R: ModelRenderer

    @JvmField
    var b1R: ModelRenderer

    @JvmField
    var b2R: ModelRenderer

    @JvmField
    var b3R: ModelRenderer

    @JvmField
    var b4R: ModelRenderer

    @JvmField
    var b5R: ModelRenderer

    @JvmField
    var b6R: ModelRenderer

    @JvmField
    var b7R: ModelRenderer

    @JvmField
    var b8R: ModelRenderer

    @JvmField
    var b9R: ModelRenderer

    @JvmField
    var b10R: ModelRenderer

    @JvmField
    var b11R: ModelRenderer

    @JvmField
    var b12R: ModelRenderer

    @JvmField
    var b13R: ModelRenderer

    @JvmField
    var b14R: ModelRenderer

    @JvmField
    var b15R: ModelRenderer

    @JvmField
    var b16R: ModelRenderer

    @JvmField
    var b17R: ModelRenderer

    @JvmField
    var b18R: ModelRenderer

    @JvmField
    var b19R: ModelRenderer

    @JvmField
    var b20R: ModelRenderer

    @JvmField
    var c0L: ModelRenderer

    @JvmField
    var c1L: ModelRenderer

    @JvmField
    var c2L: ModelRenderer

    @JvmField
    var c3L: ModelRenderer

    @JvmField
    var c4L: ModelRenderer

    @JvmField
    var c5L: ModelRenderer

    @JvmField
    var c6L: ModelRenderer

    @JvmField
    var c7L: ModelRenderer

    @JvmField
    var c8L: ModelRenderer

    @JvmField
    var c9L: ModelRenderer

    @JvmField
    var c10L: ModelRenderer

    @JvmField
    var c11L: ModelRenderer

    @JvmField
    var c12L: ModelRenderer

    @JvmField
    var c13L: ModelRenderer

    @JvmField
    var c14L: ModelRenderer

    @JvmField
    var c15L: ModelRenderer

    @JvmField
    var c16L: ModelRenderer

    @JvmField
    var c17L: ModelRenderer

    @JvmField
    var c18L: ModelRenderer

    @JvmField
    var c19L: ModelRenderer

    @JvmField
    var c20L: ModelRenderer

    @JvmField
    var d0P: ModelRenderer

    @JvmField
    var d1P: ModelRenderer

    @JvmField
    var d2P: ModelRenderer

    @JvmField
    var d3P: ModelRenderer

    @JvmField
    var d4P: ModelRenderer

    @JvmField
    var d5P: ModelRenderer

    @JvmField
    var d6P: ModelRenderer

    @JvmField
    var d7P: ModelRenderer

    @JvmField
    var d8P: ModelRenderer

    @JvmField
    var d9P: ModelRenderer

    @JvmField
    var d10P: ModelRenderer

    @JvmField
    var d11P: ModelRenderer

    @JvmField
    var d12P: ModelRenderer

    @JvmField
    var d13P: ModelRenderer

    @JvmField
    var d14P: ModelRenderer

    @JvmField
    var d15P: ModelRenderer

    @JvmField
    var d16P: ModelRenderer

    @JvmField
    var d17P: ModelRenderer

    @JvmField
    var d18P: ModelRenderer

    @JvmField
    var d19P: ModelRenderer

    @JvmField
    var d20P: ModelRenderer

    init {
        this.textureWidth = 130
        this.textureHeight = 55
        this.c4L = ModelRenderer(this, 0, 0)
        this.c4L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c4L.addBox(-4.0f, -0.99f, -6.0f, 8, 1, 1, 0.0f)
        this.setRotateAngle(c4L, 0.0f, -1.5707963267948966f, 0.0f)
        this.c15L = ModelRenderer(this, 0, 0)
        this.c15L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c15L.addBox(-15.0f, -0.99f, -17.0f, 30, 1, 1, 0.0f)
        this.setRotateAngle(c15L, 0.0f, -1.5707963267948966f, 0.0f)
        this.d7P = ModelRenderer(this, 0, 0)
        this.d7P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d7P.addBox(-7.0f, -0.99f, -9.0f, 14, 1, 1, 0.0f)
        this.setRotateAngle(d7P, 0.0f, 3.141592653589793f, 0.0f)
        this.d3P = ModelRenderer(this, 0, 0)
        this.d3P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d3P.addBox(-3.0f, -0.99f, -5.0f, 6, 1, 1, 0.0f)
        this.setRotateAngle(d3P, 0.0f, 3.141592653589793f, 0.0f)
        this.d9P = ModelRenderer(this, 0, 0)
        this.d9P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d9P.addBox(-9.0f, -0.99f, -11.0f, 18, 1, 1, 0.0f)
        this.setRotateAngle(d9P, 0.0f, 3.141592653589793f, 0.0f)
        this.d18P = ModelRenderer(this, 0, 0)
        this.d18P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d18P.addBox(-18.0f, -0.99f, -20.0f, 36, 1, 1, 0.0f)
        this.setRotateAngle(d18P, 0.0f, 3.141592653589793f, 0.0f)
        this.c3L = ModelRenderer(this, 0, 0)
        this.c3L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c3L.addBox(-3.0f, -0.99f, -5.0f, 6, 1, 1, 0.0f)
        this.setRotateAngle(c3L, 0.0f, -1.5707963267948966f, 0.0f)
        this.a11F = ModelRenderer(this, 0, 0)
        this.a11F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a11F.addBox(-11.0f, -0.99f, -13.0f, 22, 1, 1, 0.0f)
        this.b6R = ModelRenderer(this, 0, 0)
        this.b6R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b6R.addBox(-6.0f, -0.99f, -8.0f, 12, 1, 1, 0.0f)
        this.setRotateAngle(b6R, 0.0f, 1.5707963267948966f, 0.0f)
        this.b14R = ModelRenderer(this, 0, 0)
        this.b14R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b14R.addBox(-14.0f, -0.99f, -16.0f, 28, 1, 1, 0.0f)
        this.setRotateAngle(b14R, 0.0f, 1.5707963267948966f, 0.0f)
        this.a15F = ModelRenderer(this, 0, 0)
        this.a15F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a15F.addBox(-15.0f, -0.99f, -17.0f, 30, 1, 1, 0.0f)
        this.FrameBack = ModelRenderer(this, 0, 6)
        this.FrameBack.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.FrameBack.addBox(-24.0f, -1.0f, 23.0f, 48, 1, 1, 0.0f)
        this.b10R = ModelRenderer(this, 0, 0)
        this.b10R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b10R.addBox(-10.0f, -0.99f, -12.0f, 20, 1, 1, 0.0f)
        this.setRotateAngle(b10R, 0.0f, 1.5707963267948966f, 0.0f)
        this.d8P = ModelRenderer(this, 0, 0)
        this.d8P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d8P.addBox(-8.0f, -0.99f, -10.0f, 16, 1, 1, 0.0f)
        this.setRotateAngle(d8P, 0.0f, 3.141592653589793f, 0.0f)
        this.d20P = ModelRenderer(this, 0, 0)
        this.d20P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d20P.addBox(-20.0f, -0.99f, -22.0f, 40, 1, 1, 0.0f)
        this.setRotateAngle(d20P, 0.0f, 3.141592653589793f, 0.0f)
        this.d15P = ModelRenderer(this, 0, 0)
        this.d15P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d15P.addBox(-15.0f, -0.99f, -17.0f, 30, 1, 1, 0.0f)
        this.setRotateAngle(d15P, 0.0f, 3.141592653589793f, 0.0f)
        this.b12R = ModelRenderer(this, 0, 0)
        this.b12R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b12R.addBox(-12.0f, -0.99f, -14.0f, 24, 1, 1, 0.0f)
        this.setRotateAngle(b12R, 0.0f, 1.5707963267948966f, 0.0f)
        this.d1P = ModelRenderer(this, 0, 0)
        this.d1P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d1P.addBox(-1.0f, -0.99f, -3.0f, 2, 1, 1, 0.0f)
        this.setRotateAngle(d1P, 0.0f, 3.141592653589793f, 0.0f)
        this.FrameFL = ModelRenderer(this, 0, 10)
        this.FrameFL.setRotationPoint(0.0f, 8.0f, 0.0f)
        this.FrameFL.addBox(-0.25f, -0.5f, -0.5f, 34, 1, 1, 0.0f)
        this.setRotateAngle(FrameFL, -0.24434609527920614f, 0.7539822368615504f, -0.3490658503988659f)
        this.d17P = ModelRenderer(this, 0, 0)
        this.d17P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d17P.addBox(-17.0f, -0.99f, -19.0f, 34, 1, 1, 0.0f)
        this.setRotateAngle(d17P, 0.0f, 3.141592653589793f, 0.0f)
        this.FrameBRFLB = ModelRenderer(this, 0, 16)
        this.FrameBRFLB.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.FrameBRFLB.addBox(-32.0f, -1.0f, 0.5f, 64, 1, 1, 0.0f)
        this.setRotateAngle(FrameBRFLB, 0.0f, 0.7853981633974483f, 0.0f)
        this.a19F = ModelRenderer(this, 0, 0)
        this.a19F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a19F.addBox(-19.0f, -0.99f, -21.0f, 38, 1, 1, 0.0f)
        this.FrameFRBLB = ModelRenderer(this, 0, 20)
        this.FrameFRBLB.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.FrameFRBLB.addBox(-32.0f, -1.0f, 0.5f, 64, 1, 1, 0.0f)
        this.setRotateAngle(FrameFRBLB, 0.0f, -0.7853981633974483f, 0.0f)
        this.b17R = ModelRenderer(this, 0, 0)
        this.b17R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b17R.addBox(-17.0f, -0.99f, -19.0f, 34, 1, 1, 0.0f)
        this.setRotateAngle(b17R, 0.0f, 1.5707963267948966f, 0.0f)
        this.d4P = ModelRenderer(this, 0, 0)
        this.d4P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d4P.addBox(-4.0f, -0.99f, -6.0f, 8, 1, 1, 0.0f)
        this.setRotateAngle(d4P, 0.0f, 3.141592653589793f, 0.0f)
        this.a9F = ModelRenderer(this, 0, 0)
        this.a9F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a9F.addBox(-9.0f, -0.99f, -11.0f, 18, 1, 1, 0.0f)
        this.a10F = ModelRenderer(this, 0, 0)
        this.a10F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a10F.addBox(-10.0f, -0.99f, -12.0f, 20, 1, 1, 0.0f)
        this.FrameBR = ModelRenderer(this, 0, 14)
        this.FrameBR.setRotationPoint(0.0f, 8.0f, 0.0f)
        this.FrameBR.addBox(-0.25f, -0.5f, -0.5f, 34, 1, 1, 0.0f)
        this.setRotateAngle(FrameBR, -0.24434609527920614f, -2.3876104167282426f, 0.3490658503988659f)
        this.b19R = ModelRenderer(this, 0, 0)
        this.b19R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b19R.addBox(-19.0f, -0.99f, -21.0f, 38, 1, 1, 0.0f)
        this.setRotateAngle(b19R, 0.0f, 1.5707963267948966f, 0.0f)
        this.a16F = ModelRenderer(this, 0, 0)
        this.a16F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a16F.addBox(-16.0f, -0.99f, -18.0f, 32, 1, 1, 0.0f)
        this.b15R = ModelRenderer(this, 0, 0)
        this.b15R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b15R.addBox(-15.0f, -0.99f, -17.0f, 30, 1, 1, 0.0f)
        this.setRotateAngle(b15R, 0.0f, 1.5707963267948966f, 0.0f)
        this.a17F = ModelRenderer(this, 0, 0)
        this.a17F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a17F.addBox(-17.0f, -0.99f, -19.0f, 34, 1, 1, 0.0f)
        this.FrameFR = ModelRenderer(this, 0, 8)
        this.FrameFR.setRotationPoint(0.0f, 8.0f, 0.0f)
        this.FrameFR.addBox(-0.25f, -0.5f, -0.5f, 34, 1, 1, 0.0f)
        this.setRotateAngle(FrameFR, 0.24434609527920614f, 2.3876104167282426f, 0.3490658503988659f)
        this.b2R = ModelRenderer(this, 0, 0)
        this.b2R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b2R.addBox(-2.0f, -0.99f, -4.0f, 4, 1, 1, 0.0f)
        this.setRotateAngle(b2R, 0.0f, 1.5707963267948966f, 0.0f)
        this.c5L = ModelRenderer(this, 0, 0)
        this.c5L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c5L.addBox(-5.0f, -0.99f, -7.0f, 10, 1, 1, 0.0f)
        this.setRotateAngle(c5L, 0.0f, -1.5707963267948966f, 0.0f)
        this.b13R = ModelRenderer(this, 0, 0)
        this.b13R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b13R.addBox(-13.0f, -0.99f, -15.0f, 26, 1, 1, 0.0f)
        this.setRotateAngle(b13R, 0.0f, 1.5707963267948966f, 0.0f)
        this.d13P = ModelRenderer(this, 0, 0)
        this.d13P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d13P.addBox(-13.0f, -0.99f, -15.0f, 26, 1, 1, 0.0f)
        this.setRotateAngle(d13P, 0.0f, 3.141592653589793f, 0.0f)
        this.a5F = ModelRenderer(this, 0, 0)
        this.a5F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a5F.addBox(-5.0f, -0.99f, -7.0f, 10, 1, 1, 0.0f)
        this.d12P = ModelRenderer(this, 0, 0)
        this.d12P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d12P.addBox(-12.0f, -0.99f, -14.0f, 24, 1, 1, 0.0f)
        this.setRotateAngle(d12P, 0.0f, 3.141592653589793f, 0.0f)
        this.b4R = ModelRenderer(this, 0, 0)
        this.b4R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b4R.addBox(-4.0f, -0.99f, -6.0f, 8, 1, 1, 0.0f)
        this.setRotateAngle(b4R, 0.0f, 1.5707963267948966f, 0.0f)
        this.d11P = ModelRenderer(this, 0, 0)
        this.d11P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d11P.addBox(-11.0f, -0.99f, -13.0f, 22, 1, 1, 0.0f)
        this.setRotateAngle(d11P, 0.0f, 3.141592653589793f, 0.0f)
        this.c19L = ModelRenderer(this, 0, 0)
        this.c19L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c19L.addBox(-19.0f, -0.99f, -21.0f, 38, 1, 1, 0.0f)
        this.setRotateAngle(c19L, 0.0f, -1.5707963267948966f, 0.0f)
        this.c8L = ModelRenderer(this, 0, 0)
        this.c8L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c8L.addBox(-8.0f, -0.99f, -10.0f, 16, 1, 1, 0.0f)
        this.setRotateAngle(c8L, 0.0f, -1.5707963267948966f, 0.0f)
        this.a3F = ModelRenderer(this, 0, 0)
        this.a3F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a3F.addBox(-3.0f, -0.99f, -5.0f, 6, 1, 1, 0.0f)
        this.a0F = ModelRenderer(this, 0, 0)
        this.a0F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a0F.addBox(-21.0f, -0.99f, -23.0f, 42, 1, 1, 0.0f)
        this.c0L = ModelRenderer(this, 0, 0)
        this.c0L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c0L.addBox(-21.0f, -0.99f, -23.0f, 42, 1, 1, 0.0f)
        this.setRotateAngle(c0L, 0.0f, -1.5707963267948966f, 0.0f)
        this.d14P = ModelRenderer(this, 0, 0)
        this.d14P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d14P.addBox(-14.0f, -0.99f, -16.0f, 28, 1, 1, 0.0f)
        this.setRotateAngle(d14P, 0.0f, 3.141592653589793f, 0.0f)
        this.FrameLeft = ModelRenderer(this, 0, 4)
        this.FrameLeft.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.FrameLeft.addBox(-23.0f, -1.0f, -24.0f, 46, 1, 1, 0.0f)
        this.setRotateAngle(FrameLeft, 0.0f, -1.5707963267948966f, 0.0f)
        this.a6F = ModelRenderer(this, 0, 0)
        this.a6F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a6F.addBox(-6.0f, -0.99f, -8.0f, 12, 1, 1, 0.0f)
        this.b7R = ModelRenderer(this, 0, 0)
        this.b7R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b7R.addBox(-7.0f, -0.99f, -9.0f, 14, 1, 1, 0.0f)
        this.setRotateAngle(b7R, 0.0f, 1.5707963267948966f, 0.0f)
        this.d19P = ModelRenderer(this, 0, 0)
        this.d19P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d19P.addBox(-19.0f, -0.99f, -21.0f, 38, 1, 1, 0.0f)
        this.setRotateAngle(d19P, 0.0f, 3.141592653589793f, 0.0f)
        this.a7F = ModelRenderer(this, 0, 0)
        this.a7F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a7F.addBox(-7.0f, -0.99f, -9.0f, 14, 1, 1, 0.0f)
        this.c12L = ModelRenderer(this, 0, 0)
        this.c12L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c12L.addBox(-12.0f, -0.99f, -14.0f, 24, 1, 1, 0.0f)
        this.setRotateAngle(c12L, 0.0f, -1.5707963267948966f, 0.0f)
        this.d6P = ModelRenderer(this, 0, 0)
        this.d6P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d6P.addBox(-6.0f, -0.99f, -8.0f, 12, 1, 1, 0.0f)
        this.setRotateAngle(d6P, 0.0f, 3.141592653589793f, 0.0f)
        this.b9R = ModelRenderer(this, 0, 0)
        this.b9R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b9R.addBox(-9.0f, -0.99f, -11.0f, 18, 1, 1, 0.0f)
        this.setRotateAngle(b9R, 0.0f, 1.5707963267948966f, 0.0f)
        this.FrameBRFLF = ModelRenderer(this, 0, 18)
        this.FrameBRFLF.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.FrameBRFLF.addBox(-32.0f, -1.0f, -1.5f, 64, 1, 1, 0.0f)
        this.setRotateAngle(FrameBRFLF, 0.0f, 0.7853981633974483f, 0.0f)
        this.c11L = ModelRenderer(this, 0, 0)
        this.c11L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c11L.addBox(-11.0f, -0.99f, -13.0f, 22, 1, 1, 0.0f)
        this.setRotateAngle(c11L, 0.0f, -1.5707963267948966f, 0.0f)
        this.c10L = ModelRenderer(this, 0, 0)
        this.c10L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c10L.addBox(-10.0f, -0.99f, -12.0f, 20, 1, 1, 0.0f)
        this.setRotateAngle(c10L, 0.0f, -1.5707963267948966f, 0.0f)
        this.b11R = ModelRenderer(this, 0, 0)
        this.b11R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b11R.addBox(-11.0f, -0.99f, -13.0f, 22, 1, 1, 0.0f)
        this.setRotateAngle(b11R, 0.0f, 1.5707963267948966f, 0.0f)
        this.b0R = ModelRenderer(this, 0, 0)
        this.b0R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b0R.addBox(-21.0f, -0.99f, -23.0f, 42, 1, 1, 0.0f)
        this.setRotateAngle(b0R, 0.0f, 1.5707963267948966f, 0.0f)
        this.c7L = ModelRenderer(this, 0, 0)
        this.c7L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c7L.addBox(-7.0f, -0.99f, -9.0f, 14, 1, 1, 0.0f)
        this.setRotateAngle(c7L, 0.0f, -1.5707963267948966f, 0.0f)
        this.a13F = ModelRenderer(this, 0, 0)
        this.a13F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a13F.addBox(-13.0f, -0.99f, -15.0f, 26, 1, 1, 0.0f)
        this.a4F = ModelRenderer(this, 0, 0)
        this.a4F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a4F.addBox(-4.0f, -0.99f, -6.0f, 8, 1, 1, 0.0f)
        this.b8R = ModelRenderer(this, 0, 0)
        this.b8R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b8R.addBox(-8.0f, -0.99f, -10.0f, 16, 1, 1, 0.0f)
        this.setRotateAngle(b8R, 0.0f, 1.5707963267948966f, 0.0f)
        this.c18L = ModelRenderer(this, 0, 0)
        this.c18L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c18L.addBox(-18.0f, -0.99f, -20.0f, 36, 1, 1, 0.0f)
        this.setRotateAngle(c18L, 0.0f, -1.5707963267948966f, 0.0f)
        this.d0P = ModelRenderer(this, 0, 0)
        this.d0P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d0P.addBox(-21.0f, -0.99f, -23.0f, 42, 1, 1, 0.0f)
        this.setRotateAngle(d0P, 0.0f, 3.141592653589793f, 0.0f)
        this.FrameRight = ModelRenderer(this, 0, 2)
        this.FrameRight.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.FrameRight.addBox(-23.0f, -1.0f, -24.0f, 46, 1, 1, 0.0f)
        this.setRotateAngle(FrameRight, 0.0f, 1.5707963267948966f, 0.0f)
        this.FrameFront = ModelRenderer(this, 0, 24)
        this.FrameFront.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.FrameFront.addBox(-24.0f, -1.0f, -24.0f, 48, 1, 1, 0.0f)
        this.FrameFRBLF = ModelRenderer(this, 0, 22)
        this.FrameFRBLF.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.FrameFRBLF.addBox(-32.0f, -1.0f, -1.5f, 64, 1, 1, 0.0f)
        this.setRotateAngle(FrameFRBLF, 0.0f, -0.7853981633974483f, 0.0f)
        this.c20L = ModelRenderer(this, 0, 0)
        this.c20L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c20L.addBox(-20.0f, -0.99f, -22.0f, 40, 1, 1, 0.0f)
        this.setRotateAngle(c20L, 0.0f, -1.5707963267948966f, 0.0f)
        this.b16R = ModelRenderer(this, 0, 0)
        this.b16R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b16R.addBox(-16.0f, -0.99f, -18.0f, 32, 1, 1, 0.0f)
        this.setRotateAngle(b16R, 0.0f, 1.5707963267948966f, 0.0f)
        this.d16P = ModelRenderer(this, 0, 0)
        this.d16P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d16P.addBox(-16.0f, -0.99f, -18.0f, 32, 1, 1, 0.0f)
        this.setRotateAngle(d16P, 0.0f, 3.141592653589793f, 0.0f)
        this.a1F = ModelRenderer(this, 0, 0)
        this.a1F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a1F.addBox(-1.0f, -0.99f, -3.0f, 2, 1, 1, 0.0f)
        this.c13L = ModelRenderer(this, 0, 0)
        this.c13L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c13L.addBox(-13.0f, -0.99f, -15.0f, 26, 1, 1, 0.0f)
        this.setRotateAngle(c13L, 0.0f, -1.5707963267948966f, 0.0f)
        this.d10P = ModelRenderer(this, 0, 0)
        this.d10P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d10P.addBox(-10.0f, -0.99f, -12.0f, 20, 1, 1, 0.0f)
        this.setRotateAngle(d10P, 0.0f, 3.141592653589793f, 0.0f)
        this.a2F = ModelRenderer(this, 0, 0)
        this.a2F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a2F.addBox(-2.0f, -0.99f, -4.0f, 4, 1, 1, 0.0f)
        this.Pole = ModelRenderer(this, 0, 26)
        this.Pole.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.Pole.addBox(-1.5f, -2.0f, -1.5f, 3, 26, 3, 0.0f)
        this.a18F = ModelRenderer(this, 0, 0)
        this.a18F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a18F.addBox(-18.0f, -0.99f, -20.0f, 36, 1, 1, 0.0f)
        this.a8F = ModelRenderer(this, 0, 0)
        this.a8F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a8F.addBox(-8.0f, -0.99f, -10.0f, 16, 1, 1, 0.0f)
        this.d2P = ModelRenderer(this, 0, 0)
        this.d2P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d2P.addBox(-2.0f, -0.99f, -4.0f, 4, 1, 1, 0.0f)
        this.setRotateAngle(d2P, 0.0f, 3.141592653589793f, 0.0f)
        this.c17L = ModelRenderer(this, 0, 0)
        this.c17L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c17L.addBox(-17.0f, -0.99f, -19.0f, 34, 1, 1, 0.0f)
        this.setRotateAngle(c17L, 0.0f, -1.5707963267948966f, 0.0f)
        this.a14F = ModelRenderer(this, 0, 0)
        this.a14F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a14F.addBox(-14.0f, -0.99f, -16.0f, 28, 1, 1, 0.0f)
        this.c16L = ModelRenderer(this, 0, 0)
        this.c16L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c16L.addBox(-16.0f, -0.99f, -18.0f, 32, 1, 1, 0.0f)
        this.setRotateAngle(c16L, 0.0f, -1.5707963267948966f, 0.0f)
        this.d5P = ModelRenderer(this, 0, 0)
        this.d5P.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.d5P.addBox(-5.0f, -0.99f, -7.0f, 10, 1, 1, 0.0f)
        this.setRotateAngle(d5P, 0.0f, 3.141592653589793f, 0.0f)
        this.b20R = ModelRenderer(this, 0, 0)
        this.b20R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b20R.addBox(-20.0f, -0.99f, -22.0f, 40, 1, 1, 0.0f)
        this.setRotateAngle(b20R, 0.0f, 1.5707963267948966f, 0.0f)
        this.a20F = ModelRenderer(this, 0, 0)
        this.a20F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a20F.addBox(-20.0f, -0.99f, -22.0f, 40, 1, 1, 0.0f)
        this.b5R = ModelRenderer(this, 0, 0)
        this.b5R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b5R.addBox(-5.0f, -0.99f, -7.0f, 10, 1, 1, 0.0f)
        this.setRotateAngle(b5R, 0.0f, 1.5707963267948966f, 0.0f)
        this.b3R = ModelRenderer(this, 0, 0)
        this.b3R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b3R.addBox(-3.0f, -0.99f, -5.0f, 6, 1, 1, 0.0f)
        this.setRotateAngle(b3R, 0.0f, 1.5707963267948966f, 0.0f)
        this.a12F = ModelRenderer(this, 0, 0)
        this.a12F.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.a12F.addBox(-12.0f, -0.99f, -14.0f, 24, 1, 1, 0.0f)
        this.b18R = ModelRenderer(this, 0, 0)
        this.b18R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b18R.addBox(-18.0f, -0.99f, -20.0f, 36, 1, 1, 0.0f)
        this.setRotateAngle(b18R, 0.0f, 1.5707963267948966f, 0.0f)
        this.c1L = ModelRenderer(this, 0, 0)
        this.c1L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c1L.addBox(-1.0f, -0.99f, -3.0f, 2, 1, 1, 0.0f)
        this.setRotateAngle(c1L, 0.0f, -1.5707963267948966f, 0.0f)
        this.c6L = ModelRenderer(this, 0, 0)
        this.c6L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c6L.addBox(-6.0f, -0.99f, -8.0f, 12, 1, 1, 0.0f)
        this.setRotateAngle(c6L, 0.0f, -1.5707963267948966f, 0.0f)
        this.FrameBL = ModelRenderer(this, 0, 12)
        this.FrameBL.setRotationPoint(0.0f, 8.0f, 0.0f)
        this.FrameBL.addBox(-0.25f, -0.5f, -0.5f, 34, 1, 1, 0.0f)
        this.setRotateAngle(FrameBL, 0.24434609527920614f, -0.7539822368615504f, -0.3490658503988659f)
        this.b1R = ModelRenderer(this, 0, 0)
        this.b1R.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.b1R.addBox(-1.0f, -0.99f, -3.0f, 2, 1, 1, 0.0f)
        this.setRotateAngle(b1R, 0.0f, 1.5707963267948966f, 0.0f)
        this.c9L = ModelRenderer(this, 0, 0)
        this.c9L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c9L.addBox(-9.0f, -0.99f, -11.0f, 18, 1, 1, 0.0f)
        this.setRotateAngle(c9L, 0.0f, -1.5707963267948966f, 0.0f)
        this.c14L = ModelRenderer(this, 0, 0)
        this.c14L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c14L.addBox(-14.0f, -0.99f, -16.0f, 28, 1, 1, 0.0f)
        this.setRotateAngle(c14L, 0.0f, -1.5707963267948966f, 0.0f)
        this.c2L = ModelRenderer(this, 0, 0)
        this.c2L.setRotationPoint(0.0f, 0.0f, 0.0f)
        this.c2L.addBox(-2.0f, -0.99f, -4.0f, 4, 1, 1, 0.0f)
        this.setRotateAngle(c2L, 0.0f, -1.5707963267948966f, 0.0f)
    }

    override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        this.c4L.render(f5)
        this.c15L.render(f5)
        this.d7P.render(f5)
        this.d3P.render(f5)
        this.d9P.render(f5)
        this.d18P.render(f5)
        this.c3L.render(f5)
        this.a11F.render(f5)
        this.b6R.render(f5)
        this.b14R.render(f5)
        this.a15F.render(f5)
        this.FrameBack.render(f5)
        this.b10R.render(f5)
        this.d8P.render(f5)
        this.d20P.render(f5)
        this.d15P.render(f5)
        this.b12R.render(f5)
        this.d1P.render(f5)
        this.FrameFL.render(f5)
        this.d17P.render(f5)
        this.FrameBRFLB.render(f5)
        this.a19F.render(f5)
        this.FrameFRBLB.render(f5)
        this.b17R.render(f5)
        this.d4P.render(f5)
        this.a9F.render(f5)
        this.a10F.render(f5)
        this.FrameBR.render(f5)
        this.b19R.render(f5)
        this.a16F.render(f5)
        this.b15R.render(f5)
        this.a17F.render(f5)
        this.FrameFR.render(f5)
        this.b2R.render(f5)
        this.c5L.render(f5)
        this.b13R.render(f5)
        this.d13P.render(f5)
        this.a5F.render(f5)
        this.d12P.render(f5)
        this.b4R.render(f5)
        this.d11P.render(f5)
        this.c19L.render(f5)
        this.c8L.render(f5)
        this.a3F.render(f5)
        this.a0F.render(f5)
        this.c0L.render(f5)
        this.d14P.render(f5)
        this.FrameLeft.render(f5)
        this.a6F.render(f5)
        this.b7R.render(f5)
        this.d19P.render(f5)
        this.a7F.render(f5)
        this.c12L.render(f5)
        this.d6P.render(f5)
        this.b9R.render(f5)
        this.FrameBRFLF.render(f5)
        this.c11L.render(f5)
        this.c10L.render(f5)
        this.b11R.render(f5)
        this.b0R.render(f5)
        this.c7L.render(f5)
        this.a13F.render(f5)
        this.a4F.render(f5)
        this.b8R.render(f5)
        this.c18L.render(f5)
        this.d0P.render(f5)
        this.FrameRight.render(f5)
        this.FrameFront.render(f5)
        this.FrameFRBLF.render(f5)
        this.c20L.render(f5)
        this.b16R.render(f5)
        this.d16P.render(f5)
        this.a1F.render(f5)
        this.c13L.render(f5)
        this.d10P.render(f5)
        this.a2F.render(f5)
        this.Pole.render(f5)
        this.a18F.render(f5)
        this.a8F.render(f5)
        this.d2P.render(f5)
        this.c17L.render(f5)
        this.a14F.render(f5)
        this.c16L.render(f5)
        this.d5P.render(f5)
        this.b20R.render(f5)
        this.a20F.render(f5)
        this.b5R.render(f5)
        this.b3R.render(f5)
        this.a12F.render(f5)
        this.b18R.render(f5)
        this.c1L.render(f5)
        this.c6L.render(f5)
        this.FrameBL.render(f5)
        this.b1R.render(f5)
        this.c9L.render(f5)
        this.c14L.render(f5)
        this.c2L.render(f5)
    }

    open fun renderPole() {
        this.Pole.render(0.0625f)
    }


    open fun renderPanel() {
        this.a0F.render(0.0625f)
        this.a1F.render(0.0625f)
        this.a2F.render(0.0625f)
        this.a3F.render(0.0625f)
        this.a4F.render(0.0625f)
        this.a5F.render(0.0625f)
        this.a6F.render(0.0625f)
        this.a7F.render(0.0625f)
        this.a8F.render(0.0625f)
        this.a9F.render(0.0625f)
        this.a10F.render(0.0625f)
        this.a11F.render(0.0625f)
        this.a12F.render(0.0625f)
        this.a13F.render(0.0625f)
        this.a14F.render(0.0625f)
        this.a15F.render(0.0625f)
        this.a16F.render(0.0625f)
        this.a17F.render(0.0625f)
        this.a18F.render(0.0625f)
        this.a19F.render(0.0625f)
        this.a20F.render(0.0625f)

        this.b0R.render(0.0625f)
        this.b1R.render(0.0625f)
        this.b2R.render(0.0625f)
        this.b3R.render(0.0625f)
        this.b4R.render(0.0625f)
        this.b5R.render(0.0625f)
        this.b6R.render(0.0625f)
        this.b7R.render(0.0625f)
        this.b8R.render(0.0625f)
        this.b9R.render(0.0625f)
        this.b10R.render(0.0625f)
        this.b11R.render(0.0625f)
        this.b12R.render(0.0625f)
        this.b13R.render(0.0625f)
        this.b14R.render(0.0625f)
        this.b15R.render(0.0625f)
        this.b16R.render(0.0625f)
        this.b17R.render(0.0625f)
        this.b18R.render(0.0625f)
        this.b19R.render(0.0625f)
        this.b20R.render(0.0625f)

        this.c0L.render(0.0625f)
        this.c1L.render(0.0625f)
        this.c2L.render(0.0625f)
        this.c3L.render(0.0625f)
        this.c4L.render(0.0625f)
        this.c5L.render(0.0625f)
        this.c6L.render(0.0625f)
        this.c7L.render(0.0625f)
        this.c8L.render(0.0625f)
        this.c9L.render(0.0625f)
        this.c10L.render(0.0625f)
        this.c11L.render(0.0625f)
        this.c12L.render(0.0625f)
        this.c13L.render(0.0625f)
        this.c14L.render(0.0625f)
        this.c15L.render(0.0625f)
        this.c16L.render(0.0625f)
        this.c17L.render(0.0625f)
        this.c18L.render(0.0625f)
        this.c19L.render(0.0625f)
        this.c20L.render(0.0625f)

        this.d0P.render(0.0625f)
        this.d1P.render(0.0625f)
        this.d2P.render(0.0625f)
        this.d3P.render(0.0625f)
        this.d4P.render(0.0625f)
        this.d5P.render(0.0625f)
        this.d6P.render(0.0625f)
        this.d7P.render(0.0625f)
        this.d8P.render(0.0625f)
        this.d9P.render(0.0625f)
        this.d10P.render(0.0625f)
        this.d11P.render(0.0625f)
        this.d12P.render(0.0625f)
        this.d13P.render(0.0625f)
        this.d14P.render(0.0625f)
        this.d15P.render(0.0625f)
        this.d16P.render(0.0625f)
        this.d17P.render(0.0625f)
        this.d18P.render(0.0625f)
        this.d19P.render(0.0625f)
        this.d20P.render(0.0625f)

        this.FrameRight.render(0.0625f)
        this.FrameFR.render(0.0625f)
        this.FrameFL.render(0.0625f)
        this.FrameBL.render(0.0625f)
        this.FrameBR.render(0.0625f)
        this.FrameBRFLB.render(0.0625f)
        this.FrameBRFLF.render(0.0625f)
        this.FrameFRBLB.render(0.0625f)
        this.FrameFRBLF.render(0.0625f)
        this.FrameBack.render(0.0625f)
        this.FrameFront.render(0.0625f)
        this.FrameLeft.render(0.0625f)
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    open fun setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
        modelRenderer.rotateAngleX = x
        modelRenderer.rotateAngleY = y
        modelRenderer.rotateAngleZ = z
    }
}
