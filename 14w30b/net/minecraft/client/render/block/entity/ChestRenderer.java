package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.render.model.block.entity.ChestModel;
import net.minecraft.client.render.model.block.entity.LargeChestModel;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChestRenderer extends BlockEntityRenderer {
   private static final Identifier DOUBLE_TRAPPED_CHEST_TEXTURE = new Identifier("textures/entity/chest/trapped_double.png");
   private static final Identifier CHRISTMAS_DOUBLE_CHEST_TEXTURE = new Identifier("textures/entity/chest/christmas_double.png");
   private static final Identifier NORMAL_DOUBLE_CHEST_TEXTURE = new Identifier("textures/entity/chest/normal_double.png");
   private static final Identifier TRAPPED_CHEST_TEXTURE = new Identifier("textures/entity/chest/trapped.png");
   private static final Identifier CHRISTMAS_CHEST_TEXTURE = new Identifier("textures/entity/chest/christmas.png");
   private static final Identifier CHEST_TEXTURE = new Identifier("textures/entity/chest/normal.png");
   private ChestModel singleChestModel = new ChestModel();
   private ChestModel doubleChestModel = new LargeChestModel();
   private boolean isChristmas;

   public ChestRenderer() {
      Calendar var1 = Calendar.getInstance();
      if (var1.get(2) + 1 == 12 && var1.get(5) >= 24 && var1.get(5) <= 26) {
         this.isChristmas = true;
      }
   }

   public void render(ChestBlockEntity c_43webfllv, double d, double e, double f, float g, int i) {
      int var10;
      if (!c_43webfllv.hasWorld()) {
         var10 = 0;
      } else {
         Block var11 = c_43webfllv.getCachedBlock();
         var10 = c_43webfllv.getCachedMetadata();
         if (var11 instanceof ChestBlock && var10 == 0) {
            ((ChestBlock)var11).updateState(c_43webfllv.getWorld(), c_43webfllv.getPos(), c_43webfllv.getWorld().getBlockState(c_43webfllv.getPos()));
            var10 = c_43webfllv.getCachedMetadata();
         }

         c_43webfllv.updateShape();
      }

      if (c_43webfllv.northNeighbor == null && c_43webfllv.westNeighbor == null) {
         ChestModel var15;
         if (c_43webfllv.eastNeighbor == null && c_43webfllv.southNeighbor == null) {
            var15 = this.singleChestModel;
            if (i >= 0) {
               this.bindTexture(MINING_PROGRESS_TEXTURES[i]);
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               GlStateManager.scalef(4.0F, 4.0F, 1.0F);
               GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.matrixMode(5888);
            } else if (c_43webfllv.getChestType() == 1) {
               this.bindTexture(TRAPPED_CHEST_TEXTURE);
            } else if (this.isChristmas) {
               this.bindTexture(CHRISTMAS_CHEST_TEXTURE);
            } else {
               this.bindTexture(CHEST_TEXTURE);
            }
         } else {
            var15 = this.doubleChestModel;
            if (i >= 0) {
               this.bindTexture(MINING_PROGRESS_TEXTURES[i]);
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               GlStateManager.scalef(8.0F, 4.0F, 1.0F);
               GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.matrixMode(5888);
            } else if (c_43webfllv.getChestType() == 1) {
               this.bindTexture(DOUBLE_TRAPPED_CHEST_TEXTURE);
            } else if (this.isChristmas) {
               this.bindTexture(CHRISTMAS_DOUBLE_CHEST_TEXTURE);
            } else {
               this.bindTexture(NORMAL_DOUBLE_CHEST_TEXTURE);
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.enableRescaleNormal();
         if (i < 0) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         }

         GlStateManager.translatef((float)d, (float)e + 1.0F, (float)f + 1.0F);
         GlStateManager.scalef(1.0F, -1.0F, -1.0F);
         GlStateManager.translatef(0.5F, 0.5F, 0.5F);
         short var12 = 0;
         if (var10 == 2) {
            var12 = 180;
         }

         if (var10 == 3) {
            var12 = 0;
         }

         if (var10 == 4) {
            var12 = 90;
         }

         if (var10 == 5) {
            var12 = -90;
         }

         if (var10 == 2 && c_43webfllv.eastNeighbor != null) {
            GlStateManager.translatef(1.0F, 0.0F, 0.0F);
         }

         if (var10 == 5 && c_43webfllv.southNeighbor != null) {
            GlStateManager.translatef(0.0F, 0.0F, -1.0F);
         }

         GlStateManager.rotatef((float)var12, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
         float var13 = c_43webfllv.lastAnimationProgress + (c_43webfllv.animationProgress - c_43webfllv.lastAnimationProgress) * g;
         if (c_43webfllv.northNeighbor != null) {
            float var14 = c_43webfllv.northNeighbor.lastAnimationProgress
               + (c_43webfllv.northNeighbor.animationProgress - c_43webfllv.northNeighbor.lastAnimationProgress) * g;
            if (var14 > var13) {
               var13 = var14;
            }
         }

         if (c_43webfllv.westNeighbor != null) {
            float var18 = c_43webfllv.westNeighbor.lastAnimationProgress
               + (c_43webfllv.westNeighbor.animationProgress - c_43webfllv.westNeighbor.lastAnimationProgress) * g;
            if (var18 > var13) {
               var13 = var18;
            }
         }

         var13 = 1.0F - var13;
         var13 = 1.0F - var13 * var13 * var13;
         var15.lid.rotationX = -(var13 * (float) Math.PI / 2.0F);
         var15.renderParts();
         GlStateManager.disableRescaleNormal();
         GlStateManager.popMatrix();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (i >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
         }
      }
   }
}
