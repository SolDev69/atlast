package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SkullItem extends Item {
   private static final String[] SKULL_TYPES = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};

   public SkullItem() {
      this.setItemGroup(ItemGroup.DECORATIONS);
      this.setMaxDamage(0);
      this.setStackable(true);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (face == Direction.DOWN) {
         return false;
      } else {
         BlockState var9 = world.getBlockState(pos);
         Block var10 = var9.getBlock();
         boolean var11 = var10.canBeReplaced(world, pos);
         if (!var11) {
            if (!world.getBlockState(pos).getBlock().getMaterial().isSolid()) {
               return false;
            }

            pos = pos.offset(face);
         }

         if (!player.canUseItem(pos, face, stack)) {
            return false;
         } else if (!Blocks.SKULL.canSurvive(world, pos)) {
            return false;
         } else {
            if (!world.isClient) {
               world.setBlockState(pos, Blocks.SKULL.defaultState().set(SkullBlock.FACING, face), 2);
               int var12 = 0;
               if (face == Direction.UP) {
                  var12 = MathHelper.floor((double)(player.yaw * 16.0F / 360.0F) + 0.5) & 15;
               }

               BlockEntity var13 = world.getBlockEntity(pos);
               if (var13 instanceof SkullBlockEntity) {
                  SkullBlockEntity var14 = (SkullBlockEntity)var13;
                  if (stack.getMetadata() == 3) {
                     GameProfile var15 = null;
                     if (stack.hasNbt()) {
                        NbtCompound var16 = stack.getNbt();
                        if (var16.isType("SkullOwner", 10)) {
                           var15 = NbtUtils.readProfile(var16.getCompound("SkullOwner"));
                        } else if (var16.isType("SkullOwner", 8) && var16.getString("SkullOwner").length() > 0) {
                           var15 = new GameProfile(null, var16.getString("SkullOwner"));
                        }
                     }

                     var14.setProfile(var15);
                  } else {
                     var14.setSkullType(stack.getMetadata());
                  }

                  var14.setRotation(var12);
                  Blocks.SKULL.trySpawnWither(world, pos, var14);
               }

               --stack.size;
            }

            return true;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      for(int var4 = 0; var4 < SKULL_TYPES.length; ++var4) {
         list.add(new ItemStack(item, 1, var4));
      }
   }

   @Override
   public int getBlockMetadata(int metadata) {
      return metadata;
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      int var2 = stack.getMetadata();
      if (var2 < 0 || var2 >= SKULL_TYPES.length) {
         var2 = 0;
      }

      return super.getTranslationKey() + "." + SKULL_TYPES[var2];
   }

   @Override
   public String getName(ItemStack stack) {
      if (stack.getMetadata() == 3 && stack.hasNbt()) {
         if (stack.getNbt().isType("SkullOwner", 8)) {
            return I18n.translate("item.skull.player.name", stack.getNbt().getString("SkullOwner"));
         }

         if (stack.getNbt().isType("SkullOwner", 10)) {
            NbtCompound var2 = stack.getNbt().getCompound("SkullOwner");
            if (var2.isType("Name", 8)) {
               return I18n.translate("item.skull.player.name", var2.getString("Name"));
            }
         }
      }

      return super.getName(stack);
   }

   @Override
   public boolean validateNbt(NbtCompound nbt) {
      super.validateNbt(nbt);
      if (nbt.isType("SkullOwner", 8)) {
         GameProfile var2 = new GameProfile(null, nbt.getString("SkullOwner"));
         var2 = SkullBlockEntity.updateProfile(var2);
         nbt.put("SkullOwner", NbtUtils.writeProfile(new NbtCompound(), var2));
         return true;
      } else {
         return false;
      }
   }
}
