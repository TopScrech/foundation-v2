package micdoodle8.mods.galacticraft.core.items

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

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
        if (!world.setBlock(pos, newState, 3)) return false

        val state: IBlockState = world.getBlockState(pos)
        if (state.getBlock().javaClass == this.block.javaClass) {
            setTileEntityNBT(world, player, pos, stack)
            this.block.onBlockPlacedBy(world, pos, state, player, stack)
        }

        return true
    }
}
