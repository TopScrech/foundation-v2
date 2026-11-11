package galaxyspace.core.prefab.blocks

import foundation.level.offset

import javax.annotation.Nonnull

import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.events.GSEventHandler
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.effect.MobEffectInstance as PotionEffect
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.BlockFluidFinite
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockFluidNatureGas constructor(fluid: Fluid, material: Material) : BlockFluidFinite(fluid, material) {
    override fun updateTick(
        @Nonnull world: World,
        @Nonnull pos: BlockPos,
        @Nonnull state: IBlockState,
        @Nonnull rand: Random
    ) {
        super.updateTick(world, pos, state, rand)

        if (GSConfigCore.enableGasExplosion) {
            var flag: Boolean = false
            for (around: BlockPos in BlockPos.getAllInBox(-1, -1, -1, 1, 1, 1)) {
                if (world.getBlockState(pos.offset(around)).getBlock() === Blocks.FIRE) flag = true
                if (world.getBlockState(pos.offset(around)).getBlock() === Blocks.TORCH) flag = true
                if (world.getBlockState(pos.offset(around)).getBlock() === Blocks.MAGMA) flag = true

                if (flag) {
                    world.newExplosion(
                        null,
                        pos.getX().toDouble(),
                        pos.getY().toDouble(),
                        pos.getZ().toDouble(),
                        5f,
                        true,
                        true
                    )
                    flag = false
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getFogColor(
        world: World,
        pos: BlockPos,
        state: IBlockState,
        entity: Entity,
        originalColor: Vec3d,
        partialTicks: Float
    ): Vec3d {
        return Vec3d(30.0, 30.0, 30.0)
    }

    override fun isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return false
    }

    override fun isReplaceable(world: IBlockAccess, pos: BlockPos): Boolean {
        return (world.getBlockState(pos).getValue(LEVEL) as Int) < 4
    }

    override fun onEntityCollision(world: World, pos: BlockPos, state: IBlockState, entity: Entity) {
        if (entity is EntityLivingBase) {
            val living: EntityLivingBase = entity
            if (living is EntityPlayerMP) {
                if (!living.capabilities.isCreativeMode) {
                    if (!living.hasEffect(MobEffects.BLINDNESS)) living.addEffect(
                        PotionEffect(
                            MobEffects.BLINDNESS,
                            20 * 5,
                            4
                        )
                    )

                    if (!OxygenUtil.hasValidOxygenSetup(living)
                        && !GSEventHandler.isValidOxygenTanks(living)
                    ) {
                        if (!living.hasEffect(MobEffects.POISON)) {
                            living.addEffect(PotionEffect(MobEffects.POISON, 20 * 5, 4))
                        }
                    } else {
                        GSEventHandler.consumeOxygenFromTank(living, 1)
                    }
                }
            }
        }
    }
}
