package micdoodle8.mods.galacticraft.core.client.model

import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelBox
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.Tessellator
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

open class ModelRendererGC constructor(par1ModelBase: ModelBase, par2: Int, par3: Int) :
    ModelRenderer(par1ModelBase, par2, par3) {
    private var compiled = false
    private var displayList = 0

    @OnlyIn(Dist.CLIENT)
    override fun render(par1: Float) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(par1)
                }

                GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ)
                var i: Int

                if (this.rotateAngleX == 0.0f && this.rotateAngleY == 0.0f && this.rotateAngleZ == 0.0f) {
                    if (this.rotationPointX == 0.0f && this.rotationPointY == 0.0f && this.rotationPointZ == 0.0f) {
                        GL11.glCallList(this.displayList)

                        if (this.childModels != null) {
                            i = 0
                            while (i < this.childModels.size) {
                                (this.childModels.get(i) as ModelRenderer).render(par1)
                                ++i
                            }
                        }
                    } else {
                        GL11.glTranslatef(
                            this.rotationPointX * par1,
                            this.rotationPointY * par1,
                            this.rotationPointZ * par1
                        )
                        GL11.glCallList(this.displayList)

                        if (this.childModels != null) {
                            i = 0
                            while (i < this.childModels.size) {
                                (this.childModels.get(i) as ModelRenderer).render(par1)
                                ++i
                            }
                        }

                        GL11.glTranslatef(
                            -this.rotationPointX * par1,
                            -this.rotationPointY * par1,
                            -this.rotationPointZ * par1
                        )
                    }
                } else {
                    GL11.glPushMatrix()
                    GL11.glTranslatef(
                        this.rotationPointX * par1,
                        this.rotationPointY * par1,
                        this.rotationPointZ * par1
                    )

                    if (this.rotateAngleY != 0.0f) {
                        GL11.glRotatef(this.rotateAngleY * Constants.RADIANS_TO_DEGREES, 0.0f, 1.0f, 0.0f)
                    }
                    //
                    if (this.rotateAngleZ != 0.0f) {
                        GL11.glRotatef(this.rotateAngleZ * Constants.RADIANS_TO_DEGREES, 0.0f, 0.0f, 1.0f)
                    }

                    if (this.rotateAngleX != 0.0f) {
                        GL11.glRotatef(this.rotateAngleX * Constants.RADIANS_TO_DEGREES, 1.0f, 0.0f, 0.0f)
                    }

                    GL11.glCallList(this.displayList)

                    if (this.childModels != null) {
                        i = 0
                        while (i < this.childModels.size) {
                            (this.childModels.get(i) as ModelRenderer).render(par1)
                            ++i
                        }
                    }

                    GL11.glPopMatrix()
                }

                GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ)
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private fun compileDisplayList(par1: Float) {
        this.displayList = GLAllocation.generateDisplayLists(1)
        GL11.glNewList(this.displayList, GL11.GL_COMPILE)
        val tessellator: Tessellator = Tessellator.getInstance()

        for (i: Int in this.cubeList.indices) {
            (this.cubeList.get(i) as ModelBox).render(tessellator.getBuffer(), par1)
        }

        GL11.glEndList()
        this.compiled = true
    }
}
