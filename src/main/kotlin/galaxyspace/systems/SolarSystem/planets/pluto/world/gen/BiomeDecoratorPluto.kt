package galaxyspace.systems.SolarSystem.planets.pluto.world.gen

import galaxyspace.systems.SolarSystem.planets.pluto.world.gen.BiomeDecoratorPluto
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import net.minecraft.world.level.Level as World

open class BiomeDecoratorPluto  //private WorldGenerator nickelGen;
constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null

    override fun decorate() {
        //if(GSConfigCore.enableOresGeneration) this.generateOre(12, nickelGen, 5, 20);
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
