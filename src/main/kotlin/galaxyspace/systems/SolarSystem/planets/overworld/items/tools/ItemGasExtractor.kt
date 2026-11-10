package galaxyspace.systems.SolarSystem.planets.overworld.items.tools

import galaxyspace.api.item.IModificationItem
import galaxyspace.core.GSFluids
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.util.GSUtils
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.Energy
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.input.Keyboard

open class ItemGasExtractor constructor() : ItemElectricBase(), ISortableItem, IModificationItem {
    init {
        this.setTranslationKey("gas_extractor")
        this.setHasSubtypes(true)
    }

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)

        if (!stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

        if (!stack.getTagCompound()!!.hasKey(ItemSpaceSuit.Companion.mod_count)) {
            stack.getTagCompound()!!.setInteger(ItemSpaceSuit.Companion.mod_count, getModificationCount(stack))
        }
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
        if (!itemStack.hasTagCompound()) itemStack.setTagCompound(NBTTagCompound())

        tooltip.add(GCCoreUtil.translate("gui.gas_extractor.desc"))
        tooltip.add("")
        tooltip.add(
            EnumColor.GREY.toString() + GCCoreUtil.translate("gui.module.available_modules") + " " + itemStack.getTagCompound()!!
                .getInteger(ItemSpaceSuit.Companion.mod_count)
        )
        tooltip.add("")
        tooltip.add(EnumColor.AQUA.toString() + GCCoreUtil.translate("gui.module.list"))
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            for (s in (this.getAvailableModules())!!) {
                if (itemStack.getTagCompound()!!.hasKey((s)!!.getName())) tooltip.addAll(
                    FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(
                        this.check(
                            itemStack,
                            ((s)!!.getName())!!
                        ) + GCCoreUtil.translate("gui.module." + (s)!!.getName()), 150
                    )
                )
            }
        } else {
            tooltip.add(
                EnumColor.DARK_AQUA.toString() + GCCoreUtil.translateWithFormat(
                    "item_desc.shift.name",
                    GameSettings.getKeyDisplayString(
                        FMLClientHandler.instance().getClient().gameSettings.keyBindSneak.getKeyCode()
                    )
                )
            )
        }
        tooltip.add("")
        super.addInformation(itemStack, worldIn, tooltip, flagIn)
    }

    private fun check(stack: ItemStack, nbt: String): String {
        if (stack.hasTagCompound() && stack.getTagCompound()!!
                .getBoolean(nbt)
        ) return TextFormatting.DARK_GREEN.toString() + "- "
        return TextFormatting.DARK_RED.toString() + "- "
    }

    override fun getMaxElectricityStored(stack: ItemStack?): Float {
        val energy: Boolean = (stack)!!.hasTagCompound() && (stack)!!.getTagCompound()!!.hasKey("energy")
        return if (energy) 18000.0f else 6000.0f
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.TOOLS
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val stack: ItemStack = player.getHeldItem(hand)

        if (world.isRemote) return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))

        if (this.getElectricityStored(stack) < DISCHARGE_COUNT) return ActionResult<ItemStack?>(
            EnumActionResult.PASS,
            player.getHeldItem(hand)
        )

        val result: RayTraceResult? = GSUtils.getRay(world, player, true)

        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (world.getBlockState(result.getBlockPos()).getBlock() === GSFluids.BLOCK_NATURE_GAS) {
                if (tryFillCanister(player, FluidStack(GSFluids.NatureGas, 200))) {
                    player.inventoryContainer.detectAndSendChanges()
                    world.setBlockToAir(result.getBlockPos())

                    if (!player.capabilities.isCreativeMode) this.discharge(stack, DISCHARGE_COUNT.toFloat(), true)

                    return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
                }
            }
        }

        return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))
    }

    private fun tryFillCanister(player: EntityPlayer, fluid: FluidStack): Boolean {
        for (i: Int in player.inventory.mainInventory.indices) {
            var stack: ItemStack = player.inventory.mainInventory.get(i)

            if (stack.getItem() is ItemCanisterGeneric) {
                if (stack.getItem() === GSItems.NATURE_GAS_CANISTER && stack.getItemDamage() > 1) {
                    //                    if(stack.getItemDamage() + fluid.amount < stack.getMaxDamage())
//                        stack.setItemDamage(stack.getItemDamage() - fluid.amount);
//                    else
//                        stack.setItemDamage(1);

                    if (stack.getItemDamage() - fluid.amount > 1) stack.setItemDamage(stack.getItemDamage() - fluid.amount)
                    else stack.setItemDamage(1)

                    player.inventory.setInventorySlotContents(i, stack)
                    return true
                }

                if (stack.getItem() === GCItems.oilCanister && stack.getItemDamage() == ItemCanisterGeneric.EMPTY) {
                    stack = ItemStack(GSItems.NATURE_GAS_CANISTER, 1, stack.getMaxDamage() - fluid.amount)
                    player.inventory.setInventorySlotContents(i, stack)
                    return true
                }
            }
        }

        return false
    }

    override fun getType(stack: ItemStack?): Module_Type {
        return Module_Type.TOOLS
    }

    override fun getAvailableModules(): Array<ItemModule?>? {
        return ((arrayOf<ItemModule>(Energy()) as Array<ItemModule?>?))!!
    }

    override fun getModificationCount(stack: ItemStack?): Int {
        return 1
    }

    companion object {
        private const val DISCHARGE_COUNT = 100
    }
}
