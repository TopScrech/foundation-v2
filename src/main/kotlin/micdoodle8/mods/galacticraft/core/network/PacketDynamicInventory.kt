package micdoodle8.mods.galacticraft.core.network

import io.netty.buffer.ByteBuf
import java.io.IOException
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.inventory.IInventorySettable
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos

open class PacketDynamicInventory : PacketBase {
    private var type = 0
    private var data: Array<Any?>? = null
    private var stacks: Array<ItemStack?>? = null

    constructor() : super()

    constructor(entity: Entity) : super(GCCoreUtil.getDimensionID(entity.world)) {
        assert(entity is IInventory) { "Entity does not implement " + IInventory::class.java.getSimpleName() }
        this.type = 0
        this.data = ((arrayOf<Any>(entity.getEntityId()) as Array<Any?>?))!!
        this.stacks = arrayOfNulls<ItemStack>((entity as IInventory).getSizeInventory())

        for (i: Int in this.stacks!!.indices) {
            this.stacks!![i] = (entity as IInventory).getStackInSlot(i)
        }
    }

    constructor(chest: TileEntity) : super(GCCoreUtil.getDimensionID(chest.getWorld())) {
        assert(chest is IInventory) { "Tile does not implement " + IInventory::class.java.getSimpleName() }
        this.type = 1
        this.data = ((arrayOf<Any>(chest.getPos()) as Array<Any?>?))!!
        this.stacks = arrayOfNulls<ItemStack>((chest as IInventory).getSizeInventory())

        for (i: Int in this.stacks!!.indices) {
            this.stacks!![i] = (chest as IInventory).getStackInSlot(i)
        }
    }

    override fun encodeInto(buffer: ByteBuf?) {
        super.encodeInto(buffer)
        (buffer)!!.writeInt(this.type)

        when (this.type) {
            0 -> (buffer)!!.writeInt((this.data!![0] as kotlin.Int?)!!)
            1 -> {
                (buffer)!!.writeInt((this.data!![0] as BlockPos).getX())
                (buffer)!!.writeInt((this.data!![0] as BlockPos).getY())
                (buffer)!!.writeInt((this.data!![0] as BlockPos).getZ())
            }
        }

        (buffer)!!.writeInt(this.stacks!!.size)

        for (i: Int in this.stacks!!.indices) {
            try {
                NetworkUtil.writeItemStack((this.stacks!![i])!!, (buffer)!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun decodeInto(buffer: ByteBuf?) {
        super.decodeInto(buffer)
        this.type = (buffer)!!.readInt()

        when (this.type) {
            0 -> {
                this.data = arrayOfNulls<Any>(1)
                this.data!![0] = (buffer)!!.readInt()
            }

            1 -> {
                this.data = arrayOfNulls<Any>(3)
                this.data!![0] = BlockPos((buffer)!!.readInt(), (buffer)!!.readInt(), (buffer)!!.readInt())
            }
        }

        this.stacks = arrayOfNulls<ItemStack>((buffer)!!.readInt())

        for (i: Int in this.stacks!!.indices) {
            try {
                this.stacks!![i] = NetworkUtil.readItemStack((buffer)!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun handleClientSide(player: EntityPlayer?) {
        if ((player)!!.world == null) {
            return
        }

        when (this.type) {
            0 -> {
                val entity: Entity? = (player)!!.world.getEntityByID((this.data!![0] as kotlin.Int?)!!)

                if (entity is IInventorySettable) {
                    this.setInventoryStacks(entity as IInventorySettable)
                }
            }

            1 -> {
                val tile: TileEntity? = (player)!!.world.getTileEntity(this.data!![0] as BlockPos?)

                if (tile is IInventorySettable) {
                    this.setInventoryStacks(tile as IInventorySettable)
                }
            }
        }
    }

    override fun handleServerSide(player: EntityPlayer?) {
        when (this.type) {
            0 -> {
                val entity: Entity? = (player)!!.world.getEntityByID((this.data!![0] as kotlin.Int?)!!)

                if (entity is IInventorySettable) {
                    (GalacticraftCore.packetPipeline)!!.sendTo(PacketDynamicInventory(entity), player as EntityPlayerMP)
                }
            }

            1 -> {
                val tile: TileEntity? = (player)!!.world.getTileEntity(this.data!![0] as BlockPos?)

                if (tile is IInventorySettable) {
                    (GalacticraftCore.packetPipeline)!!.sendTo(PacketDynamicInventory(tile), player as EntityPlayerMP)
                }
            }
        }
    }

    private fun setInventoryStacks(inv: IInventorySettable) {
        inv.setSizeInventory(this.stacks!!.size)

        for (i: Int in this.stacks!!.indices) {
            inv.setInventorySlotContents(i, this.stacks!![i])
        }
    }
}
