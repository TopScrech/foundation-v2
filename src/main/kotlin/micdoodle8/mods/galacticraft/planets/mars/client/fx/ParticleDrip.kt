package micdoodle8.mods.galacticraft.planets.mars.client.fx

import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.particle.Particle
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class ParticleDrip constructor(world: World, x: Double, y: Double, z: Double) :
    Particle(world, x, y, z, 0.0, 0.0, 0.0) {
    private var bobTimer: Int

    init {
        this.motionZ = 0.0
        this.motionY = this.motionZ
        this.motionX = this.motionY
        this.setParticleTextureIndex(113)
        this.setSize(0.01f, 0.01f)
        this.particleGravity = 0.06f
        this.bobTimer = 40
        this.particleMaxAge = (64.0 / (Math.random() * 0.8 + 0.2)).toInt()
        this.motionZ = 0.0
        this.motionY = this.motionZ
        this.motionX = this.motionY
    }

    override fun onUpdate() {
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        this.particleRed = 0.0f
        this.particleGreen = 0.2f
        this.particleBlue = 0.1f
        this.motionY -= this.particleGravity.toDouble()

        if (this.bobTimer-- > 0) {
            this.motionX *= 0.02
            this.motionY *= 0.02
            this.motionZ *= 0.02
            this.setParticleTextureIndex(113)
        } else {
            this.setParticleTextureIndex(112)
        }

        this.move(this.motionX, this.motionY, this.motionZ)
        this.motionX *= 0.9800000190734863
        this.motionY *= 0.9800000190734863
        this.motionZ *= 0.9800000190734863

        if (this.particleMaxAge-- <= 0) {
            this.setExpired()
        }

        if (this.onGround) {
            this.setExpired()
        }

        val pos: BlockPos = BlockPos(this.posX, this.posY, this.posZ)
        val state: IBlockState = this.world.getBlockState(pos)
        val material: Material = state.getMaterial()

        if (material.isLiquid() || material.isSolid()) {
            var d0: Double = 0.0

            if (state.getBlock() is BlockLiquid) {
                d0 = BlockLiquid.getLiquidHeightPercent((state.getValue(BlockLiquid.LEVEL) as Int)).toDouble()
            }

            val d1: Double = MathHelper.floor(this.posY) + 1 - d0

            if (this.posY < d1) {
                this.setExpired()
            }
        }
    }
}
