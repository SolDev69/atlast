package net.minecraft.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.resource.Identifier;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FallingBlockEntity extends Entity {
   private BlockState state;
   public int time;
   public boolean dropping = true;
   private boolean brokeAnvil;
   private boolean hurtingEntities;
   private int maxFallHurt = 40;
   private float fallHurtAmount = 2.0F;
   public NbtCompound nbt;

   public FallingBlockEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public FallingBlockEntity(World world, double x, double y, double z, BlockState state) {
      super(world);
      this.state = state;
      this.blocksBuilding = true;
      this.setDimensions(0.98F, 0.98F);
      this.setPosition(x, y, z);
      this.velocityX = 0.0;
      this.velocityY = 0.0;
      this.velocityZ = 0.0;
      this.prevX = x;
      this.prevY = y;
      this.prevZ = z;
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   protected void initDataTracker() {
   }

   @Override
   public boolean hasCollision() {
      return !this.removed;
   }

   @Override
   public void tick() {
      Block var1 = this.state.getBlock();
      if (var1.getMaterial() == Material.AIR) {
         this.remove();
      } else {
         this.prevX = this.x;
         this.prevY = this.y;
         this.prevZ = this.z;
         if (this.time++ == 0) {
            BlockPos var2 = new BlockPos(this);
            if (this.world.getBlockState(var2).getBlock() == var1) {
               this.world.removeBlock(var2);
            } else if (!this.world.isClient) {
               this.remove();
               return;
            }
         }

         this.velocityY -= 0.04F;
         this.move(this.velocityX, this.velocityY, this.velocityZ);
         this.velocityX *= 0.98F;
         this.velocityY *= 0.98F;
         this.velocityZ *= 0.98F;
         if (!this.world.isClient) {
            BlockPos var8 = new BlockPos(this);
            if (this.onGround) {
               this.velocityX *= 0.7F;
               this.velocityZ *= 0.7F;
               this.velocityY *= -0.5;
               if (this.world.getBlockState(var8).getBlock() != Blocks.MOVING_BLOCK) {
                  this.remove();
                  if (!this.brokeAnvil
                     && this.world.canReplace(var1, var8, true, Direction.UP, null, null)
                     && !FallingBlock.canFallThrough(this.world, var8.down())
                     && this.world.setBlockState(var8, this.state, 3)) {
                     if (var1 instanceof FallingBlock) {
                        ((FallingBlock)var1).onTickFallingBlockEntity(this.world, var8);
                     }

                     if (this.nbt != null && var1 instanceof BlockEntityProvider) {
                        BlockEntity var3 = this.world.getBlockEntity(var8);
                        if (var3 != null) {
                           NbtCompound var4 = new NbtCompound();
                           var3.writeNbt(var4);

                           for(String var6 : this.nbt.getKeys()) {
                              NbtElement var7 = this.nbt.get(var6);
                              if (!var6.equals("x") && !var6.equals("y") && !var6.equals("z")) {
                                 var4.put(var6, var7.copy());
                              }
                           }

                           var3.readNbt(var4);
                           var3.markDirty();
                        }
                     }
                  } else if (this.dropping && !this.brokeAnvil && this.world.getGameRules().getBoolean("doTileDrops")) {
                     this.dropItem(new ItemStack(var1, 1, var1.getDropItemMetadata(this.state)), 0.0F);
                  }
               }
            } else if (this.time > 100 && !this.world.isClient && (var8.getY() < 1 || var8.getY() > 256) || this.time > 600) {
               if (this.dropping && this.world.getGameRules().getBoolean("doTileDrops")) {
                  this.dropItem(new ItemStack(var1, 1, var1.getDropItemMetadata(this.state)), 0.0F);
               }

               this.remove();
            }
         }
      }
   }

   @Override
   public void applyFallDamage(float distance, float g) {
      Block var3 = this.state.getBlock();
      if (this.hurtingEntities) {
         int var4 = MathHelper.ceil(distance - 1.0F);
         if (var4 > 0) {
            ArrayList var5 = Lists.newArrayList(this.world.getEntities(this, this.getBoundingBox()));
            boolean var6 = var3 == Blocks.ANVIL;
            DamageSource var7 = var6 ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

            for(Entity var9 : var5) {
               var9.damage(var7, (float)Math.min(MathHelper.floor((float)var4 * this.fallHurtAmount), this.maxFallHurt));
            }

            if (var6 && (double)this.random.nextFloat() < 0.05F + (double)var4 * 0.05) {
               int var10 = this.state.get(AnvilBlock.DAMAGE);
               if (++var10 > 2) {
                  this.brokeAnvil = true;
               } else {
                  this.state = this.state.set(AnvilBlock.DAMAGE, var10);
               }
            }
         }
      }
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
      Block var2 = this.state != null ? this.state.getBlock() : Blocks.AIR;
      Identifier var3 = (Identifier)Block.REGISTRY.getKey(var2);
      nbt.putString("Block", var3 == null ? "" : var3.toString());
      nbt.putByte("Data", (byte)var2.getMetadataFromState(this.state));
      nbt.putByte("Time", (byte)this.time);
      nbt.putBoolean("DropItem", this.dropping);
      nbt.putBoolean("HurtEntities", this.hurtingEntities);
      nbt.putFloat("FallHurtAmount", this.fallHurtAmount);
      nbt.putInt("FallHurtMax", this.maxFallHurt);
      if (this.nbt != null) {
         nbt.put("TileEntityData", this.nbt);
      }
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
      int var2 = nbt.getByte("Data") & 255;
      if (nbt.isType("Block", 8)) {
         this.state = Block.byId(nbt.getString("Block")).getStateFromMetadata(var2);
      } else if (nbt.isType("TileID", 99)) {
         this.state = Block.byRawId(nbt.getInt("TileID")).getStateFromMetadata(var2);
      } else {
         this.state = Block.byRawId(nbt.getByte("Tile") & 255).getStateFromMetadata(var2);
      }

      this.time = nbt.getByte("Time") & 255;
      Block var3 = this.state.getBlock();
      if (nbt.isType("HurtEntities", 99)) {
         this.hurtingEntities = nbt.getBoolean("HurtEntities");
         this.fallHurtAmount = nbt.getFloat("FallHurtAmount");
         this.maxFallHurt = nbt.getInt("FallHurtMax");
      } else if (var3 == Blocks.ANVIL) {
         this.hurtingEntities = true;
      }

      if (nbt.isType("DropItem", 99)) {
         this.dropping = nbt.getBoolean("DropItem");
      }

      if (nbt.isType("TileEntityData", 10)) {
         this.nbt = nbt.getCompound("TileEntityData");
      }

      if (var3 == null || var3.getMaterial() == Material.AIR) {
         this.state = Blocks.SAND.defaultState();
      }
   }

   @Environment(EnvType.CLIENT)
   public World getWorld() {
      return this.world;
   }

   public void setHurtingEntities(boolean hurtingEntities) {
      this.hurtingEntities = hurtingEntities;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderOnFire() {
      return false;
   }

   @Override
   public void populateCrashReport(CashReportCategory section) {
      super.populateCrashReport(section);
      if (this.state != null) {
         Block var2 = this.state.getBlock();
         section.add("Immitating block ID", Block.getRawId(var2));
         section.add("Immitating block data", var2.getMetadataFromState(this.state));
      }
   }

   public BlockState getBlock() {
      return this.state;
   }
}
