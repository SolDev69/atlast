package net.minecraft.server.dedicated;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.BanEntry;
import net.minecraft.server.IpBanEntry;
import net.minecraft.server.IpBans;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OpEntry;
import net.minecraft.server.Ops;
import net.minecraft.server.PlayerBanEntry;
import net.minecraft.server.PlayerBans;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.text.StringUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserConverter {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File IP_BANS_FILE = new File("banned-ips.txt");
   public static final File PLAYER_BANS_FILE = new File("banned-players.txt");
   public static final File OPS_FILE = new File("ops.txt");
   public static final File WHITELIST_FILE = new File("white-list.txt");

   @Environment(EnvType.SERVER)
   static List parsePlayerList(File file, Map data) {
      List var2 = Files.readLines(file, Charsets.UTF_8);

      for(String var4 : var2) {
         var4 = var4.trim();
         if (!var4.startsWith("#") && var4.length() >= 1) {
            String[] var5 = var4.split("\\|");
            data.put(var5[0].toLowerCase(Locale.ROOT), var5);
         }
      }

      return var2;
   }

   private static void lookupProfiles(MinecraftServer server, Collection players, ProfileLookupCallback callback) {
      String[] var3 = (String[])Iterators.toArray(Iterators.filter(players.iterator(), new Predicate() {
         public boolean apply(String string) {
            return !StringUtils.isStringEmpty(string);
         }
      }), String.class);
      if (server.isOnlineMode()) {
         server.getGameProfileRepository().findProfilesByNames(var3, Agent.MINECRAFT, callback);
      } else {
         for(String var7 : var3) {
            UUID var8 = PlayerEntity.getUuid(new GameProfile(null, var7));
            GameProfile var9 = new GameProfile(var8, var7);
            callback.onProfileLookupSucceeded(var9);
         }
      }
   }

   @Environment(EnvType.SERVER)
   public static boolean convertPlayerBans(MinecraftServer server) {
      final PlayerBans var1 = new PlayerBans(PlayerManager.PLAYER_BANS_FILE);
      if (PLAYER_BANS_FILE.exists() && PLAYER_BANS_FILE.isFile()) {
         if (var1.getFile().exists()) {
            try {
               var1.load();
            } catch (FileNotFoundException var6) {
               LOGGER.warn("Could not load existing file " + var1.getFile().getName(), var6);
            }
         }

         try {
            final HashMap var2 = Maps.newHashMap();
            parsePlayerList(PLAYER_BANS_FILE, var2);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile profile) {
                  server.getPlayerCache().add(profile);
                  String[] var2x = (String[])var2.get(profile.getName().toLowerCase(Locale.ROOT));
                  if (var2x == null) {
                     UserConverter.LOGGER.warn("Could not convert user banlist entry for " + profile.getName());
                     throw new UserConverter.ConversionException("Profile not in the conversionlist");
                  } else {
                     Date var3 = var2x.length > 1 ? UserConverter.parseDateOrDefault(var2x[1], null) : null;
                     String var4 = var2x.length > 2 ? var2x[2] : null;
                     Date var5 = var2x.length > 3 ? UserConverter.parseDateOrDefault(var2x[3], null) : null;
                     String var6 = var2x.length > 4 ? var2x[4] : null;
                     var1.add(new PlayerBanEntry(profile, var3, var4, var5, var6));
                  }
               }

               public void onProfileLookupFailed(GameProfile profile, Exception e) {
                  UserConverter.LOGGER.warn("Could not lookup user banlist entry for " + profile.getName(), e);
                  if (!(e instanceof ProfileNotFoundException)) {
                     throw new UserConverter.ConversionException("Could not request user " + profile.getName() + " from backend systems", e);
                  }
               }
            };
            lookupProfiles(server, var2.keySet(), var3);
            var1.save();
            markUserListConverted(PLAYER_BANS_FILE);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old user banlist to convert it!", var4);
            return false;
         } catch (UserConverter.ConversionException var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   @Environment(EnvType.SERVER)
   public static boolean convertIpBans(MinecraftServer server) {
      IpBans var1 = new IpBans(PlayerManager.IP_BANS_FILE);
      if (IP_BANS_FILE.exists() && IP_BANS_FILE.isFile()) {
         if (var1.getFile().exists()) {
            try {
               var1.load();
            } catch (FileNotFoundException var11) {
               LOGGER.warn("Could not load existing file " + var1.getFile().getName(), var11);
            }
         }

         try {
            HashMap var2 = Maps.newHashMap();
            parsePlayerList(IP_BANS_FILE, var2);

            for(String var4 : var2.keySet()) {
               String[] var5 = (String[])var2.get(var4);
               Date var6 = var5.length > 1 ? parseDateOrDefault(var5[1], null) : null;
               String var7 = var5.length > 2 ? var5[2] : null;
               Date var8 = var5.length > 3 ? parseDateOrDefault(var5[3], null) : null;
               String var9 = var5.length > 4 ? var5[4] : null;
               var1.add(new IpBanEntry(var4, var6, var7, var8, var9));
            }

            var1.save();
            markUserListConverted(IP_BANS_FILE);
            return true;
         } catch (IOException var10) {
            LOGGER.warn("Could not parse old ip banlist to convert it!", var10);
            return false;
         }
      } else {
         return true;
      }
   }

   @Environment(EnvType.SERVER)
   public static boolean convertOps(MinecraftServer server) {
      final Ops var1 = new Ops(PlayerManager.OPS_FILE);
      if (OPS_FILE.exists() && OPS_FILE.isFile()) {
         if (var1.getFile().exists()) {
            try {
               var1.load();
            } catch (FileNotFoundException var6) {
               LOGGER.warn("Could not load existing file " + var1.getFile().getName(), var6);
            }
         }

         try {
            List var2 = Files.readLines(OPS_FILE, Charsets.UTF_8);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile profile) {
                  server.getPlayerCache().add(profile);
                  var1.add(new OpEntry(profile, server.getOpPermissionLevel()));
               }

               public void onProfileLookupFailed(GameProfile profile, Exception e) {
                  UserConverter.LOGGER.warn("Could not lookup oplist entry for " + profile.getName(), e);
                  if (!(e instanceof ProfileNotFoundException)) {
                     throw new UserConverter.ConversionException("Could not request user " + profile.getName() + " from backend systems", e);
                  }
               }
            };
            lookupProfiles(server, var2, var3);
            var1.save();
            markUserListConverted(OPS_FILE);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old oplist to convert it!", var4);
            return false;
         } catch (UserConverter.ConversionException var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   @Environment(EnvType.SERVER)
   public static boolean convertWhitelist(MinecraftServer server) {
      final Whitelist var1 = new Whitelist(PlayerManager.WHITELIST_FILE);
      if (WHITELIST_FILE.exists() && WHITELIST_FILE.isFile()) {
         if (var1.getFile().exists()) {
            try {
               var1.load();
            } catch (FileNotFoundException var6) {
               LOGGER.warn("Could not load existing file " + var1.getFile().getName(), var6);
            }
         }

         try {
            List var2 = Files.readLines(WHITELIST_FILE, Charsets.UTF_8);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile profile) {
                  server.getPlayerCache().add(profile);
                  var1.add(new WhitelistEntry(profile));
               }

               public void onProfileLookupFailed(GameProfile profile, Exception e) {
                  UserConverter.LOGGER.warn("Could not lookup user whitelist entry for " + profile.getName(), e);
                  if (!(e instanceof ProfileNotFoundException)) {
                     throw new UserConverter.ConversionException("Could not request user " + profile.getName() + " from backend systems", e);
                  }
               }
            };
            lookupProfiles(server, var2, var3);
            var1.save();
            markUserListConverted(WHITELIST_FILE);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old whitelist to convert it!", var4);
            return false;
         } catch (UserConverter.ConversionException var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static String convertMobOwner(String playerName) {
      if (!StringUtils.isStringEmpty(playerName) && playerName.length() <= 16) {
         final MinecraftServer var1 = MinecraftServer.getInstance();
         GameProfile var2 = var1.getPlayerCache().remove(playerName);
         if (var2 != null && var2.getId() != null) {
            return var2.getId().toString();
         } else if (!var1.isSinglePlayer() && var1.isOnlineMode()) {
            final ArrayList var3 = Lists.newArrayList();
            ProfileLookupCallback var4 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile profile) {
                  var1.getPlayerCache().add(profile);
                  var3.add(profile);
               }

               public void onProfileLookupFailed(GameProfile profile, Exception e) {
                  UserConverter.LOGGER.warn("Could not lookup user whitelist entry for " + profile.getName(), e);
               }
            };
            lookupProfiles(var1, Lists.newArrayList(new String[]{playerName}), var4);
            return var3.size() > 0 && ((GameProfile)var3.get(0)).getId() != null ? ((GameProfile)var3.get(0)).getId().toString() : "";
         } else {
            return PlayerEntity.getUuid(new GameProfile(null, playerName)).toString();
         }
      } else {
         return playerName;
      }
   }

   @Environment(EnvType.SERVER)
   public static boolean convertPlayers(DedicatedServer server, ServerProperties properties) {
      final File var2 = getWorldPlayersDir(properties);
      final File var3 = new File(var2.getParentFile(), "playerdata");
      final File var4 = new File(var2.getParentFile(), "unknownplayers");
      if (var2.exists() && var2.isDirectory()) {
         File[] var5 = var2.listFiles();
         ArrayList var6 = Lists.newArrayList();

         for(File var10 : var5) {
            String var11 = var10.getName();
            if (var11.toLowerCase(Locale.ROOT).endsWith(".dat")) {
               String var12 = var11.substring(0, var11.length() - ".dat".length());
               if (var12.length() > 0) {
                  var6.add(var12);
               }
            }
         }

         try {
            final String[] var14 = var6.toArray(new String[var6.size()]);
            ProfileLookupCallback var15 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile profile) {
                  server.getPlayerCache().add(profile);
                  UUID var2x = profile.getId();
                  if (var2x == null) {
                     throw new UserConverter.ConversionException("Missing UUID for user profile " + profile.getName());
                  } else {
                     this.convertPlayerFile(var3, this.getPlayerFileName(profile), var2x.toString());
                  }
               }

               public void onProfileLookupFailed(GameProfile profile, Exception e) {
                  UserConverter.LOGGER.warn("Could not lookup user uuid for " + profile.getName(), e);
                  if (e instanceof ProfileNotFoundException) {
                     String var3x = this.getPlayerFileName(profile);
                     this.convertPlayerFile(var4, var3x, var3x);
                  } else {
                     throw new UserConverter.ConversionException("Could not request user " + profile.getName() + " from backend systems", e);
                  }
               }

               private void convertPlayerFile(File playersDir, String playerFileName, String playerUuid) {
                  File var4x = new File(var2, playerFileName + ".dat");
                  File var5 = new File(playersDir, playerUuid + ".dat");
                  UserConverter.mkdirs(playersDir);
                  if (!var4x.renameTo(var5)) {
                     throw new UserConverter.ConversionException("Could not convert file for " + playerFileName);
                  }
               }

               private String getPlayerFileName(GameProfile profile) {
                  String var2x = null;

                  for(int var3x = 0; var3x < var14.length; ++var3x) {
                     if (var14[var3x] != null && var14[var3x].equalsIgnoreCase(profile.getName())) {
                        var2x = var14[var3x];
                        break;
                     }
                  }

                  if (var2x == null) {
                     throw new UserConverter.ConversionException("Could not find the filename for " + profile.getName() + " anymore");
                  } else {
                     return var2x;
                  }
               }
            };
            lookupProfiles(server, Lists.newArrayList(var14), var15);
            return true;
         } catch (UserConverter.ConversionException var13) {
            LOGGER.error("Conversion failed, please try again later", var13);
            return false;
         }
      } else {
         return true;
      }
   }

   @Environment(EnvType.SERVER)
   private static void mkdirs(File dir) {
      if (dir.exists()) {
         if (!dir.isDirectory()) {
            throw new UserConverter.ConversionException("Can't create directory " + dir.getName() + " in world save directory.");
         }
      } else if (!dir.mkdirs()) {
         throw new UserConverter.ConversionException("Can't create directory " + dir.getName() + " in world save directory.");
      }
   }

   @Environment(EnvType.SERVER)
   public static boolean areUsersConverted(ServerProperties properties) {
      boolean var1 = areUserListsConverted(properties);
      return var1 && arePlayersConverted(properties);
   }

   @Environment(EnvType.SERVER)
   private static boolean areUserListsConverted(ServerProperties properties) {
      boolean var1 = false;
      if (PLAYER_BANS_FILE.exists() && PLAYER_BANS_FILE.isFile()) {
         var1 = true;
      }

      boolean var2 = false;
      if (IP_BANS_FILE.exists() && IP_BANS_FILE.isFile()) {
         var2 = true;
      }

      boolean var3 = false;
      if (OPS_FILE.exists() && OPS_FILE.isFile()) {
         var3 = true;
      }

      boolean var4 = false;
      if (WHITELIST_FILE.exists() && WHITELIST_FILE.isFile()) {
         var4 = true;
      }

      if (!var1 && !var2 && !var3 && !var4) {
         return true;
      } else {
         LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
         LOGGER.warn("** please remove the following files and restart the server:");
         if (var1) {
            LOGGER.warn("* " + PLAYER_BANS_FILE.getName());
         }

         if (var2) {
            LOGGER.warn("* " + IP_BANS_FILE.getName());
         }

         if (var3) {
            LOGGER.warn("* " + OPS_FILE.getName());
         }

         if (var4) {
            LOGGER.warn("* " + WHITELIST_FILE.getName());
         }

         return false;
      }
   }

   @Environment(EnvType.SERVER)
   private static boolean arePlayersConverted(ServerProperties properties) {
      File var1 = getWorldPlayersDir(properties);
      if (!var1.exists() || !var1.isDirectory() || var1.list().length <= 0 && var1.delete()) {
         return true;
      } else {
         LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
         LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
         LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", new Object[]{var1.getPath()});
         return false;
      }
   }

   @Environment(EnvType.SERVER)
   private static File getWorldPlayersDir(ServerProperties properties) {
      String var1 = properties.getOrDefault("level-name", "world");
      File var2 = new File(var1);
      return new File(var2, "players");
   }

   @Environment(EnvType.SERVER)
   private static void markUserListConverted(File file) {
      File var1 = new File(file.getName() + ".converted");
      file.renameTo(var1);
   }

   @Environment(EnvType.SERVER)
   private static Date parseDateOrDefault(String s, Date defaultValue) {
      Date var2;
      try {
         var2 = BanEntry.DATE_FORMAT.parse(s);
      } catch (ParseException var4) {
         var2 = defaultValue;
      }

      return var2;
   }

   @Environment(EnvType.SERVER)
   static class ConversionException extends RuntimeException {
      private ConversionException(String message, Throwable t) {
         super(message, t);
      }

      private ConversionException(String string) {
         super(string);
      }
   }
}
