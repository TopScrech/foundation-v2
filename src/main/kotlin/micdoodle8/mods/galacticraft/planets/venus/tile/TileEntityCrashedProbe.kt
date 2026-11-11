package micdoodle8.mods.galacticraft.planets.venus.tile

import net.minecraft.world.ContainerHelper
import foundation.persistence.containsTag

import java.util.Random

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.NonNullList
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootTable
import java.util.*

open class TileEntityCrashedProbe : TileEntity(), IInventoryDefaults {
    override fun getDisplayName(): net.minecraft.network.chat.Component = getName()

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
        if (nbt.containsTag("ctd")) {
            this.hasCoreToDrop = nbt.getBooleanOr("ctd", false)
        } else {
            this.hasCoreToDrop = true //Legacy compatibility with worlds generated before this key used
        }

        if (!this.checkLootAndRead(nbt)) {
            this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
            ItemStackHelper.loadAllItems(nbt, this.stacks)
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putBoolean("ctd", this.hasCoreToDrop)

        if (!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems(nbt, this.stacks)
        }
        return nbt
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.crashed_probe.name")
    }

    override fun getContainerSize(): Int {
        return this.stacks.size
    }

    override fun getItem(var1: Int): ItemStack {
        this.fillWithLoot(null)
        return this.stacks.get(var1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        this.fillWithLoot(null)
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        this.fillWithLoot(null)
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
        this.fillWithLoot(null)
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun clearContent() {
        fillWithLoot(null)
        stacks.clear()
        setChanged()
    }

    override fun stillValid(player: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && player.getDistanceSq(
            this.getPos().getX() + 0.5,
            this.getPos().getY() + 0.5,
            this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
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
        if (compound.containsTag("LootTable", 8)) {
            this.lootTable = ResourceLocation(compound.getStringOr("LootTable", ""))
            this.lootTableSeed = compound.getLongOr("LootTableSeed", 0L)
            return true
        } else {
            return false
        }
    }

    protected open fun checkLootAndWrite(compound: NBTTagCompound): Boolean {
        if (this.lootTable != null) {
            compound.putString("LootTable", this.lootTable.toString())

            if (this.lootTableSeed != 0L) {
                compound.putLong("LootTableSeed", this.lootTableSeed)
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
