package galaxyspace.core.prefab.blocks

import javax.annotation.Nonnull

import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.events.GSEventHandler
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.BlockFluidFinite
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
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

    @SideOnly(Side.CLIENT)
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
                    if (!living.isPotionActive(MobEffects.BLINDNESS)) living.addPotionEffect(
                        PotionEffect(
                            MobEffects.BLINDNESS,
                            20 * 5,
                            4
                        )
                    )

                    if (!OxygenUtil.hasValidOxygenSetup(living)
                        && !GSEventHandler.isValidOxygenTanks(living)
                    ) {
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
