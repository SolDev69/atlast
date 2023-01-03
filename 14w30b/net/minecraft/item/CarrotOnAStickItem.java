package net.minecraft.item;

import net.minecraft.entity.living.mob.passive.animal.PigEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CarrotOnAStickItem extends Item {
   public CarrotOnAStickItem() {
      this.setItemGroup(ItemGroup.TRANSPORTATION);
      this.setMaxStackSize(1);
      this.setMaxDamage(25);
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
      if (player.hasVehicle() && player.vehicle instanceof PigEntity) {
         PigEntity var4 = (PigEntity)player.vehicle;
         if (var4.getPlayerControlGoal().canStartMoving() && stack.getMaxDamage() - stack.getMetadata() >= 7) {
            var4.getPlayerControlGoal().startMoving();
            stack.damageAndBreak(7, player);
            if (stack.size == 0) {
               ItemStack var5 = new ItemStack(Items.FISHING_ROD);
               var5.setNbt(stack.getNbt());
               return var5;
            }
         }
      }

      player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
      return stack;
   }
}
