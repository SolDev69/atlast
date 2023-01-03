package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EnchantingTableBlock extends BlockWithBlockEntity {
   protected EnchantingTableBlock() {
      super(Material.STONE);
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
      this.setOpacity(0);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      super.randomDisplayTick(world, pos, state, random);

      for(int var5 = -2; var5 <= 2; ++var5) {
         for(int var6 = -2; var6 <= 2; ++var6) {
            if (var5 > -2 && var5 < 2 && var6 == -1) {
               var6 = 2;
            }

            if (random.nextInt(16) == 0) {
               for(int var7 = 0; var7 <= 1; ++var7) {
                  BlockPos var8 = pos.add(var5, var7, var6);
                  if (world.getBlockState(var8).getBlock() == Blocks.BOOKSHELF) {
                     if (!world.isAir(pos.add(var5 / 2, 0, var6 / 2))) {
                        break;
                     }

                     world.addParticle(
                        ParticleType.ENCHANTMENT_TABLE,
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 2.0,
                        (double)pos.getZ() + 0.5,
                        (double)((float)var5 + random.nextFloat()) - 0.5,
                        (double)((float)var7 - random.nextFloat() - 1.0F),
                        (double)((float)var6 + random.nextFloat()) - 0.5
                     );
                  }
               }
            }
         }
      }
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
      return new EnchantingTableBlockEntity();
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         BlockEntity var9 = world.getBlockEntity(pos);
         if (var9 instanceof EnchantingTableBlockEntity) {
            player.openMenu((EnchantingTableBlockEntity)var9);
         }

         return true;
      }
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      super.onPlaced(world, pos, state, entity, stack);
      if (stack.hasCustomHoverName()) {
         BlockEntity var6 = world.getBlockEntity(pos);
         if (var6 instanceof EnchantingTableBlockEntity) {
            ((EnchantingTableBlockEntity)var6).setCustomName(stack.getHoverName());
         }
      }
   }
}
