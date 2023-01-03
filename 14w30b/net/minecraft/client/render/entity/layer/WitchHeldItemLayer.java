package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.WitchRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.render.model.entity.WitchModel;
import net.minecraft.entity.living.mob.hostile.WitchEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitchHeldItemLayer implements EntityRenderLayer {
   private final WitchRenderer parent;

   public WitchHeldItemLayer(WitchRenderer parent) {
      this.parent = parent;
   }

   public void render(WitchEntity c_12xxtvzpn, float f, float g, float h, float i, float j, float k, float l) {
      ItemStack var9 = c_12xxtvzpn.getStackInHand();
      if (var9 != null) {
         GlStateManager.color3f(1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         if (this.parent.getModel().isBaby) {
            GlStateManager.translatef(0.0F, 0.625F, 0.0F);
            GlStateManager.rotatef(-20.0F, -1.0F, 0.0F, 0.0F);
            float var10 = 0.5F;
            GlStateManager.scalef(var10, var10, var10);
         }

         ((WitchModel)this.parent.getModel()).nose.translate(0.0625F);
         GlStateManager.translatef(-0.0625F, 0.53125F, 0.21875F);
         Item var13 = var9.getItem();
         MinecraftClient var11 = MinecraftClient.getInstance();
         if (var13 instanceof BlockItem && var11.getBlockRenderDispatcher().m_17eyzvzcz(Block.byItem(var13), var9.getMetadata())) {
            GlStateManager.translatef(0.0F, 0.1875F, -0.3125F);
            GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
            float var16 = 0.375F;
            GlStateManager.scalef(var16, -var16, var16);
         } else if (var13 == Items.BOW) {
            GlStateManager.translatef(0.0F, 0.125F, 0.3125F);
            GlStateManager.rotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            float var12 = 0.625F;
            GlStateManager.scalef(var12, -var12, var12);
            GlStateManager.rotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
         } else if (var13.isHandheld()) {
            if (var13.shouldRotate()) {
               GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.translatef(0.0F, -0.125F, 0.0F);
            }

            this.parent.m_81npivqro();
            float var14 = 0.625F;
            GlStateManager.scalef(var14, -var14, var14);
            GlStateManager.rotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.translatef(0.25F, 0.1875F, -0.1875F);
            float var15 = 0.375F;
            GlStateManager.scalef(var15, var15, var15);
            GlStateManager.rotatef(60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(20.0F, 0.0F, 0.0F, 1.0F);
         }

         GlStateManager.rotatef(-15.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(40.0F, 0.0F, 0.0F, 1.0F);
         var11.getHeldItemRenderer().render(c_12xxtvzpn, var9, ModelTransformations.Type.THIRD_PERSON);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
