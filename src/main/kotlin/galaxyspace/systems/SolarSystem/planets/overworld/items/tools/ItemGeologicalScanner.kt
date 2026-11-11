package galaxyspace.systems.SolarSystem.planets.overworld.items.tools

import net.minecraft.network.chat.Component

import foundation.block.getMetaFromState

import foundation.level.offset

import foundation.persistence.containsTag

import java.util.HashMap

import galaxyspace.core.GSFluids
import galaxyspace.core.events.GSClientTickHandler
import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos

import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.FluidRegistry

open class ItemGeologicalScanner constructor() : ItemElectricBase(foundation.item.FoundationItemProperties.named("galaxyspace", "geo_scanner").stacksTo(1)), ISortableItem {
    private val mode = 0

    init {
        this.setTranslationKey("geo_scanner")
        this.setHasSubtypes(true)
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSArmorTab || tab === CreativeTabs.SEARCH) list.add(
            ItemStack(
                this,
                1,
                this.getMaxDamage()
            )
        )
    }

    override fun addInformation(
        itemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        tooltip.add(GCCoreUtil.translate("gui.geological_scanner.desc"))

        var color: String = ""
        val joules: Float = this.getElectricityStored(itemStack)

        if (joules <= this.getMaxElectricityStored(itemStack) / 3) {
            color = "\u00a74"
        } else if (joules > this.getMaxElectricityStored(itemStack) * 2 / 3) {
            color = "\u00a72"
        } else {
            color = "\u00a76"
        }

        tooltip.add(
            color + EnergyDisplayHelper.getEnergyDisplayS(joules) + "/" + EnergyDisplayHelper.getEnergyDisplayS(
                this.getMaxElectricityStored(itemStack)
            )
        )
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.TOOLS
    }

    override fun onItemUse(
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): EnumActionResult {
        val stack: ItemStack = player.getItemInHand(hand)

        if (!world.isClientSide && this.getElectricityStored(stack) >= 100.0f) {
            if (player.isShiftKeyDown()) {
                if (!stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

                if (stack.getTagCompound()!!.containsTag("mode")) stack.getTagCompound()!!
                    .putInt("mode", if (stack.getTagCompound()!!.getIntOr("mode", 0) > 0) 0 else 1)
                else stack.getTagCompound()!!.putInt("mode", 1)


                player.sendSystemMessage(
                    Component.literal(
                        TextFormatting.GREEN.toString() + "Mode: " + stack.getTagCompound()!!.getIntOr("mode", 0)
                    )
                )
                return EnumActionResult.SUCCESS
            }
            if (stack.getTagCompound()!!.getIntOr("mode", 0) == 0) {
                val blocks: MutableMap<IBlockState?,Int> = HashMap<IBlockState?,Int>()


                for (scan: BlockPos in BlockPos.getAllInBox(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
                    val block: IBlockState = world.getBlockState(scan)
                    if (world.isEmptyBlock(scan)) continue

                    if (blocks.containsKey(block)) {
                        blocks.put(block, blocks.get(block)!! + 1)
                        continue
                    }

                    blocks.put(block, 1)
                }

                val blocks_to_render: MutableMap<IBlockState?, String?> = HashMap<IBlockState?, String?>()
                for (block: MutableMap.MutableEntry<IBlockState?,Int> in blocks.entries) {
                    val item: ItemStack = ItemStack(
                        getItemFromBlock(block.key!!.getBlock()),
                        1,
                        block.key!!.getBlock().getMetaFromState(block.key)
                    )

                    blocks_to_render.put(block.key, item.getDisplayName() + " x" + block.value)
                }
                GSClientTickHandler.blocks = blocks_to_render
                GSClientTickHandler.ticks = 50


                //GalaxySpace.packetPipeline.sendToServer(new GSPacketSimple(GSEnumSimplePacket.C_SHOW_SCANNER_BLOCK, player.world, new Object[] {0}));
                if (!player.capabilities.isCreativeMode) this.discharge(stack, (20 * blocks.size).toFloat(), true)
            } else if (stack.getTagCompound()!!.getIntOr("mode", 0) == 1) {
                val radius: Int = 8
                var found: Boolean = false

                for (y: Int in player.y.toInt() downTo 6) {
                    if (found) break

                    for (x: Int in -radius..<radius) {
                        if (found) break
                        for (z: Int in -radius..<radius) {
                            val block: IBlockState = world.getBlockState(BlockPos(pos.getX() + x, y, pos.getZ() + z))
                            if (block.getBlock() === FluidRegistry.getFluidStack("oil", 1)!!.getFluid()
                                    .getBlock() || block.getBlock() === FluidRegistry.LAVA.getBlock() || block.getBlock() === GSFluids.BLOCK_NATURE_GAS
                            ) {
                                //player.sendMessage(new Component.literal(TextFormatting.GREEN + "Y Oil: " + y));
                                val blocks_to_render: MutableMap<IBlockState?, String?> =
                                    HashMap<IBlockState?, String?>()
                                blocks_to_render.put(block, block.getBlock().getLocalizedName() + " Y Level: " + y)
                                GSClientTickHandler.blocks = blocks_to_render
                                GSClientTickHandler.ticks = 100

                                found = true
                                break
                            }
                        }
                    }
                }

                if (!player.capabilities.isCreativeMode) this.discharge(stack, 20f, true)
            }
        }
        return EnumActionResult.SUCCESS
    }

    override fun getMaxElectricityStored(theItem: ItemStack?): Float {
        return 60000f
    }
}
