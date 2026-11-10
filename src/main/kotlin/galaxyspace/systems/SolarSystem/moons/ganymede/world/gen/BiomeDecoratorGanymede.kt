package galaxyspace.systems.SolarSystem.moons.ganymede.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.core.configs.GSConfigWorld
import galaxyspace.systems.SolarSystem.moons.ganymede.world.gen.BiomeDecoratorGanymede
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenerator

open class BiomeDecoratorGanymede constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null

    private val OreGenMagnesium: WorldGenerator
    private val OreGenIlmenite: WorldGenerator

    init {
        //this.nickelGen = new WorldGenMinableMeta(GSBlocks.MERCURY_BLOCKS, 4, 3, true, GSBlocks.MERCURY_BLOCKS, 2);
        OreGenMagnesium = WorldGenMinableMeta(GSBlocks.GANYMEDE_BLOCKS, 4, 2, true, GSBlocks.GANYMEDE_BLOCKS, 1)
        OreGenIlmenite = WorldGenMinableMeta(GSBlocks.GANYMEDE_BLOCKS, 4, 3, true, GSBlocks.GANYMEDE_BLOCKS, 1)
    }

    override fun decorate() {
        if (GSConfigWorld.enableOresGeneration) {
            this.generateOre(16, OreGenMagnesium, 10, 60)
            this.generateOre(10, OreGenIlmenite, 10, 40)
        }
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
