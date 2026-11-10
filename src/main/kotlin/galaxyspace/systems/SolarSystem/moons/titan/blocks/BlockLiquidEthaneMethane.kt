package galaxyspace.systems.SolarSystem.moons.titan.blocks

import galaxyspace.core.GSFluids
import galaxyspace.core.configs.GSConfigCore
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class BlockLiquidEthaneMethane constructor() :
    BlockFluidClassic(GSFluids.LiquidEthaneMethane, GSFluids.LEMethane) {
    init {
        this.setQuantaPerBlock(9)
        //this.setLightLevel(0.1F);
        //this.needsRandomTick = true;
        this.setTranslationKey("block_liquidethanemethane")
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

    override fun onEntityCollision(worldIn: World, pos: BlockPos, state: IBlockState, entity: Entity) {
        if (entity is EntityLivingBase) entity.attackEntityFrom(DamageSource.GENERIC, 0.5f)
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(state: IBlockState, world: World, pos: BlockPos, rand: Random) {
        if (world.isAirBlock(pos.up()) && GSConfigCore.enableMethaneParticle) {
            for (i: Int in 0..1) world.spawnParticle(
                EnumParticleTypes.SMOKE_NORMAL,
                pos.getX() + world.rand.nextDouble(),
                pos.getY() + 1.5,
                pos.getZ() + world.rand.nextDouble(),
                0.0,
                0.0,
                0.0
            )
        }
    }
}
