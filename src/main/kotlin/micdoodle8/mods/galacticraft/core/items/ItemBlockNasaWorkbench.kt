package micdoodle8.mods.galacticraft.core.items

import net.minecraft.network.chat.Component

import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Style

import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import kotlin.math.abs

open class ItemBlockNasaWorkbench constructor(block: Block?) : ItemBlockDesc(block) {
    override fun placeBlockAt(
        itemStack: ItemStack,
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        state: IBlockState
    ): Boolean {
        for (x: Int in -1..1) {
            for (y: Int in 0..3) {
                for (z: Int in -1..1) {
                    if (!(x == 0 && y == 0 && z == 0)) {
                        if (abs(x) != 1 || abs(z) != 1) {
                            val stateAt: IBlockState =
                                world.getBlockState(BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z))

                            if ((y == 0 || y == 3) && x == 0 && z == 0) {
                                if (!stateAt.getMaterial().isReplaceable()) {
                                    if (world.isClientSide) {
                                        FMLClientHandler.instance().getClient().ingameGUI.setOverlayMessage(
                                            Component.literal(GCCoreUtil.translate("gui.warning.noroom")).setStyle(
                                                Style().setColor(TextFormatting.RED)
                                            ).getFormattedText(),
                                            false
                                        )
                                    }
                                    return false
                                }
                            } else if (y != 0 && y != 3) {
                                if (!stateAt.getMaterial().isReplaceable()) {
                                    if (world.isClientSide) {
                                        FMLClientHandler.instance().getClient().ingameGUI.setOverlayMessage(
                                            Component.literal(GCCoreUtil.translate("gui.warning.noroom")).setStyle(
                                                Style().setColor(TextFormatting.RED)
                                            ).getFormattedText(),
                                            false
                                        )
                                    }
                                    return false
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.placeBlockAt(itemStack, player, world, pos, facing, hitX, hitY, hitZ, state)
    }
}
