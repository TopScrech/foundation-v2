package micdoodle8.mods.galacticraft.core.command

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.world.storage.WorldSavedData
import java.util.*

open class GCInvSaveData : WorldSavedData {
    constructor() : super(SAVE_ID)

    constructor(name: String) : super(name)

    override fun readFromNBT(filedata: NBTTagCompound) {
        for (obj: Any? in filedata.getKeySet()) {
            if (obj is NBTTagList) {
                val entry: NBTTagList = obj
                val name: String = entry.toString() // TODO See if this is equivilent to 1.6's getName function
                val saveinv: Array<ItemStack?> = arrayOfNulls<ItemStack>(6)
                if (entry.tagCount() > 0) {
                    for (j: Int in 0..<entry.tagCount()) {
                        val obj1: NBTTagCompound = entry.getCompoundTagAt(j)

                        if (obj1 != null) {
                            val i: Int = obj1.getByte("Slot").toInt() and 7
                            if (i >= 6) {
                                println("GCInv error retrieving savefile: slot was outside range 0-5")
                                return
                            }
                            saveinv[i] = ItemStack(obj1)
                        }
                    }
                }
                CommandGCInv.savedata.put(name.lowercase(Locale.getDefault()), saveinv)
            }
        }
    }

    override fun writeToNBT(toSave: NBTTagCompound): NBTTagCompound {
        for (name in CommandGCInv.savedata.keys) {
            val par1NBTTagList: NBTTagList = NBTTagList()
            val saveinv: Array<ItemStack?> = CommandGCInv.savedata.get(name)!!

            for (i: Int in 0..5) {
                if (saveinv[i] != null) {
                    val nbttagcompound: NBTTagCompound = NBTTagCompound()
                    nbttagcompound.setByte("Slot", (i + 200).toByte())
                    saveinv[i]!!.writeToNBT(nbttagcompound)
                    par1NBTTagList.appendTag(nbttagcompound)
                }
            }
            toSave.setTag(name, par1NBTTagList)
        }

        return toSave
    }

    companion object {
        @JvmField
        val SAVE_ID: String = Constants.GCDATAFOLDER + "GCInv_savefile"
    }
}
