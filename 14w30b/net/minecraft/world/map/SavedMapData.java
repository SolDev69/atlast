package net.minecraft.world.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.MapDataS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.saved.SavedData;

public class SavedMapData extends SavedData {
   public int centerX;
   public int centerZ;
   public byte dimension;
   public byte scale;
   public byte[] colors = new byte[16384];
   public List holders = Lists.newArrayList();
   private Map holdersByPlayer = Maps.newHashMap();
   public Map decorations = Maps.newLinkedHashMap();

   public SavedMapData(String string) {
      super(string);
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      this.dimension = nbt.getByte("dimension");
      this.centerX = nbt.getInt("xCenter");
      this.centerZ = nbt.getInt("zCenter");
      this.scale = nbt.getByte("scale");
      this.scale = (byte)MathHelper.clamp(this.scale, 0, 4);
      short var2 = nbt.getShort("width");
      short var3 = nbt.getShort("height");
      if (var2 == 128 && var3 == 128) {
         this.colors = nbt.getByteArray("colors");
      } else {
         byte[] var4 = nbt.getByteArray("colors");
         this.colors = new byte[16384];
         int var5 = (128 - var2) / 2;
         int var6 = (128 - var3) / 2;

         for(int var7 = 0; var7 < var3; ++var7) {
            int var8 = var7 + var6;
            if (var8 >= 0 || var8 < 128) {
               for(int var9 = 0; var9 < var2; ++var9) {
                  int var10 = var9 + var5;
                  if (var10 >= 0 || var10 < 128) {
                     this.colors[var10 + var8 * 128] = var4[var9 + var7 * var2];
                  }
               }
            }
         }
      }
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      nbt.putByte("dimension", this.dimension);
      nbt.putInt("xCenter", this.centerX);
      nbt.putInt("zCenter", this.centerZ);
      nbt.putByte("scale", this.scale);
      nbt.putShort("width", (short)128);
      nbt.putShort("height", (short)128);
      nbt.putByteArray("colors", this.colors);
   }

   public void tickHolder(PlayerEntity player, ItemStack itemStack) {
      if (!this.holdersByPlayer.containsKey(player)) {
         SavedMapData.Holder var3 = new SavedMapData.Holder(player);
         this.holdersByPlayer.put(player, var3);
         this.holders.add(var3);
      }

      if (!player.inventory.contains(itemStack)) {
         this.decorations.remove(player.getName());
      }

      for(int var6 = 0; var6 < this.holders.size(); ++var6) {
         SavedMapData.Holder var4 = (SavedMapData.Holder)this.holders.get(var6);
         if (!var4.player.removed && (var4.player.inventory.contains(itemStack) || itemStack.isInItemFrame())) {
            if (!itemStack.isInItemFrame() && var4.player.dimensionId == this.dimension) {
               this.addDecoration(0, var4.player.world, var4.player.getName(), var4.player.x, var4.player.z, (double)var4.player.yaw);
            }
         } else {
            this.holdersByPlayer.remove(var4.player);
            this.holders.remove(var4);
         }
      }

      if (itemStack.isInItemFrame()) {
         ItemFrameEntity var7 = itemStack.getItemFrame();
         BlockPos var9 = var7.getBlockPos();
         this.addDecoration(
            1, player.world, "frame-" + var7.getNetworkId(), (double)var9.getX(), (double)var9.getZ(), (double)(var7.getFacing.getIdHorizontal() * 90)
         );
      }

      if (itemStack.hasNbt() && itemStack.getNbt().isType("Decorations", 9)) {
         NbtList var8 = itemStack.getNbt().getList("Decorations", 10);

         for(int var10 = 0; var10 < var8.size(); ++var10) {
            NbtCompound var5 = var8.getCompound(var10);
            if (!this.decorations.containsKey(var5.getString("id"))) {
               this.addDecoration(var5.getByte("type"), player.world, var5.getString("id"), var5.getDouble("x"), var5.getDouble("z"), var5.getDouble("rot"));
            }
         }
      }
   }

