package galaxyspace.core.prefab.items

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemDoor
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ItemDoorGS constructor(name: String, door: Block) : Item(), ISortableItem {
    private val door: Block

    init {
        this.setTranslationKey(name)
        this.door = door
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSBlocksTab
    }

    override fun onItemUse(
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): EnumActionResult {
        var pos: BlockPos = pos
        val itemStack: ItemStack = player.getHeldItem(hand)

        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL
        } else {
            val iblockstate: IBlockState = world.getBlockState(pos)
            val block: Block = iblockstate.getBlock()

            if (!block.isReplaceable(world, pos)) {
                pos = pos.offset(facing)
            }
            if (player.canPlayerEdit(pos, facing, itemStack) && this.door.canPlaceBlockAt(world, pos)) {
                val enumfacing: EnumFacing = EnumFacing.fromAngle(player.rotationYaw.toDouble())
                val i: Int = enumfacing.getXOffset()
                val j: Int = enumfacing.getZOffset()
                val flag: Boolean =
                    i < 0 && hitZ < 0.5f || i > 0 && hitZ > 0.5f || j < 0 && hitX > 0.5f || j > 0 && hitX < 0.5f
                ItemDoor.placeDoor(world, pos, enumfacing, this.door, flag)
                val soundtype: SoundType =
                    world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player)
                world.playSound(
                    player,
                    pos,
                    soundtype.getPlaceSound(),
                    SoundCategory.BLOCKS,
                    (soundtype.getVolume() + 1.0f) / 2.0f,
                    soundtype.getPitch() * 0.8f
                )
                itemStack.shrink(1)
                return EnumActionResult.SUCCESS
            } else {
                return EnumActionResult.FAIL
            }
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
