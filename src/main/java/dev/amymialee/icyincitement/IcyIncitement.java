package dev.amymialee.icyincitement;

import dev.amymialee.icyincitement.cca.BuzzsawComponent;
import dev.amymialee.icyincitement.cca.SnowComponent;
import dev.amymialee.icyincitement.entities.SawbladeEntity;
import dev.amymialee.icyincitement.item.BuzzsawItem;
import dev.amymialee.icyincitement.item.EmptySprinklerItem;
import dev.amymialee.icyincitement.item.SnowballSprinklerItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.UseEffects;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueCategory;
import xyz.amymialee.mialib.templates.MRegistry;

import java.util.HashSet;
import java.util.Set;

public class IcyIncitement implements ModInitializer, EntityComponentInitializer {
	public static final String MOD_ID = "icyincitement";
	public static final MRegistry REGISTRY = new MRegistry(MOD_ID);

	public static final Item EMPTY_SPRINKLER = REGISTRY.register("empty_sprinkler", new Item.Properties().stacksTo(1), EmptySprinklerItem::new, CreativeModeTabs.COMBAT);
	public static final Item SNOWBALL_SPRINKLER = REGISTRY.register("snowball_sprinkler", new Item.Properties().component(DataComponents.USE_EFFECTS, new UseEffects(true, true, 1f)).rarity(Rarity.EPIC).stacksTo(1), SnowballSprinklerItem::new, CreativeModeTabs.COMBAT);
	public static final Item BUZZSAW = REGISTRY.register("buzzsaw", new Item.Properties().component(DataComponents.USE_EFFECTS, new UseEffects(true, true, 1f)).rarity(Rarity.EPIC).fireResistant().stacksTo(1), BuzzsawItem::new, CreativeModeTabs.COMBAT);

	public static final EntityType<SawbladeEntity> SAWBLADE = REGISTRY.register("sawblade", EntityType.Builder.<SawbladeEntity>of(SawbladeEntity::new, MobCategory.MISC).sized(0.6f, 0.6f).clientTrackingRange(12).noSave().updateInterval(1));

	public static final ResourceKey<DamageType> BUZZSAWED = ResourceKey.create(Registries.DAMAGE_TYPE, id("buzzsawed"));

	public static final MValueCategory CATEGORY = new MValueCategory(id(MOD_ID), SNOWBALL_SPRINKLER, Identifier.withDefaultNamespace("textures/block/ice.png"), 16, 16);
	public static final MValue<Integer> MAX_SNOWBALLS = MValue.of(id("max_snowballs"), MValue.INTEGER.of(128, 1, 256)).category(CATEGORY).item(Items.SNOW_BLOCK).build();
	public static final MValue<Float> RECHARGE_RATE = MValue.of(id("recharge_rate"), MValue.FLOAT.of(0.45f, 0f, 1f)).category(CATEGORY).item(Items.POWDER_SNOW_BUCKET).build();
	public static final MValue<Float> HORIZONTAL_RECOIL = MValue.of(id("horizontal_recoil"), MValue.FLOAT.of(0.02f, 0f, 0.2f, 3)).category(CATEGORY).item(Items.TRIDENT).build();
	public static final MValue<Float> VERTICAL_RECOIL = MValue.of(id("vertical_recoil"), MValue.FLOAT.of(0.075f, 0f, 0.2f, 3)).category(CATEGORY).item(Items.TRIDENT).build();
	public static final MValue<Float> SNOW_VELOCITY = MValue.of(id("snow_velocity"), MValue.FLOAT.of(1.95f, 0f, 3f)).category(CATEGORY).item(Items.ARROW).build();
	public static final MValue<Float> SNOW_DIVERGENCE = MValue.of(id("snow_divergence"), MValue.FLOAT.of(4f, 0f, 32f)).category(CATEGORY).item(Items.BOW).build();

	//TODO
	// buzzsaw name
	// buzzsaw recipe
	// tooltips for all items
	// advancements for the mod (as a whole, making it interesting)
	// maybe some configs for the buzzsaw
	// netherite upgrade (this would be much later, prolly not in the main release)
	// remember what the enchants are supposed to be
	// make the enchants
	// make the right click as a whole

	public @Override void onInitialize() {
		Set<BlockPos> treeTargets = new HashSet<>();
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
			if (player.getMainHandItem().is(BUZZSAW) && state.is(BlockTags.LOGS)) {
				BuzzsawComponent.fillTargetSet(world, pos, treeTargets);
			} else {
				treeTargets.clear();
			}
			return true;
		});
		PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, entity) -> {
			var stack = player.getMainHandItem();
			if (!(stack.is(BUZZSAW))) return;
			for (var target : treeTargets) {
				var blockState = level.getBlockState(target);
				if (blockState.is(BlockTags.LOGS)) {
					if (level instanceof ServerLevel) {
						Block.getDrops(blockState, (ServerLevel) level, target, blockState.hasBlockEntity() ? level.getBlockEntity(target) : null, player, stack).forEach(itemStackx -> Block.popResource(level, pos, itemStackx));
						blockState.spawnAfterBreak((ServerLevel) level, target, stack, true);
					}
					level.destroyBlock(target, false, player);
				}
			}
		});
	}

	public @Override void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(Player.class, SnowComponent.KEY).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(SnowComponent::new);
		registry.beginRegistration(Player.class, BuzzsawComponent.KEY).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(BuzzsawComponent::new);
	}

	public static @NotNull Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}