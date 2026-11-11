package galaxyspace.systems.SolarSystem.planets.mercury.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.core.configs.GSConfigWorld
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator

open class BiomeDecoratorMercury constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null
    private val ironGen: WorldGenerator
    private val nickelGen: WorldGenerator
    private val magnesiumGen: WorldGenerator
    private val magmaGen: WorldGenerator

    init {
        this.ironGen = WorldGenMinableMeta(GSBlocks.MERCURY_BLOCKS, 5, 4, true, GSBlocks.MERCURY_BLOCKS, 2)
        this.nickelGen = WorldGenMinableMeta(GSBlocks.MERCURY_BLOCKS, 4, 3, true, GSBlocks.MERCURY_BLOCKS, 2)
        this.magnesiumGen = WorldGenMinableMeta(GSBlocks.MERCURY_BLOCKS, 5, 5, true, GSBlocks.MERCURY_BLOCKS, 2)
        this.magmaGen = WorldGenMinableMeta(Blocks.MAGMA, 20, 0, true, Blocks.LAVA, 0)
    }

    override fun decorate() {
        if (GSConfigWorld.enableOresGeneration) {
            this.generateOre(18, ironGen, 5, 60)
            this.generateOre(12, nickelGen, 5, 60)
            this.generateOre(8, magnesiumGen, 5, 40)
        }

        this.generateOre(18, magmaGen, 5, 25)
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
