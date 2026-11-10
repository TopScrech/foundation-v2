package galaxyspace.api

import net.minecraft.item.ItemStack

interface IResearch {
    fun getID(): Int
    fun getName(): String?
    fun getNeedItems(): MutableList<ItemStack?>?
    fun getParents(): Array<IResearch?>?
    fun getNeedExperience(): Float
    fun getUnlockItems(): MutableList<ItemStack?>?
    fun getPosX(): Int
    fun getPosY(): Int
    fun getDescription(): String?
    fun getTextureID(): Int
}
