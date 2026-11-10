package asmodeuscore.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.mjr.planetprogression.api.research.ResearchHooksSP;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UtilsGC {
	
	public enum FluidType
	{
		STILL,
		FLOWING
	}
	
	public static TextureAtlasSprite missingIcon;
	private static TextureMap texMap = null;
	
	public static void initFluidTextures(TextureMap map) 
	{
		missingIcon = map.getMissingSprite();

		texMap = map;
	}
	
	public static TextureAtlasSprite getBlockTexture(Block block) {
		return getBlockTexture(block, "");		
	}
	
	public static TextureAtlasSprite getBlockTexture(Block block, String addpath) 
	{
		if(block == null)
		{
			return missingIcon;
		}
		
		ResourceLocation spriteLocation;
		spriteLocation = block.getRegistryName();		
		
		TextureAtlasSprite sprite = texMap.getAtlasSprite(spriteLocation.getNamespace() + ":blocks/" + spriteLocation.getPath() + addpath);

		return sprite != null ? sprite : missingIcon;
	}
	
	public static TextureAtlasSprite getFluidTexture(Fluid fluid, FluidType type) 
	{
		if(fluid == null || type == null)
		{
			return missingIcon;
		}
		
		ResourceLocation spriteLocation;
		if (type == FluidType.STILL){
			spriteLocation = fluid.getStill();
		} else {
			spriteLocation = fluid.getFlowing();
		}

		TextureAtlasSprite sprite = texMap.getTextureExtry(spriteLocation.toString());
		
		return sprite != null ? sprite : missingIcon;
	}
	
	public static void drawFluid(int xPos, int yPos, int scale, FluidStack fluid)
	{
		if(fluid == null)
		{
			return;
		}

		int guiWidth = 0;//(width - xSize) / 2;
		int guiHeight = 0;//(height - ySize) / 2;

		int start = 0;

		while(true)
		{
			int renderRemaining;

			if(scale > 16)
			{
				renderRemaining = 16;
				scale -= 16;
			}
			else {
				renderRemaining = scale;
				scale = 0;
			}

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			drawTexturedModalRect(guiWidth + xPos, guiHeight + yPos + 58 - renderRemaining - start, getFluidTexture(fluid.getFluid(), FluidType.STILL), 16, 16 - (16 - renderRemaining));
			start+=16;

			if(renderRemaining == 0 || scale == 0)
			{
				break;
			}
		}

	}
	
	public static void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn)
    {
		double zLevel = 0.0;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(xCoord + 0), (double)(yCoord + heightIn), (double)zLevel).tex((double)textureSprite.getMinU(), (double)textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos((double)(xCoord + widthIn), (double)(yCoord + heightIn), (double)zLevel).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos((double)(xCoord + widthIn), (double)(yCoord + 0), (double)zLevel).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMinV()).endVertex();
        bufferbuilder.pos((double)(xCoord + 0), (double)(yCoord + 0), (double)zLevel).tex((double)textureSprite.getMinU(), (double)textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }
	
	public static int getIntColor(int r, int g, int b, int a) {
		
		int A = a * 256 * 256 * 256;
		int R = r * 256 * 256;
		int G = g * 256;
		int B = b;
		int color = A+R+G+B;	
		
	    return color - 16777216;
	}
	
	public static float calculateCelestialAngle(long worldtime, float ticks, float daylenght)
    {
        int j = (int)(worldtime % daylenght);
        float f1 = ((float)j + ticks) / daylenght - 0.25F;

        if (f1 < 0.0F)
        {
            ++f1;
        }

        if (f1 > 1.0F)
        {
            --f1;
        }

        float f2 = f1;
        f1 = 1.0F - (float)((Math.cos((double)f1 * Math.PI) + 1.0D) / 2.0D);
        f1 = f2 + (f1 - f2) / 3.0F;
        return f1;
    }
		
	public static List<Integer> getPossibleDimensionsForSpaceshipTier(int tier, EntityPlayerMP playerBase)
    {
        List<Integer> temp = new ArrayList<Integer>();

        if (!ConfigManagerCore.disableRocketsToOverworld)
        {
            temp.add(ConfigManagerCore.idDimensionOverworld);
        }

        for (Integer element : WorldUtil.registeredPlanets)
        {
            if (element == ConfigManagerCore.idDimensionOverworld)
            {
                continue;
            }
            WorldProvider provider = WorldUtil.getProviderForDimensionServer(element);

            if (provider != null)
            {
                if (provider instanceof IGalacticraftWorldProvider)
                {
                    if (((IGalacticraftWorldProvider) provider).canSpaceshipTierPass(tier))
                    {
                        temp.add(element);
                    }
                }
                else
                {
                    temp.add(element);
                }
            }
        }

        for (Integer element : WorldUtil.registeredSpaceStations.keySet())
        {
            final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.world, element, null);

            if (!ConfigManagerCore.spaceStationsRequirePermission || data.getAllowedAll() || data.getAllowedPlayers().contains(PlayerUtil.getName(playerBase)) || ArrayUtils.contains(playerBase.server.getPlayerList().getOppedPlayerNames(), playerBase.getName()))
            {
                //Satellites always reachable from their own homeworld or from its other satellites
                if (playerBase != null)
                {
                    int currentWorld = playerBase.dimension;
                    //Player is on homeworld
                    if (currentWorld == data.getHomePlanet())
                    {
                        temp.add(element);
                        continue;
                    }
                    if (playerBase.world.provider instanceof IOrbitDimension)
                    {
                        //Player is currently on another space station around the same planet
                        final SpaceStationWorldData dataCurrent = SpaceStationWorldData.getStationData(playerBase.world, playerBase.dimension, null);
                        if (dataCurrent.getHomePlanet() == data.getHomePlanet())
                        {
                            temp.add(element);
                            continue;
                        }
                    }
                }

                //Testing dimension is a satellite, but with a different homeworld - test its tier
                WorldProvider homeWorld = WorldUtil.getProviderForDimensionServer(data.getHomePlanet());

                if (homeWorld != null)
                {
                    if (homeWorld instanceof IGalacticraftWorldProvider)
                    {
                        if (((IGalacticraftWorldProvider) homeWorld).canSpaceshipTierPass(tier))
                        {
                            temp.add(element);
                        }
                    }
                    else
                    {
                        temp.add(element);
                    }
                }
            }
        }

        return temp;
    }
	
	 public static HashMap<String, Integer> getArrayOfPossibleDimensions(int tier, EntityPlayerMP playerBase)
	    {
	        List<Integer> ids = getPossibleDimensionsForSpaceshipTier(tier, playerBase);
	        final HashMap<String, Integer> map = new HashMap<String, Integer>(ids.size(), 1F);

	        for (Integer id : ids)
	        {
	            CelestialBody celestialBody = WorldUtil.getReachableCelestialBodiesForDimensionID(id);

	            //It's a space station
	            if (id > 0 && celestialBody == null)
	            {
	                celestialBody = GalacticraftCore.satelliteSpaceStation;
	                //This no longer checks whether a WorldProvider can be created, for performance reasons (that causes the dimension to load unnecessarily at map building stage)
	                if (playerBase != null)
	                {
	                    final SpaceStationWorldData data = SpaceStationWorldData.getStationData(playerBase.world, id, null);
	                    map.put(celestialBody.getName() + "$" + data.getOwner() + "$" + data.getSpaceStationName() + "$" + id + "$" + data.getHomePlanet(), id);
	                }
	            }
	            else
	            //It's a planet or moon
	            {
	                if (celestialBody == GalacticraftCore.planetOverworld)
	                {
	                    map.put(celestialBody.getName(), id);
	                }
	                else
	                {
	                    WorldProvider provider = WorldUtil.getProviderForDimensionServer(id);
	                    if (celestialBody != null && provider != null)
	                    {
	                        if (provider instanceof IGalacticraftWorldProvider && !(provider instanceof IOrbitDimension) || GCCoreUtil.getDimensionID(provider) == 0)
	                        {
	                            map.put(celestialBody.getName(), GCCoreUtil.getDimensionID(provider));
	                        }
	                    }
	                }
	            }
	        }

	        ArrayList<CelestialBody> cBodyList = new ArrayList<CelestialBody>();
	        cBodyList.addAll(GalaxyRegistry.getRegisteredPlanets().values());
	        cBodyList.addAll(GalaxyRegistry.getRegisteredMoons().values());

	        for (CelestialBody body : cBodyList)
	        {
	            if (!body.getReachable())
	            {
	                map.put(body.getLocalizedName() + "*", body.getDimensionID());
	            }
	        }

	        WorldUtil.celestialMapCache.put(playerBase, map);
	        return map;
	    }

	    /**
	     * Get the cached version of getArrayOfPossibleDimensions() to reduce server load + unwanted dimension loading
	     * The cache will be updated every time the 'proper' version of getArrayOfPossibleDimensions is called.
	     *
	     * @param tier       - the rocket tier to test
	     * @param playerBase - the player who will be riding the rocket (needed for checking space station permissions)
	     * @return a Map of the names of the dimension vs. the dimension IDs
	     */
	    public static HashMap<String, Integer> getArrayOfPossibleDimensionsAgain(int tier, EntityPlayerMP playerBase)
	    {
	        HashMap<String, Integer> map = WorldUtil.celestialMapCache.get(playerBase);
	        if (map != null)
	        {
	            return map;
	        }
	        return getArrayOfPossibleDimensions(tier, playerBase);
	    }
	    
	    @SideOnly(Side.CLIENT)
		@Optional.Method(modid = "planetprogression")
		public static boolean hasUnlocks (EntityPlayerSP player, SolarSystem body) {
			return ResearchHooksSP.hasUnlockedBodiesInSystem(player, body);
		}

		@SideOnly(Side.CLIENT)
		@Optional.Method(modid = "planetprogression")
		public static boolean isReasearched (EntityPlayerSP player, CelestialBody body) {
			return ResearchHooksSP.hasUnlockedCelestialBody(player, body);
		}

}
