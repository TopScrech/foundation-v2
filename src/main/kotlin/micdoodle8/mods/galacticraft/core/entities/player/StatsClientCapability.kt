package micdoodle8.mods.galacticraft.core.entities.player

import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlatform
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.phys.AABB as AxisAlignedBB
import kotlin.math.abs

open class StatsClientCapability : GCPlayerStatsClient() {
    @JvmField
    var usingParachute: Boolean = false

    @JvmField
    var lastUsingParachute: Boolean = false

    @JvmField
    var usingAdvancedGoggles: Boolean = false

    @JvmField
    var thermalLevel: Int = 0

    @JvmField
    var thermalLevelNormalising: Boolean = false

    @JvmField
    var thirdPersonView: Int = 0

    @JvmField
    var tick: Long = 0

    @JvmField
    var oxygenSetupValid: Boolean = true

    @JvmField
    var boundingBoxBefore: AxisAlignedBB? = null

    @JvmField
    var lastOnGround: Boolean = false

    @JvmField
    var distanceSinceLastStep: Double = 0.0

    @JvmField
    var lastStep: Int = 0

    @JvmField
    var inFreefall: Boolean = false

    @JvmField
    var inFreefallLast: Boolean = false

    @JvmField
    var inFreefallFirstCheck: Boolean = false

    @JvmField
    var downMotionLast: Double = 0.0

    @JvmField
    var lastRidingCameraZoomEntity: Boolean = false

    @JvmField
    var landingTicks: Int = 0

    @JvmField
    var landingYOffset: FloatArray? = FloatArray(MAX_LANDINGTICKS + 1)

    @JvmField
    var platformControlled: Boolean = false
    private var platformTarget: TileEntityPlatform? = null
    private var platformMoving: TileEntityPlatform? = null
    private var platformTargetY = 0.0
    private var platformVelocityTarget = 0.0
    private var platformVelocityCurrent = 0.0
    private var platformPacketSent = false

    @JvmField
    var gdir: EnumGravity = EnumGravity.down

    @JvmField
    var gravityTurnRate: Float = 0f

    @JvmField
    var gravityTurnRatePrev: Float = 0f

    @JvmField
    var gravityTurnVecX: Float = 0f

    @JvmField
    var gravityTurnVecY: Float = 0f

    @JvmField
    var gravityTurnVecZ: Float = 0f

    @JvmField
    var gravityTurnYaw: Float = 0f

    @JvmField
    var spaceRaceInviteTeamID: Int = 0

    @JvmField
    var lastZoomed: Boolean = false

    @JvmField
    var buildFlags: Int = -1

    @JvmField
    var ssOnGroundLast: Boolean = false
    private var dungeonDirection = 0f

    @JvmField
    var freefallHandler: FreefallHandler? = FreefallHandler(this)

    @JvmField
    var unlockedSchematics: ArrayList<ISchematicPage>? = ArrayList<ISchematicPage>()

