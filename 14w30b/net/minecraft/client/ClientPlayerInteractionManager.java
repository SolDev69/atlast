package net.minecraft.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.client.sound.event.SimpleSoundEvent;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.CreativeMenuSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.MenuClickButtonC2SPacket;
import net.minecraft.network.packet.c2s.play.MenuClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerHandActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerUseItemC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectSlotC2SPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientPlayerInteractionManager {
   private final MinecraftClient client;
   private final ClientPlayNetworkHandler networkHandler;
   private BlockPos target = new BlockPos(-1, -1, -1);
   private ItemStack miningTool;
   private float miningProgress;
   private float miningSoundTimer;
   private int miningCooldown;
   private boolean isMiningBlock;
   private WorldSettings.GameMode gameMode = WorldSettings.GameMode.SURVIVAL;
   private int selectedHotbarSlot;

   public ClientPlayerInteractionManager(MinecraftClient client, ClientPlayNetworkHandler networkHandler) {
      this.client = client;
      this.networkHandler = networkHandler;
   }

   public static void mineBlockInCreative(MinecraftClient client, ClientPlayerInteractionManager interactionManager, BlockPos pos, Direction face) {
      if (!client.world.extinguishFire(client.player, pos, face)) {
         interactionManager.finishMiningBlock(pos, face);
      }
   }

   public void refreshAbilities(PlayerEntity player) {
      this.gameMode.apply(player.abilities);
   }

   public boolean isInSpectatorMode() {
      return this.gameMode == WorldSettings.GameMode.SPECTATOR;
   }

   public void setGameMode(WorldSettings.GameMode gameMode) {
      this.gameMode = gameMode;
      this.gameMode.apply(this.client.player.abilities);
   }

   public void setFacingSouth(PlayerEntity player) {
      player.yaw = -180.0F;
   }

   public boolean hasStatusBars() {
      return this.gameMode.isSurvival();
   }

   public boolean finishMiningBlock(BlockPos pos, Direction face) {
      if (this.gameMode.restrictsWorldModification()) {
         if (this.gameMode == WorldSettings.GameMode.SPECTATOR) {
            return false;
         }

         if (!this.client.player.canModifyWorld()) {
            Block var3 = this.client.world.getBlockState(pos).getBlock();
            ItemStack var4 = this.client.player.getMainHandStack();
            if (var4 == null) {
               return false;
            }

            if (!var4.hasMineBlockOverride(var3)) {
               return false;
            }
         }
      }

      if (this.gameMode.isCreative() && this.client.player.getStackInHand() != null && this.client.player.getStackInHand().getItem() instanceof SwordItem) {
         return false;
      } else {
         ClientWorld var8 = this.client.world;
         BlockState var9 = var8.getBlockState(pos);
         Block var5 = var9.getBlock();
         if (var5.getMaterial() == Material.AIR) {
            return false;
         } else {
            var8.doEvent(2001, pos, Block.serialize(var9));
            boolean var6 = var8.removeBlock(pos);
            if (var6) {
               var5.onBroken(var8, pos, var9);
            }

            this.target = new BlockPos(this.target.getX(), -1, this.target.getZ());
            if (!this.gameMode.isCreative()) {
               ItemStack var7 = this.client.player.getMainHandStack();
               if (var7 != null) {
                  var7.mineBlock(var8, var5, pos, this.client.player);
                  if (var7.size == 0) {
                     this.client.player.clearSelectedSlot();
                  }
               }
            }

            return var6;
         }
      }
   }

   public boolean startMiningBlock(BlockPos pos, Direction face) {
      if (this.gameMode.restrictsWorldModification()) {
         if (this.gameMode == WorldSettings.GameMode.SPECTATOR) {
            return false;
         }

         if (!this.client.player.canModifyWorld()) {
            Block var3 = this.client.world.getBlockState(pos).getBlock();
            ItemStack var4 = this.client.player.getMainHandStack();
            if (var4 == null) {
               return false;
            }

            if (!var4.hasMineBlockOverride(var3)) {
               return false;
            }
         }
      }

      if (!this.client.world.getWorldBorder().contains(pos)) {
         return false;
      } else {
         if (this.gameMode.isCreative()) {
            this.networkHandler.sendPacket(new PlayerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.START_DESTROY_BLOCK, pos, face));
            mineBlockInCreative(this.client, this, pos, face);
            this.miningCooldown = 5;
         } else if (!this.isMiningBlock || !this.isMiningBlock(pos)) {
            if (this.isMiningBlock) {
               this.networkHandler.sendPacket(new PlayerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.target, face));
            }

            this.networkHandler.sendPacket(new PlayerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.START_DESTROY_BLOCK, pos, face));
            Block var5 = this.client.world.getBlockState(pos).getBlock();
            boolean var6 = var5.getMaterial() != Material.AIR;
            if (var6 && this.miningProgress == 0.0F) {
               var5.startMining(this.client.world, pos, this.client.player);
            }

            if (var6 && var5.getMiningSpeed(this.client.player, this.client.player.world, pos) >= 1.0F) {
               this.finishMiningBlock(pos, face);
            } else {
               this.isMiningBlock = true;
               this.target = pos;
               this.miningTool = this.client.player.getStackInHand();
               this.miningProgress = 0.0F;
               this.miningSoundTimer = 0.0F;
               this.client.world.updateBlockMiningProgress(this.client.player.getNetworkId(), this.target, (int)(this.miningProgress * 10.0F) - 1);
            }
         }

         return true;
      }
   }

   public void stopMiningBlock() {
      if (this.isMiningBlock) {
         this.networkHandler.sendPacket(new PlayerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.target, Direction.DOWN));
         this.isMiningBlock = false;
         this.miningProgress = 0.0F;
         this.client.world.updateBlockMiningProgress(this.client.player.getNetworkId(), this.target, -1);
      }
   }

   public boolean updateBlockMining(BlockPos pos, Direction face) {
      this.updateSelectedHotbarSlot();
      if (this.miningCooldown > 0) {
         --this.miningCooldown;
         return true;
      } else if (this.gameMode.isCreative() && this.client.world.getWorldBorder().contains(pos)) {
         this.miningCooldown = 5;
         this.networkHandler.sendPacket(new PlayerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.START_DESTROY_BLOCK, pos, face));
         mineBlockInCreative(this.client, this, pos, face);
         return true;
      } else if (this.isMiningBlock(pos)) {
         Block var3 = this.client.world.getBlockState(pos).getBlock();
         if (var3.getMaterial() == Material.AIR) {
            this.isMiningBlock = false;
            return false;
         } else {
            this.miningProgress += var3.getMiningSpeed(this.client.player, this.client.player.world, pos);
            if (this.miningSoundTimer % 4.0F == 0.0F) {
               this.client
                  .getSoundManager()
                  .play(
                     new SimpleSoundEvent(
                        new Identifier(var3.sound.getStepSound()),
                        (var3.sound.getVolume() + 1.0F) / 8.0F,
                        var3.sound.getPitch() * 0.5F,
                        (float)pos.getX() + 0.5F,
                        (float)pos.getY() + 0.5F,
                        (float)pos.getZ() + 0.5F
                     )
                  );
            }

            ++this.miningSoundTimer;
            if (this.miningProgress >= 1.0F) {
               this.isMiningBlock = false;
               this.networkHandler.sendPacket(new PlayerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, face));
               this.finishMiningBlock(pos, face);
               this.miningProgress = 0.0F;
               this.miningSoundTimer = 0.0F;
               this.miningCooldown = 5;
            }

            this.client.world.updateBlockMiningProgress(this.client.player.getNetworkId(), this.target, (int)(this.miningProgress * 10.0F) - 1);
            return true;
         }
      } else {
         return this.startMiningBlock(pos, face);
      }
   }

   public float getReach() {
      return this.gameMode.isCreative() ? 5.0F : 4.5F;
   }

   public void tick() {
      this.updateSelectedHotbarSlot();
      if (this.networkHandler.getConnection().isOpen()) {
         this.networkHandler.getConnection().tick();
      } else if (this.networkHandler.getConnection().getDisconnectReason() != null) {
         this.networkHandler.getConnection().getListener().onDisconnect(this.networkHandler.getConnection().getDisconnectReason());
      } else {
         this.networkHandler.getConnection().getListener().onDisconnect(new LiteralText("Disconnected from server"));
      }
   }

   private boolean isMiningBlock(BlockPos pos) {
      ItemStack var2 = this.client.player.getStackInHand();
      boolean var3 = this.miningTool == null && var2 == null;
      if (this.miningTool != null && var2 != null) {
         var3 = var2.getItem() == this.miningTool.getItem()
            && ItemStack.matchesNbt(var2, this.miningTool)
            && (var2.isDamageable() || var2.getMetadata() == this.miningTool.getMetadata());
      }

      return pos.equals(this.target) && var3;
   }

   private void updateSelectedHotbarSlot() {
      int var1 = this.client.player.inventory.selectedSlot;
      if (var1 != this.selectedHotbarSlot) {
         this.selectedHotbarSlot = var1;
         this.networkHandler.sendPacket(new SelectSlotC2SPacket(this.selectedHotbarSlot));
      }
   }

   public boolean interactBlock(LocalClientPlayerEntity player, ClientWorld world, ItemStack stack, BlockPos pos, Direction face, Vec3d hitPos) {
      this.updateSelectedHotbarSlot();
      float var7 = (float)hitPos.x - (float)pos.getX();
      float var8 = (float)hitPos.y - (float)pos.getY();
      float var9 = (float)hitPos.z - (float)pos.getZ();
      boolean var10 = false;
      if (!this.client.world.getWorldBorder().contains(pos)) {
         return false;
      } else {
         if (this.gameMode != WorldSettings.GameMode.SPECTATOR) {
            BlockState var11 = world.getBlockState(pos);
            if ((!player.isSneaking() || player.getStackInHand() == null) && var11.getBlock().use(world, pos, var11, player, face, var7, var8, var9)) {
               var10 = true;
            }

            if (!var10 && stack != null && stack.getItem() instanceof BlockItem) {
               BlockItem var12 = (BlockItem)stack.getItem();
               if (!var12.onPlace(world, pos, face, player, stack)) {
                  return false;
               }
            }
         }

         this.networkHandler.sendPacket(new PlayerUseItemC2SPacket(pos, face.getId(), player.inventory.getMainHandStack(), var7, var8, var9));
         if (var10 || this.gameMode == WorldSettings.GameMode.SPECTATOR) {
            return true;
         } else if (stack == null) {
            return false;
         } else if (this.gameMode.isCreative()) {
            int var14 = stack.getMetadata();
            int var15 = stack.size;
            boolean var13 = stack.use(player, world, pos, face, var7, var8, var9);
            stack.setDamage(var14);
            stack.size = var15;
            return var13;
         } else {
            return stack.use(player, world, pos, face, var7, var8, var9);
         }
      }
   }

   public boolean useItem(PlayerEntity player, World world, ItemStack stack) {
      if (this.gameMode == WorldSettings.GameMode.SPECTATOR) {
         return false;
      } else {
         this.updateSelectedHotbarSlot();
         this.networkHandler.sendPacket(new PlayerUseItemC2SPacket(player.inventory.getMainHandStack()));
         int var4 = stack.size;
         ItemStack var5 = stack.startUsing(world, player);
         if (var5 != stack || var5 != null && var5.size != var4) {
            player.inventory.inventorySlots[player.inventory.selectedSlot] = var5;
            if (var5.size == 0) {
               player.inventory.inventorySlots[player.inventory.selectedSlot] = null;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public LocalClientPlayerEntity createPlayerEntity(World world, StatHandler statHandler) {
      return new LocalClientPlayerEntity(this.client, world, this.networkHandler, statHandler);
   }

   public void attackEntity(PlayerEntity player, Entity target) {
      this.updateSelectedHotbarSlot();
      this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(target, PlayerInteractEntityC2SPacket.Action.ATTACK));
      if (this.gameMode != WorldSettings.GameMode.SPECTATOR) {
         player.attack(target);
      }
   }

   public boolean interactEntity(PlayerEntity player, Entity target) {
      this.updateSelectedHotbarSlot();
      this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(target, PlayerInteractEntityC2SPacket.Action.INTERACT));
      return this.gameMode != WorldSettings.GameMode.SPECTATOR && player.interact(target);
   }

   public ItemStack clickSlot(int syncId, int slotId, int clickData, int actionType, PlayerEntity player) {
      short var6 = player.menu.getNextActionNetworkId(player.inventory);
      ItemStack var7 = player.menu.onClickSlot(slotId, clickData, actionType, player);
      this.networkHandler.sendPacket(new MenuClickSlotC2SPacket(syncId, slotId, clickData, actionType, var7, var6));
      return var7;
   }

   public void clickMenuButton(int menuId, int buttonId) {
      this.networkHandler.sendPacket(new MenuClickButtonC2SPacket(menuId, buttonId));
   }

   public void addStackToCreativeMenu(ItemStack stack, int slotId) {
      if (this.gameMode.isCreative()) {
         this.networkHandler.sendPacket(new CreativeMenuSlotC2SPacket(slotId, stack));
      }
   }

   public void dropStackFromCreativeMenu(ItemStack stack) {
      if (this.gameMode.isCreative() && stack != null) {
         this.networkHandler.sendPacket(new CreativeMenuSlotC2SPacket(-1, stack));
      }
   }

   public void stopUsingHand(PlayerEntity player) {
      this.updateSelectedHotbarSlot();
      this.networkHandler.sendPacket(new PlayerHandActionC2SPacket(PlayerHandActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
      player.stopUsingHand();
   }

   public boolean hasXpBar() {
      return this.gameMode.isSurvival();
   }

   public boolean hasAttackCooldown() {
      return !this.gameMode.isCreative();
   }

   public boolean hasCreativeInventory() {
      return this.gameMode.isCreative();
   }

   public boolean hasExtendedReach() {
      return this.gameMode.isCreative();
   }

   public boolean hasRidingInventory() {
      return this.client.player.hasVehicle() && this.client.player.vehicle instanceof HorseBaseEntity;
   }

   public boolean isSpectator() {
      return this.gameMode == WorldSettings.GameMode.SPECTATOR;
   }

   public WorldSettings.GameMode getGameMode() {
      return this.gameMode;
   }
}
