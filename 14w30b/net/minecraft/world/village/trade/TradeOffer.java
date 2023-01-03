package net.minecraft.world.village.trade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TradeOffer {
   private ItemStack primaryPayment;
   private ItemStack secondaryPayment;
   private ItemStack result;
   private int uses;
   private int maxUses;
   private boolean rewardXp;

   public TradeOffer(NbtCompound nbt) {
      this.readNbt(nbt);
   }

   public TradeOffer(ItemStack primaryPayment, ItemStack secondaryPayment, ItemStack result) {
      this(primaryPayment, secondaryPayment, result, 0, 7);
   }

   public TradeOffer(ItemStack primaryPayment, ItemStack secondaryPayment, ItemStack result, int uses, int maxUses) {
      this.primaryPayment = primaryPayment;
      this.secondaryPayment = secondaryPayment;
      this.result = result;
      this.uses = uses;
      this.maxUses = maxUses;
      this.rewardXp = true;
   }

   public TradeOffer(ItemStack payment, ItemStack result) {
      this(payment, null, result);
   }

   public TradeOffer(ItemStack payment, Item result) {
      this(payment, new ItemStack(result));
   }

   public ItemStack getPrimaryPayment() {
      return this.primaryPayment;
   }

   public ItemStack getSecondaryPayment() {
      return this.secondaryPayment;
   }

   public boolean hasSecondaryPayment() {
      return this.secondaryPayment != null;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public int getUses() {
      return this.uses;
   }

   public int getMaxUses() {
      return this.maxUses;
   }

   public void use() {
      ++this.uses;
   }

   public void increaseMaxUses(int uses) {
      this.maxUses += uses;
   }

   public boolean isDisabled() {
      return this.uses >= this.maxUses;
   }

   @Environment(EnvType.CLIENT)
   public void clearUses() {
      this.uses = this.maxUses;
   }

   public boolean rewardXp() {
      return this.rewardXp;
   }

   public void readNbt(NbtCompound nbt) {
      NbtCompound var2 = nbt.getCompound("buy");
      this.primaryPayment = ItemStack.fromNbt(var2);
      NbtCompound var3 = nbt.getCompound("sell");
      this.result = ItemStack.fromNbt(var3);
      if (nbt.isType("buyB", 10)) {
         this.secondaryPayment = ItemStack.fromNbt(nbt.getCompound("buyB"));
      }

      if (nbt.isType("uses", 99)) {
         this.uses = nbt.getInt("uses");
      }

      if (nbt.isType("maxUses", 99)) {
         this.maxUses = nbt.getInt("maxUses");
      } else {
         this.maxUses = 7;
      }

      if (nbt.isType("rewardExp", 1)) {
         this.rewardXp = nbt.getBoolean("rewardExp");
      } else {
         this.rewardXp = true;
      }
   }

   public NbtCompound toNbt() {
      NbtCompound var1 = new NbtCompound();
      var1.put("buy", this.primaryPayment.writeNbt(new NbtCompound()));
      var1.put("sell", this.result.writeNbt(new NbtCompound()));
      if (this.secondaryPayment != null) {
         var1.put("buyB", this.secondaryPayment.writeNbt(new NbtCompound()));
      }

      var1.putInt("uses", this.uses);
      var1.putInt("maxUses", this.maxUses);
      var1.putBoolean("rewardExp", this.rewardXp);
      return var1;
   }
}
