package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowLayerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BlockItem extends Item {
   protected final Block block;

   public BlockItem(Block block) {
      this.block = block;
   }

   public BlockItem setId(String string) {
      super.setId(string);
      return this;
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      BlockState var9 = world.getBlockState(pos);
      Block var10 = var9.getBlock();
      if (var10 == Blocks.SNOW_LAYER && var9.get(SnowLayerBlock.LAYERS) < 1) {
         face = Direction.UP;
      } else if (!var10.canBeReplaced(world, pos)) {
         pos = pos.offset(face);
      }

      if (stack.size == 0) {
         return false;
      } else if (!player.canUseItem(pos, face, stack)) {
         return false;
      } else if (pos.getY() == 255 && this.block.getMaterial().isSolid()) {
         return false;
      } else if (world.canReplace(this.block, pos, false, face, null, stack)) {
         int var11 = this.getBlockMetadata(stack.getMetadata());
         BlockState var12 = this.block.getPlacementState(world, pos, face, dx, dy, dz, var11, player);
         if (world.setBlockState(pos, var12, 3)) {
            var12 = world.getBlockState(pos);
            if (var12.getBlock() == this.block) {
               setBlockNbt(world, pos, stack);
               this.block.onPlaced(world, pos, var12, player, stack);
            }

            world.playSound(
               (double)((float)pos.getX() + 0.5F),
               (double)((float)pos.getY() + 0.5F),
               (double)((float)pos.getZ() + 0.5F),
               this.block.sound.getSound(),
               (this.block.sound.getVolume() + 1.0F) / 2.0F,
               this.block.sound.getPitch() * 0.8F
            );
            --stack.size;
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean setBlockNbt(World world, BlockPos pos, ItemStack stack) {
      if (stack.hasNbt() && stack.getNbt().isType("BlockEntityTag", 10)) {
         BlockEntity var3 = world.getBlockEntity(pos);
         if (var3 != null) {
            NbtCompound var4 = new NbtCompound();
            NbtCompound var5 = (NbtCompound)var4.copy();
            var3.writeNbt(var4);
            NbtCompound var6 = (NbtCompound)stack.getNbt().get("BlockEntityTag");
            var4.merge(var6);
            var4.putInt("x", pos.getX());
            var4.putInt("y", pos.getY());
            var4.putInt("z", pos.getZ());
            if (!var4.equals(var5)) {
               var3.readNbt(var4);
               var3.markDirty();
               return true;
            }
         }
      }

      return false;
   }

   @Environment(EnvType.CLIENT)
   public boolean onPlace(World world, BlockPos pos, Direction dir, PlayerEntity player, ItemStack stack) {
      Block var6 = world.getBlockState(pos).getBlock();
      if (var6 == Blocks.SNOW_LAYER) {
         dir = Direction.UP;
      } else if (!var6.canBeReplaced(world, pos)) {
         pos = pos.offset(dir);
      }

      return world.canReplace(this.block, pos, false, dir, null, stack);
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      return this.block.getTranslationKey();
   }

   @Override
   public String getTranslationKey() {
      return this.block.getTranslationKey();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public ItemGroup getItemGroup() {
      return this.block.getItemGroup();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      this.block.addToCreativeMenu(item, group, list);
   }

   public Block getBlock() {
      return this.block;
   }
}
