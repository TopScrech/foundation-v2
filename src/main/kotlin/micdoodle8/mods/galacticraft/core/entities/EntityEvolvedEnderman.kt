package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraft.world.World

open class EntityEvolvedEnderman constructor(world: World) : EntityEnderman(world), IEntityBreathable {
    override fun canBreath(): Boolean {
        return true
    }

    override fun dropLoot(wasRecentlyHit: Boolean, lootingModifier: Int, source: DamageSource) {
        super.dropLoot(wasRecentlyHit, lootingModifier, source)
        if (wasRecentlyHit && this.rand.nextFloat() < 0.025f + lootingModifier.toFloat() * 0.015f) {
            this.addRandomDrop()
        }
    }

    protected open fun addRandomDrop() {
        when (this.rand.nextInt(10)) {
            0, 1, 2, 3, 4, 5 -> this.dropItem(Items.ENDER_PEARL, 1)
            6 ->             //Oxygen tank half empty or less
                this.entityDropItem(ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0f)

            7, 8 -> this.dropItem(Items.ENDER_EYE, 1)
            9 -> this.dropItem(GCItems.oxygenConcentrator, 1)
            10 -> this.dropItem(GCItems.oxMask, 1)
        }
    }
}
