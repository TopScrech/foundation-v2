package galaxyspace.systems.SolarSystem.planets.overworld.tile

import galaxyspace.core.prefab.tile.TileEntityInventoryAdvanced
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList

open class TileEntityResearchTable constructor() : TileEntityInventoryAdvanced("tile.research_table.name") {
    init {
        this.inventory = NonNullList.withSize<ItemStack>(0, ItemStack.EMPTY)
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())
        return par1NBTTagCompound
    }

    public override fun getInventoryStackLimit(): Int {
        return 1
    }

    public override fun getSlotsForFace(side: EnumFacing?): IntArray {
        return intArrayOf(0)
    }

    override fun getPacketRange(): Double {
        return 0.0
    }

    override fun getPacketCooldown(): Int {
        return 0
    }

    override fun isNetworkedTile(): Boolean {
        return false
    }
}
