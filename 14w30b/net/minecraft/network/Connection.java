package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.crypto.SecretKey;
import net.minecraft.network.encryption.EncryptionUtils;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.handler.PacketHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.LazySupplier;
import net.minecraft.util.Tickable;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Connection extends SimpleChannelInboundHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Marker MARKER_NETWORK = MarkerManager.getMarker("NETWORK");
   public static final Marker MARKER_NETWORK_PACKETS = MarkerManager.getMarker("NETWORK_PACKETS", MARKER_NETWORK);
   public static final AttributeKey PROTOCOL = AttributeKey.valueOf("protocol");
   public static final LazySupplier NETWORK_GROUP = new LazySupplier() {
      protected NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
      }
   };
   public static final LazySupplier LOCAL_NETWORK_GROUP = new LazySupplier() {
      protected LocalEventLoopGroup load() {
         return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
      }
   };
   private final PacketFlow flow;
   private final Queue sendQueue = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress address;
   private PacketHandler listener;
   private Text disconnectReason;
   private boolean encrypted;

   public Connection(PacketFlow flow) {
      this.flow = flow;
   }

   public void channelActive(ChannelHandlerContext context) {
      super.channelActive(context);
      this.channel = context.channel();
      this.address = this.channel.remoteAddress();

      try {
         this.setProtocol(NetworkProtocol.HANDSHAKE);
      } catch (Throwable var3) {
         LOGGER.fatal(var3);
      }
   }

   public void setProtocol(NetworkProtocol protocol) {
      this.channel.attr(PROTOCOL).set(protocol);
      this.channel.config().setAutoRead(true);
      LOGGER.debug("Enabled auto read");
   }

   public void channelInactive(ChannelHandlerContext context) {
      this.disconnect(new TranslatableText("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext context, Throwable t) {
      LOGGER.debug("Disconnecting " + this.getAddress(), t);
      this.disconnect(new TranslatableText("disconnect.genericReason", "Internal Exception: " + t));
   }

   protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet c_26iwfcgbh) {
      if (this.channel.isOpen()) {
         try {
            c_26iwfcgbh.handle(this.listener);
         } catch (DifferentThreadException var4) {
         }
      }
   }

   public void setListener(PacketHandler listener) {
      Validate.notNull(listener, "packetListener", new Object[0]);
      LOGGER.debug("Set listener of {} to {}", new Object[]{this, listener});
      this.listener = listener;
   }

   public void send(Packet packet) {
      if (this.channel != null && this.channel.isOpen()) {
         this.sendQueuedPackets();
         this.sendNow(packet, null);
      } else {
         this.sendQueue.add(new Connection.QueuedPacket(packet, null));
      }
   }

   public void send(Packet packet, GenericFutureListener listener, GenericFutureListener... listeners) {
      if (this.channel != null && this.channel.isOpen()) {
         this.sendQueuedPackets();
         this.sendNow(packet, (GenericFutureListener[])ArrayUtils.add(listeners, 0, listener));
      } else {
         this.sendQueue.add(new Connection.QueuedPacket(packet, (GenericFutureListener[])ArrayUtils.add(listeners, 0, listener)));
      }
   }

   private void sendNow(Packet packet, GenericFutureListener[] listeners) {
      final NetworkProtocol var3 = NetworkProtocol.byPacket(packet);
      final NetworkProtocol var4 = (NetworkProtocol)this.channel.attr(PROTOCOL).get();
      if (var4 != var3) {
         LOGGER.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (var3 != var4) {
            this.setProtocol(var3);
         }

         ChannelFuture var5 = this.channel.writeAndFlush(packet);
         if (listeners != null) {
            var5.addListeners(listeners);
         }

         var5.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               if (var3 != var4) {
                  Connection.this.setProtocol(var3);
               }

               ChannelFuture var1 = Connection.this.channel.writeAndFlush(packet);
               if (listeners != null) {
                  var1.addListeners(listeners);
               }

               var1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
         });
      }
   }

   private void sendQueuedPackets() {
      if (this.channel != null && this.channel.isOpen()) {
         while(!this.sendQueue.isEmpty()) {
            Connection.QueuedPacket var1 = (Connection.QueuedPacket)this.sendQueue.poll();
            this.sendNow(var1.packet, var1.listeners);
         }
      }
   }

   public void tick() {
      this.sendQueuedPackets();
      if (this.listener instanceof Tickable) {
         ((Tickable)this.listener).tick();
      }

      this.channel.flush();
   }

   public SocketAddress getAddress() {
      return this.address;
   }

   public void disconnect(Text reason) {
      if (this.channel.isOpen()) {
         this.channel.close();
         this.disconnectReason = reason;
      }
   }

   public boolean isLocal() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   @Environment(EnvType.CLIENT)
   public static Connection connect(InetAddress address, int port) {
      final Connection var2 = new Connection(PacketFlow.CLIENTBOUND);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)NETWORK_GROUP.get()))
               .handler(
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
                           .addLast("timeout", new ReadTimeoutHandler(20))
                           .addLast("splitter", new SplitterHandler())
                           .addLast("decoder", new DecoderHandler(PacketFlow.CLIENTBOUND))
                           .addLast("prepender", new SizePrepender())
                           .addLast("encoder", new EncoderHandler(PacketFlow.SERVERBOUND))
                           .addLast("packet_handler", var2);
                     }
                  }
               ))
            .channel(NioSocketChannel.class))
         .connect(address, port)
         .syncUninterruptibly();
      return var2;
   }

   @Environment(EnvType.CLIENT)
   public static Connection connectLocal(SocketAddress address) {
      final Connection var1 = new Connection(PacketFlow.CLIENTBOUND);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)LOCAL_NETWORK_GROUP.get())).handler(new ChannelInitializer() {
         protected void initChannel(Channel channel) {
            channel.pipeline().addLast("packet_handler", var1);
         }
      })).channel(LocalChannel.class)).connect(address).syncUninterruptibly();
      return var1;
   }

   public void setupEncryption(SecretKey key) {
      this.encrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecryptor(EncryptionUtils.crypt(2, key)));
      this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncryptor(EncryptionUtils.crypt(1, key)));
   }

   @Environment(EnvType.CLIENT)
   public boolean isEncrypted() {
      return this.encrypted;
   }

   public boolean isOpen() {
      return this.channel != null && this.channel.isOpen();
   }

   public PacketHandler getListener() {
      return this.listener;
   }

   public Text getDisconnectReason() {
      return this.disconnectReason;
   }

   public void disableAutoRead() {
      this.channel.config().setAutoRead(false);
   }

   public void setCompressionThreshold(int threshold) {
      if (threshold >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            ((CompressionDecoder)this.channel.pipeline().get("decompress")).setThreshold(threshold);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new CompressionDecoder(threshold));
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            ((CompressionEncoder)this.channel.pipeline().get("decompress")).setThreshold(threshold);
         } else {
            this.channel.pipeline().addBefore("encoder", "compress", new CompressionEncoder(threshold));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }
   }

   static class QueuedPacket {
      private final Packet packet;
      private final GenericFutureListener[] listeners;

      public QueuedPacket(Packet packet, GenericFutureListener... listeners) {
         this.packet = packet;
         this.listeners = listeners;
      }
   }
}
