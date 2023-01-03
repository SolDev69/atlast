package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class HeldItemLayer implements EntityRenderLayer {
   private final LivingEntityRenderer parent;

   public HeldItemLayer(LivingEntityRenderer parent) {
      this.parent = parent;
   }

   @Override
   public void render(LivingEntity entity, float handSwingAmount, float handSwing, float tickDelta, float age, float headYaw, float headPitch, float scale) {
      ItemStack var9 = entity.getStackInHand();
      if (var9 != null) {
         GlStateManager.pushMatrix();
         if (this.parent.getModel().isBaby) {
            float var10 = 0.5F;
            GlStateManager.translatef(0.0F, 0.625F, 0.0F);
            GlStateManager.rotatef(-20.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.scalef(var10, var10, var10);
         }

         ((HumanoidModel)this.parent.getModel()).translateRightArm(0.0625F);
         GlStateManager.translatef(-0.0625F, 0.4375F, 0.0625F);
         Object var15 = null;
         boolean var11 = false;
         if (entity instanceof PlayerEntity) {
            PlayerEntity var12 = (PlayerEntity)entity;
            if (var12.fishingBobber != null) {
               var9 = new ItemStack(Items.STICK);
            }

            if (var12.getItemUseTimer() > 0) {
               UseAction var16 = var9.getUseAction();
               var11 = true;
            }
         }

         Item var18 = var9.getItem();
         MinecraftClient var13 = MinecraftClient.getInstance();
         if (var18 instanceof BlockItem && Block.byItem(var18).getRenderType() == 2) {
            GlStateManager.translatef(0.0F, 0.1875F, -0.3125F);
            GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
            float var14 = 0.375F;
            GlStateManager.scalef(-var14, -var14, var14);
         }

         var13.getHeldItemRenderer().render(entity, var9, ModelTransformations.Type.THIRD_PERSON);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
