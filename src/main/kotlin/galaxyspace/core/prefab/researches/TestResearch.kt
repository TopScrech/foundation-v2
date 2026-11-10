package galaxyspace.core.prefab.researches

import galaxyspace.core.util.researches.BaseResearch
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

open class TestResearch constructor(id: Int, posX: Int, posY: Int) : BaseResearch(id, "test_" + id, posX, posY) {
    init {
        this.addNeedItem(ItemStack(Items.APPLE))
    }
}
