package net.minecraft.item;

import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenseBehavior;
import net.minecraft.block.dispenser.DispenseItemBehavior;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.IBlockSource;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ArmorItem extends Item {
   private static final int[] BASE_DURABILITY = new int[]{11, 16, 15, 13};
   public static final String[] EMPTY_SLOTS = new String[]{
      "minecraft:items/empty_armor_slot_helmet",
      "minecraft:items/empty_armor_slot_chestplate",
      "minecraft:items/empty_armor_slot_leggings",
      "minecraft:items/empty_armor_slot_boots"
   };
   private static final DispenseBehavior DISPENSE_BEHAVIOR = new DispenseItemBehavior() {
      @Override
      protected ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
         BlockPos var3 = source.getPos().offset(DispenserBlock.getDirection(source.getBlockMetadata()));
         int var4 = var3.getX();
         int var5 = var3.getY();
         int var6 = var3.getZ();
         Box var7 = new Box((double)var4, (double)var5, (double)var6, (double)(var4 + 1), (double)(var5 + 1), (double)(var6 + 1));
         List var8 = source.getWorld()
            .getEntities(LivingEntity.class, var7, Predicates.and(EntityFilter.NOT_SPECTATOR, new EntityFilter.CanPickupItemsFilter(stack)));
         if (var8.size() > 0) {
            LivingEntity var9 = (LivingEntity)var8.get(0);
            int var10 = var9 instanceof PlayerEntity ? 1 : 0;
            int var11 = MobEntity.getSlotForEquipment(stack);
            ItemStack var12 = stack.copy();
            var12.size = 1;
            var9.setEquipmentStack(var11 - var10, var12);
            if (var9 instanceof MobEntity) {
               ((MobEntity)var9).setInventoryDropChances(var11, 2.0F);
            }

            --stack.size;
            return stack;
         } else {
            return super.dispenseItem(source, stack);
         }
      }
   };
   public final int slot;
   public final int protection;
   public final int materialId;
   private final ArmorItem.Material material;

   public ArmorItem(ArmorItem.Material material, int materialId, int slot) {
      this.material = material;
      this.slot = slot;
      this.materialId = materialId;
      this.protection = material.getProtection(slot);
      this.setMaxDamage(material.getDurability(slot));
      this.maxStackSize = 1;
      this.setItemGroup(ItemGroup.COMBAT);
      DispenserBlock.BEHAVIORS.put(this, DISPENSE_BEHAVIOR);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getDisplayColor(ItemStack stack, int color) {
      if (color > 0) {
         return 16777215;
      } else {
         int var3 = this.getColor(stack);
         if (var3 < 0) {
            var3 = 16777215;
         }

         return var3;
      }
   }

   @Override
   public int getEnchantability() {
      return this.material.getEnchantability();
   }

   public ArmorItem.Material getMaterial() {
      return this.material;
   }

   public boolean hasColor(ItemStack stack) {
      if (this.material != ArmorItem.Material.CLOTH) {
         return false;
      } else if (!stack.hasNbt()) {
         return false;
      } else if (!stack.getNbt().isType("display", 10)) {
         return false;
      } else {
         return stack.getNbt().getCompound("display").isType("color", 3);
      }
   }

   public int getColor(ItemStack stack) {
      if (this.material != ArmorItem.Material.CLOTH) {
         return -1;
      } else {
         NbtCompound var2 = stack.getNbt();
         if (var2 != null) {
            NbtCompound var3 = var2.getCompound("display");
            if (var3 != null && var3.isType("color", 3)) {
               return var3.getInt("color");
            }
         }

         return 10511680;
      }
   }

   public void removeColor(ItemStack stack) {
      if (this.material == ArmorItem.Material.CLOTH) {
         NbtCompound var2 = stack.getNbt();
         if (var2 != null) {
            NbtCompound var3 = var2.getCompound("display");
            if (var3.contains("color")) {
               var3.remove("color");
            }
         }
      }
   }

   public void setColor(ItemStack stack, int color) {
      if (this.material != ArmorItem.Material.CLOTH) {
         throw new UnsupportedOperationException("Can't dye non-leather!");
      } else {
         NbtCompound var3 = stack.getNbt();
         if (var3 == null) {
            var3 = new NbtCompound();
            stack.setNbt(var3);
         }

         NbtCompound var4 = var3.getCompound("display");
         if (!var3.isType("display", 10)) {
            var3.put("display", var4);
         }

         var4.putInt("color", color);
      }
   }

   @Override
   public boolean isReparable(ItemStack stack, ItemStack ingredient) {
      return this.material.getRepairIngredient() == ingredient.getItem() ? true : super.isReparable(stack, ingredient);
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      int var4 = MobEntity.getSlotForEquipment(stack) - 1;
      ItemStack var5 = player.getArmorStack(var4);
      if (var5 == null) {
         player.setEquipmentStack(var4, stack.copy());
         stack.size = 0;
      }

      return stack;
   }

   public static enum Material {
      CLOTH("leather", 5, new int[]{1, 3, 2, 1}, 15),
      CHAIN("chainmail", 15, new int[]{2, 5, 4, 1}, 12),
      IRON("iron", 15, new int[]{2, 6, 5, 2}, 9),
      GOLD("gold", 7, new int[]{2, 5, 3, 1}, 25),
      DIAMOND("diamond", 33, new int[]{3, 8, 6, 3}, 10);

      private final String id;
      private final int durability;
      private final int[] protection;
      private final int enchantability;

      private Material(String id, int durability, int[] protection, int enchantability) {
         this.id = id;
         this.durability = durability;
         this.protection = protection;
         this.enchantability = enchantability;
      }

      public int getDurability(int slot) {
         return ArmorItem.BASE_DURABILITY[slot] * this.durability;
      }

      public int getProtection(int slot) {
         return this.protection[slot];
      }

      public int getEnchantability() {
         return this.enchantability;
      }

      public Item getRepairIngredient() {
         if (this == CLOTH) {
            return Items.LEATHER;
         } else if (this == CHAIN) {
            return Items.IRON_INGOT;
         } else if (this == GOLD) {
            return Items.GOLD_INGOT;
         } else if (this == IRON) {
            return Items.IRON_INGOT;
         } else {
            return this == DIAMOND ? Items.DIAMOND : null;
         }
      }

      @Environment(EnvType.CLIENT)
      public String getId() {
         return this.id;
      }
   }
}
