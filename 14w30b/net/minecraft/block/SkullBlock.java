package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockPointer;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockStatePredicate;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.boss.WitherEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SkullBlock extends BlockWithBlockEntity {
   public static final DirectionProperty FACING = DirectionProperty.of("facing");
   public static final BooleanProperty NODROP = BooleanProperty.of("nodrop");
   private static final Predicate WITHER_SKULL_PREDICATE = new Predicate() {
      public boolean apply(BlockPointer c_96zktyemb) {
         return c_96zktyemb.getState().getBlock() == Blocks.SKULL
            && c_96zktyemb.getBlockEntity() instanceof SkullBlockEntity
            && ((SkullBlockEntity)c_96zktyemb.getBlockEntity()).getType() == 1;
      }
   };
   private BlockPattern witherBodyPattern;
   private BlockPattern witherPattern;

   protected SkullBlock() {
      super(Material.DECORATION);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(NODROP, false));
      this.setShape(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      switch((Direction)world.getBlockState(pos).get(FACING)) {
         case UP:
         default:
            this.setShape(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
            break;
         case NORTH:
            this.setShape(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
            break;
         case SOUTH:
            this.setShape(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
            break;
         case WEST:
            this.setShape(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
            break;
         case EAST:
            this.setShape(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
      }
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      this.updateShape(world, pos);
      return super.getCollisionShape(world, pos, state);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.defaultState().set(FACING, entity.getDirection()).set(NODROP, false);
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new SkullBlockEntity();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.SKULL;
   }

   @Override
   public int getPickItemMetadata(World world, BlockPos pos) {
      BlockEntity var3 = world.getBlockEntity(pos);
      return var3 instanceof SkullBlockEntity ? ((SkullBlockEntity)var3).getType() : super.getPickItemMetadata(world, pos);
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
   }

   @Override
   public void beforeMinedByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (player.abilities.creativeMode) {
         state = state.set(NODROP, true);
         world.setBlockState(pos, state, 4);
      }

      super.beforeMinedByPlayer(world, pos, state, player);
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      if (!world.isClient) {
         if (!state.get(NODROP)) {
            BlockEntity var4 = world.getBlockEntity(pos);
            if (var4 instanceof SkullBlockEntity) {
               SkullBlockEntity var5 = (SkullBlockEntity)var4;
               ItemStack var6 = new ItemStack(Items.SKULL, 1, this.getPickItemMetadata(world, pos));
               if (var5.getType() == 3 && var5.getProfile() != null) {
                  var6.setNbt(new NbtCompound());
                  NbtCompound var7 = new NbtCompound();
                  NbtUtils.writeProfile(var7, var5.getProfile());
                  var6.getNbt().put("SkullOwner", var7);
               }

               this.dropItems(world, pos, var6);
            }
         }

         super.onRemoved(world, pos, state);
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.SKULL;
   }

   public boolean canSpawnWither(World world, BlockPos pos, ItemStack stack) {
      if (stack.getMetadata() == 1 && pos.getY() >= 2 && world.getDifficulty() != Difficulty.PEACEFUL && !world.isClient) {
         return this.getWitherBodyPattern().find(world, pos) != null;
      } else {
         return false;
      }
   }

   public void trySpawnWither(World world, BlockPos pos, SkullBlockEntity skull) {
      if (skull.getType() == 1 && pos.getY() >= 2 && world.getDifficulty() != Difficulty.PEACEFUL && !world.isClient) {
         BlockPattern var4 = this.getWitherPattern();
         BlockPattern.Match var5 = var4.find(world, pos);
         if (var5 != null) {
            for(int var6 = 0; var6 < 3; ++var6) {
               BlockPointer var7 = var5.getBlock(var6, 0, 0);
               world.setBlockState(var7.getPos(), var7.getState().set(NODROP, true), 2);
            }

            for(int var12 = 0; var12 < var4.getHeight(); ++var12) {
               for(int var14 = 0; var14 < var4.getWidth(); ++var14) {
                  BlockPointer var8 = var5.getBlock(var12, var14, 0);
                  world.setBlockState(var8.getPos(), Blocks.AIR.defaultState(), 2);
               }
            }

            BlockPos var13 = var5.getBlock(1, 0, 0).getPos();
            WitherEntity var15 = new WitherEntity(world);
            BlockPos var16 = var5.getBlock(1, 2, 0).getPos();
            var15.refreshPositionAndAngles(
               (double)var16.getX() + 0.5,
               (double)var16.getY() + 0.55,
               (double)var16.getZ() + 0.5,
               var5.getForward().getAxis() == Direction.Axis.X ? 0.0F : 90.0F,
               0.0F
            );
            var15.bodyYaw = var5.getForward().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
            var15.onSummoned();

            for(PlayerEntity var10 : world.getEntities(PlayerEntity.class, var15.getBoundingBox().expand(50.0, 50.0, 50.0))) {
               var10.incrementStat(Achievements.SUMMON_WITHER);
            }

            world.addEntity(var15);

            for(int var17 = 0; var17 < 120; ++var17) {
               world.addParticle(
                  ParticleType.SNOWBALL,
                  (double)var13.getX() + world.random.nextDouble(),
                  (double)(var13.getY() - 2) + world.random.nextDouble() * 3.9,
                  (double)var13.getZ() + world.random.nextDouble(),
                  0.0,
                  0.0,
                  0.0
               );
            }

            for(int var18 = 0; var18 < var4.getHeight(); ++var18) {
               for(int var19 = 0; var19 < var4.getWidth(); ++var19) {
                  BlockPointer var11 = var5.getBlock(var18, var19, 0);
                  world.onBlockChanged(var11.getPos(), Blocks.AIR);
               }
            }
         }
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, Direction.byId(metadata & 7)).set(NODROP, (metadata & 8) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getId();
      if (state.get(NODROP)) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, NODROP);
   }

   protected BlockPattern getWitherBodyPattern() {
      if (this.witherBodyPattern == null) {
         this.witherBodyPattern = BlockPatternBuilder.start()
            .aisle("   ", "###", "~#~")
            .with('#', BlockPointer.hasState(BlockStatePredicate.of(Blocks.SOUL_SAND)))
            .with('~', BlockPointer.hasState(BlockStatePredicate.of(Blocks.AIR)))
            .build();
      }

      return this.witherBodyPattern;
   }

   protected BlockPattern getWitherPattern() {
      if (this.witherPattern == null) {
         this.witherPattern = BlockPatternBuilder.start()
            .aisle("^^^", "###", "~#~")
            .with('#', BlockPointer.hasState(BlockStatePredicate.of(Blocks.SOUL_SAND)))
            .with('^', WITHER_SKULL_PREDICATE)
            .with('~', BlockPointer.hasState(BlockStatePredicate.of(Blocks.AIR)))
            .build();
      }

      return this.witherPattern;
   }
}
