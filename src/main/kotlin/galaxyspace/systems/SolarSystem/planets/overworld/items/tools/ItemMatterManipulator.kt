package galaxyspace.systems.SolarSystem.planets.overworld.items.tools

import foundation.persistence.containsTag

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.util.EnumColor
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.item.EnumAction
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

open class ItemMatterManipulator constructor() : Item() {
    init {
        this.setMaxDamage(1000)
        this.setMaxStackSize(1)
        this.setTranslationKey("matter_manipulator")
        this.setCreativeTab(GSCreativeTabs.GSArmorTab)
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSArmorTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, 0))
            list.add(ItemStack(this, 1, this.getMaxDamage()))
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        if (stack.hasTagCompound()) {
            list.add(EnumColor.DARK_RED.toString() + "Timer: " + stack.getTagCompound()!!.getIntOr("timer", 0))
        }
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val stack: ItemStack = player.getItemInHand(hand)
        val ray: RayTraceResult? = getRay(world, player, true) //player.rayTrace(DISTANCE, player.ticksExisted);


        /*if(world.isRemote)
		{
			GalaxySpace.proxy.spawnParticle("beamFX", new Vector3(), new Vector3(), new Object[] { new Vector3(0.0F, 0.0F, 0.0F) });
		}*/
        if (!world.isClientSide) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(NBTTagCompound())
                stack.getTagCompound()!!.putInt("timer", 0)
            }

            stack.getTagCompound()!!.putInt("timer", stack.getTagCompound()!!.getIntOr("timer", 0) + 1)

            val i: Int = stack.getTagCompound()!!.getIntOr("timer", 0)


            //MODE: DIG
            if (ray != null && i >= 2) {
                for (pos: BlockPos in BlockPos.getAllInBox(
                    ray.getBlockPos().add(-1, 0, -1),
                    ray.getBlockPos().add(1, 0, 1)
                )) {
                    val block: IBlockState = world.getBlockState(pos)


                    //EntityLaserBeam beam = new EntityLaserBeam(world, player, ray);
                    //beam.posY = player.posY + (double)(player.height / 2.0F) + 0.5D;
                    //beam.shoot(player, player.rotationPitch, player.rotationYawHead, 0.0F, 0.8F, 0.0F);
                    //beam.shoot(player.posX, player.posY, player.posZ, 0.8F, 0.0F);
                    //world.spawnEntity(beam);
                    if (!world.isClientSide && block.isBlockNormalCube() && block.getBlock() !== Blocks.BEDROCK) {
                        if (player.inventory.getFreeSlot() > 0) for (stacks: ItemStack in world.getBlockState(pos)
                            .getBlock().getDrops(
                                world,
                                pos,
                                world.getBlockState(pos),
                                0
                            )) player.inventory.add(stacks)
                        world.destroyBlock(pos, false)
                    }
                }

                //return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }


            /*
			if(i % 5 == 0) {
	
				EntityIceSpike entitysmallfireball = new EntityIceSpike(world, player);
				entitysmallfireball.posY = player.posY + (double)(player.height / 2.0F) + 0.5D;
				entitysmallfireball.shoot(player, player.rotationPitch, player.rotationYawHead, 0.0F, 0.8F, 0.0F);
				world.spawnEntity(entitysmallfireball);				
				
			}
			*/
            stack.getTagCompound()!!.putInt("timer", 1)
            player.setActiveHand(hand)
            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getItemInHand(hand))
        }
        return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getItemInHand(hand))
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase, timeLeft: Int) {
        if (entityLiving is EntityPlayer) {
            val entityplayer: EntityPlayer = entityLiving


            //GalaxySpace.debug("work");
            if (entityplayer.getUseItem().hasTagCompound()) {
                if (entityplayer.getUseItem().getTagCompound()!!.containsTag("timer")) {
                    entityplayer.getUseItem().getTagCompound()!!.putInt("timer", 0)
                }
            }
        }
    }

    override fun getMaxItemUseDuration(stack: ItemStack): Int {
        return 10
    }

    override fun getItemUseAction(stack: ItemStack): EnumAction {
        return EnumAction.NONE
    }

    companion object {
        @JvmField
        var DISTANCE: Int = 15

        @JvmStatic
        fun getRay(world: World, player: EntityPlayer, useLiquids: Boolean): RayTraceResult? {
            val f: Float = player.xRot
            val f1: Float = player.yRot
            val d0: Double = player.x
            val d1: Double = player.y + player.getEyeHeight().toDouble()
            val d2: Double = player.z
            val vec3d: Vec3d = Vec3d(d0, d1, d2)
            val f2: Float = MathHelper.cos(-f1 * 0.017453292f - Math.PI.toFloat())
            val f3: Float = MathHelper.sin(-f1 * 0.017453292f - Math.PI.toFloat())
            val f4: Float = -MathHelper.cos(-f * 0.017453292f)
            val f5: Float = MathHelper.sin(-f * 0.017453292f)
            val f6: Float = f3 * f4
            val f7: Float = f2 * f4
            val d3: Double = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 10
            val vec3d1: Vec3d = vec3d.add(f6.toDouble() * d3, f5.toDouble() * d3, f7.toDouble() * d3)
            return world.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false)
        }

        @JvmStatic
        @OnlyIn(Dist.CLIENT)
        fun drawLine(start: BlockPos, end: BlockPos, x: Double, y: Double, z: Double) {
            val start1: Vec3d = Vec3d(start)
            val end1: Vec3d = Vec3d(end)
            val posDiff: Vec3d = end1.subtract(start1)
            GlStateManager.pushMatrix()
            GlStateManager.glLineWidth(2f)
            GlStateManager.disableTexture2D()
            GlStateManager.disableLighting()
            GlStateManager.translate(-x, -y, -z)

            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
            )
            val tessellator: Tessellator = Tessellator.getInstance()
            val bb: BufferBuilder = tessellator.getBuffer()
            //GalaxySpace.instance.debug(start1 + " | " + end1 + " | " + posDiff);
            bb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
            bb.pos(x, y, z).color(1f, 0f, 0f, 1f).endVertex()
            bb.pos(x + posDiff.x, y + posDiff.y, z + posDiff.z).color(1f, 1f, 1f, 0.5f).endVertex()
            tessellator.draw()

            GlStateManager.disableBlend()
            GlStateManager.enableLighting()
            GlStateManager.enableTexture2D()
            GlStateManager.popMatrix()
        }
    }
}
