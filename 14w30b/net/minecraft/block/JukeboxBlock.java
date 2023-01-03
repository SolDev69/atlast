package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class JukeboxBlock extends BlockWithBlockEntity {
   public static final BooleanProperty HAS_RECORD = BooleanProperty.of("has_record");

   protected JukeboxBlock() {
      super(Material.WOOD);
      this.setDefaultState(this.stateDefinition.any().set(HAS_RECORD, false));
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (state.get(HAS_RECORD)) {
         this.removeRecord(world, pos, state);
         return true;
      } else {
         return false;
      }
   }

   public void setRecord(World world, BlockPos pos, BlockState state, ItemStack stack) {
      if (!world.isClient) {
         BlockEntity var5 = world.getBlockEntity(pos);
         if (var5 instanceof JukeboxBlock.JukeboxBlockEntity) {
            ((JukeboxBlock.JukeboxBlockEntity)var5).setRecord(stack.copy());
            world.setBlockState(pos, state.set(HAS_RECORD, true), 2);
         }
      }
   }

   public BlockState removeRecord(World c_54ruxjwzt, BlockPos c_76varpwca, BlockState c_17agfiprw) {
      if (c_54ruxjwzt.isClient) {
         return c_17agfiprw;
      } else {
         BlockEntity var4 = c_54ruxjwzt.getBlockEntity(c_76varpwca);
         if (!(var4 instanceof JukeboxBlock.JukeboxBlockEntity)) {
            return c_17agfiprw;
         } else {
            JukeboxBlock.JukeboxBlockEntity var5 = (JukeboxBlock.JukeboxBlockEntity)var4;
            ItemStack var6 = var5.getRecord();
            if (var6 == null) {
               return c_17agfiprw;
            } else {
               c_54ruxjwzt.doEvent(1005, c_76varpwca, 0);
               c_54ruxjwzt.onRecordRemoved(c_76varpwca, null);
               var5.setRecord(null);
               c_17agfiprw = c_17agfiprw.set(HAS_RECORD, false);
               c_54ruxjwzt.setBlockState(c_76varpwca, c_17agfiprw, 2);
               float var7 = 0.7F;
               double var8 = (double)(c_54ruxjwzt.random.nextFloat() * var7) + (double)(1.0F - var7) * 0.5;
               double var10 = (double)(c_54ruxjwzt.random.nextFloat() * var7) + (double)(1.0F - var7) * 0.2 + 0.6;
               double var12 = (double)(c_54ruxjwzt.random.nextFloat() * var7) + (double)(1.0F - var7) * 0.5;
               ItemStack var14 = var6.copy();
               ItemEntity var15 = new ItemEntity(
                  c_54ruxjwzt, (double)c_76varpwca.getX() + var8, (double)c_76varpwca.getY() + var10, (double)c_76varpwca.getZ() + var12, var14
               );
               var15.resetPickupCooldown();
               c_54ruxjwzt.addEntity(var15);
               return c_17agfiprw;
            }
         }
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      state = this.removeRecord(world, pos, state);
      super.onRemoved(world, pos, state);
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      if (!world.isClient) {
         super.dropItems(world, pos, state, luck, 0);
      }
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new JukeboxBlock.JukeboxBlockEntity();
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      BlockEntity var3 = world.getBlockEntity(pos);
      if (var3 instanceof JukeboxBlock.JukeboxBlockEntity) {
         ItemStack var4 = ((JukeboxBlock.JukeboxBlockEntity)var3).getRecord();
         if (var4 != null) {
            return Item.getRawId(var4.getItem()) + 1 - Item.getRawId(Items.RECORD_13);
         }
      }

      return 0;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(HAS_RECORD, metadata > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(HAS_RECORD) ? 1 : 0;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, HAS_RECORD);
   }

   public static class JukeboxBlockEntity extends BlockEntity {
      private ItemStack record;

      @Override
      public void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         if (nbt.isType("RecordItem", 10)) {
            this.setRecord(ItemStack.fromNbt(nbt.getCompound("RecordItem")));
         } else if (nbt.getInt("Record") > 0) {
            this.setRecord(new ItemStack(Item.byRawId(nbt.getInt("Record")), 1, 0));
         }
      }

      @Override
      public void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         if (this.getRecord() != null) {
            nbt.put("RecordItem", this.getRecord().writeNbt(new NbtCompound()));
         }
      }

      public ItemStack getRecord() {
         return this.record;
      }

      public void setRecord(ItemStack record) {
         this.record = record;
         this.markDirty();
      }
   }
}
