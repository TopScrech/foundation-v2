package galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines

import galaxyspace.GalaxySpace
import asmodeuscore.api.item.IShiftDescription
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockEnergyPad
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityEnergyPad
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvancedTile
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.SoundType
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

open class BlockEnergyPad constructor() : BlockAdvancedTile(GCBlocks.machine), IShiftDescription, ISortableBlock {
    init {
        this.setTranslationKey("energy_pad")
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AABB_BOTTOM
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        worldIn.setBlockState(pos, getStateFromMeta(0), 3)
    }

    override fun isTopSolid(state: IBlockState): Boolean {
        return true
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun onEntityCollision(world: World, pos: BlockPos, state: IBlockState, entity: Entity) {
        if (world.isRemote) {
            if (entity is EntityPlayer) {
                val tile: TileEntityEnergyPad? = world.getTileEntity(pos) as TileEntityEnergyPad?


                //EntityPlayer player = (EntityPlayer) entity;
                if (tile!!.hasEnoughEnergyToRun) for (i: Int in 0..4) Minecraft.getMinecraft().world.spawnParticle(
                    EnumParticleTypes.REDSTONE,
                    pos.getX() + world.rand.nextDouble(),
                    pos.getY() + world.rand.nextDouble() + 0.2,
                    pos.getZ() + world.rand.nextDouble(),
                    0.5,
                    0.9,
                    0.0
                )
            }
        } else {
            if (entity is EntityPlayer) {
                val tile: TileEntityEnergyPad? = world.getTileEntity(pos) as TileEntityEnergyPad?
                val player: EntityPlayer = entity

                tile!!.smeltItem(player)
            }
        }
    }

    override fun onMachineActivated(
        world: World?,
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
        //playerIn.openGui(GalaxySpace.instance, -1, world, pos.getX(), pos.getY(), pos.getZ());
        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityEnergyPad()
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".desc")
    }

    override fun getDescription(meta: Int): String? {
        return null
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(STATUS, false)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, STATUS)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tile: TileEntity? = worldIn.getTileEntity(pos)

        if (tile != null) {
            val energyLevel: Boolean = (tile as TileEntityEnergyPad).isCollide
            return state.withProperty(STATUS, energyLevel)
        }
        return state
    }

    companion object {
        @JvmField
        protected val AABB_BOTTOM: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0)

        @JvmField
        val STATUS: PropertyBool = PropertyBool.create("status")
    }
}
