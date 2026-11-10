package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvancedTile
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard

open class ItemBlockDesc constructor(block: Block?) : ItemBlockGC((block)!!) {
    override fun onCreated(stack: ItemStack, world: World, player: EntityPlayer) {
        if (!world.isRemote) {
            return
        }

        //The player could be a FakePlayer made by another mod e.g. LogisticsPipes
        if (player is EntityPlayerSP) {
            if (this.getBlock() === GCBlocks.fuelLoader) {
                ClientProxyCore.playerClientHandler.onBuild(4, player)
            } else if (this.getBlock() === GCBlocks.fuelLoader) {
                ClientProxyCore.playerClientHandler.onBuild(6, player)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, info: MutableList<String?>, flagIn: ITooltipFlag) {
        if (this.getBlock() is IShiftDescription && (this.getBlock() as IShiftDescription).showDescription(stack.getItemDamage())) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                info.addAll(
                    FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(
                        (this.getBlock() as IShiftDescription).getShiftDescription(
                            stack.getItemDamage()
                        ), 150
                    )
                )
            } else {
                if (worldIn != null && this.getBlock() is BlockTileGC) {
                    val te: TileEntity? = (this.getBlock() as BlockTileGC).createTileEntity(
                        worldIn,
                        getBlock().getStateFromMeta(stack.getItemDamage() and 12)
                    )
                    if (te is TileBaseElectricBlock) {
                        val powerDrawn: Float = te.storage.getMaxExtract()
                        if (powerDrawn > 0) {
                            info.add(
                                TextFormatting.GREEN.toString() + GCCoreUtil.translateWithFormat(
                                    "item_desc.powerdraw.name",
                                    EnergyDisplayHelper.getEnergyDisplayS(powerDrawn * 20)
                                )
                            )
                        }
                    }
                } else if (worldIn != null && this.getBlock() is BlockAdvancedTile) {
                    val te: TileEntity? = (this.getBlock() as BlockAdvancedTile).createTileEntity(
                        worldIn,
                        getBlock().getStateFromMeta(stack.getItemDamage() and 12)
                    )
                    if (te is TileBaseElectricBlock) {
                        val powerDrawn: Float = te.storage.getMaxExtract()
                        if (powerDrawn > 0) {
                            info.add(
                                TextFormatting.GREEN.toString() + GCCoreUtil.translateWithFormat(
                                    "item_desc.powerdraw.name",
                                    EnergyDisplayHelper.getEnergyDisplayS(powerDrawn * 20)
                                )
                            )
                        }
                    }
                }
                info.add(
                    GCCoreUtil.translateWithFormat(
                        "item_desc.shift.name",
                        GameSettings.getKeyDisplayString(
                            FMLClientHandler.instance().getClient().gameSettings.keyBindSneak.getKeyCode()
                        )
                    )
                )
            }
        }
    }
}
