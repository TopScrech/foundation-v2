package micdoodle8.mods.galacticraft.core.entities.player

import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.HashMap

interface IStatsCapability {
    fun saveNBTData(nbt: NBTTagCompound?)

    fun loadNBTData(nbt: NBTTagCompound?)

    fun copyFrom(oldData: IStatsCapability?, keepInv: Boolean)

    fun getPlayer(): WeakReference<EntityPlayerMP>?

    fun setPlayer(player: WeakReference<EntityPlayerMP>?)

    fun getExtendedInventory(): InventoryExtended?

    fun setExtendedInventory(extendedInventory: InventoryExtended?)

    fun getAirRemaining(): Int

    fun setAirRemaining(airRemaining: Int)

    fun getAirRemaining2(): Int

    fun setAirRemaining2(airRemaining2: Int)

    fun getThermalLevel(): Int

    fun setThermalLevel(thermalLevel: Int)

    fun isThermalLevelNormalising(): Boolean

    fun setThermalLevelNormalising(thermalLevelNormalising: Boolean)

    fun getDamageCounter(): Int

    fun setDamageCounter(damageCounter: Int)

    fun getSpaceshipTier(): Int

    fun setSpaceshipTier(spaceshipTier: Int)

    fun getRocketStacks(): NonNullList<ItemStack>?

    fun setRocketStacks(rocketStacks: NonNullList<ItemStack>?)

    fun getRocketType(): Int

    fun setRocketType(rocketType: Int)

    fun getFuelLevel(): Int

    fun setFuelLevel(fuelLevel: Int)

    fun getRocketItem(): Item?

    fun setRocketItem(rocketItem: Item?)

    fun getLaunchpadStack(): ItemStack?

    fun setLaunchpadStack(launchpadStack: ItemStack?)

    fun getAstroMinerCount(): Int

    fun setAstroMinerCount(astroMinerCount: Int)

    fun isUsingParachute(): Boolean

    fun setUsingParachute(usingParachute: Boolean)

    fun getParachuteInSlot(): ItemStack?

    fun setParachuteInSlot(parachuteInSlot: ItemStack?)

    fun getLastParachuteInSlot(): ItemStack?

    fun setLastParachuteInSlot(lastParachuteInSlot: ItemStack?)

    fun getFrequencyModuleInSlot(): ItemStack?

    fun setFrequencyModuleInSlot(frequencyModuleInSlot: ItemStack?)

    fun getLastFrequencyModuleInSlot(): ItemStack?

    fun setLastFrequencyModuleInSlot(lastFrequencyModuleInSlot: ItemStack?)

    fun getMaskInSlot(): ItemStack?

    fun setMaskInSlot(maskInSlot: ItemStack?)

    fun getLastMaskInSlot(): ItemStack?

    fun setLastMaskInSlot(lastMaskInSlot: ItemStack?)

    fun getGearInSlot(): ItemStack?

    fun setGearInSlot(gearInSlot: ItemStack?)

    fun getLastGearInSlot(): ItemStack?

    fun setLastGearInSlot(lastGearInSlot: ItemStack?)

    fun getTankInSlot1(): ItemStack?

    fun setTankInSlot1(tankInSlot1: ItemStack?)

    fun getLastTankInSlot1(): ItemStack?

    fun setLastTankInSlot1(lastTankInSlot1: ItemStack?)

    fun getTankInSlot2(): ItemStack?

    fun setTankInSlot2(tankInSlot2: ItemStack?)

    fun getLastTankInSlot2(): ItemStack?

    fun setLastTankInSlot2(lastTankInSlot2: ItemStack?)

    fun getThermalHelmetInSlot(): ItemStack?

    fun setThermalHelmetInSlot(thermalHelmetInSlot: ItemStack?)

    fun getLastThermalHelmetInSlot(): ItemStack?

    fun setLastThermalHelmetInSlot(lastThermalHelmetInSlot: ItemStack?)

    fun getThermalChestplateInSlot(): ItemStack?

    fun setThermalChestplateInSlot(thermalChestplateInSlot: ItemStack?)

    fun getLastThermalChestplateInSlot(): ItemStack?

    fun setLastThermalChestplateInSlot(lastThermalChestplateInSlot: ItemStack?)

    fun getThermalLeggingsInSlot(): ItemStack?

    fun setThermalLeggingsInSlot(thermalLeggingsInSlot: ItemStack?)

    fun getLastThermalLeggingsInSlot(): ItemStack?

    fun setLastThermalLeggingsInSlot(lastThermalLeggingsInSlot: ItemStack?)

    fun getThermalBootsInSlot(): ItemStack?

    fun setThermalBootsInSlot(thermalBootsInSlot: ItemStack?)

    fun getLastThermalBootsInSlot(): ItemStack?

