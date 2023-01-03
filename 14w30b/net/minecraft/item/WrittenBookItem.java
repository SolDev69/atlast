package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.MenuSlotUpdateS2CPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TextUtils;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WrittenBookItem extends Item {
   public WrittenBookItem() {
      this.setMaxStackSize(1);
   }

   public static boolean isValid(NbtCompound tag) {
      if (!BookAndQuillItem.isValid(tag)) {
         return false;
      } else if (!tag.isType("title", 8)) {
         return false;
      } else {
         String var1 = tag.getString("title");
         if (var1 == null || var1.length() > 16) {
            return false;
         } else {
            return tag.isType("author", 8);
         }
      }
   }

   public static int getGeneration(ItemStack stack) {
      return stack.getNbt().getInt("generation");
   }

   @Override
   public String getName(ItemStack stack) {
      if (stack.hasNbt()) {
         NbtCompound var2 = stack.getNbt();
         String var3 = var2.getString("title");
         if (!StringUtils.isStringEmpty(var3)) {
            return var3;
         }
      }

      return super.getName(stack);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addHoverText(ItemStack stack, PlayerEntity player, List tooltip, boolean advanced) {
      if (stack.hasNbt()) {
         NbtCompound var5 = stack.getNbt();
         String var6 = var5.getString("author");
         if (!StringUtils.isStringEmpty(var6)) {
            tooltip.add(Formatting.GRAY + I18n.translate("book.byAuthor", var6));
         }

         tooltip.add(Formatting.GRAY + I18n.translate("book.generation." + var5.getInt("generation")));
      }
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      if (!world.isClient) {
         this.resolvePages(stack, player);
      }

      player.openEditBookScreen(stack);
      player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
      return stack;
   }

   private void resolvePages(ItemStack stack, PlayerEntity player) {
      if (stack != null && stack.getNbt() != null) {
         NbtCompound var3 = stack.getNbt();
         if (!var3.getBoolean("resolved")) {
            var3.putBoolean("resolved", true);
            if (isValid(var3)) {
               NbtList var4 = var3.getList("pages", 8);

               for(int var5 = 0; var5 < var4.size(); ++var5) {
                  String var6 = var4.getString(var5);

                  Object var7;
                  try {
                     Text var11 = Text.Serializer.fromJson(var6);
                     var7 = TextUtils.updateForEntity(player, var11, player);
                  } catch (Exception var9) {
                     var7 = new LiteralText(var6);
                  }

                  var4.set(var5, new NbtString(Text.Serializer.toJson((Text)var7)));
               }

               var3.put("pages", var4);
               if (player instanceof ServerPlayerEntity && player.getMainHandStack() == stack) {
                  InventorySlot var10 = player.menu.getSlot(player.inventory, player.inventory.selectedSlot);
                  ((ServerPlayerEntity)player).networkHandler.sendPacket(new MenuSlotUpdateS2CPacket(0, var10.id, stack));
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean hasEnchantmentGlint(ItemStack stack) {
      return true;
   }
}
