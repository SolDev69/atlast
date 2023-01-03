package net.minecraft.server.network.handler;

import net.minecraft.network.handler.PacketHandler;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseMenuC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandSuggestionsC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmMenuActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeMenuSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.MenuClickButtonC2SPacket;
import net.minecraft.network.packet.c2s.play.MenuClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerHandActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMovementActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerSpectateC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerUseItemC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.SignUpdateC2SPacket;

public interface ServerPlayPacketHandler extends PacketHandler {
   void handleHandSwing(HandSwingC2SPacket packet);

   void handleChatMessage(ChatMessageC2SPacket packet);

   void handleCommandSuggestions(CommandSuggestionsC2SPacket packet);

   void handleClientStatus(ClientStatusC2SPacket packet);

   void handleClientSettings(ClientSettingsC2SPacket packet);

   void handleConfirmMenuAction(ConfirmMenuActionC2SPacket packet);

   void handleMenuClickButton(MenuClickButtonC2SPacket packet);

   void handleMenuClickSlot(MenuClickSlotC2SPacket packet);

   void handleCloseMenu(CloseMenuC2SPacket packet);

   void handleCustomPayload(CustomPayloadC2SPacket packet);

   void handleInteractEntity(PlayerInteractEntityC2SPacket packet);

   void handleKeepAlive(KeepAliveC2SPacket packet);

   void handlePlayerMove(PlayerMoveC2SPacket packet);

   void handlePlayerAbilities(PlayerAbilitiesC2SPacket packet);

   void handlePlayerHandAction(PlayerHandActionC2SPacket packet);

   void handlePlayerMovementAction(PlayerMovementActionC2SPacket packet);

   void handlePlayerInput(PlayerInputC2SPacket packet);

   void handleSelectSlot(SelectSlotC2SPacket packet);

   void handleCreativeMenuSlot(CreativeMenuSlotC2SPacket packet);

   void handleSignUpdate(SignUpdateC2SPacket packet);

   void handlePlayerUseItem(PlayerUseItemC2SPacket packet);

   void handlePlayerSpectate(PlayerSpectateC2SPacket packet);
}
