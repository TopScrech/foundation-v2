package asmodeuscore.api.dimension

import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.api.dimension.IAdvancedSpace.StarType
import asmodeuscore.api.dimension.IAdvancedSpace.StarClass
import asmodeuscore.api.dimension.IAdvancedSpace.TempBody
import asmodeuscore.api.dimension.IAdvancedSpace
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.Vec3i


interface IAdvancedSpace : IGalacticraftWorldProvider {
    enum class TempBody {
        HOT, WARM, COMFORT, COOL, COLD, ICY
    }

    @Deprecated("")
    enum class StarClass {
        SUBDWARF,
        DWARF,
        SUBGIANT,
        GIANT,
        SUPERGIANT,
        HYPERGIANT,
        BLACKHOLE
    }


    @Deprecated("")
    enum class TypeBody {
        STAR, ASTEROID, PLANET, MOON, SATELLITE
    }

    enum class StarType {
        SUBDWARF,
        DWARF,
        SUBGIANT,
        GIANT,
        SUPERGIANT,
        HYPERGIANT,
        BLACKHOLE
    }

    enum class StarColor
        (r: Int, g: Int, b: Int) {
        BROWN(74, 2, 3),
        RED(225, 2, 0),
        ORANGE(255, 121, 0),
        YELLOW(254, 230, 8),
        WHITE(150, 150, 150),
        LIGHTBLUE(54, 199, 254),
        BLUE(11, 42, 241),

        M1(205, 88, 4),
        M2(195, 136, 66),
        M3(237, 212, 155),

        K1(236, 207, 153),
        K2(242, 228, 194),
        K3(242, 235, 224),

        G1(245, 240, 233),
        G2(242, 240, 235),
        G3(235, 234, 230),

        F1(243, 241, 238),
        F2(240, 241, 240),
        F3(237, 240, 242),

        A1(221, 227, 234),
        A2(225, 232, 243),
        A3(202, 219, 235),

        B1(221, 232, 246),
        B2(210, 232, 249),
        B3(200, 234, 253),

        O1(208, 234, 254),
        O2(173, 216, 252),
        O3(197, 233, 255);

        private val color: Vec3i

        init {
            this.color = Vec3i(r, g, b)
        }

        fun getColor(): Vec3i {
            return color
        }
    }

    enum class ClassBody {
        //PLANETS
        SELENA, DESERT, TERRA, OCEANIDE, GASGIANT, ICEGIANT, ASTEROID, TITAN, ICEWORLD
    }


    fun getSolarWindMultiplier(): Double

    fun isRetrogradeRotation(): Boolean {
        return false
    }

    fun getThermalLevelModifier(player: EntityPlayer?): Float {
        return this.getThermalLevelModifier()
    }
}
