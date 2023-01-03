package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.server.PlayerManager;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class DedicatedPlayerManager extends PlayerManager {
   private static final Logger LOGGER = LogManager.getLogger();

   public DedicatedPlayerManager(DedicatedServer server) {
      super(server);
      this.updateViewDistance(server.getPropertyOrDefault("view-distance", 10));
      this.maxPlayerCount = server.getPropertyOrDefault("max-players", 20);
      this.setWhitelistEnabled(server.getPropertyOrDefault("white-list", false));
      if (!server.isSinglePlayer()) {
         this.getPlayerBans().setEnabled(true);
         this.getIpBans().setEnabled(true);
      }

      this.loadPlayerBans();
      this.savePlayerBans();
      this.loadIpBans();
      this.saveIpBans();
      this.loadOps();
      this.loadWhitelist();
      this.saveOps();
      if (!this.getWhitelist().getFile().exists()) {
         this.saveWhitelist();
      }
   }

   @Override
   public void setWhitelistEnabled(boolean enabled) {
      super.setWhitelistEnabled(enabled);
      this.getServer().setProperty("white-list", enabled);
      this.getServer().saveProperties();
   }

   @Override
   public void addOp(GameProfile playerName) {
      super.addOp(playerName);
      this.saveOps();
   }

   @Override
   public void removeOp(GameProfile playerName) {
      super.removeOp(playerName);
      this.saveOps();
   }

   @Override
   public void removeFromWhitelist(GameProfile playerName) {
      super.removeFromWhitelist(playerName);
      this.saveWhitelist();
   }

   @Override
   public void addToWhitelist(GameProfile playerName) {
      super.addToWhitelist(playerName);
      this.saveWhitelist();
   }

   @Override
   public void reloadWhitelist() {
      this.loadWhitelist();
   }

   private void saveIpBans() {
      try {
         this.getIpBans().save();
      } catch (IOException var2) {
         LOGGER.warn("Failed to save ip banlist: ", var2);
      }
   }

   private void savePlayerBans() {
      try {
         this.getPlayerBans().save();
      } catch (IOException var2) {
         LOGGER.warn("Failed to save user banlist: ", var2);
      }
   }

   private void loadIpBans() {
      try {
         this.getIpBans().load();
      } catch (IOException var2) {
         LOGGER.warn("Failed to load ip banlist: ", var2);
      }
   }

   private void loadPlayerBans() {
      try {
         this.getPlayerBans().load();
      } catch (IOException var2) {
         LOGGER.warn("Failed to load user banlist: ", var2);
      }
   }

   private void loadOps() {
      try {
         this.getOps().load();
      } catch (Exception var2) {
         LOGGER.warn("Failed to load operators list: ", var2);
      }
   }

   private void saveOps() {
      try {
         this.getOps().save();
      } catch (Exception var2) {
         LOGGER.warn("Failed to save operators list: ", var2);
      }
   }

   private void loadWhitelist() {
      try {
         this.getWhitelist().load();
      } catch (Exception var2) {
         LOGGER.warn("Failed to load white-list: ", var2);
      }
   }

   private void saveWhitelist() {
      try {
         this.getWhitelist().save();
      } catch (Exception var2) {
         LOGGER.warn("Failed to save white-list: ", var2);
      }
   }

   @Override
   public boolean isWhitelisted(GameProfile playerName) {
      return !this.isWhitelistEnabled() || this.isOp(playerName) || this.getWhitelist().isWhitelisted(playerName);
   }

   public DedicatedServer getServer() {
      return (DedicatedServer)super.getServer();
   }
}
