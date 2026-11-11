package micdoodle8.mods.galacticraft.api.world

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.particle.Particle
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

interface IWeatherProvider {
    @OnlyIn(Dist.CLIENT)
    fun getParticle(world: WorldClient?, x: Double, y: Double, z: Double): Particle?

    fun weatherSounds(
        j: Int,
        mc: Minecraft?,
        world: World?,
        blockpos: BlockPos?,
        xx: Double,
        yy: Double,
        zz: Double,
        random: Random?
    )

    fun getSoundInterval(f: Float): Int
}
