package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import foundation.item.FoundationItemVariants
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.entity.monster.EntityWitch
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.level.Level as World

open class EntityEvolvedWitch constructor(world: World) : EntityWitch(world), IEntityBreathable {
    override fun canBreath(): Boolean {
        return true
    }

    protected open fun addRandomDrop() {
        when (this.rand.nextInt(10)) {
            0, 1, 9 ->             //Dehydrated carrot
                this.entityDropItem(foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 1), 0.0f)

            2, 3 -> this.entityDropItem(ItemStack(Blocks.GLOWSTONE, 1), 0.0f)
            4, 5 -> this.entityDropItem(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 20), 0.0f)
            6 ->             //Oxygen tank half empty or less
                this.entityDropItem(ItemOxygenTank.stack(requireNotNull(GCItems.oxTankHeavy), 1, 901 + this.rand.nextInt(900)), 0.0f)

            7 -> this.dropItem(GCItems.oxMask, 1)
            8 -> this.dropItem(GCItems.oxygenVent, 1)
        }
    }

    override fun dropLoot(wasRecentlyHit: Boolean, lootingModifier: Int, source: DamageSource) {
        super.dropLoot(wasRecentlyHit, lootingModifier, source)
        if (wasRecentlyHit && this.rand.nextFloat() < 0.025f + lootingModifier.toFloat() * 0.015f) {
            this.addRandomDrop()
        }
    }
}
