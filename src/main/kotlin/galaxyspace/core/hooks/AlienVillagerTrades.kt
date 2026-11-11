package galaxyspace.core.hooks

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import foundation.item.FoundationItemVariants
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.entities.EntityAlienVillager
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack

/** Initialized when a villager first creates its trades, after item registration */
object AlienVillagerTrades {
    val entries = arrayOf<EntityAlienVillager.ITradeList>(
        EntityAlienVillager.ItemAndEmeraldToItem(
            foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1),
            EntityAlienVillager.PriceInfo(40, 55),
            BasicItems.SCHEMATIC_BOX.getItemStack()
        ),


        EntityAlienVillager.ListItemForEmeralds(
            ItemStack(GCItems.oxMask, 1, 0),
            EntityAlienVillager.PriceInfo(1, 2)
        ),
        EntityAlienVillager.ListItemForEmeralds(
            ItemOxygenTank.stack(requireNotNull(GCItems.oxTankLight), 1, 235),
            EntityAlienVillager.PriceInfo(3, 4)
        ),
        EntityAlienVillager.ListItemForEmeralds(
            ItemStack(GCItems.oxygenGear, 1, 0),
            EntityAlienVillager.PriceInfo(3, 4)
        ),
        EntityAlienVillager.ListItemForEmeralds(
            ItemStack(GCItems.fuelCanister, 1, 317),
            EntityAlienVillager.PriceInfo(3, 4)
        ),
        EntityAlienVillager.ListItemForEmeralds(
            FoundationItemVariants.stack(requireNotNull(GCItems.parachute), 1, 0),
            EntityAlienVillager.PriceInfo(1, 2)
        ),
        EntityAlienVillager.ListItemForEmeralds(
            ItemStack(GCItems.battery, 1, 58),
            EntityAlienVillager.PriceInfo(2, 4)
        ),
        EntityAlienVillager.ItemAndEmeraldToItem(
            ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY),
            EntityAlienVillager.PriceInfo(1, 1),
            foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 1)
        ),  //carrots = also yields a tin!
        EntityAlienVillager.ListItemForEmeralds(
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, ItemBasic.WAFER_BASIC),
            EntityAlienVillager.PriceInfo(3, 4)
        ),
        EntityAlienVillager.ItemAndEmeraldToItem(
            foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 0),
            EntityAlienVillager.PriceInfo(3, 5),
            foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1)
        ),  //Exchange buggy and rocket schematics
        EntityAlienVillager.ItemAndEmeraldToItem(
            foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1),
            EntityAlienVillager.PriceInfo(3, 5),
            foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 0)
        ),  //Exchange buggy and rocket schematics
        EntityAlienVillager.ItemAndEmeraldToItem(
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 2, 3),
            EntityAlienVillager.PriceInfo(1, 1),
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 6)
        ),  //Compressed Tin - needed to craft a Fuel Loader
        EntityAlienVillager.ItemAndEmeraldToItem(
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 2, 4),
            EntityAlienVillager.PriceInfo(1, 1),
            FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 7)
        ),  //Compressed Copper - needed to craft a Fuel Loader
        EntityAlienVillager.EmeraldForItems(
            ItemStack(Blocks.SAPLING, 1, 3),
            EntityAlienVillager.PriceInfo(11, 39)
        ) //The one thing Alien Villagers don't have and can't get is jungle trees...
    )
}
