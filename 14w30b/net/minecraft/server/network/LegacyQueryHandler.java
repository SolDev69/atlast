package net.minecraft.server.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LegacyQueryHandler extends ChannelInboundHandlerAdapter {
   private static final Logger LOGGER = LogManager.getLogger();
   private ServerNetworkIo networkIo;

   public LegacyQueryHandler(ServerNetworkIo networkIo) {
      this.networkIo = networkIo;
   }

   public void channelRead(ChannelHandlerContext context, Object obj) {
      ByteBuf var3 = (ByteBuf)obj;
      var3.markReaderIndex();
      boolean var4 = true;

      try {
         try {
            if (var3.readUnsignedByte() != 254) {
               return;
            }

            InetSocketAddress var5 = (InetSocketAddress)context.channel().remoteAddress();
            MinecraftServer var6 = this.networkIo.getServer();
            int var7 = var3.readableBytes();
            switch(var7) {
               case 0:
                  LOGGER.debug("Ping: (<1.3.x) from {}:{}", new Object[]{var5.getAddress(), var5.getPort()});
                  String var23 = String.format("%s§%d§%d", var6.getServerMotd(), var6.getPlayerCount(), var6.getMaxPlayerCount());
                  this.reply(context, this.toBuffer(var23));
                  break;
               case 1:
                  if (var3.readUnsignedByte() != 1) {
                     return;
                  }

                  LOGGER.debug("Ping: (1.4-1.5.x) from {}:{}", new Object[]{var5.getAddress(), var5.getPort()});
                  String var8 = String.format(
                     "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                     127,
                     var6.getGameVersion(),
                     var6.getServerMotd(),
                     var6.getPlayerCount(),
                     var6.getMaxPlayerCount()
                  );
                  this.reply(context, this.toBuffer(var8));
                  break;
               default:
                  boolean var24 = var3.readUnsignedByte() == 1;
                  var24 &= var3.readUnsignedByte() == 250;
                  var24 &= "MC|PingHost".equals(new String(var3.readBytes(var3.readShort() * 2).array(), Charsets.UTF_16BE));
                  int var9 = var3.readUnsignedShort();
                  var24 &= var3.readUnsignedByte() >= 73;
                  var24 &= 3 + var3.readBytes(var3.readShort() * 2).array().length + 4 == var9;
                  var24 &= var3.readInt() <= 65535;
                  var24 &= var3.readableBytes() == 0;
                  if (!var24) {
                     return;
                  }

                  LOGGER.debug("Ping: (1.6) from {}:{}", new Object[]{var5.getAddress(), var5.getPort()});
                  String var10 = String.format(
                     "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                     127,
                     var6.getGameVersion(),
                     var6.getServerMotd(),
                     var6.getPlayerCount(),
                     var6.getMaxPlayerCount()
                  );
                  ByteBuf var11 = this.toBuffer(var10);

                  try {
                     this.reply(context, var11);
                  } finally {
                     var11.release();
                  }
            }

            var3.release();
            var4 = false;
         } catch (RuntimeException var21) {
         }
      } finally {
         if (var4) {
            var3.resetReaderIndex();
            context.channel().pipeline().remove("legacy_query");
            context.fireChannelRead(obj);
         }
      }
   }

   private void reply(ChannelHandlerContext context, ByteBuf buffer) {
      context.pipeline().firstContext().writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);
   }

   private ByteBuf toBuffer(String s) {
      ByteBuf var2 = Unpooled.buffer();
      var2.writeByte(255);
      char[] var3 = s.toCharArray();
      var2.writeShort(var3.length);

      for(char var7 : var3) {
         var2.writeChar(var7);
      }

      return var2;
   }
}
