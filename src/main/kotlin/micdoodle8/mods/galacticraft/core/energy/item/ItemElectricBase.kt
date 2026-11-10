package micdoodle8.mods.galacticraft.core.energy.item

import micdoodle8.mods.miccore.Annotations.RuntimeInterface

import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper
import micdoodle8.mods.galacticraft.api.item.IItemElectricBase
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagDouble
import net.minecraft.nbt.NBTTagFloat
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion
import net.minecraftforge.fml.relauncher.FMLInjectionData
import kotlin.math.max
import kotlin.math.min

abstract class ItemElectricBase : Item(), IItemElectricBase {
    @JvmField
    var transferMax: Float = 0f
    private var mcVersion: DefaultArtifactVersion? = null

    init {
        this.setMaxStackSize(1)
        this.setMaxDamage(DAMAGE_RANGE)
        this.setNoRepair()
        this.setMaxTransfer()

        this.mcVersion = DefaultArtifactVersion(FMLInjectionData.data()[4] as String?)

        run {}
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    protected open fun setMaxTransfer() {
        this.transferMax = 200f
    }

    override fun getMaxTransferGC(itemStack: ItemStack?): Float {
        return this.transferMax
    }

    override fun addInformation(
        itemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
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

    /**
     * Makes sure the item is uncharged when it is crafted and not charged.
     * Change this if you do not want this to happen!
     */
    override fun onCreated(itemStack: ItemStack, par2World: World, par3EntityPlayer: EntityPlayer) {
        this.setElectricity(itemStack, 0f)
    }

    override fun recharge(itemStack: ItemStack?, energy: Float, doReceive: Boolean): Float {
        var rejectedElectricity: Float =
            max(this.getElectricityStored(itemStack) + energy - this.getMaxElectricityStored(itemStack), 0f)
        var energyToReceive: Float = energy - rejectedElectricity
        if (energyToReceive > this.transferMax) {
            rejectedElectricity += energyToReceive - this.transferMax
            energyToReceive = this.transferMax
        }

        if (doReceive) {
            this.setElectricity(itemStack, this.getElectricityStored(itemStack) + energyToReceive)
        }

        return energyToReceive
    }

    override fun discharge(itemStack: ItemStack?, energy: Float, doTransfer: Boolean): Float {
        val thisEnergy: Float = this.getElectricityStored(itemStack)
        val energyToTransfer: Float = min(min(thisEnergy, energy), this.transferMax)

        if (doTransfer) {
            this.setElectricity(itemStack, thisEnergy - energyToTransfer)
        }

        return energyToTransfer
    }

    override fun getTierGC(itemStack: ItemStack?): Int {
        return 1
    }

    override fun setElectricity(itemStack: ItemStack?, joules: Float) {
        // Saves the frequency in the ItemStack
        if ((itemStack)!!.getTagCompound() == null) {
            (itemStack)!!.setTagCompound(NBTTagCompound())
        }

        val electricityStored: Float = max(min(joules, this.getMaxElectricityStored(itemStack)), 0f)
        if (joules > 0f || (itemStack)!!.getTagCompound()!!.hasKey("electricity")) {
            (itemStack)!!.getTagCompound()!!.setFloat("electricity", electricityStored)
        }

        /** Sets the damage as a percentage to render the bar properly.  */
        (itemStack)!!.setItemDamage(DAMAGE_RANGE - (electricityStored / this.getMaxElectricityStored(itemStack) * DAMAGE_RANGE).toInt())
    }

    override fun getTransfer(itemStack: ItemStack?): Float {
        return min(this.transferMax, this.getMaxElectricityStored(itemStack) - this.getElectricityStored(itemStack))
    }

    /**
     * Gets the energy stored in the item. Energy is stored using item NBT
     */
    override fun getElectricityStored(itemStack: ItemStack?): Float {
        if ((itemStack)!!.getTagCompound() == null) {
            (itemStack)!!.setTagCompound(NBTTagCompound())
        }
        var energyStored: Float = 0f
        if ((itemStack)!!.getTagCompound()!!.hasKey("electricity")) {
            val obj: NBTBase = (itemStack)!!.getTagCompound()!!.getTag("electricity")
            if (obj is NBTTagDouble) {
                energyStored = obj.getFloat()
            } else if (obj is NBTTagFloat) {
                energyStored = obj.getFloat()
            }
        } else  //First time check item - maybe from addInformation() in a JEI recipe display?
        {
            if ((itemStack)!!.getItemDamage() == DAMAGE_RANGE) return 0f

            energyStored =
                this.getMaxElectricityStored(itemStack) * (DAMAGE_RANGE - (itemStack)!!.getItemDamage()) / DAMAGE_RANGE
            (itemStack)!!.getTagCompound()!!.setFloat("electricity", energyStored)
        }

        /** Sets the damage as a percentage to render the bar properly.  */
        (itemStack)!!.setItemDamage(DAMAGE_RANGE - (energyStored / this.getMaxElectricityStored(itemStack) * DAMAGE_RANGE).toInt())
        return energyStored
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            list.add(ElectricItemHelper.getUncharged(ItemStack(this)))
            list.add(ElectricItemHelper.getWithCharge(ItemStack(this), this.getMaxElectricityStored(ItemStack(this))))
        }
    }

    //For RF compatibility
    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyContainerItem", modID = "")
    fun receiveEnergy(container: ItemStack, maxReceive: Int, simulate: Boolean): Int {
        return (this.recharge(
            container,
            maxReceive * EnergyConfigHandler.RF_RATIO,
            !simulate
        ) / EnergyConfigHandler.RF_RATIO).toInt()
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyContainerItem", modID = "")
    fun extractEnergy(container: ItemStack, maxExtract: Int, simulate: Boolean): Int {
        return (this.discharge(
            container,
            maxExtract / EnergyConfigHandler.TO_RF_RATIO,
            !simulate
        ) * EnergyConfigHandler.TO_RF_RATIO).toInt()
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyContainerItem", modID = "")
    fun getEnergyStored(container: ItemStack): Int {
        return (this.getElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO).toInt()
    }

    @Annotations.RuntimeInterface(clazz = "cofh.redstoneflux.api.IEnergyContainerItem", modID = "")
    fun getMaxEnergyStored(container: ItemStack?): Int {
        return (this.getMaxElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO).toInt()
    }

    fun canSend(itemStack: ItemStack?): Boolean {
        return true
    }

    companion object {
        private const val DAMAGE_RANGE = 100

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
