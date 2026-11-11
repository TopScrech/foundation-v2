package galaxyspace.systems.SolarSystem.moons.enceladus.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.core.configs.GSConfigWorld
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks.EnumEnceladusBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusCrystal
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator

open class BiomeDecoratorEnceladus constructor() : BiomeDecoratorSpace() {
    private var currentWorld: World? = null
    private val coalGen: WorldGenerator


    init {
        this.coalGen = WorldGenMinableMeta(GSBlocks.ENCELADUS_BLOCKS, 10, 2, true, GSBlocks.ENCELADUS_BLOCKS, 1)
    }

    override fun decorate() {
        if (GSConfigWorld.enableOresGeneration) {
            this.generateOre(10, coalGen, 10, 60)
        }

        val j: Int = (rand)!!.nextInt(4)
        for (i: Int in 0..49) {
            val randPosX: Int = posX + (rand)!!.nextInt(16) + 8
            val randPosY: Int = (rand)!!.nextInt(80)
            val randPosZ: Int = posZ + (rand)!!.nextInt(16) + 8

            val pos: BlockPos = BlockPos(randPosX, randPosY, randPosZ)
            if (this.getCurrentWorld()!!.getBlockState(pos) === GSBlocks.ENCELADUS_BLOCKS.defaultBlockState()
                    .setValue(
                        EnceladusBlocks.BASIC_TYPE,
                        EnumEnceladusBlocks.ENCELADUS_GRUNT
                    )
            ) {
                if (this.getCurrentWorld()!!.isEmptyBlock(pos.below())) this.getCurrentWorld()!!.setBlockAndUpdate(
                    pos.below(),
                    GSBlocks.ENCELADUS_CRYSTAL.defaultBlockState()
                        .setValue(EnceladusCrystal.FACING, EnumFacing.HORIZONTALS[j])
                )

                if (this.getCurrentWorld()!!.isEmptyBlock(pos.above())) this.getCurrentWorld()!!.setBlockAndUpdate(
                    pos.above(),
                    GSBlocks.ENCELADUS_CRYSTAL.defaultBlockState()
                        .setValue(EnceladusCrystal.FACING, EnumFacing.HORIZONTALS[j])
                )
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
