package micdoodle8.mods.galacticraft.planets.venus.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.block.Block
import net.minecraft.block.BlockCommandBlock
import net.minecraft.block.BlockStructure
import net.minecraft.block.state.IBlockState
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard

open class ItemVolcanicPickaxe constructor(assetName: String?) : ItemPickaxe(VenusItems.TOOL_VOLCANIC),
    ISortableItem, IShiftDescription {
    init {
        this.setTranslationKey(assetName)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, info: MutableList<String?>, flagIn: ITooltipFlag) {
        if (this.showDescription(stack.getItemDamage())) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                info.addAll(
                    FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(
                        this.getShiftDescription(stack.getItemDamage()),
                        150
                    )
                )
            } else {
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

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.TOOLS
    }

    override fun onBlockDestroyed(
        stack: ItemStack,
        worldIn: World,
        state: IBlockState,
        pos: BlockPos,
        entityLiving: EntityLivingBase
    ): Boolean {
        val ret: Boolean = super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving)

        if (entityLiving !is EntityPlayer || worldIn.isRemote) {
            return ret
        }

        val player: EntityPlayer = entityLiving
        var facing: EnumFacing = entityLiving.getHorizontalFacing()

        if (entityLiving.rotationPitch < -45.0f) {
            facing = EnumFacing.UP
        } else if (entityLiving.rotationPitch > 45.0f) {
            facing = EnumFacing.DOWN
        }

        val yAxis: Boolean = facing.getAxis() == EnumFacing.Axis.Y
        val xAxis: Boolean = facing.getAxis() == EnumFacing.Axis.X

        for (i: Int in -1..1) {
            var j: Int = -1
            while (j <= 1 && !stack.isEmpty()) {
                if (i == 0 && j == 0) {
                    ++j
                    continue
                }

                val pos1: BlockPos
                if (yAxis) {
                    pos1 = pos.add(i, 0, j)
                } else if (xAxis) {
                    pos1 = pos.add(0, i, j)
                } else {
                    pos1 = pos.add(i, j, 0)
                }

                //:Replicate logic of PlayerInteractionManager.tryHarvestBlock(pos1)
                val state1: IBlockState = worldIn.getBlockState(pos1)
                val f: Float = state1.getBlockHardness(worldIn, pos1)
                if (f >= 0f) {
                    val event: BlockEvent.BreakEvent = BlockEvent.BreakEvent(worldIn, pos1, state1, player)
                    MinecraftForge.EVENT_BUS.post(event)
                    if (!event.isCanceled()) {
                        val block: Block = state1.getBlock()
                        if ((block is BlockCommandBlock || block is BlockStructure) && !player.canUseCommandBlock()) {
                            worldIn.notifyBlockUpdate(pos1, state1, state1, 3)
                            ++j
                            continue
                        }
                        val tileentity: TileEntity? = worldIn.getTileEntity(pos1)
                        if (tileentity != null) {
                            val pkt: Packet<*>? = tileentity.getUpdatePacket()
                            if (pkt != null) {
                                (player as EntityPlayerMP).connection.sendPacket(pkt)
                            }
                        }

                        val canHarvest: Boolean = block.canHarvestBlock(worldIn, pos1, player)
                        val destroyed: Boolean = block.removedByPlayer(state1, worldIn, pos1, player, canHarvest)
                        if (destroyed) {
                            block.onPlayerDestroy(worldIn, pos1, state1)
                        }
                        if (canHarvest && destroyed) {
                            block.harvestBlock(worldIn, player, pos1, state1, tileentity, stack)
                            stack.damageItem(1, player)
                        }
                    }
                }
                ++j
            }
        }

        return ret
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate("item.volcanic_pickaxe.description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }
}
