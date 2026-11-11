package micdoodle8.mods.galacticraft.planets.mars.world.gen

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator

open class BiomeDecoratorMars constructor() : BiomeDecoratorSpace() {
    private val dirtGen: WorldGenerator
    private val deshGen: WorldGenerator
    private val tinGen: WorldGenerator
    private val copperGen: WorldGenerator
    private val ironGen: WorldGenerator
    private val iceGen: WorldGenerator
    private var currentWorld: World? = null

    init {
        this.copperGen = WorldGenMinableMeta((MarsBlocks.marsBlock)!!, 4, 0, true, (MarsBlocks.marsBlock)!!, 9)
        this.tinGen = WorldGenMinableMeta((MarsBlocks.marsBlock)!!, 4, 1, true, (MarsBlocks.marsBlock)!!, 9)
        this.deshGen = WorldGenMinableMeta((MarsBlocks.marsBlock)!!, 6, 2, true, (MarsBlocks.marsBlock)!!, 9)
        this.ironGen = WorldGenMinableMeta((MarsBlocks.marsBlock)!!, 8, 3, true, (MarsBlocks.marsBlock)!!, 9)
        this.dirtGen = WorldGenMinableMeta((MarsBlocks.marsBlock)!!, 32, 6, true, (MarsBlocks.marsBlock)!!, 9)
        this.iceGen = WorldGenMinableMeta(Blocks.ICE, 18, 0, true, (MarsBlocks.marsBlock)!!, 6)
    }

    override fun decorate() {
        this.generateOre(4, this.iceGen, 60, 120)
        this.generateOre(20, this.dirtGen, 0, 200)
        if (!ConfigManagerMars.disableDeshGen) {
            this.generateOre(15, this.deshGen, 20, 64)
        }
        if (!ConfigManagerMars.disableCopperGen) {
            this.generateOre(26, this.copperGen, 0, 60)
        }
        if (!ConfigManagerMars.disableTinGen) {
            this.generateOre(23, this.tinGen, 0, 60)
        }
        if (!ConfigManagerMars.disableIronGen) {
            this.generateOre(20, this.ironGen, 0, 64)
        }
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
