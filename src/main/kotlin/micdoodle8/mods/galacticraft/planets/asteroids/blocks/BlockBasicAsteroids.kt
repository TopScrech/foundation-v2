package micdoodle8.mods.galacticraft.planets.asteroids.blocks

import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList
import java.util.Random

open class BlockBasicAsteroids constructor(assetName: String?) : Block(Material.ROCK), IDetectableResource,
    IPlantableBlock, ITerraformableBlock, ISortableBlock {
    enum class EnumBlockBasic(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ASTEROID_0(0, "asteroid_rock_0"),
        ASTEROID_1(1, "asteroid_rock_1"),
        ASTEROID_2(2, "asteroid_rock_2"),
        ORE_ALUMINUM(3, "ore_aluminum_asteroids"),
        ORE_ILMENITE(4, "ore_ilmenite_asteroids"),
        ORE_IRON(5, "ore_iron_asteroids"),
        DECO(6, "asteroid_deco"),
        TITANIUM_BLOCK(7, "titanium_block");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockBasic> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockBasic? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.blockHardness = 3.0f
        this.setTranslationKey(assetName)
    }

    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getDrops(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ): MutableList<ItemStack?> {
        if (state.getValue(BASIC_TYPE) == EnumBlockBasic.ORE_ILMENITE && world is World) {
            val ret: ArrayList<ItemStack?> = ArrayList<ItemStack?>()

            var count: Int = quantityDropped(state, fortune, world.rand)
            for (i: Int in 0..<count) {
                ret.add(ItemStack(AsteroidsItems.basicItem, 1, 3))
            }
            count = quantityDropped(state, fortune, world.rand)
            for (i: Int in 0..<count) {
                ret.add(ItemStack(AsteroidsItems.basicItem, 1, 4))
            }
            return ret
        }


        return super.getDrops(world, pos, state, fortune)
    }

    override fun damageDropped(state: IBlockState): Int {
        when (getMetaFromState(state)) {
            4 -> return 0
            else -> return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        when (getMetaFromState(state)) {
            4 -> {
                if (fortune >= 1) {
                    return if (random.nextFloat() < fortune * 0.29f - 0.25f) 2 else 1
                }
                return 1
            }

            else -> return 1
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        var var4: Int

        var4 = 0
        while (var4 < EnumBlockBasic.entries.size) {
            list.add(ItemStack(this, 1, var4))
            ++var4
        }
    }

    override fun isValueable(state: IBlockState?): Boolean {
        when (this.getMetaFromState((state)!!)) {
            3, 4, 5 -> return true
            else -> return false
        }
    }

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        return false
    }

    override fun requiredLiquidBlocksNearby(): Int {
        return 4
    }

    override fun isPlantable(state: IBlockState?): Boolean {
        return false
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        return false
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        if (getMetaFromState(state) == 4) {
            return ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state))
        }

        return super.getPickBlock(state, target, world, pos, player)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumBlockBasic.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockBasic).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        when (meta) {
            3, 4, 5 -> return EnumSortCategoryBlock.ORE
            6 -> return EnumSortCategoryBlock.DECORATION
            7 -> return EnumSortCategoryBlock.INGOT_BLOCK
        }
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getExpDrop(state: IBlockState, world: IBlockAccess, pos: BlockPos, fortune: Int): Int {
        if (state.getBlock() !== this) return 0

        val meta: Int = this.getMetaFromState(state)
        if (meta == 4) {
            val rand: Random = if (world is World) world.rand else Random()
            return MathHelper.getInt(rand, 2, 3)
        }
        return 0
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockBasic> =
            PropertyEnum.create<EnumBlockBasic>("basictypeasteroids", EnumBlockBasic::class.java)
    }
}
