package net.minecraft.client.render.entity.layer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.SkullRenderer;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.ZombieEntity;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WornSkullLayer implements EntityRenderLayer {
   private final ModelPart model;

   public WornSkullLayer(ModelPart model) {
      this.model = model;
   }

   @Override
   public void render(LivingEntity entity, float handSwingAmount, float handSwing, float tickDelta, float age, float headYaw, float headPitch, float scale) {
      ItemStack var9 = entity.getArmorStack(3);
      if (var9 != null && var9.getItem() != null) {
         Item var10 = var9.getItem();
         MinecraftClient var11 = MinecraftClient.getInstance();
         GlStateManager.pushMatrix();
         if (entity.isSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.model.translate(0.0625F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var12 = entity instanceof VillagerEntity || entity instanceof ZombieEntity && ((ZombieEntity)entity).isVillager();
         if (var10 instanceof BlockItem) {
            float var13 = 0.625F;
            GlStateManager.translatef(0.0F, -0.25F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scalef(var13, -var13, -var13);
            if (var12) {
               GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
            }

            var11.getHeldItemRenderer().render(entity, var9, ModelTransformations.Type.HEAD);
         } else if (var10 == Items.SKULL) {
            float var16 = 1.1875F;
            GlStateManager.scalef(var16, -var16, -var16);
            if (var12) {
               GlStateManager.translatef(0.0F, 0.0625F, 0.0F);
            }

            GameProfile var14 = null;
            if (var9.hasNbt()) {
               NbtCompound var15 = var9.getNbt();
               if (var15.isType("SkullOwner", 10)) {
                  var14 = NbtUtils.readProfile(var15.getCompound("SkullOwner"));
               } else if (var15.isType("SkullOwner", 8)) {
                  var14 = SkullBlockEntity.updateProfile(new GameProfile(null, var15.getString("SkullOwner")));
                  var15.put("SkullOwner", NbtUtils.writeProfile(new NbtCompound(), var14));
               }
            }

            SkullRenderer.instance.render(-0.5F, 0.0F, -0.5F, Direction.UP, 180.0F, var9.getMetadata(), var14, -1);
         }

         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return true;
   }
}
