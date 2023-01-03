package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.AddPlayerS2CPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class EncoderHandler extends MessageToByteEncoder {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker MARKER = MarkerManager.getMarker("PACKET_SENT", Connection.MARKER_NETWORK_PACKETS);
   private final PacketFlow flow;

   public EncoderHandler(PacketFlow flow) {
      this.flow = flow;
   }

   protected void encode(ChannelHandlerContext channelHandlerContext, Packet c_26iwfcgbh, ByteBuf byteBuf) {
      Integer var4 = ((NetworkProtocol)channelHandlerContext.channel().attr(Connection.PROTOCOL).get()).getPacketId(this.flow, c_26iwfcgbh);
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(
            MARKER, "OUT: [{}:{}] {}", new Object[]{channelHandlerContext.channel().attr(Connection.PROTOCOL).get(), var4, c_26iwfcgbh.getClass().getName()}
         );
      }

      if (var4 == null) {
         throw new IOException("Can't serialize unregistered packet");
      } else {
         PacketByteBuf var5 = new PacketByteBuf(byteBuf);
         var5.writeVarInt(var4);

         try {
            if (c_26iwfcgbh instanceof AddPlayerS2CPacket) {
               c_26iwfcgbh = c_26iwfcgbh;
            }

            c_26iwfcgbh.read(var5);
         } catch (Throwable var7) {
            LOGGER.error(var7);
         }
      }
   }
}
