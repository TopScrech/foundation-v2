package micdoodle8.mods.galacticraft.core.util

import javax.annotation.Nonnull

import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.items.ItemBucketGC
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidActionResult
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.Locale
import kotlin.math.min

object FluidUtil {
    private const val oldFluidIDMethod = true
    private val fluidStackClass: Class<*>? = null
    private val getFluidMethod: Method? = null
    private val fluidIdField: Field? = null

    /**
     * This looks for any type of filled or partly filled container of "fuel"
     *
     * @param var4 The ItemStack being examined
     * @return True if it's a container with "fuel"
     */
    @JvmStatic
    fun isFuelContainerAny(var4: ItemStack): Boolean {
        if (var4.getItem() is ItemCanisterGeneric) {
            return var4.getItem() === GCItems.fuelCanister && var4.getItemDamage() < var4.getMaxDamage()
        }

        if (var4.getItem() === GCItems.bucketFuel) {
            return true
        }

        val liquid: FluidStack? = FluidUtil.getFluidContained(var4)
        return liquid != null && testFuel(FluidRegistry.getFluidName(liquid))
    }

    /**
     * Tests whether the named liquid is fuel / fuelgc
     * or rocket fuel from another mod, for compatibility (includes RotaryCraft Jet Fuel)
     *
     * @param name - which MUST be in lowercase, as always returned by FluidRegistry.getFluidName
     * @return true if a type of recognised fuel, false if not
     */
    @JvmStatic
    fun testFuel(name: String?): Boolean {
        if (name == null) return false

        if (name == "fuel" || name == "fuelgc" || name == "fuel_light") {
            return true
        }

        if (name.contains("rocket") && name.contains("fuel"))  //from EnderIO
        {
            return true
        }

        if (name == "rc jet fuel")  //from RotaryCraft
        {
            return true
        }

        if (name.startsWith("fuel")) {
            return !name.contains("dense") && !name.contains("heavy") && !name.contains("mixed") && !name.contains("gaseous")
        }

        return name == "refined_fuel" //from Thermal Expansion
    }

    @JvmStatic
    fun testOil(name: String?): Boolean {
        if (name == null) return false

        if (name == "oil" || name == "oilgc") {
            return true
        }

        if (name == "crude_oil") {
            return true
        }

        if (name.startsWith("oil")) {
            return !name.contains("dense") && !name.contains("residue")
        }

        return false
    }

    @JvmStatic
    fun isFuel(fluid: FluidStack?): Boolean {
        return fluid != null && testFuel(FluidRegistry.getFluidName(fluid))
    }

    @JvmStatic
    fun isOil(fluid: FluidStack?): Boolean {
        return fluid != null && testOil(FluidRegistry.getFluidName(fluid))
    }

    @JvmStatic
    fun isFluidFuzzy(fluid: FluidStack?, name: String?): Boolean {
        return fluid != null && fluid.getFluid() != null && fluid.getFluid().getName().startsWith(name!!)
    }

    @JvmStatic
    fun isFluidStrict(fluid: FluidStack?, name: String?): Boolean {
        return fluid != null && fluid.getFluid() != null && fluid.getFluid().getName() == name
    }

    /**
     * Fill Galacticraft entities (e.g. rockets, buggies) with Galacticraft fuel.
     * For legacy reasons, accepts either "fuel" or "fuelgc".
     * Auto-converts either one to the same type of fuel as is already contained.
     *
     * @param tank   The tank
     * @param liquid A FluidStack being the fuel offered
     * @param doFill True if this is not a simulation / tank capacity test
     * @return The amount filled
     */
    @JvmStatic
    fun fillWithGCFuel(tank: FluidTank, liquid: FluidStack?, doFill: Boolean): Int {
        if (liquid != null && testFuel(FluidRegistry.getFluidName(liquid))) {
            val liquidInTank: FluidStack? = tank.getFluid()

            //If the tank is empty, fill it with the current type of GC fuel
            if (liquidInTank == null) {
                return tank.fill(FluidStack(GCFluids.fluidFuel, liquid.amount), doFill)
            }

            //If the tank already contains something, fill it with more of the same
            if (liquidInTank.amount < tank.getCapacity()) {
                return tank.fill(FluidStack(liquidInTank, liquid.amount), doFill)
            }
        }

        return 0
    }

