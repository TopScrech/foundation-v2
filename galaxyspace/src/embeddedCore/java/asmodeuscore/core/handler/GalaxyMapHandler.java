package asmodeuscore.core.handler;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import asmodeuscore.core.astronomy.BodiesData;
import asmodeuscore.core.astronomy.BodiesRegistry;
import asmodeuscore.core.astronomy.gui.screen.AC_GuiCelestialSelection;
import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection;
import asmodeuscore.core.proxy.ClientProxy;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.core.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GalaxyMapHandler
{
	
	private static final ResourceLocation saturnRingTexture = new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/saturn_rings.png");    
    private static final ResourceLocation uranusRingTexture = new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/uranus_rings.png");
	//private static final ResourceLocation acrdisk = new ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/acrdisk.png");
	
    final Minecraft minecraft = FMLClientHandler.instance().getClient();
    public Random rand;

   
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRingRender(CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre renderEvent)
    {
    	if(renderEvent.celestialBody.getRingColorR() == 1.1F
    			&& renderEvent.celestialBody.getRingColorG() == 0.0F
    			&& renderEvent.celestialBody.getRingColorB() == 0.0F) 
    	{
    		this.RingRender(renderEvent, renderEvent.celestialBody, 0.7F, 0.0F, 0.0F);
    	}
    	
    	
    	if(renderEvent.celestialBody.getRingColorG() == 1.1F
    			&& renderEvent.celestialBody.getRingColorB() == 0.0F
    			&& renderEvent.celestialBody.getRingColorR() == 0.0F) {
            this.RingRender(renderEvent, renderEvent.celestialBody, 0.0F, 0.7F, 0.0F);
        }
    	
    	if(renderEvent.celestialBody.getRingColorB() == 1.1F
    			&& renderEvent.celestialBody.getRingColorG() == 0.0F
    			&& renderEvent.celestialBody.getRingColorR() == 0.0F) {
    		this.RingRender(renderEvent, renderEvent.celestialBody, 0.0F, 0.0F, 0.7F);
    	}

    	if(renderEvent.celestialBody.getRingColorR() == 0.0F
    			&& renderEvent.celestialBody.getRingColorG() == 0.0F
    			&& renderEvent.celestialBody.getRingColorB() == 0.0F) {
            this.RingRenderNull(renderEvent, renderEvent.celestialBody);
        }
    	
    	if(renderEvent.celestialBody instanceof Star && ClientProxy.renderRingZones) {
    		this.RingRenderHabitableZone(renderEvent);
    	}
    	
    }
    
    public void RingRenderNull(CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre renderEvent, CelestialBody aroundBody)
    {
    	renderEvent.setCanceled(true);
    }
    
    public void RingRenderHabitableZone(CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre renderEvent)
    {
    	BodiesData data = BodiesRegistry.getData(renderEvent.celestialBody);
    	if(data == null) return;
    	
    	Vector3f mapPos = renderEvent.parentOffset;
    	float xOffset = (float) mapPos.x;
        float yOffset = (float) mapPos.y;
        
        float sum = data.getStarHZoneDistance() - data.getStarHZoneSize();
        
        renderEvent.setCanceled(true); 
        
        final float theta = (float) (2 * Math.PI / 90);
        final float cos = (float) Math.cos(theta);
        final float sin = (float) Math.sin(theta);
        
        float min = 72.0F;
        float max = 78.0F;
        
        float x = max * (data.getStarHZoneDistance() + data.getStarHZoneSize());
        float y = 0;

        float temp;
        GL11.glColor4f(0.0F, 0.7F, 0.0F, 0.5F);
        //OUTER LINE
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for (int i = 0; i < 90; i++) {

			GL11.glVertex2f(x + xOffset, y + yOffset);

			temp = x;
			x = cos * x - sin * y;
			y = sin * temp + cos * y;

		}

		GL11.glEnd();
		//INNER LINE
		GL11.glBegin(GL11.GL_LINE_LOOP);
		x = min * (data.getStarHZoneDistance() - data.getStarHZoneSize());
		y = 0;

		for (int i = 0; i < 90; i++) {
			GL11.glVertex2f(x + xOffset, y + yOffset);

			temp = x;
			x = cos * x - sin * y;
			y = sin * temp + cos * y;
		}
		GL11.glEnd();
		//IN LINES
		GL11.glColor4f(0.0F, 0.7F, 0.0F, 0.1F);
		GL11.glBegin(GL11.GL_QUADS);
		x = min * (data.getStarHZoneDistance() - data.getStarHZoneSize());
		y = 0;
		float x2 = max * (data.getStarHZoneDistance() + data.getStarHZoneSize());
		float y2 = 0;

		for (int i = 0; i < 90; i++) {

			GL11.glVertex2f(x2 + xOffset, y2 + yOffset);
			GL11.glVertex2f(x + xOffset, y + yOffset);

			temp = x;
			x = cos * x - sin * y;
			y = sin * temp + cos * y;
			temp = x2;
			x2 = cos * x2 - sin * y2;
			y2 = sin * temp + cos * y2;

			GL11.glVertex2f(x + xOffset, y + yOffset);
			GL11.glVertex2f(x2 + xOffset, y2 + yOffset);
		}
		GL11.glEnd();
    }
    
    public void RingRender(CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre renderEvent, CelestialBody aroundBody, float par1, float par2, float par3)
    {
    	Vector3f mapPos = renderEvent.parentOffset;
    	
    	float sum = renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance - renderEvent.celestialBody.getRelativeDistanceFromCenter().scaledDistance;
        
        float xOffset = (float) mapPos.x;
        float yOffset = (float) mapPos.y;
        
           
        if (FMLClientHandler.instance().getClient().currentScreen instanceof AC_GuiCelestialSelection || FMLClientHandler.instance().getClient().currentScreen instanceof NewGuiCelestialSelection)
        	GL11.glColor4f(par1, par2, par3, 0.5F);
        else
        	GL11.glColor4f(0.3F, 0.1F, 0.1F, 0.0F);
        	
        renderEvent.setCanceled(true);           
            
        final float theta = (float) (2 * Math.PI / 90);
        final float cos = (float) Math.cos(theta);
        final float sin = (float) Math.sin(theta);

        float min = 72.0F;
        float max = 78.0F;

        if(aroundBody instanceof Planet) {
        	min = 72.F;
        	max = 78.F;
        } else if(aroundBody instanceof Moon) {
        	max = 1 / 1.5F;
        	min = 1 / 1.9F;     
        }
            
        float x = max * (renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance + sum);
        float y = 0;

        float temp;
        //OUTER LINE
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for (int i = 0; i < 90; i++) {

			GL11.glVertex2f(x + xOffset, y + yOffset);

			temp = x;
			x = cos * x - sin * y;
			y = sin * temp + cos * y;

		}

		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINE_LOOP);
		x = min * (renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance - sum);
		y = 0;

		for (int i = 0; i < 90; i++) {
			GL11.glVertex2f(x + xOffset, y + yOffset);

			temp = x;
			x = cos * x - sin * y;
			y = sin * temp + cos * y;
		}
		GL11.glEnd();
		GL11.glColor4f(par1, par2, par3, 0.1F);

		GL11.glBegin(GL11.GL_QUADS);
		x = min * (renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance - sum);
		y = 0;
		float x2 = max * (renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance + sum);
		float y2 = 0;

		for (int i = 0; i < 90; i++) {

			GL11.glVertex2f(x2 + xOffset, y2 + yOffset);
			GL11.glVertex2f(x + xOffset, y + yOffset);

			temp = x;
			x = cos * x - sin * y;
			y = sin * temp + cos * y;
			temp = x2;
			x2 = cos * x2 - sin * y2;
			y2 = sin * temp + cos * y2;

			GL11.glVertex2f(x + xOffset, y + yOffset);
			GL11.glVertex2f(x2 + xOffset, y2 + yOffset);
		}
		GL11.glEnd();
       
	}
  }