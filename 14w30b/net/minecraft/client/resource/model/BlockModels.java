package net.minecraft.client.resource.model;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockModels {
   private Map providers = Maps.newIdentityHashMap();
   private Set custom = Sets.newIdentityHashSet();

   public void addProvider(Block block, BlockModelProvider provider) {
      this.providers.put(block, provider);
   }

   public void addCustom(Block... blocks) {
      Collections.addAll(this.custom, blocks);
   }

   public Map provide() {
      IdentityHashMap var1 = Maps.newIdentityHashMap();

      for(Block var3 : Block.REGISTRY) {
         if (!this.custom.contains(var3)) {
            var1.putAll(((BlockModelProvider)Objects.firstNonNull(this.providers.get(var3), new SimpleBlockModelProvider())).provide(var3));
         }
      }

      return var1;
   }
}
