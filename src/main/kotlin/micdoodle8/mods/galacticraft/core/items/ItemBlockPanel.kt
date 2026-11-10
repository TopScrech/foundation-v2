package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ItemBlockPanel constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        val name: String = ""

        var meta: Int = par1ItemStack.getItemDamage()
        if (meta >= BlockPanelLighting.PANELTYPES_LENGTH) {
            meta = 0
        }

        return this.getBlock().getTranslationKey() + "_" + meta
    }

    override fun onItemUseFirst(
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        hand: EnumHand
    ): EnumActionResult {
        if (!player.isSneaking()) {
            return EnumActionResult.PASS
        }
        val state: IBlockState = world.getBlockState(pos)
        if (state.getBlock().isOpaqueCube(state) && state.getBlock() !is BlockPanelLighting) {
            val stack: ItemStack
            if (hand == EnumHand.OFF_HAND) {
                stack = player.inventory.offHandInventory.get(0)
            } else {
                stack = player.inventory.getStackInSlot(player.inventory.currentItem)
            }
            if (stack.getItem() !== this) {
                return EnumActionResult.FAIL
            }
            if (world.isRemote) {
                BlockPanelLighting.updateClient(stack.getItemDamage(), state)
            } else {
                var meta: Int = stack.getItemDamage()
                if (meta >= BlockPanelLighting.PANELTYPES_LENGTH) meta = 0
                val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                val panels: Array<IBlockState?> = (stats.getPanelLightingBases())!!
                panels[meta] = state
            }
        }

        return EnumActionResult.PASS
    }
}
