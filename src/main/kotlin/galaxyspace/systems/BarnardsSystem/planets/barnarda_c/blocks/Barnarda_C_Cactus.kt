package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Falling_Blocks.EnumFallingBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Falling_Blocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Cactus
import galaxyspace.core.util.GSCreativeTabs
import net.minecraft.block.BlockCactus
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.common.IPlantable

open class Barnarda_C_Cactus constructor() : BlockCactus() {
    init {
        setTranslationKey("barnarda_c_cactus")
        setDefaultState(blockState.getBaseState().setValue(AGE, 0))
        setTickRandomly(true)
        setCreativeTab(GSCreativeTabs.GSBlocksTab)
        setHardness(0.4f)
        setSoundType(SoundType.CLOTH)
    }

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        val plant: IBlockState = plantable.getPlant(world, pos.offset(direction))

        val sandBlockState: IBlockState = BRBlocks.BARNARDA_C_FALLING_BLOCKS.defaultBlockState()
            .setValue(
                Barnarda_C_Falling_Blocks.Companion.BASIC_TYPE,
                EnumFallingBlockBarnardaC.SAND
            )

        if (plant.getBlock() === BRBlocks.BARNARDA_C_CACTUS) {
            return this === BRBlocks.BARNARDA_C_CACTUS || this === sandBlockState.getBlock()
        }

        return false
    }
}
