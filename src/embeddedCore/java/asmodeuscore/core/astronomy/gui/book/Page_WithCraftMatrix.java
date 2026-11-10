package asmodeuscore.core.astronomy.gui.book;

import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class Page_WithCraftMatrix extends Page_WithScroll {
   protected Minecraft mc = Minecraft.getMinecraft();
   protected static final ResourceLocation bookPageTexture = new ResourceLocation("asmodeuscore", "textures/gui/tablet.png");

   @Override
   public abstract String titlePage();

   @Override
   public abstract ResourceLocation iconTitle();

   @Override
   public abstract int getMaxScroll();

   @Override
   public abstract String getCategory();

   public abstract ItemStack getItem();

   @Override
   public ItemStack itemIcon() {
      return this.getItem();
   }

   public ItemStack[] getRecipe() {
      IRecipe rec = CraftingManager.getRecipe((ResourceLocation)Item.REGISTRY.getNameForObject(this.getItem().getItem()));
      ItemStack[] items = new ItemStack[9];
      if (rec != null) {
         int i = 0;

         for (Ingredient s : rec.getIngredients()) {
            items[i] = s.getMatchingStacks()[0];
            i++;
         }
      }

      return items;
   }

   public abstract Page_WithCraftMatrix.Recipe_Type getRecipeType();

   @Override
   public void drawPage(int x, int y, FontRenderer font, int mouseX, int mouseY) {
      if (this.getScroll() > 0) {
         super.drawPage(x, y, font, mouseX, mouseY);
      }

      RenderHelper.enableGUIStandardItemLighting();
      this.mc.getRenderItem().renderItemAndEffectIntoGUI(this.getRecipeType().getItem(), x + 265, y + 47);
      RenderHelper.disableStandardItemLighting();
      this.drawText("Recipe for: " + this.getRecipeType().getItem().getDisplayName(), x + 248, y - 40, 0, font);
      if ((this.getRecipeType() == Page_WithCraftMatrix.Recipe_Type.CRAFTING_TABLE || this.getRecipeType() == Page_WithCraftMatrix.Recipe_Type.COMPRESSOR)
         && this.getRecipe().length > 0) {
         this.drawText("Components: ", x + 275, y - 25, 0, font);
         this.mc.renderEngine.bindTexture(bookPageTexture);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

         for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
               if (this.checkClick(mouseX, mouseY, x + 310 + k * 25, y + 82 + i * 25, 16, 16)) {
                  this.drawTexturedModalRect(
                     (float)(x + 308 + k * 25), (float)(y + 80 + i * 25), 20.0F, 20.0F, 473.0F, 94.0F, 20.0F, 20.0F, false, false, 576.0F, 250.0F
                  );
               } else {
                  this.drawTexturedModalRect(
                     (float)(x + 308 + k * 25), (float)(y + 80 + i * 25), 20.0F, 20.0F, 452.0F, 94.0F, 20.0F, 20.0F, false, false, 576.0F, 250.0F
                  );
               }
            }
         }

         int z = 0;
         int j = 0;

         for (ItemStack s : this.getRecipe()) {
            RenderHelper.enableGUIStandardItemLighting();
            this.mc.getRenderItem().renderItemAndEffectIntoGUI(s, x + 310 + j * 25, y + 82 + z * 25);
            RenderHelper.disableStandardItemLighting();
            if (this.checkClick(mouseX, mouseY, x + 310 + j * 25, y + 82 + z * 25, 16, 16)) {
               this.drawToolTip(x + 308 + j * 25, y + 80 + z * 25, s.getDisplayName());
            }

            if (++j % 3 == 0) {
               z++;
               j = 0;
            }
         }

         this.mc.getTextureManager().bindTexture(bookPageTexture);
         this.drawTexturedModalRect((float)(x + 257), (float)(y + 36), 4.0F, 188.0F, 492.0F, 10.0F, 14.0F, 160.0F, false, false, 512.0F, 256.0F);
      }

      String str = GCCoreUtil.translate("book.page." + this.titlePage() + ".text");
      this.drawText(str, x, y - 40, this.getScroll(), font);
   }

   protected boolean checkClick(int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
      return mouseX >= x && mouseX <= x + sizeX && mouseY >= y && mouseY <= y + sizeY;
   }

   protected void drawToolTip(int mousePosX, int mousePosY, String tooltip) {
      this.drawToolTip(this.mc, mousePosX, mousePosY, tooltip, this.mc.fontRenderer.getStringWidth(tooltip), 8);
   }

   public static enum Recipe_Type {
      CRAFTING_TABLE(new ItemStack(Blocks.CRAFTING_TABLE)),
      NASA_WORKBENCH(new ItemStack(GCBlocks.nasaWorkbench)),
      COMPRESSOR(new ItemStack(GCBlocks.machineBase, 1, 12));

      private ItemStack item;

      private Recipe_Type(ItemStack item) {
         this.item = item;
      }

      public ItemStack getItem() {
         return this.item;
      }
   }
}
