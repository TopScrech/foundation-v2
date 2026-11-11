package asmodeuscore.core.event

import foundation.inventory.armorItems
import java.util.Arrays
import net.minecraftforge.common.config.Config.Type
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent

import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.utils.ACCompatibilityManager
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.api.space.IExBody
import asmodeuscore.core.utils.ACAttributePlayer
import asmodeuscore.core.event.PressureEvent
import asmodeuscore.AsmodeusCore
import asmodeuscore.core.handler.capabilities.ACCapabilityStatsHandler
import asmodeuscore.core.handler.capabilities.ACCapabilityProviderStats
import asmodeuscore.core.network.packet.ACPacketSimple
import asmodeuscore.core.network.packet.ACPacketSimple.ACEnumSimplePacket
import asmodeuscore.api.dimension.IProviderWeather
import asmodeuscore.core.handler.capabilities.ACStatsCapability
import asmodeuscore.core.handler.LightningStormHandler
import asmodeuscore.api.entity.ICustomEngine
import asmodeuscore.core.astronomy.WeatherData.WeatherType
import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData
import asmodeuscore.core.astronomy.dimension.world.data.MeteoricRainSaveData
import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData
import asmodeuscore.core.event.PressureEvent.WaterPressure
import asmodeuscore.core.event.AsmodeusEvent
import asmodeuscore.core.utils.ACDamageSource
import asmodeuscore.core.event.RadiationEvent
import asmodeuscore.core.registers.potions.ACPotions
import asmodeuscore.api.item.IItemPressurized
import asmodeuscore.api.item.IItemRadiation
import asmodeuscore.core.handler.capabilities.IStatsCapability
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.block.material.Material
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.AttributeInstance as IAttributeInstance
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.effect.MobEffectInstance as PotionEffect
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.server.permission.PermissionAPI
import java.lang.NullPointerException
import java.util.HashMap

open class AsmodeusEvent {
    private var engine_id = 0

    @SubscribeEvent
    open fun onAttachCapability(event: AttachCapabilitiesEvent<Entity?>) {
        if (event.getObject() is EntityPlayerMP) {
            event.addCapability(
                ACCapabilityStatsHandler.AC_PLAYER_PROPERTIES,
                ACCapabilityProviderStats(event.getObject() as EntityPlayerMP?)
            )
        }
        /*
		 * else if (event.getObject() instanceof EntityPlayer &&
		 * ((EntityPlayer)event.getObject()).world.isRemote) {
		 * this.onAttachCapabilityClient(event); }
		 */
    }

    /*
	 * @SideOnly(Side.CLIENT) private void
	 * onAttachCapabilityClient(AttachCapabilitiesEvent<Entity> event) { if
	 * (event.getObject() instanceof EntityPlayerSP) {
	 * event.addCapability(GCCapabilities.GC_PLAYER_CLIENT_PROP, new
	 * CapabilityProviderStatsClient((EntityPlayerSP) event.getObject())); } }
	 */
    @SubscribeEvent
    open fun onConfigChanged(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (event.getModID() == AsmodeusCore.MODID) {
            ConfigManager.sync(AsmodeusCore.MODID, Config.Type.INSTANCE)

            (AsmodeusConfig.Companion.config)!!.save()
            AsmodeusConfig.Companion.syncConfig(true)
        }
        // System.out.println(event.getModID());
    }

    @SubscribeEvent
    open fun onPlayerLogIn(event: PlayerEvent.PlayerLoggedInEvent) {
        if (!event.player.world.isRemote && event.player is EntityPlayerMP) {
            println("Test event")
            (AsmodeusCore.packetPipeline)!!.sendTo(
                ACPacketSimple(
                    ACEnumSimplePacket.C_UPDATE_WORLD,
                    GCCoreUtil.getDimensionID(event.player.getEntityWorld())
                ), event.player as EntityPlayerMP
            )
        }
    }

