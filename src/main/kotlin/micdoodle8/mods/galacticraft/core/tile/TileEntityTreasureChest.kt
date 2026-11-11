package micdoodle8.mods.galacticraft.core.tile

import net.minecraft.network.chat.Component

import net.minecraft.world.ContainerHelper
import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.api.item.IKeyable
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.inventory.ContainerChest
import net.minecraft.world.Container as IInventory
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.InventoryLargeChest
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.core.NonNullList
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.network.chat.Component as ITextComponent


import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootTable
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class TileEntityTreasureChest @JvmOverloads constructor(tier: Int = 1) : TileEntityAdvanced(), ITickable,
    IInventory, IKeyable, ISidedInventory {
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(27, ItemStack.EMPTY)

    @JvmField
    var adjacentChestChecked: Boolean = false

    @JvmField
    var lidAngle: Float = 0f

    @JvmField
    var prevLidAngle: Float = 0f

    @JvmField
    var numPlayersUsing: Int = 0
    private var ticksSinceSync = 0
    private var renderAABB: AxisAlignedBB? = null

    @JvmField
    protected var lootTable: ResourceLocation? = null

    @JvmField
    protected var lootTableSeed: Long = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var locked: Boolean = true

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var tier: Int = 1

    init {
        this.tier = tier
    }

    override fun getContainerSize(): Int {
        return 27
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

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    override fun getName(): net.minecraft.network.chat.Component {
        return Component.translatable("container.treasurechest.name")
    }

    /**
     * Returns true if this thing is named
     */
    override fun hasCustomName(): Boolean {
        return false
    }

    open fun setCustomName(name: String?) {
    }


    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.locked = nbt.getBooleanOr("isLocked", false)
        this.tier = nbt.getIntOr("tier", 0)

        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)

        checkLootAndRead(nbt)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putBoolean("isLocked", this.locked)
        nbt.putInt("tier", this.tier)
        ItemStackHelper.saveAllItems(nbt, this.stacks)

        checkLootAndWrite(nbt)

        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    override fun getMaxStackSize(): Int {
        return 64
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    override fun stillValid(player: EntityPlayer): Boolean {
        return if (this.world.getBlockEntity(this.pos) !== this) false else player.getDistanceSq(
            this.pos.getX().toDouble() + 0.5, this.pos.getY().toDouble() + 0.5, this.pos.getZ().toDouble() + 0.5
        ) <= 64.0
    }

    override fun updateContainingBlockInfo() {
        super.updateContainingBlockInfo()
        this.adjacentChestChecked = false
    }

    /**
     * Updates the JList with a new model.
     */
    override fun update() {
        val i: Int = this.pos.getX()
        val j: Int = this.pos.getY()
        val k: Int = this.pos.getZ()
        ++this.ticksSinceSync
        var f: Float

        if (this.locked) {
            this.numPlayersUsing = 0
        }

        if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
            this.numPlayersUsing = 0
            f = 5.0f
            val list: MutableList<*> = this.world.getEntitiesWithinAABB<EntityPlayer>(
                EntityPlayer::class.java,
                AxisAlignedBB(
                    (i.toFloat() - f).toDouble(),
                    (j.toFloat() - f).toDouble(),
                    (k.toFloat() - f).toDouble(),
                    ((i + 1).toFloat() + f).toDouble(),
                    ((j + 1).toFloat() + f).toDouble(),
                    ((k + 1).toFloat() + f).toDouble()
                )
            )
            val iterator: MutableIterator<*> = list.iterator()

            while (iterator.hasNext()) {
                val entityplayer: EntityPlayer = iterator.next() as EntityPlayer

                if (entityplayer.openContainer is ContainerChest) {
                    val iinventory: IInventory = (entityplayer.openContainer as ContainerChest).getLowerChestInventory()

                    if (iinventory === this || iinventory is InventoryLargeChest && iinventory.isPartOfLargeChest(this)) {
                        ++this.numPlayersUsing
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle
        f = 0.1f
        var d2: Double

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0f) {
            val d1: Double = i.toDouble() + 0.5
            d2 = k.toDouble() + 0.5

            this.world.playSound(
                null,
                d1,
                j.toDouble() + 0.5,
                d2,
                SoundEvents.BLOCK_CHEST_OPEN,
                SoundCategory.BLOCKS,
                0.5f,
                this.world.rand.nextFloat() * 0.1f + 0.9f
            )
        }

        if (((this.numPlayersUsing == 0 || this.locked) && this.lidAngle > 0.0f) || this.numPlayersUsing > 0 && this.lidAngle < 1.0f) {
            val f1: Float = this.lidAngle

            if (this.numPlayersUsing == 0 || this.locked) {
                this.lidAngle -= f
            } else {
                this.lidAngle += f
            }

            if (this.lidAngle > 1.0f) {
                this.lidAngle = 1.0f
            }

            val f2: Float = 0.5f

            if (this.lidAngle < f2 && f1 >= f2) {
                d2 = i.toDouble() + 0.5
                val d0: Double = k.toDouble() + 0.5

                this.world.playSound(
                    null,
                    d2,
                    j.toDouble() + 0.5,
                    d0,
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

        super.update()
    }

    override fun receiveClientEvent(id: Int, type: Int): Boolean {
        if (id == 1) {
            this.numPlayersUsing = type
            return true
        } else {
            return super.receiveClientEvent(id, type)
        }
    }

    override fun startOpen(player: net.minecraft.world.entity.ContainerUser) {
        if (!player.getLivingEntity().isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0
            }

            ++this.numPlayersUsing
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing)
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false)
            this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false)
        }
    }

    override fun stopOpen(player: net.minecraft.world.entity.ContainerUser) {
        if (!player.getLivingEntity().isSpectator()) {
//            --this.numPlayersUsing;
//            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
//            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
//            this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
        }
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean {
        return true
    }

    /**
     * invalidates a tile entity
     */
    override fun invalidate() {
        super.invalidate()
        this.updateContainingBlockInfo()
    }

    open fun getGuiID(): String {
        return "minecraft:chest"
    }

    open fun createContainer(playerInventory: InventoryPlayer, playerIn: EntityPlayer): Container {
        return ContainerChest(playerInventory, this, playerIn)
    }




    override fun clearContent() {
        if (isEmpty) return
        stacks.clear()
        setChanged()
    }

    override fun getDisplayName(): ITextComponent = getName()

    override fun getPacketRange(): Double {
        return 20.0
    }

    override fun getPacketCooldown(): Int {
        return 3
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }

    override fun getTierOfKeyRequired(): Int {
        return this.tier
    }

    override fun onValidKeyActivated(player: EntityPlayer?, key: ItemStack?, face: EnumFacing?): Boolean {
        if (this.locked) {
            this.locked = false

            if (this.world.isRemote) {
                // player.playSound("galacticraft.player.unlockchest", 1.0F,
                // 1.0F);
            } else {
                if (!(player)!!.capabilities.isCreativeMode) {
                    (player)!!.inventory.getSelectedItem().shrink(1)
                }

                return true
            }
        }

        return false
    }

    override fun onActivatedWithoutKey(player: EntityPlayer?, face: EnumFacing?): Boolean {
        if (this.locked) {
            if ((player)!!.level().isRemote) {
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_ON_FAILED_CHEST_UNLOCK,
                        GCCoreUtil.getDimensionID(this.world),
                        arrayOf<Any>(this.getTierOfKeyRequired())
                    )
                )
            }
            return true
        }

        return false
    }

    override fun canBreak(): Boolean {
        return false
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

    open fun fillWithLoot(player: EntityPlayer?) {
        if (this.lootTable != null) {
            val loottable: LootTable = this.world.getLootTableManager().getLootTableFromLocation(this.lootTable)
            this.lootTable = null
            val random: Random

            if (this.lootTableSeed == 0L) {
                random = Random()
            } else {
                random = Random(this.lootTableSeed)
            }

            val builder: LootContext.Builder = LootContext.Builder(this.world as WorldServer?)

            if (player != null) {
                builder.withLuck(player.getLuck())
            }

            loottable.fillInventory(this, random, builder.build())
        }
    }

    open fun getLootTable(): ResourceLocation? {
        return this.lootTable
    }

    open fun setLootTable(lootTable: ResourceLocation?, lootTableSeed: Long) {
        this.lootTable = lootTable
        this.lootTableSeed = lootTableSeed
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos, pos.add(1, 2, 1))
        }
        return this.renderAABB!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_MEDIUM
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return IntArray(0)
    }

    override fun canPlaceItemThroughFace(index: Int, itemStackIn: ItemStack, direction: EnumFacing?): Boolean {
        return false
    }

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: EnumFacing): Boolean {
        return false
    }

    companion object {
        @JvmStatic
        fun findClosest(entity: Entity, tier: Int): TileEntityTreasureChest? {
            var distance: Double = Double.MAX_VALUE
            var chest: TileEntityTreasureChest? = null
            for (tile: TileEntity? in entity.level().loadedTileEntityList) {
                if (tile is TileEntityTreasureChest && tile.getTierOfKeyRequired() == tier) {
                    val dist: Double = entity.getDistanceSq(
                        tile.getPos().getX() + 0.5,
                        tile.getPos().getY() + 0.5,
                        tile.getPos().getZ() + 0.5
                    )
                    if (dist < distance) {
                        distance = dist
                        chest = tile
                    }
                }
            }

            if (chest != null) {
                GCLog.debug("Found chest to generate boss loot in: " + chest.pos)
            } else {
                GCLog.debug("Could not find chest to generate boss loot in!")
            }

            return chest
        }
    }
}
