package net.minecraft.item;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class BookAndQuillItem extends Item {
   public BookAndQuillItem() {
      this.setMaxStackSize(1);
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      player.openEditBookScreen(stack);
      player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
      return stack;
   }

   public static boolean isValid(NbtCompound tag) {
      if (tag == null) {
         return false;
      } else if (!tag.isType("pages", 9)) {
         return false;
      } else {
         NbtList var1 = tag.getList("pages", 8);

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            String var3 = var1.getString(var2);
            if (var3 == null) {
               return false;
            }

            if (var3.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}
