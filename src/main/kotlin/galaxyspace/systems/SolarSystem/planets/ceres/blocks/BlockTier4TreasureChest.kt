package galaxyspace.systems.SolarSystem.planets.ceres.blocks

import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.systems.SolarSystem.planets.ceres.tile.TileEntityTreasureChestCeres
import asmodeuscore.api.item.IShiftDescription
import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

open class BlockTier4TreasureChest constructor(assetName: String?) : BlockTier1TreasureChest(assetName),
    IShiftDescription {
    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSBlocksTab
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityTreasureChestCeres()
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".desc")
    }

    override fun getDescription(arg0: Int): String? {
        return null
    }
}
