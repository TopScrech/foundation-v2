package asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

public class DungeonConfiguration
{
    private IBlockState brickBlock;
    private IBlockState topBlock;
    private IBlockState floorBlock;
    private int yPosition;
    private int hallwayLengthMin;
    private int hallwayLengthMax;
    private int hallwayHeight;
    private int roomHeight;
    private Class<?> corridor;
    private Class<?> emptyRoom;
    private Class<?> spawnerRoom;
    private Class<?> chestRoom;
    private Class<?> bossRoom;
    private Class<?> treasureRoom;

    public DungeonConfiguration()
    {
    }

    public DungeonConfiguration(IBlockState brickBlock, IBlockState topBlock, IBlockState floorBlock, int yPosition, int hallwayLengthMin, int hallwayLengthMax, int hallwayHeight, int roomHeight, Class<?> corridor, Class<?> emptyRoom, Class<?> spawnerRoom, Class<?> chestRoom, Class<?> bossRoom, Class<?> treasureRoom)
    {
        this.brickBlock = brickBlock;
        this.topBlock = topBlock;
        this.floorBlock = floorBlock;
        
        this.yPosition = yPosition;
        this.hallwayLengthMin = hallwayLengthMin;
        this.hallwayLengthMax = hallwayLengthMax;
        this.hallwayHeight = hallwayHeight;
        this.roomHeight = roomHeight;
        this.corridor = corridor;
        this.emptyRoom = emptyRoom;
        this.spawnerRoom = spawnerRoom;
        this.chestRoom = chestRoom;
        this.bossRoom = bossRoom;
        this.treasureRoom = treasureRoom;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setString("brickBlock", Block.REGISTRY.getNameForObject(this.brickBlock.getBlock()).toString());
        tagCompound.setInteger("brickBlockMeta", this.brickBlock.getBlock().getMetaFromState(this.brickBlock));
		
		if(topBlock != null) {
			tagCompound.setString("topBlock", Block.REGISTRY.getNameForObject(this.topBlock.getBlock()).toString());
			tagCompound.setInteger("topBlockMeta", this.topBlock.getBlock().getMetaFromState(this.topBlock));
		}
		if(floorBlock != null) {
			tagCompound.setString("floorBlock", Block.REGISTRY.getNameForObject(this.floorBlock.getBlock()).toString());
			tagCompound.setInteger("floorBlockMeta", this.floorBlock.getBlock().getMetaFromState(this.floorBlock));
		}
        
        tagCompound.setInteger("yPosition", this.yPosition);
        tagCompound.setInteger("hallwayLengthMin", this.hallwayLengthMin);
        tagCompound.setInteger("hallwayLengthMax", this.hallwayLengthMax);
        tagCompound.setInteger("hallwayHeight", this.hallwayHeight);
        tagCompound.setInteger("roomHeight", this.roomHeight);
       
		if(this.corridor != null)
			tagCompound.setString("corridor", this.corridor.getName());
		if(this.emptyRoom != null)
			tagCompound.setString("emptyRoom", this.emptyRoom.getName());
		if(this.spawnerRoom != null)
			tagCompound.setString("spawnerRoom", this.spawnerRoom.getName());
		if(this.chestRoom != null)
			tagCompound.setString("chestRoom", this.chestRoom.getName());
        
        tagCompound.setString("bossRoom", this.bossRoom.getName());
        tagCompound.setString("treasureRoom", this.treasureRoom.getName());
        return tagCompound;
    }

    public void readFromNBT(NBTTagCompound tagCompound)
    {
        try
        {
            this.brickBlock = Block.getBlockFromName(tagCompound.getString("brickBlock")).getStateFromMeta(tagCompound.getInteger("brickBlockMeta"));
            if(topBlock != null) 
				this.topBlock = Block.getBlockFromName(tagCompound.getString("topBlock")).getStateFromMeta(tagCompound.getInteger("topBlockMeta"));
            if(floorBlock != null) 
				this.floorBlock = Block.getBlockFromName(tagCompound.getString("floorBlock")).getStateFromMeta(tagCompound.getInteger("floorBlockMeta"));
            this.yPosition = tagCompound.getInteger("yPosition");
            this.hallwayLengthMin = tagCompound.getInteger("hallwayLengthMin");
            this.hallwayLengthMax = tagCompound.getInteger("hallwayLengthMax");
            this.hallwayHeight = tagCompound.getInteger("hallwayHeight");
            this.roomHeight = tagCompound.getInteger("roomHeight");
			if(this.corridor != null)
				this.corridor = Class.forName(tagCompound.getString("corridor"));
			if(this.emptyRoom != null)
				this.emptyRoom = Class.forName(tagCompound.getString("emptyRoom"));
			if(this.spawnerRoom != null)
				this.spawnerRoom = Class.forName(tagCompound.getString("spawnerRoom"));
			if(this.chestRoom != null)
				this.chestRoom = Class.forName(tagCompound.getString("chestRoom"));
            this.bossRoom = Class.forName(tagCompound.getString("bossRoom"));
            this.treasureRoom = Class.forName(tagCompound.getString("treasureRoom"));
        }
        catch (Exception e)
        {
            System.err.println("Failed to read dungeon configuration from NBT");
            e.printStackTrace();
        }
    }

    public IBlockState getBrickBlock()
    {
        return brickBlock;
    }
    
    public IBlockState getOtherBlock(boolean floor)
    {
    	if(floor) return floorBlock;
        return topBlock;
    }

    public int getYPosition()
    {
        return yPosition;
    }

    public int getHallwayLengthMin()
    {
        return hallwayLengthMin;
    }

    public int getHallwayLengthMax()
    {
        return hallwayLengthMax;
    }

    public int getHallwayHeight()
    {
        return hallwayHeight;
    }

    public int getRoomHeight()
    {
        return roomHeight;
    }

    public Class<?> getCorridor()
    {
        return corridor;
    }
    
    public Class<?> getEmptyRoom()
    {
        return emptyRoom;
    }
    
    public Class<?> getSpawnerRoom()
    {
        return spawnerRoom;
    }
    
    public Class<?> getChestRoom()
    {
        return chestRoom;
    }
    
    public Class<?> getBossRoom()
    {
        return bossRoom;
    }

    public Class<?> getTreasureRoom()
    {
        return treasureRoom;
    }
}
