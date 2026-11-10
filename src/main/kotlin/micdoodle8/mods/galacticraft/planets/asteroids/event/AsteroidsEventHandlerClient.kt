package micdoodle8.mods.galacticraft.planets.asteroids.event

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore.EventSpecialRender
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.galacticraft.planets.asteroids.client.SkyProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.NetworkRenderer
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class AsteroidsEventHandlerClient {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    open fun onClientTick(event: TickEvent.ClientTickEvent?) {
        val minecraft: Minecraft = Minecraft.getMinecraft()
        val world: WorldClient? = minecraft.world

        if (world != null) {
            if (world.provider is WorldProviderAsteroids) {
                if (world.provider.getSkyRenderer() == null) {
                    world.provider.setSkyRenderer(SkyProviderAsteroids(world.provider as IGalacticraftWorldProvider))
                }

                if (world.provider.getCloudRenderer() == null) {
                    world.provider.setCloudRenderer(CloudRenderer())
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    open fun onRingRender(renderEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre) {
        if (renderEvent.celestialBody == AsteroidsModule.planetAsteroids) {
            var alpha: Float = 1.0f
            val screen: GuiScreen? = FMLClientHandler.instance().getClient().currentScreen
            if (screen is GuiCelestialSelection) {
                alpha = screen.getAlpha(renderEvent.celestialBody)
                GL11.glColor4f(0.7f, 0.0f, 0.0f, alpha / 2.0f)
            } else {
                GL11.glColor4f(0.3f, 0.1f, 0.1f, 1.0f)
            }
            renderEvent.setCanceled(true)
            GL11.glBegin(GL11.GL_LINE_LOOP)

            val theta: Float = Constants.twoPI / 90
            val cos: Float = MathHelper.cos(theta)
            val sin: Float = MathHelper.sin(theta)

            val min: Float = 72.0f
            val max: Float = 78.0f

            var x: Float = max * (renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().unScaledDistance
            var y: Float = 0f

            var temp: Float
            for (i: Int in 0..89) {
                GL11.glVertex2f(x, y)

                temp = x
                x = cos * x - sin * y
                y = sin * temp + cos * y
            }

            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINE_LOOP)

            x = min * (renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().unScaledDistance
            y = 0f

            for (i: Int in 0..89) {
                GL11.glVertex2f(x, y)

                temp = x
                x = cos * x - sin * y
                y = sin * temp + cos * y
            }

            GL11.glEnd()
            GL11.glColor4f(0.7f, 0.0f, 0.0f, alpha / 10.0f)
            GL11.glBegin(GL11.GL_QUADS)

            x = min * (renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().unScaledDistance
            y = 0f
            var x2: Float = max * (renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().unScaledDistance
            var y2: Float = 0f

            for (i: Int in 0..89) {
                GL11.glVertex2f(x2, y2)
                GL11.glVertex2f(x, y)

                temp = x
                x = cos * x - sin * y
                y = sin * temp + cos * y
                temp = x2
                x2 = cos * x2 - sin * y2
                y2 = sin * temp + cos * y2

                GL11.glVertex2f(x, y)
                GL11.glVertex2f(x2, y2)
            }

            GL11.glEnd()
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    open fun onBodyRender(renderEvent: CelestialBodyRenderEvent.Pre) {
        if (renderEvent.celestialBody == AsteroidsModule.planetAsteroids) {
            GL11.glRotatef(ClientUtil.getMilliseconds() / 10.0f % 360, 0f, 0f, 1f)
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    open fun onSpecialRender(event: ClientProxyCore.EventSpecialRender) {
        NetworkRenderer.renderNetworks(FMLClientHandler.instance().getClient().world, event.partialTicks)
    }
}
