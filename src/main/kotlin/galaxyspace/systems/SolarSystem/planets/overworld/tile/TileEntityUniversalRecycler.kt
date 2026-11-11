package galaxyspace.systems.SolarSystem.planets.overworld.tile

import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockUniversalRecycler
import galaxyspace.core.prefab.tile.TileEntityUpgradeMachine
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityUniversalRecycler
import galaxyspace.core.util.GSUtils
import galaxyspace.core.GSFluids
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes.RecycleRecipe
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.RecyclerRecipes
import galaxyspace.GalaxySpace
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.item.Items
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.LogicalSide as Side

open class TileEntityUniversalRecycler constructor() : TileEntityUpgradeMachine("tile.universal_recycler.name"),
    IFluidHandlerWrapper, ISidedInventory, IPacketReceiver {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED_BASE

    //@NetworkedField(targetSide = Side.CLIENT)
    //public int processTicks = 0;
    private val tankCapacity = 3000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var waterTank: FluidTank = FluidTank(this.tankCapacity)

    /*@NetworkedField(targetSide = Side.CLIENT)
    public FluidTank waterTank1 = new FluidTank(this.tankCapacity);
    @NetworkedField(targetSide = Side.CLIENT)
    public FluidTank waterTank2 = new FluidTank(this.tankCapacity);*/
    init {
        this.storage.setCapacity(20000f)
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 else 75).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(6 + 4, ItemStack.EMPTY)
        this.setTierGC(1)
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            GSUtils.checkFluidTankTransfer(this.getInventory(), 2, this.waterTank)

            if (!this.getInventory().get(4).isEmpty()) {
                val liquid: FluidStack? = FluidUtil.getFluidContained(this.getInventory().get(4))

                if (liquid != null) {
                    val isWater: Boolean = FluidRegistry.getFluidName(liquid).startsWith("liquidethanemethane")

                    if (isWater) {
                        if (this.waterTank.getFluid() == null || this.waterTank.getFluid()!!.amount + liquid.amount <= this.waterTank.getCapacity()) {
                            this.waterTank.fill(FluidStack(GSFluids.LiquidEthaneMethane, liquid.amount), true)

                            if (micdoodle8.mods.galacticraft.core.util.FluidUtil.isBucket(
                                    this.getInventory().get(4)
                                ) && micdoodle8.mods.galacticraft.core.util.FluidUtil.isFilledContainer(
                                    this.getInventory().get(4)
                                )
                            ) {
                                val amount: Int = this.getInventory().get(4).getCount()
                                if (amount > 1) this.waterTank.fill(
                                    FluidStack(
                                        GSFluids.LiquidEthaneMethane,
                                        (amount - 1) * Fluid.BUCKET_VOLUME
                                    ), true
                                )
                                this.getInventory().set(4, ItemStack(Items.BUCKET, amount))
                            } else {
                                this.getInventory().get(4).shrink(1)

                                if (this.getInventory().get(4).getCount() === 0) {
                                    this.getInventory().set(4, ItemStack.EMPTY)
                                }
                            }
                        }
                    }
                }
            }

            if (this.canProcess()) {
                if (this.hasEnoughEnergyToRun) {
                    /*	int boost_speed = 0, energy_boost = 0;
            	   	
            	   	//////////

                	for(int i = 0; i <= 3; i++)
                	{
                		if(this.getInventory().get(this.getInventory().size() - i - 1).isItemEqual(new ItemStack(GSItems.UPGRADES, 1, 2)))
                			boost_speed++;
                		if(this.getInventory().get(this.getInventory().size() - i - 1).isItemEqual(new ItemStack(GSItems.UPGRADES, 1, 3)))
                			energy_boost++;
                	}
                	*/
                    // this.processTicks += 1 * (1 + this.getBoost());
                    //this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 90 + (60 * this.getBoost()) - (20 * getEnergy()) : 75 + (55 * this.getBoost()) - (15 * this.getEnergy()));
                    /////////////

                    //50% extra speed boost for Tier 2 machine if powered by Tier 2 power
                    //if (this.tierGC == 2) this.processTimeRequired = 200 / (1 + this.poweredByTierGC);


                    if (this.processTicks >= this.processTimeRequired) {
                        this.smeltItem()
                        this.processTicks = 0
                    }
                }
                /*else if (this.processTicks > 0 && this.processTicks < this.processTimeRequired)
                {
                    //Apply a "cooling down" process if the electric furnace runs out of energy while smelting
                    if (this.world.rand.nextInt(4) == 0)
                    {
                        this.processTicks++;
                    }
                }*/
            } else {
                this.processTicks = 0
            }
        }
    }

    override fun canProcess(): Boolean {
        //FluidStack fluid1 = RecyclerRecipes.recycling().getRecyclingFluid1(this.waterTank1.getFluid());
        //if(fluid1 != null) GalaxySpace.debug(fluid1.getFluid().getName() + "");

        if (this.getInventory().get(1).isEmpty()) {
            return false
        } else {
            val input: ItemStack = this.getInventory().get(1)
            //Map<ItemStack, RecycleRecipe> recipes = RecyclerRecipes.recycling().getRecipes(); 
            //for(Entry<ItemStack, RecycleRecipe> recipe : recipes.entrySet())
            val recipe: RecycleRecipe? = RecyclerRecipes.recycling().getRecipe(input)
            if (recipe != null) {
                if (input.getCount() < (recipe.getInput())!!.getCount()) return false

                if (recipe.getFluidStack() != null) {
                    if (this.waterTank.getFluidAmount() > 0 && !this.waterTank.getFluid()!!
                            .isFluidEqual(recipe.getFluidStack())
                    ) return false

                    if (this.waterTank.getFluidAmount() >= this.waterTank.getCapacity()) return false
                }

                if (!(recipe.getOutput())!!.isEmpty()) {
                    if (!this.getInventory().get(3).isEmpty() && !this.getInventory().get(3)
                            .isItemEqual(recipe.getOutput())
                    ) return false

                    val result: Int = this.getInventory().get(3).getCount() + (recipe.getOutput())!!.getCount()
                    return result <= getMaxStackSize() && result <= this.getInventory().get(3).getMaxStackSize()
                }

                if (!(recipe.getOutput_2())!!.isEmpty()) {
                    if (!this.getInventory().get(4).isEmpty() && !this.getInventory().get(4)
                            .isItemEqual(recipe.getOutput_2())
                    ) return false

                    val result: Int = this.getInventory().get(4).getCount() + (recipe.getOutput_2())!!.getCount()
                    return result <= getMaxStackSize() && result <= this.getInventory().get(4).getMaxStackSize()
                }
            } else return false
        }

        return true
    }

    open fun smeltItem() {
        if (this.canProcess()) {
            val input: ItemStack? = this.getInventory().get(1)
            val recipe: RecycleRecipe? = RecyclerRecipes.recycling().getRecipe((input)!!)

            if (recipe != null) {
                val hasRand: Boolean = recipe.hasChance()
                val hasRand_2: Boolean = recipe.hasChance_2()

                if (!hasRand) {
                    if (this.getInventory().get(3).isEmpty()) this.getInventory().set(3, (recipe.getOutput())!!.copy())
                    else if (this.getInventory().get(3).isItemEqual(recipe.getOutput())) this.getInventory().get(3)
                        .grow((recipe.getOutput())!!.getCount())

                    if (!(recipe.getOutput_2())!!.isEmpty()) {
                        if (!hasRand_2) {
                            if (this.getInventory().get(4).isEmpty()) this.getInventory()
                                .set(4, (recipe.getOutput_2())!!.copy())
                            else if (this.getInventory().get(4).isItemEqual(recipe.getOutput_2())) this.getInventory()
                                .get(4).grow((recipe.getOutput_2())!!.getCount())
                        } else if (this.world.rand.nextInt(100) <= recipe.getChance_2()) {
                            if (this.getInventory().get(4).isEmpty()) this.getInventory()
                                .set(4, (recipe.getOutput_2())!!.copy())
                            else if (this.getInventory().get(4).isItemEqual(recipe.getOutput_2())) this.getInventory()
                                .get(4).grow((recipe.getOutput_2())!!.getCount())
                        }
                    }
                } else {
                    if (this.world.rand.nextInt(100) <= recipe.getChance()) {
                        if (this.getInventory().get(3).isEmpty()) this.getInventory().set(3, (recipe.getOutput())!!.copy())
                        else if (this.getInventory().get(3).isItemEqual(recipe.getOutput())) this.getInventory().get(3)
                            .grow((recipe.getOutput())!!.getCount())

                        if (!(recipe.getOutput_2())!!.isEmpty() && this.world.rand.nextInt(100) <= recipe.getChance_2()) {
                            if (this.getInventory().get(4).isEmpty()) this.getInventory()
                                .set(4, (recipe.getOutput_2())!!.copy())
                            else if (this.getInventory().get(4).isItemEqual(recipe.getOutput_2())) this.getInventory()
                                .get(4).grow((recipe.getOutput_2())!!.getCount())
                        }
                    }
                }

                this.getInventory().get(1).shrink((recipe.getInput())!!.getCount())

                if (recipe.getFluidStack() != null) this.waterTank.fill(
                    FluidStack(
                        (recipe.getFluidStack())!!.getFluid(),
                        if ((recipe.getFluidStack())!!.amount > this.waterTank.getCapacity()) this.waterTank.getCapacity() else (recipe.getFluidStack())!!.amount
                    ), true
                )
            }
        }
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)

        this.processTicks = par1NBTTagCompound.getIntOr("smeltingTicks", 0)
        this.inventory = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())

        if (par1NBTTagCompound.containsTag("waterTank")) this.waterTank.readFromNBT(par1NBTTagCompound.getCompoundOrEmpty("waterTank"))

        /* 
        if (par1NBTTagCompound.containsTag("waterTank1"))        
        	this.waterTank1.readFromNBT(par1NBTTagCompound.getCompoundOrEmpty("waterTank1"));
        
        if (par1NBTTagCompound.containsTag("waterTank2"))        
        	this.waterTank2.readFromNBT(par1NBTTagCompound.getCompoundOrEmpty("waterTank2"));*/
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.putInt("smeltingTicks", this.processTicks)
        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())

        if (this.waterTank.getFluid() != null) par1NBTTagCompound.put(
            "waterTank",
            this.waterTank.writeToNBT(NBTTagCompound())
        )


        /*  if (this.waterTank1.getFluid() != null)        
        	par1NBTTagCompound.put("waterTank1", this.waterTank1.writeToNBT(new NBTTagCompound()));
        
        if (this.waterTank2.getFluid() != null)        
        	par1NBTTagCompound.put("waterTank2", this.waterTank2.writeToNBT(new NBTTagCompound()));
        */
        return par1NBTTagCompound
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1, 3, 4)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return slotID == 3
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
            1 -> return true
        }

        return false
    }

    public override fun shouldUseEnergy(): Boolean {
        return this.canProcess()
    }

    override fun getBatteryInSlot(): ItemStack? {
        return this.getItem(0)
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        return 0
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (from == getPipe()) {
            if (resource != null && resource.isFluidEqual(this.waterTank.getFluid())) return this.waterTank.drain(
                resource.amount,
                doDrain
            )
        }
        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (from == getPipe()) {
            return this.waterTank.drain(maxDrain, doDrain)
        }
        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        return false
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == getPipe()) {
            return this.waterTank.getFluid() != null && this.waterTank.getFluidAmount() > 0
        }
        return false
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        return ((arrayOf<FluidTankInfo>(FluidTankInfo(this.waterTank)) as Array<FluidTankInfo?>?))!!
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return FluidHandlerWrapper(this, facing) as T
        }
        return super.getCapability<T?>(capability, facing)
    }

    private fun getPipe(): EnumFacing {
        return getFront().rotateYCCW()
    }

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockUniversalRecycler) {
            return state.getValue(BlockUniversalRecycler.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection()
        }
        if (type == NetworkType.FLUID) {
            val pipeSide: EnumFacing = getPipe()
            return direction == pipeSide
        }
        return false
    }

    open fun getScaledTankLevel(i: Int): Int {
        return this.waterTank.getFluidAmount() * i / this.waterTank.getCapacity()
    }

    override fun upgradeSlots(): IntArray {
        return intArrayOf(5, 6, 7, 8)
    }

    companion object {
        const val PROCESS_TIME_REQUIRED_BASE: Int = 130
    }
}
