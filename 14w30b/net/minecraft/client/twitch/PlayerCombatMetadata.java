package net.minecraft.client.twitch;

import net.minecraft.entity.living.LivingEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerCombatMetadata extends StreamMetadata {
   public PlayerCombatMetadata(LivingEntity player, LivingEntity opponent) {
      super("player_combat");
      this.put("player", player.getName());
      if (opponent != null) {
         this.put("primary_opponent", opponent.getName());
      }

      if (opponent != null) {
         this.setMessage("Combat between " + player.getName() + " and " + opponent.getName());
      } else {
         this.setMessage("Combat between " + player.getName() + " and others");
      }
   }
}
