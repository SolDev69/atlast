package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.StringUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SkullBlockEntity extends BlockEntity {
   private int skullType;
   private int rotation;
   private GameProfile profile = null;

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      nbt.putByte("SkullType", (byte)(this.skullType & 0xFF));
      nbt.putByte("Rot", (byte)(this.rotation & 0xFF));
      if (this.profile != null) {
         NbtCompound var2 = new NbtCompound();
         NbtUtils.writeProfile(var2, this.profile);
         nbt.put("Owner", var2);
      }
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      this.skullType = nbt.getByte("SkullType");
      this.rotation = nbt.getByte("Rot");
      if (this.skullType == 3) {
         if (nbt.isType("Owner", 10)) {
            this.profile = NbtUtils.readProfile(nbt.getCompound("Owner"));
         } else if (nbt.isType("ExtraType", 8)) {
            String var2 = nbt.getString("ExtraType");
            if (!StringUtils.isStringEmpty(var2)) {
               this.profile = new GameProfile(null, var2);
               this.updateProfile();
            }
         }
      }
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   @Override
   public Packet createUpdatePacket() {
      NbtCompound var1 = new NbtCompound();
      this.writeNbt(var1);
      return new BlockEntityUpdateS2CPacket(this.pos, 4, var1);
   }

   public void setSkullType(int type) {
      this.skullType = type;
      this.profile = null;
   }

   public void setProfile(GameProfile profile) {
      this.skullType = 3;
      this.profile = profile;
      this.updateProfile();
   }

   private void updateProfile() {
      this.profile = updateProfile(this.profile);
      this.markDirty();
   }

   public static GameProfile updateProfile(GameProfile profile) {
      if (profile != null && !StringUtils.isStringEmpty(profile.getName())) {
         if (profile.isComplete() && profile.getProperties().containsKey("textures")) {
            return profile;
         } else {
            GameProfile var1 = MinecraftServer.getInstance().getPlayerCache().remove(profile.getName());
            if (var1 == null) {
               return profile;
            } else {
               Property var2 = (Property)Iterables.getFirst(var1.getProperties().get("textures"), null);
               if (var2 == null) {
                  var1 = MinecraftServer.getInstance().getSessionService().fillProfileProperties(var1, true);
               }

               return var1;
            }
         }
      } else {
         return profile;
      }
   }

   public int getType() {
      return this.skullType;
   }

   @Environment(EnvType.CLIENT)
   public int getRotation() {
      return this.rotation;
   }

   public void setRotation(int rotation) {
      this.rotation = rotation;
   }
}
