package micdoodle8.mods.galacticraft.core.client.model

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.api.item.IHoldableItemCustom
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.model.ModelBiped
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.util.Mth as MathHelper
import net.minecraftforge.common.MinecraftForge
import kotlin.math.sin

object ModelBipedGC {
    @JvmStatic
    fun setRotationAngles(
        biped: ModelBiped,
        par1: Float,
        par2: Float,
        par3: Float,
        par4: Float,
        par5: Float,
        par6: Float,
        par7Entity: Entity?
    ) {
        if (par7Entity !is EntityPlayer) return
        val player: EntityPlayer = par7Entity
        val currentItemStack: ItemStack = player.inventory.getSelectedItem()
        val floatPI: Float = 3.1415927f

        if (!par7Entity.onGround && par7Entity.level().provider is IGalacticraftWorldProvider && par7Entity.getVehicle() == null && !(currentItemStack != null && currentItemStack.getItem() is IHoldableItem)) {
            val speedModifier: Float = 0.1162f * 2

            val angularSwingArm: Float = MathHelper.cos(par1 * (speedModifier / 2))
            val rightMod: Float = (if (biped.rightArmPose == ModelBiped.ArmPose.ITEM) 1 else 2).toFloat()
            biped.bipedRightArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662f + floatPI) * rightMod * par2 * 0.5f
            biped.bipedLeftArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.5f
            biped.bipedRightArm.rotateAngleX += -angularSwingArm * 4.0f * par2 * 0.5f
            biped.bipedLeftArm.rotateAngleX += angularSwingArm * 4.0f * par2 * 0.5f
            biped.bipedLeftLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662f + floatPI) * 1.4f * par2
            biped.bipedLeftLeg.rotateAngleX += MathHelper.cos(par1 * 0.1162f * 2 + floatPI) * 1.4f * par2
            biped.bipedRightLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662f) * 1.4f * par2
            biped.bipedRightLeg.rotateAngleX += MathHelper.cos(par1 * 0.1162f * 2) * 1.4f * par2
        }

        val gearData: PlayerGearData? = (GalacticraftCore.proxy)!!.getGearData(player)

        if (gearData != null) {
            if (gearData.getParachute() != null) {
                // Parachute is equipped
                biped.bipedLeftArm.rotateAngleX += floatPI
                biped.bipedLeftArm.rotateAngleZ += floatPI / 10
                biped.bipedRightArm.rotateAngleX += floatPI
                biped.bipedRightArm.rotateAngleZ -= floatPI / 10
            }
        }

        var heldItemStack: ItemStack? = null

        for (hand: EnumHand in EnumHand.entries) {
            val item: ItemStack = player.getItemInHand(hand)
            if (item != null && item.getItem() is IHoldableItem) {
                heldItemStack = item
            }
        }

        if (heldItemStack != null && player.getVehicle() !is ICameraZoomEntity) {
            val heldItem: Item = heldItemStack.getItem()
            val holdableItem: IHoldableItem = heldItem as IHoldableItem
            val holdableItemCustom: IHoldableItemCustom? =
                if (heldItem is IHoldableItemCustom) heldItem as IHoldableItemCustom else null

            if (holdableItem.shouldHoldLeftHandUp(player)) {
                var angle: Vector3? = null

                if (holdableItemCustom != null) {
                    angle = holdableItemCustom.getLeftHandRotation(player)
                }

                if (angle == null) {
                    angle = Vector3((floatPI + 0.3f).toDouble(), 0.0, (floatPI / 10.0f).toDouble())
                }

                biped.bipedLeftArm.rotateAngleX = angle.floatX()
                biped.bipedLeftArm.rotateAngleY = angle.floatY()
                biped.bipedLeftArm.rotateAngleZ = angle.floatZ()
            }

            if (holdableItem.shouldHoldRightHandUp(player)) {
                var angle: Vector3? = null

                if (holdableItemCustom != null) {
                    angle = holdableItemCustom.getRightHandRotation(player)
                }

                if (angle == null) {
                    angle = Vector3((floatPI + 0.3f).toDouble(), 0.0, (-Math.PI.toFloat() / 10.0f).toDouble())
                }

                biped.bipedRightArm.rotateAngleX = angle.floatX()
                biped.bipedRightArm.rotateAngleY = angle.floatY()
                biped.bipedRightArm.rotateAngleZ = angle.floatZ()
            }
        }

        val l: MutableList<*> = player.level().getEntitiesWithinAABBExcludingEntity(
            player,
            AxisAlignedBB(player.x - 20, 0.0, player.z - 20, player.x + 20, 200.0, player.z + 20)
        )

        for (i: Int in l.indices) {
            val e: Entity? = l.get(i) as Entity?

            if (e is EntityTieredRocket) {
                val ship: EntityTieredRocket = e

                if (!ship.getPassengers().isEmpty() && !ship.getPassengers()
                        .contains(player) && (ship.getLaunched() || ship.timeUntilLaunch < 390)
                ) {
                    biped.bipedRightArm.rotateAngleZ -= floatPI / 8f + MathHelper.sin(par3 * 0.9f) * 0.2f
                    biped.bipedRightArm.rotateAngleX = floatPI
                    break
                }
            }
        }

        if (player.isPlayerSleeping() && GalacticraftCore.isPlanetsLoaded) {
            val event: RenderPlayerGC.RotatePlayerEvent =
                RenderPlayerGC.RotatePlayerEvent(player as AbstractClientPlayer)
            MinecraftForge.EVENT_BUS.post(event)

            if (event.vanillaOverride && (event.shouldRotate == null || (event.shouldRotate)!!)) {
                biped.bipedHead.rotateAngleX = (20.0f - sin((player.tickCount / 10.0f).toDouble()) / 7.0f).toFloat()
                biped.bipedHead.rotateAngleY = 0.0f
                biped.bipedHead.rotateAngleZ = 0.0f
                biped.bipedLeftArm.rotateAngleX = 0.0f
                biped.bipedLeftArm.rotateAngleY = 0.0f
                biped.bipedLeftArm.rotateAngleZ = 0.0f
                biped.bipedRightArm.rotateAngleX = 0.0f
                biped.bipedRightArm.rotateAngleY = 0.0f
                biped.bipedRightArm.rotateAngleZ = 0.0f
            }
        }

        ModelBiped.copyModelAngles(biped.bipedHead, biped.bipedHeadwear)
    }
}
