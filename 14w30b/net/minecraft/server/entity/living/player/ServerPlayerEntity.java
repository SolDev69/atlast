package net.minecraft.server.entity.living.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.passive.Trader;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.ChestMenu;
import net.minecraft.inventory.menu.HorseMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.InventoryMenuListener;
import net.minecraft.inventory.menu.LockableMenuProvider;
import net.minecraft.inventory.menu.MenuProvider;
import net.minecraft.inventory.menu.TraderMenu;
import net.minecraft.inventory.slot.CraftingResultSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.s2c.play.CameraS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseMenuS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEventS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityRemoveStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameEventS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryMenuS2CPacket;
import net.minecraft.network.packet.s2c.play.MenuDataS2CPacket;
import net.minecraft.network.packet.s2c.play.MenuSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenMenuS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenSignEditorS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerHealthS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSleepS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerXpS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.SoundEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldChunksS2CPacket;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OpEntry;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ForwardingJsonSet;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.village.trade.TradeOffers;
import net.minecraft.world.village.trade.TraderInventory;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements InventoryMenuListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private String lang = "en_US";
   public ServerPlayNetworkHandler networkHandler;
   public final MinecraftServer server;
   public final ServerPlayerInteractionManager interactionManager;
   public double trackedX;
   public double trackedZ;
   public final List newLoadedChunks = Lists.newLinkedList();
   private final List removedEntities = Lists.newLinkedList();
   private final ServerStatHandler statHandler;
   private float completeHealth = Float.MIN_VALUE;
   private float lastHealth = -1.0E8F;
   private int lastHungerLevel = -99999999;
   private boolean wasHungry = true;
   private int lastXp = -99999999;
   private int spawnProtectionTicks = 60;
   private PlayerEntity.ChatVisibility chatVisibility;
   private boolean chatColors = true;
   private long lastActionTime = System.currentTimeMillis();
   private Entity camera = null;
   private int screenHandlerSyncId;
   public boolean useItemCooldown;
   public int ping;
   public boolean leavingTheEnd;

   public ServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager) {
      super(world, profile);
      interactionManager.player = this;
      this.interactionManager = interactionManager;
      BlockPos var5 = world.getSpawnPoint();
      if (!world.dimension.isDark() && world.getData().getDefaultGamemode() != WorldSettings.GameMode.ADVENTURE) {
         int var6 = Math.max(5, server.getSpawnProtectionRadius() - 6);
         int var7 = MathHelper.floor(world.getWorldBorder().getDistanceFrom((double)var5.getX(), (double)var5.getZ()));
         if (var7 < var6) {
            var6 = var7;
         }

         if (var7 <= 1) {
            var6 = 1;
         }

         var5 = world.getSurfaceHeight(var5.add(this.random.nextInt(var6 * 2) - var6, 0, this.random.nextInt(var6 * 2) - var6));
      }

      this.server = server;
      this.statHandler = server.getPlayerManager().getStatHandler(this);
      this.stepHeight = 0.0F;
      this.refreshPositionAndAngles(var5, 0.0F, 0.0F);

      while(!world.getCollisions(this, this.getBoundingBox()).isEmpty() && this.y < 255.0) {
         this.setPosition(this.x, this.y + 1.0, this.z);
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      if (nbt.isType("playerGameType", 99)) {
         if (MinecraftServer.getInstance().shouldForceGameMode()) {
            this.interactionManager.setGameMode(MinecraftServer.getInstance().getDefaultGameMode());
         } else {
            this.interactionManager.setGameMode(WorldSettings.GameMode.byIndex(nbt.getInt("playerGameType")));
         }
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("playerGameType", this.interactionManager.getGameMode().getIndex());
   }

   @Override
   public void addXp(int levels) {
      super.addXp(levels);
      this.lastXp = -1;
   }

   @Override
   public void m_92coykull(int i) {
      super.m_92coykull(i);
      this.lastXp = -1;
   }

   public void listenToScreenHandler() {
      this.menu.addListener(this);
   }

   @Override
   public void m_18fvbnxav() {
      super.m_18fvbnxav();
      this.networkHandler.sendPacket(new PlayerCombatS2CPacket(this.getDamageTracker(), PlayerCombatS2CPacket.Event.ENTER_COMBAT));
   }

   @Override
   public void m_10fgolizq() {
      super.m_10fgolizq();
      this.networkHandler.sendPacket(new PlayerCombatS2CPacket(this.getDamageTracker(), PlayerCombatS2CPacket.Event.END_COMBAT));
   }

   @Override
   public void tick() {
      this.interactionManager.tick();
      --this.spawnProtectionTicks;
      if (this.maxHealth > 0) {
         --this.maxHealth;
      }

      this.menu.updateListeners();
      if (!this.world.isClient && !this.menu.isValid(this)) {
         this.closeMenu();
         this.menu = this.playerMenu;
      }

      while(!this.removedEntities.isEmpty()) {
         int var1 = Math.min(this.removedEntities.size(), Integer.MAX_VALUE);
         int[] var2 = new int[var1];
         Iterator var3 = this.removedEntities.iterator();
         int var4 = 0;

         while(var3.hasNext() && var4 < var1) {
            var2[var4++] = var3.next();
            var3.remove();
         }

         this.networkHandler.sendPacket(new RemoveEntitiesS2CPacket(var2));
      }

      if (!this.newLoadedChunks.isEmpty()) {
         ArrayList var6 = Lists.newArrayList();
         Iterator var8 = this.newLoadedChunks.iterator();
         ArrayList var9 = Lists.newArrayList();

         while(var8.hasNext() && var6.size() < 10) {
            ChunkPos var10 = (ChunkPos)var8.next();
            if (var10 != null) {
               if (this.world.isLoaded(new BlockPos(var10.x << 4, 0, var10.z << 4))) {
                  WorldChunk var5 = this.world.getChunkAt(var10.x, var10.z);
                  if (var5.isPopulated()) {
                     var6.add(var5);
                     var9.addAll(((ServerWorld)this.world).getBlockEntities(var10.x * 16, 0, var10.z * 16, var10.x * 16 + 16, 256, var10.z * 16 + 16));
                     var8.remove();
                  }
               }
            } else {
               var8.remove();
            }
         }

         if (!var6.isEmpty()) {
            if (var6.size() == 1) {
               this.networkHandler.sendPacket(new WorldChunkS2CPacket((WorldChunk)var6.get(0), true, 65535));
            } else {
               this.networkHandler.sendPacket(new WorldChunksS2CPacket(var6));
            }

            for(BlockEntity var13 : var9) {
               this.updateBlockEntity(var13);
            }

            for(WorldChunk var14 : var6) {
               this.getServerWorld().getEntityTracker().updateListener(this, var14);
            }
         }
      }

      Entity var7 = this.getCamera();
      if (var7 != this) {
         if (!var7.isAlive()) {
            this.setCamera(this);
         } else {
            this.teleport(var7.x, var7.y, var7.z, var7.yaw, var7.pitch);
            this.server.getPlayerManager().updateTrackedPos(this);
            if (this.isSneaking()) {
               this.setCamera(this);
            }
         }
      }
   }

   public void tickPlayer() {
      try {
         super.tick();

         for(int var1 = 0; var1 < this.inventory.getSize(); ++var1) {
            ItemStack var6 = this.inventory.getStack(var1);
            if (var6 != null && var6.getItem().isNetworkSynced()) {
               Packet var8 = ((NetworkSyncedItem)var6.getItem()).getUpdatePacket(var6, this.world, this);
               if (var8 != null) {
                  this.networkHandler.sendPacket(var8);
               }
            }
         }

         if (this.getHealth() != this.lastHealth
            || this.lastHungerLevel != this.hungerManager.getFoodLevel()
            || this.hungerManager.getSaturationLevel() == 0.0F != this.wasHungry) {
            this.networkHandler
               .sendPacket(new PlayerHealthS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
            this.lastHealth = this.getHealth();
            this.lastHungerLevel = this.hungerManager.getFoodLevel();
            this.wasHungry = this.hungerManager.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorption() != this.completeHealth) {
            this.completeHealth = this.getHealth() + this.getAbsorption();

            for(ScoreboardObjective var9 : this.getScoreboard().getObjectives(ScoreboardCriterion.HEALTH)) {
               this.getScoreboard().getScore(this.getName(), var9).setToTotalOf(Arrays.asList(this));
            }
         }

         if (this.xp != this.lastXp) {
            this.lastXp = this.xp;
            this.networkHandler.sendPacket(new PlayerXpS2CPacket(this.xpProgress, this.xp, this.xpLevel));
         }

         if (this.time % 20 * 5 == 0 && !this.getStatHandler().hasAchievement(Achievements.ENTER_ALL_BIOMES)) {
            this.updateExploreredBiomes();
         }
      } catch (Throwable var4) {
         CrashReport var2 = CrashReport.of(var4, "Ticking player");
         CashReportCategory var3 = var2.addCategory("Player being ticked");
         this.populateCrashReport(var3);
         throw new CrashException(var2);
      }
   }

   protected void updateExploreredBiomes() {
      Biome var1 = this.world.getBiome(new BlockPos(MathHelper.floor(this.x), 0, MathHelper.floor(this.z)));
      String var2 = var1.name;
      ForwardingJsonSet var3 = (ForwardingJsonSet)this.getStatHandler().getJsonSet(Achievements.ENTER_ALL_BIOMES);
      if (var3 == null) {
         var3 = (ForwardingJsonSet)this.getStatHandler().setJsonSet(Achievements.ENTER_ALL_BIOMES, new ForwardingJsonSet());
      }

      var3.add(var2);
      if (this.getStatHandler().hasParentAchievement(Achievements.ENTER_ALL_BIOMES) && var3.size() >= Biome.EXPLORABLE.size()) {
         HashSet var4 = Sets.newHashSet(Biome.EXPLORABLE);

         for(String var6 : var3) {
            Iterator var7 = var4.iterator();

            while(var7.hasNext()) {
               Biome var8 = (Biome)var7.next();
               if (var8.name.equals(var6)) {
                  var7.remove();
               }
            }

            if (var4.isEmpty()) {
               break;
            }
         }

         if (var4.isEmpty()) {
            this.incrementStat(Achievements.ENTER_ALL_BIOMES);
         }
      }
   }

   @Override
   public void onKilled(DamageSource source) {
      if (this.world.getGameRules().getBoolean("showDeathMessages")) {
         AbstractTeam var2 = this.getScoreboardTeam();
         if (var2 == null || var2.getDeathMessageVisibility() == AbstractTeam.Visibility.ALWAYS) {
            this.server.getPlayerManager().sendSystemMessage(this.getDamageTracker().getDeathMessage());
         } else if (var2.getDeathMessageVisibility() == AbstractTeam.Visibility.HIDE_FOR_OTHER_TEAMS) {
            this.server.getPlayerManager().sendMessageToTeamMembers(this, this.getDamageTracker().getDeathMessage());
         } else if (var2.getDeathMessageVisibility() == AbstractTeam.Visibility.HIDE_FOR_OWN_TEAM) {
            this.server.getPlayerManager().sendMessageToNonTeamMembers(this, this.getDamageTracker().getDeathMessage());
         }
      }

      if (!this.world.getGameRules().getBoolean("keepInventory")) {
         this.inventory.dropAll();
      }

      for(ScoreboardObjective var4 : this.world.getScoreboard().getObjectives(ScoreboardCriterion.DEATH_COUNT)) {
         ScoreboardScore var5 = this.getScoreboard().getScore(this.getName(), var4);
         var5.increment();
      }

      LivingEntity var7 = this.getLastAttacker();
      if (var7 != null) {
         int var8 = Entities.getRawId(var7);
         Entities.SpawnEggData var9 = (Entities.SpawnEggData)Entities.RAW_ID_TO_SPAWN_EGG_DATA.get(var8);
         if (var9 != null) {
            this.incrementStat(var9.entityKilledByStat);
         }

         var7.onKillEntity(this, this.mobValue);
      }

      this.incrementStat(Stats.DEATHS);
      this.clearStat(Stats.TIME_SINCE_DEATH);
      this.getDamageTracker().clearDamageHistory();
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         boolean var3 = this.server.isDedicated() && this.canPvp() && "fall".equals(source.name);
         if (!var3 && this.spawnProtectionTicks > 0 && source != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (source instanceof EntityDamageSource) {
               Entity var4 = source.getAttacker();
               if (var4 instanceof PlayerEntity && !this.canAttack((PlayerEntity)var4)) {
                  return false;
               }

               if (var4 instanceof ArrowEntity) {
                  ArrowEntity var5 = (ArrowEntity)var4;
                  if (var5.shooter instanceof PlayerEntity && !this.canAttack((PlayerEntity)var5.shooter)) {
                     return false;
                  }
               }
            }

            return super.damage(source, amount);
         }
      }
   }

   @Override
   public boolean canAttack(PlayerEntity player) {
      return !this.canPvp() ? false : super.canAttack(player);
   }

   private boolean canPvp() {
      return this.server.isPvpEnabled();
   }

   @Override
   public void teleportToDimension(int dimensionId) {
      if (this.dimensionId == 1 && dimensionId == 1) {
         this.incrementStat(Achievements.LEAVE_THE_END);
         this.world.removeEntity(this);
         this.leavingTheEnd = true;
         this.networkHandler.sendPacket(new GameEventS2CPacket(4, 0.0F));
      } else {
         if (this.dimensionId == 0 && dimensionId == 1) {
            this.incrementStat(Achievements.ENTER_THE_END);
            BlockPos var2 = this.server.getWorld(dimensionId).getForcedSpawnPoint();
            if (var2 != null) {
               this.networkHandler.teleport((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), 0.0F, 0.0F);
            }

            dimensionId = 1;
         } else {
            this.incrementStat(Achievements.ENTER_THE_NETHER);
         }

         this.server.getPlayerManager().teleportToDimension(this, dimensionId);
         this.lastXp = -1;
         this.lastHealth = -1.0F;
         this.lastHungerLevel = -1;
      }
   }

   @Override
   public boolean m_89sxhouae(ServerPlayerEntity c_53mtutqhz) {
      if (c_53mtutqhz.isSpectator()) {
         return this.getCamera() == this;
      } else {
         return this.isSpectator() ? false : super.m_89sxhouae(c_53mtutqhz);
      }
   }

   private void updateBlockEntity(BlockEntity blockEntity) {
      if (blockEntity != null) {
         Packet var2 = blockEntity.createUpdatePacket();
         if (var2 != null) {
            this.networkHandler.sendPacket(var2);
         }
      }
   }

   @Override
   public void sendPickup(Entity entity, int count) {
      super.sendPickup(entity, count);
      this.menu.updateListeners();
   }

   @Override
   public PlayerEntity.SleepAllowedStatus trySleep(BlockPos x) {
      PlayerEntity.SleepAllowedStatus var2 = super.trySleep(x);
      if (var2 == PlayerEntity.SleepAllowedStatus.OK) {
         PlayerSleepS2CPacket var3 = new PlayerSleepS2CPacket(this, x);
         this.getServerWorld().getEntityTracker().sendToListeners(this, var3);
         this.networkHandler.teleport(this.x, this.y, this.z, this.yaw, this.pitch);
         this.networkHandler.sendPacket(var3);
      }

      return var2;
   }

   @Override
   public void wakeUp(boolean resetSleepTimer, boolean update, boolean setSpawnPoint) {
      if (this.isSleeping()) {
         this.getServerWorld().getEntityTracker().sendToListenersAndTrackedEntityIfPlayer(this, new EntityAnimationS2CPacket(this, 2));
      }

      super.wakeUp(resetSleepTimer, update, setSpawnPoint);
      if (this.networkHandler != null) {
         this.networkHandler.teleport(this.x, this.y, this.z, this.yaw, this.pitch);
      }
   }

   @Override
   public void startRiding(Entity entity) {
      Entity var2 = this.vehicle;
      super.startRiding(entity);
      if (entity != var2) {
         this.networkHandler.sendPacket(new EntityAttachS2CPacket(0, this, this.vehicle));
         this.networkHandler.teleport(this.x, this.y, this.z, this.yaw, this.pitch);
      }
   }

   @Override
   protected void onFall(double dy, boolean landed, Block block, BlockPos pos) {
   }

   public void handleFall(double distance, boolean bl) {
      int var4 = MathHelper.floor(this.x);
      int var5 = MathHelper.floor(this.y - 0.2F);
      int var6 = MathHelper.floor(this.z);
      BlockPos var7 = new BlockPos(var4, var5, var6);
      Block var8 = this.world.getBlockState(var7).getBlock();
      if (var8.getMaterial() == Material.AIR) {
         Block var9 = this.world.getBlockState(var7.down()).getBlock();
         if (var9 instanceof FenceBlock || var9 instanceof WallBlock || var9 instanceof FenceGateBlock) {
            var7 = var7.down();
            var8 = this.world.getBlockState(var7).getBlock();
         }
      }

      super.onFall(distance, bl, var8, var7);
   }

   @Override
   public void openSignEditor(SignBlockEntity sign) {
      sign.setPlayer(this);
      this.networkHandler.sendPacket(new OpenSignEditorS2CPacket(sign.getPos()));
   }

   private void incrementSyncId() {
      this.screenHandlerSyncId = this.screenHandlerSyncId % 100 + 1;
   }

   @Override
   public void openMenu(MenuProvider menuProvider) {
      this.incrementSyncId();
      this.networkHandler.sendPacket(new OpenMenuS2CPacket(this.screenHandlerSyncId, menuProvider.getMenuType(), menuProvider.getDisplayName()));
      this.menu = menuProvider.createMenu(this.inventory, this);
      this.menu.networkId = this.screenHandlerSyncId;
      this.menu.addListener(this);
   }

   @Override
   public void openInventoryMenu(Inventory inventory) {
      if (this.menu != this.playerMenu) {
         this.closeMenu();
      }

      if (inventory instanceof LockableMenuProvider) {
         LockableMenuProvider var2 = (LockableMenuProvider)inventory;
         if (var2.isLocked() && !this.m_22uhjmmik(var2.getLock()) && !this.isSpectator()) {
            this.networkHandler.sendPacket(new ChatMessageS2CPacket(new TranslatableText("container.isLocked", inventory.getDisplayName()), (byte)2));
            this.networkHandler.sendPacket(new SoundEventS2CPacket("random.door_close", this.x, this.y, this.z, 1.0F, 1.0F));
            return;
         }
      }

      this.incrementSyncId();
      if (inventory instanceof MenuProvider) {
         this.networkHandler
            .sendPacket(
               new OpenMenuS2CPacket(this.screenHandlerSyncId, ((MenuProvider)inventory).getMenuType(), inventory.getDisplayName(), inventory.getSize())
            );
         this.menu = ((MenuProvider)inventory).createMenu(this.inventory, this);
      } else {
         this.networkHandler
            .sendPacket(new OpenMenuS2CPacket(this.screenHandlerSyncId, "minecraft:container", inventory.getDisplayName(), inventory.getSize()));
         this.menu = new ChestMenu(this.inventory, inventory, this);
      }

      this.menu.networkId = this.screenHandlerSyncId;
      this.menu.addListener(this);
   }

   @Override
   public void openTraderMenu(Trader trader) {
      this.incrementSyncId();
      this.menu = new TraderMenu(this.inventory, trader, this.world);
      this.menu.networkId = this.screenHandlerSyncId;
      this.menu.addListener(this);
      TraderInventory var2 = ((TraderMenu)this.menu).getTraderInventory();
      Text var3 = trader.getDisplayName();
      this.networkHandler.sendPacket(new OpenMenuS2CPacket(this.screenHandlerSyncId, "minecraft:villager", var3, var2.getSize()));
      TradeOffers var4 = trader.getOffers(this);
      if (var4 != null) {
         PacketByteBuf var5 = new PacketByteBuf(Unpooled.buffer());

         try {
            var5.writeInt(this.screenHandlerSyncId);
            var4.serialize(var5);
            this.networkHandler.sendPacket(new CustomPayloadS2CPacket("MC|TrList", var5));
         } catch (IOException var10) {
            LOGGER.error("Couldn't send trade list", var10);
         } finally {
            var5.release();
         }
      }
   }

   @Override
   public void openHorseMenu(HorseBaseEntity horse, Inventory inventory) {
      if (this.menu != this.playerMenu) {
         this.closeMenu();
      }

      this.incrementSyncId();
      this.networkHandler
         .sendPacket(new OpenMenuS2CPacket(this.screenHandlerSyncId, "EntityHorse", inventory.getDisplayName(), inventory.getSize(), horse.getNetworkId()));
      this.menu = new HorseMenu(this.inventory, inventory, horse, this);
      this.menu.networkId = this.screenHandlerSyncId;
      this.menu.addListener(this);
   }

   @Override
   public void openEditBookScreen(ItemStack book) {
      Item var2 = book.getItem();
      if (var2 == Items.WRITTEN_BOOK) {
         this.networkHandler.sendPacket(new CustomPayloadS2CPacket("MC|BOpen", new byte[0]));
      }
   }

   @Override
   public void onSlotChanged(InventoryMenu menu, int id, ItemStack stack) {
      if (!(menu.getSlot(id) instanceof CraftingResultSlot)) {
         if (!this.useItemCooldown) {
            this.networkHandler.sendPacket(new MenuSlotUpdateS2CPacket(menu.networkId, id, stack));
         }
      }
   }

   public void setMenu(InventoryMenu handler) {
      this.updateMenu(handler, handler.getStacks());
   }

   @Override
   public void updateMenu(InventoryMenu menu, List stacks) {
      this.networkHandler.sendPacket(new InventoryMenuS2CPacket(menu.networkId, stacks));
      this.networkHandler.sendPacket(new MenuSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
   }

   @Override
   public void onDataChanged(InventoryMenu menu, int id, int value) {
      this.networkHandler.sendPacket(new MenuDataS2CPacket(menu.networkId, id, value));
   }

   @Override
   public void updateData(InventoryMenu menu, Inventory inventory) {
      for(int var3 = 0; var3 < inventory.getDataCount(); ++var3) {
         this.networkHandler.sendPacket(new MenuDataS2CPacket(menu.networkId, var3, inventory.getData(var3)));
      }
   }

   @Override
   public void closeMenu() {
      this.networkHandler.sendPacket(new CloseMenuS2CPacket(this.menu.networkId));
      this.doCloseMenu();
   }

   public void use() {
      if (!this.useItemCooldown) {
         this.networkHandler.sendPacket(new MenuSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
      }
   }

   public void doCloseMenu() {
      this.menu.close(this);
      this.menu = this.playerMenu;
   }

   public void setPlayerInput(float sidewaysSpeed, float forwardSpeed, boolean jumping, boolean sneaking) {
      if (this.vehicle != null) {
         if (sidewaysSpeed >= -1.0F && sidewaysSpeed <= 1.0F) {
            this.sidewaysSpeed = sidewaysSpeed;
         }

         if (forwardSpeed >= -1.0F && forwardSpeed <= 1.0F) {
            this.forwardSpeed = forwardSpeed;
         }

         this.jumping = jumping;
         this.setSneaking(sneaking);
      }
   }

   @Override
   public void incrementStat(Stat stat, int amount) {
      if (stat != null) {
         this.statHandler.trySetValue(this, stat, amount);

         for(ScoreboardObjective var4 : this.getScoreboard().getObjectives(stat.getCriterion())) {
            this.getScoreboard().getScore(this.getName(), var4).increase(amount);
         }

         if (this.statHandler.shouldUpdate()) {
            this.statHandler.sendStats(this);
         }
      }
   }

   @Override
   public void clearStat(Stat stat) {
      if (stat != null) {
         this.statHandler.setValue(this, stat, 0);

         for(ScoreboardObjective var3 : this.getScoreboard().getObjectives(stat.getCriterion())) {
            this.getScoreboard().getScore(this.getName(), var3).set(0);
         }

         if (this.statHandler.shouldUpdate()) {
            this.statHandler.sendStats(this);
         }
      }
   }

   public void onDisconnect() {
      if (this.rider != null) {
         this.rider.startRiding(this);
      }

      if (this.sleeping) {
         this.wakeUp(true, false, false);
      }
   }

   public void markHealthDirty() {
      this.lastHealth = -1.0E8F;
   }

   @Override
   public void addMessage(Text message) {
      this.networkHandler.sendPacket(new ChatMessageS2CPacket(message));
   }

   @Override
   protected void finishUsingItem() {
      this.networkHandler.sendPacket(new EntityEventS2CPacket(this, (byte)9));
      super.finishUsingItem();
   }

   @Override
   public void setUseItem(ItemStack stack, int maxUseTime) {
      super.setUseItem(stack, maxUseTime);
      if (stack != null && stack.getItem() != null && stack.getItem().getUseAction(stack) == UseAction.EAT) {
         this.getServerWorld().getEntityTracker().sendToListenersAndTrackedEntityIfPlayer(this, new EntityAnimationS2CPacket(this, 3));
      }
   }

   @Override
   public void copyFrom(PlayerEntity player, boolean comesFromTheEnd) {
      super.copyFrom(player, comesFromTheEnd);
      this.lastXp = -1;
      this.lastHealth = -1.0F;
      this.lastHungerLevel = -1;
      this.removedEntities.addAll(((ServerPlayerEntity)player).removedEntities);
   }

   @Override
   protected void onStatusEffectApplied(StatusEffectInstance instance) {
      super.onStatusEffectApplied(instance);
      this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getNetworkId(), instance));
   }

   @Override
   protected void onStatusEffectUpgraded(StatusEffectInstance instance, boolean timerRanOut) {
      super.onStatusEffectUpgraded(instance, timerRanOut);
      this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getNetworkId(), instance));
   }

   @Override
   protected void onStatusEffectRemoved(StatusEffectInstance effect) {
      super.onStatusEffectRemoved(effect);
      this.networkHandler.sendPacket(new EntityRemoveStatusEffectS2CPacket(this.getNetworkId(), effect));
   }

   @Override
   public void refreshPosition(double x, double y, double z) {
      this.networkHandler.teleport(x, y, z, this.yaw, this.pitch);
   }

   @Override
   public void addCritParticles(Entity entity) {
      this.getServerWorld().getEntityTracker().sendToListenersAndTrackedEntityIfPlayer(this, new EntityAnimationS2CPacket(entity, 4));
   }

   @Override
   public void addEnchantedCritParticles(Entity entity) {
      this.getServerWorld().getEntityTracker().sendToListenersAndTrackedEntityIfPlayer(this, new EntityAnimationS2CPacket(entity, 5));
   }

   @Override
   public void syncAbilities() {
      if (this.networkHandler != null) {
         this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
         this.updateVisibility();
      }
   }

   public ServerWorld getServerWorld() {
      return (ServerWorld)this.world;
   }

   @Override
   public void setGameMode(WorldSettings.GameMode gameMode) {
      this.interactionManager.setGameMode(gameMode);
      this.networkHandler.sendPacket(new GameEventS2CPacket(3, (float)gameMode.getIndex()));
      if (gameMode == WorldSettings.GameMode.SPECTATOR) {
         this.startRiding(null);
      } else {
         this.setCamera(this);
      }

      this.syncAbilities();
      this.m_38sehrahq();
   }

   @Override
   public boolean isSpectator() {
      return this.interactionManager.getGameMode() == WorldSettings.GameMode.SPECTATOR;
   }

   @Override
   public void sendMessage(Text message) {
      this.networkHandler.sendPacket(new ChatMessageS2CPacket(message));
   }

   @Override
   public boolean canUseCommand(int permissionLevel, String command) {
      if ("seed".equals(command) && !this.server.isDedicated()) {
         return true;
      } else if (!"tell".equals(command) && !"help".equals(command) && !"me".equals(command) && !"trigger".equals(command)) {
         if (this.server.getPlayerManager().isOp(this.getGameProfile())) {
            OpEntry var3 = (OpEntry)this.server.getPlayerManager().getOps().get(this.getGameProfile());
            if (var3 != null) {
               return var3.getPermissionLevel() >= permissionLevel;
            } else {
               return this.server.getOpPermissionLevel() >= permissionLevel;
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public String getIp() {
      String var1 = this.networkHandler.connection.getAddress().toString();
      var1 = var1.substring(var1.indexOf("/") + 1);
      return var1.substring(0, var1.indexOf(":"));
   }

   public void updateSettings(ClientSettingsC2SPacket packet) {
      this.lang = packet.getLanguage();
      this.chatVisibility = packet.getChatVisibility();
      this.chatColors = packet.getChatColors();
      this.getDataTracker().update(10, (byte)packet.getViewDistance());
   }

   public PlayerEntity.ChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public void sendResourcePack(String url) {
      this.networkHandler.sendPacket(new CustomPayloadS2CPacket("MC|RPack", url.getBytes(Charsets.UTF_8)));
   }

   @Override
   public BlockPos getSourceBlockPos() {
      return new BlockPos(this.x, this.y + 0.5, this.z);
   }

   public void updateLastActionTime() {
      this.lastActionTime = MinecraftServer.getTimeMillis();
   }

   public ServerStatHandler getStatHandler() {
      return this.statHandler;
   }

   public void m_26bdxtpev(Entity c_47ldwddrb) {
      if (c_47ldwddrb instanceof PlayerEntity) {
         this.networkHandler.sendPacket(new RemoveEntitiesS2CPacket(c_47ldwddrb.getNetworkId()));
      } else {
         this.removedEntities.add(c_47ldwddrb.getNetworkId());
      }
   }

   @Override
   protected void updateVisibility() {
      if (this.isSpectator()) {
         this.clearEffectParticles();
         this.setInvisible(true);
      } else {
         super.updateVisibility();
      }

      this.getServerWorld().getEntityTracker().updateVisibility(this);
   }

   public Entity getCamera() {
      return (Entity)(this.camera == null ? this : this.camera);
   }

   public void setCamera(Entity camera) {
      Entity var2 = this.getCamera();
      this.camera = (Entity)(camera == null ? this : camera);
      if (var2 != this.camera) {
         this.networkHandler.sendPacket(new CameraS2CPacket(this.camera));
         this.refreshPosition(this.camera.x, this.camera.y, this.camera.z);
      }
   }

   @Override
   public void attack(Entity target) {
      if (this.interactionManager.getGameMode() == WorldSettings.GameMode.SPECTATOR) {
         this.setCamera(target);
      } else {
         super.attack(target);
      }
   }

   public long getLastActionTime() {
      return this.lastActionTime;
   }

   public Text m_95aslciht() {
      return null;
   }
}
