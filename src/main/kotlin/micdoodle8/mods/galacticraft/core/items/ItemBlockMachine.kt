package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine2
import micdoodle8.mods.galacticraft.core.blocks.BlockMachineTiered
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.block.Block
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBlockMachine constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemstack: ItemStack): String {
        var index: Int = 0
        val typenum: Int = itemstack.getItemDamage() and 12

        if (this.getBlock() === GCBlocks.machineBase) {
            index = typenum / 4
        } else if (this.getBlock() === GCBlocks.machineTiered) {
            if (typenum == BlockMachineTiered.ELECTRIC_FURNACE_METADATA) {
                return "tile.machine.2"
            } else if (typenum == BlockMachineTiered.STORAGE_MODULE_METADATA) {
                return "tile.machine.1"
            }

            //Tier 2 versions of the same
            if (typenum == 8 + BlockMachineTiered.ELECTRIC_FURNACE_METADATA) {
                return "tile.machine.7"
            } else if (typenum == 8 + BlockMachineTiered.STORAGE_MODULE_METADATA) {
                return "tile.machine.8"
            }
        } else if (this.getBlock() === GCBlocks.machineBase2) {
            if (typenum == BlockMachine2.OXYGEN_STORAGE_MODULE_METADATA) {
                index = 6
            } else if (typenum == BlockMachine2.CIRCUIT_FABRICATOR_METADATA) {
                index = 5
            } else if (typenum == BlockMachine2.ELECTRIC_COMPRESSOR_METADATA) {
                index = 4
            } else if (typenum == BlockMachine2.DECONSTRUCTOR_METADATA) {
                index = 10
            }
        } else  //machineBase3
        {
            index = 9
        }

        return this.getBlock().getTranslationKey() + "." + index
    }

    override fun onCreated(stack: ItemStack, world: World, player: EntityPlayer) {
        if (!world.isRemote) {
            return
        }

        val typenum: Int = stack.getItemDamage() and 12

        //The player could be a FakePlayer made by another mod e.g. LogisticsPipes
        if (player is EntityPlayerSP) {
            if (this.getBlock() === GCBlocks.machineBase && typenum == BlockMachine.COMPRESSOR_METADATA) {
                ClientProxyCore.playerClientHandler.onBuild(1, player)
            } else if (this.getBlock() === GCBlocks.machineBase2 && typenum == BlockMachine2.CIRCUIT_FABRICATOR_METADATA) {
                ClientProxyCore.playerClientHandler.onBuild(2, player)
            }
        }
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
