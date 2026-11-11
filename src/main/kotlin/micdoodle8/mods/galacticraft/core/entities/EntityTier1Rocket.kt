package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.NonNullList
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

//import calclavia.api.icbm.IMissile;
open class EntityTier1Rocket : EntityTieredRocket {
    constructor(par1World: World?) : super(par1World) {
        this.setSize(1.2f, 3.5f)
        //        this.yOffset = 1.5F;
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        rocketType: IRocketType.EnumRocketType?
    ) : super(par1World, par2, par4, par6) {
        this.rocketType = rocketType
        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        this.setSize(1.2f, 3.5f)
        //        this.yOffset = 1.5F;
    }

    override fun getMountedYOffset(): Double {
        return 0.3
    }

    override fun getRotateOffset(): Float {
        return -1.5f
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        return ItemStack(GCItems.rocketTier1, 1, (this.rocketType)!!.getIndex())
    }

    override fun onUpdate() {
        super.onUpdate()

        val i: Int

        if (this.timeUntilLaunch >= 100) {
            i = abs(this.timeUntilLaunch / 100)
        } else {
            i = 1
        }

        if ((this.getLaunched() || this.launchPhase == EnumLaunchPhase.IGNITED.ordinal && this.rand.nextInt(i) == 0) && !ConfigManagerCore.disableSpaceshipParticles && this.hasValidFuel()) {
            if (this.world.isRemote) {
                this.spawnParticles(this.getLaunched())
            }
        }

        if (this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal && this.hasValidFuel()) {
            if (this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal) {
                var d: Double = (this.timeSinceLaunch / 150).toDouble()

                if (this.world.provider is IGalacticraftWorldProvider && (this.world.provider as IGalacticraftWorldProvider).hasNoAtmosphere()) {
                    d = min(d * 1.2, 1.6)
                } else {
                    d = min(d, 1.0)
                }

                if (d != 0.0) {
                    this.motionY = -d * cos((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)
                }
            } else {
                this.motionY -= 0.008
            }

            var multiplier: Double = 1.0

            if (this.world.provider is IGalacticraftWorldProvider) {
                multiplier = (this.world.provider as IGalacticraftWorldProvider).getFuelUsageMultiplier()

                if (multiplier <= 0) {
                    multiplier = 1.0
                }
            }

            if (this.timeSinceLaunch % MathHelper.floor(3 * (1 / multiplier)) == 0f) {
                this.removeFuel(1)
                if (!this.hasValidFuel()) {
                    this.stopRocketSound()
                }
            }
        } else if (!this.hasValidFuel() && this.getLaunched() && !this.world.isRemote) {
            if (abs(sin((this.timeSinceLaunch / 1000).toDouble())) / 10 != 0.0) {
                this.motionY -= abs(sin((this.timeSinceLaunch / 1000).toDouble())) / 20
            }
        }
    }

    override fun onTeleport(player: EntityPlayerMP?) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)

        if (playerBase != null) {
            val stats: GCPlayerStats = (GCPlayerStats.Companion.get((player)!!))!!

            if (this.stacks == null || ((this.stacks)!!.isEmpty())!!) {
                stats.setRocketStacks(NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY))
            } else {
                stats.setRocketStacks(this.stacks)
            }

