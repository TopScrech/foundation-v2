package galaxyspace.core.prefab.items

import foundation.item.legacyItemValue

import foundation.block.getStateFromMeta

import galaxyspace.core.util.GSCreativeTabs
import asmodeuscore.api.item.IShiftDescription
import galaxyspace.core.prefab.items.GSItemBlockDesc
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvancedTile
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.input.Keyboard

open class GSItemBlockDesc constructor(block: Block) : ItemBlock(block) {
    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSBlocksTab
    }

    override fun onCreated(stack: ItemStack, world: World, player: EntityPlayer) {
        if (!world.isClientSide) {
            return
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, info: MutableList<String?>, flagIn: ITooltipFlag) {
        if (this.getBlock() is IShiftDescription && (this.getBlock() as IShiftDescription).showDescription(stack.legacyItemValue)) {
            if ((this.getBlock() as IShiftDescription).getDescription(stack.legacyItemValue) != null) info.addAll(
                FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(
                    (this.getBlock() as IShiftDescription).getDescription(
                        stack.legacyItemValue
                    ), 150
                )
            )

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                info.addAll(
                    FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(
                        (this.getBlock() as IShiftDescription).getShiftDescription(
                            stack.legacyItemValue
                        ), 150
                    )
                )
            } else {
                if (worldIn != null && this.getBlock() is BlockTileGC) {
                    val te: TileEntity? = (this.getBlock() as BlockTileGC).createTileEntity(
                        worldIn,
                        getBlock().getStateFromMeta(stack.legacyItemValue and 12)
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
                        getBlock().getStateFromMeta(stack.legacyItemValue and 12)
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
                    TextFormatting.DARK_AQUA.toString() + GCCoreUtil.translateWithFormat(
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
