package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.Property;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class StoneSlabItem extends BlockItem {
   private final SlabBlock singleSlab;
   private final SlabBlock doubleSlab;

   public StoneSlabItem(Block block, SlabBlock singleSlab, SlabBlock doubleSlab) {
      super(block);
      this.singleSlab = singleSlab;
      this.doubleSlab = doubleSlab;
      this.setMaxDamage(0);
      this.setStackable(true);
   }

   @Override
   public int getBlockMetadata(int metadata) {
      return metadata;
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      return this.singleSlab.getName(stack.getMetadata());
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (stack.size == 0) {
         return false;
      } else if (!player.canUseItem(pos.offset(face), face, stack)) {
         return false;
      } else {
         Object var9 = this.singleSlab.getVariant(stack);
         BlockState var10 = world.getBlockState(pos);
         if (var10.getBlock() == this.singleSlab) {
            Property var11 = this.singleSlab.getVariantProperty();
            Comparable var12 = var10.get(var11);
            SlabBlock.Half var13 = (SlabBlock.Half)var10.get(SlabBlock.HALF);
            if ((face == Direction.UP && var13 == SlabBlock.Half.BOTTOM || face == Direction.DOWN && var13 == SlabBlock.Half.TOP) && var12 == var9) {
               BlockState var14 = this.doubleSlab.defaultState().set(var11, var12);
               if (world.canBuildIn(this.doubleSlab.getCollisionShape(world, pos, var14)) && world.setBlockState(pos, var14, 3)) {
                  world.playSound(
                     (double)((float)pos.getX() + 0.5F),
                     (double)((float)pos.getY() + 0.5F),
                     (double)((float)pos.getZ() + 0.5F),
                     this.doubleSlab.sound.getSound(),
                     (this.doubleSlab.sound.getVolume() + 1.0F) / 2.0F,
                     this.doubleSlab.sound.getPitch() * 0.8F
                  );
                  --stack.size;
               }

               return true;
            }
         }

         return this.combineSlabs(stack, world, pos.offset(face), var9) ? true : super.use(stack, player, world, pos, face, dx, dy, dz);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean onPlace(World world, BlockPos pos, Direction dir, PlayerEntity player, ItemStack stack) {
      Property var7 = this.singleSlab.getVariantProperty();
      Object var8 = this.singleSlab.getVariant(stack);
      BlockState var9 = world.getBlockState(pos);
      if (var9.getBlock() == this.singleSlab) {
         boolean var10 = var9.get(SlabBlock.HALF) == SlabBlock.Half.TOP;
         if ((dir == Direction.UP && !var10 || dir == Direction.DOWN && var10) && var8 == var9.get(var7)) {
            return true;
         }
      }

      BlockPos var11 = pos.offset(dir);
      BlockState var12 = world.getBlockState(var11);
      return var12.getBlock() == this.singleSlab && var8 == var12.get(var7) ? true : super.onPlace(world, pos, dir, player, stack);
   }

   private boolean combineSlabs(ItemStack item, World player, BlockPos world, Object x) {
      BlockState var5 = player.getBlockState(world);
      if (var5.getBlock() == this.singleSlab) {
         Comparable var6 = var5.get(this.singleSlab.getVariantProperty());
         if (var6 == x) {
            BlockState var7 = this.doubleSlab.defaultState().set(this.singleSlab.getVariantProperty(), var6);
            if (player.canBuildIn(this.doubleSlab.getCollisionShape(player, world, var7)) && player.setBlockState(world, var7, 3)) {
               player.playSound(
                  (double)((float)world.getX() + 0.5F),
                  (double)((float)world.getY() + 0.5F),
                  (double)((float)world.getZ() + 0.5F),
                  this.doubleSlab.sound.getSound(),
                  (this.doubleSlab.sound.getVolume() + 1.0F) / 2.0F,
                  this.doubleSlab.sound.getPitch() * 0.8F
               );
               --item.size;
            }

            return true;
         }
      }

      return false;
   }
}
