package galaxyspace.core.mixins

import galaxyspace.core.hooks.AlienVillagerTrades

import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS

import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Overwrite
import galaxyspace.core.mixins.MixinEntityAlienVillager
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.entities.EntityAlienVillager
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack
import net.minecraft.village.MerchantRecipeList
import org.spongepowered.asm.mixin.Mixin

@Mixin(value = [EntityAlienVillager::class], remap = false)
open class MixinEntityAlienVillager {
    @Shadow
    private var buyingList: MerchantRecipeList? = null

    /**
     * @author ViTold
     * @reason
     */
    @Overwrite
    private fun populateBuyingList() {
        val e: EntityAlienVillager = this as Any as EntityAlienVillager

        var buyingList: MerchantRecipeList? = this.buyingList
        if (buyingList == null) {
            buyingList = MerchantRecipeList()
        }

        for (tradeList: EntityAlienVillager.ITradeList in AlienVillagerTrades.entries) {
            tradeList.modifyMerchantRecipeList(buyingList, e.getEntityWorld().rand)
        }

        this.buyingList = buyingList
    }

}
