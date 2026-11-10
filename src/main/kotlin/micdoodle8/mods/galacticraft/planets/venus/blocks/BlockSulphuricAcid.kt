package micdoodle8.mods.galacticraft.planets.venus.blocks

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.init.SoundEvents
import net.minecraft.util.EnumFacing
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class BlockSulphuricAcid constructor(assetName: String?) :
    BlockFluidClassic(VenusModule.sulphuricAcid, VenusModule.acidMaterial) {
    init {
        this.setQuantaPerBlock(9)
        this.setLightLevel(0.1f)
        this.needsRandomTick = true
        this.setTranslationKey(assetName)
    }

    override fun isEntityInsideMaterial(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        entity: Entity,
        yToTest: Double,
        material: Material,
        testingHead: Boolean
    ): Boolean? {
        return true
    }

    override fun canDisplace(world: IBlockAccess, pos: BlockPos): Boolean {
        if (world.getBlockState(pos).getMaterial().isLiquid()) {
            return false
        }
        return super.canDisplace(world, pos)
    }

    override fun displaceIfPossible(world: World, pos: BlockPos): Boolean {
        if (world.getBlockState(pos).getMaterial().isLiquid()) {
            return false
        }
        return super.displaceIfPossible(world, pos)
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand)

        if (rand.nextInt(1200) == 0) {
            worldIn.playSound(
                null,
                (pos.getX() + 0.5f).toDouble(),
                (pos.getY() + 0.5f).toDouble(),
                (pos.getZ() + 0.5f).toDouble(),
                SoundEvents.BLOCK_LAVA_AMBIENT,
                SoundCategory.BLOCKS,
                rand.nextFloat() * 0.25f + 0.75f,
                0.00001f + rand.nextFloat() * 0.5f
            )
        }
        if (rand.nextInt(10) == 0) {
            val below: BlockPos = pos.down()
            val state: IBlockState = worldIn.getBlockState(below)
            if (state.getBlock()
                    .isSideSolid(state, worldIn, below, EnumFacing.UP) && !worldIn.getBlockState(pos.down(2))
                    .getMaterial().blocksMovement()
            ) {
                GalacticraftPlanets.spawnParticle(
                    "bacterialDrip",
                    Vector3(
                        (pos.getX() + rand.nextFloat()).toDouble(),
                        pos.getY() - 1.05,
                        (pos.getZ() + rand.nextFloat()).toDouble()
                    ),
                    Vector3(0.0, 0.0, 0.0)
                )
            }
        }
    }
}
