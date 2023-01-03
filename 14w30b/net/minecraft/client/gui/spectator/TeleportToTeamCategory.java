package net.minecraft.client.gui.spectator;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.resource.Identifier;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TeleportToTeamCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private final List items = Lists.newArrayList();

   public TeleportToTeamCategory() {
      MinecraftClient var1 = MinecraftClient.getInstance();

      for(Team var3 : var1.world.getScoreboard().getTeams()) {
         this.items.add(new TeleportToTeamCategory.TeamMenuItem(var3));
      }
   }

   @Override
   public List getItems() {
      return this.items;
   }

   @Override
   public Text getPrompt() {
      return new LiteralText("Select a team to teleport to");
   }

   @Override
   public void select(SpectatorMenu hud) {
      hud.setCategory(this);
   }

   @Override
   public Text getDisplayName() {
      return new LiteralText("Teleport to team member");
   }

   @Override
   public void render(float tickDelta, int slot) {
      MinecraftClient.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_WIDGETS_TEXTURE);
      GuiElement.drawTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   @Override
   public boolean isEnabled() {
      for(SpectatorMenuItem var2 : this.items) {
         if (var2.isEnabled()) {
            return true;
         }
      }

      return false;
   }

   @Environment(EnvType.CLIENT)
   class TeamMenuItem implements SpectatorMenuItem {
      private final Team team;
      private final Identifier textures;
      private final List players;

      public TeamMenuItem(Team team) {
         this.team = team;
         this.players = Lists.newArrayList();

         for(String var4 : team.getMembers()) {
            PlayerInfo var5 = MinecraftClient.getInstance().getNetworkHandler().getOnlinePlayer(var4);
            if (var5 != null) {
               this.players.add(var5);
            }
         }

         if (!this.players.isEmpty()) {
            String var6 = ((PlayerInfo)this.players.get(new Random().nextInt(this.players.size()))).getProfile().getName();
            this.textures = ClientPlayerEntity.getSkinTextureId(var6);
            ClientPlayerEntity.registerSkinTexture(this.textures, var6);
         } else {
            this.textures = ClientPlayerEntity.STEVE_TEXTURE;
         }
      }

      @Override
      public void select(SpectatorMenu hud) {
         hud.setCategory(new TeleportToPlayerCategory(this.players));
      }

      @Override
      public Text getDisplayName() {
         return new LiteralText(this.team.getDisplayName());
      }

      @Override
      public void render(float tickDelta, int slot) {
         int var3 = -1;
         String var4 = TextRenderer.isolateFormatting(this.team.getPrefix());
         if (var4.length() >= 2) {
            var3 = MinecraftClient.getInstance().textRenderer.getColor(var4.charAt(1));
         }

         if (var3 >= 0) {
            float var5 = (float)(var3 >> 16 & 0xFF) / 255.0F;
            float var6 = (float)(var3 >> 8 & 0xFF) / 255.0F;
            float var7 = (float)(var3 & 0xFF) / 255.0F;
            GuiElement.fill(1, 1, 15, 15, MathHelper.packRGB(var5 * tickDelta, var6 * tickDelta, var7 * tickDelta) | slot << 24);
         }

         MinecraftClient.getInstance().getTextureManager().bind(this.textures);
         GlStateManager.color4f(tickDelta, tickDelta, tickDelta, (float)slot / 255.0F);
         GuiElement.drawTexture(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
         GuiElement.drawTexture(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      }

      @Override
      public boolean isEnabled() {
         return !this.players.isEmpty();
      }
   }
}
