package galaxyspace.systems.SolarSystem.moons.phobos.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.SolarSystem.moons.phobos.blocks.PhobosBlocks.EnumPhobosBlocks
import galaxyspace.systems.SolarSystem.moons.phobos.blocks.PhobosBlocks
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class PhobosBlocks constructor() : Block(Material.ROCK), ITerraformableBlock, IDetectableResource,
    ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("phobosblocks")
        this.setSoundType(SoundType.STONE)
        this.setHardness(1.0f)
        this.setHarvestLevel(
            "shovel",
            1,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumPhobosBlocks.PHOBOS_REGOLITE)
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumPhobosBlocks.PHOBOS_STONE)
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumPhobosBlocks.PHOBOS_IRON_ORE)
        )
        this.setHarvestLevel(
            "pickaxe",
            3,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumPhobosBlocks.PHOBOS_METEORICIRON_ORE
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            3,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumPhobosBlocks.PHOBOS_DESH_ORE)
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumPhobosBlocks.PHOBOS_NICKEL_ORE)
        )
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumPhobosBlocks in EnumPhobosBlocks.entries) list.add(
            ItemStack(
                this,
                1,
                blockBasic.getMeta()
            )
        )
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
        when (getMetaFromState(state)) {
            2 -> return (AsteroidsItems.basicItem)!!
            3 -> return (GCItems.meteoricIronRaw)!!
            5 -> return (MarsItems.marsItemBasic)!!
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        when (getMetaFromState(state)) {
            2 -> return 3
            3, 5 -> return 0
            else -> return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        val bonus: Int = 0

        if (fortune > 0 && Item.getItemFromBlock(this) !== this.getItemDropped(state, random, fortune)) {
            var j: Int = random.nextInt(fortune + 2) - 1

            if (j < 0) {
                j = 0
            }

            return this.quantityDropped(random) * (j + 1) + bonus
        } else {
            return this.quantityDropped(random) + bonus
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumPhobosBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        PHOBOS_REGOLITE(0, "phobos_regolite"),
        PHOBOS_STONE(1, "phobos_stone"),
        PHOBOS_IRON_ORE(2, "phobos_iron_ore"),
        PHOBOS_METEORICIRON_ORE(3, "phobos_meteoriciron_ore"),
        PHOBOS_NICKEL_ORE(4, "phobos_nickel_ore"),
        PHOBOS_DESH_ORE(5, "phobos_desh_ore");


        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumPhobosBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumPhobosBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumPhobosBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumPhobosBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumPhobosBlocks> =
            PropertyEnum.create<EnumPhobosBlocks>("type", EnumPhobosBlocks::class.java)
    }
}
