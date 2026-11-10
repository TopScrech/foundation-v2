package galaxyspace.systems.SolarSystem.moons.ganymede.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.ganymede.blocks.GanymedeBlocks.EnumGanymedeBlocks
import galaxyspace.systems.SolarSystem.moons.ganymede.blocks.GanymedeBlocks
import galaxyspace.systems.SolarSystem.moons.ganymede.world.gen.MapGenSnowGanymede
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkPrimer

open class MapGenSnowGanymede : MapGenBaseMeta() {
    @JvmField
    var icelayerBlock: IBlockState = GSBlocks.SURFACE_ICE.getStateFromMeta(4)

    override fun generate(world: World, chunkX: Int, chunkZ: Int, primer: ChunkPrimer?) {
        for (x: Int in 0..15) for (z: Int in 0..15) for (y: Int in 255 downTo 0) {
            if ((y < 52 + world.rand.nextInt(2) || y > 90 - world.rand.nextInt(2)) && (primer)!!.getBlockState(x, y, z)
                    .getBlock() === Blocks.AIR && (primer)!!.getBlockState(
                    x,
                    y - 1,
                    z
                ) === GSBlocks.GANYMEDE_BLOCKS.getDefaultState().withProperty(
                    GanymedeBlocks.Companion.BASIC_TYPE,
                    EnumGanymedeBlocks.GANYMEDE_GRUNT
                )
            ) {
                (primer)!!.setBlockState(x, y, z, icelayerBlock)
            }
        }
    }

    override fun recursiveGenerate(world: World?, chunkX: Int, chunkZ: Int, x: Int, z: Int, primer: ChunkPrimer?) {
    }
}
