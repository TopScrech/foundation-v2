package galaxyspace.systems.SolarSystem.planets.overworld.tile

import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockUniversalRecycler
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList

open class TileEntityPlasmaProducer constructor() :
    TileEntityElectricInventory("tile.plasma_producer.name"), ISidedInventory, IPacketReceiver {
    init {
        this.storage.setCapacity(100000f)
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 else 75).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)
        this.setTierGC(1)
    }

    override fun update() {
        super.update()
    }

    private fun canProcess(): Boolean {
        return false
    }

    private fun smeltItem() {
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        return par1NBTTagCompound
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return slotID == 0
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
            1 -> return true
        }

        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.canProcess()
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getItem(0)
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockUniversalRecycler) {
            return state.getValue(BlockUniversalRecycler.FACING)
        }
        return EnumFacing.NORTH
    }
}
