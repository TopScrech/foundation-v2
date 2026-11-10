package asmodeuscore.core.astronomy.sky;

import org.lwjgl.opengl.GL11;

import asmodeuscore.core.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CloudProviderBase extends IRenderHandler {
	static Minecraft mc = Minecraft.getMinecraft();
	public long cloudTicks;
	public long cloudTicksPrev;
	public float cloudSpeed = 2F;
	
	public ResourceLocation default_clouds = new ResourceLocation("textures/environment/clouds.png");	
	public Vec3d default_color = new Vec3d(150, 150, 150);
	
	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		if(world.provider.getCloudRenderer() instanceof CloudProviderBase)
		{
			if (mc.gameSettings.shouldRenderClouds() >= 1) {
				GL11.glPushMatrix();				
				
				if (mc.gameSettings.fancyGraphics) 
					GL11.glEnable(GL11.GL_FOG);				

				this.renderClouds(partialTicks);
				GL11.glDisable(GL11.GL_FOG);
				
				GL11.glPopMatrix();
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void renderClouds (float renderPartialTicks) {
		GlStateManager.disableCull();
		Entity entity = mc.getRenderViewEntity();
		float yOffset = (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) renderPartialTicks);
		byte cloudSections = 2;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		double viewX = (entity.prevPosX + (entity.posX - entity.prevPosX) * (double) renderPartialTicks	+ getCloudMovementX(entity.world, cloudTicksPrev, cloudTicks) * 0.029999999329447746D) / 12.0D;
		double viewZ = (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) renderPartialTicks	+ getCloudMovementZ(entity.world, cloudTicksPrev, cloudTicks) * 0.029999999329447746D) / 12.0D;
		float cloudHeight = mc.world.provider.getCloudHeight() - yOffset + 0.33F;
		viewX = viewX - (double) (MathHelper.floor(viewX / 2048.0D) * 2048);
		viewZ = viewZ - (double) (MathHelper.floor(viewZ / 2048.0D) * 2048);
		mc.renderEngine.bindTexture(getCloudTexture());
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		float r = (float) getCloudColor(renderPartialTicks).x / 255F;
		float g = (float) getCloudColor(renderPartialTicks).y / 255F;
		float b = (float) getCloudColor(renderPartialTicks).z / 255F;
		float alpha = (float) (1.0F - (entity.posY / 700));
		if(alpha < 0.0F) alpha = 0.0F;
		
		float f17 = (float) MathHelper.floor(viewX) * 0.00390625F;
		float f18 = (float) MathHelper.floor(viewZ) * 0.00390625F;
		float f19 = (float) (viewX - (double) MathHelper.floor(viewX - 6));
		float f20 = (float) (viewZ - (double) MathHelper.floor(viewZ - 6));
		GlStateManager.scale(12.0F, 1.0F, 12.0F);

		for (int pass = 0; pass < 2; ++pass) {
			if (pass == 0) {
				GL11.glColorMask(false, false, false, false);
			}
			else {
				GL11.glColorMask(true, true, true, true);
			}

			for (int x = -cloudSections + 1; x <= cloudSections; ++x) {
				for (int z = -cloudSections + 1; z <= cloudSections; ++z) {
					vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
					float cU = (float) (x * 8);
					float cV = (float) (z * 8);
					float cX = cU - f19;
					float cZ = cV - f20;

					if (cloudHeight > -5.0F) {
						vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 8.0F))
							.tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18))
							.color(r, g, b, alpha).normal(0.0F, -1.0F, 0.0F).endVertex();
						vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 8.0F))
							.tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18))
							.color(r, g, b, alpha).normal(0.0F, -1.0F, 0.0F).endVertex();
						vertexbuffer.pos((double) (cX + 8.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 0.0F))
							.tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18))
							.color(r, g, b, alpha).normal(0.0F, -1.0F, 0.0F).endVertex();
						vertexbuffer.pos((double) (cX + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 0.0F))
							.tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18))
							.color(r, g, b, alpha).normal(0.0F, -1.0F, 0.0F).endVertex();
					}

					if (cloudHeight <= 5.0F) {
						vertexbuffer
							.pos((double) (cX + 0.0F), (double) (cloudHeight + 4.0F - 9.765625E-4F), (double) (cZ + 8.0F))
							.tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18))
							.color(r, g, b, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
						vertexbuffer
							.pos((double) (cX + 8.0F), (double) (cloudHeight + 4.0F - 9.765625E-4F), (double) (cZ + 8.0F))
							.tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18))
							.color(r, g, b, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
						vertexbuffer
							.pos((double) (cX + 8.0F), (double) (cloudHeight + 4.0F - 9.765625E-4F), (double) (cZ + 0.0F))
							.tex((double) ((cU + 8.0F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18))
							.color(r, g, b, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
						vertexbuffer
							.pos((double) (cX + 0.0F), (double) (cloudHeight + 4.0F - 9.765625E-4F), (double) (cZ + 0.0F))
							.tex((double) ((cU + 0.0F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18))
							.color(r, g, b, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
					}

					if (x > -1) {
						for (int v = 0; v < 8; ++v) {
							vertexbuffer
								.pos((double) (cX + (float) v + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 8.0F))
								.tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18))
								.color(r, g, b, alpha).normal(-1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer
								.pos((double) (cX + (float) v + 0.0F), (double) (cloudHeight + 4.0F), (double) (cZ + 8.0F))
								.tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 8.0F) * 0.00390625F + f18))
								.color(r, g, b, alpha).normal(-1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer
								.pos((double) (cX + (float) v + 0.0F), (double) (cloudHeight + 4.0F), (double) (cZ + 0.0F))
								.tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18))
								.color(r, g, b, alpha).normal(-1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer
								.pos((double) (cX + (float) v + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + 0.0F))
								.tex((double) ((cU + (float) v + 0.5F) * 0.00390625F + f17), (double) ((cV + 0.0F) * 0.00390625F + f18))
								.color(r, g, b, alpha).normal(-1.0F, 0.0F, 0.0F).endVertex();
						}
					}

					if (x <= 1) {
						for (int v = 0; v < 8; ++v) {
							vertexbuffer
									.pos((double) (cX + (float) v + 1.0F - 9.765625E-4F), (double) (cloudHeight
											+ 0.0F), (double) (cZ + 8.0F))
									.tex((double) ((cU + (float) v + 0.5F) * 0.00390625F
											+ f17), (double) ((cV + 8.0F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer
									.pos((double) (cX + (float) v + 1.0F - 9.765625E-4F), (double) (cloudHeight
											+ 4.0F), (double) (cZ + 8.0F))
									.tex((double) ((cU + (float) v + 0.5F) * 0.00390625F
											+ f17), (double) ((cV + 8.0F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer
									.pos((double) (cX + (float) v + 1.0F - 9.765625E-4F), (double) (cloudHeight
											+ 4.0F), (double) (cZ + 0.0F))
									.tex((double) ((cU + (float) v + 0.5F) * 0.00390625F
											+ f17), (double) ((cV + 0.0F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(1.0F, 0.0F, 0.0F).endVertex();
							vertexbuffer
									.pos((double) (cX + (float) v + 1.0F - 9.765625E-4F), (double) (cloudHeight
											+ 0.0F), (double) (cZ + 0.0F))
									.tex((double) ((cU + (float) v + 0.5F) * 0.00390625F
											+ f17), (double) ((cV + 0.0F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(1.0F, 0.0F, 0.0F).endVertex();
						}
					}

					if (z > -1) {
						for (int v = 0; v < 8; ++v) {
							vertexbuffer
									.pos((double) (cX + 0.0F), (double) (cloudHeight + 4.0F), (double) (cZ + (float) v
											+ 0.0F))
									.tex((double) ((cU + 0.0F) * 0.00390625F
											+ f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(0.0F, 0.0F, -1.0F).endVertex();
							vertexbuffer
									.pos((double) (cX + 8.0F), (double) (cloudHeight + 4.0F), (double) (cZ + (float) v
											+ 0.0F))
									.tex((double) ((cU + 8.0F) * 0.00390625F
											+ f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(0.0F, 0.0F, -1.0F).endVertex();
							vertexbuffer
									.pos((double) (cX + 8.0F), (double) (cloudHeight + 0.0F), (double) (cZ + (float) v
											+ 0.0F))
									.tex((double) ((cU + 8.0F) * 0.00390625F
											+ f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(0.0F, 0.0F, -1.0F).endVertex();
							vertexbuffer
									.pos((double) (cX + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + (float) v
											+ 0.0F))
									.tex((double) ((cU + 0.0F) * 0.00390625F
											+ f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(0.0F, 0.0F, -1.0F).endVertex();
						}
					}

					if (z <= 1) {
						for (int v = 0; v < 8; ++v) {
							vertexbuffer
									.pos((double) (cX + 0.0F), (double) (cloudHeight + 4.0F), (double) (cZ + (float) v
											+ 1.0F - 9.765625E-4F))
									.tex((double) ((cU + 0.0F) * 0.00390625F
											+ f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(0.0F, 0.0F, 1.0F).endVertex();
							vertexbuffer
									.pos((double) (cX + 8.0F), (double) (cloudHeight + 4.0F), (double) (cZ + (float) v
											+ 1.0F - 9.765625E-4F))
									.tex((double) ((cU + 8.0F) * 0.00390625F
											+ f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(0.0F, 0.0F, 1.0F).endVertex();
							vertexbuffer
									.pos((double) (cX + 8.0F), (double) (cloudHeight + 0.0F), (double) (cZ + (float) v
											+ 1.0F - 9.765625E-4F))
									.tex((double) ((cU + 8.0F) * 0.00390625F
											+ f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(0.0F, 0.0F, 1.0F).endVertex();
							vertexbuffer
									.pos((double) (cX + 0.0F), (double) (cloudHeight + 0.0F), (double) (cZ + (float) v
											+ 1.0F - 9.765625E-4F))
									.tex((double) ((cU + 0.0F) * 0.00390625F
											+ f17), (double) ((cV + (float) v + 0.5F) * 0.00390625F + f18))
									.color(r, g, b, alpha).normal(0.0F, 0.0F, 1.0F).endVertex();
						}
					}

					tessellator.draw();
				}
			}
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
	}
	
	private double getCloudMovementX(World world, float cloudTicksPrev, float cloudTicks) {
		return Utils.interpolateRotation(cloudTicksPrev, cloudTicks, Minecraft.getMinecraft().getRenderPartialTicks());
	}
	
	private double getCloudMovementZ(World world, float cloudTicksPrev, float cloudTicks) {
		return 3.0;
	}

	
	public abstract float getCloudMovementSpeed(World world);
	
	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getCloudTexture();
	
	@SideOnly(Side.CLIENT)
	public abstract Vec3d getCloudColor(float renderPartialTicks);

}
