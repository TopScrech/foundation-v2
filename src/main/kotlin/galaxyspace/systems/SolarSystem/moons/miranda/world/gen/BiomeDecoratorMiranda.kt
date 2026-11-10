package galaxyspace.systems.SolarSystem.moons.miranda.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.core.configs.GSConfigWorld
import asmodeuscore.core.astronomy.dimension.world.gen.WorldGenLakes
import galaxyspace.systems.SolarSystem.moons.miranda.world.gen.BiomeDecoratorMiranda
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenerator

open class BiomeDecoratorMiranda constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null

    private val OreGenIron: WorldGenerator
    private val OreGenDolomite: WorldGenerator
    private val OreGenDiamond: WorldGenerator
    private val OreGenQuartz: WorldGenerator
    private val OreGenCobalt: WorldGenerator
    private val OreGenNickel: WorldGenerator
    private val stoneGen_0: WorldGenerator
    private val stoneGen_1: WorldGenerator
    private val stoneGen_2: WorldGenerator
    private val lavalakesGen: WorldGenerator

    init {
        //this.nickelGen = new WorldGenMinableMeta(GSBlocks.MERCURY_BLOCKS, 4, 3, true, GSBlocks.MERCURY_BLOCKS, 2);
        OreGenIron = WorldGenMinableMeta(GSBlocks.MIRANDA_BLOCKS, 6, 3, true, GSBlocks.MIRANDA_BLOCKS, 13)
        OreGenDolomite = WorldGenMinableMeta(GSBlocks.MIRANDA_BLOCKS, 4, 4, true, GSBlocks.MIRANDA_BLOCKS, 13)
        OreGenDiamond = WorldGenMinableMeta(GSBlocks.MIRANDA_BLOCKS, 4, 5, true, GSBlocks.MIRANDA_BLOCKS, 2)
        OreGenQuartz = WorldGenMinableMeta(GSBlocks.MIRANDA_BLOCKS, 10, 6, true, GSBlocks.MIRANDA_BLOCKS, 2)
        OreGenCobalt = WorldGenMinableMeta(GSBlocks.MIRANDA_BLOCKS, 10, 7, true, GSBlocks.MIRANDA_BLOCKS, 13)
        OreGenNickel = WorldGenMinableMeta(GSBlocks.MIRANDA_BLOCKS, 10, 8, true, GSBlocks.MIRANDA_BLOCKS, 14)

        stoneGen_0 = WorldGenMinableMeta(GSBlocks.MIRANDA_BLOCKS, 14, 2, true, GSBlocks.MIRANDA_BLOCKS, 13)
        stoneGen_1 = WorldGenMinableMeta(GSBlocks.MIRANDA_BLOCKS, 14, 2, true, GSBlocks.MIRANDA_BLOCKS, 14)
        stoneGen_2 = WorldGenMinableMeta(GSBlocks.MIRANDA_BLOCKS, 14, 14, true, GSBlocks.MIRANDA_BLOCKS, 2)

        lavalakesGen = WorldGenLakes(Blocks.FLOWING_LAVA, 0, GSBlocks.MIRANDA_BLOCKS, 2)
    }

    override fun decorate() {
        if (GSConfigWorld.enableOresGeneration) {
            this.generateOre(16, OreGenIron, 10, 80)
            this.generateOre(10, OreGenDolomite, 10, 40)
            this.generateOre(4, OreGenDiamond, 5, 16)
            this.generateOre(16, OreGenQuartz, 10, 90)
            this.generateOre(10, OreGenCobalt, 10, 50)
            this.generateOre(8, OreGenNickel, 30, 90)
        }

        this.generateOre(150, stoneGen_0, 10, 100)
        this.generateOre(150, stoneGen_1, 10, 100)
        this.generateOre(150, stoneGen_2, 10, 100)

        val x: Int = posX + (rand)!!.nextInt(16) + 8
        val z: Int = posZ + (rand)!!.nextInt(16) + 8

        if ((rand)!!.nextInt(10) == 0) {
            val y: Int = 10 + (rand)!!.nextInt(30)
            val state: IBlockState = this.getCurrentWorld()!!.getBlockState(BlockPos(x, y, z).down())
            if (state === GSBlocks.MIRANDA_BLOCKS.getStateFromMeta(2)) {
                this.lavalakesGen.generate(this.getCurrentWorld(), this.rand, BlockPos(x, y, z))
            }
        }
    }

    override fun setCurrentWorld(world: World?) {
        this.currentWorld = world
    }

    override fun getCurrentWorld(): World? {
        return this.currentWorld
    }
}
