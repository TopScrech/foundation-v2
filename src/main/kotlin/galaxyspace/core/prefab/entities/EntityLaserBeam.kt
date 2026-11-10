package galaxyspace.core.prefab.entities

import galaxyspace.GalaxySpace
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

open class EntityLaserBeam : EntityThrowable {
    @JvmField
    protected var shooter: EntityLivingBase? = null
    private val lifetime = 20 * 2
    private var trace: RayTraceResult? = null

    constructor(worldIn: World) : super(worldIn)

    constructor(world: World, x: Double, y: Double, z: Double) : super(world, x, y, z)

    constructor(world: World, entity: EntityLivingBase, trace: RayTraceResult?) : super(world, entity) {
        this.trace = trace
    }

    override fun onUpdate() {
        super.onUpdate()


        //if(--this.lifetime <= 0)
        //this.setDead();
        /*
		if(world.isRemote)
			GalaxySpace.debug(this.rotationYaw + " | " + this.rotationPitch);*/
    }

    open fun getTrace(): RayTraceResult? {
        return this.trace
    }

    override fun getGravityVelocity(): Float {
        return 0.0f
    }

    override fun onImpact(result: RayTraceResult) {
        if (result.getBlockPos() != null) {
            val state: IBlockState = this.world.getBlockState(result.getBlockPos())

            if (state != null) {
                this.world.createExplosion(
                    null,
                    result.getBlockPos().getX().toDouble(),
                    result.getBlockPos().getY().toDouble(),
                    result.getBlockPos().getZ().toDouble(),
                    5f,
                    false
                )
            }
        }

        this.setDead()
    }
}
