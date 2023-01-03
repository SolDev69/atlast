package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.inventory.InventoryUtils;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BrewingStandBlock extends BlockWithBlockEntity {
   public static final BooleanProperty[] HAS_BOTTLE = new BooleanProperty[]{
      BooleanProperty.of("has_bottle_0"), BooleanProperty.of("has_bottle_1"), BooleanProperty.of("has_bottle_2")
   };
   private final Random random = new Random();

   public BrewingStandBlock() {
      super(Material.IRON);
      this.setDefaultState(this.stateDefinition.any().set(HAS_BOTTLE[0], false).set(HAS_BOTTLE[1], false).set(HAS_BOTTLE[2], false));
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new BrewingStandBlockEntity();
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      this.setShape(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setBlockItemBounds();
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
   }

   @Override
   public void setBlockItemBounds() {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         BlockEntity var9 = world.getBlockEntity(pos);
         if (var9 instanceof BrewingStandBlockEntity) {
            player.openInventoryMenu((BrewingStandBlockEntity)var9);
         }

         return true;
      }
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      if (stack.hasCustomHoverName()) {
         BlockEntity var6 = world.getBlockEntity(pos);
         if (var6 instanceof BrewingStandBlockEntity) {
            ((BrewingStandBlockEntity)var6).setCustomName(stack.getHoverName());
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      double var5 = (double)((float)pos.getX() + 0.4F + random.nextFloat() * 0.2F);
      double var7 = (double)((float)pos.getY() + 0.7F + random.nextFloat() * 0.3F);
      double var9 = (double)((float)pos.getZ() + 0.4F + random.nextFloat() * 0.2F);
      world.addParticle(ParticleType.SMOKE_NORMAL, var5, var7, var9, 0.0, 0.0, 0.0);
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      BlockEntity var4 = world.getBlockEntity(pos);
      if (var4 instanceof BrewingStandBlockEntity) {
         InventoryUtils.dropContents(world, pos, (BrewingStandBlockEntity)var4);
      }

      super.onRemoved(world, pos, state);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.BREWING_STAND;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.BREWING_STAND;
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      return InventoryMenu.getAnalogOutput(world.getBlockEntity(pos));
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      BlockState var2 = this.defaultState();

      for(int var3 = 0; var3 < 3; ++var3) {
         var2 = var2.set(HAS_BOTTLE[var3], (metadata & 1 << var3) > 0);
      }

      return var2;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;

      for(int var3 = 0; var3 < 3; ++var3) {
         if (state.get(HAS_BOTTLE[var3])) {
            var2 |= 1 << var3;
         }
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
   }
}
