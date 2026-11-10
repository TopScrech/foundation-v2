package micdoodle8.mods.galacticraft.planets.asteroids.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import java.lang.ref.WeakReference
import java.util.ArrayList

open class TileEntityTelepadFake : TileBaseElectricBlock() {
    // The the position of the main block
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var mainBlockPosition: BlockPos? = null
    private var mainTelepad: WeakReference<TileEntityShortRangeTelepad?>? = null

    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    private var canConnect = false

    open fun setMainBlock(mainBlock: BlockPos?) {
        this.setMainBlockInternal(mainBlock)

        if (!this.world.isRemote) {
            val state: IBlockState = this.world.getBlockState(this.getPos())
            this.world.notifyBlockUpdate(this.getPos(), state, state, 3)
        }
    }

    private fun setMainBlockInternal(mainBlock: BlockPos?) {
        this.mainBlockPosition = mainBlock
        this.updateConnectable()
    }

    open fun onBlockRemoval() {
        val telepad: TileEntityShortRangeTelepad? = this.getBaseTelepad()

        if (telepad != null) {
            telepad.onDestroy(this)
        }
    }

    open fun onActivated(par5EntityPlayer: EntityPlayer): Boolean {
        val telepad: TileEntityShortRangeTelepad? = this.getBaseTelepad()
        return telepad != null && telepad.onActivated(par5EntityPlayer)
    }

    override fun update() {
        super.update()

        val telepad: TileEntityShortRangeTelepad? = this.getBaseTelepad()

        if (telepad != null) {
            this.storage.setCapacity(telepad.storage.getCapacityGC())
            this.storage.setMaxExtract(telepad.storage.getMaxExtract())
            this.storage.setMaxReceive(telepad.storage.getMaxReceive())
            this.extractEnergyGC(null, telepad.receiveEnergyGC(null, this.getEnergyStoredGC(), false), false)
        }
    }

    private fun getBaseTelepad(): TileEntityShortRangeTelepad? {
        if (this.mainBlockPosition == null) {
            return null
        }

        if (mainTelepad == null) {
            val tileEntity: TileEntity? = this.world.getTileEntity(this.mainBlockPosition)

            if (tileEntity != null) {
                if (tileEntity is TileEntityShortRangeTelepad) {
                    mainTelepad = WeakReference<TileEntityShortRangeTelepad?>(tileEntity)
                }
            }
        }

        if (mainTelepad == null) {
            this.world.setBlockToAir(this.mainBlockPosition)
        } else {
            val telepad: TileEntityShortRangeTelepad? = this.mainTelepad!!.get()

            if (telepad != null) {
                return telepad
            } else {
                this.world.removeTileEntity(this.getPos())
            }
        }

        return null
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        val tagCompound: NBTTagCompound = nbt.getCompoundTag("mainBlockPosition")
        this.setMainBlockInternal(
            BlockPos(
                tagCompound.getInteger("x"),
                tagCompound.getInteger("y"),
                tagCompound.getInteger("z")
            )
        )
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        if (this.mainBlockPosition != null) {
            val tagCompound: NBTTagCompound = NBTTagCompound()
            tagCompound.setInteger("x", this.mainBlockPosition!!.getX())
            tagCompound.setInteger("y", this.mainBlockPosition!!.getY())
            tagCompound.setInteger("z", this.mainBlockPosition!!.getZ())
            nbt.setTag("mainBlockPosition", tagCompound)
        }

        return nbt
    }

    override fun getPacketRange(): Double {
        return 30.0
    }

    override fun getPacketCooldown(): Int {
        return 50
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }

    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        if (this.mainBlockPosition == null) {
            if (this.world.isRemote || !this.resetMainBlockPosition()) {
                return
            }
        }
        super.getNetworkedData((sendData)!!)
    }

    private fun resetMainBlockPosition(): Boolean {
        for (x: Int in -1..1) {
            for (z: Int in -1..1) {
                var y: Int = -2
                while (y < 1) {
                    val vecToCheck: BlockPos = this.getPos().add(x, y, z)
                    if (this.world.getTileEntity(vecToCheck) is TileEntityShortRangeTelepad) {
                        this.setMainBlock(vecToCheck)
                        return true
                    }
                    y += 2
                }
            }
        }
        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return false
    }

    override fun getElectricInputDirection(): EnumFacing? {
        if (!this.canConnect) {
            return null
        }

        return EnumFacing.UP
    }

    override fun getFront(): EnumFacing {
        return EnumFacing.NORTH
    }

    override fun getBatteryInSlot(): ItemStack {
        return ItemStack.EMPTY
    }

    private fun updateConnectable() {
        if (this.mainBlockPosition != null) {
            if (this.getPos().getX() == mainBlockPosition!!.getX() && this.getPos()
                    .getZ() == mainBlockPosition!!.getZ()
            ) {
                if (this.getPos().getY() > mainBlockPosition!!.getY()) {
                    // If the block has the same x- and y- coordinates, but is above the base block, this is the
                    //      connectable tile
                    this.canConnect = true
                    return
                }
            }
        }

        this.canConnect = false
    }
}
