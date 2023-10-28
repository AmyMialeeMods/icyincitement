package amymialee.icyincitement;

import amymialee.icyincitement.items.EmptySprinklerItem;
import amymialee.icyincitement.items.SnowballSprinklerItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class IcyIncitement implements ModInitializer {
    public static String MOD_ID = "icyincitement";
    public static Item EMPTY_SPRINKLER = new EmptySprinklerItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1));
    public static Item SNOWBALL_SPRINKLER = new SnowballSprinklerItem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).maxDamage(192));

    @Override
    public void onInitialize() {
        ItemGroupEvents
                .modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.add(EMPTY_SPRINKLER);
                    itemGroup.add(SNOWBALL_SPRINKLER);
                        });

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "empty_sprinkler"), EMPTY_SPRINKLER);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "snowball_sprinkler"), SNOWBALL_SPRINKLER);
    }
}