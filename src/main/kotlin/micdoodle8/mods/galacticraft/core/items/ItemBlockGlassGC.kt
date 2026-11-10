package micdoodle8.mods.galacticraft.core.items

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ItemBlockGlassGC constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    override fun placeBlockAt(
        stack: ItemStack,
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        newState: IBlockState
    ): Boolean {
        if (!world.setBlockState(pos, newState, 3)) return false

        val state: IBlockState = world.getBlockState(pos)
        if (state.getBlock().javaClass == this.block.javaClass) {
            setTileEntityNBT(world, player, pos, stack)
            this.block.onBlockPlacedBy(world, pos, state, player, stack)
        }

        return true
    }
}
