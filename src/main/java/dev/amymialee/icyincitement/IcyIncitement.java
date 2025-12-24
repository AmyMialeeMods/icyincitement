package dev.amymialee.icyincitement;

import dev.amymialee.icyincitement.cca.SnowComponent;
import dev.amymialee.icyincitement.item.EmptySprinklerItem;
import dev.amymialee.icyincitement.item.SnowballSprinklerItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.UseEffects;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueCategory;
import xyz.amymialee.mialib.templates.MRegistry;

public class IcyIncitement implements ModInitializer, EntityComponentInitializer {
	public static final String MOD_ID = "icyincitement";
	public static final MRegistry REGISTRY = new MRegistry(MOD_ID);

	public static final Item EMPTY_SPRINKLER = REGISTRY.register("empty_sprinkler", new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1), EmptySprinklerItem::new, CreativeModeTabs.COMBAT);
	public static final Item SNOWBALL_SPRINKLER = REGISTRY.register("snowball_sprinkler", new Item.Properties().component(DataComponents.USE_EFFECTS, new UseEffects(true, true, 1f)).rarity(Rarity.EPIC).stacksTo(1), SnowballSprinklerItem::new, CreativeModeTabs.COMBAT);

	public static final MValueCategory CATEGORY = new MValueCategory(id(MOD_ID), SNOWBALL_SPRINKLER, Identifier.withDefaultNamespace("textures/block/ice.png"), 16, 16);
	public static final MValue<Integer> MAX_SNOWBALLS = MValue.of(id("max_snowballs"), MValue.INTEGER.of(128, 1, 256)).category(CATEGORY).item(Items.SNOW_BLOCK).build();
	public static final MValue<Float> RECHARGE_RATE = MValue.of(id("recharge_rate"), MValue.FLOAT.of(0.45f, 0f, 1f)).category(CATEGORY).item(Items.POWDER_SNOW_BUCKET).build();
	public static final MValue<Float> HORIZONTAL_RECOIL = MValue.of(id("horizontal_recoil"), MValue.FLOAT.of(0.02f, 0f, 0.2f, 3)).category(CATEGORY).item(Items.TRIDENT).build();
	public static final MValue<Float> VERTICAL_RECOIL = MValue.of(id("vertical_recoil"), MValue.FLOAT.of(0.075f, 0f, 0.2f, 3)).category(CATEGORY).item(Items.TRIDENT).build();
	public static final MValue<Float> SNOW_VELOCITY = MValue.of(id("snow_velocity"), MValue.FLOAT.of(1.95f, 0f, 3f)).category(CATEGORY).item(Items.ARROW).build();
	public static final MValue<Float> SNOW_DIVERGENCE = MValue.of(id("snow_divergence"), MValue.FLOAT.of(4f, 0f, 32f)).category(CATEGORY).item(Items.BOW).build();

	public @Override void onInitialize() {

	}

	public @Override void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(Player.class, SnowComponent.KEY).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(SnowComponent::new);
	}

	public static @NotNull Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}