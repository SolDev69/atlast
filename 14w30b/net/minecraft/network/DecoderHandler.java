package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.packet.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class DecoderHandler extends ByteToMessageDecoder {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker MARKER = MarkerManager.getMarker("PACKET_RECEIVED", Connection.MARKER_NETWORK_PACKETS);
   private final PacketFlow flow;

   public DecoderHandler(PacketFlow flow) {
      this.flow = flow;
   }

   protected void decode(ChannelHandlerContext context, ByteBuf buffer, List packets) {
      if (buffer.readableBytes() != 0) {
         PacketByteBuf var4 = new PacketByteBuf(buffer);
         int var5 = var4.readVarInt();
         Packet var6 = ((NetworkProtocol)context.channel().attr(Connection.PROTOCOL).get()).createPacket(this.flow, var5);
         if (var6 == null) {
            throw new IOException("Bad packet id " + var5);
         } else {
            var6.write(var4);
            if (var4.readableBytes() > 0) {
               throw new IOException(
                  "Packet "
                     + ((NetworkProtocol)context.channel().attr(Connection.PROTOCOL).get()).getId()
                     + "/"
                     + var5
                     + " ("
                     + var6.getClass().getSimpleName()
                     + ") was larger than I expected, found "
                     + var4.readableBytes()
                     + " bytes extra whilst reading packet "
                     + var5
               );
            } else {
               packets.add(var6);
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug(MARKER, " IN: [{}:{}] {}", new Object[]{context.channel().attr(Connection.PROTOCOL).get(), var5, var6.getClass().getName()});
               }
            }
         }
      }
   }
}
