package asmodeuscore.core.astronomy.sky.galacticraft;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import org.lwjgl.opengl.GL11;

import asmodeuscore.AsmodeusCore;
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor;
import asmodeuscore.core.astronomy.sky.SkyProviderBase;
import asmodeuscore.core.utils.ACCompatibilityManager;
import galaxyspace.GalaxySpace;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;

public class SkyProviderAsteroids extends SkyProviderBase
{	  
	private static ResourceLocation ceresTexture;
	
	@Override
	protected void rendererSky(Tessellator tessellator, BufferBuilder worldRenderer, float f10, float ticks) {
		
		if(ceresTexture == null && ACCompatibilityManager.isGalaxySpaceLoaded())					
			ceresTexture = new ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/ceres.png");
		
		if(ceresTexture != null) {
	        GL11.glPushMatrix();
	        
			// Render ceres
	        f10 = 4.5F;
	        GL11.glScalef(0.6F, 0.6F, 0.6F);
	        GL11.glRotatef(-170.0F, 50.0F, 1.0F, 0.0F);
	        GL11.glRotatef(90F, 190.0F, 50.0F, 0.0F);
	        GL11.glRotatef(80F, 0.0F, 1.0F, 0.0F);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);
	        FMLClientHandler.instance().getClient().renderEngine.bindTexture(this.ceresTexture);
	        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	        worldRenderer.pos(-f10, -100.0D, f10).tex(0, 1.0).endVertex();
	        worldRenderer.pos(f10, -100.0D, f10).tex(1.0, 1.0).endVertex();
	        worldRenderer.pos(f10, -100.0D, -f10).tex(1.0, 0).endVertex();
	        worldRenderer.pos(-f10, -100.0D, -f10).tex(0, 0).endVertex();
	        tessellator.draw();
	
	        GL11.glPopMatrix();
		}
	}

	@Override
	protected boolean enableBaseImages() {
		return true;
	}

	@Override
	protected float sunSize() {
		return 4.5F;
	}

	@Override
	protected boolean enableStar() {
		return true;
	}

	@Override
	protected ResourceLocation sunImage() {
		return new ResourceLocation(AsmodeusCore.ASSET_PREFIX, "textures/gui/celestialbodies/white.png");
	}

	@Override
	protected ModeLight modeLight() {
		return ModeLight.DEFAULT;
	}

	@Override
	protected StarColor colorSunAura() {
		return StarColor.WHITE;
	}

	@Override
	protected Vector3 getAtmosphereColor() {
		return null;
	}
	
	@Override
	public boolean enableSmoothRender() {return true;}
	
	@Override
	public boolean enableRenderPlanet() {return false;}

}
