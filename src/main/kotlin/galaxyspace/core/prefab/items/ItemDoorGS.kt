package galaxyspace.core.prefab.items

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.item.ItemDoor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

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
        val itemStack: ItemStack = player.getItemInHand(hand)

        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL
        } else {
            val iblockstate: IBlockState = world.getBlockState(pos)
            val block: Block = iblockstate.getBlock()

            if (!block.isReplaceable(world, pos)) {
                pos = pos.offset(facing)
            }
            if (player.canPlayerEdit(pos, facing, itemStack) && this.door.canPlaceBlockAt(world, pos)) {
                val enumfacing: EnumFacing = EnumFacing.fromAngle(player.yRot.toDouble())
                val i: Int = enumfacing.getStepX()
                val j: Int = enumfacing.getStepZ()
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
