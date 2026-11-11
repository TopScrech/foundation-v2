package micdoodle8.mods.galacticraft.core.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.world.entity.Entity

open class ModelLander constructor() : ModelBase() {
    @JvmField
    var bodyMain1: ModelRendererGC

    @JvmField
    var bodyMain2: ModelRendererGC

    @JvmField
    var bodyAux1: ModelRendererGC

    @JvmField
    var bodyAux2: ModelRendererGC

    @JvmField
    var bodyAux3: ModelRendererGC

    @JvmField
    var bodyAux4: ModelRendererGC

    @JvmField
    var bodySupportMain1: ModelRendererGC

    @JvmField
    var bodySupportMain2: ModelRendererGC

    @JvmField
    var legStructureAux1e: ModelRendererGC

    @JvmField
    var footBaseg: ModelRendererGC

    @JvmField
    var legStructure1i: ModelRendererGC

    @JvmField
    var legBase1j: ModelRendererGC

    @JvmField
    var footJointk: ModelRendererGC

    @JvmField
    var footBasel: ModelRendererGC

    @JvmField
    var legBase1n: ModelRendererGC

    @JvmField
    var legStructureAux1s: ModelRendererGC

    @JvmField
    var legStructure1u: ModelRendererGC

    @JvmField
    var footJointv: ModelRendererGC

    @JvmField
    var footBasew: ModelRendererGC

    @JvmField
    var footBasex: ModelRendererGC

    @JvmField
    var legBase1z: ModelRendererGC

    @JvmField
    var legStructureAux1f: ModelRendererGC

    @JvmField
    var legStructure1g: ModelRendererGC

    @JvmField
    var footJointh: ModelRendererGC

    @JvmField
    var footBasei: ModelRendererGC

    @JvmField
    var footBasej: ModelRendererGC

    @JvmField
    var legBase1l: ModelRendererGC

    @JvmField
    var legStructureAux1q: ModelRendererGC

    @JvmField
    var legStructure1s: ModelRendererGC

    @JvmField
    var footJointt: ModelRendererGC

    @JvmField
    var footBaseu: ModelRendererGC

    @JvmField
    var footBasev: ModelRendererGC

    @JvmField
    var moduleMain1: ModelRendererGC

    @JvmField
    var moduleMain2: ModelRendererGC

    @JvmField
    var moduleMainDeco: ModelRendererGC

    @JvmField
    var moduleCore1: ModelRendererGC

    @JvmField
    var moduleCore2: ModelRendererGC

    @JvmField
    var moduleCore3: ModelRendererGC

    @JvmField
    var moduleAux1: ModelRendererGC

    @JvmField
    var moduleAux2: ModelRendererGC

    @JvmField
    var moduleAux3: ModelRendererGC

    @JvmField
    var moduleAux4: ModelRendererGC

    @JvmField
    var moduleAux5: ModelRendererGC

    @JvmField
    var moduleAux6: ModelRendererGC

    @JvmField
    var moduleAux7: ModelRendererGC

    @JvmField
    var moduleAux8: ModelRendererGC

    @JvmField
    var moduleThrusterBase1: ModelRendererGC

    @JvmField
    var moduleThrusterBase2: ModelRendererGC

    @JvmField
    var moduleThrusterBase3: ModelRendererGC

    @JvmField
    var moduleThrusterBase4: ModelRendererGC

    @JvmField
    var moduleThrusterBase5: ModelRendererGC

    @JvmField
    var moduleThrusterConeTop1: ModelRendererGC

    @JvmField
    var moduleThrusterConeTop2: ModelRendererGC

    @JvmField
    var moduleThrusterConeTop3: ModelRendererGC

    @JvmField
    var moduleThrusterConeBack1: ModelRendererGC

    @JvmField
    var moduleThrusterConeBack2: ModelRendererGC

    @JvmField
    var moduleThrusterConeBack3: ModelRendererGC

    @JvmField
    var moduleThrusterConeBot1: ModelRendererGC

    @JvmField
    var moduleThrusterConeBot2: ModelRendererGC

    @JvmField
    var moduleThrusterConeBot3: ModelRendererGC

    @JvmField
    var moduleThrusterConeFront1: ModelRendererGC

    @JvmField
    var moduleThrusterConeFront2: ModelRendererGC

