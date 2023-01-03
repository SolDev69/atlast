package net.minecraft.block.spawner;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class MobSpawner {
   private int delay = 20;
   private String type = "Pig";
   private final List spawnPotentials = Lists.newArrayList();
   private MobSpawner.Entry nextEntry;
   private double rotation;
   private double lastRotation;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   private Entity displayEntity;
   private int maxNearbyEntities = 6;
   private int requiredPlayerRange = 16;
   private int spawnRange = 4;

   private String getType() {
      if (this.getNextEntry() == null) {
         if (this.type.equals("Minecart")) {
            this.type = "MinecartRideable";
         }

         return this.type;
      } else {
         return this.getNextEntry().type;
      }
   }

   public void setType(String type) {
      this.type = type;
   }

   private boolean isActive() {
      BlockPos var1 = this.getPos();
      return this.getWorld()
         .isPlayerWithinRange((double)var1.getX() + 0.5, (double)var1.getY() + 0.5, (double)var1.getZ() + 0.5, (double)this.requiredPlayerRange);
   }

   public void tick() {
      if (this.isActive()) {
         BlockPos var1 = this.getPos();
         if (this.getWorld().isClient) {
            double var2 = (double)((float)var1.getX() + this.getWorld().random.nextFloat());
            double var4 = (double)((float)var1.getY() + this.getWorld().random.nextFloat());
            double var6 = (double)((float)var1.getZ() + this.getWorld().random.nextFloat());
            this.getWorld().addParticle(ParticleType.SMOKE_NORMAL, var2, var4, var6, 0.0, 0.0, 0.0);
            this.getWorld().addParticle(ParticleType.FLAME, var2, var4, var6, 0.0, 0.0, 0.0);
            if (this.delay > 0) {
               --this.delay;
            }

            this.lastRotation = this.rotation;
            this.rotation = (this.rotation + (double)(1000.0F / ((float)this.delay + 200.0F))) % 360.0;
         } else {
            if (this.delay == -1) {
               this.refreshSpawnDelay();
            }

            if (this.delay > 0) {
               --this.delay;
               return;
            }

            boolean var13 = false;

            for(int var3 = 0; var3 < this.spawnCount; ++var3) {
               Entity var14 = Entities.createSilently(this.getType(), this.getWorld());
               if (var14 == null) {
                  return;
               }

               int var5 = this.getWorld()
                  .getEntities(
                     var14.getClass(),
                     new Box(
                           (double)var1.getX(),
                           (double)var1.getY(),
                           (double)var1.getZ(),
                           (double)(var1.getX() + 1),
                           (double)(var1.getY() + 1),
                           (double)(var1.getZ() + 1)
                        )
                        .expand((double)this.spawnRange, 4.0, (double)this.spawnRange)
                  )
                  .size();
               if (var5 >= this.maxNearbyEntities) {
                  this.refreshSpawnDelay();
                  return;
               }

               double var15 = (double)var1.getX() + (this.getWorld().random.nextDouble() - this.getWorld().random.nextDouble()) * (double)this.spawnRange + 0.5;
               double var8 = (double)(var1.getY() + this.getWorld().random.nextInt(3) - 1);
               double var10 = (double)var1.getZ() + (this.getWorld().random.nextDouble() - this.getWorld().random.nextDouble()) * (double)this.spawnRange + 0.5;
               MobEntity var12 = var14 instanceof MobEntity ? (MobEntity)var14 : null;
               var14.refreshPositionAndAngles(var15, var8, var10, this.getWorld().random.nextFloat() * 360.0F, 0.0F);
               if (var12 == null || var12.canSpawn() && var12.m_52qkzdxky()) {
                  this.prepareEntityForSpawning(var14, true);
                  this.getWorld().doEvent(2004, var1, 0);
                  if (var12 != null) {
                     var12.doSpawnEffects();
                  }

                  var13 = true;
               }
            }

            if (var13) {
               this.refreshSpawnDelay();
            }
         }
      }
   }

   private Entity prepareEntityForSpawning(Entity entity, boolean doSpawn) {
      if (this.getNextEntry() != null) {
         NbtCompound var3 = new NbtCompound();
         entity.writeNbtNoRider(var3);

         for(String var5 : this.getNextEntry().properties.getKeys()) {
            NbtElement var6 = this.getNextEntry().properties.get(var5);
            var3.put(var5, var6.copy());
         }

         entity.readEntityNbt(var3);
         if (entity.world != null && doSpawn) {
            entity.world.addEntity(entity);
         }

         NbtCompound var12;
         for(Entity var11 = entity; var3.isType("Riding", 10); var3 = var12) {
            var12 = var3.getCompound("Riding");
            Entity var13 = Entities.createSilently(var12.getString("id"), entity.world);
            if (var13 != null) {
               NbtCompound var7 = new NbtCompound();
               var13.writeNbtNoRider(var7);

               for(String var9 : var12.getKeys()) {
                  NbtElement var10 = var12.get(var9);
                  var7.put(var9, var10.copy());
               }

               var13.readEntityNbt(var7);
               var13.refreshPositionAndAngles(var11.x, var11.y, var11.z, var11.yaw, var11.pitch);
               if (entity.world != null && doSpawn) {
                  entity.world.addEntity(var13);
               }

               var11.startRiding(var13);
            }

            var11 = var13;
         }
      } else if (entity instanceof LivingEntity && entity.world != null && doSpawn) {
         ((MobEntity)entity).initialize(entity.world.getLocalDifficulty(new BlockPos(entity)), null);
         entity.world.addEntity(entity);
      }

      return entity;
   }

   private void refreshSpawnDelay() {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.delay = this.minSpawnDelay;
      } else {
         this.delay = this.minSpawnDelay + this.getWorld().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
      }

      if (this.spawnPotentials.size() > 0) {
         this.setNextEntry((MobSpawner.Entry)WeightedPicker.pick(this.getWorld().random, this.spawnPotentials));
      }

      this.broadcastEntityEvent(1);
   }

   public void readNbt(NbtCompound nbt) {
      this.type = nbt.getString("EntityId");
      this.delay = nbt.getShort("Delay");
      this.spawnPotentials.clear();
      if (nbt.isType("SpawnPotentials", 9)) {
         NbtList var2 = nbt.getList("SpawnPotentials", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.spawnPotentials.add(new MobSpawner.Entry(var2.getCompound(var3)));
         }
      }

      if (nbt.isType("SpawnData", 10)) {
         this.setNextEntry(new MobSpawner.Entry(nbt.getCompound("SpawnData"), this.type));
      } else {
         this.setNextEntry(null);
      }

      if (nbt.isType("MinSpawnDelay", 99)) {
         this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
         this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
         this.spawnCount = nbt.getShort("SpawnCount");
      }

      if (nbt.isType("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
         this.requiredPlayerRange = nbt.getShort("RequiredPlayerRange");
      }

      if (nbt.isType("SpawnRange", 99)) {
         this.spawnRange = nbt.getShort("SpawnRange");
      }

      if (this.getWorld() != null) {
         this.displayEntity = null;
      }
   }

   public void writeNbt(NbtCompound nbt) {
      nbt.putString("EntityId", this.getType());
      nbt.putShort("Delay", (short)this.delay);
      nbt.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
      nbt.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
      nbt.putShort("SpawnCount", (short)this.spawnCount);
      nbt.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
      nbt.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
      nbt.putShort("SpawnRange", (short)this.spawnRange);
      if (this.getNextEntry() != null) {
         nbt.put("SpawnData", this.getNextEntry().properties.copy());
      }

      if (this.getNextEntry() != null || this.spawnPotentials.size() > 0) {
         NbtList var2 = new NbtList();
         if (this.spawnPotentials.size() > 0) {
            for(MobSpawner.Entry var4 : this.spawnPotentials) {
               var2.add(var4.toNbt());
            }
         } else {
            var2.add(this.getNextEntry().toNbt());
         }

         nbt.put("SpawnPotentials", var2);
      }
   }

   @Environment(EnvType.CLIENT)
   public Entity getDisplayEntity(World world) {
      if (this.displayEntity == null) {
         Entity var2 = Entities.createSilently(this.getType(), world);
         if (var2 != null) {
            var2 = this.prepareEntityForSpawning(var2, false);
            this.displayEntity = var2;
         }
      }

      return this.displayEntity;
   }

   public boolean doEntityEvent(int event) {
      if (event == 1 && this.getWorld().isClient) {
         this.delay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   private MobSpawner.Entry getNextEntry() {
      return this.nextEntry;
   }

   public void setNextEntry(MobSpawner.Entry entry) {
      this.nextEntry = entry;
   }

   public abstract void broadcastEntityEvent(int event);

   public abstract World getWorld();

   public abstract BlockPos getPos();

   @Environment(EnvType.CLIENT)
   public double getRotation() {
      return this.rotation;
   }

   @Environment(EnvType.CLIENT)
   public double getLastRotation() {
      return this.lastRotation;
   }

   public class Entry extends WeightedPicker.Entry {
      private final NbtCompound properties;
      private final String type;

      public Entry(NbtCompound properties) {
         this(properties.getCompound("Properties"), properties.getString("Type"), properties.getInt("Weight"));
      }

      public Entry(NbtCompound nbt, String type) {
         this(nbt, type, 1);
      }

      private Entry(NbtCompound properties, String type, int weight) {
         super(weight);
         if (type.equals("Minecart")) {
            if (properties != null) {
               type = MinecartEntity.Type.byIndex(properties.getInt("Type")).getName();
            } else {
               type = "MinecartRideable";
            }
         }

         this.properties = properties;
         this.type = type;
      }

      public NbtCompound toNbt() {
         NbtCompound var1 = new NbtCompound();
         var1.put("Properties", this.properties);
         var1.putString("Type", this.type);
         var1.putInt("Weight", this.weight);
         return var1;
      }
   }
}
