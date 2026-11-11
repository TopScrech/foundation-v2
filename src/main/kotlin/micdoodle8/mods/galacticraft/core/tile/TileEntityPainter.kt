package micdoodle8.mods.galacticraft.core.tile

import foundation.item.legacyItemValue

import net.minecraft.world.ContainerHelper
import foundation.block.getStateFromMeta

import foundation.persistence.containsTag

import net.minecraft.world.item.DyeColor as EnumDyeColor

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.item.IPaintable
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.Items
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemDye
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.LogicalSide as Side
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

//import net.minecraft.item.EnumDyeColor;
open class TileEntityPainter : TileEntity(), IDisableableMachine, IInventoryDefaults, IPacketReceiver {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileEntity>.getDisplayName()

    @JvmField
    var range: Int = RANGE_DEFAULT //currently unused

    @JvmField
    var glassColor: IntArray = intArrayOf(-1, -1, -1) //Size of this array must match GlassType enum

    @JvmField
    val playersUsing: MutableSet<EntityPlayer> = HashSet<EntityPlayer>()
    private val containingItems: NonNullList<ItemStack> =
        NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)

    @JvmField
    var guiColor: Int = 0xffffff


    open fun takeColorFromItem(itemStack: ItemStack?) {
        if (itemStack == null) {
            return
        }

        val item: Item = itemStack.getItem()
        var color: Int = -1
        if (item === Items.DYE) {
            color = ItemDye.DYE_COLORS[itemStack.legacyItemValue]
        } else if (item is ItemBlock) {
            val b: Block = item.getBlock()
            val bs: IBlockState = b.getStateFromMeta(itemStack.legacyItemValue)
            try {
                val mc: MapColor = b.getMapColor(bs, null, null)
                color = mc.colorValue
            } catch (e: Exception) {
            }
        } else {
            color = tryOtherModDyes(itemStack)
        }

        if (color >= 0) {
            color = ColorUtil.addColorsBasic(color, this.guiColor)
            this.guiColor = color
        }
    }

    private fun applyColorToItem(itemStack: ItemStack?, color: Int, side: Side?, player: EntityPlayer?) {
        var color: Int = color
        if (itemStack == null) {
            return
        }

        val item: Item = itemStack.getItem()

        if (item is IPaintable) {
            //TODO  e.g. flags, eggs, rockets???
        } else if (item is ItemBlock) {
            color = ColorUtil.lighten(color, 0.03f)
            val b: Block = item.getBlock()
            var result: Int = 0
            if (b is IPaintable) {
                result = (b as IPaintable).setColor(color, player, side)
            }
            if (b is BlockSpaceGlass) {
                val type: Int = b.type.ordinal
                this.glassColor[type] = color
                if (result > 0 && side == Side.CLIENT) {
                    BlockSpaceGlass.updateClientRender()
                }
            }
        }
    }

    private fun setGlassColors(color1: Int, color2: Int, color3: Int) {
        var changes: Boolean = false
        if (this.glassColor[0] != color1) {
            changes = true
            this.glassColor[0] = color1
        }
        if (this.glassColor[1] != color2) {
            changes = true
            this.glassColor[1] = color2
        }
        if (this.glassColor[2] != color3) {
            changes = true
            this.glassColor[2] = color3
        }

        //        if (changes)
//            ColorUtil.updateColorsForArea(this.world), this.pos, this.range, this.glassColor[0], this.glassColor[1], this.glassColor[2]);;
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        if (nbt.containsTag("guic")) {
            this.glassColor[0] = nbt.getIntOr("G1", 0)
            this.glassColor[1] = nbt.getIntOr("G2", 0)
            this.glassColor[2] = nbt.getIntOr("G3", 0)
            this.range = nbt.getIntOr("rge", 0)
            this.guiColor = nbt.getIntOr("guic", 0)
        }

        ItemStackHelper.loadAllItems(nbt, this.containingItems)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("G1", this.glassColor[0])
        nbt.putInt("G2", this.glassColor[1])
        nbt.putInt("G3", this.glassColor[2])
        nbt.putInt("guic", this.guiColor)
        nbt.putInt("rge", this.range)

        ItemStackHelper.saveAllItems(nbt, this.containingItems)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun onLoad() {
        if (this.world.isRemote) {
            //Request any networked information from server on first client update
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
        } else {
            getLoadedTiles(this.world).add(BlockVec3(this.pos))
        }
    }

    override fun onChunkUnload() {
        getLoadedTiles(this.world).remove(BlockVec3(this.pos))
        super.onChunkUnload()
    }

    override fun invalidate() {
        if (!this.world.isRemote) getLoadedTiles(this.world).remove(BlockVec3(this.pos))
        super.invalidate()
    }

    private fun dominantToPlayer(player: EntityPlayerMP) {
        (GCPlayerStats.get(player))!!.setGlassColors(this.glassColor[0], this.glassColor[1], this.glassColor[2])
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("tile.machine3.9.name")
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        //Used to do the painting!
        for (entityPlayer: EntityPlayer? in playersUsing) {
            this.buttonPressed(index, entityPlayer, Side.SERVER)
        }
    }

    open fun buttonPressed(index: Int, player: EntityPlayer?, side: Side?) {
        when (index) {
            0 -> this.applyColorToItem(this.getItem(1), this.guiColor, side, player)
            1 -> this.takeColorFromItem(this.getItem(0))
            2 -> this.guiColor = 0xffffff
        }
    }

    override fun getDisabled(index: Int): Boolean {
        return false
    }

    override fun getContainerSize(): Int {
        return 2
    }

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean {
        return false
    }

    override fun getItem(par1: Int): ItemStack {
        return this.containingItems.get(par1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.containingItems, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        val oldstack: ItemStack = ContainerHelper.takeItem(this.containingItems, index)
        if (!oldstack.isEmpty()) {
            this.setChanged()
        }
        return oldstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.containingItems.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.containingItems) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }


    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        if (this.world.isRemote) {
            return
        }

        (sendData)!!.add(this.guiColor)
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        if (this.world.isRemote) {
            try {
                this.guiColor = (buffer)!!.readInt()
            } catch (ignore: Exception) {
                ignore.printStackTrace()
            }
        }
    }

    companion object {
        private const val RANGE_DEFAULT = 96

        @JvmField
        var loadedTilesForDim: MutableMap<Int, MutableSet<BlockVec3>?> = HashMap<Int, MutableSet<BlockVec3>?>()

        @JvmStatic
        private fun getLoadedTiles(world: World): MutableSet<BlockVec3> {
            val dimID: Int = GCCoreUtil.getDimensionID(world)
            var loaded: MutableSet<BlockVec3>? = loadedTilesForDim.get(dimID)

            if (loaded == null) {
                loaded = HashSet<BlockVec3>()
                loadedTilesForDim.put(dimID, loaded)
            }

            return loaded
        }

        @JvmStatic
        fun onServerTick(world: World) {
            val loaded: MutableSet<BlockVec3> = getLoadedTiles(world)
            val dimID: Int = GCCoreUtil.getDimensionID(world)
            val allPlayers: MutableList<EntityPlayerMP> = (PlayerUtil.getPlayersOnline() as MutableList<EntityPlayerMP>)
            for (player: EntityPlayerMP in allPlayers) {
                if (player.dimension != dimID) continue

                val playerPos: BlockVec3 = BlockVec3(player)
                var nearest: BlockVec3? = null
                var shortestDistance: Int = RANGE_DEFAULT * RANGE_DEFAULT
                for (bv: BlockVec3 in loaded) {
                    val distance: Int = bv.distanceSquared(playerPos)
                    if (distance < shortestDistance) {
                        shortestDistance = distance
                        nearest = bv
                    }
                }

                if (nearest != null) {
                    val te: TileEntity? = nearest.getTileEntity(world)
                    if (te is TileEntityPainter) {
                        te.dominantToPlayer(player)
                    }
                }

                //TODO
                //Make sure this works in a way so that the nearest Painter quickly takes priority, but there is no race condition...
                //Also maybe some hysteresis?
            }
        }

        //Any special cases go here, e.g. coloured dye or paint items added by other mods
        @JvmStatic
        private fun tryOtherModDyes(itemStack: ItemStack): Int {
            val item: Item = itemStack.getItem()

            return -1
        }
    }
}
