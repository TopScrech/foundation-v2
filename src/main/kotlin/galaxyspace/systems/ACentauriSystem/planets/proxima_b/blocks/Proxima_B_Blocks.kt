package galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.GalaxySpace
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks.EnumBlockProximaB
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Blocks
import galaxyspace.api.block.IEnergyGeyser
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class Proxima_B_Blocks constructor() : Block(Material.ROCK), ISortableBlock, ITerraformableBlock,
    IDetectableResource, IEnergyGeyser, LegacyBlockMetadata {
    init {
        this.setTranslationKey("proxima_b_blocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
        this.setTickRandomly(false)
    }

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
        when (state.getValue(BASIC_TYPE)) {
            EnumBlockProximaB.MUSHROOM_GEYSER -> world.scheduleBlockUpdate(
                pos,
                this,
                this.tickRate(world) + world.random.nextInt(10),
                0
            )

            else -> {}
        }
    }

    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!world.isAreaLoaded(pos, 1)) return

        val lifetime: Int = 1
        if (rand.nextInt(20) == 0 && state === state.setValue(
                BASIC_TYPE,
                EnumBlockProximaB.MUSHROOM_GEYSER
            )
        ) {
            if (world.isEmptyBlock(pos.above()) && world.getBlockState(pos.below())
                    .getBlock() === VenusBlocks.sulphuricAcid
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
                        0.01,
                        0.0 + ((rand.nextFloat() / 20) * (if (rand.nextBoolean()) -1 else 1))
                    ),
                    (arrayOf<Any>(25 + rand.nextInt(5) + lifetime, 7, false, Vector3(0.6, 0.6, 0.2), 0.9, 0.4) as Array<Any?>?)
                )
            }
        }

        world.scheduleBlockUpdate(pos, this, 0, 0)
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

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockProximaB in EnumBlockProximaB.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        val type: EnumBlockProximaB = (state.getValue(BASIC_TYPE) as EnumBlockProximaB)
        when (type) {
            EnumBlockProximaB.COAL_ORE -> return Items.COAL
            EnumBlockProximaB.SILICON_ORE -> return (GCItems.basicItem)!!
            EnumBlockProximaB.DIAMOND_ORE -> return Items.DIAMOND
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockProximaB = (state.getValue(BASIC_TYPE) as EnumBlockProximaB)
        when (type) {
            else -> return getMetaFromState(state)
        }
    }

    override fun getDrops(
        drops: NonNullList<ItemStack>,
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ) {
        val rand: Random = if (world is World) world.random else RANDOM

        val count: Int = quantityDropped(state, fortune, rand)
        for (i: Int in 0..<count) {
            val item: Item = this.getItemDropped(state, rand, fortune)
            if (item !== Items.AIR) {
                val type: EnumBlockProximaB = (state.getValue(BASIC_TYPE) as EnumBlockProximaB)

                when (type) {
                    EnumBlockProximaB.COAL_ORE, EnumBlockProximaB.DIAMOND_ORE -> drops.add(ItemStack(item, 1, 0))
                    EnumBlockProximaB.SILICON_ORE -> drops.add(ItemStack(item, 1, 2))
                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun isValueable(metadata: IBlockState?): Boolean {
        return false
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        return false
    }

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        val type: EnumBlockProximaB = (state.getValue(BASIC_TYPE) as EnumBlockProximaB)
        when (type) {
            EnumBlockProximaB.ICE_SURFACE -> return true

            else -> return true
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockProximaB(private val meta: Int, private val serializedName: String) : IStringSerializable {
        SURFACE(0, "proxima_b_surface"),
        SUBSURFACE(1, "proxima_b_subsurface"),
        STONE(2, "proxima_b_stone"),
        ICE_SURFACE(3, "proxima_b_ice_surface"),
        ASH_ROCK(4, "proxima_b_ash_rock"),
        GOLD_ORE(5, "proxima_b_gold_ore"),
        TIN_ORE(6, "proxima_b_tin_ore"),
        COPPER_ORE(7, "proxima_b_copper_ore"),
        COAL_ORE(8, "proxima_b_coal_ore"),
        SILICON_ORE(9, "proxima_b_silicon_ore"),
        DIAMOND_ORE(10, "proxima_b_diamond_ore"),
        ROCKY_SURFACE(11, "proxima_b_rocky_surface"),
        MUSHROOM_SKIN(12, "proxima_b_mushroom_skin"),
        MUSHROOM_GEYSER(13, "proxima_b_mushroom_geyser"),
        MUD(14, "proxima_b_mud");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockProximaB? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumBlockProximaB.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockProximaB).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun isWorkGeyser(world: World?, state: IBlockState?, pos: BlockPos?): Boolean {
        // TODO Auto-generated method stub
        return false
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockProximaB> =
            PropertyEnum.create<EnumBlockProximaB>("type", EnumBlockProximaB::class.java)
    }
}
