package galaxyspace.core.prefab.tile

import galaxyspace.core.prefab.tile.TileEntityChestGS
import galaxyspace.core.prefab.world.DoubleChestItemHandlerGS
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.Container
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryLargeChest
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityLockableLoot
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

abstract class TileEntityChestGS @JvmOverloads constructor(private val block: Block?, name: String? = null) :
    TileEntityLockableLoot(), ITickable, IInventory {
    private var chestContents: NonNullList<ItemStack>

    @JvmField
    protected var adjacentChestChecked: Boolean = false

    @JvmField
    var adjacentChestZNeg: TileEntityChestGS? = null

    @JvmField
    var adjacentChestXPos: TileEntityChestGS? = null

    @JvmField
    var adjacentChestXNeg: TileEntityChestGS? = null

    @JvmField
    var adjacentChestZPos: TileEntityChestGS? = null
    private var doubleChestHandler: DoubleChestItemHandlerGS? = null

    @JvmField
    var lidAngle: Float = 0f

    @JvmField
    var prevLidAngle: Float = 0f
    private var numPlayersUsing = 0
    private var ticksSinceSync = 0


    init {
        this.setCustomName(name)
        chestContents = NonNullList.withSize<ItemStack>(27, ItemStack.EMPTY)
    }

    override fun getSizeInventory(): Int {
        return 27
    }

    override fun isEmpty(): Boolean {
        for (itemStack: ItemStack in this.chestContents) if (!itemStack.isEmpty()) return false

        return true
    }

    override fun getItems(): NonNullList<ItemStack> {
        return this.chestContents
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.chestContents = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)

        if (nbt.hasKey("CustomName", 8)) {
            this.customName = nbt.getString("CustomName")
        }
        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems(nbt, this.chestContents)
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        if (this.hasCustomName()) {
            nbt.setString("CustomName", this.customName)
        }
        if (!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems(nbt, this.chestContents)
        }
        return nbt
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun updateContainingBlockInfo() {
        super.updateContainingBlockInfo()
        this.adjacentChestChecked = false
        this.doubleChestHandler = null
    }

    override fun update() {
        this.checkForAdjacentChests()
        val i: Int = this.pos.getX()
        val j: Int = this.pos.getY()
        val k: Int = this.pos.getZ()
        ++this.ticksSinceSync

        if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
            this.numPlayersUsing = 0

            for (player: EntityPlayer in this.world.getEntitiesWithinAABB<EntityPlayer>(
                EntityPlayer::class.java,
                AxisAlignedBB(
                    (i - 5.0f).toDouble(),
                    (j - 5.0f).toDouble(),
                    (k - 5.0f).toDouble(),
                    (i + 1 + 5.0f).toDouble(),
                    (j + 1 + 5.0f).toDouble(),
                    (k + 1 + 5.0f).toDouble()
                )
            )) {
                if (player.openContainer is ContainerChest) {
                    val iinventory: IInventory = (player.openContainer as ContainerChest).getLowerChestInventory()

                    if (iinventory === this || iinventory is InventoryLargeChest && iinventory.isPartOfLargeChest(this)) {
                        ++this.numPlayersUsing
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0f && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
            var d1: Double = i + 0.5
            var d2: Double = k + 0.5

            if (this.adjacentChestZPos != null) {
                d2 += 0.5
            }
            if (this.adjacentChestXPos != null) {
                d1 += 0.5
            }
            this.world.playSound(
                null,
                d1,
                j + 0.5,
                d2,
                SoundEvents.BLOCK_CHEST_OPEN,
                SoundCategory.BLOCKS,
                0.5f,
                this.world.rand.nextFloat() * 0.1f + 0.9f
            )
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0f || this.numPlayersUsing > 0 && this.lidAngle < 1.0f) {
            val f2: Float = this.lidAngle

            if (this.numPlayersUsing > 0) {
                this.lidAngle += 0.1f
            } else {
                this.lidAngle -= 0.1f
            }

            if (this.lidAngle > 1.0f) {
                this.lidAngle = 1.0f
            }

            if (this.lidAngle < 0.5f && f2 >= 0.5f && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
                var d3: Double = i + 0.5
                var d0: Double = k + 0.5

                if (this.adjacentChestZPos != null) {
                    d0 += 0.5
                }
                if (this.adjacentChestXPos != null) {
                    d3 += 0.5
                }
                this.world.playSound(
                    null,
                    d3,
                    j + 0.5,
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
    }

    override fun receiveClientEvent(id: Int, type: Int): Boolean {
        if (id == 1) {
            this.numPlayersUsing = type
            return true
        } else {
            return super.receiveClientEvent(id, type)
        }
    }

    override fun openInventory(player: EntityPlayer) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0
            }
            ++this.numPlayersUsing
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing)
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false)
        }
    }

    override fun closeInventory(player: EntityPlayer) {
        if (!player.isSpectator() && this.getBlockType() === this.block) {
            --this.numPlayersUsing
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing)
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false)
        }
    }

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun invalidate() {
        super.invalidate()
        this.updateContainingBlockInfo()
        this.checkForAdjacentChests()
    }

    override fun getGuiID(): String {
        return "moreplanets:ancient_chest"
    }

    override fun createContainer(playerInventory: InventoryPlayer, player: EntityPlayer): Container {
        this.fillWithLoot(player)
        return ContainerChest(playerInventory, this, player)
    }

    override fun canRenderBreaking(): Boolean {
        return true
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.doubleChestHandler == null || this.doubleChestHandler!!.needsRefresh()) {
                this.doubleChestHandler = DoubleChestItemHandlerGS.Companion.get(this)
            }
            if (this.doubleChestHandler != null && this.doubleChestHandler !== DoubleChestItemHandlerGS.Companion.NO_ADJACENT_CHESTS_INSTANCE) {
                return this.doubleChestHandler as T
            }
        }
        return super.getCapability<T?>(capability, facing)
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(this.getPos().add(-1, 0, -1), this.getPos().add(2, 2, 2))
    }

    fun getSingleChestHandler(): IItemHandler? {
        return super.getCapability<IItemHandler?>(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
    }

    protected fun isChestAt(pos: BlockPos): Boolean {
        if (this.world == null) {
            return false
        } else {
            val block: Block = this.world.getBlockState(pos).getBlock()
            return block === this.block
        }
    }

    protected fun setNeighbor(tile: TileEntityChestGS, side: EnumFacing) {
        if (tile.isInvalid()) {
            this.adjacentChestChecked = false
        } else if (this.adjacentChestChecked) {
            when (side) {
                EnumFacing.NORTH -> if (this.adjacentChestZNeg !== tile) {
                    this.adjacentChestChecked = false
                }

                EnumFacing.SOUTH -> if (this.adjacentChestZPos !== tile) {
                    this.adjacentChestChecked = false
                }

                EnumFacing.EAST -> if (this.adjacentChestXPos !== tile) {
                    this.adjacentChestChecked = false
                }

                EnumFacing.WEST -> if (this.adjacentChestXNeg !== tile) {
                    this.adjacentChestChecked = false
                }
            
else -> {}
}
        }
    }

    abstract fun checkForAdjacentChests(): TileEntityChestGS?
}
