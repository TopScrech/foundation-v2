package asmodeuscore.core.handler;

import asmodeuscore.api.dimension.IProviderFog;
import asmodeuscore.core.event.AsmodeusClientEvent;
import asmodeuscore.core.utils.Utils;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProviderFogHandler
{
	private static double fogX, fogZ;
	private static boolean fogInit;
	private static float fogFarPlaneDistance;
	private static float newFogDensity;
	private static float fogDensity = 1.0F;
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGetFogColour(FogColors event)
	{
		if (event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntity();
			World world = player.world;
			int x = MathHelper.floor(player.posX);
			int y = MathHelper.floor(player.posY);
			int z = MathHelper.floor(player.posZ);
			IBlockState blockAtEyes = ActiveRenderInfo.getBlockStateAtEntityViewpoint(world, event.getEntity(), (float)event.getRenderPartialTicks());

			if (blockAtEyes.getMaterial() == Material.LAVA)
			{
				return;
			}

			Vec3d mixedColor;

			if (blockAtEyes.getMaterial() == Material.WATER)
			{
				mixedColor = getFogBlendColorWater(world, player, x, y, z, event.getRenderPartialTicks());
			}
			else
			{
				mixedColor = getFogBlendColour(world, player, x, y, z, event.getRed(), event.getGreen(), event.getBlue(), event.getRenderPartialTicks());
			}
			float temp = (float) (player.posY / 700) % 255;
			
			float r = (float) (mixedColor.x - temp < 0 ? 0.0F : mixedColor.x - temp);
			float g = (float) (mixedColor.y - temp < 0 ? 0.0F : mixedColor.y - temp);
			float b = (float) (mixedColor.z - temp < 0 ? 0.0F : mixedColor.z - temp);
			event.setRed(r);
			event.setGreen(g);
			event.setBlue(b);
			
			
		}
	}

	@SubscribeEvent
    public void renderFog(EntityViewRenderEvent.RenderFogEvent event) {

		World world = Minecraft.getMinecraft().player.world;
        WorldProvider provider = world.provider;
		
        if (provider instanceof IProviderFog) {
        	float sandstormFog = 1F;
        	IProviderFog providerAtum = (IProviderFog) provider;


        	EntityPlayer player = (EntityPlayer) event.getEntity();
        	int playerX = MathHelper.floor(player.posX);
        	int playerY = MathHelper.floor(player.posY);
        	int playerZ = MathHelper.floor(player.posZ);
        	
        	float fogDensity = 1-providerAtum.getFogDensity(playerX, playerY, playerZ); //0.015F default;

            if (event.getEntity() instanceof EntityPlayer) {
            	if(fogDensity == 0.0F)            		
            		GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
            	else
            		GlStateManager.setFog(GlStateManager.FogMode.EXP);
                
                ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                /*if (player.getPosition().getY() <= 60) {
                    fogDensity += (float) (62 - player.getPosition().getY()) * 0.025F;
                }*/
               /* if (helmet.getItem() instanceof ItemEyesOfAtum) {
                    fogDensity = fogDensity / 3;
                }
                if (helmet.getItem() == AtumItems.WANDERER_HELMET || helmet.getItem() == AtumItems.DESERT_HELMET_IRON || helmet.getItem() == AtumItems.DESERT_HELMET_GOLD || helmet.getItem() == AtumItems.DESERT_HELMET_DIAMOND) {
                    fogDensity = fogDensity / 1.5F;
                }*/
                //if (player.posY >= player.world.getSeaLevel() - 8) {
                fogDensity *= sandstormFog - (sandstormFog - sandstormFog * 1.0F);
                
                int light = player.world.getLightFor(EnumSkyBlock.SKY, new BlockPos(player.posX, player.posY + 1, player.posZ));
				if(light < 12)
				{
					if(light <= 0)
						fogDensity = 0.001F;
					else
						fogDensity = fogDensity / ((12-light) * 2);
				}
				
                //if(player.posY > 150)
                	fogDensity = (float) (fogDensity / (player.posY / 30));
                //}
                	
                if(AsmodeusClientEvent.inOxygenBlock) fogDensity = 0.05F;
                
                GlStateManager.setFogDensity(fogDensity);
            }
        }
    }
	/*
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event)
	{
		
		Entity entity = event.getEntity();
		World world = entity.world;
		int playerX = MathHelper.floor(entity.posX);
		int playerY = MathHelper.floor(entity.posY);
		int playerZ = MathHelper.floor(entity.posZ);

		if(world == null) return;
		if(!(entity instanceof EntityPlayerSP)) return;
		
		if (playerX == fogX && playerZ == fogZ && fogInit)
		{
			renderFog(event.getFogMode(), fogFarPlaneDistance, 0.75f);
			return;
		}
		
		
		fogInit = true;

		int distance = 20;
		float fpDistanceBiomeFog = 1F;
		float weightBiomeFog = 1;

		for (int x = -distance; x <= distance; ++x)
		{
			for (int z = -distance; z <= distance; ++z)
			{
				WorldProvider provider = world.provider;
				
				if (provider instanceof IProviderFog)
				{	
					float distancePart = ((IProviderFog)provider).getFogDensity(playerX + x, playerY, playerZ + z);
					
					
					
					int light = Minecraft.getMinecraft().world.getLightFor(EnumSkyBlock.SKY, Minecraft.getMinecraft().player.getPosition());
					//System.out.println(light);
					if(light < 6)
					{
						distancePart = 1.0F - ((15 - light) / 100) * light;
						
					}			
					float weightPart = 1;

					if (x == -distance)
					{
						double xDiff = 1 - (entity.posX - playerX);
						distancePart *= xDiff;
						weightPart *= xDiff;
					}
					else if (x == distance)
					{
						double xDiff = entity.posX - playerX;
						distancePart *= xDiff;
						weightPart *= xDiff;
					}

					if (z == -distance)
					{
						double zDiff = 1 - (entity.posZ - playerZ);
						distancePart *= zDiff;
						weightPart *= zDiff;
					}
					else if (z == distance)
					{
						double zDiff = entity.posZ - playerZ;
						distancePart *= zDiff;
						weightPart *= zDiff;
					}
					fpDistanceBiomeFog += distancePart;
					weightBiomeFog += weightPart;
				}
			}
		}

		float weightMixed = distance * 2 * distance * 2;
		float weightDefault = weightMixed - weightBiomeFog;
		float fpDistanceBiomeFogAvg = weightBiomeFog == 0 ? 0 : fpDistanceBiomeFog / weightBiomeFog;
		float farPlaneDistance = (fpDistanceBiomeFog * 240 + event.getFarPlaneDistance() * weightDefault) / weightMixed;
		float farPlaneDistanceScaleBiome = 0.1f * (1 - fpDistanceBiomeFogAvg) + 0.75f * fpDistanceBiomeFogAvg;
		float farPlaneDistanceScale = (farPlaneDistanceScaleBiome * weightBiomeFog + 0.75f * weightDefault) / weightMixed;
		fogX = entity.posX;
		fogZ = entity.posZ;
		fogFarPlaneDistance = Math.min(farPlaneDistance, event.getFarPlaneDistance());
		renderFog(event.getFogMode(), fogFarPlaneDistance, 0.75F);
	
	}*/
 	
	private static void renderFog(int fogMode, float farPlaneDistance, float farPlaneDistanceScale)
	{
		if (fogMode < 0)
		{
			GlStateManager.setFogStart(0F);
			GlStateManager.setFogEnd(farPlaneDistance);
		}
		else
		{	
			GlStateManager.setFogStart(farPlaneDistance * farPlaneDistanceScale);
			GlStateManager.setFogEnd(farPlaneDistance);
		}
		
	}

	private static Vec3d postProcessColor(World world, EntityLivingBase player, float r, float g, float b, double renderPartialTicks)
	{
		double darkScale = (player.lastTickPosY + (player.posY - player.lastTickPosY) * renderPartialTicks) * world.provider.getVoidFogYFactor();

		if (player.isPotionActive(MobEffects.BLINDNESS))
		{
			int duration = player.getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
			darkScale *= duration < 20 ? 1 - duration / 20f : 0;
		}
		if (darkScale < 1)
		{
			darkScale = darkScale < 0 ? 0 : darkScale * darkScale;
			r *= darkScale;
			g *= darkScale;
			b *= darkScale;
		}

		if (player.isPotionActive(MobEffects.NIGHT_VISION))
		{
			int duration = player.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();
			float brightness = duration > 200 ? 1 : 0.7f + MathHelper.sin((float)((duration - renderPartialTicks) * Math.PI * 0.2f)) * 0.3f;
			float scale = 1 / r;
			scale = Math.min(scale, 1 / g);
			scale = Math.min(scale, 1 / b);
			r = r * (1 - brightness) + r * scale * brightness;
			g = g * (1 - brightness) + g * scale * brightness;
			b = b * (1 - brightness) + b * scale * brightness;
		}
		if (Minecraft.getMinecraft().gameSettings.anaglyph)
		{
			float aR = (r * 30 + g * 59 + b * 11) / 100;
			float aG = (r * 30 + g * 70) / 100;
			float aB = (r * 30 + b * 70) / 100;
			r = aR;
			g = aG;
			b = aB;
		}
		return new Vec3d(r, g, b);
	}

	private static Vec3d getFogBlendColorWater(World world, EntityLivingBase playerEntity, int playerX, int playerY, int playerZ, double renderPartialTicks)
	{
		int distance = 2;
		float rBiomeFog = 0;
		float gBiomeFog = 0;
		float bBiomeFog = 0;

		for (int x = -distance; x <= distance; ++x)
		{
			for (int z = -distance; z <= distance; ++z)
			{
				Biome biome = world.getBiomeForCoordsBody(new BlockPos(playerX + x, playerEntity.posY,playerZ + z));
				int waterColorMult = biome.getWaterColorMultiplier();
				float rPart = (waterColorMult & 0xFF0000) >> 16;
				float gPart = (waterColorMult & 0x00FF00) >> 8;
				float bPart = waterColorMult & 0x0000FF;

				if (x == -distance)
				{
					double xDiff = 1 - (playerEntity.posX - playerX);
					rPart *= xDiff;
					gPart *= xDiff;
					bPart *= xDiff;
				}
				else if (x == distance)
				{
					double xDiff = playerEntity.posX - playerX;
					rPart *= xDiff;
					gPart *= xDiff;
					bPart *= xDiff;
				}
		
				if (z == -distance)
				{
					double zDiff = 1 - (playerEntity.posZ - playerZ);
					rPart *= zDiff;
					gPart *= zDiff;
					bPart *= zDiff;
				}
				else if (z == distance)
				{
					double zDiff = playerEntity.posZ - playerZ;
					rPart *= zDiff;
					gPart *= zDiff;
					bPart *= zDiff;
				}
		
				rBiomeFog += rPart;
				gBiomeFog += gPart;
				bBiomeFog += bPart;
			}
		}

		rBiomeFog /= 255f;
		gBiomeFog /= 255f;
		bBiomeFog /= 255f;

		float weight = distance * 2 * distance * 2;
		float respirationLevel = EnchantmentHelper.getRespirationModifier(playerEntity) * 0.2F;
		float rMixed = (rBiomeFog * 0.02f + respirationLevel) / weight;
		float gMixed = (gBiomeFog * 0.02f + respirationLevel) / weight;
		float bMixed = (bBiomeFog * 0.2f + respirationLevel) / weight;
		return postProcessColor(world, playerEntity, rMixed, gMixed, bMixed, renderPartialTicks);
	}

	private static Vec3d getFogBlendColour(World world, EntityLivingBase playerEntity, int playerX, int playerY, int playerZ, float defR, float defG, float defB, double renderPartialTicks)
	{
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		int[] ranges = ForgeModContainer.blendRanges;
		int distance = 6;

		if (settings.fancyGraphics && settings.renderDistanceChunks >= 0 && settings.renderDistanceChunks < ranges.length)
		{
			distance = ranges[settings.renderDistanceChunks];
		}

		float rBiomeFog = 0;
		float gBiomeFog = 0;
		float bBiomeFog = 0;
		float weightBiomeFog = 0;

		for (int x = -distance; x <= distance; ++x)
		{
			for (int z = -distance; z <= distance; ++z)
			{
				WorldProvider provider = world.provider;
				//BiomeGenBase biome = world.getBiomeGenForCoords(playerX + x, playerZ + z);

				if (provider instanceof IProviderFog)
				{
					IProviderFog biomeFog = (IProviderFog)provider;
					Vec3d fogColor = provider.getFogColor((float) renderPartialTicks, 0.0F);
					
					int fogColour = Utils.getIntColor(Math.round((float)fogColor.x * 255), Math.round((float)fogColor.y * 255), Math.round((float)fogColor.z * 255));//biomeFog.getFogColor(playerX + x, playerY, playerZ + z);
					float rPart = (fogColour & 0xFF0000) >> 16;
					float gPart = (fogColour & 0x00FF00) >> 8;
					float bPart = fogColour & 0x0000FF;					
				
					float weightPart = 1;

					if (x == -distance)
					{
						double xDiff = 1 - (playerEntity.posX - playerX);
						rPart *= xDiff;
						gPart *= xDiff;
						bPart *= xDiff;
						weightPart *= xDiff;
					}
					else if (x == distance)
					{
						double xDiff = playerEntity.posX - playerX;
						rPart *= xDiff;
						gPart *= xDiff;
						bPart *= xDiff;
						weightPart *= xDiff;
					}
			
					if (z == -distance)
					{
						double zDiff = 1 - (playerEntity.posZ - playerZ);
						rPart *= zDiff;
						gPart *= zDiff;
						bPart *= zDiff;
						weightPart *= zDiff;
					}
					else if (z == distance)
					{
						double zDiff = playerEntity.posZ - playerZ;
						rPart *= zDiff;
						gPart *= zDiff;
						bPart *= zDiff;
						weightPart *= zDiff;
					}
			
					float temp = (float) (playerEntity.posY / 1000);
					
					rBiomeFog += rPart;
					gBiomeFog += gPart;
					bBiomeFog += bPart;
					weightBiomeFog += weightPart;
				}
			}
		}

		if (weightBiomeFog == 0 || distance == 0)
		{
			return new Vec3d((double)defR, (double)defG, (double)defB);
		}

		rBiomeFog /= 255f;
		gBiomeFog /= 255f;
		bBiomeFog /= 255f;

		// Calculate day / night / weather scale for BiomeFog component
		float celestialAngle = world.getCelestialAngle((float)renderPartialTicks);
		float baseScale = MathHelper.clamp(MathHelper.cos(celestialAngle * (float)Math.PI * 2.0F) * 2.0F + 0.5F, 0, 1);
		float rScale = baseScale * 0.94F + 0.06F;
		float gScale = baseScale * 0.94F + 0.06F;
		float bScale = baseScale * 0.91F + 0.09F;
		float rainStrength = world.getRainStrength((float)renderPartialTicks);
		
		if (rainStrength > 0)
		{
			rScale *= 1 - rainStrength * 0.5f;
			gScale *= 1 - rainStrength * 0.5f;
			bScale *= 1 - rainStrength * 0.4f;
		}

		float thunderStrength = world.getThunderStrength((float) renderPartialTicks);

		if (thunderStrength > 0)
		{
			rScale *= 1 - thunderStrength * 0.5f;
			gScale *= 1 - thunderStrength * 0.5f;
			bScale *= 1 - thunderStrength * 0.5f;
		}
		
		

		// Apply post-processing to BiomeFog component.  Default color was already processed by Vanilla.
		rBiomeFog *= rScale / weightBiomeFog;
		gBiomeFog *= gScale / weightBiomeFog;
		bBiomeFog *= bScale / weightBiomeFog;

		Vec3d processedColor = postProcessColor(world, playerEntity, rBiomeFog, gBiomeFog, bBiomeFog, renderPartialTicks);
		rBiomeFog = (float)processedColor.x;
		gBiomeFog = (float)processedColor.y;
		bBiomeFog = (float)processedColor.z;

		// Mix default fog component with BiomeFog component
		float weightMixed = distance * 2 * distance * 2;
		float weightDefault = weightMixed - weightBiomeFog;

		double x = (rBiomeFog * weightBiomeFog + defR * weightDefault) / weightMixed;
		double y = (gBiomeFog * weightBiomeFog + defG * weightDefault) / weightMixed;
		double z = (bBiomeFog * weightBiomeFog + defB * weightDefault) / weightMixed;
		return new Vec3d(x,y,z);
	}
}