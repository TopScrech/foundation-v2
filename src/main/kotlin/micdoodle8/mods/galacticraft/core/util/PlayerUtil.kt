package micdoodle8.mods.galacticraft.core.util

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import com.mojang.authlib.GameProfile
import java.util.UUID
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.HashMap

object PlayerUtil {
    @JvmField
    var knownSkins: HashMap<String?, GameProfile?> = HashMap<String?, GameProfile?>()

    @JvmStatic
    fun getPlayerForUsernameVanilla(server: MinecraftServer, username: String): EntityPlayerMP? {
        return server.getPlayerList().getPlayerByUsername(username)
    }

    @JvmStatic
    fun getPlayerBaseServerFromPlayerUsername(username: String, ignoreCase: Boolean): EntityPlayerMP? {
        val server: MinecraftServer? = GCCoreUtil.getServer()
        return getPlayerBaseServerFromPlayerUsername(server, username, ignoreCase)
    }

    @JvmStatic
    fun getPlayerBaseServerFromPlayerUsername(
        server: MinecraftServer?,
        username: String,
        ignoreCase: Boolean
    ): EntityPlayerMP? {
        if (server != null) {
            if (ignoreCase) {
                return getPlayerForUsernameVanilla(server, username)
            } else {
                val iterator: MutableIterator<*> = server.getPlayerList().getPlayers().iterator()
                var entityplayermp: EntityPlayerMP

                do {
                    if (!iterator.hasNext()) {
                        return null
                    }

                    entityplayermp = iterator.next() as EntityPlayerMP
                } while (!entityplayermp.getName().equals(username, ignoreCase = true))

                return entityplayermp
            }
        }

        GCLog.severe("Warning: Could not find player base server instance for player " + username)

        return null
    }

    @JvmStatic
    fun getPlayerBaseServerFromPlayer(player: EntityPlayer?, ignoreCase: Boolean): EntityPlayerMP? {
        if (player == null) {
            return null
        }

        if (player is EntityPlayerMP) {
            return player
        }

        return getPlayerBaseServerFromPlayerUsername(player.getName(), ignoreCase)
    }

    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun getPlayerBaseClientFromPlayer(player: EntityPlayer?, ignoreCase: Boolean): EntityPlayerSP? {
        val clientPlayer: EntityPlayerSP? = FMLClientHandler.instance().getClientPlayerEntity()

        if (clientPlayer == null && player != null) {
            GCLog.severe("Warning: Could not find player base client instance for player " + getName(player))
        }

        return clientPlayer
    }

    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun getOtherPlayerProfile(name: String?): GameProfile {
        return knownSkins.get(name)!!
    }

    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun makeOtherPlayerProfile(strName: String?, strUUID: String): GameProfile {
        var profile: GameProfile? = null
        for (e: Any? in FMLClientHandler.instance().getWorldClient().getLoadedEntityList()) {
            if (e is AbstractClientPlayer) {
                val gp2: GameProfile = e.getGameProfile()
                if (gp2.getName() == strName) {
                    profile = gp2
                    break
                }
            }
        }
        if (profile == null) {
            val uuid: UUID = if (strUUID.isEmpty()) UUID.randomUUID() else UUID.fromString(strUUID)
            profile = GameProfile(uuid, strName)
        }

        knownSkins.put(strName, profile)
        return profile
    }

    @JvmStatic
    @OnlyIn(Dist.CLIENT)
    fun getSkinForName(strName: String, strUUID: String, dimID: Int): GameProfile {
        var profile: GameProfile? = FMLClientHandler.instance().getClientPlayerEntity().getGameProfile()
        if (strName != profile!!.getName()) {
            profile = getOtherPlayerProfile(strName)
            if (profile == null) {
                profile = makeOtherPlayerProfile(strName, strUUID)
            }
            if (!profile.getProperties().containsKey("textures")) {
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_REQUEST_PLAYERSKIN,
                        dimID,
                        arrayOf<Any>(strName)
                    )
                )
            }
        }
        return profile
    }

    @JvmStatic
    fun getPlayerByUUID(theUUID: UUID?): EntityPlayerMP? {
        val players: MutableList<EntityPlayerMP> = getPlayersOnline()
        var entityplayermp: EntityPlayerMP
        for (i: Int in players.indices.reversed()) {
            entityplayermp = players.get(i) as EntityPlayerMP

            if (entityplayermp.getUUID() == theUUID) {
                return entityplayermp
            }
        }
        return null
    }


    @JvmStatic
    fun getPlayersOnline(): MutableList<EntityPlayerMP> {
        return (GCCoreUtil.getServer())!!.getPlayerList().getPlayers()
    }


    @JvmStatic
    fun isPlayerOnline(player: EntityPlayerMP): Boolean {
        return player.server.getPlayerList().getPlayers().contains(player)
    }

    @JvmStatic
    fun getName(player: EntityPlayer?): String? {
        if (player == null) return null

        if (player.getGameProfile() == null) return null

        return player.getGameProfile().getName()
    }
}
