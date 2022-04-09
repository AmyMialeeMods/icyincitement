package amymialee.icyincitement;

import amymialee.icyincitement.items.EmptySprinklerItem;
import amymialee.icyincitement.items.SnowballSprinklerItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class IcyIncitement implements ModInitializer {
    public static String MOD_ID = "icyincitement";
    public static Item EMPTY_SPRINKLER = new EmptySprinklerItem(new FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON).maxCount(1));
    public static Item SNOWBALL_SPRINKLER = new SnowballSprinklerItem(new FabricItemSettings().group(ItemGroup.COMBAT).rarity(Rarity.EPIC).maxCount(1).maxDamage(192));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "empty_sprinkler"), EMPTY_SPRINKLER);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "snowball_sprinkler"), SNOWBALL_SPRINKLER);
    }
}
