package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntBlock extends Block {
   public static final BooleanProperty EXPLODE = BooleanProperty.of("explode");

   public TntBlock() {
      super(Material.TNT);
      this.setDefaultState(this.stateDefinition.any().set(EXPLODE, false));
      this.setItemGroup(ItemGroup.REDSTONE);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      super.onAdded(world, pos, state);
      if (world.isReceivingPower(pos)) {
         this.onBroken(world, pos, state.set(EXPLODE, true));
         world.removeBlock(pos);
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (world.isReceivingPower(pos)) {
         this.onBroken(world, pos, state.set(EXPLODE, true));
         world.removeBlock(pos);
      }
   }

   @Override
   public void onExploded(World world, BlockPos pos, Explosion explosion) {
      if (!world.isClient) {
         PrimedTntEntity var4 = new PrimedTntEntity(
            world, (double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), explosion.getSource()
         );
         var4.fuseTimer = world.random.nextInt(var4.fuseTimer / 4) + var4.fuseTimer / 8;
         world.addEntity(var4);
      }
   }

   @Override
   public void onBroken(World world, BlockPos pos, BlockState state) {
      this.ignite(world, pos, state, null);
   }

   public void ignite(World world, BlockPos pos, BlockState state, LivingEntity igniter) {
      if (!world.isClient) {
         if (state.get(EXPLODE)) {
            PrimedTntEntity var5 = new PrimedTntEntity(
               world, (double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), igniter
            );
            world.addEntity(var5);
            world.playSound(var5, "game.tnt.primed", 1.0F, 1.0F);
         }
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.FLINT_AND_STEEL) {
         this.ignite(world, pos, state.set(EXPLODE, true), player);
         world.removeBlock(pos);
         player.getMainHandStack().damageAndBreak(1, player);
         return true;
      } else {
         return super.use(world, pos, state, player, face, dx, dy, dz);
      }
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      if (!world.isClient && entity instanceof ArrowEntity) {
         ArrowEntity var5 = (ArrowEntity)entity;
         if (var5.isOnFire()) {
            this.ignite(world, pos, world.getBlockState(pos).set(EXPLODE, true), var5.shooter instanceof LivingEntity ? (LivingEntity)var5.shooter : null);
            world.removeBlock(pos);
         }
      }
   }

   @Override
   public boolean shouldDropItemsOnExplosion(Explosion explosion) {
      return false;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(EXPLODE, (metadata & 1) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(EXPLODE) ? 1 : 0;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, EXPLODE);
   }
}
