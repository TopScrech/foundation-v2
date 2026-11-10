package micdoodle8.mods.galacticraft.planets.asteroids.entities

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class EntityTier3Rocket : EntityTieredRocket {
    constructor(par1World: World?) : super(par1World) {
        this.setSize(1.8f, 6f)
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        rocketType: IRocketType.EnumRocketType?
    ) : super(par1World, par2, par4, par6) {
        this.rocketType = rocketType
        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        reversed: Boolean,
        rocketType: IRocketType.EnumRocketType?,
        inv: NonNullList<ItemStack>?
    ) : this(par1World, par2, par4, par6, rocketType) {
        this.stacks = inv
    }

    override fun getYOffset(): Double {
        return 1.5
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        return ItemStack(AsteroidsItems.tier3Rocket, 1, (this.rocketType)!!.getIndex())
    }

    override fun entityInit() {
        super.entityInit()
    }

    override fun getMountedYOffset(): Double {
        return 1.75
    }

    override fun getRotateOffset(): Float {
        return 1.5f
    }

    override fun getOnPadYOffset(): Double {
        return 0.0
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
                    d = min(d * 1.2, 2.0)
                } else {
                    d = min(d, 1.4)
                }

                if (d != 0.0) {
                    this.motionY =
                        -d * 2.5 * cos(((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES).toDouble())
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

            if (this.timeSinceLaunch % MathHelper.floor(2 * (1 / multiplier)) == 0f) {
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
            val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!

            if (this.stacks == null || ((this.stacks)!!.isEmpty())!!) {
                stats.setRocketStacks(NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY))
            } else {
                stats.setRocketStacks(this.stacks)
            }

            stats.setRocketType((this.rocketType)!!.getIndex())
            stats.setRocketItem(AsteroidsItems.tier3Rocket)
            stats.setFuelLevel(this.fuelTank.getFluidAmount())
        }
    }

    protected open fun spawnParticles(launched: Boolean) {
        if (!this.isDead) {
            val sinPitch: Double = sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES_D)
            var x1: Double = 3.2 * cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var z1: Double = 3.2 * sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var y1: Double = 3.2 * cos((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)
            if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal && this.targetVec != null) {
                var modifier: Double = this.posY - ((this.targetVec)!!.getY())!!
                modifier = max(modifier, 180.0)
                x1 *= modifier / 200.0
                y1 *= min(modifier / 200.0, 2.5)
                z1 *= modifier / 200.0
            }

            val y2: Double = this.prevPosY + (this.posY - this.prevPosY) + y1 - 0.75 * this.motionY - 0.3 + 1.2

            val x2: Double = this.posX + x1 + this.motionX
            val z2: Double = this.posZ + z1 + this.motionZ
            val motionVec: Vector3 = Vector3(x1 + this.motionX, y1 + this.motionY, z1 + this.motionZ)
            val d1: Vector3 = Vector3(y1 * 0.1, -x1 * 0.1, z1 * 0.1).rotate(315 - this.rotationYaw, motionVec)
            val d2: Vector3 = Vector3(x1 * 0.1, -z1 * 0.1, y1 * 0.1).rotate(315 - this.rotationYaw, motionVec)
            val d3: Vector3 = Vector3(-y1 * 0.1, x1 * 0.1, z1 * 0.1).rotate(315 - this.rotationYaw, motionVec)
            val d4: Vector3 = Vector3(x1 * 0.1, z1 * 0.1, -y1 * 0.1).rotate(315 - this.rotationYaw, motionVec)
            val mv1: Vector3 = motionVec.clone().translate(d1)
            val mv2: Vector3 = motionVec.clone().translate(d2)
            val mv3: Vector3 = motionVec.clone().translate(d3)
            val mv4: Vector3 = motionVec.clone().translate(d4)
            //T3 - Four flameballs which spread
            val riddenByEntity: EntityLivingBase? = if (this.getPassengers().isEmpty() || this.getPassengers()
                    .get(0) !is EntityLivingBase
            ) null else this.getPassengers().get(0) as EntityLivingBase?
            val rider: Array<Any?> = arrayOf<Any?>(riddenByEntity)
            makeFlame(x2 + d1.x, y2 + d1.y, z2 + d1.z, mv1, this.getLaunched(), rider)
            makeFlame(x2 + d2.x, y2 + d2.y, z2 + d2.z, mv2, this.getLaunched(), rider)
            makeFlame(x2 + d3.x, y2 + d3.y, z2 + d3.z, mv3, this.getLaunched(), rider)
            makeFlame(x2 + d4.x, y2 + d4.y, z2 + d4.z, mv4, this.getLaunched(), rider)
        }
    }

    private fun makeFlame(
        x2: Double,
        y2: Double,
        z2: Double,
        motionVec: Vector3,
        getLaunched: Boolean,
        rider: Array<Any?>
    ) {
        var y2: Double = y2
        if (getLaunched) {
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2, y2, z2), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2 + 0.4, y2, z2), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2 - 0.4, y2, z2), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2, y2, z2 + 0.4), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2, y2, z2 - 0.4), motionVec, rider)
            return
        }

        if (this.ticksExisted % 2 == 0) return

        y2 += 1.6
        val x1: Double = motionVec.x
        val y1: Double = motionVec.y
        val z1: Double = motionVec.z
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0),
            Vector3(x1 + 0.1 + this.rand.nextDouble() / 10.0, y1 - 0.3, z1 + 0.1 + this.rand.nextDouble() / 10.0),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0),
            Vector3(x1 - 0.1 - this.rand.nextDouble() / 10.0, y1 - 0.3, z1 + 0.1 + this.rand.nextDouble() / 10.0),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0),
            Vector3(x1 - 0.1 - this.rand.nextDouble() / 10.0, y1 - 0.3, z1 - 0.1 - this.rand.nextDouble() / 10.0),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0),
            Vector3(x1 + 0.1 + this.rand.nextDouble() / 10.0, y1 - 0.3, z1 - 0.1 - this.rand.nextDouble() / 10.0),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 + 0.4, y2, z2),
            Vector3(x1 + 0.3, y1 - 0.3, z1),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 - 0.4, y2, z2),
            Vector3(x1 - 0.3, y1 - 0.3, z1),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2, y2, z2 + 0.4),
            Vector3(x1, y1 - 0.3, z1 + 0.3),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2, y2, z2 - 0.4),
            Vector3(x1, y1 - 0.3, z1 - 0.3),
            rider
        )
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return !this.isDead && par1EntityPlayer.getDistanceSq(this) <= 64.0
    }

    override fun writeEntityToNBT(par1NBTTagCompound: NBTTagCompound) {
        super.writeEntityToNBT((par1NBTTagCompound)!!)
    }

    override fun readEntityFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound)
    }

    override fun getRocketTier(): Int {
        return 3
    }

    override fun getFuelTankCapacity(): Int {
        return 1500
    }

    override fun getPreLaunchWait(): Int {
        return 400
    }

    override fun getCameraZoom(): Float {
        return 15.0f
    }

    override fun defaultThirdPerson(): Boolean {
        return true
    }

    override fun getItemsDropped(droppedItems: MutableList<ItemStack?>?): MutableList<ItemStack>? {
        super.getItemsDropped(droppedItems)
        val rocket: ItemStack = ItemStack(AsteroidsItems.tier3Rocket, 1, (this.rocketType)!!.getIndex())
        rocket.setTagCompound(NBTTagCompound())
        rocket.getTagCompound()!!.setInteger("RocketFuel", this.fuelTank.getFluidAmount())
        (droppedItems)!!.add(rocket)
        return ((droppedItems as MutableList<ItemStack>?))!!
    }

    override fun getRenderOffsetY(): Float {
        return -1f
    }
}
