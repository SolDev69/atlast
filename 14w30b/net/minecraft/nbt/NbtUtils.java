package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.text.StringUtils;

public final class NbtUtils {
   public static GameProfile readProfile(NbtCompound nbt) {
      String var1 = null;
      String var2 = null;
      if (nbt.isType("Name", 8)) {
         var1 = nbt.getString("Name");
      }

      if (nbt.isType("Id", 8)) {
         var2 = nbt.getString("Id");
      }

      if (StringUtils.isStringEmpty(var1) && StringUtils.isStringEmpty(var2)) {
         return null;
      } else {
         UUID var3;
         try {
            var3 = UUID.fromString(var2);
         } catch (Throwable var12) {
            var3 = null;
         }

         GameProfile var4 = new GameProfile(var3, var1);
         if (nbt.isType("Properties", 10)) {
            NbtCompound var5 = nbt.getCompound("Properties");

            for(String var7 : var5.getKeys()) {
               NbtList var8 = var5.getList(var7, 10);

               for(int var9 = 0; var9 < var8.size(); ++var9) {
                  NbtCompound var10 = var8.getCompound(var9);
                  String var11 = var10.getString("Value");
                  if (var10.isType("Signature", 8)) {
                     var4.getProperties().put(var7, new Property(var7, var11, var10.getString("Signature")));
                  } else {
                     var4.getProperties().put(var7, new Property(var7, var11));
                  }
               }
            }
         }

         return var4;
      }
   }

   public static NbtCompound writeProfile(NbtCompound nbt, GameProfile profile) {
      if (!StringUtils.isStringEmpty(profile.getName())) {
         nbt.putString("Name", profile.getName());
      }

      if (profile.getId() != null) {
         nbt.putString("Id", profile.getId().toString());
      }

      if (!profile.getProperties().isEmpty()) {
         NbtCompound var2 = new NbtCompound();

         for(String var4 : profile.getProperties().keySet()) {
            NbtList var5 = new NbtList();

            for(Property var7 : profile.getProperties().get(var4)) {
               NbtCompound var8 = new NbtCompound();
               var8.putString("Value", var7.getValue());
               if (var7.hasSignature()) {
                  var8.putString("Signature", var7.getSignature());
               }

               var5.add(var8);
            }

            var2.put(var4, var5);
         }

         nbt.put("Properties", var2);
      }

      return nbt;
   }
}
