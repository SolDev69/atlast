package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.IEntityAttribute;
import net.minecraft.entity.living.effect.PotionHelper;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.text.Formatting;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PotionItem extends Item {
   private Map potionEffectCache = Maps.newHashMap();
   private static final Map ITEM_STACKS = Maps.newLinkedHashMap();

   public PotionItem() {
      this.setMaxStackSize(1);
      this.setStackable(true);
      this.setMaxDamage(0);
      this.setItemGroup(ItemGroup.BREWING);
   }

   public List getPotionEffects(ItemStack item) {
      if (item.hasNbt() && item.getNbt().isType("CustomPotionEffects", 9)) {
         ArrayList var7 = Lists.newArrayList();
         NbtList var3 = item.getNbt().getList("CustomPotionEffects", 10);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            NbtCompound var5 = var3.getCompound(var4);
            StatusEffectInstance var6 = StatusEffectInstance.fromNbt(var5);
            if (var6 != null) {
               var7.add(var6);
            }
         }

         return var7;
      } else {
         List var2 = (List)this.potionEffectCache.get(item.getMetadata());
         if (var2 == null) {
            var2 = PotionHelper.getStatusEffects(item.getMetadata(), false);
            this.potionEffectCache.put(item.getMetadata(), var2);
         }

         return var2;
      }
   }

   public List getEffectsFromMetadata(int metadata) {
      List var2 = (List)this.potionEffectCache.get(metadata);
      if (var2 == null) {
         var2 = PotionHelper.getStatusEffects(metadata, false);
         this.potionEffectCache.put(metadata, var2);
      }

      return var2;
   }

   @Override
   public ItemStack finishUsing(ItemStack stack, World world, PlayerEntity player) {
      if (!player.abilities.creativeMode) {
         --stack.size;
      }

      if (!world.isClient) {
         List var4 = this.getPotionEffects(stack);
         if (var4 != null) {
            for(StatusEffectInstance var6 : var4) {
               player.addStatusEffect(new StatusEffectInstance(var6));
            }
         }
      }

      player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
      if (!player.abilities.creativeMode) {
         if (stack.size <= 0) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         player.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE));
      }

      return stack;
   }

   @Override
   public int getUseDuration(ItemStack stack) {
      return 32;
   }

   @Override
   public UseAction getUseAction(ItemStack stack) {
      return UseAction.DRINK;
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      if (isSplashPotion(stack.getMetadata())) {
         if (!player.abilities.creativeMode) {
            --stack.size;
         }

         world.playSound((Entity)player, "random.bow", 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!world.isClient) {
            world.addEntity(new PotionEntity(world, player, stack));
         }

         player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
         return stack;
      } else {
         player.setUseItem(stack, this.getUseDuration(stack));
         return stack;
      }
   }

   public static boolean isSplashPotion(int i) {
      return (i & 16384) != 0;
   }

   @Environment(EnvType.CLIENT)
   public int getPotionColor(int metadata) {
      return PotionHelper.getColor(metadata, false);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getDisplayColor(ItemStack stack, int color) {
      return color > 0 ? 16777215 : this.getPotionColor(stack.getMetadata());
   }

   @Environment(EnvType.CLIENT)
   public boolean hasPotionMetadataEfects(int metadata) {
      List var2 = this.getEffectsFromMetadata(metadata);
      if (var2 != null && !var2.isEmpty()) {
         for(StatusEffectInstance var4 : var2) {
            if (StatusEffect.BY_ID[var4.getId()].isInstant()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public String getName(ItemStack stack) {
      if (stack.getMetadata() == 0) {
         return I18n.translate("item.emptyPotion.name").trim();
      } else {
         String var2 = "";
         if (isSplashPotion(stack.getMetadata())) {
            var2 = I18n.translate("potion.prefix.grenade").trim() + " ";
         }

         List var3 = Items.POTION.getPotionEffects(stack);
         if (var3 != null && !var3.isEmpty()) {
            String var5 = ((StatusEffectInstance)var3.get(0)).getName();
            var5 = var5 + ".postfix";
            return var2 + I18n.translate(var5).trim();
         } else {
            String var4 = PotionHelper.getName(stack.getMetadata());
            return I18n.translate(var4).trim() + " " + super.getName(stack);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addHoverText(ItemStack stack, PlayerEntity player, List tooltip, boolean advanced) {
      if (stack.getMetadata() != 0) {
         List var5 = Items.POTION.getPotionEffects(stack);
         HashMultimap var6 = HashMultimap.create();
         if (var5 != null && !var5.isEmpty()) {
            for(StatusEffectInstance var8 : var5) {
               String var9 = I18n.translate(var8.getName()).trim();
               StatusEffect var10 = StatusEffect.BY_ID[var8.getId()];
               Map var11 = var10.getModifiers();
               if (var11 != null && var11.size() > 0) {
                  for(Entry var13 : var11.entrySet()) {
                     AttributeModifier var14 = (AttributeModifier)var13.getValue();
                     AttributeModifier var15 = new AttributeModifier(var14.getName(), var10.getModifier(var8.getAmplifier(), var14), var14.getOperation());
                     var6.put(((IEntityAttribute)var13.getKey()).getName(), var15);
                  }
               }

               if (var8.getAmplifier() > 0) {
                  var9 = var9 + " " + I18n.translate("potion.potency." + var8.getAmplifier()).trim();
               }

               if (var8.getDuration() > 20) {
                  var9 = var9 + " (" + StatusEffect.getDurationString(var8) + ")";
               }

               if (var10.isHarmful()) {
                  tooltip.add(Formatting.RED + var9);
               } else {
                  tooltip.add(Formatting.GRAY + var9);
               }
            }
         } else {
            String var7 = I18n.translate("potion.empty").trim();
            tooltip.add(Formatting.GRAY + var7);
         }

         if (!var6.isEmpty()) {
            tooltip.add("");
            tooltip.add(Formatting.DARK_PURPLE + I18n.translate("potion.effects.whenDrank"));

            for(Entry var18 : var6.entries()) {
               AttributeModifier var19 = (AttributeModifier)var18.getValue();
               double var20 = var19.get();
               double var21;
               if (var19.getOperation() != 1 && var19.getOperation() != 2) {
                  var21 = var19.get();
               } else {
                  var21 = var19.get() * 100.0;
               }

               if (var20 > 0.0) {
                  tooltip.add(
                     Formatting.BLUE
                        + I18n.translate(
                           "attribute.modifier.plus." + var19.getOperation(),
                           ItemStack.MODIFIER_FORMAT.format(var21),
                           I18n.translate("attribute.name." + (String)var18.getKey())
                        )
                  );
               } else if (var20 < 0.0) {
                  var21 *= -1.0;
                  tooltip.add(
                     Formatting.RED
                        + I18n.translate(
                           "attribute.modifier.take." + var19.getOperation(),
                           ItemStack.MODIFIER_FORMAT.format(var21),
                           I18n.translate("attribute.name." + (String)var18.getKey())
                        )
                  );
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean hasEnchantmentGlint(ItemStack stack) {
      List var2 = this.getPotionEffects(stack);
      return var2 != null && !var2.isEmpty();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      super.addToCreativeMenu(item, group, list);
      if (ITEM_STACKS.isEmpty()) {
         for(int var4 = 0; var4 <= 15; ++var4) {
            for(int var5 = 0; var5 <= 1; ++var5) {
               int var6;
               if (var5 == 0) {
                  var6 = var4 | 8192;
               } else {
                  var6 = var4 | 16384;
               }

               for(int var7 = 0; var7 <= 2; ++var7) {
                  int var8 = var6;
                  if (var7 != 0) {
                     if (var7 == 1) {
                        var8 = var6 | 32;
                     } else if (var7 == 2) {
                        var8 = var6 | 64;
                     }
                  }

                  List var9 = PotionHelper.getStatusEffects(var8, false);
                  if (var9 != null && !var9.isEmpty()) {
                     ITEM_STACKS.put(var9, var8);
                  }
               }
            }
         }
      }

      for(int var11 : ITEM_STACKS.values()) {
         list.add(new ItemStack(item, 1, var11));
      }
   }
}
