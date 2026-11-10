package galaxyspace.systems.SolarSystem.moons.miranda.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks.EnumMirandaBlocks
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkPrimer

open class MapGenIceMiranda : MapGenBaseMeta() {
    @JvmField
    var icelayerBlock: IBlockState = GSBlocks.MIRANDA_BLOCKS.getDefaultState()
        .withProperty(MirandaBlocks.BASIC_TYPE, EnumMirandaBlocks.MIRANDA_ICE)

    override fun generate(world: World, chunkX: Int, chunkZ: Int, primer: ChunkPrimer?) {
        for (x: Int in 0..15) for (z: Int in 0..15) for (y: Int in 255 downTo 0) {
            if (y < 54 + world.rand.nextInt(5) && (primer)!!.getBlockState(x, y, z).getBlock() === Blocks.AIR &&
                ((primer)!!.getBlockState(x, y - 1, z) === GSBlocks.MIRANDA_BLOCKS.getDefaultState()
                    .withProperty(
                        MirandaBlocks.BASIC_TYPE,
                        EnumMirandaBlocks.MIRANDA_GRUNT
                    ) || (primer)!!.getBlockState(x, y - 1, z) === GSBlocks.MIRANDA_BLOCKS.getDefaultState()
                    .withProperty(
                        MirandaBlocks.BASIC_TYPE,
                        EnumMirandaBlocks.MIRANDA_GRUNT_2
                    ) || (primer)!!.getBlockState(x, y - 1, z) === GSBlocks.MIRANDA_BLOCKS.getDefaultState()
                    .withProperty(
                        MirandaBlocks.BASIC_TYPE,
                        EnumMirandaBlocks.MIRANDA_GRUNT_3
                    )
                        )
            ) {
                (primer)!!.setBlockState(x, y, z, icelayerBlock)
            }
        }
    }

    override fun recursiveGenerate(world: World?, chunkX: Int, chunkZ: Int, x: Int, z: Int, primer: ChunkPrimer?) {
    }
}
