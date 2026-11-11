package asmodeuscore.core.astronomy.sky.galacticraft

import org.lwjgl.opengl.GL11
import asmodeuscore.AsmodeusCore
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderMoon : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, partialTicks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()

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


        // HOME:
        f10 = 14.0f
        val earthRotation: Float = (this.mc.world.getSpawnPoint().getZ() - mc.player.z - 20000).toFloat() * 0.01f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(earthRotation, 1.0f, 0.0f, 0.0f)
        //GL11.glRotatef(220F, 0.0F, 1.0F, 0.0F);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)

        if (ClientProxyCore.overworldTexturesValid) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, (ClientProxyCore.overworldTextureClient)!!.getGlTextureId())
        } else {
            // Overworld texture is 48x48 in a 64x64 .png file
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(overworldTexture)
        }


        //this.mc.theWorld.getMoonPhase();
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        f10 = 11.0f
        val f: Float = 0.59f
        this.renderAtmo(
            (tessellator)!!,
            0f,
            0f,
            f10 + 2.2f,
            Vec3d((88 / 255.0f * f).toDouble(), (122 / 255.0f * f).toDouble(), (180 / 255.0f * f).toDouble())
        )

        GL11.glPopMatrix()
    }

    override fun modeLight(): ModeLight {
        return ModeLight.ALWAYS_LIGHT
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 6.0f
    }

    override fun sunImage(): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(AsmodeusCore.ASSET_PREFIX, "textures/gui/celestialbodies/white.png")
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun colorSunAura(): StarColor {
        return StarColor.WHITE
    }

    override fun getAtmosphereColor(): Vector3? {
        return null
    }

    override fun enableSmoothRender(): Boolean {
        return true
    }

    companion object {
        private val overworldTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png")
    }
}
