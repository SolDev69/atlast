package net.minecraft.client.gui.spectator;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TeleportToPlayerCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final Ordering PLAYER_ORDERING = Ordering.from(new Comparator() {
      public int compare(PlayerInfo c_38vawxpad, PlayerInfo c_38vawxpad2) {
         return ComparisonChain.start().compare(c_38vawxpad.getProfile().getId(), c_38vawxpad2.getProfile().getId()).result();
      }
   });
   private final List items = Lists.newArrayList();

   public TeleportToPlayerCategory() {
      this(PLAYER_ORDERING.sortedCopy(MinecraftClient.getInstance().getNetworkHandler().getOnlinePlayers()));
   }

   public TeleportToPlayerCategory(Collection players) {
      for(PlayerInfo var3 : PLAYER_ORDERING.sortedCopy(players)) {
         if (var3.getGameMode() != WorldSettings.GameMode.SPECTATOR) {
            this.items.add(new PlayerMenuItem(var3.getProfile()));
         }
      }
   }

   @Override
   public List getItems() {
      return this.items;
   }

   @Override
   public Text getPrompt() {
      return new LiteralText("Select a player to teleport to");
   }

   @Override
   public void select(SpectatorMenu hud) {
      hud.setCategory(this);
   }

   @Override
   public Text getDisplayName() {
      return new LiteralText("Teleport to player");
   }

   @Override
   public void render(float tickDelta, int slot) {
      MinecraftClient.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_WIDGETS_TEXTURE);
      GuiElement.drawTexture(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   @Override
   public boolean isEnabled() {
      return !this.items.isEmpty();
   }
}
