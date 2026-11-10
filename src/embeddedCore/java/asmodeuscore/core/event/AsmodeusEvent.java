package asmodeuscore.core.event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import asmodeuscore.AsmodeusCore;
import asmodeuscore.api.dimension.IProviderWeather;
import asmodeuscore.api.entity.ICustomEngine;
import asmodeuscore.api.item.IItemPressurized;
import asmodeuscore.api.item.IItemRadiation;
import asmodeuscore.api.space.IExBody;
import asmodeuscore.core.astronomy.BodiesData;
import asmodeuscore.core.astronomy.BodiesRegistry;
import asmodeuscore.core.astronomy.WeatherData.WeatherType;
import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData;
import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData;
import asmodeuscore.core.astronomy.dimension.world.data.MeteoricRainSaveData;
import asmodeuscore.core.configs.AsmodeusConfig;
import asmodeuscore.core.handler.LightningStormHandler;
import asmodeuscore.core.handler.capabilities.ACCapabilityProviderStats;
import asmodeuscore.core.handler.capabilities.ACCapabilityStatsHandler;
import asmodeuscore.core.handler.capabilities.ACStatsCapability;
import asmodeuscore.core.handler.capabilities.IStatsCapability;
import asmodeuscore.core.network.packet.ACPacketSimple;
import asmodeuscore.core.network.packet.ACPacketSimple.ACEnumSimplePacket;
import asmodeuscore.core.registers.potions.ACPotions;
import asmodeuscore.core.utils.ACAttributePlayer;
import asmodeuscore.core.utils.ACCompatibilityManager;
import asmodeuscore.core.utils.ACDamageSource;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.server.permission.PermissionAPI;

public class AsmodeusEvent {

