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
    public static String MOD_ID = "icyincitement";
    public static MRegistry REGISTRY = new MRegistry(MOD_ID);
    public static Item EMPTY_SPRINKLER = REGISTRY.register("empty_sprinkler", new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(1), EmptySprinklerItem::new, ItemGroups.COMBAT);
    public static Item SNOWBALL_SPRINKLER = REGISTRY.register("snowball_sprinkler", new Item.Settings().rarity(Rarity.EPIC).maxCount(1), SnowballSprinklerItem::new, ItemGroups.COMBAT);
    public static EntityType<IceBallEntity> ICEBALL = REGISTRY.register("iceball", EntityType.Builder.<IceBallEntity>create(IceBallEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(8).trackingTickInterval(2));
    public static final MValueCategory CATEGORY = new MValueCategory(id(MOD_ID), SNOWBALL_SPRINKLER, Identifier.ofVanilla("textures/block/ice.png"), 16, 16);
    public static final MValue<Integer> MAX_SNOWBALLS = MValue.of(id("max_snowballs"), MValue.INTEGER.of(100, 1, 256)).category(CATEGORY).item(Items.SNOW_BLOCK).build();
    public static final MValue<Double> RECHARGE_RATE = MValue.of(id("recharge_rate"), MValue.DOUBLE.of(0.25, 0d, 1d)).category(CATEGORY).item(Items.POWDER_SNOW_BUCKET).build();
    public static final MValue<Double> RECOIL = MValue.of(id("recoil"), MValue.DOUBLE.of(0.075, 0d, 0.2d, 3)).category(CATEGORY).item(Items.TRIDENT).build();

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