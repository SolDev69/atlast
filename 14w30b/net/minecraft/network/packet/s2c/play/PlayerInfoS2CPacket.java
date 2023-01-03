package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.List;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerInfoS2CPacket implements Packet {
   private PlayerInfoS2CPacket.Action action;
   private final List entries = Lists.newArrayList();

   public PlayerInfoS2CPacket() {
   }

   public PlayerInfoS2CPacket(PlayerInfoS2CPacket.Action action, ServerPlayerEntity... players) {
      this.action = action;

      for(ServerPlayerEntity var6 : players) {
         this.entries.add(new PlayerInfoS2CPacket.Entry(var6.getGameProfile(), var6.ping, var6.interactionManager.getGameMode(), var6.m_95aslciht()));
      }
   }

   public PlayerInfoS2CPacket(PlayerInfoS2CPacket.Action action, Iterable players) {
      this.action = action;

      for(ServerPlayerEntity var4 : players) {
         this.entries.add(new PlayerInfoS2CPacket.Entry(var4.getGameProfile(), var4.ping, var4.interactionManager.getGameMode(), var4.m_95aslciht()));
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.action = (PlayerInfoS2CPacket.Action)buffer.readEnum(PlayerInfoS2CPacket.Action.class);
      int var2 = buffer.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         GameProfile var4 = null;
         int var5 = 0;
         WorldSettings.GameMode var6 = null;
         Text var7 = null;
         switch(this.action) {
            case ADD_PLAYER:
               var4 = new GameProfile(buffer.readUuid(), buffer.readString(16));
               int var8 = buffer.readVarInt();
               int var9 = 0;

               for(; var9 < var8; ++var9) {
                  String var10 = buffer.readString(32767);
                  String var11 = buffer.readString(32767);
                  if (buffer.readBoolean()) {
                     var4.getProperties().put(var10, new Property(var10, var11, buffer.readString(32767)));
                  } else {
                     var4.getProperties().put(var10, new Property(var10, var11));
                  }
               }

               var6 = WorldSettings.GameMode.byIndex(buffer.readVarInt());
               var5 = buffer.readVarInt();
               if (buffer.readBoolean()) {
                  var7 = buffer.readText();
               }
               break;
            case UPDATE_GAME_MODE:
               var4 = new GameProfile(buffer.readUuid(), null);
               var6 = WorldSettings.GameMode.byIndex(buffer.readVarInt());
               break;
            case UPDATE_PING:
               var4 = new GameProfile(buffer.readUuid(), null);
               var5 = buffer.readVarInt();
               break;
            case UPDATE_DISPLAY_NAME:
               var4 = new GameProfile(buffer.readUuid(), null);
               if (buffer.readBoolean()) {
                  var7 = buffer.readText();
               }
               break;
            case REMOVE_PLAYER:
               var4 = new GameProfile(buffer.readUuid(), null);
         }

         this.entries.add(new PlayerInfoS2CPacket.Entry(var4, var5, var6, var7));
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeEnum(this.action);
      buffer.writeVarInt(this.entries.size());

      for(PlayerInfoS2CPacket.Entry var3 : this.entries) {
         switch(this.action) {
            case ADD_PLAYER:
               buffer.writeUuid(var3.getProfile().getId());
               buffer.writeString(var3.getProfile().getName());
               buffer.writeVarInt(var3.getProfile().getProperties().size());

               for(Property var5 : var3.getProfile().getProperties().values()) {
                  buffer.writeString(var5.getName());
                  buffer.writeString(var5.getValue());
                  if (var5.hasSignature()) {
                     buffer.writeBoolean(true);
                     buffer.writeString(var5.getSignature());
                  } else {
                     buffer.writeBoolean(false);
                  }
               }

               buffer.writeVarInt(var3.getGameMode().getIndex());
               buffer.writeVarInt(var3.getPing());
               if (var3.getDisplayName() == null) {
                  buffer.writeBoolean(false);
               } else {
                  buffer.writeBoolean(true);
                  buffer.writeText(var3.getDisplayName());
               }
               break;
            case UPDATE_GAME_MODE:
               buffer.writeUuid(var3.getProfile().getId());
               buffer.writeVarInt(var3.getGameMode().getIndex());
               break;
            case UPDATE_PING:
               buffer.writeUuid(var3.getProfile().getId());
               buffer.writeVarInt(var3.getPing());
               break;
            case UPDATE_DISPLAY_NAME:
               buffer.writeUuid(var3.getProfile().getId());
               if (var3.getDisplayName() == null) {
                  buffer.writeBoolean(false);
               } else {
                  buffer.writeBoolean(true);
                  buffer.writeText(var3.getDisplayName());
               }
               break;
            case REMOVE_PLAYER:
               buffer.writeUuid(var3.getProfile().getId());
         }
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handlePlayerInfo(this);
   }

   @Environment(EnvType.CLIENT)
   public List getEntries() {
      return this.entries;
   }

   @Environment(EnvType.CLIENT)
   public PlayerInfoS2CPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_PING,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;
   }

   public class Entry {
      private final int ping;
      private final WorldSettings.GameMode gameMode;
      private final GameProfile profile;
      private final Text displayName;

      public Entry(GameProfile profile, int ping, WorldSettings.GameMode gameMode, Text displayName) {
         this.profile = profile;
         this.ping = ping;
         this.gameMode = gameMode;
         this.displayName = displayName;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getPing() {
         return this.ping;
      }

      public WorldSettings.GameMode getGameMode() {
         return this.gameMode;
      }

      public Text getDisplayName() {
         return this.displayName;
      }
   }
}
