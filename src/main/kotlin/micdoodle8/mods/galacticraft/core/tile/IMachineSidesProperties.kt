package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.tile.IMachineSides.Face

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.util.IStringSerializable

/**
 * Used to create the blockState property corresponding to the machine sides,
 * and to correspondingly constrain the allowable faces.
 *
 * It's not compulsory to use this, a block could create its blockState model
 * from the machine sides in some different way.
 */
open class IMachineSidesProperties
constructor(private val filter: Predicate<MachineSidesModel>, private val toFaces: Array<IMachineSides.Face>?) {
    @JvmField
    var asProperty: PropertyEnum<MachineSidesModel>

    init {
        this.asProperty = PropertyEnum.create<MachineSidesModel>("msm", MachineSidesModel::class.java, filter)
    }

    /**
     * Default blockState model to use if the tile can't be read
     */
    open fun getDefault(): MachineSidesModel {
        return MachineSidesModel.LEFT1
    }

    /**
     * Allowable faces for the configurable sides, consistent with the blockState variants
     */
    open fun allowableFaces(): Array<IMachineSides.Face>? {
        return this.toFaces
    }

    open fun isValidFor(machineSidesModel: MachineSidesModel?): Boolean {
        return filter.apply(machineSidesModel)
    }

    /**
     * The strings match the blockState model implementation for rendering
     */
    enum class MachineSidesModel(private val serializedName: String) : IStringSerializable {
        //Don't change the order, the ordinal is important for the predicate definitions
        LEFT1("lr"),
        LEFT2("lb"),
        LEFT3("lu"),
        LEFT4("ld"),
        RIGHT1("rl"),
        RIGHT2("rb"),
        RIGHT3("ru"),
        RIGHT4("rd"),
        REAR1("bl"),
        REAR2("br"),
        REAR3("bu"),
        REAR4("bd"),
        TOP1("ul"),
        TOP2("ur"),
        TOP3("ub"),
        TOP4("ud"),
        BOTTOM1("dl"),
        BOTTOM2("dr"),
        BOTTOM3("db"),
        BOTTOM4("du");

        override fun getName(): String {
            return this.serializedName
        }

        fun validFor(configurationType: IMachineSidesProperties): MachineSidesModel {
            if (configurationType.isValidFor(this)) {
                return this
            }
            return configurationType.getDefault()
        }

        companion object {
            @JvmStatic
             fun oneFacedAll(): Predicate<MachineSidesModel> {
                return object : Predicate<MachineSidesModel> {
                    override fun apply(msm: MachineSidesModel?): Boolean {
                        return (msm!!.ordinal % 4) == 0
                    }
                }
            }

            @JvmStatic
             fun twoFacedAll(): Predicate<MachineSidesModel> {
                return Predicates.alwaysTrue<MachineSidesModel>()
            }

            @JvmStatic
             fun oneFacedHoriz(): Predicate<MachineSidesModel> {
                return object : Predicate<MachineSidesModel> {
                    override fun apply(msm: MachineSidesModel?): Boolean {
                        return msm!!.ordinal < 12 && (msm!!.ordinal % 4) == 0
                    }
                }
            }

            @JvmStatic
             fun twoFacedHoriz(): Predicate<MachineSidesModel> {
                return object : Predicate<MachineSidesModel> {
                    override fun apply(msm: MachineSidesModel?): Boolean {
                        return msm!!.ordinal < 12 && (msm!!.ordinal % 4) < 2
                    }
                }
            }

            @JvmStatic
             fun noneConfigurable(): Predicate<MachineSidesModel> {
                return object : Predicate<MachineSidesModel> {
                    override fun apply(msm: MachineSidesModel?): Boolean {
                        return msm == LEFT1
                    }
                }
            }
        }
    }

    companion object {
        @JvmField
        var NOT_CONFIGURABLE: IMachineSidesProperties = IMachineSidesProperties(
            MachineSidesModel.Companion.noneConfigurable(),
            IMachineSides.Face.Companion.Horizontals
        )

        @JvmField
        var ONEFACE_HORIZ: IMachineSidesProperties = IMachineSidesProperties(
            MachineSidesModel.Companion.oneFacedHoriz(),
            IMachineSides.Face.Companion.Horizontals
        )

        @JvmField
        var ONEFACE: IMachineSidesProperties = IMachineSidesProperties(
            MachineSidesModel.Companion.oneFacedAll(),
            IMachineSides.Face.Companion.AllAvailable
        )

        @JvmField
        var TWOFACES_HORIZ: IMachineSidesProperties = IMachineSidesProperties(
            MachineSidesModel.Companion.twoFacedHoriz(),
            IMachineSides.Face.Companion.Horizontals
        )

        @JvmField
        var TWOFACES_ALL: IMachineSidesProperties = IMachineSidesProperties(
            MachineSidesModel.Companion.twoFacedAll(),
            IMachineSides.Face.Companion.AllAvailable
        )

        @JvmStatic
        fun getModelForTwoFaces(faceA: IMachineSides.Face, faceB: IMachineSides.Face): MachineSidesModel {
            val result: String = faceA.getName() + faceB.getName()
            for (test: MachineSidesModel in MachineSidesModel.entries) {
                if (result == test.name) return test
            }
            return MachineSidesModel.RIGHT1
        }

        /**
         * The return results will all be allowed by the oneFacedAll predicate
         */
        @JvmStatic
        fun getModelForOneFace(allowedSide: IMachineSides.Face): MachineSidesModel {
            when (allowedSide) {
                IMachineSides.Face.RIGHT -> return MachineSidesModel.RIGHT1
                IMachineSides.Face.REAR -> return MachineSidesModel.REAR1
                IMachineSides.Face.TOP -> return MachineSidesModel.TOP1
                IMachineSides.Face.BOTTOM -> return MachineSidesModel.BOTTOM1
                IMachineSides.Face.LEFT -> return MachineSidesModel.LEFT1
                else -> return MachineSidesModel.LEFT1
            }
        }
    }
}
