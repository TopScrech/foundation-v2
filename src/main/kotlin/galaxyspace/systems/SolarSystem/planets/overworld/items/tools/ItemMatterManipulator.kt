package galaxyspace.systems.SolarSystem.planets.overworld.items.tools

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.util.EnumColor
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.EnumAction
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
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

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        if (stack.hasTagCompound()) {
            list.add(EnumColor.DARK_RED.toString() + "Timer: " + stack.getTagCompound()!!.getInteger("timer"))
        }
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val stack: ItemStack = player.getHeldItem(hand)
        val ray: RayTraceResult? = getRay(world, player, true) //player.rayTrace(DISTANCE, player.ticksExisted);


        /*if(world.isRemote)
		{
			GalaxySpace.proxy.spawnParticle("beamFX", new Vector3(), new Vector3(), new Object[] { new Vector3(0.0F, 0.0F, 0.0F) });
		}*/
        if (!world.isRemote) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(NBTTagCompound())
                stack.getTagCompound()!!.setInteger("timer", 0)
            }

            stack.getTagCompound()!!.setInteger("timer", stack.getTagCompound()!!.getInteger("timer") + 1)

            val i: Int = stack.getTagCompound()!!.getInteger("timer")


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
                    if (!world.isRemote && block.isBlockNormalCube() && block.getBlock() !== Blocks.BEDROCK) {
                        if (player.inventory.getFirstEmptyStack() > 0) for (stacks: ItemStack in world.getBlockState(pos)
                            .getBlock().getDrops(
                                world,
                                pos,
                                world.getBlockState(pos),
                                0
                            )) player.inventory.addItemStackToInventory(stacks)
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
            stack.getTagCompound()!!.setInteger("timer", 1)
            player.setActiveHand(hand)
            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
        }
        return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase, timeLeft: Int) {
        if (entityLiving is EntityPlayer) {
            val entityplayer: EntityPlayer = entityLiving


            //GalaxySpace.debug("work");
            if (entityplayer.getActiveItemStack().hasTagCompound()) {
                if (entityplayer.getActiveItemStack().getTagCompound()!!.hasKey("timer")) {
                    entityplayer.getActiveItemStack().getTagCompound()!!.setInteger("timer", 0)
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
            val f: Float = player.rotationPitch
            val f1: Float = player.rotationYaw
            val d0: Double = player.posX
            val d1: Double = player.posY + player.getEyeHeight().toDouble()
            val d2: Double = player.posZ
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
        @SideOnly(Side.CLIENT)
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
