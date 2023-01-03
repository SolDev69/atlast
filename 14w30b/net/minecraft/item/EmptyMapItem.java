package net.minecraft.item;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import net.minecraft.world.map.SavedMapData;

public class EmptyMapItem extends NetworkSyncedItem {
   protected EmptyMapItem() {
      this.setItemGroup(ItemGroup.MISC);
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      ItemStack var4 = new ItemStack(Items.FILLED_MAP, 1, world.getSavedDataCount("map"));
      String var5 = "map_" + var4.getMetadata();
      SavedMapData var6 = new SavedMapData(var5);
      world.setSavedData(var5, var6);
      var6.scale = 0;
      int var7 = 128 * (1 << var6.scale);
      var6.centerX = (int)(Math.round(player.x / (double)var7) * (long)var7);
      var6.centerZ = (int)(Math.round(player.z / (double)var7) * (long)var7);
      var6.dimension = (byte)world.dimension.getId();
      var6.markDirty();
      --stack.size;
      if (stack.size <= 0) {
         return var4;
      } else {
         if (!player.inventory.insertStack(var4.copy())) {
            player.dropItem(var4, false);
         }

         player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
         return stack;
      }
   }
}
