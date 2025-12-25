package dev.amymialee.icyincitement;

import com.mojang.serialization.MapCodec;
import dev.amymialee.icyincitement.cca.SnowComponent;
import dev.amymialee.icyincitement.client.BuzzsawVFXClient;
import dev.amymialee.icyincitement.client.SawbladeRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class IcyIncitementClient implements ClientModInitializer {
	public @Override void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(BuzzsawVFXClient::tick);
		RangeSelectItemModelProperties.ID_MAPPER.put(IcyIncitement.id("charge"), SnowProperty.CODEC);
		EntityRenderers.register(IcyIncitement.SAWBLADE, SawbladeRenderer::new);
	}

	@Environment(EnvType.CLIENT)
	public record SnowProperty() implements RangeSelectItemModelProperty {
		public static final MapCodec<SnowProperty> CODEC = MapCodec.unit(new SnowProperty());

		@Override
		public float get(@NonNull ItemStack stack, @Nullable ClientLevel world, @Nullable ItemOwner context, int seed) {
			if (context != null && context.asLivingEntity() instanceof Player player) return SnowComponent.KEY.get(player).getCharge();
			return 0.0F;
		}

		@Override
		public @NonNull MapCodec<SnowProperty> type() {
			return CODEC;
		}
	}
}