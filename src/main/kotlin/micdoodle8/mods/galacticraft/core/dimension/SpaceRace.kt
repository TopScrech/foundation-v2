package micdoodle8.mods.galacticraft.core.dimension

import java.util.ArrayList
import java.util.HashMap

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.wrappers.FlagData
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

open class SpaceRace {
    private var spaceRaceID = 0
    private var playerNames: MutableList<String?> = Lists.newArrayList<String?>()

    @JvmField
    var teamName: String? = null
    private var flagData: FlagData? = null
    private var teamColor: Vector3? = null
    private var ticksSpent = 0
    private val celestialBodyStatusList: MutableMap<CelestialBody?,Int> = HashMap<CelestialBody?,Int>(4, 1f)
    private val schematicsToUnlock: MutableMap<String?, MutableList<ItemStack?>?> =
        HashMap<String?, MutableList<ItemStack?>?>(4, 1f)

    constructor()

    constructor(playerNames: MutableList<String?>, teamName: String?, flagData: FlagData?, teamColor: Vector3?) {
        this.playerNames = playerNames
        this.teamName = teamName
        this.ticksSpent = 0
        this.flagData = flagData
        this.teamColor = teamColor
        this.spaceRaceID = ++lastSpaceRaceID
    }

    open fun loadFromNBT(nbt: NBTTagCompound) {
        this.teamName = nbt.getString("TeamName")
        if (ConfigManagerCore.enableDebug) {
            GCLog.info("Loading spacerace data for team " + this.teamName)
        }
        this.spaceRaceID = nbt.getInteger("SpaceRaceID")
        this.ticksSpent = nbt.getLong("TicksSpent").toInt() //Deal with legacy error
        this.flagData = FlagData.readFlagData(nbt)
        this.teamColor = Vector3(nbt.getDouble("teamColorR"), nbt.getDouble("teamColorG"), nbt.getDouble("teamColorB"))

        var tagList: NBTTagList = nbt.getTagList("PlayerList", 10)
        for (i: Int in 0..<tagList.tagCount()) {
            val tagAt: NBTTagCompound = tagList.getCompoundTagAt(i)
            this.playerNames.add(tagAt.getString("PlayerName"))
        }

        tagList = nbt.getTagList("CelestialBodyList", 10)
        for (i: Int in 0..<tagList.tagCount()) {
            val tagAt: NBTTagCompound = tagList.getCompoundTagAt(i)

            val body: CelestialBody? =
                GalaxyRegistry.getCelestialBodyFromUnlocalizedName(tagAt.getString("CelestialBodyName"))

            if (body != null) {
                this.celestialBodyStatusList.put(body, tagAt.getInteger("TimeTaken"))
            }
        }

        tagList = nbt.getTagList("SchList", 10)
        for (i: Int in 0..<tagList.tagCount()) {
            val tagAt: NBTTagCompound = tagList.getCompoundTagAt(i)
            val name: String = tagAt.getString("Mem")
            if (name != null && this.playerNames.contains(name)) {
                val itemList: NBTTagList = tagAt.getTagList("Sch", 10)
                for (j: Int in 0..<itemList.tagCount()) {
                    addNewSchematic(name, ItemStack(itemList.getCompoundTagAt(j)))
                }
            }
        }

        if (ConfigManagerCore.enableDebug) {
            GCLog.info("Loaded spacerace team data OK.")
        }
    }

