package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.FireworksEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FireworksItem extends Item {
   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (!world.isClient) {
         FireworksEntity var9 = new FireworksEntity(
            world, (double)((float)pos.getX() + dx), (double)((float)pos.getY() + dy), (double)((float)pos.getZ() + dz), stack
         );
         world.addEntity(var9);
         if (!player.abilities.creativeMode) {
            --stack.size;
         }

         return true;
      } else {
         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addHoverText(ItemStack stack, PlayerEntity player, List tooltip, boolean advanced) {
      if (stack.hasNbt()) {
         NbtCompound var5 = stack.getNbt().getCompound("Fireworks");
         if (var5 != null) {
            if (var5.isType("Flight", 99)) {
               tooltip.add(I18n.translate("item.fireworks.flight") + " " + var5.getByte("Flight"));
            }

            NbtList var6 = var5.getList("Explosions", 10);
            if (var6 != null && var6.size() > 0) {
               for(int var7 = 0; var7 < var6.size(); ++var7) {
                  NbtCompound var8 = var6.getCompound(var7);
                  ArrayList var9 = Lists.newArrayList();
                  FireworksChargeItem.addExplosionInfo(var8, var9);
                  if (var9.size() > 0) {
                     for(int var10 = 1; var10 < var9.size(); ++var10) {
                        var9.set(var10, "  " + (String)var9.get(var10));
                     }

                     tooltip.addAll(var9);
                  }
               }
            }
         }
      }
   }
}