    @JvmStatic
    fun isOilContainerAny(var4: ItemStack): Boolean {
        if (var4.getItem() is ItemCanisterGeneric) {
            return var4.getItem() === GCItems.oilCanister && var4.getItemDamage() < var4.getMaxDamage()
        }

        if (var4.getItem() === GCItems.bucketOil) {
            return true
        }

        val liquid: FluidStack? = FluidUtil.getFluidContained(var4)
        return isOil(liquid)
    }

    /**
     * This looks for any type of filled or partly filled container of "methane" gas
     *
     * @param var4 The ItemStack being examined
     * @return True if it's a container with "methane"
     */
    @JvmStatic
    fun isMethaneContainerAny(var4: ItemStack): Boolean {
        if (var4.getItem() is ItemCanisterGeneric) {
            return var4.getItem() === AsteroidsItems.methaneCanister && var4.getItemDamage() < var4.getMaxDamage()
        }

        val stack: FluidStack? = FluidUtil.getFluidContained(var4)
        return stack != null && stack.getFluid() != null && stack.getFluid().getName().lowercase(Locale.getDefault())
            .contains("methane")
    }

    /**
     * This looks for any type of completely full container
     * Used, for example, in canExtractItem() logic
     *
     * @param var4 The ItemStack being examined
     * @return True if it's a full container
     */
    @JvmStatic
    fun isFullContainer(var4: ItemStack): Boolean {
        if (var4.getItem() is ItemCanisterGeneric) {
            return var4.getItemDamage() == 1
        }

        if (var4.getItem() is ItemBucketGC) {
            return true
        }

        val liquid: FluidStack? = FluidUtil.getFluidContained(var4)
        return liquid != null
    }

    /**
     * This tries to fill the given container (at inventory[slot]) with fluid from the specified tank
     * If successful, it places the resulting filled container in inventory[slot]
     *
     *
     *
     * @param tank         The tank to take the fluid from
     * @param liquid       The type of liquid in that tank (the calling method will normally have checked this already)
     * @param inventory
     * @param slot
     * @param canisterType The type of canister to return, if it's a canister being filled (pre-matched with the liquid type)
     */
    @JvmStatic
    fun tryFillContainer(
        tank: FluidTank,
        liquid: FluidStack,
        inventory: NonNullList<ItemStack>,
        slot: Int,
        canisterType: Item
    ) {
        val slotItem: ItemStack = inventory.get(slot)
        if (liquid.amount <= 0 || slotItem.isEmpty()) {
            return
        }

        if (slotItem.getItem() is ItemCanisterGeneric) {
            if (slotItem.getCount() != 1 || slotItem.getItem() !== canisterType && slotItem.getItemDamage() != ItemCanisterGeneric.EMPTY) {
                return
            }

            val used: Int = min(liquid.amount, slotItem.getItemDamage() - 1)
            if (used > 0) {
                inventory.set(slot, ItemStack(canisterType, 1, slotItem.getItemDamage() - used))
                tank.drain(used, true)
            }
            return
        } else if (slotItem.getCount() == 1) {
            var bucketGC: ItemBucketGC? = null
            if (slotItem.getItem() === Items.BUCKET && liquid.amount >= Fluid.BUCKET_VOLUME && (ItemBucketGC.getBucketForFluid(
                    liquid.getFluid()
                ).also { bucketGC = it }) != null
            ) {
                inventory.set(slot, ItemStack(bucketGC))
                tank.drain(Fluid.BUCKET_VOLUME, true)
                return
            }

            val handler: IFluidHandlerItem? = FluidUtil.getFluidHandler(inventory.get(slot))
            if (handler != null) {
                val used: Int = handler.fill(liquid, true)
                if (used > 0) {
                    inventory.set(slot, handler.getContainer())
                    tank.drain(used, true)
                }
            }
        }
    }

