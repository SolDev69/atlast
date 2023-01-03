package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.network.Connection;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.EncoderHandler;
import net.minecraft.network.PacketFlow;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.SplitterHandler;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.handler.LocalHandshakeNetworkHandler;
import net.minecraft.server.network.handler.RemoteHandshakeNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.LazySupplier;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerNetworkIo {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LazySupplier NETWORK_GROUP = new LazySupplier() {
      protected NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build());
      }
   };
   public static final LazySupplier LOCAL_NETWORK_GROUP = new LazySupplier() {
      protected LocalEventLoopGroup load() {
         return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
      }
   };
   private final MinecraftServer server;
   public volatile boolean isOpen;
   private final List channels = Collections.synchronizedList(Lists.newArrayList());
   private final List connections = Collections.synchronizedList(Lists.newArrayList());

   public ServerNetworkIo(MinecraftServer server) {
      this.server = server;
      this.isOpen = true;
   }

   public void bind(InetAddress address, int port) {
      synchronized(this.channels) {
         this.channels
            .add(
               ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(NioServerSocketChannel.class))
                     .childHandler(
                        new ChannelInitializer() {
                           protected void initChannel(Channel channel) {
                              try {
                                 channel.config().setOption(ChannelOption.IP_TOS, 24);
                              } catch (ChannelException var4) {
                              }
               
                              try {
                                 channel.config().setOption(ChannelOption.TCP_NODELAY, false);
                              } catch (ChannelException var3) {
                              }
               
                              channel.pipeline()
                                 .addLast("timeout", new ReadTimeoutHandler(30))
                                 .addLast("legacy_query", new LegacyQueryHandler(ServerNetworkIo.this))
                                 .addLast("splitter", new SplitterHandler())
                                 .addLast("decoder", new DecoderHandler(PacketFlow.SERVERBOUND))
                                 .addLast("prepender", new SizePrepender())
                                 .addLast("encoder", new EncoderHandler(PacketFlow.CLIENTBOUND));
                              Connection var2 = new Connection(PacketFlow.SERVERBOUND);
                              ServerNetworkIo.this.connections.add(var2);
                              channel.pipeline().addLast("packet_handler", var2);
                              var2.setListener(new RemoteHandshakeNetworkHandler(ServerNetworkIo.this.server, var2));
                           }
                        }
                     )
                     .group((EventLoopGroup)NETWORK_GROUP.get())
                     .localAddress(address, port))
                  .bind()
                  .syncUninterruptibly()
            );
      }
   }

   @Environment(EnvType.CLIENT)
   public SocketAddress bind() {
      ChannelFuture var1;
      synchronized(this.channels) {
         var1 = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(LocalServerChannel.class)).childHandler(new ChannelInitializer() {
            protected void initChannel(Channel channel) {
               Connection var2 = new Connection(PacketFlow.SERVERBOUND);
               var2.setListener(new LocalHandshakeNetworkHandler(ServerNetworkIo.this.server, var2));
               ServerNetworkIo.this.connections.add(var2);
               channel.pipeline().addLast("packet_handler", var2);
            }
         }).group((EventLoopGroup)NETWORK_GROUP.get()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
         this.channels.add(var1);
      }

      return var1.channel().localAddress();
   }

   public void close() {
      this.isOpen = false;

      for(ChannelFuture var2 : this.channels) {
         var2.channel().close().syncUninterruptibly();
      }
   }

   public void tick() {
      synchronized(this.connections) {
         Iterator var2 = this.connections.iterator();

         while(var2.hasNext()) {
            final Connection var3 = (Connection)var2.next();
            if (!var3.hasChannel()) {
               if (!var3.isOpen()) {
                  var2.remove();
                  var3.handleDisconnection();
               } else {
                  try {
                     var3.tick();
                  } catch (Exception var8) {
                     if (var3.isLocal()) {
                        CrashReport var10 = CrashReport.of(var8, "Ticking memory connection");
                        CashReportCategory var6 = var10.addCategory("Ticking connection");
                        var6.add("Connection", new Callable() {
                           public String call() {
                              return var3.toString();
                           }
                        });
                        throw new CrashException(var10);
                     }

                     LOGGER.warn("Failed to handle packet for " + var3.getAddress(), var8);
                     final LiteralText var5 = new LiteralText("Internal server error");
                     var3.send(new DisconnectS2CPacket(var5), new GenericFutureListener() {
                        public void operationComplete(Future future) {
                           var3.disconnect(var5);
                        }
                     });
                     var3.disableAutoRead();
                  }
               }
            }
         }
      }
   }

   public MinecraftServer getServer() {
      return this.server;
   }
}
