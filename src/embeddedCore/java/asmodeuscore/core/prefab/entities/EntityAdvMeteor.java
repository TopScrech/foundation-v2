package asmodeuscore.core.prefab.entities;

import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.entities.EntityMeteor;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityAdvMeteor extends EntityMeteor {

	private IBlockState blockSpawn = GCBlocks.fallenMeteor.getDefaultState();
	
	public EntityAdvMeteor(World world) {
		super(world);
	}

	public EntityAdvMeteor(World world, double x, double y, double z, double motX, double motY, double motZ, int size) {
		super(world, x, y, z, motX, motY, motZ, size);
	}
	
	public EntityAdvMeteor setSpawnBlock(IBlockState block) {
		this.blockSpawn = block;
		return this;
	}

	@Override
	protected void onImpact(RayTraceResult movingObjPos)
    {
        if (!this.world.isRemote)
        {
            this.world.newExplosion(this, this.posX, this.posY, this.posZ, this.size / 2 + 2, false, true);

            if (movingObjPos != null)
            {
                BlockPos pos = movingObjPos.getBlockPos();
                if (pos == null)
                {
                    if (movingObjPos.entityHit != null)
                    {
                        pos = this.world.getTopSolidOrLiquidBlock(movingObjPos.entityHit.getPosition());
                    } else
                    {
                        pos = this.world.getTopSolidOrLiquidBlock(this.getPosition());
                    }
                }
                BlockPos above = pos.up();
                if (blockSpawn != null && this.world.isAirBlock(above))
                {
                    this.world.setBlockState(above, blockSpawn, 3);
                }

                if (movingObjPos.entityHit != null)
                {
                    movingObjPos.entityHit.attackEntityFrom(EntityMeteor.causeMeteorDamage(this, this.shootingEntity), ConfigManagerCore.hardMode ? 12F : 6F);
                }
            }
        }

        this.setDead();
    }
}
