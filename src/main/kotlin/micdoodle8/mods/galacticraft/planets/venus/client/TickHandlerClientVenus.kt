package micdoodle8.mods.galacticraft.planets.venus.client

import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent

import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.venus.ConfigManagerVenus
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.init.SoundEvents
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.cos
import kotlin.math.sin

@SideOnly(Side.CLIENT)
open class TickHandlerClientVenus {
    private val lightning: MutableMap<BlockPos?,Int> = Maps.newHashMap<BlockPos?,Int>()

    @SubscribeEvent
    open fun onRenderTick(event: TickEvent.RenderTickEvent) {
        val minecraft: Minecraft = FMLClientHandler.instance().getClient()
        val player: EntityPlayerSP? = minecraft.player
        val playerBaseClient: EntityPlayerSP? = PlayerUtil.getPlayerBaseClientFromPlayer(player, false)

        if (event.phase == TickEvent.Phase.END) {
        }
    }

    @SubscribeEvent
    open fun renderLightning(event: ClientProxyCore.EventSpecialRender?) {
        val minecraft: Minecraft = FMLClientHandler.instance().getClient()
        val player: EntityPlayerSP? = minecraft.player
        if (player != null && !ConfigManagerVenus.disableAmbientLightning) {
            val it: MutableIterator<MutableMap.MutableEntry<BlockPos?,Int>> = lightning.entries.iterator()
            while (it.hasNext()) {
                val entry: MutableMap.MutableEntry<BlockPos?,Int> = it.next()
                val seed: Long = (entry.value!! / 10 + entry.key!!.getX() + entry.key!!.getZ()).toLong()
                FakeLightningBoltRenderer.renderBolt(
                    seed,
                    entry.key!!.getX() - ClientProxyCore.playerPosX,
                    entry.key!!.getY() - ClientProxyCore.playerPosY,
                    entry.key!!.getZ() - ClientProxyCore.playerPosZ
                )
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    open fun onClientTick(event: TickEvent.ClientTickEvent?) {
        val minecraft: Minecraft = FMLClientHandler.instance().getClient()

        val world: WorldClient? = minecraft.world

        if (world != null) {
            if (world.provider is WorldProviderVenus) {
                if (world.provider.getSkyRenderer() == null) {
                    world.provider.setSkyRenderer(SkyProviderVenus(world.provider as IGalacticraftWorldProvider))
                }

                if (world.provider.getCloudRenderer() == null) {
                    world.provider.setCloudRenderer(CloudRenderer())
                }

                if (world.provider.getWeatherRenderer() == null) {
                    world.provider.setWeatherRenderer(WeatherRendererVenus())
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    open fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (event.side != Side.CLIENT) return

        val minecraft: Minecraft = FMLClientHandler.instance().getClient()
        val player: EntityPlayerSP = minecraft.player ?: return
        val world: WorldClient = minecraft.world ?: return

        if (player === event.player) {
            if (!ConfigManagerVenus.disableAmbientLightning) {
                val it: MutableIterator<MutableMap.MutableEntry<BlockPos?,Int>> = lightning.entries.iterator()
                while (it.hasNext()) {
                    val entry: MutableMap.MutableEntry<BlockPos?,Int> = it.next()
                    val `val`: Int = entry.value!!
                    if (`val` - 1 <= 0) {
                        it.remove()
                    } else {
                        entry.setValue(`val` - 1)
                    }
                }

                if (player.getRNG()
                        .nextInt(300 + (800f * world.rainingStrength).toInt()) == 0 && world.provider is WorldProviderVenus
                ) {
                    val freq: Double = player.getRNG().nextDouble() * Math.PI * 2.0f
                    val dist: Double = 180.0
                    val dX: Double = dist * cos(freq)
                    val dZ: Double = dist * sin(freq)
                    val posX: Double = player.posX + dX
                    val posY: Double = 70.0
                    val posZ: Double = player.posZ + dZ
                    world.playSound(
                        player,
                        posX,
                        posY,
                        posZ,
                        SoundEvents.ENTITY_LIGHTNING_THUNDER,
                        SoundCategory.WEATHER,
                        500.0f + player.getRNG().nextFloat() * 500f,
                        1.0f + player.getRNG().nextFloat() * 0.2f
                    )
                    lightning.put(BlockPos(posX, posY, posZ), 20)
                }
            }
        }
    }
}
