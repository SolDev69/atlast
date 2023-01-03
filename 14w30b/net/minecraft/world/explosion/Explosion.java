package net.minecraft.world.explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class Explosion {
   private final boolean createFire;
   private final boolean destructive;
   private final Random random = new Random();
   private final World world;
   private final double x;
   private final double y;
   private final double z;
   private final Entity source;
   private final float power;
   private final List damagedBlocks = Lists.newArrayList();
   private final Map damagedPlayers = Maps.newHashMap();

   @Environment(EnvType.CLIENT)
   public Explosion(World world, Entity source, double power, double x, double y, float z, List damagedBlocks) {
      this(world, source, power, x, y, z, false, true, damagedBlocks);
   }

   @Environment(EnvType.CLIENT)
   public Explosion(World world, Entity source, double power, double x, double y, float z, boolean createFire, boolean destructive, List damagedBlocks) {
      this(world, source, power, x, y, z, createFire, destructive);
      this.damagedBlocks.addAll(damagedBlocks);
   }

   public Explosion(World world, Entity source, double x, double y, double z, float power, boolean createFire, boolean destructive) {
      this.world = world;
      this.source = source;
      this.power = power;
      this.x = x;
      this.y = y;
      this.z = z;
      this.createFire = createFire;
      this.destructive = destructive;
   }

   public void damageEntities() {
      HashSet var1 = Sets.newHashSet();
      boolean var2 = true;

      for(int var3 = 0; var3 < 16; ++var3) {
         for(int var4 = 0; var4 < 16; ++var4) {
            for(int var5 = 0; var5 < 16; ++var5) {
               if (var3 == 0 || var3 == 15 || var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15) {
                  double var6 = (double)((float)var3 / 15.0F * 2.0F - 1.0F);
                  double var8 = (double)((float)var4 / 15.0F * 2.0F - 1.0F);
                  double var10 = (double)((float)var5 / 15.0F * 2.0F - 1.0F);
                  double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
                  var6 /= var12;
                  var8 /= var12;
                  var10 /= var12;
                  float var14 = this.power * (0.7F + this.world.random.nextFloat() * 0.6F);
                  double var15 = this.x;
                  double var17 = this.y;
                  double var19 = this.z;

                  for(float var21 = 0.3F; var14 > 0.0F; var14 -= 0.22500001F) {
                     BlockPos var22 = new BlockPos(var15, var17, var19);
                     BlockState var23 = this.world.getBlockState(var22);
                     if (var23.getBlock().getMaterial() != Material.AIR) {
                        float var24 = this.source != null
                           ? this.source.getBlastResistance(this, this.world, var22, var23)
                           : var23.getBlock().getBlastResistance(null);
                        var14 -= (var24 + 0.3F) * 0.3F;
                     }

                     if (var14 > 0.0F && (this.source == null || this.source.canExplodeBlock(this, this.world, var22, var23, var14))) {
                        var1.add(var22);
                     }

                     var15 += var6 * 0.3F;
                     var17 += var8 * 0.3F;
                     var19 += var10 * 0.3F;
                  }
               }
            }
         }
      }

      this.damagedBlocks.addAll(var1);
      float var30 = this.power * 2.0F;
      int var31 = MathHelper.floor(this.x - (double)var30 - 1.0);
      int var32 = MathHelper.floor(this.x + (double)var30 + 1.0);
      int var34 = MathHelper.floor(this.y - (double)var30 - 1.0);
      int var7 = MathHelper.floor(this.y + (double)var30 + 1.0);
      int var36 = MathHelper.floor(this.z - (double)var30 - 1.0);
      int var9 = MathHelper.floor(this.z + (double)var30 + 1.0);
      List var38 = this.world.getEntities(this.source, new Box((double)var31, (double)var34, (double)var36, (double)var32, (double)var7, (double)var9));
      Vec3d var11 = new Vec3d(this.x, this.y, this.z);

      for(int var39 = 0; var39 < var38.size(); ++var39) {
         Entity var13 = (Entity)var38.get(var39);
         double var40 = var13.getDistanceTo(this.x, this.y, this.z) / (double)var30;
         if (var40 <= 1.0) {
            double var16 = var13.x - this.x;
            double var18 = var13.y + (double)var13.getEyeHeight() - this.y;
            double var20 = var13.z - this.z;
            double var44 = (double)MathHelper.sqrt(var16 * var16 + var18 * var18 + var20 * var20);
            if (var44 != 0.0) {
               var16 /= var44;
               var18 /= var44;
               var20 /= var44;
               double var45 = (double)this.world.getBlockDensity(var11, var13.getBoundingBox());
               double var26 = (1.0 - var40) * var45;
               var13.damage(DamageSource.explosion(this), (float)((int)((var26 * var26 + var26) / 2.0 * 8.0 * (double)var30 + 1.0)));
               double var28 = ProtectionEnchantment.modifyExplosionDamage(var13, var26);
               var13.velocityX += var16 * var28;
               var13.velocityY += var18 * var28;
               var13.velocityZ += var20 * var28;
               if (var13 instanceof PlayerEntity) {
                  this.damagedPlayers.put((PlayerEntity)var13, new Vec3d(var16 * var26, var18 * var26, var20 * var26));
               }
            }
         }
      }
   }

   public void damageBlocks(boolean createFire) {
      this.world
         .playSound(this.x, this.y, this.z, "random.explode", 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);
      if (!(this.power < 2.0F) && this.destructive) {
         this.world.addParticle(ParticleType.EXPLOSION_HUGE, this.x, this.y, this.z, 1.0, 0.0, 0.0);
      } else {
         this.world.addParticle(ParticleType.EXPLOSION_LARGE, this.x, this.y, this.z, 1.0, 0.0, 0.0);
      }

      if (this.destructive) {
         for(BlockPos var3 : this.damagedBlocks) {
            Block var4 = this.world.getBlockState(var3).getBlock();
            if (createFire) {
               double var5 = (double)((float)var3.getX() + this.world.random.nextFloat());
               double var7 = (double)((float)var3.getY() + this.world.random.nextFloat());
               double var9 = (double)((float)var3.getZ() + this.world.random.nextFloat());
               double var11 = var5 - this.x;
               double var13 = var7 - this.y;
               double var15 = var9 - this.z;
               double var17 = (double)MathHelper.sqrt(var11 * var11 + var13 * var13 + var15 * var15);
               var11 /= var17;
               var13 /= var17;
               var15 /= var17;
               double var19 = 0.5 / (var17 / (double)this.power + 0.1);
               var19 *= (double)(this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
               var11 *= var19;
               var13 *= var19;
               var15 *= var19;
               this.world
                  .addParticle(
                     ParticleType.EXPLOSION_NORMAL, (var5 + this.x * 1.0) / 2.0, (var7 + this.y * 1.0) / 2.0, (var9 + this.z * 1.0) / 2.0, var11, var13, var15
                  );
               this.world.addParticle(ParticleType.SMOKE_NORMAL, var5, var7, var9, var11, var13, var15);
            }

            if (var4.getMaterial() != Material.AIR) {
               if (var4.shouldDropItemsOnExplosion(this)) {
                  var4.dropItems(this.world, var3, this.world.getBlockState(var3), 1.0F / this.power, 0);
               }

               this.world.setBlockState(var3, Blocks.AIR.defaultState(), 3);
               var4.onExploded(this.world, var3, this);
            }
         }
      }

      if (this.createFire) {
         for(BlockPos var22 : this.damagedBlocks) {
            if (this.world.getBlockState(var22).getBlock().getMaterial() == Material.AIR
               && this.world.getBlockState(var22.down()).getBlock().isOpaque()
               && this.random.nextInt(3) == 0) {
               this.world.setBlockState(var22, Blocks.FIRE.defaultState());
            }
         }
      }
   }

   public Map getDamagedPlayers() {
      return this.damagedPlayers;
   }

   public LivingEntity getSource() {
      if (this.source == null) {
         return null;
      } else if (this.source instanceof PrimedTntEntity) {
         return ((PrimedTntEntity)this.source).getIgniter();
      } else {
         return this.source instanceof LivingEntity ? (LivingEntity)this.source : null;
      }
   }

   public void clearDamagedBlocks() {
      this.damagedBlocks.clear();
   }

   public List getDamagedBlocks() {
      return this.damagedBlocks;
   }
}
