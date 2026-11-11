package galaxyspace.systems.SolarSystem.planets.overworld.tile

import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import io.netty.buffer.ByteBuf
import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityRadiationStabiliser
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockRadiationStabiliser
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.entities.IBubbleProviderColored
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.HashSet
import kotlin.math.max
import kotlin.math.min

open class TileEntityRadiationStabiliser constructor() :
    TileEntityElectricInventory("tile.radiation_stabiliser.name"), ISidedInventory, IBubbleProviderColored {
    @JvmField
    var bubbleSize: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var shouldRenderBubble: Boolean = true

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 else 45).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(1 + 4, ItemStack.EMPTY)
    }

    override fun validate() {
        super.validate()
        if (!this.world.isRemote) loadedTiles.add(BlockVec3Dim(this))
    }

    override fun onLoad() {
        if (!this.world.isRemote) loadedTiles.add(BlockVec3Dim(this))
    }

    override fun onChunkUnload() {
        if (!this.world.isRemote) loadedTiles.remove(BlockVec3Dim(this))
        super.onChunkUnload()
    }

    override fun invalidate() {
        if (!this.world.isRemote) {
            loadedTiles.remove(BlockVec3Dim(this))
        }

        super.invalidate()
    }

    override fun getPacketRange(): Double {
        return 64.0
    }

    override fun addExtraNetworkedData(networkedList: MutableList<Any?>?) {
        if (!this.world.isRemote && !this.isInvalid()) {
//            networkedList.add(this.oxygenBubble != null);
//            if (this.oxygenBubble != null)
//            {
//                networkedList.add(this.oxygenBubble.getEntityId());
//            }
            if (this.world.getMinecraftServer()!!.isDedicatedServer()) {
                (networkedList)!!.add(loadedTiles.size)
                //TODO: Limit this to ones in the same dimension as this tile?
                for (distributor: BlockVec3Dim? in loadedTiles) {
                    if (distributor == null) {
                        (networkedList)!!.add(-1)
                        (networkedList)!!.add(-1)
                        (networkedList)!!.add(-1)
                        (networkedList)!!.add(-1)
                    } else {
                        (networkedList)!!.add(distributor.x)
                        (networkedList)!!.add(distributor.y)
                        (networkedList)!!.add(distributor.z)
                        (networkedList)!!.add(distributor.dim)
                    }
                }
            } else  //Signal integrated server, do not clear loadedTiles
            {
                (networkedList)!!.add(-1)
            }
            (networkedList)!!.add(this.bubbleSize)
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(
            (this.getPos().getX() - this.bubbleSize).toDouble(),
            (this.getPos().getY() - this.bubbleSize).toDouble(),
            (this.getPos().getZ() - this.bubbleSize).toDouble(),
            (this.getPos().getX() + this.bubbleSize).toDouble(),
            (this.getPos().getY() + this.bubbleSize).toDouble(),
            (this.getPos().getZ() + this.bubbleSize).toDouble()
        )
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_LONG
    }

    override fun readExtraNetworkedData(dataStream: ByteBuf?) {
        if (this.world.isRemote) {
//            if (dataStream.readBoolean())
//            {
//                this.oxygenBubble = (EntityBubble) worldObj.getEntityByID(dataStream.readInt());
//            }
            val size: Int = (dataStream)!!.readInt()
            if (size >= 0) {
                loadedTiles.clear()
                for (i: Int in 0..<size) {
                    val i1: Int = (dataStream)!!.readInt()
                    val i2: Int = (dataStream)!!.readInt()
                    val i3: Int = (dataStream)!!.readInt()
                    val i4: Int = (dataStream)!!.readInt()
                    if (i1 == -1 && i2 == -1 && i3 == -1 && i4 == -1) {
                        continue
                    }
                    loadedTiles.add(BlockVec3Dim(i1, i2, i3, i4))
                }
            }
            this.bubbleSize = (dataStream)!!.readFloat()
        }
    }

    open fun getDistanceFromServer(par1: Int, par3: Int, par5: Int): Int {
        val d3: Int = this.getPos().getX() - par1
        val d4: Int = this.getPos().getY() - par3
        val d5: Int = this.getPos().getZ() - par5
        return d3 * d3 + d4 * d4 + d5 * d5
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            var range_boost: Int = 0
            var energy_boost: Int = 0

            for (i: Int in 0..3) {
                if (this.getInventory().get(1 + i).isItemEqual(ItemStack(GSItems.UPGRADES, 1, 0))) range_boost++
                if (this.getInventory().get(1 + i).isItemEqual(ItemStack(GSItems.UPGRADES, 1, 3))) energy_boost++
            }

            if (this.getEnergyStoredGC() > 0.0f && this.hasEnoughEnergyToRun && !this.disabled) {
                this.bubbleSize += 0.01f
            } else {
                this.bubbleSize -= 0.05f
            }

            this.bubbleSize = min(max(this.bubbleSize, 0.0f), 10.0f + (5.0f * range_boost))
            this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 + (60 * range_boost) - (20 * energy_boost) else 45 + (45 * range_boost) - (15 * energy_boost)).toFloat())
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        if (nbt.containsTag("bubbleVisible")) {
            this.setBubbleVisible(nbt.getBooleanOr("bubbleVisible", false))
        }

        if (nbt.containsTag("bubbleSize")) {
            this.bubbleSize = nbt.getFloatOr("bubbleSize", 0f)
        }
        this.inventory = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.getInventory())
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        nbt.putBoolean("bubbleVisible", this.shouldRenderBubble)
        nbt.putFloat("bubbleSize", this.bubbleSize)
        ItemStackHelper.saveAllItems(nbt, this.getInventory())
        return nbt
    }

    override fun getMaxStackSize(): Int {
        return 1
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        if (this.canPlaceItem(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemElectricBase.isElectricItemCharged(itemstack)
                1 -> return itemstack == ItemStack(GSItems.BASIC, 1, 13)
                else -> return false
            }
        }
        return false
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItemEmpty(itemstack)
            1 -> return true
            else -> return false
        }
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        if (itemstack.isEmpty()) {
            return false
        }
        if (slotID == 0) {
            return ItemElectricBase.isElectricItem(itemstack.getItem())
        }
        if (slotID == 1) {
            return itemstack == ItemStack(GSItems.BASIC, 1, 13)
        }
        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.hasEnoughEnergyToRun
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockRadiationStabiliser) {
            return state.getValue(BlockRadiationStabiliser.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY().rotateY().getOpposite()
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getItem(0)
    }

    open fun inBubble(pX: Double, pY: Double, pZ: Double): Boolean {
        var r: Double = bubbleSize.toDouble()
        r *= r
        var d3: Double = this.getPos().getX() + 0.5 - pX
        d3 *= d3
        if (d3 > r) {
            return false
        }
        var d4: Double = this.getPos().getZ() + 0.5 - pZ
        d4 *= d4
        if (d3 + d4 > r) {
            return false
        }
        val d5: Double = this.getPos().getY() + 0.5 - pY
        return d3 + d4 + d5 * d5 < r
    }

    override fun setBubbleVisible(shouldRender: Boolean) {
        this.shouldRenderBubble = shouldRender
    }

    override fun getBubbleSize(): Float {
        return this.bubbleSize
    }

    override fun getBubbleVisible(): Boolean {
        return this.shouldRenderBubble
    }

    override fun getColor(): Vector3 {
        return Vector3(0.45, 0.0, 0.1)
    }

    companion object {
        @JvmField
        var loadedTiles: HashSet<BlockVec3Dim?> = HashSet<BlockVec3Dim?>()
    }
}
