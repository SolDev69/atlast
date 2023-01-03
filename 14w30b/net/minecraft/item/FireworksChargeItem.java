package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FireworksChargeItem extends Item {
   @Environment(EnvType.CLIENT)
   @Override
   public int getDisplayColor(ItemStack stack, int color) {
      if (color != 1) {
         return super.getDisplayColor(stack, color);
      } else {
         NbtElement var3 = getExplosionNbt(stack, "Colors");
         if (!(var3 instanceof NbtIntArray)) {
            return 9079434;
         } else {
            NbtIntArray var4 = (NbtIntArray)var3;
            int[] var5 = var4.getIntArray();
            if (var5.length == 1) {
               return var5[0];
            } else {
               int var6 = 0;
               int var7 = 0;
               int var8 = 0;

               for(int var12 : var5) {
                  var6 += (var12 & 0xFF0000) >> 16;
                  var7 += (var12 & 0xFF00) >> 8;
                  var8 += (var12 & 0xFF) >> 0;
               }

               var6 /= var5.length;
               var7 /= var5.length;
               var8 /= var5.length;
               return var6 << 16 | var7 << 8 | var8;
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static NbtElement getExplosionNbt(ItemStack stack, String name) {
      if (stack.hasNbt()) {
         NbtCompound var2 = stack.getNbt().getCompound("Explosion");
         if (var2 != null) {
            return var2.get(name);
         }
      }

      return null;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addHoverText(ItemStack stack, PlayerEntity player, List tooltip, boolean advanced) {
      if (stack.hasNbt()) {
         NbtCompound var5 = stack.getNbt().getCompound("Explosion");
         if (var5 != null) {
            addExplosionInfo(var5, tooltip);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static void addExplosionInfo(NbtCompound nbt, List list) {
      byte var2 = nbt.getByte("Type");
      if (var2 >= 0 && var2 <= 4) {
         list.add(I18n.translate("item.fireworksCharge.type." + var2).trim());
      } else {
         list.add(I18n.translate("item.fireworksCharge.type").trim());
      }

      int[] var3 = nbt.getIntArray("Colors");
      if (var3.length > 0) {
         boolean var4 = true;
         String var5 = "";

         for(int var9 : var3) {
            if (!var4) {
               var5 = var5 + ", ";
            }

            var4 = false;
            boolean var10 = false;

            for(int var11 = 0; var11 < DyeItem.COLORS.length; ++var11) {
               if (var9 == DyeItem.COLORS[var11]) {
                  var10 = true;
                  var5 = var5 + I18n.translate("item.fireworksCharge." + DyeColor.byMetadata(var11).getName());
                  break;
               }
            }

            if (!var10) {
               var5 = var5 + I18n.translate("item.fireworksCharge.customColor");
            }
         }

         list.add(var5);
      }

      int[] var13 = nbt.getIntArray("FadeColors");
      if (var13.length > 0) {
         boolean var14 = true;
         String var16 = I18n.translate("item.fireworksCharge.fadeTo") + " ";

         for(int var21 : var13) {
            if (!var14) {
               var16 = var16 + ", ";
            }

            var14 = false;
            boolean var22 = false;

            for(int var12 = 0; var12 < 16; ++var12) {
               if (var21 == DyeItem.COLORS[var12]) {
                  var22 = true;
                  var16 = var16 + I18n.translate("item.fireworksCharge." + DyeColor.byMetadata(var12).getName());
                  break;
               }
            }

            if (!var22) {
               var16 = var16 + I18n.translate("item.fireworksCharge.customColor");
            }
         }

         list.add(var16);
      }

      boolean var15 = nbt.getBoolean("Trail");
      if (var15) {
         list.add(I18n.translate("item.fireworksCharge.trail"));
      }

      boolean var17 = nbt.getBoolean("Flicker");
      if (var17) {
         list.add(I18n.translate("item.fireworksCharge.flicker"));
      }
   }
}
