package micdoodle8.mods.galacticraft.planets.asteroids.entities.player

import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent

import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntitySmallAsteroid
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import kotlin.math.cos
import kotlin.math.sin

open class AsteroidsPlayerHandler {
    @SubscribeEvent
    open fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        if (event.player is EntityPlayerMP) {
            this.onPlayerLogin(event.player as EntityPlayerMP)
        }
    }

    @SubscribeEvent
    open fun onPlayerLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        if (event.player is EntityPlayerMP) {
            this.onPlayerLogout(event.player as EntityPlayerMP)
        }
    }

    @SubscribeEvent
    open fun onPlayerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        if (event.player is EntityPlayerMP) {
            this.onPlayerRespawn(event.player as EntityPlayerMP)
        }
    }

    //    @SubscribeEvent
    //    public void onEntityConstructing(EntityEvent.EntityConstructing event)
    //    {
    //        if (event.getEntity() instanceof EntityPlayerMP && GCPlayerStats.get((EntityPlayerMP) event.entity) == null)
    //        {
    //            GCPlayerStats.register((EntityPlayerMP) event.entity);
    //        }
    //    }
    private fun onPlayerLogin(player: EntityPlayerMP) {
    }

    private fun onPlayerLogout(player: EntityPlayerMP) {
    }

    private fun onPlayerRespawn(player: EntityPlayerMP) {
    }

    open fun onPlayerUpdate(player: EntityPlayerMP) {
        if (!player.level().isRemote && player.level().provider is WorldProviderAsteroids) {
            val f: Int = 50

            if (player.level().rand.nextInt(f) == 0 && player.y < 260.0) {
                val closestPlayer: EntityPlayer? = player.level().getClosestPlayerToEntity(player, 100.0)

                if (closestPlayer == null || closestPlayer.getId() <= player.getId()) {
                    val x: Double
                    val y: Double
                    val z: Double
                    val motX: Double
                    val motY: Double
                    val motZ: Double
                    val r: Double = player.level().rand.nextInt(60) + 30.0
                    val theta: Double = Math.PI * 2.0 * player.level().rand.nextDouble()
                    x = player.x + cos(theta) * r
                    y = player.y + player.level().rand.nextInt(5)
                    z = player.z + sin(theta) * r
                    motX = (player.x - x + (player.level().rand.nextDouble() - 0.5) * 40) / 400.0f
                    motY = (player.level().rand.nextDouble() - 0.5) * 0.4
                    motZ = (player.z - z + (player.level().rand.nextDouble() - 0.5) * 40) / 400.0f

                    val smallAsteroid: EntitySmallAsteroid = EntitySmallAsteroid(player.level())
                    smallAsteroid.setPosition(x, y, z)
                    smallAsteroid.motionX = motX
                    smallAsteroid.motionY = motY
                    smallAsteroid.motionZ = motZ
                    smallAsteroid.spinYaw = player.level().rand.nextFloat() * 4
                    smallAsteroid.spinPitch = player.level().rand.nextFloat() * 2

                    player.level().spawnEntity(smallAsteroid)
                }
            }
        }
    }
}
