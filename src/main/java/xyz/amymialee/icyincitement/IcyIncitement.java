package xyz.amymialee.icyincitement;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import xyz.amymialee.icyincitement.cca.FrozenComponent;
import xyz.amymialee.icyincitement.cca.SnowComponent;
import xyz.amymialee.icyincitement.entity.IceBallEntity;
import xyz.amymialee.icyincitement.item.EmptySprinklerItem;
import xyz.amymialee.icyincitement.item.SnowballSprinklerItem;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueCategory;
import xyz.amymialee.mialib.templates.MRegistry;

public class IcyIncitement implements ModInitializer, EntityComponentInitializer {
    public static final String MOD_ID = "icyincitement";
    public static final MRegistry REGISTRY = new MRegistry(MOD_ID);
    public static final Item EMPTY_SPRINKLER = REGISTRY.register("empty_sprinkler", new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(1), EmptySprinklerItem::new, ItemGroups.COMBAT);
    public static final Item SNOWBALL_SPRINKLER = REGISTRY.register("snowball_sprinkler", new Item.Settings().rarity(Rarity.EPIC).maxCount(1), SnowballSprinklerItem::new, ItemGroups.COMBAT);
    public static final EntityType<IceBallEntity> ICEBALL = REGISTRY.register("iceball", EntityType.Builder.<IceBallEntity>create(IceBallEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(8).trackingTickInterval(2));
    public static final MValueCategory CATEGORY = new MValueCategory(id(MOD_ID), SNOWBALL_SPRINKLER, Identifier.ofVanilla("textures/block/ice.png"), 16, 16);
    public static final MValue<Integer> MAX_SNOWBALLS = MValue.of(id("max_snowballs"), MValue.INTEGER.of(128, 1, 256)).category(CATEGORY).item(Items.SNOW_BLOCK).build();
    public static final MValue<Double> RECHARGE_RATE = MValue.of(id("recharge_rate"), MValue.DOUBLE.of(0.45, 0d, 1d)).category(CATEGORY).item(Items.POWDER_SNOW_BUCKET).build();
    public static final MValue<Double> HORIZONTAL_RECOIL = MValue.of(id("horizontal_recoil"), MValue.DOUBLE.of(0.02, 0d, 0.2d, 3)).category(CATEGORY).item(Items.TRIDENT).build();
    public static final MValue<Double> VERTICAL_RECOIL = MValue.of(id("vertical_recoil"), MValue.DOUBLE.of(0.075, 0d, 0.2d, 3)).category(CATEGORY).item(Items.TRIDENT).build();
    public static final MValue<Boolean> SLOWDOWN = MValue.of(id("slowdown"), MValue.BOOLEAN_FALSE).category(CATEGORY).item(Items.SHIELD).build();
    public static final MValue<Float> SNOW_DAMAGE = MValue.of(id("snow_damage"), MValue.FLOAT.of(0.05f, 0f, 4f)).category(CATEGORY).item(Items.IRON_SWORD).build();
    public static final MValue<Float> SNOW_HEALING = MValue.of(id("snow_healing"), MValue.FLOAT.of(0.25f, 0f, 4f)).category(CATEGORY).item(Items.GHAST_TEAR).build();
    public static final MValue<Float> SNOW_VELOCITY = MValue.of(id("snow_velocity"), MValue.FLOAT.of(1.95f, 0f, 3f)).category(CATEGORY).item(Items.ARROW).build();
    public static final MValue<Float> SNOW_DIVERGENCE = MValue.of(id("snow_divergence"), MValue.FLOAT.of(4f, 0f, 32f)).category(CATEGORY).item(Items.BOW).build();
    public static final MValue<Double> SNOW_GRAVITY = MValue.of(id("snow_gravity"), MValue.DOUBLE.of(0.02d, 0d, 0.2d, 3)).category(CATEGORY).item(Items.GRAVEL).build();

    public @Override void onInitialize() {
        NumericProperties.ID_MAPPER.put(id("charge"), SnowComponent.SnowProperty.CODEC);
    }

    public @Override void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, SnowComponent.KEY).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(SnowComponent::new);
        registry.beginRegistration(LivingEntity.class, FrozenComponent.KEY).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(FrozenComponent::new);
    }

    public static @NotNull Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}