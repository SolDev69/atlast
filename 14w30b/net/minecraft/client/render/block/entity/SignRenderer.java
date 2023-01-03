package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.TextRenderUtils;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.model.block.entity.SignModel;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class SignRenderer extends BlockEntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/sign.png");
   private final SignModel model = new SignModel();

   public void render(SignBlockEntity c_76mljyooy, double d, double e, double f, float g, int i) {
      Block var10 = c_76mljyooy.getCachedBlock();
      GlStateManager.pushMatrix();
      float var11 = 0.6666667F;
      if (var10 == Blocks.STANDING_SIGN) {
         GlStateManager.translatef((float)d + 0.5F, (float)e + 0.75F * var11, (float)f + 0.5F);
         float var12 = (float)(c_76mljyooy.getCachedMetadata() * 360) / 16.0F;
         GlStateManager.rotatef(-var12, 0.0F, 1.0F, 0.0F);
         this.model.pole.visible = true;
      } else {
         int var19 = c_76mljyooy.getCachedMetadata();
         float var13 = 0.0F;
         if (var19 == 2) {
            var13 = 180.0F;
         }

         if (var19 == 4) {
            var13 = 90.0F;
         }

         if (var19 == 5) {
            var13 = -90.0F;
         }

         GlStateManager.translatef((float)d + 0.5F, (float)e + 0.75F * var11, (float)f + 0.5F);
         GlStateManager.rotatef(-var13, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
         this.model.pole.visible = false;
      }

      if (i >= 0) {
         this.bindTexture(MINING_PROGRESS_TEXTURES[i]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 2.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(TEXTURE);
      }

      GlStateManager.pushMatrix();
      GlStateManager.scalef(var11, -var11, -var11);
      this.model.render();
      GlStateManager.popMatrix();
      TextRenderer var20 = this.getTextRenderer();
      float var21 = 0.015625F * var11;
      GlStateManager.translatef(0.0F, 0.5F * var11, 0.07F * var11);
      GlStateManager.scalef(var21, -var21, var21);
      GL11.glNormal3f(0.0F, 0.0F, -1.0F * var21);
      GlStateManager.depthMask(false);
      byte var14 = 0;
      if (i < 0) {
         for(int var15 = 0; var15 < c_76mljyooy.lines.length; ++var15) {
            if (c_76mljyooy.lines[var15] != null) {
               Text var16 = c_76mljyooy.lines[var15];
               List var17 = TextRenderUtils.wrapText(var16, 90, var20, false, true);
               String var18 = var17 != null && var17.size() > 0 ? ((Text)var17.get(0)).buildFormattedString() : "";
               if (var15 == c_76mljyooy.currentRow) {
                  var18 = "> " + var18 + " <";
                  var20.drawWithoutShadow(var18, -var20.getStringWidth(var18) / 2, var15 * 10 - c_76mljyooy.lines.length * 5, var14);
               } else {
                  var20.drawWithoutShadow(var18, -var20.getStringWidth(var18) / 2, var15 * 10 - c_76mljyooy.lines.length * 5, var14);
               }
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      if (i >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }
   }
}
