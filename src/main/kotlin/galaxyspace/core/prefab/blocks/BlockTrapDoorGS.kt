package galaxyspace.core.prefab.blocks

import galaxyspace.core.util.GSCreativeTabs
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockTrapDoor
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs

open class BlockTrapDoorGS constructor(name: String) : BlockTrapDoor(Material.WOOD) {
    init {
        this.setTranslationKey(name)
        this.setSoundType(SoundType.WOOD)
        this.setHardness(3.0f)
    }

    override fun setTranslationKey(name: String): Block {
        return super.setTranslationKey(name)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSBlocksTab
    }
}
