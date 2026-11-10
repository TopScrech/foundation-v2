package micdoodle8.mods.galacticraft.core.items

import java.util.HashMap
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.minecraftforge.fluids.capability.IFluidTankProperties

import javax.annotation.Nonnull

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemBucket
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.*
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBucketGC constructor(block: Block, fluid: Fluid) : ItemBucket(block), ISortableItem,
    ICapabilityProvider {
    //	private String texture_prefix;
    @JvmField
    var accepts: Fluid

    init {
        this.setContainerItem(Items.BUCKET)
        this.accepts = fluid
        //TODO - could be getNameFuzzy
        allFluids.put(fluid.getName(), this)
    }

    open fun drainBucketTo(fluidHandler: IFluidHandler): ItemStack {
        val fs: FluidStack = FluidStack(this.accepts, Fluid.BUCKET_VOLUME)
        val transferred: Int = fluidHandler.fill(fs, false)
        if (transferred == Fluid.BUCKET_VOLUME) {
            fluidHandler.fill(fs, true)
            return ItemStack(Items.BUCKET)
        }
        return ItemStack.EMPTY
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    @SideOnly(Side.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.BUCKET
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider {
        return FluidHandlerBucketGC(stack)
    }

    override fun hasCapability(@Nonnull capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY
    }

    override fun <T> getCapability(@Nonnull capability: Capability<T?>, facing: EnumFacing?): T? {
        return if (capability === CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) this as T else null
    }


    open class FluidHandlerBucketGC constructor(@Nonnull container: ItemStack) : IFluidHandlerItem,
        ICapabilityProvider {
        @JvmField
        @Nonnull
        protected var container: ItemStack

        @JvmField
        protected var capacity: Int
        private val item: ItemBucketGC

        init {
            container.setCount(1)
            this.container = container
            this.item = container.getItem() as ItemBucketGC
            this.capacity = Fluid.BUCKET_VOLUME
        }

        @Nonnull
        override fun getContainer(): ItemStack {
            return container
        }

        open fun getFluid(): FluidStack? {
            return FluidStack(item.accepts, this.capacity)
        }

        protected open fun setFluid(fluid: FluidStack?) {
        }

        override fun getTankProperties(): Array<IFluidTankProperties?> {
            return ((arrayOf<IFluidTankProperties>(FluidTankProperties(getFluid(), capacity)) as Array<IFluidTankProperties?>?))!!
        }

        override fun fill(resource: FluidStack?, doFill: Boolean): Int {
            //This is a full bucket already, can't be filled
            return 0
        }

        override fun drain(resource: FluidStack?, doDrain: Boolean): FluidStack? {
            if (resource == null || !this.canDrainFluidType(resource)) {
                return null
            }
            return drain(resource.amount, doDrain)
        }

        override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
            if (maxDrain < this.capacity) {
                return null
            }
            if (doDrain) {
                container = ItemStack(Items.BUCKET)
            }
            return this.getFluid()
        }

        open fun canFillFluidType(fluid: FluidStack?): Boolean {
            return false
        }

        open fun canDrainFluidType(fluid: FluidStack): Boolean {
            return FluidUtil.fluidsSame(item.accepts, fluid.getFluid(), true)
        }

        protected open fun setContainerToEmpty() {
            container = ItemStack(Items.BUCKET)
        }

        override fun hasCapability(@Nonnull capability: Capability<*>, facing: EnumFacing?): Boolean {
            return capability === CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY
        }

        override fun <T> getCapability(@Nonnull capability: Capability<T?>, facing: EnumFacing?): T? {
            return if (capability === CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) this as T else null
        }

        companion object {
            const val FLUID_NBT_KEY: String = "Fluid"
        }
    }

    companion object {
        private val allFluids: HashMap<String?, ItemBucketGC?> = HashMap<String?, ItemBucketGC?>()

        @JvmStatic
        fun getBucketForFluid(fluid: Fluid): ItemBucketGC {
            //TODO - could be getNameFuzzy
            return allFluids.get(fluid.getName())!!
        }

        @JvmStatic
        fun fillBucketFrom(fluidHandler: IFluidHandler): ItemStack? {
            val liquid: FluidStack? = fluidHandler.drain(Fluid.BUCKET_VOLUME, false)
            if (liquid != null && liquid.amount == Fluid.BUCKET_VOLUME) {
                val test: ItemBucketGC? = getBucketForFluid(liquid.getFluid())
                if (test != null) {
                    fluidHandler.drain(Fluid.BUCKET_VOLUME, true)
                    return ItemStack(test)
                }
            }
            return null
        }
    }
}
