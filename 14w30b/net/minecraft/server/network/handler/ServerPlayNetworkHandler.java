package net.minecraft.server.network.handler;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.AnvilMenu;
import net.minecraft.inventory.menu.BeaconMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.TraderMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.BookAndQuillItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketUtils;
import net.minecraft.network.packet.Packet;
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
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmMenuActionS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.MenuSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerBanEntry;
import net.minecraft.server.command.source.CommandExecutor;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Int2ObjectHashMap;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements ServerPlayPacketHandler, Tickable {
   private static final Logger LOGGER = LogManager.getLogger();
   public final Connection connection;
   private final MinecraftServer server;
   public ServerPlayerEntity player;
   private int ticks;
   private int lastTeleportTime;
   private int floatingTime;
   private boolean moved;
   private int savedKeepAliveTime;
   private long keepAliveTime;
   private long lastKeepAliveUpdateTime;
   private int messageCooldown;
   private int dropItemCooldown;
   private Int2ObjectHashMap transactions = new Int2ObjectHashMap();
   private double teleportTargetX;
   private double teleportTargetY;
   private double teleportTargetZ;
   private boolean teleported = true;

   public ServerPlayNetworkHandler(MinecraftServer server, Connection connection, ServerPlayerEntity player) {
      this.server = server;
      this.connection = connection;
      connection.setListener(this);
      this.player = player;
      player.networkHandler = this;
   }

   @Override
   public void tick() {
      this.moved = false;
      ++this.ticks;
      this.server.profiler.push("keepAlive");
      if ((long)this.ticks - this.lastKeepAliveUpdateTime > 40L) {
         this.lastKeepAliveUpdateTime = (long)this.ticks;
         this.keepAliveTime = this.getTimeMillis();
         this.savedKeepAliveTime = (int)this.keepAliveTime;
         this.sendPacket(new KeepAliveS2CPacket(this.savedKeepAliveTime));
      }

      this.server.profiler.pop();
      if (this.messageCooldown > 0) {
         --this.messageCooldown;
      }

      if (this.dropItemCooldown > 0) {
         --this.dropItemCooldown;
      }

      if (this.player.getLastActionTime() > 0L
         && this.server.getPlayerIdleTimeout() > 0
         && MinecraftServer.getTimeMillis() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
         this.disconnect("You have been idle for too long!");
      }
   }

   public Connection getConnection() {
      return this.connection;
   }

   public void disconnect(String reason) {
      final LiteralText var2 = new LiteralText(reason);
      this.connection.send(new DisconnectS2CPacket(var2), new GenericFutureListener() {
         public void operationComplete(Future future) {
            ServerPlayNetworkHandler.this.connection.disconnect(var2);
         }
      });
      this.connection.disableAutoRead();
   }

   @Override
   public void handlePlayerInput(PlayerInputC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.setPlayerInput(packet.getSidewaysSpeed(), packet.getForwardSpeed(), packet.getJumping(), packet.getSneaking());
   }

   @Override
   public void handlePlayerMove(PlayerMoveC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      ServerWorld var2 = this.server.getWorld(this.player.dimensionId);
      this.moved = true;
      if (!this.player.leavingTheEnd) {
         double var3 = this.player.x;
         double var5 = this.player.y;
         double var7 = this.player.z;
         if (!this.teleported) {
            double var9 = packet.getX() - this.teleportTargetX;
            double var11 = packet.getY() - this.teleportTargetY;
            double var13 = packet.getZ() - this.teleportTargetZ;
            if (var9 * var9 + var11 * var11 + var13 * var13 < 0.25) {
               this.teleported = true;
            }
         }

         if (this.teleported) {
            this.lastTeleportTime = this.ticks;
            if (this.player.vehicle != null) {
               float var40 = this.player.yaw;
               float var10 = this.player.pitch;
               this.player.vehicle.updateRiderPositon();
               double var42 = this.player.x;
               double var44 = this.player.y;
               double var45 = this.player.z;
               if (packet.hasAngles()) {
                  var40 = packet.getYaw();
                  var10 = packet.getPitch();
               }

               this.player.onGround = packet.getOnGround();
               this.player.tickPlayer();
               this.player.teleport(var42, var44, var45, var40, var10);
               if (this.player.vehicle != null) {
                  this.player.vehicle.updateRiderPositon();
               }

               this.server.getPlayerManager().updateTrackedPos(this.player);
               if (this.teleported) {
                  this.teleportTargetX = this.player.x;
                  this.teleportTargetY = this.player.y;
                  this.teleportTargetZ = this.player.z;
               }

               var2.tickEntity(this.player);
               return;
            }

            if (this.player.isSleeping()) {
               this.player.tickPlayer();
               this.player.teleport(this.teleportTargetX, this.teleportTargetY, this.teleportTargetZ, this.player.yaw, this.player.pitch);
               var2.tickEntity(this.player);
               return;
            }

            double var39 = this.player.y;
            this.teleportTargetX = this.player.x;
            this.teleportTargetY = this.player.y;
            this.teleportTargetZ = this.player.z;
            double var41 = this.player.x;
            double var43 = this.player.y;
            double var15 = this.player.z;
            float var17 = this.player.yaw;
            float var18 = this.player.pitch;
            if (packet.hasPos() && packet.getY() == -999.0) {
               packet.setHasPos(false);
            }

            if (packet.hasPos()) {
               var41 = packet.getX();
               var43 = packet.getY();
               var15 = packet.getZ();
               if (Math.abs(packet.getX()) > 3.0E7 || Math.abs(packet.getZ()) > 3.0E7) {
                  this.disconnect("Illegal position");
                  return;
               }
            }

            if (packet.hasAngles()) {
               var17 = packet.getYaw();
               var18 = packet.getPitch();
            }

            this.player.tickPlayer();
            this.player.teleport(this.teleportTargetX, this.teleportTargetY, this.teleportTargetZ, var17, var18);
            if (!this.teleported) {
               return;
            }

            double var19 = var41 - this.player.x;
            double var21 = var43 - this.player.y;
            double var23 = var15 - this.player.z;
            double var25 = Math.min(Math.abs(var19), Math.abs(this.player.velocityX));
            double var27 = Math.min(Math.abs(var21), Math.abs(this.player.velocityY));
            double var29 = Math.min(Math.abs(var23), Math.abs(this.player.velocityZ));
            double var31 = var25 * var25 + var27 * var27 + var29 * var29;
            if (var31 > 100.0 && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(this.player.getName()))) {
               LOGGER.warn(
                  this.player.getName() + " moved too quickly! " + var19 + "," + var21 + "," + var23 + " (" + var25 + ", " + var27 + ", " + var29 + ")"
               );
               this.teleport(this.teleportTargetX, this.teleportTargetY, this.teleportTargetZ, this.player.yaw, this.player.pitch);
               return;
            }

            float var33 = 0.0625F;
            boolean var34 = var2.getCollisions(this.player, this.player.getBoundingBox().contract((double)var33, (double)var33, (double)var33)).isEmpty();
            if (this.player.onGround && !packet.getOnGround() && var21 > 0.0) {
               this.player.jump();
            }

            this.player.move(var19, var21, var23);
            this.player.onGround = packet.getOnGround();
            var19 = var41 - this.player.x;
            var21 = var43 - this.player.y;
            if (var21 > -0.5 || var21 < 0.5) {
               var21 = 0.0;
            }

            var23 = var15 - this.player.z;
            var31 = var19 * var19 + var21 * var21 + var23 * var23;
            boolean var37 = false;
            if (var31 > 0.0625 && !this.player.isSleeping() && !this.player.interactionManager.isCreative()) {
               var37 = true;
               LOGGER.warn(this.player.getName() + " moved wrongly!");
            }

            this.player.teleport(var41, var43, var15, var17, var18);
            this.player.tickNonRidingMovmentRelatedStats(this.player.x - var3, this.player.y - var5, this.player.z - var7);
            if (!this.player.noClip) {
               boolean var38 = var2.getCollisions(this.player, this.player.getBoundingBox().contract((double)var33, (double)var33, (double)var33)).isEmpty();
               if (var34 && (var37 || !var38) && !this.player.isSleeping()) {
                  this.teleport(this.teleportTargetX, this.teleportTargetY, this.teleportTargetZ, var17, var18);
                  return;
               }
            }

            Box var50 = this.player.getBoundingBox().expand((double)var33, (double)var33, (double)var33).grow(0.0, -0.55, 0.0);
            if (this.server.isFlightEnabled() || this.player.abilities.canFly || var2.containsNonAir(var50)) {
               this.floatingTime = 0;
            } else if (var21 >= -0.03125) {
               ++this.floatingTime;
               if (this.floatingTime > 80) {
                  LOGGER.warn(this.player.getName() + " was kicked for floating too long!");
                  this.disconnect("Flying is not enabled on this server");
                  return;
               }
            }

            this.player.onGround = packet.getOnGround();
            this.server.getPlayerManager().updateTrackedPos(this.player);
            this.player.handleFall(this.player.y - var39, packet.getOnGround());
         } else if (this.ticks - this.lastTeleportTime > 20) {
            this.teleport(this.teleportTargetX, this.teleportTargetY, this.teleportTargetZ, this.player.yaw, this.player.pitch);
         }
      }
   }

   public void teleport(double x, double y, double z, float yaw, float pitch) {
      this.teleport(x, y, z, yaw, pitch, Collections.emptySet());
   }

   public void teleport(double x, double y, double z, float yaw, float pitch, Set relativeArgs) {
      this.teleported = false;
      this.teleportTargetX = x;
      this.teleportTargetY = y;
      this.teleportTargetZ = z;
      if (relativeArgs.contains(PlayerMoveS2CPacket.Argument.X)) {
         this.teleportTargetX += this.player.x;
      }

      if (relativeArgs.contains(PlayerMoveS2CPacket.Argument.Y)) {
         this.teleportTargetY += this.player.y;
      }

      if (relativeArgs.contains(PlayerMoveS2CPacket.Argument.Z)) {
         this.teleportTargetZ += this.player.z;
      }

      float var10 = yaw;
      float var11 = pitch;
      if (relativeArgs.contains(PlayerMoveS2CPacket.Argument.YAW)) {
         var10 = yaw + this.player.yaw;
      }

      if (relativeArgs.contains(PlayerMoveS2CPacket.Argument.PITCH)) {
         var11 = pitch + this.player.pitch;
      }

      this.player.teleport(this.teleportTargetX, this.teleportTargetY, this.teleportTargetZ, var10, var11);
      this.player.networkHandler.sendPacket(new PlayerMoveS2CPacket(x, y, z, yaw, pitch, relativeArgs));
   }

   @Override
   public void handlePlayerHandAction(PlayerHandActionC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      ServerWorld var2 = this.server.getWorld(this.player.dimensionId);
      BlockPos var3 = packet.getPos();
      this.player.updateLastActionTime();
      switch(packet.getAction()) {
         case DROP_ITEM:
            if (!this.player.isSpectator()) {
               this.player.dropItem(false);
            }

            return;
         case DROP_ALL_ITEMS:
            if (!this.player.isSpectator()) {
               this.player.dropItem(true);
            }

            return;
         case RELEASE_USE_ITEM:
            this.player.stopUsingHand();
            return;
         case START_DESTROY_BLOCK:
         case ABORT_DESTROY_BLOCK:
         case STOP_DESTROY_BLOCK:
            double var4 = this.player.x - ((double)var3.getX() + 0.5);
            double var6 = this.player.y - ((double)var3.getY() + 0.5) + 1.5;
            double var8 = this.player.z - ((double)var3.getZ() + 0.5);
            double var10 = var4 * var4 + var6 * var6 + var8 * var8;
            if (var10 > 36.0) {
               return;
            } else if (var3.getY() >= this.server.getWorldHeight()) {
               return;
            } else {
               if (packet.getAction() == PlayerHandActionC2SPacket.Action.START_DESTROY_BLOCK) {
                  if (!this.server.isSpawnProtected(var2, var3, this.player) && var2.getWorldBorder().contains(var3)) {
                     this.player.interactionManager.startMiningBlock(var3, packet.getFace());
                  } else {
                     this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(var2, var3));
                  }
               } else {
                  if (packet.getAction() == PlayerHandActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
                     this.player.interactionManager.finishMiningBlock(var3);
                  } else if (packet.getAction() == PlayerHandActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
                     this.player.interactionManager.stopMiningBlock();
                  }

                  if (var2.getBlockState(var3).getBlock().getMaterial() != Material.AIR) {
                     this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(var2, var3));
                  }
               }

               return;
            }
         default:
            throw new IllegalArgumentException("Invalid player action");
      }
   }

   @Override
   public void handlePlayerUseItem(PlayerUseItemC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      ServerWorld var2 = this.server.getWorld(this.player.dimensionId);
      ItemStack var3 = this.player.inventory.getMainHandStack();
      boolean var4 = false;
      BlockPos var5 = packet.getPos();
      Direction var6 = Direction.byId(packet.getFace());
      this.player.updateLastActionTime();
      if (packet.getFace() == 255) {
         if (var3 == null) {
            return;
         }

         this.player.interactionManager.useItem(this.player, var2, var3);
      } else if (var5.getY() < this.server.getWorldHeight() - 1 || var6 != Direction.UP && var5.getY() < this.server.getWorldHeight()) {
         if (this.teleported
            && this.player.getSquaredDistanceTo((double)var5.getX() + 0.5, (double)var5.getY() + 0.5, (double)var5.getZ() + 0.5) < 64.0
            && !this.server.isSpawnProtected(var2, var5, this.player)
            && var2.getWorldBorder().contains(var5)) {
            this.player.interactionManager.interactBlock(this.player, var2, var3, var5, var6, packet.getDx(), packet.getDy(), packet.getDz());
         }

         var4 = true;
      } else {
         TranslatableText var7 = new TranslatableText("build.tooHigh", this.server.getWorldHeight());
         var7.getStyle().setColor(Formatting.RED);
         this.player.networkHandler.sendPacket(new ChatMessageS2CPacket(var7));
         var4 = true;
      }

      if (var4) {
         this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(var2, var5));
         this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(var2, var5.offset(var6)));
      }

      var3 = this.player.inventory.getMainHandStack();
      if (var3 != null && var3.size == 0) {
         this.player.inventory.inventorySlots[this.player.inventory.selectedSlot] = null;
         var3 = null;
      }

      if (var3 == null || var3.getUseDuration() == 0) {
         this.player.useItemCooldown = true;
         this.player.inventory.inventorySlots[this.player.inventory.selectedSlot] = ItemStack.copyOf(
            this.player.inventory.inventorySlots[this.player.inventory.selectedSlot]
         );
         InventorySlot var9 = this.player.menu.getSlot(this.player.inventory, this.player.inventory.selectedSlot);
         this.player.menu.updateListeners();
         this.player.useItemCooldown = false;
         if (!ItemStack.matches(this.player.inventory.getMainHandStack(), packet.getStack())) {
            this.sendPacket(new MenuSlotUpdateS2CPacket(this.player.menu.networkId, var9.id, this.player.inventory.getMainHandStack()));
         }
      }
   }

   @Override
   public void handlePlayerSpectate(PlayerSpectateC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      if (this.player.isSpectator()) {
         Entity var2 = null;

         for(ServerWorld var6 : this.server.worlds) {
            if (var6 != null) {
               var2 = packet.getSpectateTarget(var6);
               if (var2 != null) {
                  break;
               }
            }
         }

         if (var2 != null) {
            this.player.setCamera(this.player);
            this.player.startRiding(null);
            if (var2.world != this.player.world) {
               ServerWorld var7 = this.player.getServerWorld();
               ServerWorld var8 = (ServerWorld)var2.world;
               this.player.dimensionId = var2.dimensionId;
               this.sendPacket(
                  new PlayerRespawnS2CPacket(
                     this.player.dimensionId, var7.getDifficulty(), var7.getData().getGeneratorType(), this.player.interactionManager.getGameMode()
                  )
               );
               var7.removeEntityNow(this.player);
               this.player.removed = false;
               this.player.refreshPositionAndAngles(var2.x, var2.y, var2.z, var2.yaw, var2.pitch);
               if (this.player.isAlive()) {
                  var7.tickEntity(this.player, false);
                  var8.addEntity(this.player);
                  var8.tickEntity(this.player, false);
               }

               this.player.setWorld(var8);
               this.server.getPlayerManager().onChangedDimension(this.player, var7);
               this.player.refreshPosition(var2.x, var2.y, var2.z);
               this.player.interactionManager.setWorld(var8);
               this.server.getPlayerManager().sendWorldInfo(this.player, var8);
               this.server.getPlayerManager().sendPlayerInfo(this.player);
            } else {
               this.player.refreshPosition(var2.x, var2.y, var2.z);
            }
         }
      }
   }

   @Override
   public void onDisconnect(Text reason) {
      LOGGER.info(this.player.getName() + " lost connection: " + reason);
      this.server.forcePlayerSampleUpdate();
      TranslatableText var2 = new TranslatableText("multiplayer.player.left", this.player.getDisplayName());
      var2.getStyle().setColor(Formatting.YELLOW);
      this.server.getPlayerManager().sendSystemMessage(var2);
      this.player.onDisconnect();
      this.server.getPlayerManager().remove(this.player);
      if (this.server.isSinglePlayer() && this.player.getName().equals(this.server.getUserName())) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.stopRunning();
      }
   }

   public void sendPacket(Packet packet) {
      if (packet instanceof ChatMessageS2CPacket) {
         ChatMessageS2CPacket var2 = (ChatMessageS2CPacket)packet;
         PlayerEntity.ChatVisibility var3 = this.player.getChatVisibility();
         if (var3 == PlayerEntity.ChatVisibility.HIDDEN) {
            return;
         }

         if (var3 == PlayerEntity.ChatVisibility.SYSTEM && !var2.isSystemMessage()) {
            return;
         }
      }

      try {
         this.connection.send(packet);
      } catch (Throwable var5) {
         CrashReport var6 = CrashReport.of(var5, "Sending packet");
         CashReportCategory var4 = var6.addCategory("Packet being sent");
         var4.add("Packet class", new Callable() {
            public String call() {
               return packet.getClass().getCanonicalName();
            }
         });
         throw new CrashException(var6);
      }
   }

   @Override
   public void handleSelectSlot(SelectSlotC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      if (packet.getSlot() >= 0 && packet.getSlot() < PlayerInventory.getHotbarSize()) {
         this.player.inventory.selectedSlot = packet.getSlot();
         this.player.updateLastActionTime();
      } else {
         LOGGER.warn(this.player.getName() + " tried to set an invalid carried item");
      }
   }

   @Override
   public void handleChatMessage(ChatMessageC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      if (this.player.getChatVisibility() == PlayerEntity.ChatVisibility.HIDDEN) {
         TranslatableText var5 = new TranslatableText("chat.cannotSend");
         var5.getStyle().setColor(Formatting.RED);
         this.sendPacket(new ChatMessageS2CPacket(var5));
      } else {
         this.player.updateLastActionTime();
         String var2 = packet.getMessage();
         var2 = StringUtils.normalizeSpace(var2);

         for(int var3 = 0; var3 < var2.length(); ++var3) {
            if (!SharedConstants.isValidChatChar(var2.charAt(var3))) {
               this.disconnect("Illegal characters in chat");
               return;
            }
         }

         if (var2.startsWith("/")) {
            this.runCommand(var2);
         } else {
            TranslatableText var6 = new TranslatableText("chat.type.text", this.player.getDisplayName(), var2);
            this.server.getPlayerManager().sendMessage(var6, false);
         }

         this.messageCooldown += 20;
         if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOp(this.player.getGameProfile())) {
            this.disconnect("disconnect.spam");
         }
      }
   }

   private void runCommand(String command) {
      this.server.getCommandHandler().run(this.player, command);
   }

   @Override
   public void handleHandSwing(HandSwingC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.updateLastActionTime();
      this.player.swingHand();
   }

   @Override
   public void handlePlayerMovementAction(PlayerMovementActionC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.updateLastActionTime();
      switch(packet.getAction()) {
         case START_SNEAKING:
            this.player.setSneaking(true);
            break;
         case STOP_SNEAKING:
            this.player.setSneaking(false);
            break;
         case START_SPRINTING:
            this.player.setSprinting(true);
            break;
         case STOP_SPRINTING:
            this.player.setSprinting(false);
            break;
         case STOP_SLEEPING:
            this.player.wakeUp(false, true, true);
            this.teleported = false;
            break;
         case RIDING_JUMP:
            if (this.player.vehicle instanceof HorseBaseEntity) {
               ((HorseBaseEntity)this.player.vehicle).setJumpStrength(packet.getData());
            }
            break;
         case OPEN_HORSE_INVENTORY:
            if (this.player.vehicle instanceof HorseBaseEntity) {
               ((HorseBaseEntity)this.player.vehicle).openInventory(this.player);
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid client command!");
      }
   }

   @Override
   public void handleInteractEntity(PlayerInteractEntityC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      ServerWorld var2 = this.server.getWorld(this.player.dimensionId);
      Entity var3 = packet.getInteractTarget(var2);
      this.player.updateLastActionTime();
      if (var3 != null) {
         boolean var4 = this.player.canSee(var3);
         double var5 = 36.0;
         if (!var4) {
            var5 = 9.0;
         }

         if (this.player.getSquaredDistanceTo(var3) < var5) {
            if (packet.getAction() == PlayerInteractEntityC2SPacket.Action.INTERACT) {
               this.player.interact(var3);
            } else if (packet.getAction() == PlayerInteractEntityC2SPacket.Action.ATTACK) {
               if (var3 instanceof ItemEntity || var3 instanceof XpOrbEntity || var3 instanceof ArrowEntity || var3 == this.player) {
                  this.disconnect("Attempting to attack an invalid entity");
                  this.server.warn("Player " + this.player.getName() + " tried to attack an invalid entity");
                  return;
               }

               this.player.attack(var3);
            }
         }
      }
   }

   @Override
   public void handleClientStatus(ClientStatusC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.updateLastActionTime();
      ClientStatusC2SPacket.Status var2 = packet.getStatus();
      switch(var2) {
         case PERFORM_RESPAWN:
            if (this.player.leavingTheEnd) {
               this.player = this.server.getPlayerManager().respawn(this.player, 0, true);
            } else if (this.player.getServerWorld().getData().isHardcore()) {
               if (this.server.isSinglePlayer() && this.player.getName().equals(this.server.getUserName())) {
                  this.player.networkHandler.disconnect("You have died. Game over, man, it's game over!");
                  this.server.deleteWorldAndStop();
               } else {
                  PlayerBanEntry var3 = new PlayerBanEntry(this.player.getGameProfile(), null, "(You just lost the game)", null, "Death in Hardcore");
                  this.server.getPlayerManager().getPlayerBans().add(var3);
                  this.player.networkHandler.disconnect("You have died. Game over, man, it's game over!");
               }
            } else {
               if (this.player.getHealth() > 0.0F) {
                  return;
               }

               this.player = this.server.getPlayerManager().respawn(this.player, 0, false);
            }
            break;
         case REQUEST_STATS:
            this.player.getStatHandler().sendStats(this.player);
            break;
         case OPEN_INVENTORY_ACHIEVEMENT:
            this.player.incrementStat(Achievements.OPEN_INVENTORY);
      }
   }

   @Override
   public void handleCloseMenu(CloseMenuC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.doCloseMenu();
   }

   @Override
   public void handleMenuClickSlot(MenuClickSlotC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.updateLastActionTime();
      if (this.player.menu.networkId == packet.getMenuId() && this.player.menu.isSynced(this.player)) {
         if (this.player.isSpectator()) {
            ArrayList var2 = Lists.newArrayList();

            for(int var3 = 0; var3 < this.player.menu.slots.size(); ++var3) {
               var2.add(((InventorySlot)this.player.menu.slots.get(var3)).getStack());
            }

            this.player.updateMenu(this.player.menu, var2);
         } else {
            ItemStack var5 = this.player.menu.onClickSlot(packet.getSlotId(), packet.getClickData(), packet.getAction(), this.player);
            if (ItemStack.matches(packet.getStack(), var5)) {
               this.player.networkHandler.sendPacket(new ConfirmMenuActionS2CPacket(packet.getMenuId(), packet.getActionId(), true));
               this.player.useItemCooldown = true;
               this.player.menu.updateListeners();
               this.player.use();
               this.player.useItemCooldown = false;
            } else {
               this.transactions.put(this.player.menu.networkId, packet.getActionId());
               this.player.networkHandler.sendPacket(new ConfirmMenuActionS2CPacket(packet.getMenuId(), packet.getActionId(), false));
               this.player.menu.setSynced(this.player, false);
               ArrayList var6 = Lists.newArrayList();

               for(int var4 = 0; var4 < this.player.menu.slots.size(); ++var4) {
                  var6.add(((InventorySlot)this.player.menu.slots.get(var4)).getStack());
               }

               this.player.updateMenu(this.player.menu, var6);
            }
         }
      }
   }

   @Override
   public void handleMenuClickButton(MenuClickButtonC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.updateLastActionTime();
      if (this.player.menu.networkId == packet.getMenuId() && this.player.menu.isSynced(this.player) && !this.player.isSpectator()) {
         this.player.menu.onButtonClick(this.player, packet.getButtonId());
         this.player.menu.updateListeners();
      }
   }

   @Override
   public void handleCreativeMenuSlot(CreativeMenuSlotC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      if (this.player.interactionManager.isCreative()) {
         boolean var2 = packet.getSlotId() < 0;
         ItemStack var3 = packet.getStack();
         if (var3 != null && var3.hasNbt() && var3.getNbt().isType("BlockEntityTag", 10)) {
            NbtCompound var4 = var3.getNbt().getCompound("BlockEntityTag");
            if (var4.contains("x") && var4.contains("y") && var4.contains("z")) {
               BlockPos var5 = new BlockPos(var4.getInt("x"), var4.getInt("y"), var4.getInt("z"));
               BlockEntity var6 = this.player.world.getBlockEntity(var5);
               if (var6 != null) {
                  NbtCompound var7 = new NbtCompound();
                  var6.writeNbt(var7);
                  var7.remove("x");
                  var7.remove("y");
                  var7.remove("z");
                  var3.addToNbt("BlockEntityTag", var7);
               }
            }
         }

         boolean var8 = packet.getSlotId() >= 1 && packet.getSlotId() < 36 + PlayerInventory.getHotbarSize();
         boolean var9 = var3 == null || var3.getItem() != null;
         boolean var10 = var3 == null || var3.getMetadata() >= 0 && var3.size <= 64 && var3.size > 0;
         if (var8 && var9 && var10) {
            if (var3 == null) {
               this.player.playerMenu.setStack(packet.getSlotId(), null);
            } else {
               this.player.playerMenu.setStack(packet.getSlotId(), var3);
            }

            this.player.playerMenu.setSynced(this.player, true);
         } else if (var2 && var9 && var10 && this.dropItemCooldown < 200) {
            this.dropItemCooldown += 20;
            ItemEntity var11 = this.player.dropItem(var3, true);
            if (var11 != null) {
               var11.resetAge();
            }
         }
      }
   }

   @Override
   public void handleConfirmMenuAction(ConfirmMenuActionC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      Short var2 = (Short)this.transactions.get(this.player.menu.networkId);
      if (var2 != null
         && packet.getActionId() == var2
         && this.player.menu.networkId == packet.getMenuId()
         && !this.player.menu.isSynced(this.player)
         && !this.player.isSpectator()) {
         this.player.menu.setSynced(this.player, true);
      }
   }

   @Override
   public void handleSignUpdate(SignUpdateC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.updateLastActionTime();
      ServerWorld var2 = this.server.getWorld(this.player.dimensionId);
      BlockPos var3 = packet.getPos();
      if (var2.isLoaded(var3)) {
         BlockEntity var4 = var2.getBlockEntity(var3);
         if (!(var4 instanceof SignBlockEntity)) {
            return;
         }

         SignBlockEntity var5 = (SignBlockEntity)var4;
         if (!var5.isEditable() || var5.getPlayer() != this.player) {
            this.server.warn("Player " + this.player.getName() + " just tried to change non-editable sign");
            return;
         }

         System.arraycopy(packet.getLines(), 0, var5.lines, 0, 4);
         var5.markDirty();
         var2.onBlockChanged(var3);
      }
   }

   @Override
   public void handleKeepAlive(KeepAliveC2SPacket packet) {
      if (packet.getTimeMillis() == this.savedKeepAliveTime) {
         int var2 = (int)(this.getTimeMillis() - this.keepAliveTime);
         this.player.ping = (this.player.ping * 3 + var2) / 4;
      }
   }

   private long getTimeMillis() {
      return System.nanoTime() / 1000000L;
   }

   @Override
   public void handlePlayerAbilities(PlayerAbilitiesC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.abilities.flying = packet.isFlying() && this.player.abilities.canFly;
   }

   @Override
   public void handleCommandSuggestions(CommandSuggestionsC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      ArrayList var2 = Lists.newArrayList();

      for(String var4 : this.server.getCommandSuggestions(this.player, packet.getCommand())) {
         var2.add(var4);
      }

      this.player.networkHandler.sendPacket(new CommandSuggestionsS2CPacket(var2.toArray(new String[var2.size()])));
   }

   @Override
   public void handleClientSettings(ClientSettingsC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      this.player.updateSettings(packet);
   }

   @Override
   public void handleCustomPayload(CustomPayloadC2SPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.player.getServerWorld());
      if ("MC|BEdit".equals(packet.getChannel())) {
         PacketByteBuf var44 = new PacketByteBuf(Unpooled.wrappedBuffer(packet.getData()));

         try {
            ItemStack var49 = var44.readItemStack();
            if (var49 == null) {
               return;
            }

            if (!BookAndQuillItem.isValid(var49.getNbt())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack var53 = this.player.inventory.getMainHandStack();
            if (var53 != null) {
               if (var49.getItem() == Items.WRITABLE_BOOK && var49.getItem() == var53.getItem()) {
                  var53.addToNbt("pages", var49.getNbt().getList("pages", 8));
               }

               return;
            }
         } catch (Exception var36) {
            LOGGER.error("Couldn't handle book info", var36);
            return;
         } finally {
            var44.release();
         }

         return;
      } else if ("MC|BSign".equals(packet.getChannel())) {
         PacketByteBuf var43 = new PacketByteBuf(Unpooled.wrappedBuffer(packet.getData()));

         try {
            ItemStack var48 = var43.readItemStack();
            if (var48 == null) {
               return;
            }

            if (!WrittenBookItem.isValid(var48.getNbt())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack var52 = this.player.inventory.getMainHandStack();
            if (var52 != null) {
               if (var48.getItem() == Items.WRITTEN_BOOK && var52.getItem() == Items.WRITABLE_BOOK) {
                  var52.addToNbt("author", new NbtString(this.player.getName()));
                  var52.addToNbt("title", new NbtString(var48.getNbt().getString("title")));
                  var52.addToNbt("pages", var48.getNbt().getList("pages", 8));
                  var52.setItem(Items.WRITTEN_BOOK);
               }

               return;
            }
         } catch (Exception var38) {
            LOGGER.error("Couldn't sign book", var38);
            return;
         } finally {
            var43.release();
         }

         return;
      } else if ("MC|TrSel".equals(packet.getChannel())) {
         try {
            DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(packet.getData()));
            int var3 = var2.readInt();
            InventoryMenu var4 = this.player.menu;
            if (var4 instanceof TraderMenu) {
               ((TraderMenu)var4).setRecipeIndex(var3);
            }
         } catch (Exception var35) {
            LOGGER.error("Couldn't select trade", var35);
         }
      } else if ("MC|AdvCdm".equals(packet.getChannel())) {
         if (!this.server.areCommandBlocksEnabled()) {
            this.player.sendMessage(new TranslatableText("advMode.notEnabled"));
         } else if (this.player.canUseCommand(2, "") && this.player.abilities.creativeMode) {
            PacketByteBuf var40 = new PacketByteBuf(Unpooled.wrappedBuffer(packet.getData()));

            try {
               byte var45 = var40.readByte();
               CommandExecutor var50 = null;
               if (var45 == 0) {
                  BlockEntity var5 = this.player.world.getBlockEntity(new BlockPos(var40.readInt(), var40.readInt(), var40.readInt()));
                  if (var5 instanceof CommandBlockBlockEntity) {
                     var50 = ((CommandBlockBlockEntity)var5).getCommandExecutor();
                  }
               } else if (var45 == 1) {
                  Entity var54 = this.player.world.getEntity(var40.readInt());
                  if (var54 instanceof CommandBlockMinecartEntity) {
                     var50 = ((CommandBlockMinecartEntity)var54).getCommandExecutor();
                  }
               }

               String var55 = var40.readString(var40.readableBytes());
               boolean var6 = var40.readBoolean();
               if (var50 != null) {
                  var50.setCommand(var55);
                  var50.setTrackOutput(var6);
                  if (!var6) {
                     var50.setLastOutput(null);
                  }

                  var50.markDirty();
                  this.player.sendMessage(new TranslatableText("advMode.setCommand.success", var55));
               }
            } catch (Exception var33) {
               LOGGER.error("Couldn't set command block", var33);
            } finally {
               var40.release();
            }
         } else {
            this.player.sendMessage(new TranslatableText("advMode.notAllowed"));
         }
      } else if ("MC|Beacon".equals(packet.getChannel())) {
         if (this.player.menu instanceof BeaconMenu) {
            try {
               DataInputStream var41 = new DataInputStream(new ByteArrayInputStream(packet.getData()));
               int var46 = var41.readInt();
               int var51 = var41.readInt();
               BeaconMenu var56 = (BeaconMenu)this.player.menu;
               InventorySlot var57 = var56.getSlot(0);
               if (var57.hasStack()) {
                  var57.removeStack(1);
                  Inventory var7 = var56.getBeacon();
                  var7.setData(1, var46);
                  var7.setData(2, var51);
                  var7.markDirty();
               }
            } catch (Exception var32) {
               LOGGER.error("Couldn't set beacon", var32);
            }
         }
      } else if ("MC|ItemName".equals(packet.getChannel()) && this.player.menu instanceof AnvilMenu) {
         AnvilMenu var42 = (AnvilMenu)this.player.menu;
         if (packet.getData() != null && packet.getData().length >= 1) {
            String var47 = SharedConstants.stripInvalidChars(new String(packet.getData(), Charsets.UTF_8));
            if (var47.length() <= 30) {
               var42.setItemName(var47);
            }
         } else {
            var42.setItemName("");
         }
      }
   }
}
