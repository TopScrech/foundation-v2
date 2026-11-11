package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import foundation.inventory.offhandItems
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

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

        var meta: Int = par1ItemStack.legacyItemValue
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
        if (!player.isShiftKeyDown()) {
            return EnumActionResult.PASS
        }
        val state: IBlockState = world.getBlockState(pos)
        if (state.getBlock().isOpaqueCube(state) && state.getBlock() !is BlockPanelLighting) {
            val stack: ItemStack
            if (hand == EnumHand.OFF_HAND) {
                stack = player.inventory.offhandItems.get(0)
            } else {
                stack = player.inventory.getItem(player.inventory.selectedSlot)
            }
            if (stack.getItem() !== this) {
                return EnumActionResult.FAIL
            }
            if (world.isClientSide) {
                BlockPanelLighting.updateClient(stack.legacyItemValue, state)
            } else {
                var meta: Int = stack.legacyItemValue
                if (meta >= BlockPanelLighting.PANELTYPES_LENGTH) meta = 0
                val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                val panels: Array<IBlockState?> = (stats.getPanelLightingBases())!!
                panels[meta] = state
            }
        }

        return EnumActionResult.PASS
    }
}
