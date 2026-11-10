package asmodeuscore.core.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import asmodeuscore.api.blocks.ICustomLeaves;
import asmodeuscore.core.utils.worldengine.WE_Biome;
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider;
import asmodeuscore.core.utils.worldengine.WE_WorldProvider;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ColorBlockHandler {

	private static List<IBlockState> blocks = new ArrayList<IBlockState>();
	private static List<IBlockState> leaves = new ArrayList<IBlockState>();
	private static List<IBlockState> water_blocks = new ArrayList<IBlockState>();
	private static Map<IBlockState, Integer> color_blocks = new HashMap<IBlockState, Integer>();
		
	public static World world;
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerBlockColourHandlers(ColorHandlerEvent.Block event) {
		final BlockColors blockColors = event.getBlockColors();
		
		final IBlockColor grassColourHandler = (state, blockAccess, pos, tintIndex) -> {
			if (blockAccess != null && pos != null) {			

				if(world != null) {
					if(world.provider instanceof WE_WorldProvider)
					{				
						WE_ChunkProvider chunk = ((WE_WorldProvider)world.provider).chunk_provider;
						if(chunk != null && ((WE_WorldProvider)world.provider).isColorWorld())
							return WE_Biome.getBiomeAt(chunk, (long)pos.getX(), (long)pos.getZ()).biomeBlockGrassColor;		
				
					}					
				}
				return BiomeColorHelper.getGrassColorAtPos(blockAccess, pos);
							
			}
			return ColorizerGrass.getGrassColor(0.5D, 1.0D);
		};
		
		for(IBlockState block : blocks)
			blockColors.registerBlockColorHandler(grassColourHandler, block.getBlock());		
		
		final IBlockColor leavesColourHandler = (state, blockAccess, pos, tintIndex) -> {
			if (blockAccess != null && pos != null) {
				
				if(world != null && world.provider instanceof WE_WorldProvider)
				{	
					WE_ChunkProvider chunk = ((WE_WorldProvider)world.provider).chunk_provider;
						
					if(chunk != null)
						return WE_Biome.getBiomeAt(chunk, (long)pos.getX(), (long)pos.getZ()).biomeBlockLeavesColor;		
					
				}
					
				if(state.getBlock() == Blocks.LEAVES)
				{
					BlockPlanks.EnumType blockplanks$enumtype = (BlockPlanks.EnumType)state.getValue(BlockOldLeaf.VARIANT);
		
		            if (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE)
		            {
		            	return ColorizerFoliage.getFoliageColorPine();
		            }
		            else if (blockplanks$enumtype == BlockPlanks.EnumType.BIRCH)
					{
						return ColorizerFoliage.getFoliageColorBirch();
					} else {
						return BiomeColorHelper.getFoliageColorAtPos(blockAccess, pos);
					}
				}

				if (state.getBlock() == Blocks.LEAVES2) {
					return BiomeColorHelper.getFoliageColorAtPos(blockAccess, pos);
				}
				
			}
			if(state.getBlock() instanceof ICustomLeaves)
				return ((ICustomLeaves)state.getBlock()).getDefaultLeavesColor();
			
			return ColorizerFoliage.getFoliageColorBasic();
		};
		
		for(IBlockState block : leaves)
			blockColors.registerBlockColorHandler(leavesColourHandler, block.getBlock());
		
		for(Entry<IBlockState, Integer> color_blocks : color_blocks.entrySet())
		{
			IBlockColor colorHandler = (state, blockAccess, pos, tintIndex) -> {
				return color_blocks.getValue();
			};
			blockColors.registerBlockColorHandler(colorHandler, color_blocks.getKey().getBlock());
		}
		
		final IBlockColor waterColourHandler = (state, blockAccess, pos, tintIndex) -> {
			
			if (blockAccess != null && pos != null) {				
				
				if(world != null && world.provider instanceof WE_WorldProvider)
				{		
					WE_ChunkProvider chunk = ((WE_WorldProvider)world.provider).chunk_provider;
					
					if(chunk != null) {
						WE_Biome biome =  WE_Biome.getBiomeAt(chunk, (long)pos.getX(), (long)pos.getZ());
						if(biome != null)
							return biome.biomeBlockWaterColor;					
						//return WE_Biome.getBiomeAt(chunk, (long)pos.getX(), (long)pos.getZ()).biomeBlockWaterColor;
					}
				}			
				
				return BiomeColorHelper.getWaterColorAtPos(blockAccess, pos);
			}

			return ColorizerGrass.getGrassColor(0.5D, 1.0D);
		};
		
		for(IBlockState block : water_blocks)
			blockColors.registerBlockColorHandler(waterColourHandler, block.getBlock());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerItemColourHandlers(ColorHandlerEvent.Item event) {
		final BlockColors blockColors = event.getBlockColors();
		final ItemColors itemColors = event.getItemColors();
		
		
		if(itemColors != null) {
			final IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
				if(stack != null || stack != ItemStack.EMPTY) {
					if(stack.getItem() instanceof ItemBlock) {
						final IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
						return blockColors.colorMultiplier(state, null, null, tintIndex);
					}
					return itemColors.colorMultiplier(stack, tintIndex);
				}
				return -1;
			};
			
			for(IBlockState block : blocks)
				itemColors.registerItemColorHandler(itemBlockColourHandler, block.getBlock());	
			
			for(IBlockState block : leaves)
				itemColors.registerItemColorHandler(itemBlockColourHandler, block.getBlock());	
		}
	}
	
	public static void addBlock(IBlockState block)
	{
		if(!blocks.contains(block))
			blocks.add(block);
	}
	
	public static void addLeavesBlock(IBlockState block)
	{
		if(!leaves.contains(block))
			leaves.add(block);
	}	
	
	public static void addBlockWithColor(IBlockState block, int color)
	{
		if(!color_blocks.containsKey(block))
			color_blocks.put(block, color);
	}
	
	public static void addWaterBlock(IBlockState block)
	{
		if(!water_blocks.contains(block))
			water_blocks.add(block);
	}
}
