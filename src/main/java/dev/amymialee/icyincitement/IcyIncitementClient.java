package dev.amymialee.icyincitement;

import com.mojang.serialization.MapCodec;
import dev.amymialee.icyincitement.cca.SnowComponent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class IcyIncitementClient implements ClientModInitializer {
    public @Override void onInitializeClient() {
        NumericProperties.ID_MAPPER.put(IcyIncitement.id("charge"), SnowProperty.CODEC);
    }

    @Environment(EnvType.CLIENT)
    public record SnowProperty() implements NumericProperty {
        public static final MapCodec<SnowProperty> CODEC = MapCodec.unit(new SnowProperty());

        @Override
        public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable HeldItemContext context, int seed) {
            if (context != null && context.getEntity() instanceof PlayerEntity player) return SnowComponent.KEY.get(player).getCharge();
            return 0.0F;
        }

        @Override
        public MapCodec<SnowProperty> getCodec() {
            return CODEC;
        }
    }
}