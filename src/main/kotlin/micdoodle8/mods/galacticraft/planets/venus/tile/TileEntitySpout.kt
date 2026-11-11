package micdoodle8.mods.galacticraft.planets.venus.tile

import java.util.Random

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ITickable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.*

open class TileEntitySpout : TileEntity(), ITickable {
    private val rand = Random(System.currentTimeMillis())

    override fun update() {
        if (this.world.isRemote) {
            if (rand.nextInt(400) == 0) {
                val stateAbove: IBlockState = this.world.getBlockState(this.getPos().up())
                if (stateAbove.getBlock()
                        .isAir(this.world.getBlockState(this.getPos().up()), this.world, this.getPos().up())
                ) {
                    val posX: Double = pos.getX().toDouble() + 0.45 + rand.nextDouble() * 0.1
                    val posY: Double = pos.getY().toDouble() + 1.0
                    val posZ: Double = pos.getZ().toDouble() + 0.45 + rand.nextDouble() * 0.1
                    for (i: Int in 0..<4 + rand.nextInt(4)) {
                        GalacticraftPlanets.spawnParticle(
                            "acidVapor",
                            Vector3(posX, posY, posZ),
                            Vector3(
                                rand.nextDouble() * 0.5 - 0.25,
                                rand.nextDouble() * 0.5 + 0.5,
                                rand.nextDouble() * 0.5 - 0.25
                            )
                        )
                    }
                }
            }
        }
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }
}
