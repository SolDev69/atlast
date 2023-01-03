package net.minecraft.client.network.handler;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.C_84obvpdwb;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.FlowerPotBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.ClientPlayerInteractionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.entity.living.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConfirmationListener;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.inventory.BookEditScreen;
import net.minecraft.client.gui.screen.inventory.menu.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.inventory.menu.VillagerScreen;
import net.minecraft.client.gui.screen.menu.StatsListener;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.event.GuardianAttackSoundEvent;
import net.minecraft.client.twitch.AchievementMetadata;
import net.minecraft.client.twitch.PlayerCombatMetadata;
import net.minecraft.client.twitch.PlayerDeathMetadata;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.villager.trade.ClientTrader;
import net.minecraft.entity.EnderCrystalEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworksEntity;
import net.minecraft.entity.FishingBobberEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.ClampedEntityAttribute;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.hostile.GuardianEntity;
import net.minecraft.entity.living.mob.passive.Trader;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.inventory.AnimalInventory;
import net.minecraft.inventory.MenuInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.menu.EmptyMenuProvider;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ConfirmMenuActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.AddEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.AddGlobalEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.AddMobS2CPacket;
import net.minecraft.network.packet.s2c.play.AddPaintingS2CPacket;
import net.minecraft.network.packet.s2c.play.AddPlayerS2CPacket;
import net.minecraft.network.packet.s2c.play.AddXpOrbS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockMiningProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlocksUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CameraS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseMenuS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CompressionThresholdS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmMenuActionS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDataS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEventS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityHeadAnglesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPickupS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityRemoveStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTeleportS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameEventS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryMenuS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.LoginS2CPacket;
import net.minecraft.network.packet.s2c.play.MapDataS2CPacket;
import net.minecraft.network.packet.s2c.play.MenuDataS2CPacket;
import net.minecraft.network.packet.s2c.play.MenuSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenMenuS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenSignEditorS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerHealthS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerInfoS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSleepS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPointS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerXpS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.SignBlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SoundEventS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.TabListS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TitlesS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldChunksS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeS2CPacket;
import net.minecraft.realms.DisconnectedOnlineScreen;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.stat.Stat;
import net.minecraft.stat.achievement.AchievementStat;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.map.SavedMapData;
import net.minecraft.world.village.trade.TradeOffers;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandler implements ClientPlayPacketHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Connection connection;
   private final GameProfile profile;
   private final Screen callbackScreen;
   private MinecraftClient client;
   private ClientWorld world;
   private boolean started;
   private final Map onlinePlayers = Maps.newHashMap();
   public int maxPlayerCount = 20;
   private boolean hasAchievements = false;
   private final Random random = new Random();

   public ClientPlayNetworkHandler(MinecraftClient client, Screen callbackScreen, Connection connection, GameProfile profile) {
      this.client = client;
      this.callbackScreen = callbackScreen;
      this.connection = connection;
      this.profile = profile;
   }

   public void cleanUp() {
      this.world = null;
   }

   @Override
   public void handleLogin(LoginS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
      this.world = new ClientWorld(
         this,
         new WorldSettings(0L, packet.getGameMode(), false, packet.getHardcore(), packet.getGeneratorType()),
         packet.getDimensionId(),
         packet.getDifficulty(),
         this.client.profiler
      );
      this.client.options.difficulty = packet.getDifficulty();
      this.client.setWorld(this.world);
      this.client.player.dimensionId = packet.getDimensionId();
      this.client.openScreen(new DownloadingTerrainScreen(this));
      this.client.player.setNetworkId(packet.getEntityId());
      this.maxPlayerCount = packet.getMaxPlayerCount();
      this.client.player.setReducedDebugInfo(packet.getReducedDebugInfo());
      this.client.interactionManager.setGameMode(packet.getGameMode());
      this.client.options.syncClientSettings();
      this.connection.send(new CustomPayloadC2SPacket("MC|Brand", ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)));
   }

   @Override
   public void handleAddEntity(AddEntityS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      double var2 = (double)packet.getX() / 32.0;
      double var4 = (double)packet.getY() / 32.0;
      double var6 = (double)packet.getZ() / 32.0;
      Object var8 = null;
      if (packet.getType() == 10) {
         var8 = MinecartEntity.create(this.world, var2, var4, var6, MinecartEntity.Type.byIndex(packet.getData()));
      } else if (packet.getType() == 90) {
         Entity var9 = this.world.getEntity(packet.getData());
         if (var9 instanceof PlayerEntity) {
            var8 = new FishingBobberEntity(this.world, var2, var4, var6, (PlayerEntity)var9);
         }

         packet.setData(0);
      } else if (packet.getType() == 60) {
         var8 = new ArrowEntity(this.world, var2, var4, var6);
      } else if (packet.getType() == 61) {
         var8 = new SnowballEntity(this.world, var2, var4, var6);
      } else if (packet.getType() == 71) {
         var8 = new ItemFrameEntity(
            this.world, new BlockPos(MathHelper.floor(var2), MathHelper.floor(var4), MathHelper.floor(var6)), Direction.byIdHorizontal(packet.getData())
         );
         packet.setData(0);
      } else if (packet.getType() == 77) {
         var8 = new LeadKnotEntity(this.world, new BlockPos(MathHelper.floor(var2), MathHelper.floor(var4), MathHelper.floor(var6)));
         packet.setData(0);
      } else if (packet.getType() == 65) {
         var8 = new EnderPearlEntity(this.world, var2, var4, var6);
      } else if (packet.getType() == 72) {
         var8 = new EnderEyeEntity(this.world, var2, var4, var6);
      } else if (packet.getType() == 76) {
         var8 = new FireworksEntity(this.world, var2, var4, var6, null);
      } else if (packet.getType() == 63) {
         var8 = new FireballEntity(
            this.world,
            var2,
            var4,
            var6,
            (double)packet.getVelocityX() / 8000.0,
            (double)packet.getVelocityY() / 8000.0,
            (double)packet.getVelocityZ() / 8000.0
         );
         packet.setData(0);
      } else if (packet.getType() == 64) {
         var8 = new SmallFireballEntity(
            this.world,
            var2,
            var4,
            var6,
            (double)packet.getVelocityX() / 8000.0,
            (double)packet.getVelocityY() / 8000.0,
            (double)packet.getVelocityZ() / 8000.0
         );
         packet.setData(0);
      } else if (packet.getType() == 66) {
         var8 = new WitherSkullEntity(
            this.world,
            var2,
            var4,
            var6,
            (double)packet.getVelocityX() / 8000.0,
            (double)packet.getVelocityY() / 8000.0,
            (double)packet.getVelocityZ() / 8000.0
         );
         packet.setData(0);
      } else if (packet.getType() == 62) {
         var8 = new EggEntity(this.world, var2, var4, var6);
      } else if (packet.getType() == 73) {
         var8 = new PotionEntity(this.world, var2, var4, var6, packet.getData());
         packet.setData(0);
      } else if (packet.getType() == 75) {
         var8 = new ExperienceBottleEntity(this.world, var2, var4, var6);
         packet.setData(0);
      } else if (packet.getType() == 1) {
         var8 = new BoatEntity(this.world, var2, var4, var6);
      } else if (packet.getType() == 50) {
         var8 = new PrimedTntEntity(this.world, var2, var4, var6, null);
      } else if (packet.getType() == 51) {
         var8 = new EnderCrystalEntity(this.world, var2, var4, var6);
      } else if (packet.getType() == 2) {
         var8 = new ItemEntity(this.world, var2, var4, var6);
      } else if (packet.getType() == 70) {
         var8 = new FallingBlockEntity(this.world, var2, var4, var6, Block.deserialize(packet.getData() & 65535));
         packet.setData(0);
      }

      if (var8 != null) {
         ((Entity)var8).packetX = packet.getX();
         ((Entity)var8).packetY = packet.getY();
         ((Entity)var8).packetZ = packet.getZ();
         ((Entity)var8).pitch = (float)(packet.getPitch() * 360) / 256.0F;
         ((Entity)var8).yaw = (float)(packet.getYaw() * 360) / 256.0F;
         Entity[] var12 = ((Entity)var8).getParts();
         if (var12 != null) {
            int var10 = packet.getId() - ((Entity)var8).getNetworkId();

            for(int var11 = 0; var11 < var12.length; ++var11) {
               var12[var11].setNetworkId(var12[var11].getNetworkId() + var10);
            }
         }

         ((Entity)var8).setNetworkId(packet.getId());
         this.world.addEntity(packet.getId(), (Entity)var8);
         if (packet.getData() > 0) {
            if (packet.getType() == 60) {
               Entity var13 = this.world.getEntity(packet.getData());
               if (var13 instanceof LivingEntity && var8 instanceof ArrowEntity) {
                  ((ArrowEntity)var8).shooter = var13;
               }
            }

            ((Entity)var8).setVelocity((double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0);
         }
      }
   }

   @Override
   public void handleAddXpOrb(AddXpOrbS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      XpOrbEntity var2 = new XpOrbEntity(this.world, (double)packet.getX(), (double)packet.getY(), (double)packet.getZ(), packet.getXp());
      var2.packetX = packet.getX();
      var2.packetY = packet.getY();
      var2.packetZ = packet.getZ();
      var2.yaw = 0.0F;
      var2.pitch = 0.0F;
      var2.setNetworkId(packet.getId());
      this.world.addEntity(packet.getId(), var2);
   }

   @Override
   public void handleAddGlobalEntity(AddGlobalEntityS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      double var2 = (double)packet.getX() / 32.0;
      double var4 = (double)packet.getY() / 32.0;
      double var6 = (double)packet.getZ() / 32.0;
      LightningBoltEntity var8 = null;
      if (packet.getType() == 1) {
         var8 = new LightningBoltEntity(this.world, var2, var4, var6);
      }

      if (var8 != null) {
         var8.packetX = packet.getX();
         var8.packetY = packet.getY();
         var8.packetZ = packet.getZ();
         var8.yaw = 0.0F;
         var8.pitch = 0.0F;
         var8.setNetworkId(packet.getId());
         this.world.addGlobalEntity(var8);
      }
   }

   @Override
   public void handleAddPainting(AddPaintingS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      PaintingEntity var2 = new PaintingEntity(this.world, packet.getPos(), packet.getFacing(), packet.getMotive());
      this.world.addEntity(packet.getId(), var2);
   }

   @Override
   public void handleEntityVelocity(EntityVelocityS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.getId());
      if (var2 != null) {
         var2.setVelocity((double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0);
      }
   }

   @Override
   public void handleEntityData(EntityDataS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.getId());
      if (var2 != null && packet.getDataEntries() != null) {
         var2.getDataTracker().update(packet.getDataEntries());
      }
   }

   @Override
   public void handleAddPlayer(AddPlayerS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      double var2 = (double)packet.getX() / 32.0;
      double var4 = (double)packet.getY() / 32.0;
      double var6 = (double)packet.getZ() / 32.0;
      float var8 = (float)(packet.getYaw() * 360) / 256.0F;
      float var9 = (float)(packet.getPitch() * 360) / 256.0F;
      RemoteClientPlayerEntity var10 = new RemoteClientPlayerEntity(this.client.world, this.getOnlinePlayer(packet.getUuid()).getProfile());
      var10.prevX = var10.prevTickX = (double)(var10.packetX = packet.getX());
      var10.prevY = var10.prevTickY = (double)(var10.packetY = packet.getY());
      var10.prevZ = var10.prevTickZ = (double)(var10.packetZ = packet.getZ());
      int var11 = packet.getHeldItemId();
      if (var11 == 0) {
         var10.inventory.inventorySlots[var10.inventory.selectedSlot] = null;
      } else {
         var10.inventory.inventorySlots[var10.inventory.selectedSlot] = new ItemStack(Item.byRawId(var11), 1, 0);
      }

      var10.teleport(var2, var4, var6, var8, var9);
      this.world.addEntity(packet.getId(), var10);
      List var12 = packet.getDataTrackerEntries();
      if (var12 != null) {
         var10.getDataTracker().update(var12);
      }
   }

   @Override
   public void handleEntityTeleport(EntityTeleportS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.getId());
      if (var2 != null) {
         var2.packetX = packet.getX();
         var2.packetY = packet.getY();
         var2.packetZ = packet.getZ();
         double var3 = (double)var2.packetX / 32.0;
         double var5 = (double)var2.packetY / 32.0 + 0.015625;
         double var7 = (double)var2.packetZ / 32.0;
         float var9 = (float)(packet.getYaw() * 360) / 256.0F;
         float var10 = (float)(packet.getPitch() * 360) / 256.0F;
         var2.updatePositionAndAngles(var3, var5, var7, var9, var10, 3);
         var2.onGround = packet.getOnGround();
      }
   }

   @Override
   public void handleSelectSlot(SelectSlotS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      if (packet.getSlot() >= 0 && packet.getSlot() < PlayerInventory.getHotbarSize()) {
         this.client.player.inventory.selectedSlot = packet.getSlot();
      }
   }

   @Override
   public void handleEntityMove(EntityMoveS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = packet.getEntity(this.world);
      if (var2 != null) {
         var2.packetX += packet.getDx();
         var2.packetY += packet.getDy();
         var2.packetZ += packet.getDz();
         double var3 = (double)var2.packetX / 32.0;
         double var5 = (double)var2.packetY / 32.0;
         double var7 = (double)var2.packetZ / 32.0;
         float var9 = packet.hasAngles() ? (float)(packet.getYaw() * 360) / 256.0F : var2.yaw;
         float var10 = packet.hasAngles() ? (float)(packet.getPitch() * 360) / 256.0F : var2.pitch;
         var2.updatePositionAndAngles(var3, var5, var7, var9, var10, 3);
         var2.onGround = packet.getOnGround();
      }
   }

   @Override
   public void handleEntityHeadAngles(EntityHeadAnglesS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = packet.getEntity(this.world);
      if (var2 != null) {
         float var3 = (float)(packet.getHeadYaw() * 360) / 256.0F;
         var2.setHeadYaw(var3);
      }
   }

   @Override
   public void handleRemoveEntities(RemoveEntitiesS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);

      for(int var2 = 0; var2 < packet.getIds().length; ++var2) {
         this.world.removeEntity(packet.getIds()[var2]);
      }
   }

   @Override
   public void handlePlayerMove(PlayerMoveS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      LocalClientPlayerEntity var2 = this.client.player;
      double var3 = packet.getX();
      double var5 = packet.getY();
      double var7 = packet.getZ();
      float var9 = packet.getYaw();
      float var10 = packet.getPitch();
      if (packet.getRelativeArgs().contains(PlayerMoveS2CPacket.Argument.X)) {
         var3 += var2.x;
      } else {
         var2.velocityX = 0.0;
      }

      if (packet.getRelativeArgs().contains(PlayerMoveS2CPacket.Argument.Y)) {
         var5 += var2.y;
      } else {
         var2.velocityY = 0.0;
      }

      if (packet.getRelativeArgs().contains(PlayerMoveS2CPacket.Argument.Z)) {
         var7 += var2.z;
      } else {
         var2.velocityZ = 0.0;
      }

      if (packet.getRelativeArgs().contains(PlayerMoveS2CPacket.Argument.PITCH)) {
         var10 += var2.pitch;
      }

      if (packet.getRelativeArgs().contains(PlayerMoveS2CPacket.Argument.YAW)) {
         var9 += var2.yaw;
      }

      var2.teleport(var3, var5, var7, var9, var10);
      this.connection.send(new PlayerMoveC2SPacket.PositionAndAngles(var2.x, var2.getBoundingBox().minY, var2.z, var2.yaw, var2.pitch, false));
      if (!this.started) {
         this.client.player.prevX = this.client.player.x;
         this.client.player.prevY = this.client.player.y;
         this.client.player.prevZ = this.client.player.z;
         this.started = true;
         this.client.openScreen(null);
      }
   }

   @Override
   public void handleBlocksUpdate(BlocksUpdateS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);

      for(BlocksUpdateS2CPacket.BlockUpdate var5 : packet.getUpdates()) {
         this.world.setBlockStateFromPacket(var5.getBlockPos(), var5.getBlockState());
      }
   }

   @Override
   public void handleWorldChunk(WorldChunkS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      if (packet.isFull()) {
         if (packet.getSectionsWithData() == 0) {
            this.world.updateChunk(packet.getChunkX(), packet.getChunkZ(), false);
            return;
         }

         this.world.updateChunk(packet.getChunkX(), packet.getChunkZ(), true);
      }

      this.world.regionChanged(packet.getChunkX() << 4, 0, packet.getChunkZ() << 4, (packet.getChunkX() << 4) + 15, 256, (packet.getChunkZ() << 4) + 15);
      WorldChunk var2 = this.world.getChunkAt(packet.getChunkX(), packet.getChunkZ());
      var2.set(packet.getRawChunkData(), packet.getSectionsWithData(), packet.isFull());
      this.world.onRegionChanged(packet.getChunkX() << 4, 0, packet.getChunkZ() << 4, (packet.getChunkX() << 4) + 15, 256, (packet.getChunkZ() << 4) + 15);
      if (!packet.isFull() || !(this.world.dimension instanceof OverworldDimension)) {
         var2.clearLightChecks();
      }
   }

   @Override
   public void handleBlockUpdate(BlockUpdateS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.world.setBlockStateFromPacket(packet.getPos(), packet.getBlockState());
   }

   @Override
   public void handleDisconnect(DisconnectS2CPacket packet) {
      this.connection.disconnect(packet.getReason());
   }

   @Override
   public void onDisconnect(Text reason) {
      this.client.setWorld(null);
      if (this.callbackScreen != null) {
         if (this.callbackScreen instanceof C_84obvpdwb) {
            this.client.openScreen(new DisconnectedOnlineScreen(((C_84obvpdwb)this.callbackScreen).m_23xkexgoe(), "disconnect.lost", reason).getProxy());
         } else {
            this.client.openScreen(new DisconnectedScreen(this.callbackScreen, "disconnect.lost", reason));
         }
      } else {
         this.client.openScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), "disconnect.lost", reason));
      }
   }

   public void sendPacket(Packet packet) {
      this.connection.send(packet);
   }

   @Override
   public void handleEntityPickup(EntityPickupS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.getId());
      LivingEntity var3 = (LivingEntity)this.world.getEntity(packet.getCollectorId());
      if (var3 == null) {
         LocalClientPlayerEntity var4 = this.client.player;
      }

      if (var2 != null) {
         if (var2 instanceof XpOrbEntity) {
            this.world.playSound(var2, "random.orb", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         } else {
            this.world.playSound(var2, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         this.world.addParticle(ParticleType.ITEM_TAKE, var2.x, var2.y, var2.z, var2.velocityX, var2.velocityY, var2.velocityZ, new int[0]);
         this.world.removeEntity(packet.getId());
      }
   }

   @Override
   public void handleChatMessage(ChatMessageS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      if (packet.getType() == 2) {
         this.client.gui.setOverlayMessage(packet.getMessage(), false);
      } else {
         this.client.gui.getChat().addMessage(packet.getMessage());
      }
   }

   @Override
   public void handleEntityAnimation(EntityAnimationS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.getId());
      if (var2 != null) {
         if (packet.getAction() == 0) {
            LivingEntity var3 = (LivingEntity)var2;
            var3.swingHand();
         } else if (packet.getAction() == 1) {
            var2.animateDamage();
         } else if (packet.getAction() == 2) {
            PlayerEntity var4 = (PlayerEntity)var2;
            var4.wakeUp(false, false, false);
         } else if (packet.getAction() == 4) {
            this.client.particleManager.addEmitter(var2, ParticleType.CRIT);
         } else if (packet.getAction() == 5) {
            this.client.particleManager.addEmitter(var2, ParticleType.CRIT_MAGIC);
         }
      }
   }

   @Override
   public void handlePlayerSleep(PlayerSleepS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      packet.getPlayer(this.world).trySleep(packet.getPos());
   }

   @Override
   public void handleAddMob(AddMobS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      double var2 = (double)packet.getX() / 32.0;
      double var4 = (double)packet.getY() / 32.0;
      double var6 = (double)packet.getZ() / 32.0;
      float var8 = (float)(packet.getYaw() * 360) / 256.0F;
      float var9 = (float)(packet.getPitch() * 360) / 256.0F;
      LivingEntity var10 = (LivingEntity)Entities.create(packet.getType(), this.client.world);
      var10.packetX = packet.getX();
      var10.packetY = packet.getY();
      var10.packetZ = packet.getZ();
      var10.headYaw = (float)(packet.getHeadYaw() * 360) / 256.0F;
      Entity[] var11 = var10.getParts();
      if (var11 != null) {
         int var12 = packet.getId() - var10.getNetworkId();

         for(int var13 = 0; var13 < var11.length; ++var13) {
            var11[var13].setNetworkId(var11[var13].getNetworkId() + var12);
         }
      }

      var10.setNetworkId(packet.getId());
      var10.teleport(var2, var4, var6, var8, var9);
      var10.velocityX = (double)((float)packet.getVelocityX() / 8000.0F);
      var10.velocityY = (double)((float)packet.getVelocityY() / 8000.0F);
      var10.velocityZ = (double)((float)packet.getVelocityZ() / 8000.0F);
      this.world.addEntity(packet.getId(), var10);
      List var14 = packet.getEntries();
      if (var14 != null) {
         var10.getDataTracker().update(var14);
      }
   }

   @Override
   public void handleWorldTime(WorldTimeS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.world.setTime(packet.getTime());
      this.client.world.setTimeOfDay(packet.getTimeOfDay());
   }

   @Override
   public void handlePlayerSpawnPoint(PlayerSpawnPointS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.player.setSpawnpoint(packet.getSpawnPoint(), true);
      this.client.world.getData().setSpawnPoint(packet.getSpawnPoint());
   }

   @Override
   public void handleEntityAttach(EntityAttachS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Object var2 = this.world.getEntity(packet.getId());
      Entity var3 = this.world.getEntity(packet.getHolderId());
      if (packet.getType() == 0) {
         boolean var4 = false;
         if (packet.getId() == this.client.player.getNetworkId()) {
            var2 = this.client.player;
            if (var3 instanceof BoatEntity) {
               ((BoatEntity)var3).setEmpty(false);
            }

            var4 = ((Entity)var2).vehicle == null && var3 != null;
         } else if (var3 instanceof BoatEntity) {
            ((BoatEntity)var3).setEmpty(true);
         }

         if (var2 == null) {
            return;
         }

         ((Entity)var2).startRiding(var3);
         if (var4) {
            GameOptions var5 = this.client.options;
            this.client.gui.setOverlayMessage(I18n.translate("mount.onboard", GameOptions.getKeyName(var5.sneakKey.getKeyCode())), false);
         }
      } else if (packet.getType() == 1 && var2 instanceof MobEntity) {
         if (var3 != null) {
            ((MobEntity)var2).attachLeash(var3, false);
         } else {
            ((MobEntity)var2).detachLeash(false, false);
         }
      }
   }

   @Override
   public void handleEntityEvent(EntityEventS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = packet.getEntity(this.world);
      if (var2 != null) {
         if (packet.getEvent() == 21) {
            this.client.getSoundManager().play(new GuardianAttackSoundEvent((GuardianEntity)var2));
         } else {
            var2.doEvent(packet.getEvent());
         }
      }
   }

   @Override
   public void handlePlayerHealth(PlayerHealthS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.player.setPlayerHealth(packet.getHealth());
      this.client.player.getHungerManager().setFoodLevel(packet.getHunger());
      this.client.player.getHungerManager().setSaturationLevel(packet.getSaturation());
   }

   @Override
   public void handlePlayerXp(PlayerXpS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.player.setXp(packet.getLevelProgress(), packet.getXp(), packet.getLevel());
   }

   @Override
   public void handlePlayerRespawn(PlayerRespawnS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      if (packet.getDimensionId() != this.client.player.dimensionId) {
         this.started = false;
         Scoreboard var2 = this.world.getScoreboard();
         this.world = new ClientWorld(
            this,
            new WorldSettings(0L, packet.getGameMode(), false, this.client.world.getData().isHardcore(), packet.getGeneratorType()),
            packet.getDimensionId(),
            packet.getDifficulty(),
            this.client.profiler
         );
         this.world.setScoreboard(var2);
         this.client.setWorld(this.world);
         this.client.player.dimensionId = packet.getDimensionId();
         this.client.openScreen(new DownloadingTerrainScreen(this));
      }

      this.client.teleportToDimension(packet.getDimensionId());
      this.client.interactionManager.setGameMode(packet.getGameMode());
   }

   @Override
   public void handleExplosion(ExplosionS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Explosion var2 = new Explosion(this.client.world, null, packet.getX(), packet.getY(), packet.getZ(), packet.getPower(), packet.getDamagedBlocks());
      var2.damageBlocks(true);
      this.client.player.velocityX += (double)packet.getPlayerVelocityX();
      this.client.player.velocityY += (double)packet.getPlayerVelocityY();
      this.client.player.velocityZ += (double)packet.getPlayerVelocityZ();
   }

   @Override
   public void handleOpenMenu(OpenMenuS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      LocalClientPlayerEntity var2 = this.client.player;
      if ("minecraft:container".equals(packet.getMenuType())) {
         var2.openInventoryMenu(new SimpleInventory(packet.getDisplayName(), packet.getSize()));
         var2.menu.networkId = packet.getMenuId();
      } else if ("minecraft:villager".equals(packet.getMenuType())) {
         var2.openTraderMenu(new ClientTrader(var2, packet.getDisplayName()));
         var2.menu.networkId = packet.getMenuId();
      } else if ("EntityHorse".equals(packet.getMenuType())) {
         Entity var3 = this.world.getEntity(packet.getOwnerId());
         if (var3 instanceof HorseBaseEntity) {
            var2.openHorseMenu((HorseBaseEntity)var3, new AnimalInventory(packet.getDisplayName(), packet.getSize()));
            var2.menu.networkId = packet.getMenuId();
         }
      } else if (!packet.hasSize()) {
         var2.openMenu(new EmptyMenuProvider(packet.getMenuType(), packet.getDisplayName()));
         var2.menu.networkId = packet.getMenuId();
      } else {
         MenuInventory var4 = new MenuInventory(packet.getMenuType(), packet.getDisplayName(), packet.getSize());
         var2.openInventoryMenu(var4);
         var2.menu.networkId = packet.getMenuId();
      }
   }

   @Override
   public void handleMenuSlotUpdate(MenuSlotUpdateS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      LocalClientPlayerEntity var2 = this.client.player;
      if (packet.getMenuId() == -1) {
         var2.inventory.setCursorStack(packet.getStack());
      } else {
         boolean var3 = false;
         if (this.client.currentScreen instanceof CreativeInventoryScreen) {
            CreativeInventoryScreen var4 = (CreativeInventoryScreen)this.client.currentScreen;
            var3 = var4.getSelectedTab() != ItemGroup.INVENTORY.getId();
         }

         if (packet.getMenuId() == 0 && packet.getSlotId() >= 36 && packet.getSlotId() < 45) {
            ItemStack var5 = var2.playerMenu.getSlot(packet.getSlotId()).getStack();
            if (packet.getStack() != null && (var5 == null || var5.size < packet.getStack().size)) {
               packet.getStack().popAnimationTime = 5;
            }

            var2.playerMenu.setStack(packet.getSlotId(), packet.getStack());
         } else if (packet.getMenuId() == var2.menu.networkId && (packet.getMenuId() != 0 || !var3)) {
            var2.menu.setStack(packet.getSlotId(), packet.getStack());
         }
      }
   }

   @Override
   public void handleConfirmMenuAction(ConfirmMenuActionS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      InventoryMenu var2 = null;
      LocalClientPlayerEntity var3 = this.client.player;
      if (packet.getMenuId() == 0) {
         var2 = var3.playerMenu;
      } else if (packet.getMenuId() == var3.menu.networkId) {
         var2 = var3.menu;
      }

      if (var2 != null && !packet.getAccepted()) {
         this.sendPacket(new ConfirmMenuActionC2SPacket(packet.getMenuId(), packet.getActionId(), true));
      }
   }

   @Override
   public void handleInventoryMenu(InventoryMenuS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      LocalClientPlayerEntity var2 = this.client.player;
      if (packet.getMenuId() == 0) {
         var2.playerMenu.setStacks(packet.getStacks());
      } else if (packet.getMenuId() == var2.menu.networkId) {
         var2.menu.setStacks(packet.getStacks());
      }
   }

   @Override
   public void handleOpenSignEditor(OpenSignEditorS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Object var2 = this.world.getBlockEntity(packet.getPos());
      if (!(var2 instanceof SignBlockEntity)) {
         var2 = new SignBlockEntity();
         ((BlockEntity)var2).setWorld(this.world);
         ((BlockEntity)var2).setPos(packet.getPos());
      }

      this.client.player.openSignEditor((SignBlockEntity)var2);
   }

   @Override
   public void handleSignBlockEntityUpdate(SignBlockEntityUpdateS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      boolean var2 = false;
      if (this.client.world.isLoaded(packet.getPos())) {
         BlockEntity var3 = this.client.world.getBlockEntity(packet.getPos());
         if (var3 instanceof SignBlockEntity) {
            SignBlockEntity var4 = (SignBlockEntity)var3;
            if (var4.isEditable()) {
               System.arraycopy(packet.getLines(), 0, var4.lines, 0, 4);
               var4.markDirty();
            }

            var2 = true;
         }
      }

      if (!var2 && this.client.player != null) {
         this.client
            .player
            .sendMessage(new LiteralText("Unable to locate sign at " + packet.getPos().getX() + ", " + packet.getPos().getY() + ", " + packet.getPos().getZ()));
      }
   }

   @Override
   public void handleBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      if (this.client.world.isLoaded(packet.getPos())) {
         BlockEntity var2 = this.client.world.getBlockEntity(packet.getPos());
         int var3 = packet.getType();
         if (var3 == 1 && var2 instanceof MobSpawnerBlockEntity
            || var3 == 2 && var2 instanceof CommandBlockBlockEntity
            || var3 == 3 && var2 instanceof BeaconBlockEntity
            || var3 == 4 && var2 instanceof SkullBlockEntity
            || var3 == 5 && var2 instanceof FlowerPotBlockEntity
            || var3 == 6 && var2 instanceof BannerBlockEntity) {
            var2.readNbt(packet.getNbt());
         }
      }
   }

   @Override
   public void handleMenuData(MenuDataS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      LocalClientPlayerEntity var2 = this.client.player;
      if (var2.menu != null && var2.menu.networkId == packet.getMenuId()) {
         var2.menu.setData(packet.getDataId(), packet.getValue());
      }
   }

   @Override
   public void handleEntityEquipment(EntityEquipmentS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.getId());
      if (var2 != null) {
         var2.setEquipmentStack(packet.getEquipmentSlot(), packet.getStack());
      }
   }

   @Override
   public void handleCloseMenu(CloseMenuS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.player.doCloseMenu();
   }

   @Override
   public void handleBlockEvent(BlockEventS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.world.addBlockEvent(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
   }

   @Override
   public void handleBlockMiningProgress(BlockMiningProgressS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.world.updateBlockMiningProgress(packet.getId(), packet.getPos(), packet.getProgress());
   }

   @Override
   public void handleWorldChunks(WorldChunksS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);

      for(int var2 = 0; var2 < packet.getChunkCount(); ++var2) {
         int var3 = packet.getChunkX(var2);
         int var4 = packet.getChunkZ(var2);
         this.world.updateChunk(var3, var4, true);
         this.world.regionChanged(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
         WorldChunk var5 = this.world.getChunkAt(var3, var4);
         var5.set(packet.getRawChunkData(var2), packet.getSectionsWithData(var2), true);
         this.world.onRegionChanged(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
         if (!(this.world.dimension instanceof OverworldDimension)) {
            var5.clearLightChecks();
         }
      }
   }

   @Override
   public void handleGameEvent(GameEventS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      LocalClientPlayerEntity var2 = this.client.player;
      int var3 = packet.getEvent();
      float var4 = packet.getData();
      int var5 = MathHelper.floor(var4 + 0.5F);
      if (var3 >= 0 && var3 < GameEventS2CPacket.EVENT_MESSAGES.length && GameEventS2CPacket.EVENT_MESSAGES[var3] != null) {
         var2.addMessage(new TranslatableText(GameEventS2CPacket.EVENT_MESSAGES[var3]));
      }

      if (var3 == 1) {
         this.world.getData().setRaining(true);
         this.world.setRain(0.0F);
      } else if (var3 == 2) {
         this.world.getData().setRaining(false);
         this.world.setRain(1.0F);
      } else if (var3 == 3) {
         this.client.interactionManager.setGameMode(WorldSettings.GameMode.byIndex(var5));
      } else if (var3 == 4) {
         this.client.openScreen(new CreditsScreen());
      } else if (var3 == 5) {
         GameOptions var6 = this.client.options;
         if (var4 == 0.0F) {
            this.client.openScreen(new DemoScreen());
         } else if (var4 == 101.0F) {
            this.client
               .gui
               .getChat()
               .addMessage(
                  new TranslatableText(
                     "demo.help.movement",
                     GameOptions.getKeyName(var6.forwardKey.getKeyCode()),
                     GameOptions.getKeyName(var6.leftKey.getKeyCode()),
                     GameOptions.getKeyName(var6.backKey.getKeyCode()),
                     GameOptions.getKeyName(var6.rightKey.getKeyCode())
                  )
               );
         } else if (var4 == 102.0F) {
            this.client.gui.getChat().addMessage(new TranslatableText("demo.help.jump", GameOptions.getKeyName(var6.jumpKey.getKeyCode())));
         } else if (var4 == 103.0F) {
            this.client.gui.getChat().addMessage(new TranslatableText("demo.help.inventory", GameOptions.getKeyName(var6.inventoryKey.getKeyCode())));
         }
      } else if (var3 == 6) {
         this.world.playSound(var2.x, var2.y + (double)var2.getEyeHeight(), var2.z, "random.successful_hit", 0.18F, 0.45F, false);
      } else if (var3 == 7) {
         this.world.setRain(var4);
      } else if (var3 == 8) {
         this.world.setThunder(var4);
      } else if (var3 == 10) {
         this.world.addParticle(ParticleType.MOB_APPEARANCE, var2.x, var2.y, var2.z, 0.0, 0.0, 0.0, new int[0]);
         this.world.playSound(var2.x, var2.y, var2.z, "mob.guardian.curse", 1.0F, 1.0F, false);
      }
   }

   @Override
   public void handleMapData(MapDataS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      SavedMapData var2 = FilledMapItem.getMapData(packet.getId(), this.client.world);
      packet.apply(var2);
      this.client.gameRenderer.getMapRenderer().updateTexture(var2);
   }

   @Override
   public void handleWorldEvent(WorldEventS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      if (packet.isGlobal()) {
         this.client.world.doGlobalEvent(packet.getEvent(), packet.getPos(), packet.getData());
      } else {
         this.client.world.doEvent(packet.getEvent(), packet.getPos(), packet.getData());
      }
   }

   @Override
   public void handleStatistics(StatisticsS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      boolean var2 = false;

      for(Entry var4 : packet.getStats().entrySet()) {
         Stat var5 = (Stat)var4.getKey();
         int var6 = var4.getValue();
         if (var5.isAchievement() && var6 > 0) {
            if (this.hasAchievements && this.client.player.getStatHandler().getValue(var5) == 0) {
               AchievementStat var7 = (AchievementStat)var5;
               this.client.toast.set(var7);
               this.client.getTwitchStream().m_27hgmbctc(new AchievementMetadata(var7), 0L);
               if (var5 == Achievements.OPEN_INVENTORY) {
                  this.client.options.showInventoryAchievementHint = false;
                  this.client.options.save();
               }
            }

            var2 = true;
         }

         this.client.player.getStatHandler().setValue(this.client.player, var5, var6);
      }

      if (!this.hasAchievements && !var2 && this.client.options.showInventoryAchievementHint) {
         this.client.toast.setTutorial(Achievements.OPEN_INVENTORY);
      }

      this.hasAchievements = true;
      if (this.client.currentScreen instanceof StatsListener) {
         ((StatsListener)this.client.currentScreen).onStatsReady();
      }
   }

   @Override
   public void handleEntityStatusEffect(EntityStatusEffectS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.getId());
      if (var2 instanceof LivingEntity) {
         StatusEffectInstance var3 = new StatusEffectInstance(packet.getEffect(), packet.getDuration(), packet.getAmplifier(), false, packet.getParticles());
         var3.setPermanent(packet.isPermanent());
         ((LivingEntity)var2).addStatusEffect(var3);
      }
   }

   @Override
   public void handlePlayerCombat(PlayerCombatS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.killerId);
      LivingEntity var3 = var2 instanceof LivingEntity ? (LivingEntity)var2 : null;
      if (packet.event == PlayerCombatS2CPacket.Event.END_COMBAT) {
         long var4 = (long)(1000 * packet.duration / 20);
         PlayerCombatMetadata var6 = new PlayerCombatMetadata(this.client.player, var3);
         this.client.getTwitchStream().m_75vuvosit(var6, 0L - var4, 0L);
      } else if (packet.event == PlayerCombatS2CPacket.Event.ENTITY_DIED) {
         Entity var7 = this.world.getEntity(packet.playerId);
         if (var7 instanceof PlayerEntity) {
            PlayerDeathMetadata var5 = new PlayerDeathMetadata((PlayerEntity)var7, var3);
            var5.setMessage(packet.message);
            this.client.getTwitchStream().m_27hgmbctc(var5, 0L);
         }
      }
   }

   @Override
   public void handleDifficulty(DifficultyS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.world.getData().setDifficulty(packet.getDifficulty());
      this.client.world.getData().setDifficultyLocked(packet.getLocked());
   }

   @Override
   public void handleCamera(CameraS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = packet.getCamera(this.world);
      if (var2 != null) {
         this.client.setCamera(var2);
      }
   }

   @Override
   public void handleWorldBorder(WorldBorderS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      packet.apply(this.world.getWorldBorder());
   }

   @Override
   public void handleTitles(TitlesS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      TitlesS2CPacket.Type var2 = packet.getType();
      String var3 = null;
      String var4 = null;
      String var5 = packet.getText() != null ? packet.getText().buildFormattedString() : "";
      switch(var2) {
         case TITLE:
            var3 = var5;
            break;
         case SUBTITLE:
            var4 = var5;
            break;
         case RESET:
            this.client.gui.setTitles("", "", -1, -1, -1);
            this.client.gui.resetTitleTimes();
            return;
      }

      this.client.gui.setTitles(var3, var4, packet.getFadeIn(), packet.getDuration(), packet.getFadeOut());
   }

   @Override
   public void handleCompressionThreshold(CompressionThresholdS2CPacket packet) {
      if (!this.connection.isLocal()) {
         this.connection.setCompressionThreshold(packet.getCompressionThreshold());
      }
   }

   @Override
   public void handleTabList(TabListS2CPacket packet) {
      this.client.gui.getPlayerTabOverlay().setFooter(packet.getHeader().buildFormattedString().length() == 0 ? null : packet.getHeader());
      this.client.gui.getPlayerTabOverlay().setHeader(packet.getFooter().buildFormattedString().length() == 0 ? null : packet.getFooter());
   }

   @Override
   public void handleEntityRemoveStatusEffect(EntityRemoveStatusEffectS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.getId());
      if (var2 instanceof LivingEntity) {
         ((LivingEntity)var2).removeEffect(packet.getEffect());
      }
   }

   @Override
   public void handlePlayerInfo(PlayerInfoS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);

      for(PlayerInfoS2CPacket.Entry var3 : packet.getEntries()) {
         if (packet.getAction() == PlayerInfoS2CPacket.Action.REMOVE_PLAYER) {
            this.onlinePlayers.remove(var3.getProfile().getId());
         } else {
            PlayerInfo var4 = (PlayerInfo)this.onlinePlayers.get(var3.getProfile().getId());
            if (packet.getAction() == PlayerInfoS2CPacket.Action.ADD_PLAYER) {
               var4 = new PlayerInfo(var3);
               this.onlinePlayers.put(var4.getProfile().getId(), var4);
            }

            if (var4 != null) {
               switch(packet.getAction()) {
                  case ADD_PLAYER:
                     var4.setGameMode(var3.getGameMode());
                     var4.setPing(var3.getPing());
                     break;
                  case UPDATE_GAME_MODE:
                     var4.setGameMode(var3.getGameMode());
                     break;
                  case UPDATE_PING:
                     var4.setPing(var3.getPing());
                     break;
                  case UPDATE_DISPLAY_NAME:
                     var4.setDisplayName(var3.getDisplayName());
               }
            }
         }
      }
   }

   @Override
   public void handleKeepAlive(KeepAliveS2CPacket packet) {
      this.sendPacket(new KeepAliveC2SPacket(packet.getTimeMillis()));
   }

   @Override
   public void handlePlayerAbilities(PlayerAbilitiesS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      LocalClientPlayerEntity var2 = this.client.player;
      var2.abilities.flying = packet.isFlying();
      var2.abilities.creativeMode = packet.isCreativeMode();
      var2.abilities.invulnerable = packet.isInvulnerable();
      var2.abilities.canFly = packet.allowsFlying();
      var2.abilities.setFlySpeed(packet.getFlySpeed());
      var2.abilities.setWalkSpeed(packet.getWalkSpeed());
   }

   @Override
   public void handleCommandSuggestions(CommandSuggestionsS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      String[] var2 = packet.getSuggestions();
      if (this.client.currentScreen instanceof ChatScreen) {
         ChatScreen var3 = (ChatScreen)this.client.currentScreen;
         var3.setMessageHistory(var2);
      }
   }

   @Override
   public void handleSoundEvent(SoundEventS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      this.client.world.playSound(packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), packet.getVolume(), packet.getPitch(), false);
   }

   @Override
   public void handleCustomPayload(CustomPayloadS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      if ("MC|TrList".equals(packet.getChannel())) {
         ByteBuf var2 = Unpooled.wrappedBuffer(packet.getData());

         try {
            int var3 = var2.readInt();
            Screen var4 = this.client.currentScreen;
            if (var4 != null && var4 instanceof VillagerScreen && var3 == this.client.player.menu.networkId) {
               Trader var5 = ((VillagerScreen)var4).getTrader();
               TradeOffers var6 = TradeOffers.deserialize(new PacketByteBuf(var2));
               var5.setOffers(var6);
            }
         } catch (IOException var10) {
            LOGGER.error("Couldn't load trade info", var10);
         } finally {
            var2.release();
         }
      } else if ("MC|Brand".equals(packet.getChannel())) {
         this.client.player.setServerBrand(new String(packet.getData(), Charsets.UTF_8));
      } else if ("MC|BOpen".equals(packet.getChannel())) {
         ItemStack var12 = this.client.player.getMainHandStack();
         if (var12 != null && var12.getItem() == Items.WRITTEN_BOOK) {
            this.client.openScreen(new BookEditScreen(this.client.player, var12, false));
         }
      } else if ("MC|RPack".equals(packet.getChannel())) {
         final String var13 = new String(packet.getData(), Charsets.UTF_8);
         if (var13.startsWith("level://")) {
            String var14 = var13.substring("level://".length());
            File var15 = new File(this.client.runDir, "saves");
            File var16 = new File(var15, var14);
            if (var16.isFile()) {
               this.client.getResourcePackLoader().m_56btiyfcu(var16, false);
            }

            return;
         }

         if (this.client.getCurrentServerEntry() != null
            && this.client.getCurrentServerEntry().getResourcePackStatus() == ServerListEntry.ResourcePackStatus.ENABLED) {
            this.client.getResourcePackLoader().downloadServerResourcePack(var13);
         } else if (this.client.getCurrentServerEntry() == null
            || this.client.getCurrentServerEntry().getResourcePackStatus() == ServerListEntry.ResourcePackStatus.PROMPT) {
            this.client.openScreen(new ConfirmScreen(new ConfirmationListener() {
               @Override
               public void confirmResult(boolean result, int id) {
                  ClientPlayNetworkHandler.this.client = MinecraftClient.getInstance();
                  if (ClientPlayNetworkHandler.this.client.getCurrentServerEntry() != null) {
                     ClientPlayNetworkHandler.this.client.getCurrentServerEntry().setResourcePackStatus(ServerListEntry.ResourcePackStatus.ENABLED);
                     ServerList.update(ClientPlayNetworkHandler.this.client.getCurrentServerEntry());
                  }

                  if (result) {
                     ClientPlayNetworkHandler.this.client.getResourcePackLoader().downloadServerResourcePack(var13);
                  }

                  ClientPlayNetworkHandler.this.client.openScreen(null);
               }
            }, I18n.translate("multiplayer.texturePrompt.line1"), I18n.translate("multiplayer.texturePrompt.line2"), 0));
         }
      }
   }

   @Override
   public void handleScoreboardObjective(ScoreboardObjectiveS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Scoreboard var2 = this.world.getScoreboard();
      if (packet.getAction() == 0) {
         ScoreboardObjective var3 = var2.createObjective(packet.getName(), ScoreboardCriterion.DUMMY);
         var3.setDisplayName(packet.getDisplayName());
         var3.setRenderType(packet.getRenderType());
      } else {
         ScoreboardObjective var4 = var2.getObjective(packet.getName());
         if (packet.getAction() == 1) {
            var2.removeObjective(var4);
         } else if (packet.getAction() == 2) {
            var4.setDisplayName(packet.getDisplayName());
            var4.setRenderType(packet.getRenderType());
         }
      }
   }

   @Override
   public void handleScoreboardScore(ScoreboardScoreS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Scoreboard var2 = this.world.getScoreboard();
      ScoreboardObjective var3 = var2.getObjective(packet.getObjective());
      if (packet.getAction() == ScoreboardScoreS2CPacket.Action.CHANGE) {
         ScoreboardScore var4 = var2.getScore(packet.getOwner(), var3);
         var4.set(packet.getScore());
      } else if (packet.getAction() == ScoreboardScoreS2CPacket.Action.REMOVE) {
         if (StringUtils.isStringEmpty(packet.getObjective())) {
            var2.removeScore(packet.getOwner(), null);
         } else if (var3 != null) {
            var2.removeScore(packet.getOwner(), var3);
         }
      }
   }

   @Override
   public void handleScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Scoreboard var2 = this.world.getScoreboard();
      if (packet.getObjective().length() == 0) {
         var2.setDisplayObjective(packet.getSlot(), null);
      } else {
         ScoreboardObjective var3 = var2.getObjective(packet.getObjective());
         var2.setDisplayObjective(packet.getSlot(), var3);
      }
   }

   @Override
   public void handleTeam(TeamS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Scoreboard var2 = this.world.getScoreboard();
      Team var3;
      if (packet.getAction() == 0) {
         var3 = var2.addTeam(packet.getName());
      } else {
         var3 = var2.getTeam(packet.getName());
      }

      if (packet.getAction() == 0 || packet.getAction() == 2) {
         var3.setDisplayName(packet.getDisplayName());
         var3.setPrefix(packet.getPrefix());
         var3.setSuffix(packet.getSuffix());
         var3.setColor(Formatting.byIndex(packet.getColor()));
         var3.unpackFriendlyFlags(packet.getFlags());
         AbstractTeam.Visibility var4 = AbstractTeam.Visibility.byName(packet.getNameTagVisibility());
         if (var4 != null) {
            var3.setNameTagVisibility(var4);
         }
      }

      if (packet.getAction() == 0 || packet.getAction() == 3) {
         for(String var5 : packet.getMembers()) {
            var2.addMemberToTeam(var5, packet.getName());
         }
      }

      if (packet.getAction() == 4) {
         for(String var8 : packet.getMembers()) {
            var2.removeMemberFromTeam(var8, var3);
         }
      }

      if (packet.getAction() == 1) {
         var2.removeTeam(var3);
      }
   }

   @Override
   public void handleParticle(ParticleS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      if (packet.getCount() == 0) {
         double var2 = (double)(packet.getVelocityScale() * packet.getVelocityX());
         double var4 = (double)(packet.getVelocityScale() * packet.getVelocityY());
         double var6 = (double)(packet.getVelocityScale() * packet.getVelocityZ());

         try {
            this.world
               .addParticle(packet.getType(), packet.getIgnoreDistance(), packet.getX(), packet.getY(), packet.getZ(), var2, var4, var6, packet.getParameters());
         } catch (Throwable var17) {
            LOGGER.warn("Could not spawn particle effect " + packet.getType());
         }
      } else {
         for(int var18 = 0; var18 < packet.getCount(); ++var18) {
            double var3 = this.random.nextGaussian() * (double)packet.getVelocityX();
            double var5 = this.random.nextGaussian() * (double)packet.getVelocityY();
            double var7 = this.random.nextGaussian() * (double)packet.getVelocityZ();
            double var9 = this.random.nextGaussian() * (double)packet.getVelocityScale();
            double var11 = this.random.nextGaussian() * (double)packet.getVelocityScale();
            double var13 = this.random.nextGaussian() * (double)packet.getVelocityScale();

            try {
               this.world
                  .addParticle(
                     packet.getType(),
                     packet.getIgnoreDistance(),
                     packet.getX() + var3,
                     packet.getY() + var5,
                     packet.getZ() + var7,
                     var9,
                     var11,
                     var13,
                     packet.getParameters()
                  );
            } catch (Throwable var16) {
               LOGGER.warn("Could not spawn particle effect " + packet.getType());
               return;
            }
         }
      }
   }

   @Override
   public void handleEntityAttributes(EntityAttributesS2CPacket packet) {
      PacketUtils.ensureOnSameThread(packet, this, this.client);
      Entity var2 = this.world.getEntity(packet.getId());
      if (var2 != null) {
         if (!(var2 instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
         } else {
            AbstractEntityAttributeContainer var3 = ((LivingEntity)var2).getAttributes();

            for(EntityAttributesS2CPacket.Entry var5 : packet.getEntries()) {
               IEntityAttributeInstance var6 = var3.get(var5.getId());
               if (var6 == null) {
                  var6 = var3.registerAttribute(new ClampedEntityAttribute(null, var5.getId(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
               }

               var6.setBase(var5.getBaseValue());
               var6.clearModifiers();

               for(AttributeModifier var8 : var5.getModifiers()) {
                  var6.addModifier(var8);
               }
            }
         }
      }
   }

   public Connection getConnection() {
      return this.connection;
   }

   public Collection getOnlinePlayers() {
      return this.onlinePlayers.values();
   }

   public PlayerInfo getOnlinePlayer(UUID uuid) {
      return (PlayerInfo)this.onlinePlayers.get(uuid);
   }

   public PlayerInfo getOnlinePlayer(String name) {
      for(PlayerInfo var3 : this.onlinePlayers.values()) {
         if (var3.getProfile().getName().equals(name)) {
            return var3;
         }
      }

      return null;
   }

   public GameProfile getProfile() {
      return this.profile;
   }
}
