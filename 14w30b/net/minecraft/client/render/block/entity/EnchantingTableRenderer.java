package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.model.block.entity.EnchantingTableBookModel;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnchantingTableRenderer extends BlockEntityRenderer {
   private static final Identifier BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
   private EnchantingTableBookModel bookModel = new EnchantingTableBookModel();

   public void render(EnchantingTableBlockEntity c_14wsjyqnx, double d, double e, double f, float g, int i) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)d + 0.5F, (float)e + 0.75F, (float)f + 0.5F);
      float var10 = (float)c_14wsjyqnx.ticks + g;
      GlStateManager.translatef(0.0F, 0.1F + MathHelper.sin(var10 * 0.1F) * 0.01F, 0.0F);
      float var11 = c_14wsjyqnx.pageRotation - c_14wsjyqnx.lastPageRotation;

      while(var11 >= (float) Math.PI) {
         var11 -= (float) (Math.PI * 2);
      }

      while(var11 < (float) -Math.PI) {
         var11 += (float) (Math.PI * 2);
      }

      float var12 = c_14wsjyqnx.lastPageRotation + var11 * g;
      GlStateManager.rotatef(-var12 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(BOOK_TEXTURE);
      float var13 = c_14wsjyqnx.lastPageAngle + (c_14wsjyqnx.pageAngle - c_14wsjyqnx.lastPageAngle) * g + 0.25F;
      float var14 = c_14wsjyqnx.lastPageAngle + (c_14wsjyqnx.pageAngle - c_14wsjyqnx.lastPageAngle) * g + 0.75F;
      var13 = (var13 - (float)MathHelper.fastFloor((double)var13)) * 1.6F - 0.3F;
      var14 = (var14 - (float)MathHelper.fastFloor((double)var14)) * 1.6F - 0.3F;
      if (var13 < 0.0F) {
         var13 = 0.0F;
      }

      if (var14 < 0.0F) {
         var14 = 0.0F;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      if (var14 > 1.0F) {
         var14 = 1.0F;
      }

      float var15 = c_14wsjyqnx.lastPageTurningSpeed + (c_14wsjyqnx.pageTurningSpeed - c_14wsjyqnx.lastPageTurningSpeed) * g;
      GlStateManager.enableCull();
      this.bookModel.render(null, var10, var13, var14, var15, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
