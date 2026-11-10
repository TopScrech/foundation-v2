package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
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
                                    if (world.isRemote) {
                                        FMLClientHandler.instance().getClient().ingameGUI.setOverlayMessage(
                                            TextComponentString(GCCoreUtil.translate("gui.warning.noroom")).setStyle(
                                                Style().setColor(TextFormatting.RED)
                                            ).getFormattedText(),
                                            false
                                        )
                                    }
                                    return false
                                }
                            } else if (y != 0 && y != 3) {
                                if (!stateAt.getMaterial().isReplaceable()) {
                                    if (world.isRemote) {
                                        FMLClientHandler.instance().getClient().ingameGUI.setOverlayMessage(
                                            TextComponentString(GCCoreUtil.translate("gui.warning.noroom")).setStyle(
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
