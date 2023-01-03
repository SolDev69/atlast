package net.minecraft.entity.vehicle;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Nameable;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class MinecartEntity extends Entity implements Nameable {
   private boolean yawFlipped;
   private String customName;
   private static final int[][][] ADJACENT_RAIL_POSITIONS_BY_SHAPE = new int[][][]{
      {{0, 0, -1}, {0, 0, 1}},
      {{-1, 0, 0}, {1, 0, 0}},
      {{-1, -1, 0}, {1, 0, 0}},
      {{-1, 0, 0}, {1, -1, 0}},
      {{0, 0, -1}, {0, -1, 1}},
      {{0, -1, -1}, {0, 0, 1}},
      {{0, 0, 1}, {1, 0, 0}},
      {{0, 0, 1}, {-1, 0, 0}},
      {{0, 0, -1}, {-1, 0, 0}},
      {{0, 0, -1}, {1, 0, 0}}
   };
   private int clientInterpolationSteps;
   private double clientX;
   private double clientY;
   private double clientZ;
   private double clientYaw;
   private double clientPitch;
   @Environment(EnvType.CLIENT)
   private double clientXVelocity;
   @Environment(EnvType.CLIENT)
   private double clientYVelocity;
   @Environment(EnvType.CLIENT)
   private double clientZVelocity;

   public MinecartEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.blocksBuilding = true;
      this.setDimensions(0.98F, 0.7F);
   }

   public static MinecartEntity create(World world, double x, double y, double z, MinecartEntity.Type type) {
      switch(type) {
         case CHEST:
            return new ChestMinecartEntity(world, x, y, z);
         case FURNACE:
            return new FurnaceMinecartEntity(world, x, y, z);
         case TNT:
            return new TntMinecartEntity(world, x, y, z);
         case SPAWNER:
            return new SpawnerMinecartEntity(world, x, y, z);
         case HOPPER:
            return new HopperMinecartEntity(world, x, y, z);
         case COMMAND_BLOCK:
            return new CommandBlockMinecartEntity(world, x, y, z);
         default:
            return new RideableMinecartEntity(world, x, y, z);
      }
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   protected void initDataTracker() {
      this.dataTracker.put(17, new Integer(0));
      this.dataTracker.put(18, new Integer(1));
      this.dataTracker.put(19, new Float(0.0F));
      this.dataTracker.put(20, new Integer(0));
      this.dataTracker.put(21, new Integer(6));
      this.dataTracker.put(22, (byte)0);
   }

   @Override
   public Box getHardCollisionBox(Entity collidingEntity) {
      return collidingEntity.isPushable() ? collidingEntity.getBoundingBox() : null;
   }

   @Override
   public Box getBox() {
      return null;
   }

   @Override
   public boolean isPushable() {
      return true;
   }

   public MinecartEntity(World world, double x, double y, double z) {
      this(world);
      this.setPosition(x, y, z);
      this.velocityX = 0.0;
      this.velocityY = 0.0;
      this.velocityZ = 0.0;
      this.prevX = x;
      this.prevY = y;
      this.prevZ = z;
   }

   @Override
   public double getMountHeight() {
      return (double)this.height * 0.5 - 0.2F;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.world.isClient || this.removed) {
         return true;
      } else if (this.isInvulnerable(source)) {
         return false;
      } else {
         this.setDamageWobbleSide(-this.getDamageWobbleSide());
         this.setDamageWobbleTicks(10);
         this.onDamaged();
         this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
         boolean var3 = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
         if (var3 || this.getDamageWobbleStrength() > 40.0F) {
            if (this.rider != null) {
               this.rider.startRiding(null);
            }

            if (var3 && !this.hasCustomName()) {
               this.remove();
            } else {
               this.dropItems(source);
            }
         }

         return true;
      }
   }

   public void dropItems(DamageSource damageSource) {
      this.remove();
      ItemStack var2 = new ItemStack(Items.MINECART, 1);
      if (this.customName != null) {
         var2.setHoverName(this.customName);
      }

      this.dropItem(var2, 0.0F);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void animateDamage() {
      this.setDamageWobbleSide(-this.getDamageWobbleSide());
      this.setDamageWobbleTicks(10);
      this.setDamageWobbleStrength(this.getDamageWobbleStrength() + this.getDamageWobbleStrength() * 10.0F);
   }

   @Override
   public boolean hasCollision() {
      return !this.removed;
   }

   @Override
   public void remove() {
      super.remove();
   }

   @Override
   public void tick() {
      if (this.getDamageWobbleTicks() > 0) {
         this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
      }

      if (this.getDamageWobbleStrength() > 0.0F) {
         this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
      }

      if (this.y < -64.0) {
         this.tickVoid();
      }

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

      if (this.world.isClient) {
         if (this.clientInterpolationSteps > 0) {
            double var15 = this.x + (this.clientX - this.x) / (double)this.clientInterpolationSteps;
            double var18 = this.y + (this.clientY - this.y) / (double)this.clientInterpolationSteps;
            double var19 = this.z + (this.clientZ - this.z) / (double)this.clientInterpolationSteps;
            double var7 = MathHelper.wrapDegrees(this.clientYaw - (double)this.yaw);
            this.yaw = (float)((double)this.yaw + var7 / (double)this.clientInterpolationSteps);
            this.pitch = (float)((double)this.pitch + (this.clientPitch - (double)this.pitch) / (double)this.clientInterpolationSteps);
            --this.clientInterpolationSteps;
            this.setPosition(var15, var18, var19);
            this.setRotation(this.yaw, this.pitch);
         } else {
            this.setPosition(this.x, this.y, this.z);
            this.setRotation(this.yaw, this.pitch);
         }
      } else {
         this.prevX = this.x;
         this.prevY = this.y;
         this.prevZ = this.z;
         this.velocityY -= 0.04F;
         int var14 = MathHelper.floor(this.x);
         int var16 = MathHelper.floor(this.y);
         int var17 = MathHelper.floor(this.z);
         if (AbstractRailBlock.isRail(this.world, new BlockPos(var14, var16 - 1, var17))) {
            --var16;
         }

         BlockPos var4 = new BlockPos(var14, var16, var17);
         BlockState var5 = this.world.getBlockState(var4);
         if (AbstractRailBlock.isRail(var5)) {
            this.moveOnRail(var4, var5);
            if (var5.getBlock() == Blocks.ACTIVATOR_RAIL) {
               this.onActivatorRail(var14, var16, var17, var5.get(PoweredRailBlock.POWERED));
            }
         } else {
            this.moveOffRail();
         }

         this.checkBlockCollision();
         this.pitch = 0.0F;
         double var6 = this.prevX - this.x;
         double var8 = this.prevZ - this.z;
         if (var6 * var6 + var8 * var8 > 0.001) {
            this.yaw = (float)(Math.atan2(var8, var6) * 180.0 / Math.PI);
            if (this.yawFlipped) {
               this.yaw += 180.0F;
            }
         }

         double var10 = (double)MathHelper.wrapDegrees(this.yaw - this.prevYaw);
         if (var10 < -170.0 || var10 >= 170.0) {
            this.yaw += 180.0F;
            this.yawFlipped = !this.yawFlipped;
         }

         this.setRotation(this.yaw, this.pitch);

         for(Entity var13 : this.world.getEntities(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F))) {
            if (var13 != this.rider && var13.isPushable() && var13 instanceof MinecartEntity) {
               var13.push(this);
            }
         }

         if (this.rider != null && this.rider.removed) {
            if (this.rider.vehicle == this) {
               this.rider.vehicle = null;
            }

            this.rider = null;
         }

         this.checkWaterCollision();
      }
   }

   protected double m_41gmsyuoz() {
      return 0.4;
   }

   public void onActivatorRail(int x, int y, int z, boolean powered) {
   }

   protected void moveOffRail() {
      double var1 = this.m_41gmsyuoz();
      this.velocityX = MathHelper.clamp(this.velocityX, -var1, var1);
      this.velocityZ = MathHelper.clamp(this.velocityZ, -var1, var1);
      if (this.onGround) {
         this.velocityX *= 0.5;
         this.velocityY *= 0.5;
         this.velocityZ *= 0.5;
      }

      this.move(this.velocityX, this.velocityY, this.velocityZ);
      if (!this.onGround) {
         this.velocityX *= 0.95F;
         this.velocityY *= 0.95F;
         this.velocityZ *= 0.95F;
      }
   }

   protected void moveOnRail(BlockPos x, BlockState y) {
      this.fallDistance = 0.0F;
      Vec3d var3 = this.snapPositionToRail(this.x, this.y, this.z);
      this.y = (double)x.getY();
      boolean var4 = false;
      boolean var5 = false;
      AbstractRailBlock var6 = (AbstractRailBlock)y.getBlock();
      if (var6 == Blocks.POWERED_RAIL) {
         var4 = y.get(PoweredRailBlock.POWERED);
         var5 = !var4;
      }

      double var7 = 0.0078125;
      AbstractRailBlock.Shape var9 = (AbstractRailBlock.Shape)y.get(var6.getShapeProperty());
      switch(var9) {
         case ASCENDING_EAST:
            this.velocityX -= 0.0078125;
            ++this.y;
            break;
         case ASCENDING_WEST:
            this.velocityX += 0.0078125;
            ++this.y;
            break;
         case ASCENDING_NORTH:
            this.velocityZ += 0.0078125;
            ++this.y;
            break;
         case ASCENDING_SOUTH:
            this.velocityZ -= 0.0078125;
            ++this.y;
      }

      int[][] var10 = ADJACENT_RAIL_POSITIONS_BY_SHAPE[var9.getIndex()];
      double var11 = (double)(var10[1][0] - var10[0][0]);
      double var13 = (double)(var10[1][2] - var10[0][2]);
      double var15 = Math.sqrt(var11 * var11 + var13 * var13);
      double var17 = this.velocityX * var11 + this.velocityZ * var13;
      if (var17 < 0.0) {
         var11 = -var11;
         var13 = -var13;
      }

      double var19 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
      if (var19 > 2.0) {
         var19 = 2.0;
      }

      this.velocityX = var19 * var11 / var15;
      this.velocityZ = var19 * var13 / var15;
      if (this.rider instanceof LivingEntity) {
         double var21 = (double)((LivingEntity)this.rider).forwardSpeed;
         if (var21 > 0.0) {
            double var23 = -Math.sin((double)(this.rider.yaw * (float) Math.PI / 180.0F));
            double var25 = Math.cos((double)(this.rider.yaw * (float) Math.PI / 180.0F));
            double var27 = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
            if (var27 < 0.01) {
               this.velocityX += var23 * 0.1;
               this.velocityZ += var25 * 0.1;
               var5 = false;
            }
         }
      }

      if (var5) {
         double var48 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
         if (var48 < 0.03) {
            this.velocityX *= 0.0;
            this.velocityY *= 0.0;
            this.velocityZ *= 0.0;
         } else {
            this.velocityX *= 0.5;
            this.velocityY *= 0.0;
            this.velocityZ *= 0.5;
         }
      }

      double var49 = 0.0;
      double var51 = (double)x.getX() + 0.5 + (double)var10[0][0] * 0.5;
      double var52 = (double)x.getZ() + 0.5 + (double)var10[0][2] * 0.5;
      double var53 = (double)x.getX() + 0.5 + (double)var10[1][0] * 0.5;
      double var29 = (double)x.getZ() + 0.5 + (double)var10[1][2] * 0.5;
      var11 = var53 - var51;
      var13 = var29 - var52;
      if (var11 == 0.0) {
         this.x = (double)x.getX() + 0.5;
         var49 = this.z - (double)x.getZ();
      } else if (var13 == 0.0) {
         this.z = (double)x.getZ() + 0.5;
         var49 = this.x - (double)x.getX();
      } else {
         double var31 = this.x - var51;
         double var33 = this.z - var52;
         var49 = (var31 * var11 + var33 * var13) * 2.0;
      }

      this.x = var51 + var11 * var49;
      this.z = var52 + var13 * var49;
      this.setPosition(this.x, this.y, this.z);
      double var54 = this.velocityX;
      double var56 = this.velocityZ;
      if (this.rider != null) {
         var54 *= 0.75;
         var56 *= 0.75;
      }

      double var35 = this.m_41gmsyuoz();
      var54 = MathHelper.clamp(var54, -var35, var35);
      var56 = MathHelper.clamp(var56, -var35, var35);
      this.move(var54, 0.0, var56);
      if (var10[0][1] != 0 && MathHelper.floor(this.x) - x.getX() == var10[0][0] && MathHelper.floor(this.z) - x.getZ() == var10[0][2]) {
         this.setPosition(this.x, this.y + (double)var10[0][1], this.z);
      } else if (var10[1][1] != 0 && MathHelper.floor(this.x) - x.getX() == var10[1][0] && MathHelper.floor(this.z) - x.getZ() == var10[1][2]) {
         this.setPosition(this.x, this.y + (double)var10[1][1], this.z);
      }

      this.applySlowdown();
      Vec3d var37 = this.snapPositionToRail(this.x, this.y, this.z);
      if (var37 != null && var3 != null) {
         double var38 = (var3.y - var37.y) * 0.05;
         var19 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
         if (var19 > 0.0) {
            this.velocityX = this.velocityX / var19 * (var19 + var38);
            this.velocityZ = this.velocityZ / var19 * (var19 + var38);
         }

         this.setPosition(this.x, var37.y, this.z);
      }

      int var58 = MathHelper.floor(this.x);
      int var39 = MathHelper.floor(this.z);
      if (var58 != x.getX() || var39 != x.getZ()) {
         var19 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
         this.velocityX = var19 * (double)(var58 - x.getX());
         this.velocityZ = var19 * (double)(var39 - x.getZ());
      }

      if (var4) {
         double var40 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
         if (var40 > 0.01) {
            double var42 = 0.06;
            this.velocityX += this.velocityX / var40 * var42;
            this.velocityZ += this.velocityZ / var40 * var42;
         } else if (var9 == AbstractRailBlock.Shape.EAST_WEST) {
            if (this.world.getBlockState(x.west()).getBlock().isConductor()) {
               this.velocityX = 0.02;
            } else if (this.world.getBlockState(x.east()).getBlock().isConductor()) {
               this.velocityX = -0.02;
            }
         } else if (var9 == AbstractRailBlock.Shape.NORTH_SOUTH) {
            if (this.world.getBlockState(x.north()).getBlock().isConductor()) {
               this.velocityZ = 0.02;
            } else if (this.world.getBlockState(x.south()).getBlock().isConductor()) {
               this.velocityZ = -0.02;
            }
         }
      }
   }

   protected void applySlowdown() {
      if (this.rider != null) {
         this.velocityX *= 0.997F;
         this.velocityY *= 0.0;
         this.velocityZ *= 0.997F;
      } else {
         this.velocityX *= 0.96F;
         this.velocityY *= 0.0;
         this.velocityZ *= 0.96F;
      }
   }

   @Override
   public void setPosition(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
      float var7 = this.width / 2.0F;
      float var8 = this.height;
      this.setHitbox(new Box(x - (double)var7, y, z - (double)var7, x + (double)var7, y + (double)var8, z + (double)var7));
   }

   @Environment(EnvType.CLIENT)
   public Vec3d snapPositionToRailWithOffset(double x, double y, double z, double offset) {
      int var9 = MathHelper.floor(x);
      int var10 = MathHelper.floor(y);
      int var11 = MathHelper.floor(z);
      if (AbstractRailBlock.isRail(this.world, new BlockPos(var9, var10 - 1, var11))) {
         --var10;
      }

      BlockState var12 = this.world.getBlockState(new BlockPos(var9, var10, var11));
      if (AbstractRailBlock.isRail(var12)) {
         AbstractRailBlock.Shape var13 = (AbstractRailBlock.Shape)var12.get(((AbstractRailBlock)var12.getBlock()).getShapeProperty());
         y = (double)var10;
         if (var13.isAscending()) {
            y = (double)(var10 + 1);
         }

         int[][] var14 = ADJACENT_RAIL_POSITIONS_BY_SHAPE[var13.getIndex()];
         double var15 = (double)(var14[1][0] - var14[0][0]);
         double var17 = (double)(var14[1][2] - var14[0][2]);
         double var19 = Math.sqrt(var15 * var15 + var17 * var17);
         var15 /= var19;
         var17 /= var19;
         x += var15 * offset;
         z += var17 * offset;
         if (var14[0][1] != 0 && MathHelper.floor(x) - var9 == var14[0][0] && MathHelper.floor(z) - var11 == var14[0][2]) {
            y += (double)var14[0][1];
         } else if (var14[1][1] != 0 && MathHelper.floor(x) - var9 == var14[1][0] && MathHelper.floor(z) - var11 == var14[1][2]) {
            y += (double)var14[1][1];
         }

         return this.snapPositionToRail(x, y, z);
      } else {
         return null;
      }
   }

   public Vec3d snapPositionToRail(double x, double y, double z) {
      int var7 = MathHelper.floor(x);
      int var8 = MathHelper.floor(y);
      int var9 = MathHelper.floor(z);
      if (AbstractRailBlock.isRail(this.world, new BlockPos(var7, var8 - 1, var9))) {
         --var8;
      }

      BlockState var10 = this.world.getBlockState(new BlockPos(var7, var8, var9));
      if (AbstractRailBlock.isRail(var10)) {
         AbstractRailBlock.Shape var11 = (AbstractRailBlock.Shape)var10.get(((AbstractRailBlock)var10.getBlock()).getShapeProperty());
         int[][] var12 = ADJACENT_RAIL_POSITIONS_BY_SHAPE[var11.getIndex()];
         double var13 = 0.0;
         double var15 = (double)var7 + 0.5 + (double)var12[0][0] * 0.5;
         double var17 = (double)var8 + 0.0625 + (double)var12[0][1] * 0.5;
         double var19 = (double)var9 + 0.5 + (double)var12[0][2] * 0.5;
         double var21 = (double)var7 + 0.5 + (double)var12[1][0] * 0.5;
         double var23 = (double)var8 + 0.0625 + (double)var12[1][1] * 0.5;
         double var25 = (double)var9 + 0.5 + (double)var12[1][2] * 0.5;
         double var27 = var21 - var15;
         double var29 = (var23 - var17) * 2.0;
         double var31 = var25 - var19;
         if (var27 == 0.0) {
            x = (double)var7 + 0.5;
            var13 = z - (double)var9;
         } else if (var31 == 0.0) {
            z = (double)var9 + 0.5;
            var13 = x - (double)var7;
         } else {
            double var33 = x - var15;
            double var35 = z - var19;
            var13 = (var33 * var27 + var35 * var31) * 2.0;
         }

         x = var15 + var27 * var13;
         y = var17 + var29 * var13;
         z = var19 + var31 * var13;
         if (var29 < 0.0) {
            ++y;
         }

         if (var29 > 0.0) {
            y += 0.5;
         }

         return new Vec3d(x, y, z);
      } else {
         return null;
      }
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
      if (nbt.getBoolean("CustomDisplayTile")) {
         if (nbt.isType("DisplayTile", 8)) {
            Block var2 = Block.byId(nbt.getString("DisplayTile"));
            if (var2 == null) {
               this.setDisplayBlock(Block.getRawId(Blocks.AIR));
            } else {
               this.setDisplayBlock(Block.getRawId(var2));
            }
         } else {
            this.setDisplayBlock(nbt.getInt("DisplayTile"));
         }

         this.setDisplayBlockMetadata(nbt.getInt("DisplayData"));
         this.setDisplayBlockOffset(nbt.getInt("DisplayOffset"));
      }

      if (nbt.isType("CustomName", 8) && nbt.getString("CustomName").length() > 0) {
         this.customName = nbt.getString("CustomName");
      }
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
      if (this.hasCustomDisplayBlock()) {
         nbt.putBoolean("CustomDisplayTile", true);
         Identifier var2 = (Identifier)Block.REGISTRY.getKey(this.getDisplayBlock());
         nbt.putString("DisplayTile", var2 == null ? "" : var2.toString());
         nbt.putInt("DisplayData", this.getDisplayBlockMetadata());
         nbt.putInt("DisplayOffset", this.getDisplayBlockOffset());
      }

      if (this.customName != null && this.customName.length() > 0) {
         nbt.putString("CustomName", this.customName);
      }
   }

   @Override
   public void push(Entity entity) {
      if (!this.world.isClient) {
         if (!entity.noClip && !this.noClip) {
            if (entity != this.rider) {
               if (entity instanceof LivingEntity
                  && !(entity instanceof PlayerEntity)
                  && !(entity instanceof IronGolemEntity)
                  && this.getMinecartType() == MinecartEntity.Type.RIDEABLE
                  && this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.01
                  && this.rider == null
                  && entity.vehicle == null) {
                  entity.startRiding(this);
               }

               double var2 = entity.x - this.x;
               double var4 = entity.z - this.z;
               double var6 = var2 * var2 + var4 * var4;
               if (var6 >= 1.0E-4F) {
                  var6 = (double)MathHelper.sqrt(var6);
                  var2 /= var6;
                  var4 /= var6;
                  double var8 = 1.0 / var6;
                  if (var8 > 1.0) {
                     var8 = 1.0;
                  }

                  var2 *= var8;
                  var4 *= var8;
                  var2 *= 0.1F;
                  var4 *= 0.1F;
                  var2 *= (double)(1.0F - this.pushSpeedReduction);
                  var4 *= (double)(1.0F - this.pushSpeedReduction);
                  var2 *= 0.5;
                  var4 *= 0.5;
                  if (entity instanceof MinecartEntity) {
                     double var10 = entity.x - this.x;
                     double var12 = entity.z - this.z;
                     Vec3d var14 = new Vec3d(var10, 0.0, var12).normalize();
                     Vec3d var15 = new Vec3d(
                           (double)MathHelper.cos(this.yaw * (float) Math.PI / 180.0F), 0.0, (double)MathHelper.sin(this.yaw * (float) Math.PI / 180.0F)
                        )
                        .normalize();
                     double var16 = Math.abs(var14.dot(var15));
                     if (var16 < 0.8F) {
                        return;
                     }

                     double var18 = entity.velocityX + this.velocityX;
                     double var20 = entity.velocityZ + this.velocityZ;
                     if (((MinecartEntity)entity).getMinecartType() == MinecartEntity.Type.FURNACE && this.getMinecartType() != MinecartEntity.Type.FURNACE) {
                        this.velocityX *= 0.2F;
                        this.velocityZ *= 0.2F;
                        this.addVelocity(entity.velocityX - var2, 0.0, entity.velocityZ - var4);
                        entity.velocityX *= 0.95F;
                        entity.velocityZ *= 0.95F;
                     } else if (((MinecartEntity)entity).getMinecartType() != MinecartEntity.Type.FURNACE
                        && this.getMinecartType() == MinecartEntity.Type.FURNACE) {
                        entity.velocityX *= 0.2F;
                        entity.velocityZ *= 0.2F;
                        entity.addVelocity(this.velocityX + var2, 0.0, this.velocityZ + var4);
                        this.velocityX *= 0.95F;
                        this.velocityZ *= 0.95F;
                     } else {
                        var18 /= 2.0;
                        var20 /= 2.0;
                        this.velocityX *= 0.2F;
                        this.velocityZ *= 0.2F;
                        this.addVelocity(var18 - var2, 0.0, var20 - var4);
                        entity.velocityX *= 0.2F;
                        entity.velocityZ *= 0.2F;
                        entity.addVelocity(var18 + var2, 0.0, var20 + var4);
                     }
                  } else {
                     this.addVelocity(-var2, 0.0, -var4);
                     entity.addVelocity(var2 / 4.0, 0.0, var4 / 4.0);
                  }
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch, int i, boolean bl) {
      this.clientX = x;
      this.clientY = y;
      this.clientZ = z;
      this.clientYaw = (double)yaw;
      this.clientPitch = (double)pitch;
      this.clientInterpolationSteps = i + 2;
      this.velocityX = this.clientXVelocity;
      this.velocityY = this.clientYVelocity;
      this.velocityZ = this.clientZVelocity;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setVelocity(double velocityX, double velocityY, double velocityZ) {
      this.clientXVelocity = this.velocityX = velocityX;
      this.clientYVelocity = this.velocityY = velocityY;
      this.clientZVelocity = this.velocityZ = velocityZ;
   }

   public void setDamageWobbleStrength(float strength) {
      this.dataTracker.update(19, strength);
   }

   public float getDamageWobbleStrength() {
      return this.dataTracker.getFloat(19);
   }

   public void setDamageWobbleTicks(int ticks) {
      this.dataTracker.update(17, ticks);
   }

   public int getDamageWobbleTicks() {
      return this.dataTracker.getInt(17);
   }

   public void setDamageWobbleSide(int ticks) {
      this.dataTracker.update(18, ticks);
   }

   public int getDamageWobbleSide() {
      return this.dataTracker.getInt(18);
   }

   public abstract MinecartEntity.Type getMinecartType();

   public Block getDisplayBlock() {
      if (!this.hasCustomDisplayBlock()) {
         return this.getDefaultDisplayBlock();
      } else {
         int var1 = this.getDataTracker().getInt(20) & 65535;
         return Block.byRawId(var1);
      }
   }

   public Block getDefaultDisplayBlock() {
      return Blocks.AIR;
   }

   public int getDisplayBlockMetadata() {
      return !this.hasCustomDisplayBlock() ? this.getDefaultDisplayBlockMetadata() : this.getDataTracker().getInt(20) >> 16;
   }

   public int getDefaultDisplayBlockMetadata() {
      return 0;
   }

   public int getDisplayBlockOffset() {
      return !this.hasCustomDisplayBlock() ? this.getDefaultDisplayBlockOffset() : this.getDataTracker().getInt(21);
   }

   public int getDefaultDisplayBlockOffset() {
      return 6;
   }

   public void setDisplayBlock(int blockId) {
      this.getDataTracker().update(20, blockId & 65535 | this.getDisplayBlockMetadata() << 16);
      this.setHasCustomDisplayBlock(true);
   }

   public void setDisplayBlockMetadata(int metadata) {
      this.getDataTracker().update(20, Block.getRawId(this.getDisplayBlock()) & 65535 | metadata << 16);
      this.setHasCustomDisplayBlock(true);
   }

   public void setDisplayBlockOffset(int offset) {
      this.getDataTracker().update(21, offset);
      this.setHasCustomDisplayBlock(true);
   }

   public boolean hasCustomDisplayBlock() {
      return this.getDataTracker().getByte(22) == 1;
   }

   public void setHasCustomDisplayBlock(boolean hasCustomDisplayBlock) {
      this.getDataTracker().update(22, (byte)(hasCustomDisplayBlock ? 1 : 0));
   }

   @Override
   public void setCustomName(String name) {
      this.customName = name;
   }

   @Override
   public String getName() {
      return this.customName != null ? this.customName : super.getName();
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null;
   }

   @Override
   public String getCustomName() {
      return this.customName;
   }

   @Override
   public Text getDisplayName() {
      if (this.hasCustomName()) {
         LiteralText var2 = new LiteralText(this.customName);
         var2.getStyle().setHoverEvent(this.getHoverEvent());
         var2.getStyle().setInsertion(this.getUuid().toString());
         return var2;
      } else {
         TranslatableText var1 = new TranslatableText(this.getName());
         var1.getStyle().setHoverEvent(this.getHoverEvent());
         var1.getStyle().setInsertion(this.getUuid().toString());
         return var1;
      }
   }

   public static enum Type {
      RIDEABLE(0, "MinecartRideable"),
      CHEST(1, "MinecartChest"),
      FURNACE(2, "MinecartFurnace"),
      TNT(3, "MinecartTNT"),
      SPAWNER(4, "MinecartSpawner"),
      HOPPER(5, "MinecartHopper"),
      COMMAND_BLOCK(6, "MinecartCommandBlock");

      private static final Map BY_INDEX = Maps.newHashMap();
      private final int index;
      private final String name;

      private Type(int index, String name) {
         this.index = index;
         this.name = name;
      }

      public int getIndex() {
         return this.index;
      }

      public String getName() {
         return this.name;
      }

      public static MinecartEntity.Type byIndex(int index) {
         MinecartEntity.Type var1 = (MinecartEntity.Type)BY_INDEX.get(index);
         return var1 == null ? RIDEABLE : var1;
      }

      static {
         for(MinecartEntity.Type var3 : values()) {
            BY_INDEX.put(var3.getIndex(), var3);
         }
      }
   }
}