    @SubscribeEvent
    open fun onPlayerJoinWorld(event: EntityJoinWorldEvent) {
        if (!event.getWorld().isRemote && event.getEntity() is EntityPlayerMP) {
            (AsmodeusCore.packetPipeline)!!.sendTo(
                ACPacketSimple(
                    ACEnumSimplePacket.C_UPDATE_WORLD,
                    GCCoreUtil.getDimensionID(event.getEntity().getEntityWorld())
                ),
                event.getEntity() as EntityPlayerMP?
            )
            println("Test event 2")
        }
    }

    @SubscribeEvent
    open fun onPlayerChangeDim(event: PlayerEvent.PlayerChangedDimensionEvent) {
        if (!event.player.world.isRemote && event.player is EntityPlayerMP) (AsmodeusCore.packetPipeline)!!.sendTo(
            ACPacketSimple(
                ACEnumSimplePacket.C_UPDATE_WORLD,
                GCCoreUtil.getDimensionID(event.player.world)
            ), event.player as EntityPlayerMP
        )
    }

    @SubscribeEvent
    open fun onUnloadWorld(e: WorldEvent.Unload) {
        if (e.getWorld().provider is IProviderWeather) e.getWorld().getPerWorldStorage().saveAllData()
    }

    @SubscribeEvent
    open fun onPlayerCloned(event: net.minecraftforge.event.entity.player.PlayerEvent.Clone) {
        val oldStats: IStatsCapability? = ACStatsCapability.Companion.get(event.getOriginal())
        val newStats: IStatsCapability = (ACStatsCapability.Companion.get(event.getEntityPlayer()))!!
        newStats.copyFrom(
            oldStats,
            !event.isWasDeath() || event.getOriginal().level().getGameRules().getBoolean("keepInventory")
        )
    }

    @SubscribeEvent
    open fun onEntityUpdate(event: LivingEvent.LivingUpdateEvent) {
        val living: EntityLivingBase = event.getEntityLiving()
        val world: World = living.level()

        this.doStormEffectForEntity(world, living)

        if (living is EntityPlayerMP) {
            if (!ACCompatibilityManager.isGalacticraftLoaded()) return

            val player: EntityPlayerMP = living
            val GCPlayer: GCPlayerStats = (GCPlayerStats.get(player))!!
            val GSPlayer: IStatsCapability = (ACStatsCapability.Companion.get(player))!!

            LightningStormHandler.spawnLightning(player)

            //Arrays.stream(AsmodeusConfig.igroredCelestialBody).forEach((s) -> System.out.println(s));
            if (GCPlayer.isUsingPlanetSelectionGui() && AsmodeusConfig.Companion.enableNewGalaxyMap) {
                // This sends the planets list again periodically (forcing the Celestial
                // Selection screen to open) in case of server/client lag
                // #PACKETSPAM

                this.sendPlanetList(player, GCPlayer, GSPlayer)
            }

            if (player.interactionManager.survivalOrAdventure()) {
                this.doPressureForEntity(world, player)
                this.doRadiatoinForEntity(world, player)
                this.doWaterPressureForEntity(world, player)
            }

            if (player.getVehicle() is ICustomEngine) {
                val rocket: ICustomEngine? = player.getVehicle() as ICustomEngine?
                engine_id = (rocket!!.getEngine())!!.getID()
            }

            if (player.tickCount % 100 == 0 && world.provider is IGalacticraftWorldProvider) {
                val thermal: Float = (world.provider as IGalacticraftWorldProvider).getThermalLevelModifier()
                val warn_temp: Float = 2f // > 60C
                val cool_temp: Float = -1.2f // < -36
                val thermal_check: Boolean = thermal > warn_temp || thermal < cool_temp
                (AsmodeusCore.packetPipeline)!!.sendTo(
                    ACPacketSimple(
                        ACEnumSimplePacket.C_UPDATE_OXYGEN_STATUS, world,
                        arrayOf<Any>(OxygenUtil.isAABBInBreathableAirBlock(player, thermal_check))
                    ), player
                )
            }
        }
    }

