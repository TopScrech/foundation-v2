package galaxyspace.systems.SolarSystem.moons.phobos.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.phobos.world.gen.BiomeDecoratorPhobos
import galaxyspace.core.configs.GSConfigWorld
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator

open class BiomeDecoratorPhobos constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null
    private val OreGenIron: WorldGenerator
    private val OreGenNickel: WorldGenerator
    private val OreGenDesh: WorldGenerator
    private val OreGenMeteoricIron: WorldGenerator
    private val OreGenRegolite: WorldGenerator

    init {
        OreGenIron = WorldGenMinableMeta(GSBlocks.PHOBOS_BLOCKS, 6, 2, true, GSBlocks.PHOBOS_BLOCKS, 1)
        OreGenNickel = WorldGenMinableMeta(GSBlocks.PHOBOS_BLOCKS, 6, 4, true, GSBlocks.PHOBOS_BLOCKS, 1)
        OreGenDesh = WorldGenMinableMeta(GSBlocks.PHOBOS_BLOCKS, 4, 5, true, GSBlocks.PHOBOS_BLOCKS, 1)
        OreGenMeteoricIron = WorldGenMinableMeta(GSBlocks.PHOBOS_BLOCKS, 3, 3, true, GSBlocks.PHOBOS_BLOCKS, 1)
        OreGenRegolite = WorldGenMinableMeta(GSBlocks.PHOBOS_BLOCKS, 20, 0, true, GSBlocks.PHOBOS_BLOCKS, 1)
    }

    override fun decorate() {
        if (GSConfigWorld.enableOresGeneration) {
            this.generateOre(16, OreGenIron, 10, 60)
            this.generateOre(4, OreGenMeteoricIron, 10, 30)
            this.generateOre(8, OreGenNickel, 10, 40)
            this.generateOre(4, OreGenDesh, 10, 80)
            this.generateOre(20, OreGenRegolite, 10, 80)
        }
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