    override fun setGravity(newGravity: EnumGravity?) {
        if (this.gdir == newGravity) {
            return
        }
        this.gravityTurnRate = 0.0f
        this.gravityTurnRatePrev = this.gravityTurnRate
        val turnSpeed: Float = 0.05f
        this.gravityTurnVecX = 0.0f
        this.gravityTurnVecY = 0.0f
        this.gravityTurnVecZ = 0.0f
        this.gravityTurnYaw = 0.0f

        when (this.gdir.getIntValue()) {
            1 -> when ((newGravity)!!.getIntValue()) {
                1 -> {}
                2 -> this.gravityTurnVecX = -2.0f
                3 -> {
                    this.gravityTurnVecY = -1.0f
                    this.gravityTurnYaw = -90.0f
                }

                4 -> {
                    this.gravityTurnVecY = 1.0f
                    this.gravityTurnYaw = 90.0f
                }

                5 -> this.gravityTurnVecX = 1.0f
                6 -> this.gravityTurnVecX = -1.0f
            }

            2 -> when ((newGravity)!!.getIntValue()) {
                1 -> this.gravityTurnVecX = -2.0f
                2 -> {}
                3 -> {
                    this.gravityTurnVecY = 1.0f
                    this.gravityTurnYaw = 90.0f
                }

                4 -> {
                    this.gravityTurnVecY = -1.0f
                    this.gravityTurnYaw = -90.0f
                }

                5 -> this.gravityTurnVecX = -1.0f
                6 -> this.gravityTurnVecX = 1.0f
            }

            3 -> when ((newGravity)!!.getIntValue()) {
                1 -> {
                    this.gravityTurnVecY = 1.0f
                    this.gravityTurnYaw = 90.0f
                }

                2 -> {
                    this.gravityTurnVecY = -1.0f
                    this.gravityTurnYaw = -90.0f
                }

                3 -> {}
                4 -> this.gravityTurnVecZ = -2.0f
                5 -> {
                    this.gravityTurnVecZ = -1.0f
                    this.gravityTurnYaw = -180.0f
                }

                6 -> this.gravityTurnVecZ = 1.0f
            }

            4 -> when ((newGravity)!!.getIntValue()) {
                1 -> {
                    this.gravityTurnVecY = -1.0f
                    this.gravityTurnYaw = -90.0f
                }

                2 -> {
                    this.gravityTurnVecY = 1.0f
                    this.gravityTurnYaw = 90.0f
                }

                3 -> this.gravityTurnVecZ = -2.0f
                4 -> {}
                5 -> {
                    this.gravityTurnVecZ = 1.0f
                    this.gravityTurnYaw = -180.0f
                }

                6 -> this.gravityTurnVecZ = -1.0f
            }

            5 -> when ((newGravity)!!.getIntValue()) {
                1 -> this.gravityTurnVecX = -1.0f
                2 -> this.gravityTurnVecX = 1.0f
                3 -> {
                    this.gravityTurnVecZ = 1.0f
                    this.gravityTurnYaw = 180.0f
                }

                4 -> {
                    this.gravityTurnVecZ = -1.0f
                    this.gravityTurnYaw = 180.0f
                }

                5 -> {}
                6 -> this.gravityTurnVecX = -2.0f
            }

            6 -> when ((newGravity)!!.getIntValue()) {
                1 -> this.gravityTurnVecX = 1.0f
                2 -> this.gravityTurnVecX = -1.0f
                3 -> this.gravityTurnVecZ = -1.0f
                4 -> this.gravityTurnVecZ = 1.0f
                5 -> this.gravityTurnVecX = -2.0f
                6 -> {}
            }
        }

        this.gdir = (newGravity)!!
    }

    override fun isUsingParachute(): Boolean {
        return usingParachute
    }

    override fun setUsingParachute(usingParachute: Boolean) {
        this.usingParachute = usingParachute
    }

    override fun isLastUsingParachute(): Boolean {
        return lastUsingParachute
    }

    override fun setLastUsingParachute(lastUsingParachute: Boolean) {
        this.lastUsingParachute = lastUsingParachute
    }

    override fun isUsingAdvancedGoggles(): Boolean {
        return usingAdvancedGoggles
    }

    override fun setUsingAdvancedGoggles(usingAdvancedGoggles: Boolean) {
        this.usingAdvancedGoggles = usingAdvancedGoggles
    }

    override fun getThermalLevel(): Int {
        return thermalLevel
    }

    override fun setThermalLevel(thermalLevel: Int) {
        this.thermalLevel = thermalLevel
    }

    override fun isThermalLevelNormalising(): Boolean {
        return thermalLevelNormalising
    }

    override fun setThermalLevelNormalising(thermalLevelNormalising: Boolean) {
        this.thermalLevelNormalising = thermalLevelNormalising
    }

    override fun getThirdPersonView(): Int {
        return thirdPersonView
    }

    override fun setThirdPersonView(thirdPersonView: Int) {
        this.thirdPersonView = thirdPersonView
    }

    override fun getTick(): Long {
        return tick
    }

    override fun setTick(tick: Long) {
        this.tick = tick
    }

    override fun isOxygenSetupValid(): Boolean {
        return oxygenSetupValid
    }

    override fun setOxygenSetupValid(oxygenSetupValid: Boolean) {
        this.oxygenSetupValid = oxygenSetupValid
    }

    override fun getBoundingBoxBefore(): AxisAlignedBB? {
        return boundingBoxBefore
    }

    override fun setBoundingBoxBefore(boundingBoxBefore: AxisAlignedBB?) {
        this.boundingBoxBefore = boundingBoxBefore
    }

    override fun isLastOnGround(): Boolean {
        return lastOnGround
    }

