package micdoodle8.mods.galacticraft.planets.mars.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IShearable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList
import java.util.Random
import kotlin.math.max

open class BlockCavernousVine constructor(assetName: String?) : Block(Material.VINE), IShearable,
    IShiftDescription, ISortableBlock {
    enum class EnumVineType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        VINE_0(0, "vine_0"),
        VINE_1(1, "vine_1"),
        VINE_2(2, "vine_2");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumVineType> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumVineType? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setLightLevel(1.0f)
        this.setTickRandomly(true)
        this.setSoundType(SoundType.PLANT)
        this.setTranslationKey(assetName)
    }

    override fun removedByPlayer(
        state: IBlockState,
        world: World,
        pos: BlockPos,
        player: EntityPlayer,
        willHarvest: Boolean
    ): Boolean {
        if (world.setBlockToAir(pos)) {
            var y2: Int = pos.getY() - 1
            while (world.getBlockState(BlockPos(pos.getX(), y2, pos.getZ())).getBlock() === this) {
                world.setBlockToAir(BlockPos(pos.getX(), y2, pos.getZ()))
                y2--
            }

            return true
        }

        return false
    }

    open fun canBlockStay(worldIn: World, pos: BlockPos): Boolean {
        val stateAbove: IBlockState = worldIn.getBlockState(pos.up())
        return (stateAbove.getBlock() === this || stateAbove.getMaterial().isSolid())
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos)

        if (!this.canBlockStay(worldIn, pos)) {
            worldIn.setBlockToAir(pos)
        }
    }

    public override fun onEntityCollision(
        worldIn: World?,
        pos: BlockPos?,
        state: IBlockState?,
        entityIn: Entity?
    ) {
        if (entityIn is EntityLivingBase) {
            if (entityIn is EntityPlayer && entityIn.capabilities.isFlying) {
                return
            }

            entityIn.motionY = 0.06
            entityIn.rotationYaw += 0.4f

            entityIn.addPotionEffect(PotionEffect(MobEffects.POISON, 5, 20, false, true))
        }
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        return this.getVineLight(world, pos)
    }

    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
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

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return null
    }

    override fun canPlaceBlockOnSide(world: World, pos: BlockPos, facing: EnumFacing): Boolean {
        return facing == EnumFacing.DOWN && world.getBlockState(pos.up())
            .getBlockFaceShape(world, pos.up(), facing) == BlockFaceShape.SOLID
    }

    open fun getVineLength(world: IBlockAccess, pos: BlockPos): Int {
        var vineCount: Int = 0
        var y2: Int = pos.getY()

        while (world.getBlockState(BlockPos(pos.getX(), y2, pos.getZ())).getBlock() === MarsBlocks.vine) {
            vineCount++
            y2++
        }

        return vineCount
    }

    open fun getVineLight(world: IBlockAccess, pos: BlockPos): Int {
        var vineCount: Int = 0
        var y2: Int = pos.getY()

        while (world.getBlockState(BlockPos(pos.getX(), y2, pos.getZ())).getBlock() === MarsBlocks.vine) {
            vineCount += 4
            y2--
        }

        return max(19 - vineCount, 0)
    }

    override fun tickRate(par1World: World): Int {
        return 50
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isRemote) {
            for (y2: Int in pos.getY() - 1 downTo pos.getY() - 2) {
                val pos1: BlockPos = BlockPos(pos.getX(), y2, pos.getZ())
                val blockID: Block = worldIn.getBlockState(pos1).getBlock()

                if (!blockID.isAir(worldIn.getBlockState(pos1), worldIn, pos1)) {
                    return
                }
            }

            worldIn.setBlockState(pos.down(), this.getStateFromMeta(this.getVineLength(worldIn, pos) % 3), 2)
            worldIn.checkLight(pos)
        }
    }

    //    @Override
    //    public void onBlockAdded(World world, int x, int y, int z)
    //    {
    //        if (!world.isRemote)
    //        {
    //            // world.scheduleBlockUpdate(x, y, z, this,
    //            // this.tickRate(world) + world.rand.nextInt(10));
    //        }
    //    }
    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(Blocks.AIR)
    }

    override fun quantityDropped(par1Random: Random): Int {
        return 0
    }

    override fun isShearable(item: ItemStack, world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun onSheared(
        item: ItemStack,
        world: IBlockAccess?,
        pos: BlockPos?,
        fortune: Int
    ): MutableList<ItemStack?> {
        val ret: ArrayList<ItemStack?> = ArrayList<ItemStack?>()
        ret.add(ItemStack(this, 1, 0))
        return ret
    }

    override fun isLadder(state: IBlockState, world: IBlockAccess, pos: BlockPos, entity: EntityLivingBase): Boolean {
        return true
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    @SideOnly(Side.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(VINE_TYPE, (EnumVineType.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(VINE_TYPE) as EnumVineType).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, VINE_TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    companion object {
        @JvmField
        val VINE_TYPE: PropertyEnum<EnumVineType> =
            PropertyEnum.create<EnumVineType>("vinetype", EnumVineType::class.java)
    }
}
