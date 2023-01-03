package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Connection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameEventS2CPacket;
import net.minecraft.network.packet.s2c.play.LoginS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerInfoS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPointS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerXpS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeS2CPacket;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import net.minecraft.server.scoreboard.ServerScoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Formatting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.storage.PlayerDataStorage;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerManager {
   public static final File PLAYER_BANS_FILE = new File("banned-players.json");
   public static final File IP_BANS_FILE = new File("banned-ips.json");
   public static final File OPS_FILE = new File("ops.json");
   public static final File WHITELIST_FILE = new File("whitelist.json");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   public final List players = Lists.newArrayList();
   public final Map playersByUuid = Maps.newHashMap();
   private final PlayerBans bans = new PlayerBans(PLAYER_BANS_FILE);
   private final IpBans ipBans = new IpBans(IP_BANS_FILE);
   private final Ops ops = new Ops(OPS_FILE);
   private final Whitelist whitelist = new Whitelist(WHITELIST_FILE);
   private final Map statHandlers = Maps.newHashMap();
   private PlayerDataStorage dataHandler;
   private boolean whitelistEnabled;
   protected int maxPlayerCount;
   private int viewDistance;
   private WorldSettings.GameMode defaultGamemode;
   private boolean allowCommands;
   private int pingUpdateTime;

   public PlayerManager(MinecraftServer server) {
      this.server = server;
      this.bans.setEnabled(false);
      this.ipBans.setEnabled(false);
      this.maxPlayerCount = 8;
   }

   public void onLogin(Connection connection, ServerPlayerEntity player) {
      GameProfile var3 = player.getGameProfile();
      PlayerCache var4 = this.server.getPlayerCache();
      GameProfile var5 = var4.getProfile(var3.getId());
      String var6 = var5 == null ? var3.getName() : var5.getName();
      var4.add(var3);
      NbtCompound var7 = this.loadSinglePlayerData(player);
      player.setWorld(this.server.getWorld(player.dimensionId));
      player.interactionManager.setWorld((ServerWorld)player.world);
      String var8 = "local";
      if (connection.getAddress() != null) {
         var8 = connection.getAddress().toString();
      }

      LOGGER.info(
         player.getName() + "[" + var8 + "] logged in with entity id " + player.getNetworkId() + " at (" + player.x + ", " + player.y + ", " + player.z + ")"
      );
      ServerWorld var9 = this.server.getWorld(player.dimensionId);
      WorldData var10 = var9.getData();
      BlockPos var11 = var9.getSpawnPoint();
      this.copyGameMode(player, null, var9);
      ServerPlayNetworkHandler var12 = new ServerPlayNetworkHandler(this.server, connection, player);
      var12.sendPacket(
         new LoginS2CPacket(
            player.getNetworkId(),
            player.interactionManager.getGameMode(),
            var10.isHardcore(),
            var9.dimension.getId(),
            var9.getDifficulty(),
            this.getMaxCount(),
            var10.getGeneratorType(),
            var9.getGameRules().getBoolean("reducedDebugInfo")
         )
      );
      var12.sendPacket(new CustomPayloadS2CPacket("MC|Brand", this.getServer().getServerModName().getBytes(Charsets.UTF_8)));
      var12.sendPacket(new DifficultyS2CPacket(var10.getDifficulty(), var10.isDifficultyLocked()));
      var12.sendPacket(new PlayerSpawnPointS2CPacket(var11));
      var12.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
      var12.sendPacket(new SelectSlotS2CPacket(player.inventory.selectedSlot));
      player.getStatHandler().updateStatSet();
      player.getStatHandler().sendAchievements(player);
      this.updateScoreboard((ServerScoreboard)var9.getScoreboard(), player);
      this.server.forcePlayerSampleUpdate();
      TranslatableText var13;
      if (!player.getName().equalsIgnoreCase(var6)) {
         var13 = new TranslatableText("multiplayer.player.joined.renamed", player.getDisplayName(), var6);
      } else {
         var13 = new TranslatableText("multiplayer.player.joined", player.getDisplayName());
      }

      var13.getStyle().setColor(Formatting.YELLOW);
      this.sendSystemMessage(var13);
      this.add(player);
      var12.teleport(player.x, player.y, player.z, player.yaw, player.pitch);
      this.sendWorldInfo(player, var9);
      if (this.server.getResourcePackUrl().length() > 0) {
         player.sendResourcePack(this.server.getResourcePackUrl());
      }

      for(StatusEffectInstance var15 : player.getStatusEffects()) {
         var12.sendPacket(new EntityStatusEffectS2CPacket(player.getNetworkId(), var15));
      }

      player.listenToScreenHandler();
      if (var7 != null && var7.isType("Riding", 10)) {
         Entity var16 = Entities.create(var7.getCompound("Riding"), var9);
         if (var16 != null) {
            var16.teleporting = true;
            var9.addEntity(var16);
            player.startRiding(var16);
            var16.teleporting = false;
         }
      }
   }

   protected void updateScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player) {
      HashSet var3 = Sets.newHashSet();

      for(Team var5 : scoreboard.getTeams()) {
         player.networkHandler.sendPacket(new TeamS2CPacket(var5, 0));
      }

      for(int var9 = 0; var9 < 19; ++var9) {
         ScoreboardObjective var10 = scoreboard.getDisplayObjective(var9);
         if (var10 != null && !var3.contains(var10)) {
            for(Packet var8 : scoreboard.createStartDisplayingObjectivePackets(var10)) {
               player.networkHandler.sendPacket(var8);
            }

            var3.add(var10);
         }
      }
   }

   public void onWorldsLoaded(ServerWorld[] worlds) {
      this.dataHandler = worlds[0].getStorage().getPlayerDataStorage();
      worlds[0].getWorldBorder().addListener(new WorldBorderListener() {
         @Override
         public void onSizeChanged(WorldBorder border, double size) {
            PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_SIZE));
         }

         @Override
         public void onSizeChanged(WorldBorder c_06ryzvjmf, double d, double e, int i) {
            PlayerManager.this.sendToAll(new WorldBorderS2CPacket(c_06ryzvjmf, WorldBorderS2CPacket.Type.LERP_SIZE));
         }

         @Override
         public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
            PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_CENTER));
         }

         @Override
         public void onWarningTimeChanged(WorldBorder border, int warningTime) {
            PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_WARNING_TIME));
         }

         @Override
         public void onWarningBlocksChanged(WorldBorder border, int warningBlocks) {
            PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_WARNING_BLOCKS));
         }

         @Override
         public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
         }

         @Override
         public void onSafeZoneChanged(WorldBorder border, double safeZone) {
         }
      });
   }

   public void onChangedDimension(ServerPlayerEntity player, ServerWorld prevWorld) {
      ServerWorld var3 = player.getServerWorld();
      if (prevWorld != null) {
         prevWorld.getChunkMap().onPlayerRemoved(player);
      }

      var3.getChunkMap().onPlayerAdded(player);
      var3.chunkCache.loadChunk((int)player.x >> 4, (int)player.z >> 4);
   }

   public int getViewDistance() {
      return ChunkMap.getViewDistance(this.getChunkViewDistance());
   }

   public NbtCompound loadSinglePlayerData(ServerPlayerEntity player) {
      NbtCompound var2 = this.server.worlds[0].getData().getPlayerData();
      NbtCompound var3;
      if (player.getName().equals(this.server.getUserName()) && var2 != null) {
         player.readEntityNbt(var2);
         var3 = var2;
         LOGGER.debug("loading single player");
      } else {
         var3 = this.dataHandler.loadPlayerData(player);
      }

      return var3;
   }

   protected void saveData(ServerPlayerEntity player) {
      this.dataHandler.savePlayerData(player);
      ServerStatHandler var2 = (ServerStatHandler)this.statHandlers.get(player.getUuid());
      if (var2 != null) {
         var2.save();
      }
   }

   public void add(ServerPlayerEntity player) {
      this.players.add(player);
      this.playersByUuid.put(player.getUuid(), player);
      this.sendToAll(new PlayerInfoS2CPacket(PlayerInfoS2CPacket.Action.ADD_PLAYER, player));
      ServerWorld var2 = this.server.getWorld(player.dimensionId);
      var2.addEntity(player);
      this.onChangedDimension(player, null);

      for(int var3 = 0; var3 < this.players.size(); ++var3) {
         ServerPlayerEntity var4 = (ServerPlayerEntity)this.players.get(var3);
         player.networkHandler.sendPacket(new PlayerInfoS2CPacket(PlayerInfoS2CPacket.Action.ADD_PLAYER, var4));
      }
   }

   public void updateTrackedPos(ServerPlayerEntity player) {
      player.getServerWorld().getChunkMap().onPlayerMoved(player);
   }

   public void remove(ServerPlayerEntity player) {
      player.incrementStat(Stats.GAMES_LEFT);
      this.saveData(player);
      ServerWorld var2 = player.getServerWorld();
      if (player.vehicle != null) {
         var2.removeEntityNow(player.vehicle);
         LOGGER.debug("removing player mount");
      }

      var2.removeEntity(player);
      var2.getChunkMap().onPlayerRemoved(player);
      this.players.remove(player);
      this.playersByUuid.remove(player.getUuid());
      this.statHandlers.remove(player.getUuid());
      this.sendToAll(new PlayerInfoS2CPacket(PlayerInfoS2CPacket.Action.REMOVE_PLAYER, player));
   }

   public String canLogin(SocketAddress address, GameProfile profile) {
      if (this.bans.isBanned(profile)) {
         PlayerBanEntry var5 = (PlayerBanEntry)this.bans.get(profile);
         String var6 = "You are banned from this server!\nReason: " + var5.getReason();
         if (var5.getExpirationDate() != null) {
            var6 = var6 + "\nYour ban will be removed on " + DATE_FORMAT.format(var5.getExpirationDate());
         }

         return var6;
      } else if (!this.isWhitelisted(profile)) {
         return "You are not white-listed on this server!";
      } else if (this.ipBans.isBanned(address)) {
         IpBanEntry var3 = this.ipBans.get(address);
         String var4 = "Your IP address is banned from this server!\nReason: " + var3.getReason();
         if (var3.getExpirationDate() != null) {
            var4 = var4 + "\nYour ban will be removed on " + DATE_FORMAT.format(var3.getExpirationDate());
         }

         return var4;
      } else {
         return this.players.size() >= this.maxPlayerCount ? "The server is full!" : null;
      }
   }

   public ServerPlayerEntity create(GameProfile profile) {
      UUID var2 = PlayerEntity.getUuid(profile);
      ArrayList var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < this.players.size(); ++var4) {
         ServerPlayerEntity var5 = (ServerPlayerEntity)this.players.get(var4);
         if (var5.getUuid().equals(var2)) {
            var3.add(var5);
         }
      }

      for(ServerPlayerEntity var8 : var3) {
         var8.networkHandler.disconnect("You logged in from another location");
      }

      Object var7;
      if (this.server.isDemo()) {
         var7 = new DemoServerPlayerInteractionManager(this.server.getWorld(0));
      } else {
         var7 = new ServerPlayerInteractionManager(this.server.getWorld(0));
      }

      return new ServerPlayerEntity(this.server, this.server.getWorld(0), profile, (ServerPlayerInteractionManager)var7);
   }

   public ServerPlayerEntity respawn(ServerPlayerEntity player, int dimensionId, boolean alive) {
      player.getServerWorld().getEntityTracker().removeListener(player);
      player.getServerWorld().getEntityTracker().onEntityRemoved(player);
      player.getServerWorld().getChunkMap().onPlayerRemoved(player);
      this.players.remove(player);
      this.server.getWorld(player.dimensionId).removeEntityNow(player);
      BlockPos var4 = player.getSpawnPoint();
      boolean var5 = player.isRespawnForced();
      player.dimensionId = dimensionId;
      Object var6;
      if (this.server.isDemo()) {
         var6 = new DemoServerPlayerInteractionManager(this.server.getWorld(player.dimensionId));
      } else {
         var6 = new ServerPlayerInteractionManager(this.server.getWorld(player.dimensionId));
      }

      ServerPlayerEntity var7 = new ServerPlayerEntity(
         this.server, this.server.getWorld(player.dimensionId), player.getGameProfile(), (ServerPlayerInteractionManager)var6
      );
      var7.networkHandler = player.networkHandler;
      var7.copyFrom(player, alive);
      var7.setNetworkId(player.getNetworkId());
      var7.copyCommandResults(player);
      ServerWorld var8 = this.server.getWorld(player.dimensionId);
      this.copyGameMode(var7, player, var8);
      if (var4 != null) {
         BlockPos var9 = PlayerEntity.getUpdatedSpawnpoint(this.server.getWorld(player.dimensionId), var4, var5);
         if (var9 != null) {
            var7.refreshPositionAndAngles(
               (double)((float)var9.getX() + 0.5F), (double)((float)var9.getY() + 0.1F), (double)((float)var9.getZ() + 0.5F), 0.0F, 0.0F
            );
            var7.setSpawnpoint(var4, var5);
         } else {
            var7.networkHandler.sendPacket(new GameEventS2CPacket(0, 0.0F));
         }
      }

      var8.chunkCache.loadChunk((int)var7.x >> 4, (int)var7.z >> 4);

      while(!var8.getCollisions(var7, var7.getBoundingBox()).isEmpty() && var7.y < 256.0) {
         var7.setPosition(var7.x, var7.y + 1.0, var7.z);
      }

      var7.networkHandler
         .sendPacket(
            new PlayerRespawnS2CPacket(
               var7.dimensionId, var7.world.getDifficulty(), var7.world.getData().getGeneratorType(), var7.interactionManager.getGameMode()
            )
         );
      BlockPos var10 = var8.getSpawnPoint();
      var7.networkHandler.teleport(var7.x, var7.y, var7.z, var7.yaw, var7.pitch);
      var7.networkHandler.sendPacket(new PlayerSpawnPointS2CPacket(var10));
      var7.networkHandler.sendPacket(new PlayerXpS2CPacket(var7.xpProgress, var7.xp, var7.xpLevel));
      this.sendWorldInfo(var7, var8);
      var8.getChunkMap().onPlayerAdded(var7);
      var8.addEntity(var7);
      this.players.add(var7);
      this.playersByUuid.put(var7.getUuid(), var7);
      var7.listenToScreenHandler();
      var7.setHealth(var7.getHealth());
      return var7;
   }

   public void teleportToDimension(ServerPlayerEntity player, int dimensionId) {
      int var3 = player.dimensionId;
      ServerWorld var4 = this.server.getWorld(player.dimensionId);
      player.dimensionId = dimensionId;
      ServerWorld var5 = this.server.getWorld(player.dimensionId);
      player.networkHandler
         .sendPacket(
            new PlayerRespawnS2CPacket(
               player.dimensionId, player.world.getDifficulty(), player.world.getData().getGeneratorType(), player.interactionManager.getGameMode()
            )
         );
      var4.removeEntityNow(player);
      player.removed = false;
      this.teleportEntityToDimension(player, var3, var4, var5);
      this.onChangedDimension(player, var4);
      player.networkHandler.teleport(player.x, player.y, player.z, player.yaw, player.pitch);
      player.interactionManager.setWorld(var5);
      this.sendWorldInfo(player, var5);
      this.sendPlayerInfo(player);

      for(StatusEffectInstance var7 : player.getStatusEffects()) {
         player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getNetworkId(), var7));
      }
   }

   public void teleportEntityToDimension(Entity entity, int dimensionId, ServerWorld fromWorld, ServerWorld toWorld) {
      double var5 = entity.x;
      double var7 = entity.z;
      double var9 = 8.0;
      float var11 = entity.yaw;
      fromWorld.profiler.push("moving");
      if (entity.dimensionId == -1) {
         var5 = MathHelper.clamp(var5 / var9, toWorld.getWorldBorder().getMinX() + 16.0, toWorld.getWorldBorder().getMaxX() - 16.0);
         var7 = MathHelper.clamp(var7 / var9, toWorld.getWorldBorder().getMinZ() + 16.0, toWorld.getWorldBorder().getMaxZ() - 16.0);
         entity.refreshPositionAndAngles(var5, entity.y, var7, entity.yaw, entity.pitch);
         if (entity.isAlive()) {
            fromWorld.tickEntity(entity, false);
         }
      } else if (entity.dimensionId == 0) {
         var5 = MathHelper.clamp(var5 * var9, toWorld.getWorldBorder().getMinX() + 16.0, toWorld.getWorldBorder().getMaxX() - 16.0);
         var7 = MathHelper.clamp(var7 * var9, toWorld.getWorldBorder().getMinZ() + 16.0, toWorld.getWorldBorder().getMaxZ() - 16.0);
         entity.refreshPositionAndAngles(var5, entity.y, var7, entity.yaw, entity.pitch);
         if (entity.isAlive()) {
            fromWorld.tickEntity(entity, false);
         }
      } else {
         BlockPos var12;
         if (dimensionId == 1) {
            var12 = toWorld.getSpawnPoint();
         } else {
            var12 = toWorld.getForcedSpawnPoint();
         }

         var5 = (double)var12.getX();
         entity.y = (double)var12.getY();
         var7 = (double)var12.getZ();
         entity.refreshPositionAndAngles(var5, entity.y, var7, 90.0F, 0.0F);
         if (entity.isAlive()) {
            fromWorld.tickEntity(entity, false);
         }
      }

      fromWorld.profiler.pop();
      if (dimensionId != 1) {
         fromWorld.profiler.push("placing");
         var5 = (double)MathHelper.clamp((int)var5, -29999872, 29999872);
         var7 = (double)MathHelper.clamp((int)var7, -29999872, 29999872);
         if (entity.isAlive()) {
            entity.refreshPositionAndAngles(var5, entity.y, var7, entity.yaw, entity.pitch);
            toWorld.getPortalForcer().onDimensionChanged(entity, var11);
            toWorld.addEntity(entity);
            toWorld.tickEntity(entity, false);
         }

         fromWorld.profiler.pop();
      }

      entity.setWorld(toWorld);
   }

   public void tick() {
      if (++this.pingUpdateTime > 600) {
         this.sendToAll(new PlayerInfoS2CPacket(PlayerInfoS2CPacket.Action.UPDATE_PING, this.players));
         this.pingUpdateTime = 0;
      }
   }

   public void sendToAll(Packet packet) {
      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         ((ServerPlayerEntity)this.players.get(var2)).networkHandler.sendPacket(packet);
      }
   }

   public void sendToDimension(Packet packet, int dimensionId) {
      for(int var3 = 0; var3 < this.players.size(); ++var3) {
         ServerPlayerEntity var4 = (ServerPlayerEntity)this.players.get(var3);
         if (var4.dimensionId == dimensionId) {
            var4.networkHandler.sendPacket(packet);
         }
      }
   }

   public void sendMessageToTeamMembers(PlayerEntity player, Text message) {
      AbstractTeam var3 = player.getScoreboardTeam();
      if (var3 != null) {
         for(String var6 : var3.getMembers()) {
            ServerPlayerEntity var7 = this.get(var6);
            if (var7 != null && var7 != player) {
               var7.sendMessage(message);
            }
         }
      }
   }

   public void sendMessageToNonTeamMembers(PlayerEntity player, Text message) {
      AbstractTeam var3 = player.getScoreboardTeam();
      if (var3 == null) {
         this.sendSystemMessage(message);
      } else {
         for(int var4 = 0; var4 < this.players.size(); ++var4) {
            ServerPlayerEntity var5 = (ServerPlayerEntity)this.players.get(var4);
            if (var5.getScoreboardTeam() != var3) {
               var5.sendMessage(message);
            }
         }
      }
   }

   public String getNamesAsString() {
      String var1 = "";

      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         if (var2 > 0) {
            var1 = var1 + ", ";
         }

         var1 = var1 + ((ServerPlayerEntity)this.players.get(var2)).getName();
      }

      return var1;
   }

   public String[] getNames() {
      String[] var1 = new String[this.players.size()];

      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         var1[var2] = ((ServerPlayerEntity)this.players.get(var2)).getName();
      }

      return var1;
   }

   public GameProfile[] getProfiles() {
      GameProfile[] var1 = new GameProfile[this.players.size()];

      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         var1[var2] = ((ServerPlayerEntity)this.players.get(var2)).getGameProfile();
      }

      return var1;
   }

   public PlayerBans getPlayerBans() {
      return this.bans;
   }

   public IpBans getIpBans() {
      return this.ipBans;
   }

   public void addOp(GameProfile playerName) {
      this.ops.add(new OpEntry(playerName, this.server.getOpPermissionLevel()));
   }

   public void removeOp(GameProfile playerName) {
      this.ops.remove(playerName);
   }

   public boolean isWhitelisted(GameProfile playerName) {
      return !this.whitelistEnabled || this.ops.contains(playerName) || this.whitelist.contains(playerName);
   }

   public boolean isOp(GameProfile playerName) {
      return this.ops.contains(playerName)
         || this.server.isSinglePlayer() && this.server.worlds[0].getData().allowCommands() && this.server.getUserName().equalsIgnoreCase(playerName.getName())
         || this.allowCommands;
   }

   public ServerPlayerEntity get(String playerName) {
      for(ServerPlayerEntity var3 : this.players) {
         if (var3.getName().equalsIgnoreCase(playerName)) {
            return var3;
         }
      }

      return null;
   }

   public void sendToAround(double x, double y, double z, double range, int dimensionId, Packet packet) {
      this.sendToAround(null, x, y, z, range, dimensionId, packet);
   }

   public void sendToAround(PlayerEntity source, double x, double y, double z, double range, int dimensionId, Packet packet) {
      for(int var12 = 0; var12 < this.players.size(); ++var12) {
         ServerPlayerEntity var13 = (ServerPlayerEntity)this.players.get(var12);
         if (var13 != source && var13.dimensionId == dimensionId) {
            double var14 = x - var13.x;
            double var16 = y - var13.y;
            double var18 = z - var13.z;
            if (var14 * var14 + var16 * var16 + var18 * var18 < range * range) {
               var13.networkHandler.sendPacket(packet);
            }
         }
      }
   }

   public void saveData() {
      for(int var1 = 0; var1 < this.players.size(); ++var1) {
         this.saveData((ServerPlayerEntity)this.players.get(var1));
      }
   }

   public void addToWhitelist(GameProfile playerName) {
      this.whitelist.add(new WhitelistEntry(playerName));
   }

   public void removeFromWhitelist(GameProfile playerName) {
      this.whitelist.remove(playerName);
   }

   public Whitelist getWhitelist() {
      return this.whitelist;
   }

   public String[] getWhitelistNames() {
      return this.whitelist.getNames();
   }

   public Ops getOps() {
      return this.ops;
   }

   public String[] getOpNames() {
      return this.ops.getNames();
   }

   public void reloadWhitelist() {
   }

   public void sendWorldInfo(ServerPlayerEntity player, ServerWorld world) {
      WorldBorder var3 = this.server.worlds[0].getWorldBorder();
      player.networkHandler.sendPacket(new WorldBorderS2CPacket(var3, WorldBorderS2CPacket.Type.INITIALIZE));
      player.networkHandler.sendPacket(new WorldTimeS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean("doDaylightCycle")));
      if (world.isRaining()) {
         player.networkHandler.sendPacket(new GameEventS2CPacket(1, 0.0F));
         player.networkHandler.sendPacket(new GameEventS2CPacket(7, world.getRain(1.0F)));
         player.networkHandler.sendPacket(new GameEventS2CPacket(8, world.getThunder(1.0F)));
      }
   }

   public void sendPlayerInfo(ServerPlayerEntity player) {
      player.setMenu(player.playerMenu);
      player.markHealthDirty();
      player.networkHandler.sendPacket(new SelectSlotS2CPacket(player.inventory.selectedSlot));
   }

   public int getCount() {
      return this.players.size();
   }

   public int getMaxCount() {
      return this.maxPlayerCount;
   }

   public String[] getSavedIds() {
      return this.server.worlds[0].getStorage().getPlayerDataStorage().getSavedPlayerIds();
   }

   @Environment(EnvType.SERVER)
   public boolean isWhitelistEnabled() {
      return this.whitelistEnabled;
   }

   public void setWhitelistEnabled(boolean enabled) {
      this.whitelistEnabled = enabled;
   }

   public List getAtIp(String ip) {
      ArrayList var2 = Lists.newArrayList();

      for(ServerPlayerEntity var4 : this.players) {
         if (var4.getIp().equals(ip)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public int getChunkViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public NbtCompound getSinglePlayerData() {
      return null;
   }

   @Environment(EnvType.CLIENT)
   public void setDefaultGamemode(WorldSettings.GameMode gamemode) {
      this.defaultGamemode = gamemode;
   }

   private void copyGameMode(ServerPlayerEntity player, ServerPlayerEntity fromPlayer, World world) {
      if (fromPlayer != null) {
         player.interactionManager.setGameMode(fromPlayer.interactionManager.getGameMode());
      } else if (this.defaultGamemode != null) {
         player.interactionManager.setGameMode(this.defaultGamemode);
      }

      player.interactionManager.setGameModeIfNotSet(world.getData().getDefaultGamemode());
   }

   @Environment(EnvType.CLIENT)
   public void setAllowCommands(boolean allowCommands) {
      this.allowCommands = allowCommands;
   }

   public void disconnectAll() {
      for(int var1 = 0; var1 < this.players.size(); ++var1) {
         ((ServerPlayerEntity)this.players.get(var1)).networkHandler.disconnect("Server closed");
      }
   }

   public void sendMessage(Text message, boolean system) {
      this.server.sendMessage(message);
      int var3 = system ? 1 : 0;
      this.sendToAll(new ChatMessageS2CPacket(message, (byte)var3));
   }

   public void sendSystemMessage(Text message) {
      this.sendMessage(message, true);
   }

   public ServerStatHandler getStatHandler(PlayerEntity player) {
      UUID var2 = player.getUuid();
      ServerStatHandler var3 = var2 == null ? null : (ServerStatHandler)this.statHandlers.get(var2);
      if (var3 == null) {
         File var4 = new File(this.server.getWorld(0).getStorage().getDir(), "stats");
         File var5 = new File(var4, var2.toString() + ".json");
         if (!var5.exists()) {
            File var6 = new File(var4, player.getName() + ".json");
            if (var6.exists() && var6.isFile()) {
               var6.renameTo(var5);
            }
         }

         var3 = new ServerStatHandler(this.server, var5);
         var3.load();
         this.statHandlers.put(var2, var3);
      }

      return var3;
   }

   public void updateViewDistance(int viewDistance) {
      this.viewDistance = viewDistance;
      if (this.server.worlds != null) {
         for(ServerWorld var5 : this.server.worlds) {
            if (var5 != null) {
               var5.getChunkMap().updateViewDistance(viewDistance);
            }
         }
      }
   }

   public ServerPlayerEntity getMatching(UUID pos) {
      return (ServerPlayerEntity)this.playersByUuid.get(pos);
   }
}
