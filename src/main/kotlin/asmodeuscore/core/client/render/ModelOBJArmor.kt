package asmodeuscore.core.client.render

import org.lwjgl.opengl.GL11
import asmodeuscore.core.utils.ACCompatibilityManager
import java.util.UUID
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.monster.EntityGiantZombie
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.util.math.MathHelper

abstract class ModelOBJArmor : ModelBiped() {
    @JvmField
    var color: Int = -1 //��������� ����� � ����.

    private var usingParachute = false
    abstract fun pre() //�� ���� ������.
    abstract fun post() //����� ���� ������.
    abstract fun partHead(entity: Entity?) //�����: ������.
    abstract fun partBody() //�����: ����.
    abstract fun partRightArm() //�����: ������ ����.
    abstract fun partLeftArm() //�����: ����� ����.
    abstract fun partRightLeg() //�����: ������ ����.
    abstract fun partLeftLeg() //�����: ����� ����.

    override fun render(entity: Entity, x: Float, y: Float, z: Float, yaw: Float, pitch: Float, parTicks: Float) {
        super.render(entity, x, y, z, yaw, pitch, parTicks)

        GL11.glPushMatrix()

        if (entity.isSneaking()) {
            GlStateManager.translate(0.0f, 0.2f, 0.0f)
        }

        if (ACCompatibilityManager.isGalacticraftLoaded() && entity is EntityPlayer) {
            val player: EntityPlayer = entity

            run {
                //parachute rendering
                val gearData: PlayerGearData? =
                    ClientProxyCore.playerItemData.get(player.getCommandSenderEntity()!!.getName())
                if (gearData != null) {
                    usingParachute = gearData.getParachute() != null
                } else {
                    val id: String = player.getName()

                    if (!ClientProxyCore.gearDataRequests.contains(id)) {
                        (GalacticraftCore.packetPipeline)!!.sendToServer(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.S_REQUEST_GEAR_DATA,
                                GCCoreUtil.getDimensionID(player.world),
                                arrayOf<Any?>(id)
                            )
                        )
                        ClientProxyCore.gearDataRequests.add(id)
                    }
                }
                if (usingParachute) {
                    bipedLeftArm.rotateAngleX += Math.PI.toFloat()
                    bipedLeftArm.rotateAngleZ += Math.PI.toFloat() / 10
                    bipedRightArm.rotateAngleX += Math.PI.toFloat()
                    bipedRightArm.rotateAngleZ -= Math.PI.toFloat() / 10
                }
            }
            /*
			{// motion on planets
				if (!player.onGround && player.world.provider instanceof IGalacticraftWorldProvider && player.getRidingEntity() == null) {
		            float speedModifier = 0.0001162F;
		            
		            float par1 = x, par2 = y;
		            
		            float angularSwingArm = MathHelper.cos(par1 * (speedModifier / 2));
		            float rightMod = 2;
		            this.bipedRightArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * rightMod * par2 * 0.5F;
		            this.bipedLeftArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662F) * 2.0F * par2 * 0.5F;
		            this.bipedRightArm.rotateAngleX += -angularSwingArm * 4.0F * par2 * 0.5F;
		            this.bipedLeftArm.rotateAngleX += angularSwingArm * 4.0F * par2 * 0.5F;
		            this.bipedLeftLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2;
		            this.bipedLeftLeg.rotateAngleX += MathHelper.cos(par1 * 0.1162F * 2 + (float)Math.PI) * 1.4F * par2;
		            this.bipedRightLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
		            this.bipedRightLeg.rotateAngleX += MathHelper.cos(par1 * 0.1162F * 2) * 1.4F * par2;
		        }
			}
			*/
            run { // rendering held items (rockets)
                if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem()
                        .getItem() is IHoldableItem
                ) {
                    val holdableItem: IHoldableItem = player.inventory.getCurrentItem().getItem() as IHoldableItem

                    if (holdableItem.shouldHoldLeftHandUp(player)) {
                        this.bipedLeftArm.rotateAngleX = 0f
                        this.bipedLeftArm.rotateAngleZ = 0f

                        this.bipedLeftArm.rotateAngleX += (Math.PI.toFloat() + 0.3).toFloat()
                        this.bipedLeftArm.rotateAngleZ += Math.PI.toFloat() / 10
                    }

                    if (holdableItem.shouldHoldRightHandUp(player)) {
                        this.bipedRightArm.rotateAngleX = 0f
                        this.bipedRightArm.rotateAngleZ = 0f

                        this.bipedRightArm.rotateAngleX += (Math.PI.toFloat() + 0.3).toFloat()
                        this.bipedRightArm.rotateAngleZ -= Math.PI.toFloat() / 10
                    }

                    if (player.onGround && holdableItem.shouldCrouch(player)) {
                        this.bipedBody.rotateAngleX = 0.5f
                        this.bipedRightLeg.rotationPointZ = 4.0f
                        this.bipedLeftLeg.rotationPointZ = 4.0f
                        this.bipedRightLeg.rotationPointY = 9.0f
                        this.bipedLeftLeg.rotationPointY = 9.0f
                        this.bipedHead.rotationPointY = 1.0f
                        this.bipedHeadwear.rotationPointY = 1.0f
                    }
                }
            }
        }

        if (entity is EntityZombie || entity is EntitySkeleton || entity is EntityGiantZombie) {
            val f6: Float = MathHelper.sin(swingProgress * Math.PI.toFloat())
            val f7: Float = MathHelper.sin((1.0f - (1.0f - swingProgress) * (1.0f - swingProgress)) * Math.PI.toFloat())

            bipedRightArm.rotateAngleZ = 0.0f
            bipedRightArm.rotateAngleY = -(0.1f - f6 * 0.6f)
            bipedRightArm.rotateAngleX = -(Math.PI.toFloat() / 2f)
            bipedRightArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f
            bipedRightArm.rotateAngleZ += MathHelper.cos(z * 0.09f) * 0.05f + 0.05f
            bipedRightArm.rotateAngleX += MathHelper.sin(z * 0.067f) * 0.05f

            bipedLeftArm.rotateAngleZ = 0.0f
            bipedLeftArm.rotateAngleY = 0.1f - f6 * 0.6f
            bipedLeftArm.rotateAngleX = -(Math.PI.toFloat() / 2f)
            bipedLeftArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(z * 0.09f) * 0.05f + 0.05f
            bipedLeftArm.rotateAngleX -= MathHelper.sin(z * 0.067f) * 0.05f

            if (entity is EntitySkeleton && !entity.isChild()) GL11.glScalef(1.2f, 1.2f, 1.2f)
            else if (entity is EntityGiantZombie) GL11.glScalef(6f, 6f, 6f)
        }



        if (color != -1) {
            val red: Float = (color shr 16 and 255).toFloat() / 255f
            val blue: Float = (color shr 8 and 255).toFloat() / 255f
            val green: Float = (color and 255).toFloat() / 255f
            GL11.glColor3f(red, blue, green)
        }

        pre()

        val f6: Float = 2.0f

        if (entity is EntityPlayer) {
            val player: EntityPlayer = entity
            this.hurt(player, parTicks)
        }

        if (Minecraft.getMinecraft().player.getActivePotionEffect(MobEffects.INVISIBILITY) != null) return

        run {
            //partHead
            GL11.glPushMatrix()
            if (isChild) {
                GL11.glScalef(1.5f / f6, 1.5f / f6, 1.5f / f6)
                GL11.glTranslatef(0.0f, 16.0f * parTicks, 0.0f)
            }
            GL11.glTranslatef(
                bipedHead.rotationPointX * parTicks,
                bipedHead.rotationPointY * parTicks,
                bipedHead.rotationPointZ * parTicks
            )
            GL11.glRotatef(bipedHead.rotateAngleZ * (180f / Math.PI.toFloat()), 0f, 0f, 1f)
            GL11.glRotatef(bipedHead.rotateAngleY * (180f / Math.PI.toFloat()), 0f, 1f, 0f)
            GL11.glRotatef(bipedHead.rotateAngleX * (180f / Math.PI.toFloat()), 1f, 0f, 0f)
            GL11.glRotatef(180f, 1f, 0f, 0f)

            partHead(entity)
            GL11.glPopMatrix()
        }

        if (isChild) {
            GL11.glPushMatrix()
            GL11.glScalef(1.0f / f6, 1.0f / f6, 1.0f / f6)
            GL11.glTranslatef(0.0f, 24.0f * parTicks, 0.0f)
        }

        run {
            //partBody
            GL11.glPushMatrix()
            GL11.glTranslatef(
                bipedBody.rotationPointX * parTicks,
                bipedBody.rotationPointY * parTicks,
                bipedBody.rotationPointZ * parTicks
            )
            GL11.glRotatef(bipedBody.rotateAngleZ * (180f / Math.PI.toFloat()), 0f, 0f, 1f)
            GL11.glRotatef(bipedBody.rotateAngleY * (180f / Math.PI.toFloat()), 0f, 1f, 0f)
            GL11.glRotatef(bipedBody.rotateAngleX * (180f / Math.PI.toFloat()), 1f, 0f, 0f)
            GL11.glRotatef(180f, 1f, 0f, 0f)

            partBody()
            GL11.glPopMatrix()
        }

        run {
            //partRightArm
            GL11.glPushMatrix()
            GL11.glTranslatef(
                bipedRightArm.rotationPointX * parTicks,
                bipedRightArm.rotationPointY * parTicks,
                bipedRightArm.rotationPointZ * parTicks
            )
            GL11.glRotatef(bipedRightArm.rotateAngleZ * (180f / Math.PI.toFloat()), 0f, 0f, 1f)
            GL11.glRotatef(bipedRightArm.rotateAngleY * (180f / Math.PI.toFloat()), 0f, 1f, 0f)
            GL11.glRotatef(bipedRightArm.rotateAngleX * (180f / Math.PI.toFloat()), 1f, 0f, 0f)
            GL11.glRotatef(180f, 1f, 0f, 0f)
            partRightArm()
            GL11.glPopMatrix()
        }

        run {
            //partLeftArm
            GL11.glPushMatrix()
            GL11.glTranslatef(
                bipedLeftArm.rotationPointX * parTicks,
                bipedLeftArm.rotationPointY * parTicks,
                bipedLeftArm.rotationPointZ * parTicks
            )
            GL11.glRotatef(bipedLeftArm.rotateAngleZ * (180f / Math.PI.toFloat()), 0f, 0f, 1f)
            GL11.glRotatef(bipedLeftArm.rotateAngleY * (180f / Math.PI.toFloat()), 0f, 1f, 0f)
            GL11.glRotatef(bipedLeftArm.rotateAngleX * (180f / Math.PI.toFloat()), 1f, 0f, 0f)
            GL11.glRotatef(180f, 1f, 0f, 0f)
            partLeftArm()
            GL11.glPopMatrix()
        }

        run {
            //partRightLeg
            GL11.glPushMatrix()
            GL11.glTranslatef(
                bipedRightLeg.rotationPointX * parTicks,
                bipedRightLeg.rotationPointY * parTicks,
                bipedRightLeg.rotationPointZ * parTicks
            )
            GL11.glRotatef(bipedRightLeg.rotateAngleZ * (180f / Math.PI.toFloat()), 0f, 0f, 1f)
            GL11.glRotatef(bipedRightLeg.rotateAngleY * (180f / Math.PI.toFloat()), 0f, 1f, 0f)
            GL11.glRotatef(bipedRightLeg.rotateAngleX * (180f / Math.PI.toFloat()), 1f, 0f, 0f)
            GL11.glRotatef(180f, 1f, 0f, 0f)
            partRightLeg()
            GL11.glPopMatrix()
        }

        run {
            //partLeftLeg
            GL11.glPushMatrix()
            GL11.glTranslatef(
                bipedLeftLeg.rotationPointX * parTicks,
                bipedLeftLeg.rotationPointY * parTicks,
                bipedLeftLeg.rotationPointZ * parTicks
            )
            GL11.glRotatef(bipedLeftLeg.rotateAngleZ * (180f / Math.PI.toFloat()), 0f, 0f, 1f)
            GL11.glRotatef(bipedLeftLeg.rotateAngleY * (180f / Math.PI.toFloat()), 0f, 1f, 0f)
            GL11.glRotatef(bipedLeftLeg.rotateAngleX * (180f / Math.PI.toFloat()), 1f, 0f, 0f)
            GL11.glRotatef(180f, 1f, 0f, 0f)
            partLeftLeg()
            GL11.glPopMatrix()
        }

        if (isChild) {
            GL11.glPopMatrix()
        }

        post()

        GL11.glColor3f(1f, 1f, 1f)
        GL11.glPopMatrix()
    }

    private fun hurt(player: EntityPlayer, ticks: Float) {
        if (player.hurtTime > 0 || player.deathTime > 0) {
            GL11.glPushMatrix()
            GL11.glDepthMask(true)
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_ALPHA_TEST)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glDepthFunc(GL11.GL_EQUAL)

            GL11.glColor4f(player.getBrightness(), 0.0f, 0.0f, 1.0f)

            GL11.glDepthFunc(GL11.GL_LEQUAL)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_ALPHA_TEST)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }
    }
}
