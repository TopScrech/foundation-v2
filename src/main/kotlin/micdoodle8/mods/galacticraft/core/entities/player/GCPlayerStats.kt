package micdoodle8.mods.galacticraft.core.entities.player

import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import java.lang.ref.WeakReference
import java.util.HashMap

abstract class GCPlayerStats {
    abstract fun saveNBTData(nbt: NBTTagCompound?)

    abstract fun loadNBTData(nbt: NBTTagCompound?)

    abstract fun copyFrom(oldData: GCPlayerStats?, keepInv: Boolean)

    abstract fun getPlayer(): WeakReference<EntityPlayerMP>?

    abstract fun setPlayer(player: WeakReference<EntityPlayerMP>?)

    abstract fun getExtendedInventory(): InventoryExtended?

    abstract fun setExtendedInventory(extendedInventory: InventoryExtended?)

    abstract fun getAirRemaining(): Int

    abstract fun setAirRemaining(airRemaining: Int)

    abstract fun getAirRemaining2(): Int

    abstract fun setAirRemaining2(airRemaining2: Int)

    abstract fun getThermalLevel(): Int

    abstract fun setThermalLevel(thermalLevel: Int)

    abstract fun isThermalLevelNormalising(): Boolean

    abstract fun setThermalLevelNormalising(thermalLevelNormalising: Boolean)

    abstract fun getDamageCounter(): Int

    abstract fun setDamageCounter(damageCounter: Int)

    abstract fun getSpaceshipTier(): Int

    abstract fun setSpaceshipTier(spaceshipTier: Int)

    abstract fun getRocketStacks(): NonNullList<ItemStack>?

    abstract fun setRocketStacks(rocketStacks: NonNullList<ItemStack>?)

    abstract fun getRocketType(): Int

    abstract fun setRocketType(rocketType: Int)

    abstract fun getFuelLevel(): Int

    abstract fun setFuelLevel(fuelLevel: Int)

    abstract fun getRocketItem(): Item?

    abstract fun setRocketItem(rocketItem: Item?)

    abstract fun getLaunchpadStack(): ItemStack?

    abstract fun setLaunchpadStack(launchpadStack: ItemStack?)

    abstract fun getAstroMinerCount(): Int

    abstract fun setAstroMinerCount(astroMinerCount: Int)

    abstract fun getActiveAstroMinerChunks(): MutableList<BlockVec3?>?

    abstract fun isUsingParachute(): Boolean

    abstract fun setUsingParachute(usingParachute: Boolean)

    abstract fun getParachuteInSlot(): ItemStack?

    abstract fun setParachuteInSlot(parachuteInSlot: ItemStack?)

    abstract fun getLastParachuteInSlot(): ItemStack?

    abstract fun setLastParachuteInSlot(lastParachuteInSlot: ItemStack?)

    abstract fun getFrequencyModuleInSlot(): ItemStack?

    abstract fun setFrequencyModuleInSlot(frequencyModuleInSlot: ItemStack?)

    abstract fun getLastFrequencyModuleInSlot(): ItemStack?

    abstract fun setLastFrequencyModuleInSlot(lastFrequencyModuleInSlot: ItemStack?)

    abstract fun getMaskInSlot(): ItemStack?

    abstract fun setMaskInSlot(maskInSlot: ItemStack?)

    abstract fun getLastMaskInSlot(): ItemStack?

    abstract fun setLastMaskInSlot(lastMaskInSlot: ItemStack?)

    abstract fun getGearInSlot(): ItemStack?

    abstract fun setGearInSlot(gearInSlot: ItemStack?)

    abstract fun getLastGearInSlot(): ItemStack?

    abstract fun setLastGearInSlot(lastGearInSlot: ItemStack?)

    abstract fun getTankInSlot1(): ItemStack?

    abstract fun setTankInSlot1(tankInSlot1: ItemStack?)

    abstract fun getLastTankInSlot1(): ItemStack?

    abstract fun setLastTankInSlot1(lastTankInSlot1: ItemStack?)

    abstract fun getTankInSlot2(): ItemStack?

    abstract fun setTankInSlot2(tankInSlot2: ItemStack?)

    abstract fun getLastTankInSlot2(): ItemStack?

    abstract fun setLastTankInSlot2(lastTankInSlot2: ItemStack?)

    abstract fun getThermalHelmetInSlot(): ItemStack?

    abstract fun setThermalHelmetInSlot(thermalHelmetInSlot: ItemStack?)

    abstract fun getLastThermalHelmetInSlot(): ItemStack?

    abstract fun setLastThermalHelmetInSlot(lastThermalHelmetInSlot: ItemStack?)

    abstract fun getThermalChestplateInSlot(): ItemStack?

    abstract fun setThermalChestplateInSlot(thermalChestplateInSlot: ItemStack?)

    abstract fun getLastThermalChestplateInSlot(): ItemStack?

    abstract fun setLastThermalChestplateInSlot(lastThermalChestplateInSlot: ItemStack?)

    abstract fun getThermalLeggingsInSlot(): ItemStack?

    abstract fun setThermalLeggingsInSlot(thermalLeggingsInSlot: ItemStack?)

    abstract fun getLastThermalLeggingsInSlot(): ItemStack?

    abstract fun setLastThermalLeggingsInSlot(lastThermalLeggingsInSlot: ItemStack?)

    abstract fun getThermalBootsInSlot(): ItemStack?

    abstract fun setThermalBootsInSlot(thermalBootsInSlot: ItemStack?)

    abstract fun getLastThermalBootsInSlot(): ItemStack?

    abstract fun setLastThermalBootsInSlot(lastThermalBootsInSlot: ItemStack?)

    abstract fun getShieldControllerInSlot(): ItemStack?