    @JvmField
    var moduleThrusterConeFront3: ModelRendererGC

    init {
        this.textureWidth = 512
        this.textureHeight = 512

        this.bodyMain1 = ModelRendererGC(this, 125, 128)
        this.bodyMain1.addBox(-24f, 0f, -24f, 48, 12, 48)
        this.bodyMain1.setRotationPoint(0f, 0f, 0f)
        this.bodyMain1.setTextureSize(512, 512)
        this.bodyMain1.mirror = true
        this.setRotation(this.bodyMain1, 0f, -0.3926991f, 0f)
        this.bodyMain2 = ModelRendererGC(this, 125, 128)
        this.bodyMain2.addBox(-24f, -0.1f, -24f, 48, 12, 48)
        this.bodyMain2.setRotationPoint(0f, 0f, 0f)
        this.bodyMain2.setTextureSize(512, 512)
        this.bodyMain2.mirror = true
        this.setRotation(this.bodyMain2, 0f, 0.3926991f, 0f)
        this.bodyAux1 = ModelRendererGC(this, 125, 192)
        this.bodyAux1.addBox(-13f, -5f, -34f, 26, 15, 34)
        this.bodyAux1.setRotationPoint(0f, 0f, 0f)
        this.bodyAux1.setTextureSize(512, 512)
        this.bodyAux1.mirror = true
        this.setRotation(this.bodyAux1, 0.0698132f, 0.7853982f, 0f)
        this.bodyAux2 = ModelRendererGC(this, 125, 192)
        this.bodyAux2.addBox(-13f, -5f, -34f, 26, 15, 34)
        this.bodyAux2.setRotationPoint(0f, 0f, 0f)
        this.bodyAux2.setTextureSize(512, 512)
        this.bodyAux2.mirror = true
        this.setRotation(this.bodyAux2, 0.0698132f, -0.7853982f, 0f)
        this.bodyAux3 = ModelRendererGC(this, 125, 192)
        this.bodyAux3.addBox(-13f, -5f, -34f, 26, 15, 34)
        this.bodyAux3.setRotationPoint(0f, 0f, 0f)
        this.bodyAux3.setTextureSize(512, 512)
        this.bodyAux3.mirror = true
        this.setRotation(this.bodyAux3, 0.0698132f, -2.356194f, 0f)
        this.bodyAux4 = ModelRendererGC(this, 125, 192)
        this.bodyAux4.addBox(-13f, -5f, -34f, 26, 15, 34)
        this.bodyAux4.setRotationPoint(0f, 0f, 0f)
        this.bodyAux4.setTextureSize(512, 512)
        this.bodyAux4.mirror = true
        this.setRotation(this.bodyAux4, 0.0698132f, 2.356194f, 0f)
        this.bodySupportMain1 = ModelRendererGC(this, 125, 243)
        this.bodySupportMain1.addBox(-19f, -7f, -19f, 38, 10, 38)
        this.bodySupportMain1.setRotationPoint(0f, 0f, 0f)
        this.bodySupportMain1.setTextureSize(512, 512)
        this.bodySupportMain1.mirror = true
        this.setRotation(this.bodySupportMain1, 0f, 0f, 0f)
        this.bodySupportMain2 = ModelRendererGC(this, 125, 293)
        this.bodySupportMain2.addBox(-12f, -15f, -12f, 24, 15, 24)
        this.bodySupportMain2.setRotationPoint(0f, 0f, 0f)
        this.bodySupportMain2.setTextureSize(512, 512)
        this.bodySupportMain2.mirror = true
        this.setRotation(this.bodySupportMain2, 0f, 0f, 0f)
        this.legStructureAux1e = ModelRendererGC(this, 0, 81)
        this.legStructureAux1e.addBox(-26f, 28f, -2f, 3, 24, 3)
        this.legStructureAux1e.setRotationPoint(0f, 0f, 0f)
        this.legStructureAux1e.setTextureSize(512, 512)
        this.legStructureAux1e.mirror = true
        this.setRotation(this.legStructureAux1e, 0f, -2.356194f, 0.7330383f)
        this.footBaseg = ModelRendererGC(this, 125, 81)
        this.footBaseg.addBox(-64f, 18.9f, -2f, 11, 6, 2)
        this.footBaseg.setRotationPoint(0f, 0f, 0f)
        this.footBaseg.setTextureSize(512, 512)
        this.footBaseg.mirror = true
        this.setRotation(this.footBaseg, 0f, -2.356194f, 0f)
        this.legStructure1i = ModelRendererGC(this, 0, 0)
        this.legStructure1i.addBox(-27f, 33f, -3f, 5, 14, 5)
        this.legStructure1i.setRotationPoint(0f, 0f, 0f)
        this.legStructure1i.setTextureSize(512, 512)
        this.legStructure1i.mirror = true
        this.setRotation(this.legStructure1i, 0f, -2.356194f, 0.7330383f)
        this.legBase1j = ModelRendererGC(this, 16, 81)
        this.legBase1j.addBox(-29f, 20f, -5f, 9, 10, 9)
        this.legBase1j.setRotationPoint(0f, 0f, 0f)
        this.legBase1j.setTextureSize(512, 512)
        this.legBase1j.mirror = true
        this.setRotation(this.legBase1j, 0f, -2.356194f, 0.7330383f)
        this.footJointk = ModelRendererGC(this, 64, 104)
        this.footJointk.addBox(-58f, 20f, -6.5f, 7, 5, 11)
        this.footJointk.setRotationPoint(0f, 0f, 0f)
        this.footJointk.setTextureSize(512, 512)
        this.footJointk.mirror = true
        this.setRotation(this.footJointk, 0f, -2.356194f, 0f)
        this.footBasel = ModelRendererGC(this, 64, 81)
        this.footBasel.addBox(-62f, 23f, -8.5f, 10, 2, 15)
        this.footBasel.setRotationPoint(0f, 0f, 0f)
        this.footBasel.setTextureSize(512, 512)
        this.footBasel.mirror = true
        this.setRotation(this.footBasel, 0f, -2.356194f, 0f)
        this.legBase1n = ModelRendererGC(this, 16, 81)
        this.legBase1n.addBox(-29f, 20f, -5f, 9, 10, 9)
        this.legBase1n.setRotationPoint(0f, 0f, 0f)
        this.legBase1n.setTextureSize(512, 512)
        this.legBase1n.mirror = true
        this.setRotation(this.legBase1n, 0f, -0.7853982f, 0.7330383f)
        this.legStructureAux1s = ModelRendererGC(this, 0, 81)
        this.legStructureAux1s.addBox(-26f, 28f, -2f, 3, 24, 3)
        this.legStructureAux1s.setRotationPoint(0f, 0f, 0f)
        this.legStructureAux1s.setTextureSize(512, 512)
        this.legStructureAux1s.mirror = true
        this.setRotation(this.legStructureAux1s, 0f, -0.7853982f, 0.7330383f)
        this.legStructure1u = ModelRendererGC(this, 0, 0)
        this.legStructure1u.addBox(-27f, 33f, -3f, 5, 14, 5)
        this.legStructure1u.setRotationPoint(0f, 0f, 0f)
        this.legStructure1u.setTextureSize(512, 512)
        this.legStructure1u.mirror = true
        this.setRotation(this.legStructure1u, 0f, -0.7853982f, 0.7330383f)
        this.footJointv = ModelRendererGC(this, 64, 104)
        this.footJointv.addBox(-58f, 20f, -6.5f, 7, 5, 11)
        this.footJointv.setRotationPoint(0f, 0f, 0f)
        this.footJointv.setTextureSize(512, 512)
        this.footJointv.mirror = true
        this.setRotation(this.footJointv, 0f, -0.7853982f, 0f)
        this.footBasew = ModelRendererGC(this, 64, 81)
        this.footBasew.addBox(-62f, 23f, -8.5f, 10, 2, 15)
        this.footBasew.setRotationPoint(0f, 0f, 0f)
        this.footBasew.setTextureSize(512, 512)
        this.footBasew.mirror = true
        this.setRotation(this.footBasew, 0f, -0.7853982f, 0f)
        this.footBasex = ModelRendererGC(this, 125, 81)
        this.footBasex.addBox(-64f, 18.9f, -2f, 11, 6, 2)
        this.footBasex.setRotationPoint(0f, 0f, 0f)
        this.footBasex.setTextureSize(512, 512)
        this.footBasex.mirror = true
        this.setRotation(this.footBasex, 0f, -0.7853982f, 0f)
        this.legBase1z = ModelRendererGC(this, 16, 81)
        this.legBase1z.addBox(-29f, 20f, -5f, 9, 10, 9)
        this.legBase1z.setRotationPoint(0f, 0f, 0f)
        this.legBase1z.setTextureSize(512, 512)
        this.legBase1z.mirror = true
        this.setRotation(this.legBase1z, 0f, 0.7853982f, 0.7330383f)
        this.legStructureAux1f = ModelRendererGC(this, 0, 81)
        this.legStructureAux1f.addBox(-26f, 28f, -2f, 3, 24, 3)
        this.legStructureAux1f.setRotationPoint(0f, 0f, 0f)
        this.legStructureAux1f.setTextureSize(512, 512)
        this.legStructureAux1f.mirror = true
        this.setRotation(this.legStructureAux1f, 0f, 0.7853982f, 0.7330383f)
        this.legStructure1g = ModelRendererGC(this, 0, 0)
        this.legStructure1g.addBox(-27f, 33f, -3f, 5, 14, 5)
        this.legStructure1g.setRotationPoint(0f, 0f, 0f)
        this.legStructure1g.setTextureSize(512, 512)
        this.legStructure1g.mirror = true
        this.setRotation(this.legStructure1g, 0f, 0.7853982f, 0.7330383f)
        this.footJointh = ModelRendererGC(this, 64, 104)
        this.footJointh.addBox(-58f, 20f, -6.5f, 7, 5, 11)
        this.footJointh.setRotationPoint(0f, 0f, 0f)
        this.footJointh.setTextureSize(512, 512)
        this.footJointh.mirror = true
        this.setRotation(this.footJointh, 0f, 0.7853982f, 0f)
        this.footBasei = ModelRendererGC(this, 64, 81)
        this.footBasei.addBox(-62f, 23f, -8.5f, 10, 2, 15)
        this.footBasei.setRotationPoint(0f, 0f, 0f)
        this.footBasei.setTextureSize(512, 512)
        this.footBasei.mirror = true
        this.setRotation(this.footBasei, 0f, 0.7853982f, 0f)
        this.footBasej = ModelRendererGC(this, 125, 81)
        this.footBasej.addBox(-64f, 18.9f, -2f, 11, 6, 2)
        this.footBasej.setRotationPoint(0f, 0f, 0f)
        this.footBasej.setTextureSize(512, 512)
        this.footBasej.mirror = true
        this.setRotation(this.footBasej, 0f, 0.7853982f, 0f)
        this.legBase1l = ModelRendererGC(this, 16, 81)
        this.legBase1l.addBox(-29f, 20f, -5f, 9, 10, 9)
        this.legBase1l.setRotationPoint(0f, 0f, 0f)
        this.legBase1l.setTextureSize(512, 512)
        this.legBase1l.mirror = true
        this.setRotation(this.legBase1l, 0f, 2.356194f, 0.7330383f)
        this.legStructureAux1q = ModelRendererGC(this, 0, 81)
        this.legStructureAux1q.addBox(-26f, 28f, -2f, 3, 24, 3)
        this.legStructureAux1q.setRotationPoint(0f, 0f, 0f)
        this.legStructureAux1q.setTextureSize(512, 512)
        this.legStructureAux1q.mirror = true
        this.setRotation(this.legStructureAux1q, 0f, 2.356194f, 0.7330383f)
        this.legStructure1s = ModelRendererGC(this, 0, 0)
        this.legStructure1s.addBox(-27f, 33f, -3f, 5, 14, 5)
        this.legStructure1s.setRotationPoint(0f, 0f, 0f)
        this.legStructure1s.setTextureSize(512, 512)
        this.legStructure1s.mirror = true
        this.setRotation(this.legStructure1s, 0f, 2.356194f, 0.7330383f)
        this.footJointt = ModelRendererGC(this, 64, 104)
        this.footJointt.addBox(-58f, 20f, -6.5f, 7, 5, 11)
        this.footJointt.setRotationPoint(0f, 0f, 0f)
        this.footJointt.setTextureSize(512, 512)
        this.footJointt.mirror = true
        this.setRotation(this.footJointt, 0f, 2.356194f, 0f)
        this.footBaseu = ModelRendererGC(this, 64, 81)
        this.footBaseu.addBox(-62f, 23f, -8.5f, 10, 2, 15)
        this.footBaseu.setRotationPoint(0f, 0f, 0f)
        this.footBaseu.setTextureSize(512, 512)
        this.footBaseu.mirror = true
        this.setRotation(this.footBaseu, 0f, 2.356194f, 0f)
        this.footBasev = ModelRendererGC(this, 125, 81)
        this.footBasev.addBox(-64f, 18.9f, -2f, 11, 6, 2)
        this.footBasev.setRotationPoint(0f, 0f, 0f)
        this.footBasev.setTextureSize(512, 512)
        this.footBasev.mirror = true
        this.setRotation(this.footBasev, 0f, 2.356194f, 0f)
        this.moduleMain1 = ModelRendererGC(this, 289, 166)
        this.moduleMain1.addBox(-6f, -44f, -16f, 32, 32, 32)
        this.moduleMain1.setRotationPoint(0f, 0f, 0f)
        this.moduleMain1.setTextureSize(512, 512)
        this.moduleMain1.mirror = true
        this.setRotation(this.moduleMain1, 0f, 0f, 0f)
        this.moduleMain2 = ModelRendererGC(this, 249, 234)
        this.moduleMain2.addBox(-16f, -30f, -13f, 32, 16, 26)
        this.moduleMain2.setRotationPoint(0f, 0f, 0f)
        this.moduleMain2.setTextureSize(512, 512)
        this.moduleMain2.mirror = true
        this.setRotation(this.moduleMain2, 0f, 0f, 0f)
        this.moduleMainDeco = ModelRendererGC(this, 70, 300)
        this.moduleMainDeco.addBox(16f, -46f, -20.9f, 5, 30, 37)
        this.moduleMainDeco.setRotationPoint(0f, 0f, 0f)
        this.moduleMainDeco.setTextureSize(512, 512)
        this.moduleMainDeco.mirror = true
        this.setRotation(this.moduleMainDeco, 0f, 0f, 0f)
        this.moduleCore1 = ModelRendererGC(this, 197, 293)
        this.moduleCore1.mirror = true
        this.moduleCore1.addBox(-20f, -48f, -25f, 5, 36, 42)
        this.moduleCore1.setRotationPoint(0f, 0f, 0f)
        this.moduleCore1.setTextureSize(512, 512)
        this.moduleCore1.mirror = true
        this.setRotation(this.moduleCore1, 0f, 0f, 0f)
        this.moduleCore1.mirror = false
        this.moduleCore2 = ModelRendererGC(this, 197, 293)
        this.moduleCore2.addBox(-30f, -48f, -25f, 5, 36, 42)
        this.moduleCore2.setRotationPoint(0f, 0f, 0f)
        this.moduleCore2.setTextureSize(512, 512)
        this.moduleCore2.mirror = true
        this.setRotation(this.moduleCore2, 0f, 0f, 0f)
        this.moduleCore3 = ModelRendererGC(this, 295, 300)
        this.moduleCore3.addBox(-25f, -44f, -21f, 5, 28, 34)
        this.moduleCore3.setRotationPoint(0f, 0f, 0f)
        this.moduleCore3.setTextureSize(512, 512)
        this.moduleCore3.mirror = true
        this.setRotation(this.moduleCore3, 0f, 0f, 0f)
        this.moduleAux1 = ModelRendererGC(this, 85, 238)
        this.moduleAux1.addBox(26f, -34f, -13f, 4, 16, 26)
        this.moduleAux1.setRotationPoint(0f, 0f, 0f)
        this.moduleAux1.setTextureSize(512, 512)
        this.moduleAux1.mirror = true
        this.setRotation(this.moduleAux1, 0f, 0f, 0f)
        this.moduleAux2 = ModelRendererGC(this, 0, 110)
        this.moduleAux2.addBox(26.1f, -60f, 9f, 1, 28, 1)
        this.moduleAux2.setRotationPoint(0f, 0f, 0f)
        this.moduleAux2.setTextureSize(512, 512)
        this.moduleAux2.mirror = true
        this.setRotation(this.moduleAux2, 0f, 0f, 0f)
        this.moduleAux3 = ModelRendererGC(this, 17, 110)
        this.moduleAux3.addBox(27f, -57f, -12f, 1, 23, 1)
        this.moduleAux3.setRotationPoint(0f, 0f, 0f)
        this.moduleAux3.setTextureSize(512, 512)
        this.moduleAux3.mirror = true
        this.setRotation(this.moduleAux3, 0f, 0f, 0f)
        this.moduleAux4 = ModelRendererGC(this, 5, 110)
        this.moduleAux4.addBox(28f, -48f, -8f, 1, 14, 1)
        this.moduleAux4.setRotationPoint(0f, 0f, 0f)
        this.moduleAux4.setTextureSize(512, 512)
        this.moduleAux4.mirror = true
        this.setRotation(this.moduleAux4, 0f, 0f, 0f)
        this.moduleAux5 = ModelRendererGC(this, 5, 110)
        this.moduleAux5.addBox(28f, -48f, -6f, 1, 14, 1)
        this.moduleAux5.setRotationPoint(0f, 0f, 0f)
        this.moduleAux5.setTextureSize(512, 512)
        this.moduleAux5.mirror = true
        this.setRotation(this.moduleAux5, 0f, 0f, 0f)
        this.moduleAux6 = ModelRendererGC(this, 5, 110)
        this.moduleAux6.addBox(28f, -48f, -4f, 1, 14, 1)
        this.moduleAux6.setRotationPoint(0f, 0f, 0f)
        this.moduleAux6.setTextureSize(512, 512)
        this.moduleAux6.mirror = true
        this.setRotation(this.moduleAux6, 0f, 0f, 0f)
        this.moduleAux7 = ModelRendererGC(this, 11, 110)
        this.moduleAux7.addBox(27f, -53f, 2f, 1, 19, 1)
        this.moduleAux7.setRotationPoint(0f, 0f, 0f)
        this.moduleAux7.setTextureSize(512, 512)
        this.moduleAux7.mirror = true
        this.setRotation(this.moduleAux7, 0f, 0f, 0f)
        this.moduleAux8 = ModelRendererGC(this, 0, 110)
        this.moduleAux8.addBox(28.9f, -60f, 9f, 1, 28, 1)
        this.moduleAux8.setRotationPoint(0f, 0f, 0f)
        this.moduleAux8.setTextureSize(512, 512)
        this.moduleAux8.mirror = true
        this.setRotation(this.moduleAux8, 0f, 0f, 0f)
        this.moduleThrusterBase1 = ModelRendererGC(this, 21, 19)
        this.moduleThrusterBase1.addBox(30f, -28f, -5f, 3, 6, 10)
        this.moduleThrusterBase1.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterBase1.setTextureSize(512, 512)
        this.moduleThrusterBase1.mirror = true
        this.setRotation(this.moduleThrusterBase1, 0f, 0f, 0f)
        this.moduleThrusterBase2 = ModelRendererGC(this, 50, 0)
        this.moduleThrusterBase2.addBox(5f, -39f, -5f, 3, 5, 10)
        this.moduleThrusterBase2.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterBase2.setTextureSize(512, 512)
        this.moduleThrusterBase2.mirror = true
        this.setRotation(this.moduleThrusterBase2, 0f, 0f, 0.7853982f)
        this.moduleThrusterBase3 = ModelRendererGC(this, 50, 0)
        this.moduleThrusterBase3.addBox(-43f, -4f, -5f, 3, 6, 10)
        this.moduleThrusterBase3.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterBase3.setTextureSize(512, 512)
        this.moduleThrusterBase3.mirror = true
        this.setRotation(this.moduleThrusterBase3, 0f, 0f, 2.356194f)
        this.moduleThrusterBase4 = ModelRendererGC(this, 0, 23)
        this.moduleThrusterBase4.addBox(24f, -28f, 13.7f, 3, 6, 6)
        this.moduleThrusterBase4.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterBase4.setTextureSize(512, 512)
        this.moduleThrusterBase4.mirror = true
        this.setRotation(this.moduleThrusterBase4, 0f, 0.7853982f, 0f)
        this.moduleThrusterBase5 = ModelRendererGC(this, 0, 23)
        this.moduleThrusterBase5.mirror = true
        this.moduleThrusterBase5.addBox(24f, -28f, -19.5f, 3, 6, 6)
        this.moduleThrusterBase5.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterBase5.setTextureSize(512, 512)
        this.moduleThrusterBase5.mirror = true
        this.setRotation(this.moduleThrusterBase5, 0f, -0.7853982f, 0f)
        this.moduleThrusterBase5.mirror = false
        this.moduleThrusterConeTop1 = ModelRendererGC(this, 40, 9)
        this.moduleThrusterConeTop1.addBox(-44f, -3f, -1f, 1, 2, 2)
        this.moduleThrusterConeTop1.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeTop1.setTextureSize(512, 512)
        this.moduleThrusterConeTop1.mirror = true
        this.setRotation(this.moduleThrusterConeTop1, 0f, 0f, 2.356194f)
        this.moduleThrusterConeTop2 = ModelRendererGC(this, 30, 9)
        this.moduleThrusterConeTop2.addBox(-45f, -3.5f, -1.5f, 1, 3, 3)
        this.moduleThrusterConeTop2.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeTop2.setTextureSize(512, 512)
        this.moduleThrusterConeTop2.mirror = true
        this.setRotation(this.moduleThrusterConeTop2, 0f, 0f, 2.356194f)
        this.moduleThrusterConeTop3 = ModelRendererGC(this, 29, 0)
        this.moduleThrusterConeTop3.addBox(-46f, -4f, -2f, 1, 4, 4)
        this.moduleThrusterConeTop3.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeTop3.setTextureSize(512, 512)
        this.moduleThrusterConeTop3.mirror = true
        this.setRotation(this.moduleThrusterConeTop3, 0f, 0f, 2.356194f)
        this.moduleThrusterConeBack1 = ModelRendererGC(this, 40, 9)
        this.moduleThrusterConeBack1.addBox(27f, -26f, -18.5f, 1, 2, 2)
        this.moduleThrusterConeBack1.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeBack1.setTextureSize(512, 512)
        this.moduleThrusterConeBack1.mirror = true
        this.setRotation(this.moduleThrusterConeBack1, 0f, -0.7853982f, 0f)
        this.moduleThrusterConeBack2 = ModelRendererGC(this, 30, 9)
        this.moduleThrusterConeBack2.addBox(28f, -26.5f, -19f, 1, 3, 3)
        this.moduleThrusterConeBack2.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeBack2.setTextureSize(512, 512)
        this.moduleThrusterConeBack2.mirror = true
        this.setRotation(this.moduleThrusterConeBack2, 0f, -0.7853982f, 0f)
        this.moduleThrusterConeBack3 = ModelRendererGC(this, 29, 0)
        this.moduleThrusterConeBack3.addBox(29f, -27f, -19.5f, 1, 4, 4)
        this.moduleThrusterConeBack3.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeBack3.setTextureSize(512, 512)
        this.moduleThrusterConeBack3.mirror = true
        this.setRotation(this.moduleThrusterConeBack3, 0f, -0.7853982f, 0f)
        this.moduleThrusterConeBot1 = ModelRendererGC(this, 40, 9)
        this.moduleThrusterConeBot1.addBox(8f, -38f, -1f, 1, 2, 2)
        this.moduleThrusterConeBot1.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeBot1.setTextureSize(512, 512)
        this.moduleThrusterConeBot1.mirror = true
        this.setRotation(this.moduleThrusterConeBot1, 0f, 0f, 0.7853982f)
        this.moduleThrusterConeBot2 = ModelRendererGC(this, 30, 9)
        this.moduleThrusterConeBot2.addBox(9f, -38.5f, -1.5f, 1, 3, 3)
        this.moduleThrusterConeBot2.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeBot2.setTextureSize(512, 512)
        this.moduleThrusterConeBot2.mirror = true
        this.setRotation(this.moduleThrusterConeBot2, 0f, 0f, 0.7853982f)
        this.moduleThrusterConeBot3 = ModelRendererGC(this, 29, 0)
        this.moduleThrusterConeBot3.addBox(10f, -39f, -2f, 1, 4, 4)
        this.moduleThrusterConeBot3.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeBot3.setTextureSize(512, 512)
        this.moduleThrusterConeBot3.mirror = true
        this.setRotation(this.moduleThrusterConeBot3, 0f, 0f, 0.7853982f)
        this.moduleThrusterConeFront1 = ModelRendererGC(this, 40, 9)
        this.moduleThrusterConeFront1.addBox(27f, -26f, 16.5f, 1, 2, 2)
        this.moduleThrusterConeFront1.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeFront1.setTextureSize(512, 512)
        this.moduleThrusterConeFront1.mirror = true
        this.setRotation(this.moduleThrusterConeFront1, 0f, 0.7853982f, 0f)
        this.moduleThrusterConeFront2 = ModelRendererGC(this, 30, 9)
        this.moduleThrusterConeFront2.addBox(28f, -26.5f, 16f, 1, 3, 3)
        this.moduleThrusterConeFront2.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeFront2.setTextureSize(512, 512)
        this.moduleThrusterConeFront2.mirror = true
        this.setRotation(this.moduleThrusterConeFront2, 0f, 0.7853982f, 0f)
        this.moduleThrusterConeFront3 = ModelRendererGC(this, 29, 0)
        this.moduleThrusterConeFront3.addBox(29f, -27f, 15.5f, 1, 4, 4)
        this.moduleThrusterConeFront3.setRotationPoint(0f, 0f, 0f)
        this.moduleThrusterConeFront3.setTextureSize(512, 512)
        this.moduleThrusterConeFront3.mirror = true
        this.setRotation(this.moduleThrusterConeFront3, 0f, 0.7853982f, 0f)
    }

