package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.JavaUtil
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.NonNullList
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.ItemFluidContainer
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.math.max
import kotlin.math.min

abstract class ItemCanisterGeneric(assetName: String?) : ItemFluidContainer(Fluid.BUCKET_VOLUME) {
    private var allowedFluid: String? = null

    init {
        this.setMaxDamage(EMPTY)
        this.setMaxStackSize(1)
        this.setNoRepair()
        this.setTranslationKey(assetName)
        this.setContainerItem(GCItems.oilCanister)
        this.setHasSubtypes(true)
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return ItemCanisterGenericHandler(stack)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, 1))
        }
    }

    override fun getContainerItem(itemStack: ItemStack): ItemStack {
        //Workaround for strange behaviour in TE Transposer
        if (isTELoaded) {
            if (JavaUtil.instance.isCalledBy("thermalexpansion.block.machine.TileTransposer")) {
                return ItemStack.EMPTY
            }
        }

        return ItemStack(this.getContainerItem(), 1, EMPTY)
    }

    override fun onUpdate(par1ItemStack: ItemStack, par2World: World, par3Entity: Entity, par4: Int, par5: Boolean) {
        if (EMPTY == par1ItemStack.legacyItemValue) {
            if (par1ItemStack.getItem() !== GCItems.oilCanister) {
                this.replaceEmptyCanisterItem(par1ItemStack, GCItems.oilCanister)
            }
            par1ItemStack.setTagCompound(null)
        } else if (par1ItemStack.legacyItemValue <= 0) {
            par1ItemStack.setItemDamage(1)
        }
    }

    fun setAllowedFluid(name: String?) {
        this.allowedFluid = name
    }

    fun getAllowedFluid(): String? {
        return this.allowedFluid
    }

    open fun fill(container: ItemStack?, resource: FluidStack?, doFill: Boolean): Int {
        if (resource == null || resource.getFluid() == null || resource.amount <= 0 || container == null || container.legacyItemValue <= 1 || (container.getItem() !is ItemCanisterGeneric)) {
            return 0
        }
        val fluidName: String = resource.getFluid().getName()

        var capacityPlusOne: Int = container.legacyItemValue
        if (capacityPlusOne <= 1) {
            if (capacityPlusOne < 1) {
                //It shouldn't be possible, but just in case, set this to a proper filled item
                container.setItemDamage(1)
            }
            return 0
        }
        if (capacityPlusOne >= EMPTY) {
            //Empty canister - find a new canister to match the fluid
            for (i in GCItems.canisterTypes) {
                if (fluidName.equals((i)!!.allowedFluid, ignoreCase = true)) {
                    if (!doFill) {
                        return min(resource.amount, this.capacity)
                    }

                    this.replaceEmptyCanisterItem(container, i)
                    break
                }
            }
            if (capacityPlusOne > EMPTY) {
                //It shouldn't be possible, but just in case, set this to a proper empty item
                capacityPlusOne = EMPTY
                container.setItemDamage(capacityPlusOne)
            }
        }

        if (fluidName.equals((container.getItem() as ItemCanisterGeneric).allowedFluid, ignoreCase = true)) {
            val added: Int = min(resource.amount, capacityPlusOne - 1)
            if (doFill && added > 0) {
                container.setItemDamage(max(1, container.legacyItemValue - added))
            }
            return added
        }

        return 0
    }

    open fun drain(container: ItemStack, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (this.allowedFluid == null || container.legacyItemValue >= EMPTY) {
            return null
        }

        val used: FluidStack? = this.getFluid(container)
        if (used != null && used.amount > maxDrain) used.amount = maxDrain
        if (doDrain && used != null && used.amount > 0) {
            this.setNewDamage(container, container.legacyItemValue + used.amount)
        }
        return used
    }

    protected open fun setNewDamage(container: ItemStack, newDamage: Int) {
        var newDamage: Int = newDamage
        newDamage = min(newDamage, EMPTY)
        container.setItemDamage(newDamage)
        if (newDamage == EMPTY) {
            if (container.getItem() !== GCItems.oilCanister) {
                this.replaceEmptyCanisterItem(container, GCItems.oilCanister)
                return
            }
        }
    }

    private fun replaceEmptyCanisterItem(container: ItemStack, newItem: Item?) {
        try {
            val itemStack: Class<*> = container.javaClass
            val itemId: Field =
                itemStack.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "item" else "field_151002_e")
            itemId.setAccessible(true)
            itemId.set(container, newItem)
            val forgeInit: Method = itemStack.getDeclaredMethod("forgeInit")
            forgeInit.setAccessible(true)
            forgeInit.invoke(container)
        } catch (ignore: Exception) {
        }
    }

    fun getFluid(container: ItemStack): FluidStack? {
        val fluidName: String? = (container.getItem() as ItemCanisterGeneric).allowedFluid
        if (fluidName == null || container.legacyItemValue >= EMPTY) {
            return null
        }

        val fluid: Fluid? = FluidRegistry.getFluid(fluidName)
        if (fluid == null) {
            return null
        }

        return FluidStack(fluid, EMPTY - container.legacyItemValue)
    }

    companion object {
        @JvmField
        val EMPTY: Int = Fluid.BUCKET_VOLUME + 1
        private val isTELoaded = CompatibilityManager.isTELoaded()
    }
}