    fun setLastThermalBootsInSlot(lastThermalBootsInSlot: ItemStack?)

    fun getShieldControllerInSlot(): ItemStack?

    fun setShieldControllerInSlot(shieldControllerInSlot: ItemStack?)

    fun getLastShieldControllerInSlot(): ItemStack?

    fun setLastShieldControllerInSlot(lastShieldControllerInSlot: ItemStack?)

    fun getLaunchAttempts(): Int

    fun setLaunchAttempts(launchAttempts: Int)

    fun getSpaceRaceInviteTeamID(): Int

    fun setSpaceRaceInviteTeamID(spaceRaceInviteTeamID: Int)

    fun isUsingPlanetSelectionGui(): Boolean

    fun setUsingPlanetSelectionGui(usingPlanetSelectionGui: Boolean)

    fun getSavedPlanetList(): String?

    fun setSavedPlanetList(savedPlanetList: String?)

    fun getOpenPlanetSelectionGuiCooldown(): Int

    fun setOpenPlanetSelectionGuiCooldown(openPlanetSelectionGuiCooldown: Int)

    fun hasOpenedPlanetSelectionGui(): Boolean

    fun setHasOpenedPlanetSelectionGui(hasOpenedPlanetSelectionGui: Boolean)

    fun getChestSpawnCooldown(): Int

    fun setChestSpawnCooldown(chestSpawnCooldown: Int)

    fun getChestSpawnVector(): Vector3?

    fun setChestSpawnVector(chestSpawnVector: Vector3?)

    fun getTeleportCooldown(): Int

    fun setTeleportCooldown(teleportCooldown: Int)

    fun getChatCooldown(): Int

    fun setChatCooldown(chatCooldown: Int)

    fun getDistanceSinceLastStep(): Double

    fun setDistanceSinceLastStep(distanceSinceLastStep: Double)

    fun getLastStep(): Int

    fun setLastStep(lastStep: Int)

    fun getCoordsTeleportedFromX(): Double

    fun setCoordsTeleportedFromX(coordsTeleportedFromX: Double)

    fun getCoordsTeleportedFromZ(): Double

    fun setCoordsTeleportedFromZ(coordsTeleportedFromZ: Double)

    fun getSpaceStationDimensionData(): HashMap<Int,Int>?

    fun setSpaceStationDimensionData(spaceStationDimensionData: HashMap<Int,Int>?)

    fun isOxygenSetupValid(): Boolean

    fun setOxygenSetupValid(oxygenSetupValid: Boolean)

    fun isLastOxygenSetupValid(): Boolean

    fun setLastOxygenSetupValid(lastOxygenSetupValid: Boolean)

    fun isTouchedGround(): Boolean

    fun setTouchedGround(touchedGround: Boolean)

    fun isLastOnGround(): Boolean

    fun setLastOnGround(lastOnGround: Boolean)

    fun isInLander(): Boolean

    fun setInLander(inLander: Boolean)

    fun hasJustLanded(): Boolean

    fun setJustLanded(justLanded: Boolean)

    fun getUnlockedSchematics(): ArrayList<ISchematicPage>?

    fun setUnlockedSchematics(unlockedSchematics: ArrayList<ISchematicPage>?)

    fun getLastUnlockedSchematics(): ArrayList<ISchematicPage>?

    fun setLastUnlockedSchematics(lastUnlockedSchematics: ArrayList<ISchematicPage>?)

    fun getCryogenicChamberCooldown(): Int

    fun setCryogenicChamberCooldown(cryogenicChamberCooldown: Int)

    fun hasReceivedSoundWarning(): Boolean

    fun setReceivedSoundWarning(receivedSoundWarning: Boolean)

    fun hasReceivedBedWarning(): Boolean

    fun setReceivedBedWarning(receivedBedWarning: Boolean)

    fun hasOpenedSpaceRaceManager(): Boolean

    fun setOpenedSpaceRaceManager(openedSpaceRaceManager: Boolean)

    fun hasSentFlags(): Boolean

    fun setSentFlags(sentFlags: Boolean)

    fun isNewInOrbit(): Boolean

    fun setNewInOrbit(newInOrbit: Boolean)

    fun isNewAdventureSpawn(): Boolean

    fun setNewAdventureSpawn(newAdventureSpawn: Boolean)

    fun getBuildFlags(): Int

    fun setBuildFlags(buildFlags: Int)

    fun getIncrementalDamage(): Int

    fun setIncrementalDamage(incrementalDamage: Int)

    fun getStartDimension(): String?

    fun setStartDimension(startDimension: String?)

    fun setGlassColors(color1: Int, color2: Int, color3: Int)

    fun getGlassColor1(): Int

    fun getGlassColor2(): Int

    fun getGlassColor3(): Int
}
