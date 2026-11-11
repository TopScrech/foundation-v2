package galaxyspace.systems.SolarSystem.planets.overworld.items.tools

import foundation.item.legacyItemValue

import foundation.inventory.mainItems
import foundation.persistence.containsTag

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
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.util.math.RayTraceResult
import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.input.Keyboard

open class ItemGasExtractor constructor() : ItemElectricBase(foundation.item.FoundationItemProperties.named("galaxyspace", "gas_extractor").stacksTo(1)), ISortableItem, IModificationItem {
    init {
        this.setTranslationKey("gas_extractor")
        this.setHasSubtypes(true)
    }

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)

        if (!stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

        if (!stack.getTagCompound()!!.containsTag(ItemSpaceSuit.Companion.mod_count)) {
            stack.getTagCompound()!!.putInt(ItemSpaceSuit.Companion.mod_count, getModificationCount(stack))
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
                .getIntOr(ItemSpaceSuit.Companion.mod_count, 0)
        )
        tooltip.add("")
        tooltip.add(EnumColor.AQUA.toString() + GCCoreUtil.translate("gui.module.list"))
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            for (s in (this.getAvailableModules())!!) {
                if (itemStack.getTagCompound()!!.containsTag((s)!!.getName())) tooltip.addAll(
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
        val energy: Boolean = (stack)!!.hasTagCompound() && (stack)!!.getTagCompound()!!.containsTag("energy")
        return if (energy) 18000.0f else 6000.0f
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.TOOLS
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val stack: ItemStack = player.getItemInHand(hand)

        if (world.isClientSide) return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getItemInHand(hand))

        if (this.getElectricityStored(stack) < DISCHARGE_COUNT) return ActionResult<ItemStack?>(
            EnumActionResult.PASS,
            player.getItemInHand(hand)
        )

        val result: RayTraceResult? = GSUtils.getRay(world, player, true)

        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (world.getBlockState(result.getBlockPos()).getBlock() === GSFluids.BLOCK_NATURE_GAS) {
                if (tryFillCanister(player, FluidStack(GSFluids.NatureGas, 200))) {
                    player.inventoryContainer.detectAndSendChanges()
                    world.setBlockAndUpdate(result.getBlockPos(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())

                    if (!player.capabilities.isCreativeMode) this.discharge(stack, DISCHARGE_COUNT.toFloat(), true)

                    return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getItemInHand(hand))
                }
            }
        }

        return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getItemInHand(hand))
    }

    private fun tryFillCanister(player: EntityPlayer, fluid: FluidStack): Boolean {
        for (i: Int in player.inventory.mainItems.indices) {
            var stack: ItemStack = player.inventory.mainItems.get(i)

            if (stack.getItem() is ItemCanisterGeneric) {
                if (stack.getItem() === GSItems.NATURE_GAS_CANISTER && stack.legacyItemValue > 1) {
                    //                    if(stack.getItemDamage() + fluid.amount < stack.getMaxDamage())
//                        stack.setItemDamage(stack.getItemDamage() - fluid.amount);
//                    else
//                        stack.setItemDamage(1);

                    if (stack.legacyItemValue - fluid.amount > 1) stack.setItemDamage(stack.legacyItemValue - fluid.amount)
                    else stack.setItemDamage(1)

                    player.inventory.setItem(i, stack)
                    return true
                }

                if (stack.getItem() === GCItems.oilCanister && stack.legacyItemValue == ItemCanisterGeneric.EMPTY) {
                    stack = ItemStack(GSItems.NATURE_GAS_CANISTER, 1, stack.getMaxDamage() - fluid.amount)
                    player.inventory.setItem(i, stack)
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
