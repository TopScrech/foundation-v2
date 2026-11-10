package galaxyspace.core.handler

import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

import galaxyspace.systems.SolarSystem.planets.mars.dimension.sky.SkyProviderMars
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.planets.mars.dimension.WorldProviderMars
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.WorldClient
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class GSSkyProviderHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    open fun onSkyRendererTick(event: TickEvent.ClientTickEvent?) {
        val minecraft: Minecraft = FMLClientHandler.instance().getClient()
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
