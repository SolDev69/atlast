package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class HoeItem extends Item {
   protected Item.ToolMaterial material;

   public HoeItem(Item.ToolMaterial material) {
      this.material = material;
      this.maxStackSize = 1;
      this.setMaxDamage(material.getMaxDurability());
      this.setItemGroup(ItemGroup.TOOLS);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (!player.canUseItem(pos.offset(face), face, stack)) {
         return false;
      } else {
         Block var9 = world.getBlockState(pos).getBlock();
         if (face != Direction.DOWN && world.getBlockState(pos.up()).getBlock().getMaterial() == Material.AIR && (var9 == Blocks.GRASS || var9 == Blocks.DIRT)) {
            Block var10 = Blocks.FARMLAND;
            world.playSound(
               (double)((float)pos.getX() + 0.5F),
               (double)((float)pos.getY() + 0.5F),
               (double)((float)pos.getZ() + 0.5F),
               var10.sound.getStepSound(),
               (var10.sound.getVolume() + 1.0F) / 2.0F,
               var10.sound.getPitch() * 0.8F
            );
            if (world.isClient) {
               return true;
            } else {
               world.setBlockState(pos, var10.defaultState());
               stack.damageAndBreak(1, player);
               return true;
            }
         } else {
            return false;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isHandheld() {
      return true;
   }

   public String getAsString() {
      return this.material.toString();
   }
}
