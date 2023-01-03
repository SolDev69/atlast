package net.minecraft.client.world.villager.trade;

import net.minecraft.entity.living.mob.passive.Trader;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.village.trade.TradeOffer;
import net.minecraft.world.village.trade.TradeOffers;
import net.minecraft.world.village.trade.TraderInventory;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientTrader implements Trader {
   private TraderInventory traderInventory;
   private PlayerEntity player;
   private TradeOffers offers;
   private Text customName;

   public ClientTrader(PlayerEntity player, Text customName) {
      this.player = player;
      this.customName = customName;
      this.traderInventory = new TraderInventory(player, this);
   }

   @Override
   public PlayerEntity getCustomer() {
      return this.player;
   }

   @Override
   public void setCustomer(PlayerEntity player) {
   }

   @Override
   public TradeOffers getOffers(PlayerEntity player) {
      return this.offers;
   }

   @Override
   public void setOffers(TradeOffers offers) {
      this.offers = offers;
   }

   @Override
   public void trade(TradeOffer offer) {
      offer.use();
   }

   @Override
   public void updateOffer(ItemStack stack) {
   }

   @Override
   public Text getDisplayName() {
      return (Text)(this.customName != null ? this.customName : new TranslatableText("entity.Villager.name"));
   }
}
