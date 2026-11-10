package galaxyspace.core.client.gui.book.pages

import asmodeuscore.core.astronomy.gui.book.Page_WithCraftMatrix
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.AssemblyRecipes
import micdoodle8.mods.galacticraft.api.recipe.ShapelessOreRecipeGC
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import org.lwjgl.opengl.GL11

abstract class Page_AssemblerCraft : Page_WithCraftMatrix() {
    override fun getRecipe(): Array<ItemStack> {
        val rec: MutableList<IRecipe?> = (AssemblyRecipes.getRecipes((getItem())!!) as MutableList<IRecipe?>)

        val items: Array<ItemStack?> = arrayOfNulls<ItemStack>(9)

        if (rec.get(0) is ShapelessOreRecipeGC) {
            var i: Int = 0
            for (s in (rec.get(0) as ShapelessOreRecipeGC).getInput()) {
                if (s is ItemStack) items[i] = s
                else {
                    items[i] = (s as NonNullList<ItemStack>).get(0)
                }

                i++
            }
        }
        return (items as Array<ItemStack>)
    }

    override fun drawPage(x: Int, y: Int, font: FontRenderer?, mouseX: Int, mouseY: Int) {
        super.drawPage(x, y, font, mouseX, mouseY)

        if (getRecipe().size > 0) {
            this.drawText("Components: ", x + 275, y - 25, 0, (font)!!)

            this.mc.renderEngine.bindTexture(bookPageTexture)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

            for (i: Int in 0..2) for (k: Int in 0..2) {
                if (this.checkClick(
                        mouseX,
                        mouseY,
                        x + 310 + (k * 25),
                        y + 82 + (i * 25),
                        16,
                        16
                    )
                ) this.drawTexturedModalRect(
                    (x + 308 + (k * 25)).toFloat(),
                    (y + 80 + (i * 25)).toFloat(),
                    20f,
                    20f,
                    473f,
                    94f,
                    20f,
                    20f,
                    false,
                    false,
                    576f,
                    250f
                )
                else this.drawTexturedModalRect(
                    (x + 308 + (k * 25)).toFloat(),
                    (y + 80 + (i * 25)).toFloat(),
                    20f,
                    20f,
                    452f,
                    94f,
                    20f,
                    20f,
                    false,
                    false,
                    576f,
                    250f
                )
            }


            var z: Int = 0
            var j: Int = 0


            //GalaxySpace.debug(getRecipe()+ "");
            for (s: ItemStack? in getRecipe()) {
                if (s == null) continue

                RenderHelper.enableGUIStandardItemLighting()
                this.mc.getRenderItem().renderItemAndEffectIntoGUI(s, x + 310 + (j * 25), y + 82 + (z * 25))
                RenderHelper.disableStandardItemLighting()

                if (this.checkClick(
                        mouseX,
                        mouseY,
                        x + 310 + (j * 25),
                        y + 82 + (z * 25),
                        16,
                        16
                    )
                ) this.drawToolTip(x + 308 + (j * 25), y + 80 + (z * 25), s.getDisplayName())

                j++
                if (j % 3 == 0) {
                    z++
                    j = 0
                }
            }

            this.mc.getTextureManager().bindTexture(bookPageTexture)
            drawTexturedModalRect(
                (x + 257).toFloat(),
                (y + 36).toFloat(),
                4f,
                188f,
                492f,
                10f,
                14f,
                160f,
                false,
                false,
                512f,
                256f
            )
        }
        val str: String? = GCCoreUtil.translate("book.page." + titlePage() + ".text")
        this.drawText((str)!!, x, y - 40, this.getScroll(), (font)!!)
    }
}
