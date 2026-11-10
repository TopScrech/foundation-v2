package micdoodle8.mods.galacticraft.planets.venus.tile

import java.util.Random

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootTable
import java.util.*

open class TileEntityCrashedProbe : TileEntity(), IInventoryDefaults {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileEntity>.getDisplayName()

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(6, ItemStack.EMPTY)
    private var hasCoreToDrop = false

    @JvmField
    protected var lootTable: ResourceLocation? = null

    @JvmField
    protected var lootTableSeed: Long = 0

    protected open fun fillWithLoot(player: EntityPlayer?) {
        if (this.lootTable != null) {
            val loottable: LootTable = this.world.getLootTableManager().getLootTableFromLocation(this.lootTable)
            this.lootTable = null
            val random: Random

            if (this.lootTableSeed == 0L) {
                random = Random()
            } else {
                random = Random(this.lootTableSeed)
            }

            val `lootcontext$builder`: LootContext.Builder = LootContext.Builder(this.world as WorldServer?)

            if (player != null) {
                `lootcontext$builder`.withLuck(player.getLuck())
            }

            loottable.fillInventory(this, random, `lootcontext$builder`.build())
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        if (nbt.hasKey("ctd")) {
            this.hasCoreToDrop = nbt.getBoolean("ctd")
        } else {
            this.hasCoreToDrop = true //Legacy compatibility with worlds generated before this key used
        }

        if (!this.checkLootAndRead(nbt)) {
            this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
            ItemStackHelper.loadAllItems(nbt, this.stacks)
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setBoolean("ctd", this.hasCoreToDrop)

        if (!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems(nbt, this.stacks)
        }
        return nbt
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.crashed_probe.name")
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    override fun getStackInSlot(var1: Int): ItemStack {
        this.fillWithLoot(null)
        return this.stacks.get(var1)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        this.fillWithLoot(null)
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
        }

        return itemstack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        this.fillWithLoot(null)
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

    override fun isEmpty(): Boolean {
        this.fillWithLoot(null)
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun clear() {
        this.fillWithLoot(null)
    }

    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && player.getDistanceSq(
            this.getPos().getX() + 0.5,
            this.getPos().getY() + 0.5,
            this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        return true
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }

    open fun setDropCore() {
        this.hasCoreToDrop = true
    }

    open fun getDropCore(): Boolean {
        return this.hasCoreToDrop
    }

    protected open fun checkLootAndRead(compound: NBTTagCompound): Boolean {
        if (compound.hasKey("LootTable", 8)) {
            this.lootTable = ResourceLocation(compound.getString("LootTable"))
            this.lootTableSeed = compound.getLong("LootTableSeed")
            return true
        } else {
            return false
        }
    }

    protected open fun checkLootAndWrite(compound: NBTTagCompound): Boolean {
        if (this.lootTable != null) {
            compound.setString("LootTable", this.lootTable.toString())

            if (this.lootTableSeed != 0L) {
                compound.setLong("LootTableSeed", this.lootTableSeed)
            }

            return true
        } else {
            return false
        }
    }

    open fun setLootTable(lootTable: ResourceLocation?, lootTableSeed: Long) {
        this.lootTable = lootTable
        this.lootTableSeed = lootTableSeed
    }
}