    /**
     * @param tank
     * @param inventory
     * @param slot
     */
    @JvmStatic
    fun tryFillContainerFuel(tank: FluidTank, inventory: NonNullList<ItemStack>, slot: Int) {
        if (isValidContainer(inventory.get(slot))) {
            var liquid: FluidStack? = tank.getFluid()

            if (liquid != null && liquid.amount > 0) {
                val liquidname: String = liquid.getFluid().getName()

                //Test for the GC fuels (though anything similarly named would also pass here)
                if (liquidname.startsWith("fuel")) {
                    //Make sure it is the current GC fuel
                    if (liquidname != (GCFluids.fluidFuel)!!.getName()) {
                        liquid = FluidStack(GCFluids.fluidFuel, liquid.amount)
                    }

                    //But match any existing fuel fluid in the container
                    val stack: ItemStack = inventory.get(slot)
                    val item: Item? = if (stack.isEmpty()) null else stack.getItem()
                    if (!(item is ItemCanisterGeneric || item is ItemBucketGC)) {
                        val handler: IFluidHandlerItem? = FluidUtil.getFluidHandler(stack)
                        if (handler != null) {
                            val existingFluid: FluidStack? = FluidUtil.getFluidContained(stack)
                            if (existingFluid != null && existingFluid.getFluid()
                                    .getName() != (GCFluids.fluidFuel)!!.getName()
                            ) {
                                liquid = FluidStack(existingFluid, liquid.amount)
                            }
                        }
                    }

                    tryFillContainer(tank, liquid, inventory, slot, (GCItems.fuelCanister)!!)
                }
            }
        }
    }

