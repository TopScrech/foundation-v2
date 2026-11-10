package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.fluid.OxygenPressureProtocol
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.Random

open class BlockBrightBreathableAir constructor(assetName: String?) : BlockAir() {
    init {
        this.setResistance(1000.0f)
        this.setDefaultState(this.blockState.getBaseState().withProperty(THERMAL, false))
        this.setHardness(0.0f)
        this.setTranslationKey(assetName)
        this.setLightLevel(1.0f)
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return true
    }

    public override fun getPushReaction(state: IBlockState?): EnumPushReaction {
        return EnumPushReaction.DESTROY
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(Blocks.AIR)
    }

    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        val block: Block = blockAccess.getBlockState(pos).getBlock()
        if (block === this || block === GCBlocks.breatheableAir) {
            return false
        } else {
            return block is BlockAir
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (Blocks.AIR === blockIn)  //Do no check if replacing breatheableAir with a solid block, although that could be dividing a sealed space
        {
            OxygenPressureProtocol.onEdgeBlockUpdated(worldIn, pos)
        }
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, THERMAL)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(THERMAL, meta % 2 == 1)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (if (state.getValue(THERMAL)) 1 else 0)
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        return 15
    }

    override fun getLightOpacity(state: IBlockState): Int {
        return 0
    }

    override fun breakBlock(worldIn: World, vec: BlockPos, state: IBlockState) {
    }

    companion object {
        @JvmField
        val THERMAL: PropertyBool = PropertyBool.create("thermal")
    }
}
