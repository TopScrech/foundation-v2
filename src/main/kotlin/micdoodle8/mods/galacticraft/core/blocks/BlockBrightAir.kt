package micdoodle8.mods.galacticraft.core.blocks

import net.minecraft.block.BlockAir
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.Random

open class BlockBrightAir constructor(assetName: String?) : BlockAir() {
    init {
        this.setResistance(1000.0f)
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
        return false
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        return 15
    }

    override fun getLightOpacity(state: IBlockState): Int {
        return 0
    }

    override fun breakBlock(worldIn: World, vec: BlockPos, state: IBlockState) {
    }
}
