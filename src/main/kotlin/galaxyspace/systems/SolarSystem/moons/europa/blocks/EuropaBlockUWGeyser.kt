package galaxyspace.systems.SolarSystem.moons.europa.blocks

import galaxyspace.GalaxySpace
import galaxyspace.api.block.IEnergyGeyser
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldProviderSurface
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class EuropaBlockUWGeyser constructor() : Block(Material.GRASS), IEnergyGeyser {
    init {
        this.setTranslationKey("europa_uwgeyser")
        this.setSoundType(SoundType.GLASS)
        this.setHardness(0.5f)
    }

    override fun onEntityWalk(world: World, pos: BlockPos, entity: Entity) {
        entity.motionY += 0.8
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, world: World, pos: BlockPos, rand: Random) {
        var gravity: Double = 1.0
        if (world.provider is WorldProviderSurface) gravity = 0.05

        if (world.getBlockState(pos.down()).getBlock() === FluidRegistry.WATER.getBlock()) {
            if (world.isAirBlock(
                    BlockPos(
                        pos.getX(),
                        pos.getY() + 1,
                        pos.getZ()
                    )
                ) || world.getBlockState(BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()))
                    .getBlock() === Blocks.SNOW_LAYER
            ) {
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, 0.6 * gravity, 0.0),
                    (arrayOf<Any>(50, 5, false, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, 0.6 * gravity, 0.0),
                    (arrayOf<Any>(50, 5, false, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, 0.6 * gravity, 0.0),
                    (arrayOf<Any>(50, 5, false, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )

                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0 + 0.03, 0.6 * gravity, 0.0 + 0.03),
                    (arrayOf<Any>(160, 17, true, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, 0.6 * gravity, 0.0),
                    (arrayOf<Any>(160, 17, true, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0 - 0.03, 0.6 * gravity, 0.0 - 0.03),
                    (arrayOf<Any>(160, 17, true, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )

                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0 + 0.03, 0.6 * gravity, 0.0 - 0.03),
                    (arrayOf<Any>(160, 17, true, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0 - 0.03, 0.6 * gravity, 0.0 + 0.03),
                    (arrayOf<Any>(160, 17, true, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )

                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0 + 0.03, 0.6 * gravity, 0.0 + 0.03),
                    (arrayOf<Any>(160, 17, true, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, 0.6 * gravity, 0.0),
                    (arrayOf<Any>(160, 17, true, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0 - 0.03, 0.6 * gravity, 0.0 - 0.03),
                    (arrayOf<Any>(160, 17, true, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
            } else if (world.getBlockState(BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()))
                    .getMaterial() === Material.WATER
            ) {
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, 0.3, 0.0),
                    (arrayOf<Any>(50, 32, false, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, 0.3, 0.0),
                    (arrayOf<Any>(50, 32, false, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, 0.3, 0.0),
                    (arrayOf<Any>(50, 32, false, Vector3(1.0, 1.0, 1.0), 1.0) as Array<Any?>?)
                )
            }
        }
    }

    override fun isWorkGeyser(world: World?, state: IBlockState?, pos: BlockPos?): Boolean {
        return (world)!!.getBlockState((pos)!!.down()).getBlock() === FluidRegistry.WATER.getBlock()
    }
}
