package micdoodle8.mods.galacticraft.core.energy.item

import foundation.item.FoundationItemComponents
import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper
import micdoodle8.mods.galacticraft.api.item.IItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.util.function.Consumer
import net.minecraft.core.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.IEnergyStorage
import kotlin.math.min

abstract class ItemElectricBase(properties: Item.Properties) : Item(properties), IItemElectricBase {
    @JvmField var transferMax = 0f
    protected open val emptyStackLimit: Int = 1
    protected open val nameColor: ChatFormatting? = null

    init { setMaxTransfer() }
    protected open fun setMaxTransfer() { transferMax = 200f }
    override fun getMaxTransferGC(itemStack: ItemStack?): Float = transferMax
    override fun getTierGC(itemStack: ItemStack?): Int = 1

    override fun getName(stack: ItemStack): Component {
        val name = super.getName(stack)
        return nameColor?.let { name.copy().withStyle(it) } ?: name
    }

    override fun onCraftedPostProcess(itemStack: ItemStack, level: Level) {
        super.onCraftedPostProcess(itemStack, level)
        setElectricity(itemStack, 0f)
    }

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, display: TooltipDisplay, builder: Consumer<Component>, flag: TooltipFlag) {
        val energy = getElectricityStored(stack)
        val capacity = getMaxElectricityStored(stack)
        val color = if (energy <= capacity / 3) ChatFormatting.DARK_RED else if (energy > capacity * 2 / 3) ChatFormatting.DARK_GREEN else ChatFormatting.GOLD
        builder.accept(Component.literal(EnergyDisplayHelper.getEnergyDisplayS(energy) + "/" + EnergyDisplayHelper.getEnergyDisplayS(capacity)).withStyle(color))
    }

    override fun getElectricityStored(itemStack: ItemStack?): Float {
        if (itemStack == null || itemStack.isEmpty) return 0f
        val capacity = getMaxElectricityStored(itemStack)
        itemStack.get(FoundationItemComponents.ENERGY_JOULES)?.let { return if (it.isNaN()) 0f else it.coerceIn(0f, capacity) }
        val legacy = itemStack.get(DataComponents.CUSTOM_DATA)?.copyTag()?.get("electricity")
        return when (legacy) {
            is FloatTag -> legacy.floatValue().let { if (it.isNaN()) 0f else it.coerceIn(0f, capacity) }
            is DoubleTag -> legacy.floatValue().let { if (it.isNaN()) 0f else it.coerceIn(0f, capacity) }
            null -> capacity
            else -> 0f
        }
    }

    override fun setElectricity(itemStack: ItemStack?, joules: Float) {
        if (itemStack == null || itemStack.isEmpty) return
        val capacity = getMaxElectricityStored(itemStack)
        require(capacity.isFinite() && capacity > 0f)
        val energy = if (joules.isNaN()) 0f else joules.coerceIn(0f, capacity)
        require(energy == 0f || itemStack.count == 1) { "Charged items must occupy individual stacks" }
        val hadLegacyCharge = itemStack.get(DataComponents.CUSTOM_DATA)?.copyTag()?.contains("electricity") == true
        val cleanEmpty = energy == 0f && !hadLegacyCharge && (!itemStack.has(FoundationItemComponents.ENERGY_JOULES) || itemStack.maxStackSize > 1)
        itemStack.set(FoundationItemComponents.ENERGY_JOULES, energy)
        itemStack.set(DataComponents.MAX_STACK_SIZE, if (cleanEmpty) emptyStackLimit else 1)
        itemStack.remove(DataComponents.DAMAGE)
        itemStack.remove(DataComponents.MAX_DAMAGE)
        val data = itemStack.get(DataComponents.CUSTOM_DATA)
        if (data != null && data.copyTag().contains("electricity")) net.minecraft.world.item.component.CustomData.update(DataComponents.CUSTOM_DATA, itemStack) { it.remove("electricity") }
    }

    /** Preserve whether an empty legacy battery had ever acquired a stored electricity tag */
    fun importLegacy(itemStack: ItemStack, damage: Int, data: net.minecraft.nbt.CompoundTag?) {
        require(damage in 0..100) { "Invalid legacy battery display value $damage" }
        val tag = data?.get("electricity")
        val energy = when (tag) {
            is FloatTag -> tag.floatValue()
            is DoubleTag -> tag.floatValue()
            null -> getMaxElectricityStored(itemStack) * (100 - damage) / 100
            else -> 0f
        }
        setElectricity(itemStack, energy)
        if (tag != null) itemStack.set(DataComponents.MAX_STACK_SIZE, 1)
    }

    override fun recharge(itemStack: ItemStack?, energy: Float, doReceive: Boolean): Float {
        if (itemStack == null || itemStack.isEmpty || itemStack.count != 1 || energy.isNaN() || energy <= 0f) return 0f
        val accepted = min(min(energy, transferMax), getMaxElectricityStored(itemStack) - getElectricityStored(itemStack)).coerceAtLeast(0f)
        if (doReceive && accepted > 0f) setElectricity(itemStack, getElectricityStored(itemStack) + accepted)
        return accepted
    }

    override fun discharge(itemStack: ItemStack?, energy: Float, doTransfer: Boolean): Float {
        if (itemStack == null || itemStack.isEmpty || itemStack.count != 1 || energy.isNaN() || energy <= 0f) return 0f
        val supplied = min(min(energy, transferMax), getElectricityStored(itemStack)).coerceAtLeast(0f)
        if (doTransfer && supplied > 0f) setElectricity(itemStack, getElectricityStored(itemStack) - supplied)
        return supplied
    }

    override fun getTransfer(itemStack: ItemStack?): Float = min(transferMax, getMaxElectricityStored(itemStack) - getElectricityStored(itemStack)).coerceAtLeast(0f)
    fun legacyDisplayValue(stack: ItemStack): Int {
        val capacity = getMaxElectricityStored(stack)
        return if (capacity.isInfinite()) 0 else 100 - (getElectricityStored(stack) / capacity * 100).toInt()
    }
    override fun isBarVisible(stack: ItemStack): Boolean = getElectricityStored(stack) < getMaxElectricityStored(stack)
    override fun getBarWidth(stack: ItemStack): Int = Math.round(13.0 - legacyDisplayValue(stack) / 100.0 * 13.0).toInt().coerceIn(0, 13)
    override fun getBarColor(stack: ItemStack): Int = Mth.hsvToRgb((1f - legacyDisplayValue(stack) / 100f) / 3f, 1f, 1f)
    open fun creativeStacks(): List<ItemStack> = listOf(requireNotNull(ElectricItemHelper.getUncharged(this)), requireNotNull(ElectricItemHelper.getWithCharge(this, getMaxElectricityStored(null))))

    fun receiveEnergy(container: ItemStack, maxReceive: Int, simulate: Boolean): Int = (recharge(container, maxReceive.coerceAtLeast(0) * EnergyConfigHandler.RF_RATIO, !simulate) / EnergyConfigHandler.RF_RATIO).toInt()
    fun extractEnergy(container: ItemStack, maxExtract: Int, simulate: Boolean): Int = (discharge(container, maxExtract.coerceAtLeast(0) / EnergyConfigHandler.TO_RF_RATIO, !simulate) * EnergyConfigHandler.TO_RF_RATIO).toInt()
    fun getEnergyStored(container: ItemStack): Int = (getElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO).toInt()
    fun getMaxEnergyStored(container: ItemStack?): Int = (getMaxElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO).toInt()
    fun canSend(itemStack: ItemStack?): Boolean = true

    private class EnergyProvider(private val item: ItemElectricBase, private val stack: ItemStack) : ICapabilityProvider {
        private val energy = LazyOptional.of<IEnergyStorage> {
            object : IEnergyStorage {
                override fun receiveEnergy(amount: Int, simulate: Boolean): Int = if (canReceive()) item.receiveEnergy(stack, amount, simulate) else 0
                override fun extractEnergy(amount: Int, simulate: Boolean): Int = if (canExtract()) item.extractEnergy(stack, amount, simulate) else 0
                private fun present(): Boolean = !stack.isEmpty && stack.count == 1 && stack.item === item
                override fun getEnergyStored(): Int = if (present()) item.getEnergyStored(stack) else 0
                override fun getMaxEnergyStored(): Int = if (present()) item.getMaxEnergyStored(stack) else 0
                override fun canExtract(): Boolean = present() && !EnergyConfigHandler.disableFEOutput && item.canSend(stack)
                override fun canReceive(): Boolean = present() && !EnergyConfigHandler.disableFEInput && item.getTransfer(stack) > 0f
            }
        }
        override fun <T : Any> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> =
            if (cap === ForgeCapabilities.ENERGY) energy.cast() else LazyOptional.empty()
        fun invalidate() = energy.invalidate()
    }

    companion object {
        private var installed = false
        @JvmStatic @Synchronized fun install() {
            if (installed) return
            installed = true
            net.minecraftforge.event.AttachCapabilitiesEvent.ItemStacks.BUS.addListener { event ->
                val stack = event.getObject()
                val item = stack.item as? ItemElectricBase
                if (item != null) {
                    val provider = EnergyProvider(item, stack)
                    event.addCapability(net.minecraft.resources.Identifier.parse("foundation:electric_item"), provider)
                    event.addListener(provider::invalidate)
                }
            }
        }

        @JvmStatic fun isElectricItem(item: Item?): Boolean = item is IItemElectricBase
        @JvmStatic fun isElectricItemEmpty(stack: ItemStack): Boolean = !stack.isEmpty && (stack.item as? IItemElectricBase)?.getElectricityStored(stack)?.let { it <= 0f } == true
        @JvmStatic fun isElectricItemCharged(stack: ItemStack?): Boolean = stack != null && !stack.isEmpty && (stack.item as? IItemElectricBase)?.getElectricityStored(stack)?.let { it > 0f } == true
    }
}
