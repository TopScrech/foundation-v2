package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockParaChest
import micdoodle8.mods.galacticraft.core.entities.IScaleableFuelLevel
import micdoodle8.mods.galacticraft.core.inventory.ContainerParaChest
import micdoodle8.mods.galacticraft.core.inventory.IInventorySettable
import micdoodle8.mods.galacticraft.core.network.PacketDynamicInventory
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fml.relauncher.Side

open class TileEntityParaChest constructor() : TileEntityAdvanced(), IInventorySettable, IScaleableFuelLevel {
    private val tankCapacity = 5000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var fuelTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(3, ItemStack.EMPTY)

    @JvmField
    var adjacentChestChecked: Boolean = false

    @JvmField
    var lidAngle: Float = 0f

    @JvmField
    var prevLidAngle: Float = 0f

    @JvmField
    var numUsingPlayers: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var color: EnumDyeColor = EnumDyeColor.RED

    init {
        this.color = EnumDyeColor.RED
    }

    override fun onLoad() {
        if (this.world.isRemote) {
            //Request size + contents information from server
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamicInventory(this))
        }
    }

    override fun getScaledFuelLevel(i: Int): Int {
        val fuelLevel: Double =
            (if (this.fuelTank.getFluid() == null) 0 else this.fuelTank.getFluid()!!.amount).toDouble()

        return (fuelLevel * i / this.tankCapacity).toInt()
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    override fun getStackInSlot(index: Int): ItemStack {
        return this.stacks.get(index)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
        }

        return itemstack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.markDirty()
        }
        return oldstack
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.markDirty()
    }

    override fun setSizeInventory(size: Int) {
        this.stacks = NonNullList.withSize<ItemStack>(size, ItemStack.EMPTY)
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.parachest.name")
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        var size: Int = nbt.getInteger("chestContentLength")
        if ((size - 3) % 18 != 0) {
            size += 18 - ((size - 3) % 18)
        }
        this.stacks = NonNullList.withSize<ItemStack>(size, ItemStack.EMPTY)

        ItemStackHelper.loadAllItems(nbt, this.stacks)

        if (nbt.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(nbt.getCompoundTag("fuelTank"))
        }

        if (nbt.hasKey("color")) {
            this.color = EnumDyeColor.byDyeDamage(nbt.getInteger("color"))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        nbt.setInteger("chestContentLength", this.stacks.size)
        ItemStackHelper.saveAllItems(nbt, this.stacks)

        if (this.fuelTank.getFluid() != null) {
            nbt.setTag("fuelTank", this.fuelTank.writeToNBT(NBTTagCompound()))
        }

        nbt.setInteger("color", this.color.getDyeDamage())
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun updateContainingBlockInfo() {
        super.updateContainingBlockInfo()
        this.adjacentChestChecked = false
    }

    override fun update() {
        super.update()
        var f: Float

        if (!this.world.isRemote && this.numUsingPlayers != 0 && (this.ticks + this.getPos().getX() + this.getPos()
                .getY() + this.getPos().getZ()) % 200 == 0
        ) {
            this.numUsingPlayers = 0
            f = 5.0f
            val list: MutableList<*> = this.world.getEntitiesWithinAABB<EntityPlayer>(
                EntityPlayer::class.java,
                AxisAlignedBB(
                    (this.getPos().getX() - f).toDouble(),
                    (this.getPos().getY() - f).toDouble(),
                    (this.getPos().getZ() - f).toDouble(),
                    (this.getPos().getX() + 1 + f).toDouble(),
                    (this.getPos().getY() + 1 + f).toDouble(),
                    (this.getPos().getZ() + 1 + f).toDouble()
                )
            )
            val iterator: MutableIterator<*> = list.iterator()

            while (iterator.hasNext()) {
                val entityplayer: EntityPlayer = iterator.next() as EntityPlayer

                if (entityplayer.openContainer is ContainerParaChest) {
                    ++this.numUsingPlayers
                }
            }
        }

        this.prevLidAngle = this.lidAngle
        f = 0.1f
        var d0: Double

        if (this.numUsingPlayers > 0 && this.lidAngle == 0.0f) {
            val d1: Double = this.getPos().getX() + 0.5
            d0 = this.getPos().getZ() + 0.5

            this.world.playSound(
                null,
                d1,
                this.getPos().getY() + 0.5,
                d0,
                SoundEvents.BLOCK_CHEST_OPEN,
                SoundCategory.BLOCKS,
                0.5f,
                this.world.rand.nextFloat() * 0.1f + 0.9f
            )
        }

        if (this.numUsingPlayers == 0 && this.lidAngle > 0.0f || this.numUsingPlayers > 0 && this.lidAngle < 1.0f) {
            val f1: Float = this.lidAngle

            if (this.numUsingPlayers > 0) {
                this.lidAngle += f
            } else {
                this.lidAngle -= f
            }

            if (this.lidAngle > 1.0f) {
                this.lidAngle = 1.0f
            }

            val f2: Float = 0.5f

            if (this.lidAngle < f2 && f1 >= f2) {
                d0 = this.getPos().getX() + 0.5
                val d2: Double = this.getPos().getZ() + 0.5

                this.world.playSound(
                    null,
                    d0,
                    this.getPos().getY() + 0.5,
                    d2,
                    SoundEvents.BLOCK_CHEST_CLOSE,
                    SoundCategory.BLOCKS,
                    0.5f,
                    this.world.rand.nextFloat() * 0.1f + 0.9f
                )
            }

            if (this.lidAngle < 0.0f) {
                this.lidAngle = 0.0f
            }
        }

        if (!this.world.isRemote) {
            this.checkFluidTankTransfer(this.stacks.size - 1, this.fuelTank)
        }
    }

    private fun checkFluidTankTransfer(slot: Int, tank: FluidTank) {
        FluidUtil.tryFillContainerFuel(tank, this.stacks, slot)
    }


    override fun receiveClientEvent(par1: Int, par2: Int): Boolean {
        if (par1 == 1) {
            this.numUsingPlayers = par2
            return true
        } else {
            return super.receiveClientEvent(par1, par2)
        }
    }

    override fun openInventory(player: EntityPlayer) {
        if (this.numUsingPlayers < 0) {
            this.numUsingPlayers = 0
        }

        ++this.numUsingPlayers
        this.world.addBlockEvent(this.getPos(), this.getBlockType(), 1, this.numUsingPlayers)
        this.world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), false)
        this.world.notifyNeighborsOfStateChange(this.getPos().down(), this.getBlockType(), false)
    }

    override fun closeInventory(player: EntityPlayer) {
        if (this.getBlockType() != null && this.getBlockType() is BlockParaChest) {
            --this.numUsingPlayers
            this.world.addBlockEvent(this.getPos(), this.getBlockType(), 1, this.numUsingPlayers)
            this.world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), false)
            this.world.notifyNeighborsOfStateChange(this.getPos().down(), this.getBlockType(), false)
        }
    }

    override fun isItemValidForSlot(par1: Int, par2ItemStack: ItemStack): Boolean {
        return true
    }

    override fun invalidate() {
        super.invalidate()
        this.updateContainingBlockInfo()
    }

    override fun getPacketRange(): Double {
        return 12.0
    }

    override fun getPacketCooldown(): Int {
        return 3
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }

    override fun getField(id: Int): Int {
        return 0
    }

    override fun setField(id: Int, value: Int) {
    }

    override fun getFieldCount(): Int {
        return 0
    }

    override fun clear() {
    }

    override fun getDisplayName(): ITextComponent? {
        return (if (this.hasCustomName()) TextComponentString(this.getName()) else TextComponentTranslation(
            this.getName(),
            *arrayOfNulls<Any>(0)
        ))
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        val color: EnumDyeColor = this.color

        super.decodePacketdata(buffer)

        if (this.world.isRemote && color != this.color) {
            this.world.markBlockRangeForRenderUpdate(getPos(), getPos())
        }
    }
}
