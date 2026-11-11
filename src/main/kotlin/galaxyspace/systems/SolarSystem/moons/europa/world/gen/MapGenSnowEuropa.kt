package galaxyspace.systems.SolarSystem.moons.europa.world.gen

import foundation.block.getStateFromMeta

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.europa.world.gen.MapGenSnowEuropa
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.Level as World
import net.minecraft.world.chunk.ChunkPrimer

open class MapGenSnowEuropa : MapGenBaseMeta() {
    @JvmField
    var snow_layerBlock: Block = Blocks.SNOW_LAYER

    @JvmField
    var snow_layerBlockMeta: Byte = 0

    @JvmField
    var snowBlock: Block = Blocks.SNOW

    @JvmField
    var snowBlockMeta: Byte = 0

    override fun generate(world: World, chunkX: Int, chunkZ: Int, primer: ChunkPrimer?) {
        for (x: Int in 0..15) for (z: Int in 0..15) for (y: Int in 255 downTo 0) {
            if ((primer)!!.getBlockState(x, y, z).getBlock() === Blocks.AIR && (primer)!!.getBlockState(x, y - 1, z)
                    .getBlock() === GSBlocks.EUROPA_BLOCKS
            ) {
                if (world.random.nextInt(5) == 0) (primer)!!.setBlockState(
                    x,
                    y + 1,
                    z,
                    snow_layerBlock.getStateFromMeta(snow_layerBlockMeta.toInt())
                )
                (primer)!!.setBlockState(x, y, z, snowBlock.getStateFromMeta(snowBlockMeta.toInt()))
            }
        }
    }

    override fun recursiveGenerate(world: World?, chunkX: Int, chunkZ: Int, x: Int, z: Int, primer: ChunkPrimer?) {
    }
}
