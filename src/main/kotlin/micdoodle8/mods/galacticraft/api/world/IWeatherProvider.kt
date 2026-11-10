package micdoodle8.mods.galacticraft.api.world

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.particle.Particle
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

interface IWeatherProvider {
    @SideOnly(Side.CLIENT)
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
