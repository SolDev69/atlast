package net.minecraft.world.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Village {
   private World world;
   private final List doors = Lists.newArrayList();
   private BlockPos combinedDoorPositions = BlockPos.ORIGIN;
   private BlockPos center = BlockPos.ORIGIN;
   private int radius;
   private int ticksOfLastDoorAdded;
   private int ticks;
   private int populationSize;
   private int ticksOfLastMating;
   private TreeMap playerReputations = new TreeMap();
   private List attackers = Lists.newArrayList();
   private int golems;

   public Village() {
   }

   public Village(World world) {
      this.world = world;
   }

   public void setWorld(World world) {
      this.world = world;
   }

   public void tick(int ticks) {
      this.ticks = ticks;
      this.cleanUpDoors();
      this.cleanUpAttackers();
      if (ticks % 20 == 0) {
         this.updatePopulationSize();
      }

      if (ticks % 30 == 0) {
         this.updateIronGolemCount();
      }

      int var2 = this.populationSize / 10;
      if (this.golems < var2 && this.doors.size() > 20 && this.world.random.nextInt(7000) == 0) {
         Vec3d var3 = this.getSpawnPosForIronGolem(this.center, 2, 4, 2);
         if (var3 != null) {
            IronGolemEntity var4 = new IronGolemEntity(this.world);
            var4.setPosition(var3.x, var3.y, var3.z);
            this.world.addEntity(var4);
            ++this.golems;
         }
      }
   }

   private Vec3d getSpawnPosForIronGolem(BlockPos center, int rangeX, int rangeY, int rangeZ) {
      for(int var5 = 0; var5 < 10; ++var5) {
         BlockPos var6 = center.add(this.world.random.nextInt(16) - 8, this.world.random.nextInt(6) - 3, this.world.random.nextInt(16) - 8);
         if (this.contains(var6) && this.canIronGolemSpawn(new BlockPos(rangeX, rangeY, rangeZ), var6)) {
            return new Vec3d((double)var6.getX(), (double)var6.getY(), (double)var6.getZ());
         }
      }

      return null;
   }

   private boolean canIronGolemSpawn(BlockPos range, BlockPos pos) {
      if (!World.hasSolidTop(this.world, pos.down())) {
         return false;
      } else {
         int var3 = pos.getX() - range.getX() / 2;
         int var4 = pos.getZ() - range.getZ() / 2;

         for(int var5 = var3; var5 < var3 + range.getX(); ++var5) {
            for(int var6 = pos.getY(); var6 < pos.getY() + range.getY(); ++var6) {
               for(int var7 = var4; var7 < var4 + range.getZ(); ++var7) {
                  if (this.world.getBlockState(new BlockPos(var5, var6, var7)).getBlock().isConductor()) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private void updateIronGolemCount() {
      List var1 = this.world
         .getEntities(
            IronGolemEntity.class,
            new Box(
               (double)(this.center.getX() - this.radius),
               (double)(this.center.getY() - 4),
               (double)(this.center.getZ() - this.radius),
               (double)(this.center.getX() + this.radius),
               (double)(this.center.getY() + 4),
               (double)(this.center.getZ() + this.radius)
            )
         );
      this.golems = var1.size();
   }

   private void updatePopulationSize() {
      List var1 = this.world
         .getEntities(
            VillagerEntity.class,
            new Box(
               (double)(this.center.getX() - this.radius),
               (double)(this.center.getY() - 4),
               (double)(this.center.getZ() - this.radius),
               (double)(this.center.getX() + this.radius),
               (double)(this.center.getY() + 4),
               (double)(this.center.getZ() + this.radius)
            )
         );
      this.populationSize = var1.size();
      if (this.populationSize == 0) {
         this.playerReputations.clear();
      }
   }

   public BlockPos getCenter() {
      return this.center;
   }

   public int getRadius() {
      return this.radius;
   }

   public int getDoorCount() {
      return this.doors.size();
   }

   public int getTimeSinceLastDoorAdded() {
      return this.ticks - this.ticksOfLastDoorAdded;
   }

   public int getPopulationSize() {
      return this.populationSize;
   }

   public boolean contains(BlockPos pos) {
      return this.center.squaredDistanceTo(pos) < (double)(this.radius * this.radius);
   }

   public List getDoors() {
      return this.doors;
   }

   public VillageDoor getClosestDoor(BlockPos pos) {
      VillageDoor var2 = null;
      int var3 = Integer.MAX_VALUE;

      for(VillageDoor var5 : this.doors) {
         int var6 = var5.getSquaredDistanceTo(pos);
         if (var6 < var3) {
            var2 = var5;
            var3 = var6;
         }
      }

      return var2;
   }

   public VillageDoor getClosestTickingDoor(BlockPos pos) {
      VillageDoor var2 = null;
      int var3 = Integer.MAX_VALUE;

      for(VillageDoor var5 : this.doors) {
         int var6 = var5.getSquaredDistanceTo(pos);
         if (var6 > 256) {
            var6 *= 1000;
         } else {
            var6 = var5.getRestrictedTicks();
         }

         if (var6 < var3) {
            var2 = var5;
            var3 = var6;
         }
      }

      return var2;
   }

   public VillageDoor getDoor(BlockPos pos) {
      if (this.center.squaredDistanceTo(pos) > (double)(this.radius * this.radius)) {
         return null;
      } else {
         for(VillageDoor var3 : this.doors) {
            if (var3.getPos().getX() == pos.getX() && var3.getPos().getZ() == pos.getZ() && Math.abs(var3.getPos().getY() - pos.getY()) <= 1) {
               return var3;
            }
         }

         return null;
      }
   }

   public void addDoor(VillageDoor door) {
      this.doors.add(door);
      this.combinedDoorPositions = this.combinedDoorPositions.add(door.getPos());
      this.updateCenterAndRadius();
      this.ticksOfLastDoorAdded = door.getLastVillageTick();
   }

   public boolean hasNoDoors() {
      return this.doors.isEmpty();
   }

   public void addOrUpdateAttacker(LivingEntity attacker) {
      for(Village.Attacker var3 : this.attackers) {
         if (var3.attacker == attacker) {
            var3.ticks = this.ticks;
            return;
         }
      }

      this.attackers.add(new Village.Attacker(attacker, this.ticks));
   }

   public LivingEntity getClosestAttacker(LivingEntity entity) {
      double var2 = Double.MAX_VALUE;
      Village.Attacker var4 = null;

      for(int var5 = 0; var5 < this.attackers.size(); ++var5) {
         Village.Attacker var6 = (Village.Attacker)this.attackers.get(var5);
         double var7 = var6.attacker.getSquaredDistanceTo(entity);
         if (!(var7 > var2)) {
            var4 = var6;
            var2 = var7;
         }
      }

      return var4 != null ? var4.attacker : null;
   }

   public PlayerEntity getClosestPlayer(LivingEntity entity) {
      double var2 = Double.MAX_VALUE;
      PlayerEntity var4 = null;

      for(String var6 : this.playerReputations.keySet()) {
         if (this.hasBadReputation(var6)) {
            PlayerEntity var7 = this.world.getPlayer(var6);
            if (var7 != null) {
               double var8 = var7.getSquaredDistanceTo(entity);
               if (!(var8 > var2)) {
                  var4 = var7;
                  var2 = var8;
               }
            }
         }
      }

      return var4;
   }

   private void cleanUpAttackers() {
      Iterator var1 = this.attackers.iterator();

      while(var1.hasNext()) {
         Village.Attacker var2 = (Village.Attacker)var1.next();
         if (!var2.attacker.isAlive() || Math.abs(this.ticks - var2.ticks) > 300) {
            var1.remove();
         }
      }
   }

   private void cleanUpDoors() {
      boolean var1 = false;
      boolean var2 = this.world.random.nextInt(50) == 0;
      Iterator var3 = this.doors.iterator();

      while(var3.hasNext()) {
         VillageDoor var4 = (VillageDoor)var3.next();
         if (var2) {
            var4.resetRestrictedTicks();
         }

         if (!this.isWoodenDoor(var4.getPos()) || Math.abs(this.ticks - var4.getLastVillageTick()) > 1200) {
            this.combinedDoorPositions = this.combinedDoorPositions.add(var4.getPos().scale(-1));
            var1 = true;
            var4.setOutSideVillage(true);
            var3.remove();
         }
      }

      if (var1) {
         this.updateCenterAndRadius();
      }
   }

   private boolean isWoodenDoor(BlockPos pos) {
      return this.world.getBlockState(pos).getBlock() == Blocks.WOODEN_DOOR;
   }

   private void updateCenterAndRadius() {
      int var1 = this.doors.size();
      if (var1 == 0) {
         this.center = new BlockPos(0, 0, 0);
         this.radius = 0;
      } else {
         this.center = new BlockPos(
            this.combinedDoorPositions.getX() / var1, this.combinedDoorPositions.getY() / var1, this.combinedDoorPositions.getZ() / var1
         );
         int var2 = 0;

         for(VillageDoor var4 : this.doors) {
            var2 = Math.max(var4.getSquaredDistanceTo(this.center), var2);
         }

         this.radius = Math.max(32, (int)Math.sqrt((double)var2) + 1);
      }
   }

   public int getReputation(String playerName) {
      Integer var2 = (Integer)this.playerReputations.get(playerName);
      return var2 != null ? var2 : 0;
   }

   public int updateReputation(String playerName, int reputation) {
      int var3 = this.getReputation(playerName);
      int var4 = MathHelper.clamp(var3 + reputation, -30, 10);
      this.playerReputations.put(playerName, var4);
      return var4;
   }

   public boolean hasBadReputation(String playerName) {
      return this.getReputation(playerName) <= -15;
   }

   public void readNbt(NbtCompound nbt) {
      this.populationSize = nbt.getInt("PopSize");
      this.radius = nbt.getInt("Radius");
      this.golems = nbt.getInt("Golems");
      this.ticksOfLastDoorAdded = nbt.getInt("Stable");
      this.ticks = nbt.getInt("Tick");
      this.ticksOfLastMating = nbt.getInt("MTick");
      this.center = new BlockPos(nbt.getInt("CX"), nbt.getInt("CY"), nbt.getInt("CZ"));
      this.combinedDoorPositions = new BlockPos(nbt.getInt("ACX"), nbt.getInt("ACY"), nbt.getInt("ACZ"));
      NbtList var2 = nbt.getList("Doors", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NbtCompound var4 = var2.getCompound(var3);
         VillageDoor var5 = new VillageDoor(
            new BlockPos(var4.getInt("X"), var4.getInt("Y"), var4.getInt("Z")), var4.getInt("IDX"), var4.getInt("IDZ"), var4.getInt("TS")
         );
         this.doors.add(var5);
      }

      NbtList var6 = nbt.getList("Players", 10);

      for(int var7 = 0; var7 < var6.size(); ++var7) {
         NbtCompound var8 = var6.getCompound(var7);
         this.playerReputations.put(var8.getString("Name"), var8.getInt("S"));
      }
   }

   public void writeNbt(NbtCompound nbt) {
      nbt.putInt("PopSize", this.populationSize);
      nbt.putInt("Radius", this.radius);
      nbt.putInt("Golems", this.golems);
      nbt.putInt("Stable", this.ticksOfLastDoorAdded);
      nbt.putInt("Tick", this.ticks);
      nbt.putInt("MTick", this.ticksOfLastMating);
      nbt.putInt("CX", this.center.getX());
      nbt.putInt("CY", this.center.getY());
      nbt.putInt("CZ", this.center.getZ());
      nbt.putInt("ACX", this.combinedDoorPositions.getX());
      nbt.putInt("ACY", this.combinedDoorPositions.getY());
      nbt.putInt("ACZ", this.combinedDoorPositions.getZ());
      NbtList var2 = new NbtList();

      for(VillageDoor var4 : this.doors) {
         NbtCompound var5 = new NbtCompound();
         var5.putInt("X", var4.getPos().getX());
         var5.putInt("Y", var4.getPos().getY());
         var5.putInt("Z", var4.getPos().getZ());
         var5.putInt("IDX", var4.getIndoorsOffsetX());
         var5.putInt("IDZ", var4.getIndoorsOffsetZ());
         var5.putInt("TS", var4.getLastVillageTick());
         var2.add(var5);
      }

      nbt.put("Doors", var2);
      NbtList var7 = new NbtList();

      for(String var9 : this.playerReputations.keySet()) {
         NbtCompound var6 = new NbtCompound();
         var6.putString("Name", var9);
         var6.putInt("S", this.playerReputations.get(var9));
         var7.add(var6);
      }

      nbt.put("Players", var7);
   }

   public void stopMating() {
      this.ticksOfLastMating = this.ticks;
   }

   public boolean canMate() {
      return this.ticksOfLastMating == 0 || this.ticks - this.ticksOfLastMating >= 3600;
   }

   public void updateAllReputations(int reputation) {
      for(String var3 : this.playerReputations.keySet()) {
         this.updateReputation(var3, reputation);
      }
   }

   class Attacker {
      public LivingEntity attacker;
      public int ticks;

      Attacker(LivingEntity attacker, int ticks) {
         this.attacker = attacker;
         this.ticks = ticks;
      }
   }
}
