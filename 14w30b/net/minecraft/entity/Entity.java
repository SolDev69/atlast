package net.minecraft.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.HitResult;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class Entity implements CommandSource {
   private static final Box f_61kvhagrg = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   private static int idCounter;
   private int networkId;
   public double viewDistanceScaling;
   public boolean blocksBuilding;
   public Entity rider;
   public Entity vehicle;
   public boolean teleporting;
   public World world;
   public double prevX;
   public double prevY;
   public double prevZ;
   public double x;
   public double y;
   public double z;
   public double velocityX;
   public double velocityY;
   public double velocityZ;
   public float yaw;
   public float pitch;
   public float prevYaw;
   public float prevPitch;
   private Box hitbox;
   public boolean onGround;
   public boolean collidingHorizontally;
   public boolean collidingVertically;
   public boolean colliding;
   public boolean damaged;
   protected boolean inCobweb;
   private boolean f_75fqsqunh;
   public boolean removed;
   public float width;
   public float height;
   public float prevHorizontalSpeed;
   public float horizontalVelocity;
   public float distanceMoved;
   public float fallDistance;
   private int distanceOnNextBlock;
   public double prevTickX;
   public double prevTickY;
   public double prevTickZ;
   public float stepHeight;
   public boolean noClip;
   public float pushSpeedReduction;
   protected Random random;
   public int time;
   public int fireResistance;
   private int onFireTimer;
   protected boolean inWater;
   public int maxHealth;
   private boolean inFirstTick;
   protected boolean immuneToFire;
   protected DataTracker dataTracker;
   private double ridingEntityPitchDelta;
   private double ridingEntityYawDelta;
   public boolean isLoaded;
   public int chunkX;
   public int chunkY;
   public int chunkZ;
   @Environment(EnvType.CLIENT)
   public int packetX;
   @Environment(EnvType.CLIENT)
   public int packetY;
   @Environment(EnvType.CLIENT)
   public int packetZ;
   public boolean ignoreCameraFrustum;
   public boolean velocityDirty;
   public int netherPortalCooldown;
   protected boolean changingDimension;
   protected int netherPortalTime;
   public int dimensionId;
   protected int facing;
   private boolean invulnerable;
   protected UUID uuid;
   private final CommandResults commandResults;

   public int getNetworkId() {
      return this.networkId;
   }

   public void setNetworkId(int id) {
      this.networkId = id;
   }

   public void m_59lfywdxf() {
      this.remove();
   }

   public Entity(World world) {
      this.networkId = idCounter++;
      this.viewDistanceScaling = 1.0;
      this.hitbox = f_61kvhagrg;
      this.width = 0.6F;
      this.height = 1.8F;
      this.distanceOnNextBlock = 1;
      this.random = new Random();
      this.fireResistance = 1;
      this.inFirstTick = true;
      this.uuid = MathHelper.nextUuid(this.random);
      this.commandResults = new CommandResults();
      this.world = world;
      this.setPosition(0.0, 0.0, 0.0);
      if (world != null) {
         this.dimensionId = world.dimension.getId();
      }

      this.dataTracker = new DataTracker(this);
      this.dataTracker.put(0, (byte)0);
      this.dataTracker.put(1, (short)300);
      this.dataTracker.put(3, (byte)0);
      this.dataTracker.put(2, "");
      this.dataTracker.put(4, (byte)0);
      this.initDataTracker();
   }

   protected abstract void initDataTracker();

   public DataTracker getDataTracker() {
      return this.dataTracker;
   }

   @Override
   public boolean equals(Object object) {
      if (object instanceof Entity) {
         return ((Entity)object).networkId == this.networkId;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.networkId;
   }

   @Environment(EnvType.CLIENT)
   protected void postSpawn() {
      if (this.world != null) {
         while(this.y > 0.0 && this.y < 256.0) {
            this.setPosition(this.x, this.y, this.z);
            if (this.world.getCollisions(this, this.getBoundingBox()).isEmpty()) {
               break;
            }

            ++this.y;
         }

         this.velocityX = this.velocityY = this.velocityZ = 0.0;
         this.pitch = 0.0F;
      }
   }

   public void remove() {
      this.removed = true;
   }

   protected void setDimensions(float width, float height) {
      if (width != this.width || height != this.height) {
         float var3 = this.width;
         this.width = width;
         this.height = height;
         this.setHitbox(
            new Box(
               this.getBoundingBox().minX,
               this.getBoundingBox().minY,
               this.getBoundingBox().minZ,
               this.getBoundingBox().minX + (double)this.width,
               this.getBoundingBox().minY + (double)this.height,
               this.getBoundingBox().minZ + (double)this.width
            )
         );
         if (this.width > var3 && !this.inFirstTick && !this.world.isClient) {
            this.move((double)(var3 - this.width), 0.0, (double)(var3 - this.width));
         }
      }
   }

   protected void setRotation(float yaw, float pitch) {
      this.yaw = yaw % 360.0F;
      this.pitch = pitch % 360.0F;
   }

   public void setPosition(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
      float var7 = this.width / 2.0F;
      float var8 = this.height;
      this.setHitbox(new Box(x - (double)var7, y, z - (double)var7, x + (double)var7, y + (double)var8, z + (double)var7));
   }

   @Environment(EnvType.CLIENT)
   public void updateSmoothCamera(float yaw, float pitch) {
      float var3 = this.pitch;
      float var4 = this.yaw;
      this.yaw = (float)((double)this.yaw + (double)yaw * 0.15);
      this.pitch = (float)((double)this.pitch - (double)pitch * 0.15);
      this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
      this.prevPitch += this.pitch - var3;
      this.prevYaw += this.yaw - var4;
   }

   public void tick() {
      this.baseTick();
   }

   public void baseTick() {
      this.world.profiler.push("entityBaseTick");
      if (this.vehicle != null && this.vehicle.removed) {
         this.vehicle = null;
      }

      this.prevHorizontalSpeed = this.horizontalVelocity;
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      this.prevPitch = this.pitch;
      this.prevYaw = this.yaw;
      if (!this.world.isClient && this.world instanceof ServerWorld) {
         this.world.profiler.push("portal");
         MinecraftServer var1 = ((ServerWorld)this.world).getServer();
         int var2 = this.getMaxNetherPortalTime();
         if (this.changingDimension) {
            if (var1.isNetherAllowed()) {
               if (this.vehicle == null && this.netherPortalTime++ >= var2) {
                  this.netherPortalTime = var2;
                  this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
                  byte var3;
                  if (this.world.dimension.getId() == -1) {
                     var3 = 0;
                  } else {
                     var3 = -1;
                  }

                  this.teleportToDimension(var3);
               }

               this.changingDimension = false;
            }
         } else {
            if (this.netherPortalTime > 0) {
               this.netherPortalTime -= 4;
            }

            if (this.netherPortalTime < 0) {
               this.netherPortalTime = 0;
            }
         }

         if (this.netherPortalCooldown > 0) {
            --this.netherPortalCooldown;
         }

         this.world.profiler.pop();
      }

      if (this.isSprinting() && !this.isInWater()) {
         int var7 = MathHelper.floor(this.x);
         int var8 = MathHelper.floor(this.y - 0.2F);
         int var9 = MathHelper.floor(this.z);
         BlockPos var4 = new BlockPos(var7, var8, var9);
         BlockState var5 = this.world.getBlockState(var4);
         Block var6 = var5.getBlock();
         if (var6.getRenderType() != -1) {
            this.world
               .addParticle(
                  ParticleType.BLOCK_CRACK,
                  this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
                  this.getBoundingBox().minY + 0.1,
                  this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
                  -this.velocityX * 4.0,
                  1.5,
                  -this.velocityZ * 4.0,
                  Block.serialize(var5)
               );
         }
      }

      this.checkWaterCollision();
      if (this.world.isClient) {
         this.onFireTimer = 0;
      } else if (this.onFireTimer > 0) {
         if (this.immuneToFire) {
            this.onFireTimer -= 4;
            if (this.onFireTimer < 0) {
               this.onFireTimer = 0;
            }
         } else {
            if (this.onFireTimer % 20 == 0) {
               this.damage(DamageSource.ON_FIRE, 1.0F);
            }

            --this.onFireTimer;
         }
      }

      if (this.isInLava()) {
         this.setOnFireFromLava();
         this.fallDistance *= 0.5F;
      }

      if (this.y < -64.0) {
         this.tickVoid();
      }

      if (!this.world.isClient) {
         this.setFlag(0, this.onFireTimer > 0);
      }

      this.inFirstTick = false;
      this.world.profiler.pop();
   }

   public int getMaxNetherPortalTime() {
      return 0;
   }

   protected void setOnFireFromLava() {
      if (!this.immuneToFire) {
         this.damage(DamageSource.LAVA, 4.0F);
         this.setOnFireFor(15);
      }
   }

   public void setOnFireFor(int seconds) {
      int var2 = seconds * 20;
      var2 = ProtectionEnchantment.modifyOnFireTimer(this, var2);
      if (this.onFireTimer < var2) {
         this.onFireTimer = var2;
      }
   }

   public void extinguish() {
      this.onFireTimer = 0;
   }

   protected void tickVoid() {
      this.remove();
   }

   public boolean canMove(double dx, double dy, double dz) {
      Box var7 = this.getBoundingBox().move(dx, dy, dz);
      return this.m_32hcejgqr(var7);
   }

   private boolean m_32hcejgqr(Box c_19wdelfat) {
      return this.world.getCollisions(this, c_19wdelfat).isEmpty() && !this.world.containsLiquid(c_19wdelfat);
   }

   public void move(double dx, double dy, double dz) {
      if (this.noClip) {
         this.setHitbox(this.getBoundingBox().move(dx, dy, dz));
         this.m_81xwdhsjk();
      } else {
         this.world.profiler.push("move");
         double var7 = this.x;
         double var9 = this.y;
         double var11 = this.z;
         if (this.inCobweb) {
            this.inCobweb = false;
            dx *= 0.25;
            dy *= 0.05F;
            dz *= 0.25;
            this.velocityX = 0.0;
            this.velocityY = 0.0;
            this.velocityZ = 0.0;
         }

         double var13 = dx;
         double var15 = dy;
         double var17 = dz;
         boolean var19 = this.onGround && this.isSneaking() && this instanceof PlayerEntity;
         if (var19) {
            double var20;
            for(var20 = 0.05; dx != 0.0 && this.world.getCollisions(this, this.getBoundingBox().move(dx, -1.0, 0.0)).isEmpty(); var13 = dx) {
               if (dx < var20 && dx >= -var20) {
                  dx = 0.0;
               } else if (dx > 0.0) {
                  dx -= var20;
               } else {
                  dx += var20;
               }
            }

            for(; dz != 0.0 && this.world.getCollisions(this, this.getBoundingBox().move(0.0, -1.0, dz)).isEmpty(); var17 = dz) {
               if (dz < var20 && dz >= -var20) {
                  dz = 0.0;
               } else if (dz > 0.0) {
                  dz -= var20;
               } else {
                  dz += var20;
               }
            }

            for(; dx != 0.0 && dz != 0.0 && this.world.getCollisions(this, this.getBoundingBox().move(dx, -1.0, dz)).isEmpty(); var17 = dz) {
               if (dx < var20 && dx >= -var20) {
                  dx = 0.0;
               } else if (dx > 0.0) {
                  dx -= var20;
               } else {
                  dx += var20;
               }

               var13 = dx;
               if (dz < var20 && dz >= -var20) {
                  dz = 0.0;
               } else if (dz > 0.0) {
                  dz -= var20;
               } else {
                  dz += var20;
               }
            }
         }

         List var37 = this.world.getCollisions(this, this.getBoundingBox().grow(dx, dy, dz));
         Box var21 = this.getBoundingBox();

         for(Box var23 : var37) {
            dy = var23.intersectY(this.getBoundingBox(), dy);
         }

         this.setHitbox(this.getBoundingBox().move(0.0, dy, 0.0));
         boolean var38 = this.onGround || var15 != dy && var15 < 0.0;

         for(Box var24 : var37) {
            dx = var24.intersectX(this.getBoundingBox(), dx);
         }

         this.setHitbox(this.getBoundingBox().move(dx, 0.0, 0.0));

         for(Box var43 : var37) {
            dz = var43.intersectZ(this.getBoundingBox(), dz);
         }

         this.setHitbox(this.getBoundingBox().move(0.0, 0.0, dz));
         if (this.stepHeight > 0.0F && var38 && (var13 != dx || var17 != dz)) {
            double var41 = dx;
            double var25 = dy;
            double var27 = dz;
            Box var29 = this.getBoundingBox();
            this.setHitbox(var21);
            dx = var13;
            dy = (double)this.stepHeight;
            dz = var17;
            List var30 = this.world.getCollisions(this, this.getBoundingBox().grow(var13, dy, var17));

            for(Box var32 : var30) {
               dy = var32.intersectY(this.getBoundingBox().grow(dx, 0.0, dz), dy);
            }

            this.setHitbox(this.getBoundingBox().move(0.0, dy, 0.0));

            for(Box var55 : var30) {
               dx = var55.intersectX(this.getBoundingBox(), dx);
            }

            this.setHitbox(this.getBoundingBox().move(dx, 0.0, 0.0));

            for(Box var56 : var30) {
               dz = var56.intersectZ(this.getBoundingBox(), dz);
            }

            this.setHitbox(this.getBoundingBox().move(0.0, 0.0, dz));
            dy = (double)(-this.stepHeight);

            for(Box var57 : var30) {
               dy = var57.intersectY(this.getBoundingBox(), dy);
            }

            this.setHitbox(this.getBoundingBox().move(0.0, dy, 0.0));
            if (var41 * var41 + var27 * var27 >= dx * dx + dz * dz) {
               dx = var41;
               dy = var25;
               dz = var27;
               this.setHitbox(var29);
            }
         }

         this.world.profiler.pop();
         this.world.profiler.push("rest");
         this.m_81xwdhsjk();
         this.collidingHorizontally = var13 != dx || var17 != dz;
         this.collidingVertically = var15 != dy;
         this.onGround = this.collidingVertically && var15 < 0.0;
         this.colliding = this.collidingHorizontally || this.collidingVertically;
         int var42 = MathHelper.floor(this.x);
         int var44 = MathHelper.floor(this.y - 0.2F);
         int var45 = MathHelper.floor(this.z);
         BlockPos var26 = new BlockPos(var42, var44, var45);
         Block var46 = this.world.getBlockState(var26).getBlock();
         if (var46.getMaterial() == Material.AIR) {
            Block var28 = this.world.getBlockState(var26.down()).getBlock();
            if (var28 instanceof FenceBlock || var28 instanceof WallBlock || var28 instanceof FenceGateBlock) {
               var46 = var28;
               var26 = var26.down();
            }
         }

         this.onFall(dy, this.onGround, var46, var26);
         if (var13 != dx) {
            this.velocityX = 0.0;
         }

         if (var17 != dz) {
            this.velocityZ = 0.0;
         }

         if (var15 != dy) {
            var46.beforeCollision(this.world, this);
         }

         if (this.canClimb() && !var19 && this.vehicle == null) {
            double var47 = this.x - var7;
            double var50 = this.y - var9;
            double var58 = this.z - var11;
            if (var46 != Blocks.LADDER) {
               var50 = 0.0;
            }

            if (var46 != null && this.onGround) {
               var46.onSteppedOn(this.world, var26, this);
            }

            this.horizontalVelocity = (float)((double)this.horizontalVelocity + (double)MathHelper.sqrt(var47 * var47 + var58 * var58) * 0.6);
            this.distanceMoved = (float)((double)this.distanceMoved + (double)MathHelper.sqrt(var47 * var47 + var50 * var50 + var58 * var58) * 0.6);
            if (this.distanceMoved > (float)this.distanceOnNextBlock && var46.getMaterial() != Material.AIR) {
               this.distanceOnNextBlock = (int)this.distanceMoved + 1;
               if (this.isInWater()) {
                  float var34 = MathHelper.sqrt(
                        this.velocityX * this.velocityX * 0.2F + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ * 0.2F
                     )
                     * 0.35F;
                  if (var34 > 1.0F) {
                     var34 = 1.0F;
                  }

                  this.playSound(this.getSwimSound(), var34, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
               }

               this.playStepSound(var26, var46);
            }
         }

         try {
            this.checkBlockCollision();
         } catch (Throwable var35) {
            CrashReport var49 = CrashReport.of(var35, "Checking entity block collision");
            CashReportCategory var51 = var49.addCategory("Entity being checked for collision");
            this.populateCrashReport(var51);
            throw new CrashException(var49);
         }

         boolean var48 = this.isWet();
         if (this.world.containsFireSource(this.getBoundingBox().contract(0.001, 0.001, 0.001))) {
            this.applyFireDamage(1);
            if (!var48) {
               ++this.onFireTimer;
               if (this.onFireTimer == 0) {
                  this.setOnFireFor(8);
               }
            }
         } else if (this.onFireTimer <= 0) {
            this.onFireTimer = -this.fireResistance;
         }

         if (var48 && this.onFireTimer > 0) {
            this.playSound("random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            this.onFireTimer = -this.fireResistance;
         }

         this.world.profiler.pop();
      }
   }

   private void m_81xwdhsjk() {
      this.x = (this.getBoundingBox().minX + this.getBoundingBox().maxX) / 2.0;
      this.y = this.getBoundingBox().minY;
      this.z = (this.getBoundingBox().minZ + this.getBoundingBox().maxZ) / 2.0;
   }

   protected String getSwimSound() {
      return "game.neutral.swim";
   }

   protected void checkBlockCollision() {
      BlockPos var1 = new BlockPos(this.getBoundingBox().minX + 0.001, this.getBoundingBox().minY + 0.001, this.getBoundingBox().minZ + 0.001);
      BlockPos var2 = new BlockPos(this.getBoundingBox().maxX - 0.001, this.getBoundingBox().maxY - 0.001, this.getBoundingBox().maxZ - 0.001);
      if (this.world.isRegionLoaded(var1, var2)) {
         for(int var3 = var1.getX(); var3 <= var2.getX(); ++var3) {
            for(int var4 = var1.getY(); var4 <= var2.getY(); ++var4) {
               for(int var5 = var1.getZ(); var5 <= var2.getZ(); ++var5) {
                  BlockPos var6 = new BlockPos(var3, var4, var5);
                  BlockState var7 = this.world.getBlockState(var6);

                  try {
                     var7.getBlock().onEntityCollision(this.world, var6, var7, this);
                  } catch (Throwable var11) {
                     CrashReport var9 = CrashReport.of(var11, "Colliding entity with block");
                     CashReportCategory var10 = var9.addCategory("Block being collided with");
                     CashReportCategory.addBlockDetails(var10, var6, var7);
                     throw new CrashException(var9);
                  }
               }
            }
         }
      }
   }

   protected void playStepSound(BlockPos x, Block y) {
      Block.Sound var3 = y.sound;
      if (this.world.getBlockState(x.up()).getBlock() == Blocks.SNOW_LAYER) {
         var3 = Blocks.SNOW_LAYER.sound;
         this.playSound(var3.getStepSound(), var3.getVolume() * 0.15F, var3.getPitch());
      } else if (!y.getMaterial().isLiquid()) {
         this.playSound(var3.getStepSound(), var3.getVolume() * 0.15F, var3.getPitch());
      }
   }

   public void playSound(String id, float volume, float pitch) {
      if (!this.isSilent()) {
         this.world.playSound(this, id, volume, pitch);
      }
   }

   public boolean isSilent() {
      return this.dataTracker.getByte(4) == 1;
   }

   public void setSilent(boolean bl) {
      this.dataTracker.update(4, Byte.valueOf((byte)(bl ? 1 : 0)));
   }

   protected boolean canClimb() {
      return true;
   }

   protected void onFall(double dy, boolean landed, Block block, BlockPos pos) {
      if (landed) {
         if (this.fallDistance > 0.0F) {
            if (block != null) {
               block.onFallenOn(this.world, pos, this, this.fallDistance);
            } else {
               this.applyFallDamage(this.fallDistance, 1.0F);
            }

            this.fallDistance = 0.0F;
         }
      } else if (dy < 0.0) {
         this.fallDistance = (float)((double)this.fallDistance - dy);
      }
   }

   public Box getBox() {
      return null;
   }

   protected void applyFireDamage(int amount) {
      if (!this.immuneToFire) {
         this.damage(DamageSource.FIRE, (float)amount);
      }
   }

   public final boolean isImmuneToFire() {
      return this.immuneToFire;
   }

   public void applyFallDamage(float distance, float g) {
      if (this.rider != null) {
         this.rider.applyFallDamage(distance, g);
      }
   }

   public boolean isWet() {
      return this.inWater
         || this.world.isRaining(new BlockPos(this.x, this.y, this.z))
         || this.world.isRaining(new BlockPos(this.x, this.y + (double)this.height, this.z));
   }

   public boolean isInWater() {
      return this.inWater;
   }

   public boolean checkWaterCollision() {
      if (this.world.applyMaterialDrag(this.getBoundingBox().expand(0.0, -0.4F, 0.0).contract(0.001, 0.001, 0.001), Material.WATER, this)) {
         if (!this.inWater && !this.inFirstTick) {
            this.m_72fqgmfka();
         }

         this.fallDistance = 0.0F;
         this.inWater = true;
         this.onFireTimer = 0;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   protected void m_72fqgmfka() {
      float var1 = MathHelper.sqrt(this.velocityX * this.velocityX * 0.2F + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ * 0.2F) * 0.2F;
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      this.playSound(this.getSplashSound(), var1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      float var2 = (float)MathHelper.floor(this.getBoundingBox().minY);

      for(int var3 = 0; (float)var3 < 1.0F + this.width * 20.0F; ++var3) {
         float var4 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
         float var5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
         this.world
            .addParticle(
               ParticleType.WATER_BUBBLE,
               this.x + (double)var4,
               (double)(var2 + 1.0F),
               this.z + (double)var5,
               this.velocityX,
               this.velocityY - (double)(this.random.nextFloat() * 0.2F),
               this.velocityZ
            );
      }

      for(int var6 = 0; (float)var6 < 1.0F + this.width * 20.0F; ++var6) {
         float var7 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
         float var8 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
         this.world
            .addParticle(
               ParticleType.WATER_SPLASH, this.x + (double)var7, (double)(var2 + 1.0F), this.z + (double)var8, this.velocityX, this.velocityY, this.velocityZ
            );
      }
   }

   protected String getSplashSound() {
      return "game.neutral.swim.splash";
   }

   public boolean isSubmergedIn(Material fluid) {
      double var2 = this.y + (double)this.getEyeHeight();
      BlockPos var4 = new BlockPos(this.x, var2, this.z);
      BlockState var5 = this.world.getBlockState(var4);
      Block var6 = var5.getBlock();
      if (var6.getMaterial() == fluid) {
         float var7 = LiquidBlock.getHeightLoss(var5.getBlock().getMetadataFromState(var5)) - 0.11111111F;
         float var8 = (float)(var4.getY() + 1) - var7;
         boolean var9 = var2 < (double)var8;
         return !var9 && this instanceof PlayerEntity ? false : var9;
      } else {
         return false;
      }
   }

   public boolean isInLava() {
      return this.world.containsMaterial(this.getBoundingBox().expand(-0.1F, -0.4F, -0.1F), Material.LAVA);
   }

   public void updateVelocity(float sideways, float forward, float factor) {
      float var4 = sideways * sideways + forward * forward;
      if (!(var4 < 1.0E-4F)) {
         var4 = MathHelper.sqrt(var4);
         if (var4 < 1.0F) {
            var4 = 1.0F;
         }

         var4 = factor / var4;
         sideways *= var4;
         forward *= var4;
         float var5 = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F);
         float var6 = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F);
         this.velocityX += (double)(sideways * var6 - forward * var5);
         this.velocityZ += (double)(forward * var6 + sideways * var5);
      }
   }

   @Environment(EnvType.CLIENT)
   public int getLightLevel(float tickDelta) {
      BlockPos var2 = new BlockPos(this.x, 0.0, this.z);
      if (this.world.isLoaded(var2)) {
         double var3 = (this.getBoundingBox().maxY - this.getBoundingBox().minY) * 0.66;
         int var5 = MathHelper.floor(this.y + var3);
         return this.world.getLightColor(var2.up(var5), 0);
      } else {
         return 0;
      }
   }

   public float getBrightness(float tickDelta) {
      BlockPos var2 = new BlockPos(this.x, 0.0, this.z);
      if (this.world.isLoaded(var2)) {
         double var3 = (this.getBoundingBox().maxY - this.getBoundingBox().minY) * 0.66;
         int var5 = MathHelper.floor(this.y + var3);
         return this.world.getBrightness(var2.up(var5));
      } else {
         return 0.0F;
      }
   }

   public void setWorld(World world) {
      this.world = world;
   }

   public void teleport(double x, double y, double z, float yaw, float pitch) {
      this.prevX = this.x = x;
      this.prevY = this.y = y;
      this.prevZ = this.z = z;
      this.prevYaw = this.yaw = yaw;
      this.prevPitch = this.pitch = pitch;
      double var9 = (double)(this.prevYaw - yaw);
      if (var9 < -180.0) {
         this.prevYaw += 360.0F;
      }

      if (var9 >= 180.0) {
         this.prevYaw -= 360.0F;
      }

      this.setPosition(this.x, this.y, this.z);
      this.setRotation(yaw, pitch);
   }

   public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) {
      this.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, yaw, pitch);
   }

   public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.prevTickX = this.prevX = this.x = x;
      this.prevTickY = this.prevY = this.y = y;
      this.prevTickZ = this.prevZ = this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
      this.setPosition(this.x, this.y, this.z);
   }

   public float getDistanceTo(Entity entity) {
      float var2 = (float)(this.x - entity.x);
      float var3 = (float)(this.y - entity.y);
      float var4 = (float)(this.z - entity.z);
      return MathHelper.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public double getSquaredDistanceTo(double x, double y, double z) {
      double var7 = this.x - x;
      double var9 = this.y - y;
      double var11 = this.z - z;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double getSquaredDistanceTo(BlockPos pos) {
      return pos.squaredDistanceTo(this.x, this.y, this.z);
   }

   public double getSquaredDistanceToCenter(BlockPos pos) {
      return pos.squaredDistanceToCenter(this.x, this.y, this.z);
   }

   public double getDistanceTo(double x, double y, double z) {
      double var7 = this.x - x;
      double var9 = this.y - y;
      double var11 = this.z - z;
      return (double)MathHelper.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
   }

   public double getSquaredDistanceTo(Entity entity) {
      double var2 = this.x - entity.x;
      double var4 = this.y - entity.y;
      double var6 = this.z - entity.z;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public void onPlayerCollision(PlayerEntity player) {
   }

   public void push(Entity entity) {
      if (entity.rider != this && entity.vehicle != this) {
         if (!entity.noClip && !this.noClip) {
            double var2 = entity.x - this.x;
            double var4 = entity.z - this.z;
            double var6 = MathHelper.absMax(var2, var4);
            if (var6 >= 0.01F) {
               var6 = (double)MathHelper.sqrt(var6);
               var2 /= var6;
               var4 /= var6;
               double var8 = 1.0 / var6;
               if (var8 > 1.0) {
                  var8 = 1.0;
               }

               var2 *= var8;
               var4 *= var8;
               var2 *= 0.05F;
               var4 *= 0.05F;
               var2 *= (double)(1.0F - this.pushSpeedReduction);
               var4 *= (double)(1.0F - this.pushSpeedReduction);
               if (this.rider == null) {
                  this.addVelocity(-var2, 0.0, -var4);
               }

               if (entity.rider == null) {
                  entity.addVelocity(var2, 0.0, var4);
               }
            }
         }
      }
   }

   public void addVelocity(double velocityX, double velocityY, double velocityZ) {
      this.velocityX += velocityX;
      this.velocityY += velocityY;
      this.velocityZ += velocityZ;
      this.velocityDirty = true;
   }

   protected void onDamaged() {
      this.damaged = true;
   }

   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         this.onDamaged();
         return false;
      }
   }

   public Vec3d m_01qqqsfds(float f) {
      if (f == 1.0F) {
         return this.m_37mcgfsrt(this.pitch, this.yaw);
      } else {
         float var2 = this.prevPitch + (this.pitch - this.prevPitch) * f;
         float var3 = this.prevYaw + (this.yaw - this.prevYaw) * f;
         return this.m_37mcgfsrt(var2, var3);
      }
   }

   protected final Vec3d m_37mcgfsrt(float f, float g) {
      float var3 = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var4 = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var5 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
      float var6 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
      return new Vec3d((double)(var4 * var5), (double)var6, (double)(var3 * var5));
   }

   @Environment(EnvType.CLIENT)
   public Vec3d m_24itdohjr(float f) {
      if (f == 1.0F) {
         return new Vec3d(this.x, this.y + (double)this.getEyeHeight(), this.z);
      } else {
         double var2 = this.prevX + (this.x - this.prevX) * (double)f;
         double var4 = this.prevY + (this.y - this.prevY) * (double)f + (double)this.getEyeHeight();
         double var6 = this.prevZ + (this.z - this.prevZ) * (double)f;
         return new Vec3d(var2, var4, var6);
      }
   }

   @Environment(EnvType.CLIENT)
   public HitResult rayTrace(double d, float f) {
      Vec3d var4 = this.m_24itdohjr(f);
      Vec3d var5 = this.m_01qqqsfds(f);
      Vec3d var6 = var4.add(var5.x * d, var5.y * d, var5.z * d);
      return this.world.rayTrace(var4, var6, false, false, true);
   }

   public boolean hasCollision() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public void onKillEntity(Entity entity, int score) {
   }

   @Environment(EnvType.CLIENT)
   public boolean isWithinViewDistanceOf(double x, double y, double z) {
      double var7 = this.x - x;
      double var9 = this.y - y;
      double var11 = this.z - z;
      double var13 = var7 * var7 + var9 * var9 + var11 * var11;
      return this.isWithinViewDistance(var13);
   }

   @Environment(EnvType.CLIENT)
   public boolean isWithinViewDistance(double distance) {
      double var3 = this.getBoundingBox().getAverageSideLength();
      var3 *= 64.0 * this.viewDistanceScaling;
      return distance < var3 * var3;
   }

   public boolean writeNbt(NbtCompound nbt) {
      String var2 = this.getTypeId();
      if (!this.removed && var2 != null) {
         nbt.putString("id", var2);
         this.writeEntityNbt(nbt);
         return true;
      } else {
         return false;
      }
   }

   public boolean writeNbtNoRider(NbtCompound nbt) {
      String var2 = this.getTypeId();
      if (!this.removed && var2 != null && this.rider == null) {
         nbt.putString("id", var2);
         this.writeEntityNbt(nbt);
         return true;
      } else {
         return false;
      }
   }

   public void writeEntityNbt(NbtCompound nbt) {
      try {
         nbt.put("Pos", this.toNbtList(this.x, this.y, this.z));
         nbt.put("Motion", this.toNbtList(this.velocityX, this.velocityY, this.velocityZ));
         nbt.put("Rotation", this.toNbtList(this.yaw, this.pitch));
         nbt.putFloat("FallDistance", this.fallDistance);
         nbt.putShort("Fire", (short)this.onFireTimer);
         nbt.putShort("Air", (short)this.getBreath());
         nbt.putBoolean("OnGround", this.onGround);
         nbt.putInt("Dimension", this.dimensionId);
         nbt.putBoolean("Invulnerable", this.invulnerable);
         nbt.putInt("PortalCooldown", this.netherPortalCooldown);
         nbt.putLong("UUIDMost", this.getUuid().getMostSignificantBits());
         nbt.putLong("UUIDLeast", this.getUuid().getLeastSignificantBits());
         if (this.getCustomName() != null && this.getCustomName().length() > 0) {
            nbt.putString("CustomName", this.getCustomName());
            nbt.putBoolean("CustomNameVisible", this.isCustomNameVisible());
         }

         this.commandResults.writeNbt(nbt);
         if (this.isSilent()) {
            nbt.putBoolean("Silent", this.isSilent());
         }

         this.writeCustomNbt(nbt);
         if (this.vehicle != null) {
            NbtCompound var2 = new NbtCompound();
            if (this.vehicle.writeNbt(var2)) {
               nbt.put("Riding", var2);
            }
         }
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.of(var5, "Saving entity NBT");
         CashReportCategory var4 = var3.addCategory("Entity being saved");
         this.populateCrashReport(var4);
         throw new CrashException(var3);
      }
   }

   public void readEntityNbt(NbtCompound nbt) {
      try {
         NbtList var2 = nbt.getList("Pos", 6);
         NbtList var6 = nbt.getList("Motion", 6);
         NbtList var7 = nbt.getList("Rotation", 5);
         this.velocityX = var6.getDouble(0);
         this.velocityY = var6.getDouble(1);
         this.velocityZ = var6.getDouble(2);
         if (Math.abs(this.velocityX) > 10.0) {
            this.velocityX = 0.0;
         }

         if (Math.abs(this.velocityY) > 10.0) {
            this.velocityY = 0.0;
         }

         if (Math.abs(this.velocityZ) > 10.0) {
            this.velocityZ = 0.0;
         }

         this.prevX = this.prevTickX = this.x = var2.getDouble(0);
         this.prevY = this.prevTickY = this.y = var2.getDouble(1);
         this.prevZ = this.prevTickZ = this.z = var2.getDouble(2);
         this.prevYaw = this.yaw = var7.getFloat(0);
         this.prevPitch = this.pitch = var7.getFloat(1);
         this.fallDistance = nbt.getFloat("FallDistance");
         this.onFireTimer = nbt.getShort("Fire");
         this.setBreath(nbt.getShort("Air"));
         this.onGround = nbt.getBoolean("OnGround");
         this.dimensionId = nbt.getInt("Dimension");
         this.invulnerable = nbt.getBoolean("Invulnerable");
         this.netherPortalCooldown = nbt.getInt("PortalCooldown");
         if (nbt.isType("UUIDMost", 4) && nbt.isType("UUIDLeast", 4)) {
            this.uuid = new UUID(nbt.getLong("UUIDMost"), nbt.getLong("UUIDLeast"));
         } else if (nbt.isType("UUID", 8)) {
            this.uuid = UUID.fromString(nbt.getString("UUID"));
         }

         this.setPosition(this.x, this.y, this.z);
         this.setRotation(this.yaw, this.pitch);
         if (nbt.isType("CustomName", 8) && nbt.getString("CustomName").length() > 0) {
            this.setCustomName(nbt.getString("CustomName"));
         }

         this.setCustomNameVisible(nbt.getBoolean("CustomNameVisible"));
         this.commandResults.readNbt(nbt);
         this.setSilent(nbt.getBoolean("Silent"));
         this.readCustomNbt(nbt);
         if (this.shouldSetPositionOnLoad()) {
            this.setPosition(this.x, this.y, this.z);
         }
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.of(var5, "Loading entity NBT");
         CashReportCategory var4 = var3.addCategory("Entity being loaded");
         this.populateCrashReport(var4);
         throw new CrashException(var3);
      }
   }

   protected boolean shouldSetPositionOnLoad() {
      return true;
   }

   protected final String getTypeId() {
      return Entities.getId(this);
   }

   protected abstract void readCustomNbt(NbtCompound nbt);

   protected abstract void writeCustomNbt(NbtCompound nbt);

   public void beforeLoadedIntoWorld() {
   }

   protected NbtList toNbtList(double... values) {
      NbtList var2 = new NbtList();

      for(double var6 : values) {
         var2.add(new NbtDouble(var6));
      }

      return var2;
   }

   protected NbtList toNbtList(float... values) {
      NbtList var2 = new NbtList();

      for(float var6 : values) {
         var2.add(new NbtFloat(var6));
      }

      return var2;
   }

   public ItemEntity dropItem(Item item, int count) {
      return this.dropItem(item, count, 0.0F);
   }

   public ItemEntity dropItem(Item item, int count, float yOffset) {
      return this.dropItem(new ItemStack(item, count, 0), yOffset);
   }

   public ItemEntity dropItem(ItemStack stack, float yOffset) {
      if (stack.size != 0 && stack.getItem() != null) {
         ItemEntity var3 = new ItemEntity(this.world, this.x, this.y + (double)yOffset, this.z, stack);
         var3.resetPickupCooldown();
         this.world.addEntity(var3);
         return var3;
      } else {
         return null;
      }
   }

   public boolean isAlive() {
      return !this.removed;
   }

   public boolean isInWall() {
      if (this.noClip) {
         return false;
      } else {
         for(int var1 = 0; var1 < 8; ++var1) {
            double var2 = this.x + (double)(((float)((var1 >> 0) % 2) - 0.5F) * this.width * 0.8F);
            double var4 = this.y + (double)(((float)((var1 >> 1) % 2) - 0.5F) * 0.1F);
            double var6 = this.z + (double)(((float)((var1 >> 2) % 2) - 0.5F) * this.width * 0.8F);
            if (this.world.getBlockState(new BlockPos(var2, var4 + (double)this.getEyeHeight(), var6)).getBlock().isViewBlocking()) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean interact(PlayerEntity player) {
      return false;
   }

   public Box getHardCollisionBox(Entity collidingEntity) {
      return null;
   }

   public void tickRiding() {
      if (this.vehicle.removed) {
         this.vehicle = null;
      } else {
         this.velocityX = 0.0;
         this.velocityY = 0.0;
         this.velocityZ = 0.0;
         this.tick();
         if (this.vehicle != null) {
            this.vehicle.updateRiderPositon();
            this.ridingEntityYawDelta += (double)(this.vehicle.yaw - this.vehicle.prevYaw);
            this.ridingEntityPitchDelta += (double)(this.vehicle.pitch - this.vehicle.prevPitch);

            while(this.ridingEntityYawDelta >= 180.0) {
               this.ridingEntityYawDelta -= 360.0;
            }

            while(this.ridingEntityYawDelta < -180.0) {
               this.ridingEntityYawDelta += 360.0;
            }

            while(this.ridingEntityPitchDelta >= 180.0) {
               this.ridingEntityPitchDelta -= 360.0;
            }

            while(this.ridingEntityPitchDelta < -180.0) {
               this.ridingEntityPitchDelta += 360.0;
            }

            double var1 = this.ridingEntityYawDelta * 0.5;
            double var3 = this.ridingEntityPitchDelta * 0.5;
            float var5 = 10.0F;
            if (var1 > (double)var5) {
               var1 = (double)var5;
            }

            if (var1 < (double)(-var5)) {
               var1 = (double)(-var5);
            }

            if (var3 > (double)var5) {
               var3 = (double)var5;
            }

            if (var3 < (double)(-var5)) {
               var3 = (double)(-var5);
            }

            this.ridingEntityYawDelta -= var1;
            this.ridingEntityPitchDelta -= var3;
         }
      }
   }

   public void updateRiderPositon() {
      if (this.rider != null) {
         this.rider.setPosition(this.x, this.y + this.getMountHeight() + this.rider.getRideHeight(), this.z);
      }
   }

   public double getRideHeight() {
      return 0.0;
   }

   public double getMountHeight() {
      return (double)this.height * 0.75;
   }

   public void startRiding(Entity entity) {
      this.ridingEntityPitchDelta = 0.0;
      this.ridingEntityYawDelta = 0.0;
      if (entity == null) {
         if (this.vehicle != null) {
            this.refreshPositionAndAngles(
               this.vehicle.x, this.vehicle.getBoundingBox().minY + (double)this.vehicle.height, this.vehicle.z, this.yaw, this.pitch
            );
            this.vehicle.rider = null;
         }

         this.vehicle = null;
      } else {
         if (this.vehicle != null) {
            this.vehicle.rider = null;
         }

         if (entity != null) {
            for(Entity var2 = entity.vehicle; var2 != null; var2 = var2.vehicle) {
               if (var2 == this) {
                  return;
               }
            }
         }

         this.vehicle = entity;
         entity.rider = this;
      }
   }

   @Environment(EnvType.CLIENT)
   public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch, int i, boolean bl) {
      this.setPosition(x, y, z);
      this.setRotation(yaw, pitch);
      List var11 = this.world.getCollisions(this, this.getBoundingBox().contract(0.03125, 0.0, 0.03125));
      if (!var11.isEmpty()) {
         double var12 = 0.0;

         for(Box var15 : var11) {
            if (var15.maxY > var12) {
               var12 = var15.maxY;
            }
         }

         y += var12 - this.getBoundingBox().minY;
         this.setPosition(x, y, z);
      }
   }

   public float getExtraHitboxSize() {
      return 0.1F;
   }

   public Vec3d getCameraRotation() {
      return null;
   }

   public void onPortalCollision() {
      if (this.netherPortalCooldown > 0) {
         this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
      } else {
         double var1 = this.prevX - this.x;
         double var3 = this.prevZ - this.z;
         if (!this.world.isClient && !this.changingDimension) {
            int var5;
            if (MathHelper.abs((float)var1) > MathHelper.abs((float)var3)) {
               var5 = var1 > 0.0 ? Direction.WEST.getIdHorizontal() : Direction.EAST.getIdHorizontal();
            } else {
               var5 = var3 > 0.0 ? Direction.NORTH.getIdHorizontal() : Direction.SOUTH.getIdHorizontal();
            }

            this.facing = var5;
         }

         this.changingDimension = true;
      }
   }

   public int getDefaultNetherPortalCooldown() {
      return 300;
   }

   @Environment(EnvType.CLIENT)
   public void setVelocity(double velocityX, double velocityY, double velocityZ) {
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
   }

   @Environment(EnvType.CLIENT)
   public void doEvent(byte event) {
   }

   @Environment(EnvType.CLIENT)
   public void animateDamage() {
   }

   public ItemStack[] getEquipmentStacks() {
      return null;
   }

   public void setEquipmentStack(int slot, ItemStack stack) {
   }

   public boolean isOnFire() {
      boolean var1 = this.world != null && this.world.isClient;
      return !this.immuneToFire && (this.onFireTimer > 0 || var1 && this.getFlag(0));
   }

   public boolean hasVehicle() {
      return this.vehicle != null;
   }

   public boolean isSneaking() {
      return this.getFlag(1);
   }

   public void setSneaking(boolean sneaking) {
      this.setFlag(1, sneaking);
   }

   public boolean isSprinting() {
      return this.getFlag(3);
   }

   public void setSprinting(boolean sprinting) {
      this.setFlag(3, sprinting);
   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   @Environment(EnvType.CLIENT)
   public boolean isInvisibleTo(PlayerEntity player) {
      return player.isSpectator() ? false : this.isInvisible();
   }

   public void setInvisible(boolean invisible) {
      this.setFlag(5, invisible);
   }

   @Environment(EnvType.CLIENT)
   public boolean isSwimming() {
      return this.getFlag(4);
   }

   public void setSwimming(boolean swimming) {
      this.setFlag(4, swimming);
   }

   protected boolean getFlag(int index) {
      return (this.dataTracker.getByte(0) & 1 << index) != 0;
   }

   protected void setFlag(int index, boolean value) {
      byte var3 = this.dataTracker.getByte(0);
      if (value) {
         this.dataTracker.update(0, (byte)(var3 | 1 << index));
      } else {
         this.dataTracker.update(0, (byte)(var3 & ~(1 << index)));
      }
   }

   public int getBreath() {
      return this.dataTracker.getShort(1);
   }

   public void setBreath(int air) {
      this.dataTracker.update(1, (short)air);
   }

   public void onLightningStrike(LightningBoltEntity lightning) {
      this.applyFireDamage(5);
      ++this.onFireTimer;
      if (this.onFireTimer == 0) {
         this.setOnFireFor(8);
      }
   }

   public void onKill(LivingEntity victim) {
   }

   protected boolean pushAwayFrom(double x, double y, double z) {
      BlockPos var7 = new BlockPos(x, y, z);
      double var8 = x - (double)var7.getX();
      double var10 = y - (double)var7.getY();
      double var12 = z - (double)var7.getZ();
      List var14 = this.world.getCollisionBoxes(this.getBoundingBox());
      if (var14.isEmpty() && !this.world.isFullCube(var7)) {
         return false;
      } else {
         byte var15 = 3;
         double var16 = 9999.0;
         if (!this.world.isFullCube(var7.west()) && var8 < var16) {
            var16 = var8;
            var15 = 0;
         }

         if (!this.world.isFullCube(var7.east()) && 1.0 - var8 < var16) {
            var16 = 1.0 - var8;
            var15 = 1;
         }

         if (!this.world.isFullCube(var7.up()) && 1.0 - var10 < var16) {
            var16 = 1.0 - var10;
            var15 = 3;
         }

         if (!this.world.isFullCube(var7.north()) && var12 < var16) {
            var16 = var12;
            var15 = 4;
         }

         if (!this.world.isFullCube(var7.south()) && 1.0 - var12 < var16) {
            var16 = 1.0 - var12;
            var15 = 5;
         }

         float var18 = this.random.nextFloat() * 0.2F + 0.1F;
         if (var15 == 0) {
            this.velocityX = (double)(-var18);
         }

         if (var15 == 1) {
            this.velocityX = (double)var18;
         }

         if (var15 == 3) {
            this.velocityY = (double)var18;
         }

         if (var15 == 4) {
            this.velocityZ = (double)(-var18);
         }

         if (var15 == 5) {
            this.velocityZ = (double)var18;
         }

         return true;
      }
   }

   public void onCobwebCollision() {
      this.inCobweb = true;
      this.fallDistance = 0.0F;
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomName();
      } else {
         String var1 = Entities.getId(this);
         if (var1 == null) {
            var1 = "generic";
         }

         return I18n.translate("entity." + var1 + ".name");
      }
   }

   public Entity[] getParts() {
      return null;
   }

   public boolean is(Entity entity) {
      return this == entity;
   }

   public float getHeadYaw() {
      return 0.0F;
   }

   public void setHeadYaw(float headYaw) {
   }

   public boolean canBePunched() {
      return true;
   }

   public boolean onPunched(Entity attacker) {
      return false;
   }

   @Override
   public String toString() {
      return String.format(
         "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
         this.getClass().getSimpleName(),
         this.getName(),
         this.networkId,
         this.world == null ? "~NULL~" : this.world.getData().getName(),
         this.x,
         this.y,
         this.z
      );
   }

   public boolean isInvulnerable(DamageSource c_64nqsnjso) {
      return this.invulnerable && c_64nqsnjso != DamageSource.OUT_OF_WORLD && !c_64nqsnjso.isCreativePlayer();
   }

   public void copyPositionAndRotationFrom(Entity entity) {
      this.refreshPositionAndAngles(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
   }

   public void copyNbtFrom(Entity sourceEntity) {
      NbtCompound var2 = new NbtCompound();
      sourceEntity.writeEntityNbt(var2);
      this.readEntityNbt(var2);
      this.netherPortalCooldown = sourceEntity.netherPortalCooldown;
      this.facing = sourceEntity.facing;
   }

   public void teleportToDimension(int dimensionId) {
      if (!this.world.isClient && !this.removed) {
         this.world.profiler.push("changeDimension");
         MinecraftServer var2 = MinecraftServer.getInstance();
         int var3 = this.dimensionId;
         ServerWorld var4 = var2.getWorld(var3);
         ServerWorld var5 = var2.getWorld(dimensionId);
         this.dimensionId = dimensionId;
         if (var3 == 1 && dimensionId == 1) {
            var5 = var2.getWorld(0);
            this.dimensionId = 0;
         }

         this.world.removeEntity(this);
         this.removed = false;
         this.world.profiler.push("reposition");
         var2.getPlayerManager().teleportEntityToDimension(this, var3, var4, var5);
         this.world.profiler.swap("reloading");
         Entity var6 = Entities.createSilently(Entities.getId(this), var5);
         if (var6 != null) {
            var6.copyNbtFrom(this);
            if (var3 == 1 && dimensionId == 1) {
               BlockPos var7 = this.world.getSurfaceHeight(var5.getSpawnPoint());
               var6.refreshPositionAndAngles(var7, var6.yaw, var6.pitch);
            }

            var5.addEntity(var6);
         }

         this.removed = true;
         this.world.profiler.pop();
         var4.resetIdleTimeout();
         var5.resetIdleTimeout();
         this.world.profiler.pop();
      }
   }

   public float getBlastResistance(Explosion explosion, World world, BlockPos x, BlockState y) {
      return y.getBlock().getBlastResistance(this);
   }

   public boolean canExplodeBlock(Explosion explosion, World world, BlockPos x, BlockState y, float z) {
      return true;
   }

   public int getSafeFallDistance() {
      return 3;
   }

   public int getFacing() {
      return this.facing;
   }

   public boolean canAvoidTraps() {
      return false;
   }

   public void populateCrashReport(CashReportCategory section) {
      section.add("Entity Type", new Callable() {
         public String call() {
            return Entities.getId(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
         }
      });
      section.add("Entity ID", this.networkId);
      section.add("Entity Name", new Callable() {
         public String call() {
            return Entity.this.getName();
         }
      });
      section.add("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.x, this.y, this.z));
      section.add(
         "Entity's Block location",
         CashReportCategory.formatPosition((double)MathHelper.floor(this.x), (double)MathHelper.floor(this.y), (double)MathHelper.floor(this.z))
      );
      section.add("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.velocityX, this.velocityY, this.velocityZ));
      section.add("Entity's Rider", new Callable() {
         public String call() {
            return Entity.this.rider.toString();
         }
      });
      section.add("Entity's Vehicle", new Callable() {
         public String call() {
            return Entity.this.vehicle.toString();
         }
      });
   }

   @Environment(EnvType.CLIENT)
   public boolean shouldRenderOnFire() {
      return this.isOnFire();
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public boolean hasLiquidCollision() {
      return true;
   }

   @Override
   public Text getDisplayName() {
      LiteralText var1 = new LiteralText(this.getName());
      var1.getStyle().setHoverEvent(this.getHoverEvent());
      var1.getStyle().setInsertion(this.getUuid().toString());
      return var1;
   }

   public void setCustomName(String name) {
      this.dataTracker.update(2, name);
   }

   public String getCustomName() {
      return this.dataTracker.getString(2);
   }

   public boolean hasCustomName() {
      return this.dataTracker.getString(2).length() > 0;
   }

   public void setCustomNameVisible(boolean visible) {
      this.dataTracker.update(3, Byte.valueOf((byte)(visible ? 1 : 0)));
   }

   public boolean isCustomNameVisible() {
      return this.dataTracker.getByte(3) == 1;
   }

   @Environment(EnvType.CLIENT)
   public boolean shouldShowNameTag() {
      return this.isCustomNameVisible();
   }

   public void onDataValueChanged(int id) {
   }

   public Direction getDirection() {
      return Direction.byIdHorizontal(MathHelper.floor((double)(this.yaw * 4.0F / 360.0F) + 0.5) & 3);
   }

   protected HoverEvent getHoverEvent() {
      NbtCompound var1 = new NbtCompound();
      String var2 = Entities.getId(this);
      var1.putString("id", this.getUuid().toString());
      if (var2 != null) {
         var1.putString("type", var2);
      }

      var1.putString("name", this.getName());
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new LiteralText(var1.toString()));
   }

   public boolean m_89sxhouae(ServerPlayerEntity c_53mtutqhz) {
      return true;
   }

   public Box getBoundingBox() {
      return this.hitbox;
   }

   public void setHitbox(Box hitbox) {
      this.hitbox = hitbox;
   }

   public float getEyeHeight() {
      return this.height * 0.85F;
   }

   public boolean m_13uofunxk() {
      return this.f_75fqsqunh;
   }

   public void m_72yjttsjc(boolean bl) {
      this.f_75fqsqunh = bl;
   }

   public boolean m_81zmldzmm(int i, ItemStack c_72owraavl) {
      return false;
   }

   @Override
   public void sendMessage(Text message) {
   }

   @Override
   public boolean canUseCommand(int permissionLevel, String command) {
      return true;
   }

   @Override
   public BlockPos getSourceBlockPos() {
      return new BlockPos(this.x, this.y + 0.5, this.z);
   }

   @Override
   public World getSourceWorld() {
      return this.world;
   }

   @Override
   public Entity asEntity() {
      return this;
   }

   @Override
   public boolean sendCommandFeedback() {
      return false;
   }

   @Override
   public void addResult(CommandResults.Type type, int result) {
      this.commandResults.add(this, type, result);
   }

   public CommandResults getCommandResults() {
      return this.commandResults;
   }

   public void copyCommandResults(Entity entity) {
      this.commandResults.copy(entity.getCommandResults());
   }
}
