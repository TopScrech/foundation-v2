package galaxyspace.core.client.render.entity

import galaxyspace.core.util.GSTrace
import galaxyspace.systems.SolarSystem.planets.overworld.items.tools.ItemMatterManipulator
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.math.sin

@SideOnly(Side.CLIENT)
open class RenderLaserBeam<T : EntityLivingBase> {
    open fun onRenderWorldLast(event: RenderWorldLastEvent) {
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE, 0, 0)

        GlStateManager.pushMatrix()
        GlStateManager.translate(
            -Minecraft.getMinecraft().player.posX,
            -Minecraft.getMinecraft().player.posY,
            -Minecraft.getMinecraft().player.posZ
        )
        renderClient(event.getPartialTicks())
        renderOthers(event.getPartialTicks())
        GlStateManager.popMatrix()

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableBlend()
        GlStateManager.enableCull()
    }

    open fun renderOthers(ticks: Float) {
        Minecraft.getMinecraft().world.getLoadedEntityList().stream()
            .filter { o: Entity? -> o is EntityPlayer }
            .filter { player: Entity? -> player != Minecraft.getMinecraft().player }
            .forEach { o: Entity? ->
                val player: EntityPlayer = o as EntityPlayer
                if (shouldRenderBeam(player)) {
                    renderRaycastedBeam(
                        player.getPositionEyes(ticks).add(0.0, player.getEyeHeight().toDouble(), 0.0),
                        player.getLook(0f),
                        Vec3d(-0.5, -0.3, 1.0),
                        player
                    )
                } else {
                    //stopWeaponSound(player);
                }
            }
    }

    open fun renderClient(ticks: Float) {
        val player: EntityPlayerSP = Minecraft.getMinecraft().player

        if (shouldRenderBeam(player)) {
            val pos: Vec3d = player.getPositionEyes(1f)
            val look: Vec3d = player.getLook(0f)
            renderRaycastedBeam(pos, look, Vec3d(-0.1, -0.1, 0.15), player)
        } else {
            //stopWeaponSound(player);
        }
    }

    protected open fun shouldRenderBeam(entity: EntityPlayer): Boolean {
        return entity.isHandActive() && entity.getActiveItemStack() != null &&
                (entity.getActiveItemStack().getItem() is ItemMatterManipulator)
    }

    protected open fun onBeamRaycastHit(hit: RayTraceResult, caster: EntityPlayer) {
        val weaponStack: ItemStack = caster.getActiveItemStack()
        if (weaponStack != null && weaponStack.getItem() is ItemMatterManipulator) {
            //((EnergyWeapon)weaponStack.getItem()).onProjectileHit(hit, weaponStack, caster.worldObj, 1);
            if (weaponStack.getItem() is ItemMatterManipulator && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                GlStateManager.pushMatrix()
                //RenderUtils.applyColorWithMultipy(getBeamColor(caster), 0.5f + (float)(1 + Math.sin(caster.worldObj.getWorldTime() * 0.5f)) * 0.5f);
                //Minecraft.getMinecraft().renderEngine.bindTexture(TileEntityRendererStation.glowTexture);
                GlStateManager.translate(
                    hit.getBlockPos().getX() + 0.5,
                    hit.getBlockPos().getY() + 0.5,
                    hit.getBlockPos().getZ() + 0.5
                )
                GlStateManager.translate(
                    hit.sideHit.getDirectionVec().getX() * 0.5,
                    hit.sideHit.getDirectionVec().getY() * 0.5,
                    hit.sideHit.getDirectionVec().getZ() * 0.5
                )
                if (hit.sideHit == EnumFacing.SOUTH) {
                    GlStateManager.rotate(90f, 1f, 0f, 0f)
                } else if (hit.sideHit == EnumFacing.NORTH) {
                    GlStateManager.rotate(90f, -1f, 0f, 0f)
                } else if (hit.sideHit == EnumFacing.EAST) {
                    GlStateManager.rotate(90f, 0f, 0f, -1f)
                } else if (hit.sideHit == EnumFacing.WEST) {
                    GlStateManager.rotate(90f, 0f, 0f, 1f)
                } else if (hit.sideHit == EnumFacing.DOWN) {
                    GlStateManager.rotate(180f, 1f, 0f, 0f)
                }
                GlStateManager.scale(1.0, 1.5 + sin(caster.world.getWorldTime() * 0.5) * 0.5, 1.0)
                GlStateManager.popMatrix()
            }
        }
    }

    protected open fun onBeamRender(caster: EntityPlayer?) {
    }

    protected open fun getBeamMaxDistance(caster: EntityPlayer?): Float {
        return 15.0f
    }

    protected open fun getBeamThickness(caster: EntityPlayer?): Float {
        return 0.05f
    }

    protected open fun renderRaycastedBeam(direction: Vec3d, offset: Vec3d, caster: EntityPlayer): Boolean {
        return renderRaycastedBeam(caster.getPositionEyes(1f), direction, offset, caster)
    }

    protected open fun renderRaycastedBeam(
        position: Vec3d,
        direction: Vec3d,
        offset: Vec3d,
        caster: EntityPlayer
    ): Boolean {
        val maxDistance: Double = getBeamMaxDistance(caster).toDouble()

        val hit: RayTraceResult? = GSTrace.rayTrace(
            position,
            caster.world,
            maxDistance,
            0f,
            Vec3d(0.0, 0.0, 0.0),
            false,
            true,
            direction,
            caster
        )
        if (hit != null && hit.typeOfHit != RayTraceResult.Type.MISS) {
            renderBeam(position, hit.hitVec, offset, null, getBeamThickness(caster), caster)
            onBeamRender(caster)
            onBeamRaycastHit(hit, caster)
            return true
        } else {
            renderBeam(
                position,
                position.add(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance),
                offset,
                null,
                getBeamThickness(caster),
                caster
            )
            onBeamRender(caster)
        }
        return false
    }

    protected open fun renderBeam(
        from: Vec3d,
        to: Vec3d,
        offest: Vec3d,
        texture: ResourceLocation?,
        tickness: Float,
        viewer: EntityPlayer
    ) {
        if (texture != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(texture)
        }

        GlStateManager.color(0.0f, 1.0f, 1.0f)
        //GlStateManager.disableCull();
        //GlStateManager.enableBlend();
        //GlStateManager.blendFunc(GL_ONE, GL_ONE);
        //GlStateManager.disableLighting();
        val distance: Double = from.subtract(to).length()
        val v: Double = -viewer.world.getWorldTime() * 0.2

        GlStateManager.pushMatrix()
        GlStateManager.translate(from.x, from.y, from.z)
        GlStateManager.rotate(-viewer.getRotationYawHead(), 0f, 1f, 0f)
        GlStateManager.rotate(viewer.rotationPitch, 1f, 0f, 0f)
        GlStateManager.translate(offest.x, offest.y, offest.z)
        val tessellator: Tessellator = Tessellator.getInstance()
        val wr: BufferBuilder = tessellator.getBuffer()
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        wr.pos(tickness.toDouble(), 0.0, 0.0).tex(0.0, v).endVertex()
        wr.pos(tickness.toDouble(), 0.0, distance).tex(0.0, v + distance * 1.5).endVertex()
        wr.pos(-tickness.toDouble(), 0.0, distance).tex(1.0, v + distance * 1.5).endVertex()
        wr.pos(-tickness.toDouble(), 0.0, 0.0).tex(1.0, v).endVertex()

        wr.pos(0.0, tickness.toDouble(), 0.0).tex(0.0, v).endVertex()
        wr.pos(0.0, tickness.toDouble(), distance).tex(0.0, v + distance * 1.5).endVertex()
        wr.pos(0.0, -tickness.toDouble(), distance).tex(1.0, v + distance * 1.5).endVertex()
        wr.pos(0.0, -tickness.toDouble(), 0.0).tex(1.0, v).endVertex()
        Tessellator.getInstance().draw()
        GlStateManager.popMatrix()

        //GlStateManager.enableCull();
        //GlStateManager.disableBlend();
        //GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
}
