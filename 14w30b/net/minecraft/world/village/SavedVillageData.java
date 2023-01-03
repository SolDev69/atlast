package net.minecraft.world.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.saved.SavedData;

public class SavedVillageData extends SavedData {
   private World world;
   private final List villagerPositions = Lists.newArrayList();
   private final List newDoors = Lists.newArrayList();
   private final List villages = Lists.newArrayList();
   private int ticks;

   public SavedVillageData(String string) {
      super(string);
   }

   public SavedVillageData(World world) {
      super(getId(world.dimension));
      this.world = world;
      this.markDirty();
   }

   public void setWorld(World world) {
      this.world = world;

      for(Village var3 : this.villages) {
         var3.setWorld(world);
      }
   }

   public void addVillagerPosition(BlockPos pos) {
      if (this.villagerPositions.size() <= 64) {
         if (!this.hasVillagerPosition(pos)) {
            this.villagerPositions.add(pos);
         }
      }
   }

   public void tick() {
      ++this.ticks;

      for(Village var2 : this.villages) {
         var2.tick(this.ticks);
      }

      this.removeVillagesWithoutDoors();
      this.removeOldestVillagerPosition();
      this.addDoorsToVillages();
      if (this.ticks % 400 == 0) {
         this.markDirty();
      }
   }

   private void removeVillagesWithoutDoors() {
      Iterator var1 = this.villages.iterator();

      while(var1.hasNext()) {
         Village var2 = (Village)var1.next();
         if (var2.hasNoDoors()) {
            var1.remove();
            this.markDirty();
         }
      }
   }

   public List getVillages() {
      return this.villages;
   }

   public Village getClosestVillage(BlockPos pos, int range) {
      Village var3 = null;
      double var4 = Float.MAX_VALUE;

      for(Village var7 : this.villages) {
         double var8 = var7.getCenter().squaredDistanceTo(pos);
         if (!(var8 >= var4)) {
            float var10 = (float)(range + var7.getRadius());
            if (!(var8 > (double)(var10 * var10))) {
               var3 = var7;
               var4 = var8;
            }
         }
      }

      return var3;
   }

   private void removeOldestVillagerPosition() {
      if (!this.villagerPositions.isEmpty()) {
         this.findDoorsAround((BlockPos)this.villagerPositions.remove(0));
      }
   }

   private void addDoorsToVillages() {
      for(int var1 = 0; var1 < this.newDoors.size(); ++var1) {
         VillageDoor var2 = (VillageDoor)this.newDoors.get(var1);
         Village var3 = this.getClosestVillage(var2.getPos(), 32);
         if (var3 == null) {
            var3 = new Village(this.world);
            this.villages.add(var3);
            this.markDirty();
         }

         var3.addDoor(var2);
      }

      this.newDoors.clear();
   }

   private void findDoorsAround(BlockPos pos) {
      byte var2 = 16;
      byte var3 = 4;
      byte var4 = 16;

      for(int var5 = -var2; var5 < var2; ++var5) {
         for(int var6 = -var3; var6 < var3; ++var6) {
            for(int var7 = -var4; var7 < var4; ++var7) {
               BlockPos var8 = pos.add(var5, var6, var7);
               if (this.isWoodenDoor(var8)) {
                  VillageDoor var9 = this.getDoor(var8);
                  if (var9 == null) {
                     this.addNewDoor(var8);
                  } else {
                     var9.setLastVillageTick(this.ticks);
                  }
               }
            }
         }
      }
   }

   private VillageDoor getDoor(BlockPos pos) {
      for(VillageDoor var3 : this.newDoors) {
         if (var3.getPos().getX() == pos.getX() && var3.getPos().getZ() == pos.getZ() && Math.abs(var3.getPos().getY() - pos.getY()) <= 1) {
            return var3;
         }
      }

      for(Village var6 : this.villages) {
         VillageDoor var4 = var6.getDoor(pos);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   private void addNewDoor(BlockPos pos) {
      Direction var2 = DoorBlock.getFacingFromMetadata(this.world, pos);
      Direction var3 = var2.getOpposite();
      int var4 = this.getDistanceWithSkyAccess(pos, var2, 5);
      int var5 = this.getDistanceWithSkyAccess(pos, var3, var4 + 1);
      if (var4 != var5) {
         this.newDoors.add(new VillageDoor(pos, var4 < var5 ? var2 : var3, this.ticks));
      }
   }

   private int getDistanceWithSkyAccess(BlockPos pos, Direction dir, int limit) {
      int var4 = 0;

      for(int var5 = 1; var5 <= 5; ++var5) {
         if (this.world.hasSkyAccess(pos.offset(dir, var5))) {
            if (++var4 >= limit) {
               return var4;
            }
         }
      }

      return var4;
   }

   private boolean hasVillagerPosition(BlockPos pos) {
      for(BlockPos var3 : this.villagerPositions) {
         if (var3.equals(pos)) {
            return true;
         }
      }

      return false;
   }

   private boolean isWoodenDoor(BlockPos pos) {
      return this.world.getBlockState(pos).getBlock() == Blocks.WOODEN_DOOR;
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      this.ticks = nbt.getInt("Tick");
      NbtList var2 = nbt.getList("Villages", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NbtCompound var4 = var2.getCompound(var3);
         Village var5 = new Village();
         var5.readNbt(var4);
         this.villages.add(var5);
      }
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      nbt.putInt("Tick", this.ticks);
      NbtList var2 = new NbtList();

      for(Village var4 : this.villages) {
         NbtCompound var5 = new NbtCompound();
         var4.writeNbt(var5);
         var2.add(var5);
      }

      nbt.put("Villages", var2);
   }

   public static String getId(Dimension dimension) {
      return "villages" + dimension.getDataSuffix();
   }
}
