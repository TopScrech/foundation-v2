package galaxyspace.core.prefab.blocks

import foundation.level.offset

import javax.annotation.Nonnull

import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.events.GSEventHandler
import galaxyspace.core.GSFluids
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.effect.MobEffectInstance as PotionEffect
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import java.util.Random

open class BlockFluidGS @JvmOverloads constructor(
    fluid: Fluid,
    material: Material,
    isDamage: Boolean,
    isFlammable: Boolean = false
) : BlockFluidClassic(fluid, material) {
    private val isDamaged: Boolean
    private val isFlammable: Boolean

    init {
        this.setQuantaPerBlock(if (fluid.isGaseous()) 0 else 9)
        this.setTranslationKey("block_" + fluid.getName())

        if (this.density <= FluidRegistry.WATER.getDensity()) {
            this.displacements.put(Blocks.WATER, false)
            this.displacements.put(Blocks.FLOWING_WATER, false)
        }
        if (this.density <= FluidRegistry.LAVA.getDensity()) {
            this.displacements.put(Blocks.LAVA, false)
            this.displacements.put(Blocks.FLOWING_LAVA, false)
        }

        this.isDamaged = isDamage
        this.isFlammable = isFlammable
    }

    override fun updateTick(
        @Nonnull world: World,
        @Nonnull pos: BlockPos,
        @Nonnull state: IBlockState,
        @Nonnull rand: Random
    ) {
        super.updateTick(world, pos, state, rand)

        if (isFlammable && GSConfigCore.enableGasExplosion) {
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

    override fun isEntityInsideMaterial(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        entity: Entity,
        yToTest: Double,
        material: Material,
        testingHead: Boolean
    ): Boolean? {
        return !stack.getFluid().isGaseous()
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

    override fun onEntityCollision(world: World, pos: BlockPos, state: IBlockState, entity: Entity) {
        if (this.isDamaged) entity.attackEntityFrom(DamageSource.GENERIC, 0.5f)

        if (stack.getFluid() === GSFluids.NatureGas) {
            val flag: Boolean = false
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

                        if (!OxygenUtil.hasValidOxygenSetup(living) && !GSEventHandler.isValidOxygenTanks(living)) {
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
}
