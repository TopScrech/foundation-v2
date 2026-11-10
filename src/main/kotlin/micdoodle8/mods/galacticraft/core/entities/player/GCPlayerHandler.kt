package micdoodle8.mods.galacticraft.core.entities.player

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint

import java.util.UUID
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.event.oxygen.GCCoreOxygenSuffocationEvent
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.api.item.IArmorCorrosionResistant
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.api.item.IItemThermal
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon
import micdoodle8.mods.galacticraft.core.blocks.BlockUnlitTorch
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase
import micdoodle8.mods.galacticraft.core.entities.EntityMeteor
import micdoodle8.mods.galacticraft.core.entities.EntityParachest
import micdoodle8.mods.galacticraft.core.items.ItemParaChute
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.DamageSourceGC
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon
import micdoodle8.mods.galacticraft.core.wrappers.Footprint
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemArmorAsteroids
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.MobEffects
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.SPacketRespawn
import net.minecraft.potion.PotionEffect
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.server.permission.PermissionAPI
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class GCPlayerHandler {
    private val torchItemsSpace: MutableList<Item?> = ArrayList<Item?>(2)
    private val torchItemsRegular: MutableList<Item?> = ArrayList<Item?>(2)
    private val itemChangesPre: MutableList<ItemStack> = ArrayList<ItemStack>(6)
    private val itemChangesPost: MutableList<ItemStack?> = ArrayList<ItemStack?>(6)

    @SubscribeEvent
    open fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        if (event.player is EntityPlayerMP) {
            this.onPlayerLogin(event.player as EntityPlayerMP)
        }
    }

    //    @SubscribeEvent
    //    public void onPlayerLogout(PlayerLoggedOutEvent event)
    //    {
    //        if (event.player instanceof EntityPlayerMP)
    //        {
    //            this.onPlayerLogout((EntityPlayerMP) event.player);
    //        }
    //    }
    //
    //    @SubscribeEvent
    //    public void onPlayerRespawn(PlayerRespawnEvent event)
    //    {
    //        if (event.player instanceof EntityPlayerMP)
    //        {
    //            this.onPlayerRespawn((EntityPlayerMP) event.player);
    //        }
    //    }
    @SubscribeEvent
    open fun onPlayerCloned(event: net.minecraftforge.event.entity.player.PlayerEvent.Clone) {
        val oldStats: GCPlayerStats? = GCPlayerStats.Companion.get(event.getOriginal())
        val newStats: GCPlayerStats = (GCPlayerStats.Companion.get(event.getEntityPlayer()))!!
        newStats.copyFrom(
            oldStats,
            !event.isWasDeath() || event.getOriginal().world.getGameRules().getBoolean("keepInventory")
        )
        if (event.getOriginal() is EntityPlayerMP && event.getEntityPlayer() is EntityPlayerMP) {
            TileEntityTelemetry.updateLinkedPlayer(
                event.getOriginal() as EntityPlayerMP?,
                event.getEntityPlayer() as EntityPlayerMP?
            )
        }
        if (event.isWasDeath() && event.getOriginal() is EntityPlayerMP) {
            val uu: UUID = event.getOriginal().getPersistentID()
            if (event.getOriginal().world.provider is IGalacticraftWorldProvider) {
                val timeA: Int? = deathTimes.get(uu)
                val bb: Int = (event.getOriginal() as EntityPlayerMP).server.getTickCounter()
                if (timeA != null && (bb - timeA) < 1500) {
                    val msg: String =
                        EnumColor.YELLOW.toString() + GCCoreUtil.translate("commands.gchouston.help.1") + " " + EnumColor.WHITE + GCCoreUtil.translate(
                            "commands.gchouston.help.2"
                        )
                    event.getOriginal().sendMessage(TextComponentString(msg))
                }
                deathTimes.put(uu, bb)
            } else {
                deathTimes.remove(uu)
            }
        }
    }

    @SubscribeEvent
    open fun onAttachCapability(event: AttachCapabilitiesEvent<Entity?>) {
        if (event.getObject() is EntityPlayerMP) {
            event.addCapability(
                GCCapabilities.GC_PLAYER_PROP,
                CapabilityProviderStats(event.getObject() as EntityPlayerMP?)
            )
        } else if (event.getObject() is EntityPlayer && (event.getObject() as EntityPlayer).world.isRemote) {
            this.onAttachCapabilityClient(event)
        }
    }

    @SideOnly(Side.CLIENT)
    private fun onAttachCapabilityClient(event: AttachCapabilitiesEvent<Entity?>) {
        if (event.getObject() is EntityPlayerSP) {
            event.addCapability(
                GCCapabilities.GC_PLAYER_CLIENT_PROP,
                CapabilityProviderStatsClient(event.getObject() as EntityPlayerSP?)
            )
        }
    }

    //    @SubscribeEvent
    //    public void onEntityConstructing(EntityEvent.EntityConstructing event)
    //    {
    //        if (event.getEntity() instanceof EntityPlayerMP && GCPlayerStats.get((EntityPlayerMP) event.getEntity()) == null)
    //        {
    //            GCPlayerStats.register((EntityPlayerMP) event.getEntity());
    //        }
    //
    //        if (event.entity instanceof EntityPlayer && event.entity.world.isRemote)
    //        {
    //            this.onEntityConstructingClient(event);
    //        }
    //    }
    //
    //    @SideOnly(Side.CLIENT)
    //    public void onEntityConstructingClient(EntityEvent.EntityConstructing event)
    //    {
    //        if (event.getEntity() instanceof EntityPlayerSP)
    //        {
    //            if (GCPlayerStatsClient.get((EntityPlayerSP) event.getEntity()) == null)
    //            {
    //                GCPlayerStatsClient.register((EntityPlayerSP) event.getEntity());
    //            }
    //
    //            Minecraft.getMinecraft().gameSettings.sendSettingsToServer();
    //        }
    //    }
    private fun onPlayerLogin(player: EntityPlayerMP) {
//        GCPlayerStats oldData = this.playerStatsMap.remove(player.getPersistentID());
//        if (oldData != null)
//        {
//            oldData.saveNBTData(player.getEntityData());
//        }

        val stats: GCPlayerStats = (GCPlayerStats.Companion.get(player))!!

        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_GET_CELESTIAL_BODY_LIST,
                GCCoreUtil.getDimensionID(player.world),
                arrayOf<Any?>()
            ), player
        )
        val repeatCount: Int = stats.getBuildFlags() shr 9
        if (repeatCount < 3) {
            stats.setBuildFlags(stats.getBuildFlags() and 1536)
        }
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_STATS,
                GCCoreUtil.getDimensionID(player.world),
                (stats.getMiscNetworkedStats())!!
            ), player
        )
        ColorUtil.sendUpdatedColorsToPlayer(stats)
    }

    protected open fun checkThermalStatus(player: EntityPlayerMP, playerStats: GCPlayerStats) {
        if (player.world.provider is IGalacticraftWorldProvider && !player.capabilities.isCreativeMode && !CompatibilityManager.isAndroid(
                player
            )
        ) {
            val thermalPaddingHelm: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(6)
            val thermalPaddingChestplate: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(7)
            val thermalPaddingLeggings: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(8)
            val thermalPaddingBoots: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(9)
            var lowestThermalStrength: Float = 0.0f
            if (!thermalPaddingHelm.isEmpty() && !thermalPaddingChestplate.isEmpty() && !thermalPaddingLeggings.isEmpty() && !thermalPaddingBoots.isEmpty()) {
                if (thermalPaddingHelm.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingHelm.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                if (thermalPaddingChestplate.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingChestplate.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                if (thermalPaddingLeggings.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingLeggings.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                if (thermalPaddingBoots.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingBoots.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                lowestThermalStrength /= 4.0f
                lowestThermalStrength = abs(lowestThermalStrength) //It shouldn't be negative, but just in case!
            }

            val provider: IGalacticraftWorldProvider = player.world.provider as IGalacticraftWorldProvider
            var thermalLevelMod: Float = provider.getThermalLevelModifier()
            var absThermalLevelMod: Float = abs(thermalLevelMod)

            if (absThermalLevelMod > 0.0) {
                val thermalLevelCooldownBase: Int = abs(MathHelper.floor(200 / thermalLevelMod))
                var normaliseCooldown: Int = MathHelper.floor(150 / lowestThermalStrength)
                var thermalLevelTickCooldown: Int = thermalLevelCooldownBase
                if (thermalLevelTickCooldown < 1) {
                    thermalLevelTickCooldown = 1 //Prevent divide by zero errors
                }

                if (!thermalPaddingHelm.isEmpty() && !thermalPaddingChestplate.isEmpty() && !thermalPaddingLeggings.isEmpty() && !thermalPaddingBoots.isEmpty()) {
                    //If the thermal strength exceeds the dimension's thermal level mod, it can't improve the normalise
                    //This factor of 1.5F is chosen so that a combination of Tier 1 and Tier 2 thermal isn't enough to normalise on Venus (three Tier 2, one Tier 1 stays roughly constant)
                    val relativeFactor: Float = max(1.0f, absThermalLevelMod / lowestThermalStrength) / 1.5f
                    normaliseCooldown = MathHelper.floor(normaliseCooldown / absThermalLevelMod * relativeFactor)
                    if (normaliseCooldown < 1) {
                        normaliseCooldown = 1 //Prevent divide by zero errors
                    }
                    // Player is wearing all required thermal padding items
                    if ((player.ticksExisted - 1) % normaliseCooldown == 0) {
                        this.normaliseThermalLevel(player, playerStats, 1)
                    }
                    thermalLevelMod /= max(1.0f, lowestThermalStrength / 2.0f)
                    absThermalLevelMod = abs(thermalLevelMod)
                }

                if (OxygenUtil.isAABBInBreathableAirBlock(player, true)) {
                    playerStats.setThermalLevelNormalising(true)
                    this.normaliseThermalLevel(player, playerStats, 1)
                    // If player is in ambient thermal area, slowly reset to normal
                    return
                }

                // For each piece of thermal equipment being used, slow down the the harmful thermal change slightly
                if (!thermalPaddingHelm.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }
                if (!thermalPaddingChestplate.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }
                if (!thermalPaddingLeggings.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }
                if (!thermalPaddingBoots.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }

                // Instead of increasing/decreasing the thermal level by a large amount every ~200 ticks, increase/decrease
                //      by a small amount each time (still the same average increase/decrease)
                var thermalLevelTickCooldownSingle: Int =
                    MathHelper.floor(thermalLevelTickCooldown / absThermalLevelMod)
                if (thermalLevelTickCooldownSingle < 1) {
                    thermalLevelTickCooldownSingle = 1 //Prevent divide by zero errors
                }

                if ((player.ticksExisted - 1) % thermalLevelTickCooldownSingle == 0) {
                    val last: Int = playerStats.getThermalLevel()
                    playerStats.setThermalLevel(min(max(last + (if (thermalLevelMod < 0) -1 else 1), -22), 22))

                    if (playerStats.getThermalLevel() != last) {
                        this.sendThermalLevelPacket(player, playerStats)
                    }
                }

                // If the normalisation is outpacing the freeze/overheat
                playerStats.setThermalLevelNormalising(thermalLevelTickCooldownSingle > normaliseCooldown && !thermalPaddingHelm.isEmpty() && !thermalPaddingChestplate.isEmpty() && !thermalPaddingLeggings.isEmpty() && !thermalPaddingBoots.isEmpty())

                if (!playerStats.isThermalLevelNormalising()) {
                    if ((player.ticksExisted - 1) % thermalLevelTickCooldown == 0) {
                        if (abs(playerStats.getThermalLevel()) >= 22) {
                            player.attackEntityFrom(DamageSourceGC.thermal, 1.5f)
                        }
                    }

                    if (playerStats.getThermalLevel() < -15) {
                        player.addPotionEffect(PotionEffect(MobEffects.SLOWNESS, 5, 2, true, true))
                    }

                    if (playerStats.getThermalLevel() > 15) {
                        player.addPotionEffect(PotionEffect(MobEffects.NAUSEA, 5, 2, true, true))
                    }
                }
            } else  //Normalise thermal level if on Space Station or non-modifier planet
            {
                playerStats.setThermalLevelNormalising(true)
                this.normaliseThermalLevel(player, playerStats, 2)
            }
        } else  //Normalise thermal level if on Overworld or any non-GC dimension
        {
            playerStats.setThermalLevelNormalising(true)
            this.normaliseThermalLevel(player, playerStats, 3)
        }
    }

    open fun normaliseThermalLevel(player: EntityPlayerMP, stats: GCPlayerStats, increment: Int) {
        val last: Int = stats.getThermalLevel()

        if (stats.getThermalLevel() < 0) {
            stats.setThermalLevel(stats.getThermalLevel() + min(increment, -stats.getThermalLevel()))
        } else if (stats.getThermalLevel() > 0) {
            stats.setThermalLevel(stats.getThermalLevel() - min(increment, stats.getThermalLevel()))
        }

        if (stats.getThermalLevel() != last) {
            this.sendThermalLevelPacket(player, stats)
        }
    }

    protected open fun checkShield(playerMP: EntityPlayerMP, playerStats: GCPlayerStats) {
        if (playerMP.ticksExisted % 20 == 0 && playerMP.world.provider is IGalacticraftWorldProvider) {
            if ((playerMP.world.provider as IGalacticraftWorldProvider).shouldCorrodeArmor()) {
                val shieldController: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(10)
                var valid: Boolean = false

                if (!shieldController.isEmpty()) {
                    val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                        shieldController,
                        EnumExtendedInventorySlot.SHIELD_CONTROLLER
                    )

                    if (gearID != -1) {
                        valid = true
                    }
                }

                if (!valid) {
                    for (armor: ItemStack in playerMP.getArmorInventoryList()) {
                        if (!armor.isEmpty() && armor.getItem() is ItemArmor && (armor.getItem() !is IArmorCorrosionResistant)) {
                            armor.damageItem(1, playerMP)
                        }
                    }
                }
            }
        }
    }

    protected open fun checkOxygen(player: EntityPlayerMP, stats: GCPlayerStats) {
        if ((player.dimension == 0 || player.world.provider is IGalacticraftWorldProvider) && (!(player.dimension == 0 || (player.world.provider as IGalacticraftWorldProvider).hasBreathableAtmosphere()) || player.posY > OXYGENHEIGHTLIMIT) && !player.capabilities.isCreativeMode && (player.getRidingEntity() !is EntityLanderBase) && (player.getRidingEntity() !is EntityAutoRocket) && (player.getRidingEntity() !is EntityCelestialFake) && !CompatibilityManager.isAndroid(
                player
            )
        ) {
            val tankInSlot: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(2)
            val tankInSlot2: ItemStack = (stats.getExtendedInventory())!!.getStackInSlot(3)

            val drainSpacing: Int = OxygenUtil.getDrainSpacing(tankInSlot, tankInSlot2)

            if (tankInSlot.isEmpty()) {
                stats.setAirRemaining(0)
            } else {
                stats.setAirRemaining(tankInSlot.getMaxDamage() - tankInSlot.getItemDamage())
            }

            if (tankInSlot2.isEmpty()) {
                stats.setAirRemaining2(0)
            } else {
                stats.setAirRemaining2(tankInSlot2.getMaxDamage() - tankInSlot2.getItemDamage())
            }

            if (drainSpacing > 0) {
                if ((player.ticksExisted - 1) % drainSpacing == 0 && !OxygenUtil.isAABBInBreathableAirBlock(player) && !stats.isUsingPlanetSelectionGui()) {
                    var toTake: Int = 1
                    //Take 1 oxygen from Tank 1
                    if (stats.getAirRemaining() > 0) {
                        tankInSlot.damageItem(1, player)
                        stats.setAirRemaining(stats.getAirRemaining() - 1)
                        toTake = 0
                    }

                    //Alternatively, take 1 oxygen from Tank 2
                    if (toTake > 0 && stats.getAirRemaining2() > 0) {
                        tankInSlot2.damageItem(1, player)
                        stats.setAirRemaining2(stats.getAirRemaining2() - 1)
                        toTake = 0
                    }
                }
            } else {
                if ((player.ticksExisted - 1) % 60 == 0) {
                    if (OxygenUtil.isAABBInBreathableAirBlock(player)) {
                        if (stats.getAirRemaining() < 90 && !tankInSlot.isEmpty()) {
                            stats.setAirRemaining(
                                min(
                                    stats.getAirRemaining() + 1,
                                    tankInSlot.getMaxDamage() - tankInSlot.getItemDamage()
                                )
                            )
                        }

                        if (stats.getAirRemaining2() < 90 && !tankInSlot2.isEmpty()) {
                            stats.setAirRemaining2(
                                min(
                                    stats.getAirRemaining2() + 1,
                                    tankInSlot2.getMaxDamage() - tankInSlot2.getItemDamage()
                                )
                            )
                        }
                    } else {
                        if (stats.getAirRemaining() > 0) {
                            stats.setAirRemaining(stats.getAirRemaining() - 1)
                        }

                        if (stats.getAirRemaining2() > 0) {
                            stats.setAirRemaining2(stats.getAirRemaining2() - 1)
                        }
                    }
                }
            }

            val airEmpty: Boolean = stats.getAirRemaining() <= 0 && stats.getAirRemaining2() <= 0

            if (player.isOnLadder()) {
                stats.setOxygenSetupValid(stats.isLastOxygenSetupValid())
            } else {
                stats.setOxygenSetupValid(
                    !((!OxygenUtil.hasValidOxygenSetup(player) || airEmpty) && !OxygenUtil.isAABBInBreathableAirBlock(
                        player
                    ))
                )
            }

            if (!player.world.isRemote && player.isEntityAlive()) {
                if (!stats.isOxygenSetupValid()) {
                    val suffocationEvent: GCCoreOxygenSuffocationEvent = GCCoreOxygenSuffocationEvent.Pre(player)
                    MinecraftForge.EVENT_BUS.post(suffocationEvent)

                    if (!suffocationEvent.isCanceled()) {
                        if (stats.getDamageCounter() == 0) {
                            stats.setDamageCounter(ConfigManagerCore.suffocationCooldown)

                            player.attackEntityFrom(
                                DamageSourceGC.oxygenSuffocation,
                                (ConfigManagerCore.suffocationDamage * (2 + stats.getIncrementalDamage()) / 2).toFloat()
                            )
                            if (ConfigManagerCore.hardMode) stats.setIncrementalDamage(stats.getIncrementalDamage() + 1)

                            val suffocationEventPost: GCCoreOxygenSuffocationEvent =
                                GCCoreOxygenSuffocationEvent.Post(player)
                            MinecraftForge.EVENT_BUS.post(suffocationEventPost)
                        }
                    } else stats.setOxygenSetupValid(true)
                } else stats.setIncrementalDamage(0)
            }
        } else if ((player.ticksExisted - 1) % 20 == 0 && !player.capabilities.isCreativeMode && stats.getAirRemaining() < 90) {
            stats.setAirRemaining(stats.getAirRemaining() + 1)
            stats.setAirRemaining2(stats.getAirRemaining2() + 1)
        } else if (player.capabilities.isCreativeMode) {
            stats.setAirRemaining(90)
            stats.setAirRemaining2(90)
        } else {
            stats.setOxygenSetupValid(true)
        }
    }

    protected open fun throwMeteors(player: EntityPlayerMP) {
        val world: World = player.world
        if (world.provider is IGalacticraftWorldProvider && !world.isRemote) {
            if ((world.provider as IGalacticraftWorldProvider).getMeteorFrequency() > 0 && ConfigManagerCore.meteorSpawnMod > 0.0) {
                val f: Int =
                    ((world.provider as IGalacticraftWorldProvider).getMeteorFrequency() * 750.0 * (1.0 / ConfigManagerCore.meteorSpawnMod)).toInt()

                if (world.rand.nextInt(f) == 0) {
                    val closestPlayer: EntityPlayer? = world.getClosestPlayerToEntity(player, 100.0)

                    if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId()) {
                        val r: Int = world.getMinecraftServer()!!.getPlayerList().getViewDistance()
                        var x: Int
                        val z: Int
                        val motX: Double
                        val motZ: Double
                        x = world.rand.nextInt(20) + 160
                        z = world.rand.nextInt(20) - 10
                        motX = world.rand.nextDouble() * 2 - 2.5
                        motZ = world.rand.nextDouble() * 5 - 2.5
                        val px: Int = MathHelper.floor(player.posX)
                        if ((x + px shr 4) - (px shr 4) >= r) {
                            x = ((px shr 4) + r shl 4) - 1 - px
                        }

                        val meteor: EntityMeteor =
                            EntityMeteor(world, player.posX + x, 355.0, player.posZ + z, motX, 0.0, motZ, 1)

                        if (!world.isRemote) {
                            world.spawnEntity(meteor)
                        }
                    }
                }

                if (world.rand.nextInt(f * 3) == 0) {
                    val closestPlayer: EntityPlayer? = world.getClosestPlayerToEntity(player, 100.0)

                    if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId()) {
                        val r: Int = world.getMinecraftServer()!!.getPlayerList().getViewDistance()
                        var x: Int
                        val z: Int
                        val motX: Double
                        val motZ: Double
                        x = world.rand.nextInt(20) + 160
                        z = world.rand.nextInt(20) - 10
                        motX = world.rand.nextDouble() * 2 - 2.5
                        motZ = world.rand.nextDouble() * 5 - 2.5
                        val px: Int = MathHelper.floor(player.posX)
                        if ((x + px shr 4) - (px shr 4) >= r) {
                            x = ((px shr 4) + r shl 4) - 1 - px
                        }

                        val meteor: EntityMeteor =
                            EntityMeteor(world, player.posX + x, 355.0, player.posZ + z, motX, 0.0, motZ, 6)

                        if (!world.isRemote) {
                            world.spawnEntity(meteor)
                        }
                    }
                }
            }
        }
    }

    protected open fun checkCurrentItem(player: EntityPlayerMP) {
        val theCurrentItem: ItemStack = player.inventory.getCurrentItem()
        val offHandItem: ItemStack = player.getHeldItemOffhand()

        if (!theCurrentItem.isEmpty()) {
            var postChangeItem: Int = 0
            for (i: ItemStack in itemChangesPre) {
                if (i.getItem() === theCurrentItem.getItem() && i.getItemDamage() == theCurrentItem.getItemDamage()) {
                    val postChange: ItemStack = itemChangesPost.get(postChangeItem)!!.copy()
                    postChange.setCount(theCurrentItem.getCount())
                    player.inventory.mainInventory.set(player.inventory.currentItem, postChange)
                    break
                }
                postChangeItem++
            }
            if (OxygenUtil.noAtmosphericCombustion(player.world.provider)) {
                //Is it a type of overworld torch?
                if (torchItemsRegular.contains(theCurrentItem.getItem())) {
                    //Get space torch for this overworld torch
                    var torchItem: Int = 0
                    for (i: Item? in torchItemsRegular) {
                        if (i === theCurrentItem.getItem()) {
                            player.inventory.mainInventory.set(
                                player.inventory.currentItem,
                                ItemStack(torchItemsSpace.get(torchItem), theCurrentItem.getCount(), 0)
                            )
                            break
                        }
                        torchItem++
                    }
                }
            } else {
                //Is it a type of space torch?
                if (torchItemsSpace.contains(theCurrentItem.getItem())) {
                    //Get the overworld torch equivalent
                    var torchItem: Int = 0
                    for (i: Item? in torchItemsSpace) {
                        if (i === theCurrentItem.getItem()) {
                            player.inventory.mainInventory.set(
                                player.inventory.currentItem,
                                ItemStack(torchItemsRegular.get(torchItem), theCurrentItem.getCount(), 0)
                            )
                            break
                        }
                        torchItem++
                    }
                }
            }

            // If the player is holding a two-handed item in both hands, switch the off-hand item out, or drop if necessary
            if (!offHandItem.isEmpty()) {
                if (theCurrentItem.getItem() is IHoldableItem && offHandItem.getItem() is IHoldableItem) {
                    val emptyStack: Int = player.inventory.getFirstEmptyStack()

                    if (emptyStack >= 0) {
                        val copyOffHandItem: ItemStack = offHandItem.copy()
                        copyOffHandItem.setAnimationsToGo(5)
                        player.inventory.mainInventory.set(emptyStack, copyOffHandItem)
                    } else {
                        player.dropItem(offHandItem, false)
                    }

                    player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY)
                }
            }
        }
    }

    open fun registerTorchTypes() {
        for (type: MutableMap.MutableEntry<Block?, Block?> in BlockUnlitTorch.registeredTorches.entries) {
            //Space Torch registration must be unique; there may be multiple mappings for vanillaTorch
            torchItemsSpace.add(Item.getItemFromBlock(type.key))
            torchItemsRegular.add(Item.getItemFromBlock(type.value))
        }
    }

    open fun registerItemChanges() {
        for (type: MutableMap.MutableEntry<Block?, Block?> in GCBlocks.itemChanges.entries) {
            itemChangesPre.add(ItemStack(type.key))
            itemChangesPost.add(ItemStack(type.value))
        }
        for (type in GCItems.itemChanges.entries) {
            itemChangesPre.add(type.key!!)
            itemChangesPost.add(type.value)
        }
    }

    protected open fun updateSchematics(player: EntityPlayerMP, stats: GCPlayerStats) {
        SchematicRegistry.addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(0))
        SchematicRegistry.addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(Int.MAX_VALUE))

        Collections.sort<ISchematicPage>((stats.getUnlockedSchematics())!!)

        if (player.connection != null && ((stats.getUnlockedSchematics())!!.size != (stats.getLastUnlockedSchematics())!!.size || (player.ticksExisted - 1) % 100 == 0)) {
            val iArray: Array<Int> = (arrayOfNulls<Int>((stats.getUnlockedSchematics())!!.size) as Array<Int>)

            for (i: Int in iArray.indices) {
                val page: ISchematicPage? = (stats.getUnlockedSchematics())!!.get(i)
                iArray[i] = if (page == null) -2 else page.getPageID()
            }

            val objList: MutableList<Any?> = ArrayList<Any?>()
            objList.add(iArray)

            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_SCHEMATIC_LIST,
                    GCCoreUtil.getDimensionID(player.world),
                    objList
                ), player
            )
        }
    }

    open class ThermalArmorEvent constructor(@JvmField val armorIndex: Int, @JvmField val armorStack: ItemStack?) :
        Event() {
        @JvmField
        var armorResult: ArmorAddResult? = ArmorAddResult.NOTHING

        open fun setArmorAddResult(result: ArmorAddResult?) {
            this.armorResult = result
        }

        enum class ArmorAddResult {
            ADD,
            REMOVE,
            NOTHING
        }
    }


    protected open fun sendPlanetList(player: EntityPlayerMP, stats: GCPlayerStats) {
        val map: HashMap<String?,Int>
        if (player.ticksExisted % 50 == 0)  //Check for genuine update - e.g. maybe some other player created a space station or changed permissions
        //CAUTION: possible server load due to dimension loading, if any planets or moons were (contrary to GC default) set to hotload
        {
            map = WorldUtil.getArrayOfPossibleDimensions(stats.getSpaceshipTier(), player)
        } else {
            map = WorldUtil.getArrayOfPossibleDimensionsAgain(stats.getSpaceshipTier(), player)
        }

        var temp: String = ""
        var count: Int = 0

        for (entry: MutableMap.MutableEntry<String?,Int> in map.entries) {
            temp = temp + entry.key + (if (count < map.entries.size - 1) "?" else "")
            count++
        }

        if (temp != stats.getSavedPlanetList() || (player.ticksExisted % 100 == 0)) {
            val canCreateStations: Boolean = PermissionAPI.hasPermission(player, Constants.PERMISSION_CREATE_STATION)
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_DIMENSION_LIST,
                    GCCoreUtil.getDimensionID(player.world),
                    arrayOf<Any?>(PlayerUtil.getName(player), temp, canCreateStations)
                ), player
            )
            stats.setSavedPlanetList(temp)
            //GCLog.debug("Sending to " + PlayerUtil.getName(player) + ": " + temp);
        }
    }

    protected open fun sendThermalLevelPacket(player: EntityPlayerMP, stats: GCPlayerStats) {
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_THERMAL_LEVEL,
                GCCoreUtil.getDimensionID(player.world),
                arrayOf<Any>(stats.getThermalLevel(), stats.isThermalLevelNormalising())
            ), player
        )
    }

    protected open fun sendDungeonDirectionPacket(player: EntityPlayerMP, stats: GCPlayerStats?) {
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_DUNGEON_DIRECTION,
                GCCoreUtil.getDimensionID(player.world),
                arrayOf<Any>(
                    MapGenDungeon.directionToNearestDungeon(
                        player.world,
                        player.posX + player.motionX,
                        player.posZ + player.motionZ
                    )
                )
            ), player
        )
    }

    enum class EnumModelPacketType {
        ADD,
        REMOVE
    }

    open fun onPlayerUpdate(player: EntityPlayerMP) {
        val tick: Int = player.ticksExisted - 1

        //This will speed things up a little
        val stats: GCPlayerStats = (GCPlayerStats.Companion.get(player))!!

        if ((ConfigManagerCore.challengeSpawnHandling) && (stats.getUnlockedSchematics())!!.size == 0) {
            if ((stats.getStartDimension())!!.length > 0) {
                stats.setStartDimension("")
            } else {
                //PlayerAPI is installed
                val worldOld: WorldServer = player.world as WorldServer
                try {
                    worldOld.getPlayerChunkMap().removePlayer(player)
                } catch (e: Exception) {
                }
                worldOld.playerEntities.remove(player)
                worldOld.updateAllPlayersSleepingFlag()
                worldOld.loadedEntityList.remove(player)
                worldOld.onEntityRemoved(player)
                worldOld.getEntityTracker().untrack(player)
                if (player.addedToChunk && worldOld.getChunkProvider()
                        .chunkExists(player.chunkCoordX, player.chunkCoordZ)
                ) {
                    val chunkOld: Chunk = worldOld.getChunk(player.chunkCoordX, player.chunkCoordZ)
                    chunkOld.removeEntity(player)
                    chunkOld.setModified(true)
                }

                val worldNew: WorldServer = (WorldUtil.getStartWorld(worldOld))!!
                val dimID: Int = GCCoreUtil.getDimensionID(worldNew)
                player.dimension = dimID
                GCLog.debug("DEBUG: Sending respawn packet to player for dim " + dimID)
                player.connection.sendPacket(
                    SPacketRespawn(
                        dimID,
                        player.world.getDifficulty(),
                        player.world.getWorldInfo().getTerrainType(),
                        player.interactionManager.getGameType()
                    )
                )

                if (worldNew.provider is WorldProviderSpaceStation) {
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON,
                            GCCoreUtil.getDimensionID(player.world),
                            arrayOf<Any?>()
                        ), player
                    )
                }
                worldNew.spawnEntity(player)
                player.setWorld(worldNew)
                player.server.getPlayerList().preparePlayer(player, worldOld)
            }

            //This is a mini version of the code at WorldUtil.teleportEntity
            player.interactionManager.setWorld(player.world as WorldServer?)
            val type: ITeleportType = (GalacticraftRegistry.getTeleportTypeForDimension(player.world.provider.javaClass))!!
            val spawnPos: Vector3 = (type.getPlayerSpawnLocation(player.world as WorldServer?, player))!!
            val pair: ChunkPos =
                player.world.getChunk(spawnPos.intX() shr 4, spawnPos.intZ() shr 4).getPos()
            GCLog.debug("Loading first chunk in new dimension.")
            (player.world as WorldServer).getChunkProvider().loadChunk(pair.x, pair.z)
            player.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, player.rotationYaw, player.rotationPitch)
            type.setupAdventureSpawn(player)
            type.onSpaceDimensionChanged(player.world, player, false)
            player.setSpawnChunk(
                BlockPos(spawnPos.intX(), spawnPos.intY(), spawnPos.intZ()),
                true,
                GCCoreUtil.getDimensionID(player.world)
            )
            stats.setNewAdventureSpawn(true)
        }
        val isInGCDimension: Boolean = player.world.provider is IGalacticraftWorldProvider

        if (tick >= 25) {
            if (ConfigManagerCore.enableSpaceRaceManagerPopup && !stats.hasOpenedSpaceRaceManager()) {
                val race: SpaceRace? = SpaceRaceManager.getSpaceRaceFromPlayer(PlayerUtil.getName(player))

                if (race == null || race.teamName == SpaceRace.DEFAULT_NAME) {
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_OPEN_SPACE_RACE_GUI,
                            GCCoreUtil.getDimensionID(player.world),
                            arrayOf<Any?>()
                        ), player
                    )
                }

                stats.setOpenedSpaceRaceManager(true)
            }
            if (!stats.hasSentFlags()) {
                (GalacticraftCore.packetPipeline)!!.sendTo(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_UPDATE_STATS,
                        GCCoreUtil.getDimensionID(player.world),
                        (stats.getMiscNetworkedStats())!!
                    ), player
                )
                stats.setSentFlags(true)
            }
        }

        if (stats.getCryogenicChamberCooldown() > 0) {
            stats.setCryogenicChamberCooldown(stats.getCryogenicChamberCooldown() - 1)
        }

        if (!player.onGround && stats.isLastOnGround()) {
            stats.setTouchedGround(true)
        }

        if (stats.getTeleportCooldown() > 0) {
            stats.setTeleportCooldown(stats.getTeleportCooldown() - 1)
        }

        if (stats.getChatCooldown() > 0) {
            stats.setChatCooldown(stats.getChatCooldown() - 1)
        }

        if (stats.getOpenPlanetSelectionGuiCooldown() > 0) {
            stats.setOpenPlanetSelectionGuiCooldown(stats.getOpenPlanetSelectionGuiCooldown() - 1)

            if (stats.getOpenPlanetSelectionGuiCooldown() == 1 && !stats.hasOpenedPlanetSelectionGui()) {
                WorldUtil.toCelestialSelection(player, stats, stats.getSpaceshipTier())
                stats.setHasOpenedPlanetSelectionGui(true)
            }
        }

        if (stats.isUsingParachute()) {
            if (!(stats.getLastParachuteInSlot())!!.isEmpty()) {
                player.fallDistance = 0.0f
            }
            if (player.onGround) {
                setUsingParachute(player, stats, false)
            }
        }

        this.checkCurrentItem(player)

        if (stats.isUsingPlanetSelectionGui()) {
            //This sends the planets list again periodically (forcing the Celestial Selection screen to open) in case of server/client lag
            //#PACKETSPAM
            this.sendPlanetList(player, stats)
        }

        /*		if (isInGCDimension || player.usingPlanetSelectionGui)
                {
					player.connection.ticksForFloatKick = 0;
				}	
		*/
        if (stats.getDamageCounter() > 0) {
            stats.setDamageCounter(stats.getDamageCounter() - 1)
        }

        if (isInGCDimension) {
            if (tick % 10 == 0) {
                var doneDungeon: Boolean = false
                val current: ItemStack = player.inventory.getCurrentItem()
                if (current != null && current.getItem() === GCItems.dungeonFinder) {
                    this.sendDungeonDirectionPacket(player, stats)
                    doneDungeon = true
                }
                if (tick % 30 == 0) {
                    sendAirRemainingPacket(player, stats)
                    this.sendThermalLevelPacket(player, stats)

                    if (!doneDungeon) {
                        for (stack: ItemStack? in player.inventory.mainInventory) {
                            if (stack != null && stack.getItem() === GCItems.dungeonFinder) {
                                this.sendDungeonDirectionPacket(player, stats)
                                break
                            }
                        }
                    }
                }
            }

            if (player.getRidingEntity() is EntityLanderBase) {
                stats.setInLander(true)
                stats.setJustLanded(false)
            } else {
                if (stats.isInLander()) {
                    stats.setJustLanded(true)
                }
                stats.setInLander(false)
            }

            if (player.onGround && stats.hasJustLanded()) {
                stats.setJustLanded(false)

                //Set spawn point here if just descended from a lander for the first time
                if (player.getBedLocation(GCCoreUtil.getDimensionID(player.world)) == null || stats.isNewAdventureSpawn()) {
                    val i: Int = 30000000
                    val j: Int = min(i, max(-i, MathHelper.floor(player.posX + 0.5)))
                    val k: Int = min(256, max(0, MathHelper.floor(player.posY + 1.5)))
                    val l: Int = min(i, max(-i, MathHelper.floor(player.posZ + 0.5)))
                    val coords: BlockPos = BlockPos(j, k, l)
                    player.setSpawnChunk(coords, true, GCCoreUtil.getDimensionID(player.world))
                    stats.setNewAdventureSpawn(false)
                }

                (GalacticraftCore.packetPipeline)!!.sendTo(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON,
                        GCCoreUtil.getDimensionID(player.world),
                        arrayOf<Any?>()
                    ), player
                )
            }

            if (player.world.provider is WorldProviderSpaceStation || player.world.provider is IZeroGDimension || GalacticraftCore.isPlanetsLoaded && player.world.provider is WorldProviderAsteroids) {
                this.preventFlyingKicks(player)
                if (player.world.provider is WorldProviderSpaceStation && stats.isNewInOrbit()) {
                    (player.world.provider as WorldProviderSpaceStation).getSpinManager().sendPackets(player)
                    stats.setNewInOrbit(false)
                }
            } else {
                stats.setNewInOrbit(true)
            }
        } else {
            stats.setNewInOrbit(true)
        }

        checkGear(player, stats, false)

        if (stats.getChestSpawnCooldown() > 0) {
            stats.setChestSpawnCooldown(stats.getChestSpawnCooldown() - 1)

            if (stats.getChestSpawnCooldown() == 180) {
                if (stats.getChestSpawnVector() != null) {
                    val chest: EntityParachest =
                        EntityParachest(player.world, (stats.getRocketStacks())!!, stats.getFuelLevel())

                    chest.setPosition(
                        ((stats.getChestSpawnVector())!!.x)!!,
                        ((stats.getChestSpawnVector())!!.y)!!,
                        ((stats.getChestSpawnVector())!!.z)!!
                    )
                    chest.color = if ((stats.getParachuteInSlot())!!
                            .isEmpty()
                    ) EnumDyeColor.WHITE else ItemParaChute.getDyeEnumFromParachuteDamage(
                        (stats.getParachuteInSlot())!!.getItemDamage()
                    )

                    if (!player.world.isRemote) {
                        player.world.spawnEntity(chest)
                    }
                }
            }
        }

        //
        if (stats.getLaunchAttempts() > 0 && player.getRidingEntity() == null) {
            stats.setLaunchAttempts(0)
        }

        this.checkThermalStatus(player, stats)
        this.checkOxygen(player, stats)
        this.checkShield(player, stats)

        if (isInGCDimension && (stats.isOxygenSetupValid() != stats.isLastOxygenSetupValid() || tick % 100 == 0)) {
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_OXYGEN_VALIDITY,
                    player.world.provider.getDimension(),
                    arrayOf<Any>(stats.isOxygenSetupValid())
                ), player
            )
        }

        this.throwMeteors(player)

        this.updateSchematics(player, stats)

        if (tick % 250 == 0 && (stats.getFrequencyModuleInSlot())!!
                .isEmpty() && !stats.hasReceivedSoundWarning() && isInGCDimension && player.onGround && tick > 0 && (player.world.provider as IGalacticraftWorldProvider).getSoundVolReductionAmount() > 1.0f
        ) {
            val string2: Array<String?> =
                GCCoreUtil.translate("gui.frequencymodule.warning1").split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val sb: StringBuilder = StringBuilder()
            for (aString2: String? in string2) {
                sb.append(" ").append(EnumColor.YELLOW).append(aString2)
            }
            player.sendMessage(
                TextComponentString(
                    EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.frequencymodule.warning0") + " " + EnumColor.AQUA + (GCItems.basicItem)!!.getItemStackDisplayName(
                        ItemStack(GCItems.basicItem, 1, 19)
                    ) + sb.toString()
                )
            )
            stats.setReceivedSoundWarning(true)
        }


        // Player moves and sprints 18% faster with full set of Titanium Armor
        if (GalacticraftCore.isPlanetsLoaded && tick % 40 == 1 && player.inventory != null) {
            var titaniumCount: Int = 0
            for (armorPiece: ItemStack? in player.getArmorInventoryList()) {
                if (armorPiece != null && armorPiece.getItem() is ItemArmorAsteroids) {
                    titaniumCount++
                }
            }
            if (stats.getSavedSpeed() == 0f) {
                if (titaniumCount == 4) {
                    val speed: Float = player.capabilities.getWalkSpeed()
                    if (speed < 0.118f) {
                        try {
                            val f: Field =
                                player.capabilities.javaClass.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "walkSpeed" else "field_75097_g")
                            f.setAccessible(true)
                            f.set(player.capabilities, 0.118f)
                            stats.setSavedSpeed(speed)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } else if (titaniumCount < 4) {
                try {
                    val f: Field =
                        player.capabilities.javaClass.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "walkSpeed" else "field_75097_g")
                    f.setAccessible(true)
                    f.set(player.capabilities, stats.getSavedSpeed())
                    stats.setSavedSpeed(0f)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        stats.setLastOxygenSetupValid(stats.isOxygenSetupValid())
        stats.setLastUnlockedSchematics(stats.getUnlockedSchematics())
        stats.setLastOnGround(player.onGround)
    }

    open fun preventFlyingKicks(player: EntityPlayerMP) {
        player.fallDistance = 0.0f
        player.connection.floatingTickCount = 0
    }

    companion object {
        private const val OXYGENHEIGHTLIMIT = 450

        @JvmField
        val GEAR_NOT_PRESENT: Int = -1

        private val deathTimes = HashMap<UUID?,Int>()

        //    private void onPlayerLogout(EntityPlayerMP player)
        //    {
        //    }
        //
        //    private void onPlayerRespawn(EntityPlayerMP player)
        //    {
        //        GCPlayerStats oldData = this.playerStatsMap.remove(player.getPersistentID());
        //        GCPlayerStats stats = GCPlayerStats.get(player);
        //
        //        if (oldData != null)
        //        {
        //            stats.copyFrom(oldData, false);
        //        }
        //
        //        stats.player = new WeakReference<EntityPlayerMP>(player);
        //    }
        @JvmStatic
        fun checkGear(player: EntityPlayerMP, stats: GCPlayerStats, forceSend: Boolean) {
            stats.setMaskInSlot((stats.getExtendedInventory())!!.getStackInSlot(0))
            stats.setGearInSlot((stats.getExtendedInventory())!!.getStackInSlot(1))
            stats.setTankInSlot1((stats.getExtendedInventory())!!.getStackInSlot(2))
            stats.setTankInSlot2((stats.getExtendedInventory())!!.getStackInSlot(3))
            stats.setParachuteInSlot((stats.getExtendedInventory())!!.getStackInSlot(4))
            stats.setFrequencyModuleInSlot((stats.getExtendedInventory())!!.getStackInSlot(5))
            stats.setThermalHelmetInSlot((stats.getExtendedInventory())!!.getStackInSlot(6))
            stats.setThermalChestplateInSlot((stats.getExtendedInventory())!!.getStackInSlot(7))
            stats.setThermalLeggingsInSlot((stats.getExtendedInventory())!!.getStackInSlot(8))
            stats.setThermalBootsInSlot((stats.getExtendedInventory())!!.getStackInSlot(9))
            stats.setShieldControllerInSlot((stats.getExtendedInventory())!!.getStackInSlot(10))

            //
            if (stats.getFrequencyModuleInSlot() != stats.getLastFrequencyModuleInSlot() || forceSend) {
                if (player.server != null) {
                    if ((stats.getFrequencyModuleInSlot())!!.isEmpty()) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.REMOVE,
                            EnumExtendedInventorySlot.FREQUENCY_MODULE
                        )
                        TileEntityTelemetry.frequencyModulePlayer(stats.getLastFrequencyModuleInSlot(), player, true)
                    } else if ((stats.getLastFrequencyModuleInSlot())!!.isEmpty()) {
                        val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                            (stats.getFrequencyModuleInSlot())!!,
                            EnumExtendedInventorySlot.FREQUENCY_MODULE
                        )

                        if (gearID >= 0) {
                            sendGearUpdatePacket(
                                player,
                                EnumModelPacketType.ADD,
                                EnumExtendedInventorySlot.FREQUENCY_MODULE,
                                gearID
                            )
                            TileEntityTelemetry.frequencyModulePlayer(stats.getFrequencyModuleInSlot(), player, false)
                        }
                    }
                }

                stats.setLastFrequencyModuleInSlot(stats.getFrequencyModuleInSlot())
            }

            //
            if (stats.getMaskInSlot() != stats.getLastMaskInSlot() || forceSend) {
                if ((stats.getMaskInSlot())!!.isEmpty()) {
                    sendGearUpdatePacket(player, EnumModelPacketType.REMOVE, EnumExtendedInventorySlot.MASK)
                } else if ((stats.getLastMaskInSlot())!!.isEmpty() || forceSend) {
                    val gearID: Int =
                        GalacticraftRegistry.findMatchingGearID((stats.getMaskInSlot())!!, EnumExtendedInventorySlot.MASK)

                    if (gearID >= 0) {
                        sendGearUpdatePacket(player, EnumModelPacketType.ADD, EnumExtendedInventorySlot.MASK, gearID)
                    }
                }

                stats.setLastMaskInSlot(stats.getMaskInSlot())
            }

            //
            if (stats.getGearInSlot() != stats.getLastGearInSlot() || forceSend) {
                if ((stats.getGearInSlot())!!.isEmpty()) {
                    sendGearUpdatePacket(player, EnumModelPacketType.REMOVE, EnumExtendedInventorySlot.GEAR)
                } else if ((stats.getGearInSlot())!!.getItem() === GCItems.oxygenGear && ((stats.getLastGearInSlot())!!
                        .isEmpty() || forceSend)
                ) {
                    val gearID: Int =
                        GalacticraftRegistry.findMatchingGearID((stats.getGearInSlot())!!, EnumExtendedInventorySlot.GEAR)

                    if (gearID >= 0) {
                        sendGearUpdatePacket(player, EnumModelPacketType.ADD, EnumExtendedInventorySlot.GEAR, gearID)
                    }
                }

                stats.setLastGearInSlot(stats.getGearInSlot())
            }

            //
            if (stats.getTankInSlot1() != stats.getLastTankInSlot1() || forceSend) {
                if ((stats.getTankInSlot1())!!.isEmpty()) {
                    sendGearUpdatePacket(player, EnumModelPacketType.REMOVE, EnumExtendedInventorySlot.LEFT_TANK)
                    stats.setAirRemaining(0)
                    sendAirRemainingPacket(player, stats)
                } else if ((stats.getLastTankInSlot1())!!.isEmpty() || forceSend) {
                    val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                        (stats.getTankInSlot1())!!,
                        EnumExtendedInventorySlot.LEFT_TANK
                    )

                    if (gearID >= 0) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.ADD,
                            EnumExtendedInventorySlot.LEFT_TANK,
                            gearID
                        )
                    }
                    if (!(stats.getMaskInSlot())!!.isEmpty() && !(stats.getGearInSlot())!!.isEmpty()) {
                        stats.setAirRemaining(
                            (stats.getTankInSlot1())!!.getMaxDamage() - (stats.getTankInSlot1())!!.getItemDamage()
                        )
                        sendAirRemainingPacket(player, stats)
                    }
                } else if ((stats.getTankInSlot1())!!.getItem() !== (stats.getLastTankInSlot1())!!.getItem()) {
                    val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                        (stats.getTankInSlot1())!!,
                        EnumExtendedInventorySlot.LEFT_TANK
                    )

                    if (gearID >= 0) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.ADD,
                            EnumExtendedInventorySlot.LEFT_TANK,
                            gearID
                        )
                    }
                    if (!(stats.getMaskInSlot())!!.isEmpty() && !(stats.getGearInSlot())!!.isEmpty()) {
                        stats.setAirRemaining(
                            (stats.getTankInSlot1())!!.getMaxDamage() - (stats.getTankInSlot1())!!.getItemDamage()
                        )
                        sendAirRemainingPacket(player, stats)
                    }
                }

                stats.setLastTankInSlot1(stats.getTankInSlot1())
            }

            //
            if (stats.getTankInSlot2() != stats.getLastTankInSlot2() || forceSend) {
                if ((stats.getTankInSlot2())!!.isEmpty()) {
                    sendGearUpdatePacket(player, EnumModelPacketType.REMOVE, EnumExtendedInventorySlot.RIGHT_TANK)
                    stats.setAirRemaining2(0)
                    sendAirRemainingPacket(player, stats)
                } else if ((stats.getLastTankInSlot2())!!.isEmpty() || forceSend) {
                    val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                        (stats.getTankInSlot2())!!,
                        EnumExtendedInventorySlot.RIGHT_TANK
                    )

                    if (gearID >= 0) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.ADD,
                            EnumExtendedInventorySlot.RIGHT_TANK,
                            gearID
                        )
                    }
                    if (!(stats.getMaskInSlot())!!.isEmpty() && !(stats.getGearInSlot())!!.isEmpty()) {
                        stats.setAirRemaining2(
                            (stats.getTankInSlot2())!!.getMaxDamage() - (stats.getTankInSlot2())!!.getItemDamage()
                        )
                        sendAirRemainingPacket(player, stats)
                    }
                } else if ((stats.getTankInSlot2())!!.getItem() !== (stats.getLastTankInSlot2())!!.getItem()) {
                    val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                        (stats.getTankInSlot2())!!,
                        EnumExtendedInventorySlot.RIGHT_TANK
                    )

                    if (gearID >= 0) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.ADD,
                            EnumExtendedInventorySlot.RIGHT_TANK,
                            gearID
                        )
                    }
                    if (!(stats.getMaskInSlot())!!.isEmpty() && !(stats.getGearInSlot())!!.isEmpty()) {
                        stats.setAirRemaining2(
                            (stats.getTankInSlot2())!!.getMaxDamage() - (stats.getTankInSlot2())!!.getItemDamage()
                        )
                        sendAirRemainingPacket(player, stats)
                    }
                }

                stats.setLastTankInSlot2(stats.getTankInSlot2())
            }

            //
            if (stats.getParachuteInSlot() != stats.getLastParachuteInSlot() || forceSend) {
                if (stats.isUsingParachute()) {
                    if ((stats.getParachuteInSlot())!!.isEmpty()) {
                        sendGearUpdatePacket(player, EnumModelPacketType.REMOVE, EnumExtendedInventorySlot.PARACHUTE)
                    } else if (forceSend || (stats.getLastParachuteInSlot())!!.isEmpty() || (stats.getParachuteInSlot())!!
                            .getItemDamage() != (stats.getLastParachuteInSlot())!!.getItemDamage()
                    ) {
                        val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                            (stats.getParachuteInSlot())!!,
                            EnumExtendedInventorySlot.PARACHUTE
                        )

                        if (gearID >= 0) {
                            sendGearUpdatePacket(
                                player,
                                EnumModelPacketType.ADD,
                                EnumExtendedInventorySlot.PARACHUTE,
                                (stats.getParachuteInSlot())!!.getItemDamage()
                            )
                        }
                    }
                }

                stats.setLastParachuteInSlot(stats.getParachuteInSlot())
            }

            //
            if (stats.getThermalHelmetInSlot() != stats.getLastThermalHelmetInSlot() || forceSend) {
                val armorEvent: ThermalArmorEvent = ThermalArmorEvent(0, stats.getThermalHelmetInSlot())
                MinecraftForge.EVENT_BUS.post(armorEvent)

                if (armorEvent.armorResult != ThermalArmorEvent.ArmorAddResult.NOTHING) {
                    if ((stats.getThermalHelmetInSlot())!!
                            .isEmpty() || armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.REMOVE
                    ) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.REMOVE,
                            EnumExtendedInventorySlot.THERMAL_HELMET
                        )
                    } else if (armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.ADD && ((stats.getLastThermalHelmetInSlot())!!
                            .isEmpty() || forceSend)
                    ) {
                        val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                            (stats.getThermalHelmetInSlot())!!,
                            EnumExtendedInventorySlot.THERMAL_HELMET
                        )

                        if (gearID >= 0) {
                            sendGearUpdatePacket(
                                player,
                                EnumModelPacketType.ADD,
                                EnumExtendedInventorySlot.THERMAL_HELMET,
                                gearID
                            )
                        }
                    }
                }

                stats.setLastThermalHelmetInSlot(stats.getThermalHelmetInSlot())
            }

            if (stats.getThermalChestplateInSlot() != stats.getLastThermalChestplateInSlot() || forceSend) {
                val armorEvent: ThermalArmorEvent = ThermalArmorEvent(1, stats.getThermalChestplateInSlot())
                MinecraftForge.EVENT_BUS.post(armorEvent)

                if (armorEvent.armorResult != ThermalArmorEvent.ArmorAddResult.NOTHING) {
                    if ((stats.getThermalChestplateInSlot())!!
                            .isEmpty() || armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.REMOVE
                    ) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.REMOVE,
                            EnumExtendedInventorySlot.THERMAL_CHESTPLATE
                        )
                    } else if (armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.ADD && ((stats.getLastThermalChestplateInSlot())!!
                            .isEmpty() || forceSend)
                    ) {
                        val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                            (stats.getThermalChestplateInSlot())!!,
                            EnumExtendedInventorySlot.THERMAL_CHESTPLATE
                        )

                        if (gearID >= 0) {
                            sendGearUpdatePacket(
                                player,
                                EnumModelPacketType.ADD,
                                EnumExtendedInventorySlot.THERMAL_CHESTPLATE,
                                gearID
                            )
                        }
                    }
                }

                stats.setLastThermalChestplateInSlot(stats.getThermalChestplateInSlot())
            }

            if (stats.getThermalLeggingsInSlot() != stats.getLastThermalLeggingsInSlot() || forceSend) {
                val armorEvent: ThermalArmorEvent = ThermalArmorEvent(2, stats.getThermalLeggingsInSlot())
                MinecraftForge.EVENT_BUS.post(armorEvent)

                if (armorEvent.armorResult != ThermalArmorEvent.ArmorAddResult.NOTHING) {
                    if ((stats.getThermalLeggingsInSlot())!!
                            .isEmpty() || armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.REMOVE
                    ) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.REMOVE,
                            EnumExtendedInventorySlot.THERMAL_LEGGINGS
                        )
                    } else if (armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.ADD && ((stats.getLastThermalLeggingsInSlot())!!
                            .isEmpty() || forceSend)
                    ) {
                        val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                            (stats.getThermalLeggingsInSlot())!!,
                            EnumExtendedInventorySlot.THERMAL_LEGGINGS
                        )

                        if (gearID >= 0) {
                            sendGearUpdatePacket(
                                player,
                                EnumModelPacketType.ADD,
                                EnumExtendedInventorySlot.THERMAL_LEGGINGS,
                                gearID
                            )
                        }
                    }
                }

                stats.setLastThermalLeggingsInSlot(stats.getThermalLeggingsInSlot())
            }

            if (stats.getThermalBootsInSlot() != stats.getLastThermalBootsInSlot() || forceSend) {
                val armorEvent: ThermalArmorEvent = ThermalArmorEvent(3, stats.getThermalBootsInSlot())
                MinecraftForge.EVENT_BUS.post(armorEvent)

                if (armorEvent.armorResult != ThermalArmorEvent.ArmorAddResult.NOTHING) {
                    if ((stats.getThermalBootsInSlot())!!
                            .isEmpty() || armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.REMOVE
                    ) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.REMOVE,
                            EnumExtendedInventorySlot.THERMAL_BOOTS
                        )
                    } else if (armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.ADD && ((stats.getLastThermalBootsInSlot())!!
                            .isEmpty() || forceSend)
                    ) {
                        val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                            (stats.getThermalBootsInSlot())!!,
                            EnumExtendedInventorySlot.THERMAL_BOOTS
                        )

                        if (gearID >= 0) {
                            sendGearUpdatePacket(
                                player,
                                EnumModelPacketType.ADD,
                                EnumExtendedInventorySlot.THERMAL_BOOTS,
                                gearID
                            )
                        }
                    }
                }

                stats.setLastThermalBootsInSlot(stats.getThermalBootsInSlot())
            }

            if ((stats.getShieldControllerInSlot() != stats.getLastShieldControllerInSlot() || forceSend) && GalacticraftCore.isPlanetsLoaded) {
                if ((stats.getShieldControllerInSlot())!!.isEmpty()) {
                    sendGearUpdatePacket(
                        player,
                        EnumModelPacketType.REMOVE,
                        EnumExtendedInventorySlot.SHIELD_CONTROLLER
                    )
                } else if ((stats.getShieldControllerInSlot())!!
                        .getItem() === VenusItems.basicItem && ((stats.getLastShieldControllerInSlot())!!
                        .isEmpty() || forceSend)
                ) {
                    val gearID: Int = GalacticraftRegistry.findMatchingGearID(
                        (stats.getShieldControllerInSlot())!!,
                        EnumExtendedInventorySlot.SHIELD_CONTROLLER
                    )

                    if (gearID >= 0) {
                        sendGearUpdatePacket(
                            player,
                            EnumModelPacketType.ADD,
                            EnumExtendedInventorySlot.SHIELD_CONTROLLER,
                            gearID
                        )
                    }
                }

                stats.setLastShieldControllerInSlot(stats.getShieldControllerInSlot())
            }
        }

        @JvmStatic
        fun setUsingParachute(player: EntityPlayerMP, playerStats: GCPlayerStats, tf: Boolean) {
            playerStats.setUsingParachute(tf)

            if (tf) {
                var subtype: Int = GEAR_NOT_PRESENT

                if (!(playerStats.getParachuteInSlot())!!.isEmpty()) {
                    subtype = (playerStats.getParachuteInSlot())!!.getItemDamage()
                }

                sendGearUpdatePacket(player, EnumModelPacketType.ADD, EnumExtendedInventorySlot.PARACHUTE, subtype)
            } else {
                sendGearUpdatePacket(player, EnumModelPacketType.REMOVE, EnumExtendedInventorySlot.PARACHUTE)
            }
        }

        @JvmStatic
        fun updateFeet(player: EntityPlayerMP, motionX: Double, motionZ: Double) {
            val motionSqrd: Double = motionX * motionX + motionZ * motionZ
            if (motionSqrd > 0.001 && !player.capabilities.isFlying) {
                val iPosX: Int = MathHelper.floor(player.posX)
                val iPosY: Int = MathHelper.floor(player.posY - 0.05)
                val iPosZ: Int = MathHelper.floor(player.posZ)

                // If the block below is the moon block
                val state: IBlockState = player.world.getBlockState(BlockPos(iPosX, iPosY, iPosZ))
                if (state.getBlock() === GCBlocks.blockMoon) {
                    // And is the correct metadata (moon turf)
                    if (state.getValue(BlockBasicMoon.BASIC_TYPE_MOON) == BlockBasicMoon.EnumBlockBasicMoon.MOON_TURF) {
                        val stats: GCPlayerStats = (GCPlayerStats.Companion.get(player))!!
                        // If it has been long enough since the last step
                        if (stats.getDistanceSinceLastStep() > 0.35) {
                            var pos: Vector3 = Vector3(player)
                            // Set the footprint position to the block below and add random number to stop z-fighting
                            pos.y =
                                (MathHelper.floor(player.posY - 1.0) + player.world.rand.nextFloat() / 100.0f).toDouble()

                            // Adjust footprint to left or right depending on step count
                            var a: Float = 0f
when (stats.getLastStep()) {
                                0 -> {
                                    a = (-player.rotationYaw + 90f) / Constants.RADIANS_TO_DEGREES
                                    pos.translate(
                                        Vector3(
                                            (MathHelper.sin(a) * 0.25f).toDouble(),
                                            0.0,
                                            (MathHelper.cos(a) * 0.25f).toDouble()
                                        )
                                    )
                                }

                                1 -> {
                                    a = (-player.rotationYaw - 90f) / Constants.RADIANS_TO_DEGREES
                                    pos.translate(Vector3(MathHelper.sin(a) * 0.25, 0.0, MathHelper.cos(a) * 0.25))
                                }
                            }

                            val rotation: Float = player.rotationYaw - 180
                            pos = WorldUtil.getFootprintPosition(player.world, rotation, pos, BlockVec3(player))

                            val chunkKey: Long = ChunkPos.asLong(pos.intX() shr 4, pos.intZ() shr 4)
                            TickHandlerServer.addFootprint(
                                chunkKey,
                                Footprint(GCCoreUtil.getDimensionID(player.world), pos, rotation, player.getName(), -1),
                                GCCoreUtil.getDimensionID(player.world)
                            )

                            // Increment and cap step counter at 1
                            stats.setLastStep((stats.getLastStep() + 1) % 2)
                            stats.setDistanceSinceLastStep(0.0)
                        } else {
                            stats.setDistanceSinceLastStep(stats.getDistanceSinceLastStep() + motionSqrd)
                        }
                    }
                }
            }
        }

        @JvmStatic
        protected fun sendAirRemainingPacket(player: EntityPlayerMP, stats: GCPlayerStats) {
            val f1: Float =
                if ((stats.getTankInSlot1())!!.isEmpty()) 0.0f else (stats.getTankInSlot1())!!.getMaxDamage() / 90.0f
            val f2: Float =
                if ((stats.getTankInSlot2())!!.isEmpty()) 0.0f else (stats.getTankInSlot2())!!.getMaxDamage() / 90.0f
            (GalacticraftCore.packetPipeline)!!.sendTo(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_AIR_REMAINING,
                    GCCoreUtil.getDimensionID(player.world),
                    arrayOf<Any?>(
                        MathHelper.floor(stats.getAirRemaining() / f1),
                        MathHelper.floor(stats.getAirRemaining2() / f2),
                        PlayerUtil.getName(player)
                    )
                ), player
            )
        }

        @JvmStatic
        fun sendGearUpdatePacket(
            player: EntityPlayerMP,
            packetType: EnumModelPacketType,
            gearType: EnumExtendedInventorySlot
        ) {
            sendGearUpdatePacket(player, packetType, gearType, GEAR_NOT_PRESENT)
        }

        @JvmStatic
        fun sendGearUpdatePacket(
            player: EntityPlayerMP,
            packetType: EnumModelPacketType,
            gearType: EnumExtendedInventorySlot,
            gearID: Int
        ) {
            val theServer: MinecraftServer? = player.server
            if (theServer != null && PlayerUtil.getPlayerForUsernameVanilla(
                    theServer,
                    (PlayerUtil.getName(player))!!
                ) != null
            ) {
                (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_UPDATE_GEAR_SLOT,
                        GCCoreUtil.getDimensionID(player.world),
                        arrayOf<Any?>(PlayerUtil.getName(player), packetType.ordinal, gearType.ordinal, gearID)
                    ),
                    NetworkRegistry.TargetPoint(
                        GCCoreUtil.getDimensionID(player.world),
                        player.posX,
                        player.posY,
                        player.posZ,
                        50.0
                    )
                )
            }
        }
    }
}
