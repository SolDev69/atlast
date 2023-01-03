package net.minecraft;

import com.mojang.authlib.GameProfile;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.dispenser.DispenseBehavior;
import net.minecraft.block.dispenser.DispenseItemBehavior;
import net.minecraft.block.dispenser.DispenseProjectileBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Dispensable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FireworksEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.IPosition;
import net.minecraft.world.IBlockSource;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   private static boolean initialized = false;
   private static final Logger LOGGER = LogManager.getLogger();

   public static boolean isInitialized() {
      return initialized;
   }

   static void registerDispenseBehaviors() {
      DispenserBlock.BEHAVIORS.put(Items.ARROW, new DispenseProjectileBehavior() {
         @Override
         protected Dispensable createProjectile(World world, IPosition pos) {
            ArrowEntity var3 = new ArrowEntity(world, pos.getX(), pos.getY(), pos.getZ());
            var3.pickup = 1;
            return var3;
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.EGG, new DispenseProjectileBehavior() {
         @Override
         protected Dispensable createProjectile(World world, IPosition pos) {
            return new EggEntity(world, pos.getX(), pos.getY(), pos.getZ());
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.SNOWBALL, new DispenseProjectileBehavior() {
         @Override
         protected Dispensable createProjectile(World world, IPosition pos) {
            return new SnowballEntity(world, pos.getX(), pos.getY(), pos.getZ());
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.EXPERIENCE_BOTTLE, new DispenseProjectileBehavior() {
         @Override
         protected Dispensable createProjectile(World world, IPosition pos) {
            return new ExperienceBottleEntity(world, pos.getX(), pos.getY(), pos.getZ());
         }

         @Override
         protected float getVariation() {
            return super.getVariation() * 0.5F;
         }

         @Override
         protected float getForce() {
            return super.getForce() * 1.25F;
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.POTION, new DispenseBehavior() {
         private final DispenseItemBehavior normal = new DispenseItemBehavior();

         @Override
         public ItemStack dispense(IBlockSource source, ItemStack stack) {
            return PotionItem.isSplashPotion(stack.getMetadata()) ? (new DispenseProjectileBehavior() {
               @Override
               protected Dispensable createProjectile(World world, IPosition pos) {
                  return new PotionEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copy());
               }

               @Override
               protected float getVariation() {
                  return super.getVariation() * 0.5F;
               }

               @Override
               protected float getForce() {
                  return super.getForce() * 1.25F;
               }
            }).dispense(source, stack) : this.normal.dispense(source, stack);
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.SPAWN_EGG, new DispenseItemBehavior() {
         @Override
         public ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            Direction var3 = DispenserBlock.getDirection(source.getBlockMetadata());
            double var4 = source.getX() + (double)var3.getOffsetX();
            double var6 = (double)((float)source.getPos().getY() + 0.2F);
            double var8 = source.getZ() + (double)var3.getOffsetZ();
            Entity var10 = SpawnEggItem.spawnEntity(source.getWorld(), stack.getMetadata(), var4, var6, var8);
            if (var10 instanceof LivingEntity && stack.hasCustomHoverName()) {
               ((MobEntity)var10).setCustomName(stack.getHoverName());
            }

            stack.split(1);
            return stack;
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.FIREWORKS, new DispenseItemBehavior() {
         @Override
         public ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            Direction var3 = DispenserBlock.getDirection(source.getBlockMetadata());
            double var4 = source.getX() + (double)var3.getOffsetX();
            double var6 = (double)((float)source.getPos().getY() + 0.2F);
            double var8 = source.getZ() + (double)var3.getOffsetZ();
            FireworksEntity var10 = new FireworksEntity(source.getWorld(), var4, var6, var8, stack);
            source.getWorld().addEntity(var10);
            stack.split(1);
            return stack;
         }

         @Override
         protected void playSound(IBlockSource source) {
            source.getWorld().doEvent(1002, source.getPos(), 0);
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.FIRE_CHARGE, new DispenseItemBehavior() {
         @Override
         public ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            Direction var3 = DispenserBlock.getDirection(source.getBlockMetadata());
            IPosition var4 = DispenserBlock.getDispensePos(source);
            double var5 = var4.getX() + (double)((float)var3.getOffsetX() * 0.3F);
            double var7 = var4.getY() + (double)((float)var3.getOffsetX() * 0.3F);
            double var9 = var4.getZ() + (double)((float)var3.getOffsetZ() * 0.3F);
            World var11 = source.getWorld();
            Random var12 = var11.random;
            double var13 = var12.nextGaussian() * 0.05 + (double)var3.getOffsetX();
            double var15 = var12.nextGaussian() * 0.05 + (double)var3.getOffsetY();
            double var17 = var12.nextGaussian() * 0.05 + (double)var3.getOffsetZ();
            var11.addEntity(new SmallFireballEntity(var11, var5, var7, var9, var13, var15, var17));
            stack.split(1);
            return stack;
         }

         @Override
         protected void playSound(IBlockSource source) {
            source.getWorld().doEvent(1009, source.getPos(), 0);
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.BOAT, new DispenseItemBehavior() {
         private final DispenseItemBehavior normal = new DispenseItemBehavior();

         @Override
         public ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            Direction var3 = DispenserBlock.getDirection(source.getBlockMetadata());
            World var4 = source.getWorld();
            double var5 = source.getX() + (double)((float)var3.getOffsetX() * 1.125F);
            double var7 = source.getY() + (double)((float)var3.getOffsetY() * 1.125F);
            double var9 = source.getZ() + (double)((float)var3.getOffsetZ() * 1.125F);
            BlockPos var11 = source.getPos().offset(var3);
            Material var12 = var4.getBlockState(var11).getBlock().getMaterial();
            double var13;
            if (Material.WATER.equals(var12)) {
               var13 = 1.0;
            } else {
               if (!Material.AIR.equals(var12) || !Material.WATER.equals(var4.getBlockState(var11.down()).getBlock().getMaterial())) {
                  return this.normal.dispense(source, stack);
               }

               var13 = 0.0;
            }

            BoatEntity var15 = new BoatEntity(var4, var5, var7 + var13, var9);
            var4.addEntity(var15);
            stack.split(1);
            return stack;
         }

         @Override
         protected void playSound(IBlockSource source) {
            source.getWorld().doEvent(1000, source.getPos(), 0);
         }
      });
      DispenseItemBehavior var0 = new DispenseItemBehavior() {
         private final DispenseItemBehavior normal = new DispenseItemBehavior();

         @Override
         public ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            BucketItem var3 = (BucketItem)stack.getItem();
            BlockPos var4 = source.getPos().offset(DispenserBlock.getDirection(source.getBlockMetadata()));
            if (var3.place(source.getWorld(), var4)) {
               stack.setItem(Items.BUCKET);
               stack.size = 1;
               return stack;
            } else {
               return this.normal.dispense(source, stack);
            }
         }
      };
      DispenserBlock.BEHAVIORS.put(Items.LAVA_BUCKET, var0);
      DispenserBlock.BEHAVIORS.put(Items.WATER_BUCKET, var0);
      DispenserBlock.BEHAVIORS.put(Items.BUCKET, new DispenseItemBehavior() {
         private final DispenseItemBehavior normal = new DispenseItemBehavior();

         @Override
         public ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            World var3 = source.getWorld();
            BlockPos var4 = source.getPos().offset(DispenserBlock.getDirection(source.getBlockMetadata()));
            BlockState var5 = var3.getBlockState(var4);
            Block var6 = var5.getBlock();
            Material var7 = var6.getMaterial();
            Item var8;
            if (Material.WATER.equals(var7) && var6 instanceof LiquidBlock && var5.get(LiquidBlock.LEVEL) == 0) {
               var8 = Items.WATER_BUCKET;
            } else {
               if (!Material.LAVA.equals(var7) || !(var6 instanceof LiquidBlock) || var5.get(LiquidBlock.LEVEL) != 0) {
                  return super.dispenseItem(source, stack);
               }

               var8 = Items.LAVA_BUCKET;
            }

            var3.removeBlock(var4);
            if (--stack.size == 0) {
               stack.setItem(var8);
               stack.size = 1;
            } else if (((DispenserBlockEntity)source.getBlockEntity()).insertStack(new ItemStack(var8)) < 0) {
               this.normal.dispense(source, new ItemStack(var8));
            }

            return stack;
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.FLINT_AND_STEEL, new DispenseItemBehavior() {
         private boolean ignited = true;

         @Override
         protected ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            World var3 = source.getWorld();
            BlockPos var4 = source.getPos().offset(DispenserBlock.getDirection(source.getBlockMetadata()));
            if (var3.isAir(var4)) {
               var3.setBlockState(var4, Blocks.FIRE.defaultState());
               if (stack.damage(1, var3.random)) {
                  stack.size = 0;
               }
            } else if (var3.getBlockState(var4).getBlock() == Blocks.TNT) {
               Blocks.TNT.onBroken(var3, var4, Blocks.TNT.defaultState().set(TntBlock.EXPLODE, true));
               var3.removeBlock(var4);
            } else {
               this.ignited = false;
            }

            return stack;
         }

         @Override
         protected void playSound(IBlockSource source) {
            if (this.ignited) {
               source.getWorld().doEvent(1000, source.getPos(), 0);
            } else {
               source.getWorld().doEvent(1001, source.getPos(), 0);
            }
         }
      });
      DispenserBlock.BEHAVIORS.put(Items.DYE, new DispenseItemBehavior() {
         private boolean dyed = true;

         @Override
         protected ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            if (DyeColor.WHITE == DyeColor.byMetadata(stack.getMetadata())) {
               World var3 = source.getWorld();
               BlockPos var4 = source.getPos().offset(DispenserBlock.getDirection(source.getBlockMetadata()));
               if (DyeItem.fertilize(stack, var3, var4)) {
                  if (!var3.isClient) {
                     var3.doEvent(2005, var4, 0);
                  }
               } else {
                  this.dyed = false;
               }

               return stack;
            } else {
               return super.dispenseItem(source, stack);
            }
         }

         @Override
         protected void playSound(IBlockSource source) {
            if (this.dyed) {
               source.getWorld().doEvent(1000, source.getPos(), 0);
            } else {
               source.getWorld().doEvent(1001, source.getPos(), 0);
            }
         }
      });
      DispenserBlock.BEHAVIORS
         .put(
            Item.byBlock(Blocks.TNT),
            new DispenseItemBehavior() {
               @Override
               protected ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
                  World var3 = source.getWorld();
                  BlockPos var4 = source.getPos().offset(DispenserBlock.getDirection(source.getBlockMetadata()));
                  PrimedTntEntity var5 = new PrimedTntEntity(
                     var3, (double)((float)var4.getX() + 0.5F), (double)((float)var4.getY() + 0.5F), (double)((float)var4.getZ() + 0.5F), null
                  );
                  var3.addEntity(var5);
                  var3.playSound(var5, "game.tnt.primed", 1.0F, 1.0F);
                  --stack.size;
                  return stack;
               }
            }
         );
      DispenserBlock.BEHAVIORS.put(Items.SKULL, new DispenseItemBehavior() {
         private boolean spawnedWither = true;

         @Override
         protected ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            World var3 = source.getWorld();
            Direction var4 = DispenserBlock.getDirection(source.getBlockMetadata());
            BlockPos var5 = source.getPos().offset(var4);
            SkullBlock var6 = Blocks.SKULL;
            if (!var3.isAir(var5) || !var6.canSpawnWither(var3, var5, stack)) {
               this.spawnedWither = false;
            } else if (!var3.isClient) {
               var3.setBlockState(var5, var6.defaultState().set(SkullBlock.FACING, Direction.UP), 3);
               BlockEntity var7 = var3.getBlockEntity(var5);
               if (var7 instanceof SkullBlockEntity) {
                  if (stack.getMetadata() == 3) {
                     GameProfile var8 = null;
                     if (stack.hasNbt()) {
                        NbtCompound var9 = stack.getNbt();
                        if (var9.isType("SkullOwner", 10)) {
                           var8 = NbtUtils.readProfile(var9.getCompound("SkullOwner"));
                        } else if (var9.isType("SkullOwner", 8)) {
                           var8 = new GameProfile(null, var9.getString("SkullOwner"));
                        }
                     }

                     ((SkullBlockEntity)var7).setProfile(var8);
                  } else {
                     ((SkullBlockEntity)var7).setSkullType(stack.getMetadata());
                  }

                  ((SkullBlockEntity)var7).setRotation(var4.getOpposite().getIdHorizontal() * 4);
                  Blocks.SKULL.trySpawnWither(var3, var5, (SkullBlockEntity)var7);
               }

               --stack.size;
            }

            return stack;
         }

         @Override
         protected void playSound(IBlockSource source) {
            if (this.spawnedWither) {
               source.getWorld().doEvent(1000, source.getPos(), 0);
            } else {
               source.getWorld().doEvent(1001, source.getPos(), 0);
            }
         }
      });
      DispenserBlock.BEHAVIORS.put(Item.byBlock(Blocks.PUMPKIN), new DispenseItemBehavior() {
         private boolean spawnedGolem = true;

         @Override
         protected ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            World var3 = source.getWorld();
            BlockPos var4 = source.getPos().offset(DispenserBlock.getDirection(source.getBlockMetadata()));
            PumpkinBlock var5 = (PumpkinBlock)Blocks.PUMPKIN;
            if (var3.isAir(var4) && var5.canSpawnGolem(var3, var4)) {
               if (!var3.isClient) {
                  var3.setBlockState(var4, var5.defaultState(), 3);
               }

               --stack.size;
            } else {
               this.spawnedGolem = false;
            }

            return stack;
         }

         @Override
         protected void playSound(IBlockSource source) {
            if (this.spawnedGolem) {
               source.getWorld().doEvent(1000, source.getPos(), 0);
            } else {
               source.getWorld().doEvent(1001, source.getPos(), 0);
            }
         }
      });
      DispenserBlock.BEHAVIORS.put(Item.byBlock(Blocks.COMMAND_BLOCK), new DispenseItemBehavior() {
         @Override
         protected ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
            World var3 = source.getWorld();
            BlockPos var4 = source.getPos().offset(DispenserBlock.getDirection(source.getBlockMetadata()));
            if (var3.isAir(var4)) {
               if (!var3.isClient) {
                  BlockState var5 = Blocks.COMMAND_BLOCK.defaultState().set(CommandBlock.TRIGGERED, false);
                  var3.setBlockState(var4, var5, 3);
                  BlockItem.setBlockNbt(var3, var4, stack);
                  var3.updateNeighbors(source.getPos(), source.getBlock());
               }

               --stack.size;
            }

            return stack;
         }

         @Override
         protected void playSound(IBlockSource source) {
         }

         @Override
         protected void doWorldEvent(IBlockSource source, Direction facing) {
         }
      });
   }

   public static void init() {
      if (!initialized) {
         initialized = true;
         Block.init();
         FireBlock.registerBurnProperties();
         Item.init();
         Stats.init();
         registerDispenseBehaviors();
      }
   }
}
