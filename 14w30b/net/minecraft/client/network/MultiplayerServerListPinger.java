package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.handler.ClientQueryPacketHandler;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.network.Connection;
import net.minecraft.network.NetworkProtocol;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.PingC2SPacket;
import net.minecraft.network.packet.c2s.query.ServerStatusC2SPacket;
import net.minecraft.network.packet.s2c.query.PingS2CPacket;
import net.minecraft.network.packet.s2c.query.ServerStatusS2CPacket;
import net.minecraft.server.ServerStatus;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerServerListPinger {
   private static final Splitter ZERO_SPLITTER = Splitter.on('\u0000').limit(6);
   private static final Logger LOGGER = LogManager.getLogger();
   private final List clientConnectionList = Collections.synchronizedList(Lists.newArrayList());

   public void add(ServerListEntry serverEntry) {
      ServerAddress var2 = ServerAddress.parse(serverEntry.address);
      final Connection var3 = Connection.connect(InetAddress.getByName(var2.getAddress()), var2.getPort());
      this.clientConnectionList.add(var3);
      serverEntry.description = "Pinging...";
      serverEntry.ping = -1L;
      serverEntry.playerListString = null;
      var3.setListener(
         new ClientQueryPacketHandler() {
            private boolean hasResponse = false;
   
            @Override
            public void handleServerStatus(ServerStatusS2CPacket packet) {
               ServerStatus var2 = packet.getServerStatus();
               if (var2.getDescription() != null) {
                  serverEntry.description = var2.getDescription().buildFormattedString();
               } else {
                  serverEntry.description = "";
               }
   
               if (var2.getVersion() != null) {
                  serverEntry.version = var2.getVersion().getName();
                  serverEntry.protocol = var2.getVersion().getProtocol();
               } else {
                  serverEntry.version = "Old";
                  serverEntry.protocol = 0;
               }
   
               if (var2.getPlayers() != null) {
                  serverEntry.onlinePlayers = Formatting.GRAY
                     + ""
                     + var2.getPlayers().getOnline()
                     + ""
                     + Formatting.DARK_GRAY
                     + "/"
                     + Formatting.GRAY
                     + var2.getPlayers().getMax();
                  if (ArrayUtils.isNotEmpty(var2.getPlayers().get())) {
                     StringBuilder var3x = new StringBuilder();
   
                     for(GameProfile var7 : var2.getPlayers().get()) {
                        if (var3x.length() > 0) {
                           var3x.append("\n");
                        }
   
                        var3x.append(var7.getName());
                     }
   
                     if (var2.getPlayers().get().length < var2.getPlayers().getOnline()) {
                        if (var3x.length() > 0) {
                           var3x.append("\n");
                        }
   
                        var3x.append("... and ").append(var2.getPlayers().getOnline() - var2.getPlayers().get().length).append(" more ...");
                     }
   
                     serverEntry.playerListString = var3x.toString();
                  }
               } else {
                  serverEntry.onlinePlayers = Formatting.DARK_GRAY + "???";
               }
   
               if (var2.getFavicon() != null) {
                  String var8 = var2.getFavicon();
                  if (var8.startsWith("data:image/png;base64,")) {
                     serverEntry.setIcon(var8.substring("data:image/png;base64,".length()));
                  } else {
                     MultiplayerServerListPinger.LOGGER.error("Invalid server icon (unknown format)");
                  }
               } else {
                  serverEntry.setIcon(null);
               }
   
               var3.send(new PingC2SPacket(MinecraftClient.getTime()));
               this.hasResponse = true;
            }
   
            @Override
            public void handlePing(PingS2CPacket packet) {
               long var2 = packet.getTime();
               long var4 = MinecraftClient.getTime();
               serverEntry.ping = var4 - var2;
               var3.disconnect(new LiteralText("Finished"));
            }
   
            @Override
            public void onDisconnect(Text reason) {
               if (!this.hasResponse) {
                  MultiplayerServerListPinger.LOGGER.error("Can't ping " + serverEntry.address + ": " + reason.buildString());
                  serverEntry.description = Formatting.DARK_RED + "Can't connect to server.";
                  serverEntry.onlinePlayers = "";
                  MultiplayerServerListPinger.this.ping(serverEntry);
               }
            }
         }
      );

      try {
         var3.send(new HandshakeC2SPacket(31, var2.getAddress(), var2.getPort(), NetworkProtocol.STATUS));
         var3.send(new ServerStatusC2SPacket());
      } catch (Throwable var5) {
         LOGGER.error(var5);
      }
   }

   private void ping(ServerListEntry serverListEntry) {
      final ServerAddress var2 = ServerAddress.parse(serverListEntry.address);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)Connection.NETWORK_GROUP.get())).handler(new ChannelInitializer() {
         protected void initChannel(Channel channel) {
            try {
               channel.config().setOption(ChannelOption.IP_TOS, 24);
            } catch (ChannelException var4) {
            }

            try {
               channel.config().setOption(ChannelOption.TCP_NODELAY, false);
            } catch (ChannelException var3) {
            }

            channel.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler() {
               public void channelActive(ChannelHandlerContext channelHandlerContext) {
                  super.channelActive(channelHandlerContext);
                  ByteBuf var2x = Unpooled.buffer();

                  try {
                     var2x.writeByte(254);
                     var2x.writeByte(1);
                     var2x.writeByte(250);
                     char[] var3 = "MC|PingHost".toCharArray();
                     var2x.writeShort(var3.length);

                     for(char var7 : var3) {
                        var2x.writeChar(var7);
                     }

                     var2x.writeShort(7 + 2 * var2.getAddress().length());
                     var2x.writeByte(127);
                     var3 = var2.getAddress().toCharArray();
                     var2x.writeShort(var3.length);

                     for(char var15 : var3) {
                        var2x.writeChar(var15);
                     }

                     var2x.writeInt(var2.getPort());
                     channelHandlerContext.channel().writeAndFlush(var2x).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                  } finally {
                     var2x.release();
                  }
               }

               protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
                  short var3 = byteBuf.readUnsignedByte();
                  if (var3 == 255) {
                     String var4 = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), Charsets.UTF_16BE);
                     String[] var5 = (String[])Iterables.toArray(MultiplayerServerListPinger.ZERO_SPLITTER.split(var4), String.class);
                     if ("§1".equals(var5[0])) {
                        int var6 = MathHelper.parseInt(var5[1], 0);
                        String var7 = var5[2];
                        String var8 = var5[3];
                        int var9 = MathHelper.parseInt(var5[4], -1);
                        int var10 = MathHelper.parseInt(var5[5], -1);
                        serverListEntry.protocol = -1;
                        serverListEntry.version = var7;
                        serverListEntry.description = var8;
                        serverListEntry.onlinePlayers = Formatting.GRAY + "" + var9 + "" + Formatting.DARK_GRAY + "/" + Formatting.GRAY + var10;
                     }
                  }

                  channelHandlerContext.close();
               }

               public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable exeception) {
                  channelHandlerContext.close();
               }
            }});
         }
      })).channel(NioSocketChannel.class)).connect(var2.getAddress(), var2.getPort());
   }

   public void tick() {
      synchronized(this.clientConnectionList) {
         Iterator var2 = this.clientConnectionList.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if (var3.isOpen()) {
               var3.tick();
            } else {
               var2.remove();
               var3.handleDisconnection();
            }
         }
      }
   }

   public void cancel() {
      synchronized(this.clientConnectionList) {
         Iterator var2 = this.clientConnectionList.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if (var3.isOpen()) {
               var2.remove();
               var3.disconnect(new LiteralText("Cancelled"));
            }
         }
      }
   }
}
