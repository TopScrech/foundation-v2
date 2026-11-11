package micdoodle8.mods.galacticraft.core.entities.player

import api.player.client.ClientPlayerAPI
import api.player.client.ClientPlayerBase
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.TransformerHooks
import micdoodle8.mods.galacticraft.core.client.EventHandlerClient
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import net.minecraft.world.entity.MoverType
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class GCPlayerBaseSP constructor(playerAPI: ClientPlayerAPI) : ClientPlayerBase(playerAPI) {
    @JvmField
    var lastIsFlying: Boolean = false

    @JvmField
    var lastLandingTicks: Int = 0


    private fun getClientHandler(): IPlayerClient? {
        return ClientProxyCore.playerClientHandler
    }

    //    @Override
    //    public void wakeUpPlayer(boolean par1, boolean par2, boolean par3)
    //    {
    //        if (!this.getClientHandler().wakeUpPlayer(this, par1, par2, par3))
    //        {
    //            super.wakeUpPlayer(par1, par2, par3);
    //        }
    //    }
    override fun isEntityInsideOpaqueBlock(): Boolean {
        return this.getClientHandler()!!.isEntityInsideOpaqueBlock(this.player, super.isEntityInsideOpaqueBlock())
    }

    override fun onLivingUpdate() {
        this.getClientHandler()!!.onLivingUpdatePre(this.player)
        super.onLivingUpdate()
        this.getClientHandler()!!.onLivingUpdatePost(this.player)
    }

    override fun beforeUpdateEntityActionState() {
        if (this.player.world.provider is IZeroGDimension) {
            val stats: GCPlayerStatsClient = (GCPlayerStatsClient.Companion.get(this.player))!!
            if (stats.getLandingTicks() > 0) {
                this.player.movementInput.moveStrafe *= 0.5f
                this.player.movementInput.moveForward *= 0.5f
            }

            //TODO: equivalent to getEyeHeight() in GCEntityClientPlayerMP

            //TODO: set this.player.flyToggleTimer = 0;
        }
    }

    override fun afterUpdateEntityActionState() {
        if (this.player.world.provider is IZeroGDimension) {
            this.player.setJumping(false)
            val aABB: AxisAlignedBB = this.player.getEntityBoundingBox()
            if ((aABB.minY % 1.0) == 0.5) this.player.setEntityBoundingBox(aABB.offset(0.0, 0.00001, 0.0))
        }
    }

    override fun moveEntity(moverType: MoverType?, x: Double, y: Double, z: Double) {
        super.moveEntity(moverType, x, y, z)
        this.getClientHandler()!!.move(this.player, moverType, x, y, z)
    }

    override fun afterMoveEntityWithHeading(paramFloat1: Float, paramFloat2: Float, paramFloat3: Float) {
        super.afterMoveEntityWithHeading(paramFloat1, paramFloat2, paramFloat3)

        if (CompatibilityManager.isSmartMovingLoaded && !this.player.capabilities.isFlying) {
            this.player.motionY += 0.080000000000000002
            this.player.motionY -= TransformerHooks.getGravityForEntity(this.player)
        }
    }

    override fun onUpdate() {
        this.getClientHandler()!!.onUpdate(this.player)
        super.onUpdate()
    }

    override fun isSneaking(): Boolean {
        if (this.player.world.provider is IZeroGDimension) {
            val stats: GCPlayerStatsClient = (GCPlayerStatsClient.Companion.get(this.player))!!
            if (stats.getLandingTicks() > 0) {
                if (this.lastLandingTicks == 0) this.lastLandingTicks = stats.getLandingTicks()

                return stats.getLandingTicks() < this.lastLandingTicks
            } else this.lastLandingTicks = 0

            if ((stats.getFreefallHandler())!!.pjumpticks > 0) return true

            if (EventHandlerClient.sneakRenderOverride) {
                if ((stats.getFreefallHandler())!!.testFreefall(this.player)) return false
                if (stats.isInFreefall()) return false
            }
        }
        return super.isSneaking()
    }

    //    @Override
    //    @OnlyIn(Dist.CLIENT)
    //    public float getBedOrientationInDegrees()
    //    {
    //        return this.getClientHandler().getBedOrientationInDegrees(this, super.getBedOrientationInDegrees());
    //    }
    @OnlyIn(Dist.CLIENT)
    override fun getBrightnessForRender(): Int {
        var height: Double = this.player.posY + this.player.getEyeHeight().toDouble()
        if (height > 255.0) height = 255.0
        val blockpos: BlockPos = BlockPos(this.player.posX, height, this.player.posZ)
        return if (this.player.world.isBlockLoaded(blockpos)) this.player.world.getCombinedLight(blockpos, 0) else 0
    }
}
