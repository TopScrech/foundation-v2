package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.GCItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


/**
 * An advanced block class that is to be extended for wrenching capabilities.
 */
public abstract class BlockAdvanced extends Block
{
    public BlockAdvanced(Material material)
    {
        super(material);
        this.setHardness(0.6f);
        this.setResistance(2.5F);
        //A default blast resistance for GC machines and tiles, similar to a bookshelf
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (hand != EnumHand.MAIN_HAND)
        {
            return false;
        }

        ItemStack heldItem = playerIn.getHeldItem(hand);

        if (this.useWrench(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ))
        {
            return true;
        }

        if (playerIn.isSneaking())
        {
            if (this.onSneakMachineActivated(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ))
            {
                return true;
            }
        }

        return this.onMachineActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    protected boolean useWrench(World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        /**
         * Check if the player is holding a wrench or an electric item. If so,
         * call the wrench event.
         */
        if (this.isUsableWrench(playerIn, heldItem, pos))
        {
            this.damageWrench(playerIn, heldItem, pos);

            if (playerIn.isSneaking())
            {
                if (this.onSneakUseWrench(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ))
                {
                    playerIn.swingArm(hand);
                    return true;
                }
            }

            if (this.onUseWrench(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ))
            {
                playerIn.swingArm(hand);
                return true;
            }
        }

        return false;
    }

    public boolean isUsableWrench(EntityPlayer entityPlayer, ItemStack itemStack, BlockPos pos)
    {
        return entityPlayer != null && itemStack != null && itemStack.getItem() == GCItems.wrench;
    }

    public boolean damageWrench(EntityPlayer entityPlayer, ItemStack itemStack, BlockPos pos)
    {
        if (this.isUsableWrench(entityPlayer, itemStack, pos))
        {
            ((micdoodle8.mods.galacticraft.core.items.ItemUniversalWrench) GCItems.wrench).wrenchUsed(entityPlayer, pos);
            return true;
        }

        return false;
    }

    /**
     * Called when the machine is right clicked by the player
     *
     * @return True if something happens
     */
    public boolean onMachineActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    /**
     * Called when the machine is being wrenched by a player while sneaking.
     *
     * @return True if something happens
     */
    public boolean onSneakMachineActivated(World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    /**
     * Called when a player uses a wrench on the machine
     *
     * @return True if some happens
     */
    public boolean onUseWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    /**
     * Called when a player uses a wrench on the machine while sneaking. Only
     * works with the UE wrench.
     *
     * @return True if some happens
     */
    public boolean onSneakUseWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return this.onUseWrench(world, pos, entityPlayer, hand, heldItem, side, hitX, hitY, hitZ);
    }
    
    public void rotate6Ways(World world, BlockPos pos)
    {
        int metadata = this.getMetaFromState(world.getBlockState(pos));
        int metaDir = ((metadata & 7) + 1) % 6;
        //DOWN->UP->NORTH->*EAST*->*SOUTH*->WEST
        //0->1 1->2 2->5 3->4 4->0 5->3 
        if (metaDir == 3) //after north
        {
            metaDir = 5;
        }
        else if (metaDir == 0)
        {
            metaDir = 3;
        }
        else if (metaDir == 5)
        {
            metaDir = 0;
        }
            
        world.setBlockState(pos, this.getStateFromMeta(metaDir), 3);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return this.isNormalCube(base_state, world, pos);
    }
}
