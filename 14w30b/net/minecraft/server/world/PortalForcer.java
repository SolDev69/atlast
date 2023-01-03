package net.minecraft.server.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.Long2ObjectHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class PortalForcer {
   private final ServerWorld world;
   private final Random random;
   private final Long2ObjectHashMap portalCache = new Long2ObjectHashMap();
   private final List portalCacheKeys = Lists.newArrayList();

   public PortalForcer(ServerWorld world) {
      this.world = world;
      this.random = new Random(world.getSeed());
   }

   public void onDimensionChanged(Entity entity, float x) {
      if (this.world.dimension.getId() != 1) {
         if (!this.findNetherPortal(entity, x)) {
            this.generateNetherPortal(entity);
            this.findNetherPortal(entity, x);
         }
      } else {
         int var3 = MathHelper.floor(entity.x);
         int var4 = MathHelper.floor(entity.y) - 1;
         int var5 = MathHelper.floor(entity.z);
         byte var6 = 1;
         byte var7 = 0;

         for(int var8 = -2; var8 <= 2; ++var8) {
            for(int var9 = -2; var9 <= 2; ++var9) {
               for(int var10 = -1; var10 < 3; ++var10) {
                  int var11 = var3 + var9 * var6 + var8 * var7;
                  int var12 = var4 + var10;
                  int var13 = var5 + var9 * var7 - var8 * var6;
                  boolean var14 = var10 < 0;
                  this.world.setBlockState(new BlockPos(var11, var12, var13), var14 ? Blocks.OBSIDIAN.defaultState() : Blocks.AIR.defaultState());
               }
            }
         }

         entity.refreshPositionAndAngles((double)var3, (double)var4, (double)var5, entity.yaw, 0.0F);
         entity.velocityX = entity.velocityY = entity.velocityZ = 0.0;
      }
   }

   public boolean findNetherPortal(Entity entity, float x) {
      boolean var3 = true;
      double var4 = -1.0;
      int var6 = MathHelper.floor(entity.x);
      int var7 = MathHelper.floor(entity.z);
      boolean var8 = true;
      Object var9 = BlockPos.ORIGIN;
      long var10 = ChunkPos.toLong(var6, var7);
      if (this.portalCache.contains(var10)) {
         PortalForcer.PortalPos var12 = (PortalForcer.PortalPos)this.portalCache.get(var10);
         var4 = 0.0;
         var9 = var12;
         var12.lastUseTime = this.world.getTime();
         var8 = false;
      } else {
         BlockPos var34 = new BlockPos(entity);

         for(int var13 = -128; var13 <= 128; ++var13) {
            BlockPos var16;
            for(int var14 = -128; var14 <= 128; ++var14) {
               for(BlockPos var15 = var34.add(var13, this.world.getDimensionHeight() - 1 - var34.getY(), var14); var15.getY() >= 0; var15 = var16) {
                  var16 = var15.down();
                  if (this.world.getBlockState(var15).getBlock() == Blocks.NETHER_PORTAL) {
                     while(this.world.getBlockState(var16 = var15.down()).getBlock() == Blocks.NETHER_PORTAL) {
                        var15 = var16;
                     }

                     double var17 = var15.squaredDistanceTo(var34);
                     if (var4 < 0.0 || var17 < var4) {
                        var4 = var17;
                        var9 = var15;
                     }
                  }
               }
            }
         }
      }

      if (var4 >= 0.0) {
         if (var8) {
            this.portalCache.put(var10, new PortalForcer.PortalPos((BlockPos)var9, this.world.getTime()));
            this.portalCacheKeys.add(var10);
         }

         double var35 = (double)((BlockPos)var9).getX() + 0.5;
         double var37 = (double)((BlockPos)var9).getY() + 0.5;
         double var38 = (double)((BlockPos)var9).getZ() + 0.5;
         Direction var18 = null;
         if (this.world.getBlockState(((BlockPos)var9).west()).getBlock() == Blocks.NETHER_PORTAL) {
            var18 = Direction.NORTH;
         }

         if (this.world.getBlockState(((BlockPos)var9).east()).getBlock() == Blocks.NETHER_PORTAL) {
            var18 = Direction.SOUTH;
         }

         if (this.world.getBlockState(((BlockPos)var9).north()).getBlock() == Blocks.NETHER_PORTAL) {
            var18 = Direction.EAST;
         }

         if (this.world.getBlockState(((BlockPos)var9).south()).getBlock() == Blocks.NETHER_PORTAL) {
            var18 = Direction.WEST;
         }

         Direction var19 = Direction.byIdHorizontal(entity.getFacing());
         if (var18 != null) {
            Direction var20 = var18.counterClockwiseY();
            BlockPos var21 = ((BlockPos)var9).offset(var18);
            boolean var22 = this.m_02dkeyjad(var21);
            boolean var23 = this.m_02dkeyjad(var21.offset(var20));
            if (var23 && var22) {
               var9 = ((BlockPos)var9).offset(var20);
               var18 = var18.getOpposite();
               var20 = var20.getOpposite();
               BlockPos var24 = ((BlockPos)var9).offset(var18);
               var22 = this.m_02dkeyjad(var24);
               var23 = this.m_02dkeyjad(var24.offset(var20));
            }

            float var40 = 0.5F;
            float var25 = 0.5F;
            if (!var23 && var22) {
               var40 = 1.0F;
            } else if (var23 && !var22) {
               var40 = 0.0F;
            } else if (var23) {
               var25 = 0.0F;
            }

            var35 = (double)((BlockPos)var9).getX() + 0.5;
            var37 = (double)((BlockPos)var9).getY() + 0.5;
            var38 = (double)((BlockPos)var9).getZ() + 0.5;
            var35 += (double)((float)var20.getOffsetX() * var40 + (float)var18.getOffsetX() * var25);
            var38 += (double)((float)var20.getOffsetZ() * var40 + (float)var18.getOffsetZ() * var25);
            float var26 = 0.0F;
            float var27 = 0.0F;
            float var28 = 0.0F;
            float var29 = 0.0F;
            if (var18 == var19) {
               var26 = 1.0F;
               var27 = 1.0F;
            } else if (var18 == var19.getOpposite()) {
               var26 = -1.0F;
               var27 = -1.0F;
            } else if (var18 == var19.clockwiseY()) {
               var28 = 1.0F;
               var29 = -1.0F;
            } else {
               var28 = -1.0F;
               var29 = 1.0F;
            }

            double var30 = entity.velocityX;
            double var32 = entity.velocityZ;
            entity.velocityX = var30 * (double)var26 + var32 * (double)var29;
            entity.velocityZ = var30 * (double)var28 + var32 * (double)var27;
            entity.yaw = x - (float)(var19.getIdHorizontal() * 90) + (float)(var18.getIdHorizontal() * 90);
         } else {
            entity.velocityX = entity.velocityY = entity.velocityZ = 0.0;
         }

         entity.refreshPositionAndAngles(var35, var37, var38, entity.yaw, entity.pitch);
         return true;
      } else {
         return false;
      }
   }

   private boolean m_02dkeyjad(BlockPos c_76varpwca) {
      return !this.world.isAir(c_76varpwca) || !this.world.isAir(c_76varpwca.up());
   }

   public boolean generateNetherPortal(Entity entity) {
      byte var2 = 16;
      double var3 = -1.0;
      int var5 = MathHelper.floor(entity.x);
      int var6 = MathHelper.floor(entity.y);
      int var7 = MathHelper.floor(entity.z);
      int var8 = var5;
      int var9 = var6;
      int var10 = var7;
      int var11 = 0;
      int var12 = this.random.nextInt(4);

      for(int var13 = var5 - var2; var13 <= var5 + var2; ++var13) {
         double var14 = (double)var13 + 0.5 - entity.x;

         for(int var16 = var7 - var2; var16 <= var7 + var2; ++var16) {
            double var17 = (double)var16 + 0.5 - entity.z;

            label296:
            for(int var19 = this.world.getDimensionHeight() - 1; var19 >= 0; --var19) {
               if (this.world.isAir(new BlockPos(var13, var19, var16))) {
                  while(var19 > 0 && this.world.isAir(new BlockPos(var13, var19 - 1, var16))) {
                     --var19;
                  }

                  for(int var20 = var12; var20 < var12 + 4; ++var20) {
                     int var21 = var20 % 2;
                     int var22 = 1 - var21;
                     if (var20 % 4 >= 2) {
                        var21 = -var21;
                        var22 = -var22;
                     }

                     for(int var23 = 0; var23 < 3; ++var23) {
                        for(int var24 = 0; var24 < 4; ++var24) {
                           for(int var25 = -1; var25 < 4; ++var25) {
                              int var26 = var13 + (var24 - 1) * var21 + var23 * var22;
                              int var27 = var19 + var25;
                              int var28 = var16 + (var24 - 1) * var22 - var23 * var21;
                              if (var25 < 0 && !this.world.getBlockState(new BlockPos(var26, var27, var28)).getBlock().getMaterial().isSolid()
                                 || var25 >= 0 && !this.world.isAir(new BlockPos(var26, var27, var28))) {
                                 continue label296;
                              }
                           }
                        }
                     }

                     double var51 = (double)var19 + 0.5 - entity.y;
                     double var61 = var14 * var14 + var51 * var51 + var17 * var17;
                     if (var3 < 0.0 || var61 < var3) {
                        var3 = var61;
                        var8 = var13;
                        var9 = var19;
                        var10 = var16;
                        var11 = var20 % 4;
                     }
                  }
               }
            }
         }
      }

      if (var3 < 0.0) {
         for(int var30 = var5 - var2; var30 <= var5 + var2; ++var30) {
            double var31 = (double)var30 + 0.5 - entity.x;

            for(int var33 = var7 - var2; var33 <= var7 + var2; ++var33) {
               double var35 = (double)var33 + 0.5 - entity.z;

               label233:
               for(int var37 = this.world.getDimensionHeight() - 1; var37 >= 0; --var37) {
                  if (this.world.isAir(new BlockPos(var30, var37, var33))) {
                     while(var37 > 0 && this.world.isAir(new BlockPos(var30, var37 - 1, var33))) {
                        --var37;
                     }

                     for(int var40 = var12; var40 < var12 + 2; ++var40) {
                        int var43 = var40 % 2;
                        int var47 = 1 - var43;

                        for(int var52 = 0; var52 < 4; ++var52) {
                           for(int var57 = -1; var57 < 4; ++var57) {
                              int var62 = var30 + (var52 - 1) * var43;
                              int var67 = var37 + var57;
                              int var69 = var33 + (var52 - 1) * var47;
                              if (var57 < 0 && !this.world.getBlockState(new BlockPos(var62, var67, var69)).getBlock().getMaterial().isSolid()
                                 || var57 >= 0 && !this.world.isAir(new BlockPos(var62, var67, var69))) {
                                 continue label233;
                              }
                           }
                        }

                        double var53 = (double)var37 + 0.5 - entity.y;
                        double var63 = var31 * var31 + var53 * var53 + var35 * var35;
                        if (var3 < 0.0 || var63 < var3) {
                           var3 = var63;
                           var8 = var30;
                           var9 = var37;
                           var10 = var33;
                           var11 = var40 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int var32 = var8;
      int var15 = var9;
      int var34 = var10;
      int var36 = var11 % 2;
      int var18 = 1 - var36;
      if (var11 % 4 >= 2) {
         var36 = -var36;
         var18 = -var18;
      }

      if (var3 < 0.0) {
         var9 = MathHelper.clamp(var9, 70, this.world.getDimensionHeight() - 10);
         var15 = var9;

         for(int var38 = -1; var38 <= 1; ++var38) {
            for(int var41 = 1; var41 < 3; ++var41) {
               for(int var44 = -1; var44 < 3; ++var44) {
                  int var48 = var32 + (var41 - 1) * var36 + var38 * var18;
                  int var54 = var15 + var44;
                  int var58 = var34 + (var41 - 1) * var18 - var38 * var36;
                  boolean var64 = var44 < 0;
                  this.world.setBlockState(new BlockPos(var48, var54, var58), var64 ? Blocks.OBSIDIAN.defaultState() : Blocks.AIR.defaultState());
               }
            }
         }
      }

      int var39 = var36 != 0 ? PortalBlock.getMatadataForAxis(Direction.Axis.X) : PortalBlock.getMatadataForAxis(Direction.Axis.Z);

      for(int var42 = 0; var42 < 4; ++var42) {
         for(int var45 = 0; var45 < 4; ++var45) {
            for(int var49 = -1; var49 < 4; ++var49) {
               int var55 = var32 + (var45 - 1) * var36;
               int var59 = var15 + var49;
               int var65 = var34 + (var45 - 1) * var18;
               boolean var68 = var45 == 0 || var45 == 3 || var49 == -1 || var49 == 3;
               this.world
                  .setBlockState(
                     new BlockPos(var55, var59, var65), var68 ? Blocks.OBSIDIAN.defaultState() : Blocks.NETHER_PORTAL.getStateFromMetadata(var39), 2
                  );
            }
         }

         for(int var46 = 0; var46 < 4; ++var46) {
            for(int var50 = -1; var50 < 4; ++var50) {
               int var56 = var32 + (var46 - 1) * var36;
               int var60 = var15 + var50;
               int var66 = var34 + (var46 - 1) * var18;
               this.world.updateNeighbors(new BlockPos(var56, var60, var66), this.world.getBlockState(new BlockPos(var56, var60, var66)).getBlock());
            }
         }
      }

      return true;
   }

   public void tick(long time) {
      if (time % 100L == 0L) {
         Iterator var3 = this.portalCacheKeys.iterator();
         long var4 = time - 600L;

         while(var3.hasNext()) {
            Long var6 = (Long)var3.next();
            PortalForcer.PortalPos var7 = (PortalForcer.PortalPos)this.portalCache.get(var6);
            if (var7 == null || var7.lastUseTime < var4) {
               var3.remove();
               this.portalCache.remove(var6);
            }
         }
      }
   }

   public class PortalPos extends BlockPos {
      public long lastUseTime;

      public PortalPos(BlockPos x, long y) {
         super(x.getX(), x.getY(), x.getZ());
         this.lastUseTime = y;
      }
   }
}
