package net.minecraft.entity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FishItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.HitResult;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FishingBobberEntity extends Entity {
   private static final List JUNK_LOOT = Arrays.asList(
      new FishingLootEntry(new ItemStack(Items.LEATHER_BOOTS), 10).setDamage(0.9F),
      new FishingLootEntry(new ItemStack(Items.LEATHER), 10),
      new FishingLootEntry(new ItemStack(Items.BONE), 10),
      new FishingLootEntry(new ItemStack(Items.POTION), 10),
      new FishingLootEntry(new ItemStack(Items.STRING), 5),
      new FishingLootEntry(new ItemStack(Items.FISHING_ROD), 2).setDamage(0.9F),
      new FishingLootEntry(new ItemStack(Items.BOWL), 10),
      new FishingLootEntry(new ItemStack(Items.STICK), 5),
      new FishingLootEntry(new ItemStack(Items.DYE, 10, DyeColor.BLACK.getMetadata()), 1),
      new FishingLootEntry(new ItemStack(Blocks.TRIPWIRE_HOOK), 10),
      new FishingLootEntry(new ItemStack(Items.ROTTEN_FLESH), 10)
   );
   private static final List TREASURE_LOOT = Arrays.asList(
      new FishingLootEntry(new ItemStack(Blocks.LILY_PAD), 1),
      new FishingLootEntry(new ItemStack(Items.NAME_TAG), 1),
      new FishingLootEntry(new ItemStack(Items.SADDLE), 1),
      new FishingLootEntry(new ItemStack(Items.BOW), 1).setDamage(0.25F).setEnchantable(),
      new FishingLootEntry(new ItemStack(Items.FISHING_ROD), 1).setDamage(0.25F).setEnchantable(),
      new FishingLootEntry(new ItemStack(Items.BOOK), 1).setEnchantable()
   );
   private static final List FISH_LOOT = Arrays.asList(
      new FishingLootEntry(new ItemStack(Items.FISH, 1, FishItem.Type.COD.getId()), 60),
      new FishingLootEntry(new ItemStack(Items.FISH, 1, FishItem.Type.SALMON.getId()), 25),
      new FishingLootEntry(new ItemStack(Items.FISH, 1, FishItem.Type.CLOWNFISH.getId()), 2),
      new FishingLootEntry(new ItemStack(Items.FISH, 1, FishItem.Type.PUFFERFISH.getId()), 13)
   );
   private int posX = -1;
   private int posY = -1;
   private int posZ = -1;
   private Block inBlock;
   private boolean inGround;
   public int shake;
   public PlayerEntity player;
   private int removalTimer;
   private int ticksInAir;
   private int selfHitTimer;
   private int catchTimer;
   private int fishTravelTimer;
   private float fishAngle;
   public Entity caughtEntity;
   private int steps;
   private double fishingBobberX;
   private double fishingBobberY;
   private double fishingBobberZ;
   private double fishingBobberYaw;
   private double fishingBobberPitch;
   @Environment(EnvType.CLIENT)
   private double clientVelocityX;
   @Environment(EnvType.CLIENT)
   private double clientVelocityY;
   @Environment(EnvType.CLIENT)
   private double clientVelocityZ;

   public static List getFishLoot() {
      return FISH_LOOT;
   }

   public FishingBobberEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.25F, 0.25F);
      this.ignoreCameraFrustum = true;
   }

   @Environment(EnvType.CLIENT)
   public FishingBobberEntity(World world, double x, double y, double z, PlayerEntity player) {
      this(world);
      this.setPosition(x, y, z);
      this.ignoreCameraFrustum = true;
      this.player = player;
      player.fishingBobber = this;
   }

   public FishingBobberEntity(World world, PlayerEntity player) {
      super(world);
      this.ignoreCameraFrustum = true;
      this.player = player;
      this.player.fishingBobber = this;
      this.setDimensions(0.25F, 0.25F);
      this.refreshPositionAndAngles(player.x, player.y + (double)player.getEyeHeight(), player.z, player.yaw, player.pitch);
      this.x -= (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F);
      this.y -= 0.1F;
      this.z -= (double)(MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F);
      this.setPosition(this.x, this.y, this.z);
      float var3 = 0.4F;
      this.velocityX = (double)(-MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * var3);
      this.velocityZ = (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * var3);
      this.velocityY = (double)(-MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * var3);
      this.setVelocity(this.velocityX, this.velocityY, this.velocityZ, 1.5F, 1.0F);
   }

   @Override
   protected void initDataTracker() {
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isWithinViewDistance(double distance) {
      double var3 = this.getBoundingBox().getAverageSideLength() * 4.0;
      var3 *= 64.0;
      return distance < var3 * var3;
   }

   public void setVelocity(double velocityX, double velocityY, double velocityZ, float scale, float min) {
      float var9 = MathHelper.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
      velocityX /= (double)var9;
      velocityY /= (double)var9;
      velocityZ /= (double)var9;
      velocityX += this.random.nextGaussian() * 0.0075F * (double)min;
      velocityY += this.random.nextGaussian() * 0.0075F * (double)min;
      velocityZ += this.random.nextGaussian() * 0.0075F * (double)min;
      velocityX *= (double)scale;
      velocityY *= (double)scale;
      velocityZ *= (double)scale;
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
      float var10 = MathHelper.sqrt(velocityX * velocityX + velocityZ * velocityZ);
      this.prevYaw = this.yaw = (float)(Math.atan2(velocityX, velocityZ) * 180.0 / (float) Math.PI);
      this.prevPitch = this.pitch = (float)(Math.atan2(velocityY, (double)var10) * 180.0 / (float) Math.PI);
      this.removalTimer = 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch, int steps) {
      this.fishingBobberX = x;
      this.fishingBobberY = y;
      this.fishingBobberZ = z;
      this.fishingBobberYaw = (double)yaw;
      this.fishingBobberPitch = (double)pitch;
      this.steps = steps;
      this.velocityX = this.clientVelocityX;
      this.velocityY = this.clientVelocityY;
      this.velocityZ = this.clientVelocityZ;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setVelocity(double velocityX, double velocityY, double velocityZ) {
      this.clientVelocityX = this.velocityX = velocityX;
      this.clientVelocityY = this.velocityY = velocityY;
      this.clientVelocityZ = this.velocityZ = velocityZ;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.steps > 0) {
         double var29 = this.x + (this.fishingBobberX - this.x) / (double)this.steps;
         double var31 = this.y + (this.fishingBobberY - this.y) / (double)this.steps;
         double var32 = this.z + (this.fishingBobberZ - this.z) / (double)this.steps;
         double var7 = MathHelper.wrapDegrees(this.fishingBobberYaw - (double)this.yaw);
         this.yaw = (float)((double)this.yaw + var7 / (double)this.steps);
         this.pitch = (float)((double)this.pitch + (this.fishingBobberPitch - (double)this.pitch) / (double)this.steps);
         --this.steps;
         this.setPosition(var29, var31, var32);
         this.setRotation(this.yaw, this.pitch);
      } else {
         if (!this.world.isClient) {
            ItemStack var1 = this.player.getMainHandStack();
            if (this.player.removed
               || !this.player.isAlive()
               || var1 == null
               || var1.getItem() != Items.FISHING_ROD
               || this.getSquaredDistanceTo(this.player) > 1024.0) {
               this.remove();
               this.player.fishingBobber = null;
               return;
            }

            if (this.caughtEntity != null) {
               if (!this.caughtEntity.removed) {
                  this.x = this.caughtEntity.x;
                  double var10002 = (double)this.caughtEntity.height;
                  this.y = this.caughtEntity.getBoundingBox().minY + var10002 * 0.8;
                  this.z = this.caughtEntity.z;
                  return;
               }

               this.caughtEntity = null;
            }
         }

         if (this.shake > 0) {
            --this.shake;
         }

         if (this.inGround) {
            if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getBlock() == this.inBlock) {
               ++this.removalTimer;
               if (this.removalTimer == 1200) {
                  this.remove();
               }

               return;
            }

            this.inGround = false;
            this.velocityX *= (double)(this.random.nextFloat() * 0.2F);
            this.velocityY *= (double)(this.random.nextFloat() * 0.2F);
            this.velocityZ *= (double)(this.random.nextFloat() * 0.2F);
            this.removalTimer = 0;
            this.ticksInAir = 0;
         } else {
            ++this.ticksInAir;
         }

         Vec3d var27 = new Vec3d(this.x, this.y, this.z);
         Vec3d var2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
         HitResult var3 = this.world.rayTrace(var27, var2);
         var27 = new Vec3d(this.x, this.y, this.z);
         var2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
         if (var3 != null) {
            var2 = new Vec3d(var3.pos.x, var3.pos.y, var3.pos.z);
         }

         Entity var4 = null;
         List var5 = this.world.getEntities(this, this.getBoundingBox().grow(this.velocityX, this.velocityY, this.velocityZ).expand(1.0, 1.0, 1.0));
         double var6 = 0.0;

         for(int var8 = 0; var8 < var5.size(); ++var8) {
            Entity var9 = (Entity)var5.get(var8);
            if (var9.hasCollision() && (var9 != this.player || this.ticksInAir >= 5)) {
               float var10 = 0.3F;
               Box var11 = var9.getBoundingBox().expand((double)var10, (double)var10, (double)var10);
               HitResult var12 = var11.clip(var27, var2);
               if (var12 != null) {
                  double var13 = var27.distanceTo(var12.pos);
                  if (var13 < var6 || var6 == 0.0) {
                     var4 = var9;
                     var6 = var13;
                  }
               }
            }
         }

         if (var4 != null) {
            var3 = new HitResult(var4);
         }

         if (var3 != null) {
            if (var3.entity != null) {
               if (var3.entity.damage(DamageSource.thrownProjectile(this, this.player), 0.0F)) {
                  this.caughtEntity = var3.entity;
               }
            } else {
               this.inGround = true;
            }
         }

         if (!this.inGround) {
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            float var33 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * 180.0 / (float) Math.PI);
            this.pitch = (float)(Math.atan2(this.velocityY, (double)var33) * 180.0 / (float) Math.PI);

            while(this.pitch - this.prevPitch < -180.0F) {
               this.prevPitch -= 360.0F;
            }

            while(this.pitch - this.prevPitch >= 180.0F) {
               this.prevPitch += 360.0F;
            }

            while(this.yaw - this.prevYaw < -180.0F) {
               this.prevYaw -= 360.0F;
            }

            while(this.yaw - this.prevYaw >= 180.0F) {
               this.prevYaw += 360.0F;
            }

            this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
            this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
            float var34 = 0.92F;
            if (this.onGround || this.collidingHorizontally) {
               var34 = 0.5F;
            }

            byte var35 = 5;
            double var36 = 0.0;

            for(int var37 = 0; var37 < var35; ++var37) {
               double var14 = this.getBoundingBox().minY
                  + (this.getBoundingBox().maxY - this.getBoundingBox().minY) * (double)(var37 + 0) / (double)var35
                  - 0.125
                  + 0.125;
               double var16 = this.getBoundingBox().minY
                  + (this.getBoundingBox().maxY - this.getBoundingBox().minY) * (double)(var37 + 1) / (double)var35
                  - 0.125
                  + 0.125;
               Box var18 = new Box(this.getBoundingBox().minX, var14, this.getBoundingBox().minZ, this.getBoundingBox().maxX, var16, this.getBoundingBox().maxZ);
               if (this.world.containsLiquid(var18, Material.WATER)) {
                  var36 += 1.0 / (double)var35;
               }
            }

            if (!this.world.isClient && var36 > 0.0) {
               ServerWorld var38 = (ServerWorld)this.world;
               int var40 = 1;
               BlockPos var15 = new BlockPos(this).up();
               if (this.random.nextFloat() < 0.25F && this.world.isRaining(var15)) {
                  var40 = 2;
               }

               if (this.random.nextFloat() < 0.5F && !this.world.hasSkyAccess(var15)) {
                  --var40;
               }

               if (this.selfHitTimer > 0) {
                  --this.selfHitTimer;
                  if (this.selfHitTimer <= 0) {
                     this.catchTimer = 0;
                     this.fishTravelTimer = 0;
                  }
               } else if (this.fishTravelTimer > 0) {
                  this.fishTravelTimer -= var40;
                  if (this.fishTravelTimer <= 0) {
                     this.velocityY -= 0.2F;
                     this.playSound("random.splash", 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                     float var41 = (float)MathHelper.floor(this.getBoundingBox().minY);
                     var38.addParticle(
                        ParticleType.WATER_BUBBLE,
                        this.x,
                        (double)(var41 + 1.0F),
                        this.z,
                        (int)(1.0F + this.width * 20.0F),
                        (double)this.width,
                        0.0,
                        (double)this.width,
                        0.2F
                     );
                     var38.addParticle(
                        ParticleType.WATER_WAKE,
                        this.x,
                        (double)(var41 + 1.0F),
                        this.z,
                        (int)(1.0F + this.width * 20.0F),
                        (double)this.width,
                        0.0,
                        (double)this.width,
                        0.2F
                     );
                     this.selfHitTimer = MathHelper.nextInt(this.random, 10, 30);
                  } else {
                     this.fishAngle = (float)((double)this.fishAngle + this.random.nextGaussian() * 4.0);
                     float var42 = this.fishAngle * (float) (Math.PI / 180.0);
                     float var17 = MathHelper.sin(var42);
                     float var45 = MathHelper.cos(var42);
                     double var19 = this.x + (double)(var17 * (float)this.fishTravelTimer * 0.1F);
                     double var21 = (double)((float)MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
                     double var23 = this.z + (double)(var45 * (float)this.fishTravelTimer * 0.1F);
                     if (this.random.nextFloat() < 0.15F) {
                        var38.addParticle(ParticleType.WATER_BUBBLE, var19, var21 - 0.1F, var23, 1, (double)var17, 0.1, (double)var45, 0.0);
                     }

                     float var25 = var17 * 0.04F;
                     float var26 = var45 * 0.04F;
                     var38.addParticle(ParticleType.WATER_WAKE, var19, var21, var23, 0, (double)var26, 0.01, (double)(-var25), 1.0);
                     var38.addParticle(ParticleType.WATER_WAKE, var19, var21, var23, 0, (double)(-var26), 0.01, (double)var25, 1.0);
                  }
               } else if (this.catchTimer > 0) {
                  this.catchTimer -= var40;
                  float var43 = 0.15F;
                  if (this.catchTimer < 20) {
                     var43 = (float)((double)var43 + (double)(20 - this.catchTimer) * 0.05);
                  } else if (this.catchTimer < 40) {
                     var43 = (float)((double)var43 + (double)(40 - this.catchTimer) * 0.02);
                  } else if (this.catchTimer < 60) {
                     var43 = (float)((double)var43 + (double)(60 - this.catchTimer) * 0.01);
                  }

                  if (this.random.nextFloat() < var43) {
                     float var44 = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
                     float var46 = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
                     double var47 = this.x + (double)(MathHelper.sin(var44) * var46 * 0.1F);
                     double var48 = (double)((float)MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
                     double var49 = this.z + (double)(MathHelper.cos(var44) * var46 * 0.1F);
                     var38.addParticle(ParticleType.WATER_SPLASH, var47, var48, var49, 2 + this.random.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
                  }

                  if (this.catchTimer <= 0) {
                     this.fishAngle = MathHelper.nextFloat(this.random, 0.0F, 360.0F);
                     this.fishTravelTimer = MathHelper.nextInt(this.random, 20, 80);
                  }
               } else {
                  this.catchTimer = MathHelper.nextInt(this.random, 100, 900);
                  this.catchTimer -= EnchantmentHelper.getLureLevel(this.player) * 20 * 5;
               }

               if (this.selfHitTimer > 0) {
                  this.velocityY -= (double)(this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat()) * 0.2;
               }
            }

            double var39 = var36 * 2.0 - 1.0;
            this.velocityY += 0.04F * var39;
            if (var36 > 0.0) {
               var34 = (float)((double)var34 * 0.9);
               this.velocityY *= 0.8;
            }

            this.velocityX *= (double)var34;
            this.velocityY *= (double)var34;
            this.velocityZ *= (double)var34;
            this.setPosition(this.x, this.y, this.z);
         }
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putShort("xTile", (short)this.posX);
      nbt.putShort("yTile", (short)this.posY);
      nbt.putShort("zTile", (short)this.posZ);
      Identifier var2 = (Identifier)Block.REGISTRY.getKey(this.inBlock);
      nbt.putString("inTile", var2 == null ? "" : var2.toString());
      nbt.putByte("shake", (byte)this.shake);
      nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      this.posX = nbt.getShort("xTile");
      this.posY = nbt.getShort("yTile");
      this.posZ = nbt.getShort("zTile");
      if (nbt.isType("inTile", 8)) {
         this.inBlock = Block.byId(nbt.getString("inTile"));
      } else {
         this.inBlock = Block.byRawId(nbt.getByte("inTile") & 255);
      }

      this.shake = nbt.getByte("shake") & 255;
      this.inGround = nbt.getByte("inGround") == 1;
   }

   public int retract() {
      if (this.world.isClient) {
         return 0;
      } else {
         byte var1 = 0;
         if (this.caughtEntity != null) {
            double var2 = this.player.x - this.x;
            double var4 = this.player.y - this.y;
            double var6 = this.player.z - this.z;
            double var8 = (double)MathHelper.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
            double var10 = 0.1;
            this.caughtEntity.velocityX += var2 * var10;
            this.caughtEntity.velocityY += var4 * var10 + (double)MathHelper.sqrt(var8) * 0.08;
            this.caughtEntity.velocityZ += var6 * var10;
            var1 = 3;
         } else if (this.selfHitTimer > 0) {
            ItemEntity var13 = new ItemEntity(this.world, this.x, this.y, this.z, this.getResult());
            double var3 = this.player.x - this.x;
            double var5 = this.player.y - this.y;
            double var7 = this.player.z - this.z;
            double var9 = (double)MathHelper.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
            double var11 = 0.1;
            var13.velocityX = var3 * var11;
            var13.velocityY = var5 * var11 + (double)MathHelper.sqrt(var9) * 0.08;
            var13.velocityZ = var7 * var11;
            this.world.addEntity(var13);
            this.player
               .world
               .addEntity(new XpOrbEntity(this.player.world, this.player.x, this.player.y + 0.5, this.player.z + 0.5, this.random.nextInt(6) + 1));
            var1 = 1;
         }

         if (this.inGround) {
            var1 = 2;
         }

         this.remove();
         this.player.fishingBobber = null;
         return var1;
      }
   }

   private ItemStack getResult() {
      float var1 = this.world.random.nextFloat();
      int var2 = EnchantmentHelper.getLuckOfTheSeaLevel(this.player);
      int var3 = EnchantmentHelper.getLureLevel(this.player);
      float var4 = 0.1F - (float)var2 * 0.025F - (float)var3 * 0.01F;
      float var5 = 0.05F + (float)var2 * 0.01F - (float)var3 * 0.01F;
      var4 = MathHelper.clamp(var4, 0.0F, 1.0F);
      var5 = MathHelper.clamp(var5, 0.0F, 1.0F);
      if (var1 < var4) {
         this.player.incrementStat(Stats.JUNK_FISHED);
         return ((FishingLootEntry)WeightedPicker.pick(this.random, JUNK_LOOT)).getItemStack(this.random);
      } else {
         var1 -= var4;
         if (var1 < var5) {
            this.player.incrementStat(Stats.TREASURE_FISHED);
            return ((FishingLootEntry)WeightedPicker.pick(this.random, TREASURE_LOOT)).getItemStack(this.random);
         } else {
            var1 -= var5;
            this.player.incrementStat(Stats.FISH_CAUGHT);
            return ((FishingLootEntry)WeightedPicker.pick(this.random, FISH_LOOT)).getItemStack(this.random);
         }
      }
   }

   @Override
   public void remove() {
      super.remove();
      if (this.player != null) {
         this.player.fishingBobber = null;
      }
   }
}
