package galaxyspace.systems.SolarSystem.moons.callisto.world.gen

import galaxyspace.systems.SolarSystem.moons.callisto.world.gen.BiomeDecoratorCallisto
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import net.minecraft.world.World

open class BiomeDecoratorCallisto constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null


    override fun decorate() {
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
