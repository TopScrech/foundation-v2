package micdoodle8.mods.galacticraft.core.dimension

import net.minecraft.network.chat.Component

import foundation.persistence.listOfType

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import micdoodle8.mods.galacticraft.core.wrappers.FlagData
import java.util.ArrayList
import java.util.HashSet

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.server.MinecraftServer
import net.minecraft.network.chat.Style

import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.server.level.ServerLevel as WorldServer

object SpaceRaceManager {
    private val spaceRaces: MutableSet<SpaceRace> = Sets.newHashSet<SpaceRace>()

    @JvmStatic
    fun addSpaceRace(spaceRace: SpaceRace): SpaceRace? {
        spaceRaces.remove(spaceRace)
        spaceRaces.add(spaceRace)
        return spaceRace
    }

    @JvmStatic
    fun removeSpaceRace(race: SpaceRace?) {
        spaceRaces.remove(race)
    }

    @JvmStatic
    fun tick() {
        for (race: SpaceRace in spaceRaces) {
            var playerOnline: Boolean = false

            for (player in PlayerUtil.getPlayersOnline()) {
                if ((race.getPlayerNames())!!.contains((PlayerUtil.getName(player))!!)) {
                    val body: CelestialBody? =
                        GalaxyRegistry.getCelestialBodyFromDimensionID((player)!!.level().provider.getDimension())

                    if (body != null) {
                        if (!(race.getCelestialBodyStatusList())!!.containsKey(body)) {
                            race.setCelestialBodyReached(body)
                        }
                    }

                    playerOnline = true
                }
            }

            if (playerOnline) {
                race.tick()
            }
        }
    }

    @JvmStatic
    fun loadSpaceRaces(nbt: NBTTagCompound) {
        val tagList: NBTTagList = nbt.listOfType("SpaceRaceList", 10)

        for (i: Int in 0..<tagList.size) {
            val nbt2: NBTTagCompound = tagList.getCompoundOrEmpty(i)
            val race: SpaceRace = SpaceRace()
            race.loadFromNBT(nbt2)
            spaceRaces.add(race)
        }
    }

    @JvmStatic
    fun saveSpaceRaces(nbt: NBTTagCompound): NBTTagCompound {
        val tagList: NBTTagList = NBTTagList()

        for (race: SpaceRace in spaceRaces) {
            val nbt2: NBTTagCompound = NBTTagCompound()
            race.saveToNBT(nbt2)
            tagList.add(nbt2)
        }

        nbt.put("SpaceRaceList", tagList)
        return nbt
    }

    @JvmStatic
    fun getSpaceRaceFromPlayer(username: String?): SpaceRace? {
        for (race: SpaceRace in spaceRaces) {
            if ((race.getPlayerNames())!!.contains((username)!!)) {
                return race
            }
        }

        return null
    }

    @JvmStatic
    fun getSpaceRaceFromID(teamID: Int): SpaceRace? {
        for (race: SpaceRace in spaceRaces) {
            if (race.getSpaceRaceID() == teamID) {
                return race
            }
        }

        return null
    }

    @JvmStatic
    fun sendSpaceRaceData(theServer: MinecraftServer, toPlayer: EntityPlayerMP?, spaceRace: SpaceRace?) {
        if (spaceRace != null) {
            val objList: MutableList<Any?> = ArrayList<Any?>()
            objList.add(spaceRace.getSpaceRaceID())
            objList.add(spaceRace.getTeamName())
            objList.add(spaceRace.getFlagData())
            objList.add(spaceRace.getTeamColor())
            objList.add((spaceRace.getPlayerNames())!!.toTypedArray<String?>())

            if (toPlayer != null) {
                (GalacticraftCore.packetPipeline)!!.sendTo(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_UPDATE_SPACE_RACE_DATA,
                        GCCoreUtil.getDimensionID(toPlayer.level()),
                        objList
                    ), toPlayer
                )
                spaceRace.updatePlayerSchematics(toPlayer)
            } else {
                for (server: WorldServer in theServer.worlds) {
                    (GalacticraftCore.packetPipeline)!!.sendToDimension(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_UPDATE_SPACE_RACE_DATA,
                            GCCoreUtil.getDimensionID(server),
                            objList
                        ), GCCoreUtil.getDimensionID(server)
                    )
                }
            }
        }
    }

    @JvmStatic
    fun getSpaceRaces(): ImmutableSet<SpaceRace?>? {
        return ImmutableSet.copyOf<SpaceRace?>(HashSet<SpaceRace?>(spaceRaces))
    }

    @JvmStatic
    fun onPlayerRemoval(server: MinecraftServer, player: String?, race: SpaceRace) {
        for (member: String? in (race.getPlayerNames())!!) {
            val memberObj: EntityPlayerMP? = PlayerUtil.getPlayerForUsernameVanilla(server, (member)!!)

            if (memberObj != null) {
                memberObj.sendSystemMessage(
                    Component.literal(
                        EnumColor.DARK_AQUA.toString() + GCCoreUtil.translateWithFormat(
                            "gui.space_race.chat.remove_success",
                            EnumColor.RED.toString() + player + EnumColor.DARK_AQUA
                        )
                    ).setStyle(Style().setColor(TextFormatting.DARK_AQUA))
                )
            }
        }

        val playerList: MutableList<String?> = ArrayList<String?>()
        playerList.add(player)
        val newRace: SpaceRace = micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager.addSpaceRace(
            micdoodle8.mods.galacticraft.core.dimension.SpaceRace(
                playerList,
                micdoodle8.mods.galacticraft.core.dimension.SpaceRace.Companion.DEFAULT_NAME,
                micdoodle8.mods.galacticraft.core.wrappers.FlagData(48, 32),
                micdoodle8.mods.galacticraft.api.vector.Vector3(1.0, 1.0, 1.0)
            )
        )!!
        val playerToRemove: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayerUsername(server, (player)!!, true)

        if (playerToRemove != null) {
            sendSpaceRaceData(server, playerToRemove, newRace)
            sendSpaceRaceData(server, playerToRemove, race)
        }
    }

    @JvmStatic
    fun teamUnlockSchematic(player: EntityPlayerMP?, stack: ItemStack?) {
        val race: SpaceRace? = getSpaceRaceFromPlayer(PlayerUtil.getName(player))
        if (race == null) return
        val server: MinecraftServer = (player)!!.server
        for (member: String? in (race.getPlayerNames())!!) {
            if (player!!.getName().equals(member, ignoreCase = true)) continue

            val memberObj: EntityPlayerMP? = PlayerUtil.getPlayerForUsernameVanilla(server, (member)!!)
            if (memberObj != null) {
                SchematicRegistry.unlockNewPage(memberObj, stack)
            } else {
                race.addNewSchematic(member, (stack)!!)
            }
        }
    }
}
