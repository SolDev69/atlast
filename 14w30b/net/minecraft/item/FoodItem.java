package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class FoodItem extends Item {
   public final int eatingTime = 32;
   private final int hungerPoints;
   private final float saturation;
   private final boolean canBeCooked;
   private boolean alwaysEdible;
   private int statusEffectId;
   private int duration;
   private int multiplier;
   private float effectChance;

   public FoodItem(int hungerPoints, float saturation, boolean canBeCooked) {
      this.hungerPoints = hungerPoints;
      this.canBeCooked = canBeCooked;
      this.saturation = saturation;
      this.setItemGroup(ItemGroup.FOOD);
   }

   public FoodItem(int hungerPoints, boolean canBeCooked) {
      this(hungerPoints, 0.6F, canBeCooked);
   }

   @Override
   public ItemStack finishUsing(ItemStack stack, World world, PlayerEntity player) {
      --stack.size;
      player.getHungerManager().add(this, stack);
      world.playSound((Entity)player, "random.burp", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
      this.eat(stack, world, player);
      player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
      return stack;
   }

   protected void eat(ItemStack stack, World world, PlayerEntity player) {
      if (!world.isClient && this.statusEffectId > 0 && world.random.nextFloat() < this.effectChance) {
         player.addStatusEffect(new StatusEffectInstance(this.statusEffectId, this.duration * 20, this.multiplier));
      }
   }

   @Override
   public int getUseDuration(ItemStack stack) {
      return 32;
   }

   @Override
   public UseAction getUseAction(ItemStack stack) {
      return UseAction.EAT;
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      if (player.canEat(this.alwaysEdible)) {
         player.setUseItem(stack, this.getUseDuration(stack));
      }

      return stack;
   }

   public int getHungerPoints(ItemStack stack) {
      return this.hungerPoints;
   }

   public float getSaturation(ItemStack stack) {
      return this.saturation;
   }

   public boolean canBeCooked() {
      return this.canBeCooked;
   }

   public FoodItem setStatusEffect(int id, int duration, int multiplier, float effectChance) {
      this.statusEffectId = id;
      this.duration = duration;
      this.multiplier = multiplier;
      this.effectChance = effectChance;
      return this;
   }

   public FoodItem alwaysEdible() {
      this.alwaysEdible = true;
      return this;
   }
}
