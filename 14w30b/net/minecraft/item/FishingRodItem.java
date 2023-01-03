package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FishingBobberEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FishingRodItem extends Item {
   public FishingRodItem() {
      this.setMaxDamage(64);
      this.setMaxStackSize(1);
      this.setItemGroup(ItemGroup.TOOLS);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isHandheld() {
      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRotate() {
      return true;
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      if (player.fishingBobber != null) {
         int var4 = player.fishingBobber.retract();
         stack.damageAndBreak(var4, player);
         player.swingHand();
      } else {
         world.playSound((Entity)player, "random.bow", 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!world.isClient) {
            world.addEntity(new FishingBobberEntity(world, player));
         }

         player.swingHand();
         player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
      }

      return stack;
   }

   @Override
   public boolean isEnchantable(ItemStack stack) {
      return super.isEnchantable(stack);
   }

   @Override
   public int getEnchantability() {
      return 1;
   }
}
