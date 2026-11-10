package micdoodle8.mods.galacticraft.planets.asteroids.items

import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase
import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemAstroMiner constructor(assetName: String?) : Item(), IHoldableItem, ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setMaxStackSize(1)
        this.setTranslationKey(assetName)
        //this.setTextureName("arrow");
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    @SideOnly(Side.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun onItemUseFirst(
        playerIn: EntityPlayer,
        worldIn: World,
        pos: BlockPos,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        hand: EnumHand
    ): EnumActionResult {
        var tile: TileEntity? = null

        if (playerIn == null) {
            return EnumActionResult.PASS
        } else {
            val id: Block = worldIn.getBlockState(pos).getBlock()

            if (id === GCBlocks.fakeBlock) {
                tile = worldIn.getTileEntity(pos)

                if (tile is TileEntityMulti) {
                    tile = tile.getMainBlockTile()
                }
            }

            if (id === AsteroidBlocks.minerBaseFull) {
                tile = worldIn.getTileEntity(pos)
            }

            if (tile is TileEntityMinerBase) {
                //Don't open GUI on client
                if (worldIn.isRemote) {
                    return EnumActionResult.FAIL
                }

                if (worldIn.provider is WorldProviderSpaceStation) {
                    playerIn.sendMessage(TextComponentString(GCCoreUtil.translate("gui.message.astro_miner7.fail")))
                    return EnumActionResult.FAIL
                }

                if (tile.getLinkedMiner() != null) {
                    playerIn.sendMessage(TextComponentString(GCCoreUtil.translate("gui.message.astro_miner.fail")))
                    return EnumActionResult.FAIL
                }

                //Gives a chance for any loaded Astro Miner to link itself
                if (tile.ticks < 15) {
                    return EnumActionResult.FAIL
                }

                val playerMP: EntityPlayerMP = playerIn as EntityPlayerMP
                val stats: GCPlayerStats = (GCPlayerStats.get(playerIn))!!

                val astroCount: Int = stats.getAstroMinerCount()
                if (astroCount >= ConfigManagerAsteroids.astroMinerMax && (!playerIn.capabilities.isCreativeMode)) {
                    playerIn.sendMessage(TextComponentString(GCCoreUtil.translate("gui.message.astro_miner2.fail")))
                    return EnumActionResult.FAIL
                }

                if (!tile.spawnMiner(playerMP)) {
                    playerIn.sendMessage(
                        TextComponentString(
                            GCCoreUtil.translate("gui.message.astro_miner1.fail") + " " + GCCoreUtil.translate(
                                EntityAstroMiner.blockingBlock.toString()
                            )
                        )
                    )
                    return EnumActionResult.FAIL
                }

                if (!playerIn.capabilities.isCreativeMode) {
                    stats.setAstroMinerCount(stats.getAstroMinerCount() + 1)
                    playerIn.getHeldItem(hand).shrink(1)
                }
                return EnumActionResult.SUCCESS
            }
        }
        return EnumActionResult.PASS
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        //TODO
    }

    override fun shouldHoldLeftHandUp(player: EntityPlayer?): Boolean {
        return true
    }

    override fun shouldHoldRightHandUp(player: EntityPlayer?): Boolean {
        return true
    }

    override fun shouldCrouch(player: EntityPlayer?): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }
}
