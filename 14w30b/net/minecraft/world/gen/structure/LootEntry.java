package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedPicker;

public class LootEntry extends WeightedPicker.Entry {
   private ItemStack stack;
   private int minItemChance;
   private int maxItemChance;

   public LootEntry(Item item, int metadata, int minItemChance, int maxItemChance, int weight) {
      super(weight);
      this.stack = new ItemStack(item, 1, metadata);
      this.minItemChance = minItemChance;
      this.maxItemChance = maxItemChance;
   }

   public LootEntry(ItemStack stack, int minItemChance, int maxItemChance, int weight) {
      super(weight);
      this.stack = stack;
      this.minItemChance = minItemChance;
      this.maxItemChance = maxItemChance;
   }

   public static void addLoot(Random random, List entries, Inventory inventory, int amount) {
      for(int var4 = 0; var4 < amount; ++var4) {
         LootEntry var5 = (LootEntry)WeightedPicker.pick(random, entries);
         int var6 = var5.minItemChance + random.nextInt(var5.maxItemChance - var5.minItemChance + 1);
         if (var5.stack.getMaxSize() >= var6) {
            ItemStack var9 = var5.stack.copy();
            var9.size = var6;
            inventory.setStack(random.nextInt(inventory.getSize()), var9);
         } else {
            for(int var7 = 0; var7 < var6; ++var7) {
               ItemStack var8 = var5.stack.copy();
               var8.size = 1;
               inventory.setStack(random.nextInt(inventory.getSize()), var8);
            }
         }
      }
   }

   public static void addLoot(Random rand, List entries, DispenserBlockEntity dispenserBlockEntity, int amount) {
      for(int var4 = 0; var4 < amount; ++var4) {
         LootEntry var5 = (LootEntry)WeightedPicker.pick(rand, entries);
         int var6 = var5.minItemChance + rand.nextInt(var5.maxItemChance - var5.minItemChance + 1);
         if (var5.stack.getMaxSize() >= var6) {
            ItemStack var9 = var5.stack.copy();
            var9.size = var6;
            dispenserBlockEntity.setStack(rand.nextInt(dispenserBlockEntity.getSize()), var9);
         } else {
            for(int var7 = 0; var7 < var6; ++var7) {
               ItemStack var8 = var5.stack.copy();
               var8.size = 1;
               dispenserBlockEntity.setStack(rand.nextInt(dispenserBlockEntity.getSize()), var8);
            }
         }
      }
   }

   public static List addAll(List entries, LootEntry... moreEntries) {
      ArrayList var2 = Lists.newArrayList(entries);
      Collections.addAll(var2, moreEntries);
      return var2;
   }
}
