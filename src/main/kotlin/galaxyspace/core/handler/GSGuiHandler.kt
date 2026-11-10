package galaxyspace.core.handler

import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.prefab.inventory.ContainerAstroWolf
import galaxyspace.core.prefab.entities.EntityAstroWolf
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityFuelGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerFuelGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerModernSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityRocketAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerRocketAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityUniversalRecycler
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerUniversalRecycler
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidExtractor
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerLiquidExtractor
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidSeparator
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerLiquidSeparator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicBase
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerHydroponicBase
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGravitationModule
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerGravitationModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityRadiationStabiliser
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerRadiationStabiliser
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityPlanetShield
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerPlanetShield
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerModernStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModificationTable
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerModificationTable
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvOxygenStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvCircuitFabricator
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerAdvCircuitFabricator
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityResearchTable
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerResearchTable
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasCollector
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerGasCollector
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerGasGenerator
import galaxyspace.core.client.gui.entity.GuiAstroWolfInventory
import asmodeuscore.core.astronomy.gui.book.ACGuiGuideBook
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiFuelGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiModernSolarPanel
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiWindGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiRocketAssembler
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiUniversalRecycler
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiLiquidExtractor
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiLiquidSeparator
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiHydroponicBase
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiGravitationModule
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiRadiationStabiliser
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiPlanetShield
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiModernStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiModificationTable
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiAdvCircuitFabricator
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiResearchTable
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiGasCollector
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiGasGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.gui.GuiOxygenStorageModule
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerOxygenStorageModule
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.ContainerPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class GSGuiHandler : IGuiHandler {
    override fun getServerGuiElement(ID: Int, player: EntityPlayer?, world: World, x: Int, y: Int, z: Int): Any? {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)

        if (playerBase == null) {
            player!!.sendMessage(TextComponentString("Galaxy Space player instance null server-side. This is a bug."))
            return null
        }

        val stats: GCPlayerStats? = GCPlayerStats.get(playerBase)


        val pos: BlockPos = BlockPos(x, y, z)
        val tile: TileEntity? = world.getTileEntity(pos)

        if (ID == GSConfigCore.guiIDGuideBook) return ContainerPlayer(player!!.inventory, false, player)

        if (ID == 1005) return ContainerAstroWolf(
            player!!.inventory,
            (world.getEntityByID(x) as EntityAstroWolf?)!!,
            player
        )

        if (tile != null) {
            if (tile is TileEntityFuelGenerator) return ContainerFuelGenerator(player!!.inventory, tile)
            else if (tile is TileEntityAssembler) return ContainerAssembler(player!!.inventory, tile)
            else if (tile is TileEntityModernSolarPanel) return ContainerModernSolarPanel(player!!.inventory, tile)
            else if (tile is TileEntityWindGenerator) return ContainerWindGenerator(player!!.inventory, tile)
            else if (tile is TileEntityRocketAssembler) return ContainerRocketAssembler(player!!.inventory, tile)
            else if (tile is TileEntityUniversalRecycler) return ContainerUniversalRecycler(player!!.inventory, tile)
            else if (tile is TileEntityLiquidExtractor) return ContainerLiquidExtractor(player!!.inventory, tile)
            else if (tile is TileEntityLiquidSeparator) return ContainerLiquidSeparator(player!!.inventory, tile)
            else if (tile is TileEntityHydroponicBase) return ContainerHydroponicBase(player!!.inventory, tile)
            else if (tile is TileEntityGravitationModule) return ContainerGravitationModule(player!!.inventory, tile)
            else if (tile is TileEntityRadiationStabiliser) return ContainerRadiationStabiliser(
                player!!.inventory,
                tile
            )
            else if (tile is TileEntityPlanetShield) return ContainerPlanetShield(player!!.inventory, tile)
            else if (tile is TileEntityModernStorageModule) return ContainerModernStorageModule(
                player!!.inventory,
                tile
            )
            else if (tile is TileEntityModificationTable) return ContainerModificationTable(player!!.inventory, tile)
            else if (tile is TileEntityAdvOxygenStorageModule) return ContainerOxygenStorageModule(
                player!!.inventory,
                tile
            )
            else if (tile is TileEntityAdvCircuitFabricator) return ContainerAdvCircuitFabricator(
                player!!.inventory,
                tile
            )
            else if (tile is TileEntityResearchTable) return ContainerResearchTable(player!!.inventory, tile)
            else if (tile is TileEntityGasCollector) return ContainerGasCollector(player!!.inventory, tile)
            else if (tile is TileEntityGasGenerator) return ContainerGasGenerator(player!!.inventory, tile)
        }


        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        if (GCCoreUtil.getEffectiveSide() == Side.CLIENT) {
            if (ID == 1005) return GuiAstroWolfInventory(player, (world.getEntityByID(x) as EntityAstroWolf?)!!)

            return this.getClientGuiElement(ID, player, world, BlockPos(x, y, z))
        }

        return null
    }

    @SideOnly(Side.CLIENT)
    private fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, position: BlockPos): Any? {
        val playerClient: EntityPlayerSP? = PlayerUtil.getPlayerBaseClientFromPlayer(player, false)
        val tile: TileEntity? = world.getTileEntity(position)

        if (ID == GSConfigCore.guiIDGuideBook) return ACGuiGuideBook()



        if (tile != null) {
            if (tile is TileEntityFuelGenerator) return GuiFuelGenerator(player.inventory, tile)
            else if (tile is TileEntityAssembler) return GuiAssembler(player.inventory, tile)
            else if (tile is TileEntityModernSolarPanel) return GuiModernSolarPanel(player.inventory, tile)
            else if (tile is TileEntityWindGenerator) return GuiWindGenerator(player.inventory, tile)
            else if (tile is TileEntityRocketAssembler) return GuiRocketAssembler(player.inventory, tile)
            else if (tile is TileEntityUniversalRecycler) return GuiUniversalRecycler(player.inventory, tile)
            else if (tile is TileEntityLiquidExtractor) return GuiLiquidExtractor(player.inventory, tile)
            else if (tile is TileEntityLiquidSeparator) return GuiLiquidSeparator(player.inventory, tile)
            else if (tile is TileEntityHydroponicBase) return GuiHydroponicBase(player.inventory, tile)
            else if (tile is TileEntityGravitationModule) return GuiGravitationModule(player.inventory, tile)
            else if (tile is TileEntityRadiationStabiliser) return GuiRadiationStabiliser(player.inventory, tile)
            else if (tile is TileEntityPlanetShield) return GuiPlanetShield(player.inventory, tile)
            else if (tile is TileEntityModernStorageModule) return GuiModernStorageModule(player.inventory, tile)
            else if (tile is TileEntityModificationTable) return GuiModificationTable(player.inventory, tile)
            else if (tile is TileEntityAdvOxygenStorageModule) return GuiOxygenStorageModule(player.inventory, tile)
            else if (tile is TileEntityAdvCircuitFabricator) return GuiAdvCircuitFabricator(player.inventory, tile)
            else if (tile is TileEntityResearchTable) return GuiResearchTable(player.inventory, tile)
            else if (tile is TileEntityGasCollector) return GuiGasCollector(player.inventory, tile)
            else if (tile is TileEntityGasGenerator) return GuiGasGenerator(player.inventory, tile)
        }
        return null
    }
}
