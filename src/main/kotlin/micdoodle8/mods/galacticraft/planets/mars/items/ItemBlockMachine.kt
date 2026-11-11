package micdoodle8.mods.galacticraft.planets.mars.items

import net.minecraft.network.chat.Component

import foundation.item.legacyItemValue

import foundation.level.offset

import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMarsT2
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Style

import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockMachine constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

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
        val metaAt: Int = itemStack.legacyItemValue

        //If it is a Cryogenic Chamber, check the space
        if (metaAt == BlockMachineMars.CRYOGENIC_CHAMBER_METADATA) {
            for (y: Int in 0..2) {
                val stateAt: IBlockState = world.getBlockState(pos.offset(0, y, 0))

                if (this.getBlock() === MarsBlocks.machine) {
                    if (!stateAt.getMaterial().isReplaceable()) {
                        if (world.isClientSide) {
                            FMLClientHandler.instance().getClient().ingameGUI.setOverlayMessage(
                                Component.literal(
                                    GCCoreUtil.translate("gui.warning.noroom")
                                ).setStyle(Style().setColor(TextFormatting.RED)).getFormattedText(), false
                            )
                        }
                        return false
                    }
                }
            }
        }
        return super.placeBlockAt(itemStack, player, world, pos, facing, hitX, hitY, hitZ, state)
    }

    public override fun getTranslationKey(itemstack: ItemStack): String {
        var index: Int = 0
        val typenum: Int = itemstack.legacyItemValue and 12

        if (this.getBlock() === MarsBlocks.machine) {
            if (typenum == BlockMachineMars.LAUNCH_CONTROLLER_METADATA) {
                index = 2
            } else if (typenum == BlockMachineMars.CRYOGENIC_CHAMBER_METADATA) {
                index = 1
            }
        } else if (this.getBlock() === MarsBlocks.machineT2) {
            if (typenum == BlockMachineMarsT2.GAS_LIQUEFIER_META) {
                return "tile.mars_machine.4"
            } else if (typenum == BlockMachineMarsT2.METHANE_SYNTHESIZER_META) {
                return "tile.mars_machine.5"
            } else if (typenum == BlockMachineMarsT2.ELECTROLYZER_META) {
                return "tile.mars_machine.6"
            }
        }

        return this.getBlock().getTranslationKey() + "." + index
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
