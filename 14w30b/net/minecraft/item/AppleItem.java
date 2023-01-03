package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AppleItem extends FoodItem {
   public AppleItem(int i, float f, boolean bl) {
      super(i, f, bl);
      this.setStackable(true);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean hasEnchantmentGlint(ItemStack stack) {
      return stack.getMetadata() > 0;
   }

   @Override
   public Rarity getRarity(ItemStack stack) {
      return stack.getMetadata() == 0 ? Rarity.RARE : Rarity.EPIC;
   }

   @Override
   protected void eat(ItemStack stack, World world, PlayerEntity player) {
      if (!world.isClient) {
         player.addStatusEffect(new StatusEffectInstance(StatusEffect.ABSORPTION.id, 2400, 0));
      }

      if (stack.getMetadata() > 0) {
         if (!world.isClient) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffect.REGENERATION.id, 600, 4));
            player.addStatusEffect(new StatusEffectInstance(StatusEffect.RESISTANCE.id, 6000, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffect.FIRE_RESISTANCE.id, 6000, 0));
         }
      } else {
         super.eat(stack, world, player);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      list.add(new ItemStack(item, 1, 0));
      list.add(new ItemStack(item, 1, 1));
   }
}
