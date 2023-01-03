package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.WeightedPicker;

public class EnchantmentHelper {
   private static final Random RANDOM = new Random();
   private static final EnchantmentHelper.ProtectionModifier PROTECTION_MODIFIER = new EnchantmentHelper.ProtectionModifier();
   private static final EnchantmentHelper.DamageModifier DAMAGE_MODIFIER = new EnchantmentHelper.DamageModifier();
   private static final EnchantmentHelper.ProtectionWildcard PROTECTION_WILDCARD = new EnchantmentHelper.ProtectionWildcard();
   private static final EnchantmentHelper.DamageWildcard DAMAGE_WILDCARD = new EnchantmentHelper.DamageWildcard();

   public static int getLevel(int id, ItemStack stack) {
      if (stack == null) {
         return 0;
      } else {
         NbtList var2 = stack.getEnchantments();
         if (var2 == null) {
            return 0;
         } else {
            for(int var3 = 0; var3 < var2.size(); ++var3) {
               short var4 = var2.getCompound(var3).getShort("id");
               short var5 = var2.getCompound(var3).getShort("lvl");
               if (var4 == id) {
                  return var5;
               }
            }

            return 0;
         }
      }
   }

   public static Map getEnchantments(ItemStack stack) {
      LinkedHashMap var1 = Maps.newLinkedHashMap();
      NbtList var2 = stack.getItem() == Items.ENCHANTED_BOOK ? Items.ENCHANTED_BOOK.getStoredEnchantments(stack) : stack.getEnchantments();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.size(); ++var3) {
            short var4 = var2.getCompound(var3).getShort("id");
            short var5 = var2.getCompound(var3).getShort("lvl");
            var1.put(Integer.valueOf(var4), Integer.valueOf(var5));
         }
      }

      return var1;
   }

   public static void setEnchantments(Map enchantments, ItemStack stack) {
      NbtList var2 = new NbtList();

      for(int var4 : enchantments.keySet()) {
         Enchantment var5 = Enchantment.byRawId(var4);
         if (var5 != null) {
            NbtCompound var6 = new NbtCompound();
            var6.putShort("id", (short)var4);
            var6.putShort("lvl", (short)((Integer)enchantments.get(var4)).intValue());
            var2.add(var6);
            if (stack.getItem() == Items.ENCHANTED_BOOK) {
               Items.ENCHANTED_BOOK.addEnchantment(stack, new EnchantmentEntry(var5, enchantments.get(var4)));
            }
         }
      }

      if (var2.size() > 0) {
         if (stack.getItem() != Items.ENCHANTED_BOOK) {
            stack.addToNbt("ench", var2);
         }
      } else if (stack.hasNbt()) {
         stack.getNbt().remove("ench");
      }
   }

   public static int getHighestEnchantmentLevel(int id, ItemStack[] stacks) {
      if (stacks == null) {
         return 0;
      } else {
         int var2 = 0;

         for(ItemStack var6 : stacks) {
            int var7 = getLevel(id, var6);
            if (var7 > var2) {
               var2 = var7;
            }
         }

         return var2;
      }
   }

   private static void applyAttackModifier(EnchantmentHelper.AttackModifier modifier, ItemStack stack) {
      if (stack != null) {
         NbtList var2 = stack.getEnchantments();
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.size(); ++var3) {
               short var4 = var2.getCompound(var3).getShort("id");
               short var5 = var2.getCompound(var3).getShort("lvl");
               if (Enchantment.byRawId(var4) != null) {
                  modifier.apply(Enchantment.byRawId(var4), var5);
               }
            }
         }
      }
   }

   private static void applyAttackModifier(EnchantmentHelper.AttackModifier modifier, ItemStack[] stacks) {
      for(ItemStack var5 : stacks) {
         applyAttackModifier(modifier, var5);
      }
   }

   public static int modifyProtection(ItemStack[] armor, DamageSource source) {
      PROTECTION_MODIFIER.protection = 0;
      PROTECTION_MODIFIER.source = source;
      applyAttackModifier(PROTECTION_MODIFIER, armor);
      if (PROTECTION_MODIFIER.protection > 25) {
         PROTECTION_MODIFIER.protection = 25;
      }

      return (PROTECTION_MODIFIER.protection + 1 >> 1) + RANDOM.nextInt((PROTECTION_MODIFIER.protection >> 1) + 1);
   }

   public static float m_01divsmlb(LivingEntity c_97zulxhng, LivingEntity c_97zulxhng2) {
      return modifyDamage(c_97zulxhng.getStackInHand(), c_97zulxhng2.getMobType());
   }

   public static float modifyDamage(ItemStack weapon, LivingEntityType target) {
      DAMAGE_MODIFIER.damage = 0.0F;
      DAMAGE_MODIFIER.entity = target;
      applyAttackModifier(DAMAGE_MODIFIER, weapon);
      return DAMAGE_MODIFIER.damage;
   }

   public static void applyProtectionWildcard(LivingEntity target, Entity attacker) {
      PROTECTION_WILDCARD.attacker = attacker;
      PROTECTION_WILDCARD.target = target;
      applyAttackModifier(PROTECTION_WILDCARD, target.getEquipmentStacks());
      if (attacker instanceof PlayerEntity) {
         applyAttackModifier(PROTECTION_WILDCARD, target.getStackInHand());
      }
   }

   public static void applyDamageWildcard(LivingEntity attacker, Entity target) {
      DAMAGE_WILDCARD.attacker = attacker;
      DAMAGE_WILDCARD.target = target;
      applyAttackModifier(DAMAGE_WILDCARD, attacker.getEquipmentStacks());
      if (attacker instanceof PlayerEntity) {
         applyAttackModifier(DAMAGE_WILDCARD, attacker.getStackInHand());
      }
   }

   public static int getKnockbackLevel(LivingEntity attacker, LivingEntity target) {
      return getLevel(Enchantment.KNOCKBACK.id, attacker.getStackInHand());
   }

   public static int getFireAspectLevel(LivingEntity entity) {
      return getLevel(Enchantment.FIRE_ASPECT.id, entity.getStackInHand());
   }

   public static int getRespirationLevel(Entity entity) {
      return getHighestEnchantmentLevel(Enchantment.RESPIRATION.id, entity.getEquipmentStacks());
   }

   public static int getDepthStriderLevel(Entity entity) {
      return getHighestEnchantmentLevel(Enchantment.DEPTH_STRIDER.id, entity.getEquipmentStacks());
   }

   public static int getEfficiencyLevel(LivingEntity entity) {
      return getLevel(Enchantment.EFFICIENCY.id, entity.getStackInHand());
   }

   public static boolean hasSilkTouch(LivingEntity entity) {
      return getLevel(Enchantment.SILK_TOUCH.id, entity.getStackInHand()) > 0;
   }

   public static int getFortuneLevel(LivingEntity entity) {
      return getLevel(Enchantment.FORTUNE.id, entity.getStackInHand());
   }

   public static int getLuckOfTheSeaLevel(LivingEntity entity) {
      return getLevel(Enchantment.LUCK_OF_THE_SEA.id, entity.getStackInHand());
   }

   public static int getLureLevel(LivingEntity entity) {
      return getLevel(Enchantment.LURE.id, entity.getStackInHand());
   }

   public static int getLootingLevel(LivingEntity entity) {
      return getLevel(Enchantment.LOOTING.id, entity.getStackInHand());
   }

   public static boolean getAquaAffinityLevel(LivingEntity entity) {
      return getHighestEnchantmentLevel(Enchantment.AQUA_AFFINITY.id, entity.getEquipmentStacks()) > 0;
   }

   public static ItemStack getFirstArmorStackWithEnchantment(Enchantment enchantment, LivingEntity entity) {
      for(ItemStack var5 : entity.getEquipmentStacks()) {
         if (var5 != null && getLevel(enchantment.id, var5) > 0) {
            return var5;
         }
      }

      return null;
   }

   public static int getRequiredXpLevel(Random random, int entry, int max, ItemStack stack) {
      Item var4 = stack.getItem();
      int var5 = var4.getEnchantability();
      if (var5 <= 0) {
         return 0;
      } else {
         if (max > 15) {
            max = 15;
         }

         int var6 = random.nextInt(8) + 1 + (max >> 1) + random.nextInt(max + 1);
         if (entry == 0) {
            return Math.max(var6 / 3, 1);
         } else {
            return entry == 1 ? var6 * 2 / 3 + 1 : Math.max(var6, max * 2);
         }
      }
   }

   public static ItemStack addRandomEnchantment(Random random, ItemStack stack, int xpLevel) {
      List var3 = getEnchantmentEntries(random, stack, xpLevel);
      boolean var4 = stack.getItem() == Items.BOOK;
      if (var4) {
         stack.setItem(Items.ENCHANTED_BOOK);
      }

      if (var3 != null) {
         for(EnchantmentEntry var6 : var3) {
            if (var4) {
               Items.ENCHANTED_BOOK.addEnchantment(stack, var6);
            } else {
               stack.addEnchantment(var6.enchantment, var6.level);
            }
         }
      }

      return stack;
   }

   public static List getEnchantmentEntries(Random random, ItemStack stack, int xpLevel) {
      Item var3 = stack.getItem();
      int var4 = var3.getEnchantability();
      if (var4 <= 0) {
         return null;
      } else {
         var4 /= 2;
         var4 = 1 + random.nextInt((var4 >> 1) + 1) + random.nextInt((var4 >> 1) + 1);
         int var5 = var4 + xpLevel;
         float var6 = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
         int var7 = (int)((float)var5 * (1.0F + var6) + 0.5F);
         if (var7 < 1) {
            var7 = 1;
         }

         ArrayList var8 = null;
         Map var9 = getAvailableEnchantmentEntries(var7, stack);
         if (var9 != null && !var9.isEmpty()) {
            EnchantmentEntry var10 = (EnchantmentEntry)WeightedPicker.pick(random, var9.values());
            if (var10 != null) {
               var8 = Lists.newArrayList();
               var8.add(var10);

               for(int var11 = var7; random.nextInt(50) <= var11; var11 >>= 1) {
                  Iterator var12 = var9.keySet().iterator();

                  while(var12.hasNext()) {
                     Integer var13 = (Integer)var12.next();
                     boolean var14 = true;

                     for(EnchantmentEntry var16 : var8) {
                        if (!var16.enchantment.checkCompatibility(Enchantment.byRawId(var13))) {
                           var14 = false;
                           break;
                        }
                     }

                     if (!var14) {
                        var12.remove();
                     }
                  }

                  if (!var9.isEmpty()) {
                     EnchantmentEntry var19 = (EnchantmentEntry)WeightedPicker.pick(random, var9.values());
                     var8.add(var19);
                  }
               }
            }
         }

         return var8;
      }
   }

   public static Map getAvailableEnchantmentEntries(int xp, ItemStack stack) {
      Item var2 = stack.getItem();
      HashMap var3 = null;
      boolean var4 = stack.getItem() == Items.BOOK;

      for(Enchantment var8 : Enchantment.ALL) {
         if (var8 != null && (var8.target.matches(var2) || var4)) {
            for(int var9 = var8.getMinLevel(); var9 <= var8.getMaxLevel(); ++var9) {
               if (xp >= var8.getMinXpRequirement(var9) && xp <= var8.getMaxXpRequirement(var9)) {
                  if (var3 == null) {
                     var3 = Maps.newHashMap();
                  }

                  var3.put(var8.id, new EnchantmentEntry(var8, var9));
               }
            }
         }
      }

      return var3;
   }

   interface AttackModifier {
      void apply(Enchantment enchantment, int level);
   }

   static final class DamageModifier implements EnchantmentHelper.AttackModifier {
      public float damage;
      public LivingEntityType entity;

      private DamageModifier() {
      }

      @Override
      public void apply(Enchantment enchantment, int level) {
         this.damage += enchantment.getExtraDamage(level, this.entity);
      }
   }

   static final class DamageWildcard implements EnchantmentHelper.AttackModifier {
      public LivingEntity attacker;
      public Entity target;

      private DamageWildcard() {
      }

      @Override
      public void apply(Enchantment enchantment, int level) {
         enchantment.applyDamageWildcard(this.attacker, this.target, level);
      }
   }

   static final class ProtectionModifier implements EnchantmentHelper.AttackModifier {
      public int protection;
      public DamageSource source;

      private ProtectionModifier() {
      }

      @Override
      public void apply(Enchantment enchantment, int level) {
         this.protection += enchantment.getExtraProtection(level, this.source);
      }
   }

   static final class ProtectionWildcard implements EnchantmentHelper.AttackModifier {
      public LivingEntity target;
      public Entity attacker;

      private ProtectionWildcard() {
      }

      @Override
      public void apply(Enchantment enchantment, int level) {
         enchantment.applyProtectionWildcard(this.target, this.attacker, level);
      }
   }
}
