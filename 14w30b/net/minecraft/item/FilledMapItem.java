package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.map.SavedMapData;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FilledMapItem extends NetworkSyncedItem {
   protected FilledMapItem() {
      this.setStackable(true);
   }

   @Environment(EnvType.CLIENT)
   public static SavedMapData getMapData(int id, World world) {
      String var2 = "map_" + id;
      SavedMapData var3 = (SavedMapData)world.loadSavedData(SavedMapData.class, var2);
      if (var3 == null) {
         var3 = new SavedMapData(var2);
         world.setSavedData(var2, var3);
      }

      return var3;
   }

   public SavedMapData getSavedMapData(ItemStack stack, World world) {
      String var3 = "map_" + stack.getMetadata();
      SavedMapData var4 = (SavedMapData)world.loadSavedData(SavedMapData.class, var3);
      if (var4 == null && !world.isClient) {
         stack.setDamage(world.getSavedDataCount("map"));
         var3 = "map_" + stack.getMetadata();
         var4 = new SavedMapData(var3);
         var4.scale = 3;
         int var5 = 128 * (1 << var4.scale);
         var4.centerX = Math.round((float)world.getData().getSpawnX() / (float)var5) * var5;
         var4.centerZ = Math.round((float)(world.getData().getSpawnZ() / var5)) * var5;
         var4.dimension = (byte)world.dimension.getId();
         var4.markDirty();
         world.setSavedData(var3, var4);
      }

      return var4;
   }

   public void update(World world, Entity entity, SavedMapData data) {
      if (world.dimension.getId() == data.dimension && entity instanceof PlayerEntity) {
         int var4 = 1 << data.scale;
         int var5 = data.centerX;
         int var6 = data.centerZ;
         int var7 = MathHelper.floor(entity.x - (double)var5) / var4 + 64;
         int var8 = MathHelper.floor(entity.z - (double)var6) / var4 + 64;
         int var9 = 128 / var4;
         if (world.dimension.isDark()) {
            var9 /= 2;
         }

         SavedMapData.Holder var10 = data.addHolder((PlayerEntity)entity);
         ++var10.step;
         boolean var11 = false;

         for(int var12 = var7 - var9 + 1; var12 < var7 + var9; ++var12) {
            if ((var12 & 15) == (var10.step & 15) || var11) {
               var11 = false;
               double var13 = 0.0;

               for(int var15 = var8 - var9 - 1; var15 < var8 + var9; ++var15) {
                  if (var12 >= 0 && var15 >= -1 && var12 < 128 && var15 < 128) {
                     int var16 = var12 - var7;
                     int var17 = var15 - var8;
                     boolean var18 = var16 * var16 + var17 * var17 > (var9 - 2) * (var9 - 2);
                     int var19 = (var5 / var4 + var12 - 64) * var4;
                     int var20 = (var6 / var4 + var15 - 64) * var4;
                     HashMultiset var21 = HashMultiset.create();
                     WorldChunk var22 = world.getChunk(new BlockPos(var19, 0, var20));
                     if (!var22.isEmpty()) {
                        int var23 = var19 & 15;
                        int var24 = var20 & 15;
                        int var25 = 0;
                        double var26 = 0.0;
                        if (world.dimension.isDark()) {
                           int var28 = var19 + var20 * 231871;
                           var28 = var28 * var28 * 31287121 + var28 * 11;
                           if ((var28 >> 20 & 1) == 0) {
                              var21.add(Blocks.DIRT.getMaterialColor(Blocks.DIRT.defaultState().set(DirtBlock.VARIANT, DirtBlock.Variant.DIRT)), 10);
                           } else {
                              var21.add(Blocks.STONE.getMaterialColor(Blocks.STONE.defaultState().set(StoneBlock.VARIANT, StoneBlock.Variant.STONE)), 100);
                           }

                           var26 = 100.0;
                        } else {
                           for(int var36 = 0; var36 < var4; ++var36) {
                              for(int var29 = 0; var29 < var4; ++var29) {
                                 int var30 = var22.getHeight(var36 + var23, var29 + var24) + 1;
                                 BlockState var31 = Blocks.AIR.defaultState();
                                 if (var30 > 1) {
                                    do {
                                       var31 = var22.getBlockState(new BlockPos(var36 + var23, --var30, var29 + var24));
                                    } while(var31.getBlock().getMaterialColor(var31) == MaterialColor.AIR && var30 > 0);

                                    if (var30 > 0 && var31.getBlock().getMaterial().isLiquid()) {
                                       int var32 = var30 - 1;

                                       Block var33;
                                       do {
                                          var33 = var22.getBlock(var36 + var23, var32--, var29 + var24);
                                          ++var25;
                                       } while(var32 > 0 && var33.getMaterial().isLiquid());
                                    }
                                 }

                                 var26 += (double)var30 / (double)(var4 * var4);
                                 var21.add(var31.getBlock().getMaterialColor(var31));
                              }
                           }
                        }

                        var25 /= var4 * var4;
                        double var37 = (var26 - var13) * 4.0 / (double)(var4 + 4) + ((double)(var12 + var15 & 1) - 0.5) * 0.4;
                        byte var39 = 1;
                        if (var37 > 0.6) {
                           var39 = 2;
                        }

                        if (var37 < -0.6) {
                           var39 = 0;
                        }

                        MaterialColor var40 = (MaterialColor)Iterables.getFirst(Multisets.copyHighestCountFirst(var21), MaterialColor.AIR);
                        if (var40 == MaterialColor.WATER) {
                           var37 = (double)var25 * 0.1 + (double)(var12 + var15 & 1) * 0.2;
                           var39 = 1;
                           if (var37 < 0.5) {
                              var39 = 2;
                           }

                           if (var37 > 0.9) {
                              var39 = 0;
                           }
                        }

                        var13 = var26;
                        if (var15 >= 0 && var16 * var16 + var17 * var17 < var9 * var9 && (!var18 || (var12 + var15 & 1) != 0)) {
                           byte var41 = data.colors[var12 + var15 * 128];
                           byte var42 = (byte)(var40.id * 4 + var39);
                           if (var41 != var42) {
                              data.colors[var12 + var15 * 128] = var42;
                              data.markDirty(var12, var15);
                              var11 = true;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void tick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
      if (!world.isClient) {
         SavedMapData var6 = this.getSavedMapData(stack, world);
         if (entity instanceof PlayerEntity) {
            PlayerEntity var7 = (PlayerEntity)entity;
            var6.tickHolder(var7, stack);
         }

         if (selected) {
            this.update(world, entity, var6);
         }
      }
   }

   @Override
   public Packet getUpdatePacket(ItemStack stack, World world, PlayerEntity player) {
      return this.getSavedMapData(stack, world).createUpdatePacket(stack, world, player);
   }

   @Override
   public void onResult(ItemStack stack, World world, PlayerEntity player) {
      if (stack.hasNbt() && stack.getNbt().getBoolean("map_is_scaling")) {
         SavedMapData var4 = Items.FILLED_MAP.getSavedMapData(stack, world);
         stack.setDamage(world.getSavedDataCount("map"));
         SavedMapData var5 = new SavedMapData("map_" + stack.getMetadata());
         var5.scale = (byte)(var4.scale + 1);
         if (var5.scale > 4) {
            var5.scale = 4;
         }

         var5.centerX = var4.centerX;
         var5.centerZ = var4.centerZ;
         var5.dimension = var4.dimension;
         var5.markDirty();
         world.setSavedData("map_" + stack.getMetadata(), var5);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addHoverText(ItemStack stack, PlayerEntity player, List tooltip, boolean advanced) {
      SavedMapData var5 = this.getSavedMapData(stack, player.world);
      if (advanced) {
         if (var5 == null) {
            tooltip.add("Unknown map");
         } else {
            tooltip.add("Scaling at 1:" + (1 << var5.scale));
            tooltip.add("(Level " + var5.scale + "/" + 4 + ")");
         }
      }
   }
}
