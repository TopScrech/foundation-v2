package galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks

import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Blocks.EnumBlockTauCetiF
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Blocks
import galaxyspace.systems.TauCetiSystem.core.TCBlocks
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.blocks.TauCeti_F_Watergrass
import net.minecraft.block.Block
import net.minecraft.block.BlockBush
import net.minecraft.block.BlockLiquid
import net.minecraft.block.IGrowable
import net.minecraft.block.SoundType
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IShearable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class TauCeti_F_Watergrass constructor() : BlockBush(Material.WATER), IGrowable, IShearable {
    init {
        this.setTranslationKey("tauceti_f_watergrass")
        this.setTickRandomly(true)
        this.setHardness(0.0f)
        this.setSoundType(SoundType.PLANT)
        this.setDefaultState(
            getDefaultState().withProperty(
                BASIC_TYPE,
                EnumBlockDandelions.WATERGRASS_RED
            ).withProperty(BlockLiquid.LEVEL, 15)
        )
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return TALL_GRASS_AABB
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return NULL_AABB
    }

    override fun onEntityCollision(world: World, pos: BlockPos, state: IBlockState, entity: Entity) {
        /*if(getType(state) == EnumBlockDandelions.DESERT_DOWN)
		{
			if(entity instanceof EntityLivingBase)
				entity.attackEntityFrom(DamageSource.CACTUS, 0.5F);
		}*/
    }


    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

        when (type) {
            else -> return Material.GROUND.getMaterialMapColor()

        }
    }

    override fun getMaterial(state: IBlockState): Material {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)
        when (type) {
            else -> return this.material
        }
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

        when (type) {
            else -> return state.getLightValue()
        }
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

        //if(type == EnumBlockDandelions.REEDS || type == EnumBlockDandelions.REEDS_FRUITS)
        //return new ItemStack(BRItems.BASIC, 1, 0);
        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockDandelions in EnumBlockDandelions.entries) {
            /*if(!blockBasic.equals(blockBasic.DESERT_UP) 
        			&& !blockBasic.equals(blockBasic.REEDS) 
        			&& !blockBasic.equals(blockBasic.REEDS_FRUITS)
        			&& !blockBasic.equals(blockBasic.YELLOW_GRASS_UP))*/
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun onBlockActivated(
        world: World,
        pos: BlockPos,
        state: IBlockState,
        player: EntityPlayer,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        /*EnumBlockDandelions type = ((EnumBlockDandelions) state.getValue(BASIC_TYPE));
		if(!world.isRemote) {	
			if(type == EnumBlockDandelions.REEDS_FRUITS) {
				world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(BRItems.FOODS, 1, 0)));
				world.setBlockState(pos, this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS), 3);
				return true;	
			}
		}
		*/

        return false
    }

    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        /*if (!world.isAreaLoaded(pos, 1)) return; // Forge: prevent growing cactus from loading unloaded chunks with block update
		
		if (!world.isRemote)
        {
			if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS) || state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS_FRUITS))
			{
				if(world.isAirBlock(pos.up())) {
					int length = 1;
					IBlockState reed1 = this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS);
					IBlockState reed2 = this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS_FRUITS);
					
					while(world.getBlockState(pos.down(length)) == reed1 || world.getBlockState(pos.down(length)) == reed2) 
						length++;
							
					if(length < 4)
						world.setBlockState(pos.up(), this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS), 3);
				}
				if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS))
					if(rand.nextInt(40) == 0)
					{
						world.setBlockState(pos, this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS_FRUITS), 3);
					}
				
							
			}
			
			if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.VIOLET_TREE_SAPLING))
			{
				if (world.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
					this.grow(world, rand, pos, state);
				}
			}
        }*/
    }

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos) {
        //super.neighborChanged(state, world, pos, block, fromPos);
        //canPlaceAt(state, world, pos, EnumBlockDandelions.REEDS, BRBlocks.BARNARDA_C_GRASS.getDefaultState(), this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS));

        /*	if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.YELLOW_GRASS_UP))
		{
			if(!world.getBlockState(pos.down()).equals(this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.YELLOW_GRASS_DOWN)))
			{
				this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
				world.destroyBlock(pos, false);
			}
		}
		
		
		if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS))
		{			
			if(world.isAirBlock(pos.down()))
			{
				this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
				world.destroyBlock(pos, false);
			}
		}
		
		if(state != this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.LEAVES_BALLS) && world.isAirBlock(pos.down()))
		{
			this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
			world.destroyBlock(pos, false);
		}
		
		if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.LEAVES_BALLS) && world.isAirBlock(pos.up()))
		{
			this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
			world.destroyBlock(pos, false);
		}*/
    }

    private fun canPlaceAt(
        state: IBlockState,
        world: World,
        pos: BlockPos,
        placer: EntityLivingBase,
        type: EnumBlockDandelions,
        vararg valide: IBlockState?
    ) {
        if (!world.isRemote && state === this.getDefaultState()
                .withProperty(BASIC_TYPE, type)
        ) {
            var is_forriden: Boolean = true
            for (block: IBlockState? in valide) if (world.getBlockState(pos.down()) === block) is_forriden = false

            if (is_forriden) {
                world.destroyBlock(pos, false)
                if (placer is EntityPlayer && !placer.capabilities.isCreativeMode) world.spawnEntity(
                    EntityItem(
                        world,
                        pos.getX().toDouble(),
                        pos.getY().toDouble(),
                        pos.getZ().toDouble(),
                        ItemStack(this.getDefaultState().getBlock(), 1, this.getMetaFromState(state))
                    )
                )
            }
        }
    }

    override fun onBlockPlacedBy(
        world: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        canPlaceAt(
            state, world, pos, placer, EnumBlockDandelions.WATERGRASS_RED,
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.GRASS
            ),
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.DIRT
            )
        )
        canPlaceAt(
            state, world, pos, placer, EnumBlockDandelions.WATERGRASS_STANDART_BOTTOM,
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.GRASS
            ),
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.DIRT
            )
        )
        canPlaceAt(
            state, world, pos, placer, EnumBlockDandelions.WATERGRASS_STANDART_MIDDLE,
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.GRASS
            ),
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.DIRT
            )
        )
        canPlaceAt(
            state, world, pos, placer, EnumBlockDandelions.WATERGRASS_STANDART_TOP,
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.GRASS
            ),
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.DIRT
            )
        )
        canPlaceAt(
            state, world, pos, placer, EnumBlockDandelions.WATERGRASS_SLIME_BOTTOM,
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.GRASS
            ),
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.DIRT
            )
        )
        canPlaceAt(
            state, world, pos, placer, EnumBlockDandelions.WATERGRASS_SLIME_TOP,
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.GRASS
            ),
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.DIRT
            ),
            this.getDefaultState().withProperty(
                BASIC_TYPE,
                EnumBlockDandelions.WATERGRASS_SLIME_BOTTOM
            )
        )
        canPlaceAt(
            state, world, pos, placer, EnumBlockDandelions.WATERGRASS_THICK_GREEN,
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.GRASS
            ),
            TCBlocks.TAUCETI_F_BLOCKS.getDefaultState().withProperty(
                TauCeti_F_Blocks.Companion.BASIC_TYPE,
                EnumBlockTauCetiF.DIRT
            )
        )

        //canPlaceAt(state, world, pos, placer, EnumBlockDandelions.REEDS, BRBlocks.BARNARDA_C_GRASS.getDefaultState(), this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS));

        //canPlaceAt(state, world, pos, placer, EnumBlockDandelions.VIOLET_TREE_SAPLING, BRBlocks.BARNARDA_C_GRASS.getDefaultState(), BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0));

        /*if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.DESERT_DOWN))
		{
			if(world.isAirBlock(pos.up()))
				world.setBlockState(pos.up(), this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.DESERT_UP));
		}
		
		if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.YELLOW_GRASS_DOWN))
		{
			if(world.isAirBlock(pos.up()))
				world.setBlockState(pos.up(), this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.YELLOW_GRASS_UP));
		}
		*/
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        if (state === this.getDefaultState().withProperty(
                BASIC_TYPE,
                EnumBlockDandelions.WATERGRASS_STANDART_BOTTOM
            ) ||
            state === this.getDefaultState().withProperty(
                BASIC_TYPE,
                EnumBlockDandelions.WATERGRASS_STANDART_MIDDLE
            )
        ) {
            var i: Int = 1

            while (world.getBlockState(pos.up(i)).getBlock() === this) world.destroyBlock(pos.up(i++), true)
        }

        if (state === this.getDefaultState().withProperty(
                BASIC_TYPE,
                EnumBlockDandelions.WATERGRASS_SLIME_BOTTOM
            )
        ) {
            if (world.getBlockState(pos.up()) === this.getDefaultState()
                    .withProperty(
                        BASIC_TYPE,
                        EnumBlockDandelions.WATERGRASS_SLIME_TOP
                    )
            ) world.destroyBlock(pos.up(), true)
        }
        /*
		if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.YELLOW_GRASS_UP))
		{
			if(world.getBlockState(pos.down()) == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.YELLOW_GRASS_DOWN))
				world.destroyBlock(pos.down(), true);
		}
		
		if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.YELLOW_GRASS_DOWN))
		{
			if(world.getBlockState(pos.up()) == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.YELLOW_GRASS_UP))
				world.destroyBlock(pos.up(), true);
		}*/
        super.breakBlock(world, pos, state)
    }

    override fun isReplaceable(world: IBlockAccess, pos: BlockPos): Boolean {
        //if(world.getBlockState(pos) == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS))
        //return false;
        return false
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

        when (type) {
            else -> return null
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

        when (type) {
            else -> return 0
        }
    }

    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean): Boolean {
        /*if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.VIOLET_TREE_SAPLING))		
			return true;*/

        return false
    }

    override fun canUseBonemeal(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState): Boolean {
        /*if(state == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.VIOLET_TREE_SAPLING))		
			return true;*/

        return false
    }

    override fun grow(world: World, rand: Random, pos: BlockPos, state: IBlockState) {
        /*if(rand.nextInt(7) == 0)
        {
            this.generateTree(world, pos, state, rand);
        }*/
    }


    /*
	public void generateTree(World world, BlockPos pos, IBlockState state, Random rand)
    {
		if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(world, rand, pos)) return;
		
		WorldGenerator tree = new WorldGenTree_Forest(BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0), rand.nextInt(3));
		WorldGenerator tree_2 = new WorldGenTree_Forest2(BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0), BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0), rand.nextInt(3));
		
		boolean check = true;
		for(BlockPos poscheck : BlockPos.getAllInBox(pos.add(-5, 2, -5), pos.add(5, 10, 5)))
		{
			if(!world.isAirBlock(poscheck))
				check = false;
		}
		
		if(check)
		{
			if(rand.nextInt(2) == 0)
				tree.generate(world, rand, pos);
			else
				tree_2.generate(world, rand, pos);
		}
    }*/
    override fun isShearable(item: ItemStack, world: IBlockAccess?, pos: BlockPos?): Boolean {
        return false
    }

    override fun onSheared(
        item: ItemStack,
        world: IBlockAccess?,
        pos: BlockPos?,
        fortune: Int
    ): MutableList<ItemStack?> {
        return mutableListOf()
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
    }

    override fun getDrops(
        drops: NonNullList<ItemStack>,
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ) {
        val rand: Random = if (world is World) world.rand else RANDOM

        val count: Int = quantityDropped(state, fortune, rand)
        for (i: Int in 0..<count) {
            val item: Item = (this.getItemDropped(state, rand, fortune))!!
            if (item !== Items.AIR) {
                val type: EnumBlockDandelions =
                    (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

                when (type) {
                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    private fun getType(state: IBlockState): EnumBlockDandelions {
        return (state.getValue(BASIC_TYPE) as EnumBlockDandelions)
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockDandelions(private val meta: Int, private val serializedName: String) : IStringSerializable {
        WATERGRASS_RED(0, "watergrass_red"),
        WATERGRASS_STANDART_BOTTOM(1, "watergrass_standart_bottom"),
        WATERGRASS_STANDART_MIDDLE(2, "watergrass_standart_middle"),
        WATERGRASS_STANDART_TOP(3, "watergrass_standart_top"),
        WATERGRASS_SLIME_BOTTOM(4, "watergrass_slime_bottom"),
        WATERGRASS_SLIME_TOP(5, "watergrass_slime_top"),
        WATERGRASS_THICK_GREEN(6, "watergrass_thick_green"),
        KELP(7, "kelp");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockDandelions? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(
            BASIC_TYPE,
            (EnumBlockDandelions.Companion.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockDandelions).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(BlockLiquid.LEVEL, BASIC_TYPE))
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        return true
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return true
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockDandelions> =
            PropertyEnum.create<EnumBlockDandelions>("type", EnumBlockDandelions::class.java)

        @JvmField
        protected val TALL_GRASS_AABB: AxisAlignedBB = AxisAlignedBB(
            0.09999999403953552,
            0.0,
            0.09999999403953552,
            0.8999999761581421,
            0.800000011920929,
            0.8999999761581421
        )
    }
}
