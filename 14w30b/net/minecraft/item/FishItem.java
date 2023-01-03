package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.living.effect.PotionHelper;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FishItem extends FoodItem {
   private final boolean cooked;

   public FishItem(boolean cooked) {
      super(0, 0.0F, false);
      this.cooked = cooked;
   }

   @Override
   public int getHungerPoints(ItemStack stack) {
      FishItem.Type var2 = FishItem.Type.byItem(stack);
      return this.cooked && var2.canBeCooked() ? var2.getCookedHungerPoints() : var2.getUncookedHungerPoints();
   }

   @Override
   public float getSaturation(ItemStack stack) {
      FishItem.Type var2 = FishItem.Type.byItem(stack);
      return this.cooked && var2.canBeCooked() ? var2.getCookedSaturation() : var2.getUncookedSaturation();
   }

   @Override
   public String getBrewingRecipe(ItemStack stack) {
      return FishItem.Type.byItem(stack) == FishItem.Type.PUFFERFISH ? PotionHelper.WATER_BREATHING : null;
   }

   @Override
   protected void eat(ItemStack stack, World world, PlayerEntity player) {
      FishItem.Type var4 = FishItem.Type.byItem(stack);
      if (var4 == FishItem.Type.PUFFERFISH) {
         player.addStatusEffect(new StatusEffectInstance(StatusEffect.POISON.id, 1200, 3));
         player.addStatusEffect(new StatusEffectInstance(StatusEffect.HUNGER.id, 300, 2));
         player.addStatusEffect(new StatusEffectInstance(StatusEffect.NAUSEA.id, 300, 1));
      }

      super.eat(stack, world, player);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      for(FishItem.Type var7 : FishItem.Type.values()) {
         if (!this.cooked || var7.canBeCooked()) {
            list.add(new ItemStack(this, 1, var7.getId()));
         }
      }
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      FishItem.Type var2 = FishItem.Type.byItem(stack);
      return this.getTranslationKey() + "." + var2.getName() + "." + (this.cooked && var2.canBeCooked() ? "cooked" : "raw");
   }

   public static enum Type {
      COD(0, "cod", 2, 0.1F, 5, 0.6F),
      SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
      CLOWNFISH(2, "clownfish", 1, 0.1F),
      PUFFERFISH(3, "pufferfish", 1, 0.1F);

      private static final Map ALL = Maps.newHashMap();
      private final int id;
      private final String name;
      private final int uncookedHungerPoints;
      private final float uncookedSaturation;
      private final int cookedHungerPoints;
      private final float cookedSaturation;
      private boolean canBeCooked = false;

      private Type(int id, String name, int uncookedHungerPoints, float uncookedSaturation, int cookedHungerPoints, float cookedSaturation) {
         this.id = id;
         this.name = name;
         this.uncookedHungerPoints = uncookedHungerPoints;
         this.uncookedSaturation = uncookedSaturation;
         this.cookedHungerPoints = cookedHungerPoints;
         this.cookedSaturation = cookedSaturation;
         this.canBeCooked = true;
      }

      private Type(int id, String name, int uncookedHungerPoints, float uncookedSaturation) {
         this.id = id;
         this.name = name;
         this.uncookedHungerPoints = uncookedHungerPoints;
         this.uncookedSaturation = uncookedSaturation;
         this.cookedHungerPoints = 0;
         this.cookedSaturation = 0.0F;
         this.canBeCooked = false;
      }

      public int getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public int getUncookedHungerPoints() {
         return this.uncookedHungerPoints;
      }

      public float getUncookedSaturation() {
         return this.uncookedSaturation;
      }

      public int getCookedHungerPoints() {
         return this.cookedHungerPoints;
      }

      public float getCookedSaturation() {
         return this.cookedSaturation;
      }

      public boolean canBeCooked() {
         return this.canBeCooked;
      }

      public static FishItem.Type byId(int id) {
         FishItem.Type var1 = (FishItem.Type)ALL.get(id);
         return var1 == null ? COD : var1;
      }

      public static FishItem.Type byItem(ItemStack stack) {
         return stack.getItem() instanceof FishItem ? byId(stack.getMetadata()) : COD;
      }

      static {
         for(FishItem.Type var3 : values()) {
            ALL.put(var3.getId(), var3);
         }
      }
   }
}
