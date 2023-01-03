package net.minecraft.client.network;

import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Iterator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.resource.skin.SkinManager;
import net.minecraft.network.packet.s2c.play.PlayerInfoS2CPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.text.Text;
import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerInfo {
   private final GameProfile profile;
   private WorldSettings.GameMode gameMode;
   private int ping;
   private boolean waitingForTextures = false;
   private Identifier skinTexture;
   private Identifier capeTexture;
   private Text displayName;

   public PlayerInfo(GameProfile name) {
      this.profile = name;
   }

   public PlayerInfo(PlayerInfoS2CPacket.Entry entry) {
      this.profile = entry.getProfile();
      this.gameMode = entry.getGameMode();
      this.ping = entry.getPing();
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   public WorldSettings.GameMode getGameMode() {
      return this.gameMode;
   }

   public int getPing() {
      return this.ping;
   }

   protected void setGameMode(WorldSettings.GameMode gameMode) {
      this.gameMode = gameMode;
   }

   protected void setPing(int ping) {
      this.ping = ping;
   }

   public boolean hasSkinTexture() {
      return this.skinTexture != null;
   }

   public String getModelType() {
      PropertyMap var1 = this.profile.getProperties();
      if (var1.containsKey("model")) {
         Iterator var2 = var1.get("model").iterator();
         if (var2.hasNext()) {
            return ((Property)var2.next()).getValue();
         }
      }

      return "default";
   }

   public Identifier getSkinTexture() {
      if (this.skinTexture == null) {
         this.registerTextures();
      }

      return (Identifier)Objects.firstNonNull(this.skinTexture, ClientPlayerEntity.STEVE_TEXTURE);
   }

   public Identifier getCapeTexture() {
      if (this.capeTexture == null) {
         this.registerTextures();
      }

      return this.capeTexture;
   }

   public Team getTeam() {
      return MinecraftClient.getInstance().world.getScoreboard().getTeamOfMember(this.getProfile().getName());
   }

   protected void registerTextures() {
      synchronized(this) {
         if (!this.waitingForTextures) {
            this.waitingForTextures = true;
            MinecraftClient.getInstance().getSkinManager().register(this.profile, new SkinManager.SkinTextureCallback() {
               @Override
               public void textureAvailable(Type type, Identifier c_07ipdbewr) {
                  switch(type) {
                     case SKIN:
                        PlayerInfo.this.skinTexture = c_07ipdbewr;
                        break;
                     case CAPE:
                        PlayerInfo.this.capeTexture = c_07ipdbewr;
                  }
               }
            }, true);
         }
      }
   }

   public void setDisplayName(Text displayName) {
      this.displayName = displayName;
   }

   public Text getDisplayName() {
      return this.displayName;
   }
}
