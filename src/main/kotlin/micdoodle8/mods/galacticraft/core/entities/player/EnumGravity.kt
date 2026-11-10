package micdoodle8.mods.galacticraft.core.entities.player


enum class EnumGravity
    (
    private val intValue: Int,
    private val pitchGravityX: Float,
    private val pitchGravityY: Float,
    private val yawGravityX: Float,
    private val yawGravityY: Float,
    private val yawGravityZ: Float,
    private val thetaX: Float,
    private val thetaZ: Float,
    private val sneakVecX: Float,
    private val sneakVecY: Float,
    private val sneakVecZ: Float,
    private val eyeVecX: Float,
    private val eyeVecY: Float,
    private val eyeVecZ: Float
) {
    down(0, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f),
    up(1, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f),
    west(2, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f),
    east(3, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f),
    south(4, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.5f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f),
    north(5, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f);

    fun getIntValue(): Int {
        return intValue
    }

    fun getPitchGravityX(): Float {
        return pitchGravityX
    }

    fun getPitchGravityY(): Float {
        return pitchGravityY
    }

    fun getYawGravityX(): Float {
        return yawGravityX
    }

    fun getYawGravityY(): Float {
        return yawGravityY
    }

    fun getYawGravityZ(): Float {
        return yawGravityZ
    }

    fun getThetaX(): Float {
        return thetaX
    }

    fun getThetaZ(): Float {
        return thetaZ
    }

    fun getSneakVecX(): Float {
        return sneakVecX
    }

    fun getSneakVecY(): Float {
        return sneakVecY
    }

    fun getSneakVecZ(): Float {
        return sneakVecZ
    }

    fun getEyeVecX(): Float {
        return eyeVecX
    }

    fun getEyeVecY(): Float {
        return eyeVecY
    }

    fun getEyeVecZ(): Float {
        return eyeVecZ
    }

    companion object {
        private val GDirections = arrayOf<EnumGravity>(down, up, west, east, south, north)

        @JvmStatic
        fun getGDirections(): Array<EnumGravity> {
            return GDirections
        }
    }
}
