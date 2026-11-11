package micdoodle8.mods.galacticraft.planets.venus.blocks

import foundation.item.FoundationItemVariants
import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityCrashedProbe
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockCrashedProbe constructor(assetName: String?) : BlockTileGC(Material.IRON), ISortableBlock,
    ITileEntityProvider {
    //    public static final String CRASHED_PROBE = "crashedProbe";
    //    private static final List<WeightedRandomChestContent> CONTENTS = Lists.newArrayList(
    //            new WeightedRandomChestContent(MarsItems.marsItemBasic, 3, 3, 6, 5), // Tier 2 plate
    //            new WeightedRandomChestContent(GCItems.heavyPlatingTier1, 0, 3, 6, 5), // Tier 1 plate
    //            new WeightedRandomChestContent(AsteroidsItems.basicItem, 6, 3, 6, 5), // Titanium plate
    //            new WeightedRandomChestContent(Items.IRON_INGOT, 0, 5, 9, 5), // Titanium plate
    //            new WeightedRandomChestContent(AsteroidsItems.basicItem, 5, 3, 6, 5)); // Tier 3 plate
    //    static
    //    {
    //        net.minecraftforge.common.ChestGenHooks.init(CRASHED_PROBE, CONTENTS, 4, 6);
    //    }
    init {
        this.blockHardness = 4.5f
        this.blockResistance = 2.5f
        this.setSoundType(SoundType.METAL)
        this.setTickRandomly(true)
        this.setTranslationKey(assetName)
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityCrashedProbe()
    }

    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        worldIn.spawnParticle(
            EnumParticleTypes.SMOKE_NORMAL,
            pos.getX() + 0.65,
            pos.getY() + 1.0,
            pos.getZ() + 0.9,
            0.0,
            0.0,
            0.0
        )
        worldIn.spawnParticle(
            EnumParticleTypes.SMOKE_NORMAL,
            pos.getX() + 0.2,
            pos.getY() + 1.0,
            pos.getZ() + 0.2,
            0.0,
            0.0,
            0.0
        )
        worldIn.spawnParticle(
            EnumParticleTypes.SMOKE_NORMAL,
            pos.getX() + 1.0,
            pos.getY() + 0.25,
            pos.getZ() + 0.5,
            0.0,
            0.0,
            0.0
        )
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
        playerIn.openGui(
            GalacticraftPlanets.instance,
            GuiIdsPlanets.MACHINE_VENUS,
            worldIn,
            pos.getX(),
            pos.getY(),
            pos.getZ()
        )
        return true
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile: TileEntity? = worldIn.getBlockEntity(pos)

        if (tile is TileEntityCrashedProbe && tile.getDropCore()) {
            spawnItem(worldIn, pos)
        }

        super.breakBlock(worldIn, pos, state)
    }

    //Drops a Radioisotope Core as well as the Crashed Probe block
    private fun spawnItem(worldIn: World, pos: BlockPos) {
        val f: Float = 0.7f
        val syncRandom: Random = GCCoreUtil.getRandom(pos)
        val d0: Double = syncRandom.nextFloat() * f + (1.0f - f) * 0.5
        val d1: Double = syncRandom.nextFloat() * f + (1.0f - f) * 0.2 + 0.6
        val d2: Double = syncRandom.nextFloat() * f + (1.0f - f) * 0.5
        val entityitem: EntityItem = EntityItem(
            worldIn,
            pos.getX() + d0,
            pos.getY() + d1,
            pos.getZ() + d2,
            FoundationItemVariants.stack(requireNotNull(VenusItems.basicItem), 1, 2)
        )
        entityitem.setDefaultPickupDelay()
        worldIn.addFreshEntity(entityitem)
    }
}
