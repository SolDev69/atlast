package net.minecraft.entity.living.mob.passive;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.C_54zeovuss;
import net.minecraft.C_71bgxuxfc;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEntry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowGolemGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.GoToEntityGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.LongDoorInteractGoal;
import net.minecraft.entity.ai.goal.LookAtCustomerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RestrictOpenDoorGoal;
import net.minecraft.entity.ai.goal.StayIndoorsGoal;
import net.minecraft.entity.ai.goal.StopFollowingCustomerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.VillagerMatingGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.Monster;
import net.minecraft.entity.living.mob.hostile.WitchEntity;
import net.minecraft.entity.living.mob.hostile.ZombieEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.village.Village;
import net.minecraft.world.village.trade.TradeOffer;
import net.minecraft.world.village.trade.TradeOffers;
import net.ornithemc.api.EnvType;

public class VillagerEntity extends PassiveEntity implements Tradable, Trader {
   private int updateVillageCooldown;
   private boolean mating;
   private boolean inCaravan;
   Village village;
   private PlayerEntity customer;
   private TradeOffers traderOffers;
   private int levelUpCountdown;
   private boolean levelUp;
   private boolean f_82cjepner;
   private int riches;
   private String tradingPlayer;
   private int f_79mamwpbe;
   private int f_45ysyuqng;
   private boolean convertedZombie;
   private boolean f_20azncsca;
   private SimpleInventory f_55owflzlw = new SimpleInventory("Items", false, 8);
   private static final VillagerEntity.C_79vpxdszo[][][][] f_65exduzut = new VillagerEntity.C_79vpxdszo[][][][]{
      {
            {
                  {
                        new VillagerEntity.C_83eqviaik(Items.WHEAT, new VillagerEntity.C_45fvddynq(18, 22)),
                        new VillagerEntity.C_83eqviaik(Items.POTATO, new VillagerEntity.C_45fvddynq(15, 19)),
                        new VillagerEntity.C_83eqviaik(Items.CARROT, new VillagerEntity.C_45fvddynq(15, 19)),
                        new VillagerEntity.C_39jrwpbyv(Items.BREAD, new VillagerEntity.C_45fvddynq(-4, -2))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Item.byBlock(Blocks.PUMPKIN), new VillagerEntity.C_45fvddynq(8, 13)),
                        new VillagerEntity.C_39jrwpbyv(Items.PUMPKIN_PIE, new VillagerEntity.C_45fvddynq(-3, -2))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Item.byBlock(Blocks.MELON_BLOCK), new VillagerEntity.C_45fvddynq(7, 12)),
                        new VillagerEntity.C_39jrwpbyv(Items.APPLE, new VillagerEntity.C_45fvddynq(-5, -7))
                  },
                  {
                        new VillagerEntity.C_39jrwpbyv(Items.COOKIE, new VillagerEntity.C_45fvddynq(-6, -10)),
                        new VillagerEntity.C_39jrwpbyv(Items.CAKE, new VillagerEntity.C_45fvddynq(1, 1))
                  }
            },
            {
                  {
                        new VillagerEntity.C_83eqviaik(Items.STRING, new VillagerEntity.C_45fvddynq(15, 20)),
                        new VillagerEntity.C_83eqviaik(Items.COAL, new VillagerEntity.C_45fvddynq(16, 24)),
                        new VillagerEntity.C_46bxfmjhl(
                           Items.FISH, new VillagerEntity.C_45fvddynq(6, 6), Items.COOKED_FISH, new VillagerEntity.C_45fvddynq(6, 6)
                        )
                  },
                  {new VillagerEntity.C_34fgrhslg(Items.FISHING_ROD, new VillagerEntity.C_45fvddynq(7, 8))}
            },
            {
                  {
                        new VillagerEntity.C_83eqviaik(Item.byBlock(Blocks.WOOL), new VillagerEntity.C_45fvddynq(16, 22)),
                        new VillagerEntity.C_39jrwpbyv(Items.SHEARS, new VillagerEntity.C_45fvddynq(3, 4))
                  },
                  {
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 0), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 1), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 2), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 3), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 4), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 5), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 6), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 7), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 8), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 9), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 10), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 11), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 12), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 13), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 14), new VillagerEntity.C_45fvddynq(1, 2)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Item.byBlock(Blocks.WOOL), 1, 15), new VillagerEntity.C_45fvddynq(1, 2))
                  }
            },
            {
                  {
                        new VillagerEntity.C_83eqviaik(Items.STRING, new VillagerEntity.C_45fvddynq(15, 20)),
                        new VillagerEntity.C_39jrwpbyv(Items.ARROW, new VillagerEntity.C_45fvddynq(-12, -8))
                  },
                  {
                        new VillagerEntity.C_39jrwpbyv(Items.BOW, new VillagerEntity.C_45fvddynq(2, 3)),
                        new VillagerEntity.C_46bxfmjhl(
                           Item.byBlock(Blocks.GRAVEL), new VillagerEntity.C_45fvddynq(10, 10), Items.FLINT, new VillagerEntity.C_45fvddynq(6, 10)
                        )
                  }
            }
      },
      {
            {
                  {new VillagerEntity.C_83eqviaik(Items.PAPER, new VillagerEntity.C_45fvddynq(24, 36)), new VillagerEntity.C_62mujlken()},
                  {
                        new VillagerEntity.C_83eqviaik(Items.BOOK, new VillagerEntity.C_45fvddynq(8, 10)),
                        new VillagerEntity.C_39jrwpbyv(Items.COMPASS, new VillagerEntity.C_45fvddynq(10, 12)),
                        new VillagerEntity.C_39jrwpbyv(Item.byBlock(Blocks.BOOKSHELF), new VillagerEntity.C_45fvddynq(3, 4))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Items.WRITTEN_BOOK, new VillagerEntity.C_45fvddynq(2, 2)),
                        new VillagerEntity.C_39jrwpbyv(Items.CLOCK, new VillagerEntity.C_45fvddynq(10, 12)),
                        new VillagerEntity.C_39jrwpbyv(Item.byBlock(Blocks.GLASS), new VillagerEntity.C_45fvddynq(-5, -3))
                  },
                  {new VillagerEntity.C_62mujlken()},
                  {new VillagerEntity.C_62mujlken()},
                  {new VillagerEntity.C_39jrwpbyv(Items.NAME_TAG, new VillagerEntity.C_45fvddynq(20, 22))}
            }
      },
      {
            {
                  {
                        new VillagerEntity.C_83eqviaik(Items.ROTTEN_FLESH, new VillagerEntity.C_45fvddynq(36, 40)),
                        new VillagerEntity.C_83eqviaik(Items.GOLD_INGOT, new VillagerEntity.C_45fvddynq(8, 10))
                  },
                  {
                        new VillagerEntity.C_39jrwpbyv(Items.REDSTONE, new VillagerEntity.C_45fvddynq(-4, -1)),
                        new VillagerEntity.C_39jrwpbyv(new ItemStack(Items.DYE, 1, DyeColor.BLUE.getMetadata()), new VillagerEntity.C_45fvddynq(-2, -1))
                  },
                  {
                        new VillagerEntity.C_39jrwpbyv(Items.ENDER_EYE, new VillagerEntity.C_45fvddynq(7, 11)),
                        new VillagerEntity.C_39jrwpbyv(Item.byBlock(Blocks.GLOWSTONE), new VillagerEntity.C_45fvddynq(-3, -1))
                  }
            }
      },
      {
            {
                  {
                        new VillagerEntity.C_83eqviaik(Items.COAL, new VillagerEntity.C_45fvddynq(16, 24)),
                        new VillagerEntity.C_39jrwpbyv(Items.IRON_HELMET, new VillagerEntity.C_45fvddynq(4, 6))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Items.IRON_INGOT, new VillagerEntity.C_45fvddynq(7, 9)),
                        new VillagerEntity.C_39jrwpbyv(Items.IRON_CHESTPLATE, new VillagerEntity.C_45fvddynq(10, 14))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Items.DIAMOND, new VillagerEntity.C_45fvddynq(3, 4)),
                        new VillagerEntity.C_34fgrhslg(Items.DIAMOND_CHESTPLATE, new VillagerEntity.C_45fvddynq(16, 19))
                  },
                  {
                        new VillagerEntity.C_39jrwpbyv(Items.CHAINMAIL_BOOTS, new VillagerEntity.C_45fvddynq(5, 7)),
                        new VillagerEntity.C_39jrwpbyv(Items.CHAINMAIL_LEGGINGS, new VillagerEntity.C_45fvddynq(9, 11)),
                        new VillagerEntity.C_39jrwpbyv(Items.CHAINMAIL_HELMET, new VillagerEntity.C_45fvddynq(5, 7)),
                        new VillagerEntity.C_39jrwpbyv(Items.CHAINMAIL_CHESTPLATE, new VillagerEntity.C_45fvddynq(11, 15))
                  }
            },
            {
                  {
                        new VillagerEntity.C_83eqviaik(Items.COAL, new VillagerEntity.C_45fvddynq(16, 24)),
                        new VillagerEntity.C_39jrwpbyv(Items.IRON_AXE, new VillagerEntity.C_45fvddynq(6, 8))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Items.IRON_INGOT, new VillagerEntity.C_45fvddynq(7, 9)),
                        new VillagerEntity.C_34fgrhslg(Items.IRON_SWORD, new VillagerEntity.C_45fvddynq(9, 10))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Items.DIAMOND, new VillagerEntity.C_45fvddynq(3, 4)),
                        new VillagerEntity.C_34fgrhslg(Items.DIAMOND_SWORD, new VillagerEntity.C_45fvddynq(12, 15)),
                        new VillagerEntity.C_34fgrhslg(Items.DIAMOND_AXE, new VillagerEntity.C_45fvddynq(9, 12))
                  }
            },
            {
                  {
                        new VillagerEntity.C_83eqviaik(Items.COAL, new VillagerEntity.C_45fvddynq(16, 24)),
                        new VillagerEntity.C_34fgrhslg(Items.IRON_SHOVEL, new VillagerEntity.C_45fvddynq(5, 7))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Items.IRON_INGOT, new VillagerEntity.C_45fvddynq(7, 9)),
                        new VillagerEntity.C_34fgrhslg(Items.IRON_PICKAXE, new VillagerEntity.C_45fvddynq(9, 11))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Items.DIAMOND, new VillagerEntity.C_45fvddynq(3, 4)),
                        new VillagerEntity.C_34fgrhslg(Items.DIAMOND_PICKAXE, new VillagerEntity.C_45fvddynq(12, 15))
                  }
            }
      },
      {
            {
                  {
                        new VillagerEntity.C_83eqviaik(Items.PORKCHOP, new VillagerEntity.C_45fvddynq(14, 18)),
                        new VillagerEntity.C_83eqviaik(Items.CHICKEN, new VillagerEntity.C_45fvddynq(14, 18))
                  },
                  {
                        new VillagerEntity.C_83eqviaik(Items.COAL, new VillagerEntity.C_45fvddynq(16, 24)),
                        new VillagerEntity.C_39jrwpbyv(Items.COOKED_PORKCHOP, new VillagerEntity.C_45fvddynq(-7, -5)),
                        new VillagerEntity.C_39jrwpbyv(Items.COOKED_CHICKEN, new VillagerEntity.C_45fvddynq(-8, -6))
                  }
            },
            {
                  {
                        new VillagerEntity.C_83eqviaik(Items.LEATHER, new VillagerEntity.C_45fvddynq(9, 12)),
                        new VillagerEntity.C_39jrwpbyv(Items.LEATHER_LEGGINGS, new VillagerEntity.C_45fvddynq(2, 4))
                  },
                  {new VillagerEntity.C_34fgrhslg(Items.LEATHER_CHESTPLATE, new VillagerEntity.C_45fvddynq(7, 12))},
                  {new VillagerEntity.C_39jrwpbyv(Items.SADDLE, new VillagerEntity.C_45fvddynq(8, 10))}
            }
      }
   };

   public VillagerEntity(World c_54ruxjwzt) {
      this(c_54ruxjwzt, 0);
   }

   public VillagerEntity(World world, int profession) {
      super(world);
      this.setProfession(profession);
      this.setDimensions(0.6F, 1.8F);
      ((MobEntityNavigation)this.getNavigation()).m_54onmfdow(true);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new FleeEntityGoal(this, new Predicate() {
         public boolean apply(Entity c_47ldwddrb) {
            return c_47ldwddrb instanceof ZombieEntity;
         }
      }, 8.0F, 0.6, 0.6));
      this.goalSelector.addGoal(1, new StopFollowingCustomerGoal(this));
      this.goalSelector.addGoal(1, new LookAtCustomerGoal(this));
      this.goalSelector.addGoal(2, new StayIndoorsGoal(this));
      this.goalSelector.addGoal(3, new RestrictOpenDoorGoal(this));
      this.goalSelector.addGoal(4, new LongDoorInteractGoal(this, true));
      this.goalSelector.addGoal(5, new GoToWalkTargetGoal(this, 0.6));
      this.goalSelector.addGoal(6, new VillagerMatingGoal(this));
      this.goalSelector.addGoal(7, new FollowGolemGoal(this));
      this.goalSelector.addGoal(9, new GoToEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(9, new C_71bgxuxfc(this));
      this.goalSelector.addGoal(9, new WanderAroundGoal(this, 0.6));
      this.goalSelector.addGoal(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
      this.setCanPickupLoot(true);
   }

   private void m_82hlklsdo() {
      if (!this.f_20azncsca) {
         this.f_20azncsca = true;
         if (this.isBaby()) {
            this.goalSelector.addGoal(8, new FormCaravanGoal(this, 0.32));
         } else if (this.getProfession() == 0) {
            this.goalSelector.addGoal(6, new C_54zeovuss(this, 0.6));
         }
      }
   }

   @Override
   protected void m_90sjxdogf() {
      if (this.getProfession() == 0) {
         this.goalSelector.addGoal(8, new C_54zeovuss(this, 0.6));
      }

      super.m_90sjxdogf();
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.5);
   }

   @Override
   protected void m_45jbqtvrb() {
      if (--this.updateVillageCooldown <= 0) {
         BlockPos var1 = new BlockPos(this);
         this.world.getVillageData().addVillagerPosition(var1);
         this.updateVillageCooldown = 70 + this.random.nextInt(50);
         this.village = this.world.getVillageData().getClosestVillage(var1, 32);
         if (this.village == null) {
            this.resetVillageRadius();
         } else {
            BlockPos var2 = this.village.getCenter();
            this.setVillagePosAndRadius(var2, (int)((float)this.village.getRadius() * 1.0F));
            if (this.convertedZombie) {
               this.convertedZombie = false;
               this.village.updateAllReputations(5);
            }
         }
      }

      if (!this.hasCustomer() && this.levelUpCountdown > 0) {
         --this.levelUpCountdown;
         if (this.levelUpCountdown <= 0) {
            if (this.levelUp) {
               for(TradeOffer var4 : this.traderOffers) {
                  if (var4.isDisabled()) {
                     var4.increaseMaxUses(this.random.nextInt(6) + this.random.nextInt(6) + 2);
                  }
               }

               this.m_61wxpeuzu();
               this.levelUp = false;
               if (this.village != null && this.tradingPlayer != null) {
                  this.world.doEntityEvent(this, (byte)14);
                  this.village.updateReputation(this.tradingPlayer, 1);
               }
            }

            this.addStatusEffect(new StatusEffectInstance(StatusEffect.REGENERATION.id, 200, 0));
         }
      }

      super.m_45jbqtvrb();
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      boolean var3 = var2 != null && var2.getItem() == Items.SPAWN_EGG;
      if (!var3 && this.isAlive() && !this.hasCustomer() && !this.isBaby()) {
         if (!this.world.isClient && (this.traderOffers == null || this.traderOffers.size() > 0)) {
            this.setCustomer(player);
            player.openTraderMenu(this);
         }

         player.incrementStat(Stats.TALKED_TO_VILLAGER);
         return true;
      } else {
         return super.canInteract(player);
      }
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, 0);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("Profession", this.getProfession());
      nbt.putInt("Riches", this.riches);
      nbt.putInt("Career", this.f_79mamwpbe);
      nbt.putInt("CareerLevel", this.f_45ysyuqng);
      nbt.putBoolean("Willing", this.f_82cjepner);
      if (this.traderOffers != null) {
         nbt.put("Offers", this.traderOffers.toNbt());
      }

      NbtList var2 = new NbtList();

      for(int var3 = 0; var3 < this.f_55owflzlw.getSize(); ++var3) {
         ItemStack var4 = this.f_55owflzlw.getStack(var3);
         if (var4 != null) {
            var2.add(var4.writeNbt(new NbtCompound()));
         }
      }

      nbt.put("Inventory", var2);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.setProfession(nbt.getInt("Profession"));
      this.riches = nbt.getInt("Riches");
      this.f_79mamwpbe = nbt.getInt("Career");
      this.f_45ysyuqng = nbt.getInt("CareerLevel");
      this.f_82cjepner = nbt.getBoolean("Willing");
      if (nbt.isType("Offers", 10)) {
         NbtCompound var2 = nbt.getCompound("Offers");
         this.traderOffers = new TradeOffers(var2);
      }

      NbtList var5 = nbt.getList("Inventory", 10);

      for(int var3 = 0; var3 < var5.size(); ++var3) {
         ItemStack var4 = ItemStack.fromNbt(var5.getCompound(var3));
         if (var4 != null) {
            this.f_55owflzlw.addStack(var4);
         }
      }

      this.setCanPickupLoot(true);
      this.m_82hlklsdo();
   }

   @Override
   protected boolean canDespawn() {
      return false;
   }

   @Override
   protected String getAmbientSound() {
      return this.hasCustomer() ? "mob.villager.haggle" : "mob.villager.idle";
   }

   @Override
   protected String getHurtSound() {
      return "mob.villager.hit";
   }

   @Override
   protected String getDeathSound() {
      return "mob.villager.death";
   }

   public void setProfession(int profession) {
      this.dataTracker.update(16, profession);
   }

   public int getProfession() {
      return Math.max(this.dataTracker.getInt(16) % 5, 0);
   }

   public boolean getMating() {
      return this.mating;
   }

   public void setMating(boolean value) {
      this.mating = value;
   }

   public void setInCaravan(boolean value) {
      this.inCaravan = value;
   }

   public boolean getInCaravan() {
      return this.inCaravan;
   }

   @Override
   public void setAttacker(LivingEntity attacker) {
      super.setAttacker(attacker);
      if (this.village != null && attacker != null) {
         this.village.addOrUpdateAttacker(attacker);
         if (attacker instanceof PlayerEntity) {
            byte var2 = -1;
            if (this.isBaby()) {
               var2 = -3;
            }

            this.village.updateReputation(attacker.getName(), var2);
            if (this.isAlive()) {
               this.world.doEntityEvent(this, (byte)13);
            }
         }
      }
   }

   @Override
   public void onKilled(DamageSource source) {
      if (this.village != null) {
         Entity var2 = source.getAttacker();
         if (var2 != null) {
            if (var2 instanceof PlayerEntity) {
               this.village.updateReputation(var2.getName(), -2);
            } else if (var2 instanceof Monster) {
               this.village.stopMating();
            }
         } else {
            PlayerEntity var3 = this.world.getClosestPlayer(this, 16.0);
            if (var3 != null) {
               this.village.stopMating();
            }
         }
      }

      super.onKilled(source);
   }

   @Override
   public void setCustomer(PlayerEntity player) {
      this.customer = player;
   }

   @Override
   public PlayerEntity getCustomer() {
      return this.customer;
   }

   public boolean hasCustomer() {
      return this.customer != null;
   }

   public boolean m_59wrmvuvt(boolean bl) {
      if (!this.f_82cjepner && bl && this.m_25ajushpn()) {
         boolean var2 = false;

         for(int var3 = 0; var3 < this.f_55owflzlw.getSize(); ++var3) {
            ItemStack var4 = this.f_55owflzlw.getStack(var3);
            if (var4 != null) {
               if (var4.getItem() == Items.BREAD && var4.size >= 3) {
                  var2 = true;
                  this.f_55owflzlw.removeStack(var3, 3);
               } else if ((var4.getItem() == Items.POTATO || var4.getItem() == Items.CARROT) && var4.size >= 12) {
                  var2 = true;
                  this.f_55owflzlw.removeStack(var3, 12);
               }
            }

            if (var2) {
               this.world.doEntityEvent(this, (byte)18);
               this.f_82cjepner = true;
               break;
            }
         }
      }

      return this.f_82cjepner;
   }

   public void m_07mjfzjua(boolean bl) {
      this.f_82cjepner = bl;
   }

   @Override
   public void trade(TradeOffer offer) {
      offer.use();
      this.ambientSoundDelay = -this.getMinAmbientSoundDelay();
      this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
      int var2 = 3 + this.random.nextInt(4);
      if (offer.getUses() == 1 || this.random.nextInt(5) == 0) {
         this.levelUpCountdown = 40;
         this.levelUp = true;
         this.f_82cjepner = true;
         if (this.customer != null) {
            this.tradingPlayer = this.customer.getName();
         } else {
            this.tradingPlayer = null;
         }

         var2 += 5;
      }

      if (offer.getPrimaryPayment().getItem() == Items.EMERALD) {
         this.riches += offer.getPrimaryPayment().size;
      }

      if (offer.rewardXp()) {
         this.world.addEntity(new XpOrbEntity(this.world, this.x, this.y + 0.5, this.z, var2));
      }
   }

   @Override
   public void updateOffer(ItemStack stack) {
      if (!this.world.isClient && this.ambientSoundDelay > -this.getMinAmbientSoundDelay() + 20) {
         this.ambientSoundDelay = -this.getMinAmbientSoundDelay();
         if (stack != null) {
            this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
         } else {
            this.playSound("mob.villager.no", this.getSoundVolume(), this.getSoundPitch());
         }
      }
   }

   @Override
   public TradeOffers getOffers(PlayerEntity player) {
      if (this.traderOffers == null) {
         this.m_61wxpeuzu();
      }

      return this.traderOffers;
   }

   private void m_61wxpeuzu() {
      VillagerEntity.C_79vpxdszo[][][] var1 = f_65exduzut[this.getProfession()];
      if (this.f_79mamwpbe != 0 && this.f_45ysyuqng != 0) {
         ++this.f_45ysyuqng;
      } else {
         this.f_79mamwpbe = this.random.nextInt(var1.length) + 1;
         this.f_45ysyuqng = 1;
      }

      if (this.traderOffers == null) {
         this.traderOffers = new TradeOffers();
      }

      int var2 = this.f_79mamwpbe - 1;
      int var3 = this.f_45ysyuqng - 1;
      VillagerEntity.C_79vpxdszo[][] var4 = var1[var2];
      if (var3 < var4.length) {
         VillagerEntity.C_79vpxdszo[] var5 = var4[var3];

         for(VillagerEntity.C_79vpxdszo var9 : var5) {
            var9.m_60atouomx(this.traderOffers, this.random);
         }
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void setOffers(TradeOffers offers) {
   }

   @Override
   public Text getDisplayName() {
      String var1 = this.getCustomName();
      if (var1 != null && var1.length() > 0) {
         return new LiteralText(var1);
      } else {
         if (this.traderOffers == null) {
            this.m_61wxpeuzu();
         }

         String var2 = null;
         switch(this.getProfession()) {
            case 0:
               if (this.f_79mamwpbe == 1) {
                  var2 = "farmer";
               } else if (this.f_79mamwpbe == 2) {
                  var2 = "fisherman";
               } else if (this.f_79mamwpbe == 3) {
                  var2 = "shepherd";
               } else if (this.f_79mamwpbe == 4) {
                  var2 = "fletcher";
               }
               break;
            case 1:
               var2 = "librarian";
               break;
            case 2:
               var2 = "cleric";
               break;
            case 3:
               if (this.f_79mamwpbe == 1) {
                  var2 = "armor";
               } else if (this.f_79mamwpbe == 2) {
                  var2 = "weapon";
               } else if (this.f_79mamwpbe == 3) {
                  var2 = "tool";
               }
               break;
            case 4:
               if (this.f_79mamwpbe == 1) {
                  var2 = "butcher";
               } else if (this.f_79mamwpbe == 2) {
                  var2 = "leather";
               }
         }

         if (var2 != null) {
            TranslatableText var3 = new TranslatableText("entity.Villager." + var2);
            var3.getStyle().setHoverEvent(this.getHoverEvent());
            var3.getStyle().setInsertion(this.getUuid().toString());
            return var3;
         } else {
            return super.getDisplayName();
         }
      }
   }

   @Override
   public float getEyeHeight() {
      float var1 = 1.62F;
      if (this.isBaby()) {
         var1 = (float)((double)var1 - 0.81);
      }

      return var1;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 12) {
         this.addParticles(ParticleType.HEART);
      } else if (event == 13) {
         this.addParticles(ParticleType.VILLAGER_ANGRY);
      } else if (event == 14) {
         this.addParticles(ParticleType.VILLAGER_HAPPY);
      } else {
         super.doEvent(event);
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   private void addParticles(ParticleType particleName) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = this.random.nextGaussian() * 0.02;
         double var5 = this.random.nextGaussian() * 0.02;
         double var7 = this.random.nextGaussian() * 0.02;
         this.world
            .addParticle(
               particleName,
               this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
               this.y + 1.0 + (double)(this.random.nextFloat() * this.height),
               this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
               var3,
               var5,
               var7
            );
      }
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      entityData = super.initialize(localDifficulty, entityData);
      this.setProfession(this.world.random.nextInt(5));
      this.m_82hlklsdo();
      return entityData;
   }

   public void setConvertedZombie() {
      this.convertedZombie = true;
   }

   public VillagerEntity makeChild(PassiveEntity c_19nmglwmx) {
      VillagerEntity var2 = new VillagerEntity(this.world);
      var2.initialize(this.world.getLocalDifficulty(new BlockPos(var2)), null);
      return var2;
   }

   @Override
   public boolean isTameable() {
      return false;
   }

   @Override
   public void onLightningStrike(LightningBoltEntity lightning) {
      if (!this.world.isClient) {
         WitchEntity var2 = new WitchEntity(this.world);
         var2.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
         var2.initialize(this.world.getLocalDifficulty(new BlockPos(var2)), null);
         this.world.addEntity(var2);
         this.remove();
      }
   }

   public SimpleInventory m_73ivhbact() {
      return this.f_55owflzlw;
   }

   @Override
   protected void m_30rammcie(ItemEntity c_32myydzeb) {
      ItemStack var2 = c_32myydzeb.getItemStack();
      Item var3 = var2.getItem();
      if (this.m_69yrawowx(var3)) {
         ItemStack var4 = this.f_55owflzlw.addStack(var2);
         if (var4 == null) {
            c_32myydzeb.remove();
         } else {
            var2.size = var4.size;
         }
      }
   }

   private boolean m_69yrawowx(Item c_30vndvelc) {
      return c_30vndvelc == Items.BREAD
         || c_30vndvelc == Items.POTATO
         || c_30vndvelc == Items.CARROT
         || c_30vndvelc == Items.WHEAT
         || c_30vndvelc == Items.WHEAT_SEEDS;
   }

   public boolean m_25ajushpn() {
      return this.m_62nbanuuf(1);
   }

   public boolean m_53whewcuu() {
      return this.m_62nbanuuf(2);
   }

   public boolean m_19khndpdy() {
      boolean var1 = this.getProfession() == 0;
      if (var1) {
         return !this.m_62nbanuuf(5);
      } else {
         return !this.m_62nbanuuf(1);
      }
   }

   private boolean m_62nbanuuf(int i) {
      boolean var2 = this.getProfession() == 0;

      for(int var3 = 0; var3 < this.f_55owflzlw.getSize(); ++var3) {
         ItemStack var4 = this.f_55owflzlw.getStack(var3);
         if (var4 != null) {
            if (var4.getItem() == Items.BREAD && var4.size >= 3 * i
               || var4.getItem() == Items.POTATO && var4.size >= 12 * i
               || var4.getItem() == Items.CARROT && var4.size >= 12 * i) {
               return true;
            }

            if (var2 && var4.getItem() == Items.WHEAT && var4.size >= 9 * i) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean m_73suzockq() {
      for(int var1 = 0; var1 < this.f_55owflzlw.getSize(); ++var1) {
         ItemStack var2 = this.f_55owflzlw.getStack(var1);
         if (var2 != null && (var2.getItem() == Items.WHEAT_SEEDS || var2.getItem() == Items.POTATO || var2.getItem() == Items.CARROT)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean m_81zmldzmm(int i, ItemStack c_72owraavl) {
      if (super.m_81zmldzmm(i, c_72owraavl)) {
         return true;
      } else {
         int var3 = i - 300;
         if (var3 >= 0 && var3 < this.f_55owflzlw.getSize()) {
            this.f_55owflzlw.setStack(var3, c_72owraavl);
            return true;
         } else {
            return false;
         }
      }
   }

   static class C_34fgrhslg implements VillagerEntity.C_79vpxdszo {
      public ItemStack f_68hwldsvk;
      public VillagerEntity.C_45fvddynq f_35bldqctm;

      public C_34fgrhslg(Item c_30vndvelc, VillagerEntity.C_45fvddynq c_45fvddynq) {
         this.f_68hwldsvk = new ItemStack(c_30vndvelc);
         this.f_35bldqctm = c_45fvddynq;
      }

      @Override
      public void m_60atouomx(TradeOffers c_82jtsktsa, Random random) {
         int var3 = 1;
         if (this.f_35bldqctm != null) {
            var3 = this.f_35bldqctm.m_86sgunoxu(random);
         }

         ItemStack var4 = new ItemStack(Items.EMERALD, var3, 0);
         ItemStack var5 = new ItemStack(this.f_68hwldsvk.getItem(), 1, this.f_68hwldsvk.getMetadata());
         var5 = EnchantmentHelper.addRandomEnchantment(random, var5, 5 + random.nextInt(15));
         c_82jtsktsa.add(new TradeOffer(var4, var5));
      }
   }

   static class C_39jrwpbyv implements VillagerEntity.C_79vpxdszo {
      public ItemStack f_51rbymsgw;
      public VillagerEntity.C_45fvddynq f_56hqutnxn;

      public C_39jrwpbyv(Item c_30vndvelc, VillagerEntity.C_45fvddynq c_45fvddynq) {
         this.f_51rbymsgw = new ItemStack(c_30vndvelc);
         this.f_56hqutnxn = c_45fvddynq;
      }

      public C_39jrwpbyv(ItemStack c_72owraavl, VillagerEntity.C_45fvddynq c_45fvddynq) {
         this.f_51rbymsgw = c_72owraavl;
         this.f_56hqutnxn = c_45fvddynq;
      }

      @Override
      public void m_60atouomx(TradeOffers c_82jtsktsa, Random random) {
         int var3 = 1;
         if (this.f_56hqutnxn != null) {
            var3 = this.f_56hqutnxn.m_86sgunoxu(random);
         }

         ItemStack var4;
         ItemStack var5;
         if (var3 < 0) {
            var4 = new ItemStack(Items.EMERALD, 1, 0);
            var5 = new ItemStack(this.f_51rbymsgw.getItem(), -var3, this.f_51rbymsgw.getMetadata());
         } else {
            var4 = new ItemStack(Items.EMERALD, var3, 0);
            var5 = new ItemStack(this.f_51rbymsgw.getItem(), 1, this.f_51rbymsgw.getMetadata());
         }

         c_82jtsktsa.add(new TradeOffer(var4, var5));
      }
   }

   static class C_45fvddynq extends Pair {
      public C_45fvddynq(int i, int j) {
         super(i, j);
      }

      public int m_86sgunoxu(Random random) {
         return this.getLeft() >= this.getRight() ? this.getLeft() : this.getLeft() + random.nextInt(this.getRight() - this.getLeft() + 1);
      }
   }

   static class C_46bxfmjhl implements VillagerEntity.C_79vpxdszo {
      public ItemStack f_21ruzbsol;
      public VillagerEntity.C_45fvddynq f_33teawgpk;
      public ItemStack f_29qllwuou;
      public VillagerEntity.C_45fvddynq f_60ejfbmam;

      public C_46bxfmjhl(Item c_30vndvelc, VillagerEntity.C_45fvddynq c_45fvddynq, Item c_30vndvelc2, VillagerEntity.C_45fvddynq c_45fvddynq2) {
         this.f_21ruzbsol = new ItemStack(c_30vndvelc);
         this.f_33teawgpk = c_45fvddynq;
         this.f_29qllwuou = new ItemStack(c_30vndvelc2);
         this.f_60ejfbmam = c_45fvddynq2;
      }

      @Override
      public void m_60atouomx(TradeOffers c_82jtsktsa, Random random) {
         int var3 = 1;
         if (this.f_33teawgpk != null) {
            var3 = this.f_33teawgpk.m_86sgunoxu(random);
         }

         int var4 = 1;
         if (this.f_60ejfbmam != null) {
            var4 = this.f_60ejfbmam.m_86sgunoxu(random);
         }

         c_82jtsktsa.add(
            new TradeOffer(
               new ItemStack(this.f_21ruzbsol.getItem(), var3, this.f_21ruzbsol.getMetadata()),
               new ItemStack(Items.EMERALD),
               new ItemStack(this.f_29qllwuou.getItem(), var4, this.f_29qllwuou.getMetadata())
            )
         );
      }
   }

   static class C_62mujlken implements VillagerEntity.C_79vpxdszo {
      public C_62mujlken() {
      }

      @Override
      public void m_60atouomx(TradeOffers c_82jtsktsa, Random random) {
         Enchantment var3 = Enchantment.ALL[random.nextInt(Enchantment.ALL.length)];
         int var4 = MathHelper.nextInt(random, var3.getMinLevel(), var3.getMaxLevel());
         ItemStack var5 = Items.ENCHANTED_BOOK.getStackWithEnchantment(new EnchantmentEntry(var3, var4));
         int var6 = 2 + random.nextInt(5 + var4 * 10) + 3 * var4;
         if (var6 > 64) {
            var6 = 64;
         }

         c_82jtsktsa.add(new TradeOffer(new ItemStack(Items.BOOK), new ItemStack(Items.EMERALD, var6), var5));
      }
   }

   interface C_79vpxdszo {
      void m_60atouomx(TradeOffers c_82jtsktsa, Random random);
   }

   static class C_83eqviaik implements VillagerEntity.C_79vpxdszo {
      public Item f_05styuvxe;
      public VillagerEntity.C_45fvddynq f_52oidcgki;

      public C_83eqviaik(Item c_30vndvelc, VillagerEntity.C_45fvddynq c_45fvddynq) {
         this.f_05styuvxe = c_30vndvelc;
         this.f_52oidcgki = c_45fvddynq;
      }

      @Override
      public void m_60atouomx(TradeOffers c_82jtsktsa, Random random) {
         int var3 = 1;
         if (this.f_52oidcgki != null) {
            var3 = this.f_52oidcgki.m_86sgunoxu(random);
         }

         c_82jtsktsa.add(new TradeOffer(new ItemStack(this.f_05styuvxe, var3, 0), Items.EMERALD));
      }
   }
}
