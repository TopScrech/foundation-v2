package asmodeuscore.core.utils;

import asmodeuscore.api.item.IItemPressurized;
import org.lwjgl.opengl.GL11;

import asmodeuscore.api.dimension.IProviderWeather;
import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData;
import asmodeuscore.core.utils.worldengine.WE_Biome;
import asmodeuscore.core.utils.worldengine2.world.properties.WE_BiomeProperties;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

public class Utils {
	
	public static Biome WE_Biome = new WE_Biome(new BiomeProperties("-=|WorldEngine|=-").setBaseHeight(1.2F));
	public static Biome WE_Biome_2 = new asmodeuscore.core.utils.worldengine2.world.biome.WE_Biome(new WE_BiomeProperties());
	
	
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
	
	public static boolean inWater(EntityPlayer player)
    {
    	BlockPos pos = player.getPosition();
    	
    	return player.world.getBlockState(pos.up()).getMaterial() == Material.WATER;
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
	
	public static void drawTexturedModalRect(float x, float y, float width, float height, float u, float v, float uWidth, float vHeight, boolean invertX, boolean invertY, float texSizeX, float texSizeY)
    {
		float zLevel = 0.0F;
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        float texModX = 1F / texSizeX;
        float texModY = 1F / texSizeY;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        float height0 = invertY ? 0 : vHeight;
        float height1 = invertY ? vHeight : 0;
        float width0 = invertX ? uWidth : 0;
        float width1 = invertX ? 0 : uWidth;
        worldRenderer.pos(x, y + height, zLevel).tex((u + width0) * texModX, (v + height0) * texModY).endVertex();
        worldRenderer.pos(x + width, y + height, zLevel).tex((u + width1) * texModX, (v + height0) * texModY).endVertex();
        worldRenderer.pos(x + width, y, zLevel).tex((u + width1) * texModX, (v + height1) * texModY).endVertex();
        worldRenderer.pos(x, y, zLevel).tex((u + width0) * texModX, (v + height1) * texModY).endVertex();
        tessellator.draw();
    }
	
	public static int getIntColor(int r, int g, int b) {
		return getIntColor(r, g, b, 255);
	}
	
	public static int getIntColorWHC(int r, int g, int b, int a)
	{
		return getIntColor(r, g, b, a) + 16777216;
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
		if(daylenght == 0)
			daylenght = Long.MAX_VALUE;

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
	
	public static float interpolateRotation (float a1, float a2, float p) {
		float angle = a2 - a1;
		angle = angle < -180F ? angle += 360F : angle;
		return a1 + (p * (angle = angle >= 180F ? angle -= 360F : angle));
	}
	
	public static Side getEffectiveSide()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER || Thread.currentThread().getName().startsWith("Netty Epoll Server IO"))
        {
            return Side.SERVER;
        }

        return Side.CLIENT;
    }
	
	public static IBlockState getBlock(String par1)
	{
		String[] meta = par1.split(":");
		Block blocks = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(meta[0] + ":" + meta[1]));
		if (meta.length > 2)
		{
			return blocks.getStateFromMeta(Integer.parseInt(meta[2]));
		}
		return blocks.getDefaultState();
	}
	
	public static String localeBoolean(boolean bol)
	{
		return bol ? GCCoreUtil.translate("gui.message.yes") : GCCoreUtil.translate("gui.message.no");		
	}

	public static boolean getAtmoArmor(EntityPlayer player)
	{
		for(int i = 0; i < 3; i++)
			if(player.inventory.armorItemInSlot(i).isEmpty() || !(player.inventory.armorItemInSlot(i).getItem() instanceof IItemPressurized))
				return false;
		return true;
	}

	public static boolean isDustStorm(World world) {
		if (!world.isRemote && world.getTotalWorldTime() % 20 == 0 && world.provider instanceof IProviderWeather
				&& world.provider instanceof IGalacticraftWorldProvider) {

			IGalacticraftWorldProvider gcprovider = (IGalacticraftWorldProvider) world.provider;
			String name = gcprovider.getCelestialBody().getName();
			DustStormSaveData msd = DustStormSaveData.get(world, name);

			return msd.isDustStorm();

		}
		return false;
	}


}
