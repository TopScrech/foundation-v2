package micdoodle8.mods.galacticraft.planets.mars.entities

import java.util.Random

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import java.util.*
import kotlin.math.*

open class EntityTier2Rocket : EntityTieredRocket {
    constructor(par1World: World?) : super(par1World) {
        this.setSize(1.2f, 4.5f)
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
        this.setSize(1.2f, 4.5f)
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

    override fun entityInit() {
        super.entityInit()
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        return ItemStack(MarsItems.rocketMars, 1, (this.rocketType)!!.getIndex())
    }

    override fun getMountedYOffset(): Double {
        return 1.6
    }

    override fun getRotateOffset(): Float {
        return 1.25f
    }

    override fun getOnPadYOffset(): Double {
        return -0.2
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
                    d = min(d * 1.2, 1.8)
                } else {
                    d = min(d, 1.2)
                }

                if (d != 0.0) {
                    this.motionY = -d * 2.0 * cos((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)
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

            if (this.stacks == null || ((this.stacks)!!.size == 0)!!) {
                stats.setRocketStacks(NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY))
            } else {
                stats.setRocketStacks(this.stacks)
            }

            stats.setRocketType((this.rocketType)!!.getIndex())
            stats.setRocketItem(MarsItems.rocketMars)
            stats.setFuelLevel(this.fuelTank.getFluidAmount())
        }
    }

    protected open fun spawnParticles(launched: Boolean) {
        if (!this.isDead) {
            val sinPitch: Double = sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES_D)
            var x1: Double = 2.9 * cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var z1: Double = 2.9 * sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var y1: Double = 2.9 * cos((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)
            if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal && this.targetVec != null) {
                var modifier: Double = this.posY - ((this.targetVec)!!.getY())!!
                modifier = min(max(modifier, 80.0), 200.0)
                x1 *= modifier / 100.0
                y1 *= modifier / 100.0
                z1 *= modifier / 100.0
            }

            val y: Double =
                this.prevPosY + (this.posY - this.prevPosY) + y1 - this.motionY + (if (!this.getLaunched()) 2.5 else 1.0)


            val x2: Double = this.posX + x1 - this.motionX
            val z2: Double = this.posZ + z1 - this.motionZ
            val x3: Double = x2 + x1 / 2.0
            val y3: Double = y + y1 / 2.0
            val z3: Double = z2 + z1 / 2.0
            val motionVec: Vector3 = Vector3(x1, y1, z1)

            if (this.ticksExisted % 2 == 0 && !this.getLaunched()) return

            val flame: String = if (this.getLaunched()) "launchFlameLaunched" else "launchFlameIdle"

            val riddenByEntity: EntityLivingBase? = if (this.getPassengers().isEmpty() || this.getPassengers()
                    .get(0) !is EntityLivingBase
            ) null else this.getPassengers().get(0) as EntityLivingBase?
            val rider: Array<Any?> = arrayOf<Any?>(riddenByEntity)
            val none: Array<Any?> = arrayOf<Any?>()
            val random: Random = this.rand
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x2 + 0.4 - random.nextDouble() / 10.0, y, z2 + 0.4 - random.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x2 - 0.4 + random.nextDouble() / 10.0, y, z2 + 0.4 - random.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x2 - 0.4 + random.nextDouble() / 10.0, y, z2 - 0.4 + random.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x2 + 0.4 - random.nextDouble() / 10.0, y, z2 - 0.4 + random.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(flame, Vector3(x2, y, z2), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle(flame, Vector3(x2 + 0.4, y, z2), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle(flame, Vector3(x2 - 0.4, y, z2), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle(flame, Vector3(x2, y, z2 + 0.4), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle(flame, Vector3(x2, y, z2 - 0.4), motionVec, rider)
            //Larger flameball for T2 - positioned behind the smaller one
            val a: Double = 4.0
            val bx: Double = motionVec.x + 0.5 / a
            val bz: Double = motionVec.z + 0.5 / a
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 + 0.2 - random.nextDouble() / 6.0, y3 + 0.4, z3 + 0.2 - random.nextDouble() / 6.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 - 0.2 + random.nextDouble() / 6.0, y3 + 0.4, z3 + 0.2 - random.nextDouble() / 6.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 - 0.2 + random.nextDouble() / 6.0, y3 + 0.4, z3 - 0.2 + random.nextDouble() / 6.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 + 0.2 - random.nextDouble() / 6.0, y3 + 0.4, z3 - 0.2 + random.nextDouble() / 6.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 + 0.2 - random.nextDouble() / 6.0, y3 - 0.4, z3 + 0.2 - random.nextDouble() / 6.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 - 0.2 + random.nextDouble() / 6.0, y3 - 0.4, z3 + 0.2 - random.nextDouble() / 6.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 - 0.2 + random.nextDouble() / 6.0, y3 - 0.4, z3 - 0.2 + random.nextDouble() / 6.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 + 0.2 - random.nextDouble() / 6.0, y3 - 0.4, z3 - 0.2 + random.nextDouble() / 6.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 + 0.7 - random.nextDouble() / 8.0, y3, z3 + 0.7 - random.nextDouble() / 8.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 - 0.7 + random.nextDouble() / 8.0, y3, z3 + 0.7 - random.nextDouble() / 8.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 - 0.7 + random.nextDouble() / 8.0, y3, z3 - 0.7 + random.nextDouble() / 8.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 + 0.7 - random.nextDouble() / 8.0, y3, z3 - 0.7 + random.nextDouble() / 8.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 + 0.7 - random.nextDouble() / 8.0, y3, z3 - random.nextDouble() / 8.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 - 0.7 + random.nextDouble() / 8.0, y3, z3 - random.nextDouble() / 8.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 + random.nextDouble() / 8.0, y3, z3 + 0.7 + random.nextDouble() / 8.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                flame,
                Vector3(x3 - random.nextDouble() / 8.0, y3, z3 - 0.7 + random.nextDouble() / 8.0),
                Vector3(bx - random.nextDouble() / a, motionVec.y, bz - random.nextDouble() / a),
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle("blueflame", Vector3(x2 - 0.8, y, z2), motionVec, none)
            (GalacticraftCore.proxy)!!.spawnParticle("blueflame", Vector3(x2 + 0.8, y, z2), motionVec, none)
            (GalacticraftCore.proxy)!!.spawnParticle("blueflame", Vector3(x2, y, z2 - 0.8), motionVec, none)
            (GalacticraftCore.proxy)!!.spawnParticle("blueflame", Vector3(x2, y, z2 + 0.8), motionVec, none)
        }
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

    override fun isDockValid(dock: IFuelDock?): Boolean {
        return dock is TileEntityLandingPad
    }

    override fun getRocketTier(): Int {
        return 2
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
        val rocket: ItemStack = ItemStack(MarsItems.rocketMars, 1, (this.rocketType)!!.getIndex())
        rocket.setTagCompound(NBTTagCompound())
        rocket.getTagCompound()!!.setInteger("RocketFuel", this.fuelTank.getFluidAmount())
        (droppedItems)!!.add(rocket)
        return ((droppedItems as MutableList<ItemStack>?))!!
    }

    override fun getRenderOffsetY(): Float {
        return -0.1f
    }
}
