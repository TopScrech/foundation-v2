package micdoodle8.mods.galacticraft.planets.mars.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntitySlimelingEgg
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Enchantments
import net.minecraft.item.Item
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockSlimelingEgg constructor(assetName: String?) : Block(Material.ROCK), ITileEntityProvider,
    IShiftDescription, ISortableBlock {
    enum class EnumEggColor(private val meta: Int, private val serializedName: String) : IStringSerializable {
        RED(0, "red"),
        BLUE(1, "blue"),
        YELLOW(2, "yellow");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }
    }

    init {
        //        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.625F, 0.75F);
        this.setTranslationKey(assetName)
        this.setDefaultState(
            this.blockState.getBaseState().withProperty(EGG_COLOR, EnumEggColor.RED)
                .withProperty(BROKEN, false)
        )
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AABB
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)
    }

    //    @Override
    //    public boolean canBlockStay(World par1World, int par2, int par3, int par4)
    //    {
    //        Block block = par1World.getBlock(par2, par3 - 1, par4);
    //        return block.isSideSolid(par1World, par2, par3, par4, ForgeDirection.UP);
    //    }
    private fun beginHatch(world: World, pos: BlockPos, player: EntityPlayer, time: Int): Boolean {
        val state: IBlockState = world.getBlockState(pos)
        val l: Int = state.getBlock().getMetaFromState(state)

        if (l < 3) {
            world.setBlockState(pos, state.getBlock().getStateFromMeta(l + 3), 2)

            val tile: TileEntity? = world.getTileEntity(pos)

            if (tile is TileEntitySlimelingEgg) {
                tile.timeToHatch = world.rand.nextInt(30 + time) + time
                tile.lastTouchedPlayerUUID = player.getUniqueID().toString()
                tile.lastTouchedPlayerName = player.getName()
            }

            world.markBlockRangeForRenderUpdate(pos, pos)

            return true
        } else {
            world.markBlockRangeForRenderUpdate(pos, pos)
            return false
        }
    }

    override fun removedByPlayer(
        state: IBlockState,
        world: World,
        pos: BlockPos,
        player: EntityPlayer,
        willHarvest: Boolean
    ): Boolean {
        val currentStack: ItemStack = player.getHeldItemMainhand()
        if (currentStack != null && currentStack.getItem() is ItemPickaxe) {
            return world.setBlockToAir(pos)
        } else if (player.capabilities.isCreativeMode) {
            return world.setBlockToAir(pos)
        } else {
            beginHatch(world, pos, player, 0)
            return false
        }
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        return beginHatch(worldIn, pos, playerIn, 20)
    }

    override fun harvestBlock(
        worldIn: World,
        player: EntityPlayer,
        pos: BlockPos,
        state: IBlockState,
        te: TileEntity?,
        currentStack: ItemStack?
    ) {
        if (!currentStack!!.isEmpty() && currentStack.getItem() is ItemPickaxe) {
            player.addStat(StatList.getBlockStats(this))
            player.addExhaustion(0.025f)
            this.dropBlockAsItem(
                worldIn,
                pos,
                state.getBlock().getStateFromMeta(state.getBlock().getMetaFromState(state) % 3),
                0
            )
            if (currentStack.getItem() === MarsItems.deshPickaxe && EnchantmentHelper.getEnchantmentLevel(
                    Enchantments.SILK_TOUCH,
                    currentStack
                ) > 0
            ) {
                val itemstack: ItemStack = ItemStack(MarsItems.deshPickSlime, 1, currentStack.getItemDamage())
                if (currentStack.getTagCompound() != null) {
                    itemstack.setTagCompound(currentStack.getTagCompound()!!.copy())
                }
                player.inventory.setInventorySlotContents(player.inventory.currentItem, itemstack)
            }
        }
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return this.icons[metadata % 6];
    }*/
    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    //    @Override
    //    public Item getItemDropped(int meta, Random random, int par3)
    //    {
    //        return Item.getItemFromBlock(this);
    //    }
    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    //    @Override
    //    public int quantityDropped(int meta, int fortune, Random random)
    //    {
    //        return 1;
    //    }
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (var4: Int in EnumEggColor.entries.toTypedArray().indices) {
            list.add(ItemStack(this, 1, var4))
        }
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntitySlimelingEgg()
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        val metadata: Int = state.getBlock().getMetaFromState(state)

        if (metadata == 3) {
            return ItemStack(Item.getItemFromBlock(this), 1, 0)
        }
        if (metadata == 4) {
            return ItemStack(Item.getItemFromBlock(this), 1, 1)
        }
        if (metadata == 5) {
            return ItemStack(Item.getItemFromBlock(this), 1, 2)
        }
        return super.getPickBlock(state, target, world, pos, player)
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(EGG_COLOR, EnumEggColor.entries[meta % 3])
            .withProperty(BROKEN, meta - 3 >= 0)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(EGG_COLOR) as EnumEggColor).getMeta() + (if (state.getValue(
                BROKEN
            )
        ) 3 else 0)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, EGG_COLOR, BROKEN)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.EGG
    }

    companion object {
        //    private IIcon[] icons;
        @JvmField
        val EGG_COLOR: PropertyEnum<EnumEggColor> =
            PropertyEnum.create<EnumEggColor>("eggcolor", EnumEggColor::class.java)

        @JvmField
        val BROKEN: PropertyBool = PropertyBool.create("broken")

        @JvmField
        protected val AABB: AxisAlignedBB = AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.625, 0.75)
    }
}
