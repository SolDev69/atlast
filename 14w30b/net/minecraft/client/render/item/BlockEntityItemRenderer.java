package net.minecraft.client.render.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockEntityItemRenderer {
   public static BlockEntityItemRenderer INSTANCE = new BlockEntityItemRenderer();
   private ChestBlockEntity chest = new ChestBlockEntity(0);
   private ChestBlockEntity trappedChest = new ChestBlockEntity(1);
   private EnderChestBlockEntity enderChest = new EnderChestBlockEntity();
   private BannerBlockEntity banner = new BannerBlockEntity();
   private SkullBlockEntity skull = new SkullBlockEntity();

   public void render(ItemStack stack) {
      if (stack.getItem() == Items.BANNER) {
         this.banner.set(stack);
         BlockEntityRenderDispatcher.INSTANCE.render(this.banner, 0.0, 0.0, 0.0, 0.0F);
      } else if (stack.getItem() == Items.SKULL) {
         GameProfile var2 = null;
         if (stack.hasNbt()) {
            NbtCompound var3 = stack.getNbt();
            if (var3.isType("SkullOwner", 10)) {
               var2 = NbtUtils.readProfile(var3.getCompound("SkullOwner"));
            } else if (var3.isType("SkullOwner", 8) && var3.getString("SkullOwner").length() > 0) {
               var2 = new GameProfile(null, var3.getString("SkullOwner"));
            }
         }

         if (SkullRenderer.instance != null) {
            SkullRenderer.instance.render(0.0F, 0.0F, 0.0F, Direction.UP, 0.0F, stack.getMetadata(), var2, -1);
         }
      } else {
         Block var4 = Block.byItem(stack.getItem());
         if (var4 == Blocks.ENDER_CHEST) {
            BlockEntityRenderDispatcher.INSTANCE.render(this.enderChest, 0.0, 0.0, 0.0, 0.0F);
         } else if (var4 == Blocks.TRAPPED_CHEST) {
            BlockEntityRenderDispatcher.INSTANCE.render(this.trappedChest, 0.0, 0.0, 0.0, 0.0F);
         } else {
            BlockEntityRenderDispatcher.INSTANCE.render(this.chest, 0.0, 0.0, 0.0, 0.0F);
         }
      }
   }
}
