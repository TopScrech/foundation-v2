package micdoodle8.mods.galacticraft.core.tile

import net.minecraft.world.ContainerHelper
import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCollector
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.BlockAir
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.LogicalSide as Side
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

open class TileEntityOxygenCollector constructor() : TileEntityOxygen(6000, 0), IInventoryDefaults,
    ISidedInventory {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileEntityOxygen>.getDisplayName()

    @JvmField
    var active: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var lastOxygenCollected: Float = 0f
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)
    private var noAtmosphericOxygen = true
    private var isInitialised = false
    private var producedLastTick = false

    init {
        this.noRedstoneControl = true
    }

    override fun getCappedScaledOxygenLevel(scale: Int): Int {
        return max(
            min(
                floor(this.getOxygenStored().toDouble() / this.getMaxOxygenStored().toDouble() * scale),
                scale.toDouble()
            ), 0.0
        ).toInt()
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            producedLastTick = this.getOxygenStored() < this.getMaxOxygenStored()

            this.produceOxygen()

            // if (this.getEnergyStored() > 0)
            // {
            // int gasToSend = Math.min(this.storedOxygen,
            // GCCoreTileEntityOxygenCollector.OUTPUT_PER_TICK);
            // GasStack toSend = new GasStack(GalacticraftCore.gasOxygen,
            // gasToSend);
            // this.storedOxygen -= GasTransmission.emitGasToNetwork(toSend,
            // this, this.getOxygenOutputDirection());
            //
            // Vector3 thisVec = new Vector3(this);
            // TileEntity tileEntity =
            // thisVec.modifyPositionFromSide(this.getOxygenOutputDirection()).getTileEntity(this.world);
            //
            // if (tileEntity instanceof IGasAcceptor)
            // {
            // if (((IGasAcceptor)
            // tileEntity).canReceiveGas(this.getOxygenOutputDirection().getOpposite(),
            // GalacticraftCore.gasOxygen))
            // {
            // double sendingGas = 0;
            //
            // if (this.storedOxygen >=
            // GCCoreTileEntityOxygenCollector.OUTPUT_PER_TICK)
            // {
            // sendingGas = GCCoreTileEntityOxygenCollector.OUTPUT_PER_TICK;
            // }
            // else
            // {
            // sendingGas = this.storedOxygen;
            // }
            //
            // this.storedOxygen -= sendingGas - ((IGasAcceptor)
            // tileEntity).receiveGas(new GasStack(GalacticraftCore.gasOxygen,
            // (int) Math.floor(sendingGas)));
            // }
            // }
            // }

            //Approximately once every 40 ticks, search out oxygen producing blocks
            if (this.world.rand.nextInt(10) == 0) {
                if (this.hasEnoughEnergyToRun) {
                    // The later calculations are more efficient if power is a float, so
                    // there are fewer casts
                    var nearbyLeaves: Float = 0f

                    if (!this.isInitialised) {
                        this.noAtmosphericOxygen =
                            (this.world.provider is IGalacticraftWorldProvider && !(this.world.provider as IGalacticraftWorldProvider).isGasPresent(
                                EnumAtmosphericGas.OXYGEN
                            ))
                        this.isInitialised = true
                    }

                    if (this.noAtmosphericOxygen) {
                        // Pre-test to see if close to the map edges, so code
                        // doesn't have to continually test for map edges inside the
                        // loop
                        if (this.getPos().getX() > -29999995 && this.getPos().getY() < 2999995 && this.getPos()
                                .getZ() > -29999995 && this.getPos().getZ() < 29999995
                        ) {
                            // Test the y coordinates, so code doesn't have to keep
                            // testing that either
                            var miny: Int = this.getPos().getY() - 5
                            var maxy: Int = this.getPos().getY() + 5
                            if (miny < 0) {
                                miny = 0
                            }
                            if (maxy >= this.world.getHeight()) {
                                maxy = this.world.getHeight() - 1
                            }

                            // Loop the x and the z first, so the y loop will be at
                            // fixed (x,z) coordinates meaning fixed chunk
                            // coordinates
                            for (x: Int in this.getPos().getX() - 5..this.getPos().getX() + 5) {
                                val chunkx: Int = x shr 4
                                val intrachunkx: Int = x and 15
                                // Preload the first chunk for the z loop - there
                                // can be a maximum of 2 chunks in the z loop
                                var chunkz: Int = this.getPos().getZ() - 5 shr 4
                                var chunk: Chunk = this.world.getChunk(chunkx, chunkz)
                                for (z: Int in this.getPos().getZ() - 5..this.getPos().getZ() + 5) {
                                    if (z shr 4 != chunkz) {
                                        // moved across z chunk boundary into a new
                                        // chunk, so load the new chunk
                                        chunkz = z shr 4
                                        chunk = this.world.getChunk(chunkx, chunkz)
                                    }
                                    for (y: Int in miny..maxy) {
                                        // chunk.getBlockID is like world.getBlock
                                        // but faster - needs to be given
                                        // intra-chunk coordinates though
                                        val state: IBlockState = chunk.getBlockState(intrachunkx, y, z and 15)
                                        // Test for the two most common blocks (air
                                        // and breatheable air) without looking up
                                        // in the blocksList
                                        if (state.getBlock() !is BlockAir) {
                                            val pos: BlockPos = BlockPos(x, y, z)
                                            if (state.getBlock().isLeaves(
                                                    state,
                                                    this.world,
                                                    pos
                                                ) || state.getBlock() is IPlantable && (state.getBlock() as IPlantable).getPlantType(
                                                    this.world,
                                                    pos
                                                ) == EnumPlantType.Crop
                                            ) {
                                                nearbyLeaves += 0.075f * 10f
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        nearbyLeaves = 9.3f * 10f
                    }

                    nearbyLeaves = floor(nearbyLeaves.toDouble()).toFloat()

                    this.lastOxygenCollected = nearbyLeaves / 10f

                    this.tank.setFluid(
                        FluidStack(
                            GCFluids.fluidOxygenGas,
                            max(
                                min(this.getOxygenStored() + nearbyLeaves, this.getMaxOxygenStored().toFloat()),
                                0f
                            ).toInt()
                        )
                    )
                } else {
                    this.lastOxygenCollected = 0f
                }
            }
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        ItemStackHelper.saveAllItems(nbt, this.stacks)

        return nbt
    }

    override fun getContainerSize(): Int {
        return this.stacks.size
    }

    override fun getItem(var1: Int): ItemStack {
        return this.stacks.get(var1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        val oldstack: ItemStack = ContainerHelper.takeItem(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.setChanged()
        }
        return oldstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.oxygencollector.name")
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return slotID == 0
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem())
    }

    override fun shouldUseEnergy(): Boolean {
        return this.getOxygenStored() > 0f && producedLastTick
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockOxygenCollector) {
            return state.getValue(BlockOxygenCollector.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getItem(0)
    }

    override fun shouldPullOxygen(): Boolean {
        return false
    }

    //    @Override
    //    public boolean canReceiveGas(EnumFacing side, Gas type)
    //    {
    //    	return false;
    //    }
    //
    //    @Override
    //    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer)
    //    {
    //    	return 0;
    //    }
    //
    //    @Override
    //    public int receiveGas(EnumFacing side, GasStack stack)
    //    {
    //    	return 0;
    //    }
    override fun shouldUseOxygen(): Boolean {
        return false
    }

    override fun getOxygenInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun getOxygenOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(this.getElectricInputDirection().getOpposite())
    }

    override fun getOxygenProvide(direction: EnumFacing?): Int {
        return if (this.getOxygenOutputDirections()
                .contains(direction)
        ) min(TileEntityOxygenStorageModule.OUTPUT_PER_TICK, this.getOxygenStored()) else 0
    }

    companion object {
        const val OUTPUT_PER_TICK: Int = 100
    }
}
