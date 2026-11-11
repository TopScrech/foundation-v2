package galaxyspace.core.handler

import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

import galaxyspace.systems.SolarSystem.planets.mars.dimension.sky.SkyProviderMars
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.planets.mars.dimension.WorldProviderMars
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class GSSkyProviderHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun onSkyRendererTick(event: TickEvent.ClientTickEvent?) {
        val minecraft: Minecraft = Minecraft.getInstance()
        val world: WorldClient? = minecraft.world
        val player: EntityPlayerSP? = minecraft.player

        if (world != null) {
            if (world.provider is WorldProviderMars) {
                if (world.provider.getSkyRenderer() == null) world.provider.setSkyRenderer(SkyProviderMars())

                if (world.provider.getCloudRenderer() == null) world.provider.setCloudRenderer(CloudRenderer())
            }
        }
    }
}
