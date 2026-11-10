package micdoodle8.mods.galacticraft.planets.asteroids.entities.player

import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent

import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntitySmallAsteroid
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
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
        if (!player.world.isRemote && player.world.provider is WorldProviderAsteroids) {
            val f: Int = 50

            if (player.world.rand.nextInt(f) == 0 && player.posY < 260.0) {
                val closestPlayer: EntityPlayer? = player.world.getClosestPlayerToEntity(player, 100.0)

                if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId()) {
                    val x: Double
                    val y: Double
                    val z: Double
                    val motX: Double
                    val motY: Double
                    val motZ: Double
                    val r: Double = player.world.rand.nextInt(60) + 30.0
                    val theta: Double = Math.PI * 2.0 * player.world.rand.nextDouble()
                    x = player.posX + cos(theta) * r
                    y = player.posY + player.world.rand.nextInt(5)
                    z = player.posZ + sin(theta) * r
                    motX = (player.posX - x + (player.world.rand.nextDouble() - 0.5) * 40) / 400.0f
                    motY = (player.world.rand.nextDouble() - 0.5) * 0.4
                    motZ = (player.posZ - z + (player.world.rand.nextDouble() - 0.5) * 40) / 400.0f

                    val smallAsteroid: EntitySmallAsteroid = EntitySmallAsteroid(player.world)
                    smallAsteroid.setPosition(x, y, z)
                    smallAsteroid.motionX = motX
                    smallAsteroid.motionY = motY
                    smallAsteroid.motionZ = motZ
                    smallAsteroid.spinYaw = player.world.rand.nextFloat() * 4
                    smallAsteroid.spinPitch = player.world.rand.nextFloat() * 2

                    player.world.spawnEntity(smallAsteroid)
                }
            }
        }
    }
}
