package net.minecraft.server;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.LockableMenuProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerInfoS2CPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class ServerPlayerInteractionManager {
   public World world;
   public ServerPlayerEntity player;
   private WorldSettings.GameMode gameMode = WorldSettings.GameMode.NOT_SET;
   private boolean isMiningBlock;
   private int miningStartTime;
   private BlockPos target = BlockPos.ORIGIN;
   private int ticks;
   private boolean wasMiningBlock;
   private BlockPos prevTarget = BlockPos.ORIGIN;
   private int prevMiningStartTime;
   private int prevMiningProgress = -1;

   public ServerPlayerInteractionManager(World world) {
      this.world = world;
   }

   public void setGameMode(WorldSettings.GameMode gameMode) {
      this.gameMode = gameMode;
      gameMode.apply(this.player.abilities);
      this.player.syncAbilities();
      this.player.server.getPlayerManager().sendToAll(new PlayerInfoS2CPacket(PlayerInfoS2CPacket.Action.UPDATE_GAME_MODE, this.player));
   }

   public WorldSettings.GameMode getGameMode() {
      return this.gameMode;
   }

   public boolean isSurvival() {
      return this.gameMode.isSurvival();
   }

   public boolean isCreative() {
      return this.gameMode.isCreative();
   }

   public void setGameModeIfNotSet(WorldSettings.GameMode gameMode) {
      if (this.gameMode == WorldSettings.GameMode.NOT_SET) {
         this.gameMode = gameMode;
      }

      this.setGameMode(this.gameMode);
   }

   public void tick() {
      ++this.ticks;
      if (this.wasMiningBlock) {
         int var1 = this.ticks - this.prevMiningStartTime;
         Block var2 = this.world.getBlockState(this.prevTarget).getBlock();
         if (var2.getMaterial() == Material.AIR) {
            this.wasMiningBlock = false;
         } else {
            float var3 = var2.getMiningSpeed(this.player, this.player.world, this.prevTarget) * (float)(var1 + 1);
            int var4 = (int)(var3 * 10.0F);
            if (var4 != this.prevMiningProgress) {
               this.world.updateBlockMiningProgress(this.player.getNetworkId(), this.prevTarget, var4);
               this.prevMiningProgress = var4;
            }

            if (var3 >= 1.0F) {
               this.wasMiningBlock = false;
               this.tryMineBlock(this.prevTarget);
            }
         }
      } else if (this.isMiningBlock) {
         Block var5 = this.world.getBlockState(this.target).getBlock();
         if (var5.getMaterial() == Material.AIR) {
            this.world.updateBlockMiningProgress(this.player.getNetworkId(), this.target, -1);
            this.prevMiningProgress = -1;
            this.isMiningBlock = false;
         } else {
            int var6 = this.ticks - this.miningStartTime;
            float var7 = var5.getMiningSpeed(this.player, this.player.world, this.prevTarget) * (float)(var6 + 1);
            int var8 = (int)(var7 * 10.0F);
            if (var8 != this.prevMiningProgress) {
               this.world.updateBlockMiningProgress(this.player.getNetworkId(), this.target, var8);
               this.prevMiningProgress = var8;
            }
         }
      }
   }

   public void startMiningBlock(BlockPos pos, Direction face) {
      if (this.isCreative()) {
         if (!this.world.extinguishFire(null, pos, face)) {
            this.tryMineBlock(pos);
         }
      } else {
         Block var3 = this.world.getBlockState(pos).getBlock();
         if (this.gameMode.restrictsWorldModification()) {
            if (this.gameMode == WorldSettings.GameMode.SPECTATOR) {
               return;
            }

            if (!this.player.canModifyWorld()) {
               ItemStack var4 = this.player.getMainHandStack();
               if (var4 == null) {
                  return;
               }

               if (!var4.hasMineBlockOverride(var3)) {
                  return;
               }
            }
         }

         this.world.extinguishFire(null, pos, face);
         this.miningStartTime = this.ticks;
         float var6 = 1.0F;
         if (var3.getMaterial() != Material.AIR) {
            var3.startMining(this.world, pos, this.player);
            var6 = var3.getMiningSpeed(this.player, this.player.world, pos);
         }

         if (var3.getMaterial() != Material.AIR && var6 >= 1.0F) {
            this.tryMineBlock(pos);
         } else {
            this.isMiningBlock = true;
            this.target = pos;
            int var5 = (int)(var6 * 10.0F);
            this.world.updateBlockMiningProgress(this.player.getNetworkId(), pos, var5);
            this.prevMiningProgress = var5;
         }
      }
   }

   public void finishMiningBlock(BlockPos pos) {
      if (pos.equals(this.target)) {
         int var2 = this.ticks - this.miningStartTime;
         Block var3 = this.world.getBlockState(pos).getBlock();
         if (var3.getMaterial() != Material.AIR) {
            float var4 = var3.getMiningSpeed(this.player, this.player.world, pos) * (float)(var2 + 1);
            if (var4 >= 0.7F) {
               this.isMiningBlock = false;
               this.world.updateBlockMiningProgress(this.player.getNetworkId(), pos, -1);
               this.tryMineBlock(pos);
            } else if (!this.wasMiningBlock) {
               this.isMiningBlock = false;
               this.wasMiningBlock = true;
               this.prevTarget = pos;
               this.prevMiningStartTime = this.miningStartTime;
            }
         }
      }
   }

   public void stopMiningBlock() {
      this.isMiningBlock = false;
      this.world.updateBlockMiningProgress(this.player.getNetworkId(), this.target, -1);
   }

   private boolean mineBlock(BlockPos pos) {
      BlockState var2 = this.world.getBlockState(pos);
      var2.getBlock().beforeMinedByPlayer(this.world, pos, var2, this.player);
      boolean var3 = this.world.removeBlock(pos);
      if (var3) {
         var2.getBlock().onBroken(this.world, pos, var2);
      }

      return var3;
   }

   public boolean tryMineBlock(BlockPos pos) {
      if (this.gameMode.isCreative() && this.player.getStackInHand() != null && this.player.getStackInHand().getItem() instanceof SwordItem) {
         return false;
      } else {
         BlockState var2 = this.world.getBlockState(pos);
         BlockEntity var3 = this.world.getBlockEntity(pos);
         if (this.gameMode.restrictsWorldModification()) {
            if (this.gameMode == WorldSettings.GameMode.SPECTATOR) {
               return false;
            }

            if (!this.player.canModifyWorld()) {
               ItemStack var4 = this.player.getMainHandStack();
               if (var4 == null) {
                  return false;
               }

               if (!var4.hasMineBlockOverride(var2.getBlock())) {
                  return false;
               }
            }
         }

         this.world.doEvent(this.player, 2001, pos, Block.serialize(var2));
         boolean var7 = this.mineBlock(pos);
         if (this.isCreative()) {
            this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.world, pos));
         } else {
            ItemStack var5 = this.player.getMainHandStack();
            boolean var6 = this.player.canBreakBlock(var2.getBlock());
            if (var5 != null) {
               var5.mineBlock(this.world, var2.getBlock(), pos, this.player);
               if (var5.size == 0) {
                  this.player.clearSelectedSlot();
               }
            }

            if (var7 && var6) {
               var2.getBlock().afterMinedByPlayer(this.world, this.player, pos, var2, var3);
            }
         }

         return var7;
      }
   }

   public boolean useItem(PlayerEntity player, World world, ItemStack stack) {
      if (this.gameMode == WorldSettings.GameMode.SPECTATOR) {
         return false;
      } else {
         int var4 = stack.size;
         int var5 = stack.getMetadata();
         ItemStack var6 = stack.startUsing(world, player);
         if (var6 != stack || var6 != null && (var6.size != var4 || var6.getUseDuration() > 0 || var6.getMetadata() != var5)) {
            player.inventory.inventorySlots[player.inventory.selectedSlot] = var6;
            if (this.isCreative()) {
               var6.size = var4;
               if (var6.isDamageable()) {
                  var6.setDamage(var5);
               }
            }

            if (var6.size == 0) {
               player.inventory.inventorySlots[player.inventory.selectedSlot] = null;
            }

            if (!player.isHoldingItem()) {
               ((ServerPlayerEntity)player).setMenu(player.playerMenu);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean interactBlock(PlayerEntity player, World world, ItemStack stack, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (this.gameMode == WorldSettings.GameMode.SPECTATOR) {
         BlockEntity var13 = world.getBlockEntity(pos);
         if (var13 instanceof LockableMenuProvider) {
            Block var14 = world.getBlockState(pos).getBlock();
            LockableMenuProvider var15 = (LockableMenuProvider)var13;
            if (var15 instanceof ChestBlockEntity && var14 instanceof ChestBlock) {
               var15 = ((ChestBlock)var14).getInventory(world, pos);
            }

            if (var15 != null) {
               player.openInventoryMenu(var15);
               return true;
            }
         } else if (var13 instanceof Inventory) {
            player.openInventoryMenu((Inventory)var13);
            return true;
         }

         return false;
      } else {
         if (!player.isSneaking() || player.getStackInHand() == null) {
            BlockState var9 = world.getBlockState(pos);
            if (var9.getBlock().use(world, pos, var9, player, face, dx, dy, dz)) {
               return true;
            }
         }

         if (stack == null) {
            return false;
         } else if (this.isCreative()) {
            int var12 = stack.getMetadata();
            int var10 = stack.size;
            boolean var11 = stack.use(player, world, pos, face, dx, dy, dz);
            stack.setDamage(var12);
            stack.size = var10;
            return var11;
         } else {
            return stack.use(player, world, pos, face, dx, dy, dz);
         }
      }
   }

   public void setWorld(ServerWorld world) {
      this.world = world;
   }
}
