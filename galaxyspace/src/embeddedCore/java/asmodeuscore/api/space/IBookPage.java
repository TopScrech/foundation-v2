package asmodeuscore.api.space;

import org.lwjgl.opengl.GL11;

import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IBookPage {

	public String titlePage();
	public ResourceLocation iconTitle();
	public void drawPage(int x, int y, FontRenderer font, int mouseX, int mouseY);
	public void mouseClick(int mouseX, int mouseY, int mouseButton, int x, int z);	
	public String getCategory();
		
	public default ItemStack itemIcon()
	{
		return ItemStack.EMPTY;
	}
	
	public default boolean hookBackButton()
	{
		return false;
	}
	
	public default boolean rawTitle()
	{
		return false;
	}
	
	public default void drawText(String text, int x, int y, int pos, FontRenderer font)
	{
		drawText(text, x, y, pos, 360, font);
	}
	
	public default void drawText(String text, int x, int y, int pos, int size, FontRenderer font)
	{
		//String str = Utils.translate("book.page." + titlePage() + ".text");
		String[] str1 = text.split("!n");
		
		for(int i = pos; i < str1.length; i++)
		{
			String strings = str1[i];
			strings = strings.replace("!c_o", EnumColor.ORANGE + "");
			strings = strings.replace("!c_w", EnumColor.WHITE + "");
			strings = strings.replace("!c_r", EnumColor.RED + "");
			strings = strings.replace("!c_y", EnumColor.YELLOW + "");
			strings = strings.replace("!c_g", EnumColor.BRIGHT_GREEN + "");
			
			font.drawSplitString(strings, x + 40, y + 90 + (10 * (i - pos)), size, 0xFFFFFF);			
		}
	}
	
	public default void drawItemStack(Minecraft mc, ItemStack stack, int x, int y, int mouseX, int mouseY, boolean tooltip)
	{
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		if(tooltip)
		{
			if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16)			
				this.drawToolTip(mc, mouseX, y, stack.getDisplayName()); 
			
		}
		RenderHelper.disableStandardItemLighting();  
	}
	
	public default void drawTexturedModalRect(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, boolean invertX, boolean invertY)
    {
        this.drawTexturedModalRect(x, y, width, height, u, v, uWidth, vHeight, invertX, invertY, 512, 512);
    }

    public default void drawTexturedModalRect(float x, float y, float width, float height, float u, float v, float uWidth, float vHeight, boolean invertX, boolean invertY, float texSizeX, float texSizeY)
    {
    	float zLevel = 0.0F;

        float texModX = 1F / texSizeX;
        float texModY = 1F / texSizeY;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        float height0 = invertY ? 0 : vHeight;
        float height1 = invertY ? vHeight : 0;
        float width0 = invertX ? uWidth : 0;
        float width1 = invertX ? 0 : uWidth;
        vertexbuffer.pos(x, y + height, zLevel).tex((u + width0) * texModX, (v + height0) * texModY).endVertex();
        vertexbuffer.pos(x + width, y + height, zLevel).tex((u + width1) * texModX, (v + height0) * texModY).endVertex();
        vertexbuffer.pos(x + width, y, zLevel).tex((u + width1) * texModX, (v + height1) * texModY).endVertex();
        vertexbuffer.pos(x, y, zLevel).tex((u + width0) * texModX, (v + height1) * texModY).endVertex();
        tessellator.draw();
    }
    
    public default void drawToolTip(Minecraft mc, int mousePosX, int mousePosY, String tooltip)
	{
		this.drawToolTip(mc, mousePosX, mousePosY, tooltip, mc.fontRenderer.getStringWidth(tooltip), 8);
	}
	
    public default void drawToolTip(Minecraft mc, int mousePosX, int mousePosY, String tooltip, int width, int height)
	{
		GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 300);
        int k = width;
        int j2 = mousePosX - k / 2;
        int k2 = mousePosY - 12;
        int i1 = height;
/*
        if (j2 + k > width)
        {
            j2 -= (j2 - width + k);
        }

        if (k2 + i1 + 6 > height)
        {
            k2 = height - i1 - 6;
        }
*/
        int j1 = ColorUtil.to32BitColor(190, 0, 153, 255);
        this.drawGradientRect1(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
        this.drawGradientRect1(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
        this.drawGradientRect1(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
        this.drawGradientRect1(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
        this.drawGradientRect1(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
        int k1 = ColorUtil.to32BitColor(170, 0, 153, 255);
        int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
        this.drawGradientRect1(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
        this.drawGradientRect1(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
        this.drawGradientRect1(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
        this.drawGradientRect1(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

        mc.fontRenderer.drawSplitString(tooltip, j2, k2, 150, ColorUtil.to32BitColor(255, 255, 255, 255));

        GL11.glPopMatrix();   
	}
	
    public default void drawGradientRect1(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        float zLevel = 0.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
