package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.locale.I18n;
import net.minecraft.resource.Identifier;

public abstract class Enchantment {
   private static final Enchantment[] BY_RAW_ID = new Enchantment[256];
   public static final Enchantment[] ALL;
   private static final Map BY_ID = Maps.newHashMap();
   public static final Enchantment PROTECTION = new ProtectionEnchantment(0, new Identifier("protection"), 10, 0);
   public static final Enchantment FIRE_PROTECTION = new ProtectionEnchantment(1, new Identifier("fire_protection"), 5, 1);
   public static final Enchantment FEATHER_FALLING = new ProtectionEnchantment(2, new Identifier("feather_falling"), 5, 2);
   public static final Enchantment BLAST_PROTECTION = new ProtectionEnchantment(3, new Identifier("blast_protection"), 2, 3);
   public static final Enchantment PROJECTILE_PROTECTION = new ProtectionEnchantment(4, new Identifier("projectile_protection"), 5, 4);
   public static final Enchantment RESPIRATION = new RespirationEnchantment(5, new Identifier("respiration"), 2);
   public static final Enchantment AQUA_AFFINITY = new AquaAffinityEnchantment(6, new Identifier("aqua_affinity"), 2);
   public static final Enchantment THORNS = new ThornsEnchantment(7, new Identifier("thorns"), 1);
   public static final Enchantment DEPTH_STRIDER = new DepthStriderEnchantment(8, new Identifier("depth_strider"), 2);
   public static final Enchantment SHARPNESS = new DamageEnchantment(16, new Identifier("sharpness"), 10, 0);
   public static final Enchantment SMITE = new DamageEnchantment(17, new Identifier("smite"), 5, 1);
   public static final Enchantment BANE_OF_ARTHROPODS = new DamageEnchantment(18, new Identifier("bane_of_arthropods"), 5, 2);
   public static final Enchantment KNOCKBACK = new KnockbackEnchantment(19, new Identifier("knockback"), 5);
   public static final Enchantment FIRE_ASPECT = new FireAspectEnchantment(20, new Identifier("fire_aspect"), 2);
   public static final Enchantment LOOTING = new BetterLootEnchantment(21, new Identifier("looting"), 2, EnchantmentTarget.WEAPON);
   public static final Enchantment EFFICIENCY = new EfficiencyEnchantment(32, new Identifier("efficiency"), 10);
   public static final Enchantment SILK_TOUCH = new SilkTouchEnchantment(33, new Identifier("silk_touch"), 1);
   public static final Enchantment UNBREAKING = new UnbreakingEnchantment(34, new Identifier("unbreaking"), 5);
   public static final Enchantment FORTUNE = new BetterLootEnchantment(35, new Identifier("fortune"), 2, EnchantmentTarget.DIGGER);
   public static final Enchantment POWER = new PowerEnchantment(48, new Identifier("power"), 10);
   public static final Enchantment PUNCH = new PunchEnchantment(49, new Identifier("punch"), 2);
   public static final Enchantment FLAME = new FlameEnchantment(50, new Identifier("flame"), 2);
   public static final Enchantment INFINITY = new InfinityEnchantment(51, new Identifier("infinity"), 1);
   public static final Enchantment LUCK_OF_THE_SEA = new BetterLootEnchantment(61, new Identifier("luck_of_the_sea"), 2, EnchantmentTarget.FISHING_ROD);
   public static final Enchantment LURE = new LureEnchantment(62, new Identifier("lure"), 2, EnchantmentTarget.FISHING_ROD);
   public final int id;
   private final int type;
   public EnchantmentTarget target;
   protected String name;

   public static Enchantment byRawId(int id) {
      return id >= 0 && id < BY_RAW_ID.length ? BY_RAW_ID[id] : null;
   }

   protected Enchantment(int rawId, Identifier id, int type, EnchantmentTarget target) {
      this.id = rawId;
      this.type = type;
      this.target = target;
      if (BY_RAW_ID[rawId] != null) {
         throw new IllegalArgumentException("Duplicate enchantment id!");
      } else {
         BY_RAW_ID[rawId] = this;
         BY_ID.put(id, this);
      }
   }

   public static Enchantment byId(String id) {
      return (Enchantment)BY_ID.get(new Identifier(id));
   }

   public static String[] m_04ntpdulz() {
      String[] var0 = new String[BY_ID.size()];
      int var1 = 0;

      for(Identifier var3 : BY_ID.keySet()) {
         var0[var1++] = var3.toString();
      }

      return var0;
   }

   public int getType() {
      return this.type;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return 1;
   }

   public int getMinXpRequirement(int level) {
      return 1 + level * 10;
   }

   public int getMaxXpRequirement(int level) {
      return this.getMinXpRequirement(level) + 5;
   }

   public int getExtraProtection(int level, DamageSource source) {
      return 0;
   }

   public float getExtraDamage(int level, LivingEntityType entity) {
      return 0.0F;
   }

   public boolean checkCompatibility(Enchantment other) {
      return this != other;
   }

   public Enchantment setName(String name) {
      this.name = name;
      return this;
   }

   public String getName() {
      return "enchantment." + this.name;
   }

   public String getDisplayName(int level) {
      String var2 = I18n.translate(this.getName());
      return var2 + " " + I18n.translate("enchantment.level." + level);
   }

   public boolean isValidTarget(ItemStack stack) {
      return this.target.matches(stack.getItem());
   }

   public void applyDamageWildcard(LivingEntity attacker, Entity target, int level) {
   }

   public void applyProtectionWildcard(LivingEntity target, Entity attacker, int level) {
   }

   static {
      ArrayList var0 = Lists.newArrayList();

      for(Enchantment var4 : BY_RAW_ID) {
         if (var4 != null) {
            var0.add(var4);
         }
      }

      ALL = var0.toArray(new Enchantment[var0.size()]);
   }
}
