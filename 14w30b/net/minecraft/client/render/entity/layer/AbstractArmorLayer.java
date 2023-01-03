package net.minecraft.client.render.entity.layer;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractArmorLayer implements EntityRenderLayer {
   protected static final Identifier ENCHANTMENT_GLINT_TEXTURE = new Identifier("textures/misc/enchanted_item_glint.png");
   protected Model innerModel;
   protected Model outerModel;
   private final LivingEntityRenderer parent;
   private float alpha = 1.0F;
   private float red = 1.0F;
   private float green = 1.0F;
   private float blue = 1.0F;
   private boolean hasColor;
   private static final Map TEXTURE_CACHE = Maps.newHashMap();

   public AbstractArmorLayer(LivingEntityRenderer parent) {
      this.parent = parent;
      this.hideAll();
   }

   @Override
   public void render(LivingEntity entity, float handSwingAmount, float handSwing, float tickDelta, float age, float headYaw, float headPitch, float scale) {
      this.renderArmor(entity, handSwingAmount, handSwing, tickDelta, age, headYaw, headPitch, scale, 4);
      this.renderArmor(entity, handSwingAmount, handSwing, tickDelta, age, headYaw, headPitch, scale, 3);
      this.renderArmor(entity, handSwingAmount, handSwing, tickDelta, age, headYaw, headPitch, scale, 2);
      this.renderArmor(entity, handSwingAmount, handSwing, tickDelta, age, headYaw, headPitch, scale, 1);
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }

   private void renderArmor(
      LivingEntity entity, float handSwingAmount, float handSwing, float tickDelta, float age, float headYaw, float headPitch, float scale, int equipmentSlot
   ) {
      ItemStack var10 = this.getArmor(entity, equipmentSlot);
      if (var10 != null && var10.getItem() instanceof ArmorItem) {
         ArmorItem var11 = (ArmorItem)var10.getItem();
         Model var12 = this.getModel(equipmentSlot);
         var12.copyPropertiesFrom(this.parent.getModel());
         var12.renderMobAnimation(entity, handSwingAmount, handSwing, tickDelta);
         this.setVisible(var12, equipmentSlot);
         boolean var13 = this.usesInnerModel(equipmentSlot);
         this.parent.bindTexture(this.getArmorTexture(var11, var13));
         switch(var11.getMaterial()) {
            case CLOTH:
               int var14 = var11.getColor(var10);
               float var15 = (float)(var14 >> 16 & 0xFF) / 255.0F;
               float var16 = (float)(var14 >> 8 & 0xFF) / 255.0F;
               float var17 = (float)(var14 & 0xFF) / 255.0F;
               GlStateManager.color4f(this.red * var15, this.green * var16, this.blue * var17, this.alpha);
               var12.render(entity, handSwingAmount, handSwing, age, headYaw, headPitch, scale);
               this.parent.bindTexture(this.getArmorTexture(var11, var13, "overlay"));
            case CHAIN:
            case IRON:
            case GOLD:
            case DIAMOND:
               GlStateManager.color4f(this.red, this.green, this.blue, this.alpha);
               var12.render(entity, handSwingAmount, handSwing, age, headYaw, headPitch, scale);
            default:
               if (!this.hasColor && var10.hasEnchantments()) {
                  this.renderEnchantmentGlint(entity, var12, handSwingAmount, handSwing, tickDelta, age, headYaw, headPitch, scale);
               }
         }
      }
   }

   public ItemStack getArmor(LivingEntity entity, int equipmentSlot) {
      return entity.getArmorStack(equipmentSlot - 1);
   }

   public Model getModel(int equipmentSlot) {
      return this.usesInnerModel(equipmentSlot) ? this.innerModel : this.outerModel;
   }

   private boolean usesInnerModel(int equipmentSlot) {
      return equipmentSlot == 2;
   }

   private void renderEnchantmentGlint(
      LivingEntity entity, Model model, float handSwingAmount, float handSwing, float tickDelta, float age, float headYaw, float headPitch, float scale
   ) {
      float var10 = (float)entity.time + tickDelta;
      this.parent.bindTexture(ENCHANTMENT_GLINT_TEXTURE);
      GlStateManager.disableBlend();
      GlStateManager.depthFunc(514);
      GlStateManager.depthMask(false);
      float var11 = 0.5F;
      GlStateManager.color4f(var11, var11, var11, 1.0F);

      for(int var12 = 0; var12 < 2; ++var12) {
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(768, 1);
         float var13 = 0.76F;
         GlStateManager.color4f(0.5F * var13, 0.25F * var13, 0.8F * var13, 1.0F);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float var14 = 0.33333334F;
         GlStateManager.scalef(var14, var14, var14);
         GlStateManager.rotatef(30.0F - (float)var12 * 60.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translatef(0.0F, var10 * (0.001F + (float)var12 * 0.003F) * 20.0F, 0.0F);
         GlStateManager.matrixMode(5888);
         model.render(entity, handSwingAmount, handSwing, age, headYaw, headPitch, scale);
      }

      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      GlStateManager.enableLighting();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
      GlStateManager.enableBlend();
   }

   private Identifier getArmorTexture(ArmorItem armor, boolean innerModel) {
      return this.getArmorTexture(armor, innerModel, null);
   }

   private Identifier getArmorTexture(ArmorItem armor, boolean innerModel, String type) {
      String var4 = String.format(
         "textures/models/armor/%s_layer_%d%s.png", armor.getMaterial().getId(), innerModel ? 2 : 1, type == null ? "" : String.format("_%s", type)
      );
      Identifier var5 = (Identifier)TEXTURE_CACHE.get(var4);
      if (var5 == null) {
         var5 = new Identifier(var4);
         TEXTURE_CACHE.put(var4, var5);
      }

      return var5;
   }

   protected abstract void hideAll();

   protected abstract void setVisible(Model model, int equipmentSlot);
}
