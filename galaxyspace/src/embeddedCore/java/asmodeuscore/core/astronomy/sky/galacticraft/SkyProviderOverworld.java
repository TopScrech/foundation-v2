package asmodeuscore.core.astronomy.sky.galacticraft;

import org.lwjgl.opengl.GL11;

import asmodeuscore.api.dimension.IAdvancedSpace.StarColor;
import asmodeuscore.core.astronomy.sky.SkyProviderBase;
import asmodeuscore.core.configs.AsmodeusConfig;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class SkyProviderOverworld extends SkyProviderBase {

	private static final ResourceLocation moonTexture = new ResourceLocation("textures/environment/moon_phases.png");

	@Override
	protected void rendererSky(Tessellator tessellator, BufferBuilder worldRenderer, float f10, float ticks) {
		
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		
		//GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		
		float f24 = 1.0F - this.mc.world.getRainStrength(ticks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, f24);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);        
        GlStateManager.rotate(this.mc.world.getCelestialAngle(ticks) * 360.0F, 1.0F, 0.0F, 0.0F);
        
		f10 = 20.0F;
		mc.renderEngine.bindTexture(this.moonTexture);
		int k = mc.world.getMoonPhase();
		int l = k % 4;
		int i1 = k / 4 % 2;
		float f14 = (float) (l + 0) / 4.0F;
		float f15 = (float) (i1 + 0) / 2.0F;
		float f16 = (float) (l + 1) / 4.0F;
		float f17 = (float) (i1 + 1) / 2.0F;
		
		long worldtime = mc.world.getWorldTime();
		double offangle = worldtime % 192000.0D * 2.0D * 3.141592653589793D / 192000.0D + 2.748893571891069D;
		double moony = 100.0D * Math.cos(offangle);
		double moonz = -100.0D * Math.sin(offangle);
		double moonx = 30.0D * Math.cos(offangle / 12.0D);
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		if(AsmodeusConfig.enableBetterMoonOnSky) {
			worldRenderer.pos(moonx - f10, moony - f10 * Math.sin(offangle), moonz - f10 * Math.cos(offangle)).tex(f14, f17).endVertex();
			worldRenderer.pos(moonx + f10, moony - f10 * Math.sin(offangle), moonz - f10 * Math.cos(offangle)).tex(f14, f15).endVertex();
			worldRenderer.pos(moonx + f10, moony + f10 * Math.sin(offangle), moonz + f10 * Math.cos(offangle)).tex(f16, f15).endVertex();
			worldRenderer.pos(moonx - f10, moony + f10 * Math.sin(offangle), moonz + f10 * Math.cos(offangle)).tex(f16, f17).endVertex();
		} else {
			worldRenderer.pos(-f10, -100.0D, f10).tex(f16, f17).endVertex();
			worldRenderer.pos(f10, -100.0D, f10).tex(f14, f17).endVertex();
			worldRenderer.pos(f10, -100.0D, -f10).tex(f14, f15).endVertex();
			worldRenderer.pos(-f10, -100.0D, -f10).tex(f16, f15).endVertex();	
			
		}
		tessellator.draw();
	
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
	
	@Override
	protected boolean enableBaseImages() {
		return true;
	}

	@Override
	protected float sunSize() {
		return 19.5F;
	}

	@Override
	protected boolean enableStar() {
		return true;
	}

	@Override
	protected ResourceLocation sunImage() {
		return new ResourceLocation("textures/environment/sun.png");
	}

	@Override
	protected ModeLight modeLight() {
		return ModeLight.DEFAULT;
	}

	@Override
	protected StarColor colorSunAura() {
		return null;
	}

	@Override
	protected Vector3 getAtmosphereColor() {
		float f = mc.world.getSunBrightness(ticks) + 0.2F;
		return new Vector3(120 / 255.0F * f, 120 / 255.0F * f, 160 / 255.0F * f);
	}
	
	@Override
	public boolean enableLargeSunAura() {return false;}
	
	@Override
	protected StarCount getStarCount() {return StarCount.SMALL;}
}