            stats.setRocketType((this.rocketType)!!.getIndex())
            stats.setRocketItem(GCItems.rocketTier1)
            stats.setFuelLevel(this.fuelTank.getFluidAmount())
        }
    }

    protected open fun spawnParticles(launched: Boolean) {
        if (!this.isDead) {
            val sinPitch: Double = sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES_D)
            var x1: Double = 2 * cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var z1: Double = 2 * sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var y1: Double = 2 * cos((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)

            if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal && this.targetVec != null) {
                var modifier: Double = this.posY - ((this.targetVec)!!.getY())!!
                modifier = min(max(modifier, 120.0), 300.0)
                x1 *= modifier / 100.0
                y1 *= modifier / 100.0
                z1 *= modifier / 100.0
            }

            var y: Double = this.prevPosY + (this.posY - this.prevPosY) + y1 - this.motionY + 1.2

            val x2: Double = this.posX + x1 - this.motionX
            val z2: Double = this.posZ + z1 - this.motionZ

            val riddenByEntity: EntityLivingBase? = if (!this.getPassengers().isEmpty() && this.getPassengers()
                    .get(0) is EntityLivingBase
            ) this.getPassengers().get(0) as EntityLivingBase? else null

            if (this.getLaunched()) {
                val motionVec: Vector3 = Vector3(x1, y1, z1)
                val rider: Array<Any?> = arrayOf<Any?>(riddenByEntity)
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y, z2 + 0.4 - this.rand.nextDouble() / 10.0),
                    motionVec,
                    rider
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y, z2 + 0.4 - this.rand.nextDouble() / 10.0),
                    motionVec,
                    rider
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y, z2 - 0.4 + this.rand.nextDouble() / 10.0),
                    motionVec,
                    rider
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y, z2 - 0.4 + this.rand.nextDouble() / 10.0),
                    motionVec,
                    rider
                )
                (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2, y, z2), motionVec, rider)
                (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2 + 0.4, y, z2), motionVec, rider)
                (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2 - 0.4, y, z2), motionVec, rider)
                (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2, y, z2 + 0.4), motionVec, rider)
                (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2, y, z2 - 0.4), motionVec, rider)
            } else if (this.ticksExisted % 2 == 0) {
                val rider: Array<Any?> = arrayOf<Any?>(riddenByEntity)
                y += 0.6
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameIdle",
                    Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y, z2 + 0.4 - this.rand.nextDouble() / 10.0),
                    Vector3(this.rand.nextDouble() / 2.0 - 0.25, 0.0, this.rand.nextDouble() / 2.0 - 0.25),
                    rider
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameIdle",
                    Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y, z2 + 0.4 - this.rand.nextDouble() / 10.0),
                    Vector3(this.rand.nextDouble() / 2.0 - 0.25, 0.0, this.rand.nextDouble() / 2.0 - 0.25),
                    rider
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameIdle",
                    Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y, z2 - 0.4 + this.rand.nextDouble() / 10.0),
                    Vector3(this.rand.nextDouble() / 2.0 - 0.25, 0.0, this.rand.nextDouble() / 2.0 - 0.25),
                    rider
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameIdle",
                    Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y, z2 - 0.4 + this.rand.nextDouble() / 10.0),
                    Vector3(this.rand.nextDouble() / 2.0 - 0.25, 0.0, this.rand.nextDouble() / 2.0 - 0.25),
                    rider
                )
            }
        }
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return !this.isDead && par1EntityPlayer.getDistanceSq(this) <= 64.0
    }

    override fun writeEntityToNBT(par1NBTTagCompound: NBTTagCompound) {
        super.writeEntityToNBT((par1NBTTagCompound)!!)
    }

    override fun readEntityFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound)
    }

    override fun getPreLaunchWait(): Int {
        return 400
    }

    override fun getItemsDropped(droppedItems: MutableList<ItemStack?>?): MutableList<ItemStack>? {
        super.getItemsDropped(droppedItems)
        val rocket: ItemStack = ItemStack(GCItems.rocketTier1, 1, (this.rocketType)!!.getIndex())
        rocket.setTagCompound(NBTTagCompound())
        rocket.getTagCompound()!!.putInt("RocketFuel", this.fuelTank.getFluidAmount())
        (droppedItems)!!.add(rocket)
        return ((droppedItems as MutableList<ItemStack>?))!!
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun canPlaceItem(i: Int, itemstack: ItemStack): Boolean {
        return false
    }

    override fun isDockValid(dock: IFuelDock?): Boolean {
        return dock is TileEntityLandingPad
    }

    override fun getRocketTier(): Int {
        return 1
    }

    override fun getFuelTankCapacity(): Int {
        return 1000
    }

    override fun getCameraZoom(): Float {
        return 15.0f
    }

    override fun defaultThirdPerson(): Boolean {
        return true
    }

    override fun getOnPadYOffset(): Double {
        return 0.0
    }
}
