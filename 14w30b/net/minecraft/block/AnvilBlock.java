package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.menu.AnvilMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AnvilBlock extends FallingBlock {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);
   public static final IntegerProperty DAMAGE = IntegerProperty.of("damage", 0, 2);

   protected AnvilBlock() {
      super(Material.ANVIL);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(DAMAGE, 0));
      this.setOpacity(0);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      Direction var9 = entity.getDirection().clockwiseY();
      return super.getPlacementState(world, pos, dir, dx, dy, dz, metadata, entity).set(FACING, var9).set(DAMAGE, 0);
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (!world.isClient) {
         player.openMenu(new AnvilBlock.MenuProvider(world, pos));
      }

      return true;
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return state.get(DAMAGE);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      Direction var3 = (Direction)world.getBlockState(pos).get(FACING);
      if (var3.getAxis() == Direction.Axis.X) {
         this.setShape(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
      } else {
         this.setShape(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      stacks.add(new ItemStack(item, 1, 0));
      stacks.add(new ItemStack(item, 1, 1));
      stacks.add(new ItemStack(item, 1, 2));
   }

   @Override
   protected void beforeStartFalling(FallingBlockEntity fallingBlockEntity) {
      fallingBlockEntity.setHurtingEntities(true);
   }

   @Override
   public void onTickFallingBlockEntity(World world, BlockPos pos) {
      world.doEvent(1022, pos, 0);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int m_43rfjsapl(int i) {
      return i << 2;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, Direction.byIdHorizontal(metadata & 3)).set(DAMAGE, (metadata & 15) >> 2);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getIdHorizontal();
      return var2 | state.get(DAMAGE) << 2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, DAMAGE);
   }

   public static class MenuProvider implements net.minecraft.inventory.menu.MenuProvider {
      private final World world;
      private final BlockPos pos;

      public MenuProvider(World world, BlockPos pos) {
         this.world = world;
         this.pos = pos;
      }

      @Override
      public String getName() {
         return "anvil";
      }

      @Override
      public boolean hasCustomName() {
         return false;
      }

      @Override
      public Text getDisplayName() {
         return new TranslatableText(Blocks.ANVIL.getTranslationKey() + ".name");
      }

      @Override
      public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
         return new AnvilMenu(playerInventory, this.world, this.pos, player);
      }

      @Override
      public String getMenuType() {
         return "minecraft:anvil";
      }
   }
}
