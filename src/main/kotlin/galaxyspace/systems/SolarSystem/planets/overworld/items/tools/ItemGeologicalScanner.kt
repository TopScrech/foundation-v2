package galaxyspace.systems.SolarSystem.planets.overworld.items.tools

import java.util.HashMap

import galaxyspace.core.GSFluids
import galaxyspace.core.events.GSClientTickHandler
import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidRegistry

open class ItemGeologicalScanner constructor() : ItemElectricBase(), ISortableItem {
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
        val stack: ItemStack = player.getHeldItem(hand)

        if (!world.isRemote && this.getElectricityStored(stack) >= 100.0f) {
            if (player.isSneaking()) {
                if (!stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

                if (stack.getTagCompound()!!.hasKey("mode")) stack.getTagCompound()!!
                    .setInteger("mode", if (stack.getTagCompound()!!.getInteger("mode") > 0) 0 else 1)
                else stack.getTagCompound()!!.setInteger("mode", 1)


                player.sendMessage(
                    TextComponentString(
                        TextFormatting.GREEN.toString() + "Mode: " + stack.getTagCompound()!!.getInteger("mode")
                    )
                )
                return EnumActionResult.SUCCESS
            }
            if (stack.getTagCompound()!!.getInteger("mode") == 0) {
                val blocks: MutableMap<IBlockState?,Int> = HashMap<IBlockState?,Int>()


                for (scan: BlockPos in BlockPos.getAllInBox(pos.add(-2, -2, -2), pos.add(2, 2, 2))) {
                    val block: IBlockState = world.getBlockState(scan)
                    if (world.isAirBlock(scan)) continue

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
            } else if (stack.getTagCompound()!!.getInteger("mode") == 1) {
                val radius: Int = 8
                var found: Boolean = false

                for (y: Int in player.posY.toInt() downTo 6) {
                    if (found) break

                    for (x: Int in -radius..<radius) {
                        if (found) break
                        for (z: Int in -radius..<radius) {
                            val block: IBlockState = world.getBlockState(BlockPos(pos.getX() + x, y, pos.getZ() + z))
                            if (block.getBlock() === FluidRegistry.getFluidStack("oil", 1)!!.getFluid()
                                    .getBlock() || block.getBlock() === FluidRegistry.LAVA.getBlock() || block.getBlock() === GSFluids.BLOCK_NATURE_GAS
                            ) {
                                //player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Y Oil: " + y));
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