    @SubscribeEvent
    open fun onWorldTick(e: TickEvent.WorldTickEvent) {
        val w: World = e.world
        if (!w.isClientSide) {
            val flag: Boolean = w.getGameRules().getBoolean("doWeatherCycle")

            if (flag && w.provider is IGalacticraftWorldProvider && w.provider is IProviderWeather) {
                val weaher: IProviderWeather = w.provider as IProviderWeather
                val gcprovider: IGalacticraftWorldProvider = w.provider as IGalacticraftWorldProvider
                val name: String? = (gcprovider.getCelestialBody())!!.getName()

                try {
                    if ((weaher.getWeather())!!.getWeatherType() == WeatherType.DUST_STORM) {
                        val storm_data: DustStormSaveData = DustStormSaveData.get(w, name)
                        DustStormSaveData.calculateWeather(storm_data, w.random)
                    }
                    if ((weaher.getWeather())!!.getWeatherType() == WeatherType.METEORIC_RAIN) {
                        val rain_data: MeteoricRainSaveData = MeteoricRainSaveData.get(w, name)
                        MeteoricRainSaveData.calculateWeather(rain_data, w.random)
                        asmodeuscore.core.astronomy.dimension.world.MeteoricRainSpawner.spawnMeteor(rain_data, w as net.minecraft.server.level.ServerLevel)
                    }
                    if ((weaher.getWeather())!!.getWeatherType() == WeatherType.FROZEN_STORM) {
                        val frozenstorm_data: FrozenStormSaveData = FrozenStormSaveData.get(w, name)
                        FrozenStormSaveData.calculateWeather(frozenstorm_data, w.random)
                    }
                } catch (ignored: AbstractMethodError) {
                } catch (ignored: NullPointerException) {
                }
            }
        }
    }

    private fun doStormEffectForEntity(world: World, living: EntityLivingBase) {
        if (!world.isClientSide && world.getGameTime() % 20 == 0L && world.provider is IProviderWeather
            && world.provider is IGalacticraftWorldProvider
        ) {
            val gcprovider: IGalacticraftWorldProvider = world.provider as IGalacticraftWorldProvider
            val name: String? = (gcprovider.getCelestialBody())!!.getName()
            val msd: DustStormSaveData = DustStormSaveData.get(world, name)
            val msd_frozen: FrozenStormSaveData = FrozenStormSaveData.get(world, name)

            if (living.isEntityUndead()) return

            if (living is EntityPlayerMP) {
                val player: EntityPlayerMP = living
                (AsmodeusCore.packetPipeline)!!.sendTo(
                    ACPacketSimple(
                        ACEnumSimplePacket.C_UPDATE_STORM_USER, world,
                        arrayOf<Any?>(
                            name, msd.isDustStorm(), msd.tickDustStorm, msd.tickClearWeather,
                            msd.prevStormStrength, msd.stormStrength, 0
                        )
                    ),
                    player
                )

                (AsmodeusCore.packetPipeline)!!.sendTo(
                    ACPacketSimple(
                        ACEnumSimplePacket.C_UPDATE_STORM_USER, world,
                        arrayOf<Any?>(
                            name, msd_frozen.isFrozenStorm(), msd_frozen.tickStorm, msd_frozen.tickClearWeather,
                            msd_frozen.prevStormStrength, msd_frozen.stormStrength, 1
                        )
                    ),
                    player
                )
            }

            if (msd.isDustStorm()) {
                var flag: Boolean = true
                for (y: Int in 256 downTo living.getPosition().getY() + 1) {
                    if (!world.isEmptyBlock(BlockPos(living.getPosition().getX(), y, living.getPosition().getZ()))) {
                        flag = false
                        break
                    }
                }
                if (AsmodeusConfig.Companion.enableWeatherDamage && flag) living.attackEntityFrom(
                    DamageSource.CACTUS,
                    0.5f * AsmodeusConfig.Companion.stormDamageRate
                )
            }
        }
    }

