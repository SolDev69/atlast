package net.minecraft.server.network.handler;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.network.Connection;
import net.minecraft.network.encryption.EncryptionUtils;
import net.minecraft.network.packet.c2s.login.HelloC2SPacket;
import net.minecraft.network.packet.c2s.login.KeyC2SPacket;
import net.minecraft.network.packet.s2c.login.CompressionThresholdS2CPacket;
import net.minecraft.network.packet.s2c.login.HelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginFailS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLoginNetworkHandler implements ServerLoginPacketHandler, Tickable {
   private static final AtomicInteger authenticatorThreadId = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   private final byte[] nonce = new byte[4];
   private final MinecraftServer server;
   public final Connection connection;
   private ServerLoginNetworkHandler.Stage stage = ServerLoginNetworkHandler.Stage.HELLO;
   private int ticks;
   private GameProfile profile;
   private String serverId = "";
   private SecretKey secretKey;

   public ServerLoginNetworkHandler(MinecraftServer server, Connection connection) {
      this.server = server;
      this.connection = connection;
      RANDOM.nextBytes(this.nonce);
   }

   @Override
   public void tick() {
      if (this.stage == ServerLoginNetworkHandler.Stage.READY_TO_ACCEPT) {
         this.acceptLogin();
      }

      if (this.ticks++ == 600) {
         this.disconnect("Took too long to log in");
      }
   }

   public void disconnect(String reason) {
      try {
         LOGGER.info("Disconnecting " + this.getConnectionInfo() + ": " + reason);
         LiteralText var2 = new LiteralText(reason);
         this.connection.send(new LoginFailS2CPacket(var2));
         this.connection.disconnect(var2);
      } catch (Exception var3) {
         LOGGER.error("Error whilst disconnecting player", var3);
      }
   }

   public void acceptLogin() {
      if (!this.profile.isComplete()) {
         this.profile = this.createFakeProfile(this.profile);
      }

      String var1 = this.server.getPlayerManager().canLogin(this.connection.getAddress(), this.profile);
      if (var1 != null) {
         this.disconnect(var1);
      } else {
         this.stage = ServerLoginNetworkHandler.Stage.ACCEPTED;
         if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
            this.connection.send(new CompressionThresholdS2CPacket(this.server.getNetworkCompressionThreshold()), new ChannelFutureListener() {
               public void operationComplete(ChannelFuture channelFuture) {
                  ServerLoginNetworkHandler.this.connection.setCompressionThreshold(ServerLoginNetworkHandler.this.server.getNetworkCompressionThreshold());
               }
            });
         }

         this.connection.send(new LoginSuccessS2CPacket(this.profile));
         this.server.getPlayerManager().onLogin(this.connection, this.server.getPlayerManager().create(this.profile));
      }
   }

   @Override
   public void onDisconnect(Text reason) {
      LOGGER.info(this.getConnectionInfo() + " lost connection: " + reason.buildString());
   }

   public String getConnectionInfo() {
      return this.profile != null
         ? this.profile.toString() + " (" + this.connection.getAddress().toString() + ")"
         : String.valueOf(this.connection.getAddress());
   }

   @Override
   public void handleHello(HelloC2SPacket packet) {
      Validate.validState(this.stage == ServerLoginNetworkHandler.Stage.HELLO, "Unexpected hello packet", new Object[0]);
      this.profile = packet.getProfile();
      if (this.server.isOnlineMode() && !this.connection.isLocal()) {
         this.stage = ServerLoginNetworkHandler.Stage.KEY;
         this.connection.send(new HelloS2CPacket(this.serverId, this.server.getKeyPair().getPublic(), this.nonce));
      } else {
         this.stage = ServerLoginNetworkHandler.Stage.READY_TO_ACCEPT;
      }
   }

   @Override
   public void handleKey(KeyC2SPacket packet) {
      Validate.validState(this.stage == ServerLoginNetworkHandler.Stage.KEY, "Unexpected key packet", new Object[0]);
      PrivateKey var2 = this.server.getKeyPair().getPrivate();
      if (!Arrays.equals(this.nonce, packet.getNonce(var2))) {
         throw new IllegalStateException("Invalid nonce!");
      } else {
         this.secretKey = packet.getSecretKey(var2);
         this.stage = ServerLoginNetworkHandler.Stage.AUTHENTICATING;
         this.connection.setupEncryption(this.secretKey);
         (new Thread("User Authenticator #" + authenticatorThreadId.incrementAndGet()) {
               @Override
               public void run() {
                  GameProfile var1 = ServerLoginNetworkHandler.this.profile;
   
                  try {
                     String var2 = new BigInteger(
                           EncryptionUtils.generateServerId(
                              ServerLoginNetworkHandler.this.serverId,
                              ServerLoginNetworkHandler.this.server.getKeyPair().getPublic(),
                              ServerLoginNetworkHandler.this.secretKey
                           )
                        )
                        .toString(16);
                     ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.server
                        .getSessionService()
                        .hasJoinedServer(new GameProfile(null, var1.getName()), var2);
                     if (ServerLoginNetworkHandler.this.profile != null) {
                        ServerLoginNetworkHandler.LOGGER
                           .info("UUID of player " + ServerLoginNetworkHandler.this.profile.getName() + " is " + ServerLoginNetworkHandler.this.profile.getId());
                        ServerLoginNetworkHandler.this.stage = ServerLoginNetworkHandler.Stage.READY_TO_ACCEPT;
                     } else if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
                        ServerLoginNetworkHandler.LOGGER.warn("Failed to verify username but will let them in anyway!");
                        ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.createFakeProfile(var1);
                        ServerLoginNetworkHandler.this.stage = ServerLoginNetworkHandler.Stage.READY_TO_ACCEPT;
                     } else {
                        ServerLoginNetworkHandler.this.disconnect("Failed to verify username!");
                        ServerLoginNetworkHandler.LOGGER
                           .error("Username '" + ServerLoginNetworkHandler.this.profile.getName() + "' tried to join with an invalid session");
                     }
                  } catch (AuthenticationUnavailableException var3) {
                     if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
                        ServerLoginNetworkHandler.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.createFakeProfile(var1);
                        ServerLoginNetworkHandler.this.stage = ServerLoginNetworkHandler.Stage.READY_TO_ACCEPT;
                     } else {
                        ServerLoginNetworkHandler.this.disconnect("Authentication servers are down. Please try again later, sorry!");
                        ServerLoginNetworkHandler.LOGGER.error("Couldn't verify username because servers are unavailable");
                     }
                  }
               }
            })
            .start();
      }
   }

   protected GameProfile createFakeProfile(GameProfile profile) {
      UUID var2 = UUID.nameUUIDFromBytes(("OfflinePlayer:" + profile.getName()).getBytes(Charsets.UTF_8));
      return new GameProfile(var2, profile.getName());
   }

   static enum Stage {
      HELLO,
      KEY,
      AUTHENTICATING,
      READY_TO_ACCEPT,
      ACCEPTED;
   }
}