    override fun setLastOnGround(lastOnGround: Boolean) {
        this.lastOnGround = lastOnGround
    }

    override fun getDistanceSinceLastStep(): Double {
        return distanceSinceLastStep
    }

    override fun setDistanceSinceLastStep(distanceSinceLastStep: Double) {
        this.distanceSinceLastStep = distanceSinceLastStep
    }

    override fun getLastStep(): Int {
        return lastStep
    }

    override fun setLastStep(lastStep: Int) {
        this.lastStep = lastStep
    }

    override fun isInFreefall(): Boolean {
        return inFreefall
    }

    override fun setInFreefall(inFreefall: Boolean) {
        this.inFreefall = inFreefall
    }

    override fun isInFreefallLast(): Boolean {
        return inFreefallLast
    }

    override fun setInFreefallLast(inFreefallLast: Boolean) {
        this.inFreefallLast = inFreefallLast
    }

    override fun isInFreefallFirstCheck(): Boolean {
        return inFreefallFirstCheck
    }

    override fun setInFreefallFirstCheck(inFreefallFirstCheck: Boolean) {
        this.inFreefallFirstCheck = inFreefallFirstCheck
    }

    override fun getDownMotionLast(): Double {
        return downMotionLast
    }

    override fun setDownMotionLast(downMotionLast: Double) {
        this.downMotionLast = downMotionLast
    }

    override fun isLastRidingCameraZoomEntity(): Boolean {
        return lastRidingCameraZoomEntity
    }

    override fun setLastRidingCameraZoomEntity(lastRidingCameraZoomEntity: Boolean) {
        this.lastRidingCameraZoomEntity = lastRidingCameraZoomEntity
    }

    override fun getLandingTicks(): Int {
        return landingTicks
    }

    override fun setLandingTicks(landingTicks: Int) {
        this.landingTicks = landingTicks
    }

    override fun getGdir(): EnumGravity? {
        return gdir
    }

    override fun setGdir(gdir: EnumGravity?) {
        this.gdir = (gdir)!!
    }

    override fun getGravityTurnRate(): Float {
        return gravityTurnRate
    }

    override fun setGravityTurnRate(gravityTurnRate: Float) {
        this.gravityTurnRate = gravityTurnRate
    }

    override fun getGravityTurnRatePrev(): Float {
        return gravityTurnRatePrev
    }

    override fun setGravityTurnRatePrev(gravityTurnRatePrev: Float) {
        this.gravityTurnRatePrev = gravityTurnRatePrev
    }

    override fun getGravityTurnVecX(): Float {
        return gravityTurnVecX
    }

    override fun setGravityTurnVecX(gravityTurnVecX: Float) {
        this.gravityTurnVecX = gravityTurnVecX
    }

    override fun getGravityTurnVecY(): Float {
        return gravityTurnVecY
    }

    override fun setGravityTurnVecY(gravityTurnVecY: Float) {
        this.gravityTurnVecY = gravityTurnVecY
    }

    override fun getGravityTurnVecZ(): Float {
        return gravityTurnVecZ
    }

    override fun setGravityTurnVecZ(gravityTurnVecZ: Float) {
        this.gravityTurnVecZ = gravityTurnVecZ
    }

    override fun getGravityTurnYaw(): Float {
        return gravityTurnYaw
    }

    override fun setGravityTurnYaw(gravityTurnYaw: Float) {
        this.gravityTurnYaw = gravityTurnYaw
    }

    override fun getSpaceRaceInviteTeamID(): Int {
        return spaceRaceInviteTeamID
    }

    override fun setSpaceRaceInviteTeamID(spaceRaceInviteTeamID: Int) {
        this.spaceRaceInviteTeamID = spaceRaceInviteTeamID
    }

    override fun isLastZoomed(): Boolean {
        return lastZoomed
    }

    override fun setLastZoomed(lastZoomed: Boolean) {
        this.lastZoomed = lastZoomed
    }

    override fun getBuildFlags(): Int {
        return buildFlags
    }

    override fun setBuildFlags(buildFlags: Int) {
        this.buildFlags = buildFlags
    }

    override fun isSsOnGroundLast(): Boolean {
        return ssOnGroundLast
    }

    override fun setSsOnGroundLast(ssOnGroundLast: Boolean) {
        this.ssOnGroundLast = ssOnGroundLast
    }