    private fun doWaterPressureForEntity(world: World, living: EntityLivingBase) {
        val player: EntityPlayerMP = living as EntityPlayerMP
        if (player.level().provider is IGalacticraftWorldProvider && !player.capabilities.isCreativeMode) {
            val provider: IGalacticraftWorldProvider = player.level().provider as IGalacticraftWorldProvider
            if (provider.getCelestialBody() is IExBody) {
                var i: Float = 0f
                var blocks: Int = 0

                while (player.level().getBlockState(player.getPosition().up(blocks)).getMaterial() !== Material.AIR) {
                    i += (0.1 * (provider.getCelestialBody() as IExBody).getWaterPressure()).toFloat()
                    blocks++
                }

                val event: WaterPressure = WaterPressure(living, i)
                MinecraftForge.EVENT_BUS.post(event)
            }
        }
    }

    private fun doPressureForEntity(world: World, living: EntityLivingBase) {
        val player: EntityPlayerMP = living as EntityPlayerMP

        // TODO Experimental
        if (player.level().provider is IGalacticraftWorldProvider && !player.capabilities.isCreativeMode) {
            val provider: IGalacticraftWorldProvider = player.level().provider as IGalacticraftWorldProvider

            var level: Float = 0f

            if (provider.getCelestialBody() is IExBody) level =
                (provider.getCelestialBody() as IExBody).getAtmosphericPressure()

            val data: BodiesData? = BodiesRegistry.getData(provider.getCelestialBody())
            if (data != null) level = data.getPressure().toFloat()

            val event: PressureEvent = PressureEvent(living, level)
            MinecraftForge.EVENT_BUS.post(event)

            if (!event.isCanceled() && !getPressureArmor(player)) {
                if (player.tickCount % 50 == 0) {
                    if (level > 10) player.addEffect(PotionEffect(MobEffects.NAUSEA, 10 * 20))
                    if (level > 35) player.addEffect(PotionEffect(MobEffects.BLINDNESS, 10 * 20))
                    if (level > 25) player.addEffect(PotionEffect(MobEffects.SLOWNESS, 280, 4))
                    if (level > 45) foundation.damage.FoundationDamageTypes.hurt(player, ACDamageSource.pressure,
                        2.5f * AsmodeusConfig.Companion.atmospherePressureDamageRate
                    )
                }
            }
        }
    }

    private fun doRadiatoinForEntity(world: World, living: EntityLivingBase) {
        val player: EntityPlayerMP = living as EntityPlayerMP

        val lvl: IAttributeInstance = player.getAttribute(ACAttributePlayer.RADIATION_LVL)!!

        if (player.level().provider is IGalacticraftWorldProvider && !player.capabilities.isCreativeMode) {
            val provider: IGalacticraftWorldProvider = player.level().provider as IGalacticraftWorldProvider
            val getModify: Float = 1.0f

            val eventR: RadiationEvent = RadiationEvent(living, lvl.getValue().toInt(), 20)
            MinecraftForge.EVENT_BUS.post(eventR)

            var isRad: Boolean = false
            val data: BodiesData? = BodiesRegistry.getData(provider.getCelestialBody())
            if (data != null) isRad = data.isSolarRadiation()

            if (provider.getCelestialBody() is IExBody) isRad =
                (provider.getCelestialBody() as IExBody).isSolarRadiation()

            if (isRad) {
                if (!getRadiationArmor(
                        player,
                        getModify
                    ) && !eventR.isCanceled() && player.tickCount % eventR.getRadiationSpeed() == 0
                ) {
                    if (lvl.getValue() < 58 && player.level().isDaytime()
                        && player.level().canBlockSeeSky(player.getPosition())
                    ) {
                        lvl.setBaseValue(lvl.getValue() + 1)
                    } else if (lvl.getValue() > 45) lvl.setBaseValue(lvl.getValue() - 1)

                    if (lvl.getValue() > 54) {
                        if (living is EntityPlayerMP && living.y <= 1000) {
                            if (player.tickCount % 20 == 0 && lvl.getValue() > 48) {
                                player.addEffect(PotionEffect(ACPotions.holder(ACPotions.radiation), 20 * 5))
                            }
                        }
                    }
                } else if (getRadiationArmor(player, getModify)) {
                    player.removeEffect(ACPotions.holder(ACPotions.radiation))
                } // else

                // lvl.setBaseValue(0);
            } else {
                if (player.tickCount % 600 == 0 && lvl.getValue() > 0) lvl.setBaseValue(lvl.getValue() - 1)
            }
        }

        /*
		 * if (lvl.getValue() > 54) { WorldProvider provider =
		 * player.world.provider;
		 * 
		 * if(living.posY <= 1000) { if (living instanceof EntityPlayerMP) { if
		 * (player.ticksExisted % 20 == 0 && !player.capabilities.isCreativeMode &&
		 * lvl.getValue() > 48 && !(this.getRadiationArmor(player))) {
		 * player.addEffect(new PotionEffect(ACPotions.holder(ACPotions.radiation), 5)); } else if
		 * (this.getRadiationArmor(player)) {
		 * player.removeEffect(ACPotions.holder(ACPotions.radiation)); } } } }
		 */
    }

