package net.minecraft.enchantment;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

public enum EnchantmentTarget {
   ALL,
   ARMOR,
   ARMOR_FEET,
   ARMOR_LEGS,
   ARMOR_TORSO,
   ARMOR_HEAD,
   WEAPON,
   DIGGER,
   FISHING_ROD,
   BREAKABLE,
   BOW;

   public boolean matches(Item item) {
      if (this == ALL) {
         return true;
      } else if (this == BREAKABLE && item.isDamageable()) {
         return true;
      } else if (item instanceof ArmorItem) {
         if (this == ARMOR) {
            return true;
         } else {
            ArmorItem var2 = (ArmorItem)item;
            if (var2.slot == 0) {
               return this == ARMOR_HEAD;
            } else if (var2.slot == 2) {
               return this == ARMOR_LEGS;
            } else if (var2.slot == 1) {
               return this == ARMOR_TORSO;
            } else if (var2.slot == 3) {
               return this == ARMOR_FEET;
            } else {
               return false;
            }
         }
      } else if (item instanceof SwordItem) {
         return this == WEAPON;
      } else if (item instanceof ToolItem) {
         return this == DIGGER;
      } else if (item instanceof BowItem) {
         return this == BOW;
      } else if (item instanceof FishingRodItem) {
         return this == FISHING_ROD;
      } else {
         return false;
      }
   }
}
