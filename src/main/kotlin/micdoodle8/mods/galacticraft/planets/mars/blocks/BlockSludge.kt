package micdoodle8.mods.galacticraft.planets.mars.blocks

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.MarsModule
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySludgeling
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockSludge constructor(assetName: String?) :
    BlockFluidClassic(MarsModule.Companion.sludge, MarsModule.Companion.sludgeMaterial) {
    //    @OnlyIn(Dist.CLIENT)
    //    IIcon stillIcon;
    //    @OnlyIn(Dist.CLIENT)
    //    IIcon flowingIcon;
    public override fun onEntityCollision(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState?,
        entityIn: Entity?
    ) {
        if (!worldIn.isClientSide) {
            if (entityIn is EntityPlayer && entityIn.capabilities.isFlying || entityIn is EntitySludgeling) {
                return
            }

            val range: Int = 5
            val l: MutableList<*> = worldIn.getEntitiesWithinAABB<EntitySludgeling?>(
                EntitySludgeling::class.java,
                AxisAlignedBB(
                    (pos.getX() - range).toDouble(),
                    (pos.getY() - range).toDouble(),
                    (pos.getZ() - range).toDouble(),
                    (pos.getX() + range).toDouble(),
                    (pos.getY() + range).toDouble(),
                    (pos.getZ() + range).toDouble()
                )
            )

            if (l.size < 3) {
                val sludgeling: EntitySludgeling = EntitySludgeling(worldIn)
                sludgeling.setPosition(
                    (pos.getX() + worldIn.random.nextInt(5) - 2).toDouble(),
                    pos.getY().toDouble(),
                    (pos.getZ() + worldIn.random.nextInt(5) - 2).toDouble()
                )
                worldIn.addFreshEntity(sludgeling)
            }
        }

        super.onEntityCollision(worldIn, pos, state, entityIn)
    }

    override fun isEntityInsideMaterial(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        entity: Entity,
        yToTest: Double,
        material: Material,
        testingHead: Boolean
    ): Boolean? {
        return true
    }

    init {
        this.setQuantaPerBlock(9)
        this.setLightLevel(1.0f)
        this.needsRandomTick = true
        this.setTranslationKey(assetName)
    }

    /*@Override
    @OnlyIn(Dist.CLIENT)
    public IIcon getIcon(int par1, int par2)
    {
        return par1 != 0 && par1 != 1 ? this.flowingIcon : this.stillIcon;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.stillIcon = par1IconRegister.registerIcon(MarsModule.TEXTURE_PREFIX + "sludge_still");
        this.flowingIcon = par1IconRegister.registerIcon(MarsModule.TEXTURE_PREFIX + "sludge_flow");
        MarsModule.SLUDGE.setStillIcon(this.stillIcon);
        MarsModule.SLUDGE.setFlowingIcon(this.flowingIcon);
    }*/
    override fun canDisplace(world: IBlockAccess, pos: BlockPos): Boolean {
        if (world.getBlockState(pos).getMaterial().isLiquid()) {
            return false
        }
        return super.canDisplace(world, pos)
    }

    override fun displaceIfPossible(world: World, pos: BlockPos): Boolean {
        if (world.getBlockState(pos).getMaterial().isLiquid()) {
            return false
        }
        return super.displaceIfPossible(world, pos)
    }

    @OnlyIn(Dist.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand)

        if (rand.nextInt(1200) == 0) {
            worldIn.playSound(
                null,
                (pos.getX() + 0.5f).toDouble(),
                (pos.getY() + 0.5f).toDouble(),
                (pos.getZ() + 0.5f).toDouble(),
                SoundEvents.BLOCK_LAVA_AMBIENT,
                SoundCategory.BLOCKS,
                rand.nextFloat() * 0.25f + 0.75f,
                0.00001f + rand.nextFloat() * 0.5f
            )
        }
        if (rand.nextInt(10) == 0) {
            val below: BlockPos = pos.below()
            val state: IBlockState = worldIn.getBlockState(below)
            if (state.getBlock()
                    .isSideSolid(state, worldIn, below, EnumFacing.UP) && !worldIn.getBlockState(pos.below(2))
                    .getMaterial().blocksMovement()
            ) {
                GalacticraftPlanets.spawnParticle(
                    "bacterialDrip",
                    Vector3(
                        (pos.getX() + rand.nextFloat()).toDouble(),
                        pos.getY() - 1.05,
                        (pos.getZ() + rand.nextFloat()).toDouble()
                    ),
                    Vector3(0.0, 0.0, 0.0)
                )
            }
        }
    }
}
