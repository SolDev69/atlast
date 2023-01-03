package net.minecraft.server;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameEventS2CPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DemoServerPlayerInteractionManager extends ServerPlayerInteractionManager {
   private boolean sentHelp;
   private boolean demoEnded;
   private int reminderTicks;
   private int ticks;

   public DemoServerPlayerInteractionManager(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   @Override
   public void tick() {
      super.tick();
      ++this.ticks;
      long var1 = this.world.getTime();
      long var3 = var1 / 24000L + 1L;
      if (!this.sentHelp && this.ticks > 20) {
         this.sentHelp = true;
         this.player.networkHandler.sendPacket(new GameEventS2CPacket(5, 0.0F));
      }

      this.demoEnded = var1 > 120500L;
      if (this.demoEnded) {
         ++this.reminderTicks;
      }

      if (var1 % 24000L == 500L) {
         if (var3 <= 6L) {
            this.player.sendMessage(new TranslatableText("demo.day." + var3));
         }
      } else if (var3 == 1L) {
         if (var1 == 100L) {
            this.player.networkHandler.sendPacket(new GameEventS2CPacket(5, 101.0F));
         } else if (var1 == 175L) {
            this.player.networkHandler.sendPacket(new GameEventS2CPacket(5, 102.0F));
         } else if (var1 == 250L) {
            this.player.networkHandler.sendPacket(new GameEventS2CPacket(5, 103.0F));
         }
      } else if (var3 == 5L && var1 % 24000L == 22000L) {
         this.player.sendMessage(new TranslatableText("demo.day.warning"));
      }
   }

   private void sendDemoReminder() {
      if (this.reminderTicks > 100) {
         this.player.sendMessage(new TranslatableText("demo.reminder"));
         this.reminderTicks = 0;
      }
   }

   @Override
   public void startMiningBlock(BlockPos pos, Direction face) {
      if (this.demoEnded) {
         this.sendDemoReminder();
      } else {
         super.startMiningBlock(pos, face);
      }
   }

   @Override
   public void finishMiningBlock(BlockPos pos) {
      if (!this.demoEnded) {
         super.finishMiningBlock(pos);
      }
   }

   @Override
   public boolean tryMineBlock(BlockPos pos) {
      return this.demoEnded ? false : super.tryMineBlock(pos);
   }

   @Override
   public boolean useItem(PlayerEntity player, World world, ItemStack stack) {
      if (this.demoEnded) {
         this.sendDemoReminder();
         return false;
      } else {
         return super.useItem(player, world, stack);
      }
   }

   @Override
   public boolean interactBlock(PlayerEntity player, World world, ItemStack stack, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (this.demoEnded) {
         this.sendDemoReminder();
         return false;
      } else {
         return super.interactBlock(player, world, stack, pos, face, dx, dy, dz);
      }
   }
}
