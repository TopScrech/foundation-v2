package micdoodle8.mods.galacticraft.core.client

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

open class EventHandlerClient {
    @SubscribeEvent(priority = EventPriority.LOWEST) //Lowest priority to do the PushMatrix last, just before vanilla RenderPlayer - this also means if it gets cancelled first by another mod, this will never be called
    open fun onRenderPlayerPre(event: RenderPlayerEvent.Pre) {
        GL11.glPushMatrix()

        val player: EntityPlayer = event.getEntityPlayer()

        if (player.getRidingEntity() is ICameraZoomEntity && player === Minecraft.getMinecraft().player && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            val entity: Entity? = player.getRidingEntity()
            var rotateOffset: Float = (entity as ICameraZoomEntity).getRotateOffset()
            if (rotateOffset > -10f) {
                rotateOffset += ClientProxyCore.PLAYER_Y_OFFSET
                GL11.glTranslatef(0f, -rotateOffset, 0f)
                val anglePitch: Float =
                    entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * event.getPartialRenderTick()
                val angleYaw: Float =
                    entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * event.getPartialRenderTick()
                GL11.glRotatef(-angleYaw, 0.0f, 1.0f, 0.0f)
                GL11.glRotatef(anglePitch, 0.0f, 0.0f, 1.0f)
                GL11.glTranslatef(0f, rotateOffset, 0f)
            }
        }

        if (player is EntityPlayerSP) {
            sneakRenderOverride = true
        }

        //Gravity - freefall - jetpack changes in player model orientation can go here
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) //Highest priority to do the PushMatrix first, just after vanilla RenderPlayer
    open fun onRenderPlayerPost(event: RenderPlayerEvent.Post) {
        GL11.glPopMatrix()

        if (event.getEntityPlayer() is EntityPlayerSP) sneakRenderOverride = false
    }

    @SubscribeEvent
    open fun onRenderPlanetPre(event: CelestialBodyRenderEvent.Pre) {
        if (event.celestialBody === GalacticraftCore.planetOverworld) {
            if (!ClientProxyCore.overworldTextureRequestSent) {
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any?>()
                    )
                )
                ClientProxyCore.overworldTextureRequestSent = true
            }

            if (ClientProxyCore.overworldTexturesValid) {
                event.celestialBodyTexture = null
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, (ClientProxyCore.overworldTextureClient)!!.getGlTextureId())
            }
        }
    }

    @SubscribeEvent
    open fun onRenderPlanetPost(event: CelestialBodyRenderEvent.Post) {
        if (mc.currentScreen is GuiCelestialSelection) {
            if (event.celestialBody === GalacticraftCore.planetSaturn) {
                mc.renderEngine.bindTexture(ClientProxyCore.saturnRingTexture)
                val size: Float =
                    (mc.currentScreen as GuiCelestialSelection).getWidthForCelestialBody(event.celestialBody) / 6.0f
                (mc.currentScreen as GuiCelestialSelection).drawTexturedModalRect(
                    -7.5f * size,
                    -1.75f * size,
                    15.0f * size,
                    3.5f * size,
                    0f,
                    0f,
                    30f,
                    7f,
                    false,
                    false,
                    32f,
                    32f
                )
            } else if (event.celestialBody === GalacticraftCore.planetUranus) {
                mc.renderEngine.bindTexture(ClientProxyCore.uranusRingTexture)
                val size: Float =
                    (mc.currentScreen as GuiCelestialSelection).getWidthForCelestialBody(event.celestialBody) / 6.0f
                (mc.currentScreen as GuiCelestialSelection).drawTexturedModalRect(
                    -1.75f * size,
                    -7.0f * size,
                    3.5f * size,
                    14.0f * size,
                    0f,
                    0f,
                    7f,
                    28f,
                    false,
                    false,
                    32f,
                    32f
                )
            }
        }
    }

    companion object {
        @JvmField
        var mc: Minecraft = FMLClientHandler.instance().getClient()

        @JvmField
        var sneakRenderOverride: Boolean = false
    }
}
