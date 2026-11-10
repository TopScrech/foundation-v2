package asmodeuscore.core.event;

import asmodeuscore.AsmodeusCore;
import asmodeuscore.api.dimension.IAdvancedSpace;
import asmodeuscore.api.item.IItemPressurized;
import asmodeuscore.api.space.IExBody;
import asmodeuscore.core.astronomy.BodiesData;
import asmodeuscore.core.astronomy.BodiesRegistry;
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace;
import asmodeuscore.core.astronomy.gui.overlay.OverlayDetectors;
import asmodeuscore.core.astronomy.gui.screen.AC_GuiCelestialSelection;
import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection;
import asmodeuscore.core.astronomy.sky.CloudProviderBase;
import asmodeuscore.core.astronomy.sky.CustomCloudRender;
import asmodeuscore.core.astronomy.sky.galacticraft.SkyProviderAsteroids;
import asmodeuscore.core.astronomy.sky.galacticraft.SkyProviderMoon;
import asmodeuscore.core.astronomy.sky.galacticraft.SkyProviderOverworld;
import asmodeuscore.core.configs.AsmodeusConfig;
import asmodeuscore.core.handler.ColorBlockHandler;
import asmodeuscore.core.utils.ACAttributePlayer;
import asmodeuscore.core.utils.ACCompatibilityManager;
import asmodeuscore.core.utils.Utils;
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider;
import asmodeuscore.core.utils.worldengine.WE_PerlinNoise;
import asmodeuscore.core.utils.worldengine.WE_WorldProvider;
import com.google.common.base.Strings;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids;
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static asmodeuscore.core.utils.Utils.getAtmoArmor;

public class AsmodeusClientEvent {