   private void addDecoration(int type, World world, String id, double x, double z, double rotation) {
      int var10 = 1 << this.scale;
      float var11 = (float)(x - (double)this.centerX) / (float)var10;
      float var12 = (float)(z - (double)this.centerZ) / (float)var10;
      byte var13 = (byte)((int)((double)(var11 * 2.0F) + 0.5));
      byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5));
      byte var16 = 63;
      byte var15;
      if (var11 >= (float)(-var16) && var12 >= (float)(-var16) && var11 <= (float)var16 && var12 <= (float)var16) {
         rotation += rotation < 0.0 ? -8.0 : 8.0;
         var15 = (byte)((int)(rotation * 16.0 / 360.0));
         if (this.dimension < 0) {
            int var17 = (int)(world.getData().getTimeOfDay() / 10L);
            var15 = (byte)(var17 * var17 * 34187121 + var17 * 121 >> 15 & 15);
         }
      } else {
         if (!(Math.abs(var11) < 320.0F) || !(Math.abs(var12) < 320.0F)) {
            this.decorations.remove(id);
            return;
         }

         type = 6;
         var15 = 0;
         if (var11 <= (float)(-var16)) {
            var13 = (byte)((int)((double)(var16 * 2) + 2.5));
         }

         if (var12 <= (float)(-var16)) {
            var14 = (byte)((int)((double)(var16 * 2) + 2.5));
         }

         if (var11 >= (float)var16) {
            var13 = (byte)(var16 * 2 + 1);
         }

         if (var12 >= (float)var16) {
            var14 = (byte)(var16 * 2 + 1);
         }
      }

      this.decorations.put(id, new MapDecoration((byte)type, var13, var14, var15));
   }

   public Packet createUpdatePacket(ItemStack stack, World world, PlayerEntity player) {
      SavedMapData.Holder var4 = (SavedMapData.Holder)this.holdersByPlayer.get(player);
      return var4 == null ? null : var4.createUpdatePacket(stack);
   }

   public void markDirty(int x, int y) {
      super.markDirty();

      for(SavedMapData.Holder var4 : this.holders) {
         var4.markDirty(x, y);
      }
   }

   public SavedMapData.Holder addHolder(PlayerEntity player) {
      SavedMapData.Holder var2 = (SavedMapData.Holder)this.holdersByPlayer.get(player);
      if (var2 == null) {
         var2 = new SavedMapData.Holder(player);
         this.holdersByPlayer.put(player, var2);
         this.holders.add(var2);
      }

      return var2;
   }

   public class Holder {
      public final PlayerEntity player;
      private boolean dirty = true;
      private int dirtyMinX = 0;
      private int dirtyMinY = 0;
      private int dirtyMaxX = 127;
      private int dirtyMaxY = 127;
      private int ticks;
      public int step;

      public Holder(PlayerEntity player) {
         this.player = player;
      }

      public Packet createUpdatePacket(ItemStack stack) {
         if (this.dirty) {
            this.dirty = false;
            return new MapDataS2CPacket(
               stack.getMetadata(),
               SavedMapData.this.scale,
               SavedMapData.this.decorations.values(),
               SavedMapData.this.colors,
               this.dirtyMinX,
               this.dirtyMinY,
               this.dirtyMaxX + 1 - this.dirtyMinX,
               this.dirtyMaxY + 1 - this.dirtyMinY
            );
         } else {
            return this.ticks++ % 5 == 0
               ? new MapDataS2CPacket(
                  stack.getMetadata(), SavedMapData.this.scale, SavedMapData.this.decorations.values(), SavedMapData.this.colors, 0, 0, 0, 0
               )
               : null;
         }
      }

      public void markDirty(int x, int y) {
         if (this.dirty) {
            this.dirtyMinX = Math.min(this.dirtyMinX, x);
            this.dirtyMinY = Math.min(this.dirtyMinY, y);
            this.dirtyMaxX = Math.max(this.dirtyMaxX, x);
            this.dirtyMaxY = Math.max(this.dirtyMaxY, y);
         } else {
            this.dirty = true;
            this.dirtyMinX = x;
            this.dirtyMinY = y;
            this.dirtyMaxX = x;
            this.dirtyMaxY = y;
         }
      }
   }
}
