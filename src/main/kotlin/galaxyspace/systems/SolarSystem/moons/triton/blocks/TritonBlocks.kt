package galaxyspace.systems.SolarSystem.moons.triton.blocks

import galaxyspace.GalaxySpace
import galaxyspace.api.block.IEnergyGeyser
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks.EnumTritonBlocks
import galaxyspace.systems.SolarSystem.moons.triton.blocks.TritonBlocks
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockIce
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraft.world.WorldProviderSurface
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class TritonBlocks constructor() : Block(Material.ROCK), ISortableBlock, IEnergyGeyser {
    init {
        this.setTranslationKey("tritonblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
        this.setTickRandomly(false)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumTritonBlocks in EnumTritonBlocks.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }


    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!world.isAreaLoaded(pos, 1)) return

        var lifetime: Int = 1
        if (world.provider is WorldProviderSurface) lifetime = -250

        if (state === state.withProperty(
                BASIC_TYPE,
                EnumTritonBlocks.TRITON_GEYSER
            )
        ) {
            if (world.isAirBlock(pos.up()) && (world.getBlockState(pos.down())
                    .getBlock() === FluidRegistry.WATER.getBlock() || world.getBlockState(pos.down())
                    .getBlock() is BlockIce)
            ) {
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles1",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 10) * (if (rand.nextBoolean()) -1 else 1)),
                        0.0001,
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(200 + rand.nextInt(100) + lifetime, 7, false, Vector3(0.3, 0.35, 0.3), 1.9, 5.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles1",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1)),
                        0.0001,
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(200 + rand.nextInt(100) + lifetime, 7, false, Vector3(0.4, 0.4, 0.4), 1.9, 5.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles1",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1)),
                        0.0001,
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(200 + rand.nextInt(100) + lifetime, 7, false, Vector3(0.1, 0.1, 0.1), 1.9, 5.0) as Array<Any?>?)
                )
            }
        }

        if (world.provider is WorldProviderSurface) lifetime = -60

        if (state === state.withProperty(
                BASIC_TYPE,
                EnumTritonBlocks.TRITON_GEYSER_2
            )
        ) {
            if (world.isAirBlock(pos.up()) && (world.getBlockState(pos.down())
                    .getBlock() === FluidRegistry.WATER.getBlock() || world.getBlockState(pos.down())
                    .getBlock() is BlockIce)
            ) {
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles1",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1)),
                        1.0 + rand.nextFloat(),
                        0.0 + ((rand.nextFloat() / 5) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(40 + rand.nextInt(14) + lifetime, 7, true, Vector3(0.3, 0.35, 0.3), 1.0, 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles1",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1)),
                        1.0 + rand.nextFloat(),
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(40 + rand.nextInt(14) + lifetime, 7, true, Vector3(0.4, 0.4, 0.4), 1.0, 1.0) as Array<Any?>?)
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "waterbubbles1",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 1.0 + rand.nextDouble(),
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1)),
                        1.0 + rand.nextFloat(),
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(40 + rand.nextInt(14) + lifetime, 7, true, Vector3(0.1, 0.1, 0.1), 1.0, 1.0) as Array<Any?>?)
                )
            }
        }

        world.scheduleBlockUpdate(pos, this, 0, 0)
    }

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
        when (state.getValue(BASIC_TYPE)) {
            EnumTritonBlocks.TRITON_GEYSER, EnumTritonBlocks.TRITON_GEYSER_2 -> world.scheduleBlockUpdate(
                pos,
                this,
                this.tickRate(world) + world.rand.nextInt(10),
                0
            )

            else -> {}
        }
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        val type: EnumTritonBlocks = state.getValue(BASIC_TYPE)
        when (type) {
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumTritonBlocks = state.getValue(BASIC_TYPE)
        when (type) {
            else -> return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        val bonus: Int = 0
        val default_count: Int = 0

        val type: EnumTritonBlocks = state.getValue(BASIC_TYPE)
        /*
        if (type == EnumTritonBlocks.TITAN_LAPIS_ORE)
        {
            bonus = 2;
            default_count = 8;
        }


        */
        if (fortune > 0 && Item.getItemFromBlock(this) !== this.getItemDropped(state, random, fortune)) {
            var j: Int = random.nextInt(fortune + 2) - 1

            if (j < 0) {
                j = 0
            }

            return (this.quantityDropped(random) + default_count) * (j + 1) + bonus
        } else {
            return this.quantityDropped(random) + default_count + random.nextInt(1 + bonus)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun isWorkGeyser(world: World?, state: IBlockState?, pos: BlockPos?): Boolean {
        if (state === (state)!!.withProperty(
                BASIC_TYPE,
                EnumTritonBlocks.TRITON_GEYSER
            )
        ) return (world)!!.getBlockState((pos)!!.down()).getBlock() === FluidRegistry.WATER.getBlock()
        if (state === (state)!!.withProperty(
                BASIC_TYPE,
                EnumTritonBlocks.TRITON_GEYSER_2
            )
        ) return (world)!!.getBlockState((pos)!!.down()).getBlock() === FluidRegistry.WATER.getBlock()
        return false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumTritonBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        TRITON_GRUNT(0, "triton_grunt"),
        TRITON_SUBGRUNT(1, "triton_subgrunt"),
        TRITON_STONE(2, "triton_stone"),
        TRITON_GEYSER(3, "triton_geyser"),
        TRITON_GEYSER_2(4, "triton_geyser_2");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumTritonBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumTritonBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumTritonBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumTritonBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumTritonBlocks> =
            PropertyEnum.create<EnumTritonBlocks>("type", EnumTritonBlocks::class.java)
    }
}
