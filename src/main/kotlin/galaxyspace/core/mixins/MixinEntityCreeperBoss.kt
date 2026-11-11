package galaxyspace.core.mixins

import org.spongepowered.asm.mixin.Overwrite
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCreeperBoss
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import java.util.LinkedList
import java.util.Random

@Mixin(value = [EntityCreeperBoss::class], remap = false)
open class MixinEntityCreeperBoss {
    /**
     * @author ViTold
     * @reason Normal rolling loot
     */
    @Overwrite
    open fun getGuaranteedLoot(rand: Random): ItemStack {
        val stackList: MutableList<ItemStack?> = LinkedList<ItemStack?>()
        stackList.addAll(GalacticraftRegistry.getDungeonLoot(2))

        return stackList.get(rand.nextInt(stackList.size))!!.copy()
    }
}
