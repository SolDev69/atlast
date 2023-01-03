package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemEntityRenderer extends EntityRenderer {
   private final ItemRenderer itemRenderer;
   private Random random = new Random();

   public ItemEntityRenderer(EntityRenderDispatcher dispatcher, ItemRenderer itemRenderer) {
      super(dispatcher);
      this.itemRenderer = itemRenderer;
      this.shadowSize = 0.15F;
      this.shadowDarkness = 0.75F;
   }

   private int m_46plmsdve(ItemEntity c_32myydzeb, double d, double e, double f, float g, BakedModel c_51yvnkdmo) {
      ItemStack var10 = c_32myydzeb.getItemStack();
      Item var11 = var10.getItem();
      if (var11 == null) {
         return 0;
      } else {
         boolean var12 = c_51yvnkdmo.isGui3d();
         int var13 = this.m_70kndcjfu(var10);
         float var14 = 0.25F;
         float var15 = MathHelper.sin(((float)c_32myydzeb.getAge() + g) / 10.0F + c_32myydzeb.hoverHeight) * 0.1F + 0.1F;
         GlStateManager.translatef((float)d, (float)e + var15 + 0.25F, (float)f);
         if (var12 || this.dispatcher.options.fancyGraphics) {
            float var16 = (((float)c_32myydzeb.getAge() + g) / 20.0F + c_32myydzeb.hoverHeight) * (180.0F / (float)Math.PI);
            GlStateManager.rotatef(var16, 0.0F, 1.0F, 0.0F);
         }

         if (!var12) {
            float var19 = -0.0F * (float)(var13 - 1) * 0.5F;
            float var17 = -0.0F * (float)(var13 - 1) * 0.5F;
            float var18 = -0.046875F * (float)(var13 - 1) * 0.5F;
            GlStateManager.translatef(var19, var17, var18);
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         return var13;
      }
   }

   private int m_70kndcjfu(ItemStack c_72owraavl) {
      byte var2 = 1;
      if (c_72owraavl.size > 48) {
         var2 = 5;
      } else if (c_72owraavl.size > 32) {
         var2 = 4;
      } else if (c_72owraavl.size > 16) {
         var2 = 3;
      } else if (c_72owraavl.size > 1) {
         var2 = 2;
      }

      return var2;
   }

   public void render(ItemEntity c_32myydzeb, double d, double e, double f, float g, float h) {
      ItemStack var10 = c_32myydzeb.getItemStack();
      this.random.setSeed(187L);
      boolean var11 = false;
      if (this.bindTexture(c_32myydzeb)) {
         this.dispatcher.textureManager.getTexture(this.getTexture(c_32myydzeb)).m_60hztdglb(false, false);
         var11 = true;
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.pushMatrix();
      BakedModel var12 = this.itemRenderer.getModelShaper().getModel(var10);
      int var13 = this.m_46plmsdve(c_32myydzeb, d, e, f, h, var12);

      for(int var14 = 0; var14 < var13; ++var14) {
         if (var12.isGui3d()) {
            GlStateManager.pushMatrix();
            if (var14 > 0) {
               float var15 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var16 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var17 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translatef(var15, var16, var17);
            }

            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            this.itemRenderer.renderHeldItem(var10, var12);
            GlStateManager.popMatrix();
         } else {
            this.itemRenderer.renderHeldItem(var10, var12);
            GlStateManager.translatef(0.0F, 0.0F, 0.046875F);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.enableBlend();
      this.bindTexture(c_32myydzeb);
      if (var11) {
         this.dispatcher.textureManager.getTexture(this.getTexture(c_32myydzeb)).m_42jngdvts();
      }

      super.render(c_32myydzeb, d, e, f, g, h);
   }

   protected Identifier getTexture(ItemEntity c_32myydzeb) {
      return SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS;
   }
}