    open fun saveToNBT(nbt: NBTTagCompound) {
        if (ConfigManagerCore.enableDebug) {
            GCLog.info("Saving spacerace data for team " + this.teamName)
        }
        nbt.setString("TeamName", this.teamName)
        nbt.setInteger("SpaceRaceID", this.spaceRaceID)
        nbt.setLong("TicksSpent", this.ticksSpent.toLong())
        this.flagData!!.saveFlagData(nbt)
        nbt.setDouble("teamColorR", this.teamColor!!.x)
        nbt.setDouble("teamColorG", this.teamColor!!.y)
        nbt.setDouble("teamColorB", this.teamColor!!.z)

        var tagList: NBTTagList = NBTTagList()
        for (player in this.playerNames) {
            val tagComp: NBTTagCompound = NBTTagCompound()
            tagComp.setString("PlayerName", player)
            tagList.appendTag(tagComp)
        }

        nbt.setTag("PlayerList", tagList)

        tagList = NBTTagList()
        for (celestialBody: MutableMap.MutableEntry<CelestialBody?,Int> in this.celestialBodyStatusList.entries) {
            val tagComp: NBTTagCompound = NBTTagCompound()
            tagComp.setString("CelestialBodyName", celestialBody.key!!.getTranslationKey())
            tagComp.setInteger("TimeTaken", celestialBody.value!!)
            tagList.appendTag(tagComp)
        }
        nbt.setTag("CelestialBodyList", tagList)

        tagList = NBTTagList()
        for (schematic in this.schematicsToUnlock.entries) {
            if (this.playerNames.contains(schematic.key)) {
                val tagComp: NBTTagCompound = NBTTagCompound()
                tagComp.setString("Mem", schematic.key)
                val itemList: NBTTagList = NBTTagList()
                for (stack in schematic.value!!) {
                    val itemTag: NBTTagCompound = NBTTagCompound()
                    (stack)!!.writeToNBT(itemTag)
                    itemList.appendTag(itemTag)
                }
                tagComp.setTag("Sch", itemList)
                tagList.appendTag(tagComp)
            }
        }
        nbt.setTag("SchList", tagList)

        if (ConfigManagerCore.enableDebug) {
            GCLog.info("Saved spacerace team data OK.")
        }
    }

    open fun tick() {
        this.ticksSpent++
    }

    open fun getTeamName(): String? {
        var ret: String? = this.teamName
        if (DEFAULT_NAME == ret) {
            ret = GCCoreUtil.translate(DEFAULT_NAME)
        }
        return ret
    }

    open fun getPlayerNames(): MutableList<String?>? {
        return this.playerNames
    }

    open fun getFlagData(): FlagData? {
        return this.flagData
    }

    open fun setFlagData(flagData: FlagData?) {
        this.flagData = flagData
    }

    open fun getTeamColor(): Vector3? {
        return this.teamColor
    }

    open fun setTeamColor(teamColor: Vector3?) {
        this.teamColor = teamColor
    }

    open fun setTeamName(teamName: String?) {
        this.teamName = teamName
    }

    open fun setPlayerNames(playerNames: MutableList<String?>) {
        this.playerNames = playerNames
    }

    open fun setSpaceRaceID(raceID: Int) {
        this.spaceRaceID = raceID
    }

    open fun getSpaceRaceID(): Int {
        return this.spaceRaceID
    }

    open fun getCelestialBodyStatusList(): MutableMap<CelestialBody?,Int>? {
        return ImmutableMap.copyOf<CelestialBody?,Int>(this.celestialBodyStatusList)
    }

    open fun setCelestialBodyReached(body: CelestialBody?) {
        this.celestialBodyStatusList.put(body, this.ticksSpent)
    }

    open fun getTicksSpent(): Int {
        return this.ticksSpent
    }

    override fun hashCode(): Int {
        return this.spaceRaceID
    }

    override fun equals(other: Any?): Boolean {
        if (other is SpaceRace) {
            return other.getSpaceRaceID() == this.getSpaceRaceID()
        }

        return false
    }

    /*
     * Used to store schematics which need to be unlocked for offline players
     */
    open fun addNewSchematic(member: String?, stack: ItemStack) {
        var list: MutableList<ItemStack?>?
        list = this.schematicsToUnlock.get(member)
        if (list == null) {
            list = ArrayList<ItemStack?>(1)
            this.schematicsToUnlock.put(member, list)
        }
        list.add(stack)
    }

    /*
     * Used to give a stored schematic to a team player on next login
     */
    open fun updatePlayerSchematics(player: EntityPlayerMP?) {
        val list: MutableList<ItemStack?>? = this.schematicsToUnlock.get(PlayerUtil.getName(player))
        if (list != null) {
            for (stack: ItemStack? in list) {
                SchematicRegistry.unlockNewPage(player, stack)
            }
            this.schematicsToUnlock.remove(PlayerUtil.getName(player))
        }
    }

    companion object {
        const val DEFAULT_NAME: String = "gui.space_race.unnamed"
        private var lastSpaceRaceID = 0
    }
}
