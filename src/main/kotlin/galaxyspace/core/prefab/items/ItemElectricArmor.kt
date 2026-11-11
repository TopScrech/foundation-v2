package galaxyspace.core.prefab.items

import foundation.persistence.containsTag

import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.items.ItemElectricArmor
import cofh.redstoneflux.api.IEnergyContainerItem
import micdoodle8.mods.galacticraft.api.item.IItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.Tag as NBTBase
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.NBTTagDouble
import net.minecraft.nbt.NBTTagFloat
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.fml.common.Optional
import kotlin.math.max
import kotlin.math.min

@Optional.Interface(modid = "redstoneflux", iface = "cofh.redstoneflux.api.IEnergyContainerItem")
open class ItemElectricArmor constructor(materialIn: ArmorMaterial, armorIndex: EntityEquipmentSlot) :
    ItemArmor(materialIn, 0, armorIndex), IEnergyContainerItem, IItemElectricBase {
    @JvmField
    var transferMax: Float = 200.0f

    init {
        this.setMaxDamage(200)
        this.setNoRepair()

        run {}
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSArmorTab
    }

    override fun getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, type: String): String? {
        return GalaxySpace.TEXTURE_PREFIX + "textures/model/armor/nullarmor.png"
    }

    override fun onCreated(itemStack: ItemStack, world: World, player: EntityPlayer) {
        this.setElectricity(itemStack, 0.0f)
    }

    override fun recharge(stack: ItemStack?, energy: Float, doRecharge: Boolean): Float {
        if (!(stack)!!.hasTagCompound()) {
            (stack)!!.setTagCompound(NBTTagCompound())
        }

        val rejectedElectricity: Float =
            max(this.getElectricityStored(stack) + energy - this.getMaxElectricityStored(stack), 0.0f)
        var energyToReceive: Float = energy - rejectedElectricity
        if (energyToReceive > this.transferMax) {
            val var10000: Float = rejectedElectricity + (energyToReceive - this.transferMax)
            energyToReceive = this.transferMax
        }

        if (doRecharge) {
            this.setElectricity(stack, this.getElectricityStored(stack) + energyToReceive)
        }

        return energyToReceive
    }

    override fun discharge(stack: ItemStack?, energy: Float, doDischarge: Boolean): Float {
        val energyToTransfer: Float = min(min(this.getElectricityStored(stack), energy), this.transferMax)
        if (doDischarge) {
            this.setElectricity(stack, this.getElectricityStored(stack) - energyToTransfer)
        }

        return energyToTransfer
    }

    override fun getElectricityStored(stack: ItemStack?): Float {
        if (!(stack)!!.hasTagCompound()) {
            (stack)!!.setTagCompound(NBTTagCompound())
        }

        var energyStored: Float = 0.0f
        if ((stack)!!.getTagCompound()!!.containsTag("electricity")) {
            val obj: NBTBase = (stack)!!.getTagCompound()!!.getTag("electricity")
            if (obj is NBTTagDouble) {
                energyStored = obj.getFloat()
            } else if (obj is NBTTagFloat) {
                energyStored = obj.getFloat()
            }
        }

        (stack)!!.setItemDamage(
            ((stack)!!.getMaxDamage().toFloat() - energyStored / this.getMaxElectricityStored(stack) * (stack)!!.getMaxDamage()
                .toFloat()).toInt()
        )
        return energyStored
    }

    override fun getMaxElectricityStored(stack: ItemStack?): Float {
        return 200000.0f
    }

    override fun setElectricity(stack: ItemStack?, joules: Float) {
        if (!(stack)!!.hasTagCompound()) {
            (stack)!!.setTagCompound(NBTTagCompound())
        }

        val electricityStored: Float = max(min(joules, this.getMaxElectricityStored(stack)), 0.0f)
        (stack)!!.getTagCompound()!!.putFloat("electricity", electricityStored)
        (stack)!!.setItemDamage(
            ((stack)!!.getMaxDamage() - electricityStored / this.getMaxElectricityStored(stack) * (stack)!!.getMaxDamage()
                .toFloat()).toInt()
        )
    }

    override fun getTransfer(stack: ItemStack?): Float {
        if (!(stack)!!.hasTagCompound()) {
            (stack)!!.setTagCompound(NBTTagCompound())
        }
        return min(this.transferMax, this.getMaxElectricityStored(stack) - this.getElectricityStored(stack))
    }

    override fun getTierGC(itemStack: ItemStack?): Int {
        return 1
    }

    override fun getMaxTransferGC(itemStack: ItemStack?): Float {
        return this.transferMax
    }

    // RF Compact
    override fun receiveEnergy(container: ItemStack, maxReceive: Int, simulate: Boolean): Int {
        return (this.recharge(
            container,
            maxReceive * EnergyConfigHandler.RF_RATIO,
            !simulate
        ) / EnergyConfigHandler.RF_RATIO).toInt()
    }

    override fun extractEnergy(container: ItemStack, maxExtract: Int, simulate: Boolean): Int {
        return (this.discharge(
            container,
            maxExtract / EnergyConfigHandler.TO_RF_RATIO,
            !simulate
        ) * EnergyConfigHandler.TO_RF_RATIO).toInt()
    }

    override fun getEnergyStored(container: ItemStack): Int {
        return (this.getElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO).toInt()
    }

    override fun getMaxEnergyStored(container: ItemStack?): Int {
        return (this.getMaxElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO).toInt()
    }

    // Forge Energy Compact
    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return EnergyCapabilityProvider(stack, this)
    }

    private open inner class EnergyCapabilityProvider constructor(stack: ItemStack, item: ItemElectricArmor) :
        ICapabilityProvider {
        @JvmField
        var storage: EnergyStorage

        init {
            storage = object : EnergyStorage(
                (item.getMaxElectricityStored(stack) * EnergyConfigHandler.TO_RF_RATIO).toInt(),
                item.transferMax.toInt()
            ) {
                override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
                    if (!this.canReceive()) {
                        return 0
                    }
                    return (item.recharge(
                        stack,
                        maxReceive * EnergyConfigHandler.RF_RATIO,
                        !simulate
                    ) / EnergyConfigHandler.RF_RATIO).toInt()
                }

                override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
                    if (!canExtract()) {
                        return 0
                    }
                    return (item.discharge(
                        stack,
                        maxReceive / EnergyConfigHandler.RF_RATIO,
                        !simulate
                    ) * EnergyConfigHandler.RF_RATIO).toInt()
                }

                override fun getEnergyStored(): Int {
                    return (item.getMaxElectricityStored(stack) * EnergyConfigHandler.TO_RF_RATIO).toInt()
                }
            }
        }

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
            if (capability === CapabilityEnergy.ENERGY) {
                return true
            } else return false
        }

        override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
            if (hasCapability(capability, facing)) {
                return CapabilityEnergy.ENERGY.cast<T?>(storage)
            } else {
                return null
            }
        }
    }

    companion object {
        @JvmStatic
        fun isElectricItem(item: Item?): Boolean {
            if (item is IItemElectricBase) {
                return true
            }

            return false
        }

        @JvmStatic
        fun isElectricItemEmpty(itemstack: ItemStack): Boolean {
            if (itemstack.isEmpty()) {
                return false
            }
            val item: Item = itemstack.getItem()

            if (item is IItemElectricBase) {
                return (item as IItemElectricBase).getElectricityStored(itemstack) <= 0
            }

            return false
        }

        @JvmStatic
        fun isElectricItemCharged(itemstack: ItemStack?): Boolean {
            if (itemstack == null) return false
            val item: Item = itemstack.getItem()

            if (item is IItemElectricBase) {
                return (item as IItemElectricBase).getElectricityStored(itemstack) > 0
            }

            return false
        }
    }
}