	public Minecraft mc = FMLClientHandler.instance().getClient();
	public static boolean inOxygenBlock = false;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGuiOpenEvent(GuiOpenEvent event) {
		if(ACCompatibilityManager.isGalacticraftLoaded())
			if(event.getGui() instanceof GuiCelestialSelection && AsmodeusConfig.enableNewGalaxyMap) {
                /*if(Loader.isModLoaded("ExtraPlanets"))
					event.getGui().mc.displayGuiScreen(null);
				*/
				//if (!ACCompatibilityManager.isPlanetProgressionLoaded())
				if(mc.gameSettings.isKeyDown(KeyHandlerClient.galaxyMap))
					event.setGui(new NewGuiCelestialSelection(true, null, false, null));
				if(mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && mc.gameSettings.isKeyDown(KeyHandlerClient.galaxyMap))
					event.setGui(new AC_GuiCelestialSelection(true, null, false));
			}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		World world = mc.world;

		if (world != null) {
			if(world.provider.getCloudRenderer() instanceof CloudProviderBase && !mc.isGamePaused()) {
				CloudProviderBase clouds = (CloudProviderBase) world.provider.getCloudRenderer();

				clouds.cloudTicksPrev = clouds.cloudTicks;
				clouds.cloudTicks += clouds.getCloudMovementSpeed(world);
			}

			if(world.provider.getCloudRenderer() instanceof CustomCloudRender && !mc.isGamePaused()) {
				CustomCloudRender clouds = (CustomCloudRender) world.provider.getCloudRenderer();

				clouds.cloudTicksPrev = clouds.cloudTicks;
				clouds.cloudTicks += clouds.getCloudMovementSpeed((WorldClient) world);
			}

			if (world.provider.getDimension() == 0 && AsmodeusConfig.enableSkyOverworld) {
				if (!(world.provider.getSkyRenderer() instanceof SkyProviderOverworld))
					world.provider.setSkyRenderer(new SkyProviderOverworld());
			}

			if (world.provider instanceof WorldProviderMoon && AsmodeusConfig.enableSkyMoon) {

				if (!(world.provider.getSkyRenderer() instanceof SkyProviderMoon))
					world.provider.setSkyRenderer(new SkyProviderMoon());
			}

			if (world.provider instanceof WorldProviderAsteroids && AsmodeusConfig.enableSkyAsteroids) {
				if (!(world.provider.getSkyRenderer() instanceof SkyProviderAsteroids))
					world.provider.setSkyRenderer(new SkyProviderAsteroids());
			}

			if(world.provider instanceof WorldProviderVenus && AsmodeusConfig.enableCloudsVenus) {
				if(!(world.provider.getCloudRenderer() instanceof CustomCloudRender)) {
					float[] f = {1.0F, 4.0F, 8F};
					world.provider.setCloudRenderer(new CustomCloudRender(f) {

						@Override
						public ResourceLocation getCloudTexture() {
							return default_clouds;
						}

						@Override
						public Vec3d getCloudColor(float renderPartialTicks) {
							return new Vec3d(81, 61, 0);
						}

						@Override
						public float getCloudMovementSpeed(WorldClient world) {
							return 3F;
						}
					});
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderTick(RenderTickEvent event) {
		if(!ACCompatibilityManager.isGalacticraftLoaded()) return;

		final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final EntityPlayerSP player = minecraft.player;
        final EntityPlayerSP playerBaseClient = PlayerUtil.getPlayerBaseClientFromPlayer(player, false);
        float level = 0;
        if (event.phase == Phase.END)
        {
			if (player != null)
            {
				if(player.world.provider instanceof IGalacticraftWorldProvider && OxygenUtil.shouldDisplayTankGui(minecraft.currentScreen) && !playerBaseClient.isSpectator() && !minecraft.gameSettings.showDebugInfo) {
					IGalacticraftWorldProvider provider = (IGalacticraftWorldProvider) player.world.provider;
					BodiesData data = BodiesRegistry.getData(provider.getCelestialBody());

					GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);


					if(provider.getCelestialBody() instanceof IExBody && !minecraft.player.isCreative()) {
						level = ((IExBody)provider.getCelestialBody()).getAtmosphericPressure();

						if(level != 0)
							OverlayDetectors.renderPressureIndicator(level, this.getInvalidLevel(1, level), !ConfigManagerCore.oxygenIndicatorLeft, !ConfigManagerCore.oxygenIndicatorBottom);

						if(((IExBody)provider.getCelestialBody()).isSolarRadiation())
							OverlayDetectors.renderRadiationIndicator((int)player.getEntityAttribute(ACAttributePlayer.RADIATION_LVL).getAttributeValue(), this.getInvalidLevel(2, level), !ConfigManagerCore.oxygenIndicatorLeft, !ConfigManagerCore.oxygenIndicatorBottom);

						if(((IExBody)provider.getCelestialBody()).getWaterPressure() > 0.0F && Utils.inWater(player)) {
							float i = 0;
							int blocks = 0;

							while(player.world.getBlockState(player.getPosition().up(blocks)).getMaterial() != Material.AIR) {
								i += 0.1 * ((IExBody)provider.getCelestialBody()).getWaterPressure();
								blocks++;
							}

							OverlayDetectors.renderWaterPressureIndicator(i, this.getInvalidLevel(3, i), !ConfigManagerCore.oxygenIndicatorLeft, !ConfigManagerCore.oxygenIndicatorBottom);
						}

					} else {
						if(data != null)
							level = data.getPressure();

						if(level != 0)
							OverlayDetectors.renderPressureIndicator(level, this.getInvalidLevel(1, level), !ConfigManagerCore.oxygenIndicatorLeft, !ConfigManagerCore.oxygenIndicatorBottom);

						if((data != null && data.isSolarRadiation()))
							OverlayDetectors.renderRadiationIndicator((int)player.getEntityAttribute(ACAttributePlayer.RADIATION_LVL).getAttributeValue(), this.getInvalidLevel(2, level), !ConfigManagerCore.oxygenIndicatorLeft, !ConfigManagerCore.oxygenIndicatorBottom);

					}

					float temp;

					//0.0 == getDefaultDergrees

					float mod = provider.getThermalLevelModifier();
					if(player.world.provider instanceof IAdvancedSpace)
						mod = ((IAdvancedSpace)provider).getThermalLevelModifier(player);
					if(mod > 0) mod += 1.0F;
					temp = mod * BodiesRegistry.getDefaultDergrees;


					//System.out.println(stats.getThermalLevel());

					OverlayDetectors.renderInfoPanel(temp, this.getInvalidLevel(2, level), !ConfigManagerCore.oxygenIndicatorLeft, !ConfigManagerCore.oxygenIndicatorBottom);

				}

				if(AsmodeusConfig.enableDebug && minecraft.inGameHasFocus){
					this.renderDebugInfo(minecraft);
				}
				//OverlaySpaceSuit.render();
            }
        }
	}

	private boolean getInvalidLevel(int mode, float level) {
		final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final EntityPlayer player = minecraft.player;

		PressureEvent event = new PressureEvent(player, level);
		if(event.isCanceled())
			return false;
		
        /*if(player.capabilities.isCreativeMode) 
        	return false;*/
        
        if(mode == 1)
        {
			if(level > 25 && !getAtmoArmor(player))
				return true;
		}
        else if(mode == 2)
        {
			if(player.getEntityAttribute(ACAttributePlayer.RADIATION_LVL).getAttributeValue() > 45D) return true;
		}
        else if(mode == 3)
			if(level > 40 && !getAtmoArmor(player)) return true;

		return false;
	}

	private void renderDebugInfo(Minecraft mc){
		List<String> list = getDebugList();

		int size = 3;
		if(mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
			size = list.size();

		int yOffset = 0;
		if(mc.gameSettings.showDebugInfo)
		{
			ScaledResolution screen = new ScaledResolution(mc);
			yOffset = screen.getScaledHeight() / 2 - 50;
		}
		for(int i = 0; i < size; ++i) {
			String s = list.get(i);
			if (!Strings.isNullOrEmpty(s)) {
				int j = mc.fontRenderer.FONT_HEIGHT;
				int k = mc.fontRenderer.getStringWidth(s);

				int i1 = 2 + j * i;
				Gui.drawRect(1, i1 - 1 + yOffset, 2 + k + 1, i1 + j - 1 + yOffset, -1873784752);
				mc.fontRenderer.drawString(s, 2, i1 + yOffset, 14737632);
			}
		}
	}

	public static List<String> getDebugList(){
		Minecraft mc = Minecraft.getMinecraft();
		String body = ((mc.player.getEntityWorld().provider instanceof IGalacticraftWorldProvider) ? ((IGalacticraftWorldProvider)mc.player.getEntityWorld().provider).getCelestialBody().getLocalizedName() : "Unnamed");
		String current_item = (mc.player.inventory.getCurrentItem().getItem() != Item.getItemFromBlock(Blocks.AIR) ? Item.REGISTRY.getNameForObject(mc.player.inventory.getCurrentItem().getItem()) + ":" +  mc.player.inventory.getCurrentItem().getItemDamage(): "None");

		float temp = mc.player.world.provider instanceof IGalacticraftWorldProvider ? ((IGalacticraftWorldProvider) mc.player.world.provider).getThermalLevelModifier() : 1.0F;
		long t1 = mc.player.world.provider instanceof WorldProviderSpace ? ((WorldProviderSpace) mc.player.world.provider).getDayLength() : 24000;
		long time = mc.player.world.getWorldTime() % (t1 > 0 ? t1 : 1);
		Chunk get_chunk = mc.player.getEntityWorld().getChunk(mc.player.getPosition());

		boolean isWE = mc.player.getEntityWorld().provider instanceof WE_WorldProvider;
		double count = 0;

		World world = ColorBlockHandler.world;
		if(isWE && mc.player.getEntityWorld().provider != null && world.provider instanceof WE_WorldProvider) {
			WE_ChunkProvider chunk = ((WE_WorldProvider)world.provider).chunk_provider;
			if(chunk != null) {
				double scaleX = chunk.biomemapScaleX;
				double persistance = chunk.biomemapPersistence;
				count = WE_PerlinNoise.PerlinNoise2D((mc.player.getEntityWorld().getSeed() * 11) ^ 6,	mc.player.getEntityWorld().getChunk(mc.player.getPosition()).x / scaleX, mc.player.getEntityWorld().getChunk(mc.player.getPosition()).z / scaleX,
						persistance, chunk.biomemapNumberOfOctaves)
						* chunk.biomemapScaleY;
			}
		}

		List<String> list = new ArrayList<>();
		list.add(AsmodeusCore.NAME + " " + AsmodeusCore.VERSION + " Debug MODE | FPS: " + mc.getDebugFPS());
		list.add("");
		list.add("Celestial Body: " + EnumColor.BRIGHT_GREEN +  body);
		list.add("");
		list.add("Current item: " + EnumColor.ORANGE + current_item);
		list.add(String.format("Player pos: x=%s y=%s z=%s", mc.player.getPosition().getX(), mc.player.getPosition().getY(), mc.player.getPosition().getZ()));
		list.add("Chunk pos: x=" + get_chunk.x + " z=" + get_chunk.z);
		list.add("");
		list.add("World Data:");
		list.add(String.format("Dimension: %s (ID: %s)", mc.player.world.provider.getDimensionType().getName(), mc.player.world.provider.getDimensionType().getId()));
		list.add("Temperature (F): " + temp);
		list.add("Vanilla Biome: " + mc.player.world.getBiomeForCoordsBody(mc.player.getPosition()).getBiomeName());
		list.add("Current Time: " + time + " | Day Length: " + (mc.player.world.provider instanceof WorldProviderSpace ? ((WorldProviderSpace) mc.player.world.provider).getDayLength() : mc.player.world.provider instanceof WE_WorldProviderSpace ?  ((WE_WorldProviderSpace) mc.player.world.provider).getDayLength() : "24000") + " | Total Time: " + mc.player.world.getWorldTime());
		list.add("Moon Phase: " + mc.player.world.getMoonPhase());
		list.add("Is Advance Space Provider: " + ((mc.player.getEntityWorld().provider instanceof IAdvancedSpace) ? "Yes" : "No"));

		if(isWE) {
			list.add("[WE] Biome Perlin Count: " + count);
		}
		return list;
	}
 }
