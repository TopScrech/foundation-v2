package micdoodle8.mods.galacticraft.core.wrappers

import net.minecraft.item.Item

/**
 * Used to register canisters with partially filled textures
 */
open class PartialCanister
constructor(
    private val item: Item?,
    private val modID: String?,
    private val baseName: String?,
    private val textureCount: Int
) {
    open fun getItem(): Item? {
        return item
    }

    open fun getModID(): String? {
        return modID
    }

    open fun getBaseName(): String? {
        return baseName
    }

    open fun getTextureCount(): Int {
        return textureCount
    }
}
