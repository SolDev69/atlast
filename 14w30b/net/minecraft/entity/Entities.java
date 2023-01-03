package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.living.mob.GhastEntity;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.entity.living.mob.MagmaCubeEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.SlimeEntity;
import net.minecraft.entity.living.mob.SnowGolemEntity;
import net.minecraft.entity.living.mob.ambient.BatEntity;
import net.minecraft.entity.living.mob.hostile.BlazeEntity;
import net.minecraft.entity.living.mob.hostile.CaveSpiderEntity;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;
import net.minecraft.entity.living.mob.hostile.EndermanEntity;
import net.minecraft.entity.living.mob.hostile.EndermiteEntity;
import net.minecraft.entity.living.mob.hostile.GiantEntity;
import net.minecraft.entity.living.mob.hostile.GuardianEntity;
import net.minecraft.entity.living.mob.hostile.HostileEntity;
import net.minecraft.entity.living.mob.hostile.SkeletonEntity;
import net.minecraft.entity.living.mob.hostile.SliverfishEntity;
import net.minecraft.entity.living.mob.hostile.SpiderEntity;
import net.minecraft.entity.living.mob.hostile.WitchEntity;
import net.minecraft.entity.living.mob.hostile.ZombieEntity;
import net.minecraft.entity.living.mob.hostile.ZombiePigmanEntity;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragonEntity;
import net.minecraft.entity.living.mob.hostile.boss.WitherEntity;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.entity.living.mob.passive.animal.ChickenEntity;
import net.minecraft.entity.living.mob.passive.animal.CowEntity;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.living.mob.passive.animal.MooshroomEntity;
import net.minecraft.entity.living.mob.passive.animal.PigEntity;
import net.minecraft.entity.living.mob.passive.animal.RabbitEntity;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.OcelotEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.entity.living.mob.water.SquidEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.RideableMinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Entities {
   private static final Logger LOGGER = LogManager.getLogger();
   private static Map ID_TO_TYPE = Maps.newHashMap();
   private static Map TYPE_TO_ID = Maps.newHashMap();
   private static Map RAW_ID_TO_TYPE = Maps.newHashMap();
   private static Map TYPE_TO_RAW_ID = Maps.newHashMap();
   private static Map ID_TO_RAW_ID = Maps.newHashMap();
   public static Map RAW_ID_TO_SPAWN_EGG_DATA = Maps.newLinkedHashMap();

   private static void register(Class type, String id, int rawId) {
      if (ID_TO_TYPE.containsKey(id)) {
         throw new IllegalArgumentException("ID is already registered: " + id);
      } else if (RAW_ID_TO_TYPE.containsKey(rawId)) {
         throw new IllegalArgumentException("ID is already registered: " + rawId);
      } else {
         ID_TO_TYPE.put(id, type);
         TYPE_TO_ID.put(type, id);
         RAW_ID_TO_TYPE.put(rawId, type);
         TYPE_TO_RAW_ID.put(type, rawId);
         ID_TO_RAW_ID.put(id, rawId);
      }
   }

   private static void registerWithSpawnEgg(Class type, String id, int rawId, int baseColor, int spotsColor) {
      register(type, id, rawId);
      RAW_ID_TO_SPAWN_EGG_DATA.put(rawId, new Entities.SpawnEggData(rawId, baseColor, spotsColor));
   }

   public static Entity createSilently(String id, World world) {
      Entity var2 = null;

      try {
         Class var3 = (Class)ID_TO_TYPE.get(id);
         if (var3 != null) {
            var2 = (Entity)var3.getConstructor(World.class).newInstance(world);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return var2;
   }

   public static Entity create(NbtCompound nbt, World world) {
      Entity var2 = null;
      if ("Minecart".equals(nbt.getString("id"))) {
         nbt.putString("id", MinecartEntity.Type.byIndex(nbt.getInt("Type")).getName());
         nbt.remove("Type");
      }

      try {
         Class var3 = (Class)ID_TO_TYPE.get(nbt.getString("id"));
         if (var3 != null) {
            var2 = (Entity)var3.getConstructor(World.class).newInstance(world);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      if (var2 != null) {
         var2.readEntityNbt(nbt);
      } else {
         LOGGER.warn("Skipping Entity with id " + nbt.getString("id"));
      }

      return var2;
   }

   public static Entity create(int rawId, World world) {
      Entity var2 = null;

      try {
         Class var3 = getType(rawId);
         if (var3 != null) {
            var2 = (Entity)var3.getConstructor(World.class).newInstance(world);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      if (var2 == null) {
         LOGGER.warn("Skipping Entity with id " + rawId);
      }

      return var2;
   }

   public static int getRawId(Entity entity) {
      Class var1 = entity.getClass();
      return TYPE_TO_RAW_ID.containsKey(var1) ? TYPE_TO_RAW_ID.get(var1) : 0;
   }

   public static Class getType(int rawId) {
      return (Class)RAW_ID_TO_TYPE.get(rawId);
   }

   public static String getId(Entity entity) {
      return (String)TYPE_TO_ID.get(entity.getClass());
   }

   @Environment(EnvType.CLIENT)
   public static int getRawId(String id) {
      Integer var1 = (Integer)ID_TO_RAW_ID.get(id);
      return var1 == null ? 90 : var1;
   }

   public static String getId(int rawId) {
      Class var1 = getType(rawId);
      return var1 != null ? (String)TYPE_TO_ID.get(var1) : null;
   }

   public static void load() {
   }

   public static List getIds() {
      Set var0 = ID_TO_TYPE.keySet();
      ArrayList var1 = Lists.newArrayList();

      for(String var3 : var0) {
         Class var4 = (Class)ID_TO_TYPE.get(var3);
         if ((var4.getModifiers() & 1024) != 1024) {
            var1.add(var3);
         }
      }

      return var1;
   }

   static {
      register(ItemEntity.class, "Item", 1);
      register(XpOrbEntity.class, "XPOrb", 2);
      register(LeadKnotEntity.class, "LeashKnot", 8);
      register(PaintingEntity.class, "Painting", 9);
      register(ArrowEntity.class, "Arrow", 10);
      register(SnowballEntity.class, "Snowball", 11);
      register(FireballEntity.class, "Fireball", 12);
      register(SmallFireballEntity.class, "SmallFireball", 13);
      register(EnderPearlEntity.class, "ThrownEnderpearl", 14);
      register(EnderEyeEntity.class, "EyeOfEnderSignal", 15);
      register(PotionEntity.class, "ThrownPotion", 16);
      register(ExperienceBottleEntity.class, "ThrownExpBottle", 17);
      register(ItemFrameEntity.class, "ItemFrame", 18);
      register(WitherSkullEntity.class, "WitherSkull", 19);
      register(PrimedTntEntity.class, "PrimedTnt", 20);
      register(FallingBlockEntity.class, "FallingSand", 21);
      register(FireworksEntity.class, "FireworksRocketEntity", 22);
      register(BoatEntity.class, "Boat", 41);
      register(RideableMinecartEntity.class, MinecartEntity.Type.RIDEABLE.getName(), 42);
      register(ChestMinecartEntity.class, MinecartEntity.Type.CHEST.getName(), 43);
      register(FurnaceMinecartEntity.class, MinecartEntity.Type.FURNACE.getName(), 44);
      register(TntMinecartEntity.class, MinecartEntity.Type.TNT.getName(), 45);
      register(HopperMinecartEntity.class, MinecartEntity.Type.HOPPER.getName(), 46);
      register(SpawnerMinecartEntity.class, MinecartEntity.Type.SPAWNER.getName(), 47);
      register(CommandBlockMinecartEntity.class, MinecartEntity.Type.COMMAND_BLOCK.getName(), 40);
      register(MobEntity.class, "Mob", 48);
      register(HostileEntity.class, "Monster", 49);
      registerWithSpawnEgg(CreeperEntity.class, "Creeper", 50, 894731, 0);
      registerWithSpawnEgg(SkeletonEntity.class, "Skeleton", 51, 12698049, 4802889);
      registerWithSpawnEgg(SpiderEntity.class, "Spider", 52, 3419431, 11013646);
      register(GiantEntity.class, "Giant", 53);
      registerWithSpawnEgg(ZombieEntity.class, "Zombie", 54, 44975, 7969893);
      registerWithSpawnEgg(SlimeEntity.class, "Slime", 55, 5349438, 8306542);
      registerWithSpawnEgg(GhastEntity.class, "Ghast", 56, 16382457, 12369084);
      registerWithSpawnEgg(ZombiePigmanEntity.class, "PigZombie", 57, 15373203, 5009705);
      registerWithSpawnEgg(EndermanEntity.class, "Enderman", 58, 1447446, 0);
      registerWithSpawnEgg(CaveSpiderEntity.class, "CaveSpider", 59, 803406, 11013646);
      registerWithSpawnEgg(SliverfishEntity.class, "Silverfish", 60, 7237230, 3158064);
      registerWithSpawnEgg(BlazeEntity.class, "Blaze", 61, 16167425, 16775294);
      registerWithSpawnEgg(MagmaCubeEntity.class, "LavaSlime", 62, 3407872, 16579584);
      register(EnderDragonEntity.class, "EnderDragon", 63);
      register(WitherEntity.class, "WitherBoss", 64);
      registerWithSpawnEgg(BatEntity.class, "Bat", 65, 4996656, 986895);
      registerWithSpawnEgg(WitchEntity.class, "Witch", 66, 3407872, 5349438);
      registerWithSpawnEgg(EndermiteEntity.class, "Endermite", 67, 1447446, 7237230);
      registerWithSpawnEgg(GuardianEntity.class, "Guardian", 68, 5931634, 15826224);
      registerWithSpawnEgg(PigEntity.class, "Pig", 90, 15771042, 14377823);
      registerWithSpawnEgg(SheepEntity.class, "Sheep", 91, 15198183, 16758197);
      registerWithSpawnEgg(CowEntity.class, "Cow", 92, 4470310, 10592673);
      registerWithSpawnEgg(ChickenEntity.class, "Chicken", 93, 10592673, 16711680);
      registerWithSpawnEgg(SquidEntity.class, "Squid", 94, 2243405, 7375001);
      registerWithSpawnEgg(WolfEntity.class, "Wolf", 95, 14144467, 13545366);
      registerWithSpawnEgg(MooshroomEntity.class, "MushroomCow", 96, 10489616, 12040119);
      register(SnowGolemEntity.class, "SnowMan", 97);
      registerWithSpawnEgg(OcelotEntity.class, "Ozelot", 98, 15720061, 5653556);
      register(IronGolemEntity.class, "VillagerGolem", 99);
      registerWithSpawnEgg(HorseBaseEntity.class, "EntityHorse", 100, 12623485, 15656192);
      registerWithSpawnEgg(RabbitEntity.class, "Rabbit", 101, 10051392, 7555121);
      registerWithSpawnEgg(VillagerEntity.class, "Villager", 120, 5651507, 12422002);
      register(EnderCrystalEntity.class, "EnderCrystal", 200);
   }

   public static class SpawnEggData {
      public final int id;
      public final int baseColor;
      public final int spotsColor;
      public final Stat killEntityStat;
      public final Stat entityKilledByStat;

      public SpawnEggData(int id, int baseColor, int spotsColor) {
         this.id = id;
         this.baseColor = baseColor;
         this.spotsColor = spotsColor;
         this.killEntityStat = Stats.createEntityKillStat(this);
         this.entityKilledByStat = Stats.createKilledByEntityStat(this);
      }
   }
}
