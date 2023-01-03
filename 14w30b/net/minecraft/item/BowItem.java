package net.minecraft.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class BowItem extends Item {
   public static final String[] BOW_PULLING_LEVELS = new String[]{"pulling_0", "pulling_1", "pulling_2"};

   public BowItem() {
      this.maxStackSize = 1;
      this.setMaxDamage(384);
      this.setItemGroup(ItemGroup.COMBAT);
   }

   @Override
   public void stopUsing(ItemStack stack, World world, PlayerEntity player, int remainingUseTime) {
      boolean var5 = player.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantment.INFINITY.id, stack) > 0;
      if (var5 || player.inventory.contains(Items.ARROW)) {
         int var6 = this.getUseDuration(stack) - remainingUseTime;
         float var7 = (float)var6 / 20.0F;
         var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;
         if ((double)var7 < 0.1) {
            return;
         }

         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         ArrowEntity var8 = new ArrowEntity(world, player, var7 * 2.0F);
         if (var7 == 1.0F) {
            var8.setCritical(true);
         }

         int var9 = EnchantmentHelper.getLevel(Enchantment.POWER.id, stack);
         if (var9 > 0) {
            var8.setDamage(var8.getDamage() + (double)var9 * 0.5 + 0.5);
         }

         int var10 = EnchantmentHelper.getLevel(Enchantment.PUNCH.id, stack);
         if (var10 > 0) {
            var8.setPunchLevel(var10);
         }

         if (EnchantmentHelper.getLevel(Enchantment.FLAME.id, stack) > 0) {
            var8.setOnFireFor(100);
         }

         stack.damageAndBreak(1, player);
         world.playSound((Entity)player, "random.bow", 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + var7 * 0.5F);
         if (var5) {
            var8.pickup = 2;
         } else {
            player.inventory.consumeOne(Items.ARROW);
         }

         player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
         if (!world.isClient) {
            world.addEntity(var8);
         }
      }
   }

   @Override
   public ItemStack finishUsing(ItemStack stack, World world, PlayerEntity player) {
      return stack;
   }

   @Override
   public int getUseDuration(ItemStack stack) {
      return 72000;
   }

   @Override
   public UseAction getUseAction(ItemStack stack) {
      return UseAction.BOW;
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      if (player.abilities.creativeMode || player.inventory.contains(Items.ARROW)) {
         player.setUseItem(stack, this.getUseDuration(stack));
      }

      return stack;
   }

   @Override
   public int getEnchantability() {
      return 1;
   }
}
