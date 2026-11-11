package micdoodle8.mods.galacticraft.core.entities.player

import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import net.minecraft.world.phys.AABB as AxisAlignedBB
import java.util.ArrayList

interface IStatsClientCapability {
    fun setGravity(newGravity: EnumGravity?)

    fun isUsingParachute(): Boolean

    fun setUsingParachute(usingParachute: Boolean)

    fun isLastUsingParachute(): Boolean

    fun setLastUsingParachute(lastUsingParachute: Boolean)

    fun isUsingAdvancedGoggles(): Boolean

    fun setUsingAdvancedGoggles(usingAdvancedGoggles: Boolean)

    fun getThermalLevel(): Int

    fun setThermalLevel(thermalLevel: Int)

    fun isThermalLevelNormalising(): Boolean

    fun setThermalLevelNormalising(thermalLevelNormalising: Boolean)

    fun getThirdPersonView(): Int

    fun setThirdPersonView(thirdPersonView: Int)

    fun getTick(): Long

    fun setTick(tick: Long)

    fun isOxygenSetupValid(): Boolean

    fun setOxygenSetupValid(oxygenSetupValid: Boolean)

    fun getBoundingBoxBefore(): AxisAlignedBB?

    fun setBoundingBoxBefore(boundingBoxBefore: AxisAlignedBB?)

    fun isLastOnGround(): Boolean

    fun setLastOnGround(lastOnGround: Boolean)

    fun getDistanceSinceLastStep(): Double

    fun setDistanceSinceLastStep(distanceSinceLastStep: Double)

    fun getLastStep(): Int

    fun setLastStep(lastStep: Int)

    fun isInFreefall(): Boolean

    fun setInFreefall(inFreefall: Boolean)

    fun isInFreefallLast(): Boolean

    fun setInFreefallLast(inFreefallLast: Boolean)

    fun isInFreefallFirstCheck(): Boolean

    fun setInFreefallFirstCheck(inFreefallFirstCheck: Boolean)

    fun getDownMotionLast(): Double

    fun setDownMotionLast(downMotionLast: Double)

    fun isLastRidingCameraZoomEntity(): Boolean

    fun setLastRidingCameraZoomEntity(lastRidingCameraZoomEntity: Boolean)

    fun getLandingTicks(): Int

    fun setLandingTicks(landingTicks: Int)

    fun getGdir(): EnumGravity?

    fun setGdir(gdir: EnumGravity?)

    fun getGravityTurnRate(): Float

    fun setGravityTurnRate(gravityTurnRate: Float)

    fun getGravityTurnRatePrev(): Float

    fun setGravityTurnRatePrev(gravityTurnRatePrev: Float)

    fun getGravityTurnVecX(): Float

    fun setGravityTurnVecX(gravityTurnVecX: Float)

    fun getGravityTurnVecY(): Float

    fun setGravityTurnVecY(gravityTurnVecY: Float)

    fun getGravityTurnVecZ(): Float

    fun setGravityTurnVecZ(gravityTurnVecZ: Float)

    fun getGravityTurnYaw(): Float

    fun setGravityTurnYaw(gravityTurnYaw: Float)

    fun getSpaceRaceInviteTeamID(): Int

    fun setSpaceRaceInviteTeamID(spaceRaceInviteTeamID: Int)

    fun isLastZoomed(): Boolean

    fun setLastZoomed(lastZoomed: Boolean)

    fun getBuildFlags(): Int

    fun setBuildFlags(buildFlags: Int)

    fun isSsOnGroundLast(): Boolean

    fun setSsOnGroundLast(ssOnGroundLast: Boolean)

    fun getFreefallHandler(): FreefallHandler?

    fun setFreefallHandler(freefallHandler: FreefallHandler?)

    fun getUnlockedSchematics(): ArrayList<ISchematicPage>?

    fun setUnlockedSchematics(unlockedSchematics: ArrayList<ISchematicPage>?)

    fun getMaxLandingticks(): Int

    fun getLandingYOffset(): FloatArray?

    fun setLandingYOffset(landingYOffset: FloatArray?)
}