	private int engine_id = 0;

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing e) {
		if (e.getEntity() instanceof EntityPlayer) {
			((EntityPlayer) e.getEntity()).getAttributeMap().registerAttribute(ACAttributePlayer.RADIATION_LVL);
		}
	}

	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayerMP) {
			event.addCapability(ACCapabilityStatsHandler.AC_PLAYER_PROPERTIES,
					new ACCapabilityProviderStats((EntityPlayerMP) event.getObject()));
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
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(AsmodeusCore.MODID)) {
			ConfigManager.sync(AsmodeusCore.MODID, Type.INSTANCE);

			AsmodeusConfig.config.save();
			AsmodeusConfig.syncConfig(true);
		}
		// System.out.println(event.getModID());
	}

	@SubscribeEvent
	public void onPlayerLogIn(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote && event.player instanceof EntityPlayerMP) {
			System.out.println("Test event");
			AsmodeusCore.packetPipeline.sendTo(new ACPacketSimple(ACEnumSimplePacket.C_UPDATE_WORLD,
					GCCoreUtil.getDimensionID(event.player.getEntityWorld())), (EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
			AsmodeusCore.packetPipeline.sendTo(
					new ACPacketSimple(ACEnumSimplePacket.C_UPDATE_WORLD,
							GCCoreUtil.getDimensionID(event.getEntity().getEntityWorld())),
					(EntityPlayerMP) event.getEntity());
			System.out.println("Test event 2");

		}
	}

	@SubscribeEvent
	public void onPlayerChangeDim(PlayerChangedDimensionEvent event) {
		if (!event.player.world.isRemote && event.player instanceof EntityPlayerMP)
			AsmodeusCore.packetPipeline.sendTo(new ACPacketSimple(ACEnumSimplePacket.C_UPDATE_WORLD,
					GCCoreUtil.getDimensionID(event.player.world)), (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void onUnloadWorld(WorldEvent.Unload e) {
		if (e.getWorld().provider instanceof IProviderWeather)
			e.getWorld().getPerWorldStorage().saveAllData();
	}

	@SubscribeEvent
	public void onPlayerCloned(PlayerEvent.Clone event) {
		IStatsCapability oldStats = ACStatsCapability.get(event.getOriginal());
		IStatsCapability newStats = ACStatsCapability.get(event.getEntityPlayer());
		newStats.copyFrom(oldStats,
				!event.isWasDeath() || event.getOriginal().world.getGameRules().getBoolean("keepInventory"));
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		World world = living.world;

		this.doStormEffectForEntity(world, living);

		if (living instanceof EntityPlayerMP) {
			if (!ACCompatibilityManager.isGalacticraftLoaded())
				return;

			EntityPlayerMP player = (EntityPlayerMP) living;
			final GCPlayerStats GCPlayer = GCPlayerStats.get(player);
			IStatsCapability GSPlayer = ACStatsCapability.get(player);

			LightningStormHandler.spawnLightning(player);

			//Arrays.stream(AsmodeusConfig.igroredCelestialBody).forEach((s) -> System.out.println(s));

			if (GCPlayer.isUsingPlanetSelectionGui() && AsmodeusConfig.enableNewGalaxyMap) {
				// This sends the planets list again periodically (forcing the Celestial
				// Selection screen to open) in case of server/client lag
				// #PACKETSPAM

				this.sendPlanetList(player, GCPlayer, GSPlayer);
			}

			if (player.interactionManager.survivalOrAdventure()) {
				this.doPressureForEntity(world, player);
				this.doRadiatoinForEntity(world, player);
				this.doWaterPressureForEntity(world, player);
			}

			if (player.getRidingEntity() instanceof ICustomEngine) {
				ICustomEngine rocket = (ICustomEngine) player.getRidingEntity();
				engine_id = rocket.getEngine().getID();
			}

			if (player.ticksExisted % 100 == 0 && world.provider instanceof IGalacticraftWorldProvider) {
				float thermal = ((IGalacticraftWorldProvider) world.provider).getThermalLevelModifier();
				final float warn_temp = 2F; // > 60C
				final float cool_temp = -1.2F; // < -36
				boolean thermal_check = thermal > warn_temp || thermal < cool_temp;
				AsmodeusCore.packetPipeline.sendTo(new ACPacketSimple(ACEnumSimplePacket.C_UPDATE_OXYGEN_STATUS, world,
						new Object[] { OxygenUtil.isAABBInBreathableAirBlock(player, thermal_check) }), player);
			}
		}

	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent e) {
		World w = e.world;
		if (!w.isRemote) {

			boolean flag = w.getGameRules().getBoolean("doWeatherCycle");

			if (flag && w.provider instanceof IGalacticraftWorldProvider && w.provider instanceof IProviderWeather) {

				IProviderWeather weaher = (IProviderWeather) w.provider;
				IGalacticraftWorldProvider gcprovider = (IGalacticraftWorldProvider) w.provider;
				String name = gcprovider.getCelestialBody().getName();

				try {
					if (weaher.getWeather().getWeatherType() == WeatherType.DUST_STORM) {
						DustStormSaveData storm_data = DustStormSaveData.get(w, name);
						DustStormSaveData.calculateWeather(storm_data, w.rand);
					}
					if (weaher.getWeather().getWeatherType() == WeatherType.METEORIC_RAIN) {
						MeteoricRainSaveData rain_data = MeteoricRainSaveData.get(w, name);
						MeteoricRainSaveData.calculateWeather(rain_data, w.rand);
						MeteoricRainSaveData.spawnMeteor(rain_data);
					}
					if(weaher.getWeather().getWeatherType() == WeatherType.FROZEN_STORM) {
						FrozenStormSaveData frozenstorm_data = FrozenStormSaveData.get(w, name);
						FrozenStormSaveData.calculateWeather(frozenstorm_data, w.rand);
					}
				} catch (AbstractMethodError | NullPointerException ignored){

				}
			}
		}
	}

	private void doStormEffectForEntity(World world, EntityLivingBase living) {
		if (!world.isRemote && world.getTotalWorldTime() % 20 == 0 && world.provider instanceof IProviderWeather
				&& world.provider instanceof IGalacticraftWorldProvider) {
			IGalacticraftWorldProvider gcprovider = (IGalacticraftWorldProvider) world.provider;
			String name = gcprovider.getCelestialBody().getName();
			DustStormSaveData msd = DustStormSaveData.get(world, name);
			FrozenStormSaveData msd_frozen = FrozenStormSaveData.get(world, name);

			if (living.isEntityUndead())
				return;

			if (living instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) living;
				AsmodeusCore.packetPipeline.sendTo(new ACPacketSimple(ACEnumSimplePacket.C_UPDATE_STORM_USER, world,
								new Object[] { name, msd.isDustStorm(), msd.tickDustStorm, msd.tickClearWeather,
										msd.prevStormStrength, msd.stormStrength, 0 }),
						player);

				AsmodeusCore.packetPipeline.sendTo(new ACPacketSimple(ACEnumSimplePacket.C_UPDATE_STORM_USER, world,
								new Object[] { name, msd_frozen.isFrozenStorm(), msd_frozen.tickStorm, msd_frozen.tickClearWeather,
										msd_frozen.prevStormStrength, msd_frozen.stormStrength, 1 }),
						player);
			}

			if (msd.isDustStorm()) {

				boolean flag = true;
				for (int y = 256; y > living.getPosition().getY(); y--) {
					if (!world.isAirBlock(new BlockPos(living.getPosition().getX(), y, living.getPosition().getZ()))) {
						flag = false;
						break;
					}
				}
				if (AsmodeusConfig.enableWeatherDamage && flag)
					living.attackEntityFrom(DamageSource.CACTUS, 0.5F * AsmodeusConfig.stormDamageRate);
			}

		}
	}

	private void doWaterPressureForEntity(World world, EntityLivingBase living) {
		EntityPlayerMP player = (EntityPlayerMP) living;
		if (player.world.provider instanceof IGalacticraftWorldProvider && !player.capabilities.isCreativeMode) {
			IGalacticraftWorldProvider provider = (IGalacticraftWorldProvider) player.world.provider;
			if (provider.getCelestialBody() instanceof IExBody) {
				float i = 0;
				int blocks = 0;

				while (player.world.getBlockState(player.getPosition().up(blocks)).getMaterial() != Material.AIR) {
					i += 0.1 * ((IExBody) provider.getCelestialBody()).getWaterPressure();
					blocks++;
				}

				PressureEvent.WaterPressure event = new PressureEvent.WaterPressure(living, i);
				MinecraftForge.EVENT_BUS.post(event);
			}
		}
	}

	private void doPressureForEntity(World world, EntityLivingBase living) {
		EntityPlayerMP player = (EntityPlayerMP) living;

		// TODO Experimental
		if (player.world.provider instanceof IGalacticraftWorldProvider && !player.capabilities.isCreativeMode) {
			IGalacticraftWorldProvider provider = (IGalacticraftWorldProvider) player.world.provider;

			float level = 0;

			if (provider.getCelestialBody() instanceof IExBody)
				level = ((IExBody) provider.getCelestialBody()).getAtmosphericPressure();

			BodiesData data = BodiesRegistry.getData(provider.getCelestialBody());
			if (data != null)
				level = data.getPressure();

			PressureEvent event = new PressureEvent(living, level);
			MinecraftForge.EVENT_BUS.post(event);

			if (!event.isCanceled() && !this.getPressureArmor(player)) {
				if (player.ticksExisted % 50 == 0) {
					if (level > 10)
						player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 10 * 20));
					if (level > 35)
						player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 10 * 20));
					if (level > 25)
						player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 280, 4));
					if (level > 45)
						player.attackEntityFrom(ACDamageSource.pressure, 2.5F * AsmodeusConfig.atmospherePressureDamageRate);
				}
			}
		}
	}

	private void doRadiatoinForEntity(World world, EntityLivingBase living) {
		EntityPlayerMP player = (EntityPlayerMP) living;

		IAttributeInstance lvl = player.getEntityAttribute(ACAttributePlayer.RADIATION_LVL);

		if (player.world.provider instanceof IGalacticraftWorldProvider && !player.capabilities.isCreativeMode) {
			IGalacticraftWorldProvider provider = (IGalacticraftWorldProvider) player.world.provider;
			float getModify = 1.0F;

			RadiationEvent eventR = new RadiationEvent(living, (int) lvl.getAttributeValue(), 20);
			MinecraftForge.EVENT_BUS.post(eventR);

			boolean isRad = false;
			BodiesData data = BodiesRegistry.getData(provider.getCelestialBody());
			if (data != null)
				isRad = data.isSolarRadiation();

			if (provider.getCelestialBody() instanceof IExBody)
				isRad = ((IExBody) provider.getCelestialBody()).isSolarRadiation();

			if (isRad) {

				if (!this.getRadiationArmor(player, getModify) && !eventR.isCanceled()
						&& player.ticksExisted % eventR.getRadiationSpeed() == 0) {
					if (lvl.getAttributeValue() < 58 && player.getEntityWorld().isDaytime()
							&& player.getEntityWorld().canBlockSeeSky(player.getPosition())) {
						lvl.setBaseValue(lvl.getAttributeValue() + 1);

					} else if (lvl.getAttributeValue() > 45)
						lvl.setBaseValue(lvl.getAttributeValue() - 1);

					if (lvl.getAttributeValue() > 54) {
						if (living instanceof EntityPlayerMP && living.posY <= 1000) {
							if (player.ticksExisted % 20 == 0 && lvl.getAttributeValue() > 48) {
								player.addPotionEffect(new PotionEffect(ACPotions.radiation, 20 * 5));
							}
						}
					}
				} else if (this.getRadiationArmor(player, getModify)) {
					player.removePotionEffect(ACPotions.radiation);
				} // else
				// lvl.setBaseValue(0);
			} else {
				if (player.ticksExisted % 600 == 0 && lvl.getAttributeValue() > 0)
					lvl.setBaseValue(lvl.getAttributeValue() - 1);
			}
		}

		/*
		 * if (lvl.getAttributeValue() > 54) { WorldProvider provider =
		 * player.world.provider;
		 * 
		 * if(living.posY <= 1000) { if (living instanceof EntityPlayerMP) { if
		 * (player.ticksExisted % 20 == 0 && !player.capabilities.isCreativeMode &&
		 * lvl.getAttributeValue() > 48 && !(this.getRadiationArmor(player))) {
		 * player.addPotionEffect(new PotionEffect(ACPotions.radiation, 5)); } else if
		 * (this.getRadiationArmor(player)) {
		 * player.removePotionEffect(ACPotions.radiation); } } } }
		 */
	}

	public static boolean getPressureArmor(EntityPlayerMP player) {
		for (int i = 0; i < 4; i++)
			if (player.inventory.armorInventory.get(i).isEmpty()
					|| !(player.inventory.armorInventory.get(i).getItem() instanceof IItemPressurized))
				return false;
		return true;
	}

	public static boolean getRadiationArmor(EntityPlayerMP player, float dimModify) {
		for (int i = 0; i < 4; i++)
			if (player.inventory.armorInventory.get(i).isEmpty()
					|| !(player.inventory.armorInventory.get(i).getItem() instanceof IItemRadiation
					&& ((IItemRadiation) player.inventory.armorInventory.get(i).getItem())
					.getSolarRadiationProtectModify() >= dimModify))
				return false;
		return true;
	}

	protected void sendPlanetList(EntityPlayerMP player, GCPlayerStats playerStats, IStatsCapability acstats) {
		HashMap<String, Integer> map;
		if (player.ticksExisted % 50 == 0)
			// Check for genuine update - e.g. maybe some other player created a space
			// station or changed permissions
			// CAUTION: possible server load due to dimension loading, if any planets or
			// moons were (contrary to GC default) set to hotload
			map = WorldUtil.getArrayOfPossibleDimensions(playerStats.getSpaceshipTier(), player);
		else
			map = WorldUtil.getArrayOfPossibleDimensionsAgain(playerStats.getSpaceshipTier(), player);

		String temp = "";
		int count = 0;

		for (Entry<String, Integer> entry : map.entrySet()) {
			temp = temp.concat(entry.getKey() + (count < map.entrySet().size() - 1 ? "?" : ""));
			count++;
		}

		if (!temp.equals(playerStats.getSavedPlanetList()) || (player.ticksExisted % 1 == 0)) {

			boolean canCreateStations = PermissionAPI.hasPermission(player, Constants.PERMISSION_CREATE_STATION);
			AsmodeusCore.packetPipeline.sendTo(new ACPacketSimple(ACEnumSimplePacket.C_UPDATE_DIMENSION_LIST,
							GCCoreUtil.getDimensionID(player.world),
							new Object[] { player.getGameProfile().getName(), temp, canCreateStations,
									playerStats.getSpaceshipTier(), engine_id, playerStats.getFuelLevel(),
									acstats.isSecondPassenger() }),
					player);
			// GalacticraftCore.packetPipeline.sendTo(new
			// GSPacketSimple(GSEnumSimplePacket.C_UPDATE_DIMENSION_LIST,
			// GCCoreUtil.getDimensionID(player.world), new Object[] {
			// PlayerUtil.getName(player), temp, canCreateStations }), player);

			playerStats.setSavedPlanetList(temp);
			// GCLog.debug("Sending to " + player.getGameProfile().getName() + ": " + temp);
		}
	}

}
