package galaxyspace.core.prefab.items

import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.handler.GSItemCanisterGenericHandler
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.JavaUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.math.max
import kotlin.math.min

open class ItemFluidCanisterGS @JvmOverloads constructor(
    assetName: String?,
    private val fluid: Fluid,
    private val amount: Int = EMPTY
) : ItemCanisterGeneric(assetName), ISortableItem {
    init {
        this.setAllowedFluid(fluid.getName())
        this.setMaxDamage(amount)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return EnumRarity.COMMON
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, 1))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage() > 0) {
            tooltip.add(GCCoreUtil.translate(this.fluid.getUnlocalizedName()) + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()))
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.CANISTER
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return GSItemCanisterGenericHandler(stack, this.amount)
    }

    override fun getContainerItem(itemStack: ItemStack): ItemStack {
        //Workaround for strange behaviour in TE Transposer
        if (CompatibilityManager.isTELoaded()) {
            if (JavaUtil.instance.isCalledBy("thermalexpansion.block.machine.TileTransposer")) {
                return ItemStack.EMPTY
            }
        }

        return ItemStack(this.getContainerItem(), 1, this.amount)
    }

    override fun onUpdate(par1ItemStack: ItemStack, par2World: World, par3Entity: Entity, par4: Int, par5: Boolean) {
        if (this.amount == par1ItemStack.getItemDamage()) {
            if (par1ItemStack.getItem() !== GCItems.oilCanister) {
                this.replaceEmptyCanisterItem(par1ItemStack, GCItems.oilCanister)
            }
            par1ItemStack.setTagCompound(null)
        } else if (par1ItemStack.getItemDamage() <= 0) {
            par1ItemStack.setItemDamage(1)
        }
    }

    override fun fill(container: ItemStack?, resource: FluidStack?, doFill: Boolean): Int {
        if (resource == null || resource.getFluid() == null || resource.amount <= 0 || container == null || container.getItemDamage() <= 1 || (container.getItem() !is ItemCanisterGeneric)) {
            return 0
        }
        val fluidName: String = resource.getFluid().getName()

        var capacityPlusOne: Int = container.getItemDamage()
        if (capacityPlusOne <= 1) {
            if (capacityPlusOne < 1) {
                //It shouldn't be possible, but just in case, set this to a proper filled item
                container.setItemDamage(1)
            }
            return 0
        }
        if (capacityPlusOne >= this.amount) {
            //Empty canister - find a new canister to match the fluid
            for (i in GCItems.canisterTypes) {
                if (fluidName.equals((i)!!.getAllowedFluid(), ignoreCase = true)) {
                    if (!doFill) {
                        return min(resource.amount, this.capacity)
                    }

                    this.replaceEmptyCanisterItem(container, i)
                    break
                }
            }
            if (capacityPlusOne > this.amount) {
                //It shouldn't be possible, but just in case, set this to a proper empty item
                capacityPlusOne = EMPTY
                container.setItemDamage(capacityPlusOne)
            }
        }

        if (fluidName.equals((container.getItem() as ItemCanisterGeneric).getAllowedFluid(), ignoreCase = true)) {
            val added: Int = min(resource.amount, capacityPlusOne - 1)
            if (doFill && added > 0) {
                container.setItemDamage(max(1, container.getItemDamage() - added))
            }
            return added
        }

        return 0
    }

    override fun drain(container: ItemStack, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (this.getAllowedFluid() == null || container.getItemDamage() >= this.amount) {
            return null
        }

        val used: FluidStack? = this.getFluid(container)
        if (used != null && used.amount > maxDrain) used.amount = maxDrain
        if (doDrain && used != null && used.amount > 0) {
            this.setNewDamage(container, container.getItemDamage() + used.amount)
        }
        return used
    }

    override fun setNewDamage(container: ItemStack, newDamage: Int) {
        var newDamage: Int = newDamage
        newDamage = min(newDamage, this.amount)
        container.setItemDamage(newDamage)
        if (newDamage == this.amount) {
            if (container.getItem() !== GCItems.oilCanister) {
                this.replaceEmptyCanisterItem(container, GCItems.oilCanister)
                return
            }
        }
    }

    private fun replaceEmptyCanisterItem(container: ItemStack, newItem: Item?) {
        try {
            val itemStack: Class<out ItemStack> = container.javaClass
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
}
