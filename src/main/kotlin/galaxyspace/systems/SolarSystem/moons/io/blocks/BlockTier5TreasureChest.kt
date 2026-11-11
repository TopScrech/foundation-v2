package galaxyspace.systems.SolarSystem.moons.io.blocks

import asmodeuscore.api.item.IShiftDescription
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.systems.SolarSystem.moons.io.tile.TileEntityTreasureChestIo
import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.level.Level as World

open class BlockTier5TreasureChest constructor(assetName: String?) : BlockTier1TreasureChest(assetName),
    IShiftDescription {
    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSBlocksTab
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityTreasureChestIo()
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".desc")
    }

    override fun getDescription(arg0: Int): String? {
        return null
    }
}