    /**
     * This tries to empty the container (at inventory[slot]) into the specified tank
     * forcing the tank contents to Fluid: desiredLiquid even if the container had
     * something different (useful for different types of fuel, for example).
     * If successful, it replaces inventory[slot] with the corresponding empty container
     *
     *
     *
     * @param tank         The tank to fill with the fluid
     * @param desiredLiquid       The type of liquid intended for that tank
     * @param stacks
     * @param slot
     * @param amountOffered  The amount in the container being offered
     */
    @JvmStatic
    fun loadFromContainer(
        tank: FluidTank,
        desiredLiquid: Fluid,
        stacks: NonNullList<ItemStack>,
        slot: Int,
        amountOffered: Int
    ) {
        val slotItem: ItemStack = stacks.get(slot)

        if (slotItem.getItem() is ItemCanisterGeneric) {
            val originalDamage: Int = slotItem.getItemDamage()
            val used: Int = tank.fill(FluidStack(desiredLiquid, ItemCanisterGeneric.EMPTY - originalDamage), true)
            if (originalDamage + used >= ItemCanisterGeneric.EMPTY) {
                stacks.set(slot, ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY))
            } else {
                stacks.set(slot, ItemStack(slotItem.getItem(), 1, originalDamage + used))
            }
        } else if (slotItem.getItem() is ItemBucketGC && amountOffered == Fluid.BUCKET_VOLUME) {
            val bucket: ItemBucketGC = slotItem.getItem() as ItemBucketGC
            if (fluidsSame(bucket.accepts, desiredLiquid, true)) {
                tank.fill(FluidStack(desiredLiquid, amountOffered), true)
                stacks.set(slot, ItemStack(Items.BUCKET))
            }
        } else {
            if (tank.getFluid() == null || amountOffered <= tank.getCapacity() - tank.getFluid()!!.amount) {
                //Now deal with the resulting container
                if (isFilledContainer(slotItem)) {
                    val itemCount: Int = slotItem.getCount()
                    if (isBucket(slotItem)) {
                        val used: Int = tank.fill(FluidStack(desiredLiquid, amountOffered), true)
                        if (itemCount > 1) {
                            tank.fill(FluidStack(desiredLiquid, (itemCount - 1) * Fluid.BUCKET_VOLUME), true)
                        }
                        stacks.set(slot, ItemStack(Items.BUCKET, itemCount))
                        return
                    } else if (itemCount == 1) {
                        val handlerItem: IFluidHandlerItem? = FluidUtil.getFluidHandler(slotItem)
                        if (handlerItem != null) {
                            val used: Int = tank.fill(FluidStack(desiredLiquid, amountOffered), false)
                            val given: FluidStack? = handlerItem.drain(used, true)
                            if (given != null) {
                                tank.fill(FluidStack(desiredLiquid, given.amount), true)
                                stacks.set(slot, handlerItem.getContainer())
                            }
                            return
                        }
                        //Fallback basic treatment
                        val emptyStack: ItemStack = getUsedContainer(slotItem)
                        tank.fill(FluidStack(desiredLiquid, getContainerCapacity(slotItem)), true)
                        stacks.set(slot, emptyStack)
                        return
                    }
                }
            }
        }
    }

    /**
     * Tests for any type of container with some space in it
     * It can be either an empty container, or a Galacticraft canister
     * of the appropriate type, either empty or at least with some capacity remaining
     * Will return true for any type of empty Forge fluid container including vanilla buckets
     *
     * @param var4
     * @param canisterType
     * @return
     */
    @JvmStatic
    fun isPartialContainer(var4: ItemStack, canisterType: Item?): Boolean {
        if (var4.getItem() is ItemCanisterGeneric) {
            return var4.getItemDamage() == ItemCanisterGeneric.EMPTY || (var4.getItem() === canisterType && var4.getItemDamage() > 1)
        }

        val handler: IFluidHandlerItem? = FluidUtil.getFluidHandler(var4)
        var containedFluid: FluidStack? = FluidUtil.getFluidContained(var4)

        if (handler != null) {
            if (containedFluid == null) {
                return true
            }

            // Check that there is at least some space in container
            containedFluid = containedFluid.copy()
            containedFluid.amount = 1
            if (handler.fill(containedFluid, false) > 0) {
                return true
            }
        }

        return false
    }

    /**
     * Tests for any empty container which can accept the specified fluid
     * Either Galacticraft canisters or Forge containers
     *
     * @param var4
     * @param targetFluid
     * @return
     */
    @JvmStatic
    fun isEmptyContainerFor(var4: ItemStack, targetFluid: FluidStack?): Boolean {
        if (var4.getItem() is ItemCanisterGeneric) {
            if (var4.getItemDamage() == ItemCanisterGeneric.EMPTY) {
                return true
            }
            if (var4.getItemDamage() == 1) {
                return false
            }

            return fluidsSame(getFluidContained(var4), targetFluid)
        }

        if (isEmptyContainer(var4)) {
            return true
        }

        return fluidsSame(FluidUtil.getFluidContained(var4), targetFluid)
    }

    /**
     * Returns the amount of fluid this container can provide in its current state
     * (Similar to Forge's FluidUtil.getFluidContained())
     *
     * @param container
     * @return
     */
    @JvmStatic
    fun getContainerCapacity(container: ItemStack): Int {
        var container: ItemStack = container
        if (container.getItem() is ItemCanisterGeneric) {
            return container.getItemDamage() - 1
        }
        if (!container.isEmpty()) {
            container = container.copy()
            container.setCount(1)
            val fluidHandler: IFluidHandlerItem? = FluidUtil.getFluidHandler(container)
            if (fluidHandler != null) {
                val drain: FluidStack? = fluidHandler.drain(Int.MAX_VALUE, false)
                return if (drain == null) 0 else drain.amount
            }
        }
        return 0
    }

    /**
     * @param fs1 First FluidStack to compare
     * @param fs2 Second FluidStack to compare
     * @return True if the FluidStacks are both not null and the same fluid type
     * False otherwise
     */
    @JvmStatic
    fun fluidsSame(fs1: FluidStack?, fs2: FluidStack?): Boolean {
        if (fs1 == null || fs2 == null) {
            return false
        }
        val f1: Fluid? = fs1.getFluid()
        val f2: Fluid? = fs2.getFluid()
        if (f1 == null || f2 == null || f1.getName() == null) {
            return false
        }
        return f1.getName() == f2.getName()
    }

    @JvmStatic
    fun fluidsSame(f1: Fluid?, f2: Fluid?, fuzzy: Boolean): Boolean {
        if (f1 == null || f2 == null || f1.getName() == null) {
            return false
        }
        if (fuzzy) {
            if (f1.getName().startsWith("fuel")) {
                return testFuel(f2.getName())
            }
            if (f1.getName().startsWith("oil")) {
                return f2.getName().startsWith("oil")
            }
            if (f1.getName().startsWith("water")) {
                return f2.getName().startsWith("water")
            }
        }
        return f1.getName() == f2.getName()
    }

    @JvmStatic
    fun fluidsSame(f1: Fluid?, name2: String?, fuzzy: Boolean): Boolean {
        if (f1 == null || name2 == null || f1.getName() == null) {
            return false
        }
        if (fuzzy) {
            if (name2 == "fuel") {
                return testFuel(f1.getName())
            }

            return f1.getName().startsWith(name2)
        }
        return f1.getName() == name2
    }

    /**
     * Test for any completely empty container of either type
     * Used, for example, in canExtractItem() logic
     *
     * @param var4 The ItemStack being tested
     * @return True if the container is empty
     */
    @JvmStatic
    fun isEmptyContainer(var4: ItemStack): Boolean {
        if (var4.getItem() is ItemCanisterGeneric) {
            return var4.getItemDamage() == ItemCanisterGeneric.EMPTY
        }

        if (var4.getItem() === Items.BUCKET) {
            return true
        }

        val handler: IFluidHandlerItem? = FluidUtil.getFluidHandler(var4)
        val containedFluid: FluidStack? = FluidUtil.getFluidContained(var4)
        return handler != null && (containedFluid == null || containedFluid.amount == 0)
    }

    @JvmStatic
    fun isEmptyGasContainer(var4: ItemStack?): Boolean {
        return false
    }

    /**
     * Test for any container with some level of contents
     * Used, for example, in shift-clicking items into input item slots
     *
     * @param var4 The ItemStack being tested
     * @return True if the container contains something
     */
    @JvmStatic
    fun isFilledContainer(var4: ItemStack): Boolean {
        if (FluidUtil.getFluidHandler(var4) == null) {
            return false
        }
        if (var4.getItem() is ItemCanisterGeneric) {
            return var4.getItemDamage() < ItemCanisterGeneric.EMPTY
        }
        return FluidUtil.getFluidContained(var4) != null
    }

    /**
     * Test for any container with water
     * (vanilla or Forge items only, Galacticraft does not have a water container)
     *
     * @param var4 The ItemStack being tested
     * @return True if the container contains water
     */
    @JvmStatic
    fun isWaterContainer(var4: ItemStack): Boolean {
        val liquid: FluidStack? = FluidUtil.getFluidContained(var4)
        return liquid != null && liquid.getFluid() != null && liquid.getFluid().getName() == "water"
    }


    @JvmStatic
    fun getFluidContained(container: ItemStack?): FluidStack? {
        if (container == null) {
            return null
        }

        if (container.getItem() is ItemCanisterGeneric) {
            val canister: ItemCanisterGeneric = container.getItem() as ItemCanisterGeneric
            return canister.getFluid(container)
        }

        if (container.getItem() is ItemBucketGC) {
            return FluidStack((container.getItem() as ItemBucketGC).accepts, Fluid.BUCKET_VOLUME)
        }

        return FluidUtil.getFluidContained(container)
    }

    /**
     * Test for any container type at all
     * Used, for example, in isItemValidForSlot() logic
     *
     * @param slotItem
     * @return True if it is a container; False if it is null or not a container
     */
    @JvmStatic
    fun isValidContainer(slotItem: ItemStack?): Boolean {
        return slotItem != null && slotItem.getCount() == 1 && (slotItem.getItem() is ItemCanisterGeneric || slotItem.getItem() is ItemBucketGC || FluidUtil.getFluidHandler(
            slotItem
        ) != null)
    }

    /**
     * Returns the used (empty) container, for example an empty bucket
     * Used, for example, in isItemValidForSlot() logic
     *
     * @param container
     * @return True if it is a container; False if it is null or not a container
     */
    @JvmStatic
    fun getUsedContainer(container: ItemStack): ItemStack {
        val handler: IFluidHandlerItem? = FluidUtil.getFluidHandler(container)
        if (isBucket(container) && handler != null) {
            return ItemStack(Items.BUCKET, container.getCount())
        } else if (handler != null) {
            handler.drain(Int.MAX_VALUE, true)
            return handler.getContainer()
        } else {
            container.shrink(1)

            if (!container.isEmpty()) {
                return ItemStack.EMPTY
            }

            return container
        }
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun isInsideOfFluid(entity: Entity, fluid: Fluid): Boolean {
        val d0: Double = entity.posY + entity.getEyeHeight()
        val i: Int = MathHelper.floor(entity.posX)
        val j: Int = MathHelper.floor(MathHelper.floor(d0).toFloat())
        val k: Int = MathHelper.floor(entity.posZ)
        val pos: BlockPos = BlockPos(i, j, k)
        val block: Block = entity.world.getBlockState(pos).getBlock()

        if (block != null && block is IFluidBlock && (block as IFluidBlock).getFluid() != null && (block as IFluidBlock).getFluid()
                .getName() == fluid.getName()
        ) {
            var filled: Double = (block as IFluidBlock).getFilledPercentage(entity.world, pos).toDouble()
            if (filled < 0) {
                filled *= -1.0
                return d0 > j + (1 - filled)
            } else {
                return d0 < j + filled
            }
        } else {
            return false
        }
    }

    @JvmStatic
    fun isBucket(container: ItemStack?): Boolean {
        if (container == null) {
            return false
        }

        val handlerItem: IFluidHandlerItem? = FluidUtil.getFluidHandler(container)
        if (handlerItem != null) {
            if (container.getItem() === Items.BUCKET) {
                return true
            }

            if (handlerItem.getContainer().getItem() === Items.BUCKET) {
                return true
            }
        }

        return false
    }

    @JvmStatic
    @Nonnull
    fun interactWithFluidHandler(
        @Nonnull container: ItemStack,
        fluidHandler: IFluidHandler?,
        player: EntityPlayer?
    ): FluidActionResult {
        if (container.isEmpty() || fluidHandler == null || player == null) {
            return FluidActionResult.FAILURE
        }

        //-----------Special code for canisters onto FluidTanks------------
        //This is NOT strictly necessary, as our 1.11 code for canisters implements IFluidHandler 100% correctly
        //
        //But our code offers three improvements over Forge:
        // 1: Performance :)
        // 2: Tries to empty partial canister BEFORE filling it, that seems more natural
        // 3: Forge's behaviour in Creative Mode with partial canisters is not a bug, but it still feels wrong
        //
        if (container.getItem() is ItemCanisterGeneric) {
            var result: ItemStack
            if ((tryEmptyCanister(container, fluidHandler, player.capabilities.isCreativeMode).also {
                    result = it
                }) != ItemStack.EMPTY || (tryFillCanister(
                    container,
                    fluidHandler,
                    player.capabilities.isCreativeMode
                ).also { result = it }) != ItemStack.EMPTY
            ) {
                // send inventory updates to client
                if (player.inventoryContainer != null) {
                    player.inventoryContainer.detectAndSendChanges()
                }
                return FluidActionResult(result)
            }

            return FluidActionResult.FAILURE
        }

        //Forge's UniversalBucket appears to be non-functional, currently, for FILLING modded buckets
        //Try to fill an empty vanilla bucket:
        if (container.getItem() === Items.BUCKET) {
            var result: ItemStack? = ItemBucketGC.fillBucketFrom(fluidHandler)
            if (result != null) {
                //Handle stacks of buckets: insert the filled bucket into another inventory slot or drop it
                if (container.getCount() > 1) {
                    val remainder: ItemStack =
                        ItemHandlerHelper.insertItemStacked(PlayerMainInvWrapper(player.inventory), (result)!!, false)
                    if (!remainder.isEmpty() && player != null) {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder)
                    }
                    result = container.copy()
                    ((result)!!.shrink(1))!!
                }
                return FluidActionResult((result)!!)
            }
            //If failure, fall through to other Forge methods
        }


        //---------the rest of this is standard Forge code from interactWithFluidHandler()---------
        //-----------(but without the InvWrapper bug which could put fluids in armor slots!)-------
        val playerInventory: IItemHandler = PlayerMainInvWrapper(player.inventory)

        val fillResult: FluidActionResult =
            FluidUtil.tryFillContainerAndStow(container, fluidHandler, playerInventory, Int.MAX_VALUE, player)
        if (fillResult.isSuccess()) {
            return fillResult
        } else {
            return FluidUtil.tryEmptyContainerAndStow(container, fluidHandler, playerInventory, Int.MAX_VALUE, player)
        }
    }

    @JvmStatic
    private fun tryFillCanister(canister: ItemStack, tank: IFluidHandler, isCreativeMode: Boolean): ItemStack {
        val currCapacity: Int = canister.getItemDamage() - 1
        if (currCapacity <= 0) {
            return ItemStack.EMPTY
        }
        var liquid: FluidStack? = tank.drain(currCapacity, false)
        val transferred: Int = (canister.getItem() as ItemCanisterGeneric).fill(canister, liquid, !isCreativeMode)
        if (transferred > 0) {
            liquid = tank.drain(transferred, true)
            return canister
        }
        return ItemStack.EMPTY
    }

    @JvmStatic
    private fun tryEmptyCanister(canister: ItemStack, tank: IFluidHandler, isCreativeMode: Boolean): ItemStack {
        val currContents: Int = ItemCanisterGeneric.EMPTY - canister.getItemDamage()
        if (currContents <= 0) {
            return ItemStack.EMPTY
        }
        val liquid: FluidStack? = (canister.getItem() as ItemCanisterGeneric).drain(canister, currContents, false)
        val transferred: Int = tank.fill(liquid, true)
        if (transferred > 0) {
            (canister.getItem() as ItemCanisterGeneric).drain(canister, transferred, !isCreativeMode)
            return canister
        }
        return ItemStack.EMPTY
    }
}
