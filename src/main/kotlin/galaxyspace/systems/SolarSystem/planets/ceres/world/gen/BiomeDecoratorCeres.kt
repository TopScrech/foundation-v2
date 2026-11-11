package galaxyspace.systems.SolarSystem.planets.ceres.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.core.configs.GSConfigWorld
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator

open class BiomeDecoratorCeres constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null
    private val iceGen: WorldGenerator
    private val meteoricIronGen: WorldGenerator
    private val dolomiteGen: WorldGenerator

    init {
        this.dolomiteGen = WorldGenMinableMeta(GSBlocks.CERES_BLOCKS, 6, 2, true, GSBlocks.CERES_BLOCKS, 1)
        this.meteoricIronGen = WorldGenMinableMeta(GSBlocks.CERES_BLOCKS, 4, 3, true, GSBlocks.CERES_BLOCKS, 1)
        this.iceGen = WorldGenMinableMeta(GSBlocks.SURFACE_ICE, 8, 4, true, GSBlocks.CERES_BLOCKS, 1)
    }

    override fun decorate() {
        if (GSConfigWorld.enableOresGeneration) {
            this.generateOre(12, dolomiteGen, 10, 60)
            this.generateOre(12, meteoricIronGen, 5, 40)
            this.generateOre(20, iceGen, 5, 60)
        }
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