    override fun render(entity: Entity, par2: Float, par3: Float, par4: Float, par5: Float, par6: Float, par7: Float) {
        super.render(entity, par2, par3, par4, par5, par6, par7)
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity)
        this.bodyMain1.render(par7)
        this.bodyMain2.render(par7)
        this.bodyAux1.render(par7)
        this.bodyAux2.render(par7)
        this.bodyAux3.render(par7)
        this.bodyAux4.render(par7)
        this.bodySupportMain1.render(par7)
        this.bodySupportMain2.render(par7)
        this.legStructureAux1e.render(par7)
        this.footBaseg.render(par7)
        this.legStructure1i.render(par7)
        this.legBase1j.render(par7)
        this.footJointk.render(par7)
        this.footBasel.render(par7)
        this.legBase1n.render(par7)
        this.legStructureAux1s.render(par7)
        this.legStructure1u.render(par7)
        this.footJointv.render(par7)
        this.footBasew.render(par7)
        this.footBasex.render(par7)
        this.legBase1z.render(par7)
        this.legStructureAux1f.render(par7)
        this.legStructure1g.render(par7)
        this.footJointh.render(par7)
        this.footBasei.render(par7)
        this.footBasej.render(par7)
        this.legBase1l.render(par7)
        this.legStructureAux1q.render(par7)
        this.legStructure1s.render(par7)
        this.footJointt.render(par7)
        this.footBaseu.render(par7)
        this.footBasev.render(par7)
        this.moduleMain1.render(par7)
        this.moduleMain2.render(par7)
        this.moduleMainDeco.render(par7)
        this.moduleCore1.render(par7)
        this.moduleCore2.render(par7)
        this.moduleCore3.render(par7)
        this.moduleAux1.render(par7)
        this.moduleAux2.render(par7)
        this.moduleAux3.render(par7)
        this.moduleAux4.render(par7)
        this.moduleAux5.render(par7)
        this.moduleAux6.render(par7)
        this.moduleAux7.render(par7)
        this.moduleAux8.render(par7)
        this.moduleThrusterBase1.render(par7)
        this.moduleThrusterBase2.render(par7)
        this.moduleThrusterBase3.render(par7)
        this.moduleThrusterBase4.render(par7)
        this.moduleThrusterBase5.render(par7)
        this.moduleThrusterConeTop1.render(par7)
        this.moduleThrusterConeTop2.render(par7)
        this.moduleThrusterConeTop3.render(par7)
        this.moduleThrusterConeBack1.render(par7)
        this.moduleThrusterConeBack2.render(par7)
        this.moduleThrusterConeBack3.render(par7)
        this.moduleThrusterConeBot1.render(par7)
        this.moduleThrusterConeBot2.render(par7)
        this.moduleThrusterConeBot3.render(par7)
        this.moduleThrusterConeFront1.render(par7)
        this.moduleThrusterConeFront2.render(par7)
        this.moduleThrusterConeFront3.render(par7)
    }

    private fun setRotation(model: ModelRendererGC, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }
}
