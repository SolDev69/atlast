package net.minecraft.client.entity.living.player;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.particle.EntityPickupParticle;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.inventory.BookEditScreen;
import net.minecraft.client.gui.screen.inventory.menu.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.menu.BeaconScreen;
import net.minecraft.client.gui.screen.inventory.menu.BrewingStandScreen;
import net.minecraft.client.gui.screen.inventory.menu.ChestScreen;
import net.minecraft.client.gui.screen.inventory.menu.CraftingTableScreen;
import net.minecraft.client.gui.screen.inventory.menu.DispenserScreen;
import net.minecraft.client.gui.screen.inventory.menu.EnchantingTableScreen;
import net.minecraft.client.gui.screen.inventory.menu.FurnaceScreen;
import net.minecraft.client.gui.screen.inventory.menu.HopperScreen;
import net.minecraft.client.gui.screen.inventory.menu.HorseScreen;
import net.minecraft.client.gui.screen.inventory.menu.SignEditScreen;
import net.minecraft.client.gui.screen.inventory.menu.VillagerScreen;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.client.player.input.PlayerInput;
import net.minecraft.client.sound.event.MinecartWithPlayerSoundEvent;
import net.minecraft.client.sound.event.SimpleSoundEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.mob.passive.Trader;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.MenuProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseMenuC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerHandActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMovementActionC2SPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.server.command.source.CommandExecutor;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LocalClientPlayerEntity extends ClientPlayerEntity {
   public final ClientPlayNetworkHandler networkHandler;
   private final StatHandler statHandler;
   private double lastX;
   private double lastY;
   private double lastZ;
   private float lastYaw;
   private float lastPitch;
   private boolean wasSneaking;
   private boolean wasSprinting;
   private int movementUpdateTimer;
   private boolean healthInitialized;
   private String serverBrand;
   public PlayerInput input;
   protected MinecraftClient client;
   protected int doubleTapSprintTimer;
   public int sprintingCooldown;
   public float f_49gybzpgn;
   public float f_40xrfnzqh;
   public float f_68idpvjry;
   public float f_45fovxhju;
   private int horseJumpTimer;
   private float horseJumpSize;
   public float netherPortalDuration;
   public float oldNetherPortalDuration;

   public LocalClientPlayerEntity(MinecraftClient client, World world, ClientPlayNetworkHandler session, StatHandler networkHandler) {
      super(world, session.getProfile());
      this.networkHandler = session;
      this.statHandler = networkHandler;
      this.client = client;
      this.dimensionId = 0;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      return false;
   }

   @Override
   public void heal(float amount) {
   }

   @Override
   public void startRiding(Entity entity) {
      super.startRiding(entity);
      if (entity instanceof MinecartEntity) {
         this.client.getSoundManager().play(new MinecartWithPlayerSoundEvent(this, (MinecartEntity)entity));
      }
   }

   @Override
   public void tick() {
      if (this.world.isLoaded(new BlockPos(this.x, 0.0, this.z))) {
         super.tick();
         if (this.hasVehicle()) {
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Angles(this.yaw, this.pitch, this.onGround));
            this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.sidewaysSpeed, this.forwardSpeed, this.input.jumping, this.input.sneaking));
         } else {
            this.updateMovement();
         }
      }
   }

   public void updateMovement() {
      boolean var1 = this.isSprinting();
      if (var1 != this.wasSprinting) {
         if (var1) {
            this.networkHandler.sendPacket(new PlayerMovementActionC2SPacket(this, PlayerMovementActionC2SPacket.Action.START_SPRINTING));
         } else {
            this.networkHandler.sendPacket(new PlayerMovementActionC2SPacket(this, PlayerMovementActionC2SPacket.Action.STOP_SPRINTING));
         }

         this.wasSprinting = var1;
      }

      boolean var2 = this.isSneaking();
      if (var2 != this.wasSneaking) {
         if (var2) {
            this.networkHandler.sendPacket(new PlayerMovementActionC2SPacket(this, PlayerMovementActionC2SPacket.Action.START_SNEAKING));
         } else {
            this.networkHandler.sendPacket(new PlayerMovementActionC2SPacket(this, PlayerMovementActionC2SPacket.Action.STOP_SNEAKING));
         }

         this.wasSneaking = var2;
      }

      if (this.m_06ybmoruq()) {
         double var3 = this.x - this.lastX;
         double var5 = this.getBoundingBox().minY - this.lastY;
         double var7 = this.z - this.lastZ;
         double var9 = (double)(this.yaw - this.lastYaw);
         double var11 = (double)(this.pitch - this.lastPitch);
         boolean var13 = var3 * var3 + var5 * var5 + var7 * var7 > 9.0E-4 || this.movementUpdateTimer >= 20;
         boolean var14 = var9 != 0.0 || var11 != 0.0;
         if (this.vehicle == null) {
            if (var13 && var14) {
               this.networkHandler
                  .sendPacket(new PlayerMoveC2SPacket.PositionAndAngles(this.x, this.getBoundingBox().minY, this.z, this.yaw, this.pitch, this.onGround));
            } else if (var13) {
               this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Position(this.x, this.getBoundingBox().minY, this.z, this.onGround));
            } else if (var14) {
               this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Angles(this.yaw, this.pitch, this.onGround));
            } else {
               this.networkHandler.sendPacket(new PlayerMoveC2SPacket(this.onGround));
            }
         } else {
            this.networkHandler
               .sendPacket(new PlayerMoveC2SPacket.PositionAndAngles(this.velocityX, -999.0, this.velocityZ, this.yaw, this.pitch, this.onGround));
            var13 = false;
         }

         ++this.movementUpdateTimer;
         if (var13) {
            this.lastX = this.x;
            this.lastY = this.getBoundingBox().minY;
            this.lastZ = this.z;
            this.movementUpdateTimer = 0;
         }

         if (var14) {
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;
         }
      }
   }

   @Override
   public ItemEntity dropItem(boolean wholeStack) {
      PlayerHandActionC2SPacket.Action var2 = wholeStack ? PlayerHandActionC2SPacket.Action.DROP_ALL_ITEMS : PlayerHandActionC2SPacket.Action.DROP_ITEM;
      this.networkHandler.sendPacket(new PlayerHandActionC2SPacket(var2, BlockPos.ORIGIN, Direction.DOWN));
      return null;
   }

   @Override
   protected void spawnDroppedItem(ItemEntity item) {
   }

   public void sendChat(String message) {
      this.networkHandler.sendPacket(new ChatMessageC2SPacket(message));
   }

   @Override
   public void swingHand() {
      super.swingHand();
      this.networkHandler.sendPacket(new HandSwingC2SPacket());
   }

   @Override
   public void tryRespawn() {
      this.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Status.PERFORM_RESPAWN));
   }

   @Override
   protected void applyDamage(DamageSource source, float damage) {
      if (!this.isInvulnerable(source)) {
         this.setHealth(this.getHealth() - damage);
      }
   }

   @Override
   public void closeMenu() {
      this.networkHandler.sendPacket(new CloseMenuC2SPacket(this.menu.networkId));
      this.doCloseMenu();
   }

   public void doCloseMenu() {
      this.inventory.setCursorStack(null);
      super.closeMenu();
      this.client.openScreen(null);
   }

   public void setPlayerHealth(float health) {
      if (this.healthInitialized) {
         float var2 = this.getHealth() - health;
         if (var2 <= 0.0F) {
            this.setHealth(health);
            if (var2 < 0.0F) {
               this.maxHealth = this.defaultMaxHealth / 2;
            }
         } else {
            this.damageAmount = var2;
            this.setHealth(this.getHealth());
            this.maxHealth = this.defaultMaxHealth;
            this.applyDamage(DamageSource.GENERIC, var2);
            this.hurtTimer = this.hurtAnimationTicks = 10;
         }
      } else {
         this.setHealth(health);
         this.healthInitialized = true;
      }
   }

   @Override
   public void incrementStat(Stat stat, int amount) {
      if (stat != null) {
         if (stat.local) {
            super.incrementStat(stat, amount);
         }
      }
   }

   @Override
   public void syncAbilities() {
      this.networkHandler.sendPacket(new PlayerAbilitiesC2SPacket(this.abilities));
   }

   @Override
   public boolean m_08txklcju() {
      return true;
   }

   protected void m_98npsrcgi() {
      this.networkHandler
         .sendPacket(new PlayerMovementActionC2SPacket(this, PlayerMovementActionC2SPacket.Action.RIDING_JUMP, (int)(this.getRidingJumpProgress() * 100.0F)));
   }

   public void openRidingInventory() {
      this.networkHandler.sendPacket(new PlayerMovementActionC2SPacket(this, PlayerMovementActionC2SPacket.Action.OPEN_HORSE_INVENTORY));
   }

   public void setServerBrand(String serverBrand) {
      this.serverBrand = serverBrand;
   }

   public String getServerBrand() {
      return this.serverBrand;
   }

   public StatHandler getStatHandler() {
      return this.statHandler;
   }

   @Override
   public void addMessage(Text message) {
      this.client.gui.getChat().addMessage(message);
   }

   @Override
   protected boolean pushAwayFrom(double x, double y, double z) {
      if (this.noClip) {
         return false;
      } else {
         BlockPos var7 = new BlockPos(x, y, z);
         double var8 = x - (double)var7.getX();
         double var10 = z - (double)var7.getZ();
         if (!this.isBlockAtPosFullCube(var7)) {
            byte var12 = -1;
            double var13 = 9999.0;
            if (this.isBlockAtPosFullCube(var7.west()) && var8 < var13) {
               var13 = var8;
               var12 = 0;
            }

            if (this.isBlockAtPosFullCube(var7.east()) && 1.0 - var8 < var13) {
               var13 = 1.0 - var8;
               var12 = 1;
            }

            if (this.isBlockAtPosFullCube(var7.north()) && var10 < var13) {
               var13 = var10;
               var12 = 4;
            }

            if (this.isBlockAtPosFullCube(var7.south()) && 1.0 - var10 < var13) {
               var13 = 1.0 - var10;
               var12 = 5;
            }

            float var15 = 0.1F;
            if (var12 == 0) {
               this.velocityX = (double)(-var15);
            }

            if (var12 == 1) {
               this.velocityX = (double)var15;
            }

            if (var12 == 4) {
               this.velocityZ = (double)(-var15);
            }

            if (var12 == 5) {
               this.velocityZ = (double)var15;
            }
         }

         return false;
      }
   }

   private boolean isBlockAtPosFullCube(BlockPos c_76varpwca) {
      return !this.world.getBlockState(c_76varpwca).getBlock().isConductor() && !this.world.getBlockState(c_76varpwca.up()).getBlock().isConductor();
   }

   @Override
   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      this.sprintingCooldown = sprinting ? 600 : 0;
   }

   public void setXp(float xpProgress, int xp, int xpLevel) {
      this.xpProgress = xpProgress;
      this.xp = xp;
      this.xpLevel = xpLevel;
   }

   @Override
   public void sendMessage(Text message) {
      this.client.gui.getChat().addMessage(message);
   }

   @Override
   public boolean canUseCommand(int permissionLevel, String command) {
      return permissionLevel <= 0;
   }

   @Override
   public BlockPos getSourceBlockPos() {
      return new BlockPos(this.x + 0.5, this.y + 0.5, this.z + 0.5);
   }

   @Override
   public void playSound(String id, float volume, float pitch) {
      this.world.playSound(this.x, this.y, this.z, id, volume, pitch, false);
   }

   @Override
   public boolean isServer() {
      return true;
   }

   public boolean isRidingRideableMob() {
      return this.vehicle != null && this.vehicle instanceof HorseBaseEntity && ((HorseBaseEntity)this.vehicle).isSaddled();
   }

   public float getRidingJumpProgress() {
      return this.horseJumpSize;
   }

   @Override
   public void openSignEditor(SignBlockEntity sign) {
      this.client.openScreen(new SignEditScreen(sign));
   }

   @Override
   public void openCommandBlockScreen(CommandExecutor executor) {
      this.client.openScreen(new CommandBlockScreen(executor));
   }

   @Override
   public void openEditBookScreen(ItemStack book) {
      Item var2 = book.getItem();
      if (var2 == Items.WRITABLE_BOOK) {
         this.client.openScreen(new BookEditScreen(this, book, true));
      }
   }

   @Override
   public void openInventoryMenu(Inventory inventory) {
      String var2 = inventory instanceof MenuProvider ? ((MenuProvider)inventory).getMenuType() : "minecraft:container";
      if ("minecraft:chest".equals(var2)) {
         this.client.openScreen(new ChestScreen(this.inventory, inventory));
      } else if ("minecraft:hopper".equals(var2)) {
         this.client.openScreen(new HopperScreen(this.inventory, inventory));
      } else if ("minecraft:furnace".equals(var2)) {
         this.client.openScreen(new FurnaceScreen(this.inventory, inventory));
      } else if ("minecraft:brewing_stand".equals(var2)) {
         this.client.openScreen(new BrewingStandScreen(this.inventory, inventory));
      } else if ("minecraft:beacon".equals(var2)) {
         this.client.openScreen(new BeaconScreen(this.inventory, inventory));
      } else if (!"minecraft:dispenser".equals(var2) && !"minecraft:dropper".equals(var2)) {
         this.client.openScreen(new ChestScreen(this.inventory, inventory));
      } else {
         this.client.openScreen(new DispenserScreen(this.inventory, inventory));
      }
   }

   @Override
   public void openHorseMenu(HorseBaseEntity horse, Inventory inventory) {
      this.client.openScreen(new HorseScreen(this.inventory, inventory, horse));
   }

   @Override
   public void openMenu(MenuProvider menuProvider) {
      String var2 = menuProvider.getMenuType();
      if ("minecraft:crafting_table".equals(var2)) {
         this.client.openScreen(new CraftingTableScreen(this.inventory, this.world));
      } else if ("minecraft:enchanting_table".equals(var2)) {
         this.client.openScreen(new EnchantingTableScreen(this.inventory, this.world, menuProvider));
      } else if ("minecraft:anvil".equals(var2)) {
         this.client.openScreen(new AnvilScreen(this.inventory, this.world));
      }
   }

   @Override
   public void openTraderMenu(Trader trader) {
      this.client.openScreen(new VillagerScreen(this.inventory, trader, this.world));
   }

   @Override
   public void addCritParticles(Entity entity) {
      this.client.particleManager.addEmitter(entity, ParticleType.CRIT);
   }

   @Override
   public void addEnchantedCritParticles(Entity entity) {
      this.client.particleManager.addEmitter(entity, ParticleType.CRIT_MAGIC);
   }

   @Override
   public void sendPickup(Entity entity, int count) {
      this.client.particleManager.addParticle(new EntityPickupParticle(this.client.world, entity, this, -0.5F));
   }

   @Override
   public boolean isSneaking() {
      return this.input.sneaking && !this.sleeping;
   }

   @Override
   public void tickAISetup() {
      super.tickAISetup();
      if (this.m_06ybmoruq()) {
         this.sidewaysSpeed = this.input.movementSideways;
         this.forwardSpeed = this.input.movementForward;
         this.jumping = this.input.jumping;
         this.f_68idpvjry = this.f_49gybzpgn;
         this.f_45fovxhju = this.f_40xrfnzqh;
         this.f_40xrfnzqh = (float)((double)this.f_40xrfnzqh + (double)(this.pitch - this.f_40xrfnzqh) * 0.5);
         this.f_49gybzpgn = (float)((double)this.f_49gybzpgn + (double)(this.yaw - this.f_49gybzpgn) * 0.5);
      }
   }

   protected boolean m_06ybmoruq() {
      return this.client.getCamera() == this;
   }

   @Override
   public void tickAI() {
      if (this.sprintingCooldown > 0) {
         --this.sprintingCooldown;
         if (this.sprintingCooldown == 0) {
            this.setSprinting(false);
         }
      }

      if (this.doubleTapSprintTimer > 0) {
         --this.doubleTapSprintTimer;
      }

      this.oldNetherPortalDuration = this.netherPortalDuration;
      if (this.changingDimension) {
         if (this.client.currentScreen != null) {
            this.client.openScreen(null);
         }

         if (this.netherPortalDuration == 0.0F) {
            this.client.getSoundManager().play(SimpleSoundEvent.of(new Identifier("portal.trigger"), this.random.nextFloat() * 0.4F + 0.8F));
         }

         this.netherPortalDuration += 0.0125F;
         if (this.netherPortalDuration >= 1.0F) {
            this.netherPortalDuration = 1.0F;
         }

         this.changingDimension = false;
      } else if (this.hasStatusEffect(StatusEffect.NAUSEA) && this.getEffectInstance(StatusEffect.NAUSEA).getDuration() > 60) {
         this.netherPortalDuration += 0.006666667F;
         if (this.netherPortalDuration > 1.0F) {
            this.netherPortalDuration = 1.0F;
         }
      } else {
         if (this.netherPortalDuration > 0.0F) {
            this.netherPortalDuration -= 0.05F;
         }

         if (this.netherPortalDuration < 0.0F) {
            this.netherPortalDuration = 0.0F;
         }
      }

      if (this.netherPortalCooldown > 0) {
         --this.netherPortalCooldown;
      }

      boolean var1 = this.input.jumping;
      boolean var2 = this.input.sneaking;
      float var3 = 0.8F;
      boolean var4 = this.input.movementForward >= var3;
      this.input.tick();
      if (this.isHoldingItem() && !this.hasVehicle()) {
         this.input.movementSideways *= 0.2F;
         this.input.movementForward *= 0.2F;
         this.doubleTapSprintTimer = 0;
      }

      this.pushAwayFrom(this.x - (double)this.width * 0.35, this.getBoundingBox().minY + 0.5, this.z + (double)this.width * 0.35);
      this.pushAwayFrom(this.x - (double)this.width * 0.35, this.getBoundingBox().minY + 0.5, this.z - (double)this.width * 0.35);
      this.pushAwayFrom(this.x + (double)this.width * 0.35, this.getBoundingBox().minY + 0.5, this.z - (double)this.width * 0.35);
      this.pushAwayFrom(this.x + (double)this.width * 0.35, this.getBoundingBox().minY + 0.5, this.z + (double)this.width * 0.35);
      boolean var5 = (float)this.getHungerManager().getFoodLevel() > 6.0F || this.abilities.canFly;
      if (this.onGround
         && !var2
         && !var4
         && this.input.movementForward >= var3
         && !this.isSprinting()
         && var5
         && !this.isHoldingItem()
         && !this.hasStatusEffect(StatusEffect.BLINDNESS)) {
         if (this.doubleTapSprintTimer <= 0 && !this.client.options.sprintKey.isPressed()) {
            this.doubleTapSprintTimer = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting()
         && this.input.movementForward >= var3
         && var5
         && !this.isHoldingItem()
         && !this.hasStatusEffect(StatusEffect.BLINDNESS)
         && this.client.options.sprintKey.isPressed()) {
         this.setSprinting(true);
      }

      if (this.isSprinting() && (this.input.movementForward < var3 || this.collidingHorizontally || !var5)) {
         this.setSprinting(false);
      }

      if (this.abilities.canFly) {
         if (this.client.interactionManager.isSpectator()) {
            if (!this.abilities.flying) {
               this.abilities.flying = true;
               this.syncAbilities();
            }
         } else if (!var1 && this.input.jumping) {
            if (this.pressedJumpTwiceTimer == 0) {
               this.pressedJumpTwiceTimer = 7;
            } else {
               this.abilities.flying = !this.abilities.flying;
               this.syncAbilities();
               this.pressedJumpTwiceTimer = 0;
            }
         }
      }

      if (this.abilities.flying && this.m_06ybmoruq()) {
         if (this.input.sneaking) {
            this.velocityY -= (double)(this.abilities.getFlySpeed() * 3.0F);
         }

         if (this.input.jumping) {
            this.velocityY += (double)(this.abilities.getFlySpeed() * 3.0F);
         }
      }

      if (this.isRidingRideableMob()) {
         if (this.horseJumpTimer < 0) {
            ++this.horseJumpTimer;
            if (this.horseJumpTimer == 0) {
               this.horseJumpSize = 0.0F;
            }
         }

         if (var1 && !this.input.jumping) {
            this.horseJumpTimer = -10;
            this.m_98npsrcgi();
         } else if (!var1 && this.input.jumping) {
            this.horseJumpTimer = 0;
            this.horseJumpSize = 0.0F;
         } else if (var1) {
            ++this.horseJumpTimer;
            if (this.horseJumpTimer < 10) {
               this.horseJumpSize = (float)this.horseJumpTimer * 0.1F;
            } else {
               this.horseJumpSize = 0.8F + 2.0F / (float)(this.horseJumpTimer - 9) * 0.1F;
            }
         }
      } else {
         this.horseJumpSize = 0.0F;
      }

      super.tickAI();
      if (this.onGround && this.abilities.flying && !this.client.interactionManager.isSpectator()) {
         this.abilities.flying = false;
         this.syncAbilities();
      }
   }
}
