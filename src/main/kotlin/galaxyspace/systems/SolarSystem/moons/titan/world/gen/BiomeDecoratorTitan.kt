package galaxyspace.systems.SolarSystem.moons.titan.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.core.configs.GSConfigWorld
import galaxyspace.systems.SolarSystem.moons.titan.world.gen.BiomeDecoratorTitan
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator

open class BiomeDecoratorTitan constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null
    private val subgruntGen: WorldGenerator
    private val sapphireOre: WorldGenerator
    private val emeraldOre: WorldGenerator
    private val diamondOre: WorldGenerator
    private val coalOre: WorldGenerator
    private val lapisOre: WorldGenerator
    private val redstoneOre: WorldGenerator


    init {
        this.subgruntGen = WorldGenMinableMeta(GSBlocks.TITAN_BLOCKS, 38, 1, true, GSBlocks.TITAN_BLOCKS, 0)
        this.sapphireOre = WorldGenMinableMeta(GSBlocks.TITAN_BLOCKS, 4, 3, true, GSBlocks.TITAN_BLOCKS, 2)
        this.emeraldOre = WorldGenMinableMeta(GSBlocks.TITAN_BLOCKS, 4, 4, true, GSBlocks.TITAN_BLOCKS, 2)
        this.diamondOre = WorldGenMinableMeta(GSBlocks.TITAN_BLOCKS, 6, 5, true, GSBlocks.TITAN_BLOCKS, 2)
        this.coalOre = WorldGenMinableMeta(GSBlocks.TITAN_BLOCKS, 16, 6, true, GSBlocks.TITAN_BLOCKS, 2)
        this.lapisOre = WorldGenMinableMeta(GSBlocks.TITAN_BLOCKS, 4, 7, true, GSBlocks.TITAN_BLOCKS, 2)
        this.redstoneOre = WorldGenMinableMeta(GSBlocks.TITAN_BLOCKS, 6, 8, true, GSBlocks.TITAN_BLOCKS, 2)
    }

    override fun decorate() {
        this.generateOre(25, subgruntGen, 0, 256)
        if (GSConfigWorld.enableOresGeneration) {
            this.generateOre(6, sapphireOre, 0, 40)
            this.generateOre(4, emeraldOre, 0, 20)
            this.generateOre(6, diamondOre, 0, 20)
            this.generateOre(16, coalOre, 0, 90)
            this.generateOre(8, lapisOre, 0, 40)
            this.generateOre(10, redstoneOre, 0, 30)
        }
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }

    override fun generateOre(amountPerChunk: Int, worldGenerator: WorldGenerator, minY: Int, maxY: Int) {
        val currentWorld: World? = this.getCurrentWorld()
        for (var5: Int in 0..<amountPerChunk) {
            val var6: Int = this.posX + (this.rand)!!.nextInt(16)
            val var7: Int = (this.rand)!!.nextInt(maxY - minY) + minY
            val var8: Int = this.posZ + (this.rand)!!.nextInt(16)
            if (currentWorld!!.isBlockLoaded(BlockPos(var6, var7, var8))) worldGenerator.generate(
                currentWorld,
                this.rand,
                BlockPos(var6, var7, var8)
            )
        }
    }
}
