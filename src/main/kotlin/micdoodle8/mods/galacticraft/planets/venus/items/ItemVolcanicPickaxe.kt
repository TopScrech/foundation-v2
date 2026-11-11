package micdoodle8.mods.galacticraft.planets.venus.items

import foundation.item.legacyItemValue

import foundation.level.offset

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockCommandBlock
import net.minecraft.block.BlockStructure
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemPickaxe
import net.minecraft.world.item.ItemStack
import net.minecraft.network.protocol.Packet
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.input.Keyboard

open class ItemVolcanicPickaxe constructor(assetName: String?) : ItemPickaxe(VenusItems.TOOL_VOLCANIC),
    ISortableItem, IShiftDescription {
    init {
        this.setTranslationKey(assetName)
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, info: MutableList<String?>, flagIn: ITooltipFlag) {
        if (this.showDescription(stack.legacyItemValue)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                info.addAll(
                    FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(
                        this.getShiftDescription(stack.legacyItemValue),
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

    @OnlyIn(Dist.CLIENT)
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

        if (entityLiving !is EntityPlayer || worldIn.isClientSide) {
            return ret
        }

        val player: EntityPlayer = entityLiving
        var facing: EnumFacing = entityLiving.getHorizontalFacing()

        if (entityLiving.xRot < -45.0f) {
            facing = EnumFacing.UP
        } else if (entityLiving.xRot > 45.0f) {
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
                    pos1 = pos.offset(i, 0, j)
                } else if (xAxis) {
                    pos1 = pos.offset(0, i, j)
                } else {
                    pos1 = pos.offset(i, j, 0)
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
                            worldIn.sendBlockUpdated(pos1, state1, state1, 3)
                            ++j
                            continue
                        }
                        val tileentity: TileEntity? = worldIn.getBlockEntity(pos1)
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