    protected open fun sendPlanetList(player: EntityPlayerMP, playerStats: GCPlayerStats, acstats: IStatsCapability) {
        val map: HashMap<String?,Int>
        if (player.tickCount % 50 == 0)  // Check for genuine update - e.g. maybe some other player created a space
        // station or changed permissions
        // CAUTION: possible server load due to dimension loading, if any planets or
        // moons were (contrary to GC default) set to hotload
            map = WorldUtil.getArrayOfPossibleDimensions(playerStats.getSpaceshipTier(), player)
        else map = WorldUtil.getArrayOfPossibleDimensionsAgain(playerStats.getSpaceshipTier(), player)

        var temp: String = ""
        var count: Int = 0

        for (entry: MutableMap.MutableEntry<String?,Int> in map.entries) {
            temp = temp + entry.key + (if (count < map.entries.size - 1) "?" else "")
            count++
        }

        if (temp != playerStats.getSavedPlanetList() || (player.tickCount % 1 == 0)) {
            val canCreateStations: Boolean = PermissionAPI.hasPermission(player, Constants.PERMISSION_CREATE_STATION)
            (AsmodeusCore.packetPipeline)!!.sendTo(
                ACPacketSimple(
                    ACEnumSimplePacket.C_UPDATE_DIMENSION_LIST,
                    GCCoreUtil.getDimensionID(player.level()),
                    *arrayOf<Any?>(
                        player.getGameProfile().getName(), temp, canCreateStations,
                        playerStats.getSpaceshipTier(), engine_id, playerStats.getFuelLevel(),
                        acstats.isSecondPassenger()
                    )
                ),
                player
            )

            // GalacticraftCore.packetPipeline.sendTo(new
            // GSPacketSimple(GSEnumSimplePacket.C_UPDATE_DIMENSION_LIST,
            // GCCoreUtil.getDimensionID(player.world), new Object[] {
            // PlayerUtil.getName(player), temp, canCreateStations }), player);
            playerStats.setSavedPlanetList(temp)
            // GCLog.debug("Sending to " + player.getGameProfile().getName() + ": " + temp);
        }
    }

    companion object {
        @JvmStatic
        fun getPressureArmor(player: EntityPlayerMP): Boolean {
            for (i: Int in 0..3) if (player.inventory.armorItems.get(i).isEmpty()
                || player.inventory.armorItems.get(i).getItem() !is IItemPressurized
            ) return false
            return true
        }

        @JvmStatic
        fun getRadiationArmor(player: EntityPlayerMP, dimModify: Float): Boolean {
            for (i: Int in 0..3) if (player.inventory.armorItems.get(i).isEmpty()
                || !(player.inventory.armorItems.get(i).getItem() is IItemRadiation
                        && (player.inventory.armorItems.get(i).getItem() as IItemRadiation)
                    .getSolarRadiationProtectModify() >= dimModify)
            ) return false
            return true
        }
    }
}
