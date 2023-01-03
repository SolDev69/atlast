package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawner;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.locale.I18n;
import net.minecraft.stat.Stats;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SpawnEggItem extends Item {
   public SpawnEggItem() {
      this.setStackable(true);
      this.setItemGroup(ItemGroup.MISC);
   }

   @Override
   public String getName(ItemStack stack) {
      String var2 = ("" + I18n.translate(this.getTranslationKey() + ".name")).trim();
      String var3 = Entities.getId(stack.getMetadata());
      if (var3 != null) {
         var2 = var2 + " " + I18n.translate("entity." + var3 + ".name");
      }

      return var2;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getDisplayColor(ItemStack stack, int color) {
      Entities.SpawnEggData var3 = (Entities.SpawnEggData)Entities.RAW_ID_TO_SPAWN_EGG_DATA.get(stack.getMetadata());
      if (var3 != null) {
         return color == 0 ? var3.baseColor : var3.spotsColor;
      } else {
         return 16777215;
      }
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else if (!player.canUseItem(pos.offset(face), face, stack)) {
         return false;
      } else {
         BlockState var9 = world.getBlockState(pos);
         if (var9.getBlock() == Blocks.MOB_SPAWNER) {
            BlockEntity var10 = world.getBlockEntity(pos);
            if (var10 instanceof MobSpawnerBlockEntity) {
               MobSpawner var11 = ((MobSpawnerBlockEntity)var10).getSpawner();
               var11.setType(Entities.getId(stack.getMetadata()));
               var10.markDirty();
               world.onBlockChanged(pos);
               if (!player.abilities.creativeMode) {
                  --stack.size;
               }

               return true;
            }
         }

         pos = pos.offset(face);
         double var14 = 0.0;
         if (face == Direction.UP && var9 instanceof FenceBlock) {
            var14 = 0.5;
         }

         Entity var12 = spawnEntity(world, stack.getMetadata(), (double)pos.getX() + 0.5, (double)pos.getY() + var14, (double)pos.getZ() + 0.5);
         if (var12 != null) {
            if (var12 instanceof LivingEntity && stack.hasCustomHoverName()) {
               var12.setCustomName(stack.getHoverName());
            }

            if (!player.abilities.creativeMode) {
               --stack.size;
            }
         }

         return true;
      }
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      if (world.isClient) {
         return stack;
      } else {
         HitResult var4 = this.getUseTarget(world, player, true);
         if (var4 == null) {
            return stack;
         } else {
            if (var4.type == HitResult.Type.BLOCK) {
               BlockPos var5 = var4.getBlockPos();
               if (!world.canModify(player, var5)) {
                  return stack;
               }

               if (!player.canUseItem(var5, var4.face, stack)) {
                  return stack;
               }

               if (world.getBlockState(var5).getBlock() instanceof LiquidBlock) {
                  Entity var6 = spawnEntity(world, stack.getMetadata(), (double)var5.getX() + 0.5, (double)var5.getY() + 0.5, (double)var5.getZ() + 0.5);
                  if (var6 != null) {
                     if (var6 instanceof LivingEntity && stack.hasCustomHoverName()) {
                        ((MobEntity)var6).setCustomName(stack.getHoverName());
                     }

                     if (!player.abilities.creativeMode) {
                        --stack.size;
                     }

                     player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
                  }
               }
            }

            return stack;
         }
      }
   }

   public static Entity spawnEntity(World world, int id, double x, double y, double z) {
      if (!Entities.RAW_ID_TO_SPAWN_EGG_DATA.containsKey(id)) {
         return null;
      } else {
         Entity var8 = null;

         for(int var9 = 0; var9 < 1; ++var9) {
            var8 = Entities.create(id, world);
            if (var8 instanceof LivingEntity) {
               MobEntity var10 = (MobEntity)var8;
               var8.refreshPositionAndAngles(x, y, z, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);
               var10.headYaw = var10.yaw;
               var10.bodyYaw = var10.yaw;
               var10.initialize(world.getLocalDifficulty(new BlockPos(var10)), null);
               world.addEntity(var8);
               var10.playAmbientSound();
            }
         }

         return var8;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      for(Entities.SpawnEggData var5 : Entities.RAW_ID_TO_SPAWN_EGG_DATA.values()) {
         list.add(new ItemStack(item, 1, var5.id));
      }
   }
}
