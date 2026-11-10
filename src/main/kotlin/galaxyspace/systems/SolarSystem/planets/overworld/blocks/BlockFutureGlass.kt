package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import net.minecraft.block.BlockGlass
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class BlockFutureGlass constructor() : BlockGlass(Material.GLASS, false), ITerraformableBlock {
    init {
        this.setTranslationKey("futureglass_none")
        this.setHardness(2.0f)
        this.setSoundType(SoundType.GLASS)
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        return false
    } /*	
	@Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityBlockCrystallTE();
    }
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }
	   */
}
