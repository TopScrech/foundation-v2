package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.item.FoundationItemComponents
import foundation.item.FoundationItemVariants
import foundation.item.legacyItemValue
import foundation.persistence.FoundationItemStackPersistence
import io.netty.buffer.Unpooled
import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import net.minecraft.client.renderer.item.CuboidItemModelWrapper
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RepairItemRecipe
import net.minecraftforge.common.capabilities.ForgeCapabilities
import java.nio.file.Files
import java.nio.file.Path

fun verifyElectricItems(resources: Path, lookup: HolderLookup.Provider) {
    fun item(id: String) = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse(id))) as ItemElectricBase
    fun roundTrip(stack: ItemStack): ItemStack {
        val saved = FoundationItemStackPersistence.read(FoundationItemStackPersistence.write(stack, lookup), lookup)
        check(ItemStack.matches(saved, stack))
        val bytes = RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY))
        try {
            ItemStack.STREAM_CODEC.encode(bytes, stack)
            check(ItemStack.matches(ItemStack.STREAM_CODEC.decode(bytes), stack) && !bytes.isReadable)
        } finally { bytes.release() }
        return saved
    }
    val profiles = listOf("galacticraftcore:battery" to 15000f, "galaxyspace:advanced_battery" to 60000f,
        "galaxyspace:modern_battery" to 120000f, "galaxyspace:extra_battery" to 180000f, "galaxyspace:ultimate_battery" to 300000f)
    for ((id, capacity) in profiles) {
        val battery = item(id)
        val stack = ItemStack(battery)
        check(battery.getElectricityStored(stack) == capacity && battery.getMaxElectricityStored(stack) == capacity)
        check(stack.legacyItemValue == 0 && !stack.isDamageableItem && !stack.has(DataComponents.DAMAGE) && !stack.has(DataComponents.MAX_DAMAGE))
        check(battery.getTierGC(stack) == 1 && battery.getMaxTransferGC(stack) == 200f && stack.maxStackSize == 1)
        val energyView = stack.getCapability(ForgeCapabilities.ENERGY).orElseThrow { IllegalStateException() }
        check(energyView.maxEnergyStored == (capacity * EnergyConfigHandler.TO_RF_RATIO).toInt())
        check(energyView.energyStored == energyView.maxEnergyStored && energyView.extractEnergy(1000, true) == 125)
        battery.setElectricity(stack, 0f)
        check(stack.legacyItemValue == 100 && battery.getBarWidth(stack) == 0 && battery.isBarVisible(stack))
        check(stack.maxStackSize == if (id == "galacticraftcore:battery") 4 else 1)
        val before = stack.copy()
        check(battery.recharge(stack, 500f, false) == 200f && ItemStack.matches(before, stack))
        check(battery.recharge(stack, 500f, true) == 200f && battery.getElectricityStored(stack) == 200f && stack.maxStackSize == 1)
        check(battery.discharge(stack, 500f, false) == 200f && battery.getElectricityStored(stack) == 200f)
        check(battery.discharge(stack, 500f, true) == 200f && battery.getElectricityStored(stack) == 0f && stack.maxStackSize == 1)
        battery.setElectricity(stack, capacity - 50f)
        check(battery.recharge(stack, 100f, true) == 50f && battery.getElectricityStored(stack) == capacity)
        check(!battery.isBarVisible(stack) && battery.getBarWidth(stack) == 13)
        for (request in listOf(-1f, Float.NaN)) {
            val unchanged = stack.copy()
            check(battery.recharge(stack, request, true) == 0f && battery.discharge(stack, request, true) == 0f && ItemStack.matches(stack, unchanged))
        }
        roundTrip(stack)
        check(RepairItemRecipe.INSTANCE.assemble(CraftingInput.of(2, 1, listOf(stack.copy(), stack.copy()))).isEmpty)
        battery.setElectricity(stack, Float.POSITIVE_INFINITY)
        check(battery.getElectricityStored(stack) == capacity)
        battery.setElectricity(stack, -1f)
        check(battery.getElectricityStored(stack) == 0f)
        battery.setElectricity(stack, Float.NaN)
        check(battery.getElectricityStored(stack) == 0f)
        for (damage in listOf(0, 50, 100)) {
            val old = CompoundTag().also { it.putString("id", id); it.putByte("Count", 1); it.putShort("Damage", damage.toShort()) }
            val copy = old.copy()
            val imported = FoundationItemStackPersistence.read(old, lookup)
            check(old == copy && battery.getElectricityStored(imported) == capacity * (100 - damage) / 100 && imported.legacyItemValue == damage)
            check(!imported.has(DataComponents.DAMAGE) && !imported.has(DataComponents.MAX_DAMAGE))
            roundTrip(imported)
        }
        for (double in listOf(false, true)) {
            val old = CompoundTag().also { outer ->
                outer.putString("id", id); outer.putByte("Count", 1); outer.putShort("Damage", 100)
                outer.put("tag", CompoundTag().also { if (double) it.putDouble("electricity", 1234.5) else it.putFloat("electricity", 1234.5f); it.putString("owner", "Retained owner") })
            }
            val imported = FoundationItemStackPersistence.read(old, lookup)
            check(battery.getElectricityStored(imported) == 1234.5f && imported.maxStackSize == 1)
            check(imported.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getStringOr("owner", "") == "Retained owner")
            check(imported.get(DataComponents.CUSTOM_DATA)?.copyTag()?.contains("electricity") != true)
            roundTrip(imported)
        }
        val name = Identifier.parse(id)
        val json = JsonParser.parseString(Files.readString(resources.resolve("assets/${name.namespace}/items/${name.path}.json")))
        val model = ItemModels.CODEC.parse(JsonOps.INSTANCE, json.asJsonObject["model"]).getOrThrow() as CuboidItemModelWrapper.Unbaked
        check(Files.exists(resources.resolve("assets/${name.namespace}/models/${model.model().path}.json")))
        val creative = battery.creativeStacks()
        check(creative.size == 2 && creative.map { battery.getElectricityStored(it) } == if (id.startsWith("galaxyspace:")) listOf(capacity, 0f) else listOf(0f, capacity))
    }
    val core = item("galacticraftcore:battery")
    val clean = requireNotNull(ElectricItemHelper.getUncharged(core)).also { it.count = 4 }
    check(clean.maxStackSize == 4 && core.recharge(clean, 100f, true) == 0f)
    check(ItemStack.validateStrict(roundTrip(clean)).isSuccess)
    val oldFour = CompoundTag().also { it.putString("id", "galacticraftcore:battery"); it.putByte("Count", 4); it.putShort("Damage", 100) }
    val importedFour = FoundationItemStackPersistence.read(oldFour, lookup)
    check(importedFour.count == 4 && importedFour.maxStackSize == 4 && importedFour.legacyItemValue == 100)
    roundTrip(importedFour)
    val low = requireNotNull(ElectricItemHelper.getWithCharge(core, 6000f))
    val close = requireNotNull(ElectricItemHelper.getWithCharge(core, 6049f))
    val next = requireNotNull(ElectricItemHelper.getWithCharge(core, 6150f))
    check(FoundationItemVariants.sameVariant(low, close) && !FoundationItemVariants.sameVariant(low, next))
    check(core.getBarWidth(low) == 5 && low.legacyItemValue == 60)
    val copy = low.copy()
    core.setElectricity(copy, 0f)
    check(core.getElectricityStored(low) == 6000f && core.getElectricityStored(copy) == 0f)
    for ((id, tier, amount) in listOf(Triple("galacticraftcore:infinite_battery", 3, 500f), Triple("galacticraftplanets:atomic_battery", 2, 7f))) {
        val battery = item(id)
        val stack = ItemStack(battery)
        check(battery.getElectricityStored(stack).isInfinite() && battery.getMaxElectricityStored(stack).isInfinite())
        check(battery.getTierGC(stack) == tier && battery.recharge(stack, 100f, true) == 0f && battery.discharge(stack, 500f, true) == amount)
        battery.setElectricity(stack, 0f)
        check(battery.getElectricityStored(stack).isInfinite() && stack.legacyItemValue == 0 && !battery.isBarVisible(stack))
        check(battery.isFoil(stack) == (tier == 3) && battery.creativeStacks().size == 1)
        check(!stack.has(FoundationItemComponents.ENERGY_JOULES))
        val generated = stack.getCapability(ForgeCapabilities.ENERGY).orElseThrow { IllegalStateException() }
        check(!generated.canReceive() && generated.canExtract() && generated.energyStored == Int.MAX_VALUE)
        check(generated.extractEnergy(1000, true) == if (tier == 3) 1000 else 4)
        check(generated.extractEnergy(1000, false) == if (tier == 3) 1000 else 4)
        roundTrip(stack)
        val key = Identifier.parse(id)
        val json = JsonParser.parseString(Files.readString(resources.resolve("assets/${key.namespace}/items/${key.path}.json")))
        ItemModels.CODEC.parse(JsonOps.INSTANCE, json.asJsonObject["model"]).getOrThrow()
    }
    val originalInput = EnergyConfigHandler.disableFEInput
    val originalOutput = EnergyConfigHandler.disableFEOutput
    try {
        EnergyConfigHandler.disableFEInput = false; EnergyConfigHandler.disableFEOutput = false
        val stack = requireNotNull(ElectricItemHelper.getUncharged(core))
        val capability = stack.getCapability(ForgeCapabilities.ENERGY).orElseThrow { IllegalStateException("Missing native item energy capability") }
        check(System.getProperty("foundation.checks.capabilityTransformations").toInt() > 1)
        check(capability === stack.getCapability(ForgeCapabilities.ENERGY, Direction.NORTH).orElseThrow { IllegalStateException() })
        check(capability.canReceive() && capability.canExtract())
        val unchanged = stack.copy()
        check(capability.receiveEnergy(1000, true) == 125 && ItemStack.matches(stack, unchanged))
        check(capability.receiveEnergy(1000, false) == 125 && core.getElectricityStored(stack) == 200f && capability.energyStored == 125)
        check(capability.extractEnergy(1000, true) == 125 && core.getElectricityStored(stack) == 200f)
        check(capability.extractEnergy(1000, false) == 125 && core.getElectricityStored(stack) == 0f)
        EnergyConfigHandler.disableFEInput = true; EnergyConfigHandler.disableFEOutput = true
        check(!capability.canReceive() && !capability.canExtract() && capability.receiveEnergy(10, false) == 0 && capability.extractEnergy(10, false) == 0)
        EnergyConfigHandler.disableFEInput = false; EnergyConfigHandler.disableFEOutput = false
        val copyStack = stack.copy()
        val copied = copyStack.getCapability(ForgeCapabilities.ENERGY).orElseThrow { IllegalStateException() }
        check(copied !== capability && copied.receiveEnergy(100, false) == 100 && core.getElectricityStored(stack) == 0f)
        stack.shrink(1)
        check(!capability.canReceive() && !capability.canExtract() && capability.energyStored == 0)
        val restored = roundTrip(copyStack)
        check(restored.getCapability(ForgeCapabilities.ENERGY).orElseThrow { IllegalStateException() }.energyStored == copied.energyStored)
        check(!restored.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent)
        val lazy = restored.getCapability(ForgeCapabilities.ENERGY)
        check(lazy.isPresent)
        restored.invalidateCaps()
        check(!lazy.isPresent && !restored.getCapability(ForgeCapabilities.ENERGY).isPresent)
    } finally { EnergyConfigHandler.disableFEInput = originalInput; EnergyConfigHandler.disableFEOutput = originalOutput }
    check(EnergyDisplayHelper.getEnergyDisplayS(12345.9f) == "12,345 gJ")
    println("Passed seven native battery profiles, bounded and simulated charge transfers, legacy charge imports, empty stack history, unrepairable charge components, quantized recipe matching and bars, saves, packets, models, creative output, infinite generation, and native Forge Energy item capabilities")
}
