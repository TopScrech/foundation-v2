package micdoodle8.mods.galacticraft.planets.asteroids.blocks

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockTileGC
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.ShortRangeTelepadHandler
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random
import kotlin.math.min

open class BlockShortRangeTelepad constructor(assetName: String?) : BlockTileGC(Material.IRON), IShiftDescription,
    ISortableBlock {
    init {
        this.blockHardness = 3.0f
        this.setTranslationKey(assetName)
        this.setSoundType(SoundType.METAL)
    }

    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.INVISIBLE
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

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityShortRangeTelepad()
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AABB_TELEPAD
    }

    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    //    {
    //        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.45F, 1.0F);
    //    }
    //    @Override
    //    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    //    {
    //        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.45F, 1.0F);
    //        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //    }
    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)

        val tile: TileEntity? = worldIn.getTileEntity(pos)

        var validSpot: Boolean = true

        for (x: Int in -1..1) {
            var y: Int = 0
            while (y < 3) {
                for (z: Int in -1..1) {
                    if (!(x == 0 && y == 0 && z == 0)) {
                        val stateAt: IBlockState = worldIn.getBlockState(pos.add(x, y, z))

                        if (!stateAt.getMaterial().isReplaceable()) {
                            validSpot = false
                        }
                    }
                }
                y += 2
            }
        }

        if (!validSpot) {
            worldIn.setBlockToAir(pos)

            if (placer is EntityPlayer) {
                if (!worldIn.isRemote) {
                    placer.sendMessage(TextComponentString(EnumColor.RED.toString() + GCCoreUtil.translate("gui.warning.noroom")))
                }
                placer.inventory.addItemStackToInventory(ItemStack(Item.getItemFromBlock(this), 1, 0))
            }

            return
        }

        if (tile is TileEntityShortRangeTelepad) {
            tile.onCreate(worldIn, pos)
            tile.setOwner((PlayerUtil.getName((placer as EntityPlayer)))!!)
        }
    }

    override fun onMachineActivated(
        worldIn: World?,
        pos: BlockPos?,
        state: IBlockState?,
        playerIn: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        return ((worldIn)!!.getTileEntity(pos) as IMultiBlock).onActivated(playerIn)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileAt: TileEntity? = worldIn.getTileEntity(pos)

        var fakeBlockCount: Int = 0

        for (x: Int in -1..1) {
            var y: Int = 0
            while (y < 3) {
                for (z: Int in -1..1) {
                    if (!(x == 0 && y == 0 && z == 0)) {
                        if (worldIn.getBlockState(pos.add(x, y, z)).getBlock() === AsteroidBlocks.fakeTelepad) {
                            fakeBlockCount++
                        }
                    }
                }
                y += 2
            }
        }

        if (tileAt is TileEntityShortRangeTelepad) {
            if (fakeBlockCount > 0) {
                tileAt.onDestroy(tileAt)
            }
            ShortRangeTelepadHandler.removeShortRangeTeleporter(tileAt)
        }

        super.breakBlock(worldIn, pos, state)
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        val tileAt: TileEntity? = worldIn.getTileEntity(pos)

        if (tileAt is TileEntityShortRangeTelepad) {
            val telepad: TileEntityShortRangeTelepad = tileAt
            val teleportTimeScaled: Float =
                min(1.0f, telepad.teleportTime / TileEntityShortRangeTelepad.MAX_TELEPORT_TIME.toFloat())
            var f: Float
            var r: Float
            var g: Float
            var b: Float

            for (i: Int in 0..5) {
                for (j: Int in 0..3) {
                    f = rand.nextFloat() * 0.6f + 0.4f
                    r = f * 0.3f
                    g = f * (0.3f + (teleportTimeScaled * 0.7f))
                    b = f * (1.0f - (teleportTimeScaled * 0.7f))
                    GalacticraftPlanets.Companion.spawnParticle(
                        "portalBlue",
                        Vector3(
                            pos.getX() + 0.2 + rand.nextDouble() * 0.6,
                            pos.getY() + 0.1,
                            pos.getZ() + 0.2 + rand.nextDouble() * 0.6
                        ),
                        Vector3(0.0, 1.4, 0.0),
                        telepad,
                        false
                    )
                }

                f = rand.nextFloat() * 0.6f + 0.4f
                r = f * 0.3f
                g = f * (0.3f + (teleportTimeScaled * 0.7f))
                b = f * (1.0f - (teleportTimeScaled * 0.7f))
                GalacticraftPlanets.Companion.spawnParticle(
                    "portalBlue",
                    Vector3(
                        pos.getX() + 0.0 + rand.nextDouble() * 0.2,
                        pos.getY() + 2.9,
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, -2.95, 0.0),
                    telepad,
                    true
                )
                GalacticraftPlanets.Companion.spawnParticle(
                    "portalBlue",
                    Vector3(
                        pos.getX() + 0.8 + rand.nextDouble() * 0.2,
                        pos.getY() + 2.9,
                        pos.getZ() + rand.nextDouble()
                    ),
                    Vector3(0.0, -2.95, 0.0),
                    telepad,
                    true
                )
                GalacticraftPlanets.Companion.spawnParticle(
                    "portalBlue",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 2.9,
                        pos.getZ() + 0.2 + rand.nextDouble() * 0.2
                    ),
                    Vector3(0.0, -2.95, 0.0),
                    telepad,
                    true
                )
                GalacticraftPlanets.Companion.spawnParticle(
                    "portalBlue",
                    Vector3(
                        pos.getX() + rand.nextDouble(),
                        pos.getY() + 2.9,
                        pos.getZ() + 0.8 + rand.nextDouble() * 0.2
                    ),
                    Vector3(0.0, -2.95, 0.0),
                    telepad,
                    true
                )
            }
        }
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        protected val AABB_TELEPAD: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.45, 1.0)
    }
}
