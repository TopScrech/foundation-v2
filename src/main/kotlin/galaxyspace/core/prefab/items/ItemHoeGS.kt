package galaxyspace.core.prefab.items

import galaxyspace.core.util.GSCreativeTabs
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemHoe

open class ItemHoeGS constructor(assetName: String, material: ToolMaterial) : ItemHoe(material) {
    init {
        this.setTranslationKey(assetName)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSArmorTab
    }
}
