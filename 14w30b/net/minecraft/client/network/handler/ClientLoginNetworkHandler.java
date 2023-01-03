package net.minecraft.client.network.handler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.Connection;
import net.minecraft.network.NetworkProtocol;
import net.minecraft.network.encryption.EncryptionUtils;
import net.minecraft.network.packet.c2s.login.KeyC2SPacket;
import net.minecraft.network.packet.s2c.login.CompressionThresholdS2CPacket;
import net.minecraft.network.packet.s2c.login.HelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginFailS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientLoginNetworkHandler implements ClientLoginPacketHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftClient client;
   private final Screen screen;
   private final Connection connection;
   private GameProfile profile;

   public ClientLoginNetworkHandler(Connection connection, MinecraftClient client, Screen screen) {
      this.connection = connection;
      this.client = client;
      this.screen = screen;
   }

   @Override
   public void handleHello(HelloS2CPacket packet) {
      final SecretKey var2 = EncryptionUtils.generateKey();
      String var3 = packet.getServerId();
      PublicKey var4 = packet.getPublicKey();
      String var5 = new BigInteger(EncryptionUtils.generateServerId(var3, var4, var2)).toString(16);

      try {
         this.createAuthenticationService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), var5);
      } catch (AuthenticationUnavailableException var7) {
         this.connection.disconnect(new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.serversUnavailable")));
         return;
      } catch (InvalidCredentialsException var8) {
         this.connection.disconnect(new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.invalidSession")));
         return;
      } catch (AuthenticationException var9) {
         this.connection.disconnect(new TranslatableText("disconnect.loginFailedInfo", var9.getMessage()));
         return;
      }

      this.connection.send(new KeyC2SPacket(var2, var4, packet.getNonce()), new GenericFutureListener() {
         public void operationComplete(Future future) {
            ClientLoginNetworkHandler.this.connection.setupEncryption(var2);
         }
      });
   }

   private MinecraftSessionService createAuthenticationService() {
      return this.client.createAuthenticationService();
   }

   @Override
   public void handleLoginSuccess(LoginSuccessS2CPacket packet) {
      this.profile = packet.getProfile();
      this.connection.setProtocol(NetworkProtocol.PLAY);
      this.connection.setListener(new ClientPlayNetworkHandler(this.client, this.screen, this.connection, this.profile));
   }

   @Override
   public void onDisconnect(Text reason) {
      this.client.openScreen(new DisconnectedScreen(this.screen, "connect.failed", reason));
   }

   @Override
   public void handleLoginFail(LoginFailS2CPacket packet) {
      this.connection.disconnect(packet.getReason());
   }

   @Override
   public void handleCompressionThreshold(CompressionThresholdS2CPacket packet) {
      if (!this.connection.isLocal()) {
         this.connection.setCompressionThreshold(packet.getCompressionThreshold());
      }
   }
}
