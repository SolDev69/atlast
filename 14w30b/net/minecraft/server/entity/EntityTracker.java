package net.minecraft.server.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.entity.EnderCrystalEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworksEntity;
import net.minecraft.entity.FishingBobberEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.decoration.DecorationEntity;
import net.minecraft.entity.living.mob.ambient.BatEntity;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragonEntity;
import net.minecraft.entity.living.mob.hostile.boss.WitherEntity;
import net.minecraft.entity.living.mob.water.SquidEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Int2ObjectHashMap;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerWorld world;
   private Set entries = Sets.newHashSet();
   private Int2ObjectHashMap trackedEntityIds = new Int2ObjectHashMap();
   private int viewDistance;

   public EntityTracker(ServerWorld world) {
      this.world = world;
      this.viewDistance = world.getServer().getPlayerManager().getViewDistance();
   }

   public void onEntityAdded(Entity entity) {
      if (entity instanceof ServerPlayerEntity) {
         this.startTracking(entity, 512, 2);
         ServerPlayerEntity var2 = (ServerPlayerEntity)entity;

         for(EntityTrackerEntry var4 : this.entries) {
            if (var4.currentTrackedEntity != var2) {
               var4.updateListener(var2);
            }
         }
      } else if (entity instanceof FishingBobberEntity) {
         this.startTracking(entity, 64, 5, true);
      } else if (entity instanceof ArrowEntity) {
         this.startTracking(entity, 64, 20, false);
      } else if (entity instanceof SmallFireballEntity) {
         this.startTracking(entity, 64, 10, false);
      } else if (entity instanceof ProjectileEntity) {
         this.startTracking(entity, 64, 10, false);
      } else if (entity instanceof SnowballEntity) {
         this.startTracking(entity, 64, 10, true);
      } else if (entity instanceof EnderPearlEntity) {
         this.startTracking(entity, 64, 10, true);
      } else if (entity instanceof EnderEyeEntity) {
         this.startTracking(entity, 64, 4, true);
      } else if (entity instanceof EggEntity) {
         this.startTracking(entity, 64, 10, true);
      } else if (entity instanceof PotionEntity) {
         this.startTracking(entity, 64, 10, true);
      } else if (entity instanceof ExperienceBottleEntity) {
         this.startTracking(entity, 64, 10, true);
      } else if (entity instanceof FireworksEntity) {
         this.startTracking(entity, 64, 10, true);
      } else if (entity instanceof ItemEntity) {
         this.startTracking(entity, 64, 20, true);
      } else if (entity instanceof MinecartEntity) {
         this.startTracking(entity, 80, 3, true);
      } else if (entity instanceof BoatEntity) {
         this.startTracking(entity, 80, 3, true);
      } else if (entity instanceof SquidEntity) {
         this.startTracking(entity, 64, 3, true);
      } else if (entity instanceof WitherEntity) {
         this.startTracking(entity, 80, 3, false);
      } else if (entity instanceof BatEntity) {
         this.startTracking(entity, 80, 3, false);
      } else if (entity instanceof EnderDragonEntity) {
         this.startTracking(entity, 160, 3, true);
      } else if (entity instanceof EntityCategoryProvider) {
         this.startTracking(entity, 80, 3, true);
      } else if (entity instanceof PrimedTntEntity) {
         this.startTracking(entity, 160, 10, true);
      } else if (entity instanceof FallingBlockEntity) {
         this.startTracking(entity, 160, 20, true);
      } else if (entity instanceof DecorationEntity) {
         this.startTracking(entity, 160, Integer.MAX_VALUE, false);
      } else if (entity instanceof XpOrbEntity) {
         this.startTracking(entity, 160, 20, true);
      } else if (entity instanceof EnderCrystalEntity) {
         this.startTracking(entity, 256, Integer.MAX_VALUE, false);
      }
   }

   public void startTracking(Entity entity, int trackedDistance, int tracingFrequency) {
      this.startTracking(entity, trackedDistance, tracingFrequency, false);
   }

   public void startTracking(Entity entity, int trackedDistance, int tracingFrequency, boolean alwaysUpdateVelocity) {
      if (trackedDistance > this.viewDistance) {
         trackedDistance = this.viewDistance;
      }

      try {
         if (this.trackedEntityIds.contains(entity.getNetworkId())) {
            throw new IllegalStateException("Entity is already tracked!");
         }

         EntityTrackerEntry var5 = new EntityTrackerEntry(entity, trackedDistance, tracingFrequency, alwaysUpdateVelocity);
         this.entries.add(var5);
         this.trackedEntityIds.put(entity.getNetworkId(), var5);
         var5.updateListeners(this.world.players);
      } catch (Throwable var11) {
         CrashReport var6 = CrashReport.of(var11, "Adding entity to track");
         CashReportCategory var7 = var6.addCategory("Entity To Track");
         var7.add("Tracking range", trackedDistance + " blocks");
         var7.add("Update interval", new Callable() {
            public String call() {
               String var1 = "Once per " + tracingFrequency + " ticks";
               if (tracingFrequency == Integer.MAX_VALUE) {
                  var1 = "Maximum (" + var1 + ")";
               }

               return var1;
            }
         });
         entity.populateCrashReport(var7);
         CashReportCategory var8 = var6.addCategory("Entity That Is Already Tracked");
         ((EntityTrackerEntry)this.trackedEntityIds.get(entity.getNetworkId())).currentTrackedEntity.populateCrashReport(var8);

         try {
            throw new CrashException(var6);
         } catch (CrashException var10) {
            LOGGER.error("\"Silently\" catching entity tracking error.", var10);
         }
      }
   }

   public void onEntityRemoved(Entity entity) {
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity var2 = (ServerPlayerEntity)entity;

         for(EntityTrackerEntry var4 : this.entries) {
            var4.notifyEntityRemoved(var2);
         }
      }

      EntityTrackerEntry var5 = (EntityTrackerEntry)this.trackedEntityIds.remove(entity.getNetworkId());
      if (var5 != null) {
         this.entries.remove(var5);
         var5.notifyEntityRemoved();
      }
   }

   public void tick() {
      ArrayList var1 = Lists.newArrayList();

      for(EntityTrackerEntry var3 : this.entries) {
         var3.notifyNewLocation(this.world.players);
         if (var3.newPlayerDataUpdated && var3.currentTrackedEntity instanceof ServerPlayerEntity) {
            var1.add((ServerPlayerEntity)var3.currentTrackedEntity);
         }
      }

      for(int var6 = 0; var6 < var1.size(); ++var6) {
         ServerPlayerEntity var7 = (ServerPlayerEntity)var1.get(var6);

         for(EntityTrackerEntry var5 : this.entries) {
            if (var5.currentTrackedEntity != var7) {
               var5.updateListener(var7);
            }
         }
      }
   }

   public void updateVisibility(ServerPlayerEntity player) {
      for(EntityTrackerEntry var3 : this.entries) {
         if (var3.currentTrackedEntity == player) {
            var3.updateListeners(this.world.players);
         } else {
            var3.updateListener(player);
         }
      }
   }

   public void sendToListeners(Entity entity, Packet packet) {
      EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntityIds.get(entity.getNetworkId());
      if (var3 != null) {
         var3.sendToListeners(packet);
      }
   }

   public void sendToListenersAndTrackedEntityIfPlayer(Entity entity, Packet packet) {
      EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntityIds.get(entity.getNetworkId());
      if (var3 != null) {
         var3.sendToListenersAndTrackedEntityIfPlayer(packet);
      }
   }

   public void removeListener(ServerPlayerEntity player) {
      for(EntityTrackerEntry var3 : this.entries) {
         var3.removeListener(player);
      }
   }

   public void updateListener(ServerPlayerEntity player, WorldChunk chunk) {
      for(EntityTrackerEntry var4 : this.entries) {
         if (var4.currentTrackedEntity != player && var4.currentTrackedEntity.chunkX == chunk.chunkX && var4.currentTrackedEntity.chunkZ == chunk.chunkZ) {
            var4.updateListener(player);
         }
      }
   }
}
