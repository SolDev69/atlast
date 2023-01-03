package net.minecraft.world.village.trade;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TradeOffers extends ArrayList {
   public TradeOffers() {
   }

   public TradeOffers(NbtCompound nbt) {
      this.readNbt(nbt);
   }

   public TradeOffer get(ItemStack primaryPayment, ItemStack secondaryPayment, int index) {
      if (index > 0 && index < this.size()) {
         TradeOffer var6 = (TradeOffer)this.get(index);
         return !ItemStack.matchesItem(primaryPayment, var6.getPrimaryPayment())
               || (secondaryPayment != null || var6.hasSecondaryPayment())
                  && (!var6.hasSecondaryPayment() || !ItemStack.matchesItem(secondaryPayment, var6.getSecondaryPayment()))
               || primaryPayment.size < var6.getPrimaryPayment().size
               || var6.hasSecondaryPayment() && secondaryPayment.size < var6.getSecondaryPayment().size
            ? null
            : var6;
      } else {
         for(int var4 = 0; var4 < this.size(); ++var4) {
            TradeOffer var5 = (TradeOffer)this.get(var4);
            if (ItemStack.matchesItem(primaryPayment, var5.getPrimaryPayment())
               && primaryPayment.size >= var5.getPrimaryPayment().size
               && (
                  !var5.hasSecondaryPayment() && secondaryPayment == null
                     || var5.hasSecondaryPayment()
                        && ItemStack.matchesItem(secondaryPayment, var5.getSecondaryPayment())
                        && secondaryPayment.size >= var5.getSecondaryPayment().size
               )) {
               return var5;
            }
         }

         return null;
      }
   }

   public void serialize(PacketByteBuf buffer) {
      buffer.writeByte((byte)(this.size() & 0xFF));

      for(int var2 = 0; var2 < this.size(); ++var2) {
         TradeOffer var3 = (TradeOffer)this.get(var2);
         buffer.writeItemStack(var3.getPrimaryPayment());
         buffer.writeItemStack(var3.getResult());
         ItemStack var4 = var3.getSecondaryPayment();
         buffer.writeBoolean(var4 != null);
         if (var4 != null) {
            buffer.writeItemStack(var4);
         }

         buffer.writeBoolean(var3.isDisabled());
         buffer.writeInt(var3.getUses());
         buffer.writeInt(var3.getMaxUses());
      }
   }

   @Environment(EnvType.CLIENT)
   public static TradeOffers deserialize(PacketByteBuf buffer) {
      TradeOffers var1 = new TradeOffers();
      int var2 = buffer.readByte() & 255;

      for(int var3 = 0; var3 < var2; ++var3) {
         ItemStack var4 = buffer.readItemStack();
         ItemStack var5 = buffer.readItemStack();
         ItemStack var6 = null;
         if (buffer.readBoolean()) {
            var6 = buffer.readItemStack();
         }

         boolean var7 = buffer.readBoolean();
         int var8 = buffer.readInt();
         int var9 = buffer.readInt();
         TradeOffer var10 = new TradeOffer(var4, var6, var5, var8, var9);
         if (var7) {
            var10.clearUses();
         }

         var1.add(var10);
      }

      return var1;
   }

   public void readNbt(NbtCompound nbt) {
      NbtList var2 = nbt.getList("Recipes", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NbtCompound var4 = var2.getCompound(var3);
         this.add(new TradeOffer(var4));
      }
   }

   public NbtCompound toNbt() {
      NbtCompound var1 = new NbtCompound();
      NbtList var2 = new NbtList();

      for(int var3 = 0; var3 < this.size(); ++var3) {
         TradeOffer var4 = (TradeOffer)this.get(var3);
         var2.add(var4.toNbt());
      }

      var1.put("Recipes", var2);
      return var1;
   }
}
