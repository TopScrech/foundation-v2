package galaxyspace.core.prefab.entities

import galaxyspace.core.GSBlocks
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.entities.EntityTieredRocketWithEngine
import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockAdvancedLandingPadFull
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvLandingPad
import galaxyspace.core.util.GSUtils
import galaxyspace.core.prefab.entities.EntityTier5Rocket
import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.event.EventLandingPadRemoval
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fluids.FluidStack
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class EntityTier5Rocket : EntityTieredRocketWithEngine {
    constructor(par1World: World?) : super(par1World, 5) {
        this.setSize(2.2f, 11f)
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        rocketType: IRocketType.EnumRocketType?
    ) : super(par1World, par2, par4, par6, 5, rocketType) {
        this.rocketType = rocketType
        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        rocketType: IRocketType.EnumRocketType?,
        engine: Engine_Type?
    ) : super(par1World, par2, par4, par6, 5, rocketType) {
        this.setEngine((engine)!!)
    }

    override fun getYOffset(): Double {
        return 1.5
    }

    override fun getPickedResult(target: RayTraceResult?): ItemStack {
        return ItemStack(GSItems.ROCKET_TIER_5, 1, (this.rocketType)!!.getIndex())
    }

    override fun entityInit() {
        super.entityInit()
    }

    public override fun getMountedYOffset(): Double {
        return 0.75
    }

    public override fun getRotateOffset(): Float {
        return 0.25f
    }

    public override fun getOnPadYOffset(): Double {
        return 0.0
    }

    override fun onLaunch() {
        super.onLaunch()

        if (!this.world.isRemote) {
            var stats: GCPlayerStats? = null

            if (!this.getPassengers().isEmpty()) {
                for (player: Entity? in this.getPassengers()) {
                    if (player is EntityPlayerMP) {
                        stats = GCPlayerStats.get(player)
                        (stats)!!.setLaunchpadStack(null)

                        if (this.world.provider !is IOrbitDimension) {
                            (stats)!!.setCoordsTeleportedFromX(player.posX)
                            (stats)!!.setCoordsTeleportedFromZ(player.posZ)
                        }
                    }
                }

                val playerMain: Entity? = this.getPassengers().get(0)
                if (playerMain is EntityPlayerMP) stats = GCPlayerStats.get(playerMain)
            }

            var amountRemoved: Int = 0

            PADSEARCH@ for (x: Int in MathHelper.floor(this.posX) - 2..MathHelper.floor(this.posX) + 2) {
                for (y: Int in MathHelper.floor(this.posY) - 3..MathHelper.floor(this.posY) + 1) {
                    for (z: Int in MathHelper.floor(this.posZ) - 2..MathHelper.floor(this.posZ) + 2) {
                        val pos: BlockPos = BlockPos(x, y, z)
                        val block: Block = this.world.getBlockState(pos).getBlock()

                        if (block != null && block is BlockAdvancedLandingPadFull) {
                            if (amountRemoved < 25) {
                                val event: EventLandingPadRemoval = EventLandingPadRemoval(this.world, pos)
                                MinecraftForge.EVENT_BUS.post(event)

                                if (event.allow) {
                                    this.world.setBlockToAir(pos)
                                    amountRemoved = 25
                                }
                                break@PADSEARCH
                            }
                        }
                    }
                }
            }

            //Set the player's launchpad item for return on landing - or null if launchpads not removed
            if (stats != null && amountRemoved == 25) {
                stats.setLaunchpadStack(ItemStack(GSBlocks.ADVANCED_LANDING_PAD_SINGLE, 25, 0))
            }

            this.playSound(
                SoundEvents.ENTITY_ITEM_PICKUP,
                0.2f,
                ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f
            )
        }
    }

    override fun spawnParticles(launched: Boolean) {
        if (!this.isDead) {
            val sinPitch: Double = sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES_D)
            var x1: Double = 3.2 * cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var z1: Double = 3.2 * sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var y1: Double = 3.2 * cos((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)
            if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal && this.targetVec != null) {
                var modifier: Double = this.posY - (this.targetVec)!!.getY()
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
            val d5: Vector3 = Vector3(y1 * 0.1, -x1 * 0.1, z1 * 0.1).rotate(270 - this.rotationYaw, motionVec)
            val d6: Vector3 = Vector3(x1 * 0.1, -z1 * 0.1, y1 * 0.1).rotate(270 - this.rotationYaw, motionVec)
            val d7: Vector3 = Vector3(-y1 * 0.1, x1 * 0.1, z1 * 0.1).rotate(270 - this.rotationYaw, motionVec)
            val d8: Vector3 = Vector3(x1 * 0.1, z1 * 0.1, -y1 * 0.1).rotate(270 - this.rotationYaw, motionVec)
            val mv1: Vector3 = motionVec.clone().translate(d1)
            val mv2: Vector3 = motionVec.clone().translate(d2)
            val mv3: Vector3 = motionVec.clone().translate(d3)
            val mv4: Vector3 = motionVec.clone().translate(d4)
            val mv5: Vector3 = motionVec.clone().translate(d5)
            val mv6: Vector3 = motionVec.clone().translate(d6)
            val mv7: Vector3 = motionVec.clone().translate(d7)
            val mv8: Vector3 = motionVec.clone().translate(d8)
            //T3 - Four flameballs which spread
            makeFlame(x2 + d1.x, y2 + d1.y, z2 + d1.z, mv1, this.getLaunched())
            makeFlame(x2 + d2.x, y2 + d2.y, z2 + d2.z, mv2, this.getLaunched())
            makeFlame(x2 + d3.x, y2 + d3.y, z2 + d3.z, mv3, this.getLaunched())
            makeFlame(x2 + d4.x, y2 + d4.y, z2 + d4.z, mv4, this.getLaunched())

            makeFlame(x2 + d5.x, y2 + d5.y, z2 + d5.z, mv5, this.getLaunched())
            makeFlame(x2 + d6.x, y2 + d6.y, z2 + d6.z, mv6, this.getLaunched())
            makeFlame(x2 + d7.x, y2 + d7.y, z2 + d7.z, mv7, this.getLaunched())
            makeFlame(x2 + d8.x, y2 + d8.y, z2 + d8.z, mv8, this.getLaunched())
            makeFlame(x2, y2, z2, Vector3(x1, y1, z1), this.getLaunched())
        }
    }

    private fun makeFlame(x2: Double, y2: Double, z2: Double, motionVec: Vector3, getLaunched: Boolean) {
        var y2: Double = y2
        val riddenByEntity: EntityLivingBase? = if (this.getPassengers().isEmpty() || this.getPassengers()
                .get(0) !is EntityLivingBase
        ) null else this.getPassengers().get(0) as EntityLivingBase?

        if (getLaunched) {
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                motionVec,
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                motionVec,
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                motionVec,
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                motionVec,
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2, y2, z2),
                motionVec,
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 + 0.4, y2, z2),
                motionVec,
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 - 0.4, y2, z2),
                motionVec,
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2, y2, z2 + 0.4),
                motionVec,
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2, y2, z2 - 0.4),
                motionVec,
                arrayOf<Any?>(riddenByEntity)
            )
            return
        }

        if (this.ticksExisted % 2 == 0) return

        y2 += 1.6
        val x1: Double = motionVec.x
        val y1: Double = motionVec.y
        val z1: Double = motionVec.z
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
            Vector3(x1 + 0.1 + this.rand.nextDouble() / 10, y1 - 0.3, z1 + 0.1 + this.rand.nextDouble() / 10),
            arrayOf<Any?>(riddenByEntity)
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
            Vector3(x1 - 0.1 - this.rand.nextDouble() / 10, y1 - 0.3, z1 + 0.1 + this.rand.nextDouble() / 10),
            arrayOf<Any?>(riddenByEntity)
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
            Vector3(x1 - 0.1 - this.rand.nextDouble() / 10, y1 - 0.3, z1 - 0.1 - this.rand.nextDouble() / 10),
            arrayOf<Any?>(riddenByEntity)
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
            Vector3(x1 + 0.1 + this.rand.nextDouble() / 10, y1 - 0.3, z1 - 0.1 - this.rand.nextDouble() / 10),
            arrayOf<Any?>(riddenByEntity)
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 + 0.4, y2, z2),
            Vector3(x1 + 0.3, y1 - 0.3, z1),
            arrayOf<Any?>(riddenByEntity)
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 - 0.4, y2, z2),
            Vector3(x1 - 0.3, y1 - 0.3, z1),
            arrayOf<Any?>(riddenByEntity)
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2, y2, z2 + 0.4),
            Vector3(x1, y1 - 0.3, z1 + 0.3),
            arrayOf<Any?>(riddenByEntity)
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2, y2, z2 - 0.4),
            Vector3(x1, y1 - 0.3, z1 - 0.3),
            arrayOf<Any?>(riddenByEntity)
        )
    }

    override fun getFuelTankCapacity(): Int {
        return 2800
    }

    override fun getRenderOffsetY(): Float {
        return -0.15f
    }

    override fun isDockValid(dock: IFuelDock?): Boolean {
        return dock is TileEntityAdvLandingPad
    }

    override fun addFuel(liquid: FluidStack?, doFill: Boolean): Int {
        return GSUtils.fillWithGCFuel(this.fuelTank, liquid, doFill, this)
    }
}
