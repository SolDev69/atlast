package net.minecraft.server.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.DataTracker;
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
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributeContainer;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.AddEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.AddMobS2CPacket;
import net.minecraft.network.packet.s2c.play.AddPaintingS2CPacket;
import net.minecraft.network.packet.s2c.play.AddPlayerS2CPacket;
import net.minecraft.network.packet.s2c.play.AddXpOrbS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDataS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityHeadAnglesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTeleportS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSleepS2CPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.map.SavedMapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
   private static final Logger LOGGER = LogManager.getLogger();
   public Entity currentTrackedEntity;
   public int trackedDistance;
   public int tracingFrequency;
   public int lastX;
   public int lastY;
   public int lastZ;
   public int lastYaw;
   public int lastPitch;
   public int lastHeadPitch;
   public double velocityX;
   public double velocityY;
   public double velocityZ;
   public int ticks;
   private double x;
   private double y;
   private double z;
   private boolean isInitialized;
   private boolean alwaysUpdateVelocity;
   private int ticksSinceLastDismount;
   private Entity rider;
   private boolean riding;
   private boolean onGround;
   public boolean newPlayerDataUpdated;
   public Set listeners = Sets.newHashSet();

   public EntityTrackerEntry(Entity entity, int trackedDistance, int tracingFrequency, boolean alwaysUpdateVelocity) {
      this.currentTrackedEntity = entity;
      this.trackedDistance = trackedDistance;
      this.tracingFrequency = tracingFrequency;
      this.alwaysUpdateVelocity = alwaysUpdateVelocity;
      this.lastX = MathHelper.floor(entity.x * 32.0);
      this.lastY = MathHelper.floor(entity.y * 32.0);
      this.lastZ = MathHelper.floor(entity.z * 32.0);
      this.lastYaw = MathHelper.floor(entity.yaw * 256.0F / 360.0F);
      this.lastPitch = MathHelper.floor(entity.pitch * 256.0F / 360.0F);
      this.lastHeadPitch = MathHelper.floor(entity.getHeadYaw() * 256.0F / 360.0F);
      this.onGround = entity.onGround;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof EntityTrackerEntry) {
         return ((EntityTrackerEntry)obj).currentTrackedEntity.getNetworkId() == this.currentTrackedEntity.getNetworkId();
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.currentTrackedEntity.getNetworkId();
   }

   public void notifyNewLocation(List players) {
      this.newPlayerDataUpdated = false;
      if (!this.isInitialized || this.currentTrackedEntity.getSquaredDistanceTo(this.x, this.y, this.z) > 16.0) {
         this.x = this.currentTrackedEntity.x;
         this.y = this.currentTrackedEntity.y;
         this.z = this.currentTrackedEntity.z;
         this.isInitialized = true;
         this.newPlayerDataUpdated = true;
         this.updateListeners(players);
      }

      if (this.rider != this.currentTrackedEntity.vehicle || this.currentTrackedEntity.vehicle != null && this.ticks % 60 == 0) {
         this.rider = this.currentTrackedEntity.vehicle;
         this.sendToListeners(new EntityAttachS2CPacket(0, this.currentTrackedEntity, this.currentTrackedEntity.vehicle));
      }

      if (this.currentTrackedEntity instanceof ItemFrameEntity && this.ticks % 10 == 0) {
         ItemFrameEntity var2 = (ItemFrameEntity)this.currentTrackedEntity;
         ItemStack var3 = var2.getItemStackInItemFrame();
         if (var3 != null && var3.getItem() instanceof FilledMapItem) {
            SavedMapData var4 = Items.FILLED_MAP.getSavedMapData(var3, this.currentTrackedEntity.world);

            for(PlayerEntity var6 : players) {
               ServerPlayerEntity var7 = (ServerPlayerEntity)var6;
               var4.tickHolder(var7, var3);
               Packet var8 = Items.FILLED_MAP.getUpdatePacket(var3, this.currentTrackedEntity.world, var7);
               if (var8 != null) {
                  var7.networkHandler.sendPacket(var8);
               }
            }
         }

         this.updateListeners();
      }

      if (this.ticks % this.tracingFrequency == 0 || this.currentTrackedEntity.velocityDirty || this.currentTrackedEntity.getDataTracker().isDirty()) {
         if (this.currentTrackedEntity.vehicle == null) {
            ++this.ticksSinceLastDismount;
            int var24 = MathHelper.floor(this.currentTrackedEntity.x * 32.0);
            int var27 = MathHelper.floor(this.currentTrackedEntity.y * 32.0);
            int var29 = MathHelper.floor(this.currentTrackedEntity.z * 32.0);
            int var30 = MathHelper.floor(this.currentTrackedEntity.yaw * 256.0F / 360.0F);
            int var31 = MathHelper.floor(this.currentTrackedEntity.pitch * 256.0F / 360.0F);
            int var32 = var24 - this.lastX;
            int var33 = var27 - this.lastY;
            int var9 = var29 - this.lastZ;
            Object var10 = null;
            boolean var11 = Math.abs(var32) >= 4 || Math.abs(var33) >= 4 || Math.abs(var9) >= 4 || this.ticks % 60 == 0;
            boolean var12 = Math.abs(var30 - this.lastYaw) >= 4 || Math.abs(var31 - this.lastPitch) >= 4;
            if (this.ticks > 0 || this.currentTrackedEntity instanceof ArrowEntity) {
               if (var32 < -128
                  || var32 >= 128
                  || var33 < -128
                  || var33 >= 128
                  || var9 < -128
                  || var9 >= 128
                  || this.ticksSinceLastDismount > 400
                  || this.riding
                  || this.onGround != this.currentTrackedEntity.onGround) {
                  this.onGround = this.currentTrackedEntity.onGround;
                  this.ticksSinceLastDismount = 0;
                  var10 = new EntityTeleportS2CPacket(
                     this.currentTrackedEntity.getNetworkId(), var24, var27, var29, (byte)var30, (byte)var31, this.currentTrackedEntity.onGround
                  );
               } else if (var11 && var12) {
                  var10 = new EntityMoveS2CPacket.PositionAndAngles(
                     this.currentTrackedEntity.getNetworkId(),
                     (byte)var32,
                     (byte)var33,
                     (byte)var9,
                     (byte)var30,
                     (byte)var31,
                     this.currentTrackedEntity.onGround
                  );
               } else if (var11) {
                  var10 = new EntityMoveS2CPacket.Position(
                     this.currentTrackedEntity.getNetworkId(), (byte)var32, (byte)var33, (byte)var9, this.currentTrackedEntity.onGround
                  );
               } else if (var12) {
                  var10 = new EntityMoveS2CPacket.Angles(this.currentTrackedEntity.getNetworkId(), (byte)var30, (byte)var31, this.currentTrackedEntity.onGround);
               }
            }

            if (this.alwaysUpdateVelocity) {
               double var13 = this.currentTrackedEntity.velocityX - this.velocityX;
               double var15 = this.currentTrackedEntity.velocityY - this.velocityY;
               double var17 = this.currentTrackedEntity.velocityZ - this.velocityZ;
               double var19 = 0.02;
               double var21 = var13 * var13 + var15 * var15 + var17 * var17;
               if (var21 > var19 * var19
                  || var21 > 0.0
                     && this.currentTrackedEntity.velocityX == 0.0
                     && this.currentTrackedEntity.velocityY == 0.0
                     && this.currentTrackedEntity.velocityZ == 0.0) {
                  this.velocityX = this.currentTrackedEntity.velocityX;
                  this.velocityY = this.currentTrackedEntity.velocityY;
                  this.velocityZ = this.currentTrackedEntity.velocityZ;
                  this.sendToListeners(new EntityVelocityS2CPacket(this.currentTrackedEntity.getNetworkId(), this.velocityX, this.velocityY, this.velocityZ));
               }
            }

            if (var10 != null) {
               this.sendToListeners((Packet)var10);
            }

            this.updateListeners();
            if (var11) {
               this.lastX = var24;
               this.lastY = var27;
               this.lastZ = var29;
            }

            if (var12) {
               this.lastYaw = var30;
               this.lastPitch = var31;
            }

            this.riding = false;
         } else {
            int var23 = MathHelper.floor(this.currentTrackedEntity.yaw * 256.0F / 360.0F);
            int var26 = MathHelper.floor(this.currentTrackedEntity.pitch * 256.0F / 360.0F);
            boolean var28 = Math.abs(var23 - this.lastYaw) >= 4 || Math.abs(var26 - this.lastPitch) >= 4;
            if (var28) {
               this.sendToListeners(
                  new EntityMoveS2CPacket.Angles(this.currentTrackedEntity.getNetworkId(), (byte)var23, (byte)var26, this.currentTrackedEntity.onGround)
               );
               this.lastYaw = var23;
               this.lastPitch = var26;
            }

            this.lastX = MathHelper.floor(this.currentTrackedEntity.x * 32.0);
            this.lastY = MathHelper.floor(this.currentTrackedEntity.y * 32.0);
            this.lastZ = MathHelper.floor(this.currentTrackedEntity.z * 32.0);
            this.updateListeners();
            this.riding = true;
         }

         int var25 = MathHelper.floor(this.currentTrackedEntity.getHeadYaw() * 256.0F / 360.0F);
         if (Math.abs(var25 - this.lastHeadPitch) >= 4) {
            this.sendToListeners(new EntityHeadAnglesS2CPacket(this.currentTrackedEntity, (byte)var25));
            this.lastHeadPitch = var25;
         }

         this.currentTrackedEntity.velocityDirty = false;
      }

      ++this.ticks;
      if (this.currentTrackedEntity.damaged) {
         this.sendToListenersAndTrackedEntityIfPlayer(new EntityVelocityS2CPacket(this.currentTrackedEntity));
         this.currentTrackedEntity.damaged = false;
      }
   }

   private void updateListeners() {
      DataTracker var1 = this.currentTrackedEntity.getDataTracker();
      if (var1.isDirty()) {
         this.sendToListenersAndTrackedEntityIfPlayer(new EntityDataS2CPacket(this.currentTrackedEntity.getNetworkId(), var1, false));
      }

      if (this.currentTrackedEntity instanceof LivingEntity) {
         EntityAttributeContainer var2 = (EntityAttributeContainer)((LivingEntity)this.currentTrackedEntity).getAttributes();
         Set var3 = var2.getTracked();
         if (!var3.isEmpty()) {
            this.sendToListenersAndTrackedEntityIfPlayer(new EntityAttributesS2CPacket(this.currentTrackedEntity.getNetworkId(), var3));
         }

         var3.clear();
      }
   }

   public void sendToListeners(Packet packet) {
      for(ServerPlayerEntity var3 : this.listeners) {
         var3.networkHandler.sendPacket(packet);
      }
   }

   public void sendToListenersAndTrackedEntityIfPlayer(Packet packet) {
      this.sendToListeners(packet);
      if (this.currentTrackedEntity instanceof ServerPlayerEntity) {
         ((ServerPlayerEntity)this.currentTrackedEntity).networkHandler.sendPacket(packet);
      }
   }

   public void notifyEntityRemoved() {
      for(ServerPlayerEntity var2 : this.listeners) {
         var2.m_26bdxtpev(this.currentTrackedEntity);
      }
   }

   public void notifyEntityRemoved(ServerPlayerEntity player) {
      if (this.listeners.contains(player)) {
         player.m_26bdxtpev(this.currentTrackedEntity);
         this.listeners.remove(player);
      }
   }

   public void updateListener(ServerPlayerEntity player) {
      if (player != this.currentTrackedEntity) {
         if (this.m_12ivhpwsx(player)) {
            if (!this.listeners.contains(player) && (this.isInViewOfPlayer(player) || this.currentTrackedEntity.teleporting)) {
               this.listeners.add(player);
               Packet var2 = this.createAddEntityPacket();
               player.networkHandler.sendPacket(var2);
               if (!this.currentTrackedEntity.getDataTracker().isEmpty()) {
                  player.networkHandler
                     .sendPacket(new EntityDataS2CPacket(this.currentTrackedEntity.getNetworkId(), this.currentTrackedEntity.getDataTracker(), true));
               }

               if (this.currentTrackedEntity instanceof LivingEntity) {
                  EntityAttributeContainer var3 = (EntityAttributeContainer)((LivingEntity)this.currentTrackedEntity).getAttributes();
                  Collection var4 = var3.getTrackable();
                  if (!var4.isEmpty()) {
                     player.networkHandler.sendPacket(new EntityAttributesS2CPacket(this.currentTrackedEntity.getNetworkId(), var4));
                  }
               }

               this.velocityX = this.currentTrackedEntity.velocityX;
               this.velocityY = this.currentTrackedEntity.velocityY;
               this.velocityZ = this.currentTrackedEntity.velocityZ;
               if (this.alwaysUpdateVelocity && !(var2 instanceof AddMobS2CPacket)) {
                  player.networkHandler
                     .sendPacket(
                        new EntityVelocityS2CPacket(
                           this.currentTrackedEntity.getNetworkId(),
                           this.currentTrackedEntity.velocityX,
                           this.currentTrackedEntity.velocityY,
                           this.currentTrackedEntity.velocityZ
                        )
                     );
               }

               if (this.currentTrackedEntity.vehicle != null) {
                  player.networkHandler.sendPacket(new EntityAttachS2CPacket(0, this.currentTrackedEntity, this.currentTrackedEntity.vehicle));
               }

               if (this.currentTrackedEntity instanceof MobEntity && ((MobEntity)this.currentTrackedEntity).getHoldingEntity() != null) {
                  player.networkHandler
                     .sendPacket(new EntityAttachS2CPacket(1, this.currentTrackedEntity, ((MobEntity)this.currentTrackedEntity).getHoldingEntity()));
               }

               if (this.currentTrackedEntity instanceof LivingEntity) {
                  for(int var6 = 0; var6 < 5; ++var6) {
                     ItemStack var9 = ((LivingEntity)this.currentTrackedEntity).getStackInInventory(var6);
                     if (var9 != null) {
                        player.networkHandler.sendPacket(new EntityEquipmentS2CPacket(this.currentTrackedEntity.getNetworkId(), var6, var9));
                     }
                  }
               }

               if (this.currentTrackedEntity instanceof PlayerEntity) {
                  PlayerEntity var7 = (PlayerEntity)this.currentTrackedEntity;
                  if (var7.isSleeping()) {
                     player.networkHandler.sendPacket(new PlayerSleepS2CPacket(var7, new BlockPos(this.currentTrackedEntity)));
                  }
               }

               if (this.currentTrackedEntity instanceof LivingEntity) {
                  LivingEntity var8 = (LivingEntity)this.currentTrackedEntity;

                  for(StatusEffectInstance var5 : var8.getStatusEffects()) {
                     player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.currentTrackedEntity.getNetworkId(), var5));
                  }
               }
            }
         } else if (this.listeners.contains(player)) {
            this.listeners.remove(player);
            player.m_26bdxtpev(this.currentTrackedEntity);
         }
      }
   }

   public boolean m_12ivhpwsx(ServerPlayerEntity c_53mtutqhz) {
      double var2 = c_53mtutqhz.x - (double)(this.lastX / 32);
      double var4 = c_53mtutqhz.z - (double)(this.lastZ / 32);
      return var2 >= (double)(-this.trackedDistance)
         && var2 <= (double)this.trackedDistance
         && var4 >= (double)(-this.trackedDistance)
         && var4 <= (double)this.trackedDistance
         && this.currentTrackedEntity.m_89sxhouae(c_53mtutqhz);
   }

   private boolean isInViewOfPlayer(ServerPlayerEntity player) {
      return player.getServerWorld().getChunkMap().isChunkWithinView(player, this.currentTrackedEntity.chunkX, this.currentTrackedEntity.chunkZ);
   }

   public void updateListeners(List players) {
      for(int var2 = 0; var2 < players.size(); ++var2) {
         this.updateListener((ServerPlayerEntity)players.get(var2));
      }
   }

   private Packet createAddEntityPacket() {
      if (this.currentTrackedEntity.removed) {
         LOGGER.warn("Fetching addPacket for removed entity");
      }

      if (this.currentTrackedEntity instanceof ItemEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 2, 1);
      } else if (this.currentTrackedEntity instanceof ServerPlayerEntity) {
         return new AddPlayerS2CPacket((PlayerEntity)this.currentTrackedEntity);
      } else if (this.currentTrackedEntity instanceof MinecartEntity) {
         MinecartEntity var9 = (MinecartEntity)this.currentTrackedEntity;
         return new AddEntityS2CPacket(this.currentTrackedEntity, 10, var9.getMinecartType().getIndex());
      } else if (this.currentTrackedEntity instanceof BoatEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 1);
      } else if (this.currentTrackedEntity instanceof EntityCategoryProvider) {
         this.lastHeadPitch = MathHelper.floor(this.currentTrackedEntity.getHeadYaw() * 256.0F / 360.0F);
         return new AddMobS2CPacket((LivingEntity)this.currentTrackedEntity);
      } else if (this.currentTrackedEntity instanceof FishingBobberEntity) {
         PlayerEntity var8 = ((FishingBobberEntity)this.currentTrackedEntity).player;
         return new AddEntityS2CPacket(this.currentTrackedEntity, 90, var8 != null ? var8.getNetworkId() : this.currentTrackedEntity.getNetworkId());
      } else if (this.currentTrackedEntity instanceof ArrowEntity) {
         Entity var7 = ((ArrowEntity)this.currentTrackedEntity).shooter;
         return new AddEntityS2CPacket(this.currentTrackedEntity, 60, var7 != null ? var7.getNetworkId() : this.currentTrackedEntity.getNetworkId());
      } else if (this.currentTrackedEntity instanceof SnowballEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 61);
      } else if (this.currentTrackedEntity instanceof PotionEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 73, ((PotionEntity)this.currentTrackedEntity).getMetadata());
      } else if (this.currentTrackedEntity instanceof ExperienceBottleEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 75);
      } else if (this.currentTrackedEntity instanceof EnderPearlEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 65);
      } else if (this.currentTrackedEntity instanceof EnderEyeEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 72);
      } else if (this.currentTrackedEntity instanceof FireworksEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 76);
      } else if (this.currentTrackedEntity instanceof ProjectileEntity) {
         ProjectileEntity var6 = (ProjectileEntity)this.currentTrackedEntity;
         AddEntityS2CPacket var11 = null;
         byte var14 = 63;
         if (this.currentTrackedEntity instanceof SmallFireballEntity) {
            var14 = 64;
         } else if (this.currentTrackedEntity instanceof WitherSkullEntity) {
            var14 = 66;
         }

         if (var6.shooter != null) {
            var11 = new AddEntityS2CPacket(this.currentTrackedEntity, var14, ((ProjectileEntity)this.currentTrackedEntity).shooter.getNetworkId());
         } else {
            var11 = new AddEntityS2CPacket(this.currentTrackedEntity, var14, 0);
         }

         var11.setVelocityX((int)(var6.accelerationX * 8000.0));
         var11.setVelocityY((int)(var6.accelerationY * 8000.0));
         var11.setVelocityZ((int)(var6.accelerationZ * 8000.0));
         return var11;
      } else if (this.currentTrackedEntity instanceof EggEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 62);
      } else if (this.currentTrackedEntity instanceof PrimedTntEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 50);
      } else if (this.currentTrackedEntity instanceof EnderCrystalEntity) {
         return new AddEntityS2CPacket(this.currentTrackedEntity, 51);
      } else if (this.currentTrackedEntity instanceof FallingBlockEntity) {
         FallingBlockEntity var5 = (FallingBlockEntity)this.currentTrackedEntity;
         return new AddEntityS2CPacket(this.currentTrackedEntity, 70, Block.serialize(var5.getBlock()));
      } else if (this.currentTrackedEntity instanceof PaintingEntity) {
         return new AddPaintingS2CPacket((PaintingEntity)this.currentTrackedEntity);
      } else if (this.currentTrackedEntity instanceof ItemFrameEntity) {
         ItemFrameEntity var4 = (ItemFrameEntity)this.currentTrackedEntity;
         AddEntityS2CPacket var10 = new AddEntityS2CPacket(this.currentTrackedEntity, 71, var4.getFacing.getIdHorizontal());
         BlockPos var13 = var4.getBlockPos();
         var10.setX(MathHelper.floor((float)(var13.getX() * 32)));
         var10.setY(MathHelper.floor((float)(var13.getY() * 32)));
         var10.setZ(MathHelper.floor((float)(var13.getZ() * 32)));
         return var10;
      } else if (this.currentTrackedEntity instanceof LeadKnotEntity) {
         LeadKnotEntity var1 = (LeadKnotEntity)this.currentTrackedEntity;
         AddEntityS2CPacket var2 = new AddEntityS2CPacket(this.currentTrackedEntity, 77);
         BlockPos var3 = var1.getBlockPos();
         var2.setX(MathHelper.floor((float)(var3.getX() * 32)));
         var2.setY(MathHelper.floor((float)(var3.getY() * 32)));
         var2.setZ(MathHelper.floor((float)(var3.getZ() * 32)));
         return var2;
      } else if (this.currentTrackedEntity instanceof XpOrbEntity) {
         return new AddXpOrbS2CPacket((XpOrbEntity)this.currentTrackedEntity);
      } else {
         throw new IllegalArgumentException("Don't know how to add " + this.currentTrackedEntity.getClass() + "!");
      }
   }

   public void removeListener(ServerPlayerEntity player) {
      if (this.listeners.contains(player)) {
         this.listeners.remove(player);
         player.m_26bdxtpev(this.currentTrackedEntity);
      }
   }
}
