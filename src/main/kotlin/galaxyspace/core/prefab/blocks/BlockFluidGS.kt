package galaxyspace.core.prefab.blocks

import javax.annotation.Nonnull

import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.events.GSEventHandler
import galaxyspace.core.GSFluids
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect
import net.minecraft.util.DamageSource
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
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
                if (world.getBlockState(pos.add(around)).getBlock() === Blocks.FIRE) flag = true
                if (world.getBlockState(pos.add(around)).getBlock() === Blocks.TORCH) flag = true
                if (world.getBlockState(pos.add(around)).getBlock() === Blocks.MAGMA) flag = true

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
                        if (!living.isPotionActive(MobEffects.BLINDNESS)) living.addPotionEffect(
                            PotionEffect(
                                MobEffects.BLINDNESS,
                                20 * 5,
                                4
                            )
                        )

                        if (!OxygenUtil.hasValidOxygenSetup(living) && !GSEventHandler.isValidOxygenTanks(living)) {
                            if (!living.isPotionActive(MobEffects.POISON)) {
                                living.addPotionEffect(PotionEffect(MobEffects.POISON, 20 * 5, 4))
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
