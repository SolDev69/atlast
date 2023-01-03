package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEntry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.gen.structure.LootEntry;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EnchantedBookItem extends Item {
   @Environment(EnvType.CLIENT)
   @Override
   public boolean hasEnchantmentGlint(ItemStack stack) {
      return true;
   }

   @Override
   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   @Override
   public Rarity getRarity(ItemStack stack) {
      return this.getStoredEnchantments(stack).size() > 0 ? Rarity.UNCOMMON : super.getRarity(stack);
   }

   public NbtList getStoredEnchantments(ItemStack stack) {
      NbtCompound var2 = stack.getNbt();
      return var2 != null && var2.isType("StoredEnchantments", 9) ? (NbtList)var2.get("StoredEnchantments") : new NbtList();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addHoverText(ItemStack stack, PlayerEntity player, List tooltip, boolean advanced) {
      super.addHoverText(stack, player, tooltip, advanced);
      NbtList var5 = this.getStoredEnchantments(stack);
      if (var5 != null) {
         for(int var6 = 0; var6 < var5.size(); ++var6) {
            short var7 = var5.getCompound(var6).getShort("id");
            short var8 = var5.getCompound(var6).getShort("lvl");
            if (Enchantment.byRawId(var7) != null) {
               tooltip.add(Enchantment.byRawId(var7).getDisplayName(var8));
            }
         }
      }
   }

   public void addEnchantment(ItemStack stack, EnchantmentEntry entry) {
      NbtList var3 = this.getStoredEnchantments(stack);
      boolean var4 = true;

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         NbtCompound var6 = var3.getCompound(var5);
         if (var6.getShort("id") == entry.enchantment.id) {
            if (var6.getShort("lvl") < entry.level) {
               var6.putShort("lvl", (short)entry.level);
            }

            var4 = false;
            break;
         }
      }

      if (var4) {
         NbtCompound var7 = new NbtCompound();
         var7.putShort("id", (short)entry.enchantment.id);
         var7.putShort("lvl", (short)entry.level);
         var3.add(var7);
      }

      if (!stack.hasNbt()) {
         stack.setNbt(new NbtCompound());
      }

      stack.getNbt().put("StoredEnchantments", var3);
   }

   public ItemStack getStackWithEnchantment(EnchantmentEntry entry) {
      ItemStack var2 = new ItemStack(this);
      this.addEnchantment(var2, entry);
      return var2;
   }

   @Environment(EnvType.CLIENT)
   public void getStacksWithEnchantment(Enchantment enchantment, List list) {
      for(int var3 = enchantment.getMinLevel(); var3 <= enchantment.getMaxLevel(); ++var3) {
         list.add(this.getStackWithEnchantment(new EnchantmentEntry(enchantment, var3)));
      }
   }

   public LootEntry getRandomChestEntry(Random random) {
      return this.getRandomChestEntry(random, 1, 1, 1);
   }

   public LootEntry getRandomChestEntry(Random random, int x, int y, int z) {
      ItemStack var5 = new ItemStack(Items.BOOK, 1, 0);
      EnchantmentHelper.addRandomEnchantment(random, var5, 30);
      return new LootEntry(var5, x, y, z);
   }
}
