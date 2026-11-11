package micdoodle8.mods.galacticraft.core.client.gui

import net.minecraft.network.chat.Component

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.api.recipe.ISchematicResultPage
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.client.gui.container.*
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiJoinSpaceRace
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiNewSpaceRace
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiPreLaunchChecklist
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.inventory.*
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tile.*
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos

import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class GuiHandler : IGuiHandler {
    override fun getServerGuiElement(ID: Int, player: EntityPlayer?, world: World, x: Int, y: Int, z: Int): Any? {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)

        if (playerBase == null) {
            player!!.sendSystemMessage(Component.literal("Galacticraft player instance null server-side. This is a bug."))
            return null
        }

        val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!

        if (ID == GuiIdsCore.ROCKET_INVENTORY && player!!.getVehicle() is EntityTieredRocket) {
            return ContainerRocketInventory(
                player.inventory,
                (player.getVehicle() as EntityTieredRocket?)!!,
                ((player.getVehicle() as EntityTieredRocket).getType())!!,
                player
            )
        } else if (ID == GuiIdsCore.EXTENDED_INVENTORY) {
            return ContainerExtendedInventory((player)!!, stats.getExtendedInventory())
        }

        val pos: BlockPos = BlockPos(x, y, z)
        val tile: TileEntity? = world.getBlockEntity(pos)

        if (tile != null) {
            if (tile is TileEntityCrafting) {
                return ContainerCrafting(player!!.inventory, tile)
            } else if (tile is TileEntityRefinery) {
                return ContainerRefinery(player!!.inventory, tile, player)
            } else if (tile is TileEntityOxygenCollector) {
                return ContainerOxygenCollector(player!!.inventory, tile)
            } else if (tile is TileEntityOxygenDistributor) {
                return ContainerOxygenDistributor(player!!.inventory, tile)
            } else if (tile is TileEntityFuelLoader) {
                return ContainerFuelLoader(player!!.inventory, tile)
            } else if (tile is TileEntityOxygenSealer) {
                return ContainerOxygenSealer(player!!.inventory, tile)
            } else if (tile is TileEntityCargoLoader) {
                return ContainerCargoLoader(player!!.inventory, tile)
            } else if (tile is TileEntityCargoUnloader) {
                return ContainerCargoLoader(player!!.inventory, tile)
            } else if (tile is TileEntityParaChest) {
                return ContainerParaChest(player!!.inventory, tile, player)
            } else if (tile is TileEntitySolar) {
                return ContainerSolar(player!!.inventory, tile)
            } else if (tile is TileEntityEnergyStorageModule) {
                return ContainerEnergyStorageModule(player!!.inventory, tile)
            } else if (tile is TileEntityCoalGenerator) {
                return ContainerCoalGenerator(player!!.inventory, tile)
            } else if (tile is TileEntityElectricFurnace) {
                return ContainerElectricFurnace(player!!.inventory, tile)
            } else if (tile is TileEntityIngotCompressor) {
                return ContainerIngotCompressor(player!!.inventory, tile)
            } else if (tile is TileEntityElectricIngotCompressor) {
                return ContainerElectricIngotCompressor(player!!.inventory, tile)
            } else if (tile is TileEntityCircuitFabricator) {
                return ContainerCircuitFabricator(player!!.inventory, tile)
            } else if (tile is TileEntityOxygenStorageModule) {
                return ContainerOxygenStorageModule(player!!.inventory, tile)
            } else if (tile is TileEntityOxygenCompressor) {
                return ContainerOxygenCompressor(player!!.inventory, tile, player)
            } else if (tile is TileEntityOxygenDecompressor) {
                return ContainerOxygenDecompressor(player!!.inventory, tile, player)
            } else if (tile is TileEntityDeconstructor) {
                return ContainerDeconstructor(player!!.inventory, tile)
            } else if (tile is TileEntityPainter) {
                return ContainerPainter(player!!.inventory, tile)
            }
        }

        for (page in (stats.getUnlockedSchematics())!!) {
            if (ID == (page)!!.getGuiID()) {
                return (page)!!.getResultContainer(playerBase, BlockPos(x, y, z))
            }
        }

        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
            return this.getClientGuiElement(ID, player, world, BlockPos(x, y, z))
        }

        return null
    }

    @OnlyIn(Dist.CLIENT)
    private fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, position: BlockPos): Any? {
        val playerClient: EntityPlayerSP? = PlayerUtil.getPlayerBaseClientFromPlayer(player, false)

        if (ID == GuiIdsCore.GALAXY_MAP) {
            return GuiCelestialSelection(true, null, true)
        } else if (ID == GuiIdsCore.ROCKET_INVENTORY && player.getVehicle() is EntityTieredRocket) {
            return GuiRocketInventory(
                player.inventory,
                (player.getVehicle() as EntityTieredRocket?)!!,
                ((player.getVehicle() as EntityTieredRocket).getType())!!
            )
        } else if (ID == GuiIdsCore.EXTENDED_INVENTORY) {
            return GuiExtendedInventory(player, ClientProxyCore.dummyInventory)
        } else if (ID == GuiIdsCore.SPACE_RACE_START) {
            return GuiNewSpaceRace(player)
        } else if (ID == GuiIdsCore.SPACE_RACE_JOIN) {
            return GuiJoinSpaceRace((playerClient)!!)
        } else if (ID == GuiIdsCore.PRE_LAUNCH_CHECKLIST) {
            val checkList: ItemStack? = GCCoreUtil.getMatchingItemEitherHand((playerClient)!!, GCItems.prelaunchChecklist)
            return GuiPreLaunchChecklist(
                WorldUtil.getAllChecklistKeys(),
                if (checkList != null && checkList.hasTagCompound()) checkList.getTagCompound()!!
                    .getTag("checklistData") as NBTTagCompound else null
            )
        }

        val tile: TileEntity? = world.getBlockEntity(position)

        if (tile != null) {
            if (tile is TileEntityCrafting) {
                return GuiCrafting(player.inventory, tile)
            } else if (tile is TileEntityRefinery) {
                return GuiRefinery(player.inventory, (world.getBlockEntity(position) as TileEntityRefinery?)!!)
            } else if (tile is TileEntityOxygenCollector) {
                return GuiOxygenCollector(player.inventory, tile)
            } else if (tile is TileEntityOxygenDistributor) {
                return GuiOxygenDistributor(player.inventory, tile)
            } else if (tile is TileEntityFuelLoader) {
                return GuiFuelLoader(player.inventory, tile)
            } else if (tile is TileEntityOxygenSealer) {
                return GuiOxygenSealer(player.inventory, tile)
            } else if (tile is TileEntityCargoLoader) {
                return GuiCargoLoader(player.inventory, tile)
            } else if (tile is TileEntityCargoUnloader) {
                return GuiCargoUnloader(player.inventory, tile)
            } else if (tile is TileEntityParaChest) {
                return GuiParaChest(player.inventory, tile)
            } else if (tile is TileEntitySolar) {
                return GuiSolar(player.inventory, tile)
            } else if (tile is TileEntityAirLockController) {
                return GuiAirLockController(tile)
            } else if (tile is TileEntityEnergyStorageModule) {
                return GuiEnergyStorageModule(player.inventory, tile)
            } else if (tile is TileEntityCoalGenerator) {
                return GuiCoalGenerator(player.inventory, tile)
            } else if (tile is TileEntityElectricFurnace) {
                return GuiElectricFurnace(player.inventory, tile)
            } else if (tile is TileEntityIngotCompressor) {
                return GuiIngotCompressor(player.inventory, tile)
            } else if (tile is TileEntityElectricIngotCompressor) {
                return GuiElectricIngotCompressor(player.inventory, tile)
            } else if (tile is TileEntityCircuitFabricator) {
                return GuiCircuitFabricator(player.inventory, tile)
            } else if (tile is TileEntityOxygenStorageModule) {
                return GuiOxygenStorageModule(player.inventory, tile)
            } else if (tile is TileEntityOxygenCompressor) {
                return GuiOxygenCompressor(player.inventory, tile)
            } else if (tile is TileEntityOxygenDecompressor) {
                return GuiOxygenDecompressor(player.inventory, tile)
            } else if (tile is TileEntityDeconstructor) {
                return GuiDeconstructor(player.inventory, tile)
            } else if (tile is TileEntityPainter) {
                return GuiPainter(player.inventory, tile)
            }
        }

        if (playerClient != null) {
            val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(playerClient))!!

            for (page in (stats.getUnlockedSchematics())!!) {
                if (ID == (page)!!.getGuiID()) {
                    val screen: GuiScreen? = (page)!!.getResultScreen(playerClient, position)

                    if (screen is ISchematicResultPage) {
                        (screen as ISchematicResultPage).setPageIndex((page)!!.getPageID())
                    }

                    return screen
                }
            }
        }

        return null
    }
}
