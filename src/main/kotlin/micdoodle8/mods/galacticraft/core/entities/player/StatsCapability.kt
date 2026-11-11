package micdoodle8.mods.galacticraft.core.entities.player

import foundation.item.legacyItemValue

import foundation.persistence.containsTag
import foundation.persistence.listOfType

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.LinkedList

import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting
import micdoodle8.mods.galacticraft.core.command.CommandGCInv
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended
import micdoodle8.mods.galacticraft.core.tile.TileEntityPanelLight
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.asteroids.tick.AsteroidsTickHandlerServer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.core.NonNullList
import java.lang.ref.WeakReference
import java.util.*

open class StatsCapability : GCPlayerStats() {
    @JvmField
    var player: WeakReference<EntityPlayerMP>? = null

    @JvmField
    var extendedInventory: InventoryExtended = InventoryExtended()

    @JvmField
    var airRemaining: Int = 0

    @JvmField
    var airRemaining2: Int = 0

    @JvmField
    var thermalLevel: Int = 0

    @JvmField
    var thermalLevelNormalising: Boolean = false

    @JvmField
    var damageCounter: Int = 0

    // temporary data while player is in planet selection GUI
    @JvmField
    var spaceshipTier: Int = 1

    @JvmField
    var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)

    @JvmField
    var rocketType: Int = 0

    @JvmField
    var fuelLevel: Int = 0

    @JvmField
    var rocketItem: Item? = null

    @JvmField
    var launchpadStack: ItemStack? = null

    @JvmField
    var astroMinerCount: Int = 0
    private var activeAstroMinerChunks: MutableList<BlockVec3?> = LinkedList<BlockVec3?>()

    @JvmField
    var usingParachute: Boolean = false

    @JvmField
    var parachuteInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastParachuteInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var frequencyModuleInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastFrequencyModuleInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var maskInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastMaskInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var gearInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastGearInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var tankInSlot1: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastTankInSlot1: ItemStack? = ItemStack.EMPTY

    @JvmField
    var tankInSlot2: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastTankInSlot2: ItemStack? = ItemStack.EMPTY

    @JvmField
    var thermalHelmetInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastThermalHelmetInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var thermalChestplateInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastThermalChestplateInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var thermalLeggingsInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastThermalLeggingsInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var thermalBootsInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastThermalBootsInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var shieldControllerInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var lastShieldControllerInSlot: ItemStack? = ItemStack.EMPTY

    @JvmField
    var launchAttempts: Int = 0

    @JvmField
    var spaceRaceInviteTeamID: Int = 0

    @JvmField
    var usingPlanetSelectionGui: Boolean = false

    @JvmField
    var savedPlanetList: String? = ""

    @JvmField
    var openPlanetSelectionGuiCooldown: Int = 0

    @JvmField
    var hasOpenedPlanetSelectionGui: Boolean = false

    @JvmField
    var chestSpawnCooldown: Int = 0

    @JvmField
    var chestSpawnVector: Vector3? = null

    @JvmField
    var teleportCooldown: Int = 0

    @JvmField
    var chatCooldown: Int = 0

    @JvmField
    var distanceSinceLastStep: Double = 0.0

    @JvmField
    var lastStep: Int = 0

    @JvmField
    var coordsTeleportedFromX: Double = 0.0

    @JvmField
    var coordsTeleportedFromZ: Double = 0.0

    @JvmField
    var spaceStationDimensionData: HashMap<Int,Int> = Maps.newHashMap<Int,Int>()

    @JvmField
    var oxygenSetupValid: Boolean = false

    @JvmField
    var lastOxygenSetupValid: Boolean = false

    @JvmField
    var touchedGround: Boolean = false

    @JvmField
    var lastOnGround: Boolean = false

    @JvmField
    var inLander: Boolean = false

    @JvmField
    var justLanded: Boolean = false

    @JvmField
    var unlockedSchematics: MutableList<ISchematicPage> = LinkedList<ISchematicPage>()

    @JvmField
    var lastUnlockedSchematics: MutableList<ISchematicPage>? = LinkedList<ISchematicPage>()

    @JvmField
    var cryogenicChamberCooldown: Int = 0

    @JvmField
    var receivedSoundWarning: Boolean = false

    @JvmField
    var receivedBedWarning: Boolean = false

    @JvmField
    var openedSpaceRaceManager: Boolean = false

    @JvmField
    var sentFlags: Boolean = false

    @JvmField
    var newInOrbit: Boolean = true

    @JvmField
    var newAdventureSpawn: Boolean = false

    @JvmField
    var buildFlags: Int = 0

    @JvmField
    var incrementalDamage: Int = 0
    private var savedSpeed = 0f // used by titanium armor

    @JvmField
    var startDimension: String = ""

    @JvmField
    var glassColor1: Int = -1

    @JvmField
    var glassColor2: Int = -1

    @JvmField
    var glassColor3: Int = -1

    private var panelLightingBases = arrayOfNulls<IBlockState>(BlockPanelLighting.PANELTYPES_LENGTH)
    private var panelLightingColor = 0xf0f0e0

    override fun getPlayer(): WeakReference<EntityPlayerMP>? {
        return player
    }

    override fun setPlayer(player: WeakReference<EntityPlayerMP>?) {
        this.player = player
    }

    override fun getExtendedInventory(): InventoryExtended? {
        return extendedInventory
    }

    override fun setExtendedInventory(extendedInventory: InventoryExtended?) {
        this.extendedInventory = (extendedInventory)!!
    }

    override fun getAirRemaining(): Int {
        return airRemaining
    }

    override fun setAirRemaining(airRemaining: Int) {
        this.airRemaining = airRemaining
    }

    override fun getAirRemaining2(): Int {
        return airRemaining2
    }

    override fun setAirRemaining2(airRemaining2: Int) {
        this.airRemaining2 = airRemaining2
    }

    override fun getThermalLevel(): Int {
        return thermalLevel
    }

    override fun setThermalLevel(thermalLevel: Int) {
        this.thermalLevel = thermalLevel
    }

    override fun isThermalLevelNormalising(): Boolean {
        return thermalLevelNormalising
    }

    override fun setThermalLevelNormalising(thermalLevelNormalising: Boolean) {
        this.thermalLevelNormalising = thermalLevelNormalising
    }

    override fun getDamageCounter(): Int {
        return damageCounter
    }

    override fun setDamageCounter(damageCounter: Int) {
        this.damageCounter = damageCounter
    }

    override fun getSpaceshipTier(): Int {
        return spaceshipTier
    }

    override fun setSpaceshipTier(spaceshipTier: Int) {
        this.spaceshipTier = spaceshipTier
    }

    override fun getRocketStacks(): NonNullList<ItemStack>? {
        return stacks
    }

    override fun setRocketStacks(rocketStacks: NonNullList<ItemStack>?) {
        this.stacks = (rocketStacks)!!
    }

    override fun getRocketType(): Int {
        return rocketType
    }

    override fun setRocketType(rocketType: Int) {
        this.rocketType = rocketType
    }

    override fun getFuelLevel(): Int {
        return fuelLevel
    }

    override fun setFuelLevel(fuelLevel: Int) {
        this.fuelLevel = fuelLevel
    }

    override fun getRocketItem(): Item? {
        return rocketItem
    }

    override fun setRocketItem(rocketItem: Item?) {
        this.rocketItem = rocketItem
    }

    override fun getLaunchpadStack(): ItemStack? {
        return launchpadStack
    }

    override fun setLaunchpadStack(launchpadStack: ItemStack?) {
        this.launchpadStack = launchpadStack
    }

    override fun getAstroMinerCount(): Int {
        return astroMinerCount
    }

    override fun setAstroMinerCount(astroMinerCount: Int) {
        this.astroMinerCount = astroMinerCount
    }

    override fun getActiveAstroMinerChunks(): MutableList<BlockVec3?>? {
        return this.activeAstroMinerChunks
    }

    override fun isUsingParachute(): Boolean {
        return usingParachute
    }

    override fun setUsingParachute(usingParachute: Boolean) {
        this.usingParachute = usingParachute
    }

    override fun getParachuteInSlot(): ItemStack? {
        return parachuteInSlot
    }

    override fun setParachuteInSlot(parachuteInSlot: ItemStack?) {
        this.parachuteInSlot = parachuteInSlot
    }

    override fun getLastParachuteInSlot(): ItemStack? {
        return lastParachuteInSlot
    }

    override fun setLastParachuteInSlot(lastParachuteInSlot: ItemStack?) {
        this.lastParachuteInSlot = lastParachuteInSlot
    }

    override fun getFrequencyModuleInSlot(): ItemStack? {
        return frequencyModuleInSlot
    }

    override fun setFrequencyModuleInSlot(frequencyModuleInSlot: ItemStack?) {
        this.frequencyModuleInSlot = frequencyModuleInSlot
    }

    override fun getLastFrequencyModuleInSlot(): ItemStack? {
        return lastFrequencyModuleInSlot
    }

    override fun setLastFrequencyModuleInSlot(lastFrequencyModuleInSlot: ItemStack?) {
        this.lastFrequencyModuleInSlot = lastFrequencyModuleInSlot
    }

    override fun getMaskInSlot(): ItemStack? {
        return maskInSlot
    }

    override fun setMaskInSlot(maskInSlot: ItemStack?) {
        this.maskInSlot = maskInSlot
    }

    override fun getLastMaskInSlot(): ItemStack? {
        return lastMaskInSlot
    }

    override fun setLastMaskInSlot(lastMaskInSlot: ItemStack?) {
        this.lastMaskInSlot = lastMaskInSlot
    }

    override fun getGearInSlot(): ItemStack? {
        return gearInSlot
    }

    override fun setGearInSlot(gearInSlot: ItemStack?) {
        this.gearInSlot = gearInSlot
    }

    override fun getLastGearInSlot(): ItemStack? {
        return lastGearInSlot
    }

    override fun setLastGearInSlot(lastGearInSlot: ItemStack?) {
        this.lastGearInSlot = lastGearInSlot
    }

    override fun getTankInSlot1(): ItemStack? {
        return tankInSlot1
    }

    override fun setTankInSlot1(tankInSlot1: ItemStack?) {
        this.tankInSlot1 = tankInSlot1
    }

    override fun getLastTankInSlot1(): ItemStack? {
        return lastTankInSlot1
    }

    override fun setLastTankInSlot1(lastTankInSlot1: ItemStack?) {
        this.lastTankInSlot1 = lastTankInSlot1
    }

    override fun getTankInSlot2(): ItemStack? {
        return tankInSlot2
    }

    override fun setTankInSlot2(tankInSlot2: ItemStack?) {
        this.tankInSlot2 = tankInSlot2
    }

    override fun getLastTankInSlot2(): ItemStack? {
        return lastTankInSlot2
    }

    override fun setLastTankInSlot2(lastTankInSlot2: ItemStack?) {
        this.lastTankInSlot2 = lastTankInSlot2
    }

    override fun getThermalHelmetInSlot(): ItemStack? {
        return thermalHelmetInSlot
    }

    override fun setThermalHelmetInSlot(thermalHelmetInSlot: ItemStack?) {
        this.thermalHelmetInSlot = thermalHelmetInSlot
    }

    override fun getLastThermalHelmetInSlot(): ItemStack? {
        return lastThermalHelmetInSlot
    }

    override fun setLastThermalHelmetInSlot(lastThermalHelmetInSlot: ItemStack?) {
        this.lastThermalHelmetInSlot = lastThermalHelmetInSlot
    }

    override fun getThermalChestplateInSlot(): ItemStack? {
        return thermalChestplateInSlot
    }

    override fun setThermalChestplateInSlot(thermalChestplateInSlot: ItemStack?) {
        this.thermalChestplateInSlot = thermalChestplateInSlot
    }

    override fun getLastThermalChestplateInSlot(): ItemStack? {
        return lastThermalChestplateInSlot
    }

    override fun setLastThermalChestplateInSlot(lastThermalChestplateInSlot: ItemStack?) {
        this.lastThermalChestplateInSlot = lastThermalChestplateInSlot
    }

    override fun getThermalLeggingsInSlot(): ItemStack? {
        return thermalLeggingsInSlot
    }

    override fun setThermalLeggingsInSlot(thermalLeggingsInSlot: ItemStack?) {
        this.thermalLeggingsInSlot = thermalLeggingsInSlot
    }

    override fun getLastThermalLeggingsInSlot(): ItemStack? {
        return lastThermalLeggingsInSlot
    }

    override fun setLastThermalLeggingsInSlot(lastThermalLeggingsInSlot: ItemStack?) {
        this.lastThermalLeggingsInSlot = lastThermalLeggingsInSlot
    }

    override fun getThermalBootsInSlot(): ItemStack? {
        return thermalBootsInSlot
    }

    override fun setThermalBootsInSlot(thermalBootsInSlot: ItemStack?) {
        this.thermalBootsInSlot = thermalBootsInSlot
    }

    override fun getLastThermalBootsInSlot(): ItemStack? {
        return lastThermalBootsInSlot
    }

    override fun setLastThermalBootsInSlot(lastThermalBootsInSlot: ItemStack?) {
        this.lastThermalBootsInSlot = lastThermalBootsInSlot
    }

    override fun getShieldControllerInSlot(): ItemStack? {
        return shieldControllerInSlot
    }

    override fun setShieldControllerInSlot(shieldControllerInSlot: ItemStack?) {
        this.shieldControllerInSlot = shieldControllerInSlot
    }

    override fun getLastShieldControllerInSlot(): ItemStack? {
        return lastShieldControllerInSlot
    }

    override fun setLastShieldControllerInSlot(lastShieldControllerInSlot: ItemStack?) {
        this.lastShieldControllerInSlot = lastShieldControllerInSlot
    }

    override fun getLaunchAttempts(): Int {
        return launchAttempts
    }

    override fun setLaunchAttempts(launchAttempts: Int) {
        this.launchAttempts = launchAttempts
    }

    override fun getSpaceRaceInviteTeamID(): Int {
        return spaceRaceInviteTeamID
    }

    override fun setSpaceRaceInviteTeamID(spaceRaceInviteTeamID: Int) {
        this.spaceRaceInviteTeamID = spaceRaceInviteTeamID
    }

    override fun isUsingPlanetSelectionGui(): Boolean {
        return usingPlanetSelectionGui
    }

    override fun setUsingPlanetSelectionGui(usingPlanetSelectionGui: Boolean) {
        this.usingPlanetSelectionGui = usingPlanetSelectionGui
    }

    override fun getSavedPlanetList(): String? {
        return savedPlanetList
    }

    override fun setSavedPlanetList(savedPlanetList: String?) {
        this.savedPlanetList = savedPlanetList
    }

    override fun getOpenPlanetSelectionGuiCooldown(): Int {
        return openPlanetSelectionGuiCooldown
    }

    override fun setOpenPlanetSelectionGuiCooldown(openPlanetSelectionGuiCooldown: Int) {
        this.openPlanetSelectionGuiCooldown = openPlanetSelectionGuiCooldown
    }

    override fun hasOpenedPlanetSelectionGui(): Boolean {
        return hasOpenedPlanetSelectionGui
    }

    override fun setHasOpenedPlanetSelectionGui(hasOpenedPlanetSelectionGui: Boolean) {
        this.hasOpenedPlanetSelectionGui = hasOpenedPlanetSelectionGui
    }

    override fun getChestSpawnCooldown(): Int {
        return chestSpawnCooldown
    }

    override fun setChestSpawnCooldown(chestSpawnCooldown: Int) {
        this.chestSpawnCooldown = chestSpawnCooldown
    }

    override fun getChestSpawnVector(): Vector3? {
        return chestSpawnVector
    }

    override fun setChestSpawnVector(chestSpawnVector: Vector3?) {
        this.chestSpawnVector = chestSpawnVector
    }

    override fun getTeleportCooldown(): Int {
        return teleportCooldown
    }

    override fun setTeleportCooldown(teleportCooldown: Int) {
        this.teleportCooldown = teleportCooldown
    }

    override fun getChatCooldown(): Int {
        return chatCooldown
    }

    override fun setChatCooldown(chatCooldown: Int) {
        this.chatCooldown = chatCooldown
    }

    override fun getDistanceSinceLastStep(): Double {
        return distanceSinceLastStep
    }

    override fun setDistanceSinceLastStep(distanceSinceLastStep: Double) {
        this.distanceSinceLastStep = distanceSinceLastStep
    }

    override fun getLastStep(): Int {
        return lastStep
    }

    override fun setLastStep(lastStep: Int) {
        this.lastStep = lastStep
    }

    override fun getCoordsTeleportedFromX(): Double {
        return coordsTeleportedFromX
    }

    override fun setCoordsTeleportedFromX(coordsTeleportedFromX: Double) {
        this.coordsTeleportedFromX = coordsTeleportedFromX
    }

    override fun getCoordsTeleportedFromZ(): Double {
        return coordsTeleportedFromZ
    }

    override fun setCoordsTeleportedFromZ(coordsTeleportedFromZ: Double) {
        this.coordsTeleportedFromZ = coordsTeleportedFromZ
    }

    override fun getSpaceStationDimensionData(): HashMap<Int,Int>? {
        return spaceStationDimensionData
    }

    override fun setSpaceStationDimensionData(spaceStationDimensionData: HashMap<Int,Int>?) {
        this.spaceStationDimensionData = (spaceStationDimensionData)!!
    }

    override fun isOxygenSetupValid(): Boolean {
        return oxygenSetupValid
    }

    override fun setOxygenSetupValid(oxygenSetupValid: Boolean) {
        this.oxygenSetupValid = oxygenSetupValid
    }

    override fun isLastOxygenSetupValid(): Boolean {
        return lastOxygenSetupValid
    }

    override fun setLastOxygenSetupValid(lastOxygenSetupValid: Boolean) {
        this.lastOxygenSetupValid = lastOxygenSetupValid
    }

    override fun isTouchedGround(): Boolean {
        return touchedGround
    }

    override fun setTouchedGround(touchedGround: Boolean) {
        this.touchedGround = touchedGround
    }

    override fun isLastOnGround(): Boolean {
        return lastOnGround
    }

    override fun setLastOnGround(lastOnGround: Boolean) {
        this.lastOnGround = lastOnGround
    }

    override fun isInLander(): Boolean {
        return inLander
    }

    override fun setInLander(inLander: Boolean) {
        this.inLander = inLander
    }

    override fun hasJustLanded(): Boolean {
        return justLanded
    }

    override fun setJustLanded(justLanded: Boolean) {
        this.justLanded = justLanded
    }

    override fun getUnlockedSchematics(): MutableList<ISchematicPage>? {
        return unlockedSchematics
    }

    override fun setUnlockedSchematics(unlockedSchematics: MutableList<ISchematicPage>?) {
        this.unlockedSchematics = (unlockedSchematics)!!
    }

    override fun getLastUnlockedSchematics(): MutableList<ISchematicPage>? {
        return lastUnlockedSchematics
    }

    override fun setLastUnlockedSchematics(lastUnlockedSchematics: MutableList<ISchematicPage>?) {
        this.lastUnlockedSchematics = lastUnlockedSchematics
    }

    override fun getCryogenicChamberCooldown(): Int {
        return cryogenicChamberCooldown
    }

    override fun setCryogenicChamberCooldown(cryogenicChamberCooldown: Int) {
        this.cryogenicChamberCooldown = cryogenicChamberCooldown
    }

    override fun hasReceivedSoundWarning(): Boolean {
        return receivedSoundWarning
    }

    override fun setReceivedSoundWarning(receivedSoundWarning: Boolean) {
        this.receivedSoundWarning = receivedSoundWarning
    }

    override fun hasReceivedBedWarning(): Boolean {
        return receivedBedWarning
    }

    override fun setReceivedBedWarning(receivedBedWarning: Boolean) {
        this.receivedBedWarning = receivedBedWarning
    }

    override fun hasOpenedSpaceRaceManager(): Boolean {
        return openedSpaceRaceManager
    }

    override fun setOpenedSpaceRaceManager(openedSpaceRaceManager: Boolean) {
        this.openedSpaceRaceManager = openedSpaceRaceManager
    }

    override fun hasSentFlags(): Boolean {
        return sentFlags
    }

    override fun setSentFlags(sentFlags: Boolean) {
        this.sentFlags = sentFlags
    }

    override fun isNewInOrbit(): Boolean {
        return newInOrbit
    }

    override fun setNewInOrbit(newInOrbit: Boolean) {
        this.newInOrbit = newInOrbit
    }

    override fun isNewAdventureSpawn(): Boolean {
        return newAdventureSpawn
    }

    override fun setNewAdventureSpawn(newAdventureSpawn: Boolean) {
        this.newAdventureSpawn = newAdventureSpawn
    }

    override fun getBuildFlags(): Int {
        return buildFlags
    }

    override fun setBuildFlags(buildFlags: Int) {
        this.buildFlags = buildFlags
    }

    override fun getIncrementalDamage(): Int {
        return incrementalDamage
    }

    override fun setIncrementalDamage(incrementalDamage: Int) {
        this.incrementalDamage = incrementalDamage
    }

    override fun getStartDimension(): String? {
        return startDimension
    }

    override fun setStartDimension(startDimension: String?) {
        this.startDimension = (startDimension)!!
    }

    override fun saveNBTData(nbt: NBTTagCompound?) {
        (nbt)!!.put("ExtendedInventoryGC", this.extendedInventory.writeToNBT(NBTTagList(), requireNotNull(this.player?.get()).level().registryAccess()))
        (nbt)!!.putInt("playerAirRemaining", this.airRemaining)
        (nbt)!!.putInt("damageCounter", this.damageCounter)
        (nbt)!!.putBoolean("OxygenSetupValid", this.oxygenSetupValid)
        (nbt)!!.putBoolean("usingParachute2", this.usingParachute)
        (nbt)!!.putBoolean("usingPlanetSelectionGui", this.usingPlanetSelectionGui)
        (nbt)!!.putInt("teleportCooldown", this.teleportCooldown)
        (nbt)!!.putDouble("coordsTeleportedFromX", this.coordsTeleportedFromX)
        (nbt)!!.putDouble("coordsTeleportedFromZ", this.coordsTeleportedFromZ)
        (nbt)!!.putString("startDimension", this.startDimension)
        (nbt)!!.putString("spaceStationDimensionInfo", WorldUtil.spaceStationDataToString(this.spaceStationDimensionData))
        (nbt)!!.putInt("thermalLevel", this.thermalLevel)

        Collections.sort<ISchematicPage>((this.unlockedSchematics)!!)

        val tagList: NBTTagList = NBTTagList()

        for (page: ISchematicPage? in this.unlockedSchematics) {
            if (page != null) {
                val nbttagcompound: NBTTagCompound = NBTTagCompound()
                nbttagcompound.putInt("UnlockedPage", page.getPageID())
                tagList.add(nbttagcompound)
            }
        }

        (nbt)!!.put("Schematics", tagList)

        (nbt)!!.putInt("rocketStacksLength", this.stacks.size)
        (nbt)!!.putInt("SpaceshipTier", this.spaceshipTier)
        (nbt)!!.putInt("FuelLevel", this.fuelLevel)
        if (this.rocketItem != null) {
            val returnRocket: ItemStack = ItemStack(this.rocketItem, 1, this.rocketType)
            (nbt)!!.put("ReturnRocket", returnRocket.writeToNBT(NBTTagCompound()))
        }

        val nbttaglist: NBTTagList = NBTTagList()

        for (i: Int in this.stacks.indices) {
            val itemstack: ItemStack = this.stacks.get(i)

            if (!itemstack.isEmpty()) {
                val nbttagcompound: NBTTagCompound = NBTTagCompound()
                nbttagcompound.putByte("Slot", i.toByte())
                itemstack.writeToNBT(nbttagcompound)
                nbttaglist.add(nbttagcompound)
            }
        }

        if (!nbttaglist.isEmpty()) {
            (nbt)!!.put("RocketItems", nbttaglist)
        }

        val var4: NBTTagCompound = NBTTagCompound()
        if (this.launchpadStack != null) {
            (nbt)!!.put("LaunchpadStack", this.launchpadStack!!.writeToNBT(var4))
        } else {
            (nbt)!!.put("LaunchpadStack", var4)
        }

        (nbt)!!.putInt("CryogenicChamberCooldown", this.cryogenicChamberCooldown)
        (nbt)!!.putBoolean("ReceivedSoundWarning", this.receivedSoundWarning)
        (nbt)!!.putBoolean("ReceivedBedWarning", this.receivedBedWarning)
        (nbt)!!.putInt("BuildFlags", this.buildFlags)
        (nbt)!!.putBoolean("ShownSpaceRace", this.openedSpaceRaceManager)
        (nbt)!!.putInt("AstroMinerCount", this.astroMinerCount)
        val astroList: NBTTagList = NBTTagList()
        for (data: BlockVec3? in this.activeAstroMinerChunks) {
            if (data != null) {
                astroList.add(data.writeToNBT(NBTTagCompound()))
            }
        }
        (nbt)!!.put("AstroData", astroList)

        (nbt)!!.putInt("GlassColor1", this.glassColor1)
        (nbt)!!.putInt("GlassColor2", this.glassColor2)
        (nbt)!!.putInt("GlassColor3", this.glassColor3)

        val panelList: NBTTagList = NBTTagList()
        for (i: Int in 0..<BlockPanelLighting.PANELTYPES_LENGTH) {
            val stateNBT: NBTTagCompound = NBTTagCompound()
            val bs: IBlockState? = this.panelLightingBases[i]
            if (bs != null) {
                TileEntityPanelLight.writeBlockState(stateNBT, bs)
            }
            panelList.add(stateNBT)
        }
        (nbt)!!.put("PanLi", panelList)

        (nbt)!!.putInt("PanCo", this.panelLightingColor)
    }

    override fun loadNBTData(nbt: NBTTagCompound?) {
        try {
            this.airRemaining = (nbt)!!.getIntOr("playerAirRemaining", 0)
            this.damageCounter = (nbt)!!.getIntOr("damageCounter", 0)
            this.lastOxygenSetupValid = (nbt)!!.getBooleanOr("OxygenSetupValid", false)
            this.oxygenSetupValid = this.lastOxygenSetupValid
            this.thermalLevel = (nbt)!!.getIntOr("thermalLevel", 0)

            val registries = requireNotNull(this.player?.get()).level().registryAccess()
            if (nbt.containsTag("ExtendedInventoryGC")) {
                this.extendedInventory.readFromNBT(nbt.listOfType("ExtendedInventoryGC", 10), registries)
            } else {
                this.extendedInventory.readFromNBTOld(nbt.listOfType("Inventory", 10), registries)
            }

            this.player?.get()?.let { CommandGCInv.restoreOnLogin(it, this.extendedInventory) }

            if ((nbt)!!.containsTag("SpaceshipTier")) {
                this.spaceshipTier = (nbt)!!.getIntOr("SpaceshipTier", 0)
            }

            //New keys in version 3.0.5.220
            if ((nbt)!!.containsTag("FuelLevel")) {
                this.fuelLevel = (nbt)!!.getIntOr("FuelLevel", 0)
            }
            if ((nbt)!!.containsTag("ReturnRocket")) {
                val returnRocket: ItemStack = ItemStack((nbt)!!.getCompoundOrEmpty("ReturnRocket"))
                this.rocketItem = returnRocket.getItem()
                this.rocketType = returnRocket.legacyItemValue
            }

            this.usingParachute = (nbt)!!.getBooleanOr("usingParachute2", false)
            this.usingPlanetSelectionGui = (nbt)!!.getBooleanOr("usingPlanetSelectionGui", false)
            this.teleportCooldown = (nbt)!!.getIntOr("teleportCooldown", 0)
            this.coordsTeleportedFromX = (nbt)!!.getDoubleOr("coordsTeleportedFromX", 0.0)
            this.coordsTeleportedFromZ = (nbt)!!.getDoubleOr("coordsTeleportedFromZ", 0.0)
            this.startDimension = if ((nbt)!!.containsTag("startDimension")) (nbt)!!.getStringOr("startDimension", "") else ""
            if ((nbt)!!.containsTag("spaceStationDimensionID")) {
                // If loading from an old save file, the home space station is always the overworld, so use 0 as home planet
                this.spaceStationDimensionData =
                    WorldUtil.stringToSpaceStationData("0$" + (nbt)!!.getIntOr("spaceStationDimensionID", 0))
            } else {
                this.spaceStationDimensionData =
                    WorldUtil.stringToSpaceStationData((nbt)!!.getStringOr("spaceStationDimensionInfo", ""))
            }

            if ((nbt)!!.getBooleanOr("usingPlanetSelectionGui", false)) {
                this.openPlanetSelectionGuiCooldown = 20
            }

            if ((nbt)!!.containsTag("RocketItems") && (nbt)!!.containsTag("rocketStacksLength")) {
                val length: Int = (nbt)!!.getIntOr("rocketStacksLength", 0)

                this.stacks = NonNullList.withSize<ItemStack>(length, ItemStack.EMPTY)

                val nbttaglist1: NBTTagList = (nbt)!!.listOfType("RocketItems", 10)

                for (i: Int in 0..<nbttaglist1.size) {
                    val nbttagcompound: NBTTagCompound = nbttaglist1.getCompoundOrEmpty(i)
                    val j: Int = nbttagcompound.getByteOr("Slot", 0).toInt() and 255

                    if (j >= 0 && j < this.stacks.size) {
                        this.stacks.set(j, ItemStack(nbttagcompound))
                    }
                }
            }

            this.unlockedSchematics = ArrayList<ISchematicPage>()

            if (p != null) {
                for (i: Int in 0..<(nbt)!!.listOfType("Schematics", 10).size) {
                    val nbttagcompound: NBTTagCompound = (nbt)!!.listOfType("Schematics", 10).getCompoundOrEmpty(i)

                    val j: Int = nbttagcompound.getIntOr("UnlockedPage", 0)

                    SchematicRegistry.addUnlockedPage(p, SchematicRegistry.getMatchingRecipeForID(j))
                }
            }

            Collections.sort<ISchematicPage>((this.unlockedSchematics)!!)

            this.cryogenicChamberCooldown = (nbt)!!.getIntOr("CryogenicChamberCooldown", 0)

            if ((nbt)!!.containsTag("ReceivedSoundWarning")) {
                this.receivedSoundWarning = (nbt)!!.getBooleanOr("ReceivedSoundWarning", false)
            }
            if ((nbt)!!.containsTag("ReceivedBedWarning")) {
                this.receivedBedWarning = (nbt)!!.getBooleanOr("ReceivedBedWarning", false)
            }

            if ((nbt)!!.containsTag("LaunchpadStack")) {
                this.launchpadStack = ItemStack((nbt)!!.getCompoundOrEmpty("LaunchpadStack"))
            } else {
                // for backwards compatibility with saves which don't have this tag - players can't lose launchpads
                this.launchpadStack = ItemStack(GCBlocks.landingPad, 9, 0)
            }

            if ((nbt)!!.containsTag("BuildFlags")) {
                this.buildFlags = (nbt)!!.getIntOr("BuildFlags", 0)
            }

            if ((nbt)!!.containsTag("ShownSpaceRace")) {
                this.openedSpaceRaceManager = (nbt)!!.getBooleanOr("ShownSpaceRace", false)
            }

            if ((nbt)!!.containsTag("AstroMinerCount")) {
                this.astroMinerCount = (nbt)!!.getIntOr("AstroMinerCount", 0)
            }
            if ((nbt)!!.containsTag("AstroData")) {
                this.activeAstroMinerChunks.clear()
                val astroList: NBTTagList = (nbt)!!.listOfType("AstroData", 10)
                for (i: Int in 0..<astroList.size) {
                    val nbttagcompound: NBTTagCompound = astroList.getCompoundOrEmpty(i)
                    val data: BlockVec3 = BlockVec3.readFromNBT(nbttagcompound)
                    this.activeAstroMinerChunks.add(data)
                }
                if (GalacticraftCore.isPlanetsLoaded) {
                    AsteroidsTickHandlerServer.loadAstroChunkList(this.activeAstroMinerChunks)
                }
            }

            if ((nbt)!!.containsTag("GlassColor1")) {
                this.glassColor1 = (nbt)!!.getIntOr("GlassColor1", 0)
                this.glassColor2 = (nbt)!!.getIntOr("GlassColor2", 0)
                this.glassColor3 = (nbt)!!.getIntOr("GlassColor3", 0)
            }

            if ((nbt)!!.containsTag("PanLi")) {
                val panels: NBTTagList = (nbt)!!.listOfType("PanLi", 10)
                for (i: Int in 0..<panels.size) {
                    if (i == BlockPanelLighting.PANELTYPES_LENGTH) break
                    val stateNBT: NBTTagCompound = panels.getCompoundOrEmpty(i)
                    val bs: IBlockState = TileEntityPanelLight.readBlockState(stateNBT)
                    this.panelLightingBases[i] = if (bs.getBlock() === Blocks.AIR) null else bs
                }
            }

            if ((nbt)!!.containsTag("PanCo")) {
                this.panelLightingColor = (nbt)!!.getIntOr("PanCo", 0)
            }


            GCLog.debug("Loading GC player data for " + PlayerUtil.getName(player!!.get()) + " : " + this.buildFlags)

            this.sentFlags = false
        } catch (e: Exception) {
            GCLog.severe("Found error in saved Galacticraft player data for " + PlayerUtil.getName(player!!.get()) + " - this should fix itself next relog.")
            e.printStackTrace()
        }

        GCLog.debug("Finished loading GC player data for " + PlayerUtil.getName(player!!.get()) + " : " + this.buildFlags)
    }

    override fun copyFrom(oldData: GCPlayerStats?, keepInv: Boolean) {
        if (keepInv) {
            this.extendedInventory.copyInventory((oldData)!!.getExtendedInventory())
        }

        this.spaceStationDimensionData = ((oldData)!!.getSpaceStationDimensionData())!!
        this.unlockedSchematics = ((oldData)!!.getUnlockedSchematics())!!
        this.receivedSoundWarning = (oldData)!!.hasReceivedSoundWarning()
        this.receivedBedWarning = (oldData)!!.hasReceivedBedWarning()
        this.openedSpaceRaceManager = (oldData)!!.hasOpenedSpaceRaceManager()
        this.spaceRaceInviteTeamID = (oldData)!!.getSpaceRaceInviteTeamID()
        this.buildFlags = (oldData)!!.getBuildFlags()
        this.glassColor1 = (oldData)!!.getGlassColor1()
        this.glassColor2 = (oldData)!!.getGlassColor2()
        this.glassColor3 = (oldData)!!.getGlassColor3()
        this.panelLightingBases = ((oldData)!!.getPanelLightingBases())!!
        this.panelLightingColor = (oldData)!!.getPanelLightingColor()
        this.astroMinerCount = (oldData)!!.getAstroMinerCount()
        this.activeAstroMinerChunks = ((oldData)!!.getActiveAstroMinerChunks())!!
        this.sentFlags = false
    }

    override fun setGlassColors(color1: Int, color2: Int, color3: Int) {
        var changes: Boolean = false
        if (this.glassColor1 != color1) {
            changes = true
            this.glassColor1 = color1
        }
        if (this.glassColor2 != color2) {
            changes = true
            this.glassColor2 = color2
        }
        if (this.glassColor3 != color3) {
            changes = true
            this.glassColor3 = color3
        }
        if (changes) ColorUtil.sendUpdatedColorsToPlayer(this)
    }

    override fun getGlassColor1(): Int {
        return glassColor1
    }

    override fun getGlassColor2(): Int {
        return glassColor2
    }

    override fun getGlassColor3(): Int {
        return glassColor3
    }

    override fun getPanelLightingBases(): Array<IBlockState?>? {
        return panelLightingBases
    }

    override fun getPanelLightingColor(): Int {
        return panelLightingColor
    }

    override fun setPanelLightingColor(color: Int) {
        panelLightingColor = color
    }

    override fun getMiscNetworkedStats(): Array<Any?> {
        val length: Int = 2 + BlockPanelLighting.PANELTYPES_LENGTH * 2
        val result: Array<Any?> = arrayOfNulls<Any>(length)
        result[0] = this.getBuildFlags()
        BlockPanelLighting.getNetworkedData(result, this.panelLightingBases)
        result[length - 1] = this.panelLightingColor
        return result
    }

    override fun setSavedSpeed(value: Float) {
        this.savedSpeed = value
    }

    override fun getSavedSpeed(): Float {
        return this.savedSpeed
    }
}
