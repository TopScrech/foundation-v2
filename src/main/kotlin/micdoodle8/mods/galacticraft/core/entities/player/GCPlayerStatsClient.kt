package micdoodle8.mods.galacticraft.core.entities.player

import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlatform
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB as AxisAlignedBB
import java.util.ArrayList

abstract class GCPlayerStatsClient {
    abstract fun setGravity(newGravity: EnumGravity?)

    abstract fun isUsingParachute(): Boolean

    abstract fun setUsingParachute(usingParachute: Boolean)

    abstract fun isLastUsingParachute(): Boolean

    abstract fun setLastUsingParachute(lastUsingParachute: Boolean)

    abstract fun isUsingAdvancedGoggles(): Boolean

    abstract fun setUsingAdvancedGoggles(usingAdvancedGoggles: Boolean)

    abstract fun getThermalLevel(): Int

    abstract fun setThermalLevel(thermalLevel: Int)

    abstract fun isThermalLevelNormalising(): Boolean

    abstract fun setThermalLevelNormalising(thermalLevelNormalising: Boolean)

    abstract fun getThirdPersonView(): Int

    abstract fun setThirdPersonView(thirdPersonView: Int)

    abstract fun getTick(): Long

    abstract fun setTick(tick: Long)

    abstract fun isOxygenSetupValid(): Boolean

    abstract fun setOxygenSetupValid(oxygenSetupValid: Boolean)

    abstract fun getBoundingBoxBefore(): AxisAlignedBB?

    abstract fun setBoundingBoxBefore(boundingBoxBefore: AxisAlignedBB?)

    abstract fun isLastOnGround(): Boolean

    abstract fun setLastOnGround(lastOnGround: Boolean)

    abstract fun getDistanceSinceLastStep(): Double

    abstract fun setDistanceSinceLastStep(distanceSinceLastStep: Double)

    abstract fun getLastStep(): Int

    abstract fun setLastStep(lastStep: Int)

    abstract fun isInFreefall(): Boolean

    abstract fun setInFreefall(inFreefall: Boolean)

    abstract fun isInFreefallLast(): Boolean

    abstract fun setInFreefallLast(inFreefallLast: Boolean)

    abstract fun isInFreefallFirstCheck(): Boolean

    abstract fun setInFreefallFirstCheck(inFreefallFirstCheck: Boolean)

    abstract fun getDownMotionLast(): Double

    abstract fun setDownMotionLast(downMotionLast: Double)

    abstract fun isLastRidingCameraZoomEntity(): Boolean

    abstract fun setLastRidingCameraZoomEntity(lastRidingCameraZoomEntity: Boolean)

    abstract fun getLandingTicks(): Int

    abstract fun setLandingTicks(landingTicks: Int)

    abstract fun getGdir(): EnumGravity?

    abstract fun setGdir(gdir: EnumGravity?)

    abstract fun getGravityTurnRate(): Float

    abstract fun setGravityTurnRate(gravityTurnRate: Float)

    abstract fun getGravityTurnRatePrev(): Float

    abstract fun setGravityTurnRatePrev(gravityTurnRatePrev: Float)

    abstract fun getGravityTurnVecX(): Float

    abstract fun setGravityTurnVecX(gravityTurnVecX: Float)

    abstract fun getGravityTurnVecY(): Float

    abstract fun setGravityTurnVecY(gravityTurnVecY: Float)

    abstract fun getGravityTurnVecZ(): Float

    abstract fun setGravityTurnVecZ(gravityTurnVecZ: Float)

    abstract fun getGravityTurnYaw(): Float

    abstract fun setGravityTurnYaw(gravityTurnYaw: Float)

    abstract fun getSpaceRaceInviteTeamID(): Int

    abstract fun setSpaceRaceInviteTeamID(spaceRaceInviteTeamID: Int)

    abstract fun isLastZoomed(): Boolean

    abstract fun setLastZoomed(lastZoomed: Boolean)

    abstract fun getBuildFlags(): Int

    abstract fun setBuildFlags(buildFlags: Int)

    abstract fun isSsOnGroundLast(): Boolean

    abstract fun setSsOnGroundLast(ssOnGroundLast: Boolean)

    abstract fun getFreefallHandler(): FreefallHandler?

    abstract fun setFreefallHandler(freefallHandler: FreefallHandler?)

    abstract fun getUnlockedSchematics(): ArrayList<ISchematicPage>?

    abstract fun setUnlockedSchematics(unlockedSchematics: ArrayList<ISchematicPage>?)

    abstract fun getMaxLandingticks(): Int

    abstract fun getLandingYOffset(): FloatArray?

    abstract fun setLandingYOffset(landingYOffset: FloatArray?)

    abstract fun setDungeonDirection(dir: Float)

    abstract fun getDungeonDirection(): Float

    abstract fun startPlatformAscent(noCollide: TileEntityPlatform?, moving: TileEntityPlatform?, target: Double)

    abstract fun finishPlatformAscent()

    abstract fun getPlatformControlled(): Boolean

    abstract fun getPlatformVelocity(posY: Double): Double

    companion object {
        @JvmStatic
        fun get(entity: Entity): GCPlayerStatsClient? {
            return entity.getCapability<GCPlayerStatsClient?>((GCCapabilities.GC_STATS_CLIENT_CAPABILITY)!!, null)
        }
    }
}
