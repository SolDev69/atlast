package net.minecraft.client.entity.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.resource.Identifier;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ParticleManager {
   private static final Identifier TEXTURES = new Identifier("textures/particle/particles.png");
   protected World world;
   private List[][] particles = new List[4][];
   private List emitters = Lists.newArrayList();
   private TextureManager textureManager;
   private Random random = new Random();
   private Map factories = Maps.newHashMap();

   public ParticleManager(World world, TextureManager textureManager) {
      this.world = world;
      this.textureManager = textureManager;

      for(int var3 = 0; var3 < 4; ++var3) {
         this.particles[var3] = new List[2];

         for(int var4 = 0; var4 < 2; ++var4) {
            this.particles[var3][var4] = Lists.newArrayList();
         }
      }

      this.registerFactories();
   }

   private void registerFactories() {
      this.register(ParticleType.EXPLOSION_NORMAL.getId(), new ExplosionParticle.Factory());
      this.register(ParticleType.WATER_BUBBLE.getId(), new WaterBubbleParticle.Factory());
      this.register(ParticleType.WATER_SPLASH.getId(), new WaterSplashParticle.Factory());
      this.register(ParticleType.WATER_WAKE.getId(), new WakeParticle.Factory());
      this.register(ParticleType.WATER_DROP.getId(), new RainSplashParticle.Factory());
      this.register(ParticleType.SUSPENDED.getId(), new SuspendedParticle.Factory());
      this.register(ParticleType.SUSPENDED_DEPTH.getId(), new DepthSuspendParticle.Factory());
      this.register(ParticleType.CRIT.getId(), new CriticalHitParticle.Factory());
      this.register(ParticleType.CRIT_MAGIC.getId(), new CriticalHitParticle.MagicFactory());
      this.register(ParticleType.SMOKE_NORMAL.getId(), new SmokeParticle.Factory());
      this.register(ParticleType.SMOKE_LARGE.getId(), new LargeSmokeParticle.Factory());
      this.register(ParticleType.SPELL.getId(), new SpellParticle.Factory());
      this.register(ParticleType.SPELL_INSTANT.getId(), new SpellParticle.InstantFactory());
      this.register(ParticleType.SPELL_MOB.getId(), new SpellParticle.MobFactory());
      this.register(ParticleType.SPELL_MOB_AMBIENT.getId(), new SpellParticle.MobAmbientFactory());
      this.register(ParticleType.SPELL_WITCH.getId(), new SpellParticle.WitchFactory());
      this.register(ParticleType.DRIP_WATER.getId(), new LiquidDripParticle.WaterFactory());
      this.register(ParticleType.DRIP_LAVA.getId(), new LiquidDripParticle.LavaFactory());
      this.register(ParticleType.VILLAGER_ANGRY.getId(), new EmotionParticle.AngryFactory());
      this.register(ParticleType.VILLAGER_HAPPY.getId(), new DepthSuspendParticle.HappyFactory());
      this.register(ParticleType.TOWN_AURA.getId(), new DepthSuspendParticle.Factory());
      this.register(ParticleType.NOTE.getId(), new NoteParticle.Factory());
      this.register(ParticleType.PORTAL.getId(), new PortalParticle.Factory());
      this.register(ParticleType.ENCHANTMENT_TABLE.getId(), new EnchantingParticle.Factory());
      this.register(ParticleType.FLAME.getId(), new FlameParticle.Factory());
      this.register(ParticleType.LAVA.getId(), new LavaParticle.Factory());
      this.register(ParticleType.FOOTSTEP.getId(), new FootstepParticle.Factory());
      this.register(ParticleType.CLOUD.getId(), new CloudParticle.Factory());
      this.register(ParticleType.REDSTONE.getId(), new RedstoneParticle.Factory());
      this.register(ParticleType.SNOWBALL.getId(), new ItemParticle.SnowballFactory());
      this.register(ParticleType.SNOW_SHOVEL.getId(), new SnowShovelParticle.Factory());
      this.register(ParticleType.SLIME.getId(), new ItemParticle.SlimeBallFactory());
      this.register(ParticleType.HEART.getId(), new EmotionParticle.Factory());
      this.register(ParticleType.BARRIER.getId(), new BarrierParticle.Factory());
      this.register(ParticleType.ITEM_CRACK.getId(), new ItemParticle.Factory());
      this.register(ParticleType.BLOCK_CRACK.getId(), new BlockBreakingParticle.Factory());
      this.register(ParticleType.BLOCK_DUST.getId(), new BlockDustParticle.Factory());
      this.register(ParticleType.EXPLOSION_HUGE.getId(), new HugeExplosionParticle.Factory());
      this.register(ParticleType.EXPLOSION_LARGE.getId(), new LargeExplosionParticle.Factory());
      this.register(ParticleType.FIREWORKS_SPARK.getId(), new FireworksParticles.Factory());
      this.register(ParticleType.MOB_APPEARANCE.getId(), new MobAppearanceParticle.Factory());
   }

   public void register(int type, ParticleFactory factory) {
      this.factories.put(type, factory);
   }

   public void addEmitter(Entity entity, ParticleType type) {
      this.emitters.add(new EmitterParticle(this.world, entity, type));
   }

   public Particle addParticle(int type, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
      ParticleFactory var15 = (ParticleFactory)this.factories.get(type);
      if (var15 != null) {
         Particle var16 = var15.create(type, this.world, x, y, z, velocityX, velocityY, velocityZ, parameters);
         if (var16 != null) {
            this.addParticle(var16);
            return var16;
         }
      }

      return null;
   }

   public void addParticle(Particle particle) {
      int var2 = particle.getTextureType();
      int var3 = particle.getAlpha() != 1.0F ? 0 : 1;
      if (this.particles[var2][var3].size() >= 4000) {
         this.particles[var2][var3].remove(0);
      }

      this.particles[var2][var3].add(particle);
   }

   public void tick() {
      for(int var1 = 0; var1 < 4; ++var1) {
         this.tickParticles(var1);
      }

      ArrayList var4 = Lists.newArrayList();

      for(EmitterParticle var3 : this.emitters) {
         var3.tick();
         if (var3.removed) {
            var4.add(var3);
         }
      }

      this.emitters.removeAll(var4);
   }

   private void tickParticles(int renderType) {
      for(int var2 = 0; var2 < 2; ++var2) {
         this.tickParticles(this.particles[renderType][var2]);
      }
   }

   private void tickParticles(List particles) {
      ArrayList var2 = Lists.newArrayList();

      for(int var3 = 0; var3 < particles.size(); ++var3) {
         Particle var4 = (Particle)particles.get(var3);
         this.tickParticle(var4);
         if (var4.removed) {
            var2.add(var4);
         }
      }

      particles.removeAll(var2);
   }

   private void tickParticle(Particle particle) {
      try {
         particle.tick();
      } catch (Throwable var6) {
         CrashReport var3 = CrashReport.of(var6, "Ticking Particle");
         CashReportCategory var4 = var3.addCategory("Particle being ticked");
         final int var5 = particle.getTextureType();
         var4.add("Particle", new Callable() {
            public String call() {
               return particle.toString();
            }
         });
         var4.add("Particle Type", new Callable() {
            public String call() {
               if (var5 == 0) {
                  return "MISC_TEXTURE";
               } else if (var5 == 1) {
                  return "TERRAIN_TEXTURE";
               } else {
                  return var5 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var5;
               }
            }
         });
         throw new CrashException(var3);
      }
   }

   public void renderParticles(Entity camera, float tickDelta) {
      float var3 = Camera.dx();
      float var4 = Camera.dz();
      float var5 = Camera.forwards();
      float var6 = Camera.sideways();
      float var7 = Camera.dy();
      Particle.currentX = camera.prevTickX + (camera.x - camera.prevTickX) * (double)tickDelta;
      Particle.currentY = camera.prevTickY + (camera.y - camera.prevTickY) * (double)tickDelta;
      Particle.currentZ = camera.prevTickZ + (camera.z - camera.prevTickZ) * (double)tickDelta;
      GlStateManager.disableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.alphaFunc(516, 0.003921569F);

      for(final int var8 = 0; var8 < 3; ++var8) {
         for(int var9 = 0; var9 < 2; ++var9) {
            if (!this.particles[var8][var9].isEmpty()) {
               switch(var9) {
                  case 0:
                     GlStateManager.depthMask(false);
                     break;
                  case 1:
                     GlStateManager.depthMask(true);
               }

               switch(var8) {
                  case 0:
                  default:
                     this.textureManager.bind(TEXTURES);
                     break;
                  case 1:
                     this.textureManager.bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
               }

               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               Tessellator var10 = Tessellator.getInstance();
               BufferBuilder var11 = var10.getBufferBuilder();
               var11.start();

               for(int var12 = 0; var12 < this.particles[var8][var9].size(); ++var12) {
                  final Particle var13 = (Particle)this.particles[var8][var9].get(var12);
                  var11.brightness(var13.getLightLevel(tickDelta));

                  try {
                     var13.render(var11, camera, tickDelta, var3, var7, var4, var5, var6);
                  } catch (Throwable var18) {
                     CrashReport var15 = CrashReport.of(var18, "Rendering Particle");
                     CashReportCategory var16 = var15.addCategory("Particle being rendered");
                     var16.add("Particle", new Callable() {
                        public String call() {
                           return var13.toString();
                        }
                     });
                     var16.add("Particle Type", new Callable() {
                        public String call() {
                           if (var8 == 0) {
                              return "MISC_TEXTURE";
                           } else if (var8 == 1) {
                              return "TERRAIN_TEXTURE";
                           } else {
                              return var8 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var8;
                           }
                        }
                     });
                     throw new CrashException(var15);
                  }
               }

               var10.end();
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableBlend();
      GlStateManager.alphaFunc(516, 0.1F);
   }

   public void renderLitParticles(Entity camera, float tickDelta) {
      float var3 = (float) (Math.PI / 180.0);
      float var4 = MathHelper.cos(camera.yaw * (float) (Math.PI / 180.0));
      float var5 = MathHelper.sin(camera.yaw * (float) (Math.PI / 180.0));
      float var6 = -var5 * MathHelper.sin(camera.pitch * (float) (Math.PI / 180.0));
      float var7 = var4 * MathHelper.sin(camera.pitch * (float) (Math.PI / 180.0));
      float var8 = MathHelper.cos(camera.pitch * (float) (Math.PI / 180.0));

      for(int var9 = 0; var9 < 2; ++var9) {
         List var10 = this.particles[3][var9];
         if (!var10.isEmpty()) {
            Tessellator var11 = Tessellator.getInstance();
            BufferBuilder var12 = var11.getBufferBuilder();

            for(int var13 = 0; var13 < var10.size(); ++var13) {
               Particle var14 = (Particle)var10.get(var13);
               var12.brightness(var14.getLightLevel(tickDelta));
               var14.render(var12, camera, tickDelta, var4, var8, var5, var6, var7);
            }
         }
      }
   }

   public void setWorld(World world) {
      this.world = world;

      for(int var2 = 0; var2 < 4; ++var2) {
         for(int var3 = 0; var3 < 2; ++var3) {
            this.particles[var2][var3].clear();
         }
      }

      this.emitters.clear();
   }

   public void addBlockMiningParticles(BlockPos pos, BlockState state) {
      if (state.getBlock().getMaterial() != Material.AIR) {
         state = state.getBlock().updateShape(state, this.world, pos);
         byte var3 = 4;

         for(int var4 = 0; var4 < var3; ++var4) {
            for(int var5 = 0; var5 < var3; ++var5) {
               for(int var6 = 0; var6 < var3; ++var6) {
                  double var7 = (double)pos.getX() + ((double)var4 + 0.5) / (double)var3;
                  double var9 = (double)pos.getY() + ((double)var5 + 0.5) / (double)var3;
                  double var11 = (double)pos.getZ() + ((double)var6 + 0.5) / (double)var3;
                  this.addParticle(
                     new BlockBreakingParticle(
                           this.world,
                           var7,
                           var9,
                           var11,
                           var7 - (double)pos.getX() - 0.5,
                           var9 - (double)pos.getY() - 0.5,
                           var11 - (double)pos.getZ() - 0.5,
                           state
                        )
                        .updateColor(pos)
                  );
               }
            }
         }
      }
   }

   public void addBlockMiningParticles(BlockPos pos, Direction face) {
      BlockState var3 = this.world.getBlockState(pos);
      Block var4 = var3.getBlock();
      if (var4.getRenderType() != -1) {
         int var5 = pos.getX();
         int var6 = pos.getY();
         int var7 = pos.getZ();
         float var8 = 0.1F;
         double var9 = (double)var5 + this.random.nextDouble() * (var4.getMaxX() - var4.getMinX() - (double)(var8 * 2.0F)) + (double)var8 + var4.getMinX();
         double var11 = (double)var6 + this.random.nextDouble() * (var4.getMaxY() - var4.getMinY() - (double)(var8 * 2.0F)) + (double)var8 + var4.getMinY();
         double var13 = (double)var7 + this.random.nextDouble() * (var4.getMaxZ() - var4.getMinZ() - (double)(var8 * 2.0F)) + (double)var8 + var4.getMinZ();
         if (face == Direction.DOWN) {
            var11 = (double)var6 + var4.getMinY() - (double)var8;
         }

         if (face == Direction.UP) {
            var11 = (double)var6 + var4.getMaxY() + (double)var8;
         }

         if (face == Direction.NORTH) {
            var13 = (double)var7 + var4.getMinZ() - (double)var8;
         }

         if (face == Direction.SOUTH) {
            var13 = (double)var7 + var4.getMaxZ() + (double)var8;
         }

         if (face == Direction.WEST) {
            var9 = (double)var5 + var4.getMinX() - (double)var8;
         }

         if (face == Direction.EAST) {
            var9 = (double)var5 + var4.getMaxX() + (double)var8;
         }

         this.addParticle(
            new BlockBreakingParticle(this.world, var9, var11, var13, 0.0, 0.0, 0.0, var3).updateColor(pos).multiplyVelocity(0.2F).multiplyScale(0.6F)
         );
      }
   }

   public void m_10rnyabex(Particle c_81rxpkmau) {
      this.m_20xscbhmw(c_81rxpkmau, 1, 0);
   }

   public void m_25qkddiik(Particle c_81rxpkmau) {
      this.m_20xscbhmw(c_81rxpkmau, 0, 1);
   }

   private void m_20xscbhmw(Particle c_81rxpkmau, int i, int j) {
      for(int var4 = 0; var4 < 4; ++var4) {
         if (this.particles[var4][i].contains(c_81rxpkmau)) {
            this.particles[var4][i].remove(c_81rxpkmau);
            this.particles[var4][j].add(c_81rxpkmau);
         }
      }
   }

   public String getParticlesDebugInfo() {
      int var1 = 0;

      for(int var2 = 0; var2 < 4; ++var2) {
         for(int var3 = 0; var3 < 2; ++var3) {
            var1 += this.particles[var2][var3].size();
         }
      }

      return "" + var1;
   }
}
