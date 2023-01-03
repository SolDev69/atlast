package net.minecraft.entity.living.mob.passive;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.village.trade.TradeOffer;
import net.minecraft.world.village.trade.TradeOffers;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public interface Trader {
   void setCustomer(PlayerEntity player);

   PlayerEntity getCustomer();

   TradeOffers getOffers(PlayerEntity player);

   @Environment(EnvType.CLIENT)
   void setOffers(TradeOffers offers);

   void trade(TradeOffer offer);

   void updateOffer(ItemStack stack);

   Text getDisplayName();
}
