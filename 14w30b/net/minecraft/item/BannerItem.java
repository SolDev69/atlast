package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BannerItem extends BlockItem {
   public BannerItem() {
      super(Blocks.STANDING_BANNER);
      this.maxStackSize = 16;
      this.setItemGroup(ItemGroup.DECORATIONS);
      this.setStackable(true);
      this.setMaxDamage(0);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (face == Direction.DOWN) {
         return false;
      } else if (!world.getBlockState(pos).getBlock().getMaterial().isSolid()) {
         return false;
      } else {
         pos = pos.offset(face);
         if (!player.canUseItem(pos, face, stack)) {
            return false;
         } else if (!Blocks.STANDING_BANNER.canSurvive(world, pos)) {
            return false;
         } else if (world.isClient) {
            return true;
         } else {
            if (face == Direction.UP) {
               int var9 = MathHelper.floor((double)((player.yaw + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
               world.setBlockState(pos, Blocks.STANDING_BANNER.defaultState().set(StandingSignBlock.ROTATION, var9), 3);
            } else {
               world.setBlockState(pos, Blocks.WALL_BANNER.defaultState().set(WallSignBlock.FACING, face), 3);
            }

            --stack.size;
            BlockEntity var11 = world.getBlockEntity(pos);
            if (var11 instanceof BannerBlockEntity) {
               ((BannerBlockEntity)var11).set(stack);
            }

            return true;
         }
      }
   }

   @Override
   public String getName(ItemStack stack) {
      String var2 = "item.banner.name";
      DyeColor var3 = this.getBaseColor(stack);
      return I18n.translate(var2, I18n.translate("item.fireworksCharge." + var3.getName()));
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addHoverText(ItemStack stack, PlayerEntity player, List tooltip, boolean advanced) {
      NbtCompound var5 = stack.getNbt("BlockEntityTag", false);
      if (var5 != null && var5.contains("Patterns")) {
         NbtList var6 = var5.getList("Patterns", 10);

         for(int var7 = 0; var7 < var6.size() && var7 < 6; ++var7) {
            NbtCompound var8 = var6.getCompound(var7);
            DyeColor var9 = DyeColor.byMetadata(var8.getInt("Color"));
            BannerBlockEntity.Pattern var10 = BannerBlockEntity.Pattern.byId(var8.getString("Pattern"));
            if (var10 != null) {
               tooltip.add(I18n.translate("item.banner." + var10.getName(), I18n.translate("item.fireworksCharge." + var9.getName())));
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getDisplayColor(ItemStack stack, int color) {
      if (color == 0) {
         return 16777215;
      } else {
         DyeColor var3 = this.getBaseColor(stack);
         return var3.getMaterialColor().color;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      for(DyeColor var7 : DyeColor.values()) {
         list.add(new ItemStack(item, 1, var7.getMetadata()));
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public ItemGroup getItemGroup() {
      return ItemGroup.DECORATIONS;
   }

   private DyeColor getBaseColor(ItemStack stack) {
      NbtCompound var2 = stack.getNbt("BlockEntityTag", false);
      DyeColor var3 = null;
      if (var2 != null && var2.contains("Base")) {
         var3 = DyeColor.byMetadata(var2.getInt("Base"));
      } else {
         var3 = DyeColor.byMetadata(stack.getMetadata());
      }

      return var3;
   }
}
