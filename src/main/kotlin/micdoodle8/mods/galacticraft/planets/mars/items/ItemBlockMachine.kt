package micdoodle8.mods.galacticraft.planets.mars.items

import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMarsT2
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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
        val metaAt: Int = itemStack.getItemDamage()

        //If it is a Cryogenic Chamber, check the space
        if (metaAt == BlockMachineMars.CRYOGENIC_CHAMBER_METADATA) {
            for (y: Int in 0..2) {
                val stateAt: IBlockState = world.getBlockState(pos.add(0, y, 0))

                if (this.getBlock() === MarsBlocks.machine) {
                    if (!stateAt.getMaterial().isReplaceable()) {
                        if (world.isRemote) {
                            FMLClientHandler.instance().getClient().ingameGUI.setOverlayMessage(
                                TextComponentString(
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
        val typenum: Int = itemstack.getItemDamage() and 12

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

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