    override fun getFreefallHandler(): FreefallHandler? {
        return freefallHandler
    }

    override fun setFreefallHandler(freefallHandler: FreefallHandler?) {
        this.freefallHandler = freefallHandler
    }

    override fun getUnlockedSchematics(): ArrayList<ISchematicPage>? {
        return unlockedSchematics
    }

    override fun setUnlockedSchematics(unlockedSchematics: ArrayList<ISchematicPage>?) {
        this.unlockedSchematics = unlockedSchematics
    }

    override fun getMaxLandingticks(): Int {
        return MAX_LANDINGTICKS
    }

    override fun getLandingYOffset(): FloatArray? {
        return landingYOffset
    }

    override fun setLandingYOffset(landingYOffset: FloatArray?) {
        this.landingYOffset = landingYOffset
    }

    override fun setDungeonDirection(dir: Float) {
        this.dungeonDirection = dir
    }

    override fun getDungeonDirection(): Float {
        return this.dungeonDirection
    }

    override fun getPlatformControlled(): Boolean {
        return this.platformControlled
    }

    override fun startPlatformAscent(noCollide: TileEntityPlatform?, moving: TileEntityPlatform?, target: Double) {
        this.platformControlled = true
        this.platformTarget = noCollide
        this.platformMoving = moving
        (noCollide)!!.markNoCollide(0, true)
        this.platformTargetY = target
        this.platformVelocityCurrent = 0.0
        this.platformVelocityTarget = 0.0
        this.platformPacketSent = false
        (GalacticraftCore.packetPipeline)!!.sendToServer(
            PacketSimple(
                PacketSimple.EnumSimplePacket.S_NOCLIP_PLAYER,
                GCCoreUtil.getDimensionID((noCollide)!!),
                arrayOf<Any>(true)
            )
        )
    }

    override fun finishPlatformAscent() {
    }

    override fun getPlatformVelocity(posY: Double): Double {
        val delta: Double = this.platformTargetY - posY + 0.03
        if (abs(delta) < 0.04) {
            this.platformVelocityCurrent = 0.0
            this.platformVelocityTarget = 0.0
            this.platformTarget!!.markNoCollide(0, false)
            this.platformControlled = false
            this.platformTarget!!.stopMoving()
            this.platformMoving!!.stopMoving()
            if (!this.platformPacketSent) (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_NOCLIP_PLAYER,
                    GCCoreUtil.getDimensionID((this.platformTarget)!!),
                    arrayOf<Any>(false)
                )
            )
            return 0.0
        } else {
            if (delta > 0.0) {
                this.platformVelocityTarget =
                    if (delta < 1.0 + 8 * this.platformVelocityCurrent * this.platformVelocityCurrent) 0.08 else 0.45
                if (delta < 0.6 && !this.platformPacketSent) {
                    (GalacticraftCore.packetPipeline)!!.sendToServer(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.S_NOCLIP_PLAYER,
                            GCCoreUtil.getDimensionID((this.platformTarget)!!),
                            arrayOf<Any>(false)
                        )
                    )
                    this.platformPacketSent = true
                }
            } else if (delta < 0.0) {
                this.platformVelocityTarget =
                    if (delta > -1.0 - 8 * this.platformVelocityCurrent * this.platformVelocityCurrent) -0.08 else -0.45
                if (delta > -1.0 && !this.platformPacketSent) {
                    (GalacticraftCore.packetPipeline)!!.sendToServer(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.S_NOCLIP_PLAYER,
                            GCCoreUtil.getDimensionID((this.platformTarget)!!),
                            arrayOf<Any>(false)
                        )
                    )
                    this.platformPacketSent = true
                }
            }
            if (this.platformVelocityCurrent < this.platformVelocityTarget) {
                this.platformVelocityCurrent += 0.036
                if (this.platformVelocityCurrent > this.platformVelocityTarget) {
                    this.platformVelocityCurrent = this.platformVelocityTarget
                }
            } else if (this.platformVelocityCurrent > this.platformVelocityTarget) {
                this.platformVelocityCurrent -= 0.036
                if (this.platformVelocityCurrent < this.platformVelocityTarget) {
                    this.platformVelocityCurrent = this.platformVelocityTarget
                }
            }
        }
        return this.platformVelocityCurrent
    }

    companion object {
        const val MAX_LANDINGTICKS: Int = 15
    }
}
