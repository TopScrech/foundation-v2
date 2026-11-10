package micdoodle8.mods.galacticraft.core.util

import net.minecraft.util.DamageSource

open class DamageSourceGC constructor(damageType: String) : DamageSource(damageType) {
    companion object {
        @JvmField
        val spaceshipCrash: DamageSourceGC =
            DamageSourceGC("spaceship_crash").setDamageBypassesArmor().setExplosion() as DamageSourceGC

        @JvmField
        val oxygenSuffocation: DamageSourceGC =
            DamageSourceGC("oxygen_suffocation").setDamageBypassesArmor().setDamageIsAbsolute() as DamageSourceGC

        @JvmField
        val thermal: DamageSourceGC =
            DamageSourceGC("thermal").setDamageBypassesArmor().setDamageIsAbsolute() as DamageSourceGC

        @JvmField
        val acid: DamageSourceGC = DamageSourceGC("sulphuric_acid")
    }
}