    abstract fun setShieldControllerInSlot(shieldControllerInSlot: ItemStack?)

    abstract fun getLastShieldControllerInSlot(): ItemStack?

    abstract fun setLastShieldControllerInSlot(lastShieldControllerInSlot: ItemStack?)

    abstract fun getLaunchAttempts(): Int

    abstract fun setLaunchAttempts(launchAttempts: Int)

    abstract fun getSpaceRaceInviteTeamID(): Int

    abstract fun setSpaceRaceInviteTeamID(spaceRaceInviteTeamID: Int)

    abstract fun isUsingPlanetSelectionGui(): Boolean

    abstract fun setUsingPlanetSelectionGui(usingPlanetSelectionGui: Boolean)

    abstract fun getSavedPlanetList(): String?

    abstract fun setSavedPlanetList(savedPlanetList: String?)

    abstract fun getOpenPlanetSelectionGuiCooldown(): Int

    abstract fun setOpenPlanetSelectionGuiCooldown(openPlanetSelectionGuiCooldown: Int)

    abstract fun hasOpenedPlanetSelectionGui(): Boolean

    abstract fun setHasOpenedPlanetSelectionGui(hasOpenedPlanetSelectionGui: Boolean)

    abstract fun getChestSpawnCooldown(): Int

    abstract fun setChestSpawnCooldown(chestSpawnCooldown: Int)

    abstract fun getChestSpawnVector(): Vector3?

    abstract fun setChestSpawnVector(chestSpawnVector: Vector3?)

    abstract fun getTeleportCooldown(): Int

    abstract fun setTeleportCooldown(teleportCooldown: Int)

    abstract fun getChatCooldown(): Int

    abstract fun setChatCooldown(chatCooldown: Int)

    abstract fun getDistanceSinceLastStep(): Double

    abstract fun setDistanceSinceLastStep(distanceSinceLastStep: Double)

    abstract fun getLastStep(): Int

    abstract fun setLastStep(lastStep: Int)

    abstract fun getCoordsTeleportedFromX(): Double

    abstract fun setCoordsTeleportedFromX(coordsTeleportedFromX: Double)

    abstract fun getCoordsTeleportedFromZ(): Double

    abstract fun setCoordsTeleportedFromZ(coordsTeleportedFromZ: Double)

    abstract fun getSpaceStationDimensionData(): HashMap<Int,Int>?

    abstract fun setSpaceStationDimensionData(spaceStationDimensionData: HashMap<Int,Int>?)

    abstract fun isOxygenSetupValid(): Boolean

    abstract fun setOxygenSetupValid(oxygenSetupValid: Boolean)

    abstract fun isLastOxygenSetupValid(): Boolean

    abstract fun setLastOxygenSetupValid(lastOxygenSetupValid: Boolean)

    abstract fun isTouchedGround(): Boolean

    abstract fun setTouchedGround(touchedGround: Boolean)

    abstract fun isLastOnGround(): Boolean

    abstract fun setLastOnGround(lastOnGround: Boolean)

    abstract fun isInLander(): Boolean

    abstract fun setInLander(inLander: Boolean)

    abstract fun hasJustLanded(): Boolean

    abstract fun setJustLanded(justLanded: Boolean)

    abstract fun getUnlockedSchematics(): MutableList<ISchematicPage>?

    abstract fun setUnlockedSchematics(unlockedSchematics: MutableList<ISchematicPage>?)

    abstract fun getLastUnlockedSchematics(): MutableList<ISchematicPage>?

    abstract fun setLastUnlockedSchematics(lastUnlockedSchematics: MutableList<ISchematicPage>?)

    abstract fun getCryogenicChamberCooldown(): Int

    abstract fun setCryogenicChamberCooldown(cryogenicChamberCooldown: Int)

    abstract fun hasReceivedSoundWarning(): Boolean

    abstract fun setReceivedSoundWarning(receivedSoundWarning: Boolean)

    abstract fun hasReceivedBedWarning(): Boolean

    abstract fun setReceivedBedWarning(receivedBedWarning: Boolean)

    abstract fun hasOpenedSpaceRaceManager(): Boolean

    abstract fun setOpenedSpaceRaceManager(openedSpaceRaceManager: Boolean)

    abstract fun hasSentFlags(): Boolean

    abstract fun setSentFlags(sentFlags: Boolean)

    abstract fun isNewInOrbit(): Boolean

    abstract fun setNewInOrbit(newInOrbit: Boolean)

    abstract fun isNewAdventureSpawn(): Boolean

    abstract fun setNewAdventureSpawn(newAdventureSpawn: Boolean)

    abstract fun getBuildFlags(): Int

    abstract fun setBuildFlags(buildFlags: Int)

    abstract fun getIncrementalDamage(): Int

    abstract fun setIncrementalDamage(incrementalDamage: Int)

    abstract fun getStartDimension(): String?

    abstract fun setStartDimension(startDimension: String?)

    abstract fun setGlassColors(color1: Int, color2: Int, color3: Int)

    abstract fun getGlassColor1(): Int

    abstract fun getGlassColor2(): Int

    abstract fun getGlassColor3(): Int

    abstract fun getPanelLightingBases(): Array<IBlockState?>?

    abstract fun getPanelLightingColor(): Int

    abstract fun setPanelLightingColor(color: Int)

    abstract fun getMiscNetworkedStats(): Array<Any?>?

    abstract fun setSavedSpeed(value: Float)

    abstract fun getSavedSpeed(): Float

    companion object {
        @JvmStatic
        fun get(entity: Entity): GCPlayerStats? {
            return entity.getCapability<GCPlayerStats?>((GCCapabilities.GC_STATS_CAPABILITY)!!, null)
        }
    }
}
